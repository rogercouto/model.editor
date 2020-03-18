package br.com.editor.model.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import br.com.editor.model.data.Server;
import br.com.editor.model.model.Column;
import br.com.editor.model.model.Renames;
import br.com.editor.model.model.Table;
import br.com.editor.model.model.TableModel;
import br.com.editor.model.tools.IntCounter;
import br.com.editor.model.view.ModelEditorCompView;

public class ModelEditorController extends ModelEditorCompView {

	private static final int START_POS = 25;
	private static final int SPC_BETWEEN = 20;

	public List<TableModel> models = new LinkedList<>();
	private TableModel movingModel = null;

	private Listener mouseRightListener = null;
	private Listener doubleClickListener = null;

	private static int maxWidth = 1200;

	private int selectionIndex = -1;

	private Server server;
	
	private Stack<List<TableModel>> undoStack = new Stack<>();
	
	private Renames renames = new Renames();
	
	public ModelEditorController(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	public ModelEditorController(Composite parent, int style, int canvasStyle) {
		super(parent, style, canvasStyle);
		initialize();
	}

	public ModelEditorController(Composite parent, int style, Server server) {
		super(parent, style);
		this.server = server;
		initialize();
	}

	public ModelEditorController(Composite parent, int style, int canvasStyle, Server server) {
		super(parent, style, canvasStyle);
		this.server = server;
		initialize();
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
		if (server.getDbName() != null)
			txtDbName.setText(server.getDbName());
	}

	private void initialize() {
		canvas.addPaintListener(new PaintListener() {
	        public void paintControl(PaintEvent e) {
	        	e.gc.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
	        	models.forEach(tm->{
	        		List<TableModel> om = models.stream().filter(am -> !am.equals(tm)).collect(Collectors.toList());
	        		tm.drawRelations(e.gc, om, hSelection, vSelection);
	        	});
	        	models.forEach(tm->{
	        		boolean selected = models.indexOf(tm) == selectionIndex;
	        		tm.draw(e.gc, hSelection, vSelection, selected);
	        	});
	        }
	    });
		canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				if (arg0.button != 1){
					selectionIndex = -1;
					return;
				}
				movingModel = getModel(arg0);
				selectionIndex = movingModel != null ? models.indexOf(movingModel) : -1;
			}
			@Override
			public void mouseUp(MouseEvent arg0) {
				btnEdit.setEnabled(selectionIndex >= 0);
				btnDelete.setEnabled(selectionIndex >= 0);
				movingModel = null;
				canvas.redraw();
				if (mouseRightListener != null && arg0.button == 3){
					Event event = new Event();
					TableModel model = getModel(arg0);
					if (model != null){
						event.data = model.getTable();
						event.x = arg0.x;
						event.y = arg0.y;
						mouseRightListener.handleEvent(event);
					}
				}
			}
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				movingModel = null;
				if (doubleClickListener != null && arg0.button == 1){
					Event event = new Event();
					TableModel model = getModel(arg0);
					if (model != null){
						event.data = model.getTable();
						doubleClickListener.handleEvent(event);
					}
				}
			}
		});
		canvas.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent arg0) {
				if (movingModel != null){
					Point pos = movingModel.getPosition();
					pos.x = arg0.x - (movingModel.getRect().width / 2) +hSelection;
					pos.y = arg0.y - (movingModel.getRect().height / 2) +vSelection;
					movingModel.setPosition(pos);
					canvas.redraw();
				}
			}
		});
		setDoubleClickListener(new Listener() {
			@Override
			public void handleEvent(Event event) {
				doModelEditorDoubleClick(event);
			}
		});
		setMouseRightListener(new Listener() {
			@Override
			public void handleEvent(Event event) {
				doModelEditorRightClick(event);
			}
		});
	}

	private TableModel getModel(MouseEvent event){
		List<TableModel> revModels = models;
		Collections.reverse(revModels);
		Optional<TableModel> opt = revModels.stream().filter(m -> {
			Rectangle r = new Rectangle(m.getPosition().x-hSelection, m.getPosition().y-vSelection, m.getRect().width, m.getRect().height);
			return r.contains(new Point(event.x, event.y));
		}).findFirst();
		if (opt.isPresent()){
			return opt.get();
		}
		return null;
	}

	public void addTable(Table table){
		TableModel tm = new TableModel(table, new Point(START_POS, START_POS));
		models.add(tm);
	}

	public void addTableModel(TableModel tableModel){
		models.add(tableModel);
	}

	public int getTableIndex(String tableName){
		for (int i = 0; i < models.size(); i++) {
			if (models.get(i).getTable().getName().compareTo(tableName) == 0)
				return i;
		}
		return -1;
	}
	
	public void setTable(int index, Table table){
		String name = models.get(index).getTable().getName();
		getTables()
				.stream()
				.filter(t->t.getName().compareTo(name) != 0)
				.forEach(t->{
					t.getForeignKeys().forEach(fk->{
						if (fk.getForeignKey().getTable().getName().compareTo(name) == 0) {
							Optional<Column> newFk = table.getColumns()
									.stream()
									.filter(c->c.getName().compareTo(fk.getName())==0)
									.findFirst();
							if (newFk.isPresent()) {
								fk.setForeignKey(newFk.get());
							}
						}
					});
				});
		models.get(index).setTable(table);
	}

	public void removeTable(String tableName){
		int index = getTableIndex(tableName);
		removeTable(index);
		refresh();
	}
	
	private void removeTable(int index) {
		if (index >= 0) {
			final Table table = getTables().get(index);
			saveStep();
			btnUndo.setEnabled(true);
			getTables().forEach(t->{
				List<Column> l = new ArrayList<>();
				t.getColumns().forEach(c->{
					if (c.getForeignKey() != null && c.getForeignKey().getTable().getName().compareTo(table.getName())==0) {
						l.add(c);
					}
				});
				t.removeColumns(l);
				TableModel model = getModel(t);
				model.calculeSize();
				model.setPosition(model.getPosition());
				canvas.redraw();
				
			});
			models.remove(index);
		}
	}

	public void draw(){
		canvas.redraw();
	}

	private void setDoubleClickListener(Listener listener){
		this.doubleClickListener = listener;
	}

	private void setMouseRightListener(Listener mouseRightListener) {
		this.mouseRightListener = mouseRightListener;
	}

	@Override
	protected void scrollMoved(){
		super.scrollMoved();
		draw();
	}

	public TableModel getFirstModel(){
		if (models.size() > 0)
			return models.get(0);
		return null;
	}

	public void clear(){
		models = new LinkedList<>();
		btnEdit.setEnabled(false);
		btnDelete.setEnabled(false);
	}

	public TableModel getModel(Table table){
		Optional<TableModel> om = models.stream().filter(m->m.getTable().getName().compareTo(table.getName())==0).findFirst();
		if (om.isPresent())
			return om.get();
		return null;
	}

	public static int getMaxWidth() {
		return maxWidth;
	}

	public static void setMaxWidth(int maxWidth) {
		ModelEditorController.maxWidth = maxWidth;
	}

	public void calcPositions(){
		IntCounter i = new IntCounter();
		i.inc(START_POS);
		IntCounter j = new IntCounter();
		j.inc(START_POS);
		IntCounter maxHeigth = new IntCounter();
		maxHeigth.setValue(0);
		models.forEach(m->{
			m.setPosition(new Point(i.getValue(), j.getValue()));
			if (m.getRect().height > maxHeigth.getValue())
				maxHeigth.setValue(m.getRect().height);
			if (i.getValue() > maxWidth){
				i.setValue(START_POS);
				j.inc(maxHeigth.getValue());
				j.inc(SPC_BETWEEN);
			}else{
				i.inc(m.getRect().width);
				i.inc(SPC_BETWEEN);
			}
		});
		if (canvas.getVerticalBar() != null)
			canvas.getVerticalBar().setMaximum(200);
	}

	public List<Table> getTables(){
		return models.stream().map(m->m.getTable()).collect(Collectors.toList());
	}

	private void doModelEditorDoubleClick(Event e) {
		Table table = (Table)e.data;
		editTable(table);
	}

	private void doModelEditorRightClick(Event e){
		Menu menu = new Menu(getShell(), SWT.POP_UP);
        MenuItem item1 = new MenuItem(menu, SWT.PUSH);
        item1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				Table table = (Table)e.data;
				editTable(table);
			}
		});
        item1.setText("Edit");
        MenuItem item2 = new MenuItem(menu, SWT.PUSH);
        item2.setText("Delete");
        item2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				Table table = (Table)e.data;
				removeTable(table);
			}
		});
        Point loc = new Point(e.x, e.y+45);
        Rectangle rect = e.getBounds();
        Point mLoc = new Point(loc.x-1, loc.y+rect.height);
        menu.setLocation(getShell().getDisplay().map(getParent(), null, mLoc));
        menu.setVisible(true);
	}

	private void editTable(Table table){
		int index = getTableIndex(table.getName());
		ModifyTableDialogController dialog = new ModifyTableDialogController(getShell(), server, table, getTables());
		Object res = dialog.open();
		if (res != null && res instanceof Table) {
			renames.putAll(dialog.getRenames());
			saveStep();
			table = ((Table)res).clone();
			setTable(index, table);
			btnUndo.setEnabled(true);
		}
		selectionIndex = -1;
		btnEdit.setEnabled(false);
		btnDelete.setEnabled(false);
		refresh();
	}

	private void removeTable(Table table){
		removeTable(table.getName());
		selectionIndex = -1;
		btnEdit.setEnabled(false);
		btnDelete.setEnabled(false);
		refresh();
	}

	protected void dobtnCreatewidgetSelected(SelectionEvent e) {
		Table table = new Table("");
		ModifyTableDialogController dialog = new ModifyTableDialogController(getShell(), server, table, getTables());
		Object res = dialog.open();
		if (res != null && res instanceof Table) {
			saveStep();
			addTable(table);
			int index = getTableIndex(table.getName());
			table = ((Table)res).clone();
			setTable(index, table);
		}
		btnUndo.setEnabled(true);
		refresh();
	}

	protected void dobtnEditwidgetSelected(SelectionEvent e) {
		if (selectionIndex >= 0){
			Table table = getTables().get(selectionIndex);
			editTable(table);
		}
	}

	protected void dobtnDeletewidgetSelected(SelectionEvent e) {
		if (selectionIndex >= 0){
			Table table = getTables().get(selectionIndex);
			removeTable(table);
		}
	}

	public void refresh() {
		//Fucking gambi
		boolean max = getShell().getMaximized();
		getShell().setMaximized(false);
		Point p = getShell().getSize();
		p.x += 1;
		getShell().setSize(p);
		p.x -= 1;
		getShell().setSize(p);
		getShell().setMaximized(max);
		btnUndo.setEnabled(!undoStack.isEmpty());
	}
	
	private void saveStep() {
		List<TableModel> list = new LinkedList<TableModel>();
		models.forEach(m->{
			TableModel model = new TableModel(m.getTable().clone(), m.getPosition());
			list.add(model);
		});
		undoStack.add(list);
	}
	
	private void remakeFks(Table t1, Table t2){
		List<Column> c1 = t1.getColumns();
		c1.forEach(c->{
			if (c.getForeignKey() != null && c.getForeignKey().getTable().getName().compareTo(t2.getName()) == 0){
				String columnName = c.getForeignKey().getName();
				Column column = t2.getColumnIFExists(columnName);
				if (column != null){
					c.setForeignKey(column);
				}
			}
		});
	}
	
	private void checkFks(List<TableModel> models){
		models.forEach(m->{
			List<Table> oList = models
					.stream()
					.filter(om->om.getTable().getName().compareTo(m.getTable().getName()) != 0)
					.map(om->om.getTable())
					.collect(Collectors.toList());
			oList.forEach(od->{
				remakeFks(m.getTable(), od);
			});
		});
	}
	
	protected void dobtnUndowidgetSelected(SelectionEvent e) {
		if (!undoStack.isEmpty()) {
			List<TableModel> um = undoStack.pop();
			checkFks(um);
			clear();
			um.forEach(m->{
				addTableModel(m);
			});
			refresh();
		}
	}
	
	protected void docanvaskeyPressed(KeyEvent e) {
		if (e.keyCode == SWT.DEL) {
			removeTable(selectionIndex);
			selectionIndex = -1;
			btnEdit.setEnabled(false);
			btnDelete.setEnabled(false);
		}
		if(((e.stateMask & SWT.CTRL) == SWT.CTRL) && (e.keyCode == 'z')){
            dobtnUndowidgetSelected(null);
        }
		if(((e.stateMask & SWT.CTRL) == SWT.CTRL) && (e.keyCode == 't')){
			if (selectionIndex >= 0) {
				Table table = getTables().get(selectionIndex);
				System.out.println(table);
			}
        }
	}
	
	public String getDatabaseName() {
		return txtDbName.getText();
	}

	public int getSelectionIndex() {
		return selectionIndex;
	}

	public Renames getRenames() {
		return renames;
	}
	
}