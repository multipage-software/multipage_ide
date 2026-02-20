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
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.maclan.Area;
import org.maclan.AreasModel;
import org.maclan.VersionObj;
import org.multipage.gui.Images;
import org.multipage.gui.TextFieldEx;
import org.multipage.gui.Utility;
import org.multipage.util.Resources;
import org.multipage.util.Safe;
import java.awt.Color;

/**
 * Dialog that displays inherited folders for an area.
 * @author vakol
 *
 */
public class AreaInheritedFoldersDialog extends JDialog {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Area reference.
	 */
	private Area area;

	/**
	 * Table model.
	 */
	private DefaultTableModel tableModel;

	// $hide<<$
	/**
	 * Components.
	 */
	private JPanel panel;
	private JButton buttonClose;
	private JPanel panelTop;
	private JLabel labelArea;
	private JTextField textAreaDescription;
	private JScrollPane scrollPane;
	private JTable table;
	private JPopupMenu popupMenu;
	private JMenuItem menuCopyFolderName;

	/**
	 * Show dialog.
	 * @param parent
	 * @param area 
	 * @param resource
	 */
	public static void showDialog(Component parent, Area area) {
		try {
			
			AreaInheritedFoldersDialog dialog = new AreaInheritedFoldersDialog(Utility.findWindow(parent));
			dialog.area = area;
			
			dialog.loadLoadDialogContent();
			
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
	public AreaInheritedFoldersDialog(Window parentWindow) {
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
		setTitle("org.multipage.generator.textAreaInheritedFoldersDialog");
		
		setBounds(100, 100, 474, 300);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		panel = new JPanel();
		springLayout.putConstraint(SpringLayout.WEST, panel, 0, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, panel, 0, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, panel, 0, SpringLayout.EAST, getContentPane());
		panel.setPreferredSize(new Dimension(10, 45));
		getContentPane().add(panel);
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
		
		panelTop = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, panelTop, 0, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, panelTop, 0, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, panelTop, 0, SpringLayout.EAST, getContentPane());
		panelTop.setPreferredSize(new Dimension(10, 30));
		getContentPane().add(panelTop);
		SpringLayout sl_panelTop = new SpringLayout();
		panelTop.setLayout(sl_panelTop);
		
		labelArea = new JLabel("org.multipage.generator.textInheritedFoldersArea");
		sl_panelTop.putConstraint(SpringLayout.NORTH, labelArea, 9, SpringLayout.NORTH, panelTop);
		sl_panelTop.putConstraint(SpringLayout.WEST, labelArea, 6, SpringLayout.WEST, panelTop);
		panelTop.add(labelArea);
		
		textAreaDescription = new TextFieldEx();
		textAreaDescription.setEditable(false);
		sl_panelTop.putConstraint(SpringLayout.NORTH, textAreaDescription, 6, SpringLayout.NORTH, panelTop);
		sl_panelTop.putConstraint(SpringLayout.WEST, textAreaDescription, 6, SpringLayout.EAST, labelArea);
		sl_panelTop.putConstraint(SpringLayout.EAST, textAreaDescription, -10, SpringLayout.EAST, panelTop);
		panelTop.add(textAreaDescription);
		textAreaDescription.setColumns(10);
		
		scrollPane = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 30, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, 0, SpringLayout.NORTH, panel);
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, panelTop);
		getContentPane().add(scrollPane);
		
		table = new JTable();
		table.setGridColor(Color.LIGHT_GRAY);
		table.setShowVerticalLines(false);
		table.setFillsViewportHeight(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setFont(new Font("Tahoma", Font.PLAIN, 14));
		scrollPane.setViewportView(table);
		
		popupMenu = new JPopupMenu();
		addPopup(table, popupMenu);
		
		menuCopyFolderName = new JMenuItem("org.multipage.generator.menuCopyInheritedFolderName");
		menuCopyFolderName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onCopyFolderName();
			}
		});
		popupMenu.add(menuCopyFolderName);
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			
			Utility.centerOnScreen(this);
			
			localize();
			setIcons();
			
			initializeTable();
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
			Utility.localize(labelArea);
			Utility.localize(menuCopyFolderName);
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
			
			buttonClose.setIcon(Images.getIcon("org/multipage/gui/images/cancel_icon.png"));
			menuCopyFolderName.setIcon(Images.getIcon("org/multipage/gui/images/copy_icon.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On close.
	 */
	protected void onClose() {
		
		dispose();
	}

	/**
	 * Load dialog content.
	 */
	private void loadLoadDialogContent() {
		try {
			
			// Set text field.
			textAreaDescription.setText(area.getDescriptionForGui());
			
			// Load table.
			AreasModel areasModel = ProgramGenerator.getAreasModel();
			
			for (VersionObj version : areasModel.getVersions()) {
				
				// Load inherited folder name.
				String inheritedFolder = area.getInheritedFolder(version.getId());
				
				// Add table row.
				tableModel.addRow(new String [] { version.getDescription(), inheritedFolder });
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Initialize table.
	 */
	@SuppressWarnings("serial")
	private void initializeTable() {
		try {
			
			// Create and set model (not editable).
			tableModel = new DefaultTableModel() {
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			table.setModel(tableModel);
			
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			
			// Add columns.
			tableModel.addColumn(Resources.getString("org.multipage.generator.textVersionDescriptionColumn"));
			tableModel.addColumn(Resources.getString("org.multipage.generator.textInheritedFolderColumn"));
		
			TableColumn column = table.getColumnModel().getColumn(0);
			column.setPreferredWidth(80);
			column = table.getColumnModel().getColumn(1);
			column.setPreferredWidth(355);
			
			// Set row height and margin.
			table.setRowHeight(30);
			table.setRowMargin(10);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Add popum trayMenu.
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
	 * On copy folder name.
	 */
	protected void onCopyFolderName() {
		try {
			
			// Get selected item.
			int selectedRow = table.getSelectedRow();
			if (selectedRow == -1) {
				
				Utility.show(this, "org.multipage.generator.messageSelectInhertitedFoldersTableRow");
				return;
			}
			
			// Get folder name and add it to the clipboard.
			String folderName = (String) tableModel.getValueAt(selectedRow, 1);
			
			StringSelection selection = new StringSelection(folderName);
		    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		    clipboard.setContents(selection, selection);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}