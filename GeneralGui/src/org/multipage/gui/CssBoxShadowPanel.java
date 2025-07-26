/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.LineBorder;

import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Panel that displays editor for CSS box shadow.
 * @author vakol
 *
 */
public class CssBoxShadowPanel extends InsertPanel implements StringValueEditor {
	
	/**
	 * Components.
	 */
	private JLabel labelHorizontal;
	private TextFieldEx textHorizontal;
	private JComboBox comboHorizontalUnits;
	private JLabel labelVertical;
	private TextFieldEx textVertical;
	private JComboBox comboVerticalUnits;
	private TextFieldEx textBlur;
	private JLabel labelBlur;
	private JComboBox comboBlurUnits;
	private TextFieldEx textSpread;
	private JComboBox comboSpreadUnits;
	private JLabel labelSpread;
	private JPanel panelColor;
	private JLabel labelColor;
	private JCheckBox checkInset;

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
	private static Color colorState;
	
	/**
	 * Set default states.
	 */
	public static void setDefaultData() {
		
		bounds = new Rectangle(0, 0, 500, 260);
		boundsSet = false;
		colorState = Color.BLACK;
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
		colorState = Utility.readInputStreamObject(inputStream, Color.class);
	}

	/**
	 * Save serialized states.
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream)
			throws IOException {
		
		outputStream.writeObject(bounds);
		outputStream.writeObject(colorState);
	}

	/**
	 * Initial string.
	 */
	private String initialString;
	
	/**
	 * Color.
	 */
	private Color color;
	
	// $hide<<$

