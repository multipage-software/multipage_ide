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

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.LineBorder;

import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Panel that displays editor for the CSS border values.
 * @author vakol
 *
 */
public class CssBorderPanel extends InsertPanel implements StringValueEditor {

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
	private static String style;
	private static String width;


	/**
	 * Set default states.
	 */
	public static void setDefaultData() {
		
		bounds = new Rectangle(0, 0, 400, 230);
		boundsSet = false;
		colorState = Color.BLACK;
		style = "none";
		width = "medium";
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
		style = inputStream.readUTF();
		width = inputStream.readUTF();
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
		outputStream.writeUTF(style == null ? "" : style);
		outputStream.writeUTF(width == null ? "" : width);
	}

	/**
	 * Border color.
	 */
	private Color color;
	
	/**
	 * Initial string.
	 */
	private String initialString;
	
	// $hide<<$
	private JLabel labelBorderStyle;
	private JComboBox comboBoxStyle;
	private JLabel labelBorderWidth;
	private JComboBox comboBoxWidth;
	private JTextField textWidth;
	private JComboBox comboBoxUnits;
	private JLabel labelBorderColor;
	private JPanel panelColor;

	/**
	 * Create the dialog.
	 * @param parentWindow 
	 */
	public CssBorderPanel(String initialString) {
		
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
		
		labelBorderStyle = new JLabel("org.multipage.gui.textBorderStyle");
		sl_panelMain.putConstraint(SpringLayout.NORTH, labelBorderStyle, 20, SpringLayout.NORTH, this);
		sl_panelMain.putConstraint(SpringLayout.WEST, labelBorderStyle, 10, SpringLayout.WEST, this);
		add(labelBorderStyle);
		
		comboBoxStyle = new JComboBox();
		sl_panelMain.putConstraint(SpringLayout.NORTH, comboBoxStyle, 20, SpringLayout.NORTH, this);
		comboBoxStyle.setPreferredSize(new Dimension(150, 20));
		sl_panelMain.putConstraint(SpringLayout.WEST, comboBoxStyle, 6, SpringLayout.EAST, labelBorderStyle);
		add(comboBoxStyle);
		
		labelBorderWidth = new JLabel("org.multipage.gui.textBorderWidth");
		sl_panelMain.putConstraint(SpringLayout.NORTH, labelBorderWidth, 26, SpringLayout.SOUTH, labelBorderStyle);
		sl_panelMain.putConstraint(SpringLayout.WEST, labelBorderWidth, 0, SpringLayout.WEST, labelBorderStyle);
		add(labelBorderWidth);
		
		comboBoxWidth = new JComboBox();
		comboBoxWidth.setPreferredSize(new Dimension(100, 20));
		sl_panelMain.putConstraint(SpringLayout.NORTH, comboBoxWidth, -3, SpringLayout.NORTH, labelBorderWidth);
		sl_panelMain.putConstraint(SpringLayout.WEST, comboBoxWidth, 0, SpringLayout.WEST, comboBoxStyle);
		add(comboBoxWidth);
		
		textWidth = new JTextField();
		sl_panelMain.putConstraint(SpringLayout.NORTH, textWidth, 0, SpringLayout.NORTH, comboBoxWidth);
		sl_panelMain.putConstraint(SpringLayout.WEST, textWidth, 6, SpringLayout.EAST, comboBoxWidth);
		add(textWidth);
		textWidth.setColumns(6);
		
		comboBoxUnits = new JComboBox();
		comboBoxUnits.setPreferredSize(new Dimension(50, 20));
		sl_panelMain.putConstraint(SpringLayout.NORTH, comboBoxUnits, 0, SpringLayout.NORTH, textWidth);
		sl_panelMain.putConstraint(SpringLayout.WEST, comboBoxUnits, 6, SpringLayout.EAST, textWidth);
		add(comboBoxUnits);
		
		labelBorderColor = new JLabel("org.multipage.gui.textBorderColor");
		sl_panelMain.putConstraint(SpringLayout.NORTH, labelBorderColor, 26, SpringLayout.SOUTH, labelBorderWidth);
		sl_panelMain.putConstraint(SpringLayout.WEST, labelBorderColor, 0, SpringLayout.WEST, labelBorderStyle);
		add(labelBorderColor);
		
		panelColor = new JPanel();
		panelColor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				onSelectColor();
			}
		});
		sl_panelMain.putConstraint(SpringLayout.WEST, panelColor, 6, SpringLayout.EAST, labelBorderColor);
		panelColor.setBorder(new LineBorder(Color.LIGHT_GRAY));
		panelColor.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		panelColor.setPreferredSize(new Dimension(80, 20));
		sl_panelMain.putConstraint(SpringLayout.NORTH, panelColor, 0, SpringLayout.NORTH, labelBorderColor);
		add(panelColor);
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
			
			loadFromString(initialString);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load from initial string.
	 */
	private void loadFromString(String string) {
		try {
			
			initialString = string;
			
			setBorderStyle(style);
			setBorderWidth(width);
			setBorderColor(colorState);
			
			if (initialString != null) {
				setFromInitialString();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set border color.
	 * @param color
	 */
	private void setBorderColor(Color color) {
		try {
			
			this.color = color;
			panelColor.setBackground(color);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set border color.
	 * @param cssColor
	 */
	private void setBorderColor(String cssColor) {
		try {
			
			if (cssColor.length() != 7 && cssColor.charAt(0) != '#') {
				return;
			}
			
			int red = Integer.parseInt(cssColor.substring(1, 3), 16);
			int green = Integer.parseInt(cssColor.substring(3, 5), 16);
			int blue = Integer.parseInt(cssColor.substring(5, 7), 16);
			
			Color color = new Color(red, green, blue);
			setBorderColor(color);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set border width.
	 * @param width
	 */
	private void setBorderWidth(String width) {
		try {
			
			CssBorderPanel.width = width;
			
			// Try to select width combo.
			if (Utility.selectComboNamedItem(comboBoxWidth, width)) {
				textWidth.setText("");
				return;
			}
			
			// Get width and unit.
			Obj<String> number = new Obj<String>();
			Obj<String> unit = new Obj<String>();
			Utility.convertCssStringToNumberUnit(width, number, unit);
			
			textWidth.setText(number.ref);
			
			// Select unit.
			Utility.selectComboItem(comboBoxUnits, unit.ref);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set border style.
	 * @param style
	 */
	private void setBorderStyle(String style) {
		try {
			
			CssBorderPanel.style = style;
			
			// Try to select style combo.
			Utility.selectComboNamedItem(comboBoxStyle, style);
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
		try {
			
			style = getBorderStyle();
			width = getBorderWidth();
			
			colorState = color;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			localize();
			
			loadBorderStyles();
			loadBorderWidths();
			
			loadDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load border widths.
	 */
	private void loadBorderWidths() {
		try {
			
			final NamedItem [] widths = {
					new NamedItem("org.multipage.gui.textBorderWidthThin", "thin"),
					new NamedItem("org.multipage.gui.textBorderWidthMedium", "medium"),
					new NamedItem("org.multipage.gui.textBorderWidthThick", "thick")
			};
			
			for (NamedItem width : widths) {
				comboBoxWidth.addItem(width);
			}
			
			Utility.loadCssUnits(comboBoxUnits);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load border styles.
	 */
	private void loadBorderStyles() {
		try {
			
			final NamedItem [] styles = {
					new NamedItem("org.multipage.gui.textBorderStyleNone", "none"),
					new NamedItem("org.multipage.gui.textBorderStyleDotted", "dotted"),
					new NamedItem("org.multipage.gui.textBorderStyleDashed", "dashed"),
					new NamedItem("org.multipage.gui.textBorderStyleSolid", "solid"),
					new NamedItem("org.multipage.gui.textBorderStyleDouble", "double"),
					new NamedItem("org.multipage.gui.textBorderStyleGroove", "groove"),
					new NamedItem("org.multipage.gui.textBorderStyleRidge", "ridge"),
					new NamedItem("org.multipage.gui.textBorderStyleInset", "inset"),
					new NamedItem("org.multipage.gui.textBorderStyleOutset", "outset")
			};
			
			for (NamedItem style : styles) {
				comboBoxStyle.addItem(style);
			}
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
			return getBorderStyle() + " " + getBorderWidth() + " " + getBorderColor();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get border color.
	 * @return
	 */
	private String getBorderColor() {
		
		try {
			return Utility.getCssColor(color);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get border width.
	 * @return
	 */
	private String getBorderWidth() {
		
		// Try to get width number.
		try {
			
			String numberText = textWidth.getText();
			if (!numberText.isEmpty()) {
				
				double number = Double.parseDouble(numberText);
				
				// Get unit.
				String unit = (String) comboBoxUnits.getSelectedItem();
				
				return String.valueOf(number) + unit;
			}
		}
		catch (Exception e) {
			Safe.exception(e);
		}
		
		try {
			Object object = comboBoxWidth.getSelectedItem();
			if (!(object instanceof NamedItem)) {
				return "";
			}
			
			return ((NamedItem) object).value;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get border style.
	 * @return
	 */
	private String getBorderStyle() {
		
		try {
			Object object = comboBoxStyle.getSelectedItem();
			if (!(object instanceof NamedItem)) {
				return "none";
			}
			
			return ((NamedItem) object).value;
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
			
			Scanner scanner = new Scanner(initialString.trim());
			
			try {
				// Get style.
				String style = scanner.next();
				if (style != null) {
					setBorderStyle(style.trim());
					
					// Get width.
					String width = scanner.next();
					if (width != null) {
						setBorderWidth(width.trim());
						
						// Get color.
						String color = scanner.nextLine();
						if (color != null) {
							setBorderColor(color.trim());
						}
					}
				}
			}
			catch (Exception e) {
				Safe.exception(e);
			}
			
		    scanner.close();
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
			
			Utility.localize(labelBorderStyle);
			Utility.localize(labelBorderWidth);
			Utility.localize(labelBorderColor);
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
			return Resources.getString("org.multipage.gui.textCssBorderBuilder");
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
		
		CssBorderPanel.bounds = bounds;
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
			
			loadFromString(string);
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
		
		return meansCssBorder;
	}

	/**
	 * Set controls grayed. If a false value is returned a high level editor grayed this panel.
	 */
	@Override
	public boolean setControlsGrayed(boolean isDefault) {
		
		return false;
	}
}
