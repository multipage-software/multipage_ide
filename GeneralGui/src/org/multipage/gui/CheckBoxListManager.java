/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import org.multipage.util.Obj;

/**
 * Check box content helper.
 * @author vakol
 *
 */
public class CheckBoxListManager<T> {

	/**
	 * Loads item.
	 * @param index
	 * @param text
	 * @param selected
	 * @return
	 */
	protected boolean loadItem(int index, Obj<T> object,
			Obj<String> text, Obj<Boolean> selected) {
		return false;
	}

	/**
	 * Processes change.
	 * @param object
	 * @param selected
	 * @return
	 */
	protected boolean processChange(T object, boolean selected) {
		return false;
	}
}
