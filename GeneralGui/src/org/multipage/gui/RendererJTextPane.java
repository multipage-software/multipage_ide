/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2021-09-17
 *
 */
 
package org.multipage.gui;

import java.awt.Graphics;

import javax.swing.JTextPane;

import org.multipage.util.Safe;

/**
 * Renderer that displays text panel.
 * @author vakol
 *
 */
public class RendererJTextPane extends JTextPane {

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
	public RendererJTextPane() {
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
	public RendererJTextPane set(boolean isSelected, boolean hasFocus, int index) {
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
