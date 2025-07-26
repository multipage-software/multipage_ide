/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan;

import java.util.Objects;

/**
 * MIME data to import or export.
 * @author vakol
 *
 */
public class Mime {

	public Long mimeId;
	public String extension;
	public String type;
	public Boolean preference;
	
	// Auxiliary fields.
	public Long newId;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Mime [mimeId=" + mimeId + ", extension=" + extension
				+ ", type=" + type + ", preference=" + preference + "]";
	}

	/**
	 * Check if input object equals to this object.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Mime other = (Mime) obj;
		return Objects.equals(mimeId, other.mimeId);
	}
}