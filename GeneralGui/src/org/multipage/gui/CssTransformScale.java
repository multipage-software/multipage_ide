/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import org.multipage.util.Safe;

/**
 * Class for scale transformation object.
 * @author vakol
 *
 */
public class CssTransformScale extends CssTransform {

	/**
	 * Parameters.
	 */
	public float sx, sy;
	
	/**
	 * Constructor.
	 */
	public CssTransformScale() {
		
		this.sx = 0.0f;
		this.sy = 0.0f;
	}
	
	/**
	 * Get string value.
	 */
	@Override
	public String toString() {
		
		try {
			return String.format("scale(%s, %s)",
					Utility.removeFloatNulls(String.valueOf(sx)),
					Utility.removeFloatNulls(String.valueOf(sy))
					);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Set values.
	 * @param scale
	 */
	public void setFrom(CssTransformScale scale) {
		try {
			
			this.sx = scale.sx;
			this.sy = scale.sy;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
