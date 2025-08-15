/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2025-04-29
 *
 */
package org.multipage.generator;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Transferable object used in dialog navigator Drag and Drop functionality.
 * 
 * @author vakol
 */
public class NavigatorTransferable implements Transferable {
	
	/**
	 * Drag and Drop data flavour.
	 */
	public static DataFlavor dataFlavor;
	
	/**
	 * Static constructor.
	 */
	static {
		try {
			
			String flavorDescription = Resources.getString("org.multipage.generator.textDialogNavigatorItem");
			dataFlavor = new DataFlavor("application/x-my-app-data", flavorDescription);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Transfered data.
	 */
	private NavigatorTransferableData data = null;
	
	/**
	 * Constructor.
	 * @param areaId
	 * @param editor
	 * @param buttonsPanel 
	 */
	public NavigatorTransferable(Long areaId, AreaEditorFrameBase editor, NavigatorButtonsPanel buttonsPanel) {
		try {
			
			// Delegate call.
			data = new NavigatorTransferableData(areaId, editor, buttonsPanel);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Constructor.
	 * @param areaId
	 * @param editor
	 * @param buttonsPanel
	 */
	public NavigatorTransferable(Long areaId, AreaPropertiesFrame editor, NavigatorButtonsPanel buttonsPanel) {
		try {
			
			// Delegate call.
			data = new NavigatorTransferableData(areaId, editor, buttonsPanel);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Constructor.
	 * @param areaId
	 * @param editor
	 * @param buttonsPanel
	 */
	public NavigatorTransferable(long areaId, AreasTreeEditorFrame editor, NavigatorButtonsPanel buttonsPanel) {
		try {
			
			// Delegate call.
			data = new NavigatorTransferableData(areaId, editor, buttonsPanel);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Constructor.
	 * @param resourceVersionId
	 * @param editor
	 * @param buttonsPanel
	 */
	public NavigatorTransferable(String resourceVersionId, TextResourceEditorFrame editor, NavigatorButtonsPanel buttonsPanel) {
		try {
			
			// Delegate call.
			data = new NavigatorTransferableData(resourceVersionId, editor, buttonsPanel);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Constructor.
	 * @param resourceId
	 * @param editor
	 * @param buttonsPanel
	 */
	public NavigatorTransferable(Long resourceId, TextResourceEditorFrame editor, NavigatorButtonsPanel buttonsPanel) {
		try {
			
			// Delegate call.
			data = new NavigatorTransferableData(resourceId, editor, buttonsPanel);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Constructor.
	 * @param slotId
	 * @param editor
	 * @param buttonsPanel
	 */
	public NavigatorTransferable(Long slotId, SlotEditorFrame editor, NavigatorButtonsPanel buttonsPanel) {
		try {
			
			// Delegate call.
			data = new NavigatorTransferableData(slotId, editor, buttonsPanel);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get data flavors.
	 */
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		
		try {
			DataFlavor[] flavors = { dataFlavor };
			return flavors;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Return true if the Drag and Drop flavor is supported.
	 * @param flavor
	 * @return
	 */
	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		
		try {
			boolean supported = dataFlavor.equals(flavor);
			return supported;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Return data transfered with the Drag and Drop.
	 */
	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		
		try {
			// Check flavor.
			if (!isDataFlavorSupported(flavor)) {
				throw new UnsupportedFlavorException(flavor);
			}
			
			// On success return data.
			return data;
		}
		catch (Throwable e2) {
			Safe.exception(e2);
		}
		throw new UnsupportedFlavorException(flavor);
	}
}
