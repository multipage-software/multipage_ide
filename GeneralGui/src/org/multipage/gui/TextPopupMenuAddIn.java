/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-27
 *
 */

package org.multipage.gui;

import javax.swing.*;

/**
 * Interface for popup menu add-ins.
 * @author vakol
 *
 */
public interface TextPopupMenuAddIn {

	/**
	 * Add menu.
	 * @param popupMenu
	 * @param plainTextPane
	 */
	void addMenu(JPopupMenu popupMenu, JEditorPane plainTextPane);
	
	/**
	 * Update information.
	 */
	void updateInformation();
}
