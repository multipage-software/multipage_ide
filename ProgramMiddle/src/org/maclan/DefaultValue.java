/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan;

/**
 * Base class for default values.
 * @author vakol
 *
 */
public class DefaultValue {

	/**
	 * Default value text.
	 */
	@Override
	public String toString() {
		
		return "$default";
	}

	/**
	 * Returns true value if the object is of this type.
	 */
	@Override
	public boolean equals(Object object) {
		
		return object instanceof DefaultValue;
	}
}
