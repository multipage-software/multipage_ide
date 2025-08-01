/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2021-06-20
 *
 */
package org.maclan.help.gnu_prolog;

import java.util.regex.Pattern;

import org.multipage.util.Safe;

import gnu.prolog.term.Term;
import gnu.prolog.vm.ExecuteOnlyCode;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologException;

/**
 * Implementation of Prolog predicate that does wildcard matching.
 * @author vakol
 *
 */
public class Predicate_wildcard_match extends ExecuteOnlyCode {
	
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
			Term patternTerm = args[0];
			Term textTerm = args[1];
			
			String pattern = patternTerm.dereference().toString();
			String text = textTerm.dereference().toString();
			
			// Remove apostrophes.
			pattern = PrologUtility.removeApostrophes(pattern);
			text = PrologUtility.removeApostrophes(text);
			
			// Create regular expression.
			pattern = pattern.replace("*", ".*?");
			boolean success = Pattern.matches(pattern, text);
			
			return success ? SUCCESS_LAST : FAIL;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return FAIL;
	}
}