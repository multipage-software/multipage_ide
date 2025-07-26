/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import org.maclan.Area;
import org.maclan.VersionObj;

/**
 * Item objects for ambiguous file.
 * @author vakol
 *
 */
public class AmbiguousFileNameItem {
	
	/**
	 * Area.
	 */
	public Area area;
	
	/**
	 * Version.
	 */
	public VersionObj version;
	
	/**
	 * Constructor.
	 * @param area
	 * @param version
	 */
	public AmbiguousFileNameItem(Area area, VersionObj version) {
		
		this.area = area;
		this.version = version;
	}
}