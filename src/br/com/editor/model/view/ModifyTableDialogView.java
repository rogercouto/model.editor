package br.com.editor.model.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import br.com.editor.model.tools.Util;

public class ModifyTableDialogView extends Dialog {

	protected Object result;
	protected Shell shell;
	protected Table tblModifier;
	protected Button btnOk;
	protected Composite composite;
	protected Label lblTableName;
	protected Text txtName;
	protected Button btnAdd;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ModifyTableDialogView(Shell parent) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		createContents();
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		Util.centralize(shell, getParent());
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
		shell.setSize(740, 400);
		shell.setLayout(new GridLayout(2, false));
		shell.setText("Edit table");
		composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		composite.setLayout(new GridLayout(2, false));
		lblTableName = new Label(composite, SWT.NONE);
		lblTableName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTableName.setText("Table name:");
		txtName = new Text(composite, SWT.BORDER);
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		tblModifier = new Table(shell, SWT.BORDER);
		tblModifier.setHeaderVisible(true);
		tblModifier.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		tblModifier.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		tblModifier.setLinesVisible(true);
		btnAdd = new Button(shell, SWT.NONE);
		btnAdd.setText("Add column");
		btnAdd.setImage(SWTResourceManager.getImage(ModifyTableDialogView.class, "/icon/add.png"));
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doBtnAddWidgetSelected(e);
			}
		});
		btnAdd.setToolTipText("Add column");
		btnOk = new Button(shell, SWT.NONE);
		btnOk.setImage(SWTResourceManager.getImage(ModifyTableDialogView.class, "/icon/table_save.png"));
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnOkwidgetSelected(e);
			}
		});
		btnOk.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnOk.setText("Save");

	}

	protected void dobtnOkwidgetSelected(SelectionEvent e) {
	}
	protected void doBtnAddWidgetSelected(SelectionEvent e) {
	}
}
