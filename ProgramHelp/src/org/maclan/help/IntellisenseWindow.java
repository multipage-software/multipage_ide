/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2021-06-16
 *
 */

package org.maclan.help;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.text.Caret;

import org.maclan.help.Intellisense.Suggestion;
import org.multipage.gui.Utility;
import org.multipage.util.Safe;
import org.multipage.util.j;

/**
 * Window that displays the intellisense suggestions.
 * @author vakol
 *
 */
public class IntellisenseWindow extends JDialog {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Singleton dialog object.
	 */
	private static IntellisenseWindow dialog = null;

	/**
	 * The flag informs that intellisense window can be disposed.
	 */
	private static boolean canDispose = true;
	
	/**
	 * Window size.
	 */
	public Dimension windowSize = new Dimension(250, 100);
	
	/**
	 * Scroll bars size in pixels.
	 */
	private static final int scrollbarSizePx = 5;

	/**
	 * List model.
	 */
	private DefaultListModel<Suggestion> listModel;
	
	/**
	 * Tag start position.
	 */
	private int tagStart = 0;
	
	/**
	 * Create new window.
	 * @param parent
	 */
	public static void createNew(Component parent) {
		try {
			
			// Try to close old window.
			closeIntellisense();
			
			// Create new hidden window.
			Window parentWindow = Utility.findWindow(parent);
			dialog = new IntellisenseWindow(parentWindow);
			dialog.setAlwaysOnTop(true);
			dialog.setFocusable(false);
			dialog.setVisible(false);
			
			// Add action listener for the suggestion list.
			dialog.list.addMouseListener(new MouseAdapter() {
	
				@Override
				public void mouseClicked(MouseEvent e) {
					try {
						
						// Check button bounds.
						if (IntellisenseItemPanel.linkButtonSize != null) {
						
							// Try to get selected list item index.
							int selectedIndex = dialog.list.getSelectedIndex();
							if (selectedIndex >= 0) {
								
								// Get item bounds and the mouse pointer position.
								Rectangle itemBounds = dialog.list.getCellBounds(selectedIndex, selectedIndex);
								Point mousePoint = e.getPoint();
								
								// Trim boundaries to link button.
								itemBounds.x = itemBounds.x + (itemBounds.width - IntellisenseItemPanel.linkButtonSize.width);
								
								// Get selected suggestion.
								Suggestion selectedSuggestion = dialog.list.getSelectedValue();
								
								// If the mouse pointer is on the link button, display help page.
								boolean isOnLinkButton = itemBounds.contains(mousePoint);
								if (isOnLinkButton) {
									
									Intellisense.displayHelpPage(selectedSuggestion);
								}
								// Else apply suggestion.
								else {
									Intellisense.acceptSuggestion(selectedSuggestion, dialog.tagStart);
								}
							}
						}
						
						// Delegate call.
						super.mouseClicked(e);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			
			// Add key listener.
			dialog.list.addKeyListener(new KeyAdapter() {
	
				@Override
				public void keyReleased(KeyEvent e) {
					try {
						
						// Check key.
						int keyCode = e.getKeyCode();
						if (keyCode == KeyEvent.VK_ENTER) {
							
							// Get selected suggestion.
							Suggestion selectedSuggestion = dialog.list.getSelectedValue();
							
		                    // If suggestion is not null, apply it.
							Intellisense.acceptSuggestion(selectedSuggestion, dialog.tagStart);
						}
						else if (keyCode == KeyEvent.VK_ESCAPE) {
								
		                    // Close the intellisense window.
		                    Safe.invokeLater(() -> {
		                    	closeIntellisense();
		                    });
						}
						else if (keyCode == KeyEvent.VK_RIGHT) {
							
							// Show selected suggestion help page.
							Suggestion selectedSuggestion = dialog.list.getSelectedValue();
							Intellisense.displayHelpPage(selectedSuggestion);
						}
						// Delegate call.
						super.keyReleased(e);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			
			// Add focus listener.
			dialog.list.addFocusListener(new FocusAdapter() {
	
				@Override
				public void focusLost(FocusEvent e) {
					try {
						
						// Close the intellisense window.
						Safe.invokeLater(() -> {
							closeIntellisense();
						});
						
						// Delegate the call.
						super.focusLost(e);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			
			// Add focus listener.
			final FocusListener focusListener = new FocusListener() {
	
				@Override
				public void focusGained(FocusEvent e) {
					
					// Reset the flag.
					canDispose = true;
				}
	
				@Override
				public void focusLost(FocusEvent e) {
					try {
						
						// Check flag.
						if (!canDispose) {
							return;
						}
						
						// Check dialog.
						if (dialog == null) {
							return;
						}
						
						// Check if mouse pointer is on intellisense window.
						Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
						Rectangle intellisenseBoonds = dialog.getBounds();
						
						if (intellisenseBoonds.contains(mouseLocation)) {
							return;
						}
						
						// Hide the dialog.
						Safe.invokeLater(() -> {
							closeIntellisense();
						});
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			};
			parent.addFocusListener(focusListener);
			
			// Add parent window close handler.
			parentWindow.addWindowListener(new WindowAdapter() {
				
				@Override
				public void windowClosed(WindowEvent e) {
					try {
						
						// Hide the dialog.
						Safe.invokeLater(() -> {
							closeIntellisense();
						});
						
						// Delegate the call.
						super.windowClosed(e);
						
						// Remove listener.
						parent.removeFocusListener(focusListener);
						
						// Remove listener.
						parentWindow.removeWindowListener(this);
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
	 * Try to dispose the window object.
	 */
	public static void closeIntellisense() {
		try {
			
			if (dialog != null) {
				dialog.dispose();
				dialog = null;
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Display intellisense window with suggestions near the text editor caret.
	 * @param textPane
	 * @param caret
	 * @param suggestions
	 */
	public static final void displayAtCaret(JTextPane textPane, Caret caret, int tagStart,
			LinkedList<Suggestion> suggestions) {
		try {
			
			// Set flag.
			canDispose = false;
			
			// Check dialog and possibly create new one.
			if (dialog == null) {
				createNew(textPane);
			}
			
			// Get current caret position in text.
			int caretPosition = caret.getDot();
			Rectangle2D caretBounds = null;
			try {
				caretBounds = textPane.modelToView2D(caretPosition);
			}
			catch (Exception e) {
				
				dialog.setVisible(false);
				return;
			}
			Point caretLocation = new Point();
			caretLocation.x = (int) caretBounds.getX() + 10;
			caretLocation.y = (int) caretBounds.getY();
			
			// Remember tag starting position.
			dialog.tagStart = tagStart;
			
			// Load suggestions.
			dialog.loadSuggestions(suggestions);
			
			// Trim the coordinates.
			SwingUtilities.convertPointToScreen(caretLocation, textPane);
			
			// Display window at caret location.
			Safe.invokeLater(() -> {
				dialog.setLocation(caretLocation);
			});
			
			// Update the window.
			Safe.invokeLater(() -> {
				if (dialog != null) {
					dialog.setVisible(false);
				}
			});
			Safe.invokeLater(() -> {
				if (dialog != null) {
					dialog.setVisible(true);
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Controls.
	 */
	private JScrollPane scrollPane = null;
	private JList<Suggestion> list = null;
	
	/**
	 * Create the dialog.
	 * @param parent 
	 */
	public IntellisenseWindow(Window parent) {
		super(parent, ModalityType.MODELESS);
		
		try {
			initComponents();
			postCreate(); //$hide$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Initialize components.
	 */
	private void initComponents() {
		
		setUndecorated(true);
		setMinimumSize(windowSize );
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		scrollPane = new JScrollPane();
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(scrollbarSizePx, 0));
		scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(scrollbarSizePx, 10));
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		list = new JList<Suggestion>();
		scrollPane.setViewportView(list);
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			createList();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Create list of suggestions.
	 */
	private void createList() {
		try {
			
			// Create list model.
			listModel = new DefaultListModel<Suggestion>();
			list.setModel(listModel);
			
			// Create renderer.
			list.setCellRenderer(new ListCellRenderer<Suggestion>() {
				
				// Renderer of the suggestion.
				IntellisenseItemPanel renderer = new IntellisenseItemPanel(IntellisenseWindow.this);
				
				// Callback method.
				@Override
				public Component getListCellRendererComponent(JList<? extends Suggestion> list, Suggestion suggestion, int index,
						boolean isSelected, boolean cellHasFocus) {
					
					try {
						renderer.setSuggestion(suggestion, index, isSelected, cellHasFocus);
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
	 * Load suggestions.
	 * @param suggestions
	 */
	private void loadSuggestions(LinkedList<Suggestion> suggestions) {
		try {
			
			// Clear the list.
			listModel.clear();
			
			// Insert suggestions.
			listModel.addAll(suggestions);
			list.setSelectedIndex(0);
			list.updateUI();
			
			j.log("DISPLAYED SUGGESTIONS %s", suggestions.toString());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
