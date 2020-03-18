package br.com.model.editor.data;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.swt.widgets.ProgressBar;

import br.com.model.editor.model.Column;
import br.com.model.editor.model.Table;

/**
 * Utilitario pra fzer engenharia reversa do banco de dados
 * @author Tecnico
 */
public class ReverseEng {

	private Server server = null;

	public ReverseEng(Server server) {
		super();
		this.server = server;
	}

	/**
	 * Retorna os esquemas referente a um banco de dados
	 * @return Lista de esquemas
	 */
	public List<Table> getTables(){
		List<Table> list = new LinkedList<>();
		try {
			Connection connection = server.getConnection();
			DatabaseMetaData md = connection.getMetaData();
			//Get all tables of database
			ResultSet result = md.getTables(server.name, null, null, new String[] { "TABLE" });
			ResultSetMetaData rmd = result.getMetaData();
			while (result.next()){
				Table table = new Table();
				for (int i = 1; i <= rmd.getColumnCount(); i++) {
					if (rmd.getColumnName(i).toUpperCase().compareTo("TABLE_NAME") == 0){
						//get table name
						String tableName = result.getString(i);
						table.setName(tableName);
						ResultSet result2 = md.getColumns(server.name, null, tableName, null);
						ResultSetMetaData rmd2 = result2.getMetaData();
						while (result2.next()){
							String columnName = null;
							int columnSize = -1;
							String dbTypeName = null;
							boolean notNull = false;
							boolean autoIncrement = false;
							for (int j = 1; j <= rmd2.getColumnCount(); j++) {
								if (rmd2.getColumnName(j).toUpperCase().compareTo("COLUMN_NAME") == 0){
									columnName = result2.getString(j);
								}else if (rmd2.getColumnName(j).toUpperCase().compareTo("TYPE_NAME") == 0){
									dbTypeName = result2.getString(j);
								}else if (rmd2.getColumnName(j).toUpperCase().compareTo("COLUMN_SIZE") == 0){
									columnSize = result2.getInt(j);
								}else if (rmd2.getColumnName(j).toUpperCase().compareTo("IS_NULLABLE") == 0) {
									notNull = result2.getString(j).compareTo("NO") == 0;
								}else if (rmd2.getColumnName(j).toUpperCase().compareTo("IS_AUTOINCREMENT") == 0) {
									autoIncrement = result2.getString(j).compareTo("NO") != 0;
								}
							}
							if (columnName != null && columnSize >= 0 && dbTypeName !=null){
								Column column = new Column();
								column.setName(columnName);
								column.setType(DataTypes.getJavaType(dbTypeName, columnSize));
								column.setDbType(dbTypeName);
								column.setNotNull(notNull);
								column.setSurrogateKey(autoIncrement);
								column.setMigrateValue(true);
								table.addColumn(column);
							}else{
								throw new RuntimeException("Somthing wrong is not right!");
							}
						}
						result2.close();
					}
				}
				table.setMigrateData(true);
				list.add(table);
			}
			//Get prinary keys and unique indexes
			result.close();
			for (Table table : list) {
				result = md.getIndexInfo(server.name, null, table.getName(), false, false);
				rmd = result.getMetaData();
				while (result.next()){
					String columnName = null;
					boolean pk = false;
					boolean un = false;
					for (int i = 1; i <= rmd.getColumnCount(); i++) {
						if (rmd.getColumnName(i).toUpperCase().compareTo("COLUMN_NAME") == 0)
							columnName = result.getString(i);
						else if (rmd.getColumnName(i).toUpperCase().compareTo("INDEX_NAME") == 0){
							String indexName = result.getString(i);
							if (indexName.toUpperCase().compareTo("PRIMARY") == 0 || indexName.toUpperCase().contains("_PKEY"))
								pk = true;
							else if (indexName.toUpperCase().contains("_UNIQUE"))
								un = true;
							else if (indexName.toUpperCase().contains("UK"))
								un = true;
						}else if (rmd.getColumnName(i).toUpperCase().compareTo("NON_UNIQUE") == 0) {
							un = result.getString(i).compareTo("NO") == 0;
						}
					}
					if (columnName != null){
						if (pk)
							table.getColumn(columnName).setPrimaryKey(true);
						if (un)
							table.getColumn(columnName).setUnique(true);
					}
				}
			}
			result.close();
			//get foreign keys
			for (Table table : list) {
				List<Table> otherTables = list.stream().filter(s -> !s.equals(table)).collect(Collectors.toList());
				for (Table ot : otherTables) {
					result = md.getCrossReference(server.name, null, ot.getName(), null, null, table.getName());
					rmd = result.getMetaData();
					while (result.next()){
						String fkColumnName = null;
						String pkColumnName = null;
						for (int i = 1; i <= rmd.getColumnCount(); i++) {
							if (rmd.getColumnName(i).toUpperCase().compareTo("PKCOLUMN_NAME") == 0){
								pkColumnName = result.getString(i);
							}else if (rmd.getColumnName(i).toUpperCase().compareTo("FKCOLUMN_NAME") == 0){
								fkColumnName = result.getString(i);
							}

						}
						Column ref = ot.getColumn(pkColumnName);
						Column fk = table.getColumn(fkColumnName);
						if (fk != null)
							table.getColumn(fkColumnName).setForeignKey(ref);
					}
					result.close();
				}
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static final int INC = 1000;
	
	/**
	 * Retorna os esquemas referente a um banco de dados
	 * @return Lista de esquemas
	 */
	@Deprecated
	public List<Table> getTables(ProgressBar bar){
		bar.setMaximum(50);
		List<Table> list = new LinkedList<>();
		try {
			Connection connection = server.getConnection();
			DatabaseMetaData md = connection.getMetaData();
			//Get all tables of database
			ResultSet result = md.getTables(server.name, null, null, new String[] { "TABLE" });
			ResultSetMetaData rmd = result.getMetaData();
			while (result.next()){
				Table schema = new Table();
				for (int i = 1; i <= rmd.getColumnCount(); i++) {
					if (rmd.getColumnName(i).toUpperCase().compareTo("TABLE_NAME") == 0){
						//get table name
						String tableName = result.getString(i);
						schema.setName(tableName);
						ResultSet result2 = md.getColumns(server.name, null, tableName, null);
						ResultSetMetaData rmd2 = result2.getMetaData();
						while (result2.next()){
							String columnName = null;
							int columnSize = -1;
							String dbTypeName = null;
							for (int j = 1; j <= rmd2.getColumnCount(); j++) {
								if (rmd2.getColumnName(j).toUpperCase().compareTo("COLUMN_NAME") == 0){
									columnName = result2.getString(j);
								}else if (rmd2.getColumnName(j).toUpperCase().compareTo("TYPE_NAME") == 0){
									dbTypeName = result2.getString(j);
								}else if (rmd2.getColumnName(j).toUpperCase().compareTo("COLUMN_SIZE") == 0){
									columnSize = result2.getInt(j);
								}
							}
							if (columnName != null && columnSize >= 0 && dbTypeName !=null){
								Column column = new Column();
								column.setName(columnName);
								column.setType(DataTypes.getJavaType(dbTypeName, columnSize));
								column.setDbType(dbTypeName);
								schema.addColumn(column);
							}else{
								throw new RuntimeException("Somthing wrong is not right!");
							}
						}
						result2.close();
					}
				}
				list.add(schema);
			}
			bar.setSelection(bar.getSelection()+INC);
			//Get prinary keys and unique indexes
			result.close();
			for (Table schema : list) {
				result = md.getIndexInfo(server.name, null, schema.getName(), false, false);
				rmd = result.getMetaData();
				while (result.next()){
					String columnName = null;
					boolean pk = false;
					boolean un = false;
					for (int i = 1; i <= rmd.getColumnCount(); i++) {
						if (rmd.getColumnName(i).toUpperCase().compareTo("COLUMN_NAME") == 0)
							columnName = result.getString(i);
						else if (rmd.getColumnName(i).toUpperCase().compareTo("INDEX_NAME") == 0){
							String indexName = result.getString(i);
							if (indexName.toUpperCase().compareTo("PRIMARY") == 0 || indexName.toUpperCase().contains("_PKEY"))
								pk = true;
							else if (indexName.toUpperCase().contains("_UNIQUE"))
								un = true;
						}
					}
					if (columnName != null){
						if (pk)
							schema.getColumn(columnName).setPrimaryKey(true);
						if (un)
							schema.getColumn(columnName).setUnique(true);
					}
				}
			}
			bar.setSelection(bar.getSelection()+INC);
			result.close();
			//get foreign keys
			for (Table schema : list) {
				List<Table> otherTables = list.stream().filter(s -> !s.equals(schema)).collect(Collectors.toList());
				for (Table ot : otherTables) {
					result = md.getCrossReference(server.name, null, ot.getName(), null, null, schema.getName());
					rmd = result.getMetaData();
					while (result.next()){
						String fkColumnName = null;
						String pkColumnName = null;
						for (int i = 1; i <= rmd.getColumnCount(); i++) {
							if (rmd.getColumnName(i).toUpperCase().compareTo("PKCOLUMN_NAME") == 0){
								pkColumnName = result.getString(i);
							}else if (rmd.getColumnName(i).toUpperCase().compareTo("FKCOLUMN_NAME") == 0){
								fkColumnName = result.getString(i);
							}

						}
						Column ref = ot.getColumn(pkColumnName);
						Column fk = schema.getColumn(fkColumnName);
						if (fk != null)
							schema.getColumn(fkColumnName).setForeignKey(ref);
					}
					result.close();
				}
				bar.setSelection(bar.getSelection()+INC);
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static void main(String[] args) {
		Server server = new MysqlServer("biblioteca_api", "root", "admin");
		ReverseEng re = new ReverseEng(server);
		re.getTables()
		.stream()
		.filter(t->t.getName().compareTo("user") == 0)
		.forEach(t->{
			System.out.println(t);
		});
	}

}
