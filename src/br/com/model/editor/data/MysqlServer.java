package br.com.model.editor.data;

import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import br.com.model.editor.model.Column;

public class MysqlServer extends Server {

	private static final long serialVersionUID = -7204815227892538768L;

	public MysqlServer() {
	}

	public MysqlServer(String server, int port, String userName, String password){
		this.server = server;
		if (port >= 0)
			this.port = port;
		this.userName = userName;
		this.password = password;
	}

	public MysqlServer(String server, int port, String databaseName, String userName, String password){
		this.server = server;
		if (port >= 0)
			this.port = port;
		this.name = databaseName;
		this.userName = userName;
		this.password = password;
	}

	public MysqlServer(String server, String databaseName, String userName, String password){
		this.server = server;
		this.port = 3306;
		this.name = databaseName;
		this.userName = userName;
		this.password = password;
	}

	public MysqlServer(String databaseName, String userName, String password){
		this.server = "localhost";
		this.port = 3306;
		this.name = databaseName;
		this.userName = userName;
		this.password = password;
	}

	@Override
	public Connection getConnection() {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append("jdbc:mysql://");
		urlBuilder.append(server);
		urlBuilder.append(":");
		urlBuilder.append(port);
		urlBuilder.append("/");
		if (name != null)
			urlBuilder.append(name);
		urlBuilder.append("?useTimezone=true&serverTimezone=UTC&useSSL=false");
		//urlBuilder.append("?useTimezone=true&serverTimezone=UTC&useSSL=false&verifyServerCertificate=false");
		ConnectionFactory connectionFactory = new ConnectionFactory(urlBuilder.toString(), userName, password);
		return connectionFactory.getConnection();
	}

	@Override
	public String surrogateKeyDbType() {
		return "INTEGER";
	}

	@Override
	public String getDatabaseType(Column column) {
		if (column.isSurrogateKey())
			return getDatabaseSurrogateKeyType();
		return getDatabaseType(column.getType());
	}

	@Override
	public List<String> getDatabaseTypes() {
		List<String> types = new ArrayList<String>();
		types.add("VARCHAR");
		types.add("CHAR");
		types.add("TEXT");
		types.add("LONGTEXT");
		types.add("BIT");
		types.add("TINYINT");
		types.add("INT");
		types.add("BIGINT");
		types.add("FLOAT");
		types.add("DOUBLE");
		types.add("DECIMAL");
		types.add("DATETIME");
		types.add("DATE");
		types.add("TIME");
		return types;
	}

	@Override
	public String getDatabaseType(Class<?> javaType) {
		if (javaType.equals(String.class))
			return "VARCHAR";
		else if (javaType.equals(Character.class))
			return "CHAR";
		else if (javaType.equals(Boolean.class))
			return "BIT";
		else if (javaType.equals(Integer.class)){
			return "INT";
		}else if (javaType.equals(Long.class))
			return "BIGINT";
		else if (javaType.equals(Float.class))
			return "FLOAT";
		else if (javaType.equals(Double.class))
			return "DOUBLE";
		else if (javaType.equals(LocalDateTime.class)||javaType.equals(Date.class))
			return "DATETIME";
		else if (javaType.equals(LocalDate.class))
			return "DATE";
		else if (javaType.equals(LocalTime.class))
			return "TIME";
		else if (javaType.equals(Byte[].class))
			return "LONGBLOB";
		return "VARCHAR";
	}

	@Override
	public String getDatabaseSurrogateKeyType() {
		return "INT";
	}

}
