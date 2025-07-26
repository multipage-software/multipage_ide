/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;

import org.multipage.util.Safe;

/**
 * Class for paths in tree containing nodes that are identified by ID.
 * @author vakol
 *
 */
public class IdentifierTreePath implements Serializable {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Identifier path.
	 */
	private long [] identifierPath;

	/**
	 * Constructor.
	 * @param path
	 */
	public IdentifierTreePath(TreePath path) {
		try {
			
			int nodeCount = path.getPathCount();
			
			// Create identifier array.
			identifierPath = new long [nodeCount];
			int index = 0;
			
			for (Object node : path.getPath()) {
				
				identifierPath[index] = -1;
				
				if (node instanceof IdentifiedTreeNode) {
					identifierPath[index] = ((IdentifiedTreeNode) node).getId();
				}
				if (node instanceof DefaultMutableTreeNode) {
					Object userObject = ((DefaultMutableTreeNode) node).getUserObject();
					
					if (userObject instanceof IdentifiedTreeNode) {
						identifierPath[index] = ((IdentifiedTreeNode) userObject).getId();
					}
				}
				
				index++;
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Expand tree path.
	 * @param tree
	 */
	public void expandTreePath(JTree tree) {
		try {
			
			// Get tree model.
			TreeModel model = tree.getModel();
			
			int level = 0;
			
			// Get root node.
			Object rootNode = model.getRoot();
			
			LinkedList<Object> pathNodesList = new LinkedList<Object>();
			
			// Load path nodes list.
			if (loadTreePathRecursive(rootNode, level, pathNodesList)) {
				
				pathNodesList.addFirst(rootNode);
				
				// Create tree path.
				TreePath treePath = new TreePath(pathNodesList.toArray());
				
				// Expand given path.
				tree.expandPath(treePath);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Expand tree recursive.
	 * @param node
	 * @param level
	 * @param pathNodesList 
	 */
	private boolean loadTreePathRecursive(Object node, int level, LinkedList<Object> pathNodesList) {
		
		try {
			IdentifiedTreeNode identifiedNode = null;
			List children = null;
			
			// Get node content.
			if (node instanceof IdentifiedTreeNode) {
				
				identifiedNode = (IdentifiedTreeNode) node;
				children = identifiedNode.getChildren();
			}
			
			if (node instanceof DefaultMutableTreeNode) {
				
				DefaultMutableTreeNode mutableNode = (DefaultMutableTreeNode) node;
				Object userObject = mutableNode.getUserObject();
				
				if (userObject instanceof IdentifiedTreeNode) {
					
					identifiedNode = (IdentifiedTreeNode) userObject;
					children = Collections.list(mutableNode.children());
				}
			}
			
			if (identifiedNode == null) {
				return false;
			}
			
			// Check node ID.
			if (identifiedNode.getId() != identifierPath[level]) {
				return false;
			}
			
			// If it is the last level, exit with true value.
			if (level >= identifierPath.length - 1) {
				
				return true;
			}
			
			// Increment level.
			level++;
			
			// Do loop for all children. Call this method recursively.
			for (Object child : children) {
				if (loadTreePathRecursive(child, level, pathNodesList)) {
					
					pathNodesList.addFirst(child);
					return true;
				}
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Add tree path selection.
	 * @param tree
	 */
	public void addSelection(JTree tree) {
		try {
			
			// Get tree model.
			TreeModel model = tree.getModel();
			
			int level = 0;
			
			// Get root node.
			Object rootNode = model.getRoot();
			
			LinkedList<Object> pathNodesList = new LinkedList<Object>();
			
			// Load path nodes list.
			if (loadTreePathRecursive(rootNode, level, pathNodesList)) {
				
				pathNodesList.addFirst(rootNode);
				
				// Create tree path.
				TreePath treePath = new TreePath(pathNodesList.toArray());
				
				// Add given path selection.
				tree.addSelectionPath(treePath);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
