package br.com.editor.model.model;

import java.io.Serializable;

/**
 * Classe base para os tipos Table e Column
 * @author Roger
 */
public abstract class Element implements Serializable {

	private static final long serialVersionUID = 7839635371463600807L;

	private String name;

	public Element() {
		super();
	}
	public Element(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	protected String getNormalizatedName(boolean startUper) {
		boolean nextUpper = startUper;
		char[] ca = name.toCharArray();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < ca.length; i++) {
			if (ca[i] == '_') {
				nextUpper = true;
			}else {
				if (nextUpper) {
					builder.append(Character.toUpperCase(ca[i]));
					nextUpper = false;
				}
				else {
					builder.append(ca[i]);
				}
			}
		}
		return builder.toString();
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public int getNameWidth(){
		char[] ca = name.toCharArray();
		int width = 0;
		for (char c : ca) {
			width += Character.isUpperCase(c) ? 7 : 5;
		}
		return width;
	}
		
}
