/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import org.multipage.util.Safe;

/**
 * Class for 3D translation object.
 * @author vakol
 *
 */
public class CssTransformTranslate3d extends CssTransform {

	/**
	 * Parameters.
	 */
	public float tx, ty, tz;
	public String txUnits, tyUnits, tzUnits;
	
	/**
	 * Constructor.
	 */
	public CssTransformTranslate3d() {
		
		this.tx = 0.0f;
		this.ty = 0.0f;
		this.tz = 0.0f;
		this.txUnits = "px";
		this.tyUnits = "px";
		this.tzUnits = "px";
	}
	
	/**
	 * Get string value.
	 */
	@Override
	public String toString() {
		
		try {
			return String.format("translate3d(%s%s, %s%s, %s%s)",
					Utility.removeFloatNulls(String.valueOf(tx)),
					txUnits,
					Utility.removeFloatNulls(String.valueOf(ty)),
					tyUnits,
					Utility.removeFloatNulls(String.valueOf(tz)),
					tzUnits
					);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Set values.
	 * @param translate
	 */
	public void setFrom(CssTransformTranslate3d translate) {
		try {
			
			this.tx = translate.tx;
			this.ty = translate.ty;
			this.tz = translate.tz;
			this.txUnits = translate.txUnits;
			this.tyUnits = translate.tyUnits;
			this.tzUnits = translate.tzUnits;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
