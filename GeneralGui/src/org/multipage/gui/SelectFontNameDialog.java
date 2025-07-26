/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.Color;
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
import java.text.Collator;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;

import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Dialog that can select font name. 
 * @author vakol
 *
 */
public class SelectFontNameDialog extends JDialog {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Font types.
	 */
	private static final int GENERIC_NAME = 0;
	private static final int FAMILY_NAME = 1;
	
	/**
	 * Font name class.
	 */
	private class FontName {
		
		String name;
		int type;
		
		/**
		 * Constructor.
		 */
		FontName(String name, int type) {
			
			this.name = name;
			this.type = type;
		}

		@Override
		public String toString() {
			return name;
		}
	}
	
	/**
	 * Dialog serialized states.
	 */
	private static Rectangle bounds = new Rectangle();
	private static LinkedList<String> familyFonts = new LinkedList<String>();

	/**
	 * Set default states.
	 */
	public static void setDefaultData() {
		
		bounds = new Rectangle();
		familyFonts = new LinkedList<String>();
	}

	/**
	 * Save serialized states.
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream)
			throws IOException {
		
		outputStream.writeObject(bounds);
		outputStream.writeObject(familyFonts);
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
		familyFonts = Utility.readInputStreamObject(inputStream, LinkedList.class);
	}
	
	/**
	 * Confirm flag.
	 */
	private boolean confirm = false;
	
	/**
	 * List model.
	 */
	private DefaultListModel<FontName> model;
	
	/**
	 * Output font name.
	 */
	private String outputName;
	
	// $hide<<$
	/**
	 * Components.
	 */
	private JButton buttonCancel;
	private JButton buttonOk;
	private JLabel labelFontNames;
	private JScrollPane scrollPane;
	private JLabel labelNewFont;
	private JTextField textFontName;
	private JButton buttonAddFont;
	private JList<FontName> list;
	private JPopupMenu popupMenu;
	private JMenuItem menuRename;
	private JMenuItem menuRemove;

