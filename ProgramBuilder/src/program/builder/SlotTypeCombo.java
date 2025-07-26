/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package program.builder;

import javax.swing.JComboBox;

import org.maclan.SlotType;
import org.multipage.generator.GeneratorUtility;
import org.multipage.util.Safe;

/**
 * Combo box that displays slot types.
 * @author vakol
 *
 */
public class SlotTypeCombo extends JComboBox {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor.
	 */
	public SlotTypeCombo() {
		try {
			
			load();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load combo box.
	 */
	private void load() {
		try {
			
			GeneratorUtility.loadSlotTypesCombo(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get selected type.
	 */
	public SlotType getSelected() {
		
		try {
			Object selected = getSelectedItem();
			if (selected instanceof SlotType) {
				
				SlotType type = (SlotType) selected;
				return type;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return SlotType.UNKNOWN;
	}
}
