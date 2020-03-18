package br.com.model.editor.data;

import java.util.HashMap;
import java.util.LinkedHashMap;

import br.com.model.editor.tools.IntCounter;

public class DataLine {

	private HashMap<String, Object> data = new LinkedHashMap<String, Object>();
	
	public void setValue(String columnName, Object value) {
		data.put(columnName, value);
	}
	
	public Object getValue(String columnName) {
		if (data.containsKey(columnName))
			return data.get(columnName);
		return null;
	}
	
	public Object[] toArray() {
		return data.values().toArray();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("\t{");
		IntCounter counter = new IntCounter();
		data.keySet().forEach(key->{
			if (counter.getThenInc() > 0)
				builder.append(",");
			builder.append("\n\t\t");
			builder.append("\"");
			builder.append(key);
			builder.append("\":\"");
			builder.append(getValue(key));
			builder.append("\"");
		});
		builder.append("\n\t}");
		return builder.toString();
	}
	
}
