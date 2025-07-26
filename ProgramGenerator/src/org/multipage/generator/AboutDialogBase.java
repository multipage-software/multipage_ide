/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Window;

import javax.swing.JDialog;

import org.multipage.util.Safe;

/**
 * Base class for dialog that displays information about application.
 * @author vakol
 *
 */
public class AboutDialogBase extends JDialog {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * @param owner
	 * @param applicationModal
	 */
	public AboutDialogBase(Window owner, ModalityType applicationModal) {
		super(owner, applicationModal);
	}

	/**
	 * Delegate call.
	 */
	public void setVisible(boolean visible) {
		try {
			
			super.setVisible(visible);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
