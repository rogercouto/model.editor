package br.com.model.editor.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import br.com.model.editor.data.MysqlServer;
import br.com.model.editor.data.PostgresServer;
import br.com.model.editor.data.Server;
import br.com.model.editor.data.SqliteServer;
import br.com.model.editor.view.ConnectionDialogView;

public class ConnectionDialog extends ConnectionDialogView {

	private Server server = null;
	private List<String> databaseNames = null;

	public ConnectionDialog(Shell parent) {
		super(parent);
		initialize();
	}

	private void initialize(){
		cmbDriver.add("Mysql");
		cmbDriver.add("PostgreSQL");
		//cmbDriver.add("Sqlite");
		txtPort.setEnabled(false);
		txtUser.setEnabled(false);
		txtPassword.setEnabled(false);
		cmbDatabase.setEnabled(false);
	}

	protected void docmbDriverwidgetSelected(SelectionEvent e) {
		switch (cmbDriver.getSelectionIndex()) {
		case 0:
			txtHost.setText("localhost");
			txtPort.setText("3306");
			txtUser.setText("root");
			txtPort.setEnabled(true);
			txtUser.setEnabled(true);
			txtPassword.setEnabled(true);
			cmbDatabase.setEnabled(true);
			break;
		case 1:
			txtHost.setText("localhost");
			txtPort.setText("5432");
			txtUser.setText("postgres");
			txtPort.setEnabled(true);
			txtUser.setEnabled(true);
			txtPassword.setEnabled(true);
			cmbDatabase.setEnabled(true);
			break;
		case 2:
			FileDialog dialog = new FileDialog(shell, SWT.OPEN);
			String fileName = dialog.open();
			if (fileName != null){
				txtHost.setText(fileName);
				txtPort.setText("");
				txtUser.setText("");
				txtPassword.setText("");
				txtPort.setEnabled(false);
				txtUser.setEnabled(false);
				txtPassword.setEnabled(false);
				cmbDatabase.setEnabled(false);
			}
			break;
		default:
			break;
		}
	}

	private void testConnection(Server server){
		try {
			Connection conn = server.getConnection();
			if (conn != null){
				this.server = server;
				databaseNames = this.server.getDatabaseNames();
				cmbDatabase.removeAll();
				databaseNames.forEach(name->{
					cmbDatabase.add(name);
				});
				lblTest.setText("Connection test successfully!");
			}
		} catch (java.lang.RuntimeException e) {
			lblTest.setText("Cannot connect with database!");
		}
	}

	protected void dobtnTestwidgetSelected(SelectionEvent e) {
		int index = cmbDriver.getSelectionIndex();
		Server server = null;
		int port = txtPort.getText().trim().length() > 0 ? Integer.parseInt(txtPort.getText()) : -1;
		switch (index) {
		case 0:
			server = new MysqlServer(txtHost.getText(), port, txtUser.getText(), txtPassword.getText());
			break;
		case 1:
			server = new PostgresServer(txtHost.getText(), port, txtUser.getText(), txtPassword.getText());
			break;
		case 2:
			server = new SqliteServer(txtHost.getText());
			break;
		default:
			break;
		}
		testConnection(server);
	}

	protected void dobtnImportwidgetSelected(SelectionEvent e) {
		String dbName = cmbDatabase.getText();
		try{
			if (dbName.trim().length() > 0){
				dobtnTestwidgetSelected(e);
				server.setDatabaseName(dbName);
				Connection conn = server.getConnection();
				if (conn != null){
					result = server;
					conn.close();
					shell.close();
				}
			}else if (cmbDriver.getSelectionIndex() == 2){
				dobtnTestwidgetSelected(e);
				Connection conn = server.getConnection();
				if (conn != null){
					result = server;
					shell.close();
				}
			}
		} catch (java.lang.RuntimeException | SQLException ex) {
			lblTest.setText("Cannot connect with database!");
		}
	}

	/*
	public static Server openDialog(Shell parent){
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("nix") || os.contains("nux") || os.contains("aix")){
			ConnectionDialogUn c = new ConnectionDialogUn(parent);
			Object res = c.open();
			if (res != null)
				return (Server)res;
		}else{
			ConnectionDialog c = new ConnectionDialog(parent);
			Object res = c.open();
			if (res != null)
				return (Server)res;
		}
		return null;
	}
	*/

}
