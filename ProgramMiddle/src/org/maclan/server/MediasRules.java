/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2020-03-10
 *
 */
package org.maclan.server;

import java.util.LinkedList;

/**
 * Rules for CSS media.
 * @author vakol
 *
 */
class MediasRules {
	
	LinkedList<MediaRules> mediasRules = new LinkedList<MediaRules>();

	/**
	 * Insert media rules.
	 * @param media
	 * @param socketSelector
	 * @param property
	 * @param value
	 * @param isImportant 
	 */
	public void insert(String media, String selector, String property, String value, boolean isImportant) {
		
		// Find media rules.
		MediaRules mediaRules = null;
		
		for (MediaRules mediaRulesItem : mediasRules) {
			if (mediaRulesItem.media.equals(media)) {
				
				mediaRules = mediaRulesItem;
				break;
			}
		}
		if (mediaRules == null) {
			mediaRules = new MediaRules(media);
			mediasRules.add(mediaRules);
		}
		
		// Insert socketSelector rules.
		mediaRules.insert(selector, property, value, isImportant);
	}
}
