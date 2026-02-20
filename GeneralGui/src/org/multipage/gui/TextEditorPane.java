/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.Timer;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.EditorKit;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;
import javax.swing.text.html.HTMLDocument;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;
import org.multipage.util.SimpleMethodRef;

/**
 * Panel that displays text editors.
 * @author vakol
 *
 */
public class TextEditorPane extends JPanel implements StringValueEditor {
	
	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Highlighter.
	 */
	private static final Highlighter.HighlightPainter myHighlightPainter = new FindHighlightPainter(new Color(255, 100, 100));
	
	/**
	 * Tabulator width.
	 */
	public static int tabWidth = 4;
    public static String tabWhiteSpaces = "";
	
	/**
	 * Word wrap state.
	 */
    @ProgramState(1)
	public static boolean wordWrapState;
	
	/**
	 * Static constructor.
	 */
	static {
		
		int count = tabWidth;
		while (count > 0) {
			
			tabWhiteSpaces += ' ';
			count--;
		}
	}
	
	/**
	 * Load dialog.
	 */
	protected void loadDialog() {
		try {
			
			buttonWrap.setSelected(wordWrapState);
			wrapUnwrap();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Save dialog.
	 */
	protected void saveDialog() {
		try {
			
			stopTimers();
			wordWrapState = buttonWrap.isSelected();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set default data.
	 */
	public static void setDefaultData() {

		wordWrapState = false;
	}

	/**
	 * Load data.
	 * @param inputStream
	 */
	public static void serializeData(StateInputStream inputStream)
		throws IOException, ClassNotFoundException {

		wordWrapState = inputStream.readBoolean();
	}

	public static void serializeData(StateOutputStream outputStream)
		throws IOException {
		
		outputStream.writeBoolean(wordWrapState);
	}

	/**
	 * Read only background color.
	 */
	private static final Color readOnlyBackground = new Color(230, 230, 250);
	
	/**
	 * Move line right.
	 * @param lineText
	 * @return
	 */
	public static String moveLineRight(String lineText) {
		
		try {
			StringBuffer movedText = new StringBuffer();
			
			for (int index = 0; index < tabWidth; index++) {
				movedText.append(' ');
			}
			
			movedText.append(lineText);
			
			return movedText.toString();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return lineText;
	}

	/**
	 * Move line left.
	 * @param lineText
	 * @return
	 */
	public static String moveLineLeft(String lineText) {
		
		try {
			StringBuffer movedText = new StringBuffer();
			int spacesToRemove = tabWidth;
			
			boolean isStartingWhitespace = true;
			
			for (int index = 0; index < lineText.length(); index++) {
				
				Character character = lineText.charAt(index);
				
				if (isStartingWhitespace && character != ' ') {
					isStartingWhitespace = false;
					spacesToRemove = 0;
				}
				
				if (spacesToRemove == 0) {
					movedText.append(character);
				}
				else if (spacesToRemove > 0) {
					spacesToRemove--;
				}
			}
			
			return movedText.toString();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return lineText;
	}

	/**
	 * Parent window.
	 */
	private Window parentWindow;
	
	/**
	 * Use HTML editor.
	 */
	private boolean useHtmlEditor;

	/**
	 * Text font.
	 */
	private Font textFont;

	/**
	 * Cut button.
	 */
	private JButton buttonCut;

	/**
	 * Undo button.
	 */
	private JButton buttonUndo;

	/**
	 * Redo button.
	 */
	private JButton buttonRedo;
	
	/**
	 * Reset button.
	 */
	private JComponent buttonReset;
	
	/**
	 * Undoable managers.
	 */
	private UndoManager undoManagerPlain;
	private UndoManager undoManagerHtml;

	/**
	 * Find replace dialogs.
	 */
	private FindReplaceDialog findPlainDialog;
	private FindReplaceDialog findHtmlDialog;

	/**
	 * Enable / disable word wrap button.
	 */
	private JToggleButton buttonWrap;
	
	/**
	 * Popup menus.
	 */
	private JPopupMenu popupMenuPlain;
	private JPopupMenu popupMenuHtml;
	
	/**
	 * Change flags.
	 */
	private boolean changing = false;

	/**
	 * Change listeners.
	 */
	private LinkedList<SimpleMethodRef> changeListeners = 
		new LinkedList<SimpleMethodRef>();
	
	/**
	 * Extract HTML body flag.
	 */
	private boolean extractBody = true;
	
	/**
	 * Highlight script commands timer.
	 */
	private javax.swing.Timer highlightScriptCommandsTimer;
	
	/**
	 * Lambda function that returns text hints.
	 */
	public Function<String, Function<Integer, Function<Caret, Consumer<JTextPane>>>> intellisenseLambda = null;

	/**
	 * Rich text buttons.
	 */
	private JButton buttonFont;
	private JButton buttonForeground;
	private JButton buttonBackground;
	private JToggleButton buttonBold;
	private JToggleButton buttonItalic;
	private JToggleButton buttonUnderline;
	private JToggleButton buttonStrike;
	private JToggleButton buttonSubscript;
	private JToggleButton buttonSuperscript;
	private JComboBox fontFamily;
	private JComboBox fontSize;
	private JComboBox textAlignment;

	// $hide<<$
	/**
	 * Components.
	 */
	private JToolBar toolBar;
	private JScrollPane htmlScrollPane;
	private JTextPane htmlTextPane;
	public JTabbedPane tabbedPane;
	private JPanel panelHtml;
	private JScrollPane plainScrollPane;
	private JTextPane plainTextPane;
	private JToolBar richTextToolBar;
	/**
	 * @wbp.nonvisual location=520,109
	 */
	private final JPanel panelNoWrapHtml = new JPanel();
	/**
	 * @wbp.nonvisual location=520,169
	 */
	private final JPanel panelNoWrapPlain = new JPanel();

	/**
	 * Constructor.
	 */
	public TextEditorPane(Window parentWindow, boolean useHtmlEditor) {
		
		try {
			panelNoWrapPlain.setLayout(new BorderLayout(0, 0));
			panelNoWrapHtml.setLayout(new BorderLayout(0, 0));
			
			this.parentWindow = parentWindow;
			this.useHtmlEditor = useHtmlEditor;
			// Initialize components.
			initComponents();
			// $hide>>$
			// Post creation.
			postCreation();
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
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		toolBar = new JToolBar();
		springLayout.putConstraint(SpringLayout.EAST, toolBar, 0, SpringLayout.EAST, this);
		toolBar.setFloatable(false);
		springLayout.putConstraint(SpringLayout.NORTH, toolBar, 0, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, toolBar, 0, SpringLayout.WEST, this);
		add(toolBar);
		
		tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				onTabChanged();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, tabbedPane, 0, SpringLayout.SOUTH, toolBar);
		springLayout.putConstraint(SpringLayout.WEST, tabbedPane, 0, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, tabbedPane, 0, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, tabbedPane, 0, SpringLayout.EAST, this);
		add(tabbedPane);
		
		plainScrollPane = new JScrollPane();
		plainScrollPane.setBorder(null);
		tabbedPane.addTab("org.multipage.gui.messageHtmlText", null, plainScrollPane, null);
		
		plainTextPane = new JTextPane();
		plainTextPane.setFont(new Font("Consolas", Font.PLAIN, 13));
		plainTextPane.setForeground(Color.WHITE);
		plainTextPane.setBackground(Color.BLACK);
		plainTextPane.setCaretColor(Color.WHITE); // Set caret color to white
		plainTextPane.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				onKeyReleased(e);
			}
		});
		plainTextPane.setBorder(null);
		plainTextPane.setDragEnabled(true);
		plainScrollPane.setViewportView(plainTextPane);
		
		panelHtml = new JPanel();
		tabbedPane.addTab("org.multipage.gui.messageHtmlDesign", null, panelHtml, null);
		springLayout.putConstraint(SpringLayout.NORTH, panelHtml, 24, SpringLayout.SOUTH, tabbedPane);
		springLayout.putConstraint(SpringLayout.SOUTH, panelHtml, 176, SpringLayout.SOUTH, tabbedPane);
		panelHtml.setLayout(new BorderLayout(0, 0));
		
		htmlScrollPane = new JScrollPane();
		htmlScrollPane.setBorder(null);
		panelHtml.add(htmlScrollPane, BorderLayout.CENTER);
		springLayout.putConstraint(SpringLayout.NORTH, htmlScrollPane, 48, SpringLayout.SOUTH, tabbedPane);
		springLayout.putConstraint(SpringLayout.WEST, htmlScrollPane, 0, SpringLayout.WEST, tabbedPane);
		springLayout.putConstraint(SpringLayout.SOUTH, htmlScrollPane, -20, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, htmlScrollPane, 223, SpringLayout.WEST, this);
		
		htmlTextPane = new JTextPane();
		htmlTextPane.setCaretColor(Color.WHITE); // Set caret color to white
		htmlTextPane.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				onKeyReleased(e);
			}
		});
		htmlTextPane.setBorder(null);
		htmlTextPane.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				setToolBarControls();
			}
		});
		htmlTextPane.setContentType("text/html;charset=UTF-8");
		addAncestorListener(new AncestorListener() {
			public void ancestorAdded(AncestorEvent event) {
				loadDialog();
			}
			public void ancestorMoved(AncestorEvent event) {
			}
			public void ancestorRemoved(AncestorEvent event) {
				saveDialog();
			}
		});
		
		htmlTextPane.setDragEnabled(true);
		htmlScrollPane.setViewportView(htmlTextPane);
		springLayout.putConstraint(SpringLayout.WEST, panelHtml, 34, SpringLayout.EAST, htmlScrollPane);
		springLayout.putConstraint(SpringLayout.EAST, panelHtml, 193, SpringLayout.EAST, htmlScrollPane);
		
		richTextToolBar = new JToolBar();
		panelHtml.add(richTextToolBar, BorderLayout.NORTH);
		richTextToolBar.setFloatable(false);
	}
	
	/**
	 * Post creation.
	 */
	private void postCreation() {
		try {
			
			// Initialize key strokes.
			initKeyStrokes();
			// Localize components.
			localize();
			// Set icons.
			setIcons();
			// Set tool bar.
			createToolBars();
			// Set editors.
			setEditors();
			// Set documents filter.
			setDocuments();
			// Set listeners.
			setListeners();
			// Set undoable edit.
			setUndoableEdit();
			// Create find dialog.
			createFindDialog();
			// Create popup menus.
			popupMenuPlain = new TextPopupMenu(plainTextPane);
			popupMenuHtml = new TextPopupMenu(htmlTextPane);
			// Create timers.
			createTimers();
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
			
			// Escape key.
			Action removeHighlights = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						
						Utility.removeFindHighlights(plainTextPane);
						Utility.removeFindHighlights(htmlTextPane);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}};
			KeyStroke ecsapeKey = KeyStroke.getKeyStroke("ESCAPE");
			
			
			plainTextPane.getInputMap().put(ecsapeKey, "removeHighlights");
			plainTextPane.getActionMap().put("removeHighlights", removeHighlights);
			htmlTextPane.getInputMap().put(ecsapeKey, "removeHighlights");
			htmlTextPane.getActionMap().put("removeHighlights", removeHighlights);
			
			// CTRL + F key.
			Action find = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						
						findText();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}};
			KeyStroke findKey = KeyStroke.getKeyStroke("control F");
			
			plainTextPane.getInputMap().put(findKey, "find");
			plainTextPane.getActionMap().put("find", find);
			htmlTextPane.getInputMap().put(findKey, "find");
			htmlTextPane.getActionMap().put("find", find);
			
			// Shift + TAB key.
			Action moveRight = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						
						moveTextRight();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			};
			KeyStroke moveRightKey = KeyStroke.getKeyStroke("shift TAB");
			
			plainTextPane.getInputMap().put(moveRightKey, "moveRight");
			plainTextPane.getActionMap().put("moveRight", moveRight);
			htmlTextPane.getInputMap().put(moveRightKey, "moveRight");
			htmlTextPane.getActionMap().put("moveRight", moveRight);
			
			
			// CTRL + B key.
			Action moveLeft = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						
						moveTextLeft();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			};
			KeyStroke moveLeftKey = KeyStroke.getKeyStroke("control B");
			
			plainTextPane.getInputMap().put(moveLeftKey, "moveLeft");
			plainTextPane.getActionMap().put("moveLeft", moveLeft);
			htmlTextPane.getInputMap().put(moveLeftKey, "moveLeft");
			htmlTextPane.getActionMap().put("moveLeft", moveLeft);
			
			// Shift + bacspace.
			Action backLeft = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						
						backTextLeft();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			};
			KeyStroke backLeftKey = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, InputEvent.SHIFT_DOWN_MASK);
			
			plainTextPane.getInputMap().put(backLeftKey, "backLeft");
			plainTextPane.getActionMap().put("backLeft", backLeft);
			htmlTextPane.getInputMap().put(backLeftKey, "backLeft");
			htmlTextPane.getActionMap().put("backLeft", backLeft);
			
			
			// CTRL + Z key.
			Action undo = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						
						undoText();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			};
			KeyStroke undoKey = KeyStroke.getKeyStroke("control Z");
			
			plainTextPane.getInputMap().put(undoKey, "undo");
			plainTextPane.getActionMap().put("undo", undo);
			htmlTextPane.getInputMap().put(undoKey, "undo");
			htmlTextPane.getActionMap().put("undo", undo);
			
			
			// CTRL + Y key.
			Action redo = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						
						redoText();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			};
			KeyStroke redoKey = KeyStroke.getKeyStroke("control Y");
			
			plainTextPane.getInputMap().put(redoKey, "redo");
			plainTextPane.getActionMap().put("redo", redo);
			htmlTextPane.getInputMap().put(redoKey, "redo");
			htmlTextPane.getActionMap().put("redo", redo);
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
			
			Utility.localize(tabbedPane);
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
			
			// Set tab icons.
			tabbedPane.setIconAt(0, Images.getIcon("org/multipage/gui/images/html_icon.png"));
			tabbedPane.setIconAt(1, Images.getIcon("org/multipage/gui/images/text_icon.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set tool bars.
	 */
	private void createToolBars() {
		try {
			
			buttonCut = ToolBarKit.addToolBarButton(toolBar, "org/multipage/gui/images/cut_icon.png",
					"org.multipage.gui.tooltipCutText", () -> cutText());
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/gui/images/copy_icon.png",
					"org.multipage.gui.tooltipCopySelectedText", () -> copyText());
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/gui/images/paste_icon.png",
					"org.multipage.gui.tooltipPasteText", () -> pasteText());
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/gui/images/select_all.png",
					"org.multipage.gui.tooltipSelectAll", () -> selectAll());
	
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/gui/images/copy_all.png",
					"org.multipage.gui.tooltipCopyAllText", () -> copyAll());
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/gui/images/replace_icon.png",
					"org.multipage.gui.tooltipReplaceText", () -> replaceText());
			toolBar.addSeparator();
			buttonFont = ToolBarKit.addToolBarButton(toolBar, "org/multipage/gui/images/font_icon.png",
					"org.multipage.gui.tooltipSetFont", () -> setFont());
			toolBar.addSeparator();
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/gui/images/indent.png",
					"org.multipage.gui.tooltipMoveTextRight", () -> moveTextRight());
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/gui/images/unindent.png",
					"org.multipage.gui.tooltipMoveTextLeft", () -> moveTextLeft());
			toolBar.addSeparator();
			buttonUndo = ToolBarKit.addToolBarButton(toolBar, "org/multipage/gui/images/undo_icon.png",
					"org.multipage.gui.tooltipUndoAction", () -> undoText());
			buttonRedo = ToolBarKit.addToolBarButton(toolBar, "org/multipage/gui/images/redo_icon.png",
					"org.multipage.gui.tooltipRedoAction", () -> redoText());
			toolBar.addSeparator();
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/gui/images/search_icon.png",
					"org.multipage.gui.tooltipFindText", () -> findText());
			toolBar.addSeparator();
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/gui/images/print_icon.png",
					"org.multipage.gui.tooltipPrintText", () -> print());
			buttonWrap = ToolBarKit.addToggleButton(toolBar, "org/multipage/gui/images/word_wrap.png",
					"org.multipage.gui.tooltipWrapUnwrap", () -> wrapUnwrap());
			buttonReset = ToolBarKit.addToolBarButton(toolBar, "org/multipage/gui/images/cancel_icon.png",
					"org.multipage.gui.tooltipResetContent", () -> resetText());
			
			// If the HTML editor is used.
			if (useHtmlEditor) {
	
				buttonBold = ToolBarKit.addToggleButton(richTextToolBar,
						"org/multipage/gui/images/bold.png",
						"org.multipage.gui.tooltipSetBoldText", () -> boldText());
				buttonItalic = ToolBarKit.addToggleButton(richTextToolBar,
						"org/multipage/gui/images/italic.png",
						"org.multipage.gui.tooltipSetItalicText", () -> italicText());
				buttonUnderline = ToolBarKit.addToggleButton(richTextToolBar,
						"org/multipage/gui/images/underline.png",
						"org.multipage.gui.tooltipSetUnderlinedText", () -> underlineText());
				buttonStrike = ToolBarKit.addToggleButton(richTextToolBar,
						"org/multipage/gui/images/strike.png",
						"org.multipage.gui.tooltipSetStrikedText", () -> strikeText());
				buttonSubscript = ToolBarKit.addToggleButton(richTextToolBar,
						"org/multipage/gui/images/subscript.png",
						"org.multipage.gui.tooltipSetSubscriptText", () -> subscriptText());
				buttonSuperscript = ToolBarKit.addToggleButton(richTextToolBar,
						"org/multipage/gui/images/superscript.png",
						"org.multipage.gui.tooltipSetSuperscriptText", () -> superscriptText());
				
				setFontNames(richTextToolBar);
				setFontSizes(richTextToolBar);
				setAlignment(richTextToolBar);
				
				buttonForeground = ToolBarKit.addToolBarButton(richTextToolBar,
						"org/multipage/gui/images/foreground.png",
						"org.multipage.gui.tooltipSetTextForegroundColor", () -> foregroundText());
				buttonBackground = ToolBarKit.addToolBarButton(richTextToolBar,
						"org/multipage/gui/images/background.png",
						"org.multipage.gui.tooltipSetTextBackgroundColor", () -> backgroundText());
				buttonBackground.setVisible(false);// hide it
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
	}
	
	/**
	 * Set paragraph alignment.
	 * @param toolBar
	 */
	private void setAlignment(JToolBar toolBar) {
		try {
			
			// Get current paragraph alignment.
			AttributeSet attributes = Utility.getInputAttributes(htmlTextPane);
			int alignment = StyleConstants.getAlignment(attributes);
				
			textAlignment = new JComboBox();
			textAlignment.setEnabled(false);
			textAlignment.setMaximumSize(new Dimension(40, 26));
			
			Utility.setParagraphAlignments(textAlignment, alignment);
			
			toolBar.add(textAlignment);
			
			// Set listener.
			textAlignment.addItemListener(new ItemListener() {
				// On selection.
				@Override
				public void itemStateChanged(ItemEvent e) {
					try {
						
						Object selected = textAlignment.getSelectedItem();
						if (selected instanceof Object []) {
							Object [] item = (Object []) selected;
							textAlign((Integer) item[1]);
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			
			textAlignment.setVisible(false); // hide it
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set font sizes.
	 * @param toolBar
	 */
	private void setFontSizes(JToolBar toolBar) {
		try {
			
			// Get current font size.
			AttributeSet attributes = Utility.getInputAttributes(htmlTextPane);
			int size = StyleConstants.getFontSize(attributes);
					
			fontSize = new JComboBox();
			fontSize.setMaximumSize(new Dimension(45, 26));
			
			Utility.loadFontSizes(fontSize, size);
			toolBar.add(fontSize);
			
			final JPanel thisPanel = this;
			
			// Set listeners.
			fontSize.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					try {
						
						Object selected = fontSize.getSelectedItem();
						if (selected instanceof Integer) {
							textSize((Integer) selected);
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			fontSize.getEditor().addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						
						// Check value.
						String value = fontSize.getSelectedItem().toString();
						try {
							textSize(Integer.parseInt(value));
						}
						catch (NumberFormatException err) {
							Utility.show(thisPanel, "org.multipage.gui.messageInputValueIsNotNumber");
						}
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
	 * Set font names
	 * @param toolBar
	 */
	private void setFontNames(JToolBar toolBar) {
		try {
			
			// Get current font family.
			AttributeSet attributes = Utility.getInputAttributes(htmlTextPane);
			String name = StyleConstants.getFontFamily(attributes);
			
			// Add font names.
			fontFamily = new JComboBox();
			fontFamily.setMaximumSize(new Dimension(150, 26));
			Utility.loadFontFamilies(fontFamily, name);
			toolBar.add(fontFamily);
			
			// Set listener.
			fontFamily.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					try {
						
						Object selected = fontFamily.getSelectedItem();
						if (selected instanceof String) {
							textFont((String) selected);
						}
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
	 * Tab changed.
	 */
	protected void onTabChanged() {
		try {
			
			if (buttonFont == null) {
				return;
			}
			boolean htmlEditorSelected = (tabbedPane.getSelectedIndex() == 1);
			buttonFont.setEnabled(!htmlEditorSelected);
			
			// Reset undo managers.
			undoManagerPlain.discardAllEdits();
			undoManagerHtml.discardAllEdits();
			
			onDocumentChanged();
			
			// Close find dialogs.
			if (findPlainDialog.isVisible()) {
				findPlainDialog.closeWindow();
			}
			if (findHtmlDialog.isVisible()) {
				findHtmlDialog.closeWindow();
			}
			
			// Highlight script commands.
			highlightScriptCommands();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get current editor.
	 */
	public JEditorPane getCurrentEditor() {
		
		try {
			return tabbedPane.getSelectedIndex() == 0 ? plainTextPane : htmlTextPane;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return plainTextPane;
	}
	
	/**
	 * Cut text.
	 */
	public void cutText() {
		try {
			
			JEditorPane editor = getCurrentEditor();
			editor.grabFocus();
			editor.cut();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Copy text.
	 */
	public void copyText() {
		try {
			
			JEditorPane editor = getCurrentEditor();
			editor.grabFocus();
			editor.copy();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Paste text.
	 */
	public void pasteText() {
		try {
			
			JEditorPane editor = getCurrentEditor();
			editor.grabFocus();
			editor.paste();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Select all.
	 */
	public void selectAll() {
		try {
			
			JEditorPane editor = getCurrentEditor();
			editor.grabFocus();
			editor.selectAll();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Replace text.
	 */
	public void replaceText() {
		try {
			
			selectAll();
			pasteText();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Copy all.
	 */
	public void copyAll() {
		try {
			
			selectAll();
			copyText();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get undo manager.
	 * @return
	 */
	private UndoManager getUndoManager() {
		
		try {
			return tabbedPane.getSelectedIndex() == 0 ? undoManagerPlain : undoManagerHtml;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return undoManagerPlain;
	}
	
	/**
	 * Set undoable edit.
	 */
	private void setUndoableEdit() {
		try {
			
			// Create undo manager.
			undoManagerPlain = new UndoManager();
			undoManagerHtml = new UndoManager();
			// Get documents.
			Document documentPlain = plainTextPane.getDocument();
			Document documentHtml = htmlTextPane.getDocument();
			// Set listeners.
			documentPlain.addUndoableEditListener(undoManagerPlain);
			documentHtml.addUndoableEditListener(undoManagerHtml);
			// Add listener.
			documentPlain.addDocumentListener(new DocumentListener() {
				@Override
				public void removeUpdate(DocumentEvent e) {
					try {
						
						onDocumentChanged();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				@Override
				public void insertUpdate(DocumentEvent e) {
					try {
						
						onDocumentChanged();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				@Override
				public void changedUpdate(DocumentEvent e) {
					try {
						
						onDocumentChanged();
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
	 * On document change.
	 */
	protected void onDocumentChanged() {
		try {
			
			// Enable / disable buttons.
			UndoManager undoManager = getUndoManager();
			buttonUndo.setEnabled(undoManager.canUndo());
			buttonRedo.setEnabled(undoManager.canRedo());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Enable / disable editor.
	 * @param editable
	 */
	public void setEditable(boolean editable) {
		try {
			
			plainTextPane.setEditable(editable);
			plainTextPane.setBackground(editable ? Color.WHITE : readOnlyBackground);
			htmlTextPane.setEditable(editable);
			htmlTextPane.setBackground(editable ? Color.WHITE : readOnlyBackground);
			buttonCut.setEnabled(editable);
			buttonUndo.setEnabled(editable);
			buttonRedo.setEnabled(editable);
			buttonReset.setEnabled(editable);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set font.
	 */
	public void setFont() {
		try {
			
			FontChooser dialog = new FontChooser();
			Font font = dialog.showDialog(this, textFont);
			
			if (font != null) {
				// Set font.
				plainTextPane.setFont(font);
				textFont = font;
			}
			
			plainTextPane.grabFocus();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create find dialog.
	 */
	private void createFindDialog() {
		try {
			
			findPlainDialog = new FindReplaceDialog(parentWindow, plainTextPane);
			findHtmlDialog = new FindReplaceDialog(parentWindow, htmlTextPane);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set text.
	 * @param text
	 */
	public void setText(String text) {
		try {
			
			final Point point = new Point();
			
			plainTextPane.setText(text);
			// Set caret position.
			plainTextPane.setCaretPosition(0);
			// Reset undo manager.
			undoManagerPlain.discardAllEdits();
			undoManagerHtml.discardAllEdits();
			// Scroll to the old position.
			Utility.scrollToPosition(htmlScrollPane, point);
			
			// Highlight script commands.
			highlightScriptCommands();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get text fond.
	 * @return
	 */
	public Font getTextFont() {
		
		try {
			return plainTextPane.getFont();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Set text font.
	 * @param font
	 */
	public void setTextFont(Font font) {
		try {
			// Check font.
			if (font == null) {
				return;
			}
			// Set font.
			this.textFont = font;
			plainTextPane.setFont(font);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Undo text.
	 */
	public void undoText() {
		try {
			
			try {
				getUndoManager().undo();
			}
			catch (CannotUndoException e) {
				
			}
			getCurrentEditor().grabFocus();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Redo text.
	 */
	public void redoText() {
		try {
			
			try {
				getUndoManager().redo();
			}
			catch (CannotRedoException e) {
				
			}
			getCurrentEditor().grabFocus();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Find text.
	 */
	public void findText() {
		try {
			
			// Show find dialog.
			if (tabbedPane.getSelectedIndex() == 0) {
				
				// Get selected text.
				String selectedText = plainTextPane.getSelectedText();
				findPlainDialog.setVisible(true);
				findPlainDialog.setFindText(selectedText);
			}
			else {
				
				// Get selected text.
				String selectedText = htmlTextPane.getSelectedText();
				findHtmlDialog.setVisible(true);
				findHtmlDialog.setFindText(selectedText);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get text.
	 * @return
	 */
	public String getText() {
		
		try {
			String text = plainTextPane.getText();
			return text;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get scroll position.
	 * @return
	 */
	public Point getScrollPosition() {
		
		try {
			JViewport viewport = htmlScrollPane.getViewport();
			return viewport.getViewPosition();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Scroll to given position.
	 * @param position
	 */
	public void scrollToPosition(final Point position) {
		
		Safe.invokeLater(() -> {
			
			// Scroll to the start.
			JViewport viewport = htmlScrollPane.getViewport();
			if (viewport != null) {
				viewport.setViewPosition(position);
			}
		});
	}
	
	/**
	 * Print text.
	 */
	public void print() {
		
		try {
			getCurrentEditor().print();
		}
		catch (PrinterException e) {
			// Inform user.
			Utility.show2(this, e.getLocalizedMessage());
		}
	}
	
	/**
	 * Enable / disable word wrap.
	 */
	public void wrapUnwrap() {
		try {
			
			boolean wrapText = buttonWrap.isSelected();
			
			if (wrapText) {
				htmlScrollPane.setViewportView(htmlTextPane);
				
				plainScrollPane.setViewportView(plainTextPane);
			}
			else {
				htmlScrollPane.setViewportView(panelNoWrapHtml);
				panelNoWrapHtml.add(htmlTextPane);
				
				plainScrollPane.setViewportView(panelNoWrapPlain);
				panelNoWrapPlain.add(plainTextPane);
			}
			htmlTextPane.revalidate();
			plainTextPane.revalidate();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * @return the plainTextPane
	 */
	public JEditorPane getTextPane() {
		return plainTextPane;
	}
	
	/**
	 * Add popup menu.
	 * @param popupAddIn
	 */
	public void addPopupMenusPlain(TextPopupMenuAddIn popupAddIn) {
		try {
			
			popupAddIn.addMenu(popupMenuPlain, plainTextPane);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Add popup menu.
	 * @param popupAddIn
	 */
	public void addPopupMenusHtml(TextPopupMenuAddIn popupAddIn) {
		try {
			
			popupAddIn.addMenu(popupMenuHtml, htmlTextPane);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set listerers.
	 */
	private void setListeners() {
		try {
			
			htmlTextPane.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void removeUpdate(DocumentEvent e) {
					onChangeDesign();
				}
				@Override
				public void insertUpdate(DocumentEvent e) {
					onChangeDesign();
				}
				@Override
				public void changedUpdate(DocumentEvent e) {
					onChangeDesign();
				}
			});
			plainTextPane.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void removeUpdate(DocumentEvent e) {
					onChangeSource();
				}
				@Override
				public void insertUpdate(DocumentEvent e) {
					onChangeSource();
				}
				@Override
				public void changedUpdate(DocumentEvent e) {
					onChangeSource();
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On change design window content.
	 */
	protected void onChangeDesign() {
		try {
			
			if (changing) {
				return;
			}
			
			// Disable deadlock.
			changing = true;
			
			// Read text from the design text component and insert it to the source text component.
			HTMLDocument htmlDocument = (HTMLDocument) htmlTextPane.getDocument();
			
			EditorKit kit = htmlTextPane.getEditorKit();
			StringWriter writer = new StringWriter();
			try {
				kit.write(writer, htmlDocument, 0, htmlDocument.getLength());
			}
			catch (Exception e1) {
				Safe.exception(e1);
			}
			
			String htmlText = writer.toString();
	
			htmlText = adaptTextToSource(htmlText);
			
			// Rearrange <p> paragraphs.
			if (extractBody) {
				
				Obj<Boolean> modified = new Obj<Boolean>(false);
				htmlText = rearrangeParagraphs(htmlText, modified);
				
				final String newHtmlText = htmlText;
				
				if (modified.ref) {
					Safe.invokeLater(() -> {
						
						// Update HTML editor text.
						changing = true;
						htmlTextPane.setText(newHtmlText);
						changing = false;
					});
				}
			}
			
			plainTextPane.setText(htmlText);
			
			changing = false;
			
			fireChange();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
	}
	
	/**
	 * On key released event.
	 * @param event
	 */
	protected void onKeyReleased(KeyEvent event) {
		try {
			
			// Get released key.
			int keyCode = event.getKeyCode();
			boolean isControl = event.isControlDown();
			
			// Open intellisense window.
			if (keyCode == KeyEvent.VK_SPACE && isControl) {
				
				// Get plain text and insert it to the text component.
				Document plainDocument = plainTextPane.getDocument();
				int plainLength = plainDocument.getLength();
				
				final Obj<String> plainText = new Obj<String>("");
				try {
					// Get text content.
					plainText.ref = plainDocument.getText(0, plainLength);
					
					// Get caret position.
					int selection = plainTextPane.getSelectionStart();
					Caret caret = plainTextPane.getCaret();
					
					// If the intellisense exists, get text hints.
					if (intellisenseLambda != null) {
						
						Safe.invokeLater(() -> {
							intellisenseLambda.apply(plainText.ref).apply(selection).apply(caret).accept(plainTextPane);
						});
					}
				}
				catch (BadLocationException e) {
					Safe.exception(e);
				}
			}
			// Close intellisense window.
			else if (keyCode == KeyEvent.VK_ESCAPE) {
				
				// If the intellisense exists, get text hints.
				if (intellisenseLambda != null) {
					
					Safe.invokeLater(() -> {
						intellisenseLambda.apply(null).apply(null).apply(null).accept(plainTextPane);
					});
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On change source.
	 */
	protected void onChangeSource() {
		try {
			
			if (changing) {
				return;
			}
			
			// Disable deadlock.
			changing = true;
			
			// Get plain text and insert it to the design text component.
			Document plainDocument = plainTextPane.getDocument();
			int plainLength = plainDocument.getLength();
			
			final Obj<String> plainText = new Obj<String>("");
			try {
				// Get text content.
				plainText.ref = plainDocument.getText(0, plainLength);
			}
			catch (BadLocationException e) {
				Safe.exception(e);
			}
				
			if (useHtmlEditor) {
				htmlTextPane.setText(plainText.ref);
			}
			changing = false;
			
			// Highlight script commands.
			highlightScriptCommands();
			
			fireChange();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Add listener.
	 * @param simpleMethodRef
	 */
	protected void addChangeListener(SimpleMethodRef simpleMethodRef) {
		try {
			
			changeListeners.add(simpleMethodRef);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Fire change.
	 */
	private void fireChange() {
		try {
			
			for (SimpleMethodRef listener : changeListeners) {
				listener.run();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Adapt text.
	 * @param text
	 * @return
	 */
	private String adaptTextToSource(String text) {
		
		try {
			// Extract body.
			if (extractBody) {
				text = extractBody(text);
			}
	
			// Remove character escapes.
			Pattern escapePattern = Pattern.compile("&#[0-9]+;");
			while (true) {
				
				Matcher escapeMatcher = escapePattern.matcher(text);
				// Find escape sequence.
				if (!escapeMatcher.find(0)) {
					break;
				}
				// Replace escape sequence.
				int escapeStart = escapeMatcher.start();
				int escapeEnd = escapeMatcher.end();
				String numberString = text.substring(escapeStart + 2, escapeEnd - 1);
				int number = 0;
				try {
					number = Integer.parseInt(numberString);
				}
				catch (NumberFormatException e) {
				}
				char character = (char) number;
				
				int length = text.length();
				text = text.substring(0, escapeStart) + character + text.substring(escapeEnd, length);
			}
			
			// Replace &amp;
			text = text.replace("&amp;", "&");
			// Replace &quot;
			text = text.replace("&quot;", "\"");
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return text;
	}

	/**
	 * Rearrange paragraphs.
	 * @param text
	 * @param modified
	 * @return
	 */
	private String rearrangeParagraphs(String text, Obj<Boolean> modified) {
		
		try {
			modified.ref = false;
			
			Pattern paragraphStartPattern = Pattern.compile("<\\s*p\\s*[^>]*");
			Pattern paragraphEndPattern = Pattern.compile("<\\s*/\\s*p\\s*>");
			
			// Simplify end of lines.
			text = text.replace("\r\n", "\n");
			text = text.replace('\r', '\n');
			
			String resultText = "";
			
			int length = text.length();
			int lineBegin = 0;
			
			for (int index = 0; index < length; index++) {
				
				// If a paragraph start is on current position.
				if (Utility.patternMatches(text, index, paragraphStartPattern)) {
					
					// Find end of the paragraph.
					Integer paragraphEnd = Utility.findPattarnEnd(text, index, paragraphEndPattern);
					if (paragraphEnd == null) {
						
						paragraphEnd = text.length();
					}
					
					// Save paragraph.
					resultText += text.substring(index, paragraphEnd) + '\n';
					index = paragraphEnd;
					lineBegin = index;
					
					continue;
				}
				
				// If end of text is reached.
				if (index == length - 1) {
					
					boolean isNewLineEnd = text.charAt(index) == '\n';
					String line = "";
					
					if (length > lineBegin) {
						line = text.substring(lineBegin, isNewLineEnd ? length - 1 : length);
					}
					
					// Create paragraph.
					String paragraph = String.format("<p style=\"margin-top: 0\">\n    %s\n</p>\n", line);
					resultText += paragraph;
					
					// Add possible end new line.
					if (isNewLineEnd && !line.isEmpty()) {
						resultText += "<p style=\"margin-top: 0\">\n    \n</p>\n";
					}
					
					modified.ref = true;
					
					break;
				}
				
				// If the end of line is reached, add new paragraph.
				if (text.charAt(index) == '\n') {
					
					String line = "";
					
					if (index > lineBegin) {
						line = text.substring(lineBegin, index);
					}
					
					// Create paragraph.
					String paragraph = String.format("<p style=\"margin-top: 0\">\n    %s\n</p>\n", line);
					resultText += paragraph;
					
					lineBegin = index + 1;
					
					modified.ref = true;
				}
			}
			
			return resultText;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return text;
	}

	/**
	 * Extract HTML body.
	 * @param text
	 * @return
	 */
	private String extractBody(String text) {
		
		try {
			// Find body start and end.
			final Pattern bodyStartPattern = Pattern.compile("<\\s*body\\s*>", Pattern.CASE_INSENSITIVE);
			final Pattern bodyEndPattern = Pattern.compile("<\\s*/\\s*body\\s*>", Pattern.CASE_INSENSITIVE);
			
			// Find first body start.
			Matcher bodyStartMatcher = bodyStartPattern.matcher(text);
			if (bodyStartMatcher.find()) {
				
				int bodyStart = bodyStartMatcher.end();
				int bodyEnd = text.length();
				
				// Find last body end.
				Matcher bodyEndMatcher = bodyEndPattern.matcher(text);
				while (bodyEndMatcher.find()) {
					
					bodyEnd = bodyEndMatcher.start();
				}
				
				// Extract body.
				text = text.substring(bodyStart, bodyEnd);
				
				// Move text lines left.
				String [] textLines = text.split("\n");
				String movedText = "";
				
				for (String textLine : textLines) {
					
					textLine = moveLineLeft(textLine);
					movedText += textLine + '\n';
				}
				
				// Trim text.
				text = movedText.trim();
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return text;
	}

	/**
	 * Set editors.
	 */
	private void setEditors() {
		try {
			
			if (!useHtmlEditor) {
				tabbedPane.removeTabAt(1);
			}
			else {
				// Customized HTML editor kit.
				CustomizedHTMLEditorKit editorKit = new CustomizedHTMLEditorKit();
				htmlTextPane.setEditorKit(editorKit);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Select HTML editor.
	 */
	public void selectHtmlEditor(boolean select) {
		try {
			
			if (useHtmlEditor) {
				tabbedPane.setSelectedIndex(select ? 1 : 0);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Reset text.
	 */
	public void resetText() {
		try {
			
			if (JOptionPane.showConfirmDialog(this,
					Resources.getString("org.multipage.gui.messageResetContent"))
					!= JOptionPane.YES_OPTION) {
				return;
			}
			
			changing = true;
			htmlTextPane.setText("");
			plainTextPane.setText("");
			changing = false;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Toggle bold text.
	 */
	public void boldText() {
		try {
			
			htmlTextPane.grabFocus();
			
			boolean flag = StyleConstants.isBold(Utility.getInputAttributes(htmlTextPane));
	
			SimpleAttributeSet attributes = new SimpleAttributeSet();
			StyleConstants.setBold(attributes, !flag);
			Utility.setCharacterAttributes(htmlTextPane, attributes, false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Toggle italic text.
	 */
	public void italicText() {
		try {
			
			htmlTextPane.grabFocus();
			
			boolean flag = StyleConstants.isItalic(Utility.getInputAttributes(htmlTextPane));
	
			SimpleAttributeSet attributes = new SimpleAttributeSet();
			StyleConstants.setItalic(attributes, !flag);
			Utility.setCharacterAttributes(htmlTextPane, attributes, false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Toggle underlined text.
	 */
	public void underlineText() {
		try {
			
			htmlTextPane.grabFocus();
			
			boolean flag = StyleConstants.isUnderline(Utility.getInputAttributes(htmlTextPane));
	
			SimpleAttributeSet attributes = new SimpleAttributeSet();
			StyleConstants.setUnderline(attributes, !flag);
			Utility.setCharacterAttributes(htmlTextPane, attributes, false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Toggle strike text.
	 */
	public void strikeText() {
		try {
			
			htmlTextPane.grabFocus();
			
			boolean flag = StyleConstants.isStrikeThrough(Utility.getInputAttributes(htmlTextPane));
	
			SimpleAttributeSet attributes = new SimpleAttributeSet();
			StyleConstants.setStrikeThrough(attributes, !flag);
			Utility.setCharacterAttributes(htmlTextPane, attributes, false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Toggle subscript text.
	 */
	public void subscriptText() {
		try {
			
			htmlTextPane.grabFocus();
			
			boolean flag = StyleConstants.isSubscript(Utility.getInputAttributes(htmlTextPane));
	
			SimpleAttributeSet attributes = new SimpleAttributeSet();
			StyleConstants.setSubscript(attributes, !flag);
			Utility.setCharacterAttributes(htmlTextPane, attributes, false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Toggle superscript text.
	 */
	public void superscriptText() {
		try {
			
			htmlTextPane.grabFocus();
			
			boolean flag = StyleConstants.isSuperscript(Utility.getInputAttributes(htmlTextPane));
	
			SimpleAttributeSet attributes = new SimpleAttributeSet();
			StyleConstants.setSuperscript(attributes, !flag);
			Utility.setCharacterAttributes(htmlTextPane, attributes, false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set font family.
	 * @param fontFamily
	 */
	protected void textFont(String fontFamily) {
		try {
			
			htmlTextPane.grabFocus();
			
			SimpleAttributeSet attributes = new SimpleAttributeSet();
			StyleConstants.setFontFamily(attributes, fontFamily);
			Utility.setCharacterAttributes(htmlTextPane, attributes, false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set text size.
	 * @param size
	 */
	protected void textSize(int size) {
		try {
			
			htmlTextPane.grabFocus();
			
			SimpleAttributeSet attributes = new SimpleAttributeSet();
			StyleConstants.setFontSize(attributes, size);
			Utility.setCharacterAttributes(htmlTextPane, attributes, false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set text align.
	 * @param align
	 */
	protected void textAlign(int align) {
		try {
			
			htmlTextPane.grabFocus();
	
			MutableAttributeSet attributes = new SimpleAttributeSet();
			StyleConstants.setAlignment(attributes, align);
			htmlTextPane.setParagraphAttributes(attributes, false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set tool bar controls.
	 */
	protected void setToolBarControls() {
		try {
			
			if (buttonBold == null) {
				return;
			}
			
			AttributeSet textAttributes = Utility.getInputAttributes(htmlTextPane);
			AttributeSet paragraphAttributes = htmlTextPane.getParagraphAttributes();
			
			Color color = StyleConstants.getForeground(textAttributes);
			buttonForeground.setBackground(color);
			color = (Color) textAttributes.getAttribute(StyleConstants.Background);
			if (color == null) {
				color = Color.WHITE;
			}
			buttonBackground.setBackground(color);
			buttonBold.setSelected(StyleConstants.isBold(textAttributes));
			buttonItalic.setSelected(StyleConstants.isItalic(textAttributes));
			buttonUnderline.setSelected(StyleConstants.isUnderline(textAttributes));
			buttonStrike.setSelected(StyleConstants.isStrikeThrough(textAttributes));
			buttonSubscript.setSelected(StyleConstants.isSubscript(textAttributes));
			buttonSuperscript.setSelected(StyleConstants.isSuperscript(textAttributes));
			
	
			String name = StyleConstants.getFontFamily(textAttributes);
			Utility.selectComboItem(fontFamily, name);
			
			Integer size = StyleConstants.getFontSize(textAttributes);
			fontSize.getEditor().setItem(size.toString());
			Utility.selectComboItem(fontSize, size);
	
			int align = StyleConstants.getAlignment(paragraphAttributes);
			Utility.selectComboAlign(textAlignment, align);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set foreground color.
	 */
	public void foregroundText() {
		try {
			
			Color color = StyleConstants.getForeground(Utility.getInputAttributes(htmlTextPane));
	
			// Choose color.
			color = Utility.chooseColor(this, color);
			buttonForeground.setBackground(color);
			
			// Set foreground color.
			htmlTextPane.grabFocus();
			
			SimpleAttributeSet attributes = new SimpleAttributeSet();
			StyleConstants.setForeground(attributes, color);
			Utility.setCharacterAttributes(htmlTextPane, attributes, false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set background text.
	 */
	public void backgroundText() {
		try {
			
			Color color = StyleConstants.getBackground(Utility.getInputAttributes(htmlTextPane));
	
			// Choose color.
			color = Utility.chooseColor(this, color);
			buttonBackground.setBackground(color);
			
			// Set foreground color.
			htmlTextPane.grabFocus();
			
			SimpleAttributeSet attributes = new SimpleAttributeSet();
			StyleConstants.setBackground(attributes, color);
			Utility.setCharacterAttributes(htmlTextPane, attributes, false);	
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Highlight found.
	 * @param foundAttr
	 */
	public void highlightFound(final FoundAttr foundAttr) {
		
		Safe.invokeLater(() -> {
			
			Utility.highlight(plainTextPane, foundAttr, myHighlightPainter);
			if (useHtmlEditor) {
				Utility.highlight(htmlTextPane, foundAttr, myHighlightPainter);
			}				
		});
	}

	/**
	 * Gets true value if a HTML body is extracted.
	 * @param extractBody the extractBody to set
	 */
	public void setExtractBody(boolean extractBody) {
		
		this.extractBody = extractBody;
	}
	
	/**
	 * Set documents.
	 */
	private void setDocuments() {
		try {
			
			// Create document filter.
			DocumentFilter filter = new DocumentFilter() {
	
				@Override
				public void replace(FilterBypass fb, int offset, int length,
						String text, AttributeSet attrs)
						throws BadLocationException {
					try {
						
						// Initialize.
						String theText = text;
						
						// Replace tabulator with spaces.
						theText = theText.replace("\t", tabWhiteSpaces);
						
						try {
							if (theText.equals("\n")) {
								// On new line keep leading white spaces.
								String previousText = fb.getDocument().getText(0, offset);
								String spaces = getLeadingSpacesFromPreviousText(previousText);
								theText += spaces;
							}
							super.replace(fb, offset, length, theText, attrs);
						}
						catch (Throwable e) {
							Safe.exception(e);
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			};
			
			// Set document filters.
			AbstractDocument document = (AbstractDocument) plainTextPane.getDocument();
			document.setDocumentFilter(filter);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get leading spaces from previous text.
	 * @param previousText
	 * @return
	 */
	protected String getLeadingSpacesFromPreviousText(String previousText) {
		
		try {
			// Get last line.
			String[] lines = previousText.split("\n");
			int length = lines.length;
			
			String lastLine = null;
			if (length > 0) {
				lastLine = lines[length - 1];
			}
			else {
				lastLine = previousText.replace("\n", "");
			}
			
			String spaces = "";
			
			// Get last line leading spaces.
			for (int index = 0; index < lastLine.length(); index++) {
				
				char character = lastLine.charAt(index);
				if (Character.isWhitespace(character)) {
					spaces += character;
				}
				else {
					break;
				}
			}
	
			return spaces;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Set tab size.
	 * @param pane
	 * @param size
	 */
	public static void setTabSize(JTextPane pane, int size) {
		try {
			
			String tab = "";
			for (int i = 0; i < size; i++) {
				tab += " ";
			}
			float f = (float) pane.getFontMetrics(pane.getFont()).stringWidth(tab);
			TabStop[] tabs = new TabStop[500]; // this sucks
	
			for (int i = 0; i < tabs.length; i++) {
				tabs[i] = new TabStop(f * (i + 1), TabStop.ALIGN_LEFT,
						TabStop.LEAD_NONE);
			}
	
			TabSet tabset = new TabSet(tabs);
			StyleContext sc = StyleContext.getDefaultStyleContext();
			AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
					StyleConstants.TabSet, tabset);
			pane.setParagraphAttributes(aset, false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get line from the text.
	 * @param text
	 * @param lineIndex
	 * @return
	 */
	private static String getLine(String text, int lineIndex) {
		
		try {
			int length = text.length();
			int currentLine = 0;
			StringBuffer lineText = new StringBuffer();
			
			for (int charIndex = 0; charIndex < length; charIndex++) {
				
				Character character = text.charAt(charIndex);
				
				if (currentLine == lineIndex) {
					lineText.append(character);
				}
				if (currentLine > lineIndex) {
					break;
				}
	
				// Recognize line end.
				if (character == '\n') {
					currentLine++;
				}
				else if (character == '\r') {
					Integer nextIndex = charIndex + 1;
					if (nextIndex >= length) {
						nextIndex = null;
					}
					if (nextIndex == null) {
						currentLine++;
					}
					else {
						Character nextCharacter = text.charAt(nextIndex);
						if (nextCharacter != '\n') {
							currentLine++;
						}
						else {
							charIndex++;
							currentLine++;
						}
					}
				}
			}
			
			return lineText.toString();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get line indices.
	 * @param textComponent 
	 * @return
	 */
	public static void moveLines(boolean left, JTextComponent textComponent) {
		try {
			
			// Get selection.
			Integer start = textComponent.getSelectionStart();
			Integer end = textComponent.getSelectionEnd();
			// Get text.
			String text = textComponent.getText();
	
			int lineIndex = 0;
			int length = text.length();
			Integer startLine = null;
			Integer endLine = null;
			boolean isLineStart = true;
			int position = 0;
			
			for (int charIndex = 0; charIndex < length; charIndex++) {
	
				// Get start line.
				if (position >= start) {
					if (startLine == null) {
						startLine = lineIndex;
					}
				}
				
				// Get stop line.
				if (position >= end) {
					if (endLine == null) {
						if (isLineStart && start == end || !isLineStart) {
							endLine = lineIndex;
						}
						else {
							endLine = lineIndex - 1;
							if (endLine < 0) {
								endLine = 0;
							}
	 					}
					}
				}
				
				isLineStart = false;
				
				// Recognize line end.
				Character character = text.charAt(charIndex);
				if (character == '\n') {
					lineIndex++;
					isLineStart = true;
				}
				else if (character == '\r') {
					Integer nextIndex = charIndex + 1;
					if (nextIndex >= length) {
						nextIndex = null;
					}
					if (nextIndex == null) {
						lineIndex++;
						isLineStart = true;					
					}
					else {
						Character nextCharacter = text.charAt(nextIndex);
						if (nextCharacter != '\n') {
							lineIndex++;
							isLineStart = true;							
						}
						else {
							charIndex++;
							lineIndex++;
							isLineStart = true;
						}
					}
				}
				
				position++;
			}
			
			if (startLine == null) {
				startLine = lineIndex;
			}
			if (endLine == null) {
				if (isLineStart && start == end || !isLineStart) {
					endLine = lineIndex;
				}
				else {
					endLine = lineIndex - 1;
					if (endLine < 0) {
						endLine = 0;
					}
				}
			}
			
			// Move lines left or right.
			position = 0;
			String newText = "";
			int lineCount = lineIndex;
			start = null;
			end = null;
			
			for (int index = 0; index <= lineCount; index++) {
				
				String lineText = getLine(text, index);
				if (index >= startLine && index <= endLine) {
					if (start == null) {
						start = newText.length();
					}
					newText += left ? TextEditorPane.moveLineLeft(lineText) : TextEditorPane.moveLineRight(lineText);
					
					if (index == lineCount && start != null && end == null) {
						end = newText.length();
					}
				}
				else {
					if (start != null && end == null) {
						end = newText.length();
					}
					newText += lineText;
				}
			}
			
			textComponent.setText(newText);
			textComponent.grabFocus();
	
			if (start != null && end != null) {
						
				textComponent.setSelectionStart(start);
				textComponent.setSelectionEnd(end);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Shift text back.
	 * @param textComponent
	 */
	private static void backText(JTextComponent textComponent) {
		try {
			
			int position = textComponent.getCaretPosition();
	
			// Get component text.
			int length = textComponent.getDocument().getLength();
			String text = "";
			try {
				text = textComponent.getDocument().getText(0, length);
			}
			catch (BadLocationException e) {
			}
			
			// Find previous new line character.
			int lineStart = position - 1;
			while (true) {
				
				if (lineStart < 0) {
					lineStart = 0;
					break;
				}
				
				char character = text.charAt(lineStart);
				if (character == '\n') {
	
					lineStart++;
					break;
				}
				
				lineStart--;
			}
			
			// Remove leading spaces.
			int removeCount = 0;
			int index = lineStart;
			
			while (true) {
				
				char character = text.charAt(index);
				if (character == ' ') {
					index++;
					
					removeCount++;
					if (removeCount < tabWidth) {
						continue;
					}
				}
				
				break;
			}
			
			// Remove leading spaces.
			if (removeCount > 0) {
	
				try {
					textComponent.getDocument().remove(lineStart, removeCount);
				}
				catch (BadLocationException e) {
					Safe.exception(e);
				}
				
				// Set new caret position.
				textComponent.setCaretPosition(position - removeCount);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
	}

	/**
	 * Move text right.
	 */
	public void moveTextRight() {
		try {
			
			if (getCurrentEditor().isEditable()) {
				TextEditorPane.moveLines(false, getCurrentEditor());
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Move text left.
	 */
	public void moveTextLeft() {
		try {
			
			if (getCurrentEditor().isEditable()) {
				TextEditorPane.moveLines(true, getCurrentEditor());
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Shift text back.
	 */
	protected void backTextLeft() {
		try {
			
			if (getCurrentEditor().isEditable()) {
				TextEditorPane.backText(getCurrentEditor());
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create timers.
	 */
	private void createTimers() {
		try {
			
			// Create Swing timer.
			highlightScriptCommandsTimer = new Timer(100, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						
						// Highlight script commands.
						if (plainTextPane.isShowing()) {
							highlightScriptCommands(plainTextPane);
						}
						
						if (htmlTextPane.isShowing()) {
							highlightScriptCommands(htmlTextPane);
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			
			// Timer action is invoked only once.
			highlightScriptCommandsTimer.setRepeats(false);
			highlightScriptCommandsTimer.setCoalesce(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Callback method which highlights script commands.
	 * @param textPane
	 */
	protected void highlightScriptCommands(JTextPane textPane) {
		
		// Override this method.
	}

	/**
	 * Stop timers.
	 */
	private void stopTimers() {
		try {
			
			highlightScriptCommandsTimer.stop();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Highlight script commands.
	 */
	protected void highlightScriptCommands() {
		try {
			
			// Start timer.
			if (!highlightScriptCommandsTimer.isRunning()) {
				highlightScriptCommandsTimer.start();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get component.
	 */
	@Override
	public Component getComponent() {
		
		return this;
	}

	/**
	 * Get text value.
	 */
	@Override
	public String getStringValue() {
		
		try {
			return getText();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Get specification of value
	 */
	@Override
	public String getSpecification() {
		
		try {
			return getText();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Set string value.
	 */
	@Override
	public void setStringValue(String string) {
		try {
			
			setText(string);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get value meaning.
	 */
	@Override
	public String getValueMeaning() {
		
		return meansText;
	}

	/**
	 * Text editor grabs focus.
	 */
	public void grabFocusText() {
		try {
			
			JEditorPane editorPane = getCurrentEditor();
			if (editorPane != null) {
				
				Safe.invokeLater(() -> {
					editorPane.grabFocus();
				});
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set grayed controls. If a false value is returned, program will use its own method
	 * to gray this panel controls.
	 */
	@Override
	public boolean setControlsGrayed(boolean isDefault) {
		
		return false;
	}
}
