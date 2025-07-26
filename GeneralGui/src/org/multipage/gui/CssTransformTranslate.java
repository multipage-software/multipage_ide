/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import org.multipage.util.Safe;

/**
 * Calss for translation object.
 * @author vakol
 *
 */
public class CssTransformTranslate extends CssTransform {

	/**
	 * Parameters.
	 */
	public float tx,ty;
	public String txUnits, tyUnits;
	
	/**
	 * Constructor.
	 */
	public CssTransformTranslate() {
		
		this.tx = 0.0f;
		this.ty = 0.0f;
		this.txUnits = "px";
		this.tyUnits = "px";
	}
	
	/**
	 * Get string value.
	 */
	@Override
	public String toString() {
		
		try {
			return String.format("translate(%s%s, %s%s)",
					Utility.removeFloatNulls(String.valueOf(tx)),
					txUnits,
					Utility.removeFloatNulls(String.valueOf(ty)),
					tyUnits
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
	public void setFrom(CssTransformTranslate translate) {
		try {
			
			this.tx = translate.tx;
			this.ty = translate.ty;
			this.txUnits = translate.txUnits;
			this.tyUnits = translate.tyUnits;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
