/*
 * Copyright 2010-2026 (C) vakol
 * 
 * Created on : 2026-02-13
 *
 */
package org.multipage.gui;

import java.lang.reflect.Field;

/**
 * Helper class that holds caller information (class name, parameter) taken from stack.
 */
final class StateStreamRecordInfo {
	
	/**
	 * Record source name.
	 */
	@SuppressWarnings("unused")
	public String source;
	
	/**
	 * Record value. 			
	 */
	@SuppressWarnings("unused")
	public Object value;
	
	/**
	 * Constructor.
	 * @param source
	 */
	public StateStreamRecordInfo(Class<?> theClass) {
		
		// Increment class record counter.
		Integer count = StateOutputStreamImpl.classRecordCounter.get(theClass);
		if (count == null) {
			count = 1;
		}
		
		// Try to find field with ProgramState annotation that matches the count index.
		Field[] fields = theClass.getDeclaredFields();
		for (Field field : fields) {
			
			ProgramState [] state = field.getAnnotationsByType(ProgramState.class);
			if (state == null || state.length < 1) {
				continue;
			}
			int valueIndex = state[0].value();
			if (valueIndex == count) {
				
				// Use class and field names as source.
				this.source = theClass.getSimpleName() + "." + field.getName();
				break;
			}
		}
		
		// If no field found use value index in the source.
		if (this.source == null) {
			this.source = theClass.getSimpleName() + ":" + String.valueOf(count);
		}
		
		// Increment class record counter.
		count++;
		StateOutputStreamImpl.classRecordCounter.put(theClass, count);
	}
	
	/**
	 * Set value.
	 * @param value
	 * @return
	 */
	public StateStreamRecordInfo wrap(Object value) {
		
		this.value = value;
		return this;
	}
}