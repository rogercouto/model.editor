package br.com.model.editor.data;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import br.com.model.editor.model.Column;

public class PostgresServer extends Server {

	private static final long serialVersionUID = 2666926609387563596L;

	public PostgresServer() {
	}

	public PostgresServer(String server, int port, String userName, String password){
		this.server = server;
		if (port >= 0)
			this.port = port;
		this.userName = userName;
		this.password = password;
	}

	public PostgresServer(String server, int port, String databaseName, String userName, String password){
		this.server = server;
		if (port >= 0)
			this.port = port;
		this.name = databaseName;
		this.userName = userName;
		this.password = password;
	}

	public PostgresServer(String server, String databaseName, String userName, String password){
		this.server = server;
		this.port = 5432;
		this.name = databaseName;
		this.userName = userName;
		this.password = password;
	}

	public PostgresServer(String databaseName, String userName, String password){
		this.server = "localhost";
		this.port = 5432;
		this.name = databaseName;
		this.userName = userName;
		this.password = password;
	}

	@Override
	public Connection getConnection() {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append("jdbc:postgresql://");
		urlBuilder.append(server);
		urlBuilder.append(":");
		urlBuilder.append(port);
		urlBuilder.append("/");
		if (name != null)
			urlBuilder.append(name);
		ConnectionFactory connectionFactory = new ConnectionFactory(urlBuilder.toString(), userName, password);
		return connectionFactory.getConnection();
	}

	@Override
	public List<String> getDatabaseNames(){
		List<String> names = new LinkedList<>();
		try {
			Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT datname FROM pg_database WHERE datistemplate = false;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                names.add(rs.getString(1));
            }
            rs.close();
            ps.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
		return names;
	}

	@Override
	public String surrogateKeyDbType() {
		return "serial";
	}

	@Override
	public String getDatabaseType(Column column) {
		if (column.isSurrogateKey())
			return "serial";
		return getDatabaseType(column.getType());
	}

	@Override
	public List<String> getDatabaseTypes() {
		List<String> types = new ArrayList<String>();
		types.add("serial");
		types.add("varchar");
		types.add("char");
		types.add("bool");
		types.add("integer");
		types.add("bigint");
		types.add("real");
		types.add("double precision");
		types.add("timestamp");
		types.add("date");
		types.add("time");
		return types;
	}

	public static void main(String[] args) {
		PostgresServer server = new PostgresServer("localhost", 5433, "postgres", "admin");
		server.getDatabaseNames().forEach(System.out::println);
		server.setDatabaseName("blog");
		ReverseEng revEng = new ReverseEng(server);
		revEng.getTables().forEach(System.out::println);
	}

	@Override
	public String getDatabaseType(Class<?> javaType) {
		if (javaType.equals(String.class))
			return "varchar";
		else if (javaType.equals(Character.class))
			return "char";
		else if (javaType.equals(Boolean.class))
			return "bool";
		else if (javaType.equals(Integer.class)){
			return "integer";
		}else if (javaType.equals(Long.class))
			return "bigint";
		else if (javaType.equals(Float.class))
			return "real";
		else if (javaType.equals(Double.class))
			return "double precision";
		else if (javaType.equals(LocalDateTime.class)||javaType.equals(Date.class))
			return "timestamp";
		else if (javaType.equals(LocalDate.class))
			return "date";
		else if (javaType.equals(LocalTime.class))
			return "time";
		return "varchar";
	}

	@Override
	public String getDatabaseSurrogateKeyType() {
		return "serial";
	}

}
