/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2021-01-06
 *
 */
package org.multipage.gui;

import javax.swing.text.JTextComponent;

/**
 * Class for text caret.
 * @author vakol
 *
 */
public class TextCaret {
	
	/**
	 * Caret position (can be null).
	 */
	public Integer position;
	
	/**
	 * Caret identifier.
	 */
	public Object identifier;
	
	/**
	 * Construct new text caret object for a text component.
	 * @param textComponent
	 * 
	 */
	public TextCaret(JTextComponent textComponent) {
		
		this.position = textComponent.getCaretPosition();
		this.identifier = textComponent;
	}
	

	/**
	 * Returns true value if this caret identifier matches the input value.
	 * @param textCaret
	 * @param identifier
	 * @return
	 */
	public static boolean isFor(TextCaret textCaret, Object identifier) {
		
		// Check input values.
		if (textCaret == null || identifier == null) {
			return false;
		}
		
		final Object caretIdentifier = textCaret.identifier;
		
		// Check for null value.
		if (caretIdentifier == null) {
			return false;
		}
		
		// Check identifier.
		return caretIdentifier.equals(identifier);
	}
	
	/**
	 * Returns true value if this caret is identified with input identifier.
	 * @param textComponent
	 */
	public boolean isFor(Object identifier) {
		
		boolean isOwned = this.identifier.equals(identifier);
		return isOwned;
	}
}
