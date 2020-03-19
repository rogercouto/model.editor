package br.com.model.editor.model;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.wb.swt.SWTResourceManager;

public class TableModel implements Serializable{

	private static final long serialVersionUID = 1L;

	private Table table;
	private Rectangle rect;

	public TableModel(Table table, Point position) {
		super();
		this.table = table;
		Point size = calculeSize();
		rect = new Rectangle(position.x, position.y, size.x, size.y);
	}

	public Table getTable() {
		return table;
	}
	public Point getPosition() {
		return new Point(rect.x, rect.y);
	}
	public void setPosition(Point position) {
		Point size = calculeSize();
		rect = new Rectangle(position.x, position.y, size.x, size.y);
	}
	public Rectangle getRect(){
		return rect;
	}

	public void setTable(Table table) {
		this.table = table;
		setPosition(new Point(rect.x, rect.y));
	}

	public void setRect(Rectangle rect) {
		this.rect = rect;
	}

	private static int calculeWidth(Table t){
		OptionalInt oMax = t.getElements().stream().mapToInt(e->e.getNameWidth()).max();
		if (oMax.isPresent()){
			if (t.getNameWidth()+150 > oMax.getAsInt())
				return t.getNameWidth()+150;
		}
		return oMax.isPresent()?oMax.getAsInt()+50:200;
	}

	private static int calculeHeigth(Table t){
		return t.getColumns().size() * 20 + 25;
	}


	public Point calculateSize(Table table){
		Point size = new Point(calculeWidth(table), calculeHeigth(table));
		table.getSubtables().forEach(t->{
			Point sSize = calculateSize(t);
			size.x = Math.max(size.x,sSize.x+10);
			size.y += sSize.y+5;
		});
		return size;
	}

	public Point calculeSize(){
		return calculateSize(table);
	}

	public void draw(GC gc, int hSel, int vSel, boolean selected){
		Rectangle tNameRect = new Rectangle(rect.x-hSel, rect.y-vSel, rect.width, 20);
		Rectangle rectangle = new Rectangle(rect.x-hSel, rect.y-vSel, rect.width, rect.height);
		gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		gc.fillRectangle(rectangle);
		gc.drawRectangle(rectangle);
		if (selected)
			gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		else
			gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		gc.fillRectangle(tNameRect);
		gc.drawRectangle(tNameRect);
		gc.drawLine(rect.x-hSel, rect.y-vSel+20, rect.width+rect.x-hSel, rect.y-vSel+20);
		StringBuilder nameBuilder = new StringBuilder();
		nameBuilder.append(getTable().getName());
		if (selected)
			gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		else
			gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		gc.drawText(nameBuilder.toString(), rect.x-hSel+3, rect.y-vSel+3);
		gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		table.getColumns().forEach(c->{
			int yDesl = getTable().getElementIndex(c.getName());
			gc.drawText(c.getName(), rect.x-hSel+25, rect.y-vSel+20*yDesl+25);
			if (c.isPrimaryKey() && c.getForeignKey() == null){
				gc.drawImage(SWTResourceManager.getImage(TableModel.class, "/icon/key0.png"), rect.x-hSel+5, rect.y-vSel+20*yDesl+25);
			}else if (c.getForeignKey() != null && !c.isPrimaryKey()){
				gc.drawImage(SWTResourceManager.getImage(TableModel.class, "/icon/link.png"), rect.x-hSel+5, rect.y-vSel+20*yDesl+25);
			}else if (c.getForeignKey() != null && c.isPrimaryKey()){
				gc.drawImage(SWTResourceManager.getImage(TableModel.class, "/icon/link_key_0.png"), rect.x-hSel+5, rect.y-vSel+20*yDesl+25);
			}
		});
		table.getSubtables().forEach(t->{
			int yDesl = getTable().getElementIndex(t.getName());
			TableModel model = new TableModel(t, new Point(rect.x-hSel+5, rect.y-vSel+20*yDesl+25));
			model.draw(gc, hSel, vSel, selected);
		});
	}

	public void drawRelations(GC gc, List<TableModel> otherModels, int hSel, int vSel){
		table.getForeignKeys().forEach(fk->{
			Optional<TableModel> opt = otherModels.stream().filter(m -> m.table.equals(fk.getForeignKey().getTable())).findFirst();
			if (opt.isPresent()){
				TableModel fkModel = opt.get();
				int xdist = (fkModel.rect.x - (rect.x-hSel+rect.width)) / 2;
				gc.drawLine(rect.x-hSel+rect.width, rect.y-vSel+rect.height/2, rect.x-hSel+rect.width+xdist, rect.y-vSel+rect.height/2);
				gc.drawLine(fkModel.rect.x-hSel-xdist, fkModel.rect.y-vSel+fkModel.rect.height/2, fkModel.rect.x-hSel, fkModel.rect.y-vSel+fkModel.rect.height/2);
				gc.drawLine(rect.x-hSel+rect.width+xdist, rect.y-vSel+rect.height/2, fkModel.rect.x-hSel-xdist, fkModel.rect.y-vSel+fkModel.rect.height/2);
			}
		});
	}

}
