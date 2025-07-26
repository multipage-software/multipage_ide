/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Panel that displays editor for CSS border radius.
 * @author vakol
 *
 */
public class CssBorderRadiusPanel extends InsertPanel implements StringValueEditor {
	
	/**
	 * Components.
	 */
	private JPanel panel;
	private JLabel labelTopLeft;
	private TextFieldEx textTopLeft;
	private JComboBox comboTopLeft;
	private TextFieldEx textBottomLeft;
	private JLabel labelBottomLeft;
	private JComboBox comboBottomLeft;
	private JLabel labelTopRight;
	private TextFieldEx textTopRight;
	private JComboBox comboTopRight;
	private TextFieldEx textBottomRight;
	private JLabel labelBottomRight;
	private JComboBox comboBottomRight;

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
		
		bounds = new Rectangle(0, 0, 469, 188);
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
	public CssBorderRadiusPanel(String initialString) {
		
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
		
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(460, 140));
		springLayout.putConstraint(SpringLayout.NORTH, panel, 30, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, panel, 24, SpringLayout.WEST, this);
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		add(panel);
		SpringLayout sl_panel = new SpringLayout();
		panel.setLayout(sl_panel);
		
		labelTopLeft = new JLabel("org.multipage.gui.textCssRadiusTopLeft");
		sl_panel.putConstraint(SpringLayout.NORTH, labelTopLeft, 30, SpringLayout.NORTH, panel);
		labelTopLeft.setHorizontalAlignment(SwingConstants.TRAILING);
		labelTopLeft.setPreferredSize(new Dimension(80, 14));
		sl_panel.putConstraint(SpringLayout.WEST, labelTopLeft, 10, SpringLayout.WEST, panel);
		panel.add(labelTopLeft);
		
		textTopLeft = new TextFieldEx();
		sl_panel.putConstraint(SpringLayout.NORTH, textTopLeft, 0, SpringLayout.NORTH, labelTopLeft);
		sl_panel.putConstraint(SpringLayout.WEST, textTopLeft, 6, SpringLayout.EAST, labelTopLeft);
		textTopLeft.setColumns(5);
		panel.add(textTopLeft);
		
		comboTopLeft = new JComboBox();
		sl_panel.putConstraint(SpringLayout.NORTH, comboTopLeft, 0, SpringLayout.NORTH, labelTopLeft);
		sl_panel.putConstraint(SpringLayout.WEST, comboTopLeft, 0, SpringLayout.EAST, textTopLeft);
		comboTopLeft.setPreferredSize(new Dimension(50, 20));
		panel.add(comboTopLeft);
		
		textBottomLeft = new TextFieldEx();
		sl_panel.putConstraint(SpringLayout.NORTH, textBottomLeft, 31, SpringLayout.SOUTH, textTopLeft);
		sl_panel.putConstraint(SpringLayout.WEST, textBottomLeft, 0, SpringLayout.WEST, textTopLeft);
		textBottomLeft.setColumns(5);
		panel.add(textBottomLeft);
		
		labelBottomLeft = new JLabel("org.multipage.gui.textCssRadiusBottomLeft");
		labelBottomLeft.setHorizontalAlignment(SwingConstants.TRAILING);
		labelBottomLeft.setPreferredSize(new Dimension(80, 14));
		sl_panel.putConstraint(SpringLayout.NORTH, labelBottomLeft, 0, SpringLayout.NORTH, textBottomLeft);
		sl_panel.putConstraint(SpringLayout.EAST, labelBottomLeft, 0, SpringLayout.EAST, labelTopLeft);
		panel.add(labelBottomLeft);
		
		comboBottomLeft = new JComboBox();
		sl_panel.putConstraint(SpringLayout.NORTH, comboBottomLeft, 0, SpringLayout.NORTH, textBottomLeft);
		sl_panel.putConstraint(SpringLayout.WEST, comboBottomLeft, 0, SpringLayout.WEST, comboTopLeft);
		comboBottomLeft.setPreferredSize(new Dimension(50, 20));
		panel.add(comboBottomLeft);
		
		labelTopRight = new JLabel("org.multipage.gui.textCssRadiusTopRight");
		sl_panel.putConstraint(SpringLayout.WEST, labelTopRight, 20, SpringLayout.EAST, comboTopLeft);
		labelTopRight.setHorizontalAlignment(SwingConstants.TRAILING);
		labelTopRight.setPreferredSize(new Dimension(80, 14));
		sl_panel.putConstraint(SpringLayout.NORTH, labelTopRight, 0, SpringLayout.NORTH, labelTopLeft);
		panel.add(labelTopRight);
		
		textTopRight = new TextFieldEx();
		sl_panel.putConstraint(SpringLayout.NORTH, textTopRight, 0, SpringLayout.NORTH, labelTopLeft);
		sl_panel.putConstraint(SpringLayout.WEST, textTopRight, 6, SpringLayout.EAST, labelTopRight);
		textTopRight.setColumns(5);
		panel.add(textTopRight);
		
