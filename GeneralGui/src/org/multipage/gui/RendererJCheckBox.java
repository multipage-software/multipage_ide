/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.Graphics;

import javax.swing.JCheckBox;

import org.multipage.util.Safe;

/**
 * Renderer that displays check box. 
 * @author vakol
 *
 */
public class RendererJCheckBox extends JCheckBox {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Is selected flag.
	 */
	private boolean isSelected;
	
	/**
	 * Has focus flag.
	 */
	private boolean hasFocus;
	
	/**
	 * Constructor.
	 */
	public RendererJCheckBox() {
		
		setOpaque(false);
	}
	
	/**
	 * Set properties.
	 */
	public void set(boolean isSelected, boolean hasFocus, int index) {
		try {
			
			this.isSelected = isSelected;
			this.hasFocus = hasFocus;
			
			setBackground(Utility.itemColor(index));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		try {
			
			super.paint(g);
			GraphUtility.drawSelection(g, this, isSelected, hasFocus);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
