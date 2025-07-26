/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.LinkedList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Dialog that displays editor for find and replace values.
 * @author vakol
 *
 */
public class FindReplaceDialog extends JDialog {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Maximum combo box items.
	 */
	private static final int maximumComboItems = 20;
	
	/**
	 * Text component.
	 */
	private JTextComponent textComponent;

	/**
	 * Old selection color.
	 */
	private Color oldSelectionColor;

	/**
	 * Old selected text color.
	 */
	private Color oldSelectedTextColor;

	/**
	 * An instance of the private subclass of the default highlight painter
	 */
	private Highlighter.HighlightPainter findHighlightPainter = new FindHighlightPainter(new Color(255, 100, 100));

	/**
	 * List of found items.
	 */
	private static LinkedList<String> listFoundItems = new LinkedList<String>();

	/**
	 * List of replaced items.
	 */
	private static LinkedList<String> listReplacedItems = new LinkedList<String>();

	/**
	 * Text in find editor.
	 */
	private static String textInFindEditor = "";

	/**
	 * Text in replace editor.
	 */
	private static String textInReplaceEditor = "";
	
	/**
	 * Dialog bounds.
	 */
	private static Rectangle bounds = new Rectangle();
	
	/**
	 * Set default data.
	 */
	public static void setDefaultData() {
		
	}

	/**
	 * Read data.
	 * @param inputStream
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static void serializeData(StateInputStream inputStream)
			throws ClassNotFoundException, IOException {
		
		listFoundItems = Utility.readInputStreamObject(inputStream, listFoundItems.getClass());
		listReplacedItems = Utility.readInputStreamObject(inputStream, listReplacedItems.getClass());
		textInFindEditor = inputStream.readUTF();
		textInReplaceEditor = inputStream.readUTF();
		bounds = Utility.readInputStreamObject(inputStream, bounds.getClass());
	}
	
	/**
	 * Write data.
	 * @param outputStream
	 * @throws IOException 
	 */
	public static void serializeData(StateOutputStream outputStream)
			throws IOException {
		
		outputStream.writeObject(listFoundItems);
		outputStream.writeObject(listReplacedItems);
		outputStream.writeUTF(textInFindEditor);
		outputStream.writeUTF(textInReplaceEditor);
		outputStream.writeObject(bounds);
	}

	// $hide<<$
	/**
	 * Components.
	 */
	private JLabel labelFind;
	private JComboBox<String> comboFind;
	private JComboBox comboReplace;
	private JLabel labelReplaceWith;
	private JLabel labelDirection;
	private JRadioButton buttonForward;
	private JRadioButton buttonBackward;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JLabel labelOptions;
	private JCheckBox buttonCaseSensitive;
	private JCheckBox buttonWholeWord;
	private JSeparator separator;
	private JButton buttonClose;
	private JButton buttonReplaceAll;
	private JButton buttonReplaceFind;
	private JButton buttonFind;
	private JButton buttonReplace;
	private JLabel labelInformation;

