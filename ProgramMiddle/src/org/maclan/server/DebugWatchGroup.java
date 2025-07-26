/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2024-06-01
 *
 */
package org.maclan.server;

import org.multipage.util.Resources;

/**
 * Debugger watch group.
 * @author vakol
 */
public enum DebugWatchGroup {

	/**
	 * Enumeration of watch list item types.
	 */
	TAG_PROPERTY("org.maclan.server.textDebugWatchTagProperty"),
	BLOCK_VARIABLE("org.maclan.server.textDebugWatchBlockVariable"),
	BLOCK_PROCEDURE("org.maclan.server.textDebugWatchBlockProcedure"),
	AREA("org.maclan.server.textDebugWatchArea"),
	SERVER("org.maclan.server.textDebugWatchServer");
	
	/**
	 * Description of the type.
	 */
	private String descriptionId = null;
	private String description = null;
	
	/**
	 * Constructor.
	 * @param descriptionId
	 */
	DebugWatchGroup(String descriptionId) {
		
		this.descriptionId = descriptionId; 
	}
	
	/**
	 * Get type name.
	 * @return
	 */
	public String getName() {
		
		String name = name();
		return name;
	}

	/**
	 * Get type description.
	 */
	@Override
	public String toString() {
		
		if (description == null) {
			description = Resources.getString(descriptionId);
		}
		return description;
	}
	
	/**
	 * Check if group name matches this enum item.
	 * @param groupName
	 * @return
	 */
	public boolean checkGroupName(String groupName) {
		
		String enumName = super.name();
		boolean matches = enumName.equals(groupName);
		return matches;
	}
	
	/**
	 * Get enumeration value by its name.
	 * @param typeName
	 * @return
	 */
	public static DebugWatchGroup getByName(String typeName) {
		
		DebugWatchGroup [] enumValues = DebugWatchGroup.values();
		for (DebugWatchGroup enumValue : enumValues) {
			
			String valueName = enumValue.name();
			if (valueName.equals(typeName)) {
				return enumValue;
			}
		}
		return null;
	}
}
