/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.translator;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.maclan.Area;
import org.maclan.DictionaryItem;
import org.maclan.Language;
import org.maclan.LocalizedText;
import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.multipage.basic.ProgramBasic;
import org.multipage.gui.Images;
import org.multipage.gui.ReportMessages;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.ToolBarKit;
import org.multipage.gui.TopMostButton;
import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Main window for the Translator application.
 * @author vakol
 *
 */
public class TranslatorDialog extends JDialog {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * XML validation file.
	 */
	private static final String xmlValidationFile = "/org/multipage/translator/properties/translator.xsd";

	/**
	 * XML node names.
	 */
	private static final String xmlRootName = "Dictionary";
	private static final String xmlLanguagesNode = "Languages";
	private static final String xmlLanguageNode = "Language";
	private static final String xmlLanguageIdAttribute = "id";
	private static final String xmlLanguageAliasAttribute = "alias";
	private static final String xmlLanguageDescriptionAttribute = "description";
	private static final String xmlTextNode = "Text";
	private static final String xmlTextIdAttribute = "id";
	private static final String xmlHoldersNode = "Holders";
	private static final String xmlHolderNode = "Holder";

	/**
	 * Initial boundaries.
	 */
	private static Rectangle bounds;
	
	/**
	 * Initial language selection.
	 */
	private static long selectedLanguageId = -1L;

	/**
	 * Table column widths.
	 */
	private static int tableColumn1Width = 50;
	private static int tableColumn2Width = 250;
	private static int tableColumn3Width = 250;
	private static int tableColumn4Width = 60;

	/**
	 * Table model.
	 */
	private DictionaryTableModel tableModel;
	
	/**
	 * Start language identifier.
	 */
	private long startLanguageId = 0L;
	
	/**
	 * Language.
	 */
	private Language language;

	/**
	 * Parent frame.
	 */
	private Window parentWindow;

	/**
	 * Middle layer.
	 */
	private Middle middle;
	
	/**
	 * Login properties.
	 */
	private Properties login;

	/**
	 * Selected areas.
	 */
	private LinkedList<Area> selectedAreas;

	// $hide<<$
	/**
	 * Dialog components.
	 */
	private JButton buttonClose;
	private JLabel labelLanguages;
	private JComboBox comboBoxLanguages;
	private JToolBar toolBarLanguages;
	private JScrollPane scrollPane;
	private JTable tableDictionary;
	private JToolBar toolBarDictionary;
	private JMenuBar menuBar;
	private JMenu menuFile;
	private JMenuItem menuFileExport;
	private JMenuItem menuFileImport;

