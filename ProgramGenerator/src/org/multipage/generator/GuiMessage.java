/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2022-10-25
 *
 */
package org.multipage.generator;

import org.multipage.gui.Message;
import org.multipage.util.Safe;

/**
 * GUI messages.
 * @author vakol
 *
 */
public class GuiMessage extends Message {
	
	/**
	 * Returns true if the target class of this message matches the parameter.
	 * @param classObject
	 * @return
	 */
	public boolean targetClass(Class<AreaDiagramPanel> classObject) {
		
		try {
			// Initialize output.
			boolean matches = false;
			
			// Check target class.
			if (target instanceof Class<?>) {
				
				Class<?> targetClass = (Class<?>) target;
				matches = targetClass.equals(classObject);
			}
			
			return matches;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
}
