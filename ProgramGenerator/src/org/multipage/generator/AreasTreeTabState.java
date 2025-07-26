/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2020-12-14
 *
 */
package org.multipage.generator;

import java.io.Serializable;

/**
 * State of tab that displays tree of areas.
 * @author vakol
 *
 */
public class AreasTreeTabState extends AreasTabState implements Serializable {

	/**
	 * Version
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Set of area IDs currently displayed (expanded) in the tree view
	 */
	public Long [] displayedArea = null;
	
	/**
	 * Constructor
	 */
	public AreasTreeTabState() {
		
		type = TabType.areasTree;
	}
}
