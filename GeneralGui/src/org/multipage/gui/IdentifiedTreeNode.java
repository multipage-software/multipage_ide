/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.util.LinkedList;

/**
 * Tree node with identifier.
 * @author vakol
 *
 */
public interface IdentifiedTreeNode {

	/**
	 * Get identifier.
	 * @return
	 */
	long getId();

	/**
	 * Get list of children.
	 * @return
	 */
	LinkedList<? extends Object> getChildren();
}
