/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan;

import java.util.Objects;

/**
 * Language reference.
 * @author vakol
 *
 */
public class LanguageRef {

	public Long id;
	public String alias;
	public String description;
	public long priority;

	public Long dataStart;
	public Long dataEnd;
	
	// Auxiliary fields.
	public long newId;

	/**
	 * Returns true value if flag exists.
	 * @return
	 */
	public boolean existsFlag() {
		
		if (dataStart == null || dataEnd == null) {
			return false;
		}
		
		return dataStart < dataEnd;
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
		LanguageRef other = (LanguageRef) obj;
		return Objects.equals(id, other.id);
	}
}