/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan.server;

/**
 * Class for parameter object.
 * @author vakol
 *
 */
public class Parameter {
	
	/**
	 * Parameter type.
	 */
	private ProcedureParameterType parameterType;

	/**
	 * Constructor.
	 * @param parameterType
	 */
	public Parameter(ProcedureParameterType parameterType) {

		this.parameterType = parameterType;
	}

	/**
	 * @return the isReference
	 */
	public boolean isOutput() {
		
		return parameterType.isOutput();
	}

	/**
	 * Get parameter type.
	 */
	public ProcedureParameterType getParameterType() {
		
		return parameterType;
	}
}
