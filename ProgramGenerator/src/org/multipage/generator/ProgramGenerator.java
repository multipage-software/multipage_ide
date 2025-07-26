/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Component;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.JFrame;

import org.maclan.Area;
import org.maclan.AreaId;
import org.maclan.AreasModel;
import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.maclan.Resource;
import org.maclan.Slot;
import org.maclan.VersionObj;
import org.maclan.server.AddDebugWatchDialog;
import org.maclan.server.DebugViewer;
import org.maclan.server.TextRenderer;
import org.maclan.server.XdebugSessionDialog;
import org.multipage.basic.ProgramBasic;
import org.multipage.generator.SlotEditorBasePanel.Callbacks;
import org.multipage.gui.FoundAttr;
import org.multipage.gui.SerializeStateAdapter;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.StateSerializer;
import org.multipage.gui.TextPopupMenuAddIn;
import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

import build_number.BuildNumber;

/**
 * Helper class for Generator application.
 * @author vakol
 *
 */
public class ProgramGenerator {
	
	/**
	 * Debug flag.
	 */
	private static final boolean debug = true;
	
	/**
	 * AreasModel.
	 */
	protected static AreasModel areasModel;
	
	/**
	 * Resource location.
	 */
	protected static String resourcesLocation = "org.multipage.generator.properties.messages";

	/**
	 * Extension to builder.
	 */
	private static ExtensionToBuilder extensionToBuilder = null;
	
	/**
	 * Extension to system of conditional events.
	 */
	private static ExtensionToBuilder extensionToDynamic = null;
	
	/**
	 * Application state serializer.
	 */
	private static StateSerializer serializer;
	
	/**
	 * Get state serializer
	 * @return
	 */
	public static StateSerializer getSerializer() {
		
		return serializer;
	}
	
