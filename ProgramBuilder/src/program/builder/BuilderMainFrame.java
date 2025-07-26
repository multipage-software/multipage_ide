/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package program.builder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;

import java.util.LinkedList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import org.multipage.gui.Images;
import org.multipage.gui.ToolBarKit;
import org.multipage.util.Resources;
import org.multipage.util.Safe;
import org.multipage.generator.*;
import org.maclan.Area;

/**
 * Main frame of Builder application.
 * @author vakol
 *
 */
public class BuilderMainFrame extends GeneratorMainFrame {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Load data.
	 * @param inputStream
	 */
	public static void serializeData(StateInputStream inputStream)
		throws IOException, ClassNotFoundException {
		
		// Load show hidden slots button state.
		SlotListPanelBuilder.showHiddenSlots = inputStream.readBoolean();
	}
	
	/**
	 * Save data.
	 * @param outputStream
	 */
	public static void serializeData(StateOutputStream outputStream)
		throws IOException {
		
		// Save show hidden slots button state.
		outputStream.writeBoolean(SlotListPanelBuilder.showHiddenSlots);
	}

	/**
	 * Set default data.
	 */
	public static void setDefaultData() {
		
		SlotListPanelBuilder.showHiddenSlots = false;
	}
	
	/**
	 * Show id toggle button.
	 */
	private JToggleButton showHiddenSlotsButton;
	
	/**
	 * Constructor.
	 */
	public BuilderMainFrame() {
		super();
		try {
			
			setTitle(Resources.getString("builder.textMainFrameCaption"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Add hidden slots button to the toolbar.
	 */
	@Override
	protected void addHideSlotsButton(JToolBar toolBar) {
		try {
			
			toolBar.addSeparator();
			showHiddenSlotsButton = ToolBarKit.addToggleButton(toolBar,
					"program/builder/images/show_hide_slots.png",
					"builder.tooltipShowHideSlots", () -> onHideSlots());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On hide slots.
	 */
	public void onHideSlots() {
		try {
			
			SlotListPanelBuilder.showHiddenSlots = showHiddenSlotsButton.isSelected();
			updateAll();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Load dialog.
	 */
	@Override
	protected void loadDialog() {
		try {
			
			super.loadDialog();
			
			// Set show hidden slots button state.
			showHiddenSlotsButton.setSelected(SlotListPanelBuilder.showHiddenSlots);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Save dialog.
	 */
	@Override
	protected void saveDialog() {
		try {
			
			super.saveDialog();
			
			// Save show hidden slots button state.
			SlotListPanelBuilder.showHiddenSlots = showHiddenSlotsButton.isSelected();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/*
	 * Add edit enumerations trayMenu item.
	 */
	@Override
	protected void addEditEnumerationsMenuItem(JMenu menu) {
		try {
			
			JMenuItem editEnumerations = new JMenuItem(Resources.getString("builder.menuEditEnumerations"));
			editEnumerations.setAccelerator(KeyStroke.getKeyStroke("control alt E"));
			editEnumerations.setIcon(Images.getIcon("org/multipage/generator/images/enumerations.png"));
				
			menu.add(editEnumerations);
		
			editEnumerations.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					onEnumerations();
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On enumerations.
	 */
	protected void onEnumerations() {
		try {
			
			EnumerationsEditorDialog.showDialog(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Add edit versions trayMenu item.
	 * @param trayMenu
	 */
	@Override
	protected void addEditVersionsMenuItem(JMenuItem menu) {
		try {
			
			JMenuItem versions = new JMenuItem(Resources.getString("builder.menuEditVersions"));
			versions.setAccelerator(KeyStroke.getKeyStroke("control V"));
			versions.setIcon(Images.getIcon("org/multipage/generator/images/version_icon.png"));
				
			menu.add(versions);
				
			versions.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					onVersions();
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On versions editor.
	 */
	protected void onVersions() {
		try {
			
			VersionsEditor.showDialog(this);
			updateAll();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	

	/**
	 * Add search in text resources trayMenu item.
	 * @param trayMenu
	 */
	@Override
	protected void addSearchInTextResourcesMenuItem(JMenu menu) {
		try {
			
			JMenuItem toolsSearchInTextResources = new JMenuItem(Resources.getString("builder.menuToolsSearchInTextResources"));
			toolsSearchInTextResources.setAccelerator(KeyStroke.getKeyStroke("control alt T"));
			toolsSearchInTextResources.setIcon(Images.getIcon("org/multipage/generator/images/search_resources.png"));
			
			menu.add(toolsSearchInTextResources);
			
			toolsSearchInTextResources.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					onSearchInTextResources();
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Search in text resources.
	 */
	protected void onSearchInTextResources() {
		try {
			
			// Get selected areas.
			LinkedList<Area> areas = getSelectedAreas();
			// Show dialog.
			SearchTextResources.showDialog(this, areas);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
