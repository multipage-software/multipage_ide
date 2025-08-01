/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2019-12-10
 *
 */
package org.multipage.generator;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.SpringLayout;

import org.maclan.Slot;
import org.multipage.gui.Images;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.Utility;
import org.multipage.util.Safe;

/**
 * Dialog that displays editor for external provider properties.
 * @author vakol
 *
 */
public class ExternalProviderDialog extends JDialog {
	
	/**
	 * Version
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Controls
	 */
	private JButton buttonCancel;
	private JButton buttonOk;
	
	/**
	 * Bounds.
	 */
	private static Rectangle bounds;

	/**
	 * Set default data.
	 */
	public static void setDefaultData() {
		
		bounds = new Rectangle();
	}

	/**
	 * Read data.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void serializeData(StateInputStream inputStream) 
			throws IOException, ClassNotFoundException {
		
		Object object = inputStream.readObject();
		if (!(object instanceof Rectangle)) {
			throw new ClassNotFoundException();
		}
		bounds = (Rectangle) object;
	}

	/**
	 * Write data.
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream) 
			throws IOException {
		
		outputStream.writeObject(bounds);
	}
	
	/**
	 * Panel with controls for setting external provider
	 */
	private ExternalProviderPanel panel;
	
	/**
	 * The field has true value if the dialog has been confirmed
	 */
	private boolean confirm = false;

	/**
	 * Show dialog and set slot's external provider
	 * @param parent
	 * @param slot
	 * @return
	 */
	public static boolean showDialog(Component parent, Slot slot) {
		
		try {
			// Show dialog.
			ExternalProviderDialog dialog = new ExternalProviderDialog(parent);
			dialog.updateTitle(slot);
			dialog.setVisible(true);
			
			if (!dialog.confirm) {
				return false;
			}
			
			// Get link.
			String link = dialog.getExternalProviderLink();
			slot.setExternalProvider(link);
			
			// Get flags.
			boolean readsInput = dialog.getReadsInput();
			slot.setReadsInput(readsInput);
			
			boolean writesOutput = dialog.getWritesOuput();
			slot.setWritesOutput(writesOutput);
			
			return true;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Create the dialog.
	 */
	public ExternalProviderDialog(Component parent) {
		super(Utility.findWindow(parent), ModalityType.DOCUMENT_MODAL);
		
		try {
			// Initialize components
			initComponents(); 
			// Post creation of the dialog
			postCreation(); //$hide$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Initialize components
	 */
	private void initComponents() {
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setTitle("org.multipage.generator.titleExternalProviderDialog");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});
		setBounds(100, 100, 512, 418);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		panel = new ExternalProviderPanel();
		springLayout.putConstraint(SpringLayout.NORTH, panel, 0, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, panel, 0, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, panel, 0, SpringLayout.EAST, getContentPane());
		getContentPane().add(panel);
		
		buttonCancel = new JButton("textCancel");
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		buttonCancel.setMargin(new Insets(0, 0, 0, 0));
		buttonCancel.setPreferredSize(new Dimension(80, 25));
		springLayout.putConstraint(SpringLayout.SOUTH, panel, -6, SpringLayout.NORTH, buttonCancel);
		springLayout.putConstraint(SpringLayout.SOUTH, buttonCancel, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, buttonCancel, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(buttonCancel);
		
		buttonOk = new JButton("textOk");
		buttonOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOk();
			}
		});
		buttonOk.setMargin(new Insets(0, 0, 0, 0));
		buttonOk.setPreferredSize(new Dimension(80, 25));
		springLayout.putConstraint(SpringLayout.SOUTH, buttonOk, 0, SpringLayout.SOUTH, buttonCancel);
		springLayout.putConstraint(SpringLayout.EAST, buttonOk, -6, SpringLayout.WEST, buttonCancel);
		getContentPane().add(buttonOk);
	}
	
	/**
	 * On OK button clicked
	 */
	protected void onOk() {
		try {
			
			// Check link string
			String link = getExternalProviderLink();
			if (link.isEmpty()) {
				
				Utility.show(this, "org.multipage.generator.messageExternalProviderLinkNotCorrect");
				return;
			}
			
			confirm = true;
			saveDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
		dispose();
	}
	
	/**
	 * On cancel dialog
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
	 * Post creation
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
			Utility.localize(buttonOk);
			Utility.localize(buttonCancel);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set icons
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
	 * Update title
	 * @param slot 
	 */
	private void updateTitle(Slot slot) {
		try {
			
			String title = getTitle();
			String slotName = slot.getNameForGenerator();
			title = String.format(title, slotName);
			setTitle(title);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Load dialog.
	 */
	private void loadDialog() {
		try {
			
			if (bounds.isEmpty()) {
				Utility.centerOnScreen(this);
				bounds = getBounds();
			}
			else {
				setBounds(bounds);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Save dialog.
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
	 * Get external provider link
	 * @return
	 */
	private String getExternalProviderLink() {
		
		try {
			String link = panel.getExternalProviderLink();
			return link;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Return true value if slot reads from external provider
	 * @return
	 */
	private boolean getReadsInput() {
		
		try {
			// Delegate call
			return panel.getReadsInput();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Return true value if slot writes output to external provider
	 * @return
	 */
	private boolean getWritesOuput() {
		
		try {
			// Delegate call
			return panel.getWritesOutput();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
}
