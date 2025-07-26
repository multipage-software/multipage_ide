/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.Graphics;

import javax.swing.JLabel;

import org.multipage.util.Safe;

/**
 * Renderer that displays label.
 * @author vakol
 *
 */
public class RendererJLabel extends JLabel {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Is selected flag.
	 */
	protected boolean isSelected;
	
	/**
	 * Has focus flag.
	 */
	protected boolean hasFocus;
	
	/**
	 * Constructor.
	 */
	public RendererJLabel() {
		try {
			
			setOpaque(false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set properties.
	 */
	public RendererJLabel set(boolean isSelected, boolean hasFocus, int index) {
		try {
			
			this.isSelected = isSelected;
			this.hasFocus = hasFocus;
		
			setBackground(Utility.itemColor(index));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};

		return this;
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
