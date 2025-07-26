/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.Rectangle;

import javax.swing.JPanel;

import org.multipage.util.Safe;

/**
 * Panel that contains CSS editor.
 * @author vakol
 *
 */
public class InsertPanel extends JPanel {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Get panel title.
	 */
	public String getWindowTitle() {
		
		// Override this method.
		return "";
	}
	
	/**
	 * Get result text.
	 */
	public String getResultText() {
				
		// Override this method.
		return "";
	}

	/**
	 * Save dialog.
	 */
	public void saveDialog() {
		
		// Override this method.
	}

	/**
	 * Get container dialog bounds.
	 * @return
	 */
	public Rectangle getContainerDialogBounds() {
		
		try {
			// Override this method.
			return new Rectangle();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Set container dialog bounds.
	 * @param bounds
	 */
	public void setContainerDialogBounds(Rectangle bounds) {
		
		// Override this method.
	}

	/**
	 * Bounds set flag.
	 * @return
	 */
	public boolean isBoundsSet() {
		
		// Override this method.
		return false;
	}

	/**
	 * Set bounds set.
	 * @param set
	 */
	public void setBoundsSet(boolean set) {
		
		// Override this method.
	}
}
