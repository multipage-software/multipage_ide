/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.translator;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SpringLayout;

import org.maclan.Language;
import org.multipage.gui.CheckBoxCallback;
import org.multipage.gui.CheckBoxList;
import org.multipage.gui.CheckBoxListManager;
import org.multipage.gui.Images;
import org.multipage.gui.ToolBarKit;
import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Dialog that enables to select a language.
 * @author vakol
 *
 */
public class SelectLanguagesDialog extends JDialog {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Dialog behaviour type.
	 */
	public static final int EXPORT = 0;
	public static final int IMPORT = 1;
	
	/**
	 * Confirm flag.
	 */
	private boolean confirm;

	/**
	 * Languages.
	 */
	private LinkedList<Language> selectedLanguages;

	/**
	 * Available selectedLanguages.
	 */
	private LinkedList<Language> availableLanguages;

	/**
	 * Default language ID.
	 */
	private long startLanguageId;

	/**
	 * Selected language ID.
	 */
	private long selectedLanguageId;

	/**
	 * Dialog type.
	 */
	private int type;

	/**
	 * List component.
	 */
	private CheckBoxList<Language> list = new CheckBoxList<Language>();
	
	// $hide<<$
	/**
	 * Components.
	 */
	private JButton buttonCancel;
	private JButton buttonOk;
	private JLabel labelLanguages;
	private JScrollPane scrollListContainer;
	private JToolBar toolBar;

