/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.multipage.util.Safe;

/**
 * Tooltip window.
 * @author vakol
 *
 */
public class ToolTipWindow extends JWindow {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Font for displayed text.
	 */
	public static Font font = new Font(Font.DIALOG, Font.BOLD, 11);
	
	/**
	 * Label.
	 */
	private JLabel label = new JLabel();

	/**
	 * Constructor.
	 */
	public ToolTipWindow(Component parent) {
		super(Utility.findWindow(parent));
		try {
			
			// Add label to the window.
			setLayout(new BorderLayout());
			JPanel labelWrapper = new JPanel();
			labelWrapper.setBorder(new LineBorder(Color.BLACK));
			label.setBorder(new EmptyBorder(1, 3, 1, 3));	// Label padding: top, left, bottom, right
			label.setFont(font);
			labelWrapper.setLayout(new BorderLayout());
			labelWrapper.add(label, BorderLayout.CENTER);
			add(labelWrapper, BorderLayout.CENTER);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Show window.
	 */
	public void showw(Point topleft, String tooltip) {
		try {
			
			// Set window position.
			setLocation(topleft);
			
			// Set label.
			label.setText(tooltip);
			pack();
			
			setVisible(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Hide window.
	 */
	public void hidew() {
		try {
			
			setVisible(false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set background color.
	 * @param color
	 */
	public void setBackgroundLabel(Color color) {
		try {
			
			label.setBackground(color);
			label.setOpaque(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
