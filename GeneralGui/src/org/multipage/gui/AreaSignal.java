/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2023-12-07
 *
 */
package org.multipage.gui;

/**
 * Area signals definitions.
 * @author vakol
 */
public class AreaSignal extends Signal {

	/**
	 * List of area signals.
	 */
	
	/**
	 * Static constructor.
	 */
	static {
		// Unnecessary signals in static constructor.
		//addUnnecessary();
		
		// Describe signals.
		reflectSignals(AreaSignal.class);
	}
	
	/**
	 * Check if an input object equals this signal.
	 */
	@Override
	public boolean equals(Object obj) {
		
		if (obj instanceof AreaSignal) {
			
			Signal signal = (AreaSignal) obj;
			boolean isSame = this.name.equals(signal.name);
			
			return isSame;
		}
		return false;
	}
}
