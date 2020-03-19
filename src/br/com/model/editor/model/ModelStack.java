package br.com.model.editor.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

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

	public ModelData undo(List<TableModel> models, Renames renames) {
		if (undoStack.size() > 0) {
			ModelData d = undoStack.pop();
			saveStep(new ModelData(models, renames, server), redoStack);
			TableModel.checkFks(d.getModels());
			return d;
		}
		return null;
	}

	public ModelData redo(List<TableModel> models, Renames renames) {
		if (redoStack.size() > 0) {
			ModelData d = redoStack.pop();
			saveStep(new ModelData(models, renames, server), undoStack);
			TableModel.checkFks(d.getModels());
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

}
