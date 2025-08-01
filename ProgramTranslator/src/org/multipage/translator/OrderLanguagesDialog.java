/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.translator;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.maclan.Language;
import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.multipage.basic.ProgramBasic;
import org.multipage.gui.Images;
import org.multipage.gui.RendererJLabel;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.Utility;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Dialog that enables to change language order.
 * @author vakol
 *
 */
public class OrderLanguagesDialog extends JDialog {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Dialog state.
	 */
	private static Rectangle bounds;

	/**
	 * Set default data.
	 */
	public static void setDefaultData() {
		
		bounds = new Rectangle();
	}

	/**
	 * Load data.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void serializeData(StateInputStream inputStream) 
			throws IOException, ClassNotFoundException {
		
		bounds = Utility.readInputStreamObject(inputStream, Rectangle.class);
	}

	/**
	 * Save data.
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream) 
			throws IOException {
		
		outputStream.writeObject(bounds);
	}
	
	/**
	 * Table model.
	 */
	private LocalTableModel model;
	
	// $hide<<$
	/**
	 * Components.
	 */
	private JPanel panel;
	private JButton buttonClose;
	private JPanel panelMain;
	private JLabel labelOrder;
	private JScrollPane scrollPane;
	private JButton buttonDefault;
	private JButton buttonDown;
	private JButton buttonUp;
	private JTable table;

	/**
	 * Show dialog.
	 * @param parent
	 * @param resource
	 */
	public static void showDialog(Component parent) {
		try {
			
			OrderLanguagesDialog dialog = new OrderLanguagesDialog(Utility.findWindow(parent));
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
	public OrderLanguagesDialog(Window parentWindow) {
		super(parentWindow, ModalityType.DOCUMENT_MODAL);
		
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
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				onClose();
			}
		});
		setTitle("org.multipage.translator.textOrderLanguagesDialog");
		