	/**
	 * Launch the dialog.
	 */
	public static void showDialog(JFrame parentFrame) {
		try {
			
			TranslatorDialog dialog = new TranslatorDialog(parentFrame,
					ModalityType.APPLICATION_MODAL, null);
			dialog.setVisible(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create the dialog.
	 * @param parentWindow 
	 * @param selectedAreas 
	 * @param middle 
	 * @param login 
	 */
	public TranslatorDialog(Window parentWindow, ModalityType modalityType, LinkedList<Area> selectedAreas) {
		super(parentWindow, modalityType);
		
		try {
			// Initialize components.
			initComponents();
			// $hide>>$
			this.parentWindow = parentWindow;
			this.selectedAreas = selectedAreas;
			// Post creation.
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
		setMinimumSize(new Dimension(575, 490));
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onClose();
			}
		});
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		buttonClose = new JButton("textClose");
		buttonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClose();
			}
		});
		buttonClose.setMargin(new Insets(0, 0, 0, 0));
		buttonClose.setMaximumSize(new Dimension(80, 25));
		buttonClose.setMinimumSize(new Dimension(80, 25));
		buttonClose.setPreferredSize(new Dimension(80, 25));
		springLayout.putConstraint(SpringLayout.SOUTH, buttonClose, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, buttonClose, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(buttonClose);
		
		labelLanguages = new JLabel("org.multipage.translator.textLanguages");
		springLayout.putConstraint(SpringLayout.WEST, labelLanguages, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(labelLanguages);
		
		comboBoxLanguages = new JComboBox();
		springLayout.putConstraint(SpringLayout.NORTH, comboBoxLanguages, 6, SpringLayout.SOUTH, labelLanguages);
		springLayout.putConstraint(SpringLayout.SOUTH, comboBoxLanguages, 80, SpringLayout.SOUTH, labelLanguages);
		springLayout.putConstraint(SpringLayout.EAST, comboBoxLanguages, 0, SpringLayout.EAST, buttonClose);
		springLayout.putConstraint(SpringLayout.WEST, comboBoxLanguages, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(comboBoxLanguages);
		
		toolBarLanguages = new JToolBar();
		springLayout.putConstraint(SpringLayout.NORTH, toolBarLanguages, 6, SpringLayout.SOUTH, comboBoxLanguages);
		springLayout.putConstraint(SpringLayout.EAST, toolBarLanguages, -10, SpringLayout.EAST, getContentPane());
		toolBarLanguages.setFloatable(false);
		springLayout.putConstraint(SpringLayout.WEST, toolBarLanguages, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(toolBarLanguages);
		
		scrollPane = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, toolBarLanguages);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -40, SpringLayout.NORTH, buttonClose);
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(scrollPane);
		
		tableDictionary = new JTable();
		tableDictionary.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableDictionary.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1
						&& e.getClickCount() == 2) {
					onEditDictionary();
				}
			}
		});
		tableDictionary.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(tableDictionary);
		setTitle("org.multipage.translator.textDictionaryTitle");
		setBounds(100, 100, 592, 490);
		
		toolBarDictionary = new JToolBar();
		springLayout.putConstraint(SpringLayout.NORTH, toolBarDictionary, 6, SpringLayout.SOUTH, scrollPane);
		springLayout.putConstraint(SpringLayout.WEST, toolBarDictionary, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, toolBarDictionary, -6, SpringLayout.NORTH, buttonClose);
		springLayout.putConstraint(SpringLayout.EAST, toolBarDictionary, 0, SpringLayout.EAST, buttonClose);
		toolBarDictionary.setFloatable(false);
		getContentPane().add(toolBarDictionary);
		
		menuBar = new JMenuBar();
		springLayout.putConstraint(SpringLayout.NORTH, menuBar, 26, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, labelLanguages, 10, SpringLayout.SOUTH, menuBar);
		springLayout.putConstraint(SpringLayout.SOUTH, labelLanguages, 24, SpringLayout.SOUTH, menuBar);
		springLayout.putConstraint(SpringLayout.WEST, menuBar, 0, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, menuBar, 0, SpringLayout.EAST, getContentPane());
		getContentPane().add(menuBar);
		
		menuFile = new JMenu("org.multipage.translator.menuDictionaryFile");
		menuBar.add(menuFile);
		
		menuFileExport = new JMenuItem("org.multipage.translator.menuDictionaryFileExport");
		menuFileExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onExport();
			}
		});
		menuFileExport.setPreferredSize(new Dimension(200, 22));
		menuFile.add(menuFileExport);
		
		menuFileImport = new JMenuItem("org.multipage.translator.menuDictionaryFileImport");
		menuFileImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onImport();
			}
		});
		menuFile.add(menuFileImport);
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			// Set dialog icon.
			setIconImage(Images.getImage("org/multipage/basic/images/main_icon.png"));
			// Get middle layer.
			middle = ProgramBasic.getMiddle();
			// Get login properties.
			login = ProgramBasic.getLoginProperties();
			// Add top most window toggle button.
			TopMostButton.add(this, getContentPane());
			// Set icons.
			setIcons();
			// Initialize combo box.
			initializeComboBox();
			// Initialize too bar.
			initializeToolBars();
			// Load languages.
			loadLanguagesToCombo();
			// Initialize table.
			initializeTable();
			loadColumnWidths();
			// Localize dialog components.
			localize();
			// Load dialog.
			loadDialog();
			// Load dictionary.
			loadDictionary();
			// Set listeners.
			setListeners();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set listeners.
	 */
	private void setListeners() {
		try {
			
			comboBoxLanguages.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						
						loadDictionary();
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
	 * Load dialog.
	 */
	private void loadDialog() {
		try {
			
			// Set bounds.
			if (!bounds.isEmpty()) {
				setBounds(bounds);
			}
			else {
				// Center dialog.
				Utility.centerOnScreen(this);
			}
			
			if (selectedLanguageId == -1L) {
				selectedLanguageId = startLanguageId;
			}
			setSelectedLanguageId(selectedLanguageId);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Load column widths.
	 */
	private void loadColumnWidths() {
		try {
			
			TableColumnModel columnModel = tableDictionary.getColumnModel();
			TableColumn column = columnModel.getColumn(0);
			column.setPreferredWidth(tableColumn1Width);
			column = columnModel.getColumn(1);
			column.setPreferredWidth(tableColumn2Width);
			column = columnModel.getColumn(2);
			column.setPreferredWidth(tableColumn3Width);
			column = columnModel.getColumn(3);
			column.setPreferredWidth(tableColumn4Width);
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
			selectedLanguageId = getSelectedLanguageId();
	
			saveColumnWidths();
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
			
			TableColumnModel columnModel = tableDictionary.getColumnModel();
			tableColumn1Width = columnModel.getColumn(0).getPreferredWidth();
			tableColumn2Width = columnModel.getColumn(1).getPreferredWidth();
			tableColumn3Width = columnModel.getColumn(2).getPreferredWidth();
			tableColumn4Width = columnModel.getColumn(3).getPreferredWidth();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Localize dialog components.
	 */
	private void localize() {
		try {
			
			Utility.localize(this);
			Utility.localize(buttonClose);
			Utility.localize(labelLanguages);
			Utility.localize(menuFile);
			Utility.localize(menuFileExport);
			Utility.localize(menuFileImport);
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
			menuFileExport.setIcon(Images.getIcon("org/multipage/translator/images/export_icon.png"));
			menuFileImport.setIcon(Images.getIcon("org/multipage/translator/images/import_icon.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On cancel.
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
	 * Load languages.
	 */
	private void loadLanguagesToCombo() {
		try {
			
			// Check references.
			if (middle == null) {
				return;
			}
	
			// Reset combo box.
			comboBoxLanguages.removeAllItems();
			
			// Load languages.
			LinkedList<Language> languages = new LinkedList<Language>();
			
			MiddleResult result;
			
			// Login to the database.
			result = middle.login(login);
			if (result.isOK()) {
				
				result = middle.loadLanguages(languages);
				if (result.isOK()) {
					
					// Load start language ID.
					Obj<Long> startLanguageId = new Obj<Long>();
					result = middle.loadStartLanguageId(startLanguageId);
					
					this.startLanguageId = startLanguageId.ref;
				}
	
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
			
			// Load combo box.
			for (Language language : languages) {
				comboBoxLanguages.addItem(language);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Initialize combo box.
	 */
	@SuppressWarnings("unchecked")
	private void initializeComboBox() {
		try {
			
			// Set renderer.
			comboBoxLanguages.setRenderer(new ListCellRenderer() {
				// Create renderer.
				private LanguageRenderer renderer = new LanguageRenderer();
				// Return renderer.
				@Override
				public Component getListCellRendererComponent(JList list, Object value,
						int index, boolean isSelected, boolean cellHasFocus) {
					
					try {
						// Check value.
						if (!(value instanceof Language)) {
							return null;
						}
						Language language = (Language) value;
						
						boolean isStart = language.id == startLanguageId;
		
						// Set renderer properties.
						renderer.setProperties(language.description, language.id,
								language.alias, language.image, isStart, index,
								isSelected, cellHasFocus);
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
	 * Initialize tool bars.
	 */
	private void initializeToolBars() {
		try {
			
			ToolBarKit.addToolBarButton(toolBarLanguages, "org/multipage/translator/images/add_item_icon.png",
					this, "onAddLanguage", "org.multipage.translator.tooltipAddLanguage");
			ToolBarKit.addToolBarButton(toolBarLanguages, "org/multipage/translator/images/edit.png",
					this, "onEditLanguage", "org.multipage.translator.tooltipEditLanguage");
			ToolBarKit.addToolBarButton(toolBarLanguages, "org/multipage/translator/images/remove_icon.png",
					this, "onDeleteLanguage", "org.multipage.translator.tooltipDeleteLanguage");
			toolBarLanguages.addSeparator();
			ToolBarKit.addToolBarButton(toolBarLanguages, "org/multipage/translator/images/update_icon.png",
					this, "onReloadLanguages", "org.multipage.translator.tooltipReloadLanguages");
			toolBarLanguages.addSeparator();
			JButton buttonArrange = new JButton(Resources.getString("org.multipage.translator.textArrangeLanguages"));
			toolBarLanguages.add(buttonArrange);
			buttonArrange.setMargin(new Insets(0, 0, 0, 0));
			buttonArrange.setMaximumSize(new Dimension(110, 25));
			buttonArrange.setMinimumSize(new Dimension(80, 25));
			buttonArrange.setPreferredSize(new Dimension(80, 25));
			buttonArrange.setIconTextGap(6);
			buttonArrange.setIcon(Images.getIcon("org/multipage/translator/images/order.png"));
			toolBarLanguages.add(buttonArrange);
			buttonArrange.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						
						// Edit languages order.
						onOrderLanguages();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			
			ToolBarKit.addToolBarButton(toolBarDictionary, "org/multipage/translator/images/edit.png",
					this, "onEditDictionary", "org.multipage.translator.tooltipEditDictionary");
			ToolBarKit.addToolBarButton(toolBarDictionary, "org/multipage/translator/images/search_icon.png",
					this, "onFindId", "org.multipage.translator.tooltipFindId");
			ToolBarKit.addToolBarButton(toolBarDictionary, "org/multipage/translator/images/update_icon.png",
					this, "updateDictionary", "org.multipage.translator.tooltipUpdateTexts");
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On find ID.
	 */
	public void onFindId() {
		try {
			
			// Get ID.
			String identifier = JOptionPane.showInputDialog(
					Resources.getString("org.multipage.translator.textInsertIdentifier"));
			if (identifier == null) {
				return;
			}
			
			for (int index = 0; index < tableModel.getRowCount(); index++) {
				String identifierFromTable = tableModel.getValueAt(index, 0).toString();
				if (identifier.equalsIgnoreCase(identifierFromTable)) {
					
					tableDictionary.getSelectionModel().setSelectionInterval(index, index);
					
					// Ensure that selected row is visible.
					Rectangle rect = tableDictionary.getCellRect(index, 0, true);
					tableDictionary.scrollRectToVisible(rect);
							
					onEditDictionary();
					return;
				}
			}
			
			Utility.show(this, "org.multipage.translator.messageTextIdNotFound");
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On add new language.
	 */
	public void onAddLanguage() {
		try {
			
			// Get new alias.
			String newAlias = getNewAlias();
			
			// Show dialog.
			LanguageEditor dialog = new LanguageEditor(parentWindow);
			dialog.setAlias(newAlias);
			dialog.setSart(false);
			
			dialog.setVisible(true);
			
			if (!dialog.isConfirmed()) {
				return;
			}
			
			boolean isStart = dialog.isSart();
			
			Obj<Long> languageId = new Obj<Long>();
			String description = dialog.getDescription();
			String alias = dialog.getAlias();
			BufferedImage image = dialog.getImage();
			
			MiddleResult result;
			
			// Database login.
			result = middle.login(login);
			if (result.isOK()) {
				// Insert new language.
				result = middle.insertLanguage(description, alias, image,
						languageId);
				if (result.isOK()) {
	
					// If the new language is the starting language, update
					// corresponding record.
					if (isStart) {
						result = middle.updateStartLanguage(languageId.ref);
						if (result.isOK()) {
						
							startLanguageId = languageId.ref;
						}
					}
				}
				
				// Database logout.
				MiddleResult logoutResult = middle.logout(result);
				if (result.isOK()) {
					result = logoutResult;
				}
			}
			
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			// Create new language object.
			Language language = new Language(languageId.ref, description, alias,
					image);
			// Add it to the list.
			comboBoxLanguages.addItem(language);
			comboBoxLanguages.setSelectedItem(language);
			
			// Fire on languages update.
			onLanguagesUpdate();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On edit language.
	 */
	public void onEditLanguage() {
		try {
			
			// Get selected object.
			Object selected = comboBoxLanguages.getSelectedItem();
			if (!(selected instanceof Language)) {
				Utility.show(this, "org.multipage.translator.messageSelectLanguage");
				return;
			}
			
			Language language = (Language) selected;
			
			// Open edit dialog.
			LanguageEditor dialog = new LanguageEditor(parentWindow);
			dialog.setDescription(language.description);
			dialog.setAlias(language.alias);
			dialog.setId(language.id);
			dialog.setImage(language.image);
			dialog.setSart(startLanguageId == language.id);
			
			dialog.setVisible(true);
			
			if (!dialog.isConfirmed()) {
				return;
			}
			
			// Get new properties.
			String description = dialog.getDescription();
			String alias = dialog.getAlias();
			BufferedImage image = dialog.getImage();
			
			// Database login.
			MiddleResult result = middle.login(login);
			if (result.isOK()) {
				
				// Update language.
				result = middle.updateLanguage(
						new Language(language.id, description, alias, image));
				
				if (result.isOK()) {
	
					long languageId = language.id;
					Long startLangId = null;
					
					if (dialog.isSart()) {
						startLangId = languageId;
					}
					else if (languageId == startLanguageId) {
							startLangId = 0L;
					}
					
					// Change start language.
					if (startLangId != null) {
						result = middle.updateStartLanguage(startLangId);
						if (result.isOK()) {
							startLanguageId = startLangId;
						}
					}
				}
				
				// Database logout.
				MiddleResult logoutResult = middle.logout(result);
				if (result.isOK()) {
					result = logoutResult;
				}
			}
			
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			// Set language properties.
			language.description = description;
			language.alias = alias;
			language.image = image;
			
			// Update languages.
			onLanguagesUpdate();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On delete language.
	 */
	public void onDeleteLanguage() {
		try {
			
			// Get selected object.
			Object selected = comboBoxLanguages.getSelectedItem();
			if (!(selected instanceof Language)) {
				Utility.show(this, "org.multipage.translator.messageSelectLanguage");
				return;
			}
			
			Language language = (Language) selected;
			
			// If it is the default language, exit.
			if (language.id == 0) {
				Utility.show(this, "org.multipage.translator.messageCannotRemoveDefaultLanguage");
				return;
			}
			
			// Ask user.
			if (JOptionPane.showConfirmDialog(this, String.format(
					Resources.getString("org.multipage.translator.messageConfirmLanguageDeletion"), language.toString()))
					!= JOptionPane.YES_OPTION) {
				return;
			}
			
			// Database login.
			MiddleResult result = middle.login(login);
			if (result.isOK()) {
			
				// If the language is start language, set default language as
				// a start language.
				if (startLanguageId == language.id) {
					result = middle.updateStartLanguage(0L);
					if (result.isOK()) {
						startLanguageId = 0L;
					}
				}
				
				if (result.isOK()) {
					// Remove the language.
					result = middle.removeLanguage(language);
				}
				
				// Database logout.
				MiddleResult logoutResult = middle.logout(result);
				if (result.isOK()) {
					result = logoutResult;
				}
			}
			
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			// Reload languages.
			comboBoxLanguages.removeItem(language);
			// Fire on languages update.
			onLanguagesUpdate();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On reload.
	 */
	public void onReloadLanguages() {
		try {
			
			long languageId = language.id;
			
			// Reload the combo box.
			loadLanguagesToCombo();
			
			setSelectedLanguageId(languageId);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get new alias.
	 * @return
	 */
	private String getNewAlias() {
		
		try {
			String aliasName;
			
			// Get model.
			DefaultComboBoxModel model = (DefaultComboBoxModel) comboBoxLanguages.getModel();
			
			// Get new alias text.
			String newAliasText = Resources.getString("org.multipage.translator.textNewAliasTextPart");
			// Do loop for aliases.
			for (int index = 1; ; index++) {
				// Create alias name.
				aliasName = newAliasText + String.valueOf(index);
				// Flag.
				boolean found = false;
				// If the alias already exists, continue the loop.
				for (int aliasIndex = 0; aliasIndex < model.getSize(); aliasIndex++) {
					// Get language.
					Language language = (Language) model.getElementAt(aliasIndex);
					// If the alias matches...
					if (aliasName.equals(language.alias)) {
						found = true;
						break;
					}
				}
				// If not found exit the loop.
				if (!found) {
					break;
				}
			}
			
			return aliasName;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * On languages update.
	 */
	private void onLanguagesUpdate() {
		try {
			
			// Update languages combo box.
			comboBoxLanguages.revalidate();
			comboBoxLanguages.repaint();
			// Reload the languages combo box.
			onLoadLangauges();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On load languages.
	 */
	protected void onLoadLangauges() {
		
		// Override this method.
	}

	/**
	 * On update information.
	 */
	protected void onUpdateInformation() {
		
		// Override this method.
	}
	
	/**
	 * Initialize table.
	 */
	private void initializeTable() {
		try {
			
			// Set table model.
			tableModel = new DictionaryTableModel();
			tableModel.setCurrentLanguage(language);
			tableDictionary.setModel(tableModel);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load dictionary.
	 */
	private void loadDictionary() {
		try {
			
			// Get selected language.
			Object selectedObject = comboBoxLanguages.getSelectedItem();
			if (!(selectedObject instanceof Language)) {
				return;
			}
			
			// Load dictionary.
			language = (Language) selectedObject;
			
			ArrayList<DictionaryItem> dictionary = tableModel.getDictionary();
			
			MiddleResult result = middle.loadDictionary(
					login, language, selectedAreas, dictionary);
			
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			// Save column widths.
			saveColumnWidths();
			
			// Set current language.
			tableModel.setCurrentLanguage(language);
			
			tableModel.fireTableDataChanged();
			tableModel.fireTableStructureChanged();
			
			// Load column widths.
			loadColumnWidths();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On edit dictionary.
	 */
	public void onEditDictionary() {
		try {
			
			// Get selected dictionary item.
			int selectedRow = tableDictionary.getSelectedRow();
			if (selectedRow == -1) {
				Utility.show(this, "org.multipage.translator.messageSelectDictionaryItem");
				return;
			}
			
			DictionaryItem dictionaryItem = tableModel.getDictionaryItem(selectedRow);
			if (dictionaryItem == null) {
				return;
			}
			
			// Localize text.
			if (LocalizeTextDialog.showDialog(parentWindow,
					dictionaryItem.getId(), dictionaryItem.getDefaultText(),
					dictionaryItem.getLocalizedText(),
					language)) {
				
				// Reload dictionary.
				updateDictionary();
				// Update information.
				onUpdateInformation();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Update dictionary data.
	 */
	public void updateDictionary() {
		try {
			
			loadDictionary();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Save data.
	 * @param outputStream
	 */
	public static void serializeData(StateOutputStream outputStream)
		throws IOException {

		// Save selected language.
		outputStream.writeLong(selectedLanguageId);
		// Save dialog boundaries.
		outputStream.writeObject(bounds);
		// Save table column widths.
		outputStream.writeInt(tableColumn1Width);
		outputStream.writeInt(tableColumn2Width);
		outputStream.writeInt(tableColumn3Width);
		outputStream.writeInt(tableColumn4Width);
	}

	/**
	 * Load data.
	 * @param inputStream
	 */
	public static void serializeData(StateInputStream inputStream)
		throws IOException, ClassNotFoundException {
		
		// Load selected language.
		selectedLanguageId = inputStream.readLong();
		// Load dialog bounds.
		Object object = inputStream.readObject();
		if (!(object instanceof Rectangle)) {
			throw new ClassNotFoundException();
		}
		bounds = (Rectangle) object;
		// Load table column widths.
		tableColumn1Width = inputStream.readInt();
		tableColumn2Width = inputStream.readInt();
		tableColumn3Width = inputStream.readInt();
		tableColumn4Width = inputStream.readInt();
	}

	/**
	 * Set default data
	 */
	public static void setDefaultData() {

		bounds = new Rectangle();
	}

	/**
	 * Get selected language ID.
	 * @return
	 */
	private long getSelectedLanguageId() {
		
		try {
			Object selectedObject = comboBoxLanguages.getSelectedItem();
			if (!(selectedObject instanceof Language)) {
				return 0;
			}
			
			Language language = (Language) selectedObject;
			
			return language.id;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0L;
	}

	/**
	 * Select language with given ID.
	 * @param languageId
	 */
	private void setSelectedLanguageId(long languageId) {
		try {
			
			int count = comboBoxLanguages.getItemCount();
			
			for (int index = 0; index < count; index++) {
				// Check and select language with given ID.
				Object item = comboBoxLanguages.getItemAt(index);
				if (item instanceof Language) {
					
					Language language = (Language) item;
					if (language.id == languageId) {
						
						comboBoxLanguages.setSelectedIndex(index);
						break;
					}
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On export.
	 */
	protected void onExport() {
		try {
			
			// Get available languages.
			LinkedList<Language> availableLanguages = new LinkedList<Language>();
			
			MiddleResult result = ProgramBasic.getMiddle().loadLanguages(
					ProgramBasic.getLoginProperties(), availableLanguages);
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			// Select language to export.
			LinkedList<Language> selectedLanguages = new LinkedList<Language>();
	
			if (!SelectLanguagesDialog.showDialog(parentWindow, availableLanguages,
					startLanguageId, getSelectedLanguageId(), SelectLanguagesDialog.EXPORT,
					selectedLanguages)) {
				return;
			}
			
			// Get file name.
			String [][] filters = {{"org.multipage.translator.textXmlFilesDictionary", "xml"}};
			
			File file = Utility.chooseFileNameToSave(this, filters);
			if (file == null) {
				return;
			}
			
			// Export dictionary languages.
			exportLanguages(selectedLanguages, file);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On import.
	 */
	protected void onImport() {
		try {
			
			// Get file name.
			String [][] filters = {{"org.multipage.translator.textXmlFilesDictionary", "xml"}};
			
			File file = Utility.chooseFileToOpen(this, filters);
			if (file == null) {
				return;
			}
			
			// Import languages.
			importLanguages(file);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get excludes text IDs.
	 * @return
	 */
	private LinkedList<Long> getExcludedTextIds() {
		
		try {
			// Create list.
			LinkedList<Long> excludedTextIds = new LinkedList<Long>();
			
			for (DictionaryItem dictionaryItem : tableModel.getDictionary()) {
				if (dictionaryItem.isHidden()) {
					excludedTextIds.add(dictionaryItem.getId());
				}
			}
	
			return excludedTextIds;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Export languages.
	 * @param languages
	 * @param file
	 */
	private void exportLanguages(LinkedList<Language> languages, File file) {
		try {
			
			// Get excluded text IDs.
			LinkedList<Long> excludedTextIds = getExcludedTextIds();
			
			// Database login.
			Middle middle = ProgramBasic.getMiddle();
			MiddleResult result = middle.login(ProgramBasic.getLoginProperties());
			if (result.isOK()) {
			
			    try {
			    	boolean dataExists = false;
			    	
			    	// Try to create DOM document.
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = factory.newDocumentBuilder();
					Document document = builder.newDocument();
					
					// Insert root.
					Element root = document.createElement(xmlRootName);
					document.appendChild(root);
					
					// Insert languages element.
					Element languagesNode = document.createElement(xmlLanguagesNode);
					root.appendChild(languagesNode);
					
					// Create localized texts list.
					LinkedList<LocalizedText> localizedTexts = new LinkedList<LocalizedText>();
					
					// Holders set.
					Hashtable<Long, String> holders = new Hashtable<Long, String>();
		
					// Do loop for all selected languages.
					for (Language language : languages) {
	
						// Add language to document root element.
						Element languageElement = document.createElement(xmlLanguageNode);
						languageElement.setAttribute(xmlLanguageIdAttribute, String.valueOf(language.id));
						languageElement.setAttribute(xmlLanguageAliasAttribute, language.alias);
						languageElement.setAttribute(xmlLanguageDescriptionAttribute, language.description);
						languagesNode.appendChild(languageElement);
						
						// Get localized texts and save it.
						result = middle.loadLocalizedTexts(language.id, excludedTextIds, localizedTexts);
						if (result.isNotOK()) {
							break;
						}
						
						// Create localized texts elements.
						for (LocalizedText localizedText : localizedTexts) {
							
							dataExists = true;
							long localizedTextId = localizedText.getId();
							
							// Create localized text element.
							Element textElement = document.createElement(xmlTextNode);
							languageElement.appendChild(textElement);
							textElement.setAttribute(xmlTextIdAttribute,
									String.valueOf(localizedTextId));
							textElement.setTextContent(localizedText.getText());
							
							// Add holder.
							holders.put(localizedTextId, tableModel.getHolderText(localizedTextId));
						}
					}
					// Save the XML file.
					if (dataExists && result.isOK()) {
						
						// Insert text holders element.
						Element holdersElement = document.createElement(xmlHoldersNode);
						root.appendChild(holdersElement);
						
						// Save holders.
						for (long textId : holders.keySet()) {
							
							// Get holder text.
							String holderText = holders.get(textId);
							
							// Create holder element.
							Element holderElement = document.createElement(xmlHolderNode);
							holdersElement.appendChild(holderElement);
							holderElement.setAttribute(xmlTextIdAttribute, String.valueOf(textId));
							holderElement.setTextContent(holderText);
						}
						
						// Try to save XML document to file.
						Source source = new DOMSource(document);
						Result streamResult = new StreamResult(file);
						Transformer xformer = TransformerFactory.newInstance().newTransformer();
				        xformer.transform(source, streamResult);
					}
					else if (!dataExists) {
						Utility.show(this, "org.multipage.translator.messageDictionaryIsEmpty");
					}
			    }
			    catch (Exception e) {
			    	Safe.exception(e);
			    }
			    
			    // Database logout.
			    MiddleResult logoutResult = middle.logout(result);
			    if (result.isOK()) {
			    	result = logoutResult;
			    }
			}
			
			// On error inform user.
			if (result.isNotOK()) {
				result.show(this);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Import languages.
	 * @param file
	 */
	private void importLanguages(File file) {
		try {
			
			// If the file doesn't exist, exit the method.
			if (!file.exists()) {
				Utility.show(this, "org.multipage.translator.messageDictionaryFileDoesntExist");
				return;
			}
			
			// Test if program can read the file.
			if (!file.canRead()) {
				JOptionPane.showMessageDialog(this, Resources.getString("org.multipage.translator.messageCannotReadDictionaryFile2"));
				return;
			}
	
		    final Component parent = this;
		    
		    // Create empty collections.
			LinkedList<Language> languages = new LinkedList<Language>();
			LinkedList<LocalizedText> localizedTexts = new LinkedList<LocalizedText>();
		    	
			try {
				// Try to get parser and parse file.
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db;
				db = dbf.newDocumentBuilder();
				// Error handler.
				db.setErrorHandler(new ErrorHandler() {
					@Override
					public void warning(SAXParseException exception) throws SAXException {
						try {
							
							JOptionPane.showMessageDialog(parent, exception.getMessage());
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
					@Override
					public void fatalError(SAXParseException exception) throws SAXException {
						try {
							
							JOptionPane.showMessageDialog(parent, exception.getMessage());
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
					@Override
					public void error(SAXParseException exception) throws SAXException {
						try {
							
							JOptionPane.showMessageDialog(parent, exception.getMessage());
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
				
				Document document = db.parse(file);
				
				// Validate XML file.
				InputStream schemaInputStream = getClass().getResourceAsStream(xmlValidationFile);
				if (schemaInputStream == null) {
					// Inform user and exit.
					JOptionPane.showMessageDialog(this, Resources.getString("org.multipage.translator.messageCannotLocateDictionaryValiationFile"));
					return;
				}
	
				SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				Schema schema = factory.newSchema(new StreamSource(schemaInputStream));
				Validator validator = schema.newValidator();
				try {
					validator.validate(new DOMSource(document));
				}
				catch (SAXException e) {
					// Set message.
					String message = Resources.getString("org.multipage.translator.messageDictionaryValidationException")
				        						+ "\n" + e.getMessage();
					JOptionPane.showMessageDialog(this, message);
					return;
				}
				
				schemaInputStream.close();
				        
				// Get DOM data.
				Node dictionaryRoot = document.getFirstChild();
				Node languagesNode = dictionaryRoot.getFirstChild();
	
				// Load languages and texts.
				Node languageNode = languagesNode.getFirstChild();
	
				while (languageNode != null) {
					
					String languageIdStr = languageNode.getAttributes().getNamedItem(xmlLanguageIdAttribute).getNodeValue();
					long languageId = Long.parseLong(languageIdStr);
					String languageDescription = languageNode.getAttributes().getNamedItem(xmlLanguageDescriptionAttribute).getNodeValue();
					String languageAlias = languageNode.getAttributes().getNamedItem(xmlLanguageAliasAttribute).getNodeValue();
	
					// Add new language.
					languages.add(new Language(languageId, languageDescription, languageAlias, null));
					
					// Set texts.
					Node textNode = languageNode.getFirstChild();
					while (textNode != null) {
				        		
						String textIdStr = textNode.getAttributes().getNamedItem(xmlTextIdAttribute).getNodeValue();
						String text = textNode.getTextContent();
				        		
						// Get text ID.
						long textId = Long.parseLong(textIdStr);
						
						// Add new localized text.
						localizedTexts.add(new LocalizedText(textId, text, languageId));
	
						textNode = textNode.getNextSibling();
					}
					languageNode = languageNode.getNextSibling();
				}
			}
			catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
			}
			
			// Select languages to import.
			LinkedList<Language> selectedLanguages = new LinkedList<Language>();
			
			if (!SelectLanguagesDialog.showDialog(this, languages,
					startLanguageId, getSelectedLanguageId(),
					SelectLanguagesDialog.IMPORT, selectedLanguages)) {
				return;
			}
			
			// Ask user.
			if (JOptionPane.showConfirmDialog(this,
					Resources.getString("org.multipage.translator.messageConfirmDictionaryImport"))
					!= JOptionPane.YES_OPTION) {
				return;
			}
			
			// Update database.
			LinkedList<String> errorMessages = new LinkedList<String>();
			
			MiddleResult result = ProgramBasic.getMiddle().updateDictionary(
					ProgramBasic.getLoginProperties(), selectedLanguages,
					localizedTexts, errorMessages);
			if (result.isNotOK()) {
				ReportMessages.showDialog(this, errorMessages);
			}
			
			// Update dictionary.
			updateDictionary();
			// Update information.
			onUpdateInformation();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On order languages.
	 */
	protected void onOrderLanguages() {
		try {
			
			// Get selected language.
			long languageId = getSelectedLanguageId();
			
			// Open order languages dialog.
			OrderLanguagesDialog.showDialog(this);
			
			// Update data.
			loadLanguagesToCombo();
			
			// Select language.
			setSelectedLanguageId(languageId);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}

/**
 * 
 * @author
 *
 */
class DictionaryTableModel extends AbstractTableModel {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Dictionary.
	 */
	private ArrayList<DictionaryItem> dictionary =
		new ArrayList<DictionaryItem>();

	/**
	 * Current language.
	 */
	private Language currentLanguage;

	/**
	 * Get row count.
	 */
	@Override
	public int getRowCount() {

		return dictionary.size();
	}

	/**
	 * Set current language.
	 * @param language
	 */
	public void setCurrentLanguage(Language language) {
		
		currentLanguage = language;
	}

	/**
	 * Get holder text.
	 * @param localizedTextId
	 * @return
	 */
	public String getHolderText(long localizedTextId) {
		
		try {
			// Find dictionary item.
			for (DictionaryItem dictionaryItem : dictionary) {
				
				if (dictionaryItem.getId() == localizedTextId) {
					return dictionaryItem.getHolderText();
				}
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get dictionary item.
	 * @param index
	 * @return
	 */
	public DictionaryItem getDictionaryItem(int index) {

		try {
			return dictionary.get(index);
		}
		catch (IndexOutOfBoundsException e) {
		}
		catch (Exception e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Get dictionary.
	 * @return
	 */
	public ArrayList<DictionaryItem> getDictionary() {
		
		return dictionary;
	}

	/**
	 * Get column count.
	 */
	@Override
	public int getColumnCount() {

		return 4;
	}

	/**
	 * Get value.
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		try {
			// Get dictionary item.
			DictionaryItem dictionaryItem;
			try {
				dictionaryItem = dictionary.get(rowIndex);
			}
			catch (IndexOutOfBoundsException e) {
				return null;
			}
			
			Object returned = null;
			
			switch (columnIndex) {
			// On text holder.
			case 0:
				returned = dictionaryItem.getHolderText();
				break;
			// On default language.
			case 1:
				returned = dictionaryItem.getDefaultText();
				break;
			// On localized text.
			case 2:
				returned = dictionaryItem.getLocalizedText();
				break;
			// On hide flag.
			case 3:
				returned = dictionaryItem.isHidden();
				break;
			}
			
			// Return value.
			return returned;
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
			String columnName;
			
			// Get column name.
			switch (column) {
			
			case 0:
				columnName = "org.multipage.translator.textTextHolder";
				break;
			case 1:
				columnName = "org.multipage.translator.textDefaultLanguageText";
				break;
			case 2:
				if (currentLanguage != null) {
					return currentLanguage.toString() + ":";
				}
				columnName = "org.multipage.translator.textError";
				break;
			case 3:
				columnName = "org.multipage.translator.textHideLanguage";
				break;
			default:
				columnName = "org.multipage.translator.textError";
			}
			
			// Localize the text.
			columnName = Resources.getString(columnName);
			// Return column name.
			return columnName;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		
		try {
			switch (columnIndex) {
			// On text holder.
			case 0:
				return String.class;
			// On default language.
			case 1:
				return String.class;
			// On localized text.
			case 2:
				return String.class;
			// On hide flag.
			case 3:
				return Boolean.class;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return Object.class;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {

		return columnIndex == 3;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		try {
			
			if (columnIndex == 3 && aValue instanceof Boolean) {
				
				try {
					// Set dictionary item.
					DictionaryItem dictionaryItem = dictionary.get(rowIndex);
					boolean isHidden = (Boolean) aValue;
					dictionaryItem.setHide(isHidden);
				}
				catch (IndexOutOfBoundsException e) {
					
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
