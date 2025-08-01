/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan.server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Properties;

import org.multipage.util.Obj;
import org.multipage.util.Resources;

/**
 * Class for stack of block descriptors.
 * @author vakol
 *
 */
public class BlockDescriptorsStack {

	/**
	 * Stack of block descriptors.
	 */
	private LinkedList<BlockDescriptor> stack;

	/**
	 * Constructor.
	 */
	public BlockDescriptorsStack() {
		
		stack = new LinkedList<BlockDescriptor>();
		// Push initial descriptor.
		pushBlockDescriptor(new BlockDescriptor());
	}

	/**
	 * Push block descriptor.
	 * @param blockDescriptor
	 * @return 
	 */
	public BlockDescriptor pushBlockDescriptor(BlockDescriptor blockDescriptor) {

		// Push new block descriptor into the stack.
		stack.addFirst(blockDescriptor);
		return blockDescriptor;
	}

	/**
	 * Push new block descriptor.
	 */
	public BlockDescriptor pushNewBlockDescriptor() {
		
		BlockDescriptor descriptor = new BlockDescriptor();
		stack.addFirst(descriptor);
		
		return descriptor;
	}

	/**
	 * Push new break block descriptor.
	 * @return
	 */
	public BreakBlockDescriptor pushNewBreakBlockDescriptor() {
		
		BreakBlockDescriptor descriptor = new BreakBlockDescriptor();
		stack.addFirst(descriptor);
		
		return descriptor;
	}
	
	/**
	 * Push JavaScript block descriptor
	 * @return
	 */
	public JavaScriptBlockDescriptor pushNewJavaScriptBlockDescriptor() {
		
		JavaScriptBlockDescriptor descriptor = new JavaScriptBlockDescriptor();
		stack.addFirst(descriptor);
		
		return descriptor;
	}
	
	/**
	 * Pop JavaScript descriptor.
	 * @param transparentProc - if is true, variables in this are added to the first super block
	 *                      but only if they don't already exist in the super block.
	 * @param transparentProc - if is true, procedures in this block are added to the first super block
	 *                      but only if they don't already exist in the super block.
	 */
	public String popJavaScriptDescriptor(boolean transparentVar, boolean transparentProc)
		throws Exception {
		
		BlockDescriptor descriptor = (BlockDescriptor) popBlockDescriptor(transparentVar, transparentProc);
		if (descriptor instanceof JavaScriptBlockDescriptor) {
			
			JavaScriptBlockDescriptor javaScriptDescriptor = (JavaScriptBlockDescriptor) descriptor;
			String result = javaScriptDescriptor.scriptOutputCapturer.stop();
			return result;
		}
		
		throw new Exception(Resources.getString("server.messageAttemptToPopJavaScriptBlockDescriptor"));
	}
	
	/**
	 * Pop block descriptor.
	 * @param transparent - if is true the variables and procedures are added to the first super block
	 *                      but only if they don't already exist in the super block.
	 */
	public void popBlockDescriptor(boolean transparent)
		throws Exception {
		
		popBlockDescriptor(transparent, transparent);
	}
	
	/**
	 * Pop block descriptor.
	 * @param transparentVariables  - if is true the variables are added to the first super block
	 *                                but only if they don't already exist in the super block.	 * 
	 * @param transparentProcedures - if is true the procedures are added to the first super block
	 *                                but only if they don't already exist in the super block.
	 */
	public BlockDescriptor popBlockDescriptor(boolean transparentVariables, boolean transparentProcedures)
		throws Exception {
		
		// If there is only initial descriptor throw exception.
		if (stack.size() <= 1) {
			throw new Exception(Resources.getString("server.messageAttemptToPopInitialBlockDescriptor"));
		}
		// Remove top of the stack.
		try {
			BlockDescriptor descriptor = stack.removeFirst();
			if (transparentVariables) {
				useVariables(descriptor);
			}
			if (transparentProcedures) {
				useProcedures(descriptor);
			}
			descriptor.afterBlockRemoved();
			
			return descriptor;
		}
		catch (NoSuchElementException e) {
			throw e;
		}
	}
	
	/**
	 * Pop all block descriptors above currentBlock
	 * @param currentBlock
	 */
	public void popToBlockDescriptor(BlockDescriptor currentBlock) {
		
		int currentIndex = stack.indexOf(currentBlock);
		if (currentIndex == -1) {
			return;
		}
		
		while (currentIndex-- > 0) {
			stack.removeFirst();
		}
	}
	
