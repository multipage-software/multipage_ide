/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2022-10-30
 *
 */
package org.multipage.gui;

import java.util.HashSet;

/**
 * Signal definitions for GUI.
 * @author vakol
 *
 */
public class GuiSignal extends Signal {
	
	// Display area properies.
	public static final Signal displayAreaProperties = new Signal(
			params(
				HashSet.class // Set of area IDs.
			));
	// Select all areas.
	public static final Signal selectAll = new Signal();
	// Unselect all areas.
	public static final Signal unselectAll = new Signal();
	// On show/hide IDs in areas diagram..
	public static final Signal showOrHideIds = new Signal();
	// Focus on area.
	public static final Signal focusArea = new Signal(
			params(
					Long.class,
					Object.class
			));
	// Focus on the home area.
	public static final Signal focusHomeArea = new Signal();	
	// Focus on the tab area.
	public static final Signal focusTopArea = new Signal();
	// Focus on the Basic Area.
	public static final Signal focusBasicArea = new Signal();
	// Select area in the diagram. Area is determined by its ID.
	public static final Signal selectDiagramArea = new Signal(
			params(
					Long.class,	// Area ID.
					Boolean.class // Select single area (false) or add area to previous selections (true).		
			));
	// Select areas in the diagram. The set of areas' IDs is sent in related info inside the message.
	public static final Signal selectDiagramAreas = new Signal(
			params(
					HashSet.class // The list of area IDs.
			));
	// Select area with sub areas. 
	public static final Signal selectDiagramAreaWithSubareas = new Signal(
			params(
					Long.class, // Area ID.
					Boolean.class
			));
	// Reset SWT html browser.
	public static final Signal resetSwtBrowser = new Signal();
	// Monitor home page in web browser.
	public static final Signal displayHomePage = new Signal();
	// Reactivate GUI
	public static final Signal reactivateGui = new Signal();
	// Edit area.
	public static final Signal editArea = new Signal(
            params(
                    Long.class // Area ID.
            ));
	// Edit resource.
	public static final Signal editResource = new Signal(
            params(
                    Long.class // Resource ID.
            ));
	// Edit slot.
	public static final Signal editSlot = new Signal(
            params(
                    Long.class // Slot ID.
            ));
	// Edit area slots.
	public static final Signal editAreaSlots = new Signal(
            params(
                    Long.class // Area ID.
            ));
	// Dialog navigator closed.
	public static final Signal dialogNavigatorClosed = new Signal();

	/**
	 * Static constructor.
	 */
	static {
		// Unnecessary signals in static constructor.
		//addUnnecessary();
		
		// Describe signals.
		reflectSignals(GuiSignal.class);
	}
	
	/**
	 * Check if an input object equals this signal.
	 */
	@Override
	public boolean equals(Object obj) {
		
		if (obj instanceof GuiSignal) {
			
			Signal signal = (GuiSignal) obj;
			boolean isSame = this.name.equals(signal.name);
			
			return isSame;
		}
		return false;
	}
}
