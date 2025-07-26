/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2024-06-01
 *
 */
package org.maclan.server;

/**
 * Item of debugger watch list.
 * @author vakol
 */
public class DebugWatchItem {

	/**
	 * Item name.
	 */
	private String name = null;
	
	/**
	 * Item group.
	 */
	private DebugWatchGroup group = null;
	
	/**
	 * Item full name.
	 */
	private String fullName = null;
	
	/**
	 * Text rep[resentation of the item value.
	 */
	private String value = null;

	/**
	 * Value type.
	 */
	private String valueType = null;
	
	/**
	 * Constructor.
	 * @param name
	 * @param type
	 */
	public DebugWatchItem(String name, DebugWatchGroup type) {
		
		this.name = name;
		this.group = type;
	}

	/**
	 * Constructor.
	 * @param type
	 * @param name
	 * @param fullName
	 * @param value
	 * @param valueType
	 */
	public DebugWatchItem(DebugWatchGroup type, String name, String fullName, String value, String valueType) {
		
		this.name = name;
		this.group = type;
		this.fullName = fullName;
		this.value = value;
		this.valueType = valueType;
	}

	/**
	 * Get watch item name.
	 * @return
	 */
	public String getName() {
		
		return name;
	}
	
	/**
	 * Get watch item full name.
	 * @return
	 */
	public String getFullName() {
		
		return fullName;
	}
	
	/**
	 * Get watch item group.
	 * @return
	 */
	public DebugWatchGroup getGroup() {
		
		return group;
	}
	
	/**
	 * Get group name.
	 * @return
	 */
	public String getGroupName() {
		
		if (group == null) {
			return "unknown";
		}
		String typeName = group.name();
		return typeName;
	}
	
	/**
	 * Get item value.
	 * @return
	 */
	public String getValue() {
		
		return value;
	}
	
	/**
	 * Get watched value.
	 * @return
	 */
	public String getWatchedValue() {
		
		// Remove whitespaces.
		String watchedValue = value.trim();
		watchedValue = watchedValue.replaceAll("\\s+", " ");
		
		return watchedValue;
	}
	
	/**
	 * Get watch item value type.
	 * @return
	 */
	public String getValueType() {
		
		return valueType;
	}

	/**
	 * Returns true value if the name and property type matches.
	 * @param name
	 * @param type
	 * @return
	 */
	public boolean matches(String name, DebugWatchGroup type) {
		
		if (name == null || type == null) {
			return false;
		}
		
		boolean matches = name.equals(this.name) && type.equals(this.group);
		return matches;
	}
	
	/**
	 * Get text representation of the watch item.
	 */
	@Override
	public String toString() {
		
		String watchItemName;
		if (fullName != null && !fullName.isEmpty()) {
			watchItemName = fullName;
		}
		else {
			watchItemName = name;
		}
		return watchItemName;
	}
}
