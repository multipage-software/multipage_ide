/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2021-09-10
 *
 */
package org.maclan.help.gnu_prolog;

import org.multipage.gui.Utility;
import org.multipage.util.Safe;

import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.ExecuteOnlyCode;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologException;

/**
 * Implementation of Prolog predicate that inserts text padding.
 * @author vakol
 *
 */
public class Predicate_text_padding extends ExecuteOnlyCode {
	
	/**
	 * Callback function.
	 */
	@Override
	public int execute(Interpreter interpreter, boolean backtrackMode, Term[] args) throws PrologException {
		
		try {
			// Check arity.
			if (args.length != 2) {
				return FAIL;
			}
			
			// Get pattern and the text.
			Term textTerm = args[0];
			Term patternTerm = args[1];
			
			String text = textTerm.dereference().toString();
			String pattern = patternTerm.dereference().toString();
			
			// Remove apostrophes.
			text = PrologUtility.removeApostrophes(text);
			pattern = PrologUtility.removeApostrophes(pattern);
			
			// Compute padding length.
			int textLength = text.length();
			int patternLength = pattern.length();
			int paddingLength = Math.abs(textLength - patternLength);
			
			// Check the padding length.
			if (paddingLength == 0) {
				return SUCCESS_LAST;
			}
			
			// Make padding.
			final String padding = Utility.repeat(' ', paddingLength);
			AtomTerm atomTerm = null;
			
			if (textLength < patternLength) {
				text += padding;
				
				atomTerm = AtomTerm.get(text);
				interpreter.unify(textTerm, atomTerm);
			}
			else {
				pattern += padding;
				
				atomTerm = AtomTerm.get(pattern);
				interpreter.unify(patternTerm, atomTerm);
			}
			
			return SUCCESS_LAST;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return FAIL;
	}
}