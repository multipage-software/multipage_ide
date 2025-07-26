/*
 * Copyright 2010-2020 (C) vakol
 * 
 * Created on : 2020-03-10
 *
 */
package org.maclan.server;

import java.util.LinkedList;

/**
 * Helper class for CSS media rules. 
 * @author vakol
 *
 */
public class AreasMediasRules {

	LinkedList<AreaMediasRules> areasMediasRules = new LinkedList<AreaMediasRules>();
	
	/**
	 * Insert new CSS rule.
	 * @param area
	 * @param media
	 * @param socketSelector
	 * @param property
	 * @param value
	 * @param isImportant 
	 */
	public void insert(long areaId, String media, String selector, String property, String value, boolean isImportant) {
		
		// Find area medias rules.
		AreaMediasRules areaMediasRules = null;
		
		for (AreaMediasRules areaMediasRulesItem : areasMediasRules) {
			if (areaMediasRulesItem.areaId == areaId) {
				
				areaMediasRules = areaMediasRulesItem;
				break;
			}
		}
		
		if (areaMediasRules == null) {
			areaMediasRules = new AreaMediasRules(areaId);
			areasMediasRules.add(areaMediasRules);
		}
		
		areaMediasRules.insert(media, selector, property, value, isImportant);
	}
	
	/**
	 * Clear rules.
	 */
	public void clear() {
		
		areasMediasRules.clear();
	}	
}
