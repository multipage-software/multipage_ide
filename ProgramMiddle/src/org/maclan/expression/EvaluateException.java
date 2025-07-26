/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */
package org.maclan.expression;

import org.multipage.util.Resources;

/**
 * Eexpression evaluation exception.
 * @author vakol
 *
 */
public class EvaluateException extends Exception {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public EvaluateException(ExpressionElement element, String message) {
		super(String.format(Resources.getString("middle.messageEvaluateException"),
				element.toString(), message));
	}
}
