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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;

import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Dialog for searching text.
 * @author vakol
 *
 */
public class SearchTextDialog extends JDialog {

	// $hide>>$
	
	/**
	 * Search parameters.
	 * @author
	 *
	 */
	public class Parameters {

		/**
		 * Parameters.
		 */
		private String searchedText;
		private boolean forward;
		private boolean caseSensitive;
		private boolean wholeWords;
		
		/**
		 * Access methods.
		 * @return
		 */
		public boolean isForward() {
			return forward;
		}
		public String getSearchedText() {
			return searchedText;
		}
		public boolean isCaseSensitive() {
			return caseSensitive;
		}
		public boolean isWholeWords() {
			return wholeWords;
		}
	}
	
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * States.
	 */
	private static String searchText = "";
	private static boolean isCaseSensitive = false;
	private static boolean isWholeWords = false;
	private static Rectangle bounds = null;

	/**
	 * Parameters.
	 */
	private Parameters parameters;
	
	/**
	 * Optional lambda function called when the dialog OK is clicked.
	 */
	private Consumer<Parameters> closeLambda;
	
	/**
	 * Optional lambda function called when the dialog Cancel is clicked.
	 */
	private Runnable cancelLambda;
	
	/**
	 * Is true when the dialog is modeless.
	 */
	private boolean isModeless = false;

	// $hide<<$
	/**
	 * Components.
	 */
	private JLabel labelText;
	private TextFieldEx text;
	private JCheckBox checkCaseSensitive;
	private JCheckBox checkWholeWords;
	private JButton buttonFind;
	private JRadioButton radioForward;
	private JRadioButton radioBackward;
	private final ButtonGroup buttonGroup = new ButtonGroup();

