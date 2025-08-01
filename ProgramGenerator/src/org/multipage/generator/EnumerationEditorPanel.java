/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SpringLayout;

import org.maclan.EnumerationObj;
import org.maclan.EnumerationValue;
import org.maclan.Slot;
import org.multipage.gui.RendererJLabel;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.Utility;
import org.multipage.util.Safe;

/**
 * Panel that displays editor for enumerated value.
 * @author vakol
 *
 */
public class EnumerationEditorPanel extends EnumerationEditorPanelBase {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Saved values.
	 */
	private static boolean displayEnumerationValuesState = false;
	
	/**
	 * Set default states.
	 */
	public static void setDefaultData() {
		
		displayEnumerationValuesState = false;
	}

	/**
	 * Save serialized states.
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream)
			throws IOException {
		
		outputStream.writeBoolean(displayEnumerationValuesState);
	}
	
	/**
	 * Load serialized states.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void serializeData(StateInputStream inputStream)
			throws IOException, ClassNotFoundException {
		
		displayEnumerationValuesState = inputStream.readBoolean();
	}
	
	// $hide<<$
	/**
	 * Components.
	 */
	private JComboBox<EnumerationValue> comboEnumerationValue;
	private JLabel labelSelectValue;
	private JCheckBox checkDisplayValues;

	/**
	 * Create the dialog.
	 */
	public EnumerationEditorPanel() {
		
		try {
			initComponents();
			
			// $hide>>$
			postCreate();
			setComponentsReferences(comboEnumerationValue);
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
		setBounds(100, 100, 574, 106);
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		comboEnumerationValue = new JComboBox();
		springLayout.putConstraint(SpringLayout.NORTH, comboEnumerationValue, 10, SpringLayout.NORTH, this);
		comboEnumerationValue.setFont(new Font("Tahoma", Font.BOLD, 11));
		comboEnumerationValue.setPreferredSize(new Dimension(200, 27));
		add(comboEnumerationValue);
		
		labelSelectValue = new JLabel("org.multipage.generator.textSelectSlotEnumerationValue");
		springLayout.putConstraint(SpringLayout.WEST, labelSelectValue, 60, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, labelSelectValue, 0, SpringLayout.SOUTH, comboEnumerationValue);
		springLayout.putConstraint(SpringLayout.WEST, comboEnumerationValue, 10, SpringLayout.EAST, labelSelectValue);
		springLayout.putConstraint(SpringLayout.NORTH, labelSelectValue, 10, SpringLayout.NORTH, this);
		add(labelSelectValue);
		
		checkDisplayValues = new JCheckBox("org.multipage.generator.textDisplayEnumerationValues");
		checkDisplayValues.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onDisplayEnumerationValues();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, checkDisplayValues, 6, SpringLayout.SOUTH, comboEnumerationValue);
		springLayout.putConstraint(SpringLayout.WEST, checkDisplayValues, 0, SpringLayout.WEST, comboEnumerationValue);
		add(checkDisplayValues);
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			localize();
			checkDisplayValues.setSelected(displayEnumerationValuesState);
			initializeComboBox();
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
			
			Utility.localize(labelSelectValue);
			Utility.localize(checkDisplayValues);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Initialize combo box.
	 */
	@SuppressWarnings("unchecked")
	private void initializeComboBox() {
		try {
			
			comboEnumerationValue.setRenderer(new ListCellRenderer() {
				
				// Renderer object.
				RendererJLabel renderer = new RendererJLabel();
				
				// Overridden method.
				@Override
				public Component getListCellRendererComponent(JList list,
						Object value, int index, boolean isSelected,
						boolean cellHasFocus) {
					
					try {
						if (value instanceof EnumerationValue) {
							
							EnumerationValue enumerationValue = (EnumerationValue) value;
							String textValue = checkDisplayValues.isSelected() ? 
									enumerationValue.getValueDescriptionBuilder()
									: enumerationValue.getValueDescriptionGenerator();
									
							renderer.setText(textValue);
						}
						else {
							renderer.setText(value.toString());
						}
						
						renderer.set(isSelected, cellHasFocus, index);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return renderer;
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On display enumaration values.
	 */
	protected void onDisplayEnumerationValues() {
		try {
			
			displayEnumerationValuesState = checkDisplayValues.isSelected();
			comboEnumerationValue.updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load enumeration value combo box.
	 */
	private void loadValueComboBox() {
		try {
			
			comboEnumerationValue.removeAllItems();
			
			EnumerationValue slotEnumerationValue = slot.getEnumerationValue();
			if (slotEnumerationValue == null) {
				return;
			}
			
			EnumerationObj enumeration = slotEnumerationValue.getEnumeration();
	
			// Load enumeration values.
			for (EnumerationValue enumerationValue : enumeration.getValues()) {
				comboEnumerationValue.addItem(enumerationValue);
			}
			
			// Select enumeration value.
			comboEnumerationValue.setSelectedItem(slotEnumerationValue);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set slot reference.
	 * @param slot
	 */
	@Override
	public void setSlot(Slot slot) {
		try {
			
			this.slot = slot;
			loadValueComboBox();
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
		
		// Nothing to do.
	}
}
