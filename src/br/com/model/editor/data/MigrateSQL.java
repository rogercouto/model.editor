package br.com.model.editor.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.model.editor.model.Column;
import br.com.model.editor.model.Renames;
import br.com.model.editor.model.Table;
import br.com.model.editor.tools.IntCounter;

public class MigrateSQL {

	public String dbName;
	public Server server;
	public List<Table> list;
	private Renames renames;
	
	public MigrateSQL(String dbName, Server server, List<Table> list, Renames renames) {
		super();
		this.dbName = dbName;
		this.server = server;
		this.list = list;
		this.renames = renames;
		if (this.renames == null)
			this.renames = new Renames();
		
	}
	
	public String getTableCreateSql(Table table) {
		StringBuilder builder = new StringBuilder();
		builder.append("CREATE TABLE ");
		builder.append(table.getName());
		builder.append("(");
		table.getColumns().forEach(column->{
			if (table.getColumns().indexOf(column) > 0)
				builder.append(",\n");
			else
				builder.append("\n");
			builder.append('\t');
			builder.append(column.getName());
			builder.append(' ');
			if (column.isSurrogateKey()) {
				builder.append(server.getDatabaseSurrogateKeyType());
			}else {
				if (column.getDbType() == null){
					column.setDbType(server.getDatabaseType(column));
				}
				builder.append(column.getDbType());
				if (column.getType().equals(String.class) && column.getSize() == null)
					column.setSize(255);
				if (column.getSize() != null) {
					builder.append(String.format("(%d)", column.getSize()));
				}
				if (column.isNotNull())
					builder.append(" NOT NULL");
				if (column.isUnique()&&!column.isPrimaryKey())
					builder.append(" UNIQUE");
			}
		});
		List<Column> pks = table.getPrimaryKeys();
		if (pks.size() > 0)
			builder.append(",\n\tPRIMARY KEY(");
		pks.forEach(pk->{
			if (pks.indexOf(pk) > 0)
				builder.append(", ");
			builder.append(pk.getName());
		});
		if (pks.size() > 0)
			builder.append(")");
		builder.append("\n)");
		if (server.getClass().equals(MysqlServer.class))
			builder.append("Engine=InnoDB");
		builder.append(";\n\n");
		return builder.toString();
	}
	
	public String getAllTablesCreateSql(){
		StringBuilder builder = new StringBuilder();
		list.forEach(table->{
			builder.append(getTableCreateSql(table));
		});
		return builder.toString();
	}
	
	public List<String> getTablesCreateSqls(){
		return list
				.stream()
				.map(t->getTableCreateSql(t))
				.collect(Collectors.toList());
	}
	
	public String getFKCreateSql(Table table) {
		StringBuilder builder = new StringBuilder();
		List <Column> fks = table.getForeignKeys();
		fks.forEach(fk->{
			builder.append("ALTER TABLE ");
			builder.append(table.getName());
			builder.append(" ADD FOREIGN KEY(");
			builder.append(fk.getName());
			builder.append(")");
			builder.append(" REFERENCES ");
			builder.append(fk.getForeignKey().getTable().getName());
			builder.append("(");
			builder.append(fk.getForeignKey().getName());
			builder.append(");\n\n");
		});
		return builder.toString();
	}
	
	public String getAllFksCreateSql(){
		StringBuilder builder = new StringBuilder();
		list.forEach(table->{
			builder.append(getFKCreateSql(table));
		});
		return builder.toString();
	}
	
	public List<String> getFksCreateSqls(){
		return list
				.stream()
				.map(t->getFKCreateSql(t))
				.collect(Collectors.toList());
	}
	
	public DataList getData(Table table){
		try {
			DataList list = new DataList(table);
			String sql = String.format("SELECT * FROM %s", renames.getTableName(table.getName()));		
			Connection conn = server.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet res = stmt.executeQuery(sql);
			while (res.next()) {
				List<Column> columns = table.getColumns();
				DataLine line = new DataLine();
				for (Column c : columns) {
					Object value = res.getObject(renames.getColumnName(c.getName(), table.getName()));
					line.setValue(c.getName(), value);
				}
				list.add(line);
			}
			res.close();
			conn.close();
			return list;
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Map<String, DataList> getDataMap(List<Table> tables) {
		Map<String, DataList> map = new LinkedHashMap<>();
		tables.forEach(t->{
			map.put(t.getName(), getData(t));
		});
		return map;
	}
	
	public List<Table> getDependences(Table table){
		Set<Table> refs = new HashSet<>();
		refs.add(table);
		table.getColumns().forEach(c->{
			if (c.getForeignKey() != null) {
				Table t = c.getForeignKey().getTable();
				if (!refs.contains(t))
					refs.add(t);
			}
		});
		return refs.stream().collect(Collectors.toList());
	}
	
	public String migrateData(Table table) {
		List<Table> refs = getDependences(table);
		Map<String, DataList> map = getDataMap(refs);
		StringBuilder builder = new StringBuilder();
		if (table.isMigrateData()) {
			builder.append("INSERT INTO ");
			builder.append(table.getName());
			builder.append("(");
			IntCounter i = new IntCounter();
			table.getColumns().forEach(c->{
				if (c.isMigrateValue()) {
					if (i.getThenInc() > 0)
						builder.append(", ");
					builder.append(c.getName());
				}
			});
			builder.append(") VALUES");
		}
		DataList tList = map.get(table.getName());
		IntCounter lc = new IntCounter();
		tList.getLines().forEach(line->{
			if (lc.getThenInc() > 0)
				builder.append(",");
			builder.append("\n(");
			IntCounter i = new IntCounter();
			table.getColumns().forEach(c->{
				if (c.isMigrateValue()) {
					if (i.getThenInc() > 0)
						builder.append(", ");
					Object value = null;
					if (c.getForeignKey() == null) {
						value = line.getValue(c.getName());
					}else {
						Object oldKey = line.getValue(c.getName());
						DataList fkList = map.get(c.getForeignKey().getTable().getName());
						value = fkList.getNewKey(oldKey);
					}
					builder.append(DataTypes.toDb(value, c.getType()));
				}
			});
			builder.append(")");
		});
		builder.append(";");
		return builder.toString();
	}
	
	public void saveFile(String path, String content) throws IOException {
		File file = new File(path);
		FileWriter writer = new FileWriter(file);
		writer.write(content);
		writer.close();
	}
	
	public void createFiles(File directory) throws IOException {
		int i = 0;
		if (directory.isDirectory()) {
			for(Table t : list) {
				String path = String.format("%s\\V%d_Create_Table_%s.sql",directory.getPath(),++i, t.getClassName());
				String sql = getTableCreateSql(t);
				saveFile(path, sql);
				path = String.format("%s\\V%d_Insert_Data_Into_%s.sql",directory.getPath(),++i, t.getClassName());
				sql = migrateData(t);
				saveFile(path, sql);
			}
			String path = String.format("%s\\V%d_Create_Foreign_Keys.sql",directory.getPath(),++i);
			String sql = getAllFksCreateSql();
			saveFile(path, sql);
		}
	}
}