	/**
	 * Get current block descriptor.
	 * @return
	 */
	public BlockDescriptor getCurrentBlockDescriptor() {
		
		BlockDescriptor descriptor = stack.getFirst();
		return descriptor;
	}
	
	/**
	 * Uses procedures from descriptor in current block
	 * but only if the do not exist in the current block
	 * @param descriptor
	 */
	private void useProcedures(BlockDescriptor descriptor) 
		throws Exception {
		
		if (stack.isEmpty()) {
			return;
		}
		
		// Procedures
		Map<String, Procedure> currentProcedures = stack.getLast().procedures;
		for (Map.Entry<String, Procedure> entry : descriptor.procedures.entrySet()) {
			String key = entry.getKey();
			if (!currentProcedures.containsKey(key)) {
				currentProcedures.put(key, entry.getValue());
			}
		}
	}
	/**
	 * Uses variables from descriptor in current block
	 * but only if the do not exist in the current block
	 * @param descriptor
	 */
	private void useVariables(BlockDescriptor descriptor) 
		throws Exception {
		
		if (stack.isEmpty()) {
			return;
		}
		
		// Variables
		Map<String, Variable> currentVariables = stack.getLast().variables;
		for (Map.Entry<String, Variable> entry : descriptor.variables.entrySet()) {
			String key = entry.getKey();
			if (!currentVariables.containsKey(key)) {
				currentVariables.put(key, entry.getValue());
			}
		}
	}
	
	/**
	 * Get watch list of accessible variables.
	 * @param contextId
	 * @return
	 */
	public LinkedList<DebugWatchItem> getVariableWatchList() {
		
		LinkedList<DebugWatchItem> variableWatchList = new LinkedList<>();
		if (!stack.isEmpty()) {
			
			BlockDescriptor block = stack.getLast();
			String blockName = block.name;
			if (blockName == null || blockName.isEmpty()) {
				
				int blockHashCode = block.hashCode();
				blockName = String.valueOf(blockHashCode);
			}
			
			Map<String, Variable> localVariables = block.variables;
			for (Map.Entry<String, Variable> entry : localVariables.entrySet()) {
				
				String variableName = entry.getKey();
				String variableFullName = String.format("%s:%s", blockName, variableName);
				
				DebugWatchItem variableWatch = new DebugWatchItem(DebugWatchGroup.BLOCK_VARIABLE, variableName, variableFullName, null, null);
				
				variableWatchList.add(variableWatch);
			}
		}
		return variableWatchList;
	}

	/**
	 * Find variable.
	 * @param name
	 * @return
	 */
	public Variable findVariable(String name) {
		
		// Delegate the call.
		return findVariable(name, null);
	}
	
	/**
	 * Find variable and its block descriptor.
	 * @param name
	 * @return
	 */
	public Variable findVariable(String name, Obj<BlockDescriptor> foundBlockDescriptor) {
		
		// Initialize output.
		if (foundBlockDescriptor != null) {
			foundBlockDescriptor.ref = null;
		}
		
		// Do loop for existing block descriptors.
		for (BlockDescriptor blockDescriptor : stack) {

			Variable variable = blockDescriptor.getBlockVariable(name);
			if (variable != null) {
				
				if (foundBlockDescriptor != null) {
					foundBlockDescriptor.ref = blockDescriptor;
				}
				return variable;
			}
		}
		
		return null;
	}

	/**
	 * Find variable value.
	 * @param name
	 * @return
	 */
	public Object findVariableValue(String name)
		throws Exception {
		
		Variable variable = findVariable(name);
		if (variable == null) {
			throw new Exception(String.format(Resources.getString("server.messageVariableNotFound"), name));
		}
		return variable.value;
	}

