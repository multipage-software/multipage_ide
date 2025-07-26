/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.LinkedList;

import javax.swing.JPanel;

import org.maclan.Area;
import org.multipage.addinloader.j;
import org.multipage.gui.ApplicationEvents;
import org.multipage.gui.GuiSignal;
import org.multipage.gui.ReceiverAutoRemove;
import org.multipage.gui.UpdatableComponent;
import org.multipage.gui.Utility;
import org.multipage.util.Closable;
import org.multipage.util.Resources;
import org.multipage.util.Safe;


/**
 * Panel with area properties.
 * @author vakol
 *
 */
public class PropertiesPanel extends JPanel implements ReceiverAutoRemove, UpdatableComponent, Closable {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Maximized width.
	 */
	private static final int maxWidth = 300;
	
	/**
	 * Get maximized width.
	 */
	public static int getMaxWidth() {
		return maxWidth;
	}

	/**
	 * Message panel.
	 */
	private MessagePanel messagePanel = new MessagePanel();

	/**
	 * Areas editor.
	 */
	private AreaPropertiesBasePanel areasPropertiesPanel;
	
	/**
	 * Constructor.
	 */
	public PropertiesPanel() {
		try {
			
			initComponents();
			postCreate();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
	}

	/**
	 * Initialize components.
	 */
	private void initComponents() {

		// Create area editor.
		areasPropertiesPanel = newAreasProperties(true);
		setLayout(new BorderLayout());
	}
	
	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			// Initialize.
			setNoProperties();
			setListeners();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set listeners.
	 */
	private void setListeners() {
		try {
			
			// Set listener.
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					try {
						
						setCursor(Cursor.getDefaultCursor());
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			
			// Register for updates.
			GeneratorMainFrame.registerForUpdate(this);
			
			if (areasPropertiesPanel != null && areasPropertiesPanel.isInPropertiesPanel) {
				// Receive the "display area properties" messages.
				ApplicationEvents.receiver(this, GuiSignal.displayAreaProperties, message -> {
					try {
						
						HashSet<Long> selectedAreaIds = message.getRelatedInfo();
						if (selectedAreaIds != null && !selectedAreaIds.isEmpty()) {
							setAreas(selectedAreaIds);
						}
						else {
							setNoProperties();
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				});
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create new area properties object.
	 * @param isPropertiesPanel
	 * @return
	 */
	protected AreaPropertiesBasePanel newAreasProperties(boolean isPropertiesPanel) {
		
		try {
			return ProgramGenerator.newAreasProperties(isPropertiesPanel);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Set areas.
	 * @param selectedAreaIds
	 */
	public void setAreas(HashSet<Long> selectedAreaIds) {
		try {
			
			LinkedList<Area> areas = new LinkedList<Area>();
			selectedAreaIds.forEach(areaId -> {
				
				Area area = ProgramGenerator.getArea(areaId);
				if (area == null) {
					return;
				}
				
				areas.add(area);
			});
			
			// Delegate call.
			setAreas(areas);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Edit areas.
	 */
	public void setAreas(LinkedList<Area> areas) {
		try {
			
			// Delegate call.
			areasPropertiesPanel.setAreas(areas);
			viewAreaEditor();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set no properties.
	 */
	public void setNoProperties() {
		try {
			
			// Set message.
			messagePanel.setText(Resources.getString("org.multipage.generator.textNoAreaSelected"));
			viewMessage();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * View message.
	 */
	private void viewMessage() {
		try {
			
			removeAll();
			add(messagePanel, BorderLayout.CENTER);
			
			revalidate();
			Utility.repaintLater(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * View area properties editor.
	 */
	private void viewAreaEditor() {
		try {
			
			removeAll();
			add(areasPropertiesPanel, BorderLayout.CENTER);
			
			revalidate();
			Utility.repaintLater(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get the properties editor.
	 * @return the areaEditor
	 */
	public AreaPropertiesBasePanel getPropertiesEditor() {
		
		return areasPropertiesPanel;
	}
	
	/**
     * This receiver objects cannot be removed automatically.
     * @return false
     */
	@Override
	public boolean canAutoRemove() {
		
		return false;
	}
	
	/**
	 * Update GUI components.
	 */
	@Override
	public void updateComponents() {
		try {
			
			// Check panel.
			if (areasPropertiesPanel == null) {
				setNoProperties();
				return;
			}
			
			// Update areas.
			LinkedList<Area> areas = areasPropertiesPanel.getAreas();
			areas = ProgramGenerator.getAreas(areas);
			if (!areas.isEmpty()) {
				setAreas(areas);
			}
			else {
				setNoProperties();
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Close panel.
	 */
	@Override
	public void close() {
		try {
			ApplicationEvents.removeReceivers(this);
			GeneratorMainFrame.unregisterFromUpdate(this);
			areasPropertiesPanel.close();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
