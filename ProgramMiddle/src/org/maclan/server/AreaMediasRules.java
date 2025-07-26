/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2020-03-10
 *
 */
package org.maclan.server;

/**
 * Helper class for CSS media rules.
 * @author vakol
 *
 */
class AreaMediasRules {

	public long areaId;
	public MediasRules mediasRules = new MediasRules();
	
	public AreaMediasRules(long areaId) {
		
		this.areaId = areaId;
	}

	public void insert(String media, String selector, String property, String value, boolean isImportant) {
		
		mediasRules.insert(media, selector, property, value, isImportant);
	}
}
