/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan;

import java.util.Objects;

/**
 * Class for descriptions that can be imported or exported.
 * @author vakol
 *
 */
public class DescriptionData {

	/**
	 * Identifier.
	 */
	public Long id;
	
	/**
	 * Description.
	 */
	public String description;

	/**
	 * New ID. (while importing)
	 */
	public Long newId;

	/**
	 * Constructor.
	 * @param id
	 * @param description
	 */
	public DescriptionData(Long id, String description) {
		
		this.id = id;
		this.description = description;
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
		DescriptionData other = (DescriptionData) obj;
		return Objects.equals(id, other.id);
	}
}
