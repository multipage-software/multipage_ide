/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SpringLayout;

import org.maclan.Namespace;
import org.multipage.gui.Images;
import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.Safe;

/**
 * Dialog that displays namespaces in the tree view.
 * @author vakol
 *
 */
public class NamespaceTreeDialog extends JDialog {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Dialog confirmation.
	 */
	private boolean confirm = false;
	
	/**
	 * Selected namespace ID.
	 */
	private long namespaceId;

	
	/**
	 * Dialog components.
	 */
	private NamespaceTreePanel panelNamespaces;
	private JLabel labelNamespaces;
	private JButton buttonCancel;
	private JButton buttonSelect;

	/**
	 * Launch the dialog.
	 * @param namespaceId 
	 */
	public static boolean showDialog(Component parentComponent, Obj<Long> namespaceId) {
		
		try {
			NamespaceTreeDialog dialog = new NamespaceTreeDialog(parentComponent);
			dialog.selectNamespace(namespaceId.ref);
			dialog.setVisible(true);
			
			if (dialog.confirm) {
				namespaceId.ref = dialog.namespaceId;
			}
			return dialog.confirm;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Create the dialog.
	 * @param parentFrame 
	 */
	public NamespaceTreeDialog(Component parentComponent) {
		super(Utility.findWindow(parentComponent), ModalityType.DOCUMENT_MODAL);
		
		try {
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					onCancel();
				}
			});
			setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			// Initialize components.
			initComponents();
			// Post creation.
			postCreate();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Initialize components.
	 */
	private void initComponents() {
		
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		panelNamespaces = new NamespaceTreePanel();
		springLayout.putConstraint(SpringLayout.WEST, panelNamespaces, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, panelNamespaces, -10, SpringLayout.EAST, getContentPane());
		
		getContentPane().add(panelNamespaces);
		
		labelNamespaces = new JLabel("org.multipage.generator.textNamespaces");
		springLayout.putConstraint(SpringLayout.NORTH, panelNamespaces, 6, SpringLayout.SOUTH, labelNamespaces);
		springLayout.putConstraint(SpringLayout.NORTH, labelNamespaces, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, labelNamespaces, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(labelNamespaces);
		
		buttonCancel = new JButton("textCancel");
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		springLayout.putConstraint(SpringLayout.SOUTH, panelNamespaces, -6, SpringLayout.NORTH, buttonCancel);
		springLayout.putConstraint(SpringLayout.SOUTH, buttonCancel, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, buttonCancel, -10, SpringLayout.EAST, getContentPane());
		buttonCancel.setMargin(new Insets(2, 2, 2, 2));
		buttonCancel.setPreferredSize(new Dimension(80, 25));
		getContentPane().add(buttonCancel);
		
		buttonSelect = new JButton("textSelect");
		buttonSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSelect();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, buttonSelect, 0, SpringLayout.NORTH, buttonCancel);
		springLayout.putConstraint(SpringLayout.EAST, buttonSelect, -6, SpringLayout.WEST, buttonCancel);
		buttonSelect.setMargin(new Insets(2, 2, 2, 2));
		buttonSelect.setPreferredSize(new Dimension(80, 25));
		getContentPane().add(buttonSelect);
		setTitle("org.multipage.generator.textNameSpaceDialog");
		setBounds(100, 100, 450, 300);
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			// Center dialog.
			Utility.centerOnScreen(this);
			// Localize components.
			localize();
			// Load namespaces tree.
			panelNamespaces.updateInformation();
			// Set icons.
			setIcons();
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
			Utility.localize(labelNamespaces);
			Utility.localize(buttonCancel);
			Utility.localize(buttonSelect);
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
			
			buttonCancel.setIcon(Images.getIcon("org/multipage/generator/images/cancel_icon.png"));
			buttonSelect.setIcon(Images.getIcon("org/multipage/generator/images/ok_icon.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On cancel.
	 */
	protected void onCancel() {

		confirm = false;
		dispose();
	}
	
	/**
	 * On select.
	 */
	protected void onSelect() {
		try {
			
			// Get selected namespace.
			Namespace namespace = panelNamespaces.getSelectedNamespace();
			
			namespaceId = namespace.getId();
			confirm = true;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
		dispose();
	}

	/**
	 * Select namespace.
	 * @param id
	 */
	private void selectNamespace(long id) {
		try {
			
			panelNamespaces.selectNamespace(id);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
