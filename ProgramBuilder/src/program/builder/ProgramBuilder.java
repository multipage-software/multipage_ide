/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package program.builder;

import java.awt.Component;
import java.awt.Window;
import java.io.IOException;

import javax.swing.JFrame;

import org.maclan.Area;
import org.maclan.AreasModel;
import org.maclan.Resource;
import org.maclan.Slot;
import org.maclan.VersionObj;
import org.multipage.generator.AboutDialogBase;
import org.multipage.generator.AreaDiagramContainerPanel;
import org.multipage.generator.AreaDiagramPanel;
import org.multipage.generator.AreaEditorFrameBase;
import org.multipage.generator.AreaLocalMenu;
import org.multipage.generator.AreaPropertiesBasePanel;
import org.multipage.generator.AreaResourceRendererBase;
import org.multipage.generator.BooleanEditorPanelBase;
import org.multipage.generator.EnumerationEditorPanelBase;
import org.multipage.generator.ExtensionToBuilder;
import org.multipage.generator.GeneratorMainFrame;
import org.multipage.generator.NamespaceResourceRendererBase;
import org.multipage.generator.ProgramGenerator;
import org.multipage.generator.ResourcePropertiesEditorBase;
import org.multipage.generator.SelectVersionDialog;
import org.multipage.generator.SlotEditorBasePanel;
import org.multipage.generator.SlotEditorBasePanel.Callbacks;
import org.multipage.generator.SlotEditorFrame;
import org.multipage.generator.SlotListPanel;
import org.multipage.generator.TextResourceEditorFrame;
import org.multipage.gui.FoundAttr;
import org.multipage.gui.SerializeStateAdapter;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.StateSerializer;
import org.multipage.gui.TextPopupMenuAddIn;
import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Main class for Builder package.
 * @author vakol
 *
 */
public class ProgramBuilder extends ProgramGenerator {
	
	/**
	 * Debug flag.
	 */
	private static final boolean debug = false;

	/**
	 * Resource location.
	 */
	private static final String resourcesLocation = "program.builder.properties.messages";
	
	/**
	 * Extensions to ProgramBuilderDynamic.
	 */
	private static ExtensionsToDynamic extensionsToDynamic;

	/**
	 * Initialize program basic layer.
	 * @param serializer 
	 */
	public static boolean initialize(String language, String country,
			StateSerializer serializer) {
		
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
		ExtensionsToDynamic extensions = ProgramBuilder.getExtensionsToDynamic();
		if (extensions == null) {
			areasModel = new AreasModel();
		}
		else {
			areasModel = extensions.newAreasModel();
		}
		
		
		// Set extension of program generator to program builder.
		setExtensionsToBuilder();
		
		return true;
	}

	/**
	 * Set default data.
	 */
	protected static void setDefaultData() {

		// Default main frame data.
		BuilderMainFrame.setDefaultData();
		// Default slot editor data.
		SlotEditorFrame.setDefaultData();
		// Default enumerations editor dialog state.
		EnumerationsEditorDialog.setDefaultData();
		// Default enumeration selector dialog state.
		EnumerationValueSelectionDialog.setDefaultData();
		
		SelectVersionDialog.setDefaultData();
		ConstructorsPanel.setDefaultData();
		SlotListPanel.setDefaultData();
		NewEnumerationValueDialog.setDefaultData();
		EditEnumerationValueDialog.setDefaultData();
		AreaSourceDialog.setDefaultData();
		AskConstructorHolder.setDefaultData();
	}

	/**
	 * Serialize module data.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void seriliazeData(StateInputStream inputStream)
		throws IOException, ClassNotFoundException {

		// Load main frame data.
		BuilderMainFrame.serializeData(inputStream);
		// Load slot editor data.
		SlotEditorFrame.seriliazeData(inputStream);
		// Load enumerations editor dialog state.
		EnumerationsEditorDialog.serializeData(inputStream);
		// Load enumeration selector dialog state.
		EnumerationValueSelectionDialog.serializeData(inputStream);
		
		SelectVersionDialog.serializeData(inputStream);
		ConstructorsPanel.serializeData(inputStream);
		SlotListPanel.serializeData(inputStream);
		NewEnumerationValueDialog.serializeData(inputStream);
		EditEnumerationValueDialog.serializeData(inputStream);
		AreaSourceDialog.serializeData(inputStream);
		AskConstructorHolder.serializeData(inputStream);
	}

	/**
	 * Serialize module data.
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream)
		throws IOException {

		// Save main frame data.
		BuilderMainFrame.serializeData(outputStream);
		// Save slot editor data.
		SlotEditorFrame.seriliazeData(outputStream);
		// Save enumerations editor dialog state.
		EnumerationsEditorDialog.serializeData(outputStream);
		// Save enumeration selector dialog state.
		EnumerationValueSelectionDialog.serializeData(outputStream);
		
		SelectVersionDialog.serializeData(outputStream);
		ConstructorsPanel.serializeData(outputStream);
		SlotListPanel.serializeData(outputStream);
		NewEnumerationValueDialog.serializeData(outputStream);
		EditEnumerationValueDialog.serializeData(outputStream);
		AreaSourceDialog.serializeData(outputStream);
		AskConstructorHolder.serializeData(outputStream);
	}

	/**
	 * @return the areasModel
	 */
	public static AreasModel getAreasModel() {
		return areasModel;
	}

