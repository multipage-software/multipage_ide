/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */
package org.multipage.generator;

import org.maclan.Area;
import org.multipage.util.Safe;

/**
 * Class for table item that holds information about area coordinates.
 * @author vakol
 */
class AreaCoordinatesTableItem  {
	
	/**
     * Area coordinate.
     */
	AreaCoordinates coordinate;

	/**
	 * Constructor.
	 * @param coordinate
	 */
	public AreaCoordinatesTableItem(AreaCoordinates coordinate) {
		
		this.coordinate = coordinate;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		try {
			if (coordinate != null) {
				
				Area parentArea = coordinate.getParentArea();
				
				if (parentArea != null) {
					return parentArea.getDescriptionForDiagram();
				}
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Convert to decorated string.
	 */
	public String toDecoratedString() {
		
		try {
			if (coordinate != null) {
				
				Area parentArea = coordinate.getParentArea();
				
				if (parentArea != null) {
					return parentArea.getDescriptionForcedAndDecorated(true);
				}
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
}