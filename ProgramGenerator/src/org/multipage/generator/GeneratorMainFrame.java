/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.maclan.Area;
import org.maclan.AreaRelation;
import org.maclan.AreaTreesData;
import org.maclan.AreasModel;
import org.maclan.Language;
import org.maclan.Middle;
import org.maclan.MiddleListener;
import org.maclan.MiddleResult;
import org.maclan.MiddleUtility;
import org.maclan.Slot;
import org.maclan.VersionObj;
import org.maclan.help.HelpUtility;
import org.maclan.server.AreaServer;
import org.maclan.server.BrowserParameters;
import org.maclan.server.DebugViewer;
import org.maclan.server.ProgramServlet;
import org.maclan.server.TextRenderer;
import org.multipage.basic.ProgramBasic;
import org.multipage.gui.ApplicationEvents;
import org.multipage.gui.BareBonesBrowserLaunch;
import org.multipage.gui.GuiSignal;
import org.multipage.gui.Images;
import org.multipage.gui.LogConsoles;
import org.multipage.gui.Message;
import org.multipage.gui.PreventEventEchos;
import org.multipage.gui.Progress2Dialog;
import org.multipage.gui.ProgressDialog;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.ToolBarKit;
import org.multipage.gui.TopMostButton;
import org.multipage.gui.UpdatableComponent;
import org.multipage.gui.UpdateManager;
import org.multipage.gui.Utility;
import org.multipage.sync.SyncMain;
import org.multipage.util.Obj;
import org.multipage.util.ProgressResult;
import org.multipage.util.Resources;
import org.multipage.util.Safe;
import org.multipage.util.SimpleMethodRef;
import org.multipage.util.SwingWorkerHelper;

/**
 * Main application frame.
 * @author vakol
 *
 */
public class GeneratorMainFrame extends JFrame implements UpdatableComponent, PreventEventEchos {
	
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Menu size.
	 */
	private static final Dimension preferredMenuSize = new Dimension(240, 22);
	
	/**
	 * A flag that indicates reactivation of GUI.
	 */
	private static Obj<Boolean> guiReactivationInProgress = new Obj<Boolean>(false);

	/**
	 * Close window callback.
	 */
	private SimpleMethodRef closeCallback;
	
	/**
	 * Main frame object.
	 */
	protected static GeneratorMainFrame mainFrame;
	
	/**
	 * Boundary.
	 */
	protected static Rectangle bounds;

	/**
	 * Extended state.
	 */
	private static int extendedState;
	
	/**
	 * Show ID button state.
	 */
	private static boolean showIdButtonState;
	
	/**
	 * Main diagram name.
	 */
	private static String mainDiagramName;
	
	/**
	 * Contents of tabs.
	 */
	private static LinkedList<TabState> tabsStates;

	/**
	 * Selected tab index.
	 */
	private static int selectedTabIndex;
	
	/**
	 * Debug viewer reference
	 */
	@SuppressWarnings("unused")
	private static DebugViewer debugViewer;
	
	/**
	 * Manager for component updates.
	 */
	private UpdateManager updateManager;
	
	/**
	 * Load data.
	 * @param inputStream
	 */
	public static void serializeData(StateInputStream inputStream)
		throws IOException, ClassNotFoundException {
		
		// Load dialog bounds.
		Object object = inputStream.readObject();
		if (!(object instanceof Rectangle)) {
			throw new ClassNotFoundException();
		}
		bounds = (Rectangle) object;
		// Load dialog state.
		extendedState = inputStream.readInt();
		// Load "show ID button" state.
		showIdButtonState = inputStream.readBoolean();
		// Load editor states.
		AreaDiagramContainerPanel.splitPositionStateMain = inputStream.readInt();
		AreaDiagramContainerPanel.splitPositionStateSecondary = inputStream.readInt();
		
		int count = inputStream.readInt();
		while (count > 0) {
			AreaDiagramContainerPanel.selectedAreasIdsState.add(inputStream.readLong());
			count--;
		}
		// Load text.
		SelectAreaDialog.oldText = inputStream.readUTF();
		// Load text.
		SelectAreaResource.oldText = inputStream.readUTF();
		// Load export data.
		ExportDialog.exportFolder = inputStream.readUTF();
		// Load main diagram name.
		mainDiagramName = inputStream.readUTF();
		// Load states of tabs.
		tabsStates = readTabStates(inputStream);
		// Load selected tab index.
		selectedTabIndex = inputStream.readInt();
	}

	/**
	 * Save data.
	 * @param outputStream
	 */
	public static void serializeData(StateOutputStream outputStream)
		throws IOException {

		// Save dialog boundaries.
		outputStream.writeObject(bounds);
		// Save dialog state.
		outputStream.writeInt(extendedState);
		// Save "show ID button" state.
		outputStream.writeBoolean(showIdButtonState);
		// Save editor states.
		outputStream.writeInt(AreaDiagramContainerPanel.splitPositionStateMain);
		outputStream.writeInt(AreaDiagramContainerPanel.splitPositionStateSecondary);
		
		outputStream.writeInt(AreaDiagramContainerPanel.selectedAreasIdsState.size());
		for (long areaId : AreaDiagramContainerPanel.selectedAreasIdsState) {
			outputStream.writeLong(areaId);
		}
		// Save text.
		outputStream.writeUTF(SelectAreaDialog.oldText);
		// Save text.
		outputStream.writeUTF(SelectAreaResource.oldText);
		// Save export data.
		outputStream.writeUTF(ExportDialog.exportFolder);
		// Save main diagram name.
		outputStream.writeUTF(mainDiagramName);
		// Save cloned diagrams.
		outputStream.writeObject(tabsStates);
		// Save selected tab index.
		outputStream.writeInt(selectedTabIndex);
	}

	/**
	 * Set default data.
	 */
	public static void setDefaultData() {

		bounds = null;
		extendedState = NORMAL;
		showIdButtonState = true;
		mainDiagramName = Resources.getString("org.multipage.generator.textMainAreasTab");
		tabsStates = new LinkedList<TabState>();
		selectedTabIndex = 0;
	}

	/**
	 * Read tabs states.
	 * @param inputStream
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private static LinkedList<TabState> readTabStates(
			StateInputStream inputStream)
					throws IOException, ClassNotFoundException {
		
		LinkedList<TabState> list = new LinkedList<TabState>();
		
		// Read list object.
		Object object = inputStream.readObject();
		if (object instanceof LinkedList<?>) {
			
			// Do loop for all list items.
			for (Object item : ((LinkedList<?>) object)) {
				
				// Add item to the output list.
				if (item instanceof TabState) {
					list.add((TabState) item);
				}
			}
		}
		
		return list;
	}

	/**
	 * Set close listener.
	 * @param closeCallback
	 */
	public void setCloseListener(SimpleMethodRef closeCallback) {

		this.closeCallback = closeCallback;
	}

