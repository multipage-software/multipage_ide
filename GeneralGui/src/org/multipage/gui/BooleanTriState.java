/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import org.multipage.util.Resources;

/**
 * Object that represents the tristate values.
 * @author vakol
 *
 */
public class BooleanTriState {
	
	/**
	 * List of states.
	 */
	public static final BooleanTriState UNKNOWN = new BooleanTriState(null, "org.multipage.gui.textUnknownState");
	public static final BooleanTriState TRUE = new BooleanTriState(true, "org.multipage.gui.textTrueState");
	public static final BooleanTriState FALSE = new BooleanTriState(false, "org.multipage.gui.textFalseState");
	
	/**
	 * Value and its description.
	 */
	public Boolean value;
	private String descriptionId;
	
	/**
	 * Constructor.
	 * @param value
	 * @param descriptionId
	 */
	public BooleanTriState(Boolean value, String descriptionId) {
		this.value = value;
		this.descriptionId = descriptionId;
	}

	/**
	 * Get the value string representation.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Resources.getString(descriptionId);
	}
}