	/**
	 * Set variable value.
	 * @param areaId
	 * @param name
	 * @param value
	 * @return
	 */
	public void setVariable(String name, Object value)
		throws Exception {
		
		// Parse name.
		String [] partNames = name.split("\\.");
		// Check name.
		if (partNames.length < 1) {
			throw new Exception(String.format(
					Resources.getString("server.messageCannotRecognizeVariableName"),
					name));
		}
		// Get variable name.
		String variableName = partNames[0];
		
		// Find variable.
		Variable variable = findVariable(variableName);
		if (variable == null) {
			throw new Exception(String.format(
				Resources.getString("server.messageVariableNotFoundWhileSetting"),
				name));
		}
		
		// Process sub variables.
		for (int index = 1; index < partNames.length; index++) {
			
			String partName = partNames[index];
			
			boolean error = false;
			
			Object variableValue = variable.value;
			if (variableValue instanceof Map) {
				
				// Try to get sub variable.
				Map map = (Map) variableValue;
				Object mapValue = map.get(partName);
				
				if (mapValue instanceof Variable) {
					variable = (Variable) mapValue;
				}
				else if (mapValue == null) {
					
					// Create new sub variable.
					Variable newVariable = new Variable(partName, null);
					map.put(partName, newVariable);
					
					variable = newVariable;
				}
				else {
					error = true;
				}
			}
			else if (variableValue == null) {
				
				// Create new sub variable.
				HashMap<String, Variable> subVariables = new HashMap<String, Variable>();
				Variable newVariable = new Variable(partName, null);
				
				subVariables.put(partName, newVariable);
				
				variable.value = subVariables;
				variable = newVariable;
			}
			else {
				error = true;
			}
			
			if (error) {
				// Throw error.
				AreaServer.throwError("server.messagePropertyNotResolved", partName);
			}
		}
		
		variable.value = value;
	}

	/**
	 * Get first block descriptor of given type.
	 * @param type
	 * @return
	 */
	public BlockDescriptor findFirstDescriptor(Class<?> type) {

		for (BlockDescriptor descriptor : stack) {
			if (isClassType(descriptor, type)) {
				return descriptor;
			}
		}
		return null;
	}

	/**
	 * Returns true if the object is of the given type.
	 * @param object
	 * @param type
	 * @return
	 */
	public static boolean isClassType(Object object, Class<?> type) {
		
		Class<?> objectClass = object.getClass();
		
		do {
			if (objectClass.equals(type)) {
				return true;
			}
			
			objectClass = objectClass.getSuperclass();
		}
		while (objectClass != null);
		
		return false;
	}

	/**
	 * Create variable.
	 * @param name
	 * @param value
	 * @return
	 */
	public Variable createVariable(String name, Object value)
		throws Exception {
		
		BlockDescriptor descriptor = stack.getFirst();

		return descriptor.createBlockVariable(name, value);
	}
	
	/**
	 * Create variable.
	 * @param name
	 * @param value
	 * @param superBlock
	 * @return
	 */
	public Variable createVariable(String name, Object value, long superBlock)
		throws Exception {
		
		if (superBlock < 0) {
			superBlock = 0;
		}
		long size = stack.size();
		if (size == 0) {
			AreaServer.throwError("server.messageFatalErrorNoBlockFound");
		}
		if (superBlock >= size) {
			superBlock = size - 1;
		}
		
		BlockDescriptor descriptor = stack.get((int) superBlock);

		return descriptor.createBlockVariable(name, value);
	}
	
	/**
	 * Create global variable.
	 * @param name
	 * @param value
	 * @return
	 */
	public Variable createGlobalVariable(String name, Object value)
		throws Exception {
		
		BlockDescriptor descriptor = stack.getLast();

		return descriptor.createBlockVariable(name, value);
	}
	
	/**
	 * Create variable in block with given name
	 * @param blockName
	 * @param variableName
	 * @param value
	 */
	public Variable createVariableIn(String blockName, String variableName, Object value)
			throws Exception {
		
		// Find first block with blockName
		Optional<BlockDescriptor> foundDescriptor = stack.stream().filter((BlockDescriptor descriptor) -> {
				return blockName.equals(descriptor.name);
				
			}).findFirst();
		
		// If not present, return null value
		if (!foundDescriptor.isPresent()) {
			return null;
		}
		
		// Create variable with initialized value in found block descriptor
		return foundDescriptor.get().createBlockVariable(variableName, value);
	}
	