	/**
	 * Show dialog.
	 * @param titleId 
	 * @param enumerationEditorPanel
	 * @return
	 */
	public static Parameters showDialog(Component parentComponent, String titleId) {
		
		try {
			SearchTextDialog dialog = new SearchTextDialog(parentComponent, false);
			dialog.setTitle(Resources.getString(titleId));
			dialog.setVisible(true);
			
			return dialog.parameters;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Show dialog.
	 * @param isModeless - true if the dialog is modeless or false if it is modal
	 * @param okLambda - can be null or a lambda function that is invoked on OK button.
	 * @param cancelLambda = can be null or a lambda function that is invoked on Cancel button.
	 * @return
	 */
	public static SearchTextDialog showDialog(Component parentComponent, String titleId, boolean isModeless,
			Consumer<Parameters> okLambda, Runnable cancelLambda) {
		
		try {
			// Create new dialog window.
			SearchTextDialog dialog = new SearchTextDialog(parentComponent, isModeless);
			dialog.setTitle(Resources.getString(titleId));
			dialog.closeLambda = okLambda;
			dialog.cancelLambda = cancelLambda;
			dialog.setVisible(true);
			
			return dialog;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Create the dialog.
	 * @param title 
	 * @param component 
	 */
	public SearchTextDialog(Component component, boolean isModeless) {
		super(Utility.findWindow(component), isModeless ? ModalityType.MODELESS : ModalityType.DOCUMENT_MODAL);
		
		try {
			this.isModeless = isModeless;
			
			// Initialize components.
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
		setResizable(false);
		setMinimumSize(new Dimension(320, 200));
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("");
		setBounds(100, 100, 320, 200);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		labelText = new JLabel();
		springLayout.putConstraint(SpringLayout.NORTH, labelText, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, labelText, 10, SpringLayout.WEST, getContentPane());
		labelText.setText("org.multipage.gui.textSearchStringLabel");
		getContentPane().add(labelText);
		
		text = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, text, 6, SpringLayout.SOUTH, labelText);
		springLayout.putConstraint(SpringLayout.WEST, text, 0, SpringLayout.WEST, labelText);
		text.setForeground(Color.RED);
		getContentPane().add(text);
		
		checkCaseSensitive = new JCheckBox();
		springLayout.putConstraint(SpringLayout.NORTH, checkCaseSensitive, 6, SpringLayout.SOUTH, text);
		springLayout.putConstraint(SpringLayout.WEST, checkCaseSensitive, 0, SpringLayout.WEST, labelText);
		checkCaseSensitive.setText("org.multipage.gui.textCaseSensitive");
		getContentPane().add(checkCaseSensitive);
		
		checkWholeWords = new JCheckBox();
		springLayout.putConstraint(SpringLayout.NORTH, checkWholeWords, 6, SpringLayout.SOUTH, checkCaseSensitive);
		springLayout.putConstraint(SpringLayout.WEST, checkWholeWords, 0, SpringLayout.WEST, labelText);
		checkWholeWords.setText("org.multipage.gui.textWholeWords");
		getContentPane().add(checkWholeWords);
		
		buttonFind = new JButton("org.multipage.gui.textFind");
		buttonFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOk();
			}
		});
		buttonFind.setMargin(new Insets(0, 0, 0, 0));
		buttonFind.setPreferredSize(new Dimension(80, 25));
		springLayout.putConstraint(SpringLayout.EAST, text, 0, SpringLayout.EAST, buttonFind);
		springLayout.putConstraint(SpringLayout.SOUTH, buttonFind, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, buttonFind, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(buttonFind);
		
		radioForward = new JRadioButton("org.multipage.gui.textSearchForward");
		springLayout.putConstraint(SpringLayout.WEST, radioForward, 150, SpringLayout.WEST, getContentPane());
		radioForward.setSelected(true);
		buttonGroup.add(radioForward);
		springLayout.putConstraint(SpringLayout.NORTH, radioForward, 6, SpringLayout.SOUTH, text);
		getContentPane().add(radioForward);
		
		radioBackward = new JRadioButton("org.multipage.gui.textSearchBackward");
		buttonGroup.add(radioBackward);
		springLayout.putConstraint(SpringLayout.NORTH, radioBackward, 0, SpringLayout.NORTH, checkWholeWords);
		springLayout.putConstraint(SpringLayout.WEST, radioBackward, 0, SpringLayout.WEST, radioForward);
		getContentPane().add(radioBackward);
	}
	
	/**
	 * On cancel.
	 */
	protected void onCancel() {
		try {
			
			saveDialog();
			
			if (cancelLambda != null) {
				cancelLambda.run();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
		if (!isModeless) {
			dispose();
		}
	}

	/**
	 * On OK.
	 */
	protected void onOk() {
		try {
			
			// Set parameters.
			parameters = new Parameters();
			parameters.searchedText = text.getText();
			parameters.forward = radioForward.isSelected();
			parameters.caseSensitive = checkCaseSensitive.isSelected();
			parameters.wholeWords = checkWholeWords.isSelected();
			
			saveDialog();
			
			if (closeLambda != null) {
				closeLambda.accept(parameters);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
		if (!isModeless) {
			dispose();
		}
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			// Load dialog.
			loadDialog();
			// Localize components.
			localize();
			// Set icons.
			setIcons();
			// Initialize key strokes.
			initKeyStrokes();
			// Select text.
			selectText();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Select text.
	 */
	private void selectText() {
		try {
			
			text.selectAll();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Initialize key strokes.
	 */
	@SuppressWarnings("serial")
	private void initKeyStrokes() {
		try {
			
			text.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ok");
			text.getActionMap().put("ok", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						
						onOk();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}}
			);
			text.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "cancel");
			text.getActionMap().put("cancel", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						
						onCancel();
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
			
			text.setText(searchText);
			checkCaseSensitive.setSelected(isCaseSensitive);
			checkWholeWords.setSelected(isWholeWords);
			if (bounds != null) {
				setBounds(bounds);
			}
			else {
				Utility.centerOnScreen(this);
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
			
			searchText = text.getText();
			isCaseSensitive = checkCaseSensitive.isSelected();
			isWholeWords = checkWholeWords.isSelected();
			bounds = getBounds();
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
			
			buttonFind.setIcon(Images.getIcon("org/multipage/gui/images/search_icon.png"));
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
			Utility.localize(labelText);
			Utility.localize(checkCaseSensitive);
			Utility.localize(checkWholeWords);
			Utility.localize(buttonFind);
			Utility.localize(radioForward);
			Utility.localize(radioBackward);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
