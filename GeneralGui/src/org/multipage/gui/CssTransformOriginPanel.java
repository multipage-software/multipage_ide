/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Panel that displays CSS transformation origin editor.
 * @author vakol
 *
 */
public class CssTransformOriginPanel extends InsertPanel implements StringValueEditor {
	
	/**
	 * Components.
	 */
	private JLabel labelAxisX;
	private JLabel labelAxisY;
	private JComboBox comboAxisX;
	private JComboBox comboAxisY;
	private JTextField textAxisX;
	private JTextField textAxisY;
	private JComboBox comboAxisXUnits;
	private JComboBox comboAxisYUnits;
	private TextFieldEx textAxisZ;
	private JLabel labelAxisZ;
	private JComboBox comboAxisZUnits;

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
	public CssTransformOriginPanel(String initialString) {
		
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
				
		labelAxisX = new JLabel("org.multipage.gui.textTransformOriginX");
		springLayout.putConstraint(SpringLayout.NORTH, labelAxisX, 50, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, labelAxisX, 35, SpringLayout.WEST, this);
		add(labelAxisX);
		
		labelAxisY = new JLabel("org.multipage.gui.textTransformOriginY");
		springLayout.putConstraint(SpringLayout.NORTH, labelAxisY, 25, SpringLayout.SOUTH, labelAxisX);
		springLayout.putConstraint(SpringLayout.EAST, labelAxisY, 0, SpringLayout.EAST, labelAxisX);
		add(labelAxisY);
		
		comboAxisX = new JComboBox();
		comboAxisX.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onPositionXComboChange();
			}
		});
		comboAxisX.setPreferredSize(new Dimension(100, 20));
		springLayout.putConstraint(SpringLayout.NORTH, comboAxisX, 0, SpringLayout.NORTH, labelAxisX);
		springLayout.putConstraint(SpringLayout.WEST, comboAxisX, 6, SpringLayout.EAST, labelAxisX);
		add(comboAxisX);
		
		comboAxisY = new JComboBox();
		comboAxisY.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onPositionYComboChange();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, comboAxisY, 0, SpringLayout.NORTH, labelAxisY);
		springLayout.putConstraint(SpringLayout.EAST, comboAxisY, 0, SpringLayout.EAST, comboAxisX);
		comboAxisY.setPreferredSize(new Dimension(100, 20));
		add(comboAxisY);
		
		textAxisX = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textAxisX, 0, SpringLayout.NORTH, labelAxisX);
		springLayout.putConstraint(SpringLayout.WEST, textAxisX, 23, SpringLayout.EAST, comboAxisX);
		add(textAxisX);
		textAxisX.setColumns(10);
		
		textAxisY = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textAxisY, 0, SpringLayout.NORTH, labelAxisY);
		springLayout.putConstraint(SpringLayout.WEST, textAxisY, 0, SpringLayout.WEST, textAxisX);
		textAxisY.setColumns(10);
		add(textAxisY);
		
		comboAxisXUnits = new JComboBox();
		springLayout.putConstraint(SpringLayout.NORTH, comboAxisXUnits, 0, SpringLayout.NORTH, labelAxisX);
		springLayout.putConstraint(SpringLayout.WEST, comboAxisXUnits, 0, SpringLayout.EAST, textAxisX);
		comboAxisXUnits.setPreferredSize(new Dimension(50, 20));
		add(comboAxisXUnits);
		
		comboAxisYUnits = new JComboBox();
		springLayout.putConstraint(SpringLayout.NORTH, comboAxisYUnits, 0, SpringLayout.NORTH, labelAxisY);
		springLayout.putConstraint(SpringLayout.WEST, comboAxisYUnits, 0, SpringLayout.EAST, textAxisY);
		comboAxisYUnits.setPreferredSize(new Dimension(50, 20));
		add(comboAxisYUnits);
		
		textAxisZ = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textAxisZ, 19, SpringLayout.SOUTH, textAxisY);
		springLayout.putConstraint(SpringLayout.EAST, textAxisZ, 0, SpringLayout.EAST, textAxisX);
		textAxisZ.setColumns(10);
		add(textAxisZ);
		
		labelAxisZ = new JLabel("org.multipage.gui.textTransformOriginZ");
		springLayout.putConstraint(SpringLayout.NORTH, labelAxisZ, 3, SpringLayout.NORTH, textAxisZ);
		springLayout.putConstraint(SpringLayout.EAST, labelAxisZ, 0, SpringLayout.EAST, labelAxisX);
		add(labelAxisZ);
		
		comboAxisZUnits = new JComboBox();
		springLayout.putConstraint(SpringLayout.NORTH, comboAxisZUnits, 0, SpringLayout.NORTH, textAxisZ);
		springLayout.putConstraint(SpringLayout.WEST, comboAxisZUnits, 0, SpringLayout.WEST, comboAxisXUnits);
		comboAxisZUnits.setPreferredSize(new Dimension(50, 20));
		add(comboAxisZUnits);
	}

	/**
	 * On position X combo change.
	 */
	protected void onPositionXComboChange() {
		try {
			
			if (comboAxisX.getSelectedIndex() == 0) {
				return;
			}
			
			if (settingControls) {
				return;
			}
			startSettingControls();
			
			textAxisX.setText("");
			comboAxisXUnits.setSelectedIndex(0);
			
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
			
			if (comboAxisY.getSelectedIndex() == 0) {
				return;
			}
			
			if (settingControls) {
				return;
			}
			startSettingControls();
			
			textAxisY.setText("");
			comboAxisYUnits.setSelectedIndex(0);
			
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
			
			Utility.setTextChangeListener(textAxisX, () -> {
				try {
					
					if (settingControls) {
						return;
					}
					
					startSettingControls();
					comboAxisX.setSelectedIndex(0);
					stopSettingControls();
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			Utility.setTextChangeListener(textAxisY, () -> {
				try {
					
					if (settingControls) {
						return;
					}

					startSettingControls();
					comboAxisY.setSelectedIndex(0);
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
			
			Utility.loadEmptyItem(comboAxisX);
			Utility.loadNamedItems(comboAxisX, new String [][] {
					{"left", "org.multipage.gui.textPerspectiveOriginLeft"},
					{"center", "org.multipage.gui.textPerspectiveOriginCenter"},
					{"right", "org.multipage.gui.textPerspectiveOriginRight"}
			});
			Utility.selectComboNamedItem(comboAxisX, "center");
			
			Utility.loadEmptyItem(comboAxisY);
			Utility.loadNamedItems(comboAxisY, new String [][] {
					{"top", "org.multipage.gui.textPerspectiveOriginTop"},
					{"center", "org.multipage.gui.textPerspectiveOriginCenter"},
					{"bottom", "org.multipage.gui.textPerspectiveOriginBottom"}
			});
			Utility.selectComboNamedItem(comboAxisY, "center");
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
			
			Utility.loadCssUnits(comboAxisXUnits);
			Utility.loadCssUnits(comboAxisYUnits);
			Utility.loadCssUnits(comboAxisZUnits);
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
			return getAxisX() + " " + getAxisY() + " " + getAxisZ();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get axis X.
	 * @return
	 */
	private String getAxisX() {
		
		try {
			return Utility.getCssValueAndUnits(textAxisX, comboAxisXUnits, comboAxisX, "center");
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get axis Y.
	 * @return
	 */
	private String getAxisY() {
		
		try {
			return Utility.getCssValueAndUnits(textAxisY, comboAxisYUnits, comboAxisY, "center");
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get axis Z.
	 * @return
	 */
	private String getAxisZ() {
		
		try {
			return Utility.getCssValueAndUnits(textAxisZ, comboAxisZUnits, "0");
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
			Utility.selectComboNamedItem(comboAxisX, "center");
			textAxisX.setText("");
			comboAxisXUnits.setSelectedIndex(0);
			
			Utility.selectComboNamedItem(comboAxisY, "center");
			textAxisY.setText("");
			comboAxisYUnits.setSelectedIndex(0);
			
			textAxisZ.setText("0");
			comboAxisZUnits.setSelectedItem("");
	
			if (initialString != null) {
				
				Obj<Integer> position = new Obj<Integer>(0);
				
				try {
					
					// Set X position.
					int positionAux = position.ref;
					String text = Utility.getNextMatch(initialString, position, "\\G\\s*(left|center|right)\\s+");
					if (text != null) {
						Utility.selectComboNamedItem(comboAxisX, text.trim());
					}
					else {
						position.ref = positionAux;
						text = Utility.getNextMatch(initialString, position, "\\G\\s*\\S+\\s+");
						if (text == null) {
							return;
						}
						
						Utility.setCssValueAndUnits(text.trim(), textAxisX, comboAxisXUnits, "0", "px");
						comboAxisX.setSelectedIndex(0);
					}
					
					// Set Y position.
					positionAux = position.ref;
					text = Utility.getNextMatch(initialString, position, "\\G\\s*(top|center|bottom)\\s*");
					if (text != null) {
						Utility.selectComboNamedItem(comboAxisY, text.trim());
					}
					else {
						position.ref = positionAux;
						text = Utility.getNextMatch(initialString, position, "\\G\\s*\\S+\\s*");
						if (text == null) {
							return;
						}
						
						Utility.setCssValueAndUnits(text.trim(), textAxisY, comboAxisYUnits, "0", "px");
						comboAxisY.setSelectedIndex(0);
					}
					
					// Set Z position.
					text = Utility.getNextMatch(initialString, position, "\\G\\s*\\S+\\s*");
					if (text == null) {
						return;
					}
					Utility.setCssValueAndUnits(text.trim(), textAxisZ, comboAxisZUnits, "0", "");
	
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
			
			Utility.localize(labelAxisX);
			Utility.localize(labelAxisY);
			Utility.localize(labelAxisZ);
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
			return Resources.getString("org.multipage.gui.textCssTransformOriginBuilder");
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
		
		CssTransformOriginPanel.bounds = bounds;
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
		
		return meansCssTransformOrigin;
	}

	/**
	 * Set controls grayed. If a false value is returned a high level editor grayed this panel.
	 */
	@Override
	public boolean setControlsGrayed(boolean isDefault) {
		
		return false;
	}
}
