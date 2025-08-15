/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2025-04-29
 *
 */
package org.multipage.generator;

import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.Safe;

/**
 * Data transfered with the Drag and Drop functionality in the dialog navigator.
 * 
 * @author vakol
 */
public class NavigatorTransferableData {
	
	/**
	 * Enumeration of data types.
	 */
	public static final int UNKNOWN 				= 0;
	public static final int AREA_EDITOR 			= 1;
	public static final int AREA_PROPERTIES 		= 2;
	public static final int AREA_TREE_EDITOR 		= 3;
	public static final int START_RESOURCE_EDITOR 	= 4;
	public static final int TEXT_RESOURCE_EDITOR 	= 5;
	public static final int SLOT_EDITOR 			= 6;
	
	/**
	 * Trabsfered data type.
	 */
	private int type = UNKNOWN;
	
	/**
	 * Editor key.
	 */
	private Object key = null;
	
	/**
	 * Editor object.
	 */
	private Object editor = null;
	
	/**
	 * Source buttons panel.
	 */
	private NavigatorButtonsPanel buttonsPanel = null;
	
	/**
	 * Constructor.
	 * @param areaId
	 * @param editor
	 * @param buttonsPanel 
	 */
	public NavigatorTransferableData(long areaId, AreaEditorFrameBase editor, NavigatorButtonsPanel buttonsPanel) {
		try {
			
			type = AREA_EDITOR;
			key = areaId;
			this.editor = editor;
			this.buttonsPanel = buttonsPanel;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get area editor.
	 * @return
	 * @throws Exception
	 */
	public AreaEditorFrameBase getAreaEditor()
			throws Exception {
		
		if (type != AREA_EDITOR || !(editor instanceof AreaEditorFrameBase)) {
			Utility.throwException("org.multipage.generator.messageBadNavigatorTransferableType");
		}
		return (AreaEditorFrameBase) editor;
	}
	
	/**
	 * Constructor.
	 * @param areaId
	 * @param editor
	 * @param buttonsPanel 
	 */
	public NavigatorTransferableData(long areaId, AreaPropertiesFrame editor, NavigatorButtonsPanel buttonsPanel) {
		try {
			
			type = AREA_PROPERTIES;
			key = areaId;
			this.editor = editor;
			this.buttonsPanel = buttonsPanel;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get area properties editor.
	 * @return
	 * @throws Exception
	 */
	public AreaPropertiesFrame getAreaPropertiesEditor()
			throws Exception {
		
		if (type != AREA_PROPERTIES || !(editor instanceof AreaPropertiesFrame)) {
			Utility.throwException("org.multipage.generator.messageBadNavigatorTransferableType");
		}
		return (AreaPropertiesFrame) editor;
	}
	
	/**
	 * Constructor.
	 * @param areaId
	 * @param editor
	 * @param buttonsPanel 
	 */
	public NavigatorTransferableData(long areaId, AreasTreeEditorFrame editor, NavigatorButtonsPanel buttonsPanel) {
		try {
			
			type = AREA_TREE_EDITOR;
			key = areaId;
			this.editor = editor;
			this.buttonsPanel = buttonsPanel;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get area tree editor.
	 * @return
	 * @throws Exception
	 */
	public AreasTreeEditorFrame getAreaTreeEditor()
			throws Exception {

		if (type != AREA_TREE_EDITOR || !(editor instanceof AreasTreeEditorFrame)) {
			Utility.throwException("org.multipage.generator.messageBadNavigatorTransferableType");
		}
		return (AreasTreeEditorFrame) editor;
	}
	
	/**
	 * Constructor.
	 * @param resourceVersionId
	 * @param editor
	 * @param buttonsPanel 
	 */
	public NavigatorTransferableData(String resourceVersionId, TextResourceEditorFrame editor, NavigatorButtonsPanel buttonsPanel) {
		try {
			
			type = START_RESOURCE_EDITOR;
			key = resourceVersionId;
			this.editor = editor;
			this.buttonsPanel = buttonsPanel;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get start resource version ID.
	 * @return
	 * @throws Exception
	 */
	public long getVersionId() throws Exception {
		
		try {
			if (type != START_RESOURCE_EDITOR || !(key instanceof String)) {
				Utility.throwException("org.multipage.generator.messageBadNavigatorTransferableType");
			}
			
			// Get version ID.
			String respourceVersionIds = (String) key;
			Obj<Long> versionId = new Obj<>(null);
			NavigatorButtonsPanel.parseResourceVersionId(respourceVersionIds, null, versionId);
			return versionId.ref;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		Utility.throwException("org.multipage.generator.messageCannotGetStartResourceEditorVersion");
		return 0L;
	}
	
	/**
	 * Get start resource editor.
	 * @return
	 * @throws Exception
	 */
	public TextResourceEditorFrame getStartResourceEditor()
			throws Exception {

		if (type != START_RESOURCE_EDITOR || !(editor instanceof TextResourceEditorFrame)) {
			Utility.throwException("org.multipage.generator.messageBadNavigatorTransferableType");
		}
		return (TextResourceEditorFrame) editor;
	}
	
	/**
	 * Constructor.
	 * @param resourceId
	 * @param editor
	 * @param buttonsPanel 
	 */
	public NavigatorTransferableData(long resourceId, TextResourceEditorFrame editor, NavigatorButtonsPanel buttonsPanel) {
		try {
			
			type = TEXT_RESOURCE_EDITOR;
			key = resourceId;
			this.editor = editor;
			this.buttonsPanel = buttonsPanel;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Constructor.
	 * @param slotId
	 * @param editor
	 * @param buttonsPanel 
	 */
	public NavigatorTransferableData(long slotId, SlotEditorFrame editor, NavigatorButtonsPanel buttonsPanel) {
		try {
			
			type = SLOT_EDITOR;
			key = slotId;
			this.editor = editor;
			this.buttonsPanel = buttonsPanel;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get type.
	 * @return
	 */
	public int getType() {
		
		return type;
	}

	/**
	 * Get editor key.
	 * @return
	 */
	public Object getKey() {
		
		return key;
	}
	
	/**
	 * Get source button panel.
	 * @return
	 */
	public NavigatorButtonsPanel getButtonPanel() {
		
		return buttonsPanel;
	}

	/**
	 * Get text resource editor.
	 * @return
	 * @throws Exception
	 */
	public TextResourceEditorFrame getResourceEditor()
			throws Exception {

		if (type != TEXT_RESOURCE_EDITOR || !(editor instanceof TextResourceEditorFrame)) {
			Utility.throwException("org.multipage.generator.messageBadNavigatorTransferableType");
		}
		return (TextResourceEditorFrame) editor;
	}
	
	/**
	 * Get slot editor.
	 * @return
	 */
	public SlotEditorFrame getSlotEditor()
			throws Exception {

		if (type != SLOT_EDITOR || !(editor instanceof SlotEditorFrame)) {
			Utility.throwException("org.multipage.generator.messageBadNavigatorTransferableType");
		}
		return (SlotEditorFrame) editor;
	}
}
