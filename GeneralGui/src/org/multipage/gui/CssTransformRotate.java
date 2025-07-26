/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import org.multipage.util.Safe;

/**
 * Class for rotation object.
 * @author vakol
 *
 */
public class CssTransformRotate extends CssTransform {

	/**
	 * Parameters.
	 */
	public float a;
	public String units;
	
	/**
	 * Constructor.
	 */
	public CssTransformRotate() {
		
		this.a = 0.0f;
		this.units = "deg";
	}
	
	/**
	 * Get string value.
	 */
	@Override
	public String toString() {
		
		try {
			return String.format("rotate(%s%s)",
					Utility.removeFloatNulls(String.valueOf(a)),
					units
					);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Set values.
	 * @param rotate
	 */
	public void setFrom(CssTransformRotate rotate) {
		try {
			
			this.a = rotate.a;
			this.units = rotate.units;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
