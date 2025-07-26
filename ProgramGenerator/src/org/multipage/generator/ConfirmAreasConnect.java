/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.multipage.gui.Images;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.Safe;

/**
 * Dialog that displays information about connected areas.
 * @author vakol
 *
 */
public class ConfirmAreasConnect extends JDialog {
	
	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Inherit state.
	 */
	private static boolean inheritState = false;
	
	/**
	 * Hide sub areas state.
	 */
	private static boolean hideSubAreas = false;
	
	/**
	 * Load state.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void serializeData(StateInputStream inputStream)
			throws IOException, ClassNotFoundException {
		
		inheritState = inputStream.readBoolean();
		hideSubAreas = inputStream.readBoolean();
	}

	/**
	 * Save state.
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream)
			throws IOException{
		
		outputStream.writeBoolean(inheritState);
		outputStream.writeBoolean(hideSubAreas);
	}

	/**
	 * Confirm flag.
	 */
	private static boolean confirm = false;
	// $hide<<$
	
	/**
	 * Components.
	 */
	private JCheckBox checkBoxInherit;
	private JLabel labelRelationNameSub;
	private JTextField textNameSub;
	private JButton buttonCancel;
	private JButton buttonOk;
	private JLabel labelRelationNameSuper;
	private JTextField textNameSuper;
	private JCheckBox checkHideSubAreas;
	private JLabel labelMessage;

	/**
	 * 
	 * @param frame
	 * @param inheritance
	 * @param relationNameSub 
	 * @param relationNameSuper 
	 * @param hideSub 
	 * @return
	 */
	public static boolean showConfirmDialog(Window parent, Obj<Boolean> inheritance, Obj<String> relationNameSub, Obj<String> relationNameSuper,
			Obj<Boolean> hideSub) {
		
		try {
			ConfirmAreasConnect dialog = new ConfirmAreasConnect(parent);
			dialog.setVisible(true);
			
			if (confirm) {
				relationNameSub.ref = dialog.textNameSub.getText();
				relationNameSuper.ref = dialog.textNameSuper.getText();
				
				inheritance.ref = dialog.checkBoxInherit.isSelected();
				hideSub.ref = dialog.checkHideSubAreas.isSelected();
			}
			
			return confirm;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Create the dialog.
	 * @param parent 
	 * @param isRecursion 
	 */
	public ConfirmAreasConnect(Window parent) {
		super(parent, ModalityType.APPLICATION_MODAL);
		
		try {
			// Initialize components.
			initComponents();
			// $hide>>$
			postCreate();
			// $hide<<$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Initialize components.
	 */
	private void initComponents() {
		setTitle("org.multipage.generator.textConfirmAreasConnection");
		setResizable(false);
		setBounds(100, 100, 307, 247);
		getContentPane().setLayout(null);

		checkBoxInherit = new JCheckBox("org.multipage.generator.textInheritFromSuperArea");
		checkBoxInherit.setBounds(80, 18, 195, 23);
		getContentPane().add(checkBoxInherit);
		
		labelRelationNameSub = new JLabel("org.multipage.generator.textRelationNameSub");
		labelRelationNameSub.setBounds(16, 50, 270, 14);
		getContentPane().add(labelRelationNameSub);
		
		textNameSub = new JTextField();
		textNameSub.setBounds(16, 65, 260, 20);
		getContentPane().add(textNameSub);
		textNameSub.setColumns(10);
		
		buttonCancel = new JButton("textCancel");
		buttonCancel.setMargin(new Insets(2, 0, 0, 0));
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		buttonCancel.setPreferredSize(new Dimension(80, 25));
		buttonCancel.setBounds(158, 178, 89, 23);
		getContentPane().add(buttonCancel);
		
		buttonOk = new JButton("textOk");
		buttonOk.setMargin(new Insets(2, 0, 0, 0));
		buttonOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOk();
			}
		});
		buttonOk.setPreferredSize(new Dimension(80, 25));
		buttonOk.setBounds(59, 178, 89, 23);
		getContentPane().add(buttonOk);
		
		labelRelationNameSuper = new JLabel("org.multipage.generator.textRelationNameSuper");
		labelRelationNameSuper.setBounds(16, 97, 270, 14);
		getContentPane().add(labelRelationNameSuper);
		
		textNameSuper = new JTextField();
		textNameSuper.setBounds(16, 110, 260, 20);
		getContentPane().add(textNameSuper);
		textNameSuper.setColumns(10);
		
		checkHideSubAreas = new JCheckBox("org.multipage.generator.textHideSubAreas");
		checkHideSubAreas.setSelected(true);
		checkHideSubAreas.setBounds(80, 137, 154, 23);
		getContentPane().add(checkHideSubAreas);
		
		labelMessage = new JLabel("");
		labelMessage.setHorizontalAlignment(SwingConstants.CENTER);
		labelMessage.setBounds(16, 24, 275, 14);
		getContentPane().add(labelMessage);
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			localize();
			setIcons();
			Utility.centerOnScreen(this);
			
			// Set dialog components.
			checkBoxInherit.setSelected(inheritState);
			checkHideSubAreas.setSelected(hideSubAreas);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Localize.
	 */
	private void localize() {
		try {
			
			Utility.localize(this);
			Utility.localize(checkBoxInherit);
			Utility.localize(buttonCancel);
			Utility.localize(buttonOk);
			Utility.localize(labelRelationNameSub);
			Utility.localize(labelRelationNameSuper);
			Utility.localize(checkHideSubAreas);
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
	 * On OK.
	 */
	protected void onOk() {
		try {
			
			saveDialog();
			confirm = true;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
		dispose();
	}

	/**
	 * On cancel.
	 */
	protected void onCancel() {
		
		confirm = false;
		dispose();
	}
	
	/**
	 * Save dialog.
	 */
	private void saveDialog() {
		try {
			
			inheritState = checkBoxInherit.isSelected();
			hideSubAreas = checkHideSubAreas.isSelected();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
