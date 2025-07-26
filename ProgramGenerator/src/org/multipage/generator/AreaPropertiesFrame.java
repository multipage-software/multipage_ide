/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.maclan.Area;
import org.multipage.gui.Images;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.TopMostButton;
import org.multipage.gui.UpdatableComponent;
import org.multipage.gui.Utility;
import org.multipage.util.Closable;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Frame with area properties.
 * @author vakol
 *
 */
public class AreaPropertiesFrame extends JFrame implements UpdatableComponent, Closable {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Bounds.
	 */
	private static Rectangle bounds;

	/**
	 * Set default state.
	 */
	public static void setDefaultData() {
		
		bounds = new Rectangle();
	}

	/**
	 * Read state.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void serializeData(StateInputStream inputStream)
			throws IOException, ClassNotFoundException {
		
		Object object = inputStream.readObject();
		if (!(object instanceof Rectangle)) {
			throw new ClassNotFoundException();
		}
		bounds = (Rectangle) object;
	}

	/**
	 * Write state.
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream)
			throws IOException {
		
		outputStream.writeObject(bounds);
	}
	
	/**
	 * Editor for area properties.
	 */
	private AreaPropertiesBasePanel areaPropertiesPanel;

	/**
	 * Area.
	 */
	private Area area;

	// $hide<<$
	/**
	 * Components.
	 */
	private JPanel panelContainer;

	/**
	 * Create new frame.
	 * @param area
	 */
	public static void openNewInstance(Area area) {
		try {
			
			synchronized (AreaPropertiesFrame.class) {
				// Check input.
				if (area == null) {
					return;
				}
				
				// Get area ID and try to find the already opened frame.
				AreaPropertiesFrame frame = DialogNavigator.getAreaProperties(area);
				if (frame == null) {
					
	                // Open new frame and remember it.
					frame = new AreaPropertiesFrame(area);
					DialogNavigator.addAreaProperties(frame);
	            }
				else {// If the frame is already opened, bring it to front.
					frame.setExtendedState(JFrame.NORMAL);
	                frame.toFront();
				}
				frame.setVisible(true);
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Create the frame.
	 * @param area 
	 */
	public AreaPropertiesFrame(Area area) {
		
		try {
			setAlwaysOnTop(true);
			
			this.area = area;
			// Initialize components.
			initComponents();
			// $hide>>$
			// Post creation.
			postCreation();
			// $hide<<$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Get current area.
	 * @return
	 */
	public Area getArea() {
		
		return area;
	}

	/**
	 * Initialize components.
	 */
	private void initComponents() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				close();
			}
		});
		setBounds(100, 100, 359, 523);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		panelContainer = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, panelContainer, 0, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, panelContainer, 0, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, panelContainer, 0, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, panelContainer, 0, SpringLayout.EAST, getContentPane());
		getContentPane().add(panelContainer);
		panelContainer.setLayout(new BorderLayout(0, 0));
	}

	/**
	 * Post creation.
	 */
	private void postCreation() {
		try {
			
			createEditor();
			// Add top most window toggle button.
			TopMostButton.add(this, areaPropertiesPanel, 26, null);
			
			GeneratorMainFrame.registerForUpdate(this);
			
			setTitle();
			setIcons();
			setColors();
			loadDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load dialog.
	 */
	private void loadDialog() {
		try {
			
			if (bounds.isEmpty()) {
				Utility.centerOnScreen(this);
				bounds = getBounds();
			}
			else {
				setBounds(bounds);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Save dialog.
	 */
	private void saveDialog() {
		try {
			
			bounds = getBounds();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Create editor.
	 */
	private void createEditor() {
		try {
			
			areaPropertiesPanel = newAreasProperties(false);
			panelContainer.add(areaPropertiesPanel);
			
			LinkedList<Area> areas = new LinkedList<Area>();
			areas.add(area);
			areaPropertiesPanel.setAreas(areas);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Create new areas properties object.
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
	 * Set title.
	 */
	private void setTitle() {
		try {
			
			setTitle(String.format(
					Resources.getString("org.multipage.generator.textEditArea"), area));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set icons.
	 */
	private void setIcons() {
		try {
			
			setIconImage(Images.getImage("org/multipage/generator/images/main_icon.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set component colors.
	 */
	private void setColors() {
		
		areaPropertiesPanel.setBackground(CustomizedColors.get(ColorId.AREA_PROPERTIES_FRAME));
	}
	
	/**
	 * Update GUI components.
	 */
	@Override
	public void updateComponents() {
		try {
			
			// Get updated area.
			Area updatedArea = ProgramGenerator.updateArea(area);
			if (updatedArea == null) {
				close();
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Close the frame.
	 */
	@Override
	public void close() {
		try {
			
			synchronized (AreaPropertiesFrame.class) {
				
				// Remove this frame from the map.
				DialogNavigator.removeAreaProperties(area);
				if (areaPropertiesPanel != null) {
					areaPropertiesPanel.close();
				}
				
				GeneratorMainFrame.unregisterFromUpdate(this);
				
				saveDialog();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
		dispose();
	}
}
