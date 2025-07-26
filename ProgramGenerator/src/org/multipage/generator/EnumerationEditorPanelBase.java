/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.maclan.EnumerationValue;
import org.maclan.Slot;
import org.multipage.gui.StringValueEditor;
import org.multipage.util.Safe;

/**
 * Base class for enumeration editor.
 * @author vakol
 *
 */
public class EnumerationEditorPanelBase extends JPanel implements SlotValueEditorPanelInterface {
	
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Slot reference.
	 */
	protected Slot slot;
	
	/**
	 * Components' references.
	 */
	private JComboBox<EnumerationValue> comboEnumerationValue;
	
	/**
	 * Set components' references.
	 * @param comboEnumerationValue
	 */
	protected void setComponentsReferences(JComboBox<EnumerationValue> comboEnumerationValue) {
		
		this.comboEnumerationValue = comboEnumerationValue;
	}
	
	/**
	 * Get value.
	 */
	@Override
	public Object getValue() {
		
		try {
			// Return selected enumeration value.
			return comboEnumerationValue.getSelectedItem();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Set value.
	 */
	@Override
	public void setValue(Object value) {
		try {
			
			if (value instanceof EnumerationValue) {
				EnumerationValue enumerationValue = (EnumerationValue) value;
				
				// Select enumeration type and value.
				selectEnumerationValue(enumerationValue.getId());
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Select enumeration value.
	 * @param valueId
	 */
	protected void selectEnumerationValue(long valueId) {
		try {
			
			for (int index = 0; index < comboEnumerationValue.getItemCount();
					index++) {
				
				EnumerationValue enumerationValue = comboEnumerationValue.getItemAt(index);
				if (enumerationValue.getId() == valueId) {
					
					// Select combo box item.
					comboEnumerationValue.setSelectedIndex(index);
					break;
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set slot reference.
	 * @param slot
	 */
	public void setSlot(Slot slot) {

		// Override this method.
	}
	

	/**
	 * On reset.
	 */
	protected void onReset() {
		try {
			
			comboEnumerationValue.setSelectedIndex(-1);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set enumeration value.
	 * @param ref
	 */
	public void setEnumerationValue(EnumerationValue enumerationValue) {
		try {
			
			setValue(enumerationValue);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get enumeration value.
	 * @return
	 */
	public EnumerationValue getEnumerationValue() {
		
		try {
			// Return selected enumeration value.
			return (EnumerationValue) getValue();
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

		// Nothing to do.
	}

	/**
	 * Get value meaning.
	 */
	@Override
	public String getValueMeaning() {
		
		try {
			return StringValueEditor.meansEnumeration;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
}
