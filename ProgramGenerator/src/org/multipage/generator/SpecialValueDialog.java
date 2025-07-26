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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableModel;

import org.multipage.gui.Images;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.Utility;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Dialog for special slot values.
 * @author vakol
 *
 */
public class SpecialValueDialog extends JDialog {

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
	 * Table model.
	 */
	private DefaultTableModel tableModel;

	// $hide<<$
	/**
	 * Components.
	 */
	private JButton buttonCancel;
	private JButton buttonOk;
	private JLabel labelSelectSpecialValue;
	private JScrollPane scrollPane;
	private JTable table;

	/**
	 * Show dialog.
	 * @param parent
	 * @param oldValue 
	 * @return
	 */
	public static String showDialog(Component parent, String oldValue) {
		
		try {
			SpecialValueDialog dialog = new SpecialValueDialog(parent);
			dialog.selectValue(oldValue);
			dialog.setVisible(true);
			
			if (dialog.confirm) {
				
				return dialog.getSpecialValue();
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Get special value.
	 * @return
	 */
	private String getSpecialValue() {
		
		try {
			int selectedRowIndex = table.getSelectedRow();
			if (selectedRowIndex == -1) {
				return null;
			}
			
			return (String) tableModel.getValueAt(selectedRowIndex, 0);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Create the dialog.
	 * @param parent 
	 */
	public SpecialValueDialog(Component parent) {
		super(Utility.findWindow(parent), ModalityType.APPLICATION_MODAL);
		
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
		setTitle("org.multipage.generator.textSpecialValueDialog");
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
		
		labelSelectSpecialValue = new JLabel("org.multipage.generator.textSelectSpecialValue");
		springLayout.putConstraint(SpringLayout.NORTH, labelSelectSpecialValue, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, labelSelectSpecialValue, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(labelSelectSpecialValue);
		
		scrollPane = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, labelSelectSpecialValue);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -10, SpringLayout.NORTH, buttonOk);
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(scrollPane);
		
		table = new JTable();
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				onTableClick(e);
			}
		});
		table.setFillsViewportHeight(true);
		table.setShowVerticalLines(false);
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
			
			tableModel = new DefaultTableModel() {
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
				
			};
			table.setModel(tableModel);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			tableModel.addColumn(Resources.getString("org.multipage.generator.textSpecialValueColumn"));
			tableModel.addColumn(Resources.getString("org.multipage.generator.textSpecialValueDescriptionColumn"));
			
			final String prefix = "- ";
			tableModel.addRow(new String [] { "none", prefix + Resources.getString("org.multipage.generator.textCssSpecialValueNone") });
			tableModel.addRow(new String [] { "initial", prefix + Resources.getString("org.multipage.generator.textCssSpecialValueInitial") });
			tableModel.addRow(new String [] { "inherit", prefix + Resources.getString("org.multipage.generator.textCssSpecialValueInherit") });
			tableModel.addRow(new String [] { "auto", prefix + Resources.getString("org.multipage.generator.textCssSpecialValueAuto") });
			tableModel.addRow(new String [] { "unset", prefix + Resources.getString("org.multipage.generator.textCssSpecialValueUnset") });
	
			final int width = 100;
			table.getColumnModel().getColumn(0).setPreferredWidth(width);
			table.getColumnModel().getColumn(0).setMaxWidth(width);
			table.doLayout();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Select table value.
	 * @param oldValue
	 */
	private void selectValue(String oldValue) {
		try {
			
			int count = tableModel.getRowCount();
			for (int row = 0; row < count; row++) {
				
				Object value = tableModel.getValueAt(row, 0);
				if (!(value instanceof String)) {
					continue;
				}
				String textValue = (String) value;
				if (textValue.equals(oldValue)) {
					table.getSelectionModel().setSelectionInterval(row, row);
					break;
				}
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
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
			Utility.localize(labelSelectSpecialValue);
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
			
			if (getSpecialValue() == null) {
				Utility.show(this, "org.multipage.generator.messageSelectSingleSpecialValue");
				return;
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

	/**
	 * Table click.
	 * @param e 
	 */
	protected void onTableClick(MouseEvent e) {
		try {
			
			if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
				onOk();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
