/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import org.multipage.util.Safe;

/**
 * Class for skew transformation object.
 * @author vakol
 *
 */
public class CssTransformSkew extends CssTransform {

	/**
	 * Parameters.
	 */
	public float ax, ay;
	public String axUnits, ayUnits;
	
	/**
	 * Constructor.
	 */
	public CssTransformSkew() {
		
		this.ax = 0.0f;
		this.axUnits = "deg";
		this.ay = 0.0f;
		this.ayUnits = "deg";
	}
	
	/**
	 * Get string value.
	 */
	@Override
	public String toString() {
		
		try {
			return String.format("skew(%s%s, %s%s)",
					Utility.removeFloatNulls(String.valueOf(ax)),
					axUnits,
					Utility.removeFloatNulls(String.valueOf(ay)),
					ayUnits
					);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Set values.
	 * @param skew
	 */
	public void setFrom(CssTransformSkew skew) {
		try {
			
			this.ax = skew.ax;
			this.axUnits = skew.axUnits;
			this.ay = skew.ay;
			this.ayUnits = skew.ayUnits;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
