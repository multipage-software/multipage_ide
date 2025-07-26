/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Helper class for creating toolbars.
 * @author vakol
 *
 */
public class ToolBarKit {

	/**
	 * Adds StatusBar button
	 */
	public static JButton addToolBarButton(JToolBar toolBarObject,
			String iconPictureName, Object notifyObject,
			String methodToInvoke, String toolTipResoure) {
		
		try {
			// Add StatusBar Button and set action adapter.
			JButton toolBarButton;
			toolBarButton = toolBarObject.add(new ActionAdapter(notifyObject, methodToInvoke, (Class<?>[])null));
			toolBarButton.setToolTipText(Resources.getString(toolTipResoure));
			
			ImageIcon icon = Images.getIcon(iconPictureName);
			if (icon != null) {
				toolBarButton.setIcon(icon);
			}
			else {
				String format = Resources.getString("org.multipage.gui.errorCannotLoadToolbarIcon");
				String message = String.format(format, iconPictureName);
				JOptionPane.showMessageDialog(null, message);
			}
			
			return toolBarButton;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Adds StatusBar button
	 */
	@SuppressWarnings("serial")
	public static JButton addToolBarButton(JToolBar toolBarObject,
			String iconPictureName, String toolTipResoure, Runnable actionLambda) {
		
		try {
			// Add StatusBar Button and set action adapter.
			JButton toolBarButton;
			toolBarButton = toolBarObject.add(new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					// Invoke lambda function.
					actionLambda.run();
				}
			});
			String tooltipText = null;
			if (toolTipResoure.startsWith("#")) {
				tooltipText = toolTipResoure.substring(1);
			}
			else {
				tooltipText = Resources.getString(toolTipResoure);
			}
			toolBarButton.setToolTipText(tooltipText);
			
			ImageIcon icon = Images.getIcon(iconPictureName);
			if (icon != null) {
				toolBarButton.setIcon(icon);
			}
			else {
				String format = Resources.getString("org.multipage.gui.errorCannotLoadToolbarIcon");
				String message = String.format(format, iconPictureName);
				JOptionPane.showMessageDialog(null, message);
			}
			
			return toolBarButton;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Add toggle button.
	 */
	public static JToggleButton addToggleButton(JToolBar toolBarObject,
			String iconPictureName, Object notifyObject,
			String methodToInvoke, String toolTipResoure) {
		
		try {
			// Add toggle button and set action adapter.
			JToggleButton toggleButton = new JToggleButton();
			toggleButton.setAction(new ActionAdapter(notifyObject, methodToInvoke, (Class<?>[])null));
			toolBarObject.add(toggleButton);
			toggleButton.setToolTipText(Resources.getString(toolTipResoure));
	
			ImageIcon icon = Images.getIcon(iconPictureName);
			if (icon != null) {
				toggleButton.setIcon(icon);
			}
			else {
				String format = Resources.getString("org.multipage.gui.errorCannotLoadToolbarIcon");
				String message = String.format(format, iconPictureName);
				JOptionPane.showMessageDialog(null, message);
			}
			
			return toggleButton;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	

	/**
	 * Add toggle button.
	 */
	@SuppressWarnings("serial")
	public static JToggleButton addToggleButton(JToolBar toolBarObject,
			String iconPictureName, String toolTipResoure, Runnable actionLambda) {
		
		try {
			// Add toggle button and set action adapter.
			JToggleButton toggleButton = null;
			try {
				toggleButton = new JToggleButton();
				
				if (actionLambda != null) {
					toggleButton.setAction(new AbstractAction() {
						@Override
						public void actionPerformed(ActionEvent e) {
							try {
					
								// Invoke lambda function.
								actionLambda.run();
							}
							catch(Throwable expt) {
								Safe.exception(expt);
							};
						}
					});
				}
				
				toolBarObject.add(toggleButton);
				toggleButton.setToolTipText(Resources.getString(toolTipResoure));
		
				ImageIcon icon = Images.getIcon(iconPictureName);
				if (icon != null) {
					toggleButton.setIcon(icon);
				}
				else {
					String format = Resources.getString("org.multipage.gui.errorCannotLoadToolbarIcon");
					String message = String.format(format, iconPictureName);
					JOptionPane.showMessageDialog(null, message);
				}
			}
			catch (Exception e) {
	            e.printStackTrace();
	        }
			return toggleButton;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Add toggle button.
	 */
	public static JToggleButton addToggleButton(JToolBar toolBarObject,
			String iconPictureName, String toolTipResoure) {
		
		try {
			// Delegate the call.
			return addToggleButton(toolBarObject, iconPictureName, toolTipResoure, null);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
}