		setBounds(100, 100, 506, 300);
		
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(10, 50));
		getContentPane().add(panel, BorderLayout.SOUTH);
		SpringLayout sl_panel = new SpringLayout();
		panel.setLayout(sl_panel);
		
		buttonClose = new JButton("textClose");
		buttonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClose();
			}
		});
		buttonClose.setMargin(new Insets(0, 0, 0, 0));
		sl_panel.putConstraint(SpringLayout.SOUTH, buttonClose, -10, SpringLayout.SOUTH, panel);
		sl_panel.putConstraint(SpringLayout.EAST, buttonClose, -10, SpringLayout.EAST, panel);
		buttonClose.setPreferredSize(new Dimension(80, 25));
		panel.add(buttonClose);
		
		panelMain = new JPanel();
		getContentPane().add(panelMain, BorderLayout.CENTER);
		SpringLayout sl_panelMain = new SpringLayout();
		panelMain.setLayout(sl_panelMain);
		
		labelOrder = new JLabel("org.multipage.translator.textLanguagesOrderTable");
		sl_panelMain.putConstraint(SpringLayout.NORTH, labelOrder, 6, SpringLayout.NORTH, panelMain);
		sl_panelMain.putConstraint(SpringLayout.WEST, labelOrder, 6, SpringLayout.WEST, panelMain);
		panelMain.add(labelOrder);
		
		scrollPane = new JScrollPane();
		sl_panelMain.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, labelOrder);
		sl_panelMain.putConstraint(SpringLayout.WEST, scrollPane, 6, SpringLayout.WEST, panelMain);
		sl_panelMain.putConstraint(SpringLayout.SOUTH, scrollPane, 0, SpringLayout.SOUTH, panelMain);
		panelMain.add(scrollPane);
		
		buttonDefault = new JButton("");
		buttonDefault.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onReset();
			}
		});
		sl_panelMain.putConstraint(SpringLayout.EAST, scrollPane, -6, SpringLayout.WEST, buttonDefault);
		buttonDefault.setPreferredSize(new Dimension(30, 30));
		panelMain.add(buttonDefault);
		
		buttonDown = new JButton("");
		buttonDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onDown();
			}
		});
		sl_panelMain.putConstraint(SpringLayout.EAST, buttonDown, -10, SpringLayout.EAST, panelMain);
		sl_panelMain.putConstraint(SpringLayout.NORTH, buttonDefault, 6, SpringLayout.SOUTH, buttonDown);
		sl_panelMain.putConstraint(SpringLayout.WEST, buttonDefault, 0, SpringLayout.WEST, buttonDown);
		buttonDown.setPreferredSize(new Dimension(30, 30));
		panelMain.add(buttonDown);
		
		buttonUp = new JButton("");
		buttonUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onUp();
			}
		});
		sl_panelMain.putConstraint(SpringLayout.NORTH, buttonDown, 6, SpringLayout.SOUTH, buttonUp);
		sl_panelMain.putConstraint(SpringLayout.NORTH, buttonUp, 0, SpringLayout.NORTH, scrollPane);
		
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(table);
		sl_panelMain.putConstraint(SpringLayout.EAST, buttonUp, -10, SpringLayout.EAST, panelMain);
		buttonUp.setPreferredSize(new Dimension(30, 30));
		panelMain.add(buttonUp);
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			Utility.centerOnScreen(this);
			
			localize();
			setIcons();
			setToolTips();
			
			createTable();
			loadTable();
			
			loadDialog();
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
	 * Localize components.
	 */
	private void localize() {
		try {
			
			Utility.localize(this);
			Utility.localize(buttonClose);
			Utility.localize(labelOrder);
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
			
			buttonClose.setIcon(Images.getIcon("org/multipage/translator/images/cancel_icon.png"));
			buttonDefault.setIcon(Images.getIcon("org/multipage/translator/images/reset_order.png"));
			buttonUp.setIcon(Images.getIcon("org/multipage/translator/images/up.png"));
			buttonDown.setIcon(Images.getIcon("org/multipage/translator/images/down.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set tool tips.
	 */
	protected void setToolTips() {
		try {
			
			buttonDefault.setToolTipText(Resources.getString("org.multipage.translator.tooltipSetDefaultLanguagesOrder"));
			buttonUp.setToolTipText(Resources.getString("org.multipage.translator.tooltipShiftLanguageUp"));
			buttonDown.setToolTipText(Resources.getString("org.multipage.translator.tooltipShiftLanguageDown"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On close dialog.
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
	 * Create table.
	 */
	private void createTable() {
		try {
			
			// Create table model and assign it to the table.
			model = new LocalTableModel();
			table.setModel(model);
			table.getTableHeader().setReorderingAllowed(false);
			
			// Set flag renderer.
			table.setDefaultRenderer(BufferedImage.class, new TableCellRenderer() {
				
				// Create flag renderer.
				RendererJLabel renderer;
				{
					try {
						
						renderer = new RendererJLabel();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				
				// Return flag renderer.
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value,
						boolean isSelected, boolean hasFocus, int row, int column) {
					
					try {
						// Set image. 
						if (value instanceof BufferedImage) {
							BufferedImage flag = (BufferedImage) value;
							renderer.setIcon(new ImageIcon(flag));
						}
						else {
							renderer.setIcon(null);
						}
						
						// Set properties.
						renderer.set(isSelected, hasFocus, row);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return renderer;
				}
			});
			
			// Set text renderer.
			table.setDefaultRenderer(Object.class, new TableCellRenderer() {
				
				// Create flag renderer.
				RendererJLabel renderer;
				{
					try {
						
						renderer = new RendererJLabel();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				
				// Return flag renderer.
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value,
						boolean isSelected, boolean hasFocus, int row, int column) {
					
					try {
						// Set text. 
						renderer.setText(value.toString());
						// Set properties.
						renderer.set(isSelected, hasFocus, row);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return renderer;
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Load table.
	 */
	private void loadTable() {
		try {
			
			// Reset model content.
			model.clear();
			
			// Load languages.		
			MiddleResult result;
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			
			// Login to the database.
			result = middle.login(login);
			if (result.isOK()) {
				
				result = middle.loadLanguages(model.getLanguages());
	
				// Logout from the database.
				MiddleResult logoutResult = middle.logout(result);
				if (result.isOK()) {
					result = logoutResult;
				}
			}
			
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			// Update table.
			model.fireTableDataChanged();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On move language up.
	 */
	protected void onUp() {
		try {
			
			// Get selected language.
			int selectedRow = table.getSelectedRow();
			if (selectedRow == -1) {
				Utility.show(this, "org.multipage.translator.textSelectLanguageToMove");
				return;
			}
			
			// Get previous row.
			int previousRow = selectedRow - 1;
			if (previousRow < 0) {
				return;
			}
			
			// Swap priorities.
			model.swap(selectedRow, previousRow);
			
			// Save language priorities and load new table.
			savePriorities();
			loadTable();
			
			table.setRowSelectionInterval(previousRow, previousRow);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On move language down.
	 */
	protected void onDown() {
		try {
			
			// Get selected language.
			int selectedRow = table.getSelectedRow();
			if (selectedRow == -1) {
				Utility.show(this, "org.multipage.translator.textSelectLanguageToMove");
				return;
			}
	
			// Get next row.
			int nextRow = selectedRow + 1;
			if (nextRow >= table.getModel().getRowCount()) {
				return;
			}
			
			// Swap priorities.
			model.swap(selectedRow, nextRow);
			
			// Save language priorities and load new table.
			savePriorities();
			loadTable();
			
			table.setRowSelectionInterval(nextRow, nextRow);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Save language priorities.
	 */
	private void savePriorities() {
		try {
			
			// Get list of languages.
			LinkedList<Language> languages = model.getLanguages();
			
			// Save priorities.
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			
			MiddleResult result = middle.updateLanguagePriorities(login, languages);
			if (result.isNotOK()) {
				result.show(this);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On reset.
	 */
	protected void onReset() {
		try {
			
			int selectedRow = table.getSelectedRow();
	
			// Reset language priorities.
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			
			MiddleResult result = middle.updateLanguagePrioritiesReset(login);
			if (result.isNotOK()) {
				result.show(this);
			}
			
			// Load new table.
			loadTable();
			
			if (selectedRow != -1) {
				table.setRowSelectionInterval(selectedRow, selectedRow);
			}
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
class LocalTableModel extends AbstractTableModel {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Columns definition.
	 */
	private String [] columns = {
			"org.multipage.translator.textIdentifier2",
			"org.multipage.translator.textFlag",
			"org.multipage.translator.textLanguageDescription",
			"org.multipage.translator.textLanguageAlias"};
	
	/**
	 * Languages list.
	 */
	private LinkedList<Language> languages = new LinkedList<Language>();
	
	/**
	 * Get column count.
	 */
	@Override
	public int getColumnCount() {
		
		try {
			return columns.length;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0;
	}

	/**
	 * Swap rows.
	 * @param row1
	 * @param row2
	 */
	public void swap(int row1, int row2) {
		try {
			
			// Check input values.
			int count = languages.size();
			
			if (row1 < 0 || row2 < 0 || row1 >= count || row2 >= count) {
				return;
			}
			
			// Swap languages.
			Language language1 = languages.get(row1);
			Language language2 = languages.get(row2);
			
			languages.set(row1, language2);
			languages.set(row2, language1);
			
			// Update table.
			fireTableDataChanged();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get column name.
	 */
	@Override
	public String getColumnName(int columnIndex) {
		
		try {
			if (columnIndex < 0 || columnIndex >= columns.length) {
				return "*error*";
			}
			
			return Resources.getString(columns[columnIndex]);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "*error*";
	}

	/**
	 * Get languages list.
	 * @return
	 */
	public LinkedList<Language> getLanguages() {
		
		return languages;
	}

	/**
	 * Clear data.
	 */
	public void clear() {
		try {
			
			languages.clear();
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
			return languages.size();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0;
	}

	/**
	 * Get value of given cell.
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		try {
			if (rowIndex >= languages.size()) {
				return "*error*";
			}
			
			// Get language.
			Language language = languages.get(rowIndex);
			
			switch (columnIndex) {
				case 0:
					return language.id;
				case 1:
					return language.image;
				case 2:
					return language.description;
				case 3:
					return language.alias;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "*error*";
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		
		try {
			if (columnIndex == 1) {
				return BufferedImage.class;
			}
			return super.getColumnClass(columnIndex);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
}