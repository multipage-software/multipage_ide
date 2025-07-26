/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan.server;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * Helper class for set of objects.
 * @author vakol
 *
 */
public class SetObj extends LinkedHashSet {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Get linked list.
	 * @return
	 */
	public LinkedList toList() {
		
		LinkedList list = new LinkedList();
		list.addAll(this);
		
		return list;
	}

	/**
	 * Add all items.
	 * @param collection
	 */
	public void addCollection(Collection collection) {
		
		addAll(collection);
	}
}
