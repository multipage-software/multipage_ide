/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan.server;

import java.util.Map;

import org.maclan.expression.IdentifierSolver;

/**
 * Area identifier solver.
 * @author vakol
 *
 */
public class AreaIdentifierSolver extends IdentifierSolver {

	/**
	 * Area server reference.
	 */
	private AreaServer server;

	/**
	 * Constructor.
	 * @param server
	 */
	public AreaIdentifierSolver(AreaServer server) {

		this.server = server;
	}

	/**
	 * Get area server.
	 * @return
	 */
	public AreaServer getAreaServer() {
		
		return server;
	}

	/**
	 * Get identifier value.
	 */
	@Override
	public Object getValue(Object thisObject, String name) throws Exception {
		
		// If it is a root object.
		if (thisObject == null) {

			// Try to find variable.
			try {
				return server.state.blocks.findVariableValue(name);
			}
			catch (Exception e) {
			}
			
			if (name.equals("server")) {
				return server;
			}
			
			// Try to get default value.
			return super.getValue(thisObject, name);
		}
		
		// If this object is a Map.
		if (thisObject instanceof Map) {
			
			Map map = (Map) thisObject;
			Object mapValue = map.get(name);
			
			if (mapValue == null) {
				AreaServer.throwError("server.messageUnknownVariableProperty", name);
			}
			
			if (mapValue instanceof Variable) {
				
				Variable subVariable = (Variable) mapValue;
				return subVariable.value;
			}
			
			// Return sub variable.
			return mapValue;
		}
		
		
		// Get property value.
		Object returnedValue = LanguageElementsDescriptors.property(server, thisObject, name);
		if (returnedValue != null) {
			return returnedValue;
		}

		return null;
	}
}
