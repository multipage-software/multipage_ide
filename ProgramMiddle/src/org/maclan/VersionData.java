/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan;

/**
 * Vrsion object that can be imported or exported.
 * @author vakol
 *
 */
public class VersionData implements Element {

	/**
	 * Identifier.
	 */
	private long id;
	
	/**
	 * New ID.
	 */
	private Long newId;
	
	/**
	 * Alias.
	 */
	private String alias;
	
	/**
	 * Description ID.
	 */
	private Long descriptionId;
	
	/**
	 * Constructor.
	 */
	public VersionData() {
		
		this.id = 0L;
		this.newId = null;
		this.alias = "";
		this.descriptionId = null;
	}
	/**
	 * Constructor.
	 * @param id
	 * @param alias
	 * @param descriptionId
	 */
	public VersionData(long id, String alias, Long descriptionId) {
		
		this.id = id;
		this.newId = null;
		this.alias = alias;
		this.descriptionId = descriptionId;
	}

	/**
	 * Set ID.
	 * @param id
	 */
	public void setId(long id) {
		
		this.id = id;
	}

	/**
	 * Set alias.
	 * @param alias
	 */
	public void setAlias(String alias) {
		
		this.alias = alias;
	}

	/**
	 * Set description ID.
	 * @param descriptionId
	 */
	public void setDescriptionId(Long descriptionId) {
		
		this.descriptionId = descriptionId;
	}

	/**
	 * Get ID.
	 */
	@Override
	public long getId() {
		
		return id;
	}

	/**
	 * @return the alias
	 */
	public String getAlias() {
		
		return alias;
	}

	/**
	 * @return the descriptionId
	 */
	public Long getDescriptionId() {
		
		return descriptionId;
	}
	
	/**
	 * Set new ID.
	 * @param newId
	 */
	public void setNewId(Long newId) {
		
		this.newId = newId;
	}
	
	/**
	 * @return the newId
	 */
	public Long getNewId() {
		return newId;
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
		VersionData other = (VersionData) obj;
		return id == other.id;
	}
}
