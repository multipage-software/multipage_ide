/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 */

package org.multipage.gui;

import org.multipage.util.Safe;

/**
 * Class for 3D scale transformation object.
 * @author vakol
 *
 */
public class CssTransformScale3d extends CssTransform {

	/**
	 * Parameters.
	 */
	public float sx, sy, sz;
	
	/**
	 * Constructor.
	 */
	public CssTransformScale3d() {
		
		this.sx = 0.0f;
		this.sy = 0.0f;
		this.sz = 0.0f;
	}
	
	/**
	 * Get string value.
	 */
	@Override
	public String toString() {
		
		try {
			return String.format("scale3d(%s, %s, %s)",
					Utility.removeFloatNulls(String.valueOf(sx)),
					Utility.removeFloatNulls(String.valueOf(sy)),
					Utility.removeFloatNulls(String.valueOf(sz))
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
	public void setFrom(CssTransformScale3d scale) {
		try {
			
			this.sx = scale.sx;
			this.sy = scale.sy;
			this.sz = scale.sz;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
