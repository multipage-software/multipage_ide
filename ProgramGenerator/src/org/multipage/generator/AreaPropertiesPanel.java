/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SpringLayout;

import org.maclan.Area;
import org.multipage.gui.ApplicationEvents;
import org.multipage.gui.Images;
import org.multipage.gui.TextFieldAutoSave;
import org.multipage.gui.Utility;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Area properties panel.
 * @author vakol
 *
 */
public class AreaPropertiesPanel extends AreaPropertiesBasePanel {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Panel with slot list.
	 */
	private SlotListPanel panelSlotList;
	
	/**
	 * Components.
	 */
	private JLabel labelAreaDescription;
	private TextFieldAutoSave textDescription;
	private JButton buttonSaveDescription;
	private JMenuBar menuBar;
	private JMenu menuArea;
	private JMenuItem menuEditResources;
	private JButton buttonDeleteDescription;
	private JSplitPane splitPane;
	private JLabel labelAreaAlias;
	private TextFieldAutoSave textAlias;
	private JButton buttonSaveAlias;
	private JPanel panelExtension;
	private JMenuItem menuEditDependencies;
	private JMenuItem menuAreaEdit;
	private JButton buttonStatResource;

	/**
	 * Create the panel.
	 */
	public AreaPropertiesPanel(boolean isPropertiesPanel) {
		
		try {
			this.isInPropertiesPanel = isPropertiesPanel;
			// Initialize components.
			initComponents();
			// Post creation.
			//$hide>>$
			panelSlotList = ProgramGenerator.newSlotListPanel();
			panelSlotList.setIsPropertiesPanel(isPropertiesPanel);
			splitPane.setLeftComponent(panelSlotList);
			
			menuArea.setIcon(Images.getIcon("org/multipage/generator/images/edit.png"));
			
			setComponentsReferences(
				labelAreaDescription,
				textDescription,
				buttonSaveDescription,
				menuArea,
				menuEditResources,
				buttonDeleteDescription,
				splitPane,
				panelSlotList,
				labelAreaAlias,
				textAlias,
				buttonSaveAlias,
				panelExtension,
				menuEditDependencies,
				menuAreaEdit);
			postCreate();
			//$hide<<$
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
		
		labelAreaDescription = new JLabel("org.multipage.generator.textAreaDescription");
		springLayout.putConstraint(SpringLayout.WEST, labelAreaDescription, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, labelAreaDescription, -50, SpringLayout.EAST, this);
		add(labelAreaDescription);
		
		textDescription = new TextFieldAutoSave();
		springLayout.putConstraint(SpringLayout.NORTH, textDescription, 6, SpringLayout.SOUTH, labelAreaDescription);
		springLayout.putConstraint(SpringLayout.WEST, textDescription, 10, SpringLayout.WEST, this);
		add(textDescription);
		textDescription.setColumns(10);
		
		buttonSaveDescription = new JButton();
		springLayout.putConstraint(SpringLayout.EAST, textDescription, -3, SpringLayout.WEST, buttonSaveDescription);
		springLayout.putConstraint(SpringLayout.NORTH, buttonSaveDescription, 6, SpringLayout.SOUTH, labelAreaDescription);
		springLayout.putConstraint(SpringLayout.SOUTH, buttonSaveDescription, 0, SpringLayout.SOUTH, textDescription);
		buttonSaveDescription.setMargin(new Insets(0, 0, 0, 0));
		buttonSaveDescription.setPreferredSize(new Dimension(18, 18));
		add(buttonSaveDescription);
		buttonSaveDescription.addActionListener(e -> {
			saveDescriptionChanges();
		});
		
		menuBar = new JMenuBar();
		menuBar.setPreferredSize(new Dimension(0, 24));
		springLayout.putConstraint(SpringLayout.EAST, menuBar, 0, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.NORTH, labelAreaDescription, 10, SpringLayout.SOUTH, menuBar);
		springLayout.putConstraint(SpringLayout.SOUTH, labelAreaDescription, 26, SpringLayout.SOUTH, menuBar);
		springLayout.putConstraint(SpringLayout.NORTH, menuBar, 0, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, menuBar, 0, SpringLayout.WEST, this);
		add(menuBar);
		
		menuArea = new JMenu("org.multipage.generator.menuArea");
		menuBar.add(menuArea);
		
		menuEditResources = new JMenuItem("org.multipage.generator.menuAreaEditResources");
		menuEditResources.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onEditArea(AreaEditorFrame.RESOURCES); //$hide$
			}
		});
		
		menuAreaEdit = new JMenuItem("org.multipage.generator.menuAreaEdit");
		menuAreaEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onEditArea(AreaEditorFrame.NOT_SPECIFIED); //$hide$
			}
		});
		menuArea.add(menuAreaEdit);
		menuArea.addSeparator();
		menuArea.add(menuEditResources);
		
		menuEditDependencies = new JMenuItem("org.multipage.generator.menuAreaEditDependencies");
		menuEditDependencies.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onEditArea(AreaEditorFrame.DEPENDENCIES); //$hide$
			}
		});
		menuArea.add(menuEditDependencies);
		
		buttonDeleteDescription = new JButton("");
		springLayout.putConstraint(SpringLayout.EAST, buttonSaveDescription, -3, SpringLayout.WEST, buttonDeleteDescription);
		springLayout.putConstraint(SpringLayout.SOUTH, buttonDeleteDescription, 0, SpringLayout.SOUTH, textDescription);
		buttonDeleteDescription.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onDeleteLocalText(); //$hide$
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, buttonDeleteDescription, 6, SpringLayout.SOUTH, labelAreaDescription);
		buttonDeleteDescription.setPreferredSize(new Dimension(18, 18));
		buttonDeleteDescription.setMargin(new Insets(0, 0, 0, 0));
		add(buttonDeleteDescription);
		
		splitPane = new JSplitPane();
		springLayout.putConstraint(SpringLayout.SOUTH, splitPane, -10, SpringLayout.SOUTH, this);
		splitPane.setBorder(null);
		splitPane.setOneTouchExpandable(true);
		springLayout.putConstraint(SpringLayout.WEST, splitPane, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, splitPane, -10, SpringLayout.EAST, this);
		splitPane.setContinuousLayout(true);
		splitPane.setResizeWeight(0.5);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		add(splitPane);
		
		labelAreaAlias = new JLabel("org.multipage.generator.textAreaAlias2");
		springLayout.putConstraint(SpringLayout.NORTH, labelAreaAlias, 6, SpringLayout.SOUTH, textDescription);
		springLayout.putConstraint(SpringLayout.WEST, labelAreaAlias, 10, SpringLayout.WEST, this);
		add(labelAreaAlias);
		
		textAlias = new TextFieldAutoSave();
		springLayout.putConstraint(SpringLayout.NORTH, splitPane, 6, SpringLayout.SOUTH, textAlias);
		
		panelExtension = new JPanel();
		panelExtension.setBorder(null);
		splitPane.setRightComponent(panelExtension);
		panelExtension.setLayout(new BorderLayout(0, 0));
		springLayout.putConstraint(SpringLayout.NORTH, textAlias, 6, SpringLayout.SOUTH, textDescription);
		springLayout.putConstraint(SpringLayout.WEST, textAlias, 6, SpringLayout.EAST, labelAreaAlias);
		add(textAlias);
		textAlias.setColumns(10);
		
		buttonSaveAlias = new JButton("");
		springLayout.putConstraint(SpringLayout.EAST, textAlias, -3, SpringLayout.WEST, buttonSaveAlias);
		springLayout.putConstraint(SpringLayout.EAST, buttonSaveAlias, -10, SpringLayout.EAST, this);
		
		springLayout.putConstraint(SpringLayout.NORTH, buttonSaveAlias, 0, SpringLayout.NORTH, textAlias);
		springLayout.putConstraint(SpringLayout.SOUTH, buttonSaveAlias, 0, SpringLayout.SOUTH, textAlias);
		buttonSaveAlias.setPreferredSize(new Dimension(18, 18));
		buttonSaveAlias.setMargin(new Insets(0, 0, 0, 0));
		add(buttonSaveAlias);
		buttonSaveAlias.addActionListener(e -> {
			saveAliasChanges();
		});
		
		buttonStatResource = new JButton("");
		buttonStatResource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onStartResource();
			}
		});
		springLayout.putConstraint(SpringLayout.EAST, buttonStatResource, -10, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.EAST, buttonDeleteDescription, -3, SpringLayout.WEST, buttonStatResource);
		springLayout.putConstraint(SpringLayout.NORTH, buttonStatResource, 6, SpringLayout.SOUTH, labelAreaDescription);
		springLayout.putConstraint(SpringLayout.SOUTH, buttonStatResource, 0, SpringLayout.SOUTH, textDescription);
		buttonStatResource.setPreferredSize(new Dimension(18, 18));
		buttonStatResource.setMargin(new Insets(0, 0, 0, 0));
		add(buttonStatResource);
	}
	
	/**
	 * Set icons.
	 */
	@Override
	protected void setIcons() {
		try {
			
			super.setIcons();
			buttonStatResource.setIcon(Images.getIcon("org/multipage/generator/images/start_resource.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set tool tips.
	 */
	@Override
	protected void setToolTips() {
		try {
			
			super.setToolTips();
	        buttonStatResource.setToolTipText(Resources.getString("org.multipage.generator.tooltipEditAreaStartResource"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On click start resource button.
	 */
	protected void onStartResource() {
		try {
			
			// Check area.
			if (areas.size() != 1) {
				Utility.show(this, "org.multipage.generator.messageSelectSingleArea");
				return;
			}
			
			// Get area and edit start resource.
			Area area = areas.getFirst();
			GeneratorMainFrame.editStartResource(area, false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On window close.
	 */
	@Override
	public void close() {
		try {
			
			// Remove application event receivers.
			ApplicationEvents.removeReceivers(this);
			
			super.close();
			panelSlotList.onClose();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}	
}
