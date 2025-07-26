/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.maclan.Area;
import org.maclan.AreasModel;
import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.multipage.basic.ProgramBasic;
import org.multipage.gui.ApplicationEvents;
import org.multipage.gui.Images;
import org.multipage.gui.Message;
import org.multipage.gui.PreventEventEchos;
import org.multipage.gui.ReceiverAutoRemove;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.TextFieldAutoSave;
import org.multipage.gui.UpdatableComponent;
import org.multipage.gui.Utility;
import org.multipage.util.Closable;
import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Base class for panel that displays area properties.
 * @author vakol
 *
 */
public class AreaPropertiesBasePanel extends JPanel implements UpdatableComponent, PreventEventEchos, ReceiverAutoRemove, Closable {
	
	//$hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Splitter position.
	 */
	private static int splitterPositionState = 400;
	
	/**
	 * Set default data.
	 */
	public static void setDefaultData() {

	}

	/**
	 * Load data.
	 * @param inputStream
	 */
	public static void seriliazeData(StateInputStream inputStream)
		throws IOException, ClassNotFoundException {

		// Load splitter position.
		splitterPositionState = inputStream.readInt();
	}

	/**
	 * Save data.
	 * @param outputStream
	 */
	public static void seriliazeData(StateOutputStream outputStream)
		throws IOException {

		// Save splitter position.
		outputStream.writeInt(splitterPositionState);
	}
	
	/**
	 * List of previous messages.
	 */
	private LinkedList<Message> previousMessages = new LinkedList<>();
	
	/**
	 * Is properties panel flag.
	 */
	public boolean isInPropertiesPanel = false;

	/**
	 * Area nodes.
	 */
	protected LinkedList<Area> areas = null;
	
	//$hide<<$
	/**
	 * Components.
	 */
	private JLabel labelAreaDescription;
	private TextFieldAutoSave textDescription;
	private JButton buttonSaveDescription;
	private JMenu menuArea;
	private JMenuItem menuEditResources;
	private JButton buttonDeleteText;
	private JSplitPane splitPane;
	private SlotListPanel panelSlotList;
	private JLabel labelAreaAlias;
	private TextFieldAutoSave textAlias;
	private JButton buttonSaveAlias;
	private JPanel panelExtension;
	private JMenuItem menuEditDependencies;
	private JMenuItem menuAreaEdit;
	
