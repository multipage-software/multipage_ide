/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListModel;

import org.multipage.util.Obj;
import org.multipage.util.Safe;

/**
 * Displays list of check boxes.
 * @author vakol
 *
 */
public class CheckBoxList<T> extends JList<Object> {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Manager.
	 */
	private CheckBoxListManager<T> manager;

	/**
	 * Sets content manager.
	 */
	@SuppressWarnings("serial")
	public void setContentManager(final CheckBoxListManager<T> manager) {
		try {
			
			this.manager  = manager;
			
			// Create default model.
			DefaultListModel<Object> model = new DefaultListModel<>();
			setModel(model);
	
			// Load list items.
			Obj<T> object = new Obj<T>();
			Obj<String> text = new Obj<String>();
			Obj<Boolean> selected = new Obj<Boolean>();
			
			int index = 0;
			
			while (true) {
				if (!manager.loadItem(index, object, text, selected)) {
					break;
				}
				// Create list item and add it to the model.
				CheckListItem<T> item = new CheckListItem<T>(object.ref, text.ref,
															 selected.ref);
				model.addElement(item);
				
				index++;
			}
			
			// Create and set cell renderer.
			DefaultListCellRenderer renderer = new DefaultListCellRenderer() {
				// Return list item renderer.
				@Override
				public Component getListCellRendererComponent(JList<?> list,
						Object value, int index, boolean isSelected,
						boolean cellHasFocus) {
					
					try {
						// Check object type.
						if (!(value instanceof CheckListItem)) {
							return null;
						}
						@SuppressWarnings("unchecked")
						CheckListItem<T> item = (CheckListItem<T>) value;
						
						return new CheckListItemLabel(item.text, item.selected, index);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return null;
				}
			};
			
			setCellRenderer(renderer);
			
			// Set mouse listener.
			addMouseListener(new MouseAdapter() {
				// On mouse released.
				@Override
				public void mouseReleased(MouseEvent e) {
					try {
						
						// Get list reference and mouse position.
						@SuppressWarnings("unchecked")
						JList<Object> list = (JList<Object>) e.getSource();
						Point mousePosition = e.getPoint();
						// Get item index.
						int index = list.locationToIndex(mousePosition);
						if (index == -1) {
							return;
						}
						// Check position.
						Rectangle itemRectangle = list.getCellBounds(index, index);
						if (itemRectangle == null) {
							return;
						}
						if (!itemRectangle.contains(mousePosition)) {
							return;
						}
						// Get list item.
						@SuppressWarnings("unchecked")
						CheckListItem<T> item = (CheckListItem<T>) list.getModel().getElementAt(index);
						// Fire change event.
						if (manager.processChange(item.object, !item.selected)) {
							// Toggle item selection.
							item.toggleSelection();
							// Repaint cell.
							list.repaint(list.getCellBounds(index, index));
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
	 * Select all.
	 */
	public void selectAll(boolean select) {
		try {
			
			// Get model.
			ListModel<Object> model = getModel();
			int count = model.getSize();
			
			for (int index = 0; index < count; index++) {
				
				// Get list item.
				Object itemObject = model.getElementAt(index);
				if (itemObject instanceof CheckListItem) {
					@SuppressWarnings("unchecked")
					CheckListItem<T> item = (CheckListItem<T>) itemObject;
					item.selected = select;
					// Process change.
					if (manager != null) {
						manager.processChange(item.object, select);
					}
				}
			}
			
			repaint();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Select object.
	 * @param callback
	 */
	public void selectObject(CheckBoxCallback<T> callback) {
		try {
			
			// Get model.
			ListModel<Object> model = getModel();
			int count = model.getSize();
			
			for (int index = 0; index < count; index++) {
				
				// Get list item.
				Object itemObject = model.getElementAt(index);
				if (itemObject instanceof CheckListItem) {
					@SuppressWarnings("unchecked")
					CheckListItem<T> item = (CheckListItem<T>) itemObject;
					item.selected = callback.matches(item.object);
				}
			}
			
			repaint();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get selected objects.
	 * @param selectedObjects
	 */
	public void getSelectedObjects(LinkedList<T> selectedObjects) {
		try {
			
			// Reset output list.
			selectedObjects.clear();
			
			// Get model.
			ListModel<Object> model = getModel();
			int count = model.getSize();
			
			for (int index = 0; index < count; index++) {
				
				// Get list item.
				Object itemObject = model.getElementAt(index);
				if (itemObject instanceof CheckListItem) {
					@SuppressWarnings("unchecked")
					CheckListItem<T> item = (CheckListItem<T>) itemObject;
					
					if (item.selected) {
						selectedObjects.add(item.object);
					}
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}

/**
 * 
 * @author
 *
 */
class CheckListItem<T> {
	
	/**
	 * Object.
	 */
	T object;
	
	/**
	 * Text.
	 */
	String text;
	
	/**
	 * Selection.
	 */
	boolean selected;
	
	/**
	 * Constructor.
	 */
	public CheckListItem(T object, String text, boolean selected) {
		
		this.object = object;
		this.text = text;
		this.selected = selected;
	}

	/**
	 * Toggles selection.
	 */
	public void toggleSelection() {

		selected = !selected;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return text;
	}
}

/**
 * @author
 *
 */
class CheckListItemLabel extends JCheckBox {
	
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * @param index 
	 */
	public CheckListItemLabel(String text, boolean selected, int index) {
		try {
			
			// Set text.
			setText(text);
			// Check.
			setSelected(selected);
			// Get background color.
			Color backGroundColor = Utility.itemColor(index);
			// Set color.
			setBackground(backGroundColor);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