		comboTopRight = new JComboBox();
		sl_panel.putConstraint(SpringLayout.NORTH, comboTopRight, 0, SpringLayout.NORTH, labelTopLeft);
		sl_panel.putConstraint(SpringLayout.WEST, comboTopRight, 0, SpringLayout.EAST, textTopRight);
		comboTopRight.setPreferredSize(new Dimension(50, 20));
		panel.add(comboTopRight);
		
		textBottomRight = new TextFieldEx();
		sl_panel.putConstraint(SpringLayout.NORTH, textBottomRight, 0, SpringLayout.NORTH, textBottomLeft);
		sl_panel.putConstraint(SpringLayout.WEST, textBottomRight, 0, SpringLayout.WEST, textTopRight);
		textBottomRight.setColumns(5);
		panel.add(textBottomRight);
		
		labelBottomRight = new JLabel("org.multipage.gui.textCssRadiusBottomRight");
		labelBottomRight.setHorizontalAlignment(SwingConstants.TRAILING);
		labelBottomRight.setPreferredSize(new Dimension(80, 14));
		sl_panel.putConstraint(SpringLayout.NORTH, labelBottomRight, 0, SpringLayout.NORTH, textBottomLeft);
		sl_panel.putConstraint(SpringLayout.EAST, labelBottomRight, 0, SpringLayout.EAST, labelTopRight);
		panel.add(labelBottomRight);
		
		comboBottomRight = new JComboBox();
		sl_panel.putConstraint(SpringLayout.NORTH, comboBottomRight, 0, SpringLayout.NORTH, textBottomLeft);
		sl_panel.putConstraint(SpringLayout.WEST, comboBottomRight, 0, SpringLayout.WEST, comboTopRight);
		comboBottomRight.setPreferredSize(new Dimension(50, 20));
		panel.add(comboBottomRight);
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
			
			Utility.loadCssUnits(comboBottomLeft);
			Utility.loadCssUnits(comboBottomRight);
			Utility.loadCssUnits(comboTopLeft);
			Utility.loadCssUnits(comboTopRight);
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
			return getTopLeft() + " " + getTopRight() + " " + getBottomRight() + " " + getBottomLeft();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get top left.
	 * @return
	 */
	private String getTopLeft() {
		
		try {
			return Utility.getCssValueAndUnits(textTopLeft, comboTopLeft);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get top right.
	 * @return
	 */
	private String getTopRight() {
		
		try {
			return Utility.getCssValueAndUnits(textTopRight, comboTopRight);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get bottom right.
	 * @return
	 */
	private String getBottomRight() {
		
		try {
			return Utility.getCssValueAndUnits(textBottomRight, comboBottomRight);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get bottom left.
	 * @return
	 */
	private String getBottomLeft() {
		
		try {
			return Utility.getCssValueAndUnits(textBottomLeft, comboBottomLeft);
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
			
			setTopLeft("0px");
			setTopRight("0px");
			setBottomRight("0px");
			setBottomLeft("0px");
	
			if (initialString != null) {
			
				Scanner scanner = new Scanner(initialString.trim());
				
				try {
					// Set values.
					setTopLeft(scanner.next().trim());
					setTopRight(scanner.next().trim());
					setBottomRight(scanner.next().trim());
					setBottomLeft(scanner.next().trim());
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
	 * Set top left.
	 * @param string
	 */
	private void setTopLeft(String string) {
		try {
			
			Utility.setCssValueAndUnits(string, textTopLeft, comboTopLeft);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set top right.
	 * @param string
	 */
	private void setTopRight(String string) {
		try {
			
			Utility.setCssValueAndUnits(string, textTopRight, comboTopRight);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set bottom right.
	 * @param string
	 */
	private void setBottomRight(String string) {
		try {
			
			Utility.setCssValueAndUnits(string, textBottomRight, comboBottomRight);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set bottom left.
	 * @param string
	 */
	private void setBottomLeft(String string) {
		try {
			
			Utility.setCssValueAndUnits(string, textBottomLeft, comboBottomLeft);
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
			
			Utility.localize(labelBottomLeft);
			Utility.localize(labelBottomRight);
			Utility.localize(labelTopLeft);
			Utility.localize(labelTopRight);
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
			return Resources.getString("org.multipage.gui.textCssBorderRadiusBuilder");
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
		
		CssBorderRadiusPanel.bounds = bounds;
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
		
		return meansCssBorderRadius;
	}

	/**
	 * Set controls grayed. If a false value is returned a high level editor grayed this panel.
	 */
	@Override
	public boolean setControlsGrayed(boolean isDefault) {
		
		return false;
	}
}
