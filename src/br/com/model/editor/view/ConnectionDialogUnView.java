package br.com.model.editor.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import br.com.model.editor.tools.Util;

public class ConnectionDialogUnView extends Dialog {

	protected Object result;
	protected Shell shell;
	protected Group group;
	protected Label lblHostname;
	protected Label lblDriver;
	protected Label lblUser;
	protected Label lblPassword;
	protected Label lblNewLabel;
	protected Text txtHost;
	protected Combo cmbDriver;
	protected Combo cmbDatabase;
	protected Text txtUser;
	protected Text txtPassword;
	protected Button btnImport;
	protected Button btnTest;
	protected Label lblPort;
	protected Text txtPort;
	protected Label lblTest;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ConnectionDialogUnView(Shell parent) {
		super(parent, SWT.SHELL_TRIM);
		createContents();
		setText("Import from database");
		Util.centralize(shell, getParent());
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(374, 377);
		shell.setText("Connect to database...");
		shell.setLayout(null);
		group = new Group(shell, SWT.NONE);
		group.setBounds(10, 0, 336, 298);
		group.setLayout(null);
		lblDriver = new Label(group, SWT.NONE);
		lblDriver.setBounds(32, 24, 34, 15);
		lblDriver.setText("Driver:");
		cmbDriver = new Combo(group, SWT.READ_ONLY);
		cmbDriver.setBounds(71, 20, 257, 23);
		cmbDriver.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				docmbDriverwidgetSelected(e);
			}
		});
		lblHostname = new Label(group, SWT.NONE);
		lblHostname.setBounds(8, 58, 58, 15);
		lblHostname.setText("Hostname:");
		txtHost = new Text(group, SWT.BORDER);
		txtHost.setBounds(71, 55, 257, 21);
		lblPort = new Label(group, SWT.NONE);
		lblPort.setBounds(41, 93, 25, 15);
		lblPort.setText("Port:");
		txtPort = new Text(group, SWT.BORDER);
		txtPort.setBounds(71, 90, 257, 21);
		txtPort.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent arg0) {
				dotxtPortverifyText(arg0);
			}
		});
		lblUser = new Label(group, SWT.NONE);
		lblUser.setBounds(40, 127, 26, 15);
		lblUser.setText("User:");
		txtUser = new Text(group, SWT.BORDER);
		txtUser.setBounds(71, 125, 257, 21);
		lblPassword = new Label(group, SWT.NONE);
		lblPassword.setBounds(8, 163, 53, 15);
		lblPassword.setText("Password:");
		txtPassword = new Text(group, SWT.BORDER | SWT.PASSWORD);
		txtPassword.setBounds(71, 160, 257, 21);
		btnTest = new Button(group, SWT.NONE);
		btnTest.setBounds(99, 207, 130, 30);
		btnTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnTestwidgetSelected(e);
			}
		});
		btnTest.setText("Test");
		lblTest = new Label(group, SWT.NONE);
		lblTest.setBounds(8, 182, 320, 15);
		lblTest.setAlignment(SWT.CENTER);
		lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setBounds(8, 258, 51, 15);
		lblNewLabel.setText("Database:");
		cmbDatabase = new Combo(group, SWT.NONE);
		cmbDatabase.setBounds(64, 254, 257, 23);
		btnImport = new Button(shell, SWT.NONE);
		btnImport.setBounds(110, 304, 130, 30);
		btnImport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnImportwidgetSelected(e);
			}
		});
		btnImport.setText("Import");
	}

	protected void dobtnTestwidgetSelected(SelectionEvent e) {
	}
	protected void dobtnImportwidgetSelected(SelectionEvent e) {
	}
	protected void dotxtPortverifyText(VerifyEvent arg0) {
		if (arg0.text.trim().length() > 0){
			char[] ca = arg0.text.toCharArray();
			for (char c : ca) {
				if (!Character.isDigit(c))
					arg0.doit = false;
			}
		}
	}

	protected void docmbDriverwidgetSelected(SelectionEvent e) {
	}
}