	/**
	 * Create the dialog.
	 * @param parentWindow 
	 */
	public FindReplaceDialog(Window parentWindow, JTextComponent textComponent) {
		super(parentWindow, ModalityType.MODELESS);
		
		try {
			// Initialize components.
			initComponents();
			// $hide>>$
			// Post creation.
			postCreation(textComponent);
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
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				onComponentShown();
			}
		});
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeWindow();
			}
		});
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		setTitle("org.multipage.gui.textFindReplaceDialog");
		setBounds(100, 100, 331, 319);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		labelFind = new JLabel("org.multipage.gui.textFindDialogFind");
		springLayout.putConstraint(SpringLayout.NORTH, labelFind, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, labelFind, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(labelFind);
		
		comboFind = new JComboBox();
		comboFind.setEditable(true);
		springLayout.putConstraint(SpringLayout.NORTH, comboFind, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, comboFind, -224, SpringLayout.EAST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, comboFind, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(comboFind);
		
		comboReplace = new JComboBox();
		comboReplace.setEditable(true);
		springLayout.putConstraint(SpringLayout.NORTH, comboReplace, 6, SpringLayout.SOUTH, comboFind);
		springLayout.putConstraint(SpringLayout.WEST, comboReplace, 0, SpringLayout.WEST, comboFind);
		springLayout.putConstraint(SpringLayout.EAST, comboReplace, 0, SpringLayout.EAST, comboFind);
		getContentPane().add(comboReplace);
		
		labelReplaceWith = new JLabel("org.multipage.gui.textFindDialogReplaceWith");
		springLayout.putConstraint(SpringLayout.NORTH, labelReplaceWith, 0, SpringLayout.NORTH, comboReplace);
		springLayout.putConstraint(SpringLayout.WEST, labelReplaceWith, 0, SpringLayout.WEST, labelFind);
		getContentPane().add(labelReplaceWith);
		
		labelDirection = new JLabel("org.multipage.gui.textFindDialogDirection");
		springLayout.putConstraint(SpringLayout.WEST, labelDirection, 0, SpringLayout.WEST, labelFind);
		getContentPane().add(labelDirection);
		
		buttonForward = new JRadioButton("org.multipage.gui.textFindDialogForward");
		springLayout.putConstraint(SpringLayout.SOUTH, labelDirection, -6, SpringLayout.NORTH, buttonForward);
		springLayout.putConstraint(SpringLayout.WEST, buttonForward, 0, SpringLayout.WEST, labelFind);
		buttonForward.setSelected(true);
		buttonGroup.add(buttonForward);
		getContentPane().add(buttonForward);
		
		buttonBackward = new JRadioButton("org.multipage.gui.textFindDialogBackward");
		springLayout.putConstraint(SpringLayout.NORTH, buttonBackward, 129, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, buttonForward, -6, SpringLayout.NORTH, buttonBackward);
		springLayout.putConstraint(SpringLayout.WEST, buttonBackward, 0, SpringLayout.WEST, labelFind);
		buttonGroup.add(buttonBackward);
		getContentPane().add(buttonBackward);
		
		labelOptions = new JLabel("org.multipage.gui.textFindDialogOptions");
		springLayout.putConstraint(SpringLayout.NORTH, labelOptions, 0, SpringLayout.NORTH, labelDirection);
		springLayout.putConstraint(SpringLayout.WEST, labelOptions, 49, SpringLayout.EAST, labelDirection);
		getContentPane().add(labelOptions);
		
		buttonCaseSensitive = new JCheckBox("org.multipage.gui.textFindDialogCaseSensitive");
		springLayout.putConstraint(SpringLayout.NORTH, buttonCaseSensitive, 0, SpringLayout.NORTH, buttonForward);
		springLayout.putConstraint(SpringLayout.WEST, buttonCaseSensitive, 0, SpringLayout.WEST, labelOptions);
		getContentPane().add(buttonCaseSensitive);
		
		buttonWholeWord = new JCheckBox("org.multipage.gui.textFindDialogWholeWord");
		springLayout.putConstraint(SpringLayout.NORTH, buttonWholeWord, 0, SpringLayout.NORTH, buttonBackward);
		springLayout.putConstraint(SpringLayout.WEST, buttonWholeWord, 0, SpringLayout.WEST, labelOptions);
		getContentPane().add(buttonWholeWord);
		
		separator = new JSeparator();
		springLayout.putConstraint(SpringLayout.NORTH, separator, 18, SpringLayout.SOUTH, buttonBackward);
		springLayout.putConstraint(SpringLayout.WEST, separator, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, separator, 20, SpringLayout.SOUTH, buttonBackward);
		springLayout.putConstraint(SpringLayout.EAST, separator, 315, SpringLayout.WEST, getContentPane());
		getContentPane().add(separator);
		
		buttonClose = new JButton("org.multipage.gui.textFindDialogClose");
		buttonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeWindow();
			}
		});
		buttonClose.setPreferredSize(new Dimension(100, 25));
		buttonClose.setMargin(new Insets(0, 0, 0, 0));
		springLayout.putConstraint(SpringLayout.SOUTH, buttonClose, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, buttonClose, 0, SpringLayout.EAST, comboFind);
		getContentPane().add(buttonClose);
		
		buttonReplaceAll = new JButton("org.multipage.gui.textFindDialogReplaceAll");
		buttonReplaceAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				replaceAll();
			}
		});
		buttonReplaceAll.setPreferredSize(new Dimension(100, 25));
		buttonReplaceAll.setMargin(new Insets(0, 0, 0, 0));
		springLayout.putConstraint(SpringLayout.SOUTH, buttonReplaceAll, -6, SpringLayout.NORTH, buttonClose);
		springLayout.putConstraint(SpringLayout.EAST, buttonReplaceAll, 0, SpringLayout.EAST, comboFind);
		getContentPane().add(buttonReplaceAll);
		
		buttonReplaceFind = new JButton("org.multipage.gui.textFindDialogReplaceFind");
		buttonReplaceFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				replaceFind();
			}
		});
		buttonReplaceFind.setPreferredSize(new Dimension(100, 25));
		buttonReplaceFind.setMargin(new Insets(0, 0, 0, 0));
		springLayout.putConstraint(SpringLayout.SOUTH, buttonReplaceFind, -6, SpringLayout.NORTH, buttonReplaceAll);
		springLayout.putConstraint(SpringLayout.EAST, buttonReplaceFind, 0, SpringLayout.EAST, comboFind);
		getContentPane().add(buttonReplaceFind);
		
		buttonFind = new JButton("org.multipage.gui.textFindDialogFind");
		buttonFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				find(true);
			}
		});
		buttonFind.setMargin(new Insets(0, 0, 0, 0));
		buttonFind.setPreferredSize(new Dimension(100, 25));
		springLayout.putConstraint(SpringLayout.SOUTH, buttonFind, 0, SpringLayout.SOUTH, buttonReplaceFind);
		springLayout.putConstraint(SpringLayout.EAST, buttonFind, -6, SpringLayout.WEST, buttonReplaceFind);
		getContentPane().add(buttonFind);
		
		buttonReplace = new JButton("org.multipage.gui.textFindDialogReplace");
		buttonReplace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				replace();
			}
		});
		buttonReplace.setMargin(new Insets(0, 0, 0, 0));
		buttonReplace.setPreferredSize(new Dimension(100, 25));
		springLayout.putConstraint(SpringLayout.NORTH, buttonReplace, 0, SpringLayout.NORTH, buttonReplaceAll);
		springLayout.putConstraint(SpringLayout.EAST, buttonReplace, -6, SpringLayout.WEST, buttonReplaceAll);
		getContentPane().add(buttonReplace);
		
		labelInformation = new JLabel("information");
		springLayout.putConstraint(SpringLayout.WEST, labelInformation, 0, SpringLayout.WEST, labelFind);
		springLayout.putConstraint(SpringLayout.SOUTH, labelInformation, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, labelInformation, -6, SpringLayout.WEST, buttonClose);
		getContentPane().add(labelInformation);
	}

	/**
	 * Close window.
	 */
	public void closeWindow() {
		
		try {
			
			Utility.removeFindHighlights(textComponent);
			restoreSelectionColor();
			
			saveDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
		dispose();
	}

	/**
	 * On window opened.
	 */
	protected void onComponentShown() {
		try {
			
			// Set selection color.
			setSelectionColor();
			highlightText();
			
			loadDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Post creation.
	 */
	public void postCreation(JTextComponent textComponent) {
		try {
			
			this.textComponent = textComponent;
			// Localize components.
			localize();
			// Set icons.
			setIcons();
			// Center dialog.
			Utility.centerOnScreen(this);
			// Reset information text.
			labelInformation.setText("");
			// Set closing with ESC.
			setClosingWithEsc();
			// Set default button.
			JRootPane rootPane = SwingUtilities.getRootPane(buttonFind); 
			rootPane.setDefaultButton(buttonFind);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load dialog content and state.
	 */
	private void loadDialog() {
		try {
			
			Utility.setList(comboFind, listFoundItems);
			Utility.setList(comboReplace, listReplacedItems);
			
			comboFind.getEditor().setItem(textInFindEditor);
			comboReplace.getEditor().setItem(textInReplaceEditor);
			
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
	 * Save dialog content and state.
	 */
	private void saveDialog() {
		try {
			
			listFoundItems = Utility.getList(comboFind);
			listReplacedItems = Utility.getList(comboReplace);
			
			textInFindEditor = (String) comboFind.getEditor().getItem();
			textInReplaceEditor = (String) comboReplace.getEditor().getItem();
			
			bounds = getBounds();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set closing with ESC.
	 */
	private void setClosingWithEsc() {
		
		final FindReplaceDialog dialog = this;
		try {
			
			 ActionListener escListener = new ActionListener() {
		        @Override
		        public void actionPerformed(ActionEvent e) {
		        	try {
						
						dialog.closeWindow();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
		        }
		    };
	
		    dialog.getRootPane().registerKeyboardAction(escListener,
		            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
		            JComponent.WHEN_IN_FOCUSED_WINDOW);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On find text changed.
	 */
	protected void onFindTextChange() {
		try {
			
			highlightText();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set selection color.
	 */
	private void setSelectionColor() {
		try {
			
			oldSelectionColor = textComponent.getSelectionColor();
			oldSelectedTextColor = textComponent.getSelectedTextColor();
			textComponent.setSelectionColor(Color.BLUE);
			textComponent.setSelectedTextColor(Color.WHITE);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Restore selection color.
	 */
	private void restoreSelectionColor() {
		try {
			
			textComponent.setSelectionColor(oldSelectionColor);
			textComponent.setSelectedTextColor(oldSelectedTextColor);
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
			Utility.localize(labelFind);
			Utility.localize(labelReplaceWith);
			Utility.localize(labelDirection);
			Utility.localize(buttonForward);
			Utility.localize(buttonBackward);
			Utility.localize(labelOptions);
			Utility.localize(buttonCaseSensitive);
			Utility.localize(buttonWholeWord);
			Utility.localize(buttonFind);
			Utility.localize(buttonReplaceFind);
			Utility.localize(buttonReplace);
			Utility.localize(buttonReplaceAll);
			Utility.localize(buttonClose);
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
			
			setIconImage(Images.getImage("org/multipage/gui/images/search_icon.png"));
			buttonFind.setIcon(Images.getIcon("org/multipage/gui/images/find_icon.png"));
			buttonReplace.setIcon(Images.getIcon("org/multipage/gui/images/replace_icon.png"));
			buttonClose.setIcon(Images.getIcon("org/multipage/gui/images/cancel_icon.png"));
			buttonReplaceFind.setIcon(Images.getIcon("org/multipage/gui/images/find_icon.png"));
			buttonReplaceAll.setIcon(Images.getIcon("org/multipage/gui/images/replace_icon.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Highlight text.
	 */
	private void highlightText(String text) {
		try {
			
			// Highlight text.
			FoundAttr foundAttr = new FoundAttr(text, buttonCaseSensitive.isSelected(),
					buttonWholeWord.isSelected());
			Utility.highlight(textComponent, foundAttr, findHighlightPainter);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Highlight text.
	 */
	private void highlightText() {
		try {
			
			// Get text.
			String text = getPatternText();
			// Highlight text.
			highlightText(text);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Find text.
	 */
	protected boolean find(boolean info) {
		
		try {
			boolean successful;
			
			// Get pattern.
			String pattern = getPatternText();
			boolean caseSensitive = buttonCaseSensitive.isSelected();
			boolean wholeWord = buttonWholeWord.isSelected();
			
			// Select text forward.
			if (buttonForward.isSelected()) {
				successful = selectTextForward(pattern, caseSensitive, wholeWord);
			}
			else {
				successful = selectTextBackward(pattern, caseSensitive, wholeWord);
			}
	
			highlightText();
			
			// Add pattern to the combo box list.
			addTextToCombo(comboFind, pattern);
			// Set information text.
			if (info) {
				labelInformation.setText(successful ? "" :
					Resources.getString("org.multipage.gui.messageTextNotFound"));
			}
	
			return successful;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Replace text.
	 */
	protected void replace() {
		try {
			
			// Get pattern.
			String pattern = getPatternText();
			if (pattern.isEmpty()) {
				return;
			}
			// Get replace text.
			String replaceText = getReplaceText();
			
			// Get selected text position.
			int selectionStart = textComponent.getSelectionStart();
			int selectionEnd = textComponent.getSelectionEnd();
			int selectionLength = selectionEnd - selectionStart;
			
			if (selectionLength > 0) {
				
				// Replace text.
				textComponent.replaceSelection(replaceText);
				// Select the new text.
				textComponent.setSelectionStart(selectionStart);
				textComponent.setSelectionEnd(selectionStart + replaceText.length());
			}
			
			addTextToCombo(comboReplace, replaceText);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Replace and find text.
	 */
	protected void replaceFind() {
		try {
			
			replace();
			find(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Replace all texts.
	 */
	protected void replaceAll() {
		try {
			
			// Reset caret position.
			textComponent.setCaretPosition(0);
			// Replace texts.
			while (find(false)) {
				replace();
			}
			// Highlight text.
			highlightText(getReplaceText());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Add text to combo box.
	 * @param comboBox
	 * @param text
	 */
	private static void addTextToCombo(JComboBox comboBox, String text) {
		try {
			
			int itemCount = comboBox.getItemCount();
			
			for (int index = 0; index < itemCount; index++) {
				Object item = comboBox.getItemAt(index);
				if (!(item instanceof String)) {
					
					continue;
				}
	
				String itemText = (String) item;
			
				if (itemText.equals(text)) {
					if (index != 0) {
						
						// Move text to the beginning.
						comboBox.removeItemAt(index);
						comboBox.insertItemAt(item, 0);
					}
					return;
				}
			}
			
			// Add item to the beginning.
			comboBox.insertItemAt(text, 0);
			
			// Remove item.
			if (itemCount > maximumComboItems) {
				comboBox.removeItemAt(itemCount);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set combo text.
	 */
	public void setFindText(String text) {
		try {
			
			comboFind.setSelectedItem(text);
			onFindTextChange();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Select text forward.
	 * @param pattern
	 * @param forward
	 * @param wholeWord
	 */
	private boolean selectTextForward(String pattern,
			boolean caseSensitive, boolean wholeWord) {
		
		try {
			// If the pattern is empty, exit the method.
			if (pattern.isEmpty()) {
				return false;
			}
	
			// Get document and the text length.
			Document document = textComponent.getDocument();
			int textLength = document.getLength();
			
			// If the document is empty, exit the method.
			if (textLength == 0) {
				return false;
			}
			
	        String originalText;
	        String text;
	        
			try {
				originalText = document.getText(0, document.getLength());
			}
			catch (BadLocationException e) {
				return false;
			}
			
			// If not case sensitive, covert texts to upper case.
			if (!caseSensitive) {
				text = originalText.toUpperCase();
				pattern = pattern.toUpperCase();
			}
			else {
				text = originalText;
			}
	
			
	 		// Get cursor position.
	        int searchPosition = textComponent.getCaretPosition();
	        // Get pattern length.
	        int patternLength = pattern.length();
	        
	        boolean successful = false;
	        
	        while (searchPosition <= textLength) {
	
	        	// Find pattern begin.
	        	int patternStart = text.indexOf(pattern, searchPosition);
	        	if (patternStart == -1) {
	        		break;
	        	}
	        	// Get pattern end.
	        	int patternEnd = patternStart + patternLength;
	        	boolean selectAndExit;
	        	
	        	if (wholeWord) {
	        		selectAndExit = Utility.isWordStart(originalText, patternStart) &&
	        				Utility.isWordEnd(originalText, patternEnd);
	        	}
	        	else {
	        		selectAndExit = true;
	        	}
	        	
	        	// Select text and exit.
	        	if (selectAndExit) {
	        		
	        		textComponent.setSelectionStart(patternStart);
	        		textComponent.setSelectionEnd(patternEnd);
	        		// Ensure selection visible.
	        		Utility.ensureTextVisible(textComponent, patternEnd);
	
	        		successful = true;
	        		break;
	        	}
	        	
	        	// Set new search position.
	        	searchPosition = patternEnd;
	        }
	        
	        return successful;
	    }
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Select text backward.
	 * @param meansText
	 * @param caseSensitive
	 * @param wholeWord
	 */
	private boolean selectTextBackward(String pattern, boolean caseSensitive,
			boolean wholeWord) {
		
		try {
			// If the pattern is empty, exit the method.
			if (pattern.isEmpty()) {
				return false;
			}
	
			// Get document and the text length.
			Document document = textComponent.getDocument();
			int textLength = document.getLength();
			
			// If the document is empty, exit the method.
			if (textLength == 0) {
				return false;
			}
			
	        String originalText;
	        String text;
	        
			try {
				originalText = document.getText(0, document.getLength());
			}
			catch (BadLocationException e) {
				return false;
			}
			
			// If not case sensitive, covert texts to upper case.
			if (!caseSensitive) {
				text = originalText.toUpperCase();
				pattern = pattern.toUpperCase();
			}
			else {
				text = originalText;
			}
	
			String selectedText = textComponent.getSelectedText();
			
	 		// Get cursor position.
	        int searchPosition = (selectedText == null ? textComponent.getCaretPosition() :
	        	textComponent.getSelectionStart()) - 1;
	        // Get pattern length.
	        int patternLength = pattern.length();
	        
	        boolean successful = false;
	        
	        while (searchPosition >= 0) {
	        	
	        	// Find pattern start.
	        	int patternStart = text.lastIndexOf(pattern, searchPosition);
	        	if (patternStart == -1) {
	        		break;
	        	}
	        	// Get pattern end.
	        	int patternEnd = patternStart + patternLength;
	        	boolean selectAndExit;
	        	
	        	if (wholeWord) {
	        		selectAndExit = Utility.isWordStart(originalText, patternStart) &&
	        				Utility.isWordEnd(originalText, patternEnd);
	        	}
	        	else {
	        		selectAndExit = true;
	        	}
	        	
	        	// Select text and exit.
	        	if (selectAndExit) {
	        		
	        		textComponent.setSelectionStart(patternStart);
	        		textComponent.setSelectionEnd(patternEnd);
	        		successful = true;
	        		break;
	        	}
	        	
	        	// Set new search position.
	        	searchPosition = patternStart - 1;
	        }
	        
	        return successful;
	    }
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Get find text.
	 * @return
	 */
	private String getPatternText() {
		
		try {
			return getComboText(comboFind);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get replace text.
	 * @return
	 */
	private String getReplaceText() {
		
		try {
			return getComboText(comboReplace);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get combo text.
	 * @param combo
	 * @return
	 */
	private static String getComboText(JComboBox combo) {
		
		try {
			Object selected = combo.getSelectedItem();
			if (selected instanceof String) {
				return (String) selected;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Disables GUI components used for text replacement.
	 */
	public void setOnlyFind() {
		
		labelReplaceWith.setEnabled(false);
		comboReplace.setEnabled(false);
		buttonReplaceFind.setEnabled(false);
		buttonReplace.setEnabled(false);
		buttonReplaceAll.setEnabled(false);
	}
}
