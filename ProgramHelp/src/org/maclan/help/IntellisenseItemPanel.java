/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */
package org.maclan.help;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.UIManager;

import org.maclan.help.Intellisense.Suggestion;
import org.multipage.gui.GraphUtility;
import org.multipage.gui.Images;
import org.multipage.util.Safe;

/**
 * Panel that displays intellisense information.
 * @author vakol
 *
 */
public class IntellisenseItemPanel extends JPanel {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The link button size.
	 */
	public static Dimension linkButtonSize = null;
	
	/**
	 * Cell is selected.
	 */
	private boolean isSelected = false;
	
	/**
	 * Cell has a focus.
	 */
	private boolean hasFocus = false;
	
	/**
	 * Components.
	 */
	private JLabel labelCaption;
	private JButton buttonLink;
	
	/**
	 * Create the panel.
	 * @param intellisenseWindow 
	 */
	public IntellisenseItemPanel(IntellisenseWindow intellisenseWindow) {
		
		try {
			initComponents();
			postCreation(intellisenseWindow); //$hide$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Initialize components.
	 */
	private void initComponents() {
		
		setOpaque(false);
		setBackground(Color.RED);
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		labelCaption = new JLabel("");
		labelCaption.setOpaque(true);
		labelCaption.setBackground(Color.WHITE);
		springLayout.putConstraint(SpringLayout.NORTH, labelCaption, 0, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, labelCaption, 0, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, labelCaption, 0, SpringLayout.SOUTH, this);
		add(labelCaption);
		
		buttonLink = new JButton("");
		buttonLink.setBorder(null);
		springLayout.putConstraint(SpringLayout.WEST, buttonLink, -14, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.EAST, buttonLink, 0, SpringLayout.EAST, this);
		buttonLink.setBackground(Color.WHITE);
		springLayout.putConstraint(SpringLayout.EAST, labelCaption, 0, SpringLayout.WEST, buttonLink);
		springLayout.putConstraint(SpringLayout.NORTH, buttonLink, 0, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.SOUTH, buttonLink, 14, SpringLayout.NORTH, this);
		add(buttonLink);
	}
	
	/**
	 * Post creation.
	 * @param intellisenseWindow 
	 */
	private void postCreation(IntellisenseWindow intellisenseWindow) {
		try {
			
			// Set icons.
			ImageIcon icon = Images.getIcon("org/maclan/help/images/intellisense_help.png");
			buttonLink.setIcon(icon);
			
			// Set width and height.
			int verticalScrollBarSize = ((Integer) UIManager.get("ScrollBar.width")).intValue();
			int width = intellisenseWindow.windowSize.width - verticalScrollBarSize - 2;
			setPreferredSize(new Dimension(width, 16));
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
			
			// Highlight selection.
			GraphUtility.drawSelection(g, this, isSelected, hasFocus);
			
			// Load link buttons size.
			if (linkButtonSize == null) {
				linkButtonSize = buttonLink.getSize();
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Set suggestion.
	 * @param suggestion 
	 * @param index 
	 * @param isSelected 
	 * @param cellHasFocus 
	 */
	public void setSuggestion(Suggestion suggestion, int index, boolean isSelected, boolean cellHasFocus) {
		try {
			
			// Replaces whitespaces with non-breaking spaces.
			String suggestionHtml = suggestion.toString();
			suggestionHtml = suggestionHtml.replaceAll("\\s+", "&nbsp;");
			
			// Set the caption and flags.
			labelCaption.setText(suggestionHtml);
			this.isSelected = isSelected;
			this.hasFocus = cellHasFocus;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
