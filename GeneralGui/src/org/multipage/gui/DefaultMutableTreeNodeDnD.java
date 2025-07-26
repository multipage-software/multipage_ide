/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

import org.multipage.util.Safe;

/**
 * Class for tree node with Drag and Drop capability. 
 * @author vakol
 *
 */
public class DefaultMutableTreeNodeDnD extends DefaultMutableTreeNode implements Transferable, Serializable {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Data flavors list.
	 */
	public static final DataFlavor treeNodeFlavor = new DataFlavor(
			DefaultMutableTreeNodeDnD.class, DataFlavor.javaJVMLocalObjectMimeType);
	
	private static DataFlavor [] dataFlavors = { treeNodeFlavor };

	/**
	 * Node marked flag.
	 */
	private boolean marked = false;

	/**
	 * Constructor.
	 * @param userObject
	 */
	public DefaultMutableTreeNodeDnD(Object userObject) {
		try {
			
			this.setUserObject(userObject);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get data.
	 */
	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		
		return this;
	}

	/**
	 * Get data flavors.
	 */
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		
		return dataFlavors;
	}

	/**
	 * Returns true value if a data flavor is supported.
	 */
	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		
		try {
			return flavor.equals(dataFlavors[0]);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Returns true value if the node is transferable.
	 * @return
	 */
	public boolean isTransferable() {
		
		// Override this method.
		return true;
	}

	/**
	 * Mark the node.
	 * @param value
	 */
	public void mark(boolean value) {
		
		this.marked = value;
	}
	
	/**
	 * Gets marked flag.
	 */
	public boolean isMarked() {
		
		return marked;
	}
}
