/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

import org.multipage.util.Safe;

/**
 * Panel that displays status bar.
 * @author vakol
 *
 */
public class StatusBar extends JPanel {
	
	/**
	 * Status bar height.
	 */
	protected static final int height = 25;
	
	/**
	 * Font size.
	 */
	protected static final int fontSize = 12;
	
	/**
	 * Font.
	 */
	protected static Font font = new Font("SansSerif", Font.PLAIN, fontSize);

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public StatusBar() {
		try {
			
			setPreferredSize(new Dimension(0, height));
			setBackground(new Color(220, 220, 220));
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
			
			int width = getWidth();
			
			// Draw top line.
			g.setColor(Color.BLACK);
			g.drawLine(0, 0, width, 0);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
