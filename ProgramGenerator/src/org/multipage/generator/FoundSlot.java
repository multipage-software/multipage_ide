/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.util.LinkedList;

import org.maclan.Slot;
import org.multipage.gui.FoundAttr;
import org.multipage.util.Safe;


/**
 * Class that represents found slots.
 * @author vakol
 *
 */
public class FoundSlot {
	
	/**
	 * Slot reference.
	 */
	private Slot slot;
	
	/**
	 * Found attr.
	 */
	private FoundAttr foundAttr;

	/**
	 * Constructor.
	 * @param slot
	 * @param searchText
	 * @param isCaseSensitive
	 * @param isWholeWordsButton
	 * @param isExactMatch
	 */
	public FoundSlot(Slot slot, String searchText, boolean isCaseSensitive,
			boolean isWholeWordsButton) {
		try {
			
			this.slot = slot;
			this.foundAttr = new FoundAttr(searchText, isCaseSensitive, isWholeWordsButton);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Constructor.
	 * @param slot
	 * @param foundAttr
	 */
	public FoundSlot(Slot slot, FoundAttr foundAttr) {
		
		this.slot = slot;
		this.foundAttr = foundAttr;
	}

	/**
	 * Returns true value if the slot is found.
	 * @param slot2
	 * @return
	 */
	public static boolean isSlotFound(LinkedList<FoundSlot> foundSlots, Slot slot) {
		
		try {
			// Do loop for all slots.
			for (FoundSlot foundSlot : foundSlots) {
				if (foundSlot.slot.equals(slot)) {
					return true;
				}
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Returns found attributes.
	 * @param foundSlots
	 * @param slot
	 * @return
	 */
	public static FoundAttr getFoundAtt(LinkedList<FoundSlot> foundSlots,
			Slot slot) {
		
		try {
			if (foundSlots == null) {
				return null;
			}
			
			// Do loop for all slots.
			for (FoundSlot foundSlot : foundSlots) {
				if (foundSlot.slot.equals(slot)) {
					return foundSlot.foundAttr;
				}
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		try {
			return "FoundSlot [slot=" + slot + ", foundAttr=" + foundAttr + "]";
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
}
