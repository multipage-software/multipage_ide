/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import javax.swing.JTextField;

import org.multipage.util.Safe;

/**
 * Extended text field with popup menu.
 * @author vakol
 *
 */
public class TextFieldEx extends JTextField {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Popup menu.
	 */
	private TextPopupMenu popupMenu;

	/**
	 * Constructor.
	 */
	public TextFieldEx() {
		try {
			
			popupMenu = new TextPopupMenu(this);
			setDragEnabled(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
		
	/**
	 * Get popup menu.
	 * @return
	 */
	public TextPopupMenu getMenu() {
		
		return popupMenu;
	}
}
