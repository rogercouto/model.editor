package br.com.model.editor.tools;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

public class Screen {

	/**
	 * Função que posiciona um shell no centro da tela
	 * @param shell
	 */
	public static synchronized void centralize(Shell shell) {
		Shell parent = (Shell)shell.getParent();
		if (parent != null){
			int x = parent.getLocation().x + ((parent.getSize().x- shell.getSize().x)/2);
			int y = parent.getLocation().y + ((parent.getSize().y- shell.getSize().y)/2);
			shell.setLocation(new Point(x,y));
		}else{
			Rectangle r = shell.getDisplay().getClientArea();
			int x = (r.width- shell.getSize().x)/2;
			int y = (r.height- shell.getSize().y)/2;
			shell.setLocation(new Point(x,y));
		}
	}

	public static synchronized void centralize(Shell shell, Monitor monitor) {
		Rectangle r = monitor.getClientArea();
		int x = (r.width- shell.getSize().x)/2;
		int y = (r.height- shell.getSize().y)/2;
		shell.setLocation(new Point(x,y));
	}

	public static Point getCenter(Shell shell){
		Rectangle r = shell.getDisplay().getClientArea();
		int x = (r.width- shell.getSize().x)/2;
		int y = (r.height- shell.getSize().y)/2;
		return new Point(x,y);
	}
	/**
	 * Método que posiciona um Shell no centro do parent (se houver)
	 * @param shell
	 * @param parent
	 */
	public static synchronized void centralize(Shell shell,Composite parent) {
		if (parent != null){
			int x = parent.getLocation().x + ((parent.getSize().x- shell.getSize().x)/2);
			int y = parent.getLocation().y + ((parent.getSize().y- shell.getSize().y)/2);
			shell.setLocation(new Point(x,y));
		}else{
			Rectangle r = shell.getDisplay().getClientArea();
			int x = (r.width- shell.getSize().x)/2;
			int y = (r.height- shell.getSize().y)/2;
			shell.setLocation(new Point(x,y));
		}
	}

	public static int getWidth(){
		Rectangle r = Display.getCurrent().getClientArea();
		return r.width;
	}

	public static int getHeight(){
		Rectangle r = Display.getCurrent().getClientArea();
		return r.height;
	}

	public static Point getSize(){
		Rectangle r = Display.getCurrent().getClientArea();
		return new Point(r.width,r.height);
	}

}
