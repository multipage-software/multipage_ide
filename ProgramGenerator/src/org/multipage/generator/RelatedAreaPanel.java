/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.maclan.Area;
import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.multipage.basic.ProgramBasic;
import org.multipage.gui.Images;
import org.multipage.gui.TextFieldEx;
import org.multipage.gui.Utility;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Panel that displays editor for the related area.
 * @author vakol
 *
 */
public class RelatedAreaPanel extends JPanel {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Current area reference.
	 */
	private Area currentArea;
	
	// $hide<<$
	/**
	 * Components.
	 */
	private JLabel labelRelatedArea;
	private JButton buttonClearRelatedArea;
	private JButton buttonSelectRelatedArea;
	private JButton buttonUpdateRelatedArea;
	private TextFieldEx textRelatedArea;

	/**
	 * Create the panel.
	 */
	public RelatedAreaPanel() {
		
		try {
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
		
		labelRelatedArea = new JLabel("org.multipage.generator.textRelatedArea");
		springLayout.putConstraint(SpringLayout.NORTH, labelRelatedArea, 0, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, labelRelatedArea, 0, SpringLayout.WEST, this);
		add(labelRelatedArea);
		
		buttonClearRelatedArea = new JButton("");
		buttonClearRelatedArea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClearSelectedArea();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, buttonClearRelatedArea, 0, SpringLayout.NORTH, labelRelatedArea);
		springLayout.putConstraint(SpringLayout.EAST, buttonClearRelatedArea, 0, SpringLayout.EAST, this);
		buttonClearRelatedArea.setPreferredSize(new Dimension(25, 25));
		buttonClearRelatedArea.setMargin(new Insets(0, 0, 0, 0));
		add(buttonClearRelatedArea);
		
		buttonSelectRelatedArea = new JButton("");
		buttonSelectRelatedArea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSelectRelatedArea();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, buttonSelectRelatedArea, 0, SpringLayout.NORTH, labelRelatedArea);
		springLayout.putConstraint(SpringLayout.EAST, buttonSelectRelatedArea, 0, SpringLayout.WEST, buttonClearRelatedArea);
		buttonSelectRelatedArea.setPreferredSize(new Dimension(25, 25));
		buttonSelectRelatedArea.setMargin(new Insets(0, 0, 0, 0));
		add(buttonSelectRelatedArea);
		
		buttonUpdateRelatedArea = new JButton("");
		buttonUpdateRelatedArea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateComponents();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, buttonUpdateRelatedArea, 0, SpringLayout.NORTH, labelRelatedArea);
		springLayout.putConstraint(SpringLayout.EAST, buttonUpdateRelatedArea, 0, SpringLayout.WEST, buttonSelectRelatedArea);
		buttonUpdateRelatedArea.setPreferredSize(new Dimension(25, 25));
		buttonUpdateRelatedArea.setMargin(new Insets(0, 0, 0, 0));
		add(buttonUpdateRelatedArea);
		
		textRelatedArea = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textRelatedArea, 0, SpringLayout.NORTH, labelRelatedArea);
		springLayout.putConstraint(SpringLayout.WEST, textRelatedArea, 6, SpringLayout.EAST, labelRelatedArea);
		springLayout.putConstraint(SpringLayout.EAST, textRelatedArea, 0, SpringLayout.WEST, buttonUpdateRelatedArea);
		textRelatedArea.setPreferredSize(new Dimension(6, 25));
		textRelatedArea.setEditable(false);
		textRelatedArea.setColumns(10);
		add(textRelatedArea);
	}
	
	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			localize();
			setIcons();
			setToolTips();
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
			
			Utility.localize(labelRelatedArea);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set icons.
	 */
	protected void setIcons() {
		try {
			
			buttonSelectRelatedArea.setIcon(Images.getIcon("org/multipage/generator/images/area_node.png"));
			buttonClearRelatedArea.setIcon(Images.getIcon("org/multipage/generator/images/cancel_icon.png"));
			buttonUpdateRelatedArea.setIcon(Images.getIcon("org/multipage/generator/images/update_icon.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set tool tips.
	 */
	protected void setToolTips() {
		try {
			
			buttonSelectRelatedArea.setToolTipText(Resources.getString("org.multipage.generator.tooltipSelectRelatedArea"));
			buttonClearRelatedArea.setToolTipText(Resources.getString("org.multipage.generator.tooltipClearRelatedArea"));
			buttonUpdateRelatedArea.setToolTipText(Resources.getString("org.multipage.generator.tooltipUpdateRelatedArea"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set current area.
	 * @param currentArea
	 */
	public void setArea(Area area) {
		try {
			
			this.currentArea = area;
			// Load related area.
			updateComponents();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On clear selected area.
	 */
	protected void onClearSelectedArea() {
		try {
			
			if (currentArea == null) {
				textRelatedArea.setText("");
				return;
			}
			
			// Ask user.
			if (!Utility.ask(this, "org.multipage.generator.messageClearAreaRelatedArea")) {
				return;
			}
			
			// Clear related area ID.
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			
			long currentAreaId = currentArea.getId();
			
			MiddleResult result = middle.updateAreaRelatedArea(login, currentAreaId, null);
			if (result.isNotOK()) {
				
				result.show(this);
				return;
			}
			
			currentArea.clearRelatedArea();
			textRelatedArea.setText("");
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On select related area.
	 */
	protected void onSelectRelatedArea() {
		try {
			
			if (currentArea == null) {
				textRelatedArea.setText("");
				return;
			}
			
			// Select area.
			Area rootArea = ProgramGenerator.getArea(0);
			Area oldRelatedArea = currentArea.getRelatedArea();
			
			Area relatedArea = SelectSubAreaDialog.showDialog(this, rootArea, oldRelatedArea);
			if (relatedArea == null) {
				return;
			}
			
			// Save new related area ID.
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			
			long currentAreaId = currentArea.getId();
			
			MiddleResult result = middle.updateAreaRelatedArea(login, currentAreaId,
					relatedArea != null ? relatedArea.getId() : null);
			if (result.isNotOK()) {
				
				result.show(this);
				return;
			}
			
			// Set new area.
			currentArea.setRelatedArea(relatedArea);
			textRelatedArea.setText(relatedArea != null ? relatedArea.getDescriptionForDiagram() : "");
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Method for updating the dialog components.
	 */
	public void updateComponents() {
		try {
			
			if (currentArea == null) {
				textRelatedArea.setText("");
				return;
			}
			currentArea = ProgramGenerator.updateArea(currentArea);
			Area relatedArea = currentArea.getRelatedArea();
			textRelatedArea.setText(relatedArea != null ? relatedArea.getDescriptionForced() : "");
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
