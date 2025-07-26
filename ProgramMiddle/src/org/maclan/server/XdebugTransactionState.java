/**
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2024-04-18
 *
 */
package org.maclan.server;

/**
 * Enumeration of Xdebug transaction states.
 * @author vakol
 */
public enum XdebugTransactionState {
		
	/**
	 * Transaction is created.
	 */
	created,
	
	/**
	 * Transaction is scheduled in transaction list.
	 */
	scheduled,
	
	/**
	 * Transaction has sent all data.
	 */
	sent
}
