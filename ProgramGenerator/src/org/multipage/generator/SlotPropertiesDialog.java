/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2020-02-10
 *
 */
package org.multipage.generator;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.SpringLayout;

import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.maclan.Slot;
import org.multipage.basic.ProgramBasic;
import org.multipage.gui.Images;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.Utility;
import org.multipage.util.Safe;

/**
 * Dialog for slot properties.
 * @author vakol
 *
 */
public class SlotPropertiesDialog extends JDialog {

	/**
	 * Version.
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
	 * Launch the application.
	 */
	public static void showDialog(Component parent, Slot slot) {
		try {
			
			SlotPropertiesDialog dialog = new SlotPropertiesDialog(Utility.findWindow(parent));
			dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			
			dialog.loadDialog();
			dialog.loadProperties(slot);
			
			dialog.setVisible(true);
			
			dialog.saveDialog();
			if (dialog.confirm) {
				dialog.saveProperties(slot);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Confirmation flag.
	 */
	private boolean confirm = false;
	
	/**
	 * External provider settings.
	 */
	private ExternalProviderPanel externalProviderPanel;
	private JButton buttonCancel;
	private JButton buttonSave;
	
	/**
	 * Create the dialog.
	 * @param parent 
	 */
	public SlotPropertiesDialog(Window parent) {
		super(parent, ModalityType.APPLICATION_MODAL);
		
		try {
			initComponents();
			postCreate(); //$hide$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Initialize components.
	 */
	private void initComponents() {
		setTitle("org.multipage.generator.titleSlotProperties");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});
		setBounds(100, 100, 532, 429);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		// Attach external provider panel.
		externalProviderPanel = new ExternalProviderPanel();
		springLayout.putConstraint(SpringLayout.NORTH, externalProviderPanel, 0, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, externalProviderPanel, 0, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, externalProviderPanel, 331, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, externalProviderPanel, 0, SpringLayout.EAST, getContentPane());
		getContentPane().add(externalProviderPanel);
		
		buttonCancel = new JButton("textCancel");
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		buttonCancel.setMargin(new Insets(0, 0, 0, 0));
		buttonCancel.setPreferredSize(new Dimension(80, 25));
		springLayout.putConstraint(SpringLayout.SOUTH, buttonCancel, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, buttonCancel, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(buttonCancel);
		
		buttonSave = new JButton("textSave");
		buttonSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSave();
			}
		});
		buttonSave.setMargin(new Insets(0, 0, 0, 0));
		buttonSave.setPreferredSize(new Dimension(80, 25));
		springLayout.putConstraint(SpringLayout.SOUTH, buttonSave, 0, SpringLayout.SOUTH, buttonCancel);
		springLayout.putConstraint(SpringLayout.EAST, buttonSave, -6, SpringLayout.WEST, buttonCancel);
		getContentPane().add(buttonSave);
	}
	
	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			localize();
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
			Utility.localize(buttonSave);
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
			
			buttonSave.setIcon(Images.getIcon("org/multipage/gui/images/save_icon.png"));
			buttonCancel.setIcon(Images.getIcon("org/multipage/gui/images/cancel_icon.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Load slot properties.
	 * @param slot
	 */
	private void loadProperties(Slot slot) {
		try {
			
			// Load slot properties from database.
			MiddleResult result = MiddleResult.OK;
			try {
				Middle middle = ProgramBasic.loginMiddle();
				result = middle.loadSlotProperties(slot);
			}
			catch (Exception e) {
				result = MiddleResult.exceptionToResult(e);
			}
			finally {
				ProgramBasic.logoutMiddle();
			}
			
			// On error do nothing.
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			// Load from slot.
			externalProviderPanel.loadFromSlot(slot);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Save properties.
	 * @param slot
	 */
	private void saveProperties(Slot slot) {
		try {
			
			// Get link.
			String link = externalProviderPanel.getExternalProviderLink();
			slot.setExternalProvider(link);
			
			// Get reads input flag.
			boolean readsInput = externalProviderPanel.getReadsInput();
			slot.setReadsInput(readsInput);
			
			// Get writes output flag.
			boolean writesOutput = externalProviderPanel.getWritesOutput();
			slot.setWritesOutput(writesOutput);
			
			// Save slot properties to database.
			MiddleResult result = MiddleResult.OK;
			try {
				Middle middle = ProgramBasic.loginMiddle();
				result = middle.updateSlotProperties(slot);
			}
			catch (Exception e) {
				result = MiddleResult.exceptionToResult(e);
			}
			finally {
				ProgramBasic.logoutMiddle();
			}
			
			// On error do nothing.
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
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
		
		// Dispose it.
		dispose();
	}
	
	/**
	 * On Save changes.
	 */
	protected void onSave() {
		
		confirm = true;
		
		// Dispose it.
		dispose();
	}
	
	/**
	 * Load dialog.
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
}
