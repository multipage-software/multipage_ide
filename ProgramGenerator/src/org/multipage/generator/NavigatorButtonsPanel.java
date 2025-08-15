/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2025-04-22
 *
 */
package org.multipage.generator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.UnexpectedException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.maclan.Area;
import org.maclan.Slot;
import org.multipage.gui.Images;
import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Dialog navigator panel with buttons.
 * @author vakol
 */
public class NavigatorButtonsPanel extends JPanel {
	
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Panel constants.
	 */
	public static final int SEPARATOR_HEIGHT 	= 25;
	public static final int BUTTON_HEIGHT 		= 25;
	public static final int SCROLL_MARGIN		= 6;
	public static final Dimension BUTTON_LARGE_SIZE = new Dimension(DialogNavigator.NAVIGATOR_LARGE_WIDTH - 2 * SCROLL_MARGIN, BUTTON_HEIGHT);
	public static final Insets BUTTON_MARGIN = new Insets(0, 0, 0, 0);
	
	// $hide>>$
	/**
	 * Template for resource ID with version ID.
	 */
	public static final String RESOURCE_VERSION_ID_TEMPLATE = "r%d,v%d";
	public static final Pattern resourceVersionRegex = Pattern.compile("^r(?<resource>\\d+)\\,v(?<version>\\d+)$");
	
	/**
	 * Button colors.
	 */
	public static final Color FOLDER_BUTTON_COLOR 			= Color.GRAY;
	public static final Color FOLDER_BUTTON_HIGLIGHT_COLOR 	= Color.RED;

	/**
	 * Lambda function that gets display group.
	 */
	public Supplier<Boolean> getDisplayGroupLambda = null;
	
	/**
	 * Button that can display or hide main application window.
	 */
	private JButton buttonMain = null;
	
	/**
	 * Area editor frames map (areaId->frame).
	 */
	private LinkedHashMap<Long, AreaEditorFrameBase> areaEditors = new LinkedHashMap<>();
	
	/**
	 * Area property frames map (areaId->frame).
	 */
	private LinkedHashMap<Long, AreaPropertiesFrame> areaProperties = new LinkedHashMap<>();
	
	/**
	 * Start resource frames map (resourceId_versionId->frame).
	 */
	private LinkedHashMap<String, TextResourceEditorFrame> startResources = new LinkedHashMap<>();
	
	/**
	 * Resource frames map (resourceId->frame).
	 */
	private LinkedHashMap<Long, TextResourceEditorFrame> resources = new LinkedHashMap<>();
	
	/**
	 * Area tree editor list.
	 */
	private LinkedList<AreasTreeEditorFrame> areaTrees = new LinkedList<>();
	
	/**
	 * Slor editors map (slotId->editor).
	 */
	private LinkedHashMap<Long, SlotEditorFrame> slotEditors = new LinkedHashMap<>();
	
	/**
	 * Map of folder windows and buttons.
	 */
	private LinkedHashMap<String, NavigatorWindow> folderWindows = new LinkedHashMap<>();
	private LinkedHashMap<String, JButton> folderButtons = new LinkedHashMap<>();
	
	/**
	 * Drag and Drop source adapter object.
	 */
	protected DragSourceListener dragSourceAdapter = null;
	
	// $hide<<$
	/**
	 * Panel components.
	 */
	private JScrollPane scrollPane;
	private JPanel panelButtons;
	private JPopupMenu popupFolder;
	private JMenuItem menuRenameFolder;
	private JMenuItem menuDeleteFolder;
	private JMenuItem menuDisplayFolderWindows;

