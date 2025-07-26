/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2020-12-14
 *
 */

package org.multipage.generator;

import java.io.Serializable;

/**
 * Monitor tab state.
 * @author vakol
 *
 */
public class MonitorTabState extends AreasTabState implements Serializable {

	/**
	 * Version
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * URL string
	 */
	public String url = "localhost";
	
	/**
	 * Constructor
	 */
	public MonitorTabState() {
		
		type = TabType.monitor;
	}
}
