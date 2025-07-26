/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Component;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.MenuElement;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.maclan.Area;
import org.multipage.gui.Images;
import org.multipage.gui.Utility;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Local menu for areas.
 * @author vakol
 *
 */
public class AreaLocalMenu {

	/**
	 * Constants.
	 */
	public static final int DIAGRAM = 1;
	public static final int EDITOR = 2;
	
	/**
	 * Empty menu item padding.
	 */
	private static final Insets emptyMenuItemPadding = new Insets(0, 0, 0, 0);

	/**
	 * Callback class and object.
	 */
	public static class Callbacks {

		protected Component getComponent() {
			return null;
		}
		
		public AreaDiagramContainerPanel getAreaDiagramEditor() {
			return null;
		}
		
		protected Area getCurrentArea() {
			return null;
		}
		
		protected List<Area> getCurrentAreas() {
			return null;
		}
		
		protected Area getCurrentParentArea() {
			return null;
		}
		
		protected void onNewArea(Long newAreaId) {
		}
		
		protected void selectEditorArea(Long areaId, boolean reset, boolean affectSubareas) {
		}

		protected void clearEditorAreaSelection() {
		}
	}
	
	private Callbacks callbacks = null;
	
	/**
	 * Is favorites flag.
	 */
	public boolean isAddFavorites = true;

	/**
	 * Menu purpose.
	 */
	private int purpose = 0;
	
	/**
	 * Parent component.
	 */
	private Component parentComponent;
	
	/**
	 * Taken area references.
	 */
	private List<Area> takenAreas = null;
	private Area takenAreaParent = null;
	
	/**
	 * All popup menu elements. 
	 */
	public MenuElement menuTakeAreaTree;
	public MenuElement menuCopyTakenAreaTree;
	public MenuElement menuMoveTakenAreaTree;
	public MenuElement menuAddToFavoritesArea;
	public MenuElement menuCreateAreas;
	public MenuElement menuExternalSources;
	public MenuElement menuSetHomeArea;
	public MenuElement menuEditAreaSlots;
	public MenuElement menuEditStartResources;
	public MenuElement menuFile;
	public MenuElement menuExport;
	public MenuElement menuImport;
	public MenuElement menuFocusArea;
	public MenuElement menuFocusSuperArea;
	public MenuElement menuFocusNextArea;
	public MenuElement menuFocusPreviousArea;
	public MenuElement menuFocusTabTopArea;
	public MenuElement menuEditArea;
	public MenuElement menuCopyDescription;
	public MenuElement menuCopyAlias;
	public MenuElement menuAreaTrace;
	public MenuElement menuDisplayArea;
	public MenuElement menuDisplayRenderedArea;
	public MenuElement displayMenu;
	public MenuElement menuAreaHelp;
	public MenuElement menuAreaInheritedFolders;
	public MenuElement focusMenu;
	public MenuElement selectMenu;
	public MenuElement menuSelectArea;
	public MenuElement menuSelectAreaAdd;
	public MenuElement menuSelectAreaAndSuabreas;
	public MenuElement menuSelectAreaAndSuabreasAdd;
	public MenuElement menuClearSelection;
	public MenuElement menuCloneDiagram;
	
	/**
	 * Constructor.
	 * @param areaLocalMenuListener
	 */
	public AreaLocalMenu(Callbacks callbacks) {
		
		this.callbacks = callbacks;
	}

	/**
	 * Constructor.
	 * @param callbacks
	 * @param purpose
	 */
	public AreaLocalMenu(Callbacks callbacks, int purpose) {

		this(callbacks);
		
		this.purpose = purpose;
	}
	

