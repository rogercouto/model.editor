package br.com.model.editor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;

import br.com.model.editor.controller.ConnectionDialog;
import br.com.model.editor.data.MigrateSQL;
import br.com.model.editor.data.MysqlServer;
import br.com.model.editor.data.PostgresServer;
import br.com.model.editor.data.ReverseEng;
import br.com.model.editor.data.Server;
import br.com.model.editor.model.ModelData;
import br.com.model.editor.model.Table;
import br.com.model.editor.tools.Util;
import br.com.model.editor.view.MainShellView;

public class Main extends MainShellView {

	public Main() {
		initialize();
	}

	private void initialize() {
	}

	protected void dobtnOpenDBwidgetSelected(SelectionEvent e) {
		ConnectionDialog dialog = new ConnectionDialog(shell);
		Server server = (Server)dialog.open();
		if (server != null) {
			ReverseEng revEng = new ReverseEng(server);
			List<Table> tables = revEng.getTables();
			modelEditor.setServer(server);
			modelEditor.addTablesAndInit(tables);
			modelEditor.setVisible(true);
			modelEditor.refresh();
			btnExport.setEnabled(true);
			btnSave.setEnabled(true);
		}
	}

	public static void main(String[] args) {
		Main thisClass = new Main();
		thisClass.open();
	}

	protected void doBtnNewMysqlWidgetSelected(SelectionEvent e){
		Server server = new MysqlServer();
		modelEditor.setServer(server);
		modelEditor.initEmpty();
		modelEditor.setVisible(true);
		modelEditor.refresh();
		btnExport.setEnabled(true);
		btnSave.setEnabled(true);
	}

	protected void doBtnNewPostgreSQLWidgetSelected(SelectionEvent e){
		Server server = new PostgresServer();
		modelEditor.setServer(server);
		modelEditor.initEmpty();
		modelEditor.setVisible(true);
		modelEditor.refresh();
		btnExport.setEnabled(true);
		btnSave.setEnabled(true);
	}

	protected void dotltmTestwidgetSelected(SelectionEvent e) {
		if (modelEditor.getServer() == null) {
			Util.errorMessage(shell, "Server not set!");
			return;
		}
		DirectoryDialog dialog = new DirectoryDialog(shell, SWT.SAVE);
		String dirPath = dialog.open();
		if (dirPath == null)
			return;
		File dir = new File(dirPath);
		if (!dir.isDirectory()) {
			Util.errorMessage(shell, "Not a valid directory!");
			return;
		}
		MigrateSQL export = new MigrateSQL(modelEditor.getServer().getDbName(), modelEditor.getServer(), modelEditor.getTables(), modelEditor.getRenames());
		try {
			export.createFiles(dir);
			Util.infoMessage(shell, "Successfully created files!");
		} catch (IOException e1) {
			e1.printStackTrace();
			Util.errorMessage(shell, "Unknown IO Error!");
		}
	}

	protected void dotltmSavewidgetSelected(SelectionEvent e) {
		ModelData data = modelEditor.getModelData();
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setFilterExtensions(new String[]{"*.mef"});
		String fileName = dialog.open();
		if (fileName != null){
			try {
				File file = new File(fileName);
				ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
				stream.writeObject(data);
				stream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	protected void dotltmOpenwidgetSelected(SelectionEvent e) {
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setFilterExtensions(new String[]{"*.mef"});
		String fileName = dialog.open();
		if (fileName != null){
			File file = new File(fileName);
			if (file.exists()){
				try {
					ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
					ModelData data = (ModelData)stream.readObject();
					stream.close();
					modelEditor.setServer(data.getServer());
					modelEditor.addModelsAndInit(data.getModels());
					modelEditor.setRenames(data.getRenames());
					modelEditor.setVisible(true);
					modelEditor.refresh();
					btnExport.setEnabled(true);
					btnSave.setEnabled(true);
				} catch (IOException | ClassNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

}
