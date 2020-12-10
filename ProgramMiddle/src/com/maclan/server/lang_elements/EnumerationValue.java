/*
 * Copyright 2010-2017 (C) sechance
 * 
 * Created on : 26-04-2017
 *
 */

package com.maclan.server.lang_elements;

import org.graalvm.polyglot.HostAccess;

/**
 * @author
 *
 */
public class EnumerationValue implements BoxedObject {
	
	/**
	 * Middle layer object.
	 */
	com.maclan.EnumerationValue enumerationValue;
	
	/**
	 * Public fields.
	 */
	@HostAccess.Export
	public final long id;
	@HostAccess.Export
	public final String value;
	
	/**
	 * Constructor.
	 * @param value
	 */
	public EnumerationValue(com.maclan.EnumerationValue enumerationValue) {
		
		this.enumerationValue = enumerationValue;
		
		this.id = enumerationValue.getId();
		this.value = enumerationValue.getValue();
	}

	/**
	 * Unbox object.
	 */
	@Override
	public Object unbox() {
		
		return enumerationValue;
	}

	/**
	 * Convert to string.
	 */
	@Override
	public String toString() {
		
		return value;
	}
}
