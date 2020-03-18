package br.com.editor.model;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.DirectoryDialog;

import br.com.editor.model.data.MigrateSQL;
import br.com.editor.model.data.MysqlServer;
import br.com.editor.model.data.ReverseEng;
import br.com.editor.model.data.Server;
import br.com.editor.model.model.Table;
import br.com.editor.model.tools.Util;
import br.com.editor.model.view.MainShellView;

public class Main extends MainShellView {

	public Main() {
		initialize();
	}

	private void initialize() {
		dobtnOpenDBwidgetSelected(null);
	}

	protected void dobtnOpenDBwidgetSelected(SelectionEvent e) {
		//ConnectionDialogController dialog = new ConnectionDialogController(shell);
		//Server server = (Server)dialog.open();
		Server server = new MysqlServer("biblioteca", "root", "admin");
		if (server != null) {
			ReverseEng revEng = new ReverseEng(server);
			List<Table> tables = revEng.getTables();
			modelEditor.setServer(server);
			modelEditor.clear();
			tables.forEach(t->{
				modelEditor.addTable(t);
			});
			modelEditor.calcPositions();
			modelEditor.setVisible(true);
			modelEditor.refresh();
			btnExport.setEnabled(true);
		}
	}

	public static void main(String[] args) {
		Main thisClass = new Main();
		thisClass.open();
	}

	protected void doBtnNewMysqlWidgetSelected(SelectionEvent e){
		Server server = new MysqlServer();
		modelEditor.setServer(server);
		modelEditor.clear();
		modelEditor.calcPositions();
		modelEditor.setVisible(true);
		modelEditor.refresh();
		btnExport.setEnabled(true);
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
	
}
