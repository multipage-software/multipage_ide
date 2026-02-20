/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2021-01-06
 *
 */
package org.multipage.gui;

import java.awt.Color;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.multipage.util.Safe;

/**
 * Text field with autosave capability.
 * @author vakol
 *
 */
public class TextFieldAutoSave extends TextFieldEx {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Delay in milliseconds for the SAVE TEXT operation.
	 */
	private static final int saveTimerDelay = 2000;
	
	/**
	 * Last text caret position.
	 */
	private static TextCaret lastTextCaret = null;
	
	/**
	 * Text string. When displayed, it can be edited by user.
	 */
	private String text = null;
		
	/**
	 * Save timer.
	 */
	private Timer saveTimer = null;
	
	/**
	 * Update lambda functions.
	 */
	private Consumer<String> updateLambda = null;
	
	/**
	 * Constructor which takes this text field identifier of any type.
	 */
	public TextFieldAutoSave() {
		
		try {
			// Set text box listeners and timers for SAVE operation.
			setListeners();
			setTimer();
			// $hide<<$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Set update lambda function.
	 * @param updateLambda
	 */
	public void setUpdateLambda(Consumer<String> updateLambda) {
		
		this.updateLambda = updateLambda;
	}
	
	/**
	 * Set text editor listeners.
	 */
	private void setListeners() {
		try {
			
			// Set text boxes content change callback function.
	        getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void insertUpdate(DocumentEvent e) {
					try {
						
						// Delegate call.
						onChangedText();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				@Override
				public void removeUpdate(DocumentEvent e) {
					try {
						
						// Delegate call.
						onChangedText();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				@Override
				public void changedUpdate(DocumentEvent e) {
					try {
						
						// Delegate call.
						onChangedText();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
	        });
	        
	        // Add key listener.
	        addKeyListener(new KeyAdapter() {
	
				@Override
				public void keyPressed(KeyEvent e) {
					try {
						
						// Delegate the call.
						super.keyPressed(e);
						
						// On ENTER key save the text box text.
						if (e.getKeyCode() == KeyEvent.VK_ENTER) {
							saveTextInternal();
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			
	        // Add focus listener.
			addFocusListener(new FocusAdapter() {
				
				// Wrap the call.
				@Override
				public void focusGained(FocusEvent e) {
					try {
						
						super.focusGained(e);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				
				// Wrap the call.
				@Override
				public void focusLost(FocusEvent e) {
					try {
						
						super.focusLost(e);
						
						// Try to save unsaved text content.
						saveTextInternal();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			
			// Add a listener that saves the text box caret.
			addCaretListener(e -> {
				try {
					
					saveCaret();
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
    }
	
	/**
	 * Set save timer.
	 */
	private void setTimer() {
		try {
			
			// Create timer firing one event.
			saveTimer = new Timer(saveTimerDelay, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					// Save box text.
					saveTextInternal();
				}
			});
					
			saveTimer.setInitialDelay(saveTimerDelay);
			saveTimer.setRepeats(false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set 
	 * @param text
	 */
	public void setText(String text) {
		
		Safe.tryToUpdate(this, () -> {
			
			String theText = text;
			
			// Trim text.
			if (theText == null) {
				theText = "";
			}
			
			// Set text reference.
			TextFieldAutoSave.this.text = theText;
			
			// Save caret.
			boolean hasFocus = hasFocus();
			if (hasFocus) {
				saveCaret();
			}
			
			// Get original text.
			String originalText = getText();
			
			// Delegate the call.
			if (originalText != null && !originalText.equals(theText)) {

				super.setText(theText);
			}
			
			// Request end of update signals.
			Safe.invokeLater(() -> {
				
				// Restore caret.
				if (hasFocus) {
					restoreCaret();
				}
			});
		});
	}
	
	/**
	 * Get text string.
	 */
	@Override
	public String getText() {
		
		try {
			// Check presence of editable text.
			if (this.text == null) {
				return null;
			}
			
			// Retrieve current text.
			this.text = super.getText();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return this.text;
	}
	
	/**
	 * Save text. An internal procedure.
	 */
	private void saveTextInternal() {
		
		Safe.tryOnChange(this, () -> {
			
			// Check text presence.
			if (text == null) {
				return;
			}

			// Call update event.
			if (updateLambda != null) {
				updateLambda.accept(text);
			}
			
			// Remove highlight
			setForeground(Color.BLACK);
		});
	}
	
	/**
	 * Save text.
	 */
	public void saveText() {
		try {
			
			// Get current text.
			text = getText();
			// Delegate the call.
			saveTextInternal();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On new text.
	 */
	public void onChangedText() {
		
		Safe.tryOnChange(this, () -> {
			
			// Get text.
			text = getText();
			
			Color color;

			// On message.
			boolean isReadOnlyMessage = !isEnabled();
			if (isReadOnlyMessage) {
				color = Color.lightGray;
			}
			// On simple text.
			else {
		
				// If the current text is not equal to loaded area
				// description set red text color.
				color = Color.RED;
				
				// Start save timer.
				saveTimer.restart();
			}
			
			// Set the color.
			setForeground(color);
		});
	}
	
	/**
	 * Returns true if this text box has focus.
	 */
	public boolean hasFocus() {
		
		try {
			Object owner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
			boolean hasFocus = this.equals(owner);
			return hasFocus;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Remember caret position.
	 */
	public void saveCaret() {
		try {
			
			TextFieldAutoSave.lastTextCaret = new TextCaret(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Restore text box caret.
	 */
	public void restoreCaret() {
		
		// Try to restore the caret position.
		Safe.invokeLater(() -> {
			
			// Check last caret component.
			if (lastTextCaret != null && lastTextCaret.isFor(this)) {
				
				// Set focus.
				requestFocusInWindow();
				
				try {
					setCaretPosition(lastTextCaret.position);
				}
				catch (Exception e) {
				}
				
				// Reset caret.
				TextFieldAutoSave.lastTextCaret = null;
			}
		});
	}
	
	/**
	 * Display read only message.
	 * @param message
	 */
	public void setMessage(String message) {
		
		Safe.tryToUpdate(this, () -> {
			
			setEnabled(false);
			setText(message);
		});
	}
}
