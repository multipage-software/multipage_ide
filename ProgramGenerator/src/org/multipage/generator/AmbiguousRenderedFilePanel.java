/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Font;
import java.awt.GridLayout;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.multipage.gui.Utility;
import org.multipage.util.Safe;

/**
 * Panel that displays information about ambiguous files.
 * @author vakol
 *
 */
public class AmbiguousRenderedFilePanel extends JPanel {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * File name object.
	 */
	private AmbiguousFileName fileName;
	
	// $hide<<$
	/**
	 * Components.
	 */
	private JLabel labelFullFileName;
	private JPanel panelListItems;

	/**
	 * Create the panel.
	 * @param fileName 
	 */
	public AmbiguousRenderedFilePanel(AmbiguousFileName fileName) {
		
		try {
			this.fileName = fileName;
			initComponents();
			
			postCreate(); // $hide$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Initialize components.
	 */
	private void initComponents() {
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		labelFullFileName = new JLabel("full file name");
		labelFullFileName.setFont(new Font("Arial Black", Font.PLAIN, 13));
		springLayout.putConstraint(SpringLayout.NORTH, labelFullFileName, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, labelFullFileName, 10, SpringLayout.WEST, this);
		add(labelFullFileName);
		
		panelListItems = new JPanel();
		springLayout.putConstraint(SpringLayout.EAST, panelListItems, 407, SpringLayout.WEST, this);
		panelListItems.setOpaque(false);
		springLayout.putConstraint(SpringLayout.NORTH, panelListItems, 6, SpringLayout.SOUTH, labelFullFileName);
		springLayout.putConstraint(SpringLayout.WEST, panelListItems, 20, SpringLayout.WEST, this);
		add(panelListItems);
	}
	
	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			// Set file name.
			labelFullFileName.setText(fileName.fileName);
			
			// Set list of items.
			setListOfItems();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set list of items.
	 */
	private void setListOfItems() {
		try {
			
			LinkedList<AmbiguousFileNameItem> items = fileName.items;
			
			int count = items.size();
			
			// Set list panel layout.
			panelListItems.setLayout(new GridLayout(count, 0));
	
			
			// Populate list panel.
			for (int index = 0; index < count; index++) {
				
				AmbiguousFileNameItem item = items.get(index);
				
				AmbiguousFileItemPanel itemPanel = new AmbiguousFileItemPanel(item);
				panelListItems.add(itemPanel);
			}
			
			Utility.forceDoLayout(this, 20);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