	/**
	 * Create the panel.
	 */
	public NavigatorButtonsPanel() {
		
		try {
			initComponents();
			postCreate(); // $hide$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Add main window button.
	 */
	public void addMainButton() {
		try {
			
			if (buttonMain != null) {
				return;
			}
			String caption = Resources.getString("org.multipage.generator.textNavigatorMainWindowButton");
			String tooltip = Resources.getString("org.multipage.generator.tooltipNavigatorMainWindowButton");
			buttonMain = addNewButton(caption, tooltip, "org/multipage/generator/images/main.png", null,
						 (button, event) -> {
							 onClickMain(button, event);
						 });
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}	

	/**
	 * Initialize GUI components.
	 */
	private void initComponents() {
		setOpaque(false);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		scrollPane = new JScrollPane();
		scrollPane.setOpaque(false);
		scrollPane.setBorder(new EmptyBorder(SCROLL_MARGIN, SCROLL_MARGIN, SCROLL_MARGIN, SCROLL_MARGIN));
		add(scrollPane);
		
		panelButtons = new JPanel();
		panelButtons.setOpaque(false);
		scrollPane.setViewportView(panelButtons);
		panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.Y_AXIS));
		
		popupFolder = new JPopupMenu();
		
		menuRenameFolder = new JMenuItem("org.multipage.generator.menuRenameDialogNavigatorFolder");
		popupFolder.add(menuRenameFolder);
		menuRenameFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRenameFolder();
			}
		});
		
		menuDisplayFolderWindows = new JMenuItem("org.multipage.generator.menuRenameDialogNavigatorDisplayFolderWindows");
		popupFolder.add(menuDisplayFolderWindows);
		menuDisplayFolderWindows.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onDisplayFolderWindows();
			}
		});
		
		menuDeleteFolder = new JMenuItem("org.multipage.generator.menuDeleteDialogNavigatorFolder");
		popupFolder.add(menuDeleteFolder);
		menuDeleteFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onDeleteFolder();
			}
		});
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			localize();
			setIcons();
			setColors();
			initDragAndDrop();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Localize components.
	 */
	private void localize() {
		try {
			
			Utility.localize(menuRenameFolder);
			Utility.localize(menuDeleteFolder);
			Utility.localize(menuDisplayFolderWindows);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set component icons.
	 */
	private void setIcons() {
		try {
			
			menuRenameFolder.setIcon(Images.getIcon("org/multipage/generator/images/rename_simple.png"));
			menuDeleteFolder.setIcon(Images.getIcon("org/multipage/generator/images/remove_icon.png"));
			menuDisplayFolderWindows.setIcon(Images.getIcon("org/multipage/generator/images/display.png"));
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
			scrollPane.getViewport().setBackground(background);
			panelButtons.setBackground(background);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Initialize Drag and Drop.
	 */
	private void initDragAndDrop() {
		try {
			
			// Create Drag and Drop source listener.
			dragSourceAdapter = new DragSourceAdapter() {
				@Override
				public void dragEnter(DragSourceDragEvent dsde) {
					try {
											
						Point cursorLocation = dsde.getLocation();
						highlightFolderButton(cursorLocation);
						super.dragOver(dsde);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				@Override
				public void dragExit(DragSourceEvent dse) {
					try {
						
						highlightFolderButton(null);
						super.dragExit(dse);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				@Override
				public void dragDropEnd(DragSourceDropEvent dsde) {
					try {
						
						highlightFolderButton(null);
						super.dragDropEnd(dsde);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			};
			
			// Set button panel as a drop target.
			setDropTarget(this, (data, dropAction) -> {
				// Drop new panel item. Use Drag and Drop transfer data.
	            dropNewPanelItem(null, data, dropAction);
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Highlight folder button.
	 * @param screenLocation
	 */
	protected void highlightFolderButton(Point screenLocation) {
		try {
			
			folderButtons.forEach((name, button) -> {
				
				if (screenLocation == null) {
					button.setBackground(FOLDER_BUTTON_COLOR);
					return;
				}
				
				Point buttonLocation = button.getLocationOnScreen();
				int buttonWidth = button.getWidth();
				int buttonHeight = button.getHeight();
				Rectangle buttonRectangle = new Rectangle(buttonLocation.x, buttonLocation.y, buttonWidth, buttonHeight);
				
				Color color = buttonRectangle.contains(screenLocation) ? FOLDER_BUTTON_HIGLIGHT_COLOR : FOLDER_BUTTON_COLOR;
				button.setBackground(color);
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Add new navigator folder. 
	 */
	protected void onAddNavigatorFolder() {
		try {
			
			// Get folder name.
			String folderName = Utility.inputCenter(this, "", "org.multipage.generator.messageInsertNavigatorFolderName");
			if (folderName == null) {
				return;
			}
			
			// Check if folder with the same name already exists.
			boolean exists = folderWindows.containsKey(folderName);
			if (exists) {
				Utility.showCenter(this, "org.multipage.generator.messageNavigatorFolderAlreadyExists", folderName);
				return;
			}
			
			// Add empty folder.
			NavigatorWindow folderWindow = NavigatorWindow.createInstance(this);
			folderWindows.put(folderName, folderWindow);
			reloadButtons();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Add the area editor frame.
	 * @param editor
	 * @param checkExists
	 */
	public void addAreaEditor(AreaEditorFrameBase editor, boolean checkExists)
			throws Exception {
		
		try {			
			Area area = editor.getArea();
			long areaId = area.getId();
			
			// If the editor is already in the map throw exception.
			boolean exists = areaEditors.containsKey(areaId);
			if (exists) {
				if (checkExists) {
					Utility.throwException("org.multipage.generator.messageAreaEditorAlreadyOpened", areaId);
				}
				return;
			}
			
			areaEditors.put(areaId, editor);
			reloadButtons();
		}
		catch (Throwable e) {
			throw e;
		}
	}
	
	/**
	 * Get existing area editor.
	 * @param area
	 * @return
	 */
	public AreaEditorFrameBase getAreaEditor(Area area) {
		
		try {
			long areaId = area.getId();
			AreaEditorFrameBase editor = areaEditors.get(areaId);
			if (editor instanceof AreaEditorFrameBase) {
				return (AreaEditorFrameBase) editor;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Remove area editor.
	 * @param area
	 */
	public void removeAreaEditor(Area area) {
		try {

			long areaId = area.getId();
			areaEditors.remove(areaId);
			reloadButtons();
			
			folderWindows.forEach((folderName, folderWindow) -> {
				try {
					NavigatorButtonsPanel buttonPanel = folderWindow.getButtonPanel();
					buttonPanel.removeAreaEditor(area);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Add the area properties frame.
	 * @param editor
	 * @param checkExists
	 */
	public void addAreaProperties(AreaPropertiesFrame editor, boolean checkExists)
			throws Exception {
		
		try {
			// Add editor to the map.
			Area area = editor.getArea();
			long areaId = area.getId();
			
			// If the editor is already in the map throw exception.
			boolean exists = areaProperties.containsKey(areaId);
			if (exists) {
				if (checkExists) {
					Utility.throwException("org.multipage.generator.messageAreaPropertiesAlreadyOpened", areaId);
				}
				return;
			}
			
			areaProperties.put(areaId, editor);
			reloadButtons();
		}
		catch (Throwable e) {
			throw e;
		}
	}
	
	/**
	 * Get existing area properties frame.
	 * @param area
	 * @return
	 */
	public AreaPropertiesFrame getAreaProperties(Area area) {

		try {
			long areaId = area.getId();
			AreaPropertiesFrame editor = areaProperties.get(areaId);
			return editor;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Remove area properties.
	 * @param area
	 */
	public void removeAreaProperties(Area area) {
		try {

			long areaId = area.getId();
			areaProperties.remove(areaId);
			reloadButtons();
			
			folderWindows.forEach((folderName, folderWindow) -> {
				try {
					NavigatorButtonsPanel buttonPanel = folderWindow.getButtonPanel();
					buttonPanel.removeAreaProperties(area);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Add start resource editor frame.
	 * @param resourceVersionId
	 * @param editor
	 * @param checkExists
	 */
	private void addStartResourceEditor(String resourceVersionId, TextResourceEditorFrame editor, boolean checkExists)
			throws Exception {
		
		try {
			// If the editor is already in the map throw exception.
			boolean exists = startResources.containsKey(resourceVersionId);
			if (exists) {
				if (checkExists) {
					Obj<Long> resourceId = new Obj<>(null);
					Obj<Long> versionId = new Obj<>(null);
					parseResourceVersionId(resourceVersionId, resourceId, versionId);
					Utility.throwException("org.multipage.generator.messageStartResourceAlreadyOpened",
											resourceId.ref, versionId.ref);
				}
				return;
			}
			
			startResources.put(resourceVersionId, editor);
			reloadButtons();
		}
		catch (Throwable e) {
			throw e;
		}
	}
	
	/**
	 * Add start resource editor frame.
	 * @param versionId
	 * @param editor
	 * @param checkExists
	 */
	public void addStartResourceEditor(long versionId, TextResourceEditorFrame editor, boolean checkExists)
			throws Exception {
		
		try {
			// Add editor to the map.
			long resourceId = editor.getResourceId();
			String resourceVersionId = String.format(RESOURCE_VERSION_ID_TEMPLATE, resourceId, versionId);
			
			// Delegate call.
			addStartResourceEditor(resourceVersionId, editor, checkExists);
		}
		catch (Throwable e) {
			throw e;
		}
	}
	
	/**
	 * Get start resource frame.
	 * @param resourceId
	 * @param versionId
	 * @return
	 */
	public TextResourceEditorFrame getStartResourceEditor(long resourceId, long versionId) {

		try {
			String resourceVersionId = String.format(RESOURCE_VERSION_ID_TEMPLATE, resourceId, versionId);
			TextResourceEditorFrame editor = startResources.get(resourceVersionId);
			return editor;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Remove start resource editor.
	 * @param resourceId
	 * @param versionId
	 */
	public void removeStartResourceEditor(long resourceId, long versionId) {
		try {
			
			String resourceVersionId = String.format(RESOURCE_VERSION_ID_TEMPLATE, resourceId, versionId);
			startResources.remove(resourceVersionId);
			reloadButtons();
			
			folderWindows.forEach((folderName, folderWindow) -> {
				try {
					NavigatorButtonsPanel buttonPanel = folderWindow.getButtonPanel();
					buttonPanel.removeStartResourceEditor(resourceId, versionId);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get resource ID and version ID from the input string.
	 * @param respourceVersionIds
	 * @param resourceId
	 * @param versionId
	 * @return
	 */
	public static void parseResourceVersionId(String respourceVersionIds, Obj<Long> resourceId, Obj<Long> versionId)
			throws Exception {
		
		try {
			// Match the input string against the regular expression.
			Matcher matcher = resourceVersionRegex.matcher(respourceVersionIds);
			boolean success = matcher.find();
			if (success) {
				
				// Try to get resource ID.
				if (resourceId != null) {
					resourceId.ref = null;
					String resourceIdString = matcher.group("resource");
					if (resourceIdString != null) {
						resourceId.ref = Long.parseLong(resourceIdString);
					}
					else {
						throw new UnexpectedException("");
					}
				}
				
				// Try to get version ID.
				if (versionId != null) {
					versionId.ref = null;
					String versionIdString = matcher.group("version");
					if (versionIdString != null) {
						versionId.ref = Long.parseLong(versionIdString);		
					}
					else {
						throw new UnexpectedException("");
					}
				}
				return;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		Utility.throwException("org.multipage.generator.messageCannotGetStartResourceAndVersionId");
	}
	
	/**
	 * Add resource editor frame.
	 * @param editor
	 * @param checkExists
	 */
	public void addResourceEditor(TextResourceEditorFrame editor, boolean checkExists)
			throws Exception {
		
		try {
			// Add editor to the map.
			long resourceId = editor.getResourceId();
			
			// If the editor is already in the map throw exception.
			boolean exists = resources.containsKey(resourceId);
			if (exists) {
				if (checkExists) {
					Utility.throwException("org.multipage.generator.messageResourceAlreadyOpened",
											resourceId);
				}
				return;
			}
			
			resources.put(resourceId, editor);
			reloadButtons();
		}
		catch (Throwable e) {
			throw e;
		}
	}
	
	/**
	 * Get resource frame.
	 * @param resourceId
	 * @return
	 */
	public TextResourceEditorFrame getResourceEditor(long resourceId) {
		
		try {
			TextResourceEditorFrame editor = resources.get(resourceId);
			return editor;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Remove resource editor.
	 * @param resourceId
	 */
	public void removeResourceEditor(long resourceId) {
		try {

			resources.remove(resourceId);
			reloadButtons();
			
			folderWindows.forEach((folderName, folderWindow) -> {
				try {
					NavigatorButtonsPanel buttonPanel = folderWindow.getButtonPanel();
					buttonPanel.removeResourceEditor(resourceId);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Add area tree editor frame.
	 * @param editor
	 * @param checkExists
	 */
	public void addAreaTreeEditor(AreasTreeEditorFrame editor, boolean checkExists)
			throws Exception {
		
		try {
			// Check if the input editor already exists in the editor list.
			boolean exists = areaTrees.contains(editor);
			if (exists) {
				if (checkExists) {
					Utility.throwException("org.multipage.generator.messageDialogNavigatorAreaTreeEditorAlreadyExists");
				}
				return;
			}
			// Add editor to the list.
			areaTrees.add(editor);
			reloadButtons();
		}
		catch (Throwable e) {
			Safe.exception(e);
			throw e;
		}
	}
	
	/**
	 * Remove area tree editor.
	 * @param editor
	 */
	public void removeAreaTreeEditor(AreasTreeEditorFrame editor) {
		try {
			
			areaTrees.remove(editor);
			reloadButtons();
			
			folderWindows.forEach((folderName, folderWindow) -> {
				try {
					NavigatorButtonsPanel buttonPanel = folderWindow.getButtonPanel();
					buttonPanel.removeAreaTreeEditor(editor);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Add slot editor.
	 * @param slotId
	 * @param editor
	 * @param checkExists
	 */
	public void addSlotEditor(Long slotId, SlotEditorFrame editor, boolean checkExists)
			throws Exception {
		
		try {
			// Add editor to the map.
			// If the editor is already in the map throw exception.
			boolean exists = slotEditors.containsKey(slotId);
			if (exists) {
				if (checkExists) {
					Utility.throwException("org.multipage.generator.messageSlotEditorAlreadyOpened",
											slotId);
				}
				return;
			}
			
			slotEditors.put(slotId, editor);
			reloadButtons();
		}
		catch (Throwable e) {
			throw e;
		}
	}
	
	/**
	 * Add slot editor.
	 * @param editor
	 * @param checkExists
	 */
	public void addSlotEditor(SlotEditorFrame editor, boolean checkExists)
			throws Exception {
		
		try {
			Long slotId = editor.getSlotId();
			if (slotId == null || slotId == 0L) {
				return;
			}
			// Delegate the call.
			addSlotEditor(slotId, editor, checkExists);
		}
		catch (Throwable e) {
			throw e;
		}
	}
	
	/**
	 * Get slot editor..
	 * @param slotId
	 * @return
	 */
	public SlotEditorFrame getSlotEditor(long slotId) {
		
		try {
			SlotEditorFrame editor = slotEditors.get(slotId);
			return editor;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Change slot editor ID.
	 * @param slotId
	 * @param newSlotId
	 */
	public void changeSlotEditorId(Long slotId, Long newSlotId) {
		try {
			
			SlotEditorFrame editor = slotEditors.get(slotId);
			slotEditors.remove(slotId);
			slotEditors.put(newSlotId, editor);
			reloadButtons();
			
			folderWindows.forEach((folderName, folderWindow) -> {
				try {
					NavigatorButtonsPanel buttonPanel = folderWindow.getButtonPanel();
					buttonPanel.changeSlotEditorId(slotId, newSlotId);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Remove slot editor.
	 * @param slotId
	 */
	public void removeSlotEditor(long slotId) {
		try {
		
			slotEditors.remove(slotId);
			reloadButtons();
			
			folderWindows.forEach((folderName, folderWindow) -> {
				try {
					NavigatorButtonsPanel buttonPanel = folderWindow.getButtonPanel();
					buttonPanel.removeSlotEditor(slotId);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Reload navigator buttons.
	 */
	public void reloadButtons() {
		Safe.invokeLater(() -> {
			
			// Clear current buttons.
			panelButtons.removeAll();
			
			// Add main button if it exists.
			if (buttonMain != null) {
				panelButtons.add(buttonMain);
			}
			
			// Add area tree editor buttons.
			// Add separator.
			if (!folderWindows.isEmpty()) {
				addNewSeparator("org.multipage.generator.textNavigatorForFolders");
			}
			folderButtons.clear();
			folderWindows.forEach((folderName, window) -> {
				try {
					
					// Add new folder button.
					JButton button = addNewFolderButton(folderName, "org/multipage/generator/images/folder.png",
							(newButtonClick, event) -> {
								onClickFolder(newButtonClick, event);
							});
					if (button != null) {
						folderButtons.put(folderName, button);
					}
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// Add area editor buttons.
			// Add separator.
			if (!areaEditors.isEmpty()) {
				addNewSeparator("org.multipage.generator.textNavigatorForAreaEditor");
			}
			areaEditors.forEach((areaId, editor) -> {
				try {
					
					NavigatorTransferable transferable = new NavigatorTransferable(areaId, editor, this);
					addNewAreaButton(areaId, "org/multipage/generator/images/area_node.png",
							transferable,
							() -> {
								onClickAreaEditor(areaId);
							});
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// Add area properties buttons.
			// Add separator.
			if (!areaProperties.isEmpty()) {
				addNewSeparator("org.multipage.generator.textNavigatorForAreaProperties");
			}
			areaProperties.forEach((areaId, editor) -> {
				try {
					
					NavigatorTransferable transferable = new NavigatorTransferable(areaId, editor, this);
					addNewAreaButton(areaId, "org/multipage/generator/images/list.png",
							transferable,
							() -> {
								onClickAreaProperties(areaId);
							});
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// Add start resource editor buttons.
			// Add separator.
			if (!startResources.isEmpty()) {
				addNewSeparator("org.multipage.generator.textNavigatorForStartResource");
			}
			startResources.forEach((resourceVersionId, editor) -> {
				try {
					
					NavigatorTransferable transferable = new NavigatorTransferable(resourceVersionId, editor, this);
					addNewStartResourceButton(resourceVersionId, "org/multipage/generator/images/start_resource.png",
							transferable,
							() -> {
								onClickStartResource(resourceVersionId);
							});
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// Add resource editor buttons.
			// Add separator.
			if (!resources.isEmpty()) {
				addNewSeparator("org.multipage.generator.textNavigatorForResource");
			}
			resources.forEach((resourceId, editor) -> {
				try {
					
					NavigatorTransferable transferable = new NavigatorTransferable(resourceId, editor, this);
					addNewResourceButton(resourceId, "org/multipage/generator/images/resource.png",
							transferable,
							() -> {
								onClickResource(resourceId);
							});
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// Add area tree editor buttons.
			// Add separator.
			if (!areaTrees.isEmpty()) {
				addNewSeparator("org.multipage.generator.textNavigatorForAreaTree");
			}
			areaTrees.forEach(editor -> {
				try {
					
					long areaId = editor.getAreaId();
					NavigatorTransferable transferable = new NavigatorTransferable(areaId, editor, this);
					addNewAreaButton(areaId, "org/multipage/generator/images/area_trace.png",
							transferable,
							() -> {
								onClickAreaTree(editor);
							});
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// Add slot editor buttons.
			// Add separator.
			if (!slotEditors.isEmpty()) {
				addNewSeparator("org.multipage.generator.textNavigatorForSlotEditor");
			}
			slotEditors.forEach((slotId, editor) -> {
				try {
					
					NavigatorTransferable transferable = new NavigatorTransferable(slotId, editor, this);
					addNewSlotButton(slotId, "org/multipage/generator/images/slot_icon.png",
							transferable,
							() -> {
								onClickSlotEditor(slotId);
							});
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// Update navigator components.
			SwingUtilities.updateComponentTreeUI(this);
		});
	}
	
	/**
	 * Update buttons. Check if each editor source data still exist.
	 */
	public void updateButtons() {
		try {
			
			// Remove items not in the Area Model.
			HashSet<Long> idsToRemove = new HashSet<>();
			areaEditors.forEach((areaId, editor) -> {
				try {
					
					Area area = ProgramGenerator.getArea(areaId);
					if (area == null) {
						idsToRemove.add(areaId);
					}
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			idsToRemove.forEach(areaId -> {
				areaEditors.remove(areaId);
			});
			
			// Remove items not in the Area Model.
			idsToRemove.clear();
			areaProperties.forEach((areaId, editor) -> {
				try {
					
					Area area = ProgramGenerator.getArea(areaId);
					if (area == null) {
						idsToRemove.add(areaId);
					}
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			idsToRemove.forEach(areaId -> {
				areaProperties.remove(areaId);
			});
			
			// Remove items not in the Area Model.
			HashSet<String> keysToRemove = new HashSet<>();
			startResources.forEach((resourceVersionId, editor) -> {
				try {
					
					Obj<Long> resourceId = new Obj<>();
					Obj<Long> versionId = new Obj<>();
					parseResourceVersionId(resourceVersionId, resourceId, versionId);
					boolean exists = ProgramGenerator.existsStartResource(resourceId.ref, versionId.ref);
					if (!exists) {
						keysToRemove.add(resourceVersionId);
					}
				}
				catch (Throwable e) {
					Safe.exception(e);
				}
			});
			keysToRemove.forEach(key -> {
				startResources.remove(key);
			});
			
			// Remove items not in the database.
			idsToRemove.clear();
			resources.forEach((resourceId, editor) -> {
				try {
					
					boolean exists = ProgramGenerator.existsResource(resourceId);
					if (!exists) {
						idsToRemove.add(resourceId);
					}
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			idsToRemove.forEach(resourceId -> {
				resources.remove(resourceId);
			});
			
			// Remove items not in the Area Model.
			HashSet<AreasTreeEditorFrame> editorsToRemove = new HashSet<>();
			areaTrees.forEach(editor -> {
				try {
					
					long areaId = editor.getAreaId();
					Area area = ProgramGenerator.getArea(areaId);
					if (area == null) {
						editorsToRemove.add(editor);
					}
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			areaTrees.removeAll(editorsToRemove);
			
			// Remove items not in the database.
			idsToRemove.clear();
			slotEditors.forEach((slotId, editor) -> {
				try {
					
					boolean exists = ProgramGenerator.existsSlot(slotId);
					if (!exists) {
						idsToRemove.add(slotId);
					}
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			idsToRemove.forEach(slotId -> {
				slotEditors.remove(slotId);
			});
			
			// Finally reload existing buttons.
			reloadButtons();
			
			// Update buttons in all folders.
			folderWindows.forEach((folder, window) -> {
				try {
					
					NavigatorButtonsPanel buttonsPanel = window.getButtonPanel();
					buttonsPanel.updateButtons();
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On click main window button.
	 * @param button
	 * @param event
	 */
	private void onClickMain(JButton button, ActionEvent event) {
		try {
			
			GeneratorMainFrame frame = GeneratorMainFrame.getFrame();
			Utility.toggleFrame(frame);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Add button separator.
	 * @param captionId 
	 */
	private void addNewSeparator(String captionId) {
		try {
			
			Color color = CustomizedColors.get(ColorId.DIALOG_NAVIGATOR);
			String caption = Resources.getString(captionId);
			JLabel label = new JLabel(caption);
			
			label.setMaximumSize(new Dimension(32767, SEPARATOR_HEIGHT));
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setForeground(Color.WHITE);
			label.setBackground(color);
			label.setOpaque(true);
			
			panelButtons.add(label);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Add new button.
	 * @param caption
	 * @param tooltip
	 * @param icon
	 * @param transferable
	 * @param onClickLambda
	 */
	private JButton addNewButton(String caption, String tooltip, String icon,
								 NavigatorTransferable transferable,
								 BiConsumer<JButton, ActionEvent> onClickLambda) {
		
		try {
			JButton button = new JButton(caption);
			button.setMaximumSize(BUTTON_LARGE_SIZE);
	        button.setMinimumSize(BUTTON_LARGE_SIZE);
	        button.setPreferredSize(BUTTON_LARGE_SIZE);
	        button.setMargin(BUTTON_MARGIN);
	        button.setHorizontalAlignment(SwingConstants.CENTER);
	        button.setIcon(Images.getIcon(icon));
	        button.setToolTipText(tooltip);
	        button.addActionListener(e -> {
	        	onClickLambda.accept(button, e);
	        });
	        panelButtons.add(button);
	        
	        // Set drag and drop source.
	        if (transferable != null) {
		        DragSource ds = DragSource.getDefaultDragSource();
		        ds.createDefaultDragGestureRecognizer(button, DnDConstants.ACTION_COPY_OR_MOVE, new DragGestureListener() {
		        	@Override
		            public void dragGestureRecognized(DragGestureEvent dge) {
		        		try {
							
			                ds.startDrag(dge, null, transferable, dragSourceAdapter);
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
		            }
		        });
	        }
	        return button;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Create new button.
	 * @param areaId
	 * @param icon
	 * @param transferable
	 * @param onClickLambda
	 * @return
	 */
	private JButton addNewAreaButton(Long areaId, String icon, NavigatorTransferable transferable,
								     Runnable onClickLambda) {
		try {
			Area area = ProgramGenerator.getArea(areaId);
			String caption = area.getDescription();
			String tooltip = area.getDescriptionForced(true);
			
			JButton button = addNewButton(caption, tooltip, icon, transferable, (btn, e) -> onClickLambda.run());
			return button;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Create new button.
	 * @param slotId
	 * @param icon
	 * @param transferable
	 * @param onClickLambda
	 */
	private JButton addNewSlotButton(Long slotId, String icon, NavigatorTransferable transferable,
								  Runnable onClickLambda) {
		try {
			Slot slot = ProgramGenerator.getSlot(slotId);
			String caption = slot.getAliasWithId();
			String tooltip = slot.getAliasWithName();
			
			JButton button = addNewButton(caption, tooltip, icon, transferable, (btn, e) -> onClickLambda.run());
			return button;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Create new button.
	 * @param resourceVersionId
	 * @param icon
	 * @param transferable
	 * @param onClickLambda
	 */
	private JButton addNewStartResourceButton(String resourceVersionId, String icon,
			NavigatorTransferable transferable, Runnable onClickLambda) {

		try {
			// Get editor object.
			TextResourceEditorFrame editor = startResources.get(resourceVersionId);
			if (editor == null) {
				return null;
			}
			
			// Resource, version name and area description.
			long resourceId = editor.getResourceId();
			String resourceName = ProgramGenerator.getResourceName(resourceId);
			
			long versionId = editor.getVersionId();
			String versionName = ProgramGenerator.getVersionDescription(versionId);
			
			String areaDescription = editor.getAreaDescription();
			
			String caption = String.format("%s (%s)", resourceName, versionName);
			String tooltipFormat = Resources.getString("org.multipage.generator.tooltipStartResourceNavigator");
			String tooltip = String.format(tooltipFormat, resourceName, versionName, areaDescription);
			
			// Create new button.
			JButton button = addNewButton(caption, tooltip, icon, transferable, (btn, e) -> onClickLambda.run());
			return button;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Create new button.
	 * @param resourceVersionId
	 * @param icon
	 * @param transferable
	 * @param onClickLambda
	 */
	private JButton addNewResourceButton(long resourceId, String icon,
			NavigatorTransferable transferable, Runnable onClickLambda) {
		
		try {
			// Get editor object.
			TextResourceEditorFrame editor = resources.get(resourceId);
			if (editor == null) {
				return null;
			}
			
			// Resource and area description.
			String resourceName = ProgramGenerator.getResourceName(resourceId);
			String areaDescription = editor.getAreaDescription();
			
			String caption = String.format("%s (%s)", resourceName, areaDescription);
			String tooltipFormat = Resources.getString("org.multipage.generator.tooltipResourceNavigator");
			String tooltip = String.format(tooltipFormat, resourceName, areaDescription);
			
			// Create new button.
			JButton button = addNewButton(caption, tooltip, icon, transferable, (btn, e) -> onClickLambda.run());
			return button;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Set Drag and Drop target component.
	 * @param targetComponent
	 * @param dropLambda
	 */
	public void setDropTarget(Component targetComponent, BiConsumer<NavigatorTransferableData, Integer> dropLambda) {
		try {
			
			new DropTarget(targetComponent, new DropTargetAdapter() {
				@Override
				public void drop(DropTargetDropEvent dtde) {
					try {
						Transferable tr = dtde.getTransferable();
						if (tr.isDataFlavorSupported(NavigatorTransferable.dataFlavor)) {
							dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
							int dropAction = dtde.getDropAction();
							NavigatorTransferableData data = (NavigatorTransferableData) tr.getTransferData(NavigatorTransferable.dataFlavor);
	                        dtde.dropComplete(true);
	                        
	                        // Invoke callback lambda function.
	                        dropLambda.accept(data, dropAction);
						}
					}
					catch (Throwable e) {
						Safe.exception(e);
						dtde.dropComplete(false);
					}
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Create new folder button.
	 * @param folderName
	 * @param icon
	 * @param onClickLambda
	 */
	private JButton addNewFolderButton(String folderName, String icon, BiConsumer<JButton, ActionEvent> onClickLambda) {
		
		try {
			String tooltipFormat = Resources.getString("org.multipage.generator.tooltipNavigatorFolder");
			String tooltip = String.format(tooltipFormat, folderName);
			
			JButton button = addNewButton(folderName, tooltip, icon, null, onClickLambda);
			button.setBackground(FOLDER_BUTTON_COLOR);
			
			// Set button popup menu.
			addPopup(button, popupFolder);
			
			// Set button as a Drag and Drop target.
			setDropTarget(button, (data, dropAction) -> {
				// Drop new folder item. Use Drag and Drop transfer data.
                dropNewPanelItem(folderName, data, dropAction);
			});
			
			// Set folder window location.
			NavigatorWindow folderWindow = folderWindows.get(folderName);
			folderWindow.addCaption(folderName);
			
			Dimension screenSize = Utility.getScreenSize();
			int screenWidth = screenSize.width;
			
			int x = screenWidth - 2 * DialogNavigator.NAVIGATOR_LARGE_WIDTH;
			int y = 0;
			int width = DialogNavigator.NAVIGATOR_LARGE_WIDTH;
			int height = screenSize.height;
			
			folderWindow.setBounds(x, y, width, height);
			
			return button;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Add popup menu to the input component.
	 * @param component
	 * @param popup
	 */
	private void addPopup(Component component, final JPopupMenu popup) {
		
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				try {
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			}
		});
	}
	
	/**
	 * On renaming folder.
	 */
	protected void onRenameFolder() {
		
		try {
			// Get folder name.
			Component invokerComponent = popupFolder.getInvoker();
			if (!(invokerComponent instanceof JButton)) {
				return;
			}
			JButton folderButton = (JButton) invokerComponent;
			String folderName = folderButton.getText();
			
			// Hide all windows.
			hideAllFrames();
			
			// Ask user for new folder name.
			String newFolderName = null;
			newFolderName = Utility.inputCenter(this, folderName, "org.multipage.generator.messageEnterDialogNavigatorFolderName", folderName);
			if (newFolderName == null || folderName.equals(newFolderName)) {
				return;
			}
			
			// Check if new folder name already exists.
			NavigatorWindow existingWindow = folderWindows.get(newFolderName);
			if (existingWindow != null) {
				Utility.showCenter(this, "org.multipage.generator.messageDialogNavigatorFolderNameAlreadyExists", newFolderName);
				return;
			}
			
			// Change folder components name.
			folderButton.setText(newFolderName);
			NavigatorWindow navigatorWindow = folderWindows.get(folderName);
			if (navigatorWindow == null) {
				throw new UnexpectedException("org.multipage.generator.messageNullDialogNavigatorFolderWindow");
			}
			navigatorWindow.setFolderName(newFolderName);
			folderWindows.put(newFolderName, navigatorWindow);
			folderWindows.remove(folderName);
			folderButtons.put(newFolderName, folderButton);
			folderButtons.remove(folderName);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Display folder windows.
	 */
	protected void onDisplayFolderWindows() {
		try {
			
			// Get folder name.
			Component invokerComponent = popupFolder.getInvoker();
			if (!(invokerComponent instanceof JButton)) {
				return;
			}
			JButton folderButton = (JButton) invokerComponent;
			String folderName = folderButton.getText();
			
			displayAllWindows(false);
			
			folderWindows.forEach((folder, window) -> {
				try {
					window.getButtonPanel().displayAllWindows(false);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			folderWindows.forEach((folder, window) -> {
				try {
					
					if (folder == folderName) {
						window.getButtonPanel().displayAllWindows(true);
					}
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Display all button windows.
	 * @param visible
	 */
	public void displayAllWindows(boolean visible) {
		try {
			
			// Show / hide main window.
			if (buttonMain != null) {
				GeneratorMainFrame frame = GeneratorMainFrame.getFrame();
				Utility.displayFrame(frame, visible);
			}
			
			// Show / hide area editors.
			areaEditors.forEach((areaId, editor) -> {
				try {
					JFrame frame = editor.getFrame();
					if (frame == null) {
						return;
					}
					Utility.displayFrame(frame, visible);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// Show / hide area property editors.
			areaProperties.forEach((areaId, editor) -> {
				try {
					Utility.displayFrame(editor, visible);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// Show / hide start resource editors.
			startResources.forEach((resourceVersionId, editor) -> {
				try {
					Utility.displayFrame(editor, visible);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// Show / hide resource editors.
			resources.forEach((resourceId, editor) -> {
				try {
					Utility.displayFrame(editor, visible);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// Show / hide area tree editors.
			areaTrees.forEach(editor -> {
				try {
					Utility.displayFrame(editor, visible);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// Show / hide slot editors.
			slotEditors.forEach((slotId, editor) -> {
				try {
					Utility.displayFrame(editor, visible);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Hide all opened frames.
	 */
	public void hideAllFrames() {
		try {
			
			displayAllWindows(false);
			
			// Hide folder frames.
			folderWindows.forEach((name, window) -> {
				window.getButtonPanel().displayAllWindows(false);
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On deleting folder.
	 */
	protected void onDeleteFolder() {
		
		try {
			// Get folder name.
			Component invokerComponent = popupFolder.getInvoker();
			if (!(invokerComponent instanceof JButton)) {
				return;
			}
			JButton folderButton = (JButton) invokerComponent;
			String folderName = folderButton.getText();
			
			// Ask user.
			boolean success = Utility.askCenter(this, "org.multipage.generator.messageDialogNavigatorDeleteFolder", folderName);
			if (!success) {
				return;
			}
			
			// Move all folder buttons to the main window.
			NavigatorWindow folderWindow = folderWindows.get(folderName);
			if (folderWindow == null) {
				throw new UnexpectedException("org.multipage.generator.messageNullDialogNavigatorFolderWindow");
			}
			
			// Move buttons to the main window.
			NavigatorButtonsPanel folderButtonPanel = folderWindow.getButtonPanel();
			folderButtonPanel.moveButtonsTo(this);
			
			// Remove folder.
			folderButtons.remove(folderName);
			folderWindows.remove(folderName);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Move buttons to the destination panel.
	 * @param destinationButtonsPanel
	 */
	private void moveButtonsTo(NavigatorButtonsPanel destinationButtonsPanel) {
		try {
			
			// Move buttons for area editors.
			areaEditors.forEach((areaId, editor) -> {
				try {
					destinationButtonsPanel.addAreaEditor(editor, false);
				}
				catch (Throwable e) {
					Safe.exception(e);
				}
			});
			areaEditors.clear();
			
			// Move buttons for area property editors.
			areaProperties.forEach((areaId, editor) -> {
				try {
					destinationButtonsPanel.addAreaProperties(editor, false);
				}
				catch (Throwable e) {
					Safe.exception(e);
				}
			});
			areaProperties.clear();
			
			// Move buttons for start resource editors.
			startResources.forEach((resourceVersionId, editor) -> {
				try {
					destinationButtonsPanel.addStartResourceEditor(resourceVersionId, editor, false);
				}
				catch (Throwable e) {
					Safe.exception(e);
				}
			});
			startResources.clear();
			
			// Move buttons for resource editors.
			resources.forEach((resourceId, editor) -> {
				try {
					destinationButtonsPanel.addResourceEditor(editor, false);
				}
				catch (Throwable e) {
					Safe.exception(e);
				}
			});
			resources.clear();
			
			// Move buttons for area tree editors.
			areaTrees.forEach(editor -> {
				try {
					destinationButtonsPanel.addAreaTreeEditor(editor, false);
				}
				catch (Throwable e) {
					Safe.exception(e);
				}
			});
			areaTrees.clear();
			
			// Move buttons for slot editors.
			slotEditors.forEach((slotId, editor) -> {
				try {
					destinationButtonsPanel.addSlotEditor(slotId, editor, false);
				}
				catch (Throwable e) {
					Safe.exception(e);
				}
			});
			slotEditors.clear();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Drop new button panel item.
	 * @param folderName - This value must be null when dropping to main button panel.
	 * @param data
	 * @param dropAction 
	 */
	public void dropNewPanelItem(String folderName, NavigatorTransferableData data, int dropAction) {
		
		try {
			NavigatorButtonsPanel targetButtonPanel = null;
			if (folderName != null) {
				// Get navigator window for inpyut folder name.
				NavigatorWindow navigator = folderWindows.get(folderName);
				if (navigator == null) {
					return;
				}
				navigator.setVisible(true);
				targetButtonPanel = navigator.getButtonPanel();
			}
			else {
				targetButtonPanel = this;
			}
			
			// Get button panel. If the source and target panels are the same, do noting.
			NavigatorButtonsPanel sourceButtonPanel = data.getButtonPanel();
			if (targetButtonPanel.equals(sourceButtonPanel)) {
				return;
			}
			int type = data.getType();
			Object key = data.getKey();
			
			// When moving, remove the source button.
			boolean move = (dropAction & DnDConstants.ACTION_MOVE) != 0 &&
						   (dropAction & DnDConstants.ACTION_COPY) == 0;
			
			TextResourceEditorFrame resourceEditor;
			switch (type) {
			
			case NavigatorTransferableData.AREA_EDITOR:
				AreaEditorFrameBase areaEditor = data.getAreaEditor();
				targetButtonPanel.addAreaEditor(areaEditor, false);
				if (move) {
					sourceButtonPanel.areaEditors.remove(key);
				}
				break;
				
			case NavigatorTransferableData.AREA_PROPERTIES:
				AreaPropertiesFrame areaPropertiesFrame = data.getAreaPropertiesEditor();
				targetButtonPanel.addAreaProperties(areaPropertiesFrame, false);
				if (move) {
					sourceButtonPanel.areaProperties.remove(key);
				}
				break;
				
			case NavigatorTransferableData.AREA_TREE_EDITOR:
				AreasTreeEditorFrame areaTreeEditor = data.getAreaTreeEditor();
				targetButtonPanel.addAreaTreeEditor(areaTreeEditor, false);
				if (move) {
					sourceButtonPanel.areaTrees.remove(areaTreeEditor);
				}
				break;
				
			case NavigatorTransferableData.START_RESOURCE_EDITOR:
				long versionId = data.getVersionId();
				TextResourceEditorFrame startResourceEditor = data.getStartResourceEditor();
				targetButtonPanel.addStartResourceEditor(versionId, startResourceEditor, false);
				if (move) {
					sourceButtonPanel.startResources.remove(key);
				}
				break;
				
			case NavigatorTransferableData.TEXT_RESOURCE_EDITOR:
				resourceEditor = data.getResourceEditor();
				targetButtonPanel.addResourceEditor(resourceEditor, false);
				if (move) {
					sourceButtonPanel.resources.remove(key);
				}
				break;
				
			case NavigatorTransferableData.SLOT_EDITOR:
				SlotEditorFrame slotEditor = data.getSlotEditor();
				targetButtonPanel.addSlotEditor(slotEditor, false);
				if (move) {
					sourceButtonPanel.slotEditors.remove(key);
				}
				break;
				
			default:
				Utility.show(this, "org.multipage.generator.messageUnknownNavigatorDnDType", type);
				move = false;
			}
			
			// Reload source panel buttons as one of them has been removed.
			if (move) {
				sourceButtonPanel.reloadButtons();
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * On click area editor.
	 * @param areaId
	 */
	private void onClickAreaEditor(Long areaId) {
		try {
			
			// Toggle editor visibility.
			AreaEditorFrameBase editor = areaEditors.get(areaId);
			if (editor == null) {
				
				// If the editor was not found remove related button.
				areaEditors.remove(areaId);
				reloadButtons();
				return;
			}
			
			JFrame frame = editor.getFrame();
			if (frame == null) {
				return;
			}
			Utility.toggleFrame(frame);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On click area properties.
	 * @param areaId
	 */
	private void onClickAreaProperties(Long areaId) {
		try {
			
			// Toggle editor visibility.
			AreaPropertiesFrame editor = areaProperties.get(areaId);
			if (editor == null) {
				
				// If the editor was not found remove related button.
				areaProperties.remove(areaId);
				reloadButtons();
				return;
			}
			
			Utility.toggleFrame(editor);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On click start resource.
	 * @param resourceVersionId
	 */
	private void onClickStartResource(String resourceVersionId) {
		try {
			
			// Toggle editor visibility.
			TextResourceEditorFrame editor = startResources.get(resourceVersionId);
			if (editor == null) {
				
				// If the editor was not found remove related button.
				startResources.remove(resourceVersionId);
				reloadButtons();
				return;
			}
			
			Utility.toggleFrame(editor);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On click resource.
	 * @param resourceId
	 */
	private void onClickResource(Long resourceId) {
		try {
			
			// Toggle editor visibility.
			TextResourceEditorFrame editor = resources.get(resourceId);
			if (editor == null) {
				
				// If the editor was not found remove related button.
				resources.remove(resourceId);
				reloadButtons();
				return;
			}
			
			Utility.toggleFrame(editor);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On click slot editor.
	 * @param slotId
	 */
	private void onClickSlotEditor(Long slotId) {
		try {
			
			// Toggle editor visibility.
			SlotEditorFrame editor = slotEditors.get(slotId);
			if (editor == null) {
				
				// If the editor was not found remove related button.
				slotEditors.remove(slotId);
				reloadButtons();
				return;
			}
			
			Utility.toggleFrame(editor);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On click area tree.
	 * @param editor
	 */
	private void onClickAreaTree(AreasTreeEditorFrame editor) {
		try {
			
			// Toggle editor visibility.
			if (editor == null) {
				
				// If the editor was not found remove related button.
				areaTrees.remove(editor);
				reloadButtons();
				return;
			}
			
			Utility.toggleFrame(editor);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On click folder button.
	 * @param button
	 * @param event 
	 */
	private void onClickFolder(JButton button, ActionEvent event) {
		try {
			
			int modifiers = event.getModifiers();
			boolean ctrlKey = ((modifiers & ActionEvent.CTRL_MASK) != 0);
			
			// Show selected folder items.
			hideAllFolderWindows();
			
			String folderName = button.getText();
			NavigatorWindow subWindow = folderWindows.get(folderName);
			subWindow.setVisible(true);
			
			// Get user flag.
			boolean displayGroup = false;
			if (getDisplayGroupLambda != null) {
				displayGroup = getDisplayGroupLambda.get();
			}
			
			// When display group flag set or a CTRL key + click, display folder frames.
			if (displayGroup || ctrlKey) {
				displayAllWindows(false);
				
				folderWindows.forEach((folder, window) -> {
					try {
						window.getButtonPanel().displayAllWindows(false);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				});
				folderWindows.forEach((folder, window) -> {
					try {
						
						if (folder == folderName) {
							window.getButtonPanel().displayAllWindows(true);
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				});
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get opened folder window.
	 * @return
	 */
	public NavigatorWindow getOpenedFolderWindow() {
		
		try {
			Obj<NavigatorWindow> folderWindow = new Obj<>(null);
			
			// Try to find first visible folder window and return it.
			folderWindows.forEach((name, window) -> {
				
				boolean isVisible = window.isVisible();
				if (isVisible) {
					if (folderWindow.ref == null) {
						folderWindow.ref = window;
					}
					else {
						// Hide other visible windows.
						window.setVisible(false);
					}
				}
			});
			return folderWindow.ref;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Hide all visible folder windows.
	 */
	public void hideAllFolderWindows() {
		try {
			
			folderWindows.forEach((name, window) -> {
				boolean isVisible = window.isVisible();
				if (isVisible) {
					window.setVisible(false);
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
