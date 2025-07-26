/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan;

/**
 * Area source for import and export.
 * @author vakol
 *
 */
public class AreaSourceData {

	/**
	 * Resource ID.
	 */
	public long resourceId;
	
	/**
	 * Version ID.
	 */
	public long versionId;
	
	/**
	 * Not localized flag.
	 */
	public boolean notLocalized;

	/**
	 * Constructor.
	 * @param resourceId
	 * @param notLocalized
	 * @param versionId
	 */
	public AreaSourceData(long resourceId, long versionId, boolean notLocalized) {

		this.resourceId = resourceId;
		this.versionId = versionId;
		this.notLocalized = notLocalized;
	}
}
