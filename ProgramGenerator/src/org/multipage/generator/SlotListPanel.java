/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.DefaultRowSorter;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SpringLayout;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.maclan.Area;
import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.maclan.Slot;
import org.maclan.SlotHolder;
import org.maclan.SlotType;
import org.multipage.basic.ProgramBasic;
import org.multipage.gui.ApplicationEvents;
import org.multipage.gui.EditorPaneEx;
import org.multipage.gui.FoundAttr;
import org.multipage.gui.Images;
import org.multipage.gui.Message;
import org.multipage.gui.PreventEventEchos;
import org.multipage.gui.ReceiverAutoRemove;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.ToolBarKit;
import org.multipage.gui.UpdatableComponent;
import org.multipage.gui.Utility;
import org.multipage.util.Closable;
import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Panel that displays list of slots that can be edited by user.
 * @author vakol
 *
 */
public class SlotListPanel extends JPanel implements PreventEventEchos, ReceiverAutoRemove, UpdatableComponent, Closable {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Splitter position.
	 */
	private static int splitterPositionFromEnd;

	/**
	 * An array of table column positions.
	 */
	private static Integer [] tableColumnPositions;
	
	/**
	 * Column widths.
	 */
	private static Integer [] columnWidthsState;
	
	/**
	 * Show preferred slots.
	 */
	private static boolean showUserSlots = false;

	/**
	 * Clipboard.
	 */
	private static LinkedList<Slot> clipBoard =
		new LinkedList<Slot>();

	/**
	 * Get slot clipboard.
	 */
	public static LinkedList<Slot> getSlotClipboard() {
		
		return clipBoard;
	}

	/**
	 * Set slot clipboard.
	 * @param _slotClipboard
	 */
	public static void setSlotClipboard(LinkedList<Slot> _slotClipboard) {
		
		clipBoard = _slotClipboard;
	}

	/**
	 * Set default state.
	 */
	public static void setDefaultData() {
		
		splitterPositionFromEnd = -1;
		
		if (ProgramGenerator.isExtensionToBuilder()) {
			
			columnWidthsState = new Integer [] {50, 150, 100, 100, 70, 150};
			tableColumnPositions = new Integer [] {0, 1, 2, 3, 4, 5};
		}
		else {
			columnWidthsState = new Integer [] {128, 128, 100, 128};
			tableColumnPositions = new Integer [] {0, 1, 2, 3};
		}
		
		showUserSlots = false;
	}

	/**
	 * Read state.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void serializeData(StateInputStream inputStream)
			throws IOException, ClassNotFoundException {
		
		splitterPositionFromEnd = inputStream.readInt();
		columnWidthsState = Utility.readInputStreamObject(inputStream, Integer [].class);
		tableColumnPositions = Utility.readInputStreamObject(inputStream, Integer [].class);
		showUserSlots = inputStream.readBoolean();
	}

	/**
	 * Write state.
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream)
			throws IOException {
		
		outputStream.writeInt(splitterPositionFromEnd);
		outputStream.writeObject(columnWidthsState);
		outputStream.writeObject(tableColumnPositions);
		outputStream.writeBoolean(showUserSlots);
	}
	
	/**
	 * A flag indicating that this panel is embedded in the main frame.
	 */
	private boolean isInPropertiesPanel = false;
	
	/**
	 * List of previous messages.
	 */
	private LinkedList<Message> previousUpdateMessages = new LinkedList<>();

	/**
	 * Holders list.
	 */
	protected LinkedList<Area> areas =
		new LinkedList<Area>();
	
	/**
	 * Table model for slot list.
	 */
	protected SlotsTableModel tableSlotsModel;
	
	/**
	 * List of found slots.
	 */
	protected LinkedList<FoundSlot> foundSlots = new LinkedList<FoundSlot>();
	
	/**
	 * Button that can show only found slots.
	 */
	protected JToggleButton buttonShowOnlyFound;
	
	/**
	 * Button that can show only preferred slots.
	 */
	protected JToggleButton buttonShowUserSlots;
	/**
	 * Popup menu.
	 */
	protected JPopupMenu popupMenu;

	/**
	 * Flag that signals not saving state on exit.
	 */
	protected boolean doNotSaveStateOnExit = false;
	
	/**
	 * Set divider position to maximum.
	 */
	protected boolean setDividerPositionToMaximum = false;
	
	/**
	 * Search direction.
	 */
	protected boolean searchDirectionForward = true;
	
	/**
	 * Flag that signals table properties set.
	 */
	private boolean isTablePropertiesReady = false;

	/**
	 * Flag that signals search in values.
	 */
	private boolean searchedInValues = false;

	/**
	 * Key sequence timer.
	 */
	private Timer keySequenceResetTimer;

	/**
	 * Key sequence.
	 */
	private String keySequence = "";

	/**
	 * Flag that signals not to update slot description.
	 */
	private boolean doNotUpdateSlotDescription = false;

	/***
	 * Load slot description timer.
	 */
	private Timer loadDescriptionTimer;
	
	/**
	 * Slot selected callback.
	 */
	private SlotSelectedEvent slotSelectedEvent;

	/**
	 * Menu items.
	 */
	protected JSeparator separator1;
	protected JSeparator separator2;
	protected JMenuItem menuMoveSlots;
	protected JMenuItem menuUseSlots;
	protected JMenuItem menuCopySlots;
	protected JMenuItem menuFocusArea;
	protected JMenuItem menuSetDefaultNormal;
	protected JMenuItem menuClearSearch;
	
	/**
	 * Search dialog object.
	 */
	private SearchSlotDialog searchDialog;
	
	/**
	 * Use database to load slots.
	 */
	private boolean useDatabase = true;
	
	/**
	 * Enable editing slots.
	 */
	private boolean slotEditingEnabled = true;
	
	/**
	 * Save column widths.
	 */
	private boolean saveColumnWidths = true;
	
	// $hide<<$
	/**
	 * Components.
	 */
	private JLabel labelSlots;
	protected JTable tableSlots;
	protected JSplitPane splitPane;
	private JScrollPane scrollPaneDescription;
	protected JEditorPane textDescription;
	private JPanel panelTop;
	protected JScrollPane scrollPane;
	protected JToolBar toolBar;
	
