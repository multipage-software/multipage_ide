/*
 * Copyright 2010-2021 (C) vakol
 * 
 * Created on : 20-06-2021
 *
 */
package com.maclan.help.gnu_prolog;

import java.util.regex.Pattern;

import org.multipage.util.j;

import gnu.prolog.term.Term;
import gnu.prolog.vm.ExecuteOnlyCode;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologException;

/**
 * 
 * @author vakol
 *
 */
public class Predicate_wildcard_match extends ExecuteOnlyCode {
	
	/**
	 * Callback function.
	 */
	@Override
	public int execute(Interpreter interpreter, boolean backtrackMode, Term[] args) throws PrologException {
		
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
		
		j.log("PATTERN %s MATCH %s", patternTerm.toString(), textTerm.toString());
		
		// Create regular expression.
		pattern = pattern.replace("*", ".*?");
		boolean success = Pattern.matches(pattern, text);
		
		return success ? SUCCESS_LAST : FAIL;
	}
}