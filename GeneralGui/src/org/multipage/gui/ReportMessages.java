/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import org.multipage.util.Safe;

/**
 * Dialog that displays error messages.
 * @author vakol
 *
 */
public class ReportMessages extends JDialog {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	// $hide<<$
	/**
	 * Components.
	 */
	private JScrollPane scrollPane;
	private JList list;
	private JLabel labelMessages;
	private JButton buttonClose;

	/**
	 * Launch the application.
	 */

	public static void showDialog(Window parentWindow,
			LinkedList<String> errorMessages) {
		try {
			
			ReportMessages dialog = new ReportMessages(parentWindow);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.postCreation(errorMessages);
			dialog.setVisible(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create the dialog.
	 * @param parentWindow 
	 */
	public ReportMessages(Window parentWindow) {
		super(parentWindow, ModalityType.APPLICATION_MODAL);
		
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
		setTitle("org.multipage.gui.textReportMessages");
		setBounds(100, 100, 450, 300);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		scrollPane = new JScrollPane();
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(scrollPane);
		
		list = new JList();
		list.setForeground(Color.BLACK);
		scrollPane.setViewportView(list);
		
		labelMessages = new JLabel("org.multipage.gui.textErrorMessages");
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, labelMessages);
		springLayout.putConstraint(SpringLayout.NORTH, labelMessages, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, labelMessages, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(labelMessages);
		
		buttonClose = new JButton("textClose");
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -6, SpringLayout.NORTH, buttonClose);
		springLayout.putConstraint(SpringLayout.EAST, buttonClose, -10, SpringLayout.EAST, getContentPane());
		buttonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		buttonClose.setMargin(new Insets(0, 0, 0, 0));
		buttonClose.setPreferredSize(new Dimension(80, 25));
		springLayout.putConstraint(SpringLayout.SOUTH, buttonClose, -10, SpringLayout.SOUTH, getContentPane());
		getContentPane().add(buttonClose);
	}

	/**
	 * Post creation.
	 * @param errorMessages
	 */
	private void postCreation(LinkedList<String> errorMessages) {
		try {
			
			// Localize components.
			localize();
			// Set icons.
			setIcons();
			// Center dialog.
			Utility.centerOnScreen(this);
			// Load list error messages.
			loadList(errorMessages);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Localize components.
	 */
	private void localize() {
		try {
			
			Utility.localize(this);
			Utility.localize(labelMessages);
			Utility.localize(buttonClose);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set icons.
	 */
	private void setIcons() {
		try {
			
			buttonClose.setIcon(Images.getIcon("org/multipage/gui/images/cancel_icon.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load list.
	 * @param errorMessages
	 */
	private void loadList(LinkedList<String> errorMessages) {
		try {
			
			DefaultListModel model = new DefaultListModel();
			for (String message : errorMessages) {
				model.addElement(message);
			}
			list.setModel(model);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
