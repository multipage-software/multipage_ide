/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package program.builder;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.maclan.AreasModel;
import org.maclan.EnumerationObj;
import org.maclan.EnumerationValue;
import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.multipage.basic.ProgramBasic;
import org.multipage.generator.ProgramGenerator;
import org.multipage.gui.ColumnIdentifier;
import org.multipage.gui.Images;
import org.multipage.gui.SearchTextDialog;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.ToolBarKit;
import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.Safe;

/**
 * Dialog that dislays editor for enumerations.
 * @author vakol
 *
 */
public class EnumerationsEditorDialog extends JDialog {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Dialog bounds.
	 */
	private static Rectangle bounds;
	
	/**
	 * Split position.
	 */
	private static int splitPosition;
	
	/**
	 * Enumeration types column widths.
	 */
	private static Integer[] columnsWidthsTypes;

	/**
	 * Enumeration value column widths.
	 */
	private static Integer [] columnsWidthsValues;

	/**
	 * Set default state.
	 */
	public static void setDefaultData() {
		
		bounds = new Rectangle();
		splitPosition = -1;
		columnsWidthsTypes = new Integer [] { 40, 200 };
		columnsWidthsValues = new Integer [] { 40, 100, 200 };
	}

	/**
	 * Load state.
	 * @param inputStream
	 */
	public static void serializeData(StateInputStream inputStream) 
			throws IOException, ClassNotFoundException {
		
		Object object = inputStream.readObject();
		if (!(object instanceof Rectangle)) {
			throw new ClassNotFoundException();
		}
		bounds = (Rectangle) object;
		
		splitPosition = inputStream.readInt();
		
		object = inputStream.readObject();
		if (!(object instanceof Integer [])) {
			throw new ClassNotFoundException();
		}
		Integer [] widths = (Integer []) object;
		if (widths.length != 2) {
			throw new ClassNotFoundException();
		}
		columnsWidthsTypes = widths;
		
		object = inputStream.readObject();
		if (!(object instanceof Integer [])) {
			throw new ClassNotFoundException();
		}
		widths = (Integer []) object;
		if (widths.length != 3) {
			throw new ClassNotFoundException();
		}
		columnsWidthsValues = widths;
	}

	/**
	 * Save state.
	 * @param outputStream
	 */
	public static void serializeData(StateOutputStream outputStream) 
			throws IOException {
		
		outputStream.writeObject(bounds);
		outputStream.writeInt(splitPosition);
		outputStream.writeObject(columnsWidthsTypes);
		outputStream.writeObject(columnsWidthsValues);
	}

	
	/**
	 * Single dialog object.
	 */
	private static EnumerationsEditorDialog dialog;

	/**
	 * Types table model.
	 */
	private DefaultTableModel tableModelTypes;
	
	/**
	 * Values table model.
	 */
	private DefaultTableModel tableModelValues;
	
	// $hide<<$
	/**
	 * Components.
	 */
	private JPanel panel;
	private JButton buttonClose;
	private JSplitPane splitPane;
	private JPanel panelLeft;
	private JPanel panelRight;
	private JLabel labelEnumerationTypes;
	private JLabel labelEnumerationValues;
	private JToolBar toolBarTypes;
	private JScrollPane scrollPaneTypes;
	private JToolBar toolBarValues;
	private JScrollPane scrollPaneValues;
	private JTable tableTypes;
	private JTable tableValues;
	private JPopupMenu popupMenuTypes;
	private JPopupMenu popupMenuValues;
	private JMenuItem menuAddEnumerationType;
	private JMenuItem menuAddEnumerationValue;
	private JMenuItem menuEditEnumeration;
	private JMenuItem menuEditEnumerationValue;
	private JMenuItem menuRemoveEnumeration;
	private JMenuItem menuRemoveEnumerationValue;

