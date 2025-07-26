/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2025-04-21
 *
 */
package org.multipage.generator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Window;

import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

import org.multipage.gui.Utility;
import org.multipage.util.Safe;

/**
 * Dialog navigator sub window.
 * @author vakol
 */
public class NavigatorWindow extends JWindow {
	
	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Navigator panel.
	 */
	private NavigatorButtonsPanel navigatorButtonsPanel = null;
	
	/**
	 * Panel caption.
	 */
	private JLabel labelCaption = null;
	
	// $hide<<$
	/**
	 * Window components.
	 */
	
	/**
	 * Create new window.
	 * @param parent
	 * @return
	 */
	public static NavigatorWindow createInstance(Window parent) {
		
		NavigatorWindow window = new NavigatorWindow(parent);
		return window;
	}
	
	/**
	 * Create new window.
	 * @param parent
	 * @return
	 */
	public static NavigatorWindow createInstance(Component parent) {
		
		Window parentWindow = Utility.findWindow(parent);
		NavigatorWindow window = new NavigatorWindow(parentWindow);
		return window;
	}

	/**
	 * Create the frame.
	 */
	public NavigatorWindow(Window parent) {
		super(parent);
		
		try {
			initComponents();
			postCreate(); // $hide$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Initialize components.
	 */
	private void initComponents() {
		
		getContentPane().setLayout(new BorderLayout(0, 0));
	}
	
	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			// Add button panel.
			navigatorButtonsPanel = new NavigatorButtonsPanel();
			getContentPane().add(navigatorButtonsPanel, BorderLayout.CENTER);
			
			// Set opacity and colors.
			setOpacity(DialogNavigator.NAVIGATOR_OPACITY);
			setColors();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	

	
	/**
	 * Set component colors.
	 */
	private void setColors() {
		try {
			
			Color background = CustomizedColors.get(ColorId.DIALOG_NAVIGATOR);
			
			setBackground(background);
			getContentPane().setBackground(background);
			navigatorButtonsPanel.setBackground(background);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get navigator button panel.
	 * @return
	 */
	public NavigatorButtonsPanel getButtonPanel() {
		
		return navigatorButtonsPanel;
	}
	
	/**
	 * Add panel caption.
	 * @param folderName
	 */
	public void addCaption(String folderName) {
		try {
			
			if (navigatorButtonsPanel == null) {
				return;
			}
			if (labelCaption == null) {
				labelCaption = new JLabel(folderName);
			}
			
			labelCaption.setMaximumSize(NavigatorButtonsPanel.BUTTON_LARGE_SIZE);
			labelCaption.setMinimumSize(NavigatorButtonsPanel.BUTTON_LARGE_SIZE);
			labelCaption.setPreferredSize(NavigatorButtonsPanel.BUTTON_LARGE_SIZE);
			labelCaption.setHorizontalAlignment(SwingConstants.CENTER);
			labelCaption.setBackground(NavigatorButtonsPanel.FOLDER_BUTTON_HIGLIGHT_COLOR);
			labelCaption.setOpaque(true);

			getContentPane().add(labelCaption, BorderLayout.NORTH);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set new folder name.
	 * @param newFolderName
	 */
	public void setFolderName(String newFolderName) {
		try {
			
			if (labelCaption == null) {
				return;
			}
			labelCaption.setText(newFolderName);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
