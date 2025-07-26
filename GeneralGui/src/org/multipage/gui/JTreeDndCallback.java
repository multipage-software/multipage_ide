/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.dnd.DropTargetDropEvent;

import javax.swing.tree.TreeNode;

/**
 * Interface for callback method invoked on node drop.
 * @author vakol
 *
 */
public interface JTreeDndCallback {
	
	/**
	 * On node dropped.
	 * @param droppedDndNode
	 * @param droppedNodeParent 
	 * @param transferedDndNode
	 * @param transferredNodeParent 
	 * @param e
	 */
	void onNodeDropped(DefaultMutableTreeNodeDnD droppedDndNode, TreeNode droppedNodeParent,
			DefaultMutableTreeNodeDnD transferedDndNode, TreeNode transferredNodeParent,
			DropTargetDropEvent e);
}
