/*
 * Copyright 2017 (C) multipage-software.org
 * 
 * Created on : 26-04-2017
 *
 */

package program.builder;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.swing.DefaultRowSorter;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.maclan.Slot;
import org.multipage.basic.ProgramBasic;
import org.multipage.generator.FoundSlot;
import org.multipage.generator.SlotCellRenderer;
import org.multipage.generator.SlotEditorFrame;
import org.multipage.generator.SlotListPanel;
import org.multipage.gui.GraphUtility;
import org.multipage.gui.Images;
import org.multipage.gui.ToolBarKit;
import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Editor of slots.
 * @author vakol
 *
 */
public class SlotListPanelBuilder extends SlotListPanel {
	
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Show hidden slots flag.
	 */
	public static boolean showHiddenSlots = false;

	/**
	 * Menu items.
	 */
	private JMenuItem menuSetSelectedSlots;
	protected JMenuItem menuEditDescription;
	private JMenuItem menuCopyTextValue;
	
	/**
	 * Load slots from database.
	 */
	@Override
	protected void loadSlotsFromDatabase() {
		try {
			
			MiddleResult result = ProgramBasic.getMiddle().loadAreasSlots(
					ProgramBasic.getLoginProperties(), areas, showHiddenSlots, true);
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/* (non-Javadoc)
	 * @see org.multipage.generator.SlotListPanel#postCreate()
	 */
	@Override
	protected void postCreate() {
		try {
			
			// Call superclass method.
			super.postCreate();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Initialize tool bars.
	 */
	@Override
	protected void createToolBar() {
		try {
			
			// Add tool bar buttons.
	        ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/add_item_icon.png", "builder.tooltipNewSlot", () -> onNew());
	        ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/rename_node.png", "org.multipage.generator.tooltipEditSlot", () -> onEditFull());
	        ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/edit_simple.png", "org.multipage.generator.tooltipSimpleEditSlot", () -> onEditSimple());
	        ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/remove_node.png", "builder.tooltipRemoveSlot", () -> onRemove());
	        toolBar.addSeparator();
	        ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/update_icon.png", "org.multipage.generator.tooltipUpdateSlots", () -> onUpdate());
	        toolBar.addSeparator();
	        ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/select_all.png", "builder.tooltipSelectAllSlots", () -> onSelectAll());
	        ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/deselect_all.png", "builder.tooltipUnselectAllSlots", () -> onUnselectAll());
	        toolBar.addSeparator();
	        ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/search_icon.png", "org.multipage.generator.tooltipSearchInSlots", () -> onSearch());
	        toolBar.addSeparator();
	        buttonShowUserSlots = ToolBarKit.addToggleButton(toolBar, "org/multipage/generator/images/preferred.png", "org.multipage.generator.tooltipShowUserSlots", () -> onShowUser());
	        buttonShowOnlyFound = ToolBarKit.addToggleButton(toolBar, "org/multipage/generator/images/selected.png", "org.multipage.generator.tooltipShowFoundSlots", () -> onClickShowFound());
	        toolBar.addSeparator();
	        ToolBarKit.addToggleButton(toolBar, "org/multipage/generator/images/help_small.png", "builder.tooltipEditSlotDescription", () -> onEditSlotDescription());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Localize components.
	 */
	@Override
	protected void localize() {
		try {
			
			super.localize();
			
			Utility.localize(menuSetSelectedSlots);
			Utility.localize(menuEditDescription);
			Utility.localize(menuCopyTextValue);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set icons.
	 */
	@Override
	protected void setIcons() {
		try {
			
			super.setIcons();
			
			menuSetSelectedSlots.setIcon(Images.getIcon("org/multipage/gui/images/properties.png"));
			menuEditDescription.setIcon(Images.getIcon("org/multipage/generator/images/help_small.png"));
			menuCopyTextValue.setIcon(Images.getIcon("org/multipage/generator/images/copy_value.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On new slot.
	 */
	public void onNew() {
		try {
			
			// If there is more than one holder, inform user and exit.
			if (areas.size() != 1) {
				Utility.show(this, "builder.messageMoreThanOneHolderSelected");
				return;
			}
	
			// Create new slot.
			Slot slot = new Slot(areas.getFirst());
			
			// Edit slot data.
			SlotEditorFrame.showDialog(slot, true, true, null);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On remove slot.
	 */
	public void onRemove() {
		try {
			
			// Get selected table items.
			int [] selectedRows = getSelectedRows();
			// If nothing selected, inform user and exit the method.
			if (selectedRows.length == 0) {
				Utility.show(this, "org.multipage.generator.messageSelectSlots");
				return;
			}
			
			// Confirm deletion.
			if (JOptionPane.showConfirmDialog(this,
					Resources.getString("builder.messageDeleteSelectedSlots"))
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
			
			// Remove selected items.
			tableSlotsModel.removeAll(slotsToDelete);
			
			onChange();
			
			// Update information.
			updateInformation();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Select all items.
	 */
	public void onSelectAll() {
		try {
			
			tableSlots.setRowSelectionInterval(0, tableSlots.getRowCount() - 1);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Unselect all items.
	 */
	public void onUnselectAll() {
		try {
			
			tableSlots.clearSelection();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create trayMenu.
	 */
	@Override
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
			
			menuCopyTextValue = new JMenuItem("org.multipage.generator.textCopySlotTextValue");
			menuCopyTextValue.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						
						copySlotTextValue();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			popupMenu.add(menuCopyTextValue);
			popupMenu.addSeparator();
			
			menuSetSelectedSlots = new JMenuItem("builder.textSetSelectedSlots");
			menuSetSelectedSlots.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						
						onSetSlotsProperties();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			popupMenu.add(menuSetSelectedSlots);
			
			popupMenu.addSeparator();
			
			menuEditDescription = new JMenuItem("org.multipage.generator.menuSlotDescription");
			menuEditDescription.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						
						editSlotDescription();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			popupMenu.add(menuEditDescription);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Copy slot text value.
	 */
	protected void copySlotTextValue() {
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
			String textValue = slot.getTextValue();
			
			// Copy value to clipboard.
			Clipboard clipboard = Toolkit.getDefaultToolkit ().getSystemClipboard();
			StringSelection stringSelection = new StringSelection (textValue);
			clipboard.setContents(stringSelection, null);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On set slots' properties.
	 */
	protected void onSetSlotsProperties() {
		try {
			
			// Get selected slots.
			int [] selectedRows = getSelectedRows();
			if (selectedRows.length < 1) {
				
				Utility.show(this, "org.multipage.generator.messageSelectSlots");
				return;
			}
			
			// Get slot properties.
			Obj<Character> access = new Obj<Character>();
			Obj<Boolean> hidden = new Obj<Boolean>();
			Obj<Boolean> isDefault = new Obj<Boolean>();
			Obj<Boolean> isPreferred = new Obj<Boolean>();
			
			if (!SlotPropertiesDialog.showDialog(this, access, hidden, isDefault, isPreferred)) {
				return;
			}
			
			if (access.ref == null && hidden.ref == null 
					&& isDefault.ref == null && isPreferred.ref == null) {
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
				
				if (access.ref != null) {
					// Update slot access.
					result = middle.updateSlotAccess(slot.getHolder().getId(), slot.getAlias(),
							access.ref.toString());
					if (result.isNotOK()) {
						break;
					}
				}
				
				if (hidden.ref != null) {
					// Update slot hidden flag.
					result = middle.updateSlotHidden(slot.getHolder().getId(), slot.getAlias(), hidden.ref);
					if (result.isNotOK()) {
						break;
					}
				}
				
				if (isDefault.ref != null) {
					// Update slot default value.
					result = middle.updateSlotIsDefault(slot.getHolder().getId(), slot.getAlias(), isDefault.ref);
					if (result.isNotOK()) {
						break;
					}
				}
				
				if (isPreferred.ref != null) {
					// Update slot is preferred.
					result = middle.updateSlotIsPreferred(slot.getHolder().getId(), slot.getAlias(), isPreferred.ref);
					if (result.isNotOK()) {
						break;
					}
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
			
			// Update information.
			updateInformation();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	private void updateInformation() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Initialize table.
	 */
	@Override
	protected void initTable() {
		try {
			
			tableSlotsModel = new SlotsTableModelBuilder(areas, foundSlots, buttonShowOnlyFound.isSelected(), !buttonShowUserSlots.isSelected());
			tableSlots.setModel(tableSlotsModel);
			
			tableSlots.setAutoCreateRowSorter(true);
	        DefaultRowSorter sorter = ((DefaultRowSorter) tableSlots.getRowSorter());
	        List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
	        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
	        sorter.setSortKeys(sortKeys);
	        
			// Set renderer.
			setTableRenderer();
			
			// Set selection listener.
			ListSelectionModel selectionModel = tableSlots.getSelectionModel();
			selectionModel.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					try {
						
						// Load slot description.
						loadSlotDescription();
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
	 * Set table renderer.
	 */
	@Override
	protected void setTableRenderer() {
		try {
			
			// Access renderer.
			TableColumn column1 = tableSlots.getColumnModel().getColumn(0);
			column1.setCellRenderer(new TableCellRenderer() {
				// Renderer.
				AccessRenderer renderer = new AccessRenderer();
				// Get renderer.
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value,
						boolean isSelected, boolean hasFocus, int row, int column) {
					
					try {
						if (!(value instanceof Character)) {
							return null;
						}
						
						row = tableSlots.convertRowIndexToModel(row);
						
						Slot slot = tableSlotsModel.get(row);
						char access = (Character) value;
						renderer.setProperties(access, isSelected, hasFocus,
								FoundSlot.isSlotFound(foundSlots, slot));
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return renderer;
				}
			});
			
			// Create cell renderer.
			TableCellRenderer cellRenderer = new TableCellRenderer() {
				// Renderer.
				SlotCellRenderer renderer = new SlotCellRenderer();
				// Get renderer.
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value,
						boolean isSelected, boolean hasFocus, int row, int column) {
					
					try {
						// Convert index.
						column = tableSlots.convertColumnIndexToModel(column);
						row = tableSlots.convertRowIndexToModel(row);
						
						Slot slot = tableSlotsModel.get(row);
						renderer.setSlotCell(slot, column, value, isSelected, hasFocus, 
								FoundSlot.isSlotFound(foundSlots, slot), true);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return renderer;
				}
			};
			
			// Set renderers.
			TableColumnModel columnModel = tableSlots.getColumnModel();
			for (int index = 1; index < columnModel.getColumnCount(); index++) {
				TableColumn column = columnModel.getColumn(index);
				column.setCellRenderer(cellRenderer);
			}
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
	 * Set divider position to maximum.
	 */
	public void setDividerPositionToMaximum() {

		setDividerPositionToMaximum  = true;
	}
}


/**
 * Access renderer.
 * @author
 *
 */
class AccessRenderer extends JLabel {
	
	/**
	 * Highlight color.
	 */
	private static final Color highlightColor = new Color(255, 100, 100);
	
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Icons.
	 */
	private static Icon publicIcon;
	private static Icon privateIcon;
	
	/**
	 * Constructor.
	 */
	static {
		
		try {
			publicIcon = Images.getIcon("org/multipage/generator/images/public.png");
			privateIcon = Images.getIcon("org/multipage/generator/images/private.png");
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * States.
	 */
	private boolean isSelected;
	private boolean hasFocus;

	/**
	 * Constructor.
	 */
	public AccessRenderer() {
		try {
			
			setOpaque(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set properties.
	 * @param access
	 * @param hasFocus 
	 * @param isSelected 
	 */
	public void setProperties(char access, boolean isSelected, boolean hasFocus,
			boolean isFound) {
		try {
			
			this.isSelected = isSelected;
			this.hasFocus = hasFocus;
			
			if (access == Slot.publicAccess) {
				setIcon(publicIcon);
			}
			else if (access == Slot.privateAccess) {
				setIcon(privateIcon);
			}
			else {
				setIcon(null);
			}
			setBackground(isFound ? highlightColor : Color.WHITE);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		
		try {
			super.paint(g);
			GraphUtility.drawSelection(g, this, isSelected, hasFocus);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
}
