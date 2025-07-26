/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.table.AbstractTableModel;

import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Panel that displays editor for CSS counters.
 * @author vakol
 *
 */
public class CssCountersPanel extends InsertPanel implements StringValueEditor {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Serialized dialog states.
	 */
	protected static Rectangle bounds;
	private static boolean boundsSet;
	
	/**
	 * Set default states.
	 */
	public static void setDefaultData() {
		
		bounds = new Rectangle(0, 0, 469, 450);
		boundsSet = false;
	}

	/**
	 * Load serialized states.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void serializeData(StateInputStream inputStream)
			throws IOException, ClassNotFoundException {
		
		bounds = Utility.readInputStreamObject(inputStream, Rectangle.class);
		boundsSet = true;
	}

	/**
	 * Save serialized states.
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream)
			throws IOException {
		
		outputStream.writeObject(bounds);
	}

	/**
	 * Initial string.
	 */
	private String initialString;
	
	/**
	 * Table model.
	 */
	private TableModel tableModel;
	
	// $hide<<$
	
	/**
	 * Components.
	 */
	private JLabel labelCounters;
	private JScrollPane scrollPane;
	private JTable table;
	private JLabel labelCounterName;
	private JTextField textCounterName;
	private JLabel labelNumber;
	private JTextField textNumber;
	private JButton buttonAdd;
	private JButton buttonDelete;
	
