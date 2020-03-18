package br.com.model.editor.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.SWTResourceManager;

import br.com.model.editor.controller.ModelEditorController;
import br.com.model.editor.tools.Util;

public class MainShellView {

	protected Shell shell;
	protected ToolBar toolBar;
	protected ToolItem btnOpenDB;
	protected ModelEditorController modelEditor;
	protected ToolItem btnNewDatabase;
	protected ToolItem btnExport;

	public MainShellView() {
		createContents();
	}

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MainShellView window = new MainShellView();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_DARK_SHADOW));
		shell.setSize(1280, 600);
		shell.setText("Model editor");
		GridLayout gl_shell = new GridLayout(1, false);
		gl_shell.horizontalSpacing = 0;
		gl_shell.verticalSpacing = 0;
		gl_shell.marginHeight = 0;
		gl_shell.marginWidth = 0;
		shell.setLayout(gl_shell);
		toolBar = new ToolBar(shell, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnNewDatabase = new ToolItem(toolBar, SWT.DROP_DOWN);
		btnNewDatabase.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnNewDatabasewidgetSelected(e);
			}
		});
		btnNewDatabase.setImage(SWTResourceManager.getImage(MainShellView.class, "/icon/add-database_32x32.png"));
		btnNewDatabase.setText("New database");
		btnOpenDB = new ToolItem(toolBar, SWT.NONE);
		btnOpenDB.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnOpenDBwidgetSelected(e);
			}
		});
		btnOpenDB.setImage(SWTResourceManager.getImage(MainShellView.class, "/icon/database32.png"));
		btnOpenDB.setText("Connect");
		btnExport = new ToolItem(toolBar, SWT.NONE);
		btnExport.setEnabled(false);
		btnExport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dotltmTestwidgetSelected(e);
			}
		});
		btnExport.setImage(SWTResourceManager.getImage(MainShellView.class, "/icon/export32.png"));
		btnExport.setText("Export");
		modelEditor = new ModelEditorController(shell, SWT.BORDER);
		modelEditor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		modelEditor.setVisible(false);
		Util.centralize(shell);
	}

	protected void dobtnOpenDBwidgetSelected(SelectionEvent e) {

	}

	private void dobtnNewDatabasewidgetSelected(SelectionEvent e) {
        Menu menu = new Menu(shell, SWT.POP_UP);
        MenuItem item1 = new MenuItem(menu, SWT.PUSH);
        item1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				doBtnNewMysqlWidgetSelected(arg0);
			}
		});
        item1.setText("Mysql");
        MenuItem item2 = new MenuItem(menu, SWT.PUSH);
        item2.setText("PostgreSQL");
        item2.setEnabled(false);
        MenuItem item3 = new MenuItem(menu, SWT.PUSH);
        item3.setText("Sqlite");
        item3.setEnabled(false);
        Point loc = toolBar.getLocation();
        Rectangle rect = toolBar.getBounds();
        Point mLoc = new Point(loc.x-1, loc.y+rect.height);
        menu.setLocation(shell.getDisplay().map(toolBar.getParent(), null, mLoc));
        menu.setVisible(true);
	}

	protected void doBtnNewMysqlWidgetSelected(SelectionEvent e){
	}

	protected void dotltmTestwidgetSelected(SelectionEvent e) {
	}
}
