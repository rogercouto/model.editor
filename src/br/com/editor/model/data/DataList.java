package br.com.editor.model.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import br.com.editor.model.model.Table;

public class DataList {

	private List<DataLine> lines = new LinkedList<>();
	private HashMap<Object, Object> keys = new HashMap<Object, Object>();
	
	private int previewId = 0;
	private String skColumnName = null;
	
	public DataList(Table table) {
		if (table.haveSurrogateKey())
			skColumnName = table.getSurrogateKey().getName();
	}
	
	public void add(DataLine line) {
		setKeys(line.getValue(skColumnName), ++previewId);
		lines.add(line);
	};
	
	private void setKeys(Object oldKey, Object newKey) {
		keys.put(oldKey, newKey);
	}
	
	public Object getNewKey(Object oldKey) {
		if (keys.containsKey(oldKey))
			return keys.get(oldKey);
		return null;
	}
	
	public List<DataLine> getLines(){
		return lines;
	}
	
	public int count() {
		return lines.size();
	}

}
