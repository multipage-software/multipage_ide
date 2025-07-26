/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2025-04-08
 *
 */
package org.multipage.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Window;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SpringLayout;

import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Helper class that enables user to set selected JWindow the top most window.
 * @author vakol
 */
public class TopMostButton {
	
	/**
	 * Button constants.
	 */
	private static final int BUTTON_WIDTH = 24;
	private static final int BUTTON_HEIGHT = 24;
	private static final int BUTTON_TOP_MARGIN = 0;
	private static final int BUTTON_RIGHT_MARGIN = 3;
	
	/**
	 * Main window reference. The main window determines initial setting of child windows.
	 */
	private static Window mainWindow = null;
	
	/**
	 * Set main window.
	 * @param window
	 */
	public static void setMainWindow(Window window) {
		
		mainWindow = window;
	}
	
	/**
	 * Add button to control top most behaviour.
	 * @param window
	 * @param container
	 * @param topMargin
	 * @param rightMargin
	 */
	public static void add(Window window, Container container, Integer topMargin, Integer rightMargin) {
		
		try {
			// Trim margins.
			if (topMargin == null) {
				topMargin = BUTTON_TOP_MARGIN;
			}
			if (rightMargin == null) {
				rightMargin = BUTTON_RIGHT_MARGIN;
			}
			
			// Create toggle button.
			JToggleButton topMostButton = new JToggleButton();
			topMostButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
			topMostButton.setMargin(new Insets(0, 0, 0, 0));
			topMostButton.setSelected(window.isAlwaysOnTop() || (mainWindow != null && mainWindow.isAlwaysOnTop()));
			setIcon(topMostButton);
			topMostButton.setToolTipText(Resources.getString("org.multipage.gui.tooltipToggleTopMostWindow"));
			
			// Set the top most window action.
			topMostButton.addActionListener(e -> onButtonAction(window, topMostButton));
			// Fire the button event for the first time.
			onButtonAction(window, topMostButton);
			
			// If the container is a toolbar, add toggle button to this toolbar.
			if (container instanceof JToolBar) {
				JPanel rightAlignedPanel = new JPanel(new BorderLayout());
				rightAlignedPanel.setOpaque(false);
				rightAlignedPanel.add(topMostButton, BorderLayout.EAST);
				JToolBar toolBar = (JToolBar) container;
				toolBar.add(Box.createHorizontalGlue());
				toolBar.add(rightAlignedPanel);
				return;
			}
			
			// Add the toggle button to input container. 
			LayoutManager layout = container.getLayout();
			if (layout instanceof SpringLayout) {
				SpringLayout springLayout = (SpringLayout) layout;
				add(container, springLayout, topMostButton, topMargin, rightMargin);
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * On button action.
	 * @param topMostButton 
	 * @param window 
	 */
	private static void onButtonAction(Window window, JToggleButton topMostButton) {
		try {
			
		    setIcon(topMostButton);
		    boolean isTopMost = topMostButton.isSelected();
		    window.setAlwaysOnTop(isTopMost);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Add button to control top most behaviour.
	 * @param window
	 * @param container
	 */
	public static void add(Window window, Container container) {
		try {
			
			// Delegate the call.
			add(window, container, null, null);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set toggle button icon.
	 * @param topMostButton
	 */
	private static void setIcon(JToggleButton topMostButton) {
		try {
			
			boolean isTopMost = topMostButton.isSelected();
			String iconPath = isTopMost ? "org/multipage/gui/images/top_most.png"
										: "org/multipage/gui/images/top_most_off.png";
			topMostButton.setIcon(Images.getIcon(iconPath));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Add the top most control button to Spring layout.
	 * @param container 
	 * @param springLayout
	 * @param topMostButton
	 * @param topMargin
	 * @param rightMargin
	 */
	private static void add(Container container, SpringLayout springLayout, JToggleButton topMostButton,
			int topMargin, int rightMargin) {
		try {
			
			springLayout.putConstraint(SpringLayout.NORTH, topMostButton, topMargin, SpringLayout.NORTH, container);
			springLayout.putConstraint(SpringLayout.EAST, topMostButton, -rightMargin, SpringLayout.EAST, container);
			container.add(topMostButton);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
