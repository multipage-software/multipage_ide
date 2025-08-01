/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;

import org.multipage.gui.FoundAttr;
import org.multipage.gui.Images;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.TextFieldEx;
import org.multipage.gui.Utility;
import org.multipage.util.Safe;

/**
 * Dialog that displays parameters for searching.
 * @author vakol
 *
 */
public class SearchSlotDialog extends JDialog {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * States.
	 */
	private static String searchText;
	private static boolean isCaseSensitive;
	private static boolean isWholeWords;
	private static boolean isShowCount;
	private static Rectangle bounds;
	private static int radioButtonIndex;

	/**
	 * Set default dialog states.
	 */
	public static void setDefaultData() {
		
		searchText = "";
		isCaseSensitive = false;
		isWholeWords = false;
		isShowCount = false;
		bounds = new Rectangle();
		radioButtonIndex = 1;
	}

	/**
	 * Read dialog states.
	 * @param inputStream
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static void serializeData(StateInputStream inputStream)
			throws IOException, ClassNotFoundException {
		
		searchText = inputStream.readUTF();
		isCaseSensitive = inputStream.readBoolean();
		isWholeWords = inputStream.readBoolean();
		isShowCount = inputStream.readBoolean();
		bounds = Utility.readInputStreamObject(inputStream, Rectangle.class);
		radioButtonIndex = Utility.readInputStreamObject(inputStream, Integer.class);
	}

	/**
	 * Write dialog states.
	 * @param outputStream
	 * @throws IOException 
	 */
	public static void serializeData(StateOutputStream outputStream)
			throws IOException {
		
		outputStream.writeUTF(searchText);
		outputStream.writeBoolean(isCaseSensitive);
		outputStream.writeBoolean(isWholeWords);
		outputStream.writeBoolean(isShowCount);
		outputStream.writeObject(bounds);
		outputStream.writeObject(radioButtonIndex);
	}
	
	/**
	 * Reset flag.
	 */
	private boolean reset = false;

	/**
	 * Confirm flag.
	 */
	private boolean confirm = false;

	// $hide<<$
	/**
	 * Components.
	 */
	private JLabel labelText;
	private TextFieldEx text;
	private JCheckBox checkCaseSensitive;
	private JCheckBox checkWholeWords;
	private JButton buttonFind;
	private JCheckBox checkShowCount;
	private JRadioButton radioValues;
	private JRadioButton radioNames;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private final JRadioButton radioDescriptions = new JRadioButton("org.multipage.generator.textSearchProviderDescriptions");
	private JButton buttonReset;

