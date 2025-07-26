/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan;

/**
 * Enumeration object that can be imported or exported.
 * @author vakol
 *
 */
public class EnumerationData {

	/**
	 * Identifier.
	 */
	public long id;
	
	/**
	 * Description.
	 */
	public String description;

	/**
	 * New identifier.
	 */
	private long newId;

	/**
	 * "Created new" flag.
	 */
	private boolean createdNew = false;

	/**
	 * Set new ID.
	 * @param newId
	 */
	public void setNewId(long newId) {
		
		this.newId = newId;
	}

	/**
	 * Get new ID.
	 * @return
	 */
	public long getNewId() {
		
		return newId;
	}

	/**
	 * Set "created new" flag.
	 */
	public void setCreatedNew() {
		
		createdNew = true;
	}

	/**
	 * Returns true value if the enumeration is new.
	 * @return
	 */
	public boolean isCreatedNew() {
		
		return createdNew;
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
		EnumerationData other = (EnumerationData) obj;
		return id == other.id;
	}
}
