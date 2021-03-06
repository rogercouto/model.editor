package br.com.model.editor.tools;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

public class Util {

	public static void boldFont(Control control){
		Font font = control.getFont();
		FontData[] fd = font.getFontData();
		fd[0].setStyle(SWT.BOLD);
		control.setFont(new Font(Display.getDefault(),fd));
	}

	public static void boldFont(TableItem item, int index){
		Font font = item.getFont(index);
		FontData[] fd = font.getFontData();
		fd[0].setStyle(SWT.BOLD);
		item.setFont(index, new Font(Display.getDefault(),fd));
	}

	public static boolean isUpperCased(String text){
		char[] ca = text.toCharArray();
		boolean lower = false;
		for (char c : ca) {
			if (Character.isLetter(c)&&Character.isLowerCase(c)){
				lower = true;
				break;
			}
		}
		return !lower;
	}

	public static String toSingular(String text){
		if (text.length() > 1 && text.toUpperCase().charAt(text.length()-1)=='S')
			return text.substring(0, text.length()-1);
		return text;
	}

	public static int countChar(char c, String string){
		int count = 0;
		char[] ca = string.toCharArray();
		for (char ac : ca) {
			if (Character.compare(ac, c) == 0)
				count++;
		}
		return count;
	}

	public static int countSpaces(String line[]){
		int count = 0;
		for (String string : line) {
			if (string.trim().length() == 0)
				count++;
		}
		return count;
	}

	@Deprecated
	public boolean isNumeric(Class<?> type){
		HashSet<Class<?>> set = new HashSet<>();
		set.add(Short.class);
		set.add(Integer.class);
		set.add(Long.class);
		set.add(Float.class);
		set.add(Double.class);
		set.add(Number.class);
		set.add(BigDecimal.class);
		return set.contains(type);
	}

	public static Object copyObject(Object object){
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos;
				oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
			} catch (IOException | ClassNotFoundException e) {
			}
		return null;
	}

	/**
	 * https://stackoverflow.com/questions/453018/number-of-lines-in-a-file-in-java
	 */
	public static int countLines(String fileName) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(fileName));
	    try {
	        byte[] c = new byte[1024];
	        int readChars = is.read(c);
	        if (readChars == -1) {
	            // bail out if nothing to read
	            return 0;
	        }
	        // make it easy for the optimizer to tune this loop
	        int count = 0;
	        while (readChars == 1024) {
	            for (int i=0; i<1024;) {
	                if (c[i++] == '\n') {
	                    ++count;
	                }
	            }
	            readChars = is.read(c);
	        }
	        // count remaining characters
	        while (readChars != -1) {
	            for (int i=0; i<readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	            readChars = is.read(c);
	        }
	        return count == 0 ? 1 : count;
	    } finally {
	        is.close();
	    }
	}

	public static String getTableName(String fileName) {
		String[] s1 = fileName.replace('\\' , '.').split("[.]");
		if (s1.length < 2)
			return "";
		return s1[s1.length-2];
	}

	public static boolean contains(char[] ca, char c){
		for (char ac : ca) {
			if (ac == c)
				return true;
		}
		return false;
	}

	public static String concat(Set<String> set){
		StringBuilder builder = new StringBuilder();
		set.forEach(e->{
			builder.append(e);
		});
		return builder.toString();
	}

	public static String getExtension(String fileName){
		String[] s = fileName.split("[.]");
		if (s.length > 0)
			return s[s.length-1];
		return null;
	}

	public static boolean isRealNum(String numberStr) {
		if (numberStr.trim().length() == 0)
			return false;
		char[] ca = numberStr.toCharArray();
		for (int i = 0; i < ca.length; i++) {
			if (!Character.isDigit(ca[i]))
				return false;
		}
		return true;
	}

	public static void errorMessage(Shell parent, String message) {
		MessageBox box = new MessageBox(parent, SWT.ICON_ERROR);
		box.setText("Error");
		box.setMessage(message);
		box.open();
	}

	public static void infoMessage(Shell parent, String message) {
		MessageBox box = new MessageBox(parent, SWT.ICON_INFORMATION);
		box.setText("Info");
		box.setMessage(message);
		box.open();
	}

}
