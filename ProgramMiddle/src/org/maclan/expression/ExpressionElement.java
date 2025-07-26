/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */
package org.maclan.expression;

/**
 * Interface for expression elements.
 * @author vakol
 *
 */
public interface ExpressionElement {

	/**
	 * Get expression element name.
	 * @return
	 */
	String toString();

	/**
	 * Get child count.
	 * @return
	 */
	int getChildCount();

	/**
	 * Get child.
	 * @return
	 */
	ExpressionElement getChild(int index);

	/**
	 * Evaluates this expression element.
	 * @param identifierSolver
	 * @return
	 * @throws Exception
	 */
	Object getValueObject(IdentifierSolver identifierSolver,
			FunctionSolver functionSolver)  throws Exception;
}