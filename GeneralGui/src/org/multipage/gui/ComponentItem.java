/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import org.multipage.util.SimpleMethodRef;

/**
 * Item of a component.
 * @author vakol
 *
 */
public class ComponentItem {

	/**
	 * Description.
	 */
	private String description;
	
	/**
	 * Method.
	 */
	private SimpleMethodRef method;

	/**
	 * Constructor.
	 * @param description
	 * @param method
	 */
	public ComponentItem(String description, SimpleMethodRef method) {

		this.description = description;
		this.method = method;
	}

	/** (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		return description;
	}

	/**
	 * @return the method
	 */
	public SimpleMethodRef getMethod() {
		return method;
	}
}
