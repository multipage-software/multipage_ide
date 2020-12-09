/*
 * Copyright 2010-2017 (C) sechance
 * 
 * Created on : 26-04-2017
 *
 */

package com.maclan.server;

/**
 * @author
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
