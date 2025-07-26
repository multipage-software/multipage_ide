/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2020-02-11
 *
 */
package org.multipage.generator;

import org.maclan.Area;

/**
 * Interface for external provider editor.
 * @author vakol
 *
 */
public interface ExternalProviderInterface {
	
	/**
	 * Set editor from link string.
	 * @param area 
	 */
	void setEditor(String link, Area area);
}
