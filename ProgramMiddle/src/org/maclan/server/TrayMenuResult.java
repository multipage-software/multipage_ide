/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2020-04-27
 *
 */
package org.maclan.server;

import java.util.LinkedList;

/**
 * Result of tray menu action.
 * @author vakol
 *
 */
public class TrayMenuResult {
	
	/**
	 * Class item.
	 */
	public static class Item {
		
		/**
		 * Name of trayMenu item.
		 */
		public String name;
		
		/**
		 * Action for trayMenu item.
		 */
		public String action;
		
		/**
		 * Constructor.
		 * @param name
		 * @param action
		 */
		public Item(String name, String action) {
			
			this.name = name;
			this.action = action;
		}
	}
	
	/**
	 * Area server result generated for Sync trayMenu.
	 */
	private LinkedList<Item> items = new LinkedList<Item>();
	
	/**
	 * Add tray trayMenu result.
	 * @param name
	 * @param action
	 */
	public void add(String name, String action) {
		
		items.add(new Item(name, action));
	}
	
	/**
	 * Get items.
	 * @return
	 */
	public LinkedList<Item> getItems() {
		
		return items;
	}
}
