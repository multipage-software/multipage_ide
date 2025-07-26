/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package program.builder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.maclan.Slot;
import org.multipage.generator.GeneratorTextPopupMenuAddIn;
import org.multipage.gui.Images;
import org.multipage.gui.Utility;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Popup menu add-in.
 * @author vakol
 *
 */
public class BuilderTextPopupMenuAddIn extends GeneratorTextPopupMenuAddIn {
	
	/**
	 * Constructor.
	 * @param slot
	 */
	public BuilderTextPopupMenuAddIn(Slot slot) {
		
		super(slot);
	}

	/**
	 * Add trayMenu.
	 */
	@Override
	public void addMenu(JPopupMenu popupMenu, JEditorPane textPane) {
		try {
			
			// Add separator.
			popupMenu.addSeparator();
			
			// Create trayMenu.
			// Insert slot.
			JMenuItem menuInsertSlot = new JMenuItem(Resources.getString("builder.menuInsertSlot"));
			menuInsertSlot.setIcon(Images.getIcon("org/multipage/generator/images/slot.png"));
			popupMenu.add(menuInsertSlot);
			menuInsertSlot.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					insertInheritedSlot();
				}
			});
			
			// Insert area slot.
			JMenuItem menuInsertAreaSlot = new JMenuItem(Resources.getString("builder.menuInsertAreaSlot"));
			menuInsertAreaSlot.setIcon(Images.getIcon("org/multipage/generator/images/slot_area.png"));
			popupMenu.add(menuInsertAreaSlot);
			menuInsertAreaSlot.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					insertAreaSlot();
				}
			});
			
			// Add inherited trayMenu items.
			super.addMenu(popupMenu, textPane);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Insert area slot.
	 */
	protected void insertAreaSlot() {
		try {
			
			Object [] result = SelectAreaSlot.showDialog(Utility.findWindow(textPane), slot);
			if (result == null) {
				return;
			}
			
			String local = (Boolean) result[2] ? ", local" : "";
			String slotText = null;
			
			if (result[0] instanceof Integer) {
				
				int areaIndex = (Integer) result[0];
				
				if (areaIndex == 1) {
					slotText = String.format("[@TAG slot=#%s]",
						(String) result[1]);
				}
				else if (areaIndex == 2) {
					slotText = String.format("[@TAG startArea, slot=#%s%s]",
						(String) result[1], local);
				}
			}
			else if (result[0] instanceof String) {
				slotText = String.format("[@TAG areaAlias=#%s, slot=#%s%s]",
					(String) result[0], (String) result[1], local);
			}
			
			// Replace text.
			if (slotText != null) {
				textPane.replaceSelection(slotText);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
