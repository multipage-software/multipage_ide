/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan;

import java.util.Objects;

/**
 * Localized text.
 * @author vakol
 *
 */
public class LocText {

	public Long textId;
	public Long languageId;
	public String text;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LocText [textId=" + textId + ", languageId=" + languageId
				+ ", text=" + text + "]";
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
		LocText other = (LocText) obj;
		return Objects.equals(languageId, other.languageId);
	}
}