	/**
	 * Create the panel.
	 */
	public SlotListPanel() {
		
		try {
			// Initialize components.
			initComponents();
			// $hide>>$
			// Post creation.
			postCreate();
			// $hide<<$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	public void setIsPropertiesPanel(boolean isPropertiesPanel) {
		
		this.isInPropertiesPanel = isPropertiesPanel;
	}

	/**
	 * Initialize components.
	 */
	private void initComponents() {
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		labelSlots = new JLabel("org.multipage.generator.textSlots");
		springLayout.putConstraint(SpringLayout.NORTH, labelSlots, 0, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, labelSlots, 0, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, labelSlots, 0, SpringLayout.EAST, this);
		add(labelSlots);
		
		splitPane = new JSplitPane();
		splitPane.setBorder(null);
		splitPane.setOneTouchExpandable(true);
		springLayout.putConstraint(SpringLayout.NORTH, splitPane, 0, SpringLayout.SOUTH, labelSlots);
		springLayout.putConstraint(SpringLayout.SOUTH, splitPane, 0, SpringLayout.SOUTH, this);
		splitPane.setResizeWeight(0.8);
		springLayout.putConstraint(SpringLayout.WEST, splitPane, 0, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, splitPane, 0, SpringLayout.EAST, labelSlots);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		add(splitPane);
		
		scrollPaneDescription = new JScrollPane();
		scrollPaneDescription.setBorder(null);
		splitPane.setRightComponent(scrollPaneDescription);
		
		textDescription = new EditorPaneEx();
		textDescription.setBorder(null);
		textDescription.setContentType("text/html");
		textDescription.setFont(new Font("Arial", Font.PLAIN, 12));
		textDescription.setBackground(UIManager.getColor("ToolTip.background"));
		textDescription.setEditable(false);
		scrollPaneDescription.setViewportView(textDescription);
		
		panelTop = new JPanel();
		panelTop.setBorder(null);
		splitPane.setLeftComponent(panelTop);
		SpringLayout sl_panelTop = new SpringLayout();
		panelTop.setLayout(sl_panelTop);
		
		scrollPane = new JScrollPane();
		scrollPane.setBorder(null);
		sl_panelTop.putConstraint(SpringLayout.NORTH, scrollPane, 0, SpringLayout.NORTH, panelTop);
		sl_panelTop.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, panelTop);
		sl_panelTop.putConstraint(SpringLayout.EAST, scrollPane, 0, SpringLayout.EAST, panelTop);
		panelTop.add(scrollPane);
		
		tableSlots = new JTable();
		tableSlots.setBorder(null);
		scrollPane.setViewportView(tableSlots);
		tableSlots.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		toolBar = new JToolBar();
		sl_panelTop.putConstraint(SpringLayout.SOUTH, scrollPane, 0, SpringLayout.NORTH, toolBar);
		sl_panelTop.putConstraint(SpringLayout.WEST, toolBar, 0, SpringLayout.WEST, panelTop);
		sl_panelTop.putConstraint(SpringLayout.SOUTH, toolBar, 0, SpringLayout.SOUTH, panelTop);
		sl_panelTop.putConstraint(SpringLayout.EAST, toolBar, 0, SpringLayout.EAST, panelTop);
		toolBar.setFloatable(false);
		panelTop.add(toolBar);
		tableSlots.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					
					if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
						
						if (slotEditingEnabled) {
							onEdit();
						}
					}
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			}
		});
		tableSlots.getSelectionModel().addListSelectionListener(e -> {
			try {
				if (e.getValueIsAdjusting()) {
					return;
				}
				Safe.tryOnChange(tableSlots, () -> {
					fireSlotSelected();
				});
			}
			catch(Throwable expt) {
				Safe.exception(expt);
			};
		});
	}
	
	/**
	 * Set slot selected event
	 */
	public void setSlotSelectedEvent(SlotSelectedEvent slotSelectedEvent) {
		
		this.slotSelectedEvent = slotSelectedEvent;
	}
	
	/**
	 * Fire slot selected event
	 */
	protected void fireSlotSelected() {
		try {
			
			if (slotSelectedEvent == null) {
				return;
			}
			
			// Get selected objects.
			int [] selectedRows = getSelectedRows();
			if (selectedRows.length != 1) {
				
				return;
			}
			
			// Get selected slot and edit it.
			Slot slot = (Slot) tableSlotsModel.get(selectedRows[0]);
			slotSelectedEvent.selected(slot);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create popup menu.
	 */
	protected void createMenu() {
		try {
			
			popupMenu = new JPopupMenu();
			addPopup(scrollPane, popupMenu);
			addPopup(tableSlots, popupMenu);
			
			menuUseSlots = new JMenuItem("org.multipage.generator.textUseSlots");
			menuUseSlots.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						
						useSlots();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			menuUseSlots.setPreferredSize(new Dimension(200, 22));
			popupMenu.add(menuUseSlots);
			
			separator1 = new JSeparator();
			popupMenu.add(separator1);
			
			menuCopySlots = new JMenuItem("org.multipage.generator.textCopySlots");
			menuCopySlots.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						
						moveSlots(true);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			popupMenu.add(menuCopySlots);
			
			menuMoveSlots = new JMenuItem("org.multipage.generator.textMoveSlots");
			menuMoveSlots.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						
						moveSlots(false);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			popupMenu.add(menuMoveSlots);
	
			separator2 = new JSeparator();
			popupMenu.add(separator2);
			
			menuFocusArea = new JMenuItem("org.multipage.generator.textFocusArea");
			menuFocusArea.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						
						focusSelectedArea();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			popupMenu.add(menuFocusArea);
			
			// Set default values menu item.
			menuSetDefaultNormal = new JMenuItem("org.multipage.generator.textSetDefaultNormalSlotValues");
			menuSetDefaultNormal.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						
						setDefaultNormalValues();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			popupMenu.add(menuSetDefaultNormal);
			
			// Clear search results.
			menuClearSearch = new JMenuItem("org.multipage.generator.textClearAreaSlotSearchResults");
			menuClearSearch.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						
						clearSearch();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			popupMenu.add(menuClearSearch);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set slot default values.
	 */
	protected void setDefaultNormalValues() {
		try {
			
			// Get selected slots.
			int [] selectedRows = getSelectedRows();
			if (selectedRows.length < 1) {
				
				Utility.show(this, "org.multipage.generator.messageSelectSlots");
				return;
			}
			
			Obj<Boolean> isDefault = new Obj<Boolean>();
			
			// Set default values input dialog.
			if (!SetSlotValuesDialog.showDialog(this, isDefault)) {
				return;
			}
			
			// Login to the database.
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			
			MiddleResult result = middle.login(login);
			if (result.isNotOK()) {
				
				result.show(this);
				return;
			}
			
			// Set slots' properties.
			for (int index : selectedRows) {
				
				Slot slot = (Slot) tableSlotsModel.get(index);
				
				// Update slot default value.
				result = middle.updateSlotIsDefault(slot.getHolder().getId(), slot.getAlias(), isDefault.ref);
				if (result.isNotOK()) {
					break;
				}
			}
			
			// Logout from the database.
			MiddleResult logoutResult = middle.logout(result);
			if (result.isOK()) {
				result = logoutResult;
			}
			
			// Show error.
			if (result.isNotOK()) {
				result.show(this);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Clear search results.
	 */
	protected void clearSearch() {
		try {
			
			// Escape search mode.
			escapeFoundSlotsMode();
			// Load slots.
			loadSlots();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get selected rows.
	 * @return
	 */
	protected int [] getSelectedRows() {
		
		try {
			int [] selectedRows = tableSlots.getSelectedRows();
			selectedRows = convertViewRowsToModel(selectedRows);
			
			return selectedRows;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Focus selected area.
	 */
	protected void focusSelectedArea() {
		try {
			
			// Get selected objects.
			int [] selectedRows = getSelectedRows();
			if (selectedRows.length != 1) {
				Utility.show(this, "org.multipage.generator.messageSelectSingleSlot");
				return;
			}
			
			List<Slot> slots = tableSlotsModel.getSlots();
			// Check.
			if (slots.size() == 0) {
				return;
			}
			
			Slot slot = (Slot) slots.get(selectedRows[0]);
			GeneratorMainFrame.getFrame().getAreaDiagramEditor().focusAreaNear(slot.getHolder().getId());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Post creation.
	 */
	protected void postCreate() {
		try {
			
			// Create trayMenu.
			createMenu();
			// Initialize tool bar.
			createToolBar();
			buttonShowUserSlots.setSelected(showUserSlots);
			// Initialize key strokes.
			initKeyStrokes();
			// Localize components.
			localize();
			// Initialize table.
			initTable();
			// Set icons.
			setIcons();
			// Load dialog.
			loadDialog();
			// Initialize divider listener.
			initDividerListener();
			// Set key listener.
			initKeyListener();
			initColumnListener();
			// Enable web links in description.
			Utility.enableWebLinks(this, textDescription);
			// Initialize timer
			initLoadDescriptionTimer();
			// Set listener
			setListeners();
			// Register for updates.
			GeneratorMainFrame.registerForUpdate(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set listener
	 */
	private void setListeners() {
		try {
			
			
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Update areas and select slots.
	 * @param areaIds
	 * @param selectedSlotIds
	 */
	private void updateAreasSelectSlots(HashSet<Long> areaIds, HashSet<Long> selectedSlotIds) {
		try {
			
			if (areaIds != null) {
				// Set edited areas.
				LinkedList<Area> areas = ProgramGenerator.getAreas(areaIds);
				setAreas(areas);
			}
			
			if (selectedSlotIds != null) {
				// Select slot IDs.
				selectSlotIds(selectedSlotIds);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set column change listener.
	 */
	private void initColumnListener() {
		try {
			
			tableSlots.getTableHeader().setReorderingAllowed(true);
			
			tableSlots.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
				@Override
				public void columnSelectionChanged(ListSelectionEvent e) {
				}
				@Override
				public void columnRemoved(TableColumnModelEvent e) {
				}
				@Override
				public void columnMoved(TableColumnModelEvent e) {
					try {
						
						// Get table column position.
						tableColumnPositions = getTableColumnPositions();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				@Override
				public void columnMarginChanged(ChangeEvent e) {
					try {
						
						// Set column widths.
						if (isTablePropertiesReady) {
							getTableColumnWidths(columnWidthsState);
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				@Override
				public void columnAdded(TableColumnModelEvent e) {
				}
			});
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
			
			// Resize listener.
			addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					try {
						
						super.componentResized(e);
						
						// Set splitter position.
						int maximumSplitterPosition = splitPane.getMaximumDividerLocation();
						
						if (setDividerPositionToMaximum) {
							
							splitPane.setDividerLocation(maximumSplitterPosition);
							splitterPositionFromEnd = 0;
							return;
						}
						
						if (splitterPositionFromEnd == -1) {
							splitterPositionFromEnd = maximumSplitterPosition - splitPane.getDividerLocation();
						}
						else {
		
							int splitterPosition = maximumSplitterPosition - splitterPositionFromEnd;
							splitPane.setDividerLocation(splitterPosition);
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			
			isTablePropertiesReady = true;
			
			// Set column widths.
			setTableColumnWidths(columnWidthsState);
			
			// Move column positions.
			// The input values area model indices positioned on view.
			if (tableColumnPositions != null) {
				moveTableColumns(tableColumnPositions);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Hide help panel.
	 */
	public void hideHelpPanel() {
		
		Safe.invokeLater(() -> {
			splitPane.setDividerSize(0);
			splitPane.getRightComponent().setVisible(false);
		});
	}
	
	/**
	 * Disable slots editor.
	 */
	public void doNotEditSlots() {
		try {
			
			slotEditingEnabled = false;
			toolBar.setPreferredSize(new Dimension(0, 0));
			toolBar.setVisible(false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Do not preserve column widths.
	 */
	public void doNotPreserveColumns() {
		
		saveColumnWidths = false;
	}
	
	/**
	 * Save dialog.
	 */
	public void saveDialog() {
		try {
			
			// Stop time.
			if (keySequenceResetTimer != null) {
				keySequenceResetTimer.stop();
			}
			
			if (saveColumnWidths) {
				// Get table column position.
				tableColumnPositions = getTableColumnPositions();
				// Set column widths.
				getTableColumnWidths(columnWidthsState);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
		
	/**
	 * Get table column positions. Each array item contains column model index
	 * on given view position.
	 * @return
	 */
	protected Integer [] getTableColumnPositions() {
		
		try {
			int columnCount = tableSlots.getColumnCount();
			Integer [] columnModelIndices = new Integer [columnCount];
			
			for (int viewIndex = 0; viewIndex < columnCount; viewIndex++) {
				columnModelIndices[viewIndex] = tableSlots.convertColumnIndexToModel(viewIndex);
			}
			return columnModelIndices;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Move table columns.
	 * @param columnModelIndices
	 */
	protected void moveTableColumns(Integer[] columnModelIndices) {
		try {
			
			TableColumnModel columnModel = tableSlots.getColumnModel();
			int columnCount = tableSlots.getColumnCount();
			
			// Set view positions of columns.
			for (int newViewIndex = 0; newViewIndex < columnModelIndices.length; newViewIndex++) {
				
				int modelIndex = columnModelIndices[newViewIndex];
				if (modelIndex < 0 || modelIndex >= columnCount) {
					continue;
				}
				
				int columnViewIndex = tableSlots.convertColumnIndexToView(modelIndex);
				
				// Move column to new column index.
				columnModel.moveColumn(columnViewIndex, newViewIndex);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Initialize divider listener.
	 */
	private void initDividerListener() {
		try {
			
			splitPane.addAncestorListener(new AncestorListener() {
				@Override
				public void ancestorAdded(AncestorEvent arg0) {
				}
				@Override
				public void ancestorMoved(AncestorEvent arg0) {
				}
				@Override
				public void ancestorRemoved(AncestorEvent arg0) {
					try {
						
						if (doNotSaveStateOnExit) {
							return;
						}
						
						// Save splitter position.
						int maximumSplitterPosition = splitPane.getMaximumDividerLocation();
						splitterPositionFromEnd = maximumSplitterPosition - splitPane.getDividerLocation();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Initialize key strokes.
	 */
	@SuppressWarnings("serial")
	private void initKeyStrokes() {
		try {
			
			tableSlots.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "removeHighlights");
			tableSlots.getActionMap().put("removeHighlights", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						
						escapeFoundSlotsMode();
						// Load slots.
						loadSlots();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}}
			);
			
			tableSlots.getInputMap().put(KeyStroke.getKeyStroke("control F"), "find");
			tableSlots.getActionMap().put("find", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						
						onSearch();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}}
			);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Initialize key listener.
	 */
	private void initKeyListener() {
		try {
			
			// Initialize key sequence reset timer.
			keySequenceResetTimer = new Timer(1200, e -> {
				keySequence = "";
			});
			
			keySequenceResetTimer.setRepeats(false);
			
			// Set key listener.
			tableSlots.addKeyListener(new KeyAdapter() {
				
				@Override
				public void keyTyped(KeyEvent e) {
					
					char key = e.getKeyChar();
					
					// A Key with ALT sequentially selects slot with given start character.
					if (e.isAltDown()) {
						selectSlotWithCharacter(key);
					}
					else {
						// Otherwise find start character sequence.
						keySequence += Character.toUpperCase(key);
						
						// Select slots and restart timer.
						selectSlotWithCharacterSequence(keySequence);
						
						// Start sequence reset.
						keySequenceResetTimer.restart();
					}
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Select slot with start character sequence.
	 * @param receiverObject
	 */
	protected void selectSlotWithCharacterSequence(String keySequence) {
		try {
			
			int modelColumnIndex = getSearchableColumnModelIndex();
			int size = tableSlotsModel.getRowCount();
			
			for (int viewRowIndex = 0; viewRowIndex < size; viewRowIndex++) {
				
				int modelRowIndex = tableSlots.convertRowIndexToModel(viewRowIndex);
				
				Object columnValue = tableSlotsModel.getValueAt(modelRowIndex, modelColumnIndex);
				if (columnValue == null) {
					continue;
				}
				
				// Check table cell text value.
				String textValue = columnValue.toString().toUpperCase();
				
				if (!textValue.isEmpty() && textValue.startsWith(keySequence)) {
					
					tableSlots.getSelectionModel().setSelectionInterval(viewRowIndex, viewRowIndex);
					Utility.ensureRecordVisible(tableSlots, viewRowIndex, true);
					return;
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Select slot with given character.
	 * @param key
	 */
	protected void selectSlotWithCharacter(char key) {
		try {
			
			// Select slot that start with given character. Begin from current selection.
			int currentViewRowIndex = tableSlots.getSelectedRow();
			if (currentViewRowIndex == -1) {
				
				currentViewRowIndex = 0;
				searchDirectionForward = true;
			}
			
			// Search forward.
			if (searchDirectionForward) {
				
				if (!searchForward(currentViewRowIndex, key)) {
					searchDirectionForward = false;
				}
			}
			
			// Search backward.
			if (!searchDirectionForward) {
				
				if (!searchBackward(currentViewRowIndex, key)) {
					
					searchDirectionForward = true;
					searchForward(currentViewRowIndex, key);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get searchable column index.
	 * @return
	 */
	private int getSearchableColumnModelIndex() {
		
		try {
			// Get view 0 model index.
			int modelColumnIndex = tableSlots.convertColumnIndexToModel(0);
			
			// Trim column index for Builder. Do not use access values column.
			if (ProgramGenerator.isExtensionToBuilder() && modelColumnIndex == 0) {
				// Get view 1 model index.
				modelColumnIndex = tableSlots.convertColumnIndexToModel(1);
			}
			return modelColumnIndex;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0;
	}

	/**
	 * Search forward.
	 * @param currentViewRowIndex
	 * @param key
	 * @return
	 */
	private boolean searchForward(int currentViewRowIndex, char key) {
		
		try {
			key = Character.toUpperCase(key);
			
			int modelColumnIndex = getSearchableColumnModelIndex();
			int size = tableSlotsModel.getRowCount();
			
			for (int viewRowIndex = currentViewRowIndex + 1; viewRowIndex < size; viewRowIndex++) {
				
				int modelRowIndex = tableSlots.convertRowIndexToModel(viewRowIndex);
				
				Object columnValue = tableSlotsModel.getValueAt(modelRowIndex, modelColumnIndex);
				if (columnValue == null) {
					continue;
				}
				
				// Check table cell text value.
				String textValue = columnValue.toString();
				
				if (!textValue.isEmpty() && Character.toUpperCase(textValue.charAt(0)) == key) {
					
					tableSlots.getSelectionModel().setSelectionInterval(viewRowIndex, viewRowIndex);
					Utility.ensureRecordVisible(tableSlots, viewRowIndex, true);
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
	 * Search backward.
	 * @param currentViewRowIndex
	 * @param key
	 */
	private boolean searchBackward(int currentViewRowIndex, char key) {
		
		try {
			key = Character.toUpperCase(key);
			
			int modelColumnIndex = getSearchableColumnModelIndex();
			
			for (int viewRowIndex = currentViewRowIndex - 1; viewRowIndex >= 0; viewRowIndex--) {
				
				int modelRowIndex = tableSlots.convertRowIndexToModel(viewRowIndex);
							
				Object columnValue = tableSlotsModel.getValueAt(modelRowIndex, modelColumnIndex);
				if (columnValue == null) {
					continue;
				}
				
				// Check table cell text value.
				String textValue = columnValue.toString();
				
				if (!textValue.isEmpty() && Character.toUpperCase(textValue.charAt(0)) == key) {
					
					tableSlots.getSelectionModel().setSelectionInterval(viewRowIndex, viewRowIndex);
					Utility.ensureRecordVisible(tableSlots, viewRowIndex, true);
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
	 * Escape found slots mode.
	 */
	private void escapeFoundSlotsMode() {
		try {
			
			searchDirectionForward = true;
			
			foundSlots.clear();
			buttonShowOnlyFound.setSelected(false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Localize components.
	 */
	protected void localize() {
		try {
			
			if (labelSlots != null) {
				Utility.localize(labelSlots);
			}
			if (menuUseSlots != null) {
				Utility.localize(menuUseSlots);
			}
			if (menuMoveSlots != null) {
				Utility.localize(menuMoveSlots);
			}
			if (menuCopySlots != null) {
				Utility.localize(menuCopySlots);
			}
			if (menuFocusArea != null) {
				Utility.localize(menuFocusArea);
			}
			if (menuSetDefaultNormal != null) {
				Utility.localize(menuSetDefaultNormal);
			}
			if (menuClearSearch != null) {
				Utility.localize(menuClearSearch);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set icons.
	 */
	protected void setIcons() {
		try {
			
			if (menuUseSlots != null) {
				menuUseSlots.setIcon(Images.getIcon("org/multipage/gui/images/paste_icon.png"));
			}
			if (menuMoveSlots != null) {
				menuMoveSlots.setIcon(Images.getIcon("org/multipage/generator/images/move_icon.png"));
			}
			if (menuCopySlots != null) {
				menuCopySlots.setIcon(Images.getIcon("org/multipage/gui/images/copy_icon.png"));
			}
			if (menuFocusArea != null) {
				menuFocusArea.setIcon(Images.getIcon("org/multipage/gui/images/search_icon.png"));
			}
			if (menuSetDefaultNormal != null) {
				menuSetDefaultNormal.setIcon(Images.getIcon("org/multipage/generator/images/default_value.png"));
			}
			if (menuClearSearch != null) {
				menuClearSearch.setIcon(Images.getIcon("org/multipage/generator/images/clear_search.png"));
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Initialize tool bars.
	 */
	protected void createToolBar() {
		try {
			
			// Add tool bar buttons.
	        ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/edit_full.png", "org.multipage.generator.tooltipEditSlot",
	        		() -> onEditFull());
	        ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/edit_simple.png", "org.multipage.generator.tooltipSimpleEditSlot",
	        		() -> onEditSimple());
	        toolBar.addSeparator();
	        ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/add_item_icon.png", "org.multipage.generator.tooltipNewSlot",
	        		() -> onNewUserSlot());
	        ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/remove_node.png", "org.multipage.generator.tooltipRemoveSlot",
	        		() -> onRemoveUserSlot());
	        toolBar.addSeparator();
	        ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/update_icon.png", "org.multipage.generator.tooltipUpdateSlots",
	        		() -> onUpdate());
	        toolBar.addSeparator();
	        ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/search_icon.png", "org.multipage.generator.tooltipSearchInSlots",
	        		() -> onSearch());
	        buttonShowOnlyFound = ToolBarKit.addToggleButton(toolBar, "org/multipage/generator/images/only_found.png", "org.multipage.generator.tooltipShowFoundSlots",
	        		() -> onClickShowFound());
	        toolBar.addSeparator();
	        buttonShowUserSlots = ToolBarKit.addToggleButton(toolBar, "org/multipage/generator/images/preferred.png", "org.multipage.generator.tooltipShowUserSlots",
	        		() -> onShowUser());
	        toolBar.addSeparator();
	        ToolBarKit.addToggleButton(toolBar, "org/multipage/generator/images/help_small.png", "org.multipage.generator.tooltipEditUserSlotDescription",
	        		() -> onEditSlotDescription());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On edit slot description.
	 */
	public void onEditSlotDescription() {
		try {
			
			editSlotDescription();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Edit slot description.
	 */
	protected void editSlotDescription() {
		try {
			
			// Get selected table items.
			int [] selectedRows = getSelectedRows();
			// If single slot not selected, inform user and exit the method.
			if (selectedRows.length != 1) {
				Utility.show(this, "org.multipage.generator.messageSelectSingleSlot");
				return;
			}
			
			// Get selected slot and edit it.
			Slot slot = (Slot) tableSlotsModel.get(selectedRows[0]);
			
			// Check user slot in Generator.
			if (!ProgramGenerator.isExtensionToBuilder() && !slot.isUserDefined()) {
				
				Utility.show(this, "org.multipage.generator.messageSlotHelpForUserDefined");
				return;
			}
			
			// Edit slot description.
			SlotDescriptionDialog.showDialog(this, slot);
			
			// Load slots.
			loadSlots();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On new user slot.
	 */
	private void onNewUserSlot() {
		try {
			
			// If there is more than one holder, inform user and exit.
			if (areas.size() != 1) {
				Utility.show(this, "org.multipage.generator.messageMoreThanOneHolderSelected");
				return;
			}
			
			Area area = areas.getFirst();
			
			// Cannot add user slot to area without constructor
			// TODO: temporary removed
			/*if (!area.isAreaConstructor()) {
				Utility.show(this, "org.multipage.generator.messageCannotAddSlotToArea");
				return;
			}*/
			
			// Get new user slot type and alias.
			Obj<String> slotAlias = new Obj<String>();
			Obj<SlotType> slotType = new Obj<SlotType>();
			Obj<Boolean> isInheritable = new Obj<Boolean>();
			
			if (!UserSlotInput.showDialog(this, slotAlias, slotType, isInheritable)) {
				return;
			}
			
			// Check if the slot alias already exists.
			if (existsSlotAlias(slotAlias.ref)) {
				Utility.show(this, "org.multipage.generator.messageSlotNameAlreadyExists");
				return;
			}
	
			// Create new slot.
			Slot slot = new Slot(area);
			
			// Set slot alias and set the slot as a localized text slot. Also set it as user defined and preferred.
			slot.setAlias(slotAlias.ref);
			if (SlotType.isText(slotType.ref))
				slot.setLocalizedTextValue("");
			slot.setLocalized(slotType.ref);
			slot.setUserDefined(true);
			slot.setPreferred(true);
			slot.setValueMeaning(slotType.ref);
			
			// Set inheritance.
			slot.setAccess(isInheritable.ref ? Slot.publicAccess : Slot.privateAccess);
			
			// If the slot should have an external provider, find it.
			if (SlotType.EXTERNAL_PROVIDER.equals(slotType.ref)) {
				
				if (!ExternalProviderDialog.showDialog(this, slot)) {
					return;
				}
			}
			
			// Edit slot data.
			SlotEditorFrame.showDialog(slot, true, true, null);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Return true value if first area has a slot with given alias.
	 * @param slotAlias
	 * @return
	 */
	private boolean existsSlotAlias(String slotAlias) {
		
		try {
			if (areas.size() != 1) {
				return false;
			}
			
			Area area = areas.getFirst();
			return area.getSlot(slotAlias) != null;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * On remove user slot.
	 */
	private void onRemoveUserSlot() {
		try {
			
			// Get selected table items.
			int [] selectedRows = getSelectedRows();
			// If nothing selected, inform user and exit the method.
			if (selectedRows.length == 0) {
				Utility.show(this, "org.multipage.generator.messageSelectUserSlots");
				return;
			}
			
			// Confirm deletion.
			if (JOptionPane.showConfirmDialog(this,
					Resources.getString("org.multipage.generator.messageDeleteSelectedUserSlots"))
					!= JOptionPane.YES_OPTION) {
				return;
			}
			
			// Items to delete.
			LinkedList<Slot> slotsToDelete = new LinkedList<Slot>();
			
			// Database login.
			Middle middle = ProgramBasic.getMiddle();
			MiddleResult result = middle.login(ProgramBasic.getLoginProperties());
			if (result.isOK()) {
				
				// Do loop for all selected indices.
				for (int rowIndex : selectedRows) {
					
					// Get slot and its holder and remove the slot.
					Slot slot = tableSlotsModel.get(rowIndex);
					
					if (!slot.isUserDefined()) {
						continue;
					}
					
					result = middle.removeSlot(slot);
					if (result.isNotOK()) {
						break;
					}
					
					slotsToDelete.add(slot);
				}
				
				// Database logout.
				MiddleResult logoutResult = middle.logout(result);
				if (result.isOK()) {
					result = logoutResult;
				}
			}
			
			// On error inform user and exit the method.
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			// Check list and inform user.
			if (slotsToDelete.isEmpty()) {
				Utility.show(this, "org.multipage.generator.messageNoUserSlotsDeleted");
				return;
			}
			
			// Remove selected items.
			tableSlotsModel.removeAll(slotsToDelete);
			
			onChange();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On click show found.
	 */
	public void onClickShowFound() {
		try {
			
			if (foundSlots.isEmpty()) {
				Utility.show(this, "org.multipage.generator.messageNoSlotsMarkedFound");
				
				escapeFoundSlotsMode();
				return;
			}
			
			onShowFound();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Initialize table.
	 */
	protected void initTable() {
		try {
			
			tableSlotsModel = new SlotsTableModel(areas, foundSlots, buttonShowOnlyFound.isSelected(), !buttonShowUserSlots.isSelected());
			tableSlots.setModel(tableSlotsModel);
			
			tableSlots.setAutoCreateRowSorter(true);
	        DefaultRowSorter<?, ?> sorter = ((DefaultRowSorter<?, ?>) tableSlots.getRowSorter());
	        List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
	        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
	        sorter.setSortKeys(sortKeys);
			
			// Set renderer.
			setTableRenderer();
			
			// Set selection listener.
			ListSelectionModel listSelectionModel = tableSlots.getSelectionModel();
			
			ListSelectionListener listSelectionListener = new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
	
					// Load slot description.
					loadSlotDescription();
				}
			};
			listSelectionModel.addListSelectionListener(listSelectionListener);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set table renderer.
	 */
	protected void setTableRenderer() {
		try {
			
			// Create cell renderer.
			TableCellRenderer cellRenderer = new TableCellRenderer() {
				// Renderer.
				SlotCellRenderer renderer = new SlotCellRenderer();
				// Get renderer.
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value,
						boolean isSelected, boolean hasFocus, int row, int column) {
					
					try {
						// Convert column index.
						column = tableSlots.convertColumnIndexToModel(column);
						// Convert row index.
						row = tableSlots.convertRowIndexToModel(row);
										
						Slot slot = tableSlotsModel.get(row);
						renderer.setSlotCell(slot, column, value, isSelected, hasFocus,
								FoundSlot.isSlotFound(foundSlots, slot), false);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return renderer;
				}
			};
			
			// Set renderers.
			TableColumnModel columnModel = tableSlots.getColumnModel();
			for (int index = 0; index < columnModel.getColumnCount(); index++) {
				
				TableColumn column = columnModel.getColumn(index);
				column.setCellRenderer(cellRenderer);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get table column width sum.
	 * @return
	 */
	protected int getColumnWidthSum() {
		
		try {
			if (tableSlots == null) {
				return 0;
			}
			
			TableColumnModel columnModel = tableSlots.getColumnModel();
			int count = columnModel.getColumnCount();
			int sum = 0;
			
			for (int index = 0; index < count; index++) {
				sum += columnModel.getColumn(index).getPreferredWidth();
			}
	
			return sum;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0;
	}

	/**
	 * Load slots.
	 * @param areas
	 */
	public void loadSlots() {
		try {
			
			// Load slots.
			if (useDatabase) {
				loadSlotsFromDatabase();
			}
			
			// Get selection.
			LinkedList<Slot> oldSelectedSlots = new LinkedList<Slot>();
			for (int selectedRow : getSelectedRows()) {
				
				oldSelectedSlots.add(tableSlotsModel.get(selectedRow));
			}
			
			// Set table.
			doNotUpdateSlotDescription = true;
			
			tableSlots.removeAll();
			tableSlots.clearSelection();
			tableSlotsModel.setList(areas, foundSlots, buttonShowOnlyFound.isSelected(), !buttonShowUserSlots.isSelected());
			tableSlotsModel.fireTableDataChanged();
			
			// Set selection.
			int rowCount = tableSlots.getRowCount();
			
			for (int row = 0; row < rowCount; row++) {
				Slot slot = tableSlotsModel.get(row);
				
				for (Slot oldSlot : oldSelectedSlots) {
					if (slot.equals(oldSlot)) {
						
						int viewRow = tableSlots.convertRowIndexToView(row);
						Safe.tryToUpdate(tableSlots, () -> {
							tableSlots.getSelectionModel().addSelectionInterval(viewRow, viewRow);
						});
					}
				}
			}
			
			fireSlotSelected();
			
			// Load slot description.
			Safe.invokeLater(() -> {
				
				doNotUpdateSlotDescription = false;
				loadSlotDescription();
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Select
	 * @param slotIds
	 */
	public void selectSlotIds(HashSet<Long> slotIds) {
		try {
			
			// Get selection model.
			ListSelectionModel selectionModel = tableSlots.getSelectionModel();
			if (selectionModel == null) {
				return;
			}
			
			selectionModel.clearSelection();
			
			for (Long slotId : slotIds) {
				if (slotIds == null) {
					continue;
				}
				
				// Add slot ID selection.
				addSelectionSlotId(slotId);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/** 
	 * Add slot selection.
	 * @param slotId
	 */
	private void addSelectionSlotId(Long slotId) {
		try {
			
			// Get selection model.
			ListSelectionModel selectionModel = tableSlots.getSelectionModel();
			if (selectionModel == null) {
				return;
			}
			
			// Find slot index.
			int slotCount = tableSlotsModel.getRowCount();
			for (int slotIndex = 0; slotIndex < slotCount; slotIndex++) {
				
				// Get slot.
				Slot slot = tableSlotsModel.get(slotIndex);
				if (slot == null) {
					continue;
				}
				
				// Check slot ID.
				long foundSlotId = slot.getId();
				if (foundSlotId == slotId) {
				
					// Select row with slot ID.
					selectionModel.addSelectionInterval(slotIndex, slotIndex);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Initialize load description timer.
	 */
	private void initLoadDescriptionTimer() {
		try {
			
			loadDescriptionTimer = new Timer(100, e -> {
				try {
					
					loadSlotDescriptionFunction();
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			loadDescriptionTimer.setRepeats(false);
			loadDescriptionTimer.setCoalesce(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load slot description.
	 */
	protected void loadSlotDescription() {
		try {
			
			if (doNotUpdateSlotDescription) {
				return;
			}
			
			if (loadDescriptionTimer != null) {
				loadDescriptionTimer.restart();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load slot description function.
	 */
	private void loadSlotDescriptionFunction() {
		try {
			
			// If a single slot is selected, show slot description.
			int selectionCount = tableSlots.getSelectedRowCount();
					
			final String messageFormat = "<html><i>%s</i></html>";
			
			//boolean highlightDescriptionBackground = false;
			
			if (selectionCount == 1) {
				
				// Get selected slot.
				int [] selectedRows = getSelectedRows();
				if (selectedRows.length != 1) {
					
					Utility.show(this, "org.multipage.generator.messageSelectSingleSlot");
					return;
				}
				
				// Get selected slot and its description.
				Slot slot = (Slot) tableSlotsModel.get(selectedRows[0]);						
				long descriptionSlotId = slot.getId();
	
				// Load description.
				Middle middle = ProgramBasic.getMiddle();
				Properties login = ProgramBasic.getLoginProperties();
				
				Obj<String> description = new Obj<String>("");
				
				MiddleResult result = middle.loadSlotDescription(login, descriptionSlotId, description);
				if (result.isNotOK()) {
					result.show(this);
				}
				
				// Display description.
				if (!description.ref.isEmpty()) {
					textDescription.setText(String.format("<html>%s</html>", description.ref));
					
					//highlightDescriptionBackground = true;
				}
				else {
					textDescription.setText(String.format(messageFormat,
							Resources.getString("org.multipage.generator.messageNoSlotInformation")));
				}
			}
			else if (selectionCount > 1) {
				textDescription.setText(String.format(messageFormat,
						Resources.getString("org.multipage.generator.messageMulitpleSlotSelection")));
			}
			else {
				textDescription.setText(String.format(messageFormat,
						Resources.getString("org.multipage.generator.messageNoSlotSelected")));
			}
			
			// Reset scroll bar position.
			Safe.invokeLater(() -> {
				Utility.resetScrollBarPosition(scrollPaneDescription);
			});
			
			// Set description background.
			//textDescription.setBackground(highlightDescriptionBackground ? new Color(255, 255, 204) : UIManager.getColor("Panel.background"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load slots from database.
	 */
	protected void loadSlotsFromDatabase() {
		try {
			
			// Clear area slots.
			Area.clearSlots(areas);
			
			// Load area slots.
			Properties loginProperties = ProgramBasic.getLoginProperties();
			MiddleResult result = ProgramBasic.getMiddle().loadAreasSlots(loginProperties, areas, false, true);
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get current area IDs.
	 * @return
	 */
	private HashSet<Long> getCurrentAreaIds() {
		
		try {
			HashSet<Long> areaIds = new HashSet<>();
			
			for (Area area : areas) {
				long areaId = area.getId();
				areaIds.add(areaId);
			}
			
			return areaIds;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * On change slots.
	 */
	protected void onChange() {
		try {
			
			// Update all modules.
			GeneratorMainFrame.updateAll();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On update.
	 */
	public void onUpdate() {
		try {
			
			// Reset found slots.
			update();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Update the list.
	 */
	public void update() {
		try {
			
			// Reset found slots.
			escapeFoundSlotsMode();
			// Load slots.
			loadSlots();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}	

	/**
	 * Select all items.
	 */
	public void onShowFound() {
		try {
			
			tableSlotsModel.setList(areas, foundSlots, buttonShowOnlyFound.isSelected(), buttonShowUserSlots.isSelected());
			
			// Load slots.
			loadSlots();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On show all slots.
	 */
	public void onShowUser() {
		try {
			
			showUserSlots = buttonShowUserSlots.isSelected();
			
			// Load slots.
			loadSlots();
			
			// Ensure selected slot visibility.
			Safe.invokeLater(() -> {
				
				int viewRowIndex = tableSlots.getSelectedRow();
				if (viewRowIndex != -1) {
					Utility.ensureRecordVisible(tableSlots, viewRowIndex, true);
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Edit slot.
	 */
	public void onEdit() {
		try {
			
			// Get selected objects.
			int [] selectedRows = getSelectedRows();
			if (selectedRows.length != 1) {
				
				Utility.show(this, "org.multipage.generator.messageSelectSingleSlot");
				return;
			}
			
			// Get selected slot and edit it.
			Slot slot = (Slot) tableSlotsModel.get(selectedRows[0]);
			FoundAttr foundAttr = FoundSlot.getFoundAtt(searchedInValues ? foundSlots : null, slot);
			boolean showSimple = false;
			if (slot.isLocalized()) {
				try {
					// Edit slot data.
					SlotEditorFrame.showDialog(slot, false, true, foundAttr);
				}
				catch (Exception e) {
					// Inform user.
					Utility.show(this, "org.multipage.generator.messageCannotEditHtmlUsingSimpleEditor");
					showSimple = true;
				}
			}
			else {
				showSimple = true;
			}
			if (showSimple) {
				
				// Show simple editor.
				SlotEditorFrame.showDialog(slot, false, false, foundAttr);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Convert view row indices to model indices.
	 * @param viewRows
	 * @return
	 */
	private int [] convertViewRowsToModel(int[] viewRows) {
		
		try {
			int rowsCount = viewRows.length;
			int [] modelRows = new int [rowsCount];
			
			for (int index = 0; index < rowsCount; index++) {
				modelRows[index] = tableSlots.convertRowIndexToModel(viewRows[index]);
			}
			return modelRows;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Edit slot.
	 */
	public void onEditFull() {
		try {
			
			// Get selected objects.
			int [] selectedRows = getSelectedRows();
			if (selectedRows.length != 1) {
				
				Utility.show(this, "org.multipage.generator.messageSelectSingleSlot");
				return;
			}
			
			// Get selected slot and edit it.
			Slot slot = (Slot) tableSlotsModel.get(selectedRows[0]);
			FoundAttr foundAttr = FoundSlot.getFoundAtt(foundSlots, slot);
			
			try {
				SlotEditorFrame.showDialog(slot, false, true, foundAttr);
			}
			catch (Exception e) {
				
				// Inform user and show simple editor.
				Utility.show(this, "org.multipage.generator.messageCannotEditHtmlUsingSimpleEditor");
				
				// Show simple editor.
				SlotEditorFrame.showDialog(slot, false, false, foundAttr);
			}
			
			onChange();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Edit slot (simple).
	 */
	public void onEditSimple() {
		try {
			
			// Get selected objects.
			int [] selectedRows = getSelectedRows();
			if (selectedRows.length != 1) {
				
				Utility.show(this, "org.multipage.generator.messageSelectSingleSlot");
				return;
			}
			
			// Get selected slot and edit it.
			Slot slot = (Slot) tableSlotsModel.get(selectedRows[0]);
			FoundAttr foundAttr = FoundSlot.getFoundAtt(foundSlots, slot);
			SlotEditorFrame.showDialog(slot, false, false, foundAttr);
			onChange();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set areas.
	 * @param areaCollection
	 */
	public void setAreas(Collection<? extends Object> areaCollection) {
		try {
			
			// Check input value.
			if (areaCollection == null) {
				return;
			}
			
			// Initialize the list.
			LinkedList<Area> areas = new LinkedList<Area>();
			
			// Convert input collection items to area objects.
			areaCollection.forEach(item -> {
				try {
					
					Area area = null;
					
					// On area ID.
					if (item instanceof Long) {
						
						Long areaId = (Long) item;
						area = ProgramGenerator.getArea(areaId);
					}
					// On area object.
					else if (item instanceof Area) {
						
						area = (Area) item;
					}
					
					// Add new area into the list.
					if (area != null) {
						areas.add(area);
					}
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// Check and set list of areas for the tree view or the list view.
			if (areas.isEmpty()) {
				return;
			}
			this.areas = areas;
			
			// Load slots.
			loadSlots();
			
			// Try to update slot search info.
			updateSearch();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set single area.
	 * @param area
	 */
	public void setArea(Area area) {
		try {
			
			LinkedList<Area> areas = new LinkedList<Area>();
			areas.add(area);
			
			setAreas(areas);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set whether to use database when loading slots for areas.
	 * @param use
	 */
	public void setUseDatabase(boolean use) {
		
		this.useDatabase = use;
	}

	/**
	 * Set table columns.
	 * @param columnWidths
	 */
	protected void setTableColumnWidths(int[] columnWidths) {
		try {
			
			int length = columnWidths.length;
			TableColumnModel columnModel = tableSlots.getColumnModel();
			int columnCount = columnModel.getColumnCount();
			
			for (int index = 0; index < length && index < columnCount; index++) {
				// Get column.
				TableColumn column = columnModel.getColumn(index);
				// Set preferred width.
				column.setPreferredWidth(columnWidths[index]);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set column widths.
	 * @param columnWidths
	 */
	public void setTableColumnWidths(Integer[] columnWidths) {
		try {
			
			int length = columnWidths.length;
			TableColumnModel columnModel = tableSlots.getColumnModel();
			int columnCount = columnModel.getColumnCount();
			
			for (int index = 0; index < length && index < columnCount; index++) {
				// Get column.
				TableColumn column = columnModel.getColumn(index);
				// Set preferred width.
				column.setPreferredWidth(columnWidths[index]);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get table columns.
	 * @param columnWidths
	 */
	public void getTableColumnWidths(Integer[] columnWidths) {
		try {
			
			int length = columnWidths.length;
			TableColumnModel columnModel = tableSlots.getColumnModel();
			int columnCount = columnModel.getColumnCount();
			
			for (int modelIndex = 0; modelIndex < length && modelIndex < columnCount; modelIndex++) {
				
				int viewIndex = tableSlots.convertColumnIndexToView(modelIndex);
				
				// Get column.
				TableColumn column = columnModel.getColumn(viewIndex);
				// Set preferred width.
				columnWidths[modelIndex] = column.getPreferredWidth();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Add popup trayMenu.
	 * @param component
	 * @param popup
	 */
	protected void addPopup(Component component, final JPopupMenu popup) {
		try {
			
			component.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					try {
						
						if (e.isPopupTrigger()) {
							showMenu(e);
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				public void mouseReleased(MouseEvent e) {
					try {
						
						if (e.isPopupTrigger()) {
							showMenu(e);
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				private void showMenu(MouseEvent e) {
					try {
						
						if (!slotEditingEnabled) {
							return;
						}
						enableMenu();
						popup.show(e.getComponent(), e.getX(), e.getY());
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On search in slots.
	 */
	public void onSearch() {
		try {
			
			// Show search dialog.
			if (searchDialog == null) {
				searchDialog = SearchSlotDialog.createDialog("org.multipage.generator.textSearchInSlots", this);
			}
			
			// Show dialog.
			searchDialog.showModal();
					
			// Update search.
			int resultCount = updateSearch();
			
			// Get reset flag and reset found slots.
			boolean reset = searchDialog.getResetFlag();
			if (reset) {
				escapeFoundSlotsMode();
				return;
			}
	
			// Display messages.
			if (searchDialog.showCount()) {
				Utility.show(this, "org.multipage.generator.textNumberOfFoundSlots", resultCount);
			}
			else if (resultCount == 0) {
				Utility.show(this, "org.multipage.generator.textNoSlotsFound", resultCount);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Update search.
	 * @return number of found providers
	 */
	private int updateSearch() {
		
		try {
			int resultCount = 0;
			
			// Check dialog.
			if (searchDialog == null) {
				return resultCount;
			}
			
			searchedInValues = searchDialog.getSearchInValues();
			
			foundSlots.clear();
			buttonShowOnlyFound.setSelected(false);
			loadSlots();
			
			if (searchDialog.isConfirmed()) {
				FoundAttr foundAttr = searchDialog.getFoundAttr();
				boolean searchInValues = searchDialog.isSearchInValues();
				boolean searchInDescriptions = searchDialog.isSearchInDescriptions();
				
				boolean isFirstExceptionShown = false;
				
				// Do loop for all holders.
				for (SlotHolder holder : areas) {
					// Do loop for all slots.
					for (Slot slot : holder.getSlots()) {
						
						String text = null;
						
						if (searchInValues) {
							Object value = slot.getValue();
							if (value != null) {
								text = value.toString();
							}
							else {
								continue;
							}
						}
						else if (searchInDescriptions) {
							try {
								text = slot.loadDescription(ProgramBasic.getLoginProperties(), ProgramBasic.getMiddle());
							}
							catch (Exception e) {
								
								if (!isFirstExceptionShown) {
									Utility.show2(this, e.getLocalizedMessage());
								}
								isFirstExceptionShown = true;
								continue;
							}
						}
						else {
							// Depends on application type.
							text = ProgramGenerator.isExtensionToBuilder() ? slot.getAliasWithId() : slot.getNameForGenerator() + (!foundAttr.isWholeWords ? " " + slot.getAlias() : "");
						}
						
						// If the text is found.
						if (Utility.find(text, foundAttr)) {
							
							// Add to found slots.
							foundSlots.add(new FoundSlot(slot, foundAttr));
							resultCount++;
						}
					}
				}
				
				// Show found slots.
				if (resultCount > 0) {
					
					Safe.invokeLater(() -> {
						
						buttonShowOnlyFound.setSelected(true);
						onShowFound();
					});
				}
			}		
			// Redraw the table.
			tableSlots.updateUI();
			
			// Return number of results.
			return resultCount;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0;
	}
	
	/**
	 * Set do not save state on exit flag.
	 */
	public void setDoNotSaveStateOnExit() {
		
		doNotSaveStateOnExit = true;
	}

	/**
	 * Find slot in list and set it visible.
	 * @param slotId
	 */
	protected void ensureSlotVisible(Long slotId) {
		try {
			
			if (slotId == null) {
				return;
			}
	
			int rowCount = tableSlotsModel.getRowCount();
			
			// Do loop for all slots.
			for (int modelRowIndex = 0; modelRowIndex < rowCount; modelRowIndex++) {
				
				Slot slot = tableSlotsModel.get(modelRowIndex);
				if (slot == null) {
					continue;
				}
				
				if (slot.getId() == slotId) {
					
					int viewRowIndex = tableSlots.convertRowIndexToView(modelRowIndex);
					
					tableSlots.getSelectionModel().setSelectionInterval(viewRowIndex, viewRowIndex);
					Utility.ensureRecordVisible(tableSlots, viewRowIndex, true);
					
					return;
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Returns true value if a single holder is selected. The method
	 * informs user.
	 * @return
	 */
	private boolean isSingleHolder() {
		
		try {
			if (areas.size() != 1) {
				Class<?> holderClass = areas.get(0).getClass();
				String message;
				if (holderClass.equals(Area.class)) {
					message = "org.multipage.generator.messageSelectSingleArea";
				}
				else {
					message = "org.multipage.generator.messageSelectSingleHolder";
				}
				Utility.show(this, message);
				return false;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Returns true value if the slots has not the same holder.
	 * @param holder 
	 * @param clipBoard2
	 * @return
	 */
	private boolean isAnotherHolder(LinkedList<Slot> slots, SlotHolder holder) {
		
		try {
			if (holder == null) {
				return true;
			}
			
			for (Slot slot : clipBoard) {
				
				SlotHolder slotHolder = slot.getHolder();
				if (slotHolder == null) {
					continue;
				}
				
				if (slotHolder.getId() == holder.getId()) {
					return false;
				}
			}
			return true;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Enable trayMenu.
	 * @param popup
	 */
	private void enableMenu() {
		try {
			
			boolean enable = false;
			
			if (areas.size() == 1 && !clipBoard.isEmpty()) {
				SlotHolder holder = areas.get(0);
				if (isAnotherHolder(clipBoard, holder)) {
					enable = true;
				}
			}
			
			// Enable / disable trayMenu items.
			menuMoveSlots.setEnabled(enable);
			menuCopySlots.setEnabled(enable);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Delete slots.
	 * @param middle 
	 * @param slots
	 */
	private MiddleResult deleteSlots(Middle middle, List<Slot> slots) {
		
		try {
			for (Slot slot : slots) {
				MiddleResult result = middle.removeSlot(slot);
				if (result.isNotOK()) {
					return result;
				}
			}
			
			return MiddleResult.OK;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return MiddleResult.UNKNOWN_ERROR;
	}

	/**
	 * Copy slots.
	 */
	protected void useSlots() {
		try {
			
			// Get selected objects.
			int [] selectedRows = getSelectedRows();
			if (selectedRows.length == 0) {
				Utility.show(this, "org.multipage.generator.messageSelectSlots");
				return;
			}
			
			List<Slot> slots = tableSlotsModel.getSlots();
			// Check.
			if (slots.size() == 0) {
				return;
			}
			
			clipBoard.clear();
			
			// Use selected slots.
			for (int selectedRow : selectedRows) {
				Slot slot = (Slot) slots.get(selectedRow);
				clipBoard.add(slot);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Move slots.
	 * @param copy 
	 */
	protected void moveSlots(boolean copy) {
		try {
			
			// Must be a single holder.
			if (!isSingleHolder()) {
				return;
			}
			
			// Clip board must not be empty.
			if (clipBoard.size() == 0) {
				Utility.show(this, "org.multipage.generator.messageClipboardIsEmpty");
				return;
			}
			
			SlotHolder holder = areas.get(0);
			
			// Must be another holder.
			if (!isAnotherHolder(clipBoard, holder)) {
				Utility.show(this, "org.multipage.generator.messageCopyOrMoveSlotsToAnotherPlace");
				return;
			}
	
			// Check slot existence.
			List<Slot> tableSlots = tableSlotsModel.getSlots();
			List<Slot> slotsToMoveOrCopy = new LinkedList<Slot>();
			List<Slot> slotsToDelete = new LinkedList<Slot>();
			
			// Confirm paste.
			if (!SelectSlotsOverride.showDialog(GeneratorMainFrame.getFrame(),
					tableSlots, clipBoard, slotsToDelete, slotsToMoveOrCopy)) {
				return;
			}
			
			// If nothing to move, exit the method.
			if (slotsToMoveOrCopy.isEmpty()) {
				return;
			}
			
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			MiddleResult result = middle.login(login);
			if (result.isOK()) {
				
				// Delete slots.
				result = deleteSlots(middle, slotsToDelete);
				if (result.isOK()) {
					
					if (copy) {
						
						// Remove description references.
						if (ProgramGenerator.isExtensionToBuilder()
								&& Utility.ask(this, "org.multipage.generator.textRemoveSlotDescriptionLinks")) {
							
							Slot.removeDescriptions(slotsToMoveOrCopy);
						}
						
						// Inform user about slot revisions not copied.
						Utility.show(this, "org.multipage.generator.messageSlotRevisionsNotCopied");
						
						// Copy slots.
						result = middle.insertSlotsHolder(slotsToMoveOrCopy, holder);
					}
					else {
						// Move slots.
						result = middle.updateSlotsHolder(slotsToMoveOrCopy, holder);
						// Clear clipboard.
						clipBoard.clear();
					}
				}				
				
				MiddleResult logoutResult = middle.logout(result);
				if (result.isOK()) {
					result = logoutResult;
				}
			}
			if (result.isNotOK()) {
				result.show(this);
			}
			
			// Update data.
			onChange();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get previous messages.
	 */
	@Override
	public LinkedList<Message> getPreviousMessages() {

		return previousUpdateMessages;
	}

	/**
	 * On closing the window.
	 */
	public void onClose() {
		try {
			
			close();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * This receiver objects cannot be removed automatically.
	 */
	@Override
	public boolean canAutoRemove() {
		
		return false;
	}
	
	/**
	 * On update components.
	 */
	@Override
	public void updateComponents() {
		try {
			
			update();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Close the panel.
	 */
	@Override
	public void close() {
		try {
			
			// Unregister from updates.
			GeneratorMainFrame.unregisterFromUpdate(this);
			ApplicationEvents.removeReceivers(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}