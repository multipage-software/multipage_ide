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
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.maclan.Area;
import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.multipage.basic.ProgramBasic;
import org.multipage.gui.GraphUtility;
import org.multipage.gui.Images;
import org.multipage.gui.Utility;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Dialog that displays file names.
 * @author vakol
 *
 */
public class FileNamesEditor extends JDialog {
	
	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Confirm flag.
	 */
	private boolean confirm = false;

	/**
	 * Areas list.
	 */
	private LinkedList<Area> areas;
	
	/**
	 * Table model.
	 */
	private DefaultTableModel model;
	
	// $hide<<$
	/**
	 * Dialog components.
	 */
	private JButton buttonClose;
	private JLabel labelAreaFileNamesList;
	private JScrollPane scrollPane;
	private JTable table;
	
	/**
	 * Show dialog.
	 * @param parentComponent 
	 * @param parent
	 * @return
	 */
	public static boolean showDialog(Component parentComponent, LinkedList<Area> areas) {
		
		try {
			FileNamesEditor dialog = new FileNamesEditor(Utility.findWindow(parentComponent), areas);
			dialog.setVisible(true);
	
			return dialog.confirm;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Create the dialog.
	 * @param parentWindow 
	 * @param areas 
	 */
	public FileNamesEditor(Window parentWindow, LinkedList<Area> areas) {
		super(parentWindow, ModalityType.DOCUMENT_MODAL);
		
		try {
			initComponents();
			postCreate(areas); // $hide$
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
			public void windowClosing(WindowEvent e) {
				onClose();
			}
		});
		setTitle("org.multipage.generator.textAreaFileNamesEditor");
		
		setBounds(100, 100, 540, 413);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		buttonClose = new JButton("textClose");
		buttonClose.setMargin(new Insets(0, 0, 0, 0));
		buttonClose.setPreferredSize(new Dimension(80, 25));
		buttonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClose();
			}
		});
		springLayout.putConstraint(SpringLayout.SOUTH, buttonClose, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, buttonClose, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(buttonClose);
		
		labelAreaFileNamesList = new JLabel("org.multipage.generator.textAreaFileNames");
		springLayout.putConstraint(SpringLayout.NORTH, labelAreaFileNamesList, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, labelAreaFileNamesList, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(labelAreaFileNamesList);
		
		scrollPane = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, labelAreaFileNamesList);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -6, SpringLayout.NORTH, buttonClose);
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, 0, SpringLayout.EAST, buttonClose);
		getContentPane().add(scrollPane);
		
		table = new JTable();
		scrollPane.setViewportView(table);
	}

	/**
	 * Post creation.
	 * @param areas 
	 */
	private void postCreate(LinkedList<Area> areas) {
		try {
			
			setAreas(areas);
			setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			Utility.centerOnScreen(this);
			localize();
			setIcons();
			createTable();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set areas.
	 * @param areas
	 */
	private void setAreas(LinkedList<Area> areas) {
		try {
			
			this.areas = new LinkedList<Area>();
			
			for (Area area : areas) {
				if (area.isVisible() && !area.isConstructorArea()) {
					
					this.areas.add(area);
				}
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
			
			Utility.localize(this);
			Utility.localize(buttonClose);
			Utility.localize(labelAreaFileNamesList);
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
			
			setIconImage(Images.getImage("org/multipage/generator/images/main_icon.png"));
			buttonClose.setIcon(Images.getIcon("org/multipage/generator/images/cancel_icon.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On close.
	 */
	protected void onClose() {
		try {
			
			if (table.isEditing()) {
				return;
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
		dispose();
	}

	/**
	 * Create table.
	 */
	@SuppressWarnings("serial")
	private void createTable() {
		try {
			
			final Component thisComponent = this;
	
			model = new DefaultTableModel() {
				@Override
				public int getColumnCount() {
					return 2;
				}
				@Override
				public String getColumnName(int column) {
					try {
						switch (column) {
						case 0:
							return Resources.getString("org.multipage.generator.textAreaNameColumn");
						case 1:
							return Resources.getString("org.multipage.generator.textAreaFileNameColumn");
						}
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return "Error";
				}
				@Override
				public boolean isCellEditable(int row, int column) {
					return column == 1;
				}
				@Override
				public int getRowCount() {
					try {
						return areas.size();
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return 0;
				}
				@Override
				public Object getValueAt(int row, int column) {
					try {
						Area area = areas.get(row);
						switch (column) {
						case 0:
							return area.getDescriptionForced();
						case 1:
							return area.getFileName();
						}
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return "Error";
				}
				@Override
				public void setValueAt(Object aValue, int row, int column) {
					try {
						
						Area area = areas.get(row);
						
						if (area instanceof Area && column == 1 && aValue instanceof String) {
							
							String fileName = (String) aValue;
							fileName = fileName.trim();
							
							MiddleResult result;
							Middle middle = ProgramBasic.getMiddle();
							Properties login = ProgramBasic.getLoginProperties();
							
							// Update file name.
							result = middle.updateAreaFileName(login, area.getId(), fileName);
							if (result.isNotOK()) {
								result.show(thisComponent);
								return;
							}
							
							area.setFileName(fileName);
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			};
			
			// Create renderer.
			class JRenderer extends JLabel {
				
				boolean isSelected;
				boolean hasFocus;
				
				JRenderer() {
					try {
						
						setFont(new Font("Arial", Font.PLAIN, 12));
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
	
				public void setLabel(String description, boolean isSelected,
						boolean hasFocus, boolean isIcon) {
					try {
						
						setText(description);
						setIcon(isIcon ? Images.getIcon("org/multipage/generator/images/area_node.png") : null);
						this.isSelected = isSelected;
						this.hasFocus = hasFocus;
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
	
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
			
			final JRenderer label = new JRenderer();
					
			// Set area name cell renderer.
			DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
				@Override
				public Component getTableCellRendererComponent(JTable table,
						Object value, boolean isSelected, boolean hasFocus,
						int row, int column) {
					
					try {
						if (value == null) {
							label.setLabel("", isSelected, hasFocus, column == 0);
							return label;
						}
						
						String areaDescription = value.toString();
						label.setLabel(areaDescription, isSelected, hasFocus, column == 0);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return label;
				}
			};
			
			table.setModel(model);
			table.setDefaultRenderer(Object.class, renderer);
			table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
