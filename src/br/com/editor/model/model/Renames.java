package br.com.editor.model.model;

import java.util.HashMap;
import java.util.Map;

public class Renames {

	private Map<String, String> tableMap = new HashMap<>();
	private Map<String, String> columnMap = new HashMap<>();
	private boolean showChanges = false;

	public void renameTable(String oldName, String newName) {
		if (showChanges)
			System.err.println(oldName+" -> "+newName);
		String originalValue = oldName;
		if (tableMap.containsKey(oldName)) {
			originalValue = tableMap.get(oldName);
			tableMap.remove(oldName);
		}
		tableMap.put(newName, originalValue);
	}
	
	public String getTableName(String newName) {
		if (tableMap.containsKey(newName))
			return tableMap.get(newName);
		return newName;
	}
	
	private String getKey(String tableName, String columnName) {
		String originalTableName = getTableName(tableName);
		return String.format("%s#%s", originalTableName, columnName);
	}
	
	public void renameColumn(String oldName, String newName, String tableName) {
		if (showChanges)
			System.err.println(getTableName(tableName)+"#"+oldName+" -> "+getTableName(tableName)+"#"+newName);
		String originalValue = oldName;
		if (columnMap.containsKey(getKey(tableName, oldName))) {
			originalValue = columnMap.get(getKey(tableName, oldName));
			columnMap.remove(getKey(tableName, oldName));
		}
		columnMap.put(getKey(tableName, newName), originalValue);
	}
	
	public String getColumnName(String newName, String tableName) {
		if (columnMap.containsKey(getKey(tableName, newName)))
			return columnMap.get(getKey(tableName, newName));
		return newName;
	}
	
	public void putAll(Renames another) {
		if (another != null) {
			this.showChanges = false;
			another.tableMap.keySet().forEach(k->{
				String originalName = another.tableMap.get(k);
				this.renameTable(originalName, k);
			});
			another.columnMap.keySet().forEach(k->{
				String[] sa = k.split("#");
				if (sa.length != 2)
					throw new RuntimeException("Something wrong");
				String tableName = sa[0];
				String newName = sa[1];
				String originalName = another.columnMap.get(k);
				this.renameColumn(originalName, newName, tableName);
			});
			this.showChanges = true;
		}
	}
	
	public boolean isShowChanges() {
		return showChanges;
	}

	public void setShowChanges(boolean showChanges) {
		this.showChanges = showChanges;
	}

	public static void main(String[] args) {
		Renames renames = new Renames();
		renames.setShowChanges(true);
		renames.renameTable("assunto", "assunto2");
		renames.renameTable("assunto2", "assuntos");
		
		System.out.println(renames.getTableName("assuntos"));
		
		System.out.println();
		
		renames.renameColumn("assunto_id", "key","assuntos");
		renames.renameColumn("key", "id","assuntos");
		
		System.out.println(renames.getColumnName("key", "assuntos"));
		System.out.println(renames.getColumnName("id", "assuntos"));
		System.out.println(renames.getColumnName("editora_id", "editora"));
		System.out.println(renames.getColumnName("assunto_id", "livro"));
		System.out.println(renames.getColumnName("id", "assuntos"));
		
		Renames r2 = new Renames();
		r2.renameTable("editora", "editoras");
		r2.putAll(renames);
		
		System.out.println();
		
		System.out.println(r2.getTableName("editoras")); //expect editora
		System.out.println(renames.getColumnName("id", "assuntos")); //expect assunto_id
		
	}
	
}