	/**
	 * Create the dialog.
	 * @param parentWindow 
	 */
	public CssCountersPanel(String initialString) {
		
		try {
			initComponents();
			
			// $hide>>$
			this.initialString = initialString;
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
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				onStopEditing();
			}
		});
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		labelCounters = new JLabel("org.multipage.gui.textCounters");
		springLayout.putConstraint(SpringLayout.NORTH, labelCounters, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, labelCounters, 10, SpringLayout.WEST, this);
		add(labelCounters);
		
		scrollPane = new JScrollPane();
		scrollPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				onStopEditing();
			}
		});
		scrollPane.setPreferredSize(new Dimension(280, 100));
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, labelCounters);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, this);
		add(scrollPane);
		
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(table);
		
		labelCounterName = new JLabel("org.multipage.gui.textCounterName");
		springLayout.putConstraint(SpringLayout.WEST, labelCounterName, 0, SpringLayout.WEST, labelCounters);
		add(labelCounterName);
		
		textCounterName = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textCounterName, 0, SpringLayout.NORTH, labelCounterName);
		springLayout.putConstraint(SpringLayout.WEST, textCounterName, 6, SpringLayout.EAST, labelCounterName);
		add(textCounterName);
		textCounterName.setColumns(10);
		
		labelNumber = new JLabel("org.multipage.gui.textCounterNumber");
		springLayout.putConstraint(SpringLayout.NORTH, labelNumber, 0, SpringLayout.NORTH, labelCounterName);
		springLayout.putConstraint(SpringLayout.WEST, labelNumber, 6, SpringLayout.EAST, textCounterName);
		add(labelNumber);
		
		textNumber = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textNumber, 0, SpringLayout.NORTH, labelCounterName);
		springLayout.putConstraint(SpringLayout.WEST, textNumber, 6, SpringLayout.EAST, labelNumber);
		add(textNumber);
		textNumber.setColumns(5);
		
		buttonAdd = new JButton("org.multipage.gui.textAddCounterIncrement");
		springLayout.putConstraint(SpringLayout.NORTH, labelCounterName, 26, SpringLayout.SOUTH, buttonAdd);
		springLayout.putConstraint(SpringLayout.NORTH, buttonAdd, 6, SpringLayout.SOUTH, scrollPane);
		springLayout.putConstraint(SpringLayout.WEST, buttonAdd, 0, SpringLayout.WEST, labelCounters);
		buttonAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onAdd();
			}
		});
		buttonAdd.setMargin(new Insets(0, 0, 0, 0));
		buttonAdd.setMinimumSize(new Dimension(2, 2));
		buttonAdd.setPreferredSize(new Dimension(80, 25));
		buttonAdd.setMaximumSize(new Dimension(80, 20));
		add(buttonAdd);
		
		buttonDelete = new JButton("org.multipage.gui.textDeleteCounterIncrement");
		springLayout.putConstraint(SpringLayout.NORTH, buttonDelete, 0, SpringLayout.NORTH, buttonAdd);
		springLayout.putConstraint(SpringLayout.WEST, buttonDelete, 6, SpringLayout.EAST, buttonAdd);
		buttonDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRemove();
			}
		});
		buttonDelete.setPreferredSize(new Dimension(80, 25));
		buttonDelete.setMinimumSize(new Dimension(2, 2));
		buttonDelete.setMaximumSize(new Dimension(80, 20));
		buttonDelete.setMargin(new Insets(0, 0, 0, 0));
		add(buttonDelete);
	}

	/**
	 * Stop editing.
	 */
	protected void onStopEditing() {
		try {
			
			if (table.isEditing()) {
			    table.getCellEditor().stopCellEditing();
			}
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
			
			setFromInitialString();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Save dialog.
	 */
	@Override
	public void saveDialog() {
		
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			localize();
			initTable();
			loadDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Initialize table.
	 */
	private void initTable() {
		try {
			
			tableModel = new TableModel();
			table.setModel(tableModel);
			table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On add counter increment.
	 */
	public void onAdd() {
		try {
			
			// Check counter name.
			String counterName = textCounterName.getText();
			if (counterName.isEmpty()) {
				
				Utility.show(this, "org.multipage.gui.textCounterNameCannotBeEmpty");
				return;
			}
			
			// Check number.
			int number = 1;
			String numberText = textNumber.getText();
			if (!numberText.isEmpty()) {
				
				try {
					number = Integer.parseInt(numberText);
				}
				catch (Exception e) {
					Utility.show(this, "org.multipage.gui.textCounterNumberMustBeInteger");
					return;
				}
			}
			
			// Add table item.
			tableModel.addItem(new Counter(counterName, number));
			
			resetInput();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Reset input controls.
	 */
	private void resetInput() {
		try {
			
			textCounterName.setText("");
			textNumber.setText("");
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On remove.
	 */
	protected void onRemove() {
		try {
			
			int index = table.getSelectedRow();
			if (index == -1) {
				
				Utility.show(this, "org.multipage.gui.messageSelectSingleCounter");
				return;
			}
			
			if (!Utility.ask(this, "org.multipage.gui.messageDeleteCounter", (String) tableModel.getValueAt(index, 0))) {
				return;
			}
			
			tableModel.removeItem(index);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get font specification.
	 * @return
	 */
	@Override
	public String getSpecification() {
		
		try {
			LinkedList<Counter> countersIncrements = tableModel.getCounters();
			if (countersIncrements.isEmpty()) {
				return "none";
			}
			
			// List items.
			String output = "";
			boolean isFirst = true;
			
			for (Counter counterIncrement : countersIncrements) {
				
				if (!isFirst) {
					output += " ";
				}
				output += counterIncrement.name + " " + counterIncrement.number;
				
				isFirst = false;
			}
			
			return output;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Set from initial string.
	 */
	private void setFromInitialString() {
		try {
			
			// Initialize controls.
			tableModel.clearItems();
			resetInput();
	
			if (initialString != null) {
				
				Scanner scanner = new Scanner(initialString.trim());
				
				try {
					boolean isFirst = true;
					while (true) {
						
						String name = scanner.next().trim();
						if (name.isEmpty()) {
							break;
						}
						
						if (isFirst && name.equals("none")) {
							break;
						}
						
						int increment = 1;
						String incrementText = scanner.next().trim();
						
						if (!incrementText.isEmpty()) {
							try {
								increment = Integer.parseInt(incrementText);
							}
							catch (Exception e) {
							}
						}
						
						// Add item.
						tableModel.addItem(new Counter(name, increment));
						
						if (incrementText.isEmpty()) {
							break;
						}
						isFirst = false;
					}
				}
				catch (Exception e) {
					Safe.exception(e);
				}
				
			    scanner.close();
			}
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
			
			Utility.localize(labelCounters);
			Utility.localize(labelCounterName);
			Utility.localize(labelNumber);
			Utility.localize(buttonAdd);
			Utility.localize(buttonDelete);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/* (non-Javadoc)
	 * @see org.multipage.gui.InsertPanel#getWindowTitle()
	 */
	@Override
	public String getWindowTitle() {
		
		try {
			return Resources.getString("org.multipage.gui.textCssCounterBuilder");
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/* (non-Javadoc)
	 * @see org.multipage.gui.InsertPanel#getResultText()
	 */
	@Override
	public String getResultText() {
		
		try {
			return getSpecification();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/* (non-Javadoc)
	 * @see org.multipage.gui.InsertPanel#getContainerDialogBounds()
	 */
	@Override
	public Rectangle getContainerDialogBounds() {
		
		return bounds;
	}

	/* (non-Javadoc)
	 * @see org.multipage.gui.InsertPanel#setContainerDialogBounds(java.awt.Rectangle)
	 */
	@Override
	public void setContainerDialogBounds(Rectangle bounds) {
		
		CssCountersPanel.bounds = bounds;
	}
	
	/* (non-Javadoc)
	 * @see org.multipage.gui.InsertPanel#boundsSet()
	 */
	@Override
	public boolean isBoundsSet() {

		return boundsSet;
	}
	
	/* (non-Javadoc)
	 * @see org.multipage.gui.InsertPanel#setBoundsSet(boolean)
	 */
	@Override
	public void setBoundsSet(boolean set) {
		
		boundsSet = set;
	}

	/**
	 * Get component.
	 * @return
	 */
	@Override
	public Component getComponent() {
		
		return this;
	}

	/**
	 * Get string value.
	 * @return
	 */
	@Override
	public String getStringValue() {
		
		try {
			return getSpecification();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Set string value.
	 * @param string
	 */
	@Override
	public void setStringValue(String string) {
		try {
			
			initialString = string;
			setFromInitialString();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get value meaning.
	 * @return
	 */
	@Override
	public String getValueMeaning() {
		
		return meansCssCounter;
	}

	/**
	 * Set controls grayed. If a false value is returned a high level editor grayed this panel.
	 */
	@Override
	public boolean setControlsGrayed(boolean isDefault) {
		
		return false;
	}
}

/**
 * Counter increment.
 * @author
 *
 */
class Counter {

	/**
	 * Counter name.
	 */
	public String name;
	
	/**
	 * Number.
	 */
	public int number;
	
	/**
	 * Constructor.
	 * @param name
	 * @param number
	 */
	public Counter(String name, int number) {
		try {
			
			this.name = name;
			this.number = number;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}

/**
 * Column class.
 */
class Column {

	/**
	 * Items.
	 */
	String name;
	Class type;
	
	/**
	 * Constructor.
	 * @param nameId
	 * @param type
	 */
	public Column(String nameId, Class type) {
		try {
			
			this.name = Resources.getString(nameId);
			this.type = type;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}

/**
 * Table model.
 * @author
 *
 */
class TableModel extends AbstractTableModel {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Columns.
	 */
	private static final Column [] columns = {
		new Column("org.multipage.gui.textCounterNameColumn", String.class),
		new Column("org.multipage.gui.textCounterNumberColumn", Integer.class),
		};
	
	/**
	 * Items list.
	 */
	private LinkedList<Counter> counters = new LinkedList<Counter>();

	/**
	 * Add item.
	 * @param counter
	 */
	public void addItem(Counter counter) {
		try {
			
			counters.add(counter);
			fireTableDataChanged();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Clear items.
	 */
	public void clearItems() {
		try {
			
			counters.clear();
			fireTableDataChanged();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get counters.
	 * @return
	 */
	public LinkedList<Counter> getCounters() {
		
		return counters;
	}

	/**
	 * Remove item.
	 * @param index
	 */
	public void removeItem(int index) {
		try {
			
			if (index < 0 || index >= counters.size()) {
				return;
			}
			
			counters.remove(index);
			fireTableDataChanged();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get row count.
	 */
	@Override
	public int getRowCount() {
		
		try {
			return counters.size();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0;
	}

	/**
	 * Get column count.
	 */
	@Override
	public int getColumnCount() {
		
		return columns.length;
	}

	/**
	 * Get column class.
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		
		try {
			if (columnIndex < 0 || columnIndex >= columns.length) {
				return super.getColumnClass(columnIndex);
			}
			
			return columns[columnIndex].type;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Get column name.
	 */
	@Override
	public String getColumnName(int column) {
		
		try {
			if (column < 0 || column >= columns.length) {
				return super.getColumnName(column);
			}
			
			return columns[column].name;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get cell editable flag.
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		
		try {
			if (rowIndex < 0 || rowIndex >= counters.size() ||
					columnIndex < 0 || columnIndex >= columns.length) {
				
				return super.isCellEditable(rowIndex, columnIndex);
			}
			return true;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Get value.
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		try {
			if (rowIndex < 0 || rowIndex >= counters.size()) {
				
				return null;
			}
			
			Counter counter = counters.get(rowIndex);
			if (columnIndex == 0) {
				return counter.name;
			}
			else if (columnIndex == 1) {
				return counter.number;
				
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Set value.
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		try {
			
			if (rowIndex >= 0 && rowIndex < counters.size()) {
				
				Counter counter = counters.get(rowIndex);
				if (columnIndex == 0 && aValue instanceof String) {
					
					counter.name = (String) aValue;
				}
				else if (columnIndex == 1 && aValue instanceof Integer) {
					counter.number = (Integer) aValue;
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