	/**
	 * Create tab panels.
	 */
	private void loadTabPanels() {
		try {
			
			// Reset flag.
			boolean cloned = false;
			
			int numCreated = 1;
			
			// Do loop for all diagram states.
			for (TabState tabState : tabsStates) {
				
				// Get tab type
				TabType type = tabState.type;
				
				// On areas diagram
				if (TabType.areasDiagram.equals(type) && tabState instanceof AreaDiagramTabState) {
					
					// Get extended tab state
					AreaDiagramTabState extendedTabState = (AreaDiagramTabState) tabState;
					
					// Create areas diagram panel
					AreaDiagramContainerPanel diagramPanel = createAreasDiagram(extendedTabState.title, extendedTabState.areaId);
					if (diagramPanel == null) {
						continue;
					}
					numCreated++;
					
					// Set flag.
					cloned = true;
					
					// Get inner areas diagram.
					AreaDiagramPanel innerAreasDiagram = diagramPanel.getDiagram();
					
					// Set position of areas in the inner panel.
					innerAreasDiagram.setDiagramPosition(extendedTabState.translationx,
							extendedTabState.translationy, extendedTabState.zoom);
				}
				
				// On areas tree view
				else if (TabType.areasTree.equals(type) && tabState instanceof AreasTreeTabState) {
					
					// Get extended tab state
					AreasTreeTabState extendedTabState = (AreasTreeTabState) tabState;
					
					// Create new areas tree view
					createAreasTreeView(extendedTabState.title, extendedTabState.areaId, extendedTabState.displayedArea);
					numCreated++;
					
					// Set flag.
					cloned = true;
				}
				
				// On HTML browser
				else if (TabType.monitor.equals(type) && tabState instanceof MonitorTabState) {
					
					// Get extended tab state
					MonitorTabState extendedTabState = (MonitorTabState) tabState;
					
					// Add new monitor to the tab panel
					tabPanel.addMonitor(extendedTabState.url, false);
					numCreated++;
					
					// Set flag.
					cloned = true;
				}
			}
			
			// If nothing cloned, trim selection.
			if (!cloned) {
				selectedTabIndex = 0;
			}
			if (selectedTabIndex >= numCreated) {
				selectedTabIndex = numCreated - 1;
			}
			
			// Select tab.
			try {
				tabPanel.setSelectedIndex(selectedTabIndex);
			}
			catch (Throwable e) {
				Safe.exception(e);
			}
			
			// Update window selection trayMenu.
			updateWindowSelectionMenu();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load dialog.
	 */
	protected void loadDialog() {
		try {
			
			// Set bounds.
			if (bounds == null) {
				bounds = new Rectangle(0, 0, 1040, 730);
				setBounds(bounds);
				Utility.centerOnScreen(this);
			}
			else {
				setBounds(bounds);
			}
			// Load extended state.
			setExtendedState(extendedState);
			// Show ID state.
			showIdButton.setSelected(showIdButtonState);
			onShowHideIds();
			// Lighten read only elements state.
			exposeReadOnly.setSelected(!AreaShapes.readOnlyLighter);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Save dialog.
	 */
	protected void saveDialog() {
		try {
			
			// Save bounds.
			bounds = getBounds();
			// Save extended state.
			extendedState = getExtendedState();
			// Save show ID button state.
			showIdButtonState = showIdButton.isSelected();
			// Save main dialog title.
			mainDiagramName = tabPanel.getTabTitle(0);
			// Save selected tab state.
			selectedTabIndex = tabPanel.getSelectedIndex();
			// Save tabs states.
			saveTabsStates();
			
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Save tabs states.
	 */
	private void saveTabsStates() {
		try {
			
			// Load tab states
			tabsStates = tabPanel.getTabsStates();
			
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Tool bar.
	 */
	protected JToolBar toolBar;

	/**
	 * AreasDiagram.
	 */
	private AreaDiagramContainerPanel mainAreaDiagramEditor;

	/**
	 * Status bar.
	 */
	private MainStatusBar statusBar;

	/**
	 * Connection watch dog.
	 */
	private ActionListener watchdog;

	/**
	 * Connection watch dog interval in milliseconds.
	 */
	private int watchdogMs = 60000;
	
	/**
	 * Elements properties container panel.
	 */
	private PropertiesPanel propertiesPanel;
	
	/**
	 * AreasDiagram and properties split panel.
	 */
	private SplitProperties splitDiagramProperties;

	/**
	 * Timer watch dog.
	 */
	private javax.swing.Timer timerWatchDog;
	
	/**
	 * Tab panel.
	 */
	private TabPanel tabPanel;
	
	/**
	 * Customize colors.
	 */
	private CustomizedColors customizeColors;
	
	/**
	 * Customize controls.
	 */
	private CustomizedControls cutomizeControls;

	/**
	 * Show id toggle button.
	 */
	private JToggleButton showIdButton;

	/**
	 * Light read only elements.
	 */
	private JToggleButton exposeReadOnly;
	
	/**
	 * Show/hide dialog navigator.
	 */
	private JToggleButton toggleDialogNavigator;
	
	/**
	 * Search dialog.
	 */
	private SearchDialog searchDialog;

	/**
	 * User value.
	 */
	private String userValue = "";

	/**
	 * Undo redo buttons.
	 */
	private JButton undoButton;
	private JButton redoButton;

	/**
	 * Window selection trayMenu.
	 */
	private JMenu windowSelectionMenu;

	/**
	 * Reset area tree copy timer.
	 */
	private javax.swing.Timer resetAreaTreeCopyTimer;
	
	/**
	 * Toggle debug button.
	 */
	private JToggleButton toggleDebug;

	/**
	 * Area tree data to copy.
	 */
	private static AreaTreesData areaTreeDataToCopy = null;
	
	/**
	 * Previous update messages.
	 */
	private LinkedList<Message> previousUpdateMessages = new LinkedList<Message>();

	/**
	 * Development mode menu item.
	 */
	private JCheckBoxMenuItem  developmentMode = null;

	/**
	 * Constructor.
	 */
	public GeneratorMainFrame() {
		try {
			
			// Set static member.
			mainFrame = this;
			
			// Set close action.
			this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			
			// Set window description and icon.
			setTitle(ProgramGenerator.getApplicationTitle());
			setIconImage(Images.getImage("org/multipage/generator/images/main_icon.png"));
			
			// Create update manager.
			createUpdateManager();
			// Register as updatable component.
			registerForUpdateWithFrame(this, UpdateManager.HIGH_PRIORITY + 100);
			
			// Set middle layer login listener.
			setMiddleListener();
			// Create components.
			createComponents();
			// Create trayMenu.
			createMenu();
			// Create tool bar.
			createToolBar();
			// Create status bar.
			createStatusBar();
			// Set connection watch dog.
			createConnectionWatchDog();
			// Set callback functions.
			setCallbacks();
			// Set listeners.
			setListeners();
			// Set timers.
			setTimers();
			// Initialize log window.
			LoggingDialog.initialize(this);
			// Load dialog.
			loadDialog();
			// Load tab panel contents.
			loadTabPanels();
			
			// Add top most window toggle button.
			TopMostButton.setMainWindow(this);
			TopMostButton.add(this, toolBar);
						
			// Update all components.
			updateAllWithFrame();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set timers.
	 */
	private void setTimers() {
		try {
			
			// Reset area tree copy timer.
			resetAreaTreeCopyTimer = new javax.swing.Timer(60000, (e) -> {
				
				areaTreeDataToCopy = null;
			});
			resetAreaTreeCopyTimer.setRepeats(false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set callback functions.
	 */
	private void setCallbacks() {
		try {
			
			SyncMain.setReactivateGuiCallback(() -> {
				reactivateGui();
			});
			
			AreaServer.setDevelopmentModeLambda(() -> {
				return isDevelopmentMode();
			});
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

			tabPanel.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					Safe.tryOnChange(tabPanel, () -> {
						Safe.invokeLater(() -> {
							onChangeTab();
						});
					});
				}
			});
			
			tabPanel.setRemoveListener(() -> {
					
				// Update window selection trayMenu.
				updateWindowSelectionMenu();
			});
			
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowOpened(WindowEvent e) {
					init();
				}
				public void windowClosing(WindowEvent e) {
					closeWindow();
				}
			});
			
			addWindowStateListener(new WindowStateListener() {
				@Override
				public void windowStateChanged(WindowEvent e) {
					try {
			
						// If window state changed repaint properties.
						splitDiagramProperties.redraw();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			
			// Close splash windows.
			addComponentListener(new ComponentAdapter() {
				@Override
				public void componentShown(ComponentEvent e) {
					
					Utility.closeSplash();
				}
			});
			
			// Create debug viewer
			debugViewer = DebugViewer.getInstance(this);
			
			// Receive the "display area properties" message.
			ApplicationEvents.receiver(this, GuiSignal.displayAreaProperties, message -> {
				
				setPropertiesVisible(true);
			});
						
			// Receive the "set home area" message.
			ApplicationEvents.receiver(this, GuiSignal.displayHomePage, message -> {
			
				monitorHomePage();
			});
			
			// Set Sync lambda functions.
			SyncMain.setCloseEvent(() -> {
				
				closeWindow();
			});
			
			// "Terminate" event receiver.
			ApplicationEvents.receiver(this, GuiSignal.terminate, message -> {
				closeWindow();
			});
			
			// "Reactivate GUI" event receiver.
			ApplicationEvents.receiver(this, GuiSignal.reactivateGui, message -> {
				try {
			
					// Initialize focused component.
					Component focusedComponent = null;
					
					// Try to get focused component from the event information.
					Object relatedInfo = message.relatedInfo;
					if (relatedInfo instanceof Component) {
						focusedComponent = (Component) relatedInfo;
					}
					
					// Do reactivation.
					invokeReactivationOfGui(focusedComponent);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// "Focus area" event receiver.
			ApplicationEvents.receiver(this, GuiSignal.focusArea, message -> {
				try {
		
					// Get diagram panel.
					AreaDiagramContainerPanel areasDiagramPanel = getFrame().getVisibleAreasEditor();
					
					// Try to focus area using coordinates.
					try {
						// Get coordinates.
						AreaCoordinatesTableItem coordinatesItem = message.getAdditionalInfo(0);
						
						// Focus coordinates.
						if (coordinatesItem != null) {
							areasDiagramPanel.getDiagram().focus(coordinatesItem.coordinate, null);
						}
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					
					// Try to focus area using area identifier.
					try {
						// Get area ID.
						Long areaId = message.getRelatedInfo();
						
						// Focus coordinates.
						if (areaId != null) {
							areasDiagramPanel.focusArea(areaId);
						}
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};	
			});
			
			// Receive the "debugging" signal.
			ApplicationEvents.receiver(this, GuiSignal.debugging, message -> {
				try {
					
					// Avoid receiving the signal from current dialog window.
					if (this.equals(message.source)) {
						return;
					}
					
					// Get flag value.
					Boolean debuggingEnabled = message.getRelatedInfo();
					if (debuggingEnabled == null) {
						return;
					}
					
					// Select or unselect the debug button.
					toggleDebug.setSelected(debuggingEnabled);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// Receive the "edit area" signal.
			ApplicationEvents.receiver(this, GuiSignal.editArea, message -> {
				try {
			
					// Get area ID.
					Long areaId = message.getRelatedInfo();
		            
		            // Open edit dialog for the area.
		            if (areaId!= null) {
		                Area area = ProgramGenerator.getArea(areaId);
		                if (area != null) {
		                	
		                	AreaEditorFrameBase areaEditor = ProgramGenerator.newAreaEditor(GeneratorMainFrame.this, area);
		                	areaEditor.setVisible(true);
		                }
		            }
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// Receive the "edit resource" signal.
			ApplicationEvents.receiver(this, GuiSignal.editResource, message -> {
				try {
			
					// Get sent resource ID.
					Long resourceId = message.getRelatedInfo();
					if (resourceId == null) {
						return;
					}
					
					// Open text resource editor.
					editTextResource(resourceId);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};	
			});
			
			// Receive the "edit slot" signal.
			ApplicationEvents.receiver(this, GuiSignal.editSlot, message -> {
				try {
					
					// Get sent slot ID.
					Long slotId = message.getRelatedInfo();
					if (slotId == null) {
						return;
					}
					
					editSlot(slotId);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// Receive the "edit area slots" signal.
			ApplicationEvents.receiver(this, GuiSignal.editAreaSlots, message -> {
				try {
			
					// Get sent area ID.
					Long areaId = message.getRelatedInfo();
					if (areaId == null) {
						return;
					}
					
					editAreaSlots(areaId);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// Receive the "dialog navigator closed" signal.
			ApplicationEvents.receiver(this, GuiSignal.dialogNavigatorClosed, message -> {
				Safe.tryUpdate(toggleDialogNavigator, () -> {
					
					toggleDialogNavigator.setSelected(false);
				});
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
 	}

	/**
	 * Remove listeners.
	 */
	protected void removeListeners() {
		try {
			
			// Remove event receivers.
			ApplicationEvents.removeReceivers(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Reactivate GUI.
	 */
	public static void reactivateGui() {
		try {
			
			// Get current focused control.
			Component focusedControl = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
			
			// Propagate the event with focused control.
			ApplicationEvents.transmit(GeneratorMainFrame.class, GuiSignal.reactivateGui, focusedControl);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Invoke reactivation of the GUI.
	 * @param focusedComponent - currently focused GUI component.
	 */
	public void invokeReactivationOfGui(Component focusedComponent) {
		try {
			
			// Coalesce reactivation events.
			if (guiReactivationInProgress.ref) {
				return;
			}
			
			// Set the flag.
			guiReactivationInProgress.ref = true;
			
			// Reactivate GUI.
			
			// 1. Enable the frame window.
			Safe.invokeLater(() -> {
				getFrame().setEnabled(true);
			});
			
			// 2. Set always on top.
			Safe.invokeLater(() -> {
				getFrame().setAlwaysOnTop(true);
			});
			
			// 3. Sleep for a while.
			Safe.invokeLater(() -> {
				try {
					Thread.sleep(100);
				}
				catch (Throwable e) {
					Safe.exception(e);
				}
			});
			
			// 4. Reset always on top.
			Safe.invokeLater(() -> {
				getFrame().setAlwaysOnTop(false);
			});
			
			// 5. Ensure the frame is visible.
			Safe.invokeLater(() -> {
				getFrame().setVisible(true);
			});
			
			// 6. Sleep for a while.
			Safe.invokeLater(() -> {
				try {
					Thread.sleep(100);
				}
				catch (Throwable e) {
					Safe.exception(e);
				}
			});
			
			// 7. Bring the frame to front.
			Safe.invokeLater(() -> {
				getFrame().toFront();
			});
			
			// 8. Restore focus.
			Safe.invokeLater(() -> {
				if (focusedComponent != null) {
					focusedComponent.requestFocusInWindow();
				}
			});
			
			// 9. Repaint the frame.
			Safe.invokeLater(() -> {
				getFrame().repaint();
			});
			
			// Reset the flag.
			Safe.invokeLater(() -> {
				guiReactivationInProgress.ref = false;
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};	
	}

	/**
	 * Create connection watch dog.
	 */
	private void createConnectionWatchDog() {
		try {
			
			// Schedule timer.
			watchdog = new ActionListener() {
				// Middle.
				private Middle middle = MiddleUtility.newMiddleInstance();
				@Override
				public void actionPerformed(ActionEvent event) {
					// Check login and get number of connections.
					try {
						if (GeneratorMainFrame.getFrame() != null) {
							
							Properties login = ProgramBasic.getLoginProperties();
							
							boolean isConnection = middle.checkLogin(login).isOK();
							statusBar.setConnection(isConnection);
							
							// Get number of connections.
							Obj<Integer> number = new Obj<Integer>(0);
							middle.loadNumberConnections(login, number);
							statusBar.setNumberConnections(number.ref);
						}
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
				}
			};
			timerWatchDog = new javax.swing.Timer(watchdogMs, watchdog);
			timerWatchDog.setInitialDelay(0);
			timerWatchDog.setRepeats(true);
			timerWatchDog.start();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};		
	}

	/**
	 * Create status bar.
	 */
	private void createStatusBar() {
		try {
			
			statusBar = new MainStatusBar();
			statusBar.setLoginProperties(ProgramBasic.getLoginProperties());
			this.add(statusBar, BorderLayout.PAGE_END);
			
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create components.
	 */
	private void createComponents() {
		try {
			
			mainAreaDiagramEditor = newAreasDiagramEditor();
			tabPanel = newTabPanel(mainAreaDiagramEditor);
			propertiesPanel = newPropertiesPanel();
			splitDiagramProperties = new SplitProperties(tabPanel, propertiesPanel);
			customizeColors = new CustomizedColors(this);
			cutomizeControls = newCustomizedControls(this);
			searchDialog = new SearchDialog(this, ProgramGenerator.getAreasModel());
			
			add(splitDiagramProperties, BorderLayout.CENTER);			
			
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Create new areas diagram editor object.
	 * @return
	 */
	protected AreaDiagramContainerPanel newAreasDiagramEditor() {
		
		try {
			// Create new panel and initialize it.
			AreaDiagramContainerPanel diagramPanel = new AreaDiagramContainerPanel();
			diagramPanel.init();
			
			return diagramPanel;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Create new customized controls object.
	 * @param owner
	 * @return
	 */
	protected CustomizedControls newCustomizedControls(Window owner) {
		
		try {
			return new CustomizedControls(owner);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Create new tab panel object.
	 * @param panel
	 * @return
	 */
	protected TabPanel newTabPanel(JPanel panel) {
		
		try {
			return new TabPanel(panel);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Create new element properties object.
	 * @return
	 */
	protected PropertiesPanel newPropertiesPanel() {
		
		try {
			return new PropertiesPanel();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Set middle listener.
	 */
	private void setMiddleListener() {
		try {
			
			ProgramBasic.getMiddle().addListener(new MiddleListener(){
				@Override
				public void onLogin(boolean ok) {
					// Delegate call.
					onLoginCheck(ok);
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Close main frame window.
	 */
	public void closeWindow() {
		try {
			
			// Unregister from updatable components.
			unregisterFromUpdateWithFrame(this);
			
			// Stop GUI watchdog.
			GeneratorMain.stopGuiWatchDog();
			
			// Call on close method.
			onClose();
			
			// Close properties.
			propertiesPanel.setNoProperties();
			splitDiagramProperties.minimize();
			
			// Invoke dialog saving later.
			Safe.invokeLater(() -> {
				
				// Save dialog.
				saveDialog();
				// Dispose dialog.
				mainAreaDiagramEditor.dispose();
				// Dispose splitter.
				splitDiagramProperties.dispose();
				// Dispose dialog.
				cutomizeControls.disposeDialog();
				// Dispose dialog.
				customizeColors.disposeDialog();
				// Dispose properties.
				propertiesPanel.close();
				// Run callback.
				if (closeCallback != null) {
					closeCallback.run();
				}
				removeListeners();
				// Dispose window.
				dispose();
				// Exit application.
				System.exit(0);
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On change tab.
	 */
	private void onChangeTab() {
		try {
			
			// Get visible area editor.
			AreaDiagramContainerPanel editor = getVisibleAreasEditor();
			if (editor != null) {
				editor.getDiagram().updateUndoRedo();
			}
			
			// Update window selection trayMenu.
			updateWindowSelectionMenu();
			
			// Get current tab panel.
			int tabIndex = tabPanel.getSelectedIndex();
			if (tabIndex >= 0) {
				
				Component tabComponent = tabPanel.getComponentAt(tabIndex);
				if (tabComponent instanceof TabItemInterface) {
					
					TabItemInterface tabItem = (TabItemInterface) tabComponent;
					HashSet<Long> selectedAreaIds = tabItem.getSelectedTabAreaIds();
					
					// Transmit event.
					ApplicationEvents.transmit(this, GuiSignal.displayAreaProperties, selectedAreaIds);
				}
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Initialization.
	 */
	public void init() {
		try {
			
			splitDiagramProperties.init();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Create trayMenu.
	 */
	protected void createMenu() {
		try {
			
			// Create menu components.
			JMenuBar menuBar = new JMenuBar();
			
			JMenu file = new JMenu(Resources.getString("org.multipage.generator.menuMainFile"));
			JMenu edit = new JMenu(Resources.getString("org.multipage.generator.menuEditors"));
			JMenu tools = new JMenu(Resources.getString("org.multipage.generator.menuTools"));
			JMenu window = new JMenu(Resources.getString("org.multipage.generator.menuWindow"));
			windowSelectionMenu = new JMenu(Resources.getString("org.multipage.generator.menuWindowSelection"));
			JMenu help = new JMenu(Resources.getString("org.multipage.generator.menuMainHelp"));
	
			// Create trayMenu tree.
			menuBar.add(file);
			menuBar.add(edit);
			menuBar.add(tools);
			menuBar.add(window);
			menuBar.add(help);
			
			this.setJMenuBar(menuBar);
			
			// Create optional debug trayMenu.
			if (ProgramGenerator.isDebug()) {
				
				JMenu debugMenu = new JMenu(Resources.getString("org.multipage.generator.menuDebug"));
				menuBar.add(debugMenu);
				
				developmentMode = new JCheckBoxMenuItem(Resources.getString("org.multipage.generator.menuDevelopmentMode"));
				JMenuItem test = new JMenuItem(Resources.getString("org.multipage.generator.menuTest"));
				JMenuItem setUserValue = new JMenuItem(Resources.getString("org.multipage.generator.menuSetUserValue"));
				JMenuItem loggingDialog = new JMenuItem(Resources.getString("org.multipage.generator.menuLoggingDialog"));
				JMenuItem loggingConsoles = new JMenuItem(Resources.getString("org.multipage.generator.menuLoggingConsoles"));
				
				developmentMode.setSelected(true);
				
				debugMenu.add(developmentMode);
				debugMenu.add(setUserValue);
				debugMenu.add(test);
				debugMenu.add(loggingDialog);
				debugMenu.add(loggingConsoles);
				
				setUserValue.addActionListener(e -> {
					onSetUserValue();
				});
				test.addActionListener(e -> {
				    onTest();
	            });
				loggingDialog.addActionListener(e -> {
					onLoggingDialog();
				});
				loggingConsoles.addActionListener(e -> {
					onLoggingConsoles();
				});
			}
			
			// Conditionally created login trayMenu item.
			JMenuItem loginDialog = null;
			if (ProgramBasic.isUsedLogin()) {
				loginDialog = new JMenuItem(Resources.getString("org.multipage.generator.menuLoginDialog"));
					loginDialog.setAccelerator(KeyStroke.getKeyStroke("control L"));
					loginDialog.setIcon(Images.getIcon("org/multipage/generator/images/login_small.png"));
			}
			
		
			// Add trayMenu items.
			JMenuItem fileMenuExit = new JMenuItem(Resources.getString("org.multipage.generator.menuMainFileExit"));
				fileMenuExit.setAccelerator(KeyStroke.getKeyStroke("control X"));
				fileMenuExit.setIcon(Images.getIcon("org/multipage/generator/images/exit_icon.png"));
				
			JMenuItem  helpMenuAbout = new JMenuItem(Resources.getString("org.multipage.generator.menuMainHelpAbout"));
				helpMenuAbout.setAccelerator(KeyStroke.getKeyStroke("control H"));
				helpMenuAbout.setIcon(Images.getIcon("org/multipage/generator/images/about_small.png"));
				
			JMenuItem updateData = new JMenuItem(Resources.getString("org.multipage.generator.menuUpdateData"));
				updateData.setAccelerator(KeyStroke.getKeyStroke("control U"));
				updateData.setIcon(Images.getIcon("org/multipage/generator/images/update2_icon.png"));
				
			JMenuItem render = new JMenuItem(Resources.getString("org.multipage.generator.menuRender"));
				render.setAccelerator(KeyStroke.getKeyStroke("control R"));
				render.setIcon(Images.getIcon("org/multipage/generator/images/render_small.png"));
				
			JMenuItem closeAllWindows = new JMenuItem(Resources.getString("org.multipage.generator.menuCloseAllWindows"));
				closeAllWindows.setAccelerator(KeyStroke.getKeyStroke("control shift A"));
				closeAllWindows.setIcon(Images.getIcon("org/multipage/generator/images/close_all.png"));
				
			JMenuItem toolsSearch = new JMenuItem(Resources.getString("org.multipage.generator.menuFind"));
				toolsSearch.setAccelerator(KeyStroke.getKeyStroke("control F"));
				toolsSearch.setIcon(Images.getIcon("org/multipage/generator/images/search2_icon.png"));
				
			JMenuItem toolsCustomizeColors = new JMenuItem(Resources.getString("org.multipage.generator.menuToolsCustomizeColors"));
				toolsCustomizeColors.setAccelerator(KeyStroke.getKeyStroke("alt C"));
				toolsCustomizeColors.setIcon(Images.getIcon("org/multipage/generator/images/colors_icon.png"));
				
			JMenuItem toolsCustomizeControls = new JMenuItem(Resources.getString("org.multipage.generator.menuToolsCustomizeConstrols"));
				toolsCustomizeControls.setAccelerator(KeyStroke.getKeyStroke("shift alt C"));
				toolsCustomizeControls.setIcon(Images.getIcon("org/multipage/generator/images/controls_icon.png"));
				
			JMenuItem toolsMimeTypes = new JMenuItem(Resources.getString("org.multipage.generator.menuToolsMimeTypes"));
				toolsMimeTypes.setAccelerator(KeyStroke.getKeyStroke("control M"));
				toolsMimeTypes.setIcon(Images.getIcon("org/multipage/generator/images/mime_icon.png"));
				
			JMenuItem toolsSettings = new JMenuItem(Resources.getString("org.multipage.generator.menuToolsSettings"));
				toolsSettings.setAccelerator(KeyStroke.getKeyStroke("alt S"));
				toolsSettings.setIcon(Images.getIcon("org/multipage/generator/images/settings_icon.png"));
				
			JMenuItem editResources = new JMenuItem(Resources.getString("org.multipage.generator.menuEditResources"));
				editResources.setAccelerator(KeyStroke.getKeyStroke("alt R"));
				editResources.setIcon(Images.getIcon("org/multipage/generator/images/resources_icon.png"));
				
			JMenuItem editTranslators = new JMenuItem(Resources.getString("org.multipage.generator.menuEditTranslations"));
				editTranslators.setAccelerator(KeyStroke.getKeyStroke("control T"));
				editTranslators.setIcon(Images.getIcon("org/multipage/generator/images/translator_icon.png"));
				
			JMenuItem editFileNames = new JMenuItem(Resources.getString("org.multipage.generator.menuEditFileNames"));
				editFileNames.setAccelerator(KeyStroke.getKeyStroke("alt F"));
				editFileNames.setIcon(Images.getIcon("org/multipage/generator/images/filenames_icon.png"));
				
			JMenuItem exportArea = new JMenuItem(Resources.getString("org.multipage.generator.menuFileExport"));
				exportArea.setAccelerator(KeyStroke.getKeyStroke("control E"));
				exportArea.setIcon(Images.getIcon("org/multipage/generator/images/export2_icon.png"));
				
			JMenuItem importArea = new JMenuItem(Resources.getString("org.multipage.generator.menuFileImport"));
				importArea.setAccelerator(KeyStroke.getKeyStroke("control I"));
				importArea.setIcon(Images.getIcon("org/multipage/generator/images/import2_icon.png"));
	
			JMenuItem toolsCheckRenderedFiles = new JMenuItem(Resources.getString("org.multipage.generator.menuToolsCheckRenderedFiles"));
				toolsCheckRenderedFiles.setAccelerator(KeyStroke.getKeyStroke("control alt C"));
				toolsCheckRenderedFiles.setIcon(Images.getIcon("org/multipage/generator/images/check_ambiguity_icon.png"));
			
			JMenuItem  helpMenuManualGui = new JMenuItem(Resources.getString("org.multipage.generator.menuMainHelpManualGui"));
				helpMenuManualGui.setAccelerator(KeyStroke.getKeyStroke("alt shift M"));
				helpMenuManualGui.setIcon(Images.getIcon("org/multipage/generator/images/manual.png"));
				
			JMenuItem  helpMenuManualMaclan = new JMenuItem(Resources.getString("org.multipage.generator.menuMainHelpManualGmpl"));
			helpMenuManualMaclan.setAccelerator(KeyStroke.getKeyStroke("alt shift G"));
			helpMenuManualMaclan.setIcon(Images.getIcon("org/multipage/generator/images/manual.png"));
				
			JMenuItem  helpMenuIntroductoryVideo = new JMenuItem(Resources.getString("org.multipage.generator.menuMainHelpVideo"));
				helpMenuIntroductoryVideo.setAccelerator(KeyStroke.getKeyStroke("alt shift V"));
				helpMenuIntroductoryVideo.setIcon(Images.getIcon("org/multipage/generator/images/video.png"));
				
			// Set size.
			fileMenuExit.setPreferredSize(preferredMenuSize);
			helpMenuAbout.setPreferredSize(preferredMenuSize);
			closeAllWindows.setPreferredSize(GeneratorMainFrame.preferredMenuSize);
			toolsCustomizeColors.setPreferredSize(GeneratorMainFrame.preferredMenuSize);
			editResources.setPreferredSize(GeneratorMainFrame.preferredMenuSize);
			
			// Create trayMenu tree.
			if (loginDialog != null) {
				file.add(loginDialog);
			}
	
			file.add(importArea);
			file.add(exportArea);
			file.add(render);
			file.add(updateData);
			file.add(fileMenuExit);
			
			help.add(helpMenuIntroductoryVideo);
			help.add(helpMenuManualMaclan);
			help.add(helpMenuManualGui);
			help.add(helpMenuAbout);
			
			tools.add(toolsSearch);
			addSearchInTextResourcesMenuItem(tools);
			tools.add(toolsCheckRenderedFiles);
			tools.add(toolsCustomizeColors);
			tools.add(toolsCustomizeControls);
			tools.add(toolsSettings);
			
			window.add(closeAllWindows);
			window.add(windowSelectionMenu);
			
			edit.add(editTranslators);
			edit.add(editFileNames);
			edit.add(editResources);
			addEditEnumerationsMenuItem(edit);
			addEditVersionsMenuItem(edit);
			edit.add(toolsMimeTypes);
			
			// Set listeners.
			if (loginDialog != null) {
				loginDialog.addActionListener(e -> {
					onLoginProperties();
				});
			}
			// Add action listeners.
			fileMenuExit.addActionListener(e -> {
				onFileExitMenu();
			});
			helpMenuAbout.addActionListener(e -> {
				onHelpAboutMenu();
			});
			updateData.addActionListener(e -> {
				onUpdate();
			});
			closeAllWindows.addActionListener(e -> {
				onCloseAllWindows();
			});
			toolsCustomizeColors.addActionListener(e -> {
				onCustomizeColors();
			});
			toolsCustomizeControls.addActionListener(e -> {
				onCustomizeControls();
			});
			toolsMimeTypes.addActionListener(e -> {
				onMimeTypesEditor();
			});
			toolsSettings.addActionListener(e -> {
				onSettings();
			});
			editResources.addActionListener(e -> {
				onResources();
			});
			editTranslators.addActionListener(e -> {
				onTranslator();
			});
			final Component thisComponent = this;
			render.addActionListener(e -> {
				onRender(thisComponent);
			});
			exportArea.addActionListener(e -> {
				onExport();
			});
			importArea.addActionListener(e -> {
				onImport();
			});
			editFileNames.addActionListener(e -> {
				onEditFileNames();
			});
			toolsSearch.addActionListener(e -> {
				onSearch();
			});
	
			toolsCheckRenderedFiles.addActionListener(e -> {
				onCheckRenderedFiles();
			});
			helpMenuManualGui.addActionListener(e -> {
				onManualGui();
			});
			helpMenuManualMaclan.addActionListener(e -> {
				onManualForMaclan();
			});
			helpMenuIntroductoryVideo.addActionListener(e -> {
				onVideo();
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Return true if the development mode is selected.
	 * @return
	 */
	public static boolean isDevelopmentMode() {
		
		try {
			// Get main frame.
			GeneratorMainFrame mainFrame = getFrame();
			if (mainFrame == null) {
				return false;
			}
			
			// Get menu item.
			JCheckBoxMenuItem developmentMode = mainFrame.developmentMode;
			
			// Return selection of the menu item.
			if (developmentMode == null) {
				return false;
			}
			boolean isSelected = developmentMode.isSelected();
			return isSelected;
		}
		catch (Throwable e) {
            Safe.exception(e);
            return false;
        }
	}
	
	/**
	 * On logging dialog.
	 */
	protected void onLoggingDialog() {
		try {
			
			LoggingDialog.showDialog(GeneratorMainFrame.getFrame());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	private void onLoggingConsoles() {
		try {
			
			// Initialize logging consoles.
			LogConsoles.main(new String [] {});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Add search in text resources trayMenu item.
	 * @param trayMenu
	 */
	protected void addSearchInTextResourcesMenuItem(JMenu menu) {
		
		// Override this method.
	}

	/**
	 * Add edit versions trayMenu item.
	 * @param trayMenu
	 */
	protected void addEditVersionsMenuItem(JMenuItem menu) {
		
		// Override this method.
	}

	/*
	 * Add edit enumerations trayMenu item.
	 */
	protected void addEditEnumerationsMenuItem(JMenu menu) {
		
		// Override this method.
	}

	/**
	 * On mime types editor.
	 */
	protected void onMimeTypesEditor() {
		try {
			
			MimeTypesEditor.showEditor(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On set user value.
	 */
	protected void onSetUserValue() {
		try {
			
			String message = String.format(Resources.getString("org.multipage.generator.messageInputUserValue"),
					userValue);
			String text = JOptionPane.showInputDialog(this, message, userValue);
			if (text == null) {
				return;
			}
			
			userValue = text;
			
			// Repaint diagram.
			repaint();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Customize controls.
	 */
	protected void onCustomizeControls() {
		try {
			
			// Open control customize dialog.
			cutomizeControls.setVisible(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On customize colors.
	 */
	public void onCustomizeColors() {
		try {
			
			// Open customize color dialog.
			customizeColors.setVisible(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On close all windows.
	 */
	protected void onCloseAllWindows() {
		try {
			
			tabPanel.closeAll();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create tool bar..
	 */
	protected void createToolBar() {
		try {
			
			// Create StatusBar.
			toolBar = new JToolBar(Resources.getString("org.multipage.generator.textMainToolBar"));
			this.add(toolBar, BorderLayout.PAGE_START);
			
			toolBar.setFloatable(false);
			
			// Add buttons. 24 x 24 icons
			toolBar.addSeparator();
			if (ProgramBasic.isUsedLogin()) {
				ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/login_icon.png", "org.multipage.generator.tooltipLoginWindow", () -> onLoginProperties());
				toolBar.addSeparator();
			}
			showIdButton = ToolBarKit.addToggleButton(toolBar, "org/multipage/generator/images/show_hide_id.png", "org.multipage.generator.tooltipShowHideIds", () -> onShowHideIds());
			addHideSlotsButton(toolBar);
			toolBar.addSeparator();
			exposeReadOnly = ToolBarKit.addToggleButton(toolBar, "org/multipage/generator/images/enable_remove.png", "org.multipage.generator.tooltipAreasUnprotected", () -> onExposeReadOnly());
			toolBar.addSeparator();
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/reload_icon.png", "org.multipage.generator.tooltipUpdate", () -> onUpdate());
			toolBar.addSeparator();
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/center_icon.png", "org.multipage.generator.tooltipFocusWhole", () -> onFocusBasicArea());
			toolBar.addSeparator();
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/focus_tab_big.png", "org.multipage.generator.tooltipFocus", () -> onFocusTabArea());
			toolBar.addSeparator();
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/home_icon.png", "org.multipage.generator.tooltipFocusHome", () -> onFocusHome());
			toolBar.addSeparator();
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/binoculars.png", "org.multipage.generator.tooltipSearch", () -> onSearch());
			toolBar.addSeparator();
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/select_all_large.png", "org.multipage.generator.tooltipSelectAll", () -> onSelectAll());
			toolBar.addSeparator();
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/deselect_all_large.png", "org.multipage.generator.tooltipUnselectAll", () -> onUnselectAll());
			toolBar.addSeparator();
			toggleDialogNavigator = ToolBarKit.addToggleButton(toolBar, "org/multipage/generator/images/dialog_navigator.png", "org.multipage.generator.tooltipDialogNavigator", () -> onDialogNavigator());
			toolBar.addSeparator();
			undoButton = ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/undo_focus.png", "org.multipage.generator.tooltipUndoFocus", () -> onUndoFocus());
			toolBar.addSeparator();
			redoButton = ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/redo_focus.png", "org.multipage.generator.tooltipRedoFocus", () -> onRedoFocus());
			toolBar.addSeparator();
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/render.png", "org.multipage.generator.tooltipRenderHtmlPages", () -> onRenderTool());
			toolBar.addSeparator();
			toggleDebug = ToolBarKit.addToggleButton(toolBar,  "org/multipage/generator/images/debug.png", "org.multipage.generator.tooltipEnableDisplaySourceCode", () -> onToggleDebug());
			toolBar.addSeparator();
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/display_home_page.png", "org.multipage.generator.tooltipMonitorHomePage", () -> onMonitorHomePage());
			toolBar.addSeparator();
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/revert.png", "org.multipage.generator.tooltipRevertExternalSourceCodes", () -> onRevert());
			toolBar.addSeparator();
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/about_icon.png", "org.multipage.generator.tooltipAbout", () -> onHelpAboutMenu());
			
			// Set undo and redo references.
			getAreaDiagram().setUndoRedoComponents(undoButton, redoButton);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On switch on or off debugging
	 */
	public void onToggleDebug() {
		try {
			
			final boolean selected = toggleDebug.isSelected();
			
			// Switch on or off debugging of PHP code
			Settings.setEnableDebugging(selected);
			
			// Transmit the "enable / disable" signal.
			ApplicationEvents.transmit(this, GuiSignal.debugging, selected);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Add hide slots button.
	 * @param toolBar
	 */
	protected void addHideSlotsButton(JToolBar toolBar) {
		
		// Do nothing.
	}

	/**
	 * On file exit trayMenu item.
	 */
	public void onFileExitMenu() {
		try {
			
			// Call events.
			closeWindow();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On help about trayMenu item.
	 */
	public void onHelpAboutMenu() {
		try {
			
			// Open about dialog window.
			AboutDialogBase aboutDlg = ProgramGenerator.newAboutDialog(this);
			aboutDlg.setVisible(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Focus on the Basic Area.
	 */
	public void onFocusBasicArea() {
		try {
			
			ApplicationEvents.transmit(this, AreaDiagramPanel.class, GuiSignal.focusBasicArea);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On login.
	 * @param ok
	 */
	public void onLoginCheck(boolean ok) {
		try {

			if (statusBar != null) {
				statusBar.setConnection(ok);
				
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/* (non-Javadoc)
	 * @see org.multipage.gui.MainFrame#onClose()
	 */
	protected void onClose() {
		try {
			
			// Stop dispatching events.
			ApplicationEvents.stopDispatching();
			
			// Cancel watch dog.
			if (timerWatchDog != null) {
				timerWatchDog.stop();
			}
			
			// Save windows data.
			customizeColors.saveIfDirty(false);
			
			// Close navigator window.
			DialogNavigator.closeNavigator();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set main tool bar text.
	 */
	public void setMainToolBarText(String text) {
		try {
			
			if (statusBar != null) {
				statusBar.setMainText(text);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On login dialog.
	 */
	public void onLoginProperties() {
		try {
			
			// If the database changes, reset diagram position and zoom.
			Properties loginProperties = ProgramBasic.getLoginProperties();
			String oldDatabaseName = loginProperties.getProperty("database");
			
			ProgramBasic.setAttempts(3);
			
			String title = Resources.getString(
					ProgramGenerator.isExtensionToBuilder() ? "builder.textLoginDialog" :
						"org.multipage.generator.textLoginDialog");
			
			ProgramBasic.loginDialog(this, title);
			statusBar.setLoginProperties(ProgramBasic.getLoginProperties());
			
			loginProperties = ProgramBasic.getLoginProperties();
			String newDatabaseName = loginProperties.getProperty("database");
			
			if (!newDatabaseName.equals(oldDatabaseName)) {
				getAreaDiagram().resetDiagramPosition();
			}	
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * @return the tabPanel
	 */
	public TabPanel getTabPanel() {
		return tabPanel;
	}

	/**
	 * Get showing tab area ID or null if the area doesn't exist
	 * @return
	 */
	public static Long getTabAreaId() {
		
		try {
			Long tabAreaId = getFrame().tabPanel.getTopAreaIdOfSelectedTab();
			return tabAreaId;
		}
		catch (Throwable e) {
			Safe.exception(e);
            return null;
        }
	}
	
	/**
	 * On show/hide IDs.
	 */
	public void onShowHideIds() {
		try {
			
			// Show or hide displayed IDs.
			boolean showIds = showIdButton.isSelected();
			// Inform areas and slots to use IDs in its descriptions.
			Area.setShowId(showIds);
			Slot.setShowId(showIds);
			
			// Transmit show/hide IDs signal.
			ApplicationEvents.transmit(this, GuiSignal.showOrHideIds, showIds);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * @return the areaDiagramEditor
	 */
	public AreaDiagramPanel getAreaDiagram() {
		
		try {
			return mainAreaDiagramEditor.getDiagram();
		}
		catch (Throwable e) {
			Safe.exception(e);
            return null;
        }
	}
	
	/**
	 * Area diagram editor.
	 */
	public AreaDiagramContainerPanel getAreaDiagramEditor() {
		
		try {
			AreaDiagramContainerPanel editor = getVisibleAreasEditor();
			if (editor != null) {
				return editor;
			}
			return mainAreaDiagramEditor;
		}
		catch (Throwable e) {
            Safe.exception(e);
            return null;
        }
	}
	
	/**
	 * On search.
	 */
	public void onSearch() {
		try {
			
			// Do modeless dialog.
			searchDialog.setVisible(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set properties visible.
	 * @param visible
	 */
	public void setPropertiesVisible(boolean visible) {
		try {
			
			if (visible) {
				splitDiagramProperties.maximize();
			}
			else {
				splitDiagramProperties.minimize();
			}
			
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Select all areas.
	 */
	public void onSelectAll() {
		try {
			
			// Select all areas.
			ApplicationEvents.transmit(this, GuiSignal.selectAll);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Unselect all afreas.
	 */
	public void onUnselectAll() {
		try {
			
			// Unselect all areas.
			ApplicationEvents.transmit(this, GuiSignal.unselectAll);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Show or hide dialog navigator window.
	 */
	private void onDialogNavigator() {
		Safe.tryOnChange(toggleDialogNavigator, () -> {
			
			boolean showNavigator = toggleDialogNavigator.isSelected();
			DialogNavigator.displayNavigator(showNavigator);
		});
	}

	/**
	 * Gets user value
	 * @return
	 */
	public String getUserValue() {

		return userValue;
	}

	/**
	 * On settings.
	 */
	protected void onSettings() {
		try {
			
			// Show settings dialog.
			Settings.showDialog(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On resources.
	 */
	protected void onResources() {
		try {
			
			// Edit resources.
			ResourcesEditorDialog.showDialog(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On dictionary.
	 */
	protected void onTranslator() {
		try {
			
			// Get selected areas.
			LinkedList<Area> areas = mainFrame.getAreaDiagram().getSelectedAreas();
			// Show dialog.
			GeneratorTranslatorDialog.showDialog(this, areas);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On update data.
	 */
	public void onUpdate() {
		try {
			
			// Update all components.
			updateAll();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Main frame getter method.
	 * @return
	 */
	public static GeneratorMainFrame getFrame() {
		return mainFrame;
	}
	
	/**
	 * Get selected area IDs.
	 * @return
	 */
	public static HashSet<Long> getSectedAreaIds() {

		try {
			if (mainFrame == null) {
				return null;
			}
			
			// Delegate the call.
			HashSet<Long> selectedAreaIds = mainFrame.getSelectedAreaIds();
			return selectedAreaIds;
		}
		catch (Throwable e) {
			Safe.exception(e);
            return null;
        }
	}
	
	/**
	 * Get selected area IDs.
	 * @return
	 */
	private HashSet<Long> getSelectedAreaIds() {
		
		try {
			LinkedList<Area> areaList = mainAreaDiagramEditor.getSelectedAreas();
			
			HashSet<Long> areaIdSet = new HashSet<Long>();
			for (Area area : areaList) {
				
				long areaId = area.getId();
				areaIdSet.add(areaId);
			}
			
			return areaIdSet;
		}
		catch (Throwable e) {
            Safe.exception(e);
            return null;
        }
	}

	/**
	 * Get selected areas.
	 * @param areas
	 */
	public LinkedList<Area> getSelectedAreas() {
		
		try {
			return mainAreaDiagramEditor.getSelectedAreas();
		}
		catch (Throwable e) {
            Safe.exception(e);
            return null;
        }
	}
	
	/**
	 * Focus on home area.
	 */
	public void onFocusHome() {
		try {
			
			// Propagate event.
			ApplicationEvents.transmit(this, GuiSignal.focusHomeArea);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get area properties editor.
	 * @return
	 */
	public AreaPropertiesBasePanel getAreasProperties() {
		
		try {
			return propertiesPanel.getPropertiesEditor();
		}
		catch (Throwable e) {
            Safe.exception(e);
            return null;
        }
	}

	/**
	 * On undo focus.
	 */
	public void onUndoFocus() {
		try {
			
			AreaDiagramContainerPanel editor = getVisibleAreasEditor();
			if (editor != null) {
				// Delegate the call.
				editor.getDiagram().undoFocus();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On redo focus.
	 */
	public void onRedoFocus() {
		try {
			
			AreaDiagramContainerPanel editor = getVisibleAreasEditor();
			if (editor != null) {
				// Delegate the call.
				editor.getDiagram().redoFocus();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On expose read only areas.
	 */
	public void onExposeReadOnly() {
		try {
			
			AreaShapes.readOnlyLighter = !exposeReadOnly.isSelected();
			
			// Update application components.
			updateAll();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get areas locked flag.
	 * @return
	 */
	public static boolean areasLocked() {
		
		try {
			return !mainFrame.exposeReadOnly.isSelected();
		}
		catch (Throwable e) {
            Safe.exception(e);
            return false;
        }
	}
	
	/**
	 * Clone areas diagram.
	 * @param diagramName
	 * @param topAreaId
	 */
	public AreaDiagramContainerPanel createAreasDiagram(String diagramName, Long topAreaId) {
		
		try {
			// Create new areas editor.
			AreaDiagramContainerPanel newAreasEditor = new AreaDiagramContainerPanel();
			AreaDiagramContainerPanel currentAreasEditor = getVisibleAreasEditor();
			
			newAreasEditor.initDiagramEditor(currentAreasEditor);
			
			// Set undo and redo references.
			AreaDiagramPanel diagram = newAreasEditor.getDiagram();
			
			// Get current diagram.
			AreaDiagramPanel currentDiagram = getVisibleAreasEditor().getDiagram();
			double translationX = currentDiagram.getTranslatingX();
			double translationY = currentDiagram.getTranslatingY();
			double zoom = currentDiagram.getZoom();
			
			// Add new tab.
			tabPanel.addAreasEditor(newAreasEditor, TabType.areasDiagram, diagramName, topAreaId, false);
			diagram.setDiagramPosition(translationX, translationY, zoom);
			diagram.setUndoRedoComponents(undoButton, redoButton);
			
			// Select the new tab.
			int count = tabPanel.getTabCount();
			Safe.tryUpdate(tabPanel, () -> tabPanel.setSelectedIndex(count - 1));	
			
			// Update window selection trayMenu.
			updateWindowSelectionMenu();
			
			return newAreasEditor;
		}
		catch (Throwable e) {
            Safe.exception(e);
            return null;
        }
	}
	
	/**
	 * Create area tree view.
	 * @param title
	 * @param displayedAreas 
	 * @param areaId
	 */
	private AreasTreeEditorPanel createAreasTreeView(String title, Long rootAreaId, Long [] displayedAreas) {
		
		try {
			// Trim input.
			if (rootAreaId == null) {
				rootAreaId = 0L;
			}
			
			// Add new tree view.
			AreasTreeEditorPanel areasTreePanel = new AreasTreeEditorPanel(rootAreaId);
			areasTreePanel.displayAreaIds(displayedAreas);
			tabPanel.addAreasEditor(areasTreePanel, TabType.areasTree, title, rootAreaId, true);
			
			// Select the new tab.
			int count = tabPanel.getTabCount();
			Safe.tryUpdate(tabPanel, () -> {
				tabPanel.setSelectedIndex(count - 1);
			});
			
			return areasTreePanel;
		}
		catch (Throwable e) {
            Safe.exception(e);
            return null;
        }
	}
	
	/**
	 * Clone areas diagram.
	 */
	public void cloneAreasDiagram(Area focusArea) {
		try {
			
			// Get current areas diagram.
			AreaDiagramPanel diagram = GeneratorMainFrame.getVisibleAreasDiagram();
			
			// If the area connection is in progress, exit it.
			if (diagram.isAreaConnection()) {
				diagram.escapeDiagramModes();
			}
			
			// Get selected area.
			LinkedList<Area> selectedAreas = getSelectedAreas();
			
			// Get area to clone.
			boolean useSelectedArea = selectedAreas.size() == 1;
			final Area area = focusArea != null ? focusArea : 
				(useSelectedArea ? selectedAreas.getFirst() : getBiggestVisibleArea());
			
			// Get area name.
			String areaName = area != null ? area.getDescription() : "";
			
			// Get diagram title.
			Obj<TabType> type = new Obj<TabType>();
			String title = ClonedDiagramDialog.showDialog(this, areaName, type);
			if (title == null) {
				return;
			}
			
			Long areaId = area == null ? null : area.getId();
			
			// On diagram.
			if (TabType.areasDiagram.equals(type.ref)) {
				
				// Clone diagram.
				final AreaDiagramContainerPanel areasDiagramEditor = createAreasDiagram(title, areaId);
				
				// Focus area.
				if (areasDiagramEditor != null) {
					Safe.invokeLater(() -> {
						areasDiagramEditor.focusAreaNear(area.getId());
					});
				}
			}
			// On tree view.
			else if (TabType.areasTree.equals(type.ref)) {
				
				// Clone tree view.
				createAreasTreeView(title, areaId, new Long [] {});
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Add monitor tab.
	 * @param url
	 */
	public void addMonitor(String url) {
		try {
			
			tabPanel.addMonitor(url, true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get biggest visible area.
	 * @return
	 */
	private Area getBiggestVisibleArea() {
		
		try {
			return getVisibleAreasEditor().getDiagram().getBiggestVisibleArea();
		}
		catch (Throwable e) {
            Safe.exception(e);
            return null;
        }
	}
	
	/**
	 * Get visible area editor.
	 * @return
	 */
	public AreaDiagramContainerPanel getVisibleAreasEditor() {
		
		try {
			int index = tabPanel.getSelectedIndex();
			if (index == -1) {
				return null;
			}
			
			Component component = tabPanel.getComponentAt(index);
			if (component instanceof AreaDiagramContainerPanel) {
				
				AreaDiagramContainerPanel editor = (AreaDiagramContainerPanel) component;
				return editor;
			}
			
			// Otherwise return main editor.
			component = tabPanel.getComponentAt(0);
			if (component instanceof AreaDiagramContainerPanel) {
				
				AreaDiagramContainerPanel editor = (AreaDiagramContainerPanel) component;
				return editor;
			}
		}
		catch (Throwable e) {
            Safe.exception(e);
        }
		return null;
	}

	/**
	 * Find and view area help.
	 * @param area
	 */
	public void findViewAreaHelp(Area area) {
		try {
			
			LinkedList<Long> foundAreaIds = new LinkedList<Long>();
			MiddleResult result = ProgramBasic.getMiddle().findSuperAreaWithHelp(
					ProgramBasic.getLoginProperties(), area.getId(), foundAreaIds);
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			LinkedList<Area> foundAreas = new LinkedList<Area>();
			// Load areas.
			for (long foundAreaId : foundAreaIds) {
				
				Area foundArea = ProgramGenerator.getAreasModel().getArea(foundAreaId);
				foundAreas.add(foundArea);
			}
			
			// Add constructor area to the list.
			Long constructorId = area.getConstructorHolderId();
			if (constructorId != null) {
				
				// Load constructor area.
				Properties login = ProgramBasic.getLoginProperties();
				Middle middle = ProgramBasic.getMiddle();
				
				Obj<Long> constructorAreaId = new Obj<Long>();
				
				result = middle.loadConstructorHolderAreaId(login, constructorId, constructorAreaId);
				if (result.isOK() && constructorAreaId.ref != null) {
				
					Area constructorArea = ProgramGenerator.getArea(constructorAreaId.ref);
					foundAreas.add(constructorArea);
				}
				
				// Logout and possibly show an error
				MiddleResult logoutResult = middle.logout(result);
				if (result.isOK()) {
					result = logoutResult;
				}
				
				if (result.isNotOK()) {
					result.show(this);
				}
			}
			
			if (!foundAreas.isEmpty()) {
				// View help.
				AreaHelpViewer.showDialog(this, foundAreas);
			}
			else {
				Utility.show(this, "org.multipage.generator.messageHelpAreaNotFound");
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On render HTML pages.
	 */
	public void onRenderTool() {
		try {
			
			onRender(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On render HTML pages.
	 */
	public void onRender(Component parentComponent) {
		try {
			
			final LinkedList<Area> areasToRender = new LinkedList<Area>();
			
			// Get selected areas.
			final LinkedList<Area> areas = mainAreaDiagramEditor.getSelectedAreas();
			if (!areas.isEmpty()) {
				areasToRender.addAll(areas);
			}
			else {
				//Utility.show(parentComponent, "org.multipage.generator.messagePleaseSelectAreasToRender");
				//return;
			
				// Get home area.
				Area homeArea = ProgramGenerator.getHomeArea();
				if (!homeArea.isVisible()) {
					Utility.show(parentComponent, "org.multipage.generator.messageHomeAreaIsNotVisible");
					return;
				}
				areasToRender.add(homeArea);
			}
	
			final LinkedList<Language> languages = new LinkedList<Language>();
			final Obj<String> target = new Obj<String>(TextRenderer.serializedTarget);
			final Obj<String> coding = new Obj<String>("UTF-8");
			final Obj<Boolean> showTextIds = new Obj<Boolean>(false);
			final Obj<BrowserParameters> browserParameters = new Obj<BrowserParameters>();
			final Obj<Boolean> generateList = new Obj<Boolean>();
			final Obj<Boolean> generateIndex = new Obj<Boolean>();
		    Obj<Boolean> runBrowser = new Obj<Boolean>(true);
		    Obj<Boolean> removeOldFiles = new Obj<Boolean>(false);
		    final LinkedList<VersionObj> versions = new LinkedList<VersionObj>();
		    final Obj<Boolean> renderRelatedAreas = new Obj<Boolean>(true);
			
			if (!RenderDialog.showDialog(parentComponent, languages, false, target, coding, showTextIds,
					browserParameters, generateList, generateIndex, runBrowser, removeOldFiles,
					versions, renderRelatedAreas)) {
				return;
			}
			
			// Check ambiguous file names.
			LinkedList<AmbiguousFileName> ambiguousFileNames = new LinkedList<AmbiguousFileName>();
			CheckRenderedFiles.getAmbiguousFileNames(areasToRender, versions, ambiguousFileNames);
			
			if (!ambiguousFileNames.isEmpty()) {
				
				// Ask user.
				if (Utility.ask(parentComponent, "org.multipage.generator.messageFileNameAmbiguitiesFound")) {
					if (!CheckRenderedFiles.showDialogModal(parentComponent, areasToRender)) {
						return;
					}
				}
			}
			
			// Save target.
			TextRenderer.serializedTarget = target.ref;
			
			// Remove old files.
			if (removeOldFiles.ref) {
				if (!Utility.deleteFolderContent(target.ref)) {
					return;
				}
			}
			
			String _pagesTarget = target.ref;
			
			// Correct target.
			if (browserParameters.ref != null) {
				
				String pagesFolder = browserParameters.ref.getFolder();
				if (!pagesFolder.isEmpty()) {
					
					_pagesTarget += File.separatorChar + pagesFolder;
					if (!makeFolder(_pagesTarget)) {
						return;
					}
				}
			}
	
			final String pagesTarget = _pagesTarget;
			final LinkedList<String> pageFileNames = new LinkedList<String>();
	
			// Create progress dialog.
			ProgressDialog<Object> progressDialog = new ProgressDialog<Object>(
					parentComponent, Resources.getString("org.multipage.generator.textRenderingHtmlPages"),
					_pagesTarget);
			
			ProgressResult progressResult = progressDialog.execute(new SwingWorkerHelper<Object>() {
				// Background process.
				@Override
				protected Object doBackgroundProcess() throws Exception {
					
					// Render HTML pages.
					TextRenderer renderer = new TextRenderer(ProgramBasic.getLoginProperties());
					renderer.setSkipErrorFiles(false);
					renderer.setCommonResourceFileNamesFlag(Settings.getCommonResourceFileNamesFlag());
					renderer.setResourcesFolder(Settings.getResourcesRenderFolder());
					
					try {
						renderer.render(areasToRender, languages, versions, coding.ref, showTextIds.ref, generateList.ref,
								generateIndex.ref, Settings.getExtractedCharacters(),
								pagesTarget, pageFileNames, renderRelatedAreas, this);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					
					renderer.dispose();
					
					// On error throw exception.
					if (renderer.isError()) {
						throw new Exception();
					}
					
					return null;
				}
			});
			
			// On error delete created files and exit the method.
			if (progressResult != ProgressResult.OK) {
				
				if (Settings.partiallyGeneratedRemoved()) {
					
					// Ask user and delete files.
					if (!Utility.deleteFolderContent(target.ref)) {
						return;
					}
				}
			}
	
			if (browserParameters.ref != null) {
				
				if (!pageFileNames.isEmpty()) {
					
					// Check if the home page has been generated.
					if (!checkHomePageExists(browserParameters.ref, pageFileNames)) {
						
						String message = String.format(
								Resources.getString("org.multipage.generator.textHomePageNotGeneratedSelectNew"),
								browserParameters.ref.getHomePage());
						
						String pageName = SelectStringDialog.showDialog(parentComponent, pageFileNames,
								"org/multipage/generator/images/home_page.png", "org.multipage.generator.textSelectHomePage",
								message);
						if (pageName != null) {
							browserParameters.ref.setHomePage(pageName);
							BrowserParametersDialog.serializedBrowserParameters.setHomePage(pageName);
						}
						else {
							return;
						}
					}
					// Create and run browser.
					if (generateBrowser(target.ref, browserParameters.ref) && runBrowser.ref) {
						
						runBrowser(target.ref, browserParameters.ref);
					}
				}
				else {
					Utility.show(parentComponent, "org.multipage.generator.textNoFilesGeneratedNoVisibleAreas");
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Check if the home page has been generated.
	 * @param browserParameters
	 * @param pageFileNames
	 */
	private boolean checkHomePageExists(BrowserParameters browserParameters,
			LinkedList<String> pageFileNames) {
		
		try {
			for (String pageFileName : pageFileNames) {
				
				if (browserParameters.getHomePage().equals(pageFileName)) {
					return true;
				}
			}
		}
		catch (Throwable e) {
            Safe.exception(e);
        }
		return false;
	}

	/**
	 * Makes folder.
	 * @param folderPath
	 * @return
	 */
	private boolean makeFolder(String folderPath) {
		
		try {
			File folder = new File(folderPath);
			folder.mkdirs();
			return true;
		}
		catch (Throwable e) {
			Utility.show2(this, String.format(
					Resources.getString("org.multipage.generator.messageCannotCreateHtmlDirectory"), e.getMessage()));
			return false;
		}
	}
	
	/**
	 * Generate browser.
	 * @param target
	 * @param browserParameters
	 * @return
	 */
	private boolean generateBrowser(String target,
			BrowserParameters browserParameters) {
		
		try {
			// If the browser name is empty, ask user.
			if (browserParameters.getBrowserProgramName().isEmpty()) {
				String name = Utility.input(this, "org.multipage.generator.messageInsertBrowserProgramName");
				if (name == null) {
					return false;
				}
				name = name.trim();
				if (name.isEmpty()) {
					return false;
				}
				BrowserParametersDialog.getParameters().setBrowserProgramName(name);
				browserParameters.setBrowserProgramName(name);
			}
			
			// Save browser EXE file.
			if (!saveBrowser(target, browserParameters)) {
				return false;
			}
			
			// Save browser properties.
			if (!saveBrowserProperties(target, browserParameters)) {
				return false;
			}
			
			// Save autorun file.
			if (!saveAutorunFile(target, browserParameters)) {
				return false;
			}
			
			return true;
		}
		catch (Throwable e) {
            Safe.exception(e);
            return false;
        }
	}

	/**
	 * Run browser.
	 * @param target
	 * @param browserParameters 
	 * @param browserParameters
	 */
	protected void runBrowser(String target, BrowserParameters browserParameters) {
		try {
			
			String exeFileName = browserParameters.getBrowserProgramName() + ".exe";
			
			try {
				String browserFullName = target + File.separatorChar + exeFileName;
				Runtime.getRuntime().exec(browserFullName);
			}
			catch (IOException e) {
				
				Utility.show(this, String.format(
						Resources.getString("org.multipage.generator.messageCannotRunBrowser"), exeFileName,
						e.getMessage()));
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Save browser properies.
	 * @param target
	 * @param browserParameters
	 * @return
	 */
	private boolean saveBrowserProperties(String target,
			BrowserParameters browserParameters) {
		
		try {
			FileOutputStream outputStream = null;
			OutputStreamWriter writer = null;
			
			try {
				outputStream = new FileOutputStream(target + File.separatorChar + "Properties.ini");
				writer = new OutputStreamWriter(outputStream, "UTF-16LE");
				
				writer.write("[Settings]\r\n");
				writer.write("\r\nRelativeUrl = " + browserParameters.getRelativeUrl());
				writer.write("\r\nTitle = " + browserParameters.getTitle());
				writer.write("\r\nDefaultMessage = " + browserParameters.getMessage());
				writer.write("\r\nWindowSize = " + browserParameters.getWindowSizeText());
				writer.write("\r\nWindowMaximized = " + browserParameters.getMaximizedText());
			}
			catch (Throwable e) {
				
				String message = String.format(
						Resources.getString("org.multipage.generator.messageCannotCreateBrowserProperties"),
						e.getMessage());
				
				Utility.show2(this, message);
				return false;
			}
			finally {
				try {
					if (writer != null) {
						writer.close();
					}
					if (outputStream != null) {
						outputStream.close();
					}
				}
				catch (Throwable e) {
					Safe.exception(e);
				}
			}
			
			return true;
		}
		catch (Throwable e) {
            Safe.exception(e);
            return false;
        }
	}
	
	/**
	 * Save autorun file.
	 * @param target
	 * @param browserParameters
	 * @return
	 */
	private boolean saveAutorunFile(String target,
			BrowserParameters browserParameters) {
		
		try {
			if (browserParameters.isCreateAutorun()) {
	
				FileOutputStream outputStream = null;
				OutputStreamWriter writer = null;
				
				try {
					outputStream = new FileOutputStream(target + File.separatorChar + "Autorun.inf");
					writer = new OutputStreamWriter(outputStream, "UTF-16LE");
					
					writer.write("[autorun]\r\n");
					writer.write("\r\nopen = " + browserParameters.getBrowserProgramName() + ".exe");
				}
				catch (Throwable e) {
					
					String message = String.format(
							Resources.getString("org.multipage.generator.messageCannotCreateBrowserProperties"),
							e.getMessage());
					
					Utility.show2(this, message);
					return false;
				}
				finally {
					try {
						if (writer != null) {
							writer.close();
						}
						if (outputStream != null) {
							outputStream.close();
						}
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
				}
			}
			
			return true;
		}
		catch (Throwable e) {
            Safe.exception(e);
            return false;
        }
	}

	/**
	 * Save browser.
	 * @param target
	 * @param browserParameters
	 * @return
	 */
	private boolean saveBrowser(String target,
			BrowserParameters browserParameters) {
		
		try {
			FileOutputStream browserOutputStream = null;
			InputStream inputStream = null;
			
			try {
				browserOutputStream = new FileOutputStream(
						target + File.separatorChar + browserParameters.getBrowserProgramName() + ".exe");
				
				inputStream = getClass().getResourceAsStream("/org/multipage/generator/browser/Browser.exe");
				
				// Coopy data.
				final int bufferLength = 2^16;
				byte [] buffer = new byte [bufferLength];
				
				while (true) {
					
					int bytesRead = inputStream.read(buffer);
					
					if (bytesRead == -1) {
						break;
					}
					
					browserOutputStream.write(buffer, 0, bytesRead);
				}
			}
			catch (Throwable e) {
				Utility.show2(this, String.format(
						Resources.getString("org.multipage.generator.messageCannotGenerateBrowser"), e.getMessage()));
				return false;
			}
			finally {
				try {
					if (inputStream != null) {
						inputStream.close();
					}
					if (browserOutputStream != null) {
						browserOutputStream.close();
					}
				}
				catch (Throwable e) {
					Safe.exception(e);
				}
			}
	
			return true;
		}
		catch (Throwable e) {
            Safe.exception(e);
            return false;
        }
	}
	
	/**
	 * Edit start resource.
	 * @param area
	 * @param inherits 
	 */
	public static void editStartResource(Area area, boolean inherits) {
		try {
			
			Area theArea = area;
			Component parentComponent = GeneratorMainFrame.getFrame();
			
			Obj<Long> versionId = new Obj<Long>(0L);
			
			if (inherits) {
				
				// Select version.
				Obj<VersionObj> version = new Obj<VersionObj>();
				
				if (!SelectVersionDialog.showDialog(parentComponent, version)) {
					return;
				}
				
				// Get selected version ID.
				versionId.ref = version.ref.getId();
				
				// Get inherited area.
				Area inheritedArea = ProgramGenerator.getAreasModel().getStartArea(theArea, versionId.ref);
				if (inheritedArea != null) {
					theArea = inheritedArea;
				}
			}
			
			// Load area source.
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			Obj<Long> resourceId = new Obj<Long>(0L);
			
			MiddleResult result = middle.loadAreaSource(login, theArea.getId(), versionId.ref, resourceId);
			if (result.isNotOK()) {
				result.show(null);
				return;
			}
			
			// Load old style start resource if not loaded.
			if (resourceId.ref == null) {
				result = middle.loadContainerStartResource(login, theArea, resourceId, versionId, null);
				if (result.isNotOK()) {
					result.show(null);
					return;
				}
			}
			
			if (resourceId.ref == 0L) {
				Utility.show(null, "org.multipage.generator.messageAreaHasNoStartResource");
				return;
			}
			
			// Get saving method.
			Obj<Boolean> savedAsText = new Obj<Boolean>();
			result = middle.loadResourceSavingMethod(login, resourceId.ref, savedAsText);
			if (result.isNotOK()) {
				result.show(null);
				return;
			}
			
			// Edit text resource.
			TextResourceEditor.showDialog(parentComponent, resourceId.ref,
					savedAsText.ref, theArea, true, versionId.ref, false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Edit text resource.
	 * @param resourceId
	 */
	public static void editTextResource(long resourceId) {
		try {
			
			Component parentComponent = GeneratorMainFrame.getFrame();
	
			// Edit text resource.
			TextResourceEditor.showDialog(parentComponent, resourceId, true, false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Edit slot value.
	 * @param slotId
	 */
	private void editSlot(Long slotId) {
		try {
			
			// Check input.
			if (slotId == null) {
				return;
			}
			
			// Get slot object and display slot editor.
			Slot slot = new Slot();
			boolean error = false;
			
			try {
				Middle middle = ProgramBasic.loginMiddle();
				Obj<Boolean> found = new Obj<>(false);
				middle.loadSlot(slotId, slot, found);
				
				error = !found.ref;
			}
			catch (Throwable e) {
				error = true;
				Utility.show(this, "org.multipage.generator.messageErrorLoadingSlot");
	        }
			finally {
				ProgramBasic.logoutMiddle();
			}
			if (error) {
	            return;
	        }
			
			// Open slot editor.
			SlotEditorFrame.showDialog(slot, false, true, null);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Open area slot list editor.
	 * @param areaId
	 */
	private void editAreaSlots(Long areaId) {
		try {
			
			// Get area from ID.
			Area area = ProgramGenerator.getArea(areaId);
	        if (area == null) {
	            Utility.show(getFrame(), "org.multipage.generator.messageAreaNotFound");
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
	 * Display area.
	 * @param area
	 */
	public static void displayRenderedArea(Area area) {
		try {
			
			// If the area is not visible exit the method.
			if (!area.isVisible()) {
				Utility.show(getFrame(), "org.multipage.generator.messageAreaNotVisible");
				return;
			}
			
			final LinkedList<Area> areas = new LinkedList<Area>();
			final LinkedList<Language> languages = new LinkedList<Language>();
			final LinkedList<VersionObj> versions = new LinkedList<VersionObj>();
			
			areas.add(area);
			
			// Show rendering dialog.
			final Obj<String> target = new Obj<String>(TextRenderer.serializedTarget);
			final Obj<String> coding = new Obj<String>("UTF-8");
			final Obj<Boolean> showTextIds = new Obj<Boolean>(false);
			Obj<Boolean> removeOldFiles = new Obj<Boolean>(false);
			final Obj<Boolean> renderRelatedAreas = new Obj<Boolean>(true);
			
			if (!RenderDialog.showDialog(getFrame(), languages, false, target, coding, showTextIds,
					null, null, null, null, removeOldFiles, versions, renderRelatedAreas)) {
				return;
			}
			
			if (languages.isEmpty()) {
				Utility.show(getFrame(), "org.multipage.generator.messageErrorLanguageListIsEmpty");
				return;
			}
			
			TextRenderer.serializedTarget = target.ref;
			
			// Remove old files.
			if (removeOldFiles.ref) {
				if (!Utility.deleteFolderContent(target.ref)) {
					return;
				}
			}
			
			final LinkedList<String> fileNames = new LinkedList<String>();
			
			// Create progress dialog.
			ProgressDialog<MiddleResult> progressDialog = new ProgressDialog<MiddleResult>(
					null, Resources.getString("org.multipage.generator.textRenderingHtmlPages"),
					target.ref);
			
			ProgressResult progressResult = progressDialog.execute(new SwingWorkerHelper<MiddleResult>() {
				// Background process.
				@Override
				protected MiddleResult doBackgroundProcess() throws Exception {
					
					// Render HTML pages.
					TextRenderer renderer = null;
					
					try {
						renderer = new TextRenderer(ProgramBasic.getLoginProperties());
						
						renderer.setSkipErrorFiles(false);
						renderer.setCommonResourceFileNamesFlag(Settings.getCommonResourceFileNamesFlag());
						renderer.setResourcesFolder(Settings.getResourcesRenderFolder());
						
						renderer.render(areas, languages, versions, coding.ref, showTextIds.ref, false,
								false, 0, target.ref, fileNames, renderRelatedAreas, this);
						renderer.dispose();
					}
					catch (Throwable e) {
						if (renderer != null) {
							renderer.dispose();
						}
						return new MiddleResult(null, e.getMessage());
					}
					return MiddleResult.OK;
				}});
			
			// On progress dialog error exit the method.
			if (progressResult != ProgressResult.OK) {
				return;
			}
			
			// On output error.
			MiddleResult result = progressDialog.getOutput();
			if (result == null) {
				return;
			}		
			if (result.isNotOK()) {
				Utility.show2(null, result.getMessage());
				return;
			}
			
			// If no files generated, inform user.
			if (fileNames.isEmpty()) {
				Utility.show(getFrame(), "org.multipage.generator.messageNoFilesRendered");
				return;
			}
			
			// Open the rendered HTML in the browser.
			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				if (desktop.isSupported(Desktop.Action.BROWSE)) {
					try {
						long startLanguageId = getStartLanguage();
						
						Language language = null;
						
						for (Language languageItem : languages) {
							if (languageItem.id == startLanguageId) {
								language = languageItem;
							}
						}
												
						if (language == null) {
							language = languages.getFirst();
						}
						
						String fileName = fileNames.getFirst();
	
						String pathName = TextRenderer.serializedTarget + "/" + fileName;
						pathName = pathName.replaceAll("\\\\", "/");
						
						// Open URL.
						BareBonesBrowserLaunch.openURL(pathName);
					}
					catch(Exception e) {
						Utility.show2(getFrame(), e.getMessage());
					}
				}
				else {
					Utility.show(getFrame(), "org.multipage.generator.messagePlatformDesktopBrowseNotSupported");
				}
			}
			else {
				Utility.show(getFrame(), "org.multipage.generator.messagePlatformDesktopClassNotSupported");
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	

	/**
	 * Display online area.
	 * @param area
	 * @param language
	 * @param version
	 * @param showTextIds
	 * @param parametersOrUrl
	 * @param fragment
	 * @param externalBrowser
	 */
	public void displayOnlineArea(Area area, Language language, VersionObj version, Boolean showTextIds, String parametersOrUrl, String fragment, Boolean externalBrowser) {
		try {
			
			String theParametersOrUrl = parametersOrUrl;
			
			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				if (desktop.isSupported(Desktop.Action.BROWSE)) {
					
					try {
						
						// Try to open URL.
						String url = null;
						URL urlObject = null;
						try {
							urlObject = Utility.tryUrl(theParametersOrUrl);
						}
						catch (MalformedURLException e1) {
							urlObject = null;
						}
						catch (IOException e3) {
							Utility.show2(this, e3.getLocalizedMessage());
							return;
						}
						
						// Use parameters instead.
						if (urlObject == null) {
							
							long areaId = area.getId();
							Long languageId = language != null ? language.id : 0L;
							long versionId = version != null ? version.getId() : 0L;
							
							// Load start language.
							Properties login = ProgramBasic.getLoginProperties();
							Obj<Long> startLanguageId = new Obj<Long>(0L);
							ProgramBasic.getMiddle().loadStartLanguageId(login, startLanguageId);
							
							// Get home area ID.
							Area homeArea = ProgramGenerator.getHomeArea();
							long homeAreaId = 0;
							
							if (homeArea != null) {
								homeAreaId = homeArea.getId();
							}
							
							if (theParametersOrUrl == null) {
								theParametersOrUrl = "";
							}
							if (!theParametersOrUrl.isEmpty()) {
								theParametersOrUrl = '&' + theParametersOrUrl;
							}
							
							if (areaId == homeAreaId && versionId == 0L && languageId == startLanguageId.ref) {
								url = String.format("http://localhost:%d/?%s%s", Settings.getHttpPortNumber(), ProgramServlet.displayHomeArea, theParametersOrUrl);
							}
							else {
								url = String.format("http://localhost:%d/?%s&area_id=%d&lang_id=%d&ver_id=%d%s",
										Settings.getHttpPortNumber(), ProgramServlet.displayHomeArea, areaId, languageId, versionId, theParametersOrUrl);
							}
							
							// Display localized text.
							if (showTextIds != null && showTextIds) {
								url += "&l";
							}
							
							// Add fragment identifier.
							if (fragment != null && !fragment.isEmpty()) {
								url += '#' + fragment;
							}
						}
						else {
							url = urlObject.toString();
						}
						
						if (externalBrowser != null & externalBrowser) {
							// Show external browser.
							BareBonesBrowserLaunch.openURL(url);
						}
						else {
							// Add monitor (internal browser).
							addMonitor(url);
						}
					}
					catch(Exception e) {
						Utility.show2(getFrame(), e.getMessage());
					}
				}
				else {
					Utility.show(getFrame(), "org.multipage.generator.messagePlatformDesktopBrowseNotSupported");
				}
			}
			else {
				Utility.show(getFrame(), "org.multipage.generator.messagePlatformDesktopClassNotSupported");
			}
			
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
	}
	
	/**
	 * Display online area.
	 * @param areaAlias
	 * @param fragmentAreaAlias 
	 */
	public static boolean displayOnlineArea(String projectAlias, String areaAlias, String fragmentAreaAlias) {
		
		try {
			// Check alias.
			if (areaAlias == null || areaAlias.isEmpty()) {
				return false;
			}
			
			// Try to find project area.
			Area projectArea = ProgramGenerator.getAreasModel().getArea(0L, projectAlias, AreasModel.AreaHint.last);
			if (projectArea == null || !projectArea.isProjectRoot()) {
				Utility.showHtml(getFrame().getContentPane(), "org.multipage.generator.messageAreaShouldBeProjectRoot", projectAlias);
				return false;
			}
			
			long projectAreaId = projectArea.getId();
			
			// Try to find area with alias.
			Area area = ProgramGenerator.getAreasModel().getArea(projectAreaId, areaAlias, AreasModel.AreaHint.first);
			
			// Check the area.
			if (area == null) {
				Utility.showHtml(getFrame().getContentPane(), "org.multipage.generator.messageAreaPageIsNotAvailable", areaAlias);
				return false;
			}
			
			// Try to find area for the page fragment.
			String fragment = null;
			
			if (fragmentAreaAlias != null) {
				Area fragmentArea = ProgramGenerator.getAreasModel().getArea(projectAreaId, fragmentAreaAlias, AreasModel.AreaHint.first);
				
				// Check the area.
				if (fragmentArea == null) {
					Utility.showHtml(getFrame().getContentPane(), "org.multipage.generator.messageAreaPageIsNotAvailable", fragmentAreaAlias);
					return false;
				}
				
				// Get fragment ID.
				fragment = fragmentArea.getAlias();
			}
			
			// Add Maclan help page.
			getFrame().displayAreaInIDE(area, fragment);
			return true;
		}
		catch (Throwable e) {
			Safe.exception(e);
            return false;
        }
	}
	
	/**
	 * Display online area.
	 * @param area
	 */
	public void displayOnlineArea(Area area) {
		try {
			
			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				if (desktop.isSupported(Desktop.Action.BROWSE)) {
						
					// Get online display parameters.
					Obj<Language> language = new Obj<Language>();
					Obj<VersionObj> version = new Obj<VersionObj>();
					Obj<Boolean> showTextIds = new Obj<Boolean>();
					Obj<String> parametersOrUrl = new Obj<String>();
					Obj<Boolean> externalBrowser = new Obj<Boolean>();
					
					if (!DisplayOnlineDialog.showDialog(getFrame(), language, version, showTextIds, parametersOrUrl, externalBrowser)) {
						return;
					}
					
					// Delegate the call.
					displayOnlineArea(area, language.ref, version.ref, showTextIds.ref, parametersOrUrl.ref, null, externalBrowser.ref);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Display area in IDE.
	 * @param area
	 * @param fragment 
	 */
	public void displayAreaInIDE(Area area, String fragment) {
		try {
			
			// Get current IDE language alias.
			String languageAlias = GeneratorMain.defaultLanguage;
			
			Language pageLanguage = null;
			
			try {
				// Login middle layer.
				Middle middle = ProgramBasic.loginMiddle();
				
				// Load languages.
				LinkedList<Language> languages = new LinkedList<Language>();
				
				MiddleResult result = middle.loadLanguages(languages);
				result.throwPossibleException();
				
				// Find language with given alias.
				for (Language language : languages) {
					
					// Return found language
					if (language.alias.equals(languageAlias)) {
						pageLanguage = language;
						break;
					}
				}
			}
			catch (Throwable e) {
				// Display error message.
				Utility.show2(getFrame(), e.getLocalizedMessage());
			}
			finally {
				// Logout middle layer.
				ProgramBasic.logoutMiddle();
			}
			
			// Delegate the call.
			displayOnlineArea(area, pageLanguage, null, null, null, fragment, false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get start language.
	 * @return
	 */
	private static long getStartLanguage() {
		
		try {
			Obj<Long> startLanguageId = new Obj<Long>();
			
			// Login to the database.
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			MiddleResult result;
	
			// Load start language ID.
			result = middle.loadStartLanguageId(login, startLanguageId);
	
			if (result.isNotOK()) {
				result.show(getFrame());
				return 0L;
			}
			
			return startLanguageId.ref;
		}
		catch (Throwable e) {
            Safe.exception(e);
            return 0L;
        }
	}

	/**
	 * On test.
	 */
	protected void onTest() {
		
		
	}

	/**
	 * On export data.
	 */
	protected void onExport() {
		try {
			
			// Get selected areas.
			LinkedList<Area> areas = getSelectedAreas();
			if (areas.size() != 1) {
				Utility.show(this, "org.multipage.generator.messageSelectSingleArea");
				return;
			}
			
			// Get super area.
			Area area = areas.getFirst();
			exportArea(area, this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Export area.
	 * @param area
	 */
	public static void exportArea(Area area, Component parent) {
		try {
			
			LinkedList<Area> superAreas = area.getSuperareas();
			Area parentArea = null;
			
			if (superAreas.size() == 1) {
				parentArea = superAreas.getFirst();
			}
			else if (superAreas.size() > 1) {
				
				LinkedList<AreaRelation> uniqueSuperAereaRelations = area.getUniqueSuperAreaRelations();
				if (uniqueSuperAereaRelations.size() == 1) {
					
					parentArea = superAreas.getFirst();
				}
				else {
					
					// Try to find selected area shape super area.
					AreaCoordinates lastSelectedCoordinates = getVisibleAreasDiagram().getLastSelectedAreaCoordinates();
					if (lastSelectedCoordinates != null) {
						
						parentArea = lastSelectedCoordinates.getParentArea();
					}
					
					if (parentArea == null) {
						
						// Select super area edge.
						parentArea = SelectSuperAreaDialog.showDialog(parent, area);
						if (parentArea == null) {
							return;
						}
					}
				}
			}
			
			// Open export dialog.
			ExportDialog.showDialog(parent, area, parentArea);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};

	}

	/**
	 * On import data.
	 */
	protected void onImport() {
		try {
			
			// Get selected areas.
			LinkedList<Area> areas = getSelectedAreas();
			if (areas.size() != 1) {
				Utility.show(this, "org.multipage.generator.messageSelectSingleArea");
				return;
			}
			
			Area area = areas.getFirst();
			importArea(area, this, false, true, true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Import area.
	 * @param area
	 * @param parent
	 */
	public static Long importArea(Area area, Component parent, boolean askSubName,
			boolean askImportLanguage, boolean askImportHome) {
		
		try {
			// Disable import to disallowed area.
			if (!area.canImport()) {
				Utility.show(parent, "org.multipage.generator.messageCannotImportToArea", area.getDescriptionForced());
				return null;
			}
			
			// Disable import to all hidden sub areas of given area.
			LinkedList<Area> superAreas = area.getSuperareas();
			boolean allHidden = true;
			
			if (!superAreas.isEmpty()) {
				for (Area areaAux : superAreas) {
					
					if (!area.isHideSubUseSuper(areaAux.getId())) {
						allHidden = false;
						break;
					}
				}
			}
			else {
				allHidden = false;
			}
			
			if (allHidden) {
				Utility.show(parent, "org.multipage.generator.messageCannotImportAllHidden");
				return null;
			}
			
			return ImportDialog.showDialog(parent, area, askImportLanguage, askImportHome);
		}
		catch (Throwable e) {
            Utility.show(parent, "org.multipage.generator.messageErrorImportingArea", e.getLocalizedMessage());
            return null;
        }
	}

	/**
	 * Repaint after the tools width has changed.
	 */
	public void repaintAfterToolsChanged() {
		try {
			
			GeneralDiagramPanel.updateDiagramsControls();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On edit file names.
	 */
	public void onEditFileNames() {
		try {
			
			// Get selected areas.
			LinkedList<Area> areas = getSelectedAreas();
			// Show editor.
			FileNamesEditor.showDialog(this, areas);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Check rendered files.
	 */
	protected void onCheckRenderedFiles() {
		try {
			
			// Get selected areas.
			LinkedList<Area> areas = getSelectedAreas();
			// Show dialog.
			CheckRenderedFiles.showDialog(this, areas);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On display home page.
	 */
	public void onRevert() {
		try {
			
			// Get selected areas.
			final LinkedList<Area> selectedAreas = mainAreaDiagramEditor.getSelectedAndEnabledAreas();
			
			// Check if the list contains some selected areas. If not, display a dialog with message.
			if (selectedAreas.isEmpty()) {
				
				Utility.show(this, "org.multipage.generator.messageSelectAreasWithExternalProviders");
				return;
			}
			
			// Get found external slots.
			try {
				LinkedList<Slot> externalSlots = ProgramGenerator.getExternalSlots(selectedAreas);
				
				// Let user confirm external providers' list.
				boolean confirmed = RevertExternalProvidersDialog.showDialog(this, externalSlots);
				if (!confirmed) {
					return;
				}
				
				// Ask user if he wants to rewrite external sources.
				if (Utility.ask(this, "org.multipage.generator.messageConfirmRewritingOfExternalSources")) {
				
					// Save slots' text values to external providers of code.
					String externalProviderLink = null;
					String outputText = null;
					
					for (Slot externalSlot : externalSlots) {
						
						externalProviderLink = externalSlot.getExternalProvider();
						outputText = externalSlot.getTextValue();
						
						MiddleUtility.saveValueToExternalProvider(null, externalProviderLink, outputText);
					}
				}
				
				// Ask user if he wants to unlock slots.
				if (Utility.ask(this, "org.multipage.generator.messageUnlockExternalSlots")) {
					
					MiddleResult result = MiddleResult.OK;
					try {
						Middle middle = ProgramBasic.loginMiddle();
						for (Slot externalSlot : externalSlots) {
							
							long slotId = externalSlot.getId();
							result = middle.updateSlotUnlock(slotId);
						}
					}
					catch (Throwable e) {
						result = MiddleResult.exceptionToResult(e);
					}
					finally {
						ProgramBasic.logoutMiddle();
					}
					if (result.isNotOK()) {
						result.show(this);
					}
				}
			}
			catch (Throwable e) {
				Utility.show(this, e.getLocalizedMessage());
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * MOnitor home page on-line in the web browser.
	 */
	private void monitorHomePage() {
		try {
			
			// Get home area.
			Area homeArea = ProgramGenerator.getHomeArea();
			
			// Display it.
			displayOnlineArea(homeArea);
			
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On monitor home page.
	 */
	public void onMonitorHomePage() {
		try {
			
			ApplicationEvents.transmit(this, GuiSignal.displayHomePage);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get visible area diagram.
	 * @return
	 */
	public static AreaDiagramPanel getVisibleAreasDiagram() {
		
		try {
			if (mainFrame != null) {
				AreaDiagramContainerPanel editor = mainFrame.getVisibleAreasEditor();
				
				if (editor != null) {
					return editor.getDiagram();
				}
				else {
					return mainFrame.getAreaDiagram();
				}
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Update window selection menu.
	 */
	public void updateWindowSelectionMenu() {
		try {
			
			// Create action class.
			class Action implements ActionListener {
				
				// Tab index.
				int index;
				
				// Constructor.
				Action(int index) {
					this.index = index;
				}
	
				// ActionGroup.
				@Override
				public void actionPerformed(ActionEvent e) {
					
					// Select tab item.
					Safe.tryUpdate(tabPanel, () -> {
						Safe.invokeLater(() -> {
							tabPanel.setSelectedIndex(index);
						});
					});
				}
			}
			
			// Load windows to select.
			windowSelectionMenu.removeAll();
			
			int selectedIndex = tabPanel.getSelectedIndex();
			
			// Setup trayMenu items.
			for (int index = 0; index < tabPanel.getTabCount(); index++) {
							
				// Get tab name and create trayMenu item.
				String title = tabPanel.getTabTitle(index);
				JMenuItem menuItem = new JMenuItem(title);
				
				// Set check image.
				if (index == selectedIndex) {
					menuItem.setIcon(Images.getIcon("org/multipage/gui/images/true_icon.png"));
				}
				
				// Set trayMenu listener.
				menuItem.addActionListener(new Action(index));
				
				// Insert trayMenu item into the trayMenu.
				windowSelectionMenu.add(menuItem);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set home area.
	 * @param parentComponent
	 * @param areaId
	 */
	public void setHomeArea(Component parentComponent, long areaId) {
		try {
			
			Area area = ProgramGenerator.getArea(areaId);
			if (area == null) {
				return;
			}
			setHomeArea(parentComponent, area);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Set home area.
	 * @param area
	 */
	public void setHomeArea(Component parentComponent, Area area) {
		try {
			
			String areaDescription = area.getDescriptionForced();
			
			// If an area is not visible, ask user.
			if (!area.isVisible()) {
				if (!Utility.ask(parentComponent, "org.multipage.generator.messageAreaInvisibleSetHome", areaDescription)) {
					return;
				}
			}
			
			// Ask user.
			if (!Utility.ask2(parentComponent, String.format(
					Resources.getString("org.multipage.generator.messageSetHomeArea"), areaDescription))) {
				return;
			}
			
			// Set start area.
			long areaId = area.getId();
			
			MiddleResult result = ProgramBasic.getMiddle().setStartArea(
					ProgramBasic.getLoginProperties(), areaId);
			
			if (result.isNotOK()) {
				result.show(parentComponent);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Focus on the tab top area.
	 */
	private void onFocusTabArea() {
		try {
			
			// Focus on the top area.
			ApplicationEvents.transmit(this, GuiSignal.focusTopArea);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On manual GUI.
	 */
	protected void onManualGui() {
		try {
			
			// Get directory.
			String manualDirectory = MiddleUtility.getManualDirectory() + File.separatorChar + "GUI";
			if (manualDirectory.isEmpty()) {
				
				manualDirectory = System.getProperty("user.dir") + File.separatorChar + "Manual" + File.separatorChar + "GUI";
			}
			
			// Check if index.htm exists.
			String indexFilePath = manualDirectory + File.separatorChar + "index.htm";
			
			String url = String.format("file://%s", indexFilePath);
			
			File indexFile = new File(indexFilePath);
			
			if (!indexFile.exists()) {
				Utility.show(this, "org.multipage.generator.messageManualIndexNotFound", indexFilePath);
				return;
			}
	
			BareBonesBrowserLaunch.openURL(url);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On Maclan manual event.
	 */
	protected void onManualForMaclan() {
		try {
			
			// Try to display area with help index.
			boolean success = GeneratorMainFrame.displayOnlineArea(HelpUtility.maclanReference, HelpUtility.maclanReferenceIndex, null);
			if (success) {
				return;
			}
			
			// Get directory.
			String manualDirectory = MiddleUtility.getManualDirectory() + File.separatorChar + "Maclan";
			if (manualDirectory.isEmpty()) {
				
				manualDirectory = System.getProperty("user.dir") + File.separatorChar + "Manual" + File.separatorChar + "Maclan";
			}
			
			// Check if index.htm exists.
			String indexFilePath = manualDirectory + File.separatorChar + "index.htm";
			
			String url = String.format("file://%s", indexFilePath);
			
			File indexFile = new File(indexFilePath);
			if (!indexFile.exists()) {
				
				Utility.show(this, "org.multipage.generator.messageManualIndexNotFound", indexFilePath);
				return;
			}
	
			BareBonesBrowserLaunch.openURL(url);			
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On video.
	 */
	protected void onVideo() {
		try {
			
			// Get directory.
			String manualDirectory = MiddleUtility.getManualDirectory() + File.separatorChar + "Video";
			if (manualDirectory.isEmpty()) {
				
				manualDirectory = System.getProperty("user.dir") + File.separatorChar + "Manual" + File.separatorChar + "Video";
			}
			
			// Check if index.htm exists.
			String indexFilePath = manualDirectory + File.separatorChar + "tutorial.htm";
			
			String url = String.format("file://%s", indexFilePath);
			//Utility.show2(this, url);
			
			File indexFile = new File(indexFilePath);
			
			if (!indexFile.exists()) {
				//Utility.show(this, "org.multipage.generator.messageVideoNotFound", indexFilePath);
				
				url = MiddleUtility.getWebVideoUrl();
				if (url.isEmpty()) {
					url = "http://www.multipage-software.org/video_alpha";
				}
			}
	
			BareBonesBrowserLaunch.openURL(url);			
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Take area trees.
	 * @param areaProperty
	 * @param parentArea 
	 */
	public void takeAreaTrees(List<Area> areas, Area parentAreaParam) {
		try {
			
			Area theParentArea = parentAreaParam;
			
			if (areas == null || areas.isEmpty()) {
				Utility.show(this, "org.multipage.generator.messageSelectAreasToCopy");
				return;
			}
			
			if (theParentArea == null) {
				
				// Let user select parent area.
				Area area = areas.get(0);
				theParentArea = SelectSuperAreaDialog.showDialog(this, area);
				if (theParentArea == null) {
					return;
				}
			}
			
			final Area parentArea = theParentArea;
			
			// Create and execute progress dialog.
			ProgressDialog<MiddleResult> progressDlg = new ProgressDialog<MiddleResult>(this,
					Resources.getString("org.multipage.generator.textCopyAreaProgressTitle"),
					Resources.getString("org.multipage.generator.textCopyAreaLoadingData"));
			
			// Load area tree data.
			progressDlg.execute(new SwingWorkerHelper<MiddleResult>() {
				@Override
				protected MiddleResult doBackgroundProcess() throws Exception {
					
					try {
						Middle middle = ProgramBasic.getMiddle();
						Properties login = ProgramBasic.getLoginProperties();
						
						// Create area tree object with callback methods.
						areaTreeDataToCopy = new AreaTreesData();
						AreaTreesData areaTreeData = new AreaTreesData() {
		
							@Override
							public boolean existsAreaOutside(Long areaId) {
								
								try {
									// Check in model if an area exists.
									if (areaId == null) {
										return false;
									}
									Area area = ProgramGenerator.getArea(areaId);
									return area != null;
								}
								catch (Throwable e) {
									Safe.exception(e);
                                    return false;
                                }
							}
						};
						areaTreeData.setCloned(true);
					
						Long parentAreaId = parentArea != null ? parentArea.getId() : null;
						
						LinkedList<Long> areaIds = new LinkedList<Long>();
						for (Area area : areas) {
							long areaId = area.getId();
							areaIds.add(areaId);
						}
						
						MiddleResult result = middle.loadAreaTreeData(login, areaIds, parentAreaId, areaTreeDataToCopy, this);
						return result;
					}
					catch (Throwable e) {
	                    return MiddleResult.exceptionToResult(e);
	                }
				}
			});
			
			// Reset copied data after delay.
			resetAreaTreeCopyTimer.restart();
				
			MiddleResult result = progressDlg.getOutput();
			// On error inform user.
			if (result.isNotOK()) {
				result.show(this);
			}
	
			// Set message information.
			String message = areaTreeDataToCopy.getExportMessage();
			Utility.showHtml(this, message);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Paste area trees.
	 * @param area
	 */
	public boolean copyAreaTrees(Area area) {
		
		try {
			// Check data.
			if (areaTreeDataToCopy == null) {
				Utility.show(this, "org.multipage.generator.messageCopyAreaFirst");
				return false;
			}
			
			// Check input area.
			if (area == null) {
				Utility.show(this, "org.multipage.generator.messageSelectSingleAreaToPaste");
				return false;
			}
			
			if (!area.canImport()) {
				Utility.show(this, "org.multipage.generator.messageCannotImportToArea", area.getDescriptionForced());
				return false;
			}
	
			Progress2Dialog<MiddleResult> progressDlg = null;
				
			// Reset copied data after delay.
			resetAreaTreeCopyTimer.restart();
			
			// Create and execute progress dialog.
			progressDlg = new Progress2Dialog<MiddleResult>(this,
					Resources.getString("org.multipage.generator.textPasteAreaProgressTitle"),
					Resources.getString("org.multipage.generator.textPasteAreaData"));
			
			progressDlg.execute(new SwingWorkerHelper<MiddleResult>() {
				@Override
				protected MiddleResult doBackgroundProcess() throws Exception {
					
					try {
						Middle middle = ProgramBasic.getMiddle();
						Properties login = ProgramBasic.getLoginProperties();
						
						// Import data.
						MiddleResult result = areaTreeDataToCopy.saveToDatabase(middle, login, area, null, false, this);
						return result;
					}
					catch (Throwable e) {
                        return MiddleResult.exceptionToResult(e);
                    }
				}
			});
						
			MiddleResult result = progressDlg.getOutput();
			
			// On error inform user.
			if (result != null && result.isNotOK() && result != MiddleResult.CANCELLATION) {
				result.show(this);
				return false;
			}
	
			return true;
		}
		catch (Throwable e) {
            Safe.exception(e);
            return false;
        }
	}
	
	/**
	 * Move area trees to target area.
	 * @param areas
	 * @param parentArea 
	 * @param targetArea
	 * @param parentComponent
	 */
	public void moveAreaTrees(List<Area> areas, Area parentArea, Area targetArea, Component parentComponent) {
		try {
			
			// Copy area trees and remove old ones.
			boolean success = copyAreaTrees(targetArea);
			if (success) {
				GeneratorMainFrame.removeAreas(areas, parentArea, parentComponent);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
	}

	/**
	 * Returns true value if there area area tree data to copy.
	 * @return
	 */
	public boolean isAreaTreeDataCopy() {
		
		return areaTreeDataToCopy != null;
	}

	/**
	 * Transfer area.
	 * @param transferredArea
	 * @param transferredParentArea 
	 * @param droppedArea
	 * @param droppedParentArea 
	 * @param action 
	 * @param parentComponent
	 */
	public static void transferArea(List<Area> transferredAreas, Area transferredParentArea,
			Area droppedArea, Area droppedParentArea, int action, Component parentComponent) {
		try {
			
			// If the transferred and dropped areas are same, inform user and exit.
			for (Area transferredArea : transferredAreas) {
				if (droppedArea.equals(transferredArea)) {
					Utility.show(parentComponent, "org.multipage.generator.messageCannotDropAreaToItself");
					return;
				}
			}
	
			// Check action. 1 means link, 2 means move
			if (!(action == 1 || action == 2)) {
				Utility.show(parentComponent, "org.multipage.generator.messageUnknownTransferAreaAction");
				return;
			}
			
			// Ask method.
			int method = SelectTransferMethodDialog.showDialog(parentComponent, 
					action, droppedParentArea == null);
			
			if (method == SelectTransferMethodDialog.CANCELLED) {
				return;
			}
			
			// Set parent area depending on the selected method.
			Area parentArea = null;
			
			switch (method) {
			case SelectTransferMethodDialog.BEFORE:
			case SelectTransferMethodDialog.AFTER:
				
				if (droppedParentArea == null) {
					return;
				}
				parentArea = droppedParentArea;
				break;
				
			case SelectTransferMethodDialog.SUBAREA:
				
				parentArea = droppedArea;
				break;
				
			default:
				return;
			}
			
			Boolean confirmed = null;
			
			for (Area transferredArea : transferredAreas) {
			
				// When linked, check if a cycle exists in the new areas diagram.
				if (action == 1) {
					if (AreaDiagramPanel.existsCircle(parentArea, transferredArea)) {
					
						Utility.show(parentComponent, "org.multipage.generator.messageCycleInAreaDiagramExists");
						return;
					}
				}
				else {
		
					// If the transferred area contains the dropped area, inform user and exit.
					if (AreaDiagramPanel.containsSubarea(transferredArea, droppedArea)) {
						
						Utility.show(parentComponent, "org.multipage.generator.messageAreaCannotMoveToItself");
						return;
					}
				}
				
				boolean sameParent = parentArea.equals(transferredParentArea);
				
				Obj<Boolean> inheritance = new Obj<Boolean>(true);
				Obj<String> relationNameSub = new Obj<String>();
				Obj<String> relationNameSuper = new Obj<String>();
				Obj<Boolean> hideSub = new Obj<Boolean>(true);
				
				if (!sameParent) {
					
					// Ask user for sub area edge definition.
					if (confirmed == null) {
						confirmed = AreaDiagramPanel.askNewSubAreaEdge(parentArea, transferredArea, inheritance, relationNameSub, relationNameSuper,
								hideSub, parentComponent);
						
						if (!confirmed) {
							return;
						}
					}
				}
				
				// Prepare prerequisites and update database.
				Properties login = ProgramBasic.getLoginProperties();
				Middle middle = ProgramBasic.getMiddle();
				
				MiddleResult result = middle.login(login);
				if (result.isNotOK()) {
					result.show(parentComponent);
					return;
				}
				
				boolean error = false;
				
				if (!sameParent) {
					
					// On move delete old edge.
					if (action == 2 && transferredParentArea != null) {
						
						result = middle.removeIsSubareaEdge(transferredParentArea, transferredArea);
						if (result.isNotOK()) {
							error = true;
						}
					}
					
					// Connect parent area with sub area.
					if (!error) {
						result = middle.connectSimplyAreas(parentArea, transferredArea, inheritance.ref,
								relationNameSub.ref, relationNameSuper.ref, hideSub.ref);
						
						if (result.isNotOK()) {
							error = true;
						}
					}
				}
				
				// On BEFORE and AFTER update sub areas order.
				if (!error && (method == SelectTransferMethodDialog.BEFORE || method == SelectTransferMethodDialog.AFTER)) {
					
					// Place new area in sub areas and save priorities.
					LinkedList<Area> subAreas = parentArea.getSubareas();
					long droppedAreaId = droppedArea.getId();
					long transferredAreaId = transferredArea.getId();
					
					// Insert new sub area.			
					LinkedList<Long> subAreasIds = new LinkedList<Long>();
					for (Area area : subAreas) {
						
						long areaId = area.getId();
						
						// Skip if it is the transferred area.
						if (areaId == transferredAreaId) {
							continue;
						}
						
						// Add the area before the dropped area.
						if (areaId == droppedAreaId && method == SelectTransferMethodDialog.BEFORE) {
							subAreasIds.add(transferredAreaId);
						}
						
						subAreasIds.add(areaId);
						
						// Add the area after the dropped area.
						if (areaId == droppedAreaId && method == SelectTransferMethodDialog.AFTER) {
							subAreasIds.add(transferredAreaId);
						}
					}
					
					// Update priorities.
					result = middle.initAreaSubareasPriorities(parentArea.getId(), subAreasIds);
					if (result.isNotOK()) {
						error = true;
					}
				}
				
				// Logout from the database.
				MiddleResult logoutResult = middle.logout(result);
				if (result.isOK()) {
					result = logoutResult;
				}
				
				// Display error message.
				if (result.isNotOK()) {
					result.show(parentComponent);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Hide properties.
	 */
	public static void hidePropertiesView() {
		try {
			
			// Delegate call.
			showPropertiesView(null);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Display area properties.
	 * @param areas
	 */
	public static void showPropertiesView(Collection<Long> areaIds) {
		try {
			
						// If there are no areas, do not display panel with properties.
			if (areaIds == null || areaIds.isEmpty()) {
				mainFrame.propertiesPanel.setNoProperties();
				mainFrame.setPropertiesVisible(false);
				return;
			}
			
			// Load areas from IDs.
			LinkedList<Area> areas = new LinkedList<Area>();
			areaIds.stream().forEach(areaId -> {
				Area area = ProgramGenerator.getArea(areaId);
				if (area != null) {
					areas.add(area);
				}
			});
			
			// Display editor.
			mainFrame.propertiesPanel.setAreas(areas);
			mainFrame.setPropertiesVisible(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Removes areas or a links to the areas.
	 * @param areas
	 * @param parentArea
	 * @param parentComponent
	 */
	public static void removeAreas(List<Area> areas, Area parentArea, Component parentComponent) {
		try {
			
			// Areas deletion dialog.
			HashSet<Area> areaSet = new HashSet<Area>();
			areaSet.addAll(areas);
			
			AreasDeletionDialog dialog = new AreasDeletionDialog(parentComponent, areaSet,
					parentArea);
			dialog.setVisible(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Reomve area or a link to the area.
	 * @param area
	 * @param parentArea
	 * @param parentComponent
	 */
	public static void removeArea(Area area, Area parentArea, Component parentComponent) {
		try {
			
			// Delegate the call.
			HashSet<Area> areaSet = new HashSet<Area>();
			areaSet.add(area);
			
			AreasDeletionDialog dialog = new AreasDeletionDialog(parentComponent, areaSet,
					parentArea);
			dialog.setVisible(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get previous update messages.
	 */
	@Override
	public LinkedList<Message> getPreviousMessages() {
		
		return previousUpdateMessages;
	}
	
	/**
	 * Create update manager for application objects.
	 */
	private void createUpdateManager() {
		try {
		
			updateManager = UpdateManager.getInstance();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Register updatable component.
	 * @param component
	 */
	public static void registerForUpdate(UpdatableComponent component) {
		try {
		
			// Delegate the call.
			mainFrame.registerForUpdateWithFrame(component);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Register updatable component.
	 * @param component
	 * @param priority
	 */
	public static void registerForUpdate(UpdatableComponent component, int priority) {
		try {
		
			// Delegate the call.
			mainFrame.registerForUpdateWithFrame(component, priority);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Register updatable component.
	 * @param component
	 */
	public void registerForUpdateWithFrame(UpdatableComponent component) {
		try {
			
			// Delegate the call.
			updateManager.register(component);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Register updatable component.
	 * @param component
	 * @param priority
	 */
	public void registerForUpdateWithFrame(UpdatableComponent component, int priority) {
		try {
			
			// Delegate the call.
			updateManager.register(component, priority);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Unregister updatable component.
	 * @param component
	 */
	public static void unregisterFromUpdate(UpdatableComponent component) {
		try {
			
	        // Delegate the call.
			mainFrame.unregisterFromUpdateWithFrame(component);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
    }
	
	/**
	 * Unregister updatable component.
	 * @param component
	 */
	public void unregisterFromUpdateWithFrame(UpdatableComponent component) {
        try {
			
        	// Delegate the call.
        	updateManager.unregister(component);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
    }
	
	/**
     * Update application components.
     * @param excludedComponents
     */
	public static void updateAll(UpdatableComponent ...excludedComponents) {
		try {
			
			// Delegate the call.
			mainFrame.updateAllWithFrame(excludedComponents);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
     * Update application components bound with current frame.
     * @param messages
     * @param excludedComponents
     */
	public void updateAllWithFrame(UpdatableComponent ...excludedComponents) {
		try {
			
			// Delegate the call.
			updateManager.updateAll(excludedComponents);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Called on update of application components.
	 */
	@Override
	public void updateComponents() {
		try {
			
			// Reload areas model.
			ProgramGenerator.reloadModel();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
