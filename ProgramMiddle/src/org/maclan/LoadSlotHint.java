/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2020-05-25
 *
 */
package org.maclan;

import org.multipage.util.Safe;

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
	
	/**
	 * Create hint code.
	 * @param thisArea
	 * @param superInheritance
	 * @param subInheritance
	 * @return
	 */
	public static int getHint(boolean thisArea, boolean superInheritance, boolean subInheritance) {
		try {
			
			int hint = 0;
			if (thisArea) {
				hint = hint | LoadSlotHint.area;
			}
			if (superInheritance) {
				hint = hint | LoadSlotHint.superAreas;
			}
			if (subInheritance) {
				hint = hint | LoadSlotHint.subAreas;
			}
			return hint;
		}
		catch (Throwable e) {
			Safe.exception(e);
			return area;
		}
	}
}