	/**
	 * Launch the application.
	 * @param availableLanguages 
	 * @param selectedLanguages 
	 * @param export2 
	 */
	public static boolean showDialog(Window parentWindow,
			LinkedList<Language> availableLanguages, long startLanguageId,
			long selectedLanguageId, int type,
			LinkedList<Language> selectedLanguages) {
		
		try {
			SelectLanguagesDialog dialog = new SelectLanguagesDialog(parentWindow);
			dialog.initializeDialog(availableLanguages, startLanguageId,
					selectedLanguageId, type, selectedLanguages);
			dialog.setVisible(true);
	
			return dialog.confirm;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Create the dialog. After that the dialog must be ready.
	 * @param parentWindow 
	 */
	public SelectLanguagesDialog(Window parentWindow) {
		super(parentWindow, ModalityType.APPLICATION_MODAL);
		
		try {
			// Initialize components.
			initComponents();
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
				onCancel();
			}
		});
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setTitle("");
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
		springLayout.putConstraint(SpringLayout.NORTH, buttonOk, 0, SpringLayout.NORTH, buttonCancel);
		springLayout.putConstraint(SpringLayout.EAST, buttonOk, -6, SpringLayout.WEST, buttonCancel);
		getContentPane().add(buttonOk);
		
		labelLanguages = new JLabel("org.multipage.translator.textLanguages2");
		springLayout.putConstraint(SpringLayout.NORTH, labelLanguages, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, labelLanguages, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(labelLanguages);
		
		scrollListContainer = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollListContainer, 6, SpringLayout.SOUTH, labelLanguages);
		springLayout.putConstraint(SpringLayout.WEST, scrollListContainer, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, scrollListContainer, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(scrollListContainer);
		
		toolBar = new JToolBar();
		toolBar.setFloatable(false);
		springLayout.putConstraint(SpringLayout.NORTH, toolBar, -36, SpringLayout.NORTH, buttonOk);
		springLayout.putConstraint(SpringLayout.SOUTH, scrollListContainer, 0, SpringLayout.NORTH, toolBar);
		springLayout.putConstraint(SpringLayout.WEST, toolBar, 0, SpringLayout.WEST, labelLanguages);
		springLayout.putConstraint(SpringLayout.SOUTH, toolBar, -6, SpringLayout.NORTH, buttonOk);
		springLayout.putConstraint(SpringLayout.EAST, toolBar, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(toolBar);
	}

	/**
	 * Initialize dialog.
	 * @param availableLanguages
	 * @param selectedLanguageId 
	 * @param type 
	 * @param selectedLanguages 
	 */
	private void initializeDialog(LinkedList<Language> availableLanguages,
			long startLanguageId, long selectedLanguageId, int type,
			LinkedList<Language> selectedLanguages) {
		try {
			
			this.selectedLanguages = selectedLanguages;
			this.startLanguageId = startLanguageId;
			this.selectedLanguageId = selectedLanguageId;
			this.type = type;
			this.availableLanguages = availableLanguages;
			
			// Set title.
			setTitle();
			// Localize components.
			localize();
			// Set icons.
			setIcons();
			// Center dialog.
			Utility.centerOnScreen(this);
			// Set list.
			setList();
			// Set tool bar.
			setToolBar();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set title.
	 */
	private void setTitle() {
		try {
			
			String title = Resources.getString(type == EXPORT ?
					"org.multipage.translator.textSelectLanguagesExportDialog" : "org.multipage.translator.textSelectLanguagesImportDialog");
			setTitle(title);
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
			
			// Get selected objects.
			list.getSelectedObjects(selectedLanguages);
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
	 * Set list.
	 */
	private void setList() {
		try {
			
			scrollListContainer.setViewportView(list);
			list.setContentManager(new CheckBoxListManager<Language>() {
				// Load item.
				@Override
				protected boolean loadItem(int index, Obj<Language> object,
						Obj<String> text, Obj<Boolean> selected) {
					
					try {
						Language language = availableLanguages.get(index);
						object.ref = language;
						text.ref = language.toString();
						selected.ref = isDefaultSelection(language.id);
					}
					catch (IndexOutOfBoundsException e) {
						
						return false;
					}
					return true;
				}
				// Set item.
				@Override
				protected boolean processChange(Language object, boolean selected) {
	
					return true;
				}
				
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Returns default selection of given language.
	 * @param id
	 * @return
	 */
	protected Boolean isDefaultSelection(long languageId) {
		
		try {
			boolean selected;
			
			if (type == EXPORT) {
				selected = languageId == selectedLanguageId
					|| languageId == startLanguageId;
			}
			else {
				if (availableLanguages.size() > 1) {
					selected = languageId != startLanguageId;
				}
				else {
					return true;
				}
			}
			
			return selected;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return true;
	}

	/**
	 * Set tool bar.
	 */
	private void setToolBar() {
		try {
			
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/translator/images/select_all.png",
					this, "selectAll", "org.multipage.translator.tooltipSelectAllLanguages");
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/translator/images/deselect_all.png",
					this, "deselectAll", "org.multipage.translator.tooltipUnselectAllLanguages");
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/translator/images/default_icon.png", 
					this, "selectDefault", "org.multipage.translator.tooltipSelectDefaultLanguage");
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
			
			Utility.localize(buttonOk);
			Utility.localize(buttonCancel);
			Utility.localize(labelLanguages);
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
			
			setIconImage(Images.getImage("org/multipage/translator/images/main_icon.png"));
			buttonOk.setIcon(Images.getIcon("org/multipage/translator/images/ok_icon.png"));
			buttonCancel.setIcon(Images.getIcon("org/multipage/translator/images/cancel_icon.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On select all.
	 */
	@SuppressWarnings("unused")
	private void selectAll() {
		try {
			
			list.selectAll(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On deselect all.
	 */
	@SuppressWarnings("unused")
	private void deselectAll() {
		try {
			
			list.selectAll(false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On select default.
	 */
	@SuppressWarnings("unused")
	private void selectDefault() {
		try {
			
			// Get selected languages count.
			final int count = selectedLanguages.size();
			
			list.selectObject(new CheckBoxCallback<Language>() {
				// Match object.
				@Override
				public boolean matches(Language language) {
					
					try {
						return isDefaultSelection(language.id);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return false;
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
