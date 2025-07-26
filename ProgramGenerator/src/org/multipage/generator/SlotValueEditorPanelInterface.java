/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

/**
 * Interface for slot value.
 * @author vakol
 *
 */
public interface SlotValueEditorPanelInterface {
	
	/**
	 * Get value.
	 */
	Object getValue();

	/**
	 * Set value.
	 * @param value
	 */
	void setValue(Object value);

	/**
	 * Set default state.
	 * @param isDefault
	 */
	void setDefault(boolean isDefault);

	/**
	 * Get value meaning.
	 * @return
	 */
	String getValueMeaning();
}
