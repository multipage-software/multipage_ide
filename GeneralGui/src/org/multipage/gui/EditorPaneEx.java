/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import javax.swing.JEditorPane;

import org.multipage.util.Safe;

/**
 * Extended editor panel.
 * @author vakol
 *
 */
public class EditorPaneEx extends JEditorPane {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public EditorPaneEx() {
		try {
			
			new TextPopupMenu(this);
			
			setDragEnabled(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
