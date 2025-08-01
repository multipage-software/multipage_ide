/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan.server;

import org.maclan.expression.FunctionSolver;
import org.multipage.util.Obj;

/**
 * Class for area server function solver.
 * @author vakol
 *
 */
public class AreaFunctionSolver extends FunctionSolver {
	
	/**
	 * Area server reference.
	 */
	private AreaServer server;

	/**
	 * Constructor.
	 * @param server
	 */
	public AreaFunctionSolver(AreaServer server) {
		
		this.server = server;
	}

	/**
	 * Get function value.
	 */
	@Override
	public Object getValue(Object thisObject, String name, Object[] parameters)
		throws Exception {

		// Try to get default function result.
		try {
			return super.getValue(thisObject, name, parameters);
		}
		catch (Exception e) {
			
		}
		
		Obj<Object> returnedValue = new Obj<Object>();
		
		// Try to call user defined procedure.
		boolean success = server.callProcedure(name, parameters, returnedValue);
		if (success) {
			return returnedValue.ref;
		}
		
		// Call appropriate method.
		returnedValue.ref = LanguageElementsDescriptors.method(server, thisObject, name, parameters);
		if (returnedValue.ref != null) {
			return returnedValue.ref;
		}

		return null;
	}
}
