/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2025-04-26
 *
 */

package org.multipage.gui;

import javax.swing.*;

import org.multipage.util.*;

import java.awt.*;
import java.io.*;

/**
 * Panel that displays number editor.
 * @author vakol
 *
 */
public class CssNumberPanel extends InsertPanel implements StringValueEditor {
	
	/**
	 * Components.
	 */
	private JLabel labelInsertNumber;
	private TextFieldEx textNumber;
	private JComboBox comboUnits;

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Serialized dialog states.
	 */
	protected static Rectangle bounds;
	private static boolean boundsSet;
	
	/**
	 * Set default states.
	 */
	public static void setDefaultData() {
		
		bounds = new Rectangle(0, 0, 317, 195);
		boundsSet = false;
	}

	/**
	 * Load serialized states.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void serializeData(StateInputStream inputStream)
			throws IOException, ClassNotFoundException {
		
		bounds = Utility.readInputStreamObject(inputStream, Rectangle.class);
		boundsSet = true;
	}

	/**
	 * Save serialized states.
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream)
			throws IOException {
		
		outputStream.writeObject(bounds);
	}

	/**
	 * Initial string.
	 */
	private String initialString;
	
	// $hide<<$

	/**
	 * Create the dialog.
	 * @param parentWindow 
	 */
	public CssNumberPanel(String initialString) {
		
		try {
			initComponents();
			
			// $hide>>$
			this.initialString = initialString;
			postCreate();
			// $hide<<$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Initialize components.
	 */
	private void initComponents() {
		
		SpringLayout sl_panelMain = new SpringLayout();
		setLayout(sl_panelMain);
		
		labelInsertNumber = new JLabel("org.multipage.gui.textCssInsertNumber");
		sl_panelMain.putConstraint(SpringLayout.NORTH, labelInsertNumber, 53, SpringLayout.NORTH, this);
		sl_panelMain.putConstraint(SpringLayout.WEST, labelInsertNumber, 29, SpringLayout.WEST, this);
		add(labelInsertNumber);
		
		textNumber = new TextFieldEx();
		sl_panelMain.putConstraint(SpringLayout.NORTH, textNumber, 0, SpringLayout.NORTH, labelInsertNumber);
		sl_panelMain.putConstraint(SpringLayout.WEST, textNumber, 6, SpringLayout.EAST, labelInsertNumber);
		textNumber.setColumns(10);
		add(textNumber);
		
		comboUnits = new JComboBox();
		sl_panelMain.putConstraint(SpringLayout.NORTH, comboUnits, 0, SpringLayout.NORTH, labelInsertNumber);
		sl_panelMain.putConstraint(SpringLayout.WEST, comboUnits, 0, SpringLayout.EAST, textNumber);
		comboUnits.setPreferredSize(new Dimension(50, 20));
		add(comboUnits);
	}

	/**
	 * Load dialog.
	 */
	private void loadDialog() {
		try {
			
			setFromInitialString();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Save dialog.
	 */
	@Override
	public void saveDialog() {
		
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			localize();
			loadUnits();
			loadDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load units.
	 */
	private void loadUnits() {
		try {
			
			Utility.loadCssUnits(comboUnits);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get font specification.
	 * @return
	 */
	@Override
	public String getSpecification() {
		
		try {
			return getNumber() + getUnits();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get number.
	 * @return
	 */
	private String getNumber() {

		try {
			String text = textNumber.getText();
			return String.valueOf(Double.parseDouble(text));
		}
		catch (Exception e) {
		}
		
		return "0";
	}

	/**
	 * Get units.
	 * @return
	 */
	private String getUnits() {
		
		try {
			return (String) comboUnits.getSelectedItem();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Set from initial string.
	 */
	private void setFromInitialString() {
		try {
			
			textNumber.setText("");
			Utility.selectComboItem(comboUnits, "px");
	
			if (initialString != null) {
			
				Utility.setCssValueAndUnits(initialString.trim(), textNumber, comboUnits);
			}
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
			
			Utility.localize(labelInsertNumber);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/* (non-Javadoc)
	 * @see org.multipage.gui.InsertPanel#getWindowTitle()
	 */
	@Override
	public String getWindowTitle() {
		
		try {
			return Resources.getString("org.multipage.gui.textCssNumberBuilder");
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/* (non-Javadoc)
	 * @see org.multipage.gui.InsertPanel#getResultText()
	 */
	@Override
	public String getResultText() {
		
		try {
			return getSpecification();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/* (non-Javadoc)
	 * @see org.multipage.gui.InsertPanel#getContainerDialogBounds()
	 */
	@Override
	public Rectangle getContainerDialogBounds() {
		
		return bounds;
	}

	/* (non-Javadoc)
	 * @see org.multipage.gui.InsertPanel#setContainerDialogBounds(java.awt.Rectangle)
	 */
	@Override
	public void setContainerDialogBounds(Rectangle bounds) {
		
		CssNumberPanel.bounds = bounds;
	}
	
	/* (non-Javadoc)
	 * @see org.multipage.gui.InsertPanel#boundsSet()
	 */
	@Override
	public boolean isBoundsSet() {

		return boundsSet;
	}
	
	/* (non-Javadoc)
	 * @see org.multipage.gui.InsertPanel#setBoundsSet(boolean)
	 */
	@Override
	public void setBoundsSet(boolean set) {
		
		boundsSet = set;
	}

	/**
	 * Get component.
	 */
	@Override
	public Component getComponent() {
		
		return this;
	}

	/**
	 * Get string value.
	 */
	@Override
	public String getStringValue() {
		
		try {
			return getResultText();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Set string value.
	 */
	@Override
	public void setStringValue(String string) {
		try {
			
			initialString = string;
			setFromInitialString();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get value meaning.
	 */
	@Override
	public String getValueMeaning() {
		
		return meansCssNumber;
	}

	/**
	 * Set controls grayed. If a false value is returned a high level editor grayed this panel.
	 */
	@Override
	public boolean setControlsGrayed(boolean isDefault) {
		
		return false;
	}
}
