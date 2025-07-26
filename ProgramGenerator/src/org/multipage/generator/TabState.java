/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 12-2020-12-14
 *
 */
package org.multipage.generator;

import java.io.Serializable;

/**
 * Tab state.
 * @author vakol
 *
 */
public class TabState implements Serializable {

	/**
	 * Version
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Type of tab content.
	 */
	public TabType type = TabType.unknown;
	
	/**
	 * Title of the tab displayed with label
	 */
	public String title = "";
			
	/**
	 * Set this tab state from the input tab state
	 * @param tabState
	 */
	public void setTabStateFrom(TabState tabState) {
		
		type = tabState.type;
		title = tabState.title;
	}
}
