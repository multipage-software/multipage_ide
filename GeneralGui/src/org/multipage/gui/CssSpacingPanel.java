/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2025-04-26
 *
 */

package org.multipage.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SpringLayout;

import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Panel that displays spacing editor.
 * @author vakol
 *
 */
public class CssSpacingPanel extends InsertPanel implements StringValueEditor {

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
	private JLabel labelHorizontal;
	private TextFieldEx textHorizontal;
	private JLabel labelVertical;
	private TextFieldEx textVertical;
	private JComboBox comboHorizontalUnits;
	private JComboBox comboVerticalUnits;
	
	/**
	 * Create the dialog.
	 * @param parentWindow 
	 */
	public CssSpacingPanel(String initialString) {
		
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
		
		labelHorizontal = new JLabel("org.multipage.gui.textSpacingHorizontal");
		springLayout.putConstraint(SpringLayout.NORTH, labelHorizontal, 50, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, labelHorizontal, 36, SpringLayout.WEST, this);
		add(labelHorizontal);
		
		textHorizontal = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textHorizontal, 0, SpringLayout.NORTH, labelHorizontal);
		springLayout.putConstraint(SpringLayout.WEST, textHorizontal, 6, SpringLayout.EAST, labelHorizontal);
		textHorizontal.setColumns(5);
		add(textHorizontal);
		
		labelVertical = new JLabel("org.multipage.gui.textSpacingVertical");
		springLayout.putConstraint(SpringLayout.NORTH, labelVertical, 0, SpringLayout.NORTH, labelHorizontal);
		add(labelVertical);
		
		textVertical = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textVertical, 0, SpringLayout.NORTH, labelHorizontal);
		springLayout.putConstraint(SpringLayout.WEST, textVertical, 6, SpringLayout.EAST, labelVertical);
		textVertical.setColumns(5);
		add(textVertical);
		
		comboHorizontalUnits = new JComboBox();
		springLayout.putConstraint(SpringLayout.WEST, labelVertical, 30, SpringLayout.EAST, comboHorizontalUnits);
		springLayout.putConstraint(SpringLayout.NORTH, comboHorizontalUnits, 0, SpringLayout.NORTH, labelHorizontal);
		springLayout.putConstraint(SpringLayout.WEST, comboHorizontalUnits, 0, SpringLayout.EAST, textHorizontal);
		comboHorizontalUnits.setPreferredSize(new Dimension(50, 20));
		add(comboHorizontalUnits);
		
		comboVerticalUnits = new JComboBox();
		springLayout.putConstraint(SpringLayout.NORTH, comboVerticalUnits, 0, SpringLayout.NORTH, labelHorizontal);
		springLayout.putConstraint(SpringLayout.WEST, comboVerticalUnits, 0, SpringLayout.EAST, textVertical);
		comboVerticalUnits.setPreferredSize(new Dimension(50, 20));
		add(comboVerticalUnits);
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
			
			Utility.loadCssUnits(comboHorizontalUnits);
			Utility.loadCssUnits(comboVerticalUnits);
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
			return getHorizontal() + " " + getVertical();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get horizontal.
	 * @return
	 */
	private String getHorizontal() {
		
		try {
			return Utility.getCssValueAndUnits(textHorizontal, comboHorizontalUnits);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get vertical.
	 * @return
	 */
	private String getVertical() {
		
		try {
			return Utility.getCssValueAndUnits(textVertical, comboVerticalUnits);
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
			
			// Initialize controls.
			setHorizontal("0");
			setVertical("0");
	
			if (initialString != null) {
				
				Scanner scanner = new Scanner(initialString.trim());
				
				try {
					// Set values.
					setHorizontal(scanner.next().trim());
					setVertical(scanner.next().trim());
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
	 * Set horizontal.
	 * @param string
	 */
	private void setHorizontal(String string) {
		try {
			
			Utility.setCssValueAndUnits(string, textHorizontal, comboHorizontalUnits);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set vertical.
	 * @param string
	 */
	private void setVertical(String string) {
		try {
			
			Utility.setCssValueAndUnits(string, textVertical, comboVerticalUnits);
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
			
			Utility.localize(labelHorizontal);
			Utility.localize(labelVertical);
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
			return Resources.getString("org.multipage.gui.textCssSpacingBuilder");
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
		
		CssSpacingPanel.bounds = bounds;
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
		
		return meansCssSpacing;
	}

	/**
	 * Set controls grayed. If a false value is returned a high level editor grayed this panel.
	 */
	@Override
	public boolean setControlsGrayed(boolean isDefault) {
		
		return false;
	}
}
