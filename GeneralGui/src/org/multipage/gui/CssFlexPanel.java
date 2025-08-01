/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import javax.swing.*;

import org.multipage.util.*;

import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * Panel that displays editor for flexbox layout.
 * @author vakol
 *
 */
public class CssFlexPanel extends InsertPanel implements StringValueEditor {

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
		
		bounds = new Rectangle(0, 0, 469, 218);
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
	 * Components.
	 */
	private JLabel labelGrow;
	private TextFieldEx textGrow;
	private JLabel labelShrink;
	private TextFieldEx textShrink;
	private JLabel labelBasis;
	private TextFieldEx textBasis;
	private JComboBox comboBasisUnits;
	
	/**
	 * Create the dialog.
	 * @param parentWindow 
	 */
	public CssFlexPanel(String initialString) {
		
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
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		labelGrow = new JLabel("org.multipage.gui.textFlexGrow");
		springLayout.putConstraint(SpringLayout.NORTH, labelGrow, 50, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, labelGrow, 36, SpringLayout.WEST, this);
		add(labelGrow);
		
		textGrow = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textGrow, 0, SpringLayout.NORTH, labelGrow);
		springLayout.putConstraint(SpringLayout.WEST, textGrow, 6, SpringLayout.EAST, labelGrow);
		textGrow.setColumns(5);
		add(textGrow);
		
		labelShrink = new JLabel("org.multipage.gui.textFlexShrink");
		springLayout.putConstraint(SpringLayout.NORTH, labelShrink, 0, SpringLayout.NORTH, labelGrow);
		springLayout.putConstraint(SpringLayout.WEST, labelShrink, 26, SpringLayout.EAST, textGrow);
		add(labelShrink);
		
		textShrink = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textShrink, 0, SpringLayout.NORTH, labelGrow);
		springLayout.putConstraint(SpringLayout.WEST, textShrink, 6, SpringLayout.EAST, labelShrink);
		textShrink.setColumns(5);
		add(textShrink);
		
		labelBasis = new JLabel("org.multipage.gui.textFlexBasis");
		springLayout.putConstraint(SpringLayout.NORTH, labelBasis, 45, SpringLayout.SOUTH, labelGrow);
		springLayout.putConstraint(SpringLayout.WEST, labelBasis, 37, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, labelBasis, 0, SpringLayout.EAST, labelGrow);
		add(labelBasis);
		
		textBasis = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textBasis, 0, SpringLayout.NORTH, labelBasis);
		springLayout.putConstraint(SpringLayout.WEST, textBasis, 0, SpringLayout.WEST, textGrow);
		textBasis.setColumns(5);
		add(textBasis);
		
		comboBasisUnits = new JComboBox();
		springLayout.putConstraint(SpringLayout.NORTH, comboBasisUnits, 109, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, comboBasisUnits, 0, SpringLayout.EAST, textBasis);
		springLayout.putConstraint(SpringLayout.SOUTH, comboBasisUnits, 0, SpringLayout.SOUTH, textBasis);
		comboBasisUnits.setPreferredSize(new Dimension(50, 20));
		add(comboBasisUnits);
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
			
			Utility.loadCssUnits(comboBasisUnits);
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
			return getGrow() + " " + getShrink() + " " + getBasis();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get grow.
	 * @return
	 */
	private String getGrow() {
		
		try {
			return Utility.getCssNumberValue(textGrow, "0");
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "0";
	}

	/**
	 * Get shrink.
	 * @return
	 */
	private String getShrink() {
		
		try {
			return Utility.getCssNumberValue(textShrink, "1");
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "1";
	}

	/**
	 * Get basis.
	 * @return
	 */
	private String getBasis() {
		
		try {
			return Utility.getCssValueAndUnits(textBasis, comboBasisUnits, "auto");
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "auto";
	}
	
	/**
	 * Set from initial string.
	 */
	private void setFromInitialString() {
		try {
			
			// Initialize controls.
			setGrow("0");
			setShrink("1");
			setBasis("auto");
	
			if (initialString != null) {
				
				Scanner scanner = new Scanner(initialString.trim());
				
				try {
					// Set values.
					setGrow(scanner.next().trim());
					setShrink(scanner.next().trim());
					setBasis(scanner.next().trim());
				}
				catch (Exception e) {
				}
				
			    scanner.close();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set grow.
	 * @param string
	 */
	private void setGrow(String string) {
		try {
			
			Utility.setCssNumberValue(string, textGrow);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set shrink.
	 * @param string
	 */
	private void setShrink(String string) {
		try {
			
			Utility.setCssNumberValue(string, textShrink);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set basis.
	 * @param string
	 */
	private void setBasis(String string) {
		try {
			
			if (string.equals("auto")) {
				textBasis.setText("");
				return;
			}
			
			Utility.setCssValueAndUnits(string, textBasis, comboBasisUnits);
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
			
			Utility.localize(labelGrow);
			Utility.localize(labelShrink);
			Utility.localize(labelBasis);
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
			return Resources.getString("org.multipage.gui.textCssFlexBuilder");
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
		
		CssFlexPanel.bounds = bounds;
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
	 * @return
	 */
	@Override
	public Component getComponent() {
		
		return this;
	}

	/**
	 * Get string value.
	 * @return
	 */
	@Override
	public String getStringValue() {
		
		try {
			return getSpecification();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Set string value.
	 * @param string
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
	 * @return
	 */
	@Override
	public String getValueMeaning() {
		
		return meansCssFlex;
	}

	/**
	 * Set controls grayed. If a false value is returned a high level editor grayed this panel.
	 */
	@Override
	public boolean setControlsGrayed(boolean isDefault) {
		
		return false;
	}
}
