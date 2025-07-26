/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */
package org.multipage.generator;

import java.util.LinkedList;

import javax.swing.AbstractListModel;
import javax.swing.JList;

import org.maclan.AreasModel;
import org.multipage.util.Safe;

/**
 * Favorites list model.
 * @author vakol
 *
 */
class FavoritesModel extends AbstractListModel<Long> {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Areas' IDs list.
	 */
	private LinkedList<Long> favoriteAreasIds = new LinkedList<Long>();
	
	/**
	 * Editor reference.
	 */
	private AreaDiagramContainerPanel editor;
	
	/**
	 * Constructor.
	 */
	public FavoritesModel(AreaDiagramContainerPanel editor) {
		
		this.editor = editor;
	}

	/**
	 * Update favorites model. Remove non existing areas.
	 */
	public void update() {
		try {
			
			LinkedList<Long> itemsToRemove = new LinkedList<Long>();
			AreasModel model = ProgramGenerator.getAreasModel();
			
			Long lastFocusedAreaId = null;
			if (editor.lastFocusedArea != null) {
				lastFocusedAreaId = editor.lastFocusedArea.getId();
			}
			
			for (long areaId : favoriteAreasIds) {
				if (!model.existsArea(areaId)) {
					itemsToRemove.add(areaId);
					
					// Remove last focused area from editor.
					if (lastFocusedAreaId != null && areaId == lastFocusedAreaId) {
						editor.lastFocusedArea = null;
						editor.focusedAreaIndex = 0;
					}
				}
			}
			
			favoriteAreasIds.removeAll(itemsToRemove);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Move area up.
	 * @param areaId
	 * @param list 
	 */
	public void moveUp(long areaId, JList<Long> list) {
		try {
			
			// Get area ID index.
			int index = favoriteAreasIds.indexOf(areaId);
			// Swap areas.
			if (index > 0) {
				swap(index, index - 1);
			
				list.setSelectedIndex(index - 1);
				list.updateUI();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Move area down.
	 * @param areaId
	 * @param list 
	 */
	public void moveDown(long areaId, JList<Long> list) {
		try {
			
			// Get area index and areas count.
			int index = favoriteAreasIds.indexOf(areaId);
			int count = favoriteAreasIds.size();
			// Swap areas.
			if (index < count - 1) {
				swap(index, index + 1);
				
				list.setSelectedIndex(index + 1);
				list.updateUI();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Swap areas.
	 * @param index1
	 * @param index2
	 */
	private void swap(int index1, int index2) {
		try {
			
			long areaId1 = favoriteAreasIds.get(index1);
			long areaId2 = favoriteAreasIds.get(index2);
			favoriteAreasIds.set(index1, areaId2);
			favoriteAreasIds.set(index2, areaId1);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Remove area.
	 * @param areaProperty
	 */
	public void removeArea(long areaId) {
		try {
			
			favoriteAreasIds.remove(areaId);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Add new area.
	 * @param areaId
	 */
	public void addNew(long areaId) {
		try {
			
			if (!favoriteAreasIds.contains(areaId)) {
				favoriteAreasIds.add(areaId);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get size.
	 */
	@Override
	public int getSize() {
		
		try {
			return favoriteAreasIds.size();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0;
	}

	/**
	 * Get area.
	 */
	@Override
	public Long getElementAt(int index) {
		
		try {
			return favoriteAreasIds.get(index);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * @return the areas
	 */
	public LinkedList<Long> getAreasIds() {
		
		return favoriteAreasIds;
	}
	
	/**
	 * Set areas' IDS.
	 * @param areasIds
	 */
	public void setAreasIds(LinkedList<Long> areasIds) {
		
		favoriteAreasIds = areasIds;
	}
}