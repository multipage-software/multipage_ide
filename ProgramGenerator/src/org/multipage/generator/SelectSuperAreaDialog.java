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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableModel;

import org.maclan.Area;
import org.maclan.AreaRelation;
import org.multipage.gui.Images;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.Utility;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Dialog that displays selection of the super area.
 * @author vakol
 *
 */
public class SelectSuperAreaDialog extends JDialog {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Dialog serialized states.
	 */
	private static Rectangle bounds = new Rectangle();

	/**
	 * Set default states.
	 */
	public static void setDefaultData() {
		
		bounds = new Rectangle();
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
	 * Load serialized states.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void serializeData(StateInputStream inputStream)
			throws IOException, ClassNotFoundException {
		
		bounds = Utility.readInputStreamObject(inputStream, Rectangle.class);
	}
	
	/**
	 * Confirm flag.
	 */
	private boolean confirm = false;
	
	/**
	 * Area reference.
	 */
	private Area area;

	/**
	 * Table model.
	 */
	private DefaultTableModel tableModel;

	/**
	 * Selected super area.
	 */
	private Area superArea;

	// $hide<<$
	/**
	 * Components.
	 */
	private JButton buttonCancel;
	private JButton buttonOk;
	private JLabel labelSelect;
	private JScrollPane scrollPane;
	private JTable table;

	/**
	 * Show dialog.
	 * @param parent
	 * @param area 
	 * @return
	 */
	public static Area showDialog(Component parent, Area area) {
		
		try {
			SelectSuperAreaDialog dialog = new SelectSuperAreaDialog(parent, area);
			dialog.setVisible(true);
			
			if (dialog.confirm) {
				
				return dialog.superArea;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Create the dialog.
	 * @param parent 
	 * @param area 
	 */
	public SelectSuperAreaDialog(Component parent, Area area) {
		super(Utility.findWindow(parent), ModalityType.APPLICATION_MODAL);

		try {
			initComponents();
			// $hide>>$
			this.area = area;
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
		setTitle("org.multipage.generator.textSelectSuperArea");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});
		setBounds(100, 100, 450, 300);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		buttonCancel = new JButton("textCancel");
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		buttonCancel.setMargin(new Insets(0, 0, 0, 0));
		buttonCancel.setPreferredSize(new Dimension(80, 25));
		springLayout.putConstraint(SpringLayout.SOUTH, buttonCancel, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, buttonCancel, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(buttonCancel);
		
		buttonOk = new JButton("textOk");
		buttonOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOk();
			}
		});
		buttonOk.setMargin(new Insets(0, 0, 0, 0));
		buttonOk.setPreferredSize(new Dimension(80, 25));
		springLayout.putConstraint(SpringLayout.SOUTH, buttonOk, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, buttonOk, -6, SpringLayout.WEST, buttonCancel);
		getContentPane().add(buttonOk);
		
		labelSelect = new JLabel("org.multipage.generator.textLabelSelectSuperArea");
		springLayout.putConstraint(SpringLayout.NORTH, labelSelect, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, labelSelect, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(labelSelect);
		
		scrollPane = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 5, SpringLayout.SOUTH, labelSelect);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, 194, SpringLayout.SOUTH, labelSelect);
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(scrollPane);
		
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(table);
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			
			localize();
			setIcons();
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
	@SuppressWarnings("serial")
	private void initTable() {
		try {
			
			// Create model.
			tableModel = new DefaultTableModel() {
	
				@Override
				public boolean isCellEditable(int row, int column) {

					return false;
				}
	
				@Override
				public Class<?> getColumnClass(int columnIndex) {
					
					try {
						if (columnIndex == 2 || columnIndex == 3) {
							return Boolean.class;
						}
						
						return super.getColumnClass(columnIndex);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return null;
				}
				
			};
			table.setModel(tableModel);
			
			// Add columns.
			tableModel.addColumn(Resources.getString("org.multipage.generator.textColumnID"));
			tableModel.addColumn(Resources.getString("org.multipage.generator.textColumnName"));
			tableModel.addColumn(Resources.getString("org.multipage.generator.textColumnInherits"));
			tableModel.addColumn(Resources.getString("org.multipage.generator.textColumnHides"));
			tableModel.addColumn(Resources.getString("org.multipage.generator.textColumnNameSub"));
			tableModel.addColumn(Resources.getString("org.multipage.generator.textColumnNameSuper"));
					
			long areaId = area.getId();
			
			// Add rows.
			for (Area superArea : area.getSuperareas()) {
				
				AreaRelation relation = superArea.getSubRelation(areaId);
				
				tableModel.addRow(new Object [] {
						superArea.getId(),
						superArea.getDescriptionForced(false),
						relation.isInheritance(),
						relation.isHideSub(),
						relation.getRelationNameSub(),
						relation.getRelationNameSuper()
						});
			}
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
			
			buttonOk.setIcon(Images.getIcon("org/multipage/gui/images/ok_icon.png"));
			buttonCancel.setIcon(Images.getIcon("org/multipage/gui/images/cancel_icon.png"));
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
			
			Utility.localize(this);
			Utility.localize(buttonOk);
			Utility.localize(buttonCancel);
			Utility.localize(labelSelect);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On cancel.
	 */
	protected void onCancel() {
		try {
			
			saveDialog();
			confirm = false;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
		dispose();
	}

	/**
	 * On OK.
	 */
	protected void onOk() {
		try {
			
			// Get selected row.
			int selectedIndex = table.getSelectedRow();
			if (selectedIndex == -1) {
				
				Utility.show(this, "org.multipage.generator.messageSelectSuperArea");
				return;
			}
			
			// Set area.
			Long superAreaId = (Long) tableModel.getValueAt(selectedIndex, 0);
			if (superAreaId != null) {
				
				superArea = area.getSuperarea(superAreaId);
			}
			
			saveDialog();
			confirm = true;
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
			}
			else {
				setBounds(bounds);
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
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
