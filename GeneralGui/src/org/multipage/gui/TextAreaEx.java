/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import javax.swing.JTextArea;

import org.multipage.util.Safe;


/**
 * Extended text area.
 * @author vakol
 *
 */
public class TextAreaEx extends JTextArea {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public TextAreaEx() {
		try {
			
			new TextPopupMenu(this);
			setDragEnabled(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
