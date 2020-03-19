package br.com.model.editor.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import org.eclipse.swt.graphics.Point;

import br.com.model.editor.data.MysqlServer;
import br.com.model.editor.data.Server;

public class ModelStack {

	public static final int LIMIT = 15;

	private Stack<ModelData> undoStack = new Stack<ModelData>();
	private Stack<ModelData> redoStack = new Stack<ModelData>();
	private Server server = null;


	public ModelStack(Server server) {
		super();
		this.server = server;
	}

	private void saveStep(ModelData data, Stack<ModelData> stack) {
		List<TableModel> list = new LinkedList<TableModel>();
		data.getModels().forEach(m->{
			TableModel model = new TableModel(m.getTable().clone(), m.getPosition());
			list.add(model);
		});
		Renames renames = new Renames();
		renames.putAll(data.getRenames());
		stack.push(new ModelData(list, renames, data.getServer()));
		if (stack.size() > LIMIT)
			stack.removeElementAt(0);
	}

	private void saveStep(ModelData data) {
		saveStep(data, undoStack);
		redoStack.clear();
	}

	public void saveStep(List<TableModel> models, Renames renames) {
		saveStep(new ModelData(models, renames, server));
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

	public ModelData undo(List<TableModel> models, Renames renames) {
		if (undoStack.size() > 0) {
			ModelData d = undoStack.pop();
			saveStep(new ModelData(models, renames, server), redoStack);
			checkFks(d.getModels());
			return d;
		}
		return null;
	}

	public ModelData redo(List<TableModel> models, Renames renames) {
		if (redoStack.size() > 0) {
			ModelData d = redoStack.pop();
			saveStep(new ModelData(models, renames, server), undoStack);
			checkFks(d.getModels());
			return d;
		}
		return null;
	}

	public boolean canUndo() {
		return undoStack.size() > 0;
	}

	public boolean canRedo() {
		return redoStack.size() > 0;
	}

	private static void printTables(List<TableModel> models) {
		System.out.print("tables:[");
		models.forEach(m->{
			if (models.indexOf(m) > 0)
				System.out.print(", ");
			System.out.print(m.getTable().getName());
		});
		System.out.println("]");
	}

	private List<TableModel> models = new LinkedList<>();

	private void createTestTable(String tableName) {
		Table table = new Table(tableName);
		models.add(new TableModel(table, new Point(0,0)));
	}

	public void test() {
		printTables(models);
		System.out.println("undoStack.size: "+undoStack.size()+", redoStack.size: "+redoStack.size());
	}

	public static void main(String[] args) {

		ModelStack stack = new ModelStack(new MysqlServer());
		stack.saveStep(new LinkedList<>(), new Renames());//initial state
		stack.createTestTable("T1");
		stack.createTestTable("T2");
		stack.createTestTable("T3");
		stack.test();
		stack.saveStep(stack.models, new Renames());
		stack.models.remove(1);
		//stack.saveRedoStep(stack.models, new Renames());
		stack.test();

		System.out.println("undo");
		ModelData md = stack.undo(stack.models, new Renames());
		stack.models = md.getModels();
		stack.test();
		//ModelData last = stack.redoStack.lastElement();
		//printTables(last.getModels());
		if (stack.canRedo()) {
			System.out.println("redo");
			md = stack.redo(stack.models, new Renames());
			stack.models = md.getModels();
			stack.test();
		}

		System.out.println("undo");
		md = stack.undo(stack.models, new Renames());
		stack.models = md.getModels();
		stack.test();

		/*
		stack.models = md.getModels();
		stack.test();
		md = stack.redo();
		stack.models = md.getModels();
		stack.test();
		*/
	}

}
