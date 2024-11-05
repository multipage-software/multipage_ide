/*
 * Copyright 2010-2024 (C) vakol
 * 
 * Created on : 01-06-2024
 *
 */
package org.maclan.server;

import org.multipage.util.Resources;

/**
 * Debugger watch list group.
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
	 * Check if type name matches this enum item.
	 * @param typeName
	 * @return
	 */
	public boolean checkTypeName(String typeName) {
		
		String enumName = super.name();
		boolean matches = enumName.equals(typeName);
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
