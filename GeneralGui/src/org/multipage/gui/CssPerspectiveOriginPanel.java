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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Panel that displays perspective origin editor.
 * @author vakol
 *
 */
public class CssPerspectiveOriginPanel extends InsertPanel implements StringValueEditor {
	
	/**
	 * Components.
	 */
	private JLabel labelPositionX;
	private JLabel labelPositionY;
	private JComboBox comboPositionX;
	private JComboBox comboPositionY;
	private JTextField textPositionX;
	private JTextField textPositionY;
	private JComboBox comboPositionXUnits;
	private JComboBox comboPositionYUnits;

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
	
	/**
	 * Setting controls flag.
	 */
	private boolean settingControls = false;

	/**
	 * Start setting controls.
	 */
	public void startSettingControls() {
		
		settingControls = true;
	}

	/**
	 * Stop setting controls.
	 */
	public void stopSettingControls() {
		
		Safe.invokeLater(() -> {

			settingControls = false;
		});
	}

	// $hide<<$

	/**
	 * Create the dialog.
	 * @param parentWindow 
	 */
	public CssPerspectiveOriginPanel(String initialString) {
		
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
				
		labelPositionX = new JLabel("org.multipage.gui.textPerspectiveOriginPositionX");
		springLayout.putConstraint(SpringLayout.NORTH, labelPositionX, 50, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, labelPositionX, 35, SpringLayout.WEST, this);
		add(labelPositionX);
		
		labelPositionY = new JLabel("org.multipage.gui.textPerspectiveOriginPositionY");
		springLayout.putConstraint(SpringLayout.NORTH, labelPositionY, 25, SpringLayout.SOUTH, labelPositionX);
		springLayout.putConstraint(SpringLayout.EAST, labelPositionY, 0, SpringLayout.EAST, labelPositionX);
		add(labelPositionY);
		
		comboPositionX = new JComboBox();
		comboPositionX.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onPositionXComboChange();
			}
		});
		comboPositionX.setPreferredSize(new Dimension(100, 20));
		springLayout.putConstraint(SpringLayout.NORTH, comboPositionX, 0, SpringLayout.NORTH, labelPositionX);
		springLayout.putConstraint(SpringLayout.WEST, comboPositionX, 6, SpringLayout.EAST, labelPositionX);
		add(comboPositionX);
		
		comboPositionY = new JComboBox();
		comboPositionY.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onPositionYComboChange();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, comboPositionY, 0, SpringLayout.NORTH, labelPositionY);
		springLayout.putConstraint(SpringLayout.EAST, comboPositionY, 0, SpringLayout.EAST, comboPositionX);
		comboPositionY.setPreferredSize(new Dimension(100, 20));
		add(comboPositionY);
		
		textPositionX = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textPositionX, 0, SpringLayout.NORTH, labelPositionX);
		springLayout.putConstraint(SpringLayout.WEST, textPositionX, 23, SpringLayout.EAST, comboPositionX);
		add(textPositionX);
		textPositionX.setColumns(10);
		
		textPositionY = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textPositionY, 0, SpringLayout.NORTH, labelPositionY);
		springLayout.putConstraint(SpringLayout.WEST, textPositionY, 0, SpringLayout.WEST, textPositionX);
		textPositionY.setColumns(10);
		add(textPositionY);
		
		comboPositionXUnits = new JComboBox();
		springLayout.putConstraint(SpringLayout.NORTH, comboPositionXUnits, 0, SpringLayout.NORTH, labelPositionX);
		springLayout.putConstraint(SpringLayout.WEST, comboPositionXUnits, 0, SpringLayout.EAST, textPositionX);
		comboPositionXUnits.setPreferredSize(new Dimension(50, 20));
		add(comboPositionXUnits);
		
		comboPositionYUnits = new JComboBox();
		springLayout.putConstraint(SpringLayout.NORTH, comboPositionYUnits, 0, SpringLayout.NORTH, labelPositionY);
		springLayout.putConstraint(SpringLayout.WEST, comboPositionYUnits, 0, SpringLayout.EAST, textPositionY);
		comboPositionYUnits.setPreferredSize(new Dimension(50, 20));
		add(comboPositionYUnits);
	}

	/**
	 * On position X combo change.
	 */
	protected void onPositionXComboChange() {
		try {
			
			if (comboPositionX.getSelectedIndex() == 0) {
				return;
			}
			
			if (settingControls) {
				return;
			}
			startSettingControls();
			
			textPositionX.setText("");
			comboPositionXUnits.setSelectedIndex(0);
			
			stopSettingControls();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On position Y combo change.
	 */
	protected void onPositionYComboChange() {
		try {
			
			if (comboPositionY.getSelectedIndex() == 0) {
				return;
			}
			
			if (settingControls) {
				return;
			}
			startSettingControls();
			
			textPositionY.setText("");
			comboPositionYUnits.setSelectedIndex(0);
			
			stopSettingControls();
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
			loadComboBoxes();
			loadDialog();
			
			setListeners();
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
			
			Utility.setTextChangeListener(textPositionX, () -> {
				try {
					
					if (settingControls) {
						return;
					}

					startSettingControls();
					comboPositionX.setSelectedIndex(0);
					stopSettingControls();
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			Utility.setTextChangeListener(textPositionY, () -> {
				try {
					
					if (settingControls) {
						return;
					}
					
					startSettingControls();
					comboPositionY.setSelectedIndex(0);
					stopSettingControls();
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
	 * Load combo boxes.
	 */
	private void loadComboBoxes() {
		try {
			
			Utility.loadEmptyItem(comboPositionX);
			Utility.loadNamedItems(comboPositionX, new String [][] {
					{"left", "org.multipage.gui.textPerspectiveOriginLeft"},
					{"center", "org.multipage.gui.textPerspectiveOriginCenter"},
					{"right", "org.multipage.gui.textPerspectiveOriginRight"}
			});
			Utility.selectComboNamedItem(comboPositionX, "center");
			
			Utility.loadEmptyItem(comboPositionY);
			Utility.loadNamedItems(comboPositionY, new String [][] {
					{"top", "org.multipage.gui.textPerspectiveOriginTop"},
					{"center", "org.multipage.gui.textPerspectiveOriginCenter"},
					{"bottom", "org.multipage.gui.textPerspectiveOriginBottom"}
			});
			Utility.selectComboNamedItem(comboPositionY, "center");
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
			
			Utility.loadCssUnits(comboPositionXUnits);
			Utility.loadCssUnits(comboPositionYUnits);
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
			return getPositionX() + " " + getPositionY();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get position X.
	 * @return
	 */
	private String getPositionX() {
		
		try {
			return Utility.getCssValueAndUnits(textPositionX, comboPositionXUnits, comboPositionX, "center");
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get position Y.
	 * @return
	 */
	private String getPositionY() {
		
		try {
			return Utility.getCssValueAndUnits(textPositionY, comboPositionYUnits, comboPositionY, "center");
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
			Utility.selectComboNamedItem(comboPositionX, "center");
			textPositionX.setText("");
			comboPositionXUnits.setSelectedIndex(0);
			
			Utility.selectComboNamedItem(comboPositionY, "center");
			textPositionY.setText("");
			comboPositionYUnits.setSelectedIndex(0);
	
			if (initialString != null) {
				
				Obj<Integer> position = new Obj<Integer>(0);
				
				try {
					// Set X position.
					int positionAux = position.ref;
					String text = Utility.getNextMatch(initialString, position, "\\G\\s*(left|center|right)\\s+");
					if (text != null) {
						Utility.selectComboNamedItem(comboPositionX, text.trim());
					}
					else {
						position.ref = positionAux;
						text = Utility.getNextMatch(initialString, position, "\\G\\s*\\S+\\s+");
						if (text == null) {
							return;
						}
						
						Utility.setCssValueAndUnits(text.trim(), textPositionX, comboPositionXUnits, "0", "px");
						comboPositionX.setSelectedIndex(0);
					}
					
					// Set Y position.
					positionAux = position.ref;
					text = Utility.getNextMatch(initialString, position, "\\G\\s*(top|center|bottom)\\s*");
					if (text != null) {
						Utility.selectComboNamedItem(comboPositionY, text.trim());
					}
					else {
						position.ref = positionAux;
						text = Utility.getNextMatch(initialString, position, "\\G\\s*\\S+\\s*");
						if (text == null) {
							return;
						}
						
						Utility.setCssValueAndUnits(text.trim(), textPositionY, comboPositionYUnits, "0", "px");
						comboPositionY.setSelectedIndex(0);
					}
				}
				catch (Exception e) {
				}
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
			
			Utility.localize(labelPositionX);
			Utility.localize(labelPositionY);
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
			return Resources.getString("org.multipage.gui.textCssPerspectiveOriginBuilder");
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
		
		CssPerspectiveOriginPanel.bounds = bounds;
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
		
		return meansCssPerspectiveOrigin;
	}

	/**
	 * Set controls grayed. If a false value is returned a high level editor grayed this panel.
	 */
	@Override
	public boolean setControlsGrayed(boolean isDefault) {
		
		return false;
	}
}
