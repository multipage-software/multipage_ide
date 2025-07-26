/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import javax.swing.*;

import org.multipage.util.Safe;

/**
 * Extended text panel.
 * @author vakol
 *
 */
public class TextPaneEx extends JTextPane {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public TextPaneEx() {
		try {
			
			new TextPopupMenu(this);
			setDragEnabled(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
