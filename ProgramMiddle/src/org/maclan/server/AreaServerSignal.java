/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2022-10-31
 *
 */
package org.maclan.server;

import org.multipage.gui.Signal;

/**
 * Signal definitions for Area Server.
 * @author vakol
 *
 */
public class AreaServerSignal extends Signal {
	
	/**
	 * Debug statement.
	 */
	public static final AreaServerSignal debugStatement = new AreaServerSignal();

	/**
	 * Static constructor.
	 */
	static {
		// Unnecessary signals in static constructor.
		addUnnecessary(/* Add them as parameters. */);
		
		// Describe signals.
		reflectSignals(AreaServerSignal.class);
	}
	
	/**
	 * Check if an input object equals this signal.
	 */
	@Override
	public boolean equals(Object obj) {
		
		if (obj instanceof AreaServerSignal) {
			
			Signal signal = (AreaServerSignal) obj;
			boolean isSame = this.name.equals(signal.name);
			
			return isSame;
		}
		return false;
	}
}
