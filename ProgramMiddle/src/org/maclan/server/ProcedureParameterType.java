/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan.server;

/**
 * Enumeration of procedure parameter types.
 * @author vakol
 *
 */
public enum ProcedureParameterType {
	
	/**
	 * Procedure parameter types.
	 */
	input,
	output,
	returned,
	resultText;

	/**
	 * Returns true value if it is an output type.
	 * @return
	 */
	public boolean isOutput() {
		
		return this == ProcedureParameterType.output
				|| this == ProcedureParameterType.returned
				|| this == ProcedureParameterType.resultText;
	}
}
