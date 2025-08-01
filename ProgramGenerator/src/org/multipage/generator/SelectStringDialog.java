/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.multipage.gui.GraphUtility;
import org.multipage.gui.Images;
import org.multipage.gui.Utility;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Dialog that displays string selection.
 * @author vakol
 *
 */
public class SelectStringDialog extends JDialog {

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
	 * List model.
	 */
	private DefaultListModel listModel;

	/**
	 * String list.
	 */
	private LinkedList<String> stringItems;

	
	// $hide<<$
	/**
	 * Components.
	 */
	private JLabel labelMessage;
	private JScrollPane scrollPane;
	private JList listStrings;
	private JLabel labelFilter;
	private JTextField textFilter;
	private JButton buttonCancel;
	private JButton buttonOK;
	private JLabel labelInfo;

	/**
	 * Show dialog.
	 * @param parentComponent
	 * @param stringItems
	 * @param iconFileName
	 * @param titleId 
	 * @param message 
	 * @return
	 */
	public static String showDialog(Component parentComponent,
			LinkedList<String> stringItems, String iconFileName, String titleId, String message) {
		
		try {
			SelectStringDialog dialog = new SelectStringDialog(Utility.findWindow(parentComponent),
					iconFileName);
			dialog.loadDialog(stringItems, titleId, message);
			
			dialog.setVisible(true);
			
			if (dialog.confirm) {
				return dialog.getSelectedString();
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Create the dialog.
	 * @param parentWindow 
	 * @param iconFileName 
	 */
	public SelectStringDialog(Window parentWindow, String iconFileName) {
		super(parentWindow, ModalityType.DOCUMENT_MODAL);
		
		try {
			initComponents();
			postCreate(iconFileName); // $hide$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Initialize components.
	 */
	private void initComponents() {
		setMinimumSize(new Dimension(300, 270));
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});
		setBounds(100, 100, 468, 427);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		labelMessage = new JLabel("New label");
		springLayout.putConstraint(SpringLayout.NORTH, labelMessage, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, labelMessage, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(labelMessage);
		
		scrollPane = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, labelMessage);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, labelMessage);
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(scrollPane);
		
		listStrings = new JList();
		listStrings.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				onListClick(e);
			}
		});
		listStrings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(listStrings);
		
