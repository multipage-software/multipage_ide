/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.Component;

/**
 * Invokes component update.
 * @author vakol
 *
 */
public interface ComponentUpdate {

	// Component update method.
	boolean run(Component component);
}
