/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.util.HashSet;

import javax.swing.event.ChangeEvent;

/**
 * Interface for the tab panel.
 * @author vakol
 *
 */
public interface TabItemInterface {

	/**
	 * Get tab description;
	 * @return
	 */
	String getTabDescription();
	
	/**
	 * Reload component.
	 */
	void reload();
	
	/**
	 * On tab panel change event.
	 * @param e
	 * @param selectedIndex
	 */
	void onTabPanelChange(ChangeEvent e, int selectedIndex);
	
	/**
	 * Before tab panel removed.
	 */
	void beforeTabPanelRemoved();
	
	/**
	 * Get tab state.
	 * @return
	 */
	TabState getTabState();
	
	/**
	 * Set reference to a tab label object
	 * @param tabLabel
	 */
	void setTabLabel(TabLabel tabLabel);
	
	/**
	 * Set area ID
	 * @param topAreaId
	 */
	void setAreaId(Long topAreaId);
	
	/**
	 * Get selected areas.
	 */
	HashSet<Long> getSelectedTabAreaIds();
	
	/**
	 * Called when tab panel needs to recreate contents.
	 */
	void recreateContent();
}
