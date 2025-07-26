/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.util.LinkedList;

import org.maclan.Area;
import org.maclan.VersionObj;
import org.multipage.util.Safe;

/**
 * Class for ambiguous file names.
 * @author vakol
 *
 */
public class AmbiguousFileName {
	
	/**
	 * File name.
	 */
	public String fileName;
	
	/**
	 * List of areas.
	 */
	public LinkedList<AmbiguousFileNameItem> items = new LinkedList<AmbiguousFileNameItem>();

	/**
	 * Constructor.
	 * @param fileName
	 */
	public AmbiguousFileName(String fileName) {
		
		this.fileName = fileName;
	}

	/**
	 * Add item.
	 * @param area
	 * @param version
	 */
	public void addItem(Area area, VersionObj version) {
		try {
			
			for (AmbiguousFileNameItem item : items) {
				if (item.area.equals(area) && item.version.equals(version)) {
					return;
				}
			}
			
			items.add(new AmbiguousFileNameItem(area, version));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Returns true if the file is ambiguous.
	 * @return
	 */
	public boolean isAmbiguous() {
		
		try {
			return items.size() > 1;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
}