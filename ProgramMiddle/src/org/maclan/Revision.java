/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Class representing slot revisions.
 * @author vakol
 *
 */
public class Revision {
	
	/**
	 * Format of date and time.
	 */
	private static SimpleDateFormat dateTimeFormat;
	
	/**
	 * Static constructor.
	 */
	static {
		dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * Revision number.
	 */
	public Long number;
	
	/**
     * Description of revision.
     */
	public String description;
	
	/**
	 * Time stamp.
	 */
	public Timestamp created;

	/**
	 * Slot ID.
	 */
	public long slotId;
	
	/**
	 * Get revision creation string.
	 * @return
	 */
	public String getCreationString() {
		
	    String formattedDateTime = dateTimeFormat.format(created);
	    return formattedDateTime;
	}
	
	/**
	 * Get string representation of this revision.
	 */
	@Override
	public String toString() {
		
		String creationDateTime = getCreationString();
		String revisionDescription = description != null ? "   " + description : "";
		return String.format("[%03d] %s%s", number, creationDateTime, revisionDescription);
	}
	
	/**
	 * Get string. Revision number is replaced with "*", if it is the last revision.
	 * @param last
	 * @return
	 */
	public String toString(boolean last) {
		
		String creationDateTime = getCreationString();
		String revisionDescription = description != null ? "   " + description : "";
		return last ? String.format("[ * ] %s%s", creationDateTime, revisionDescription) : toString();
	}
}