	/**
	 * Create new menu item.
	 * @param menuTextId
	 * @param iconPath
	 * @return
	 */
	private static JMenu newMenu(String menuTextId, String iconPath) { 
		
		try {
			String menuText = Resources.getString(menuTextId);
			JMenu menu = new JMenu(menuText);
			ImageIcon icon = Images.getIcon(iconPath);
			
			menu.setIcon(icon);
			return menu;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Create new menu.
	 * @param menuTextId
	 * @param iconPath
	 * @return
	 */
	private static JMenu newMenu(String menuTextId) { 
		
		try {
			String menuText = Resources.getString(menuTextId);
			JMenu menu = new JMenu(menuText);
			
			return menu;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	
	
	/**
	 * Create new menu item.
	 * @param menuTextId
	 * @param iconPath
	 * @param accelerator
	 * @return
	 */
	private static JMenuItem newMenuItem(String menuTextId, String iconPath, String accelerator) { 
		
		try {
			String menuText = Resources.getString(menuTextId);
			JMenuItem menuItem = new JMenuItem(menuText);
			ImageIcon icon = Images.getIcon(iconPath);
			
			menuItem.setIcon(icon);
			menuItem.setAccelerator(KeyStroke.getKeyStroke(accelerator));
			
			return menuItem;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Create new menu item.
	 * @param menuTextId
	 * @param iconPath
	 * @return
	 */
	private static JMenuItem newMenuItem(String menuTextId, String iconPath) { 
		
		try {
			String menuText = Resources.getString(menuTextId);
			JMenuItem menuItem = new JMenuItem(menuText);
			ImageIcon icon = Images.getIcon(iconPath);
			
			menuItem.setIcon(icon);
			return menuItem;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Create new menu item.
	 * @param menuTextId
	 * @param iconPath
	 * @return
	 */
	private static JMenuItem newMenuItem(String menuTextId) { 
		
		try {
			String menuText = Resources.getString(menuTextId);
			JMenuItem menuItem = new JMenuItem(menuText);
			
			return menuItem;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Add items to a popup trayMenu.
	 * @param popupMenu
	 */
	public void addTo(Component parentComponent, JPopupMenu popupMenu) {
		try {
			
			this.parentComponent = parentComponent;
			addTo(popupMenu, popupMenu.getComponentCount() + 1);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Add items to a popup trayMenu.
	 * @param popupMenuFavorites
	 * @param start
	 */
	public void addTo(JPopupMenu popupMenu, int start) {
		try {
			
			menuFile = newMenu("org.multipage.generator.menuFile");
			menuEditArea = newMenu("org.multipage.generator.menuEditAreaResourcesList", "org/multipage/generator/images/area_node.png");
			focusMenu = newMenu("org.multipage.generator.menuFocus");
			selectMenu = newMenu("org.multipage.generator.menuSelect");
			displayMenu = newMenu("org.multipage.generator.menuDisplayMenu");
			menuCreateAreas = newMenu("org.multipage.generator.menuCreateAreas");
			menuTakeAreaTree = newMenuItem("org.multipage.generator.menuTakeAreaTree", "org/multipage/gui/images/copy_icon.png");
			menuCopyTakenAreaTree = newMenuItem("org.multipage.generator.menuCopyTakenAreaTree", "org/multipage/gui/images/paste_icon.png");
			menuMoveTakenAreaTree = newMenuItem("org.multipage.generator.menuMoveTakenAreaTree", "org/multipage/gui/images/paste_icon.png");
			menuAddToFavoritesArea = newMenuItem("org.multipage.generator.menuAddToFavorites", "org/multipage/generator/images/favorite.png");
			menuExternalSources = newMenuItem("org.multipage.generator.menuExternalSourceCodes");
			menuSetHomeArea = newMenuItem("org.multipage.generator.menuSetHomeArea", "org/multipage/generator/images/home_page.png");
			menuEditAreaSlots = newMenuItem("org.multipage.generator.menuEditArea", "org/multipage/generator/images/list.png");
			menuEditStartResources = newMenuItem("org.multipage.generator.menuEditStartResources", "org/multipage/generator/images/start_resource.png");
			menuExport = newMenuItem("org.multipage.generator.menuFileExport", "org/multipage/generator/images/export2_icon.png");
			menuImport = newMenuItem("org.multipage.generator.menuFileImport", "org/multipage/generator/images/import2_icon.png");
			menuFocusArea = newMenuItem("org.multipage.generator.menuFocusArea", "org/multipage/generator/images/search_icon.png");
			menuFocusSuperArea = newMenuItem("org.multipage.generator.menuFocusSuperArea", "org/multipage/generator/images/search_parent.png", "control S");
			menuFocusNextArea = newMenuItem("org.multipage.generator.menuFocusNextArea", "org/multipage/generator/images/next.png");
			menuFocusPreviousArea = newMenuItem("org.multipage.generator.menuFocusPreviousArea", "org/multipage/generator/images/previous.png");
			menuFocusTabTopArea = newMenuItem("org.multipage.generator.menuFocusTabTopArea", "org/multipage/generator/images/focus_tab.png");
			menuCopyDescription = newMenuItem("org.multipage.generator.menuCopyAreaDescription", "org/multipage/gui/images/copy_icon.png");
			menuCopyAlias = newMenuItem("org.multipage.generator.menuCopyAreaAlias", "org/multipage/gui/images/copy_icon.png");
			menuAreaTrace = newMenuItem("org.multipage.generator.menuAreaTrace", "org/multipage/generator/images/area_trace.png");
			menuDisplayArea = newMenuItem("org.multipage.generator.menuDisplayOnlineArea", "org/multipage/generator/images/display.png");
			menuDisplayRenderedArea = newMenuItem("org.multipage.generator.menuDisplayRenderedArea", "org/multipage/generator/images/display_rendered.png");
			menuAreaHelp = newMenuItem("org.multipage.generator.menuAreaHelp", "org/multipage/generator/images/help_small.png");
			menuAreaInheritedFolders = newMenuItem("org.multipage.generator.menuAreaInheritedFolders", "org/multipage/generator/images/folder.png");
			menuSelectArea = newMenuItem("org.multipage.generator.menuSelectArea2", "org/multipage/generator/images/selected_area.png");
			menuSelectAreaAdd = newMenuItem("org.multipage.generator.menuSelectAreaAdd", "org/multipage/generator/images/selected_area_add.png");
			menuSelectAreaAndSuabreas = newMenuItem("org.multipage.generator.menuSelectAreaAndSubareas2", "org/multipage/generator/images/selected_subareas.png");
			menuSelectAreaAndSuabreasAdd = newMenuItem("org.multipage.generator.menuSelectAreaAndSubareasAdd", "org/multipage/generator/images/selected_subareas_add.png");
			menuClearSelection = newMenuItem("org.multipage.generator.menuClearSelection", "org/multipage/generator/images/cancel_icon.png");
			menuCloneDiagram = newMenuItem("org.multipage.generator.menuCloneAreasDiagram", "org/multipage/generator/images/clone.png");
			
			int index = start;
			
			insert(popupMenu, menuTakeAreaTree, index++);
			insert(popupMenu, menuCopyTakenAreaTree, index++);
			insert(popupMenu, menuMoveTakenAreaTree, index++);
			addSeparator(popupMenu); index++;
			if (isAddFavorites) {
				insert(popupMenu, menuAddToFavoritesArea, index++);
				addSeparator(popupMenu); index++;
			}
			if ((purpose & DIAGRAM) == 0) {
				insert(popupMenu, selectMenu, index++);
			}
			insert(displayMenu, menuDisplayArea, 0);
			insert(displayMenu, menuDisplayRenderedArea, 1);
			insert(popupMenu, menuCreateAreas, index++);
			insert(menuCreateAreas, menuExternalSources, 0);
			insert(popupMenu, menuSetHomeArea, index++);
			insert(popupMenu, menuEditArea, index++);
			insert(popupMenu, menuEditAreaSlots, index++);
			insert(popupMenu, menuEditStartResources, index++);
			addSeparator(popupMenu); index++;
			insert(popupMenu, menuSetHomeArea, index++);
			addSeparator(popupMenu); index++;
			index = insertEditResourceMenuItems(popupMenu, index);
			insert(popupMenu, focusMenu, index++);
			insert(popupMenu, displayMenu, index++);
			insert(popupMenu, menuFile, index++);
			addSeparator(popupMenu); index++;
			insert(popupMenu, menuAreaTrace, index++);
			insert(popupMenu, menuAreaInheritedFolders, index++);
			addSeparator(popupMenu); index++;
			insert(popupMenu, menuCopyDescription, index++);
			insert(popupMenu, menuCopyAlias, index++);
			addSeparator(popupMenu); index++;
			insert(popupMenu, menuAreaHelp, index++);
			addSeparator(popupMenu); index++;
			insert(popupMenu, menuCloneDiagram, index++);
			
			index = 0;
			insert(menuFile, menuImport, index++);
			insert(menuFile, menuExport, index++);
					
			index = 0;
			insert(focusMenu, menuFocusArea, index++);
			insert(focusMenu, menuFocusSuperArea, index++);
			insert(focusMenu, menuFocusNextArea, index++);
			insert(focusMenu, menuFocusPreviousArea, index++);
			index = insertFocusMenuItems((JMenu) focusMenu, index++);
			addSeparator(focusMenu); index++;
			insert(focusMenu, menuFocusTabTopArea, index++);
			
			insertEditAreaMenu((JMenu) menuEditArea);
			
			if ((purpose & DIAGRAM) == 0) {
				index = 0;
				insert(selectMenu, menuSelectArea, index++);
				insert(selectMenu, menuSelectAreaAdd, index++);
				insert(selectMenu, menuSelectAreaAndSuabreas, index++);
				insert(selectMenu, menuSelectAreaAndSuabreasAdd, index++);
				addSeparator(selectMenu); index++;
				insert(selectMenu, menuClearSelection, index++);
			}
	
			// Add listeners.
			addActionListener(menuTakeAreaTree, () -> takeAreaTree());
			addActionListener(menuCopyTakenAreaTree, () -> copyTakenAreaTree());
			addActionListener(menuMoveTakenAreaTree, () -> moveTakenAreaTree());
			if (isAddFavorites) {
				addActionListener(menuAddToFavoritesArea, () -> addToFavorites());
			}
			addActionListener(menuExternalSources, () -> createExternalSources());
			addActionListener(menuEditAreaSlots, () -> editAreaSlots());
			addActionListener(menuFocusArea, () -> focusArea());
			addActionListener(menuFocusSuperArea, () -> focusSuperArea());
			addActionListener(menuFocusNextArea, () -> focusNextArea());
			addActionListener(menuFocusPreviousArea, () -> focusPreviousArea());
			addActionListener(menuFocusTabTopArea, () -> focusTabTopArea());
			addActionListener(menuCopyDescription, () -> copyDescription());
			addActionListener(menuCopyAlias, () -> copyAlias());
			addActionListener(menuAreaTrace, () -> showAreaTrace());
			addActionListener(menuAreaInheritedFolders, () -> showAreaInheritedFolders());
			addActionListener(menuAreaHelp, () -> viewHelp());
			addActionListener(menuDisplayArea, () -> displayOnlineArea());
			addActionListener(menuDisplayRenderedArea, () -> displayRenderedArea());
			addActionListener(menuSelectArea, () -> selectArea());
			addActionListener(menuSelectAreaAdd, () -> selectAreaAdd());
			addActionListener(menuSelectAreaAndSuabreas, () -> selectAreaAndSubareas());
			addActionListener(menuSelectAreaAndSuabreasAdd, () -> selectAreaAndSubareasAdd());
			addActionListener(menuClearSelection, () -> clearSelection());
			addActionListener(menuCloneDiagram, () -> cloneDiagram());
			addActionListener(menuSetHomeArea, () -> setHomeArea());
			addActionListener(menuExport, () -> exportArea());
			addActionListener(menuImport, () -> importArea());
			addActionListener(menuEditStartResources, () -> editStartResource(true));
			
			if (getAreaDiagramEditor() == null) {
				disableMenuItems(focusMenu, menuAddToFavoritesArea);
			}
			
			// Set listeners.
			popupMenu.addPopupMenuListener(new PopupMenuListener() {
				@Override
				public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
					try {
						
						// Enable / disable pasting of area tree with tray menu item.
						boolean enable = getMainFrame().isAreaTreeDataCopy();
						
						JMenuItem menuItem = (JMenuItem) menuCopyTakenAreaTree;
						menuItem.setEnabled(enable);
						
						menuItem = (JMenuItem) menuMoveTakenAreaTree;
						menuItem.setEnabled(enable);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				@Override
				public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				}
				@Override
				public void popupMenuCanceled(PopupMenuEvent e) {
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Adds action listener to menu element.
	 * @param menuElement
	 * @param runnable
	 */
	private void addActionListener(MenuElement menuElement, Runnable runnable) {
		try {
			
			ActionListener listener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						
						runnable.run();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			};
			
			if (menuElement instanceof JMenuItem) {
				JMenuItem menuItem = (JMenuItem) menuElement;
				menuItem.addActionListener(listener);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Disable menu items.
	 * @param disableMenuItems
	 */
	public void disableMenuItems(MenuElement ...disableMenuItems) {
		try {
			
			for (MenuElement menuElementDisable : disableMenuItems) {
				
				if (menuElementDisable instanceof JMenuItem) {
					JMenuItem menuItem = (JMenuItem) menuElementDisable;
					menuItem.setEnabled(false);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Insert separator.
	 * @param menuElement
	 */
	private void addSeparator(MenuElement menuElement) {
		try {
			
			if (menuElement instanceof JMenu) {
				JMenu menu = (JMenu) menuElement;
				menu.addSeparator();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Insert menu item at indexed position.
	 * @param menu
	 * @param menuItem
	 * @param index
	 */
	private void insert(MenuElement menuElement, MenuElement subElement, int index) {
		try {
			
			if (menuElement instanceof JMenu) { 
				JMenu menu = (JMenu) menuElement;
				insert(menu, subElement, index);
			}
			else if (menuElement instanceof JPopupMenu) {
				JPopupMenu popupMenu = (JPopupMenu) menuElement;
				insert(popupMenu, subElement, index);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Insert menu item at indexed position.
	 * @param menu
	 * @param menuItem
	 * @param index
	 */
	private void insert(JMenu menu, MenuElement menuElement, int index) {
		try {
			
			JMenuItem menuItem = (JMenuItem) menuElement;
			menu.insert(menuItem, index);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Insert popup menu item at indexed position.
	 * @param popupMenu
	 * @param menuItem
	 * @param index
	 */
	private void insert(JPopupMenu popupMenu, MenuElement menuElement, int index) {
		try {
			
			JMenuItem menuItem = (JMenuItem) menuElement;
			popupMenu.insert(menuItem, index);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	
	/**
	 * Create external sources.
	 */
	protected void createExternalSources() {
		try {
			
			Area area = callbacks.getCurrentArea();
			CreateAreasFromSourceCode.showDialog(parentComponent, area);
			
			// Update application components.
			GeneratorMainFrame.updateAll();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Insert edit trayMenu items.
	 * @param popupMenu 
	 * @param index
	 * @return
	 */
	protected int insertEditResourceMenuItems(JPopupMenu popupMenu, int index) {
		
		// Override this method.
		return index;
	}

	/**
	 * Insert focus trayMenu items.
	 * @param focusMenu
	 * @param index
	 * @return
	 */
	protected int insertFocusMenuItems(JMenu focusMenu, int index) {
		
		// Override this method.
		return index;
	}

	/**
	 * Insert edit area trayMenu.
	 * @param menuEditArea
	 * @return
	 */
	protected void insertEditAreaMenu(JMenu menuEditArea) {
		try {
			
			JMenuItem menuAreaEdit = createMenuItem(Resources.getString("org.multipage.generator.menuAreaEdit"));
			menuAreaEdit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						
						onEditArea(AreaEditorFrame.NOT_SPECIFIED);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			menuEditArea.add(menuAreaEdit);
			menuEditArea.addSeparator();
			
			JMenuItem menuEditResources = createMenuItem(Resources.getString("org.multipage.generator.menuAreaEditResources"));
			menuEditResources.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						
						onEditArea(AreaEditorFrame.RESOURCES);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			
			menuEditArea.add(menuEditResources);
			
			JMenuItem menuEditDependencies = createMenuItem(Resources.getString("org.multipage.generator.menuAreaEditDependencies"));
			menuEditDependencies.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						
						onEditArea(AreaEditorFrame.DEPENDENCIES);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			menuEditArea.add(menuEditDependencies);
			
			JMenuItem menuEditConstructor = createMenuItem(Resources.getString("org.multipage.generator.menuAreaEditConstructor"));
			menuEditConstructor.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						
						onEditArea(AreaEditorFrame.CONSTRUCTOR);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			menuEditArea.add(menuEditConstructor);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create new menu item.
	 * @param caption
	 * @return
	 */
	private JMenuItem createMenuItem(String caption) {
		
		try {
			JMenuItem menuItem = new JMenuItem(caption);
			menuItem.setMargin(emptyMenuItemPadding);
			return menuItem;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Display rendered area.
	 */
	protected void displayRenderedArea() {
		try {
			
			// Get selected areas.
			Area area = getAreaInformUser();
			if (area == null) {
				return;
			}
			
			GeneratorMainFrame.displayRenderedArea(area);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Display online area.
	 */
	protected void displayOnlineArea() {
		try {
			
			// Get selected areas.
			Area area = getAreaInformUser();
			if (area == null) {
				return;
			}
			
			getMainFrame().displayOnlineArea(area);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get area. Inform user on error.
	 * @return
	 */
	protected Area getAreaInformUser() {
		
		try {
			Area area = callbacks.getCurrentArea();
			if (area == null) {
				Utility.show(null, "org.multipage.generator.messageSelectSingleArea");
				return null;
			}
			return area;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Get main frame.
	 * @return
	 */
	protected GeneratorMainFrame getMainFrame() {
		try {
			
			return GeneratorMainFrame.getFrame();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Get area diagram editor.
	 * @return
	 */
	private AreaDiagramContainerPanel getAreaDiagramEditor() {
		try {
			
			if (callbacks != null) {
				AreaDiagramContainerPanel editor = callbacks.getAreaDiagramEditor();
				return editor;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Get area diagram.
	 * @return
	 */
	private AreaDiagramPanel getAreaDiagram() {
		try {
			
			AreaDiagramContainerPanel editor = getAreaDiagramEditor();
			if (editor == null) {
				return null;
			}
			return editor.getDiagram();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Add to favorites.
	 */
	protected void addToFavorites() {
		try {
			
			AreaDiagramContainerPanel editor = getAreaDiagramEditor();
			if (editor != null) {
				// Get selected areas.
				Area area = getAreaInformUser();
				if (area == null) {
					return;
				}
				editor.addFavorite(area);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Edit area slots.
	 */
	protected void editAreaSlots() {
		try {
			
			// Get selected areas.
			Area area = getAreaInformUser();
			if (area == null) {
				return;
			}
			
			// Open area editor.
			AreaPropertiesFrame.openNewInstance(area);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Focus selected area.
	 */
	protected void focusArea() {
		try {
			
			// Get selected areas.
			Area area = getAreaInformUser();
			if (area == null) {
				return;
			}
			
			// Focus on area.
			AreaDiagramContainerPanel editor = getAreaDiagramEditor();
			if (editor != null) {
				editor.focusArea(area.getId());
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Focus tab top area.
	 */
	protected void focusTabTopArea() {
		try {
			
			// Focus on the top area.
			AreaDiagramContainerPanel editor = getAreaDiagramEditor();
			if (editor != null) {
				editor.focusTopArea();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Focus super area.
	 */
	protected void focusSuperArea() {
		try {
			
			// Get selected areas.
			Area area = getAreaInformUser();
			if (area == null) {
				return;
			}
			
			// Get area shapes.
			AreaShapes shapes = (AreaShapes) area.getUser();
			// Get parent area.
			Rectangle2D diagramRect = getAreaDiagram().getRectInCoord();
			Area superArea = shapes.getVisibleParent(diagramRect);
			
			if (superArea != null) {
				// Focus on area.
				getAreaDiagramEditor().focusAreaNear(superArea.getId());
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Focus next area.
	 */
	protected void focusNextArea() {
		try {
			
			// Get selected areas.
			Area area = getAreaInformUser();
			if (area == null) {
				return;
			}
			
			// Get area shapes.
			AreaShapes shapes = (AreaShapes) area.getUser();
			// Get parent area.
			Rectangle2D diagramRect = getAreaDiagram().getRectInCoord();
			Area superArea = shapes.getVisibleParent(diagramRect);
			
			if (superArea != null) {
				
				// Get next area.
				Area nextArea = area.getNextArea(superArea);
				if (nextArea != null) {
					
					// Focus on next area.
					getAreaDiagramEditor().focusAreaNear(nextArea.getId());
				}
				else {
					Utility.show(null, "org.multipage.generator.messageIsLastArea");
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Focus previous area.
	 */
	protected void focusPreviousArea() {
		try {
			
			// Get selected areas.
			Area area = getAreaInformUser();
			if (area == null) {
				return;
			}
			
			// Get area shapes.
			AreaShapes shapes = (AreaShapes) area.getUser();
			// Get parent area.
			Rectangle2D diagramRect = getAreaDiagram().getRectInCoord();
			Area superArea = shapes.getVisibleParent(diagramRect);
			
			if (superArea != null) {
				
				// Get previous area.
				Area previousArea = area.getPreviousArea(superArea);
				if (previousArea != null) {
					
					// Focus on previous area.
					getAreaDiagramEditor().focusAreaNear(previousArea.getId());
				}
				else {
					Utility.show(null, "org.multipage.generator.messageThisIsFirstArea");
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Edit area with area editor.
	 */
	protected void onEditArea(int tabIdentifier) {
		try {
			
			// Get selected areas.
			Area area = getAreaInformUser();
			if (area == null) {
				return;
			}
	
			// Execute area editor.
			AreaEditorFrame.showDialog(null, area, tabIdentifier);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Edit start resource.
	 * @param inherits 
	 */
	protected void editStartResource(boolean inherits) {
		try {
			
			// Get selected areas.
			Area area = getAreaInformUser();
			if (area == null) {
				return;
			}
			
			// Edit start resource.
			GeneratorMainFrame.editStartResource(area, inherits);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Copy area alias.
	 */
	protected void copyAlias() {
		try {
			
			// Get selected areas.
			Area area = getAreaInformUser();
			if (area == null) {
				return;
			}
			
			StringSelection ss = new StringSelection(area.getAlias());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Copy area description.
	 */
	protected void copyDescription() {
		try {
			
			// Get selected areas.
			Area area = getAreaInformUser();
			if (area == null) {
				return;
			}
			
			StringSelection stringSelection = new StringSelection(area.getDescriptionForced());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Show trace area.
	 */
	protected void showAreaTrace() {
		try {
			
			// Get selected areas.
			Area area = getAreaInformUser();
			if (area == null) {
				return;
			}
			
			AreasTreeEditorFrame.showNewFrame(area.getId());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Show area inherited folders.
	 */
	protected void showAreaInheritedFolders() {
		try {
			
			// Get selected areas.
			Area area = getAreaInformUser();
			if (area == null) {
				return;
			}
			
			AreaInheritedFoldersDialog.showDialog(getMainFrame(), area);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * View help.
	 */
	protected void viewHelp() {
		try {
			
			// Get selected areas.
			Area area = getAreaInformUser();
			if (area == null) {
				return;
			}
			
			getMainFrame().findViewAreaHelp(area);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Select area.
	 */
	protected void selectArea() {
		try {
			
			// Get selected areas.
			Area area = getAreaInformUser();
			if (area == null) {
				return;
			}
			
			Long areaId = area.getId();
			
			// Invoke callback function.
			if (callbacks != null) {
				callbacks.selectEditorArea(areaId, true, false);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Add area selection.
	 */
	protected void selectAreaAdd() {
		try {
			
			// Get selected areas.
			Area area = getAreaInformUser();
			if (area == null) {
				return;
			}
			
			Long areaId = area.getId();
	
			// Invoke callback function.
			if (callbacks != null) {
				callbacks.selectEditorArea(areaId, false, false);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Select area and subareas.
	 */
	protected void selectAreaAndSubareas() {
		try {
			
			// Get selected areas.
			Area area = getAreaInformUser();
			if (area == null) {
				return;
			}
			
			long areaId = area.getId();
			
			// Invoke callback function.
			if (callbacks != null) {
				callbacks.selectEditorArea(areaId, true, true);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Add area and subareas selection.
	 */
	protected void selectAreaAndSubareasAdd() {
		try {
			
			// Get selected areas.
			Area area = getAreaInformUser();
			if (area == null) {
				return;
			}
			
			long areaId = area.getId();
			
			// Invoke callback function.
			if (callbacks != null) {
				callbacks.selectEditorArea(areaId, false, true);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Clear selection.
	 */
	protected void clearSelection() {
		try {
			// Invoke callback function.
			if (callbacks != null) {
				callbacks.clearEditorAreaSelection();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Clone diagram.
	 */
	protected void cloneDiagram() {
		try {
			
			// Get selected area (can be null).
			Area area = callbacks.getCurrentArea();
			
			getMainFrame().cloneAreasDiagram(area);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set home area.
	 */
	protected void setHomeArea() {
		try {
			
			// Get selected areas.
			Area area = getAreaInformUser();
			if (area == null) {
				return;
			}
			
			// Invoke callback function.
			getMainFrame().setHomeArea(parentComponent, area);
			GeneratorMainFrame.updateAll();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Take area tree.
	 */
	protected void takeAreaTree() {
		try {
			
			// Get selected areas (can be null).
			takenAreas = callbacks.getCurrentAreas();
			takenAreaParent = callbacks.getCurrentParentArea();
			
			GeneratorMainFrame frame = getMainFrame();
			frame.takeAreaTrees(takenAreas, takenAreaParent);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Copy taken area tree.
	 */
	protected void copyTakenAreaTree() {
		try {
			
			// Check taken area.
			if (takenAreas == null || takenAreas.isEmpty() || takenAreaParent == null) {
				return;
			}
			
			// Get selected area (can be null) and copy area tree.
			Area area = callbacks.getCurrentArea();
			GeneratorMainFrame frame = getMainFrame();
			frame.copyAreaTrees(area);
			
			// Update GUI components.
			GeneratorMainFrame.updateAll();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Move taken area tree.
	 */
	protected void moveTakenAreaTree() {
		try {
			
			// Check taken area.
			if (takenAreas == null || takenAreas.isEmpty() || takenAreaParent == null) {
				return;
			}
			
			// Prepare area tree for copying.
			GeneratorMainFrame frame = getMainFrame();
			Area area = callbacks.getCurrentArea();
			Area parentArea = callbacks.getCurrentParentArea();
			frame.moveAreaTrees(takenAreas, parentArea, area, parentComponent);
			
			// Update GUI components.
			GeneratorMainFrame.updateAll();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Export area.
	 */
	protected void exportArea() {
		try {
			
			Area area = callbacks.getCurrentArea();
			if (area == null) {
				return;
			}
			
			Component parent = callbacks.getComponent();
			if (parent == null) {
				parent = getMainFrame();
			}
			
			GeneratorMainFrame.exportArea(area, parent);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Import area.
	 */
	protected void importArea() {
		try {
			
			Area area = callbacks.getCurrentArea();
			if (area == null) {
				return;
			}
			
			Component parent = callbacks.getComponent();
			if (parent == null) {
				parent = getMainFrame();
			}
			
			Long newAreaId = GeneratorMainFrame.importArea(area, parent, true, false, false);
			
			// Callback method.
			if (newAreaId != null) {
				
				Safe.invokeLater(() -> {
					callbacks.onNewArea(newAreaId);
				});
				
				// Update all modules.
				GeneratorMainFrame.updateAll();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set purpose.
	 * @param purpose
	 */
	public void setHint(int purpose) {

		this.purpose = purpose;
	}
}
