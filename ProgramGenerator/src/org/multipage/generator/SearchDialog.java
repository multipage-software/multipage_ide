/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.LinkedList;

import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.maclan.Area;
import org.maclan.AreaId;
import org.maclan.AreasModel;
import org.maclan.Slot;
import org.maclan.SlotHolder;
import org.multipage.basic.ProgramBasic;
import org.multipage.gui.ApplicationEvents;
import org.multipage.gui.GuiSignal;
import org.multipage.gui.Images;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.TextFieldEx;
import org.multipage.gui.Utility;
import org.multipage.util.Resources;
import org.multipage.util.Safe;


/**
 * Dialog for searching for areas and slots.
 * @author vakol
 *
 */
public class SearchDialog extends JDialog {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constants.
	 */
	private static final int AREAS = 0;
	private static final int SLOTS = 1;

	/**
	 * Bounds.
	 */
	private static Rectangle bounds;
	
	/**
	 * Object types to search.
	 */
	private static int searchType = AREAS;
	
	/**
	 * Columns' widths.
	 */
	private static int [] columnsWidthsForAreas;
	private static int[] columnsWidthsForSlots;
	
	/**
	 * Flag that enables to open slots or areas editor.
	 */
	private static boolean openEditorFlag = true;
	
	/**
	 * Column names.
	 */
	private String[] columnNamesForAreas;
	private String[] columnNamesForSlots;
	
	/**
	 * Set default state.
	 */
	public static void setDefaultData() {
		
		bounds = new Rectangle();
		
		searchType = AREAS;
		
		columnsWidthsForAreas = new int [] {250, 100, 30, 30, 250};
		columnsWidthsForSlots = new int [] {250, 100, 30, 30};
		
		openEditorFlag = false;
	}

	/**
	 * Load state.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void serializeData(StateInputStream inputStream)
			throws IOException, ClassNotFoundException {
		
		bounds = Utility.readInputStreamObject(inputStream, Rectangle.class);
		searchType = inputStream.readInt();
		columnsWidthsForAreas = Utility.readInputStreamObject(inputStream, Integer [].class);
		columnsWidthsForSlots = Utility.readInputStreamObject(inputStream, Integer [].class);
		openEditorFlag = inputStream.readBoolean();
	}

	/**
	 * Save state.
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream) 
			throws IOException {
		
		outputStream.writeObject(bounds);
		outputStream.writeInt(searchType);
		outputStream.writeObject(columnsWidthsForAreas);
		outputStream.writeObject(columnsWidthsForSlots);
		outputStream.writeObject(openEditorFlag);
	}

	/**
	 * Table models.
	 */
	private DefaultTableModel tableModelForAreas;
	private DefaultTableModel tableModelForSlots;
	
	/**
	 * Column models.
	 */
	private DefaultTableColumnModel columnModelForAreas;
	private DefaultTableColumnModel columnModelForSlots;
	
	/**
	 * Sort models.
	 */
	private TableRowSorter<TableModel> sortModelForAreas;
	private TableRowSorter<TableModel> sortModelForSlots;

    /**
     * AreasModel reference.
     */
	private AreasModel model;

