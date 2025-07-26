/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.util.Enumeration;
import java.util.LinkedList;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.maclan.Area;
import org.multipage.util.Safe;

/**
 * State of tree that displays areas.
 * @author vakol
 */
public class AreaTreeState {

	/**
	 * Selected tree paths.
	 */
	private LinkedList<Long []> selectedAreasIdPaths = new LinkedList<Long []>();
		
	/**
	 * Expanded area IDs.
	 */
	private LinkedList<Long []> expandedAreasIdPaths = new LinkedList<Long []>();
	
	/**
	 * Add selected area ID.
	 * @param idPath
	 */
	public void addSelectedAreaIdPath(Long [] idPath) {
		try {
			
			selectedAreasIdPaths.add(idPath);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Add selected paths.
	 * @param selectedPaths
	 */
	public void addSelectedPaths(TreePath[] selectedPaths) {
		try {
			
			// Add each path.
			for (TreePath treePath : selectedPaths) {
				
				Long [] idPath = getAreaIdPath(treePath);
				addSelectedAreaIdPath(idPath);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Add expanded area ID.
	 * @param idPath
	 */
	public void addExpandedAreaIdPath(Long [] idPath) {
		try {
			
			expandedAreasIdPaths.add(idPath);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Add expanded paths.
	 * @param expandedPaths
	 */
	public void addExpandedPaths(TreePath[] expandedPaths) {
		try {
			
			// Add each path.
			for (TreePath treePath : expandedPaths) {
				
				Long [] idPath = getAreaIdPath(treePath);
				addExpandedAreaIdPath(idPath);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Add selected and expanded paths.
	 * @param tree
	 * @param selectedPaths
	 */
	public static AreaTreeState addSelectionAndExpandIt(JTree tree, TreePath[] selectedPaths) {
		
		try {
			AreaTreeState treeState = getTreeState(tree);
			
			treeState.addSelectedPaths(selectedPaths);
			treeState.addExpandedPaths(selectedPaths);
			
			Safe.invokeLater(() -> {
				AreaTreeState.applyTreeState(treeState, tree);
			});
			
			return treeState;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Get node path.
	 * @param node
	 * @param areaIdPath
	 * @param index
	 * @param nodePath
	 */
	public static boolean getNodePath(DefaultMutableTreeNode node,
			Long [] areaIdPath, int index, DefaultMutableTreeNode [] nodePath) {
		
		try {
			// Check input values.
			if (node == null || areaIdPath == null || index < 0 || index >= areaIdPath.length || nodePath == null) {
				return true;
			}
			
			long areaId = areaIdPath[index];
			
			Area currentArea = null;
			
			Object userObject = node.getUserObject();
			if (userObject instanceof Long) {
				
				long currentAreaId = (Long) userObject;
				currentArea = ProgramGenerator.getArea(currentAreaId);
			}
			else if (userObject instanceof Area) {
				currentArea = (Area) userObject;
			}
			else {
				return false;
			}
			
			// If current area ID doesn't match, exit with false.
			if (currentArea.getId() != areaId) {
				return false;
			}
			
			// Set node path element.
			nodePath[index] = node;
			
			// Call this method recursively for all sub nodes.
			Enumeration<?> nodeChildren = node.children();
			while (nodeChildren.hasMoreElements()) {
				
				// Check child object.
				Object childObject = nodeChildren.nextElement();
				if (!(childObject instanceof DefaultMutableTreeNode)) {
					continue;
				}
				
				// Get child node.
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) childObject;
				
				boolean isCorrect = getNodePath(childNode, areaIdPath, index + 1, nodePath);
				if (isCorrect) {
					return true;
				}
			}
			
			return node.getChildCount() == 0;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Apply tree state.
	 * @param tree
	 * @param treeState
	 */
	public static void applyTreeState(AreaTreeState treeState, JTree tree) {
		try {
			
			// Get tree model.
			TreeModel treeModelBase = tree.getModel();
			if (!(treeModelBase instanceof DefaultTreeModel)) {
				return;
			}
			
			DefaultTreeModel treeModel = (DefaultTreeModel) treeModelBase;
			
			// Get root node.
			DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
			if (rootNode == null) {
				return;
			}
					
			// Expand given areas.
			for (Long [] areaIdPath : treeState.expandedAreasIdPaths) {
				
				DefaultMutableTreeNode [] nodePath = new DefaultMutableTreeNode [areaIdPath.length];
				if (AreaTreeState.getNodePath(rootNode, areaIdPath, 0, nodePath)) {
					
					if (nodePath.length <= 0) {
						continue;
					}
					if (nodePath[nodePath.length - 1] == null) {
						continue;
					}
					
					// Create tree path.
					TreePath treePath = new TreePath(nodePath);
					
					// Select given tree path.
					tree.expandPath(treePath);
				}
			}
			
			LinkedList<TreePath> selectedPaths = new LinkedList<TreePath>();
			
			// Select given areas.
			for (Long [] areaIdPath : treeState.selectedAreasIdPaths) {
				
				DefaultMutableTreeNode [] nodePath = new DefaultMutableTreeNode [areaIdPath.length];
				if (AreaTreeState.getNodePath(rootNode, areaIdPath, 0, nodePath)) {
					
					if (nodePath.length <= 0) {
						continue;
					}
					if (nodePath[nodePath.length - 1] == null) {
						continue;
					}
					
					// Create tree path.
					TreePath treePath = new TreePath(nodePath);
					
					// Select given tree path.
					selectedPaths.add(treePath);
				}
			}
			
			tree.setSelectionPaths(selectedPaths.toArray(new TreePath [0]));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get tree state.
	 * @param tree
	 * @return
	 */
	public static AreaTreeState getTreeState(JTree tree) {
		
		try {
			AreaTreeState treeState = new AreaTreeState();
		
			// Get expanded areas' IDs.
			DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
			if (rootNode != null) {
				
				// Get tree nodes.
				Enumeration<? super DefaultMutableTreeNode> allNodes = rootNode.depthFirstEnumeration();
				
				while (allNodes.hasMoreElements()) {
					
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) allNodes.nextElement();
					
					TreeNode [] path = node.getPath();
					TreePath treePath = new TreePath(path);
					
					// If the path is expanded, add the area ID to the list.
					if (tree.isExpanded(treePath)) {
						
						Long [] idPath = getAreaIdPath(treePath);
						
						treeState.addExpandedAreaIdPath(idPath);
					}
				}
			}
			
			// Get selected areas' IDs.
			TreePath [] selectedTreePaths = tree.getSelectionPaths();
			if (selectedTreePaths != null) {
				
				for (TreePath selectedTreePath : selectedTreePaths) {
					
					Long [] idPath = getAreaIdPath(selectedTreePath);
							
					// Add selected area ID to the tree state object.
					treeState.addSelectedAreaIdPath(idPath);
				}
			}
			
			return treeState;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Get area ID path.
	 * @param treePath
	 * @return
	 */
	private static Long[] getAreaIdPath(TreePath treePath) {
		
		try {
			int count = treePath.getPathCount();
			
			Long [] idPath = new Long [count];
			
			for (int index = 0; index < count; index++) {
				
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getPathComponent(index);
				Object userObject = node.getUserObject();
				
				Long areaId = null;
				if (userObject instanceof Long) {
					areaId = (Long) userObject;
				}
				else if (userObject instanceof Area) {
					Area area = (Area) userObject;
					areaId = area.getId();
				}
				
				if (areaId != null) {
					idPath[index] = areaId;
				}
			}
			
			return idPath;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Clear selected list.
	 */
	public void clearSelected() {
		try {
			
			selectedAreasIdPaths.clear();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}