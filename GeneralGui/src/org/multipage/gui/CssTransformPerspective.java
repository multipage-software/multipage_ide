/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-06-26
 *
 */

package org.multipage.gui;

import org.multipage.util.Safe;

/**
 * Class for CSS perspective object.
 * @author vakol
 *
 */
public class CssTransformPerspective extends CssTransform {

	/**
	 * Parameters.
	 */
	public float l;
	public String units;
	
	/**
	 * Constructor.
	 */
	public CssTransformPerspective() {
		
		this.l = 0.0f;
		this.units = "px";
	}
	
	/**
	 * Get string value.
	 */
	@Override
	public String toString() {
		
		try {
			return String.format("perspective(%s%s)",
					Utility.removeFloatNulls(String.valueOf(l)),
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
	 * @param perspective
	 */
	public void setFrom(CssTransformPerspective perspective) {
		try {
			
			this.l = perspective.l;
			this.units = perspective.units;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};	
	}
}
