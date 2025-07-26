/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */
package org.multipage.generator;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JDialog;

import org.multipage.generator.RevertExternalProvidersDialog.ListEntry;
import org.multipage.gui.Utility;
import org.multipage.util.Safe;

/**
 * Dialog that displays differences within external slots.
 * @author vakol
 *
 */
public class DifferDialog extends JDialog {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Differ panel.
	 */
	DifferPanel mergePanel;

	/**
	 * Show dialog.
	 * @param parent
	 * @param oldValue 
	 * @return
	 */
	public static String showDialog(Component parent, ListEntry entry) {
		
		DifferDialog dialog = new DifferDialog(parent);
		dialog.mergePanel.displayDiff(entry);
		dialog.setVisible(true);
		
		return "";
	}
	
	/**
	 * Create the dialog.
	 */
	public DifferDialog(Component parent) {
		super(Utility.findWindow(parent), ModalityType.DOCUMENT_MODAL);
		
		try {
			// Initialize components.
			initComponents();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Initialize components.
	 */
	private void initComponents() {
		setBounds(100, 100, 719, 530);
		getContentPane().setLayout(new BorderLayout(0, 0));
		mergePanel = new DifferPanel();
		getContentPane().add(mergePanel, BorderLayout.CENTER);
	}
}
