/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan;

/**
 * Class for namespace objects.
 * @author vakol
 *
 */
public class Namespace extends NamespaceElement {
	
	/**
	 * Constructor.
	 * @param description
	 * @param parentid
	 * @param id
	 */
	public Namespace(String description, Long parentid, Long id) {
		super(description, parentid, id);
	}
}
