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
 * Panel that displays editor for CSS clip region.
 * @author vakol
 *
 */
public class CssClipPanel extends InsertPanel implements StringValueEditor {
	
	/**
	 * Components.
	 */
	private JPanel panel;
	private JLabel labelTop;
	private TextFieldEx textTop;
	private JComboBox comboTopUnits;
	private TextFieldEx textBottom;
	private JLabel labelBottom;
	private JComboBox comboBottomUnits;
	private JLabel labelRight;
	private TextFieldEx textRight;
	private JComboBox comboRightUnits;
	private TextFieldEx textLeft;
	private JLabel labelLeft;
	private JComboBox comboLeftUnits;

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
	 * Create the dialog.
	 * @param parentWindow 
	 */
	public CssClipPanel(String initialString) {
		
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
		panel.setPreferredSize(new Dimension(420, 140));
		springLayout.putConstraint(SpringLayout.NORTH, panel, 30, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, panel, 24, SpringLayout.WEST, this);
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		add(panel);
		SpringLayout sl_panel = new SpringLayout();
		panel.setLayout(sl_panel);
		
		labelTop = new JLabel("org.multipage.gui.textCssClipTop");
		sl_panel.putConstraint(SpringLayout.NORTH, labelTop, 30, SpringLayout.NORTH, panel);
		labelTop.setHorizontalAlignment(SwingConstants.TRAILING);
		labelTop.setPreferredSize(new Dimension(80, 14));
		sl_panel.putConstraint(SpringLayout.WEST, labelTop, 10, SpringLayout.WEST, panel);
		panel.add(labelTop);
		
		textTop = new TextFieldEx();
		sl_panel.putConstraint(SpringLayout.NORTH, textTop, 0, SpringLayout.NORTH, labelTop);
		sl_panel.putConstraint(SpringLayout.WEST, textTop, 6, SpringLayout.EAST, labelTop);
		textTop.setColumns(5);
		panel.add(textTop);
		
		comboTopUnits = new JComboBox();
		sl_panel.putConstraint(SpringLayout.NORTH, comboTopUnits, 0, SpringLayout.NORTH, labelTop);
		sl_panel.putConstraint(SpringLayout.WEST, comboTopUnits, 0, SpringLayout.EAST, textTop);
		comboTopUnits.setPreferredSize(new Dimension(50, 20));
		panel.add(comboTopUnits);
		
		textBottom = new TextFieldEx();
		sl_panel.putConstraint(SpringLayout.NORTH, textBottom, 31, SpringLayout.SOUTH, textTop);
		sl_panel.putConstraint(SpringLayout.WEST, textBottom, 0, SpringLayout.WEST, textTop);
		textBottom.setColumns(5);
		panel.add(textBottom);
		
		labelBottom = new JLabel("org.multipage.gui.textCssClipBottom");
		labelBottom.setHorizontalAlignment(SwingConstants.TRAILING);
		labelBottom.setPreferredSize(new Dimension(80, 14));
		sl_panel.putConstraint(SpringLayout.NORTH, labelBottom, 0, SpringLayout.NORTH, textBottom);
		sl_panel.putConstraint(SpringLayout.EAST, labelBottom, 0, SpringLayout.EAST, labelTop);
		panel.add(labelBottom);
		
		comboBottomUnits = new JComboBox();
		sl_panel.putConstraint(SpringLayout.NORTH, comboBottomUnits, 0, SpringLayout.NORTH, textBottom);
		sl_panel.putConstraint(SpringLayout.WEST, comboBottomUnits, 0, SpringLayout.WEST, comboTopUnits);
		comboBottomUnits.setPreferredSize(new Dimension(50, 20));
		panel.add(comboBottomUnits);
		
		labelRight = new JLabel("org.multipage.gui.textCssClipRight");
		sl_panel.putConstraint(SpringLayout.WEST, labelRight, 20, SpringLayout.EAST, comboTopUnits);
		labelRight.setHorizontalAlignment(SwingConstants.TRAILING);
		labelRight.setPreferredSize(new Dimension(80, 14));
		sl_panel.putConstraint(SpringLayout.NORTH, labelRight, 0, SpringLayout.NORTH, labelTop);
		panel.add(labelRight);
		
		textRight = new TextFieldEx();
		sl_panel.putConstraint(SpringLayout.NORTH, textRight, 0, SpringLayout.NORTH, labelTop);
		sl_panel.putConstraint(SpringLayout.WEST, textRight, 6, SpringLayout.EAST, labelRight);
		textRight.setColumns(5);
		panel.add(textRight);
		
		comboRightUnits = new JComboBox();
		sl_panel.putConstraint(SpringLayout.NORTH, comboRightUnits, 0, SpringLayout.NORTH, labelTop);
		sl_panel.putConstraint(SpringLayout.WEST, comboRightUnits, 0, SpringLayout.EAST, textRight);
		comboRightUnits.setPreferredSize(new Dimension(50, 20));
		panel.add(comboRightUnits);
		
		textLeft = new TextFieldEx();
		sl_panel.putConstraint(SpringLayout.NORTH, textLeft, 0, SpringLayout.NORTH, textBottom);
		sl_panel.putConstraint(SpringLayout.WEST, textLeft, 0, SpringLayout.WEST, textRight);
		textLeft.setColumns(5);
		panel.add(textLeft);
		
		labelLeft = new JLabel("org.multipage.gui.textCssClipLeft");
		labelLeft.setHorizontalAlignment(SwingConstants.TRAILING);
		labelLeft.setPreferredSize(new Dimension(80, 14));
		sl_panel.putConstraint(SpringLayout.NORTH, labelLeft, 0, SpringLayout.NORTH, textBottom);
		sl_panel.putConstraint(SpringLayout.EAST, labelLeft, 0, SpringLayout.EAST, labelRight);
		panel.add(labelLeft);
		
		comboLeftUnits = new JComboBox();
		sl_panel.putConstraint(SpringLayout.NORTH, comboLeftUnits, 0, SpringLayout.NORTH, textBottom);
		sl_panel.putConstraint(SpringLayout.WEST, comboLeftUnits, 0, SpringLayout.WEST, comboRightUnits);
		comboLeftUnits.setPreferredSize(new Dimension(50, 20));
		panel.add(comboLeftUnits);
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
			
			Utility.loadCssUnits(comboBottomUnits);
			Utility.loadCssUnits(comboLeftUnits);
			Utility.loadCssUnits(comboTopUnits);
			Utility.loadCssUnits(comboRightUnits);
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
			return "rect(" + getTop() + ", " + getRight() + ", " + getBottom() + ", " + getLeft() + ")";
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get top.
	 * @return
	 */
	private String getTop() {
		
		try {
			return Utility.getCssValueAndUnits(textTop, comboTopUnits);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get right.
	 * @return
	 */
	private String getRight() {
		
		try {
			return Utility.getCssValueAndUnits(textRight, comboRightUnits);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get bottom.
	 * @return
	 */
	private String getBottom() {
		
		try {
			return Utility.getCssValueAndUnits(textBottom, comboBottomUnits);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get left.
	 * @return
	 */
	private String getLeft() {
		
		try {
			return Utility.getCssValueAndUnits(textLeft, comboLeftUnits);
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
			setTop("0px");
			setRight("0px");
			setBottom("0px");
			setLeft("0px");
	
			if (initialString != null) {
			
				String trimmedString = initialString.replaceAll("rect\\s*\\(", "");
				trimmedString = trimmedString.replaceAll(",", "");
				trimmedString = trimmedString.replaceAll("\\)", "");
				
				Scanner scanner = new Scanner(trimmedString.trim());
				
				try {
					// Set values.
					setTop(scanner.next().trim());
					setRight(scanner.next().trim());
					setBottom(scanner.next().trim());
					setLeft(scanner.next().trim());
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
	 * Set top.
	 * @param string
	 */
	private void setTop(String string) {
		try {
			
			Utility.setCssValueAndUnits(string, textTop, comboTopUnits);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set right.
	 * @param string
	 */
	private void setRight(String string) {
		try {
			
			Utility.setCssValueAndUnits(string, textRight, comboRightUnits);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set bottom.
	 * @param string
	 */
	private void setBottom(String string) {
		try {
			
			Utility.setCssValueAndUnits(string, textBottom, comboBottomUnits);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set left.
	 * @param string
	 */
	private void setLeft(String string) {
		try {
			
			Utility.setCssValueAndUnits(string, textLeft, comboLeftUnits);
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
			
			Utility.localize(labelBottom);
			Utility.localize(labelLeft);
			Utility.localize(labelTop);
			Utility.localize(labelRight);
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
			return Resources.getString("org.multipage.gui.textCssClipBuilder");
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
		
		CssClipPanel.bounds = bounds;
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
		
		return meansCssClip;
	}

	/**
	 * Set controls grayed. If a false value is returned a high level editor grayed this panel.
	 */
	@Override
	public boolean setControlsGrayed(boolean isDefault) {
		
		return false;
	}
}
