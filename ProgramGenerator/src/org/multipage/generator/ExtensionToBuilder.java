/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JFrame;

import org.maclan.Area;
import org.maclan.Resource;
import org.maclan.Slot;
import org.multipage.generator.SlotEditorBasePanel.Callbacks;
import org.multipage.gui.FoundAttr;
import org.multipage.gui.TextPopupMenuAddIn;

/**
 * Interface for extensions to Builder application.
 * @author vakol
 *
 */
public interface ExtensionToBuilder {

	/**
	 * Create new area editor object.
	 * @param parentComponent
	 * @param area
	 * @return
	 */
	AreaEditorFrameBase newAreaEditor(Component parentComponent, Area area);
	
	/**
	 * Create new areas diagram editor.
	 * @param areasDiagramEditor
	 * @return
	 */
	AreaDiagramPanel newAreasDiagram(AreaDiagramContainerPanel areasDiagramEditor);

	/**
	 * Create new slot list panel.
	 * @return
	 */
	SlotListPanel newSlotListPanel();
	
	/**
	 * Create new slot editor panel.
	 * @param parentWindow
	 * @param slotEditorFrame
	 * @param slot
	 * @param isNew
	 * @param modal
	 * @param useHtmlEditor
	 * @param foundAttr
	 * @param callbacks 
	 * @return
	 */
	SlotEditorBasePanel newSlotEditorPanel(Window parentWindow, Slot slot,
										  boolean isNew, boolean modal, boolean useHtmlEditor,
										  FoundAttr foundAttr, Callbacks callbacks);
	
	
	/**
	 * Create new boolean editor panel.
	 * @return
	 */
	BooleanEditorPanelBase newBooleanEditorPanel();

	/**
	 * Create new enumeration panel.
	 * @return
	 */
	EnumerationEditorPanelBase newEnumerationEditorPanel();

	/**
	 * Create new area local trayMenu object.
	 * @param callbacks
	 * @return 
	 */
	AreaLocalMenu newAreaLocalMenu(AreaLocalMenu.Callbacks callbacks);
	
	/**
	 * Create new area local trayMenu object for diagram.
	 * @param callbacks
	 * @return 
	 */
	AreaLocalMenu newAreaLocalMenuForDiagram(AreaLocalMenu.Callbacks callbacks);
	
	/**
	 * Create new areas properties panel.
	 * @param isPropertiesPanel
	 * @return
	 */
	AreaPropertiesBasePanel newAreasProperties(boolean isPropertiesPanel);

	/**
	 * Create new slot text popup trayMenu.
	 * @param slot
	 * @return
	 */
	TextPopupMenuAddIn newGeneratorTextPopupMenuAddIn(Slot slot);

	/**
	 * Create about dialog.
	 * @param frame
	 * @return
	 */
	AboutDialogBase newAboutDialog(JFrame frame);

	/**
	 * Create new resource properties editor object.
	 * @param parentComponent
	 * @param resource
	 * @return
	 */
	ResourcePropertiesEditorBase newResourcePropertiesEditor(
			Component parentComponent, Resource resource);

	/**
	 * Create namespace resource renderer object.
	 * @return
	 */
	NamespaceResourceRendererBase newNamespaceResourceRenderer();

	/**
	 * Create area resource object.
	 * @return
	 */
	AreaResourceRendererBase newAreaResourceRenderer();
}