	/**
	 * Show dialog.
	 * @param parent
	 * @param resource
	 */
	public static void showDialog(Component parent) {
		try {
			
			if (dialog == null) {
				dialog = new EnumerationsEditorDialog(Utility.findWindow(parent));
			}
			
			// Load enumeration types.
			dialog.loadEnumerationTypes();
			dialog.setVisible(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Create the dialog.
	 * @param parentWindow 
	 */
	public EnumerationsEditorDialog(Window parentWindow) {
		super(parentWindow, ModalityType.MODELESS);
		
		try {
			initComponents();
			// $hide>>$
			postCreate();
			// $hide<<$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Initialize components.
	 */
	private void initComponents() {
		setMinimumSize(new Dimension(520, 340));
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				onClose();
			}
		});
		setTitle("builder.textEnumerationsEditorDialog");
		
		setBounds(100, 100, 520, 340);
		
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(10, 50));
		getContentPane().add(panel, BorderLayout.SOUTH);
		SpringLayout sl_panel = new SpringLayout();
		panel.setLayout(sl_panel);
		
		buttonClose = new JButton("textClose");
		buttonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onClose();
			}
		});
		buttonClose.setMargin(new Insets(0, 0, 0, 0));
		sl_panel.putConstraint(SpringLayout.SOUTH, buttonClose, -10, SpringLayout.SOUTH, panel);
		sl_panel.putConstraint(SpringLayout.EAST, buttonClose, -10, SpringLayout.EAST, panel);
		buttonClose.setPreferredSize(new Dimension(80, 25));
		panel.add(buttonClose);
		
		splitPane = new JSplitPane();
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(0.5);
		getContentPane().add(splitPane, BorderLayout.CENTER);
		
		panelLeft = new JPanel();
		splitPane.setLeftComponent(panelLeft);
		SpringLayout sl_panelLeft = new SpringLayout();
		panelLeft.setLayout(sl_panelLeft);
		
		labelEnumerationTypes = new JLabel("builder.textEnumerationTypesList");
		sl_panelLeft.putConstraint(SpringLayout.NORTH, labelEnumerationTypes, 3, SpringLayout.NORTH, panelLeft);
		sl_panelLeft.putConstraint(SpringLayout.WEST, labelEnumerationTypes, 3, SpringLayout.WEST, panelLeft);
		panelLeft.add(labelEnumerationTypes);
		
		toolBarTypes = new JToolBar();
		toolBarTypes.setFloatable(false);
		toolBarTypes.setPreferredSize(new Dimension(13, 20));
		sl_panelLeft.putConstraint(SpringLayout.WEST, toolBarTypes, 3, SpringLayout.WEST, panelLeft);
		sl_panelLeft.putConstraint(SpringLayout.SOUTH, toolBarTypes, 0, SpringLayout.SOUTH, panelLeft);
		sl_panelLeft.putConstraint(SpringLayout.EAST, toolBarTypes, 0, SpringLayout.EAST, panelLeft);
		panelLeft.add(toolBarTypes);
		
		scrollPaneTypes = new JScrollPane();
		sl_panelLeft.putConstraint(SpringLayout.NORTH, scrollPaneTypes, 3, SpringLayout.SOUTH, labelEnumerationTypes);
		sl_panelLeft.putConstraint(SpringLayout.WEST, scrollPaneTypes, 0, SpringLayout.WEST, panelLeft);
		sl_panelLeft.putConstraint(SpringLayout.SOUTH, scrollPaneTypes, 0, SpringLayout.NORTH, toolBarTypes);
		sl_panelLeft.putConstraint(SpringLayout.EAST, scrollPaneTypes, 0, SpringLayout.EAST, panelLeft);
		panelLeft.add(scrollPaneTypes);
		
		popupMenuTypes = new JPopupMenu();
		addPopup(scrollPaneTypes, popupMenuTypes);
		
		menuAddEnumerationType = new JMenuItem("builder.menuAddEnumerationType");
		menuAddEnumerationType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onNewEnumerationType();
			}
		});
		popupMenuTypes.add(menuAddEnumerationType);
		
		tableTypes = new JTable();
		scrollPaneTypes.setViewportView(tableTypes);
		
		panelRight = new JPanel();
		splitPane.setRightComponent(panelRight);
		SpringLayout sl_panelRight = new SpringLayout();
		panelRight.setLayout(sl_panelRight);
		
		labelEnumerationValues = new JLabel("builder.textEnumerationValuesList");
		sl_panelRight.putConstraint(SpringLayout.NORTH, labelEnumerationValues, 3, SpringLayout.NORTH, panelRight);
		sl_panelRight.putConstraint(SpringLayout.WEST, labelEnumerationValues, 3, SpringLayout.WEST, panelRight);
		panelRight.add(labelEnumerationValues);
		
		toolBarValues = new JToolBar();
		sl_panelRight.putConstraint(SpringLayout.EAST, toolBarValues, 0, SpringLayout.EAST, panelRight);
		toolBarValues.setFloatable(false);
		toolBarValues.setPreferredSize(new Dimension(13, 20));
		sl_panelRight.putConstraint(SpringLayout.WEST, toolBarValues, 0, SpringLayout.WEST, labelEnumerationValues);
		sl_panelRight.putConstraint(SpringLayout.SOUTH, toolBarValues, 0, SpringLayout.SOUTH, panelRight);
		panelRight.add(toolBarValues);
		
		scrollPaneValues = new JScrollPane();
		sl_panelRight.putConstraint(SpringLayout.NORTH, scrollPaneValues, 3, SpringLayout.SOUTH, labelEnumerationValues);
		sl_panelRight.putConstraint(SpringLayout.WEST, scrollPaneValues, 0, SpringLayout.WEST, panelRight);
		sl_panelRight.putConstraint(SpringLayout.SOUTH, scrollPaneValues, 0, SpringLayout.NORTH, toolBarValues);
		sl_panelRight.putConstraint(SpringLayout.EAST, scrollPaneValues, 0, SpringLayout.EAST, panelRight);
		panelRight.add(scrollPaneValues);
		
		popupMenuValues = new JPopupMenu();
		addPopup(scrollPaneValues, popupMenuValues);
		
		menuAddEnumerationValue = new JMenuItem("builder.menuAddEnumerationValue");
		menuAddEnumerationValue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onNewEnumerationValue();
			}
		});
		popupMenuValues.add(menuAddEnumerationValue);
		
		tableValues = new JTable();
		scrollPaneValues.setViewportView(tableValues);
		
		addPopup(tableTypes, popupMenuTypes);
		
		menuEditEnumeration = new JMenuItem("builder.menuEditEnumerationType");
		menuEditEnumeration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onEditEnumerationType();
			}
		});
		popupMenuTypes.add(menuEditEnumeration);
		
		menuRemoveEnumeration = new JMenuItem("builder.menuRemoveEnumerationType");
		menuRemoveEnumeration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRemoveEnumerationType();
			}
		});
		popupMenuTypes.add(menuRemoveEnumeration);
		addPopup(tableValues, popupMenuValues);
		
		menuEditEnumerationValue = new JMenuItem("builder.menuEditEnumerationValue");
		menuEditEnumerationValue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onEditEnumerationValue();
			}
		});
		popupMenuValues.add(menuEditEnumerationValue);
		
		menuRemoveEnumerationValue = new JMenuItem("builder.menuRemoveEnumerationValue");
		menuRemoveEnumerationValue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRemoveEnumerationValue();
			}
		});
		popupMenuValues.add(menuRemoveEnumerationValue);
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
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			
			loadDialog();
			
			localize();
			setIcons();
			
			// Create tool bars.
			createToolBars();
			
			// Initialize tables.
			initializeTypesTable();
			initializeValuesTable();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Load dialog.
	 */
	private void loadDialog() {
		try {
			
			if (bounds.width == 0) {
				Utility.centerOnScreen(this);
			}
			else {
				setBounds(bounds);
			}
			
			if (splitPosition != -1) {
				splitPane.setDividerLocation(splitPosition);
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
			splitPosition = splitPane.getDividerLocation();
			columnsWidthsTypes = getColumnWidths(tableTypes);
			columnsWidthsValues = getColumnWidths(tableValues);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get column widths
	 * @param table
	 * @return
	 */
	private Integer[] getColumnWidths(JTable table) {
		
		try {
			Enumeration<TableColumn> columns = table.getColumnModel().getColumns();
			LinkedList<Integer> widths = new LinkedList<Integer>();
			
			while (columns.hasMoreElements()) {
				
				TableColumn column = columns.nextElement();
				widths.add(column.getWidth());
			}
			
			Integer [] widthsArray = new Integer [widths.size()];
			return widths.toArray(widthsArray);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Localize components.
	 */
	private void localize() {
		try {
			
			Utility.localize(this);
			Utility.localize(buttonClose);
			Utility.localize(labelEnumerationTypes);
			Utility.localize(labelEnumerationValues);
			Utility.localize(menuAddEnumerationType);
			Utility.localize(menuAddEnumerationValue);
			Utility.localize(menuEditEnumeration);
			Utility.localize(menuEditEnumerationValue);
			Utility.localize(menuRemoveEnumeration);
			Utility.localize(menuRemoveEnumerationValue);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set icons.
	 */
	private void setIcons() {
		try {
			
			buttonClose.setIcon(Images.getIcon("org/multipage/generator/images/cancel_icon.png"));
			menuAddEnumerationType.setIcon(Images.getIcon("org/multipage/generator/images/add_item_icon.png"));
			menuAddEnumerationValue.setIcon(Images.getIcon("org/multipage/generator/images/add_item_icon.png"));
			menuEditEnumeration.setIcon(Images.getIcon("org/multipage/generator/images/edit.png"));
			menuEditEnumerationValue.setIcon(Images.getIcon("org/multipage/generator/images/edit.png"));
			menuRemoveEnumeration.setIcon(Images.getIcon("org/multipage/generator/images/remove_icon.png"));
			menuRemoveEnumerationValue.setIcon(Images.getIcon("org/multipage/generator/images/remove_icon.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create tool bars.
	 */
	private void createToolBars() {
		try {
			
			ToolBarKit.addToolBarButton(toolBarTypes, "org/multipage/generator/images/add_item_icon.png",
					"builder.tooltipAddNewEnumerationType", () -> onNewEnumerationType());
			ToolBarKit.addToolBarButton(toolBarTypes, "org/multipage/generator/images/edit.png",
					"builder.tooltipEditEnumerationType", () -> onEditEnumerationType());
			ToolBarKit.addToolBarButton(toolBarTypes, "org/multipage/generator/images/remove_icon.png",
					"builder.tooltipRemoveEnumerationType", () -> onRemoveEnumerationType());
			toolBarTypes.addSeparator();
			ToolBarKit.addToolBarButton(toolBarTypes, "org/multipage/generator/images/update_icon.png",
					"builder.tooltipUpdateEnumerations", () -> onUpade());
			toolBarTypes.addSeparator();
			ToolBarKit.addToolBarButton(toolBarTypes, "org/multipage/generator/images/search_icon.png",
					"builder.tooltipSearchEnumerationType", () -> onSearchEnumerationType());
			
			
			ToolBarKit.addToolBarButton(toolBarValues, "org/multipage/generator/images/add_item_icon.png",
					"builder.tooltipAddNewEnumerationValue", () -> onNewEnumerationValue());
			ToolBarKit.addToolBarButton(toolBarValues, "org/multipage/generator/images/edit.png",
					"builder.tooltipEditEnumerationValue", () -> onEditEnumerationValue());
			ToolBarKit.addToolBarButton(toolBarValues, "org/multipage/generator/images/remove_icon.png",
					"builder.tooltipRemoveEnumerationValue", () -> onRemoveEnumerationValue());
			toolBarValues.addSeparator();
			ToolBarKit.addToolBarButton(toolBarValues, "org/multipage/generator/images/update_icon.png",
					"builder.tooltipUpdateEnumerations", () -> onUpade());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Initialize types table.
	 */
	@SuppressWarnings("serial")
	private void initializeTypesTable() {
		try {
			
			// Columns definition.
			final Object [][] columnsDefinitionTypes = {
					// [description], [is editable]
					{ new ColumnIdentifier("builder.textEnumerationTypeIdentifierColumn"), false },
					{ new ColumnIdentifier("builder.textEnumerationTypeDescriptionColumn"), true }
			};
	
			// Create and set table model.
			tableModelTypes = new DefaultTableModel() {
				// Get cell editable flag.
				@Override
				public boolean isCellEditable(int row, int column) {
					try {
						return (Boolean) columnsDefinitionTypes[column][1];
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return false;
				}
				// Set cell value.
				@Override
				public void setValueAt(Object aValue, int row, int column) {
					try {
						
						String description = (String) aValue;
						editEnumerationType(description);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			};
			
			// Add columns.
			for (Object [] columnDefinition : columnsDefinitionTypes) {
				tableModelTypes.addColumn(columnDefinition[0]);
			}
			
			// Set model.
			tableTypes.setModel(tableModelTypes);
			tableTypes.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			tableTypes.setRowHeight(20);
			tableTypes.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
			tableTypes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			Font font = tableValues.getFont();
			font = font.deriveFont(Font.ITALIC);
			tableTypes.setFont(font);
			
			// Set columns' widths.
			int index = 0;
			for (Object [] columnDefinition : columnsDefinitionTypes) {
				
				TableColumn column = tableTypes.getColumn(columnDefinition[0]);
				column.setPreferredWidth(columnsWidthsTypes[index]);
				index++;
			}
			
			// Set selection listener.
			tableTypes.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				// On row selection.
				@Override
				public void valueChanged(ListSelectionEvent e) {
					try {
						
						onEnumerationTypeSelection(e);
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
	 * Initialize values table.
	 */
	@SuppressWarnings("serial")
	private void initializeValuesTable() {
		try {
			
			// Columns definition.
			final Object [][] columnsDefinitionValues = {
					// [description], [is editable]
					{ new ColumnIdentifier("builder.textEnumerationValueIdentifierColumn"), false },
					{ new ColumnIdentifier("builder.textEnumerationValueColumn"), true },
					{ new ColumnIdentifier("builder.textEnumerationValueDescriptionColumn"), true }
			};
	
			// Create and set table model.
			tableModelValues = new DefaultTableModel() {
				// Get cell editable flag.
				@Override
				public boolean isCellEditable(int row, int column) {
					try {
						return (Boolean) columnsDefinitionValues[column][1];
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return false;
				}
				// Set cell value.
				@Override
				public void setValueAt(Object aValue, int row, int column) {
					try {
						
						String value = (String) aValue;
						
						if (column == 1) {
							editEnumerationValue(value);
						}
						else if (column == 2) {
							editEnumerationDescription(value);
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			};
			
			// Add columns.
			for (Object [] columnDefinition : columnsDefinitionValues) {
				tableModelValues.addColumn(columnDefinition[0]);
			}
			
			// Set model.
			tableValues.setModel(tableModelValues);
			tableValues.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			tableValues.setRowHeight(20);
			tableValues.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
			tableValues.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			Font font = tableValues.getFont();
			font = font.deriveFont(Font.BOLD);
			tableValues.setFont(font);
			
			// Set columns' widths.
			int index = 0;
			for (Object [] columnDefinition : columnsDefinitionValues) {
				
				TableColumn column = tableValues.getColumn(columnDefinition[0]);
				column.setPreferredWidth(columnsWidthsValues[index]);
				index++;
			}
			
			// Set selection listener.
			tableTypes.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				// On row selection.
				@Override
				public void valueChanged(ListSelectionEvent e) {
					try {
						
						onEnumerationTypeSelection(e);
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
	 * Load enumeration types.
	 */
	private void loadEnumerationTypes() {
		try {
			
			// Clear table.
			tableModelTypes.setRowCount(0);
			
			AreasModel model = ProgramGenerator.getAreasModel();
			
			// Load enumerations.
			for (EnumerationObj enumeration : model.getEnumerations()) {
				tableModelTypes.addRow(
						new Object [] { enumeration.getId(), enumeration.getDescription()});
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On enumeration type selection.
	 * @param e 
	 */
	public void onEnumerationTypeSelection(ListSelectionEvent e) {
		try {
			
			// If the selection is not complete, exit the method.
			if (e.getValueIsAdjusting()) {
				return;
			}
			
			// Load enumeration values.
			loadEnumerationValues();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load enumeration values.
	 */
	private void loadEnumerationValues() {
		try {
			
			// Clear table.
			tableModelValues.setRowCount(0);
			
			// Get selected types table row.
			int selectedRow = tableTypes.getSelectedRow();
			if (selectedRow == -1) {
				return;
			}
			
			// Get enumeration ID and corresponding object.
			long enumerationId = (Long) tableModelTypes.getValueAt(selectedRow, 0);
			EnumerationObj enumeration = ProgramGenerator.getAreasModel().getEnumeration(enumerationId);
			
			if (enumeration == null) {
				return;
			}
			
			// Load enumeration values.
			for (EnumerationValue enumerationValue : enumeration.getValues()) {
				tableModelValues.addRow(
						new Object [] { enumerationValue.getId(), enumerationValue.getValue(), enumerationValue.getDescription()});
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get selected enumeration type.
	 * @return
	 */
	private EnumerationObj getSelectedEnumerationType() {
		
		try {
			// Get selected row.
			int selectedRow = tableTypes.getSelectedRow();
			if (selectedRow == -1) {
				return null;
			}
			
			// Get enumeration identifier.
			long enumerationId = (Long) tableModelTypes.getValueAt(selectedRow, 0);
			
			// Get enumeration object reference.
			EnumerationObj enumeration = ProgramGenerator.getAreasModel().getEnumeration(enumerationId);
			return enumeration;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Get selected enumeration value.
	 * @return
	 */
	private EnumerationValue getSelectedEnumerationValue() {
		
		try {
			// Get selected enumeration type.
			EnumerationObj enumeration = getSelectedEnumerationType();
			if (enumeration == null) {
				return null;
			}
			
			// Get selected value row.
			int selectedRow = tableValues.getSelectedRow();
			if (selectedRow == -1) {
				return null;
			}
			
			long enumerationValueId = (Long) tableModelValues.getValueAt(selectedRow, 0);
			
			// Get enumeration value object.
			EnumerationValue enumerationValue = enumeration.getValue(enumerationValueId);
			return enumerationValue;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Get enumeration type.
	 * @param index
	 * @return
	 */
	private EnumerationObj getEnumerationType(int index) {
		
		try {
			long enumerationId = (Long) tableTypes.getValueAt(index, 0);
			
			EnumerationObj enumeration = ProgramGenerator.getAreasModel().getEnumeration(enumerationId);
			return enumeration;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Select enumeration type.
	 * @param enumerationId
	 */
	private void selectEnumerationType(long enumerationId) {
		try {
			
			for (int index = 0; index < tableModelTypes.getRowCount(); index++) {
				
				// Get enumeration description.
				long enumerationIdItem = (Long) tableModelTypes.getValueAt(index, 0);
				if (enumerationIdItem == enumerationId) {
					
					// Select the found row.
					tableTypes.addRowSelectionInterval(index, index);
					tableTypes.scrollRectToVisible(tableTypes.getCellRect(index, 1, true)); 
					return;
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Select enumeration type.
	 * @param description
	 */
	private void selectEnumerationType(String description) {
		try {
			
			for (int index = 0; index < tableModelTypes.getRowCount(); index++) {
				
				// Get enumeration description.
				String enumerationDescription = (String) tableModelTypes.getValueAt(index, 1);
				if (enumerationDescription.equals(description)) {
					
					// Select found row.
					tableTypes.addRowSelectionInterval(index, index);
					tableTypes.scrollRectToVisible(tableTypes.getCellRect(index, 1, true)); 
					return;
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Select enumeration value.
	 * @param valueId
	 */
	private void selectEnumerationValue(long valueId) {
		try {
			
			for (int index = 0; index < tableModelValues.getRowCount(); index++) {
				
				// Get enumeration value ID.
				long enumerationValueId = (Long) tableModelValues.getValueAt(index, 0);
				if (enumerationValueId == valueId) {
					
					// Select found row.
					tableValues.addRowSelectionInterval(index, index);
					tableValues.scrollRectToVisible(tableValues.getCellRect(index, 1, true)); 
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Select enumeration value.
	 * @param value
	 */
	private void selectEnumerationValue(String value) {
		try {
			
			for (int index = 0; index < tableModelValues.getRowCount(); index++) {
				
				// Get enumeration value description.
				String enumerationValueDescription = (String) tableModelValues.getValueAt(index, 1);
				if (enumerationValueDescription.equals(value)) {
					
					// Select found row.
					tableValues.addRowSelectionInterval(index, index);
					tableValues.scrollRectToVisible(tableValues.getCellRect(index, 1, true)); 
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On new enumeration type.
	 */
	public void onNewEnumerationType() {
		try {
			
			// Get new enumeration description.
			String description = Utility.input(this, "builder.messageInsertNewEnumerationDescription");
			if (description == null) {
				return;
			}
			
			// Prepare prerequisites.
			Properties login = ProgramBasic.getLoginProperties();
			Middle middle = ProgramBasic.getMiddle();
			
			// Insert new enumeration.
			MiddleResult result = middle.insertEnumeration(login, description);
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			updateInformation();
			
			// Load enumerations combo box and select item.
			loadEnumerationTypes();
			
			selectEnumerationType(description);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Remove enumeration type.
	 */
	public void onRemoveEnumerationType() {
		try {
			
			// Get selected item.
			EnumerationObj enumeration = getSelectedEnumerationType();
			if (enumeration == null) {
				
				Utility.show(this, "builder.messageSelectSingleEnumeration");
				return;
			}
			
			// Let user confirm deletion.
			if (!Utility.ask(this, "builder.messageDeleteEnumeration", enumeration.getDescription())) {
				return;
			}
			
			// Prepare prerequisites.
			Properties login = ProgramBasic.getLoginProperties();
			Middle middle = ProgramBasic.getMiddle();
			MiddleResult result;
			
			boolean errorReported = false;
			
			// Try to remove enumeration values.
			for (EnumerationValue enumerationValue : enumeration.getValues()) {
				
				
				// Remove enumeration value.
				result = middle.removeEnumerationValue(login, enumerationValue.getId());
				if (!errorReported && result.isNotOK()) {
					result.show(this);
					errorReported = true;
				}
			}
				
			// On error exit the method.
			if (errorReported) {
				return;
			}
			
			// Remove enumeration.
			result = middle.removeEnumeration(login, enumeration.getId());
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
	
			updateInformation();
			
			int selectedIndex = tableTypes.getSelectedRow();
			
			// Load enumerations and select item.
			loadEnumerationTypes();
			
			if (selectedIndex >= 0) {
				int rowCount = tableTypes.getRowCount();
				if (selectedIndex >= rowCount) {
					selectedIndex = rowCount - 1;
				}
				tableTypes.addRowSelectionInterval(selectedIndex, selectedIndex);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On edit enumeration type.
	 * @param description 
	 */
	public void onEditEnumerationType() {
		try {
			
			editEnumerationType(null);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On edit enumeration type.
	 * @param newDescription 
	 */
	public void editEnumerationType(String newDescription) {
		
		try {
			// Get selected item.
			EnumerationObj enumeration = getSelectedEnumerationType();
			if (enumeration == null) {
				
				Utility.show(this, "builder.messageSelectSingleEnumeration");
				return;
			}
	
			// Get new enumeration description.
			if (newDescription == null) {
				newDescription = Utility.input(this,
						"builder.messageInsertNewEnumerationDescription", enumeration.getDescription());
				
				if (newDescription == null) {
					return;
				}
			}
			
			// Prepare prerequisites.
			Properties login = ProgramBasic.getLoginProperties();
			Middle middle = ProgramBasic.getMiddle();
			
			// Update description.
			MiddleResult result = middle.updateEnumeration(login, enumeration.getId(), newDescription);
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			updateInformation();
			loadEnumerationTypes();
			
			// Restore selection.
			selectEnumerationType(enumeration.getId());
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * On update.
	 */
	public void onUpade() {
		try {
			
			updateInformation();
	
			EnumerationObj enumeration = getSelectedEnumerationType();
			EnumerationValue enumerationValue = getSelectedEnumerationValue();
			
			loadEnumerationTypes();
			
			if (enumeration != null) {
				selectEnumerationType(enumeration.getId());
			}
			
			if (enumerationValue != null) {
				selectEnumerationValue(enumerationValue.getId());
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On search enumeration type.
	 */
	public void onSearchEnumerationType() {
		try {
			
			// Get search parameters.
			SearchTextDialog.Parameters parameters = SearchTextDialog.showDialog(this,
					"builder.textSearchEnumeration");
			if (parameters == null) {
				return;
			}
			
			// Get selected item index.
			int selectedIndex = tableTypes.getSelectedRow();
			if (selectedIndex == -1) {
				selectedIndex = 0;
			}
			int count = tableTypes.getRowCount();
			
			// Search combo box item.
			for (int index = selectedIndex; index >= 0 && index < count;
					index = parameters.isForward() ? index + 1 : index - 1) {
				
				// Get item.
				EnumerationObj enumeration = getEnumerationType(index);
				String description = enumeration.getDescription();
				
				// If the enumeration is found, select it and exit the method.
				if (Utility.find(description, parameters)) {
					
					tableTypes.addRowSelectionInterval(index, index);
					tableTypes.scrollRectToVisible(tableTypes.getCellRect(index, 1, true)); 
					return;
				}
			}
			
			// If nothing found inform user.
			Utility.show(this, "builder.messageEnumerationDescriptionNotFound");
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On new enumeration value.
	 */
	public void onNewEnumerationValue() {
		try {
			
			// If the enumeration type is not selected, inform user.
			EnumerationObj enumeration = getSelectedEnumerationType();
			if (enumeration == null) {
				
				Utility.show(this, "builder.messageSelectEnumerationType");
				return;
			}
			
			// Get enumeration text value and description.
			Obj<String> value = new Obj<String>();
			Obj<String> description = new Obj<String>();
			
			if (!NewEnumerationValueDialog.showDialog(this, enumeration.getDescription(), value, description)) {
				return;
			}
			
			// Prepare prerequisites.
			Properties login = ProgramBasic.getLoginProperties();
			Middle middle = ProgramBasic.getMiddle();
			
			// Insert enumeration value.
			MiddleResult result = middle.insertEnumerationValue(login, enumeration.getId(),
					value.ref, description.ref);
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			updateInformation();
			
			loadEnumerationTypes();
			
			// Select enumeration value.
			selectEnumerationType(enumeration.getId());
			selectEnumerationValue(value.ref);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On edit enumeration value.
	 */
	public void onEditEnumerationValue() {
		try {
			
			editEnumerationValueDescription();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On edit enumeration value.
	 */
	protected void editEnumerationValue(String newEnumerationValue) {
		try {
			
			// Get selected enumeration value.
			EnumerationValue enumerationValue = getSelectedEnumerationValue();
			
			if (enumerationValue == null) {
				// Inform user.
				Utility.show(this, "builder.messageSelectSingleEnumerationValue");
				return;
			}
			
			// Prepare prerequisites.
			Properties login = ProgramBasic.getLoginProperties();
			Middle middle = ProgramBasic.getMiddle();
			
			// Update enumeration value.
			MiddleResult result = middle.updateEnumerationValue(login, enumerationValue.getId(),
					newEnumerationValue);
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			updateInformation();
	
			loadEnumerationTypes();
			
			// Select enumeration value.
			selectEnumerationType(enumerationValue.getEnumerationId());
			selectEnumerationValue(newEnumerationValue);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On edit enumeration value.
	 */
	protected void editEnumerationValueDescription() {
		try {
			
			// Get selected enumeration value.
			EnumerationValue enumerationValue = getSelectedEnumerationValue();
			
			if (enumerationValue == null) {
				// Inform user.
				Utility.show(this, "builder.messageSelectSingleEnumerationValue");
				return;
			}
			
			// Get new value and description.
			Obj<String> value = new Obj<String>(enumerationValue.getValue());
			Obj<String> description = new Obj<String>(enumerationValue.getDescription());
			
			if (!EditEnumerationValueDialog.showDialog(this, enumerationValue.getEnumeration().getDescription(), value, description)) {
				return;
			}
			
			// Prepare prerequisites.
			Properties login = ProgramBasic.getLoginProperties();
			Middle middle = ProgramBasic.getMiddle();
			
			MiddleResult result = middle.updateEnumerationValueAndDescription(
					login, enumerationValue.getId(), value.ref, description.ref) ;
	
			// On error inform user.
			if (result.isNotOK()) {
				result.show(this);
			}
			
			updateInformation();
	
			loadEnumerationTypes();
			
			// Select enumeration value.
			selectEnumerationType(enumerationValue.getEnumerationId());
			selectEnumerationValue(value.ref);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Edit enumeration description.
	 */
	protected void editEnumerationDescription(String newDescription) {
		try {
			
			// Get selected enumeration value.
			EnumerationValue enumerationValue = getSelectedEnumerationValue();
			
			if (enumerationValue == null) {
				// Inform user.
				Utility.show(this, "builder.messageSelectSingleEnumerationValue");
				return;
			}
			
			// Get new description.
			if (newDescription == null) {
	
				newDescription = Utility.input(this, "builder.messageInsertNewEnumerationValueDescription", 
						enumerationValue.getValue());
				if (newDescription == null) {
					return;
				}
			}
			
			// Prepare prerequisites.
			Properties login = ProgramBasic.getLoginProperties();
			Middle middle = ProgramBasic.getMiddle();
			
			// Update enumeration value description.
			MiddleResult result = middle.updateEnumerationValueDescription(login, enumerationValue.getId(),
					newDescription);
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			updateInformation();
			loadEnumerationTypes();
			
			// Select enumeration value.
			selectEnumerationType(enumerationValue.getEnumerationId());
			selectEnumerationValue(enumerationValue.getId());
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Remove enumeration value.
	 */
	public void onRemoveEnumerationValue() {
		try {
			
			// Get selected enumeration value.
			EnumerationValue enumerationValue = getSelectedEnumerationValue();
			
			if (enumerationValue == null) {
				// Inform user.
				Utility.show(this, "builder.messageSelectSingleEnumerationValue");
				return;
			}
			
			// Let user confirm the enumeration value deletion.
			if (!Utility.ask(this, "builder.messageDeleteEnumerationValue", enumerationValue.getValue())) {
				return;
			}
			
			// Prepare prerequisites.
			Properties login = ProgramBasic.getLoginProperties();
			Middle middle = ProgramBasic.getMiddle();
			
			// Remove enumeration value.
			MiddleResult result = middle.removeEnumerationValue(login, enumerationValue.getId());
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			int selectedIndex = tableValues.getSelectedRow();
			
			updateInformation();
			
			loadEnumerationTypes();
			selectEnumerationType(enumerationValue.getEnumerationId());
			
			// Select combo box item.
			if (selectedIndex >= 0) {
				int rowCount = tableValues.getRowCount();
				if (selectedIndex >= rowCount) {
					selectedIndex = rowCount - 1;
				}
				tableValues.addRowSelectionInterval(selectedIndex, selectedIndex);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	private void updateInformation() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Add popup.
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
}