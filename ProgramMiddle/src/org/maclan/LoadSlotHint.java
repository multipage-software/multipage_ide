/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2020-05-25
 *
 */
package org.maclan;

/**
 * Tells area server where to find slots.
 * @author vakol
 *
 */
public class LoadSlotHint {
	
	public static final int area = 1;			// In a single area.
	public static final int superAreas = 2;		// Inherits from super areas.
	public static final int subAreas = 4;		// Inherits from sub areas.
	
	/**
	 * Hint code.
	 */
	public int code;

	/**
	 * Constructor.
	 * @param code
	 */
	LoadSlotHint(int code) {
		
		this.code = code;
	}
}