	/**
	 * Create the dialog.
	 * @param parentWindow 
	 */
	public CssBoxShadowPanel(String initialString) {

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
		
		labelHorizontal = new JLabel("org.multipage.gui.textShadowHorizontalPosition");
		sl_panelMain.putConstraint(SpringLayout.NORTH, labelHorizontal, 43, SpringLayout.NORTH, this);
		sl_panelMain.putConstraint(SpringLayout.WEST, labelHorizontal, 20, SpringLayout.WEST, this);
		add(labelHorizontal);
		
		textHorizontal = new TextFieldEx();
		sl_panelMain.putConstraint(SpringLayout.NORTH, textHorizontal, 0, SpringLayout.NORTH, labelHorizontal);
		sl_panelMain.putConstraint(SpringLayout.WEST, textHorizontal, 6, SpringLayout.EAST, labelHorizontal);
		textHorizontal.setColumns(5);
		add(textHorizontal);
		
		comboHorizontalUnits = new JComboBox();
		sl_panelMain.putConstraint(SpringLayout.NORTH, comboHorizontalUnits, 0, SpringLayout.NORTH, labelHorizontal);
		sl_panelMain.putConstraint(SpringLayout.WEST, comboHorizontalUnits, 0, SpringLayout.EAST, textHorizontal);
		comboHorizontalUnits.setPreferredSize(new Dimension(50, 20));
		add(comboHorizontalUnits);
		
		labelVertical = new JLabel("org.multipage.gui.textShadowVerticalPosition");
		sl_panelMain.putConstraint(SpringLayout.NORTH, labelVertical, 0, SpringLayout.NORTH, labelHorizontal);
		sl_panelMain.putConstraint(SpringLayout.WEST, labelVertical, 16, SpringLayout.EAST, comboHorizontalUnits);
		add(labelVertical);
		
		textVertical = new TextFieldEx();
		sl_panelMain.putConstraint(SpringLayout.NORTH, textVertical, 0, SpringLayout.NORTH, labelHorizontal);
		sl_panelMain.putConstraint(SpringLayout.WEST, textVertical, 6, SpringLayout.EAST, labelVertical);
		textVertical.setColumns(5);
		add(textVertical);
		
		comboVerticalUnits = new JComboBox();
		sl_panelMain.putConstraint(SpringLayout.NORTH, comboVerticalUnits, 0, SpringLayout.NORTH, labelHorizontal);
		sl_panelMain.putConstraint(SpringLayout.WEST, comboVerticalUnits, 0, SpringLayout.EAST, textVertical);
		comboVerticalUnits.setPreferredSize(new Dimension(50, 20));
		add(comboVerticalUnits);
		
		textBlur = new TextFieldEx();
		sl_panelMain.putConstraint(SpringLayout.NORTH, textBlur, 24, SpringLayout.SOUTH, textHorizontal);
		sl_panelMain.putConstraint(SpringLayout.WEST, textBlur, 0, SpringLayout.WEST, textHorizontal);
		textBlur.setColumns(5);
		add(textBlur);
		
		labelBlur = new JLabel("org.multipage.gui.textShadowBlur");
		sl_panelMain.putConstraint(SpringLayout.NORTH, labelBlur, 0, SpringLayout.NORTH, textBlur);
		sl_panelMain.putConstraint(SpringLayout.EAST, labelBlur, 0, SpringLayout.EAST, labelHorizontal);
		add(labelBlur);
		
		comboBlurUnits = new JComboBox();
		sl_panelMain.putConstraint(SpringLayout.NORTH, comboBlurUnits, 0, SpringLayout.NORTH, textBlur);
		sl_panelMain.putConstraint(SpringLayout.WEST, comboBlurUnits, 0, SpringLayout.WEST, comboHorizontalUnits);
		comboBlurUnits.setPreferredSize(new Dimension(50, 20));
		add(comboBlurUnits);
		
		textSpread = new TextFieldEx();
		sl_panelMain.putConstraint(SpringLayout.NORTH, textSpread, 0, SpringLayout.NORTH, textBlur);
		sl_panelMain.putConstraint(SpringLayout.WEST, textSpread, 0, SpringLayout.WEST, textVertical);
		textSpread.setColumns(5);
		add(textSpread);
		
		comboSpreadUnits = new JComboBox();
		sl_panelMain.putConstraint(SpringLayout.NORTH, comboSpreadUnits, 0, SpringLayout.NORTH, textBlur);
		sl_panelMain.putConstraint(SpringLayout.WEST, comboSpreadUnits, 0, SpringLayout.WEST, comboVerticalUnits);
		comboSpreadUnits.setPreferredSize(new Dimension(50, 20));
		add(comboSpreadUnits);
		
		labelSpread = new JLabel("org.multipage.gui.textShadowSpread");
		sl_panelMain.putConstraint(SpringLayout.NORTH, labelSpread, 0, SpringLayout.NORTH, textBlur);
		sl_panelMain.putConstraint(SpringLayout.EAST, labelSpread, 0, SpringLayout.EAST, labelVertical);
		add(labelSpread);
		
		panelColor = new JPanel();
		panelColor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				onSelectColor();
			}
		});
		sl_panelMain.putConstraint(SpringLayout.EAST, panelColor, 0, SpringLayout.EAST, comboHorizontalUnits);
		panelColor.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		sl_panelMain.putConstraint(SpringLayout.NORTH, panelColor, 24, SpringLayout.SOUTH, textBlur);
		sl_panelMain.putConstraint(SpringLayout.WEST, panelColor, 0, SpringLayout.WEST, textHorizontal);
		panelColor.setPreferredSize(new Dimension(80, 20));
		panelColor.setBorder(new LineBorder(Color.LIGHT_GRAY));
		add(panelColor);
		
		labelColor = new JLabel("org.multipage.gui.textShadowColor");
		sl_panelMain.putConstraint(SpringLayout.NORTH, labelColor, 30, SpringLayout.SOUTH, labelBlur);
		sl_panelMain.putConstraint(SpringLayout.EAST, labelColor, 0, SpringLayout.EAST, labelHorizontal);
		add(labelColor);
		
		checkInset = new JCheckBox("org.multipage.gui.textShadowInset");
		checkInset.setIconTextGap(6);
		sl_panelMain.putConstraint(SpringLayout.NORTH, checkInset, 0, SpringLayout.NORTH, panelColor);
		sl_panelMain.putConstraint(SpringLayout.WEST, checkInset, 10, SpringLayout.WEST, labelVertical);
		add(checkInset);
	}

	/**
	 * On select color.
	 */
	protected void onSelectColor() {
		try {
			
			Color newColor = Utility.chooseColor(this, color);
			
			if (newColor != null) {
				color = newColor;
				
				panelColor.setBackground(color);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
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
	 * Set color.
	 * @param color
	 */
	private void setColor(Color color) {
		try {
			
			this.color = color;
			panelColor.setBackground(color);
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
		
		colorState = color;
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
			Utility.loadCssUnits(comboBlurUnits);
			Utility.loadCssUnits(comboSpreadUnits);
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
			String inset = getInset();
			if (!inset.isEmpty()) {
				inset = " " + inset;
			}
			return getHorizontal()  + " " + getVertical() + " " + getBlur() + " " + getSpread() + " " + getColor() + inset;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get inset.
	 * @return
	 */
	private String getInset() {
		
		try {
			return checkInset.isSelected() ? "inset" : "";
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get color.
	 * @return
	 */
	private String getColor() {
		
		try {
			return Utility.getCssColor(color);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get spread.
	 * @return
	 */
	private String getSpread() {
		
		try {
			return Utility.getCssValueAndUnits(textSpread, comboSpreadUnits);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get blur.
	 * @return
	 */
	private String getBlur() {
		
		try {
			return Utility.getCssValueAndUnits(textBlur, comboBlurUnits);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get vertical position.
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
	 * Get horizontal position.
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
	 * Set from initial string.
	 */
	private void setFromInitialString() {
		try {
			
			setColor(colorState);
			setHorizontal("0px");
			setVertical("0px");
			setBlur("0px");
			setSpread("0px");
			setInset(false);
	
			if (initialString != null) {
			
				Scanner scanner = new Scanner(initialString.trim());
				
				try {
					// Set values.
					setHorizontal(scanner.next().trim());
					setVertical(scanner.next().trim());
					setBlur(scanner.next().trim());
					setSpread(scanner.next().trim());
					setColor(scanner.next().trim());
	
					String inset = scanner.nextLine().trim();
					if (inset.equals("inset")) {
						setInset(true);
					}
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
	 * Set horizontal position.
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
	 * Set horizontal position.
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
	 * Set blur.
	 * @param string
	 */
	private void setBlur(String string) {
		try {
			
			Utility.setCssValueAndUnits(string, textBlur, comboBlurUnits);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set spread.
	 * @param string
	 */
	private void setSpread(String string) {
		try {
			
			Utility.setCssValueAndUnits(string, textSpread, comboSpreadUnits);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set color.
	 * @param string
	 */
	private void setColor(String string) {
		try {
			
			setColor(Utility.getColorFromCss(string));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set inset shadow.
	 * @param set
	 */
	private void setInset(boolean set) {
		try {
			
			checkInset.setSelected(set);
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
			Utility.localize(labelBlur);
			Utility.localize(labelSpread);
			Utility.localize(labelColor);
			Utility.localize(checkInset);
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
			return Resources.getString("org.multipage.gui.textCssShadowBuilder");
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
		
		CssBoxShadowPanel.bounds = bounds;
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
			return getSpecification();
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
		
		return meansCssBoxShadow;
	}

	/**
	 * Set controls grayed. If a false value is returned a high level editor grayed this panel.
	 */
	@Override
	public boolean setControlsGrayed(boolean isDefault) {
		
		return false;
	}
}
