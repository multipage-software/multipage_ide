/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2020-03-10
 *
 */
package org.maclan.server;

import java.util.HashMap;
import java.util.LinkedList;


/**
 * CSS lookup table.
 * @author vakol
 *
 */
@SuppressWarnings("serial")
class CssLookupTable extends HashMap<String, LinkedList<CssLookupTableValue>> {

	/**
	 * Insert new value.
	 * @param slotName
	 * @param propertyName
	 * @param isImportant
	 * @param process
	 */
	public void insert(String slotName, String propertyName, boolean isImportant, boolean process) {
		
		// Get values list (create one if it is needed).
		LinkedList<CssLookupTableValue> values = get(slotName);
		if (values == null) {
			
			values = new LinkedList<CssLookupTableValue>();
			put(slotName, values);
		}
		
		// Add new value.
		CssLookupTableValue value = new CssLookupTableValue(propertyName, isImportant, process);
		values.add(value);
	}
}
