package br.com.editor.model.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Label;

public class ModelEditorCompView extends Composite {

	protected Canvas canvas;
	protected int hSelection;
	protected int vSelection;
	protected ToolBar toolBar;
	protected ToolItem btnCreate;
	protected ToolItem btnEdit;
	protected ToolItem btnDelete;
	protected ToolItem btnUndo;
	protected Text txtDbName;
	protected Label lblNewLabel;
	protected Label lblDb;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ModelEditorCompView(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(4, false);
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		setLayout(gridLayout);
		lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setText("Tables:");
		toolBar = new ToolBar(this, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnCreate = new ToolItem(toolBar, SWT.NONE);
		btnCreate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnCreatewidgetSelected(e);
			}
		});
		btnCreate.setImage(SWTResourceManager.getImage(ModelEditorCompView.class, "/icon/table_add.png"));
		btnCreate.setText("Create");
		btnEdit = new ToolItem(toolBar, SWT.NONE);
		btnEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnEditwidgetSelected(e);
			}
		});
		btnEdit.setEnabled(false);
		btnEdit.setImage(SWTResourceManager.getImage(ModelEditorCompView.class, "/icon/table_edit.png"));
		btnEdit.setText("Edit");
		btnDelete = new ToolItem(toolBar, SWT.NONE);
		btnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnDeletewidgetSelected(e);
			}
		});
		btnDelete.setEnabled(false);
		btnDelete.setImage(SWTResourceManager.getImage(ModelEditorCompView.class, "/icon/table_delete.png"));
		btnDelete.setText("Delete");
		btnUndo = new ToolItem(toolBar, SWT.NONE);
		btnUndo.setEnabled(false);
		btnUndo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnUndowidgetSelected(e);
			}
		});
		btnUndo.setImage(SWTResourceManager.getImage(ModelEditorCompView.class, "/icon/undo16.png"));
		btnUndo.setText("Undo");
		lblDb = new Label(this, SWT.NONE);
		lblDb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDb.setText("DB:");
		txtDbName = new Text(this, SWT.BORDER);
		txtDbName.setText("new_database");
		GridData gd_txtDbName = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txtDbName.widthHint = 200;
		txtDbName.setLayoutData(gd_txtDbName);
		canvas = new Canvas(this, SWT.H_SCROLL | SWT.V_SCROLL);
		canvas.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				docanvaskeyPressed(e);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				docanvasKeyReleased(e);
			}
		});
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		initialize();

	}
	public ModelEditorCompView(Composite parent, int style, int canvasStyle) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		canvas = new Canvas(this, canvasStyle);
		initialize();

	}

	private void initialize() {
		if (canvas.getHorizontalBar() != null){
			canvas.getHorizontalBar().addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					scrollMoved();
				}
			});
		}
		if (canvas.getVerticalBar() != null){
			canvas.getVerticalBar().addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					scrollMoved();
				}
			});
		}
		canvas.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
	}

	@Override
	protected void checkSubclass() {
	}

	protected void scrollMoved(){
		hSelection = canvas.getHorizontalBar().getSelection()*5;
		vSelection = canvas.getVerticalBar().getSelection()*5;
	}
	protected void dobtnCreatewidgetSelected(SelectionEvent e) {
	}
	protected void dobtnEditwidgetSelected(SelectionEvent e) {
	}
	protected void dobtnDeletewidgetSelected(SelectionEvent e) {
	}
	protected void dobtnUndowidgetSelected(SelectionEvent e) {
	}
	protected void docanvaskeyPressed(KeyEvent e) {
	}
	protected void docanvasKeyReleased(KeyEvent e) {
	}
}