	/**
	 * Show dialog.
	 * @param parent
	 * @return
	 */
	public static String showDialog(Component parent) {
		
		try {
			SelectFontNameDialog dialog = new SelectFontNameDialog(parent);
			dialog.setVisible(true);
			
			if (dialog.confirm) {
				
				return dialog.outputName;
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
	 */
	public SelectFontNameDialog(Component parent) {
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
		setTitle("org.multipage.gui.textSelectFontName");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});
		setBounds(100, 100, 450, 346);
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
		
		labelFontNames = new JLabel("org.multipage.gui.textFontNames");
		springLayout.putConstraint(SpringLayout.NORTH, labelFontNames, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, labelFontNames, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(labelFontNames);
		
		scrollPane = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, labelFontNames);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(scrollPane);
		
		labelNewFont = new JLabel("org.multipage.gui.textInsertFont");
		springLayout.putConstraint(SpringLayout.WEST, labelNewFont, 0, SpringLayout.WEST, labelFontNames);
		springLayout.putConstraint(SpringLayout.SOUTH, labelNewFont, -55, SpringLayout.SOUTH, getContentPane());
		getContentPane().add(labelNewFont);
		
		textFontName = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -10, SpringLayout.NORTH, textFontName);
		
		list = new JList();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(list);
		
		popupMenu = new JPopupMenu();
		addPopup(list, popupMenu);
		
		menuRename = new JMenuItem("org.multipage.gui.textRenameFontName");
		menuRename.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRename();
			}
		});
		popupMenu.add(menuRename);
		
		menuRemove = new JMenuItem("org.multipage.gui.textRemoveFontName");
		menuRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRemove();
			}
		});
		popupMenu.add(menuRemove);
		textFontName.setPreferredSize(new Dimension(6, 22));
		textFontName.setMinimumSize(new Dimension(6, 22));
		springLayout.putConstraint(SpringLayout.NORTH, textFontName, -3, SpringLayout.NORTH, labelNewFont);
		springLayout.putConstraint(SpringLayout.WEST, textFontName, 6, SpringLayout.EAST, labelNewFont);
		getContentPane().add(textFontName);
		textFontName.setColumns(30);
		
		buttonAddFont = new JButton("");
		buttonAddFont.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onAddFont();
			}
		});
		buttonAddFont.setMargin(new Insets(0, 0, 0, 0));
		buttonAddFont.setPreferredSize(new Dimension(22, 22));
		springLayout.putConstraint(SpringLayout.NORTH, buttonAddFont, 0, SpringLayout.NORTH, textFontName);
		springLayout.putConstraint(SpringLayout.WEST, buttonAddFont, 0, SpringLayout.EAST, textFontName);
		getContentPane().add(buttonAddFont);
	}

	/**
	 * On remove.
	 */
	protected void onRemove() {
		try {
			
			int index = list.getSelectedIndex();
			FontName fontName = list.getSelectedValue();
			
			if (fontName == null) {
				Utility.show(this, "org.multipage.gui.messageSelectSingleFontName");
				return;
			}
			
			if (fontName.type == GENERIC_NAME) {
				Utility.show(this, "org.multipage.gui.messageCannotRemoveGenericFontName");
				return;
			}
			
			// Remove item.
			if (Utility.askParam(this, "org.multipage.gui.messageRemoveSelectedFontName", fontName.name)) {
				model.remove(index);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On rename.
	 */
	protected void onRename() {
		try {
			
			FontName fontName = list.getSelectedValue();
			if (fontName == null) {
				
				Utility.show(this, "org.multipage.gui.messageSelectSingleFontName");
				return;
			}
			
			if (fontName.type == GENERIC_NAME) {
				Utility.show(this, "org.multipage.gui.messageCannotRenameGenericFontName");
				return;
			}
			
			// Get new font name.
			String name = Utility.input(this, "org.multipage.gui.messageInsertNewFontName", fontName.name);
			if (name != null && !name.isEmpty()) {
				
				fontName.name = name;
				
				scrollPane.revalidate();
				scrollPane.repaint();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On add font.
	 */
	protected void onAddFont() {
		try {
			
			String name = textFontName.getText();
			if (name.isEmpty()) {
				Utility.show(this, "org.multipage.gui.messageInsertFontName");
				return;
			}
			
			if (existsFont(name, FAMILY_NAME)) {
				Utility.show(this, "org.multipage.gui.messageFontNameAlreadyExists");
				return;
			}
			
			addFont(name);
			
			list.ensureIndexIsVisible(model.getSize() - 1);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Add font.
	 * @param name
	 * @return
	 */
	private boolean addFont(String name) {
		
		try {
			if (name.isEmpty()) {
				return false;
			}
			
			// Add new element.
			if (!existsFont(name, FAMILY_NAME)) {
				model.addElement(new FontName(name, FAMILY_NAME));
			}
	
			return true;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Returns true value if a font already exists.
	 * @param name
	 * @param type
	 * @return
	 */
	private boolean existsFont(String name, int type) {
		
		try {
			Enumeration<FontName> fontNames = model.elements();
			while (fontNames.hasMoreElements()) {
				
				FontName fontName = fontNames.nextElement();
				if (fontName.name.equals(name) && fontName.type == type) {
					
					return true;
				}
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			
			localize();
			setIcons();
			setToolTips();
			
			initList();
			loadGenericNames();
			loadFamilyNames();
			
			loadDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Initialize list.
	 */
	private void initList() {
		try {
			
			// Create and set model.
			model = new DefaultListModel<FontName>();
			list.setModel(model);
			
			// Create and set renderer.
			list.setCellRenderer(new ListCellRenderer<FontName>() {
	
				// Define renderer.
				@SuppressWarnings("serial")
				final RendererJLabel renderer = new RendererJLabel() {
					{
						try {
							setOpaque(true);
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				};
							
				@Override
				public Component getListCellRendererComponent(
						JList<? extends FontName> list, FontName value, int index,
						boolean isSelected, boolean cellHasFocus) {
					
					try {
						if (value == null) {
							return null;
						}
						
						FontName fontName = (FontName) value;
						
						// Set renderer.
						renderer.set(isSelected, cellHasFocus, index);
						
						if (fontName.type == GENERIC_NAME) {
							renderer.setForeground(Color.DARK_GRAY);
							renderer.setText("<html>[<span style='font-family:" + fontName.name + ";font-size:12px'>" + fontName.name + "</span>]</html>");
						}
						else {
							renderer.setForeground(Color.BLACK);
							renderer.setText("<html><span style='font-family:" + fontName.name + ";font-size:12px'>" + fontName.name + "</span></html>");
						}
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
	 * Load generic font names.
	 */
	private void loadGenericNames() {
		try {
			
			final String [] genericNames = { "serif", "sans-serif", "monospace", "cursive", "fantasy" };
			
			for (String genericName : genericNames) {
				model.addElement(new FontName(genericName, GENERIC_NAME));
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set tool tips.
	 */
	private void setToolTips() {
		try {
			
			buttonAddFont.setToolTipText(Resources.getString("org.multipage.gui.tooltipAddFontToList"));
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
			buttonAddFont.setIcon(Images.getIcon("org/multipage/gui/images/insert.png"));
			menuRename.setIcon(Images.getIcon("org/multipage/gui/images/edit.png"));
			menuRemove.setIcon(Images.getIcon("org/multipage/gui/images/cancel_icon.png"));
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
			Utility.localize(labelFontNames);
			Utility.localize(labelNewFont);
			Utility.localize(menuRename);
			Utility.localize(menuRemove);
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
			
			// Get chosen font.
			outputName = textFontName.getText();
			if (outputName.isEmpty()) {
				
				FontName fontName = list.getSelectedValue();
				if (fontName == null) {
					
					Utility.show(this, "org.multipage.gui.messageTypeNewOrSelectListFont");
					return;
				}
				
				outputName = fontName.name;
			}
			else {
				// Try to save new list.
				addFont(outputName);
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
			
			saveFamilyFonts();
			bounds = getBounds();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Save family fonts.
	 */
	private void saveFamilyFonts() {
		try {
			
			familyFonts.clear();
			
			Enumeration<FontName> fontNames = model.elements();
			while (fontNames.hasMoreElements()) {
				
				FontName fontName = fontNames.nextElement();
				if (fontName.type == FAMILY_NAME) {
					
					familyFonts.add(fontName.name);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load family font names.
	 */
	private void loadFamilyNames() {
		try {
			
			// Sort font names.
			familyFonts.sort(new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					
					try {
						return Collator.getInstance().compare(o1,o2);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return 0;
				}});
			
			for (String name : familyFonts) {
				model.addElement(new FontName(name, FAMILY_NAME));
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On double click list.
	 */
	private void onDoubleClickList() {
		try {
			
			FontName fontName = list.getSelectedValue();
			if (fontName == null) {
				
				Utility.show(this, "org.multipage.gui.messageTypeNewOrSelectListFont");
				return;
			}
			
			outputName = fontName.name;
			
			saveDialog();
			confirm = true;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
		dispose();
	}

	/**
	 * On click list.
	 * @param e
	 */
	protected void onClickList(MouseEvent e) {
		try {
			
			if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
				onDoubleClickList();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Add popup menu.
	 * @param component
	 * @param popup
	 */
	private void addPopup(Component component, final JPopupMenu popup) {
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
				@Override
				public void mouseClicked(MouseEvent e) {
					try {
						
						onClickList(e);
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
