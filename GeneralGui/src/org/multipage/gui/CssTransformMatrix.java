/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import org.multipage.util.Safe;

/**
 * CSS transformation matrix.
 * @author vakol
 *
 */
public class CssTransformMatrix extends CssTransform {

	/**
	 * Parameters.
	 */
	public float a,b,c,d,tx,ty;
	
	/**
	 * Constructor.
	 */
	public CssTransformMatrix() {
		
		this.a = 0.0f;
		this.b = 0.0f;
		this.c = 0.0f;
		this.d = 0.0f;
		this.tx = 0.0f;
		this.ty = 0.0f;
	}
	
	/**
	 * Get string value.
	 */
	@Override
	public String toString() {
		
		try {
			return String.format("matrix(%s, %s, %s, %s, %s, %s)",
					Utility.removeFloatNulls(String.valueOf(a)),
					Utility.removeFloatNulls(String.valueOf(b)),
					Utility.removeFloatNulls(String.valueOf(c)),
					Utility.removeFloatNulls(String.valueOf(d)),
					Utility.removeFloatNulls(String.valueOf(tx)),
					Utility.removeFloatNulls(String.valueOf(ty))
					);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Set values.
	 * @param matrix
	 */
	public void setFrom(CssTransformMatrix matrix) {
		try {
			
			this.a = matrix.a;
			this.b = matrix.b;
			this.c = matrix.c;
			this.d = matrix.d;
			this.tx = matrix.tx;
			this.ty = matrix.ty;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