	/**
	 * Add procedure.
	 * @param server 
	 * @param properties
	 * @param innerText
	 */
	public void addProcedure(AreaServer server, Properties properties, String innerText)
		throws Exception {

		// Get forced procedure name.
		String name = properties.getProperty("$name");
		boolean nameForced = true;
		
		if (name == null) {
			
			// Get normal procedure name.
			name = properties.getProperty("name");
			if (name == null) {
				throw new Exception(Resources.getString("server.messageExpectingProcedureName"));
			}
			
			nameForced = false;
		}
		
		// Evaluate name.
		name = server.evaluateText(name, String.class, false);
		
		// If procedure already exists.
		if (existsName(name)) {
			// Get use_last parameter.
			if (!properties.containsKey("$useLast")) {
				throw new Exception(String.format(
						Resources.getString("server.messageProcedureAlreadyExists"), name));
			}
		}
		
		// Get $global flag.
		boolean isGlobal = properties.containsKey("$global");
		
		// Get "return text" flag.
		boolean isReturnText = properties.containsKey("$returnText");
		
		// Get "full call with inner text" flag.
		boolean fullCall = properties.containsKey("$inner");
		
		// Get "transparent" flag.
		boolean isTransparent = properties.containsKey("$transparent");
		
		// Get parameters.
		Properties parameters = (Properties) properties.clone();
		
		if (nameForced) {
			parameters.remove("$name");
		}
		else {
			parameters.remove("name");
		}
		parameters.remove("$useLast");
		parameters.remove("$global");
		parameters.remove("$returnText");
		parameters.remove("$transparent");
		parameters.remove("$inner");
		
		// Create new procedure and add it to the list.
		Procedure procedure = new Procedure(parameters, innerText, nameForced, fullCall, isTransparent);
		procedure.setReturnText(isReturnText);
		
		// If it is a global procedure, use top descriptor, else use bottom descriptor.
		if (isGlobal) {
			stack.getLast().procedures.put(name, procedure);
		}
		else {
			stack.getFirst().procedures.put(name, procedure);
		}
	}

	/**
	 * Returns true value if the procedure already exists.
	 * @param name
	 * @return
	 */
	private boolean existsName(String name) {

		return stack.getFirst().procedures.containsKey(name);
	}
	
	/**
	 * Check if procedure exists
	 * @param procedureName
	 * @return
	 */
	public boolean definedProcedure(String procedureName) {
		
		// Find given procedure.
		for (BlockDescriptor block : stack) {
			if (block.procedures.containsKey(procedureName)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Get procedure with given name.
	 * @param procedureName
	 * @return
	 */
	public Procedure getProcedure(String procedureName) {

		// Find given procedure.
		for (BlockDescriptor block : stack) {
			Procedure procedure = block.procedures.get(procedureName);
			if (procedure != null) {
				return procedure;
			}
		}
		return null;
	}
	
	/**
	 * Get local procedure names.
	 * @return
	 */
	public LinkedList<DebugWatchItem> getLocalProcedureWatchList() {
		
		LinkedList<DebugWatchItem> procedureWatchList = new LinkedList<>();
		if (!stack.isEmpty()) {
			
			BlockDescriptor block = stack.getLast();
			String blockName = block.name;
			if (blockName == null || blockName.isEmpty()) {
				
				int blockHashCode = block.hashCode();
				blockName = String.valueOf(blockHashCode);
			}
			
			Map<String, Procedure> localProcedures = block.procedures;
			for (Map.Entry<String, Procedure> entry : localProcedures.entrySet()) {
				
				String procedureName = entry.getKey();
				String procedureFullName = String.format("%s:%s", blockName, procedureName);
				
				DebugWatchItem variableWatch = new DebugWatchItem(DebugWatchGroup.BLOCK_PROCEDURE, procedureName, procedureFullName, null, null);
				
				procedureWatchList.add(variableWatch);
			}
		}
		return procedureWatchList;
	}

	/**
	 * Get blocks count.
	 * @return
	 */
	public int getCount() {
		
		return stack.size();
	}

	public String trace(boolean decorated) {
		
		String traceText = decorated ? "<style>" +
				".TraceBlock {color: black; background-color: lightgreen}" +
				"</style>"
				: "";
		
		boolean isFirst = true;
		
		// Trace blocks.
		for (BlockDescriptor block : stack) {
			
			if (decorated) {
				traceText += "<span class='TraceBlock'>[" + (isFirst ? "" : "") + block.getClass().getSimpleName() + "]</span>";
			}
			else {
				traceText += "\n" + block.getClass().getSimpleName();
			}
			
			// Trace block.
			traceText += (decorated ? "<br>" : "\n") + block.trace(decorated);
			
			isFirst = false;
		}
		
		return traceText;
	}
}
