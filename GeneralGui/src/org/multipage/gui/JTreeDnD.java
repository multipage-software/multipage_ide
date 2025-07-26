/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.LinkedList;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.multipage.util.Obj;
import org.multipage.util.Safe;

/**
 * Extension of tree view that implements Drag and Drop for tree nodes.
 * @author vakol
 *
 */
public class JTreeDnD extends JTree implements DragGestureListener, DragSourceListener, DropTargetListener {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Cursors.
	 */
	private static Cursor linkCursor;
	private static Cursor moveCursor;
	private static Cursor noDropCursor;
	
	/**
	 * Static constructor.
	 */
	static {
		try {
			
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			
			// Load cursors.
			Image image = Images.getImage("org/multipage/gui/images/copy_cursor.png");
			if (image != null) {
				
				linkCursor = toolkit.createCustomCursor(image, new Point(), "");
			}
			else {
				linkCursor = Cursor.getDefaultCursor();
			}
			
			image = Images.getImage("org/multipage/gui/images/move_cursor.png");
			if (image != null) {
				
				moveCursor = toolkit.createCustomCursor(image, new Point(), "");
			}
			else {
				moveCursor = Cursor.getDefaultCursor();
			}
			
			image = Images.getImage("org/multipage/gui/images/no_drop.png");
			if (image != null) {
				
				noDropCursor = toolkit.createCustomCursor(image, new Point(), "");
			}
			else {
				noDropCursor = Cursor.getDefaultCursor();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Drag source object.
	 */
	private DragSource dragSource;
	
	/**
	 * Drop target object.
	 */
	@SuppressWarnings("unused")
	private DropTarget dropTarget;

	/**
	 * Marked DnD node.
	 */
	private DefaultMutableTreeNodeDnD markedDndNode;

	/**
	 * Marked node parent.
	 */
	private TreeNode markedNodeParent;

	/**
	 * Drag and Drop callback.
	 */
	private JTreeDndCallback dndCallback;

	/**
	 * Enable Drag and Drop flag.
	 */
	private boolean enableDragAdnDrop = true;
	
	/**
	 * Selected tree paths.
	 */
	protected TreePath[] multipleSelectedPaths = null;

	/**
	 * Constructor.
	 */
	public JTreeDnD() {
		try {
			
			// Create drag source, drop target and drag gesture recognizer.
			dragSource = new DragSource();
			
			dropTarget = new DropTarget(this, this);
			
			dragSource.createDefaultDragGestureRecognizer(this,
					DnDConstants.ACTION_COPY | DnDConstants.ACTION_MOVE | DnDConstants.ACTION_LINK,
			        this);
			
			// Set selection listener.
			setListeners();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set selection listener.
	 */
	private void setListeners() {
		try {
			
			addTreeSelectionListener(new TreeSelectionListener() {
				
				@Override
				public void valueChanged(TreeSelectionEvent e) {
					try {
						
						// Set last multiple selection.
						TreePath[] paths = e.getPaths();
						if (paths != null && paths.length > 1 && multipleSelectedPaths == null) {
							multipleSelectedPaths = paths;
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get multiple selected paths.
	 * @return
	 */
	public TreePath[] getMultipleSelectedPaths() {
		
		try {
			TreePath[] paths = multipleSelectedPaths;
			multipleSelectedPaths = null;
			return paths;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	/**
	 * Clear all marked nodes.
	 */
	private void clearAllMarked() {
		try {
			
			markedDndNode = null;
			
			TreeModel model = getModel();
			if (model == null) {
				return;
			}
			
			// Traverse the tree model and remove marks.
			Object node = model.getRoot();
			if (node == null) {
				return;
			}
			
			LinkedList<Object> queue = new LinkedList<Object>();
			queue.add(node);
			
			while(!queue.isEmpty()) {
				
				node = queue.removeFirst();
				if (node instanceof DefaultMutableTreeNodeDnD) {
					
					DefaultMutableTreeNodeDnD dndNode = (DefaultMutableTreeNodeDnD) node;
					dndNode.mark(false);
				}
				
				int count = model.getChildCount(node);
				for (int index = 0; index < count; index++) {
					
					Object childNode = model.getChild(node, index);
					if (childNode != null) {
						queue.addLast(childNode);
					}
				}
			}
			
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Enable / disable Drag and Drop.
	 * @param enable
	 */
	public void enableDragAndDrop(boolean enable) {
		
		enableDragAdnDrop = enable;
	}

	/**
	 * On drag gesture recognized.
	 */
	@Override
	public void dragGestureRecognized(DragGestureEvent e) {
		try {
			
			if (!enableDragAdnDrop) {
				return;
			}
			
			// Get selected transferable tree node and start drag.
			Obj<Transferable> transferable = new Obj<Transferable>();
			Obj<TreeNode> parentNode = new Obj<TreeNode>();
			
			getSelectedTransferableNode(transferable, parentNode);
			if (transferable.ref == null || parentNode.ref == null) {
				return;
			}
			
			try {
				dragSource.startDrag(e, selectCursor(e.getDragAction()), transferable.ref, this);
			}
			catch (Exception exception) {
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get transferable node object.
	 * @param transferable
	 * @param parentNode 
	 * @return
	 */
	private void getSelectedTransferableNode(Obj<Transferable> transferable,
			Obj<TreeNode> parentNode) {
		try {
			
			// Get selected node and if it is transferable, return it.
			TreePath path = getSelectionPath();
			if (path == null) {
				return;
			}
			
			Object node = path.getLastPathComponent();
			if (!(node instanceof DefaultMutableTreeNodeDnD)) {
				return;
			}
			
			DefaultMutableTreeNodeDnD dndNode = (DefaultMutableTreeNodeDnD) node;
			if (!dndNode.isTransferable()) {
				return;
			}
			
			transferable.ref = dndNode;
			parentNode.ref = dndNode.getParent();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Select cursor depending on action.
	 * @param dragAction
	 * @return
	 */
	private Cursor selectCursor(int dragAction) {
		
		switch (dragAction) {
			case 2:
				return moveCursor;
			case 1:
				return linkCursor;
			default:
				return noDropCursor;
		}
	}

	/**
	 * On exit tree view.
	 * @param arg0
	 */
	@Override
	public void dragExit(DragSourceEvent e) {
		try {
			
			clearAllMarked();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On drag over.
	 */
	@Override
	public void dragOver(DragSourceDragEvent e) {
		try {
			
			// Process action, set cursor and clear marked nodes.
			clearAllMarked();
			
			int action = e.getTargetActions();
			
			setCursor(selectCursor(action));
	
			if (!(action == 1 || action == 2)) {
				return;
			}
	
			// Mark node or clear or all marks.
			Point location = e.getLocation();
			if (location == null) {
				return;
			}
			
			SwingUtilities.convertPointFromScreen(location, this);
	
			TreePath path = getPathForLocation(location.x, location.y);
			if (path == null) {
				return;
			}
	
			Object node = path.getLastPathComponent();
			if (!(node instanceof DefaultMutableTreeNodeDnD)) {
				return;
			}
			
			DefaultMutableTreeNodeDnD dndNode = (DefaultMutableTreeNodeDnD) node;
			dndNode.mark(true);
			
			markedDndNode = dndNode;
			
			// Get marked node parent.
			markedNodeParent = null;
			int nodeCount = path.getPathCount();
			
			if (nodeCount > 1) {
				Object nodeObject = path.getPathComponent(nodeCount - 2);
				
				if (nodeObject instanceof TreeNode) {
					markedNodeParent = (TreeNode) nodeObject;
				}
			}
			
			// Update component view.
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On drop.
	 * @param e
	 */
	@Override
	public void drop(DropTargetDropEvent e) {
		try {
			
			// Get transferable node and its parent.
			Obj<Transferable> transferable = new Obj<Transferable>();
			Obj<TreeNode> transferredNodeParent = new Obj<TreeNode>();
			
			getSelectedTransferableNode(transferable, transferredNodeParent);
			
			if (transferable.ref == null) {
				
				e.rejectDrop();
				return;
			}
			
			Object data = null;
			try {
				data = transferable.ref.getTransferData(DefaultMutableTreeNodeDnD.treeNodeFlavor);
			}
			catch (Exception exception) {
				
				e.rejectDrop();
				return;
			}
			
			if (!(data instanceof DefaultMutableTreeNodeDnD)) {
				
				e.rejectDrop();
				return;
			}
			
			DefaultMutableTreeNodeDnD transferedDndNode = (DefaultMutableTreeNodeDnD) data;
			
			// Get marked node.
			DefaultMutableTreeNodeDnD droppedDndNode = markedDndNode;
			if (droppedDndNode == null) {
				
				e.rejectDrop();
				return;
			}
			
			// Get dropped node parent.
			TreeNode droppedNodeParent = markedNodeParent;
			
			// Call callback.
			if (dndCallback != null) {
				dndCallback.onNodeDropped(droppedDndNode, droppedNodeParent,
						transferedDndNode, transferredNodeParent.ref, e);
				
				// Reset multiple selection.
				multipleSelectedPaths = null;
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set Drag and Drop callback.
	 * @param dndCallback
	 */
	public void setDragAndDropCallback(JTreeDndCallback dndCallback) {
		
		this.dndCallback = dndCallback;
	}

	/**
	 * On drop end.
	 */
	@Override
	public void dragDropEnd(DragSourceDropEvent e) {
		try {
			
			clearAllMarked();
			setCursor(Cursor.getDefaultCursor());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	@Override
	public void dragEnter(DragSourceDragEvent e) {
		
	}

	@Override
	public void dropActionChanged(DragSourceDragEvent e) {
		
	}

	@Override
	public void dragEnter(DropTargetDragEvent e) {
		try {
			
			// Try to select multiple tree nodes.
			if (multipleSelectedPaths != null) {
				Safe.invokeLater(() -> {
					addSelectionPaths(multipleSelectedPaths);
				});
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	@Override
	public void dragExit(DropTargetEvent e) {
		
	}

	@Override
	public void dragOver(DropTargetDragEvent e) {
		
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent e) {
		
	}
}
