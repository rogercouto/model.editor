package br.com.editor.model.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import br.com.editor.model.data.DataTypes;
import br.com.editor.model.data.Server;
import br.com.editor.model.model.Column;
import br.com.editor.model.model.Renames;
import br.com.editor.model.model.Table;
import br.com.editor.model.tools.Util;
import br.com.editor.model.view.ModifyTableDialogView;

public class ModifyTableDialogController extends ModifyTableDialogView{

	private static final List<Class<?>> TYPES = DataTypes.getAllTypes();

	private List<Table> allRefs = new ArrayList<>();
	private List<Column> allPks = new ArrayList<>();

	private List<Text> nameTexts = new ArrayList<>();
	private List<CCombo> typeCombos = new ArrayList<>();
	private List<CCombo> dbTypeCombos = new ArrayList<>();
	private List<Text> dbLenTexts = new ArrayList<>();
	private List<Button> pkChecks = new ArrayList<>();// 4
	private List<Button> aiChecks = new ArrayList<>();// 5
	private List<Button> nnChecks = new ArrayList<>();// 6
	private List<Button> uChecks = new ArrayList<>(); // 7
	private List<CCombo> fkCombos = new ArrayList<>(); // 8
	private List<Button> remButtons = new ArrayList<>(); // 9 <- 5

	private Table table;
	private Server server;
	private List<String> dbTypes = null;
	//private List<Column> newColumns = new ArrayList<>();
	private Renames renames = null;
	
	public ModifyTableDialogController(Shell parent, Server server, Table table, List<Table> allTables) {
		super(parent);
		result = false;
		this.table = table.clone();
		this.server = server;
		this.dbTypes = this.server.getDatabaseTypes();
		allTables.forEach(t->{
			t.getPrimaryKeys().forEach(pk->{
				allRefs.add(t);
				allPks.add(pk);
			});
		});
		initTable();
		if (table.getName().trim().length() == 0)
			shell.setText("New table");
	}

	public Renames getRenames() {
		return renames;
	}

	private void disableScroll(CCombo combo) {
		combo.addListener(SWT.MouseWheel, new Listener() {
			@Override
			public void handleEvent(Event e) {
				e.doit = false;
			}
		});
	}

