/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Color;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.multipage.gui.StringValueEditor;
import org.multipage.gui.Utility;
import org.multipage.util.Safe;

/**
 * Base class for editor for boolean value.
 * @author vakol
 *
 */
public class BooleanEditorPanelBase extends JPanel implements SlotValueEditorPanelInterface {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Components' references.
	 */
	private JRadioButton radioTrue;
	private JRadioButton radioFalse;
	private ButtonGroup buttonGroup;
	private JLabel labelSelectValue;

	/**
	 * Set components' referneces.
	 * @param radioTrue
	 * @param radioFalse
	 * @param buttonGroup
	 * @param labelSelectValue
	 */
	protected void setComponentsReferences(JRadioButton radioTrue,
			JRadioButton radioFalse,
			ButtonGroup buttonGroup,
			JLabel labelSelectValue) {
		
		this.radioTrue = radioTrue;
		this.radioFalse = radioFalse;
		this.buttonGroup = buttonGroup;
		this.labelSelectValue = labelSelectValue;
	}
	
	/**
	 * Post creation.
	 */
	protected void postCreate() {
		try {
			
			localize();
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
			
			Utility.localize(radioTrue);
			Utility.localize(radioFalse);
			
			if (labelSelectValue != null) {
				Utility.localize(labelSelectValue);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set value.
	 * @param booleanValue
	 */
	@Override
	public void setValue(Object value) {
		try {
			
			if (!(value instanceof Boolean)) {
				return;
			}
			
			boolean booleanValue = (Boolean) value;
			
			ButtonModel buttonModel = null;
			
			if (booleanValue) {
				buttonModel = radioTrue.getModel();
			}
			else {
				buttonModel = radioFalse.getModel();
			}
			
			buttonGroup.setSelected(buttonModel, true);
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
		
		try {
			return radioTrue.isSelected();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Set default value state.
	 */
	@Override
	public void setDefault(boolean isDefault) {
		try {
			
			// Enable / disable controls.
			boolean enable = !isDefault;
			
			Color disabledColor = Color.GRAY;
			
			radioTrue.setForeground(enable ? new Color(0, 100, 0) : disabledColor);
			radioFalse.setForeground(enable ? new Color(255, 0, 0) : disabledColor);
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
			return StringValueEditor.meansBoolean;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
}
