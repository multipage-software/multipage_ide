/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2025-04-10
 *
 */
package org.multipage.generator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import org.maclan.Area;
import org.multipage.gui.ApplicationEvents;
import org.multipage.gui.GuiSignal;
import org.multipage.gui.Images;
import org.multipage.gui.UpdatableComponent;
import org.multipage.gui.Utility;
import org.multipage.util.Closable;
import org.multipage.util.Safe;

/**
 * Window that displays dialog navigator with the list of opened windows.
 * @author vakol
 */
public class DialogNavigator extends JWindow implements UpdatableComponent, Closable {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	// $hide>>$
	
	/**
	 * Navigator small and large widths.
	 */
	public static final int NAVIGATOR_SMALL_WIDTH = 20;
	public static final int NAVIGATOR_LARGE_WIDTH = 100;
	public static final int NAVIGATOR_FOLDER_TOP = 28;
	
	/**
	 * Navigator panel opacity.
	 */
	public static final float NAVIGATOR_OPACITY = 0.75f;
	
	/**
	 * Delay between mouse watchdog ticks.
	 */
	private static final int MOUSE_WATCHDOG_MS = 200;
	
	/**
	 * Singleton dialog navigator window.
	 */
	private static DialogNavigator navigatorWindow = null;
	
	/**
	 * Mouse position watchdog timer.
	 */
	private Timer mouseWatchdogTimer = null;
	
	/**
	 * Navigator panel with buttons.
	 */
	private NavigatorButtonsPanel navigatorButtonsPanel = null;
		
	// $hide<<$
	/**
	 * Window components.
	 */
	private JMenu menuDisplay;
	private JMenuItem menuClose;
	private JMenuItem menuAddFolder;
	private JMenuItem menuHideAll;
	private JCheckBoxMenuItem menuDisplayGroup;
	
