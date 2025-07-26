/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.multipage.gui.Images;
import org.multipage.gui.TextFieldEx;
import org.multipage.gui.Utility;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Panel that displays ambiguous file information.
 * @author vakol
 *
 */
public class AmbiguousFileItemPanel extends JPanel {
	
	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * File name item.
	 */
	private AmbiguousFileNameItem item;
	
	// $hide<<$
	/**
	 * Components.
	 */
	private JLabel labelArea;
	private JTextField textArea;
	private JButton buttonAreaEditor;
	private JButton buttonFocus;
	private JLabel labelVersion;

	/**
	 * Create the panel.
	 * @param item 
	 */
	public AmbiguousFileItemPanel(AmbiguousFileNameItem item) {
		
		try {
			this.item = item; // $hide$
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
		setOpaque(false);
		setPreferredSize(new Dimension(437, 29));
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		labelArea = new JLabel("org.multipage.generator.textAreaName");
		springLayout.putConstraint(SpringLayout.NORTH, labelArea, 4, SpringLayout.NORTH, this);
		add(labelArea);
		
		textArea = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.EAST, textArea, 170, SpringLayout.EAST, labelArea);
		textArea.setPreferredSize(new Dimension(200, 22));
		springLayout.putConstraint(SpringLayout.WEST, textArea, 6, SpringLayout.EAST, labelArea);
		textArea.setOpaque(false);
		springLayout.putConstraint(SpringLayout.NORTH, textArea, 4, SpringLayout.NORTH, this);
		textArea.setEditable(false);
		add(textArea);
		textArea.setColumns(10);
		
		buttonAreaEditor = new JButton("");
		springLayout.putConstraint(SpringLayout.NORTH, buttonAreaEditor, 4, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, buttonAreaEditor, 6, SpringLayout.EAST, textArea);
		buttonAreaEditor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onEditArea();
			}
		});
		buttonAreaEditor.setPreferredSize(new Dimension(22, 22));
		add(buttonAreaEditor);
		
		buttonFocus = new JButton("");
		springLayout.putConstraint(SpringLayout.WEST, buttonFocus, 3, SpringLayout.EAST, buttonAreaEditor);
		buttonFocus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onFocusArea();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, buttonFocus, 0, SpringLayout.NORTH, labelArea);
		buttonFocus.setPreferredSize(new Dimension(22, 22));
		add(buttonFocus);
		
		labelVersion = new JLabel("version");
		springLayout.putConstraint(SpringLayout.WEST, labelVersion, 10, SpringLayout.EAST, buttonFocus);
		labelVersion.setFont(new Font("Arial", Font.ITALIC, 11));
		springLayout.putConstraint(SpringLayout.NORTH, labelVersion, 0, SpringLayout.NORTH, labelArea);
		add(labelVersion);
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			localize();
			setIcons();
			setToolTips();
			
			// Set area name.
			textArea.setText(item.area.getDescriptionForced(true));
			textArea.setCaretPosition(0);
			
			// Set version.
			labelVersion.setText(String.format(
					Resources.getString("org.multipage.generator.textAreaVersionLabel"), item.version.toString()));	
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Localize components.
	 */
	private void localize() {
		try {
			
			Utility.localize(labelArea);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set icons.
	 */
	private void setIcons() {
		try {
			
			buttonAreaEditor.setIcon(Images.getIcon("org/multipage/generator/images/edit.png"));
			buttonFocus.setIcon(Images.getIcon("org/multipage/generator/images/search_icon.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set tool tips.
	 */
	private void setToolTips() {
		try {
			
			String areaName = item.area.toString();
			
			buttonAreaEditor.setToolTipText(String.format(
					Resources.getString("org.multipage.generator.tooltipEditArea"), areaName));
			buttonFocus.setToolTipText(String.format(
					Resources.getString("org.multipage.generator.tooltipFocusOnArea"), areaName));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On edit area.
	 */
	protected void onEditArea() {
		try {
			
			// Execute area editor.
			AreaEditorFrame.showDialog(null, item.area);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Focus on area.
	 */
	protected void onFocusArea() {
		try {
			
			GeneratorMainFrame.getFrame().getAreaDiagramEditor().focusArea(item.area.getId());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