	/**
	 * @return the extensionsToDynamic
	 */
	public static ExtensionsToDynamic getExtensionsToDynamic() {
		return extensionsToDynamic;
	}

	/**
	 * @param extensionsToDynamic the extensionsToDynamic to set
	 */
	public static void setExtensionsToDynamic(ExtensionsToDynamic extensionsToDynamic) {
		ProgramBuilder.extensionsToDynamic = extensionsToDynamic;
	}

	/**
	 * @return the debug
	 */
	public static boolean isDebug() {
		return debug;
	}
	

	/**
	 * Set extensions to builder.
	 */
	private static void setExtensionsToBuilder() {
		
		ProgramGenerator.setExtensionToBuilder(new ExtensionToBuilder() {
			
			// Create new area editor object.
			@Override
			public AreaEditorFrameBase newAreaEditor(Component parentComponent, Area area) {
				
				return new AreaEditorFrameBuilder(parentComponent, area);
			}
			
			// Create new areas diagram object.
			@Override
			public AreaDiagramPanel newAreasDiagram(
					AreaDiagramContainerPanel areasDiagramEditor) {
				
				return new AreaDiagramBuilderPanel(areasDiagramEditor);
			}

			// Create new slot list panel.
			@Override
			public SlotListPanel newSlotListPanel() {
				
				return new SlotListPanelBuilder();
			}

			// Create slot editor panel.
			@Override
			public SlotEditorBasePanel newSlotEditorPanel(Window parentWindow, Slot slot,
												          boolean isNew, boolean modal, boolean useHtmlEditor,
												          FoundAttr foundAttr, Callbacks callbacks) {
				
				return new SlotEditorBuilderPanel(parentWindow, slot, isNew, modal,
												  useHtmlEditor, foundAttr, callbacks);
			}
			
			// Create boolean editor.
			@Override
			public BooleanEditorPanelBase newBooleanEditorPanel() {
				
				return new BooleanEditorPanelBuilder();
			}

			// Create new enumeration editor base.
			@Override
			public EnumerationEditorPanelBase newEnumerationEditorPanel() {
				
				return new EnumerationEditorPanelBuilder();
			}

			// Create new area local trayMenu object.
			@Override
			public AreaLocalMenu newAreaLocalMenu(AreaLocalMenu.Callbacks callbacks) {
				
				return new AreaLocalMenuBuilder(callbacks);
			}
			
			// Create new area local trayMenu object for diagram.
			@Override
			public AreaLocalMenu newAreaLocalMenuForDiagram(AreaLocalMenu.Callbacks callbacks) {
				
				return new AreaLocalMenuBuilder(callbacks, AreaLocalMenuBuilder.DIAGRAM);
			}

			// Create areas properties object.
			@Override
			public AreaPropertiesBasePanel newAreasProperties(
					boolean isPropertiesPanel) {
				
				return new AreaPropertiesBuilderPanel(isPropertiesPanel);
			}

			/**
			 * Create new slot text popup trayMenu.
			 */
			@Override
			public TextPopupMenuAddIn newGeneratorTextPopupMenuAddIn(Slot slot) {
				
				return new BuilderTextPopupMenuAddIn(slot);
			}

			/**
			 * Create new about dialog.
			 */
			@Override
			public AboutDialogBase newAboutDialog(JFrame frame) {
				
				return new AboutDialogBuilder(frame);
			}

			/**
			 * Create new resource properties editor object.
			 */
			@Override
			public ResourcePropertiesEditorBase newResourcePropertiesEditor(
					Component parentComponent, Resource resource) {
				
				return new ResourcePropertiesEditorBuilder(parentComponent, resource);
			}

			/**
			 * Create namespace resource renderer object.
			 */
			@Override
			public NamespaceResourceRendererBase newNamespaceResourceRenderer() {
				
				return new NamespaceResourceRendererBuilder();
			}

			/**
			 * Create area resource renerer object.
			 */
			@Override
			public AreaResourceRendererBase newAreaResourceRenderer() {
				
				return new AreaResourceRendererBuilder();
			}
		});
	}
	
	/**
	 * Edit text resource.
	 * @param area
	 * @param inherits 
	 */
	public static void editTextResource(Area area, boolean inherits) {
		
		try {
			Component parentComponent = GeneratorMainFrame.getFrame();
			long versionId = 0L;
			if (inherits) {
				
				// Select version.
				Obj<VersionObj> version = new Obj<VersionObj>();
				
				if (!SelectVersionDialog.showDialog(parentComponent, version)) {
					return;
				}
				
				// Get inherited area.
				versionId = version.ref.getId();
				Area inheritedArea = ProgramGenerator.getAreasModel().getStartArea(area, versionId);
				if (inheritedArea != null) {
					area = inheritedArea;
				}
			}
			
			Resource resource = SelectAreaTextResources.showDialog(GeneratorMainFrame.getFrame(), area);
			// Edit text resource.
			if (resource != null) {
				TextResourceEditorFrame.showDialog(parentComponent, resource.getId(), inherits, area,
						false, versionId, false);
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
}