		labelFilter = new JLabel("org.multipage.generator.textFilter");
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -6, SpringLayout.NORTH, labelFilter);
		springLayout.putConstraint(SpringLayout.WEST, labelFilter, 0, SpringLayout.WEST, labelMessage);
		getContentPane().add(labelFilter);
		
		textFilter = new JTextField();
		springLayout.putConstraint(SpringLayout.WEST, textFilter, 6, SpringLayout.EAST, labelFilter);
		springLayout.putConstraint(SpringLayout.NORTH, labelFilter, 0, SpringLayout.NORTH, textFilter);
		springLayout.putConstraint(SpringLayout.EAST, textFilter, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(textFilter);
		textFilter.setColumns(10);
		
		buttonCancel = new JButton("textCancel");
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		buttonCancel.setPreferredSize(new Dimension(80, 25));
		buttonCancel.setMargin(new Insets(0, 0, 0, 0));
		springLayout.putConstraint(SpringLayout.SOUTH, buttonCancel, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, buttonCancel, 0, SpringLayout.EAST, scrollPane);
		getContentPane().add(buttonCancel);
		
		buttonOK = new JButton("textOk");
		buttonOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOk();
			}
		});
		springLayout.putConstraint(SpringLayout.SOUTH, buttonOK, 0, SpringLayout.SOUTH, buttonCancel);
		springLayout.putConstraint(SpringLayout.EAST, buttonOK, -6, SpringLayout.WEST, buttonCancel);
		buttonOK.setPreferredSize(new Dimension(80, 25));
		buttonOK.setMargin(new Insets(0, 0, 0, 0));
		getContentPane().add(buttonOK);
		
		labelInfo = new JLabel("org.multipage.generator.textFilterInfo");
		springLayout.putConstraint(SpringLayout.WEST, labelInfo, 0, SpringLayout.WEST, textFilter);
		springLayout.putConstraint(SpringLayout.SOUTH, textFilter, -2, SpringLayout.NORTH, labelInfo);
		springLayout.putConstraint(SpringLayout.SOUTH, labelInfo, -10, SpringLayout.NORTH, buttonOK);
		getContentPane().add(labelInfo);
	}
	
	/**
	 * On list click.
	 * @param e 
	 */
	protected void onListClick(MouseEvent e) {
		try {
			
			if (e.getButton() == MouseEvent.BUTTON1
					&& e.getClickCount() == 2) {
				
				Safe.invokeLater(() -> {
					onOk();
				});
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On OK.
	 */
	protected void onOk() {
		try {
			
			if (getSelectedString() == null) {
				Utility.show(this, "org.multipage.generator.textSelectSingleItem");
				return;
			}
			
			confirm = true;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
		dispose();
	}

	/**
	 * On cancel.
	 */
	protected void onCancel() {
		
		confirm = false;
		
		dispose();
	}

	/**
	 * Post creation.
	 * @param iconFileName 
	 */
	private void postCreate(String iconFileName) {
		try {
			
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			Utility.centerOnScreen(this);
			
			createList(iconFileName);
			localize();
			createFilter();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create filter.
	 */
	private void createFilter() {
		try {
			
			textFilter.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void removeUpdate(DocumentEvent e) {
					try {
						
						// Load list.
						loadList();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				@Override
				public void insertUpdate(DocumentEvent e) {
					try {
											
						// Load list.
						loadList();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				@Override
				public void changedUpdate(DocumentEvent e) {
					try {
						
						// Load list.
						loadList();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			
			Safe.invokeLater(() -> {
				textFilter.setText("*");;
			});
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
			
			Utility.localize(labelFilter);
			Utility.localize(buttonCancel);
			Utility.localize(buttonOK);
			Utility.localize(labelMessage);
			Utility.localize(labelInfo);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create list.
	 * @param iconFileName
	 */
	@SuppressWarnings("unchecked")
	private void createList(final String iconFileName) {
		try {
			
			// Set list item renderer.
			listStrings.setCellRenderer(new ListCellRenderer<String>() {
				@SuppressWarnings("serial")
				class Renderer extends JLabel {
					private boolean isSelected;
					private boolean cellHasFocus;
					Renderer() {
						try {
							
							Icon icon = Images.getIcon(iconFileName);
							setIcon(icon);
							setOpaque(true);
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
					public void set(String value, int index, boolean isSelected,
							boolean cellHasFocus) {
						try {
							
							setText(value);
							setBackground(Utility.itemColor(index));
							this.isSelected = isSelected;
							this.cellHasFocus = cellHasFocus;
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
					@Override
					public void paint(Graphics g) {
						try {
							super.paint(g);
							GraphUtility.drawSelection(g, this, isSelected, cellHasFocus);
						}
						catch (Throwable e) {
							Safe.exception(e);
						}
					}
				}
				Renderer renderer = new Renderer();
				@Override
				public Component getListCellRendererComponent(
						JList<? extends String> list, String value, int index,
						boolean isSelected, boolean cellHasFocus) {
					
					try {
						if (value == null) {
							return null;
						}
						renderer.set(value, index, isSelected, cellHasFocus);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return renderer;
				}
			});
			
			// Set list model.
			listModel = new DefaultListModel<String>();
			listStrings.setModel(listModel);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load dialog data.
	 * @param stringItems
	 * @param iconFileName
	 * @param titleId
	 * @param message 
	 */
	private void loadDialog(LinkedList<String> stringItems, String titleId, String message) {
		try {
			
			setTitle(Resources.getString(titleId));
			labelMessage.setText(message);
			
			this.stringItems = stringItems;
			loadList();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load list.
	 */
	protected void loadList() {
		try {
			
			listModel.clear();
			
			String filter = textFilter.getText();
			for (String stringItem : stringItems) {
				
				if (Utility.matches(stringItem, filter, false, false, false)) {
					listModel.addElement(stringItem);
				}
			}	
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get selected string.
	 * @return
	 */
	private String getSelectedString() {
		
		try {
			return (String) listStrings.getSelectedValue();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
}
