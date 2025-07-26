/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2020-02-18
 *
 */
package org.multipage.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Renderer that displays path information.
 * @author vakol
 *
 */
public class RendererPathItem extends JPanel {
	
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Path not specified string constant.
	 */
	private static String notSpecified = null;

	/**
	 * Is selected flag.
	 */
	private boolean isSelected;
	
	/**
	 * Has focus flag.
	 */
	private boolean hasFocus;
	
	/**
	 * Controls.
	 */
	private JLabel labelCaption;
	private JLabel labelPath;
	
	/**
	 * Set properties.
	 */
	public void set(boolean enabled, boolean isSelected, boolean hasFocus, int index, String caption, String path) {
		try {
			
			this.isSelected = isSelected;
			this.hasFocus = hasFocus;
			
			String thePath = path;
			
			if (enabled) {
				this.labelCaption.setText(" " + caption + " ");
				if (thePath == null || thePath.isEmpty()) {
					
					if (notSpecified == null) {
						notSpecified = Resources.getString("org.mutlipage.gui.textNotSpecified");
					}
					thePath = notSpecified;
				}
				this.labelPath.setText(thePath);
			}
			else {
				// Empty texts.
				this.labelCaption.setText("");
				this.labelPath.setText("");
			}
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

	/**
	 * Create the panel.
	 */
	public RendererPathItem() {
		try {
			
			initComponents();
			setOpaque(false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Initialize components.
	 */
	private void initComponents() {
		setPreferredSize(new Dimension(500, 20));
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		labelCaption = new JLabel("caption");
		springLayout.putConstraint(SpringLayout.NORTH, labelCaption, 3, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, labelCaption, 3, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, labelCaption, -3, SpringLayout.SOUTH, this);
		labelCaption.setFont(new Font("Tahoma", Font.PLAIN, 11));
		labelCaption.setForeground(Color.WHITE);
		labelCaption.setOpaque(true);
		labelCaption.setBackground(Color.GRAY);
		add(labelCaption);
		
		labelPath = new JLabel("path");
		springLayout.putConstraint(SpringLayout.NORTH, labelPath, 0, SpringLayout.NORTH, labelCaption);
		springLayout.putConstraint(SpringLayout.WEST, labelPath, 6, SpringLayout.EAST, labelCaption);
		springLayout.putConstraint(SpringLayout.SOUTH, labelPath, 0, SpringLayout.SOUTH, labelCaption);
		add(labelPath);
	}
}
