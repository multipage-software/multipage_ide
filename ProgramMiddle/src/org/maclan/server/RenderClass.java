/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan.server;

/**
 * Class of rendered objects.
 * @author vakol
 *
 */
public class RenderClass {

	/**
	 * Render class name.
	 */
	private String name;
	
	/**
	 * Render class description.
	 */
	private String text;

	/**
	 * Constructor.
	 * @param name
	 * @param text
	 */
	public RenderClass(String name, String text) {
		
		this.setName(name);
		this.setText(text);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
}
