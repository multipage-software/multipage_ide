/*
 * Copyright 2010-2025 (C) Vaclav Koarcik
 * 
 * Created on : 2020-02-24
 *
 */
package org.multipage.generator;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SpringLayout;

import org.maclan.Area;
import org.multipage.generator.ProgramPaths.PathSupplier;
import org.multipage.gui.Images;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.Utility;
import org.multipage.util.Safe;

/**
 * Dialog that displays path editor.
 * @author vakol
 *
 */
public class PathSelectionDialog extends JDialog {
	
	/**
	 * Version
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Window position
	 */
	private static Rectangle bounds;
	
	/**
	 * Set default state
	 */
	public static void setDefaultData() {
		
		bounds = new Rectangle();
	}
	
	/**
	 * Load state.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void serializeData(StateInputStream inputStream)
			throws IOException, ClassNotFoundException {
		
		bounds = Utility.readInputStreamObject(inputStream, Rectangle.class);
	}
	
	/**
	 * Save state.
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream) 
			throws IOException {
		
		outputStream.writeObject(bounds);
	}
	
	/**
	 * Confirmation flag
	 */
	private boolean confirmed = false;
	private JComboBox<PathSupplier> comboBoxPaths;
	private JLabel labelSelectPath;
	private JButton buttonCancel;
	private JButton buttonOk;
	
	/**
	 * Show dialog.
	 * @param parent
	 * @param slot 
	 * @param area 
	 * @return
	 */
	public static ProgramPaths.PathSupplier showDialog(Component parent, Area area) {
		
		try {
			PathSelectionDialog dialog = new PathSelectionDialog(parent);
			dialog.loadPaths(area);
			dialog.setVisible(true);
			
			if (!dialog.confirmed) {
				return null;
			}
			
			return dialog.getPathSupplier();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Constructor.
	 * @param parent
	 */
	public PathSelectionDialog(Component parent) {
		super(Utility.findWindow(parent), ModalityType.APPLICATION_MODAL);
		
		try {
			// Initialize components.
			initComponents();
			// Post creation.
			postCreation(); //$hide$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Initialize components.
	 */
	private void initComponents() {
		setResizable(false);
		setBounds(new Rectangle(0, 0, 500, 300));
		
		setPreferredSize(new Dimension(500, 170));
		setTitle("org.multipage.generator.textSelectPath");
		
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		comboBoxPaths = new JComboBox<PathSupplier>();
		springLayout.putConstraint(SpringLayout.WEST, comboBoxPaths, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, comboBoxPaths, -10, SpringLayout.EAST, getContentPane());
		comboBoxPaths.setPreferredSize(new Dimension(200, 25));
		getContentPane().add(comboBoxPaths);
		
		labelSelectPath = new JLabel("org.multipage.generator.textSelectPath");
		springLayout.putConstraint(SpringLayout.NORTH, labelSelectPath, 16, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, comboBoxPaths, 6, SpringLayout.SOUTH, labelSelectPath);
		springLayout.putConstraint(SpringLayout.WEST, labelSelectPath, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(labelSelectPath);
		
		buttonCancel = new JButton("textCancel");
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		buttonCancel.setMargin(new Insets(0, 0, 0, 0));
		buttonCancel.setPreferredSize(new Dimension(80, 25));
		springLayout.putConstraint(SpringLayout.SOUTH, buttonCancel, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, buttonCancel, 0, SpringLayout.EAST, comboBoxPaths);
		getContentPane().add(buttonCancel);
		
		buttonOk = new JButton("textOk");
		buttonOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOk();
			}
		});
		springLayout.putConstraint(SpringLayout.SOUTH, buttonOk, 0, SpringLayout.SOUTH, buttonCancel);
		springLayout.putConstraint(SpringLayout.EAST, buttonOk, -6, SpringLayout.WEST, buttonCancel);
		buttonOk.setPreferredSize(new Dimension(80, 25));
		buttonOk.setMargin(new Insets(0, 0, 0, 0));
		getContentPane().add(buttonOk);
	}
	
	/**
	 * Post creation.
	 */
	private void postCreation() {
		try {
			
			localize();
			setIcons();
			
			loadDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Localize components
	 */
	private void localize() {
		try {
			
			Utility.localize(this);
			Utility.localize(labelSelectPath);
			Utility.localize(buttonOk);
			Utility.localize(buttonCancel);
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
			
			buttonOk.setIcon(Images.getIcon("org/multipage/generator/images/ok_icon.png"));
			buttonCancel.setIcon(Images.getIcon("org/multipage/generator/images/cancel_icon.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On OK
	 */
	protected void onOk() {
		try {
			
			// Confirm revision
			confirmed = true;
			saveDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
		dispose();
	}
	
	/**
	 * On cancel
	 */
	protected void onCancel() {
		try {
			
			saveDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
		
		dispose();
	}
	
	/**
	 * Load dialog
	 */
	private void loadDialog() {
		try {
			
			if (!bounds.isEmpty()) {
				setBounds(bounds);
			}
			else {
				Utility.centerOnScreen(this);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Load paths.
	 * @param area
	 */
	private void loadPaths(Area area) {
		try {
			
			GeneratorUtility.loadProgramPaths(comboBoxPaths);
			GeneratorUtility.loadAreaPaths(comboBoxPaths, area);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Save dialog
	 */
	private void saveDialog() {
		try {
			
			bounds = getBounds();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get path supplier.
	 * @return
	 */
	private PathSupplier getPathSupplier() {
		
		try {
			ProgramPaths.PathSupplier pathSupplier = (ProgramPaths.PathSupplier) comboBoxPaths.getSelectedItem();
			return pathSupplier;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
}