/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2021-09-14
 *
 */
package org.maclan;

import org.multipage.util.Resources;

/**
 * Area identifier.
 * @author vakol
 *
 */
public class AreaId extends SlotHolder {
	
	/**
	 * Output string format.
	 */
	private static String stringFormat = null;
	
	/**
	 * Area identifier.
	 */
	private Long areaId = null;
	
	/**
	 * Constructor.
	 * @param areaId
	 */
	public AreaId(Long areaId) {
		
		// Set area ID.
		this.areaId = areaId;
	}
	
	/**
	 * Get ID.
	 */
	@Override
	public long getId() {
		
		return this.areaId;
	}
	
	/**
	 * Get description.
	 * @param showId
	 * @return
	 */
	public String getDescriptionForced(boolean showId) {
		
		// Nothing to do.
		return toString();
	}

	/**
	 * Return description.
	 */
	@Override
	public String toString() {
		
		// Load string format.
		if (stringFormat == null) {
			stringFormat = Resources.getString("org.maclan.textAreaIdFormatter");
			
			if (stringFormat == null) {
				stringFormat = "[%d] area";
			}
		}
		
		return String.format(stringFormat, this.areaId);
	}
}
