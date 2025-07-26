/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2021-06-09
 *
 */

package org.multipage.generator;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.maclan.Area;
import org.maclan.ConstructorHolder;
import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.multipage.basic.ProgramBasic;
import org.multipage.gui.TextFieldEx;
import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.Safe;

/**
 * Panel that edits area construtor.
 * @author vakol
 *
 */
public class AreaConstructorPanel extends JPanel {
	
	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor reference.
	 */
	private ConstructorHolder constructor = null;
	
	/**
	 * ID of current area.
	 */
	private Long areaId = null;
	
	// $hide<<$
	/**
	 * Dialog components.
	 */
	private JLabel labelConstructorName;
	private JTextField textConstructorName;
	private SlotListPanel panelSlots;
	private JTextField textConstructorAreaId;
	
	/**
	 * Create the panel.
	 */
	public AreaConstructorPanel() {
		
		try {
			initComponents();
			postCreate(); //$hide$
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
		
		JPanel panelTop = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, panelTop, 0, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, panelTop, 0, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, panelTop, 0, SpringLayout.EAST, this);
		panelTop.setPreferredSize(new Dimension(10, 30));
		add(panelTop);
		SpringLayout sl_panelTop = new SpringLayout();
		panelTop.setLayout(sl_panelTop);
		
		labelConstructorName = new JLabel("org.multipage.generator.textConstructorPathName");
		sl_panelTop.putConstraint(SpringLayout.NORTH, labelConstructorName, 6, SpringLayout.NORTH, panelTop);
		sl_panelTop.putConstraint(SpringLayout.WEST, labelConstructorName, 10, SpringLayout.WEST, panelTop);
		panelTop.add(labelConstructorName);
		
		textConstructorName = new TextFieldEx();
		sl_panelTop.putConstraint(SpringLayout.EAST, textConstructorName, -10, SpringLayout.EAST, panelTop);
		textConstructorName.setEditable(false);
		sl_panelTop.putConstraint(SpringLayout.NORTH, textConstructorName, 6, SpringLayout.NORTH, panelTop);
		panelTop.add(textConstructorName);
		textConstructorName.setColumns(10);
		
		textConstructorAreaId = new TextFieldEx();
		sl_panelTop.putConstraint(SpringLayout.WEST, textConstructorName, 3, SpringLayout.EAST, textConstructorAreaId);
		sl_panelTop.putConstraint(SpringLayout.WEST, textConstructorAreaId, 6, SpringLayout.EAST, labelConstructorName);
		textConstructorAreaId.setEditable(false);
		sl_panelTop.putConstraint(SpringLayout.NORTH, textConstructorAreaId, 0, SpringLayout.NORTH, labelConstructorName);
		panelTop.add(textConstructorAreaId);
		textConstructorAreaId.setColumns(8);
		
		JPanel panelCenter = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, panelCenter, 0, SpringLayout.SOUTH, panelTop);
		springLayout.putConstraint(SpringLayout.WEST, panelCenter, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, panelCenter, -10, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, panelCenter, -10, SpringLayout.EAST, this);
		add(panelCenter);
		panelCenter.setLayout(new BorderLayout(0, 0));
		
		//$hide>>$
		panelSlots = new SlotListPanel();
		panelCenter.add(panelSlots, BorderLayout.CENTER);
		//$hide<<$
	}
	
	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			localize();
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
			
			Utility.localize(labelConstructorName);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set current area.
	 * @param area
	 */
	public void setArea(Area area) {
		try {
			
			// Check input value.
			if (area == null) {
				return;
			}
			// Delegate this call.
			long areaId = area.getId();
			setArea(areaId);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Set current area.
	 * @param areaId
	 */
	public void setArea(Long areaId) {
		try {
			
			// Initialization.
			this.areaId = areaId;
			this.constructor = null;
			
			Obj<Long> constructorId = new Obj<Long>();
			ConstructorHolder constructor = new ConstructorHolder();
			
			// Load area constructor.
			try {
				Middle middle = ProgramBasic.loginMiddle();
				
				MiddleResult result = middle.loadAreaConstructor(areaId, constructorId);
				result.throwPossibleException();
				
				if (constructorId.ref != null) {
					
					result = middle.loadConstructorHolder(constructorId.ref, constructor);
					result.throwPossibleException();
					
					// Set the constructor reference.
					this.constructor = constructor;
				}
			}
			catch (Exception e) {
				
				Utility.show2(this, e.getLocalizedMessage());
				return;
			}
			finally {
				ProgramBasic.logoutMiddle();
			}
			
			// Load dialog controls.
			loadControls();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Load dialog controls.
	 */
	private void loadControls() {
		try {
			
			// Display constructor name and associated area ID.
			textConstructorName.setText(getConstructorName());
			textConstructorAreaId.setText(getConstructorAreaId());
			
			// Get constructor area.
			Area constructorArea = null;
			if (this.constructor != null) {
				
				long areaId = this.constructor.getAreaId();
				constructorArea = ProgramGenerator.getArea(areaId);
			}
			
			// Check if constructor area exists.
			if (constructorArea == null) {
				return;
			}
			
			// Load slot values.
			try {
				Middle middle = ProgramBasic.loginMiddle();
				MiddleResult result = middle.loadSlots(constructorArea, true);
				result.throwPossibleException();
			}
			catch (Exception e) {
				Utility.show2(this, e.getLocalizedMessage());
			}
			finally {
				ProgramBasic.logoutMiddle();
			}
			
			// Load the slot list view.
			panelSlots.setArea(constructorArea);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Gets constructor name.
	 * @return
	 */
	private String getConstructorName() {
		
		try {
			String name = this.constructor == null ? "" : this.constructor.getNameText();
			return name;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Get constructor area ID.
	 * @return
	 */
	private String getConstructorAreaId() {
		
		try {
			// Check constructor.
			if (this.constructor == null) {
				return "";
			}
			
			long areaId = this.constructor.getAreaId();
			return Long.toString(areaId);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Method for updating the dialog components.
	 */
	public void updateComponents() {
		try {
			
			setArea(areaId);
			// Note: The "panelSlots" components are updated also because the panel is registered for updates.
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
}
