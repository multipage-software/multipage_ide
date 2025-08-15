/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2025-08-14
 *
 */

package org.multipage.generator;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Window;
import java.io.IOException;

import javax.swing.JFrame;

import org.maclan.Area;
import org.multipage.gui.FoundAttr;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.Utility;
import org.multipage.util.Safe;

/**
 * Text resource editor frame.
 * @author vakol
 * 
 */
public class TextResourceEditorFrame  extends JFrame {
	
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
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
	 * Load data.
	 * @param inputStream
	 */
	public static void seriliazeData(StateInputStream inputStream)
		throws IOException, ClassNotFoundException {

		bounds = Utility.readInputStreamObject(inputStream, Rectangle.class);
	}

	/**
	 * Save data.
	 * @param outputStream
	 */
	public static void seriliazeData(StateOutputStream outputStream)
		throws IOException {

		outputStream.writeObject(bounds);
	}
	
	/**
	 * Editor panel.
	 */
	private static TextResourceEditorPanel editorPanel = null;

	/**
	 * Launch the dialog.
	 * @param component
	 * @param resource 
	 * @param isSavedAsText
	 * @param modal
	 */
	public static void showDialog(Component component, long resource,
			boolean isSavedAsText, boolean modal) {
		try {
			
			showDialog(component, resource, "", isSavedAsText, null, false, 0L, modal);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Launch the dialog.
	 * @param component
	 * @param resource 
	 * @param isSavedAsText
	 * @param area
	 * @param isStartResource
	 * @param versionId
	 * @param modal
	 */
	public static void showDialog(Component component, long resource,
			boolean isSavedAsText, Area area, boolean isStartResource, long versionId,
			boolean modal) {
		try {
			
			String areaDescription = "";
			if (area != null) {
				areaDescription = area.getDescription();
			}
			showDialog(component, resource, areaDescription, isSavedAsText, null,
					isStartResource, versionId, modal);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Launch dialog.
	 * @param component
	 * @param resourceId
	 * @param areaDescription
	 * @param isSavedAsText
	 * @param foundAttributes
	 * @param isStartResource
	 * @param versionId
	 * @param modal
	 */
	public static void showDialog(Component component, long resourceId, String areaDescription,
			boolean isSavedAsText, FoundAttr foundAttributes, boolean isStartResource,
			long versionId, boolean modal) {
		
		try {
			
			// Get already opened editor.
			TextResourceEditorFrame dialog = null;
			if (isStartResource) {
				dialog = DialogNavigator.getStartResourceEditor(resourceId, versionId);
			}
			else {
				dialog = DialogNavigator.getResourceEditor(resourceId);
			}
			
			if (dialog == null) {
				Window parentWindow = Utility.findWindow(component);
		
				// If the resource is not set as text, inform user and exit
				// the method.
				if (!isSavedAsText) {
					Utility.show(parentWindow, "org.multipage.generator.messageResourceNotSavedAsText");
					return;
				}
				
				// Create new dialog poanel.
				editorPanel = new TextResourceEditorPanel(null, resourceId, isStartResource, versionId,
																				  areaDescription, modal);
				editorPanel.setFoundAttributes(foundAttributes);
				dialog = new TextResourceEditorFrame();
				dialog.setContentPane(editorPanel);
				if (isStartResource) {
					DialogNavigator.addStartResourceEditor(versionId, dialog);
				}
				else {
					DialogNavigator.addResourceEditor(dialog);
				}
			}
			
			dialog.setVisible(true);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Constructor.
	 */
	public TextResourceEditorFrame() {
		try {
			
			loadDialog();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Close dialog.
	 */
	// TODO: <---REFACTOR Move the close frame event to the frame object. 
	public void close() {
		try {
			// Remove editor from the navigator window.
			if (isStartResource) {
				DialogNavigator.removeStartResourceEditor(resourceId, versionId);
			}
			else {
				DialogNavigator.removeResourceEditor(resourceId);
			}
			// Process possible new content.
			processNewContent();
			// Save dialog data.
			saveDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
		// Dispose window.
		dispose();
	}
	
	/**
	 * Load dialog.
	 */
	private void loadDialog() {
		try {
			
			if (bounds.isEmpty()) {
				// Center the dialog.
				Utility.centerOnScreen(this);
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
	 * Get resource ID.
	 * @return
	 */
	public long getResourceId() {
		try {
			
			editorPanel.getResourceId();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0L;
	}
	
	/**
	 * Get version ID.
	 * @return
	 */
	public long getVersionId() {
		try {
			
			editorPanel.getVersionId();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0L;
	}
	
	/**
	 * Get area description.
	 * @return
	 */
	public String getAreaDescription() {
		try {
			
			editorPanel.getAreaDescription();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
}
