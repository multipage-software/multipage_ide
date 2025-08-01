/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan.server;

import java.util.LinkedList;
import java.util.Map.Entry;

import org.multipage.util.Obj;
import org.multipage.util.Resources;

/**
 * Block descriptor for procedures.
 * @author vakol
 *
 */
public class ProcedureBlockDescriptor extends BlockDescriptor {

	/**
	 * Referenced variables.
	 */
	private LinkedList<OutputVariable> outputs = new LinkedList<OutputVariable>();
	
	/**
	 * Constructor.
	 * @param procedure
	 */
	public ProcedureBlockDescriptor(Procedure procedure, ParameterInitializeListener listener)
		throws Exception {
		
		// Create variables.
		for (Entry<String, Parameter> parameterEntry : procedure.getParameters().entrySet()) {
			
			// Create block variable.
			String parameterName = parameterEntry.getKey();
			Parameter parameter = parameterEntry.getValue();
			
			boolean isOutput = parameter.isOutput();
			ProcedureParameterType type = parameter.getParameterType();
			
			try {
				// Get initial value.
				Obj<Variable> outputVariable = new Obj<Variable>();
				Object value = listener.getValue(parameterName, type, outputVariable);
				
				Variable localVariable = createBlockVariable(parameterName, value);
				
				// If the variable is output add new output.
				if (isOutput) {
					outputs.add(new OutputVariable(outputVariable.ref, localVariable));
				}
			}
			catch (Exception e) {
				throw new Exception(String.format(
						Resources.getString("server.messageCannotCreateParameter"), parameterName,
						e.getLocalizedMessage()));
			}
		}
	}

	/* (non-Javadoc)
	 * @see program.server.BlockDescriptor#onBlockLeave()
	 */
	@Override
	public void afterBlockRemoved() {

		// Resolve references.
		for (OutputVariable reference : outputs) {
			reference.saveLocalInOutput();
		}
	}
}

/**
 * 
 * @author
 *
 */
class OutputVariable {

	/**
	 * Output variable.
	 */
	private Variable outputVariable;
	
	/**
	 * Local variable.
	 */
	private Variable localVariable;

	/**
	 * Constructor.
	 * @param outputVariable
	 * @param localVariable
	 */
	public OutputVariable(Variable outputVariable, Variable localVariable) {

		this.outputVariable = outputVariable;
		this.localVariable = localVariable;
	}

	/**
	 * Save local value in the output variable.
	 */
	public void saveLocalInOutput() {

		if (outputVariable != null) {
			outputVariable.value = localVariable.value;
		}
	}
}