	private void addSelectListener(CCombo combo) {
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				int columnIndex = (int)combo.getData();
				int comboIndex = combo.getSelectionIndex();
				if (columnIndex >= 0 && comboIndex >= 0 && comboIndex < allPks.size()) {
					Column fk = allPks.get(comboIndex);
					int typeIndex = TYPES.indexOf(fk.getType());
					typeCombos.get(columnIndex).select(typeIndex);
					int dbTypeIndex = dbTypes.indexOf(fk.getDbType());
					dbTypeCombos.get(columnIndex).select(dbTypeIndex);
					Integer size = fk.getSize();
					dbLenTexts.get(columnIndex).setText(size != null ? size.toString() : "");
					String cName = nameTexts.get(columnIndex).getText().trim();
					if (cName.length() == 0 || cName.contains("column_")) {
						String columnName = String.format("%s_%s",fk.getTable().getName(), fk.getName());
						nameTexts.get(columnIndex).setText(columnName);
					}
				}
			}
		});
	}

	//gambi
	private void refresh(){
		Point s = shell.getSize();
		s.x += 1;
		shell.setSize(s);
		s.x -=1 ;
		shell.setSize(s);
	}

	private void removeItem(int index) {
		table.removeColumn(index);
		Text text = nameTexts.get(index);
		nameTexts.remove(index);
		text.dispose();
		CCombo combo = typeCombos.get(index);
		typeCombos.remove(index);
		combo.dispose();
		CCombo combo2 = dbTypeCombos.get(index);
		dbTypeCombos.remove(index);
		combo2.dispose();
		Text text2 = dbLenTexts.get(index);
		dbLenTexts.remove(index);
		text2.dispose();
		Button check = pkChecks.get(index);
		pkChecks.remove(index);
		check.dispose();
		Button check1 = aiChecks.get(index);
		aiChecks.remove(index);
		check1.dispose();
		Button check2 = nnChecks.get(index);
		nnChecks.remove(index);
		check2.dispose();
		Button check3 = uChecks.get(index);
		uChecks.remove(index);
		check3.dispose();
		CCombo combo3 = fkCombos.get(index);
		fkCombos.remove(index);
		combo3.dispose();
		Button btn = remButtons.get(index);
		remButtons.remove(index);
		btn.dispose();
		shell.redraw();
		tblModifier.remove(index);
		refresh();
	}

	private void changeJavaType(int index){
		int i = typeCombos.get(index).getSelectionIndex();
		if (index >= 0){
			Class<?> type = TYPES.get(i);
			String dbType = server.getDatabaseType(type);
			int j = dbTypes.indexOf(dbType);
			if (j >= 0){
				dbTypeCombos.get(index).select(j);
				if (type.equals(String.class)){
					dbLenTexts.get(index).setText("255");
				}else{
					dbLenTexts.get(index).setText("");
				}
			}
		}
	}

	private void changeDbType(int index){
		String dbType = dbTypeCombos.get(index).getText();
		if (dbType.trim().length() > 0){
			Class<?> type = server.getJavaType(dbType);
			int i = TYPES.indexOf(type);
			if (i >= 0){
				typeCombos.get(index).select(i);
				if (type.equals(String.class)){
					dbLenTexts.get(index).setText("255");
				}else{
					dbLenTexts.get(index).setText("");
				}
			}
		}
	}

	private void createColumn(Column c) {
		//int index = table.getColumns().indexOf(c);
		int index = tblModifier.getItemCount();
		TableItem item = new TableItem(tblModifier, SWT.NONE);
		//item.setText(c.getName());
		TableEditor editor0 = new TableEditor(tblModifier);
		Text text = new Text(tblModifier, SWT.BORDER);
		text.setText(c.getName() != null ? c.getName() : "");
		nameTexts.add(text);
		editor0.grabHorizontal = true;
		editor0.setEditor(text, item, 0);
		//Type combo
		TableEditor editor1 = new TableEditor(tblModifier);
		CCombo combo = new CCombo(tblModifier, SWT.READ_ONLY);
		List<Class<?>> allTypes = DataTypes.getAllTypes();
		allTypes.forEach(jt->{
			combo.add(jt.getSimpleName());
		});
		combo.setText(c.getType().getSimpleName());
		disableScroll(combo);
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				changeJavaType(index);
			}
		});
		typeCombos.add(combo);
		editor1.grabHorizontal = true;
		editor1.setEditor(combo, item, 1);
		//DbType combo
		TableEditor editor2 = new TableEditor(tblModifier);
		CCombo combo2 = new CCombo(tblModifier, SWT.READ_ONLY);
		dbTypes.forEach(dbt->{
			combo2.add(dbt);
		});
		combo2.setText(c.getDbType() != null ? c.getDbType() : "");
		disableScroll(combo2);
		combo2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				changeDbType(index);
			}
		});
		dbTypeCombos.add(combo2);
		editor2.grabHorizontal = true;
		editor2.setEditor(combo2, item, 2);
		//DbLen text
		TableEditor editor3 = new TableEditor(tblModifier);
		Text text2 = new Text(tblModifier, SWT.BORDER);
		String size = "";
		if (c.getSize() != null)
			size = c.getSize().toString();
		else {
			if (c.getDbType() != null && c.getDbType().toUpperCase().compareTo("VARCHAR") == 0)
				size = "255";
		}
		text2.setText(size);
		dbLenTexts.add(text2);
		editor3.grabHorizontal = true;
		editor3.setEditor(text2, item, 3);

		//Check pk
		TableEditor editor4 = new TableEditor(tblModifier);
		Button checkPk = new Button(tblModifier, SWT.CHECK);
		checkPk.setSelection(c.isPrimaryKey());
		pkChecks.add(checkPk);
		editor4.grabHorizontal = true;
		editor4.setEditor(checkPk, item, 4);

		//Check ai
		TableEditor editor5 = new TableEditor(tblModifier);
		Button checkAi = new Button(tblModifier, SWT.CHECK);
		checkAi.setSelection(c.isSurrogateKey());
		aiChecks.add(checkAi);
		editor5.grabHorizontal = true;
		editor5.setEditor(checkAi, item, 5);

		//Check nn
		TableEditor editor6 = new TableEditor(tblModifier);
		Button checkNn = new Button(tblModifier, SWT.CHECK);
		checkNn.setSelection(c.isNotNull());
		nnChecks.add(checkNn);
		editor6.grabHorizontal = true;
		editor6.setEditor(checkNn, item, 6);

		//Check nn
		TableEditor editor7 = new TableEditor(tblModifier);
		Button checkU = new Button(tblModifier, SWT.CHECK);
		checkU.setSelection(c.isUnique());
		uChecks.add(checkU);
		editor7.grabHorizontal = true;
		editor7.setEditor(checkU, item, 7);

		//Fk Combo
		TableEditor editor8 = new TableEditor(tblModifier);
		CCombo combo3 = new CCombo(tblModifier, SWT.READ_ONLY);
		allPks.forEach(pk->{
			int i = allPks.indexOf(pk);
			combo3.add(String.format("%s (%s)", allRefs.get(i).getName(), pk.getName()));
		});
		combo3.add("");
		combo3.setText("");
		if (c.getForeignKey() != null) {
			combo3.setText(String.format("%s (%s)", c.getForeignKey().getTable().getName(), c.getForeignKey().getName()));
		}
		disableScroll(combo3);
		//set index
		combo3.setData(c.getName() != null ? table.getColumns().indexOf(c) : table.getColumns().size());
		addSelectListener(combo3);
		fkCombos.add(combo3);
		editor8.grabHorizontal = true;
		editor8.setEditor(combo3, item, 8);
		//Check remove
		TableEditor editor9 = new TableEditor(tblModifier);
		Button button = new Button(tblModifier, SWT.NONE);
		button.setImage(SWTResourceManager.getImage(ModifyTableDialogController.class, "/icon/delete.png"));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				removeItem(index);
			}
		});
		remButtons.add(button);
		editor9.grabHorizontal = true;
		editor9.setEditor(button, item, 9);
	}

	private void initTable() {
		txtName.setText(table.getName());
		TableColumn tCol = new TableColumn(tblModifier, SWT.NONE);
		tCol.setText("Column name");
		TableColumn tColType = new TableColumn(tblModifier, SWT.NONE);
		tColType.setText("Type");
		tColType.setWidth(100);
		TableColumn tColDbType = new TableColumn(tblModifier, SWT.NONE);
		tColDbType.setText("DB Type");
		tColDbType.setWidth(100);
		TableColumn tColLength = new TableColumn(tblModifier, SWT.NONE);
		tColLength.setText("Legth");
		tColLength.setWidth(50);

		TableColumn tColPk = new TableColumn(tblModifier, SWT.NONE);
		tColPk.setText("pk");
		tColPk.setAlignment(SWT.CENTER);
		tColPk.setWidth(25);

		TableColumn tColAI = new TableColumn(tblModifier, SWT.NONE);
		tColAI.setText("ai");
		tColAI.setAlignment(SWT.CENTER);
		tColAI.setWidth(25);

		TableColumn tColNN = new TableColumn(tblModifier, SWT.NONE);
		tColNN.setText("nn");
		tColNN.setAlignment(SWT.CENTER);
		tColNN.setWidth(26);

		TableColumn tColU = new TableColumn(tblModifier, SWT.NONE);
		tColU.setText("u");
		tColU.setAlignment(SWT.CENTER);
		tColU.setWidth(24);

		TableColumn tColFk = new TableColumn(tblModifier, SWT.NONE);
		tColFk.setText("Foreign Key");
		tColFk.setWidth(150);

		TableColumn tColRem = new TableColumn(tblModifier, SWT.NONE);
		tColRem.setText("");
		//tColRem.setAlignment(SWT.CENTER);
		tColRem.setWidth(25);

		OptionalInt oi = table.getColumns().stream().mapToInt(c -> c.getNameWidth()).max();
		if (oi.isPresent())
			tCol.setWidth(oi.getAsInt()+60);
		else
			tCol.pack();
		table.getColumns().forEach(c->{
			createColumn(c);
		});
	}

	protected void doBtnAddWidgetSelected(SelectionEvent e) {
		Column c = new Column();
		if (table.getColumns().size() > 0){
			c.setName("column_"+table.getColumns().size());
			c.setType(String.class);
			c.setDbType("VARCHAR");
			c.setSize(255);
		}else{
			c.setName("id");
			c.setType(Integer.class);
			c.setDbType("INT");
			c.setPrimaryKey(true);
			c.setNotNull(true);
			c.setSurrogateKey(true);
			c.setUnique(true);
		}
		table.addColumn(c);
		createColumn(c);
		Text text = nameTexts.get(nameTexts.size()-1);
		text.setSelection(0, text.getText().length());
		text.setFocus();
	}

	protected void dobtnOkwidgetSelected(SelectionEvent e) {
		//Modify table name
		boolean valid = true;
		Renames renames = new Renames();
		String oldTableName = table.getName();
		String newTableName = txtName.getText();
		if (oldTableName.compareTo(newTableName) != 0) {
			renames.renameTable(oldTableName, newTableName);
		}
		table.setName(newTableName);
		//Modify column names
		for (int i = 0; i < nameTexts.size(); i++) {
			String newName = nameTexts.get(i).getText();
			if (newName.trim().length() == 0)
				valid = false;
			String oldName = table.getColumn(i).getName();
			if (oldName.compareTo(newName) != 0) {
				renames.renameColumn(oldName, newName, newTableName);
			}
			table.getColumn(i).setName(newName);
		}
		//Modify types
		for (int i = 0; i < typeCombos.size(); i++) {
			String type = typeCombos.get(i).getText();
			Optional<Class<?>> op = TYPES.stream().filter(t->t.getSimpleName().compareTo(type)==0).findFirst();
			if (op.isPresent()) {
				table.getColumn(i).setType(op.get());
			}
		}
		//Modify dbTypes
		for (int i = 0; i < dbTypeCombos.size(); i++) {
			String dbType = dbTypeCombos.get(i).getText();
			table.getColumn(i).setDbType(dbType);
		}
		//Modify sizes
		for (int i = 0; i < dbLenTexts.size(); i++) {
			String len = dbLenTexts.get(i).getText();
			if (Util.isRealNum(len)) {
				table.getColumn(i).setSize(Integer.valueOf(len));
			}else {
				table.getColumn(i).setSize(null);
			}
		}
		for (int i = 0; i < pkChecks.size(); i++) {
			table.getColumn(i).setPrimaryKey(pkChecks.get(i).getSelection());
		}
		for (int i = 0; i < aiChecks.size(); i++) {
			table.getColumn(i).setSurrogateKey(aiChecks.get(i).getSelection());
		}
		for (int i = 0; i < nnChecks.size(); i++) {
			table.getColumn(i).setNotNull(nnChecks.get(i).getSelection());
		}
		for (int i = 0; i < uChecks.size(); i++) {
			table.getColumn(i).setUnique(uChecks.get(i).getSelection());
		}
		//Set foreign Keys
		for (int i = 0; i < fkCombos.size(); i++) {
			int index = fkCombos.get(i).getSelectionIndex();
			if (index < 0 || index >= allPks.size()) {
				table.getColumn(i).setForeignKey(null);
			}else {
				Column fk = allPks.get(index);
				table.getColumn(i).setForeignKey(fk);
			}
		}
		if (!valid) {
			MessageBox box = new MessageBox(shell, SWT.ERROR);
			box.setText("Error!");
			box.setMessage("Somthing is missing!");
			box.open();
			return;
		}
		result = table;
		this.renames = renames;
		shell.close();
	}

}
