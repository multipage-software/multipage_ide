/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2025-04-26
 *
 */

package org.multipage.gui;

import org.multipage.util.Safe;

/**
 * Class for 3D rotation object.
 * @author vakol
 *
 */
public class CssTransformRotate3d extends CssTransform {

	/**
	 * Parameters.
	 */
	public float x, y, z, a;
	public String aUnits;
	
	/**
	 * Constructor.
	 */
	public CssTransformRotate3d() {
		
		this.x = 0.0f;
		this.y = 0.0f;
		this.z = 0.0f;
		this.a = 0.0f;
		this.aUnits = "deg";
	}
	
	/**
	 * Get string value.
	 */
	@Override
	public String toString() {
		
		try {
			return String.format("rotate3d(%s, %s, %s, %s%s)",
					Utility.removeFloatNulls(String.valueOf(x)),
					Utility.removeFloatNulls(String.valueOf(y)),
					Utility.removeFloatNulls(String.valueOf(z)),
					Utility.removeFloatNulls(String.valueOf(a)),
					aUnits
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
	public void setFrom(CssTransformRotate3d rotate) {
		
		try {
			
			this.x = rotate.x;
			this.y = rotate.y;
			this.z = rotate.z;
			this.a = rotate.a;
			this.aUnits = rotate.aUnits;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