	/**
	 * Initialize program basic layer.
	 * @param serializer 
	 */
	public static boolean initialize(String language, String country,
			StateSerializer serializer) {
		
		try {
			// Remember the serializer
			ProgramGenerator.serializer = serializer;
			
			// Set local identifiers.
			Resources.setLanguageAndCountry(language, country);
			
			// Load resources file.
			if (!Resources.loadResource(resourcesLocation)) {
				return false;
			}
	
			// Add state serializer.
			if (serializer != null) {
				serializer.add(new SerializeStateAdapter() {
					// On read state.
					@Override
					protected void onReadState(StateInputStream inputStream)
							throws IOException, ClassNotFoundException {
						// Serialize program dictionary.
						seriliazeData(inputStream);
					}
					// On write state.
					@Override
					protected void onWriteState(StateOutputStream outputStream)
							throws IOException {
						// Serialize program dictionary.
						serializeData(outputStream);
					}
					// On set default state.
					@Override
					protected void onSetDefaultState() {
						// Set default data.
						setDefaultData();
					}
				});
			}
	
			// Create areas model.
			areasModel = new AreasModel();
			
			// Start SWT thread for embedded browser.
			SwtBrowserCanvas.startSwtThread();
			
			return true;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Set default data.
	 */
	protected static void setDefaultData() {
		try {
			
			GeneratorMainFrame.setDefaultData();
			Settings.setDefaultData();
			GeneratorUtility.setDefaultData();
			AreaDiagramPanel.setDefaultData();
			SplitProperties.setDefaultData();
			CustomizedControls.setDefaultData();
			CustomizedColors.setDefaultData();
			OverviewControl.setDefaultData();
			SlotEditorBasePanel.setDefaultData();
			TextResourceEditor.setDefaultData();
			AreaEditorFrameBase.setDefaultData();
			ResourcesEditorDialog.setDefaultData();
			AreaPropertiesPanel.setDefaultData();
			TextRenderer.setDefaultData();
			SelectNewTextResourceDialog.setDefaultData();
			ResourceAreasDialog.setDefaultData();
			SearchDialog.setDefaultData();
			DisplayOnlineDialog.setDefaultData();
			AreaPropertiesFrame.setDefaultData();
			SlotListPanel.setDefaultData();
			AreaResourcesDialog.setDefaultData();
			SelectSubAreaDialog.setDefaultData();
			SelectVersionDialog.setDefaultData();
			SelectSuperAreaDialog.setDefaultData();
			CssMimePanel.setDefaultData();
			SpecialValueDialog.setDefaultData();
			EnumerationEditorPanel.setDefaultData();
			SelectEnumerationDialog.setDefaultData();
			SelectEnumerationFormatDialog.setDefaultData();
			ImportDialog.setDefaultData();
			SelectTransferMethodDialog.setDefaultData();
			SlotDescriptionDialog.setDefaultData();
			UserSlotInput.setDefaultData();
			SearchSlotDialog.setDefaultData();
			SlotEditorFrame.setDefaultData();
			DebugViewer.setDefaultData();
			RevisionsDialog.setDefaultData();
			ExternalProviderDialog.setDefaultData();
			RevertExternalProvidersDialog.setDefaultData();
			SlotPropertiesDialog.setDefaultData();
			PathSelectionDialog.setDefaultData();
			CreateAreasFromSourceCode.setDefaultData();
			ClonedDiagramDialog.setDefaultData();
			LoggingDialog.setDefaultData();
			LoggingSettingsDialog.setDefaultData();
			SignAddInDialog.setDefaultData();
			GenKeyDialog.setDefaultData();
			XdebugSessionDialog.setDefaultData();
			AddDebugWatchDialog.setDefaultData();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Serialize module data.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void seriliazeData(StateInputStream inputStream)
		throws IOException, ClassNotFoundException {
		
		GeneratorMainFrame.serializeData(inputStream);
		Settings.serializeData(inputStream);
		GeneratorUtility.serializeData(inputStream);
		AreaDiagramPanel.seriliazeData(inputStream);
		SplitProperties.seriliazeData(inputStream);
		CustomizedControls.seriliazeData(inputStream);
		CustomizedColors.seriliazeData(inputStream);
		OverviewControl.seriliazeData(inputStream);
		SlotEditorBasePanel.seriliazeData(inputStream);
		TextResourceEditor.seriliazeData(inputStream);
		AreaEditorFrameBase.seriliazeData(inputStream);
		ResourcesEditorDialog.seriliazeData(inputStream);
		AreaPropertiesPanel.seriliazeData(inputStream);
		TextRenderer.serializeData(inputStream);
		RenderDialog.serializeData(inputStream);
		BrowserParametersDialog.serializeData(inputStream);
		CheckRenderedFiles.serializeData(inputStream);
		ConfirmAreasConnect.serializeData(inputStream);
		AreasTreeEditorFrame.serializeData(inputStream);
		AreaHelpViewer.serializeData(inputStream);
		SelectNewTextResourceDialog.serializeData(inputStream);
		ResourceAreasDialog.serializeData(inputStream);
		SearchDialog.serializeData(inputStream);
		DisplayOnlineDialog.serializeData(inputStream);
		AreaPropertiesFrame.serializeData(inputStream);
		SlotListPanel.serializeData(inputStream);
		AreaResourcesDialog.serializeData(inputStream);
		SelectSubAreaDialog.serializeData(inputStream);
		SelectVersionDialog.serializeData(inputStream);
		SelectSuperAreaDialog.serializeData(inputStream);
		CssMimePanel.serializeData(inputStream);
		SpecialValueDialog.serializeData(inputStream);
		EnumerationEditorPanel.serializeData(inputStream);
		SelectEnumerationDialog.serializeData(inputStream);
		SelectEnumerationFormatDialog.serializeData(inputStream);
		ImportDialog.serializeData(inputStream);
		SelectTransferMethodDialog.serializeData(inputStream);
		SlotDescriptionDialog.serializeData(inputStream);
		UserSlotInput.serializeData(inputStream);
		SearchSlotDialog.serializeData(inputStream);
		SlotEditorFrame.seriliazeData(inputStream);
		DebugViewer.serializeData(inputStream);
		RevisionsDialog.serializeData(inputStream);
		ExternalProviderDialog.serializeData(inputStream);
		RevertExternalProvidersDialog.serializeData(inputStream);
		SlotPropertiesDialog.serializeData(inputStream);
		PathSelectionDialog.serializeData(inputStream);
		CreateAreasFromSourceCode.serializeData(inputStream);
		ClonedDiagramDialog.serializeData(inputStream);
		LoggingDialog.serializeData(inputStream);
		LoggingSettingsDialog.serializeData(inputStream);
		SignAddInDialog.serializeData(inputStream);
		GenKeyDialog.serializeData(inputStream);
		XdebugSessionDialog.serializeData(inputStream);
		AddDebugWatchDialog.serializeData(inputStream);
	}
	
	/**
	 * Serialize module data.
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream)
		throws IOException {
		
		GeneratorMainFrame.serializeData(outputStream);
		Settings.serializeData(outputStream);
		GeneratorUtility.serializeData(outputStream);
		AreaDiagramPanel.serializeData(outputStream);
		SplitProperties.serializeData(outputStream);
		CustomizedControls.serializeData(outputStream);
		CustomizedColors.serializeData(outputStream);
		OverviewControl.serializeData(outputStream);
		SlotEditorBasePanel.serializeData(outputStream);
		TextResourceEditor.seriliazeData(outputStream);
		AreaEditorFrameBase.seriliazeData(outputStream);
		ResourcesEditorDialog.seriliazeData(outputStream);
		AreaPropertiesPanel.seriliazeData(outputStream);
		TextRenderer.serializeData(outputStream);
		RenderDialog.serializeData(outputStream);
		BrowserParametersDialog.serializeData(outputStream);
		CheckRenderedFiles.serializeData(outputStream);
		ConfirmAreasConnect.serializeData(outputStream);
		AreasTreeEditorFrame.serializeData(outputStream);
		AreaHelpViewer.serializeData(outputStream);
		SelectNewTextResourceDialog.serializeData(outputStream);
		ResourceAreasDialog.serializeData(outputStream);
		SearchDialog.serializeData(outputStream);
		DisplayOnlineDialog.serializeData(outputStream);
		AreaPropertiesFrame.serializeData(outputStream);
		SlotListPanel.serializeData(outputStream);
		AreaResourcesDialog.serializeData(outputStream);
		SelectSubAreaDialog.serializeData(outputStream);
		SelectVersionDialog.serializeData(outputStream);
		SelectSuperAreaDialog.serializeData(outputStream);
		CssMimePanel.serializeData(outputStream);
		SpecialValueDialog.serializeData(outputStream);
		EnumerationEditorPanel.serializeData(outputStream);
		SelectEnumerationDialog.serializeData(outputStream);
		SelectEnumerationFormatDialog.serializeData(outputStream);
		ImportDialog.serializeData(outputStream);
		SelectTransferMethodDialog.serializeData(outputStream);
		SlotDescriptionDialog.serializeData(outputStream);
		UserSlotInput.serializeData(outputStream);
		SearchSlotDialog.serializeData(outputStream);
		SlotEditorFrame.seriliazeData(outputStream);
		DebugViewer.seriliazeData(outputStream);
		RevisionsDialog.serializeData(outputStream);
		ExternalProviderDialog.serializeData(outputStream);
		RevertExternalProvidersDialog.serializeData(outputStream);
		SlotPropertiesDialog.serializeData(outputStream);
		PathSelectionDialog.serializeData(outputStream);
		CreateAreasFromSourceCode.serializeData(outputStream);
		ClonedDiagramDialog.serializeData(outputStream);
		LoggingDialog.serializeData(outputStream);
		LoggingSettingsDialog.serializeData(outputStream);
		SignAddInDialog.serializeData(outputStream);
		GenKeyDialog.serializeData(outputStream);
		XdebugSessionDialog.serializeData(outputStream);
		AddDebugWatchDialog.serializeData(outputStream);
	}
	
	/**
	 * Get application title.
	 * @return
	 */
	public static String getApplicationTitle() {
		
		try {
			return String.format(Resources.getString("org.multipage.generator.textMainFrameCaption"), BuildNumber.getVersion(), 
					ProgramGenerator.class.getSuperclass().getName().equals("GeneratorFullMain") ? "Network" : "Standalone");
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Get application JAR file or its directory.
	 * @return
	 */
	public static File getApplicationFileOrDirectory() {
		
		try {
			// Get application path.
			URL applicationUrl = Utility.class.getProtectionDomain().getCodeSource().getLocation();
			String applicationPathName = applicationUrl.getPath();
			File auxiliaryFile = new File(applicationPathName);
			
			// If the path is a directory
			if (!auxiliaryFile.exists()) {
				return null;
			}
			
			if (auxiliaryFile.isDirectory()) {
				
				// To get application folder, Remove tail components of the path.
				URL generatorMainClassUrl = Utility.class.getProtectionDomain().getCodeSource().getLocation();
				String generatorMainClassName = generatorMainClassUrl.getPath();
				File generatorMainClassFile = new File(generatorMainClassName);
				generatorMainClassName = generatorMainClassFile.toString();
				String applicationRootPath = generatorMainClassName.replace(File.separator + "ProgramGenerator" + File.separator + "bin", "");
				return new File(applicationRootPath);
			}
			else if (auxiliaryFile.isFile()) {
				
				// Get application JAR file.
				String applicationJarPath = auxiliaryFile.toString();
				return new File(applicationJarPath);
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * @return the areasModel
	 */
	public static AreasModel getAreasModel() {
		return areasModel;
	}

	/**
	 * Get debug flag.
	 * @return
	 */
	public static boolean isDebug() {
		
		return debug;
	}

	/**
	 * Set extension to builder.
	 * @param extensionToBuilder the extensionToBuilder to set
	 */
	public static void setExtensionToBuilder(ExtensionToBuilder extensionToBuilder) {
		ProgramGenerator.extensionToBuilder = extensionToBuilder;
	}

	/**
	 * Set extension to dynamic.
	 * @param extensionToDynamic the extensionToBuilder to set
	 */
	public static void getExtensionToDynamic(ExtensionToBuilder extensionToDynamic) {
		
		if (!isExtensionToDynamic()) {
			ProgramGenerator.extensionToDynamic = extensionToDynamic;
		}
	}
	
	/**
	 * Returns true value, if an extension to builder exists.
	 * @return
	 */
	public static boolean isExtensionToBuilder() {
		
		return extensionToBuilder != null;
	}
	
	/**
	 * Returns true value, if an extension to dynamic exists.
	 * @return
	 */
	public static boolean isExtensionToDynamic() {
		
		return extensionToDynamic != null;
	}
	
	/**
	 * Create area editor object.
	 * @param parentComponent
	 * @param area
	 * @return
	 */
	public static AreaEditorFrameBase newAreaEditor(Component parentComponent, Area area) {
		
		try {
			if (extensionToBuilder != null) {
				return extensionToBuilder.newAreaEditor(parentComponent, area);
			}
			
			return new AreaEditorFrame(parentComponent, area);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * New areas diagram.
	 * @param areasDiagramEditor
	 * @return
	 */
	public static AreaDiagramPanel newAreasDiagram(AreaDiagramContainerPanel areasDiagramEditor) {
		
		try {
			if (extensionToBuilder != null) {
				return extensionToBuilder.newAreasDiagram(areasDiagramEditor);
			}
			
			return new AreaDiagramPanel(areasDiagramEditor);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Create new slot list panel.
	 * @return
	 */
	public static SlotListPanel newSlotListPanel() {
		
		try {
			if (extensionToBuilder != null) {
				return extensionToBuilder.newSlotListPanel();
			}
			
			return new SlotListPanel();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
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
	public static SlotEditorBasePanel newSlotEditorPanel(Window parentWindow, Slot slot,
			boolean isNew, boolean modal, boolean useHtmlEditor, FoundAttr foundAttr, Callbacks callbacks) {
		
		try {
			if (extensionToBuilder != null) {
				return extensionToBuilder.newSlotEditorPanel(parentWindow, slot, isNew, modal, useHtmlEditor, foundAttr, callbacks);
			}
			
			return new SlotEditorPanel(parentWindow, slot, isNew, modal, useHtmlEditor, foundAttr, callbacks);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Create new boolean editor object.
	 * @return
	 */
	public static BooleanEditorPanelBase newBooleanEditorPanel() {
		
		try {
			if (extensionToBuilder != null) {
				return extensionToBuilder.newBooleanEditorPanel();
			}
			
			return new BooleanEditorPanel();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Create new enumeration editor object.
	 * @return
	 */
	public static EnumerationEditorPanelBase newEnumerationEditorPanel() {
		
		try {
			if (extensionToBuilder != null) {
				return extensionToBuilder.newEnumerationEditorPanel();
			}
			
			return new EnumerationEditorPanel();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Create new area loacal trayMenu object.
	 * @param callbacks
	 * @return
	 */
	public static AreaLocalMenu newAreaLocalMenu(
			AreaLocalMenu.Callbacks callbacks) {
		
		try {
			if (extensionToBuilder != null) {
				return extensionToBuilder.newAreaLocalMenu(callbacks);
			}
			
			return new AreaLocalMenu(callbacks);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Create new area local trayMenu object for diagram.
	 * @param callbacks
	 * @return
	 */
	public static AreaLocalMenu newAreaLocalMenuForDiagram(
			AreaLocalMenu.Callbacks callbacks) {
		
		try {
			if (extensionToBuilder != null) {
				return extensionToBuilder.newAreaLocalMenuForDiagram(callbacks);
			}
			
			return new AreaLocalMenu(callbacks, AreaLocalMenu.DIAGRAM);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Create new areas properties object.
	 * @param isPropertiesPanel
	 * @return
	 */
	public static AreaPropertiesBasePanel newAreasProperties(boolean isPropertiesPanel) {
		
		try {
			if (extensionToBuilder != null) {
				return extensionToBuilder.newAreasProperties(isPropertiesPanel);
			}
			
			return new AreaPropertiesPanel(isPropertiesPanel);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Create slot text popup trayMenu.
	 * @param slot
	 * @return
	 */
	public static TextPopupMenuAddIn newGeneratorTextPopupMenuAddIn(Slot slot) {
		
		try {
			if (extensionToBuilder != null) {
				return extensionToBuilder.newGeneratorTextPopupMenuAddIn(slot);
			}
			
			return new GeneratorTextPopupMenuAddIn(slot);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Create new about dialog.
	 * @param frame
	 * @return
	 */
	public static AboutDialogBase newAboutDialog(JFrame frame) {
		
		try {
			if (extensionToBuilder != null) {
				return extensionToBuilder.newAboutDialog(frame);
			}
			
			return new AboutDialog(frame);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Get area object.
	 * @param id
	 * @return
	 */
	public static Area getArea(long id) {
		
		try {
			return areasModel.getArea(id);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Get area from area ID holder.
	 * @param areaIdHolder
	 * @return
	 */
	public static Area getArea(AreaId areaIdHolder) {
		
		try {
			long id = areaIdHolder.getId();
			return areasModel.getArea(id);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Update area.
	 * @param area
	 * @return
	 */
	public static Area updateArea(Area area) {
		
		try {
			if (area == null) {
				return null;
			}
			long id = area.getId();
			return areasModel.getArea(id);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Get home area.
	 * @return
	 */
	public static Area getHomeArea() {
		
		try {
			return areasModel.getHomeArea();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Get version.
	 * @param versionId
	 * @return
	 */
	public static VersionObj getVersion(long versionId) {
		
		try {
			return areasModel.getVersion(versionId);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Get version description.
	 * @param versionId
	 * @return
	 */
	public static String getVersionDescription(long versionId) {
		
		try {
			VersionObj version = getVersion(versionId);
			String versionName = version.getDescription();
			return versionName;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Create resource properties dialog object.
	 * @param parentComponent
	 * @param resource
	 * @return
	 */
	public static ResourcePropertiesEditorBase newResourcePropertiesEditor(
			Component parentComponent, Resource resource) {
		
		try {
			if (extensionToBuilder != null) {
				return extensionToBuilder.newResourcePropertiesEditor(parentComponent,
						resource);
			}
			
			return new ResourcePropertiesEditor(parentComponent, resource);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Create new namespace renerer object.
	 * @return
	 */
	public static NamespaceResourceRendererBase newNamespaceResourceRenderer() {
		
		try {
			if (extensionToBuilder != null) {
				return extensionToBuilder.newNamespaceResourceRenderer();
			}
			
			return new NamespaceResourceRenderer();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Create area resource renderer object.
	 * @return
	 */
	public static AreaResourceRendererBase newAreaResourceRenderer() {
		
		try {
			if (extensionToBuilder != null) {
				return extensionToBuilder.newAreaResourceRenderer();
			}
			
			return new AreaResourceRenderer();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Get external slots in input areas.
	 * @param areas
	 * @return
	 */
	public static LinkedList<Slot> getExternalSlots(LinkedList<Area> areas)
		throws Exception {
		
		LinkedList<Slot> externalSlots = new LinkedList<Slot>();
		Exception exception = null;
		
		// Get list of external providers in selected areas.
		try {
			
			Middle middle = ProgramBasic.loginMiddle();
			
			LinkedList<Slot> slots = new LinkedList<Slot>();
			for (Area area : areas) {
				
				// Load slots.
				MiddleResult result = middle.loadAreaExternalSlots(area, slots);
				result.throwPossibleException();
				
				// Append them to result list.
				externalSlots.addAll(slots);
			}
		}
		catch (Exception e) {
			exception = e;
		}
		finally {
			// Logout from middle layer.
			ProgramBasic.logoutMiddle();
		}
		
		// Throw exception.
		if (exception != null) {
			throw exception;
		}
		
		// Return result list.
		return externalSlots;
	}
	
	/**
	 * Reload areas model.
	 */
	public static MiddleResult reloadModel() {
		
		try {
			// Get model object.
			AreasModel model = ProgramGenerator.getAreasModel();
			synchronized (model) {
			
				// Get login information.
				Properties properties = ProgramBasic.getLoginProperties();
				
				// Get hidden slots flag.
				boolean loadHiddenSlots = ProgramGenerator.isExtensionToBuilder() ? true : false;
				
				// Load areas model from database.
				MiddleResult result = ProgramBasic.getMiddle().loadAreasModel(properties, model, loadHiddenSlots);
				return result;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return MiddleResult.UNKNOWN_ERROR;
	}

	/**
	 * Get all area IDs.
	 * @return
	 */
	public static HashSet<Long> getAllAreaIds() {
		
		try {
			HashSet<Long> areaIds = new HashSet<Long>();
			
			synchronized (areasModel) {
				
				for (Area area : areasModel.getAreas()) {
					
					long areaId = area.getId();
					areaIds.add(areaId);
				}
			}
			return areaIds;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Return updated list of areas.
	 * @param areas
	 * @return
	 */
	public static LinkedList<Area> getUpdatedAreas(LinkedList<Area> areas) {
		
		try {
			LinkedList<Area> updatedAreas = new LinkedList<Area>();
			
			// Update each area.
			if (areas != null) {
				for (Area area : areas) {
					
					long areaId = area.getId();
					Area updatedArea = getArea(areaId);
					
					updatedAreas.add(updatedArea);
				}
			}
			
			return updatedAreas;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Get areas with given IDs.
	 * @param areaIds
	 * @return
	 */
	public static LinkedList<Area> getAreas(HashSet<Long> areaIds) {
		
		try {
			LinkedList<Area> areas = new LinkedList<Area>();
			
			// Get list of areas with given ID.
			for (Long areaId : areaIds) {
				if (areaId != null) {
					
					Area area = getArea(areaId);
					areas.add(area);
				}
			}
			return areas;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Get areas. Areas not in current model will be removed.
	 * @param areas
	 * @return
	 */
	public static LinkedList<Area> getAreas(LinkedList<Area> areas) {
		try {
			
			LinkedList<Area> updatedAreas = new LinkedList<>();
			areas.forEach(area -> {
				area = updateArea(area);
				if (area != null) {
					updatedAreas.add(area);
				}
			});
			return updatedAreas;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Get sub area IDs.
	 * @param areaId
	 * @return
	 */
	public static HashSet<Long> getSubAreaIds(Long areaId) {
		
		if (areaId == null) {
			return null;
		}
		final HashSet<Long> areaIds = new HashSet<>();
		try {
			LinkedList<Area> areaList = areasModel.getAreaAndSubAreas(areaId);
			areaList.forEach(area -> {
				long currentAreaId = area.getId();
				areaIds.add(currentAreaId);
			});
			areaIds.remove(areaId);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return areaIds;
	}

	/**
	 * Get model identifier for debugging purposes.
	 * @return
	 */
	public static String getModelIdentifier() {
		
		try {
			if (areasModel == null) {
				return "unknown";
			}
			return areasModel.getTimeStamp();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Load resource name and type.
	 * @param resourceId
	 * @param name
	 * @param type
	 * @return
	 */
	public static MiddleResult getResourceNameType(long resourceId, Obj<String> name, Obj<String> type) {
		
		try {
			Properties login = ProgramBasic.getLoginProperties();
			Middle middle = ProgramBasic.getMiddle();
	
			MiddleResult result = middle.loadResourceName(login, resourceId,
					name, type);
			
			return result;
		}
		catch (Throwable e) {
			Safe.exception(e);
			return MiddleResult.exceptionToResult(e);
		}
	}
	
	/**
	 * Check if resource exists. 
	 * @param resourceId
	 * @return
	 */
	public static boolean existsResource(Long resourceId) {

		try {
			Obj<String> name = new Obj<>();
			Obj<String> type = new Obj<>();
			MiddleResult result = getResourceNameType(resourceId, name, type);
			if (result.isOK()) {
				return true;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Get resource name.
	 * @param resourceId
	 * @return
	 */
	public static String getResourceName(long resourceId) {

		try {
			Obj<String> name = new Obj<>();
			Obj<String> type = new Obj<>();
			MiddleResult result = getResourceNameType(resourceId, name, type);
			if (result.isNotOK()) {
				return "";
			}
			return name.ref;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Check if start resource exists. 
	 * @param resourceId
	 * @param versionId
	 * @return
	 */
	public static boolean existsStartResource(long resourceId, long versionId) {
		
		try {
			// Delegate the call.
			boolean exists = areasModel.existsStartResource(resourceId, versionId);
			return exists;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Load slot by input ID.
	 * @param slotId
	 * @return
	 */
	public static Slot getSlot(Long slotId) {
		
		Slot slot = null;
		Middle middle = null;
		MiddleResult result = MiddleResult.UNKNOWN_ERROR;
		
		try {
			Properties login = ProgramBasic.getLoginProperties();
			middle = ProgramBasic.getMiddle();
			middle.login(login);
	
			slot = new Slot();
			Obj<Boolean> found = new Obj<>(false);
			result = middle.loadSlot(slotId, slot, found);
			if (result.isNotOK() || !found.ref) {
				slot = null;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
			slot = null;
		}
		finally {
			if (middle != null) {
				middle.logout(result);
			}
		}
		return slot;
	}
	
	/**
	 * Check if slot with given input ID exists in the database.
	 * @param slotId
	 * @return
	 */
	public static boolean existsSlot(Long slotId) {

		Obj<Boolean> slotExists = new Obj<>(false);
		Middle middle = null;
		MiddleResult result = MiddleResult.UNKNOWN_ERROR;
		
		try {
			Properties login = ProgramBasic.getLoginProperties();
			middle = ProgramBasic.getMiddle();
			middle.login(login);
	
			result = middle.loadSlotExists(slotId, slotExists);
			if (result.isNotOK()) {
				slotExists.ref = false;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		finally {
			if (middle != null) {
				middle.logout(result);
			}
		}
		return slotExists.ref;
	}
}
