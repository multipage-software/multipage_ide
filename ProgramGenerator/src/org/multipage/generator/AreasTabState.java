/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2020-12-14
 *
 */
package org.multipage.generator;

import java.io.Serializable;

import org.multipage.util.Safe;

/**
 * State of tab that contains areas.
 * @author vakol
 *
 */
public class AreasTabState extends TabState implements Serializable {
	
	/**
	 * Version
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Area ID.
	 */
	public long areaId = 0L;
		
	/**
	 * Set this tab state from the input tab state
	 * @param tabState
	 */
	public void setTabStateFrom(AreasTabState tabState) {
		try {
			
			super.setTabStateFrom(tabState);
			
			areaId = tabState.areaId;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
