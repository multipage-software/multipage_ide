/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package program.builder;

import java.util.LinkedList;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import org.maclan.Area;
import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.multipage.basic.ProgramBasic;
import org.multipage.generator.AreaEditorFrameBase;
import org.multipage.generator.EditorTabActions;
import org.multipage.generator.GeneratorMainFrame;
import org.multipage.generator.ProgramGenerator;
import org.multipage.gui.CheckBoxList;
import org.multipage.gui.CheckBoxListManager;
import org.multipage.gui.UpdatableComponent;
import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.Safe;

/**
 * Editor panel that shows area inheritance information.
 * @author vakol
 *
 */
public class AreaInheritancePanel extends JPanel implements EditorTabActions, UpdatableComponent {
	
	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Reference to parent frame.
	 */
	private AreaEditorFrameBase parentFrameBase = null;
	
	/**
	 * Edited area reference.
	 */
	protected Area area;

	// $hide<<$
	/**
	 * Components.
	 */
	private JLabel labelSuperAreas;
	private JScrollPane scrollPane;
	
	/**
	 * Create the panel.
	 * @param areaEditorFrameBase 
	 */
	public AreaInheritancePanel(AreaEditorFrameBase areaEditorFrameBase) {
		
		this.parentFrameBase = areaEditorFrameBase;
		try {
			initComponents();
			postCreate(); // $hide$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Initialize components.
	 */
	private void initComponents() {
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		labelSuperAreas = new JLabel("builder.textSuperAreas");
		springLayout.putConstraint(SpringLayout.NORTH, labelSuperAreas, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, labelSuperAreas, 10, SpringLayout.WEST, this);
		add(labelSuperAreas);
		
		scrollPane = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, labelSuperAreas);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -10, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, this);
		add(scrollPane);
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			localize();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Localize components.
	 */
	private void localize() {
		try {
			
			Utility.localize(labelSuperAreas);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set area reference.
	 * @param area
	 */
	public void setArea(Area area) {
		
		this.area = area;
	}

	/**
	 * Reload inheritance.
	 */
	private void reloadInheritance() {
		try {
			
			// Check area.
			if (area == null) {
				return;
			}
			
			// Reload area object.
			area = ProgramGenerator.getArea(area.getId());
			
			// If the above area doesn't exist, close the frame.
			if (area == null) {
				Safe.invokeLater(() -> {
					// Close the frame.
					if (parentFrameBase != null) {
						parentFrameBase.close();
					}
				});
				return;
			}
	
			// Connect inheritance list.
			CheckBoxList<Area> listInheritance = new CheckBoxList<Area>();
			scrollPane.setViewportView(listInheritance);
			
			listInheritance.setContentManager(new CheckBoxListManager<Area>() {
				
				// Loads items.
				@Override
				protected boolean loadItem(int index, Obj<Area> object,
						Obj<String> text, Obj<Boolean> selected) {
					
					try {
						// Check area.
						if (area == null) {
							return false;
						}
						
						// Load superareas.
						LinkedList<Area> superAreas = area.getSuperareas();
						
						// If the index is out of bounds, return false value.
						if (index >= superAreas.size()) {
							return false;
						}
						
						Area superArea = superAreas.get(index);
						
						// Set object, text and inheritance.
						object.ref = superArea;
						text.ref = superArea.getDescriptionForGui();
						selected.ref = area.inheritsFrom(superArea);
						
						return true;
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return false;
				}
				
				// Processes change.
				@Override
				protected boolean processChange(Area inheritArea, boolean selected) {
					
					try {
						// Save inheritance.
						return saveInheritance(inheritArea, selected);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return false;
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Save inheritance.
	 * @param inherits 
	 */
	private boolean saveInheritance(Area superArea, boolean inherits) {
		
		try {
			MiddleResult result;
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			
			// Save sub area edges.
			result = middle.updateIsSubAreaEdge(login, superArea.getId(),
						area.getId(), inherits);
			if (result.isNotOK()) {
				result.show(this);
				return false;
			}
	
			// Set inheritance.
			area.setInheritanceLight(superArea.getId(), inherits);
			
			// Update all components.
			GeneratorMainFrame.updateAll();
			
			return true;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * On load panel information.
	 */
	public void onLoadPanelInformation() {
		try {
			
			// Load inheritance.
			reloadInheritance();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On save panel information.
	 */
	public void onSavePanelInformation() {

	}

	/**
	 * On close the window.
	 */
	public void onClose() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Update dialog components.
	 */
	public void updateComponents() {
		try {
			
			// Reload inheritance checkox list.
			reloadInheritance();
			
			// TODO: <---MAKE Update components.
			
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
}
