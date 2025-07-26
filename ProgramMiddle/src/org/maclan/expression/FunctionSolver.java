/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */
package org.maclan.expression;

import org.multipage.util.Resources;

/**
 * Base class for function solvers.
 * @author vakol
 *
 */
public class FunctionSolver {

	/**
	 * Get identifier value.
	 * @param thisObject 
	 * @param name
	 * @return
	 */
	public Object getValue(Object thisObject, String name, Object [] parameters) throws Exception {

		throw new Exception(String.format(
				Resources.getString("middle.messageUnknownFunction"), name, parameters.length));
	}
}