	/**
	 * Constructor.
	 * @param parent
	 */
	public DialogNavigator() {
		
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
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		getContentPane().add(menuBar, BorderLayout.NORTH);
		
		menuDisplay = new JMenu("≡");
		menuDisplay.setToolTipText("org.multipage.generator.tooltipDialogNavigatorMenu");
		menuDisplay.setIconTextGap(0);
		menuDisplay.setHorizontalAlignment(SwingConstants.TRAILING);
		menuDisplay.setMargin(new Insets(0, 3, 0, 3));
		menuDisplay.setFont(new Font("Consolas", Font.BOLD, 21));
		menuDisplay.setActionCommand("");
		menuBar.add(menuDisplay);
		
		menuClose = new JMenuItem("textClose");
		menuClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCloseNavigator();
			}
		});
		
		menuAddFolder = new JMenuItem("org.multipage.generator.menuAddNavigatorFolder");
		menuAddFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onAddNavigatorFolder();
			}
		});
		menuDisplay.add(menuAddFolder);
		
		menuHideAll = new JMenuItem("org.multipage.generator.menuNavigatorHideAll");
		menuHideAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onHideAll();
			}
		});
		menuDisplay.add(menuHideAll);
		
		menuDisplayGroup = new JCheckBoxMenuItem("org.multipage.generator.menuNavigatorDisplayGroup");
		menuDisplayGroup.setSelected(true);
		menuDisplay.add(menuDisplayGroup);
		menuDisplay.add(menuClose);
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			navigatorButtonsPanel = new NavigatorButtonsPanel();
			getContentPane().add(navigatorButtonsPanel, BorderLayout.CENTER);
			navigatorButtonsPanel.addMainButton();
			
			setWindowBounds(NAVIGATOR_SMALL_WIDTH);
			setOpacity(NAVIGATOR_OPACITY);
			
			localize();
			setIcons();
			setColors();
			
			setListeners();
			createMouseWatchDog();
			
			// Register the dialog navigator in Update Manager.
			GeneratorMainFrame.registerForUpdate(this);
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
			
			Utility.localizeTooltip(menuDisplay);
			Utility.localize(menuAddFolder);
			Utility.localize(menuHideAll);
			Utility.localize(menuDisplayGroup);
			Utility.localize(menuClose);
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
			
			menuAddFolder.setIcon(Images.getIcon("org/multipage/generator/images/folder.png"));
			menuClose.setIcon(Images.getIcon("org/multipage/gui/images/cancel_icon.png"));
			menuHideAll.setIcon(Images.getIcon("org/multipage/gui/images/hide_all.png"));
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
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set navigator window boundaries.
	 * @param navigatorWidth
	 */
	private void setWindowBounds(int navigatorWidth) {
		try {
			
			// Place navigator to the right of the screen.
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			int screenWidth = screenSize.width;
			
			Dimension windowSize = new Dimension(navigatorWidth, screenSize.height);
			Point windowLocation = new Point(screenWidth - navigatorWidth, 0);
			
			setSize(windowSize);
			setLocation(windowLocation);
			setPreferredSize(windowSize);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Display navigator window.
	 * @param showNavigator
	 */
	public static void displayNavigator(boolean showNavigator) {
		try {
			
			if (showNavigator) {
				if (navigatorWindow == null) {
					navigatorWindow = new DialogNavigator();
					navigatorWindow.setAlwaysOnTop(true);
				}
				navigatorWindow.setVisible(true);
				navigatorWindow.mouseWatchdogTimer.start();
			}
			else {
				if (navigatorWindow != null) {
					navigatorWindow.setVisible(false);
					navigatorWindow.mouseWatchdogTimer.stop();
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set listeners.
	 */
	private void setListeners() {
		try {
			
			// Create callback function that can get the display group flag.
			navigatorButtonsPanel.getDisplayGroupLambda = () -> {
				boolean checked = menuDisplayGroup.isSelected();
				return checked;
			};
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Create mouse position watchdog.
	 */
	private void createMouseWatchDog() {
		
		mouseWatchdogTimer = new Timer(MOUSE_WATCHDOG_MS, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
			
					// Get mouse position and window boundaries.
					Point mousePosition = MouseInfo.getPointerInfo().getLocation();
					Rectangle windowRectangle = getBounds();
					
					// Get opened folder window.
					NavigatorWindow folderWindow = getOpenedFolderWindow();
					Rectangle folderRectangle = null;
					if (folderWindow != null) {
						folderRectangle = folderWindow.getBounds();
					}
					
					// Make window large or small depending on mouse position.
					boolean isOnNavigatorWindow = windowRectangle.contains(mousePosition);
					boolean isOnFolderWindow = folderRectangle != null ? folderRectangle.contains(mousePosition) : false;
					
					boolean enlargeWindow = isOnNavigatorWindow || isOnFolderWindow;
					double currentWidth = windowRectangle.getWidth();
					
					if (enlargeWindow) {
						if (currentWidth == NAVIGATOR_SMALL_WIDTH) {
							enlargeWindow(true);
						}
					}
					else {
						if (currentWidth == NAVIGATOR_LARGE_WIDTH) {
							boolean menuVisible = menuDisplay.isPopupMenuVisible();
							if (menuVisible) {
								return;
							}
							enlargeWindow(false);
						}
					}
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			}
		});
		mouseWatchdogTimer.start();
	}
	
	/**
	 * Get opened folder window.
	 * @return
	 */
	protected NavigatorWindow getOpenedFolderWindow() {
		
		try {
			// Delegate the call.
			NavigatorWindow folderWindow = navigatorButtonsPanel.getOpenedFolderWindow();
			return folderWindow;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Enlarge the navigator window.
	 * @param enlarge
	 */
	protected void enlargeWindow(boolean enlarge) {
		try {
			
			if (enlarge) {
				setWindowBounds(NAVIGATOR_LARGE_WIDTH);
			}
			else {
				navigatorWindow.hideFolderWindows();
				setWindowBounds(NAVIGATOR_SMALL_WIDTH);
			}
			// Set the navigator as top most window.
			toFront();
			navigatorWindow.setAlwaysOnTop(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};	
	}
	
	/**
	 * Hide folder windows.
	 */
	private void hideFolderWindows() {
		try {
			
			// Delegate the call.
			navigatorButtonsPanel.hideAllFolderWindows();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Close the navigator window.
	 */
	protected void onCloseNavigator() {
		try {
			
			setVisible(false);
			mouseWatchdogTimer.stop();
			// Transmit the close event.
			ApplicationEvents.transmit(this, GuiSignal.dialogNavigatorClosed);
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
			
			// Delegate this call.
			navigatorButtonsPanel.onAddNavigatorFolder();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On hide all.
	 */
	protected void onHideAll() {
		try {
			
			navigatorButtonsPanel.hideAllFrames();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Add the area editor frame.
	 * @param editor
	 */
	public static void addAreaEditor(AreaEditorFrameBase editor)
			throws Exception {
		
		try {
			if (navigatorWindow == null) {
				navigatorWindow = new DialogNavigator();
			}
			// Delegate this call.
			navigatorWindow.navigatorButtonsPanel.addAreaEditor(editor, true);
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
	public static AreaEditorFrameBase getAreaEditor(Area area) {
		
		try {
			if (navigatorWindow == null) {
				return null;
			}
			// Delegate this call.
			AreaEditorFrameBase editor = navigatorWindow.navigatorButtonsPanel.getAreaEditor(area);
			return editor;
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
	public static void removeAreaEditor(Area area) {
		try {
			
			if (navigatorWindow == null || area == null) {
				return;
			}
			// Delegate this call.
			navigatorWindow.navigatorButtonsPanel.removeAreaEditor(area);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Add the area properties frame.
	 * @param editor
	 */
	public static void addAreaProperties(AreaPropertiesFrame editor)
			throws Exception {
		
		try {
			// If needed create singleton navigator object.
			if (navigatorWindow == null) {
				navigatorWindow = new DialogNavigator();
			}
			// Delegate this call.
			navigatorWindow.navigatorButtonsPanel.addAreaProperties(editor, true);
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
	public static AreaPropertiesFrame getAreaProperties(Area area) {

		try {
			if (navigatorWindow == null) {
				return null;
			}
			// Delegate this call.
			AreaPropertiesFrame editor = navigatorWindow.navigatorButtonsPanel.getAreaProperties(area);
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
	public static void removeAreaProperties(Area area) {
		try {
			
			if (navigatorWindow == null || area == null) {
				return;
			}
			// Delegate this call.
			navigatorWindow.navigatorButtonsPanel.removeAreaProperties(area);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Add start resource editor frame.
	 * @param versionId
	 * @param editor
	 */
	public static void addStartResourceEditor(long versionId, TextResourceEditorFrame editor)
			throws Exception {
		
		try {
			// If needed create singleton navigator object.
			if (navigatorWindow == null) {
				navigatorWindow = new DialogNavigator();
			}
			// Delegate this call.
			navigatorWindow.navigatorButtonsPanel.addStartResourceEditor(versionId, editor, true);
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
	public static TextResourceEditorFrame getStartResourceEditor(long resourceId, long versionId) {

		try {
			if (navigatorWindow == null) {
				return null;
			}
			// Delegate this call.
			TextResourceEditorFrame editor = navigatorWindow.navigatorButtonsPanel.getStartResourceEditor(resourceId, versionId);
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
	public static void removeStartResourceEditor(long resourceId, long versionId) {
		try {
			
			if (navigatorWindow == null) {
				return;
			}
			// Delegate this call.
			navigatorWindow.navigatorButtonsPanel.removeStartResourceEditor(resourceId, versionId);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Add resource editor frame.
	 * @param editor
	 */
	public static void addResourceEditor(TextResourceEditorFrame editor)
			throws Exception {
		
		try {
			// If needed create singleton navigator object.
			if (navigatorWindow == null) {
				navigatorWindow = new DialogNavigator();
			}
			// Delegate this call.
			navigatorWindow.navigatorButtonsPanel.addResourceEditor(editor, true);
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
	public static TextResourceEditorFrame getResourceEditor(long resourceId) {
		
		try {
			if (navigatorWindow == null) {
				return null;
			}
			// Delegate this call.
			TextResourceEditorFrame editor = navigatorWindow.navigatorButtonsPanel.getResourceEditor(resourceId);
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
	public static void removeResourceEditor(long resourceId) {
		try {
			
			if (navigatorWindow == null) {
				return;
			}
			// Delegate this call.
			navigatorWindow.navigatorButtonsPanel.removeResourceEditor(resourceId);
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
	public static void addAreaTreeEditor(AreasTreeEditorFrame editor, boolean checkExists)
			throws Exception {
		
		try {
			// If needed create singleton navigator object.
			if (navigatorWindow == null) {
				navigatorWindow = new DialogNavigator();
				
			}
			// Delegate this call.
			navigatorWindow.navigatorButtonsPanel.addAreaTreeEditor(editor, checkExists);
		}
		catch (Throwable e) {
			throw e;
		}
	}
	
	/**
	 * Remove area tree editor.
	 * @param editor
	 */
	public static void removeAreaTreeEditor(AreasTreeEditorFrame editor) {
		try {
			
			if (navigatorWindow == null || editor == null) {
				return;
			}
			// Delegate this call.
			navigatorWindow.navigatorButtonsPanel.removeAreaTreeEditor(editor);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Add slot editor.
	 * @param editor
	 * @throws Exception
	 */
	public static void addSlotEditor(SlotEditorFrame editor)
			throws Exception {
		
		try {
			// If needed create singleton navigator object.
			if (navigatorWindow == null) {
				navigatorWindow = new DialogNavigator();
			}
			// Delegate this call.
			navigatorWindow.navigatorButtonsPanel.addSlotEditor(editor, true);
		}
		catch (Throwable e) {
			throw e;
		}
	}
	
	/**
	 * Add slot editor.
	 * @param slotId
	 * @param editor
	 */
	public static void addSlotEditor(Long slotId, SlotEditorFrame editor) 
			throws Exception {
		
		try {
			// If needed create singleton navigator object.
			if (navigatorWindow == null) {
				navigatorWindow = new DialogNavigator();
			}
			// Delegate this call.
			navigatorWindow.navigatorButtonsPanel.addSlotEditor(slotId, editor, true);
		}
		catch (Throwable e) {
			throw e;
		}
	}
	
	/**
	 * Get slot editor.
	 * @param slotId
	 */
	public static SlotEditorFrame getSlotEditor(Long slotId) {
		
		try {
			if (slotId == null || slotId == 0L) {
				return null;
			}
			if (navigatorWindow == null) {
				return null;
			}
			// Delegate this call.
			SlotEditorFrame editor = navigatorWindow.navigatorButtonsPanel.getSlotEditor(slotId);
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
	public static void changeSlotEditorId(Long slotId, Long newSlotId) {
		try {
			
			if (navigatorWindow == null) {
				return;
			}
			// Delegate this call.
			navigatorWindow.navigatorButtonsPanel.changeSlotEditorId(slotId, newSlotId);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Remove slot editor.
	 * @param editor
	 */
	public static void removeSlotEditor(SlotEditorFrame editor) {
		try {
			
			if (navigatorWindow == null || editor == null) {
				return;
			}
			Long slotId = editor.getSlotId();
			if (slotId == null) {
				return;
			}
			// Delegate this call.
			navigatorWindow.navigatorButtonsPanel.removeSlotEditor(slotId);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Called with Update Manager.
	 */
	@Override
	public synchronized void updateComponents() {
		try {
			
			// Delegate call to the button panel.
			navigatorButtonsPanel.updateButtons();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Close navigator window.
	 */
	@Override
	public void close() {
		
		// Unregister navigator from Update Manager.
		GeneratorMainFrame.unregisterFromUpdate(this);
		// Stop watchdog and dispose the window.
		mouseWatchdogTimer.stop();
		dispose();
	}
	
	/**
	 * Close dialog navigator. Called with GeneratorMainFrame on application exit.
	 */
	public static void closeNavigator() {
		
		if (navigatorWindow == null) {
			return;
		}
		navigatorWindow.close();
		navigatorWindow = null;
	}
}