	/**
	 * Create dialog.
	 * @param component
	 * @return
	 */
	public static SearchSlotDialog createDialog(String title, Component component) {
		
		try {
			return new SearchSlotDialog(title, component);
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
	public SearchSlotDialog(String title, Component component) {
		super(Utility.findWindow(component), ModalityType.DOCUMENT_MODAL);
		
		try {
			// Initialize components.
			initComponents();
			// $hide>>$
			setTitle(title);
			postCreate();
			// $hide<<$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Show modal dialog.
	 */
	public void showModal() {
		try {
			
			reset = false;
			confirm = false;
			
			setVisible(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
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
		setBounds(100, 100, 320, 304);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		labelText = new JLabel();
		springLayout.putConstraint(SpringLayout.NORTH, labelText, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, labelText, 10, SpringLayout.WEST, getContentPane());
		labelText.setText("org.multipage.generator.textSearchStringLabel2");
		getContentPane().add(labelText);
		
		text = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, text, 6, SpringLayout.SOUTH, labelText);
		springLayout.putConstraint(SpringLayout.WEST, text, 0, SpringLayout.WEST, labelText);
		text.setForeground(Color.RED);
		getContentPane().add(text);
		
		checkCaseSensitive = new JCheckBox();
		springLayout.putConstraint(SpringLayout.NORTH, checkCaseSensitive, 6, SpringLayout.SOUTH, text);
		springLayout.putConstraint(SpringLayout.WEST, checkCaseSensitive, 0, SpringLayout.WEST, labelText);
		checkCaseSensitive.setText("org.multipage.generator.textCaseSensitive");
		getContentPane().add(checkCaseSensitive);
		
		checkWholeWords = new JCheckBox();
		springLayout.putConstraint(SpringLayout.NORTH, checkWholeWords, 6, SpringLayout.SOUTH, checkCaseSensitive);
		springLayout.putConstraint(SpringLayout.WEST, checkWholeWords, 0, SpringLayout.WEST, labelText);
		checkWholeWords.setText("org.multipage.generator.textWholeWords");
		getContentPane().add(checkWholeWords);
		
		buttonFind = new JButton("org.multipage.generator.textFind");
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
		
		checkShowCount = new JCheckBox("org.multipage.generator.textShowNumberResults");
		springLayout.putConstraint(SpringLayout.WEST, checkShowCount, 34, SpringLayout.EAST, checkWholeWords);
		springLayout.putConstraint(SpringLayout.SOUTH, checkShowCount, 0, SpringLayout.SOUTH, checkWholeWords);
		springLayout.putConstraint(SpringLayout.EAST, checkShowCount, -34, SpringLayout.EAST, getContentPane());
		getContentPane().add(checkShowCount);
		
		radioValues = new JRadioButton("org.multipage.generator.textSearchSlotValues");
		springLayout.putConstraint(SpringLayout.WEST, radioDescriptions, 0, SpringLayout.WEST, radioValues);
		buttonGroup.add(radioValues);
		springLayout.putConstraint(SpringLayout.NORTH, radioValues, 16, SpringLayout.SOUTH, checkWholeWords);
		springLayout.putConstraint(SpringLayout.WEST, radioValues, 85, SpringLayout.WEST, getContentPane());
		getContentPane().add(radioValues);
		
		radioNames = new JRadioButton("org.multipage.generator.textSearchSlotNames");
		springLayout.putConstraint(SpringLayout.NORTH, radioDescriptions, 6, SpringLayout.SOUTH, radioNames);
		buttonGroup.add(radioNames);
		springLayout.putConstraint(SpringLayout.NORTH, radioNames, 6, SpringLayout.SOUTH, radioValues);
		springLayout.putConstraint(SpringLayout.WEST, radioNames, 0, SpringLayout.WEST, radioValues);
		getContentPane().add(radioNames);
		buttonGroup.add(radioDescriptions);
		
		getContentPane().add(radioDescriptions);
		
		buttonReset = new JButton("org.multipage.generator.textResetSearch");
		springLayout.putConstraint(SpringLayout.NORTH, buttonReset, 0, SpringLayout.NORTH, buttonFind);
		springLayout.putConstraint(SpringLayout.EAST, buttonReset, -6, SpringLayout.WEST, buttonFind);
		buttonReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onReset();
			}
		});
		buttonReset.setPreferredSize(new Dimension(80, 25));
		buttonReset.setMargin(new Insets(0, 0, 0, 0));
		getContentPane().add(buttonReset);
	}
	
	/**
	 * On reset.
	 */
	protected void onReset() {
		try {
			
			// Set flag.
			reset = true;
			saveDialog();
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
		try {
			
			saveDialog();
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
			
			saveDialog();
			confirm = true;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
		dispose();		
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
			checkShowCount.setSelected(isShowCount);
			Utility.setSelected(buttonGroup, radioButtonIndex);
			if (!bounds.isEmpty()) {
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
			isShowCount = checkShowCount.isSelected();
			bounds = getBounds();
			radioButtonIndex = Utility.getSelected(buttonGroup);
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
			
			buttonFind.setIcon(Images.getIcon("org/multipage/generator/images/search_icon.png"));
			buttonReset.setIcon(Images.getIcon("org/multipage/gui/images/cancel_icon.png"));
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
			Utility.localize(buttonReset);
			Utility.localize(checkShowCount);
			Utility.localize(radioNames);
			Utility.localize(radioValues);
			Utility.localize(radioDescriptions);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get found attributes.
	 * @return
	 */
	public FoundAttr getFoundAttr() {
		
		try {
			return new FoundAttr(text.getText(), checkCaseSensitive.isSelected(),
					checkWholeWords.isSelected());
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * @return the confirm
	 */
	public boolean isConfirmed() {
		
		return confirm;
	}
	
	/**
	 * Show result coount.
	 */
	public boolean showCount() {
		
		try {
			return checkShowCount.isSelected();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Get "search in values" flag.
	 * @return
	 */
	public boolean isSearchInValues() {
		
		try {
			return radioValues.isSelected();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Get search in values flag.
	 * @return
	 */
	public boolean getSearchInValues() {
		
		try {
			return radioValues.isSelected();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Get search in descriptions flag.
	 * @return
	 */
	public boolean isSearchInDescriptions() {
		
		try {
			return radioDescriptions.isSelected();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Return reset flag.
	 * @return
	 */
	public boolean getResetFlag() {
		
		return reset;
	}
}