	/**
	 * Add popup trayMenu.
	 * @param component
	 * @param popup
	 */
	private static void addPopup(Component component, final JPopupMenu popup) {
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
	 * Components.
	 */
	private JPopupMenu popupMenu;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JRadioButton radioAreas;
	private JRadioButton radioSlots;
    private javax.swing.JCheckBox caseSensitive;
    private javax.swing.JButton clearButton;
    private javax.swing.JCheckBox exactMatch;
    private javax.swing.JButton globalAreaButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelSearchString;
    private javax.swing.JButton reloadButton;
    private javax.swing.JTable resultsTable;
    private javax.swing.JLabel searchResultsLabel;
    private javax.swing.JTextField searchStringText;
    private javax.swing.JCheckBox wholeWordsButton;
	private javax.swing.JCheckBox openEditor;

	/**
	 * Initialize components. Created with NetBeans.
	 */
    private void initComponents() {
		setPreferredSize(new Dimension(600, 500));
    	
        labelSearchString = new javax.swing.JLabel();
        searchStringText = new TextFieldEx();
        searchResultsLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        resultsTable = new javax.swing.JTable();
        resultsTable.setRowHeight(22);
        resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        caseSensitive = new javax.swing.JCheckBox();
        caseSensitive.setOpaque(false);
        wholeWordsButton = new javax.swing.JCheckBox();
        wholeWordsButton.setOpaque(false);
        reloadButton = new javax.swing.JButton();
        reloadButton.setPreferredSize(new Dimension(24, 24));
        clearButton = new javax.swing.JButton();
        clearButton.setPreferredSize(new Dimension(24, 24));
        exactMatch = new javax.swing.JCheckBox();
        exactMatch.setOpaque(false);
        exactMatch = new javax.swing.JCheckBox();
        exactMatch.setOpaque(false);
        openEditor = new javax.swing.JCheckBox();
        openEditor.setOpaque(false);
        globalAreaButton = new javax.swing.JButton();

        setTitle("org.multipage.generator.textSearchDialogTitle");

        labelSearchString.setText("org.multipage.generator.textSearchStringLabel");

        searchStringText.setForeground(new java.awt.Color(255, 0, 0));

        searchResultsLabel.setText("org.multipage.generator.textSearchResults");
        
        popupMenu = new JPopupMenu();
        addPopup(resultsTable, popupMenu);
        
        resultsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(resultsTable);

        caseSensitive.setText("org.multipage.generator.textCaseSensitive");
        
        openEditor.setText("org.multipage.generator.textOpenEditor");

        wholeWordsButton.setText("org.multipage.generator.textWholeWords");

        exactMatch.setText("org.multipage.generator.textExactMatch");

        globalAreaButton.setText("org.multipage.generator.textGlobalArea");
        globalAreaButton.setMargin(new Insets(0, 0, 0, 0));
        globalAreaButton.setPreferredSize(new Dimension(100, 25));
        
        radioAreas = new JRadioButton("org.multipage.generator.textSearchAreas");
        radioAreas.setOpaque(false);
        radioAreas.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		onAreas();
        	}
        });
        buttonGroup.add(radioAreas);
        
        radioSlots = new JRadioButton("org.multipage.generator.textSearchSlots");
        radioSlots.setOpaque(false);
        radioSlots.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		onSlots();
        	}
        });
        buttonGroup.add(radioSlots);
        SpringLayout springLayout = new SpringLayout();
        springLayout.putConstraint(SpringLayout.WEST, radioAreas, 180, SpringLayout.WEST, getContentPane());
        springLayout.putConstraint(SpringLayout.WEST, caseSensitive, 130, SpringLayout.WEST, radioAreas);
        springLayout.putConstraint(SpringLayout.WEST, openEditor, 20, SpringLayout.EAST, caseSensitive);
        springLayout.putConstraint(SpringLayout.NORTH, openEditor, 0, SpringLayout.NORTH, caseSensitive);
        springLayout.putConstraint(SpringLayout.WEST, exactMatch, 0, SpringLayout.WEST, wholeWordsButton);
        springLayout.putConstraint(SpringLayout.SOUTH, searchStringText, 0, SpringLayout.SOUTH, clearButton);
        springLayout.putConstraint(SpringLayout.SOUTH, jScrollPane1, -10, SpringLayout.SOUTH, getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, globalAreaButton, -10, SpringLayout.EAST, getContentPane());
        springLayout.putConstraint(SpringLayout.NORTH, jScrollPane1, 3, SpringLayout.SOUTH, searchResultsLabel);
        springLayout.putConstraint(SpringLayout.EAST, jScrollPane1, -10, SpringLayout.EAST, getContentPane());
        springLayout.putConstraint(SpringLayout.NORTH, searchResultsLabel, 35, SpringLayout.SOUTH, radioSlots);
        springLayout.putConstraint(SpringLayout.NORTH, radioAreas, 35, SpringLayout.SOUTH, searchStringText);
        springLayout.putConstraint(SpringLayout.NORTH, caseSensitive, 25, SpringLayout.SOUTH, searchStringText);
        springLayout.putConstraint(SpringLayout.NORTH, exactMatch, 0, SpringLayout.SOUTH, wholeWordsButton);
        springLayout.putConstraint(SpringLayout.NORTH, wholeWordsButton, 0, SpringLayout.SOUTH, caseSensitive);
        springLayout.putConstraint(SpringLayout.WEST, wholeWordsButton, 0, SpringLayout.WEST, caseSensitive);
        springLayout.putConstraint(SpringLayout.NORTH, radioSlots, 6, SpringLayout.SOUTH, radioAreas);
        springLayout.putConstraint(SpringLayout.WEST, radioSlots, 0, SpringLayout.WEST, radioAreas);
        springLayout.putConstraint(SpringLayout.WEST, searchStringText, 3, SpringLayout.EAST, clearButton);
        springLayout.putConstraint(SpringLayout.EAST, searchStringText, -10, SpringLayout.EAST, getContentPane());
        springLayout.putConstraint(SpringLayout.WEST, clearButton, 3, SpringLayout.EAST, reloadButton);
        springLayout.putConstraint(SpringLayout.NORTH, searchStringText, 31, SpringLayout.NORTH, getContentPane());
        springLayout.putConstraint(SpringLayout.NORTH, clearButton, 31, SpringLayout.NORTH, getContentPane());
        springLayout.putConstraint(SpringLayout.NORTH, reloadButton, 31, SpringLayout.NORTH, getContentPane());
        springLayout.putConstraint(SpringLayout.WEST, reloadButton, 10, SpringLayout.WEST, getContentPane());
        springLayout.putConstraint(SpringLayout.NORTH, globalAreaButton, 160, SpringLayout.NORTH, getContentPane());
        springLayout.putConstraint(SpringLayout.WEST, searchResultsLabel, 10, SpringLayout.WEST, getContentPane());
        springLayout.putConstraint(SpringLayout.NORTH, labelSearchString, 11, SpringLayout.NORTH, getContentPane());
        springLayout.putConstraint(SpringLayout.WEST, labelSearchString, 10, SpringLayout.WEST, getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, labelSearchString, 565, SpringLayout.WEST, getContentPane());
        springLayout.putConstraint(SpringLayout.WEST, jScrollPane1, 10, SpringLayout.WEST, getContentPane());
        getContentPane().setLayout(springLayout);
        getContentPane().add(jScrollPane1);
        getContentPane().add(labelSearchString);
        getContentPane().add(searchResultsLabel);
        getContentPane().add(globalAreaButton);
        getContentPane().add(reloadButton);
        getContentPane().add(clearButton);
        getContentPane().add(radioAreas);
        getContentPane().add(radioSlots);
        getContentPane().add(exactMatch);
        getContentPane().add(caseSensitive);
        getContentPane().add(openEditor);
        getContentPane().add(wholeWordsButton);
        getContentPane().add(searchStringText);

        pack();
        
        addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent arg0) {
        		onClose();
        	}
        });
    }
    
    /**
     * On areas.
     */
    protected void onAreas() {
		try {
			
			// Switch table.
	    	switchTable(AREAS);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
    
    /**
     * On slots.
     */
	protected void onSlots() {
		try {
			
			// Switch table.
	    	switchTable(SLOTS);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Switch search results table.
	 * @param searchType
	 */
	private void switchTable(int searchType) {
		try {
			
			// Clear table.
	    	Utility.clearTable(resultsTable);
	    	
	    	// Set table type.
			setTableType(searchType);
			
			// Update search results.
			updateSearchResults();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
     * Localize components texts.
     */
	private void localize() {
		try {
			
			Utility.localize(this);
			Utility.localize(labelSearchString);
			Utility.localize(radioAreas);
			Utility.localize(radioSlots);
			Utility.localize(searchResultsLabel);
			Utility.localize(caseSensitive);
			Utility.localize(openEditor);
			Utility.localize(wholeWordsButton);
			Utility.localize(exactMatch);
			Utility.localize(globalAreaButton);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Constructor.
	 */
	public SearchDialog() {
		
		try {
			initComponents();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Constructor.
	 * @param parent
	 */
    public SearchDialog(java.awt.Frame parent, AreasModel model) {
        super(parent, false);
        
        try {
	        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	        setPreferredSize(new Dimension(600, 600));
			setIconImage(Images.getImage("org/multipage/generator/images/main_icon.png"));
	
	        this.model = model;
	        
	        // Load table model.
	        initComponents();
	        // Set initial focus.
	        searchStringText.requestFocusInWindow();
	        // Set component texts.
	        localize();
	        // Load icons.
	        setIcons();
	        // Set colors.
	        setColors();
	        // Add listeners.
	        setListeners();
	        // Initialize popup menu.
	        initPopupMenu();
	        // Load dialog state.
	        loadDialog();
	        // Load table models.
	        loadTableModels();
	        // Set search type.
	        setTableType(searchType);
	    }
		catch (Throwable e) {
			Safe.exception(e);
		}
    }

	/**
     * Initialize popup trayMenu.
     */
    private void initPopupMenu() {
    	try {
			
			final Component thisComponent = this;
			
			AreaLocalMenu areaMenu = ProgramGenerator.newAreaLocalMenu(new AreaLocalMenu.Callbacks() {
				@Override
				protected Area getCurrentArea() {
					try {
						// Get selected area.
						return getSelectedArea();
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return null;
				}
	
				@Override
				public Component getComponent() {
					// Get this component.
					return thisComponent;
				}
			});
			
			areaMenu.addTo(this, popupMenu);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};	
	}

    /**
     * Get selected area depending on search type.
     * @return
     */
	protected Area getSelectedArea() {
		
		try {
			int selectedRow = resultsTable.getSelectedRow();
			if (selectedRow == -1) {
				return null;
			}
			selectedRow = resultsTable.getRowSorter().convertRowIndexToModel(selectedRow);
			
			Area area = null;
			switch (searchType) {
			
			case SLOTS:
				area = (Area) tableModelForSlots.getValueAt(selectedRow, 2);
				break;
				
			case AREAS:
			default:
				area = (Area) tableModelForAreas.getValueAt(selectedRow, 0);
			}
			
			return area;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
     * On close.
     */
	protected void onClose() {
		try {
			
			saveDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
		
		dispose();
	}

	/**
	 * Load dialog.
	 */
	private void loadDialog() {
		try {
			
			if (bounds.isEmpty()) {
				Utility.centerOnScreen(this);
				bounds = getBounds();
			}
			else {
				setBounds(bounds);
			}
			
			openEditor.setSelected(openEditorFlag);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
    /**
     * Decorate identifier number
     * @param id
     */
    private static String decorateId(Long id) {
    	
    	try {
	    	String idText = id != null ? String.format("[%d]", id) : "[?]";
	    	return idText;
	    }
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
    }
    
    /**
     * Deacorate count.
     * @param count
     * @return
     */
	private static String decorateCount(Integer count) {
		
		try {
	    	String countText = count != null ? String.format("#%d#", count) : "#?#";
	    	return countText;
	    }
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
     * Load table models.
     */
	@SuppressWarnings("serial")
	private void loadTableModels() {
		try {
			
			resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			
			// Create areas model.
			columnNamesForAreas = new String [] {
	                Resources.getString("org.multipage.generator.textSearchResultAreas"),
	                Resources.getString("org.multipage.generator.textSearchResultAreaAliases"),
	                Resources.getString("org.multipage.generator.textSearchResultId"),
	                Resources.getString("org.multipage.generator.textSearchResultNumSlots"),
	                Resources.getString("org.multipage.generator.textSearchResultParentArea")
	            };
			
			int columnCountForAreas = columnNamesForAreas.length;
			
			// Create model with disabled editing of table cells.
			tableModelForAreas = new DefaultTableModel(columnNamesForAreas, columnCountForAreas) {
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			
			// Initialize table.
			tableModelForAreas.setRowCount(0);
			
			// Create area columns model.
			columnModelForAreas = new DefaultTableColumnModel();
			
			for (int index = 0; index < columnCountForAreas; index++) {
				
				TableColumn column = new TableColumn(index, columnsWidthsForAreas[index]);
				column.setPreferredWidth(columnsWidthsForAreas[index]);
				columnModelForAreas.addColumn(column);
			}
			
			// Create sort model for areas.
			sortModelForAreas = new TableRowSorter<javax.swing.table.TableModel>(tableModelForAreas);
			
			// Create slots model.
			columnNamesForSlots = new String [] {
	                Resources.getString("org.multipage.generator.textSearchResultSlotAliases"),
	                Resources.getString("org.multipage.generator.textSearchResultId"),
	                Resources.getString("org.multipage.generator.textSearchResultSlotsAreas"),
	            };
			
			int columnCountForSlots = columnNamesForSlots.length;
			
			// Create model with disabled editing of table cells.
			tableModelForSlots = new DefaultTableModel(columnNamesForSlots, columnCountForSlots) {
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			
			// Initialize table.
			tableModelForSlots.setRowCount(0);
			
			// Create slot columns model.
			columnModelForSlots = new DefaultTableColumnModel();
			
			for (int index = 0; index < columnCountForSlots; index++) {
				
				TableColumn column = new TableColumn(index, columnsWidthsForSlots[index]);
				column.setPreferredWidth(columnsWidthsForSlots[index]);
				columnModelForSlots.addColumn(column);
			}
			
			// Create sort model for slots.
			sortModelForSlots = new TableRowSorter<javax.swing.table.TableModel>(tableModelForSlots);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set search type.
	 */
	private void setTableType(int searchType) {
		try {
			
			// Save column widths.
			saveColumnWidths(SearchDialog.searchType);
			
			// Set the dialog controls and labels. 
			switch (searchType) {
				
			case SLOTS:
				
				SearchDialog.searchType = SLOTS;
				
				radioSlots.setSelected(true);
				
				labelSearchString.setText(Resources.getString("org.multipage.generator.textSearchSlotsLabel"));
				searchResultsLabel.setText(Resources.getString("org.multipage.generator.textSearchResultsForSlots"));
				
				resultsTable.setColumnModel(columnModelForSlots);
				resultsTable.setRowSorter(sortModelForSlots);
				resultsTable.setModel(tableModelForSlots);
				
				break;
				
			case AREAS:
			default:
				
				SearchDialog.searchType = AREAS;
				
				radioAreas.setSelected(true);
				
				labelSearchString.setText(Resources.getString("org.multipage.generator.textSearchAreasLabel"));
				searchResultsLabel.setText(Resources.getString("org.multipage.generator.textSearchResultsForAreas"));
				
				resultsTable.setColumnModel(columnModelForAreas);
				resultsTable.setRowSorter(sortModelForAreas);
				resultsTable.setModel(tableModelForAreas);
				
				break;
			}
			
			// Load column widths.
			loadColumnWidths(SearchDialog.searchType);
			
			// Set columns listener.
			resultsTable.getTableHeader().addMouseListener(new MouseAdapter() {
	
				@Override
				public void mouseReleased(MouseEvent e) {
					try {
						
						super.mouseReleased(e);
						
						// Get clicked column index.
						int clickedTableColumn = resultsTable.columnAtPoint(e.getPoint());
						if (clickedTableColumn != -1) {
							
							// Sort table by the clicked column contents.
							Utility.sortTable(resultsTable, clickedTableColumn);
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			
			// Set table cell renderer.
			setTableCellRenderers(searchType);
			
			// Update GUI.
			resultsTable.updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Load column widths.
	 * @param searchType
	 */
	private void loadColumnWidths(int searchType) {
		try {
			
			// Set column widths.
			TableColumn column = null;
			
			switch (searchType) {
			
			case SLOTS:
				
				int columnCount = columnNamesForSlots.length;
				
				for (int index = 0; index < columnCount; index++) {
					
					column = columnModelForSlots.getColumn(index);
					int width = columnsWidthsForSlots[index];
					column.setPreferredWidth(width);
					column.setWidth(width);
					
				}
				
				resultsTable.setColumnModel(columnModelForSlots);
				break;
				
			case AREAS:
				
				columnCount = columnNamesForAreas.length;
				
				for (int index = 0; index < columnCount; index++) {
					
					column = columnModelForAreas.getColumn(index);
					int width = columnsWidthsForAreas[index];
					column.setPreferredWidth(width);
					column.setWidth(width);
					
				}
				
				resultsTable.setColumnModel(columnModelForAreas);
				break;
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Save column widths.
	 */
	private void saveColumnWidths(int searchType) {
		try {
			
			// Set column widths.
			TableColumn column = null;
			
			switch (searchType) {
			case SLOTS:
				int columnCount = columnNamesForSlots.length;
				for (int index = 0; index < columnCount; index++) {
					
					column = columnModelForSlots.getColumn(index);
					columnsWidthsForSlots[index] = column.getWidth();
				}
				break;
				
			case AREAS:
				columnCount = columnNamesForAreas.length;
				for (int index = 0; index < columnCount; index++) {
					
					column = columnModelForAreas.getColumn(index);
					columnsWidthsForAreas[index] = column.getWidth();
				}
				break;
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Save dialog.
	 */
	private void saveDialog() {
		try {
			
			bounds = getBounds();
			// Save column widths.
			saveColumnWidths();
			openEditorFlag = openEditor.isSelected();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Save column widths.
	 */
    private void saveColumnWidths() {
		try {
			
			saveColumnWidths(AREAS);
			saveColumnWidths(SLOTS);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
    
	/**
     * Load icons.
     */
    private void setIcons() {
    	try {
			
			reloadButton.setIcon(Images.getIcon("org/multipage/generator/images/update_icon.png"));
			clearButton.setIcon(Images.getIcon("org/multipage/generator/images/remove_icon.png"));
			globalAreaButton.setIcon(Images.getIcon("org/multipage/generator/images/center.png"));
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
			
			// Use customized area selection color.
			this.getContentPane().setBackground(CustomizedColors.get(ColorId.SEARCH_DIALOG));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
    
	/**
     * Add listeners.
     */
    public void setListeners() {
    	try {
			
			searchStringText.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void insertUpdate(DocumentEvent e) {
					try {
						
						updateSearchResults();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				@Override
				public void removeUpdate(DocumentEvent e) {
					try {
						
						updateSearchResults();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				@Override
				public void changedUpdate(DocumentEvent e) {
					try {
						
						updateSearchResults();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
		    });
	        caseSensitive.addItemListener(new java.awt.event.ItemListener() {
	            public void itemStateChanged(java.awt.event.ItemEvent evt) {
	            	try {
						
						updateSearchResults();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
	            }
	        });
	        wholeWordsButton.addItemListener(new java.awt.event.ItemListener() {
	            public void itemStateChanged(java.awt.event.ItemEvent evt) {
	            	try {
						
						updateSearchResults();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
	            }
	        });
	        exactMatch.addItemListener(new java.awt.event.ItemListener() {
	            public void itemStateChanged(java.awt.event.ItemEvent evt) {
	            	try {
						
						updateSearchResults();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
	            }
	        });
	        resultsTable.addMouseListener(new java.awt.event.MouseAdapter() {
	            public void mouseClicked(java.awt.event.MouseEvent evt) {
	            	try {
						
						// Invoke method.
		            	if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
		            		onSelectionChanged(resultsTable.getSelectedRow());
		            	}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
	            }
	        });
	        reloadButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						
						updateSearchResults();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
	        });
	        clearButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						
						// Reset search string and invoke change method.
						searchStringText.setText("");
						updateSearchResults();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
	        globalAreaButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						
						// Set focus on global area.
						GeneratorMainFrame.getFrame().getVisibleAreasEditor().focusGlobalArea();
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
     * On selection changed.
     * @param selectedRow
     */
	protected void onSelectionChanged(int selectedRow) {
		
		try {
			
			// If no selection, exit the method.
			if (selectedRow == -1) {
				return;
			}
			selectedRow = resultsTable.getRowSorter().convertRowIndexToModel(selectedRow);
			
			// Get flag.
			boolean openEditoFlag = openEditor.isSelected();
			
			// Do action depending on search type.
			switch (searchType) {
			
			case SLOTS:
				
				// Get selected slot.
				Object item = tableModelForSlots.getValueAt(selectedRow, 0);
				if (item == null) {
					return;
				}
				
				Slot selectedSlot = (Slot) item;
				
				// Open slot editor.
				if (openEditoFlag) {
					SlotEditorFrame.showDialog(this, selectedSlot, false, true, null);
				}
				
				// Focus area.
				AreaId areaIdHolder = (AreaId) selectedSlot.getHolder();
				if (areaIdHolder != null) {
					
					Long areaId = areaIdHolder.getId();
					if (areaId != null) {
						
						// Transmit focus area signal.
						ApplicationEvents.transmit(this, GuiSignal.focusArea, areaId);
					}
				}
				break;
				
			case AREAS:
			default:
				
				// Get selected coordinates.
				item = tableModelForAreas.getValueAt(selectedRow, 4);
				if (item == null) {
					return;
				}
				
				// Get selected area.
				Area area = getSelectedArea();
				if (area == null) {
					return;
				}
				long areaId = area.getId();
				
				// Get selected area and shapes.
				AreaCoordinatesTableItem coordinatesItem = (AreaCoordinatesTableItem) item;
				
				// Open area editor.
				if (openEditoFlag) {
					AreaEditorFrame.showDialog(this, area);
				}
				
				// Transmit focus area signal.
				ApplicationEvents.transmit(this, GuiSignal.focusArea, areaId, coordinatesItem);
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}			
	}

	/**
     * Update search results.
     */
    private void updateSearchResults() {
    	try {
			
			// Clear table.
	    	Utility.clearTable(resultsTable);
	    	
	    	// Invoke the method content asynchronously.
	    	Safe.invokeLater(() -> {
					
				// Get search text and load table.
		 		String searchText = searchStringText.getText();
		 		
		 		switch (searchType) {
		 		case SLOTS:
		 			loadSlotsTable(searchText);
		 			break;
		 			
		 		case AREAS:
		 		default:
		 			loadAreasTable(searchText);
		 		}
	    	});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
    }
    
	/**
     * Load areas table.
     * @param searchText 
     */
	private void loadAreasTable(String searchText) {
		try {
			
			// Sort the table.
			Utility.sortTable(resultsTable, -1);
			
			// Do loop for all area shapes in the model.
			for (Area area : model.getAreas()) {
				
				String areaAlias = area.getAlias();
				long areaId = area.getId();
				String areaIdText = decorateId(areaId);
				int slotCount = area.getSlotAliasesCount();
				String slotCountText = decorateCount(slotCount);
				
				// If the area description contains the search string.
				if (Utility.matches(new String [] { area.getDescription(), areaAlias, areaIdText, slotCountText },
						searchText, caseSensitive.isSelected(),
						wholeWordsButton.isSelected(), exactMatch.isSelected())) {
					
					Object user = area.getUser();
					if (user != null && user instanceof AreaShapes) {
						
						AreaShapes shapes = (AreaShapes) user;
						
						// Do loop for all shape coordinates.
						for (AreaCoordinates coordinate : shapes.getCoordinates()) {
							
							tableModelForAreas.addRow(new Object [] 
							        { area, areaAlias, areaId, slotCount, new AreaCoordinatesTableItem(coordinate) });
						}
					}
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Load slots table.
	 * @param searchText
	 */
    protected void loadSlotsTable(String searchText) {
    	try {
			
			// Sort the table.
	    	Utility.sortTable(resultsTable, -1);
			
	    	LinkedList<Slot> slots = null;
			try {
				
				ProgramBasic.loginMiddle();
				slots = ProgramBasic.getSlots();
			}
			catch (Exception e) {
				Utility.show2(this, e.getLocalizedMessage());
			}
			finally {
				ProgramBasic.logoutMiddle();
			}
			
			if (slots == null || slots.isEmpty()) {
				return;
			}
			
			// Do loop for all area shapes in the model.
			for (Slot slot : slots) {
				
				// Get holder.
				SlotHolder slotHolder = slot.getHolder();
				if (slotHolder instanceof AreaId) {
					slotHolder = ProgramGenerator.getArea((AreaId) slotHolder);
				}
				
				long slotId = slot.getId();
				String slotIdText = decorateId(slotId);
				String slotAlias = slot.getAlias();
				String slotHolderDescription = slotHolder.getDescriptionForced(true);
				
				// If the area description contains the search string.
				if (Utility.matches(new String [] { slotAlias, slotIdText, slotHolderDescription },
						searchText, caseSensitive.isSelected(),
						wholeWordsButton.isSelected(), exactMatch.isSelected())) {
					
					// Do loop for all shape coordinates.
					tableModelForSlots.addRow(new Object [] { slot, slotId, slotHolder });
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
    
	/**
	 * Set table cell renderer.
	 * @param searchType
	 */
	private void setTableCellRenderers(int searchType) {
		try {
			
			switch (searchType) {
			
			case SLOTS:
				
				// Set cell renderer for slot.
				Utility.setTableCellRenderer(resultsTable, 0, value -> isSelected -> hasFocus -> row -> ((Slot) value).getAlias());
				// Set cell renderer for slot IDs.
				Utility.setTableCellRenderer(resultsTable, 1, value -> isSelected -> hasFocus -> row -> decorateId((Long) value));
				// Set cell renderer for slot holder.
				Utility.setTableCellRenderer(resultsTable, 2, value -> isSelected -> hasFocus -> row -> ((Area) value).getDescriptionForcedAndDecorated(true));
				
				break;
				
			case AREAS:
			default:
				// Create cell renderer for area IDs.
				Utility.setTableCellRenderer(resultsTable, 0, value -> isSelected -> hasFocus -> row -> value);
				// Create cell renderer for area IDs.
				Utility.setTableCellRenderer(resultsTable, 1, value -> isSelected -> hasFocus -> row -> value);
				// Create cell renderer for area IDs.
				Utility.setTableCellRenderer(resultsTable, 2, value -> isSelected -> hasFocus -> row -> decorateId((Long) value));
				// Create cell renderer for slot count.
				Utility.setTableCellRenderer(resultsTable, 3, value -> isSelected -> hasFocus -> row -> decorateCount((Integer) value));
				// Create cell renderer for parent areas.
				Utility.setTableCellRenderer(resultsTable, 4, value -> isSelected -> hasFocus -> row -> ((AreaCoordinatesTableItem) value).toDecoratedString());
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
