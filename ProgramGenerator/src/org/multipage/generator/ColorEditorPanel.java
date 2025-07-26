/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.border.BevelBorder;

import org.maclan.ColorObj;
import org.multipage.gui.Images;
import org.multipage.gui.StringValueEditor;
import org.multipage.gui.TextFieldEx;
import org.multipage.gui.TextPaneEx;
import org.multipage.gui.Utility;
import org.multipage.util.Safe;

/**
 * Panel that displays color editor.
 * @author vakol
 *
 */
public class ColorEditorPanel extends JPanel implements SlotValueEditorPanelInterface {
	
	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Color.
	 */
	private ColorObj color;

	/**
	 * Disable change events flag.
	 */
	private boolean disableChangeEvents = false;
	
	// $hide<<$
	/**
	 * Components.
	 */
	private JLabel labelSelectColor;
	private JLabel labelColor;
	private JTextPane textColorDescription;
	private JLabel labelHexaValue;
	private JTextField textHexaValue;
	private JPopupMenu popupMenu;
	private JMenuItem menuCopyColor;
	private JMenuItem menuPasteColor;
	private JLabel labelRgbDecimal;
	private JTextField textRGBValue;
	private JMenuItem menuReset;

	/**
	 * Constructor.
	 */
	public ColorEditorPanel() {
		
		try {
			initComponents();
			// $hide>>$
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
		
		labelSelectColor = new JLabel("org.multipage.generator.textSelectSlotColor");
		springLayout.putConstraint(SpringLayout.NORTH, labelSelectColor, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, labelSelectColor, 0, SpringLayout.WEST, this);
		add(labelSelectColor);
		
		labelColor = new JLabel((String) null);
		labelColor.setBorder(new BevelBorder(BevelBorder.RAISED, new Color(255, 255, 255), new Color(255, 255, 255), new Color(128, 128, 128), new Color(128, 128, 128)));
		labelColor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				onSelectColor(e);
			}
		});
		labelColor.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		labelColor.setPreferredSize(new Dimension(0, 50));
		springLayout.putConstraint(SpringLayout.EAST, labelColor, -10, SpringLayout.EAST, this);
		labelColor.setOpaque(true);
		springLayout.putConstraint(SpringLayout.NORTH, labelColor, 6, SpringLayout.SOUTH, labelSelectColor);
		springLayout.putConstraint(SpringLayout.WEST, labelColor, 10, SpringLayout.WEST, this);
		add(labelColor);
		
		textColorDescription = new TextPaneEx();
		textColorDescription.setOpaque(false);
		textColorDescription.setContentType("text/html");
		textColorDescription.setBorder(null);
		textColorDescription.setEditable(false);
		textColorDescription.setPreferredSize(new Dimension(0, 30));
		springLayout.putConstraint(SpringLayout.NORTH, textColorDescription, 10, SpringLayout.SOUTH, labelColor);
		textColorDescription.setFont(new Font("Tahoma", Font.PLAIN, 13));
		springLayout.putConstraint(SpringLayout.WEST, textColorDescription, 0, SpringLayout.WEST, labelColor);
		springLayout.putConstraint(SpringLayout.EAST, textColorDescription, 0, SpringLayout.EAST, labelColor);
		add(textColorDescription);
		
		labelHexaValue = new JLabel("org.multipage.generator.textHexaColorValue");
		springLayout.putConstraint(SpringLayout.NORTH, labelHexaValue, 6, SpringLayout.SOUTH, textColorDescription);
		springLayout.putConstraint(SpringLayout.WEST, labelHexaValue, 0, SpringLayout.WEST, labelColor);
		springLayout.putConstraint(SpringLayout.EAST, labelHexaValue, 96, SpringLayout.WEST, labelSelectColor);
		
		popupMenu = new JPopupMenu();
		addPopup(labelColor, popupMenu);
		
		menuCopyColor = new JMenuItem("org.multipage.generator.menuCopyColor");
		menuCopyColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCopyColor();
			}
		});
		popupMenu.add(menuCopyColor);
		
		menuPasteColor = new JMenuItem("org.multipage.generator.menuPasteColor");
		menuPasteColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onPasteColor();
			}
		});
		popupMenu.add(menuPasteColor);
		
		popupMenu.addSeparator();
		
		menuReset = new JMenuItem("org.multipage.generator.menuResetColor");
		menuReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onReset();
			}
		});
		popupMenu.add(menuReset);
		add(labelHexaValue);
		
		textHexaValue = new TextFieldEx();

		springLayout.putConstraint(SpringLayout.NORTH, textHexaValue, 6, SpringLayout.SOUTH, labelHexaValue);
		springLayout.putConstraint(SpringLayout.WEST, textHexaValue, 0, SpringLayout.WEST, labelColor);
		add(textHexaValue);
		textHexaValue.setColumns(10);
		
		labelRgbDecimal = new JLabel("org.multipage.generator.textRgbDecimalColorValues");
		springLayout.putConstraint(SpringLayout.WEST, labelRgbDecimal, 16, SpringLayout.EAST, labelHexaValue);
		springLayout.putConstraint(SpringLayout.EAST, labelRgbDecimal, -227, SpringLayout.EAST, this);
		add(labelRgbDecimal);
		
		textRGBValue = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.SOUTH, labelRgbDecimal, -6, SpringLayout.NORTH, textRGBValue);
		springLayout.putConstraint(SpringLayout.WEST, textRGBValue, 16, SpringLayout.EAST, textHexaValue);
		springLayout.putConstraint(SpringLayout.NORTH, textRGBValue, 0, SpringLayout.NORTH, textHexaValue);
		textRGBValue.setColumns(10);
		add(textRGBValue);
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			localize();
			setIcons();
			
			setListeners();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Localize.
	 */
	private void localize() {
		try {
			
			Utility.localize(labelSelectColor);
			Utility.localize(menuCopyColor);
			Utility.localize(menuPasteColor);
			Utility.localize(labelHexaValue);
			Utility.localize(labelRgbDecimal);
			Utility.localize(menuReset);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set icons.
	 */
	private void setIcons() {
		try {
			
			menuCopyColor.setIcon(Images.getIcon("org/multipage/gui/images/copy_icon.png"));
			menuPasteColor.setIcon(Images.getIcon("org/multipage/gui/images/paste_icon.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set listeners.
	 */
	private void setListeners() {
		try {
			
			Utility.setTextChangeListener(textHexaValue, () -> {
				try {
					
					onChangeHexaInputValue();
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			Utility.setTextChangeListener(textRGBValue, () -> {
				try {
					
					onChangeRgbInputValue();
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get value.
	 */
	@Override
	public Object getValue() {

		return color;
	}

	/**
	 * Set value.
	 */
	@Override
	public void setValue(Object value) {
		try {
			
			// Set controls.
			if (value instanceof ColorObj) {
				setShowColor((ColorObj) value);
			}
			else {
				setShowColor(Color.BLACK);
			}
			
			setHexaTextValue(this.color);
			setRgbTextValue(this.color);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set value.
	 */
	public void setValueInner(Object value) {
		try {
			
			if (value instanceof ColorObj) {
				setShowColor((ColorObj) value);
				return;
			}
			
			setShowColor(Color.BLACK);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set hexa text value.
	 * @param color
	 */
	private void setHexaTextValue(ColorObj color) {
		try {
			
			disableChangeEvents = true;
			textHexaValue.setText(this.color.getText());
			disableChangeEvents = false;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set RGB text value.
	 * @param color
	 */
	private void setRgbTextValue(ColorObj color) {
		try {
			
			disableChangeEvents = true;
			textRGBValue.setText(this.color.getTextRgbDecimal());
			disableChangeEvents = false;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On select color.
	 * @param e 
	 */
	protected void onSelectColor(MouseEvent e) {
		try {
			
			if (e.getButton() != MouseEvent.BUTTON1) {
				return;
			}
			
			if (!labelColor.isEnabled()) {
				return;
			}
			
			// Select color.
			Color newColor = Utility.chooseColor(this, labelColor.getBackground());
			if (newColor == null) {
				return;
			}
			
			setShowColor(newColor);
			
			setHexaTextValue(this.color);
			setRgbTextValue(this.color);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Show color.
	 * @param color
	 */
	private void setShowColor(Color color) {
		try {
			
			// Set color.
			this.color = new ColorObj(color);
			
			labelColor.setBackground(color);
			textColorDescription.setText(String.format("<html><center><b>%s</b>&nbsp;&nbsp;&nbsp;<i>RGB(%d, %d, %d)&nbsp;&nbsp;&nbsp;RGBA(%d, %d, %d, %d)</i></center></html>",
					new ColorObj(color).getText(), color.getRed(), color.getGreen(), color.getBlue(),
						color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Add popup trayMenu.
	 * @param component
	 * @param popup
	 */
	private void addPopup(Component component, final JPopupMenu popup) {
		try {
			
			component.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					try {
						
						if (e.isPopupTrigger()) {
							showMenu(e);
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				public void mouseReleased(MouseEvent e) {
					try {
						
						if (e.isPopupTrigger()) {
							showMenu(e);
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				private void showMenu(MouseEvent e) {
					try {
						
						if (!labelColor.isEnabled()) {
							return;
						}
						// Get clipboard color and enable / disable paste trayMenu item.
						ColorObj color = getClipboardColor();
						menuPasteColor.setEnabled(color != null);
		
						// Show trayMenu popup.
						popup.show(e.getComponent(), e.getX(), e.getY());
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get clipboard color.
	 * @return
	 */
	protected static ColorObj getClipboardColor() {
		
		try {
			String clipboardText = Utility.getClipboardString();
			return ColorObj.convertString(clipboardText);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * On copy color.
	 */
	protected void onCopyColor() {
		try {
			
			String colorText = color.getText();
			Utility.putClipboardString(colorText);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Paste color.
	 */
	protected void onPasteColor() {
		try {
			
			String clipboardText = Utility.getClipboardString();
			
			// Get clipboard color.
			ColorObj color  = ColorObj.convertString(clipboardText);
			if (color == null) {
				Utility.show(this, "org.multipage.generator.messageClipboardColorError");
				return;
			}
			
			// Set color.
			setValueInner(color);
			setHexaTextValue(color);
			setRgbTextValue(color);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On change hexa value.
	 */
	protected void onChangeHexaInputValue() {
		try {
			
			if (disableChangeEvents) {
				return;
			}
			
			// Get hexa value and convert it to color.
			String hexaText = textHexaValue.getText();
			
			ColorObj color = ColorObj.convertStringHexa(hexaText);
			if (color != null) {
				
				setValueInner(color);
				setRgbTextValue(color);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On change decimal value.
	 */
	protected void onChangeRgbInputValue() {
		try {
			
			if (disableChangeEvents) {
				return;
			}
			
			// Get rgb value and convert it to color.
			String rgbText = textRGBValue.getText();
			
			ColorObj color = ColorObj.convertStringRgb(rgbText);
			if (color != null) {
				
				setValueInner(color);
				setHexaTextValue(color);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On reset.
	 */
	protected void onReset() {
		try {
			
			setValue(new ColorObj(Color.WHITE));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set default value state.
	 */
	@Override
	public void setDefault(boolean isDefault) {
		try {
			
			// Set background color.
			labelColor.setBackground(isDefault ? new Color(240, 240, 240) : color);
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
		
		try {
			return StringValueEditor.meansColor;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
}