	/**
     * Constructor.
     */
	public AreaPropertiesBasePanel() {
		try {
			
			// Register this component for update operation.
			GeneratorMainFrame.registerForUpdate(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set component references.
	 * @param menuEditDependencies 
	 * @param menuAreaEdit 
	 */
	protected void setComponentsReferences(
			JLabel labelAreaDescription,
			TextFieldAutoSave textDescription,
			JButton buttonSaveArea,
			JMenu menuArea,
			JMenuItem menuEditResources,
			JButton buttonDeleteText,
			JSplitPane splitPane,
			SlotListPanel panelSlotList,
			JLabel labelAreaAlias,
			TextFieldAutoSave textAlias,
			JButton buttonSaveAlias,
			JPanel panelExtension,
			JMenuItem menuEditDependencies,
			JMenuItem menuAreaEdit) {
		
		this.labelAreaDescription = labelAreaDescription;
		this.textDescription = textDescription;
		this.buttonSaveDescription = buttonSaveArea;
		this.menuArea = menuArea;
		this.menuEditResources = menuEditResources;
		this.buttonDeleteText = buttonDeleteText;
		this.splitPane = splitPane;
		this.panelSlotList = panelSlotList;
		this.labelAreaAlias = labelAreaAlias;
		this.textAlias = textAlias;
		this.buttonSaveAlias = buttonSaveAlias;
		this.panelExtension = panelExtension;
		this.menuEditDependencies = menuEditDependencies;
		this.menuAreaEdit = menuAreaEdit;
	}

	/**
	 * Post creation.
	 */
	protected void postCreate() {
		
		try {
			//$hide>>$
			// Create for the Dynamic application.
			if (!postCreateExtension(this, panelExtension)) {
				splitPane.setRightComponent(null);
				splitPane.setDividerSize(0);
			}
			
			// Localize components.
			localize();
			// Set icons.
			setIcons();
			// Set tool tips.
			setToolTips();
			// Set callback functions.
			setCallbacks();
			// Load dialog.
			loadDialog();
			//$hide<<$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Load dialog.
	 */
	private void loadDialog() {
		try {
			
			splitPane.setDividerLocation(splitterPositionState);
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
			
			panelSlotList.saveDialog();
			splitterPositionState = splitPane.getDividerLocation();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set slot selected event.
	 */
	public void setSlotSelectedEvent(SlotSelectedEvent slotSelectedEvent) {
		try {
			
			panelSlotList.setSlotSelectedEvent(slotSelectedEvent);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Post creation of the Dynamic application.
	 * @param panel 
	 * @param areasProperties 
	 * @param panelExtension 
	 * @return
	 */
	protected boolean postCreateExtension(AreaPropertiesBasePanel areasProperties,
			JPanel panelExtension) {
		
		return false;
	}

	/**
	 * Set icons.
	 */
	protected void setIcons() {
		try {
			
			buttonSaveDescription.setIcon(Images.getIcon("org/multipage/generator/images/save_icon.png"));
			buttonDeleteText.setIcon(Images.getIcon("org/multipage/generator/images/remove_icon.png"));
			buttonSaveAlias.setIcon(Images.getIcon("org/multipage/generator/images/save_icon.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set tool tips.
	 */
	protected void setToolTips() {
		try {
			
			buttonSaveDescription.setToolTipText(Resources.getString("org.multipage.generator.tooltipSaveAreaDescription"));
	        buttonDeleteText.setToolTipText(Resources.getString("org.multipage.generator.tooltipDeleteLocalizedText"));
	        buttonSaveAlias.setToolTipText(Resources.getString("org.multipage.generator.tooltipSaveAreaAlias"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Localize components.
	 */
	protected void localize() {
		try {
			
			Utility.localize(labelAreaDescription);
			Utility.localize(menuArea);
			Utility.localize(menuEditResources);
			Utility.localize(labelAreaAlias);
			Utility.localize(menuEditDependencies);
			Utility.localize(menuAreaEdit);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set areas.
	 */
	public void setAreasFromIds(HashSet<Long> areaIds) {
		Safe.invokeLater(() -> {
			
			LinkedList<Area> areas = new LinkedList<>();
			areaIds.forEach(areaId -> {
				Area area = ProgramGenerator.getArea(areaId);
				areas.add(area);
			});
			
			setAreas(areas);
		});
	}
	
	/**
	 * Set areas.
	 */
	public void setAreas(LinkedList<Area> areas) {
		Safe.invokeLater(() -> {
			
			// Get selected areas list.
			LinkedList<Area> selectedAreas;
			if (areas != null) {
				selectedAreas = Area.trim(areas);
			}
			else {
				selectedAreas = new LinkedList<Area>();
			}
			
			// Get number of selected areas.
			int areaCount = selectedAreas.size();
			
			// Remember the selected areas.
			this.areas = selectedAreas;
			
			// If is single area selected, enable description editing.
			if (areaCount == 1) {
				
				// Enable text boxes.
				textDescription.setEnabled(true);
				textAlias.setEnabled(true);
				
				// Get area.
				Area area = selectedAreas.getFirst();
				
				// Get description and alias. Trim the texts.
				String description = area.getDescription();
				String alias = area.getAlias();
				
				if (description == null) {
					description = "";
				}
				if (alias == null) {
					alias = "";
				}
				
				// Update description and alias.
				final String theDescription = description;
				textDescription.setText(theDescription);
				
				final String theAlias = alias;
				textAlias.setText(theAlias);
			}
			else if (areaCount > 1) {
				
				final String message = Resources.getString("org.multipage.generator.textMultipleAreasSelection");
				
				// Display message.
				textDescription.setMessage(message);
				textAlias.setMessage(message);
			}
			
			// Extended method.
			setAreaExtension();
			
			// Set the list of available slots.
			panelSlotList.setAreas(selectedAreas);
		});
	}
	
	/**
	 * Get current areas.
	 * @return
	 */
	public LinkedList<Area> getAreas() {
	
		return this.areas;
	}

	/**
	 * Set area extension.
	 */
	protected void setAreaExtension() {
		
		// Override this method.
	}

	/**
	 * Save description changes.
	 */
	public void saveDescriptionChanges() {
		try {
			
			// Try to save existing changes.
			textDescription.saveText();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Save alias changes.
	 */
	public void saveAliasChanges() {
		try {
			
			// Try to save existing changes.
			textAlias.saveText();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Save description.
	 * @param description
	 */
	public void saveDescription(String description) {
		try {
			
			// If the areas reference is not set exit the method.
			if (areas != null && areas.size() == 1) {
				
				// If the description changes...
				if (isAreaDescriptionChanged()) {
					
					// Try to save the area description.
					Middle middle = ProgramBasic.getMiddle();
					MiddleResult result;
					Properties login = ProgramBasic.getLoginProperties();

					Area area = areas.getFirst();
					if (area == null) {
						return;
					}
					
					result = middle.updateAreaDescription(login, area, description);
					if (result != MiddleResult.OK) {
						textDescription.setText("");
						result.show(this);
					}
					else {
						area.setDescription(description);
					}
					
					// Update all components.
					GeneratorMainFrame.updateAll(this);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Save alias.
	 * @param alias
	 */
	public void saveAlias(String alias) {
		try {
			
			// Single area must be selected.
			if (areas != null && areas.size() == 1) {
				
				// If the alias changes...
				if (isAreaAliasChanged()) {
					
					// Get new area alias.
					Area area;
					try {
						area = areas.getFirst();
					}
					catch (Exception e) {
						return;
					}
					long areaId = area.getId();

					// Check alias uniqueness against project root.
					AreasModel model = ProgramGenerator.getAreasModel();
					if (!model.isAreaAliasUnique(alias, areaId)) {
						
						Utility.show(this, "org.multipage.generator.messageAreaAliasAlreadyExists", alias);
						return;
					}
					
					// Try to save the area description.
					Middle middle = ProgramBasic.getMiddle();
					MiddleResult result;
					Properties login = ProgramBasic.getLoginProperties();
					
					result = middle.updateAreaAlias(login, areaId, alias);
					if (result != MiddleResult.OK) {
						textAlias.setText("");
						result.show(this);
					}
					else {
						area.setAlias(alias);
					}
					
					// Update all components.
					GeneratorMainFrame.updateAll();
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
		
	/**
	 * Returns true value if the area description changes.
	 * @return
	 */
	private boolean isAreaDescriptionChanged() {
		
		try {
			Area area = areas.getFirst();		
			String text = textDescription.getText();
			if (text == null) {
				return false;
			}
			return text.compareTo(area.getDescription()) != 0;
		}
		catch (Exception e) {
			Safe.exception(e);
			return false;
		}
	}
	
	/**
	 * Returns true value if the area alias changes.
	 * @return
	 */
	private boolean isAreaAliasChanged() {

		try {
			Area area = areas.getFirst();
			String text = textAlias.getText();
			if (text == null) {
				return false;
			}
			return text.compareTo(area.getAlias()) != 0;
		}
		catch (Exception e) {
			Safe.exception(e);
			return false;
		}
	}
	
	/**
	 * Set callback event functions.
	 */
	public void setCallbacks() {
		try {
			
			textDescription.setUpdateLambda(description -> {
				saveDescription(description);			
			});
			
			textAlias.setUpdateLambda(alias -> {
				saveAlias(alias);
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On edit area.
	 */
	protected void onEditArea(int tabIdentifier) {
		try {
			
			// If it is not selected exactly one area, inform user
			// and exit the method.
			if (areas.size() != 1) {
				JOptionPane.showMessageDialog(this,
						Resources.getString("org.multipage.generator.textSelectOnlyOneArea"));
				return;
			}
			
			// Execute area editor.
			AreaEditorFrame.showDialog(null, areas.getFirst(), tabIdentifier);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On delete local text.
	 */
	protected void onDeleteLocalText() {
		try {
			
			// Only one area must be selected.
			if (areas.size() != 1) {
				return;
			}
			
			// Ask user.
			if (JOptionPane.showConfirmDialog(this,
					Resources.getString("org.multipage.generator.messageDeleteTextInCurrentLanguage"))
					!= JOptionPane.YES_OPTION) {
				return;
			}
			
			// Get area.
			Area area = areas.getFirst();
			long areaId = area.getId();
			
			Middle middle = ProgramBasic.getMiddle();
			
			// Database login.
			MiddleResult result = middle.login(ProgramBasic.getLoginProperties());
			if (result.isOK()) {
				
				Obj<Long> descriptionId = new Obj<Long>();
				// Get description ID.
				result = middle.loadAreaDescriptionId(areaId, descriptionId);
				if (result.isOK()) {
					
					// Remove local text.
					result = middle.removeCurrentLanguageText(descriptionId.ref);
				}
				
				// Database logout.
				MiddleResult logoutResult = middle.logout(result);
				if (result.isOK()) {
					result = logoutResult;
				}
			}
			
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Enable/disable editing.
	 * @param flag
	 */
	public void enableEditing(boolean flag) {
		try {
			
			// Enable/disable whole frame.
			GeneratorMainFrame.getFrame().setEnabled(flag);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get previous messages.
	 */
	@Override
	public LinkedList<Message> getPreviousMessages() {

		return previousMessages;
	}
	
	/**
	 * Close panel.
	 */
	@Override
	public void close() {
		try {
			
            // Save dialog.
			saveDialog();
			// Unregister from update.
			GeneratorMainFrame.unregisterFromUpdate(this);
			// Remove application event receivers.
			ApplicationEvents.removeReceivers(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Called when updating the panel.
	 */
	@Override
	public void updateComponents() {
		try {
			
			// Update the list of areas.
			areas = ProgramGenerator.getUpdatedAreas(areas);
			// Set the areas.
			setAreas(areas);
			// Enable editing in the main frame window.
			enableEditing(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * This receiver objects cannot be removed automatically.
	 */
	@Override
	public boolean canAutoRemove() {
		
		return false;
	}
}
