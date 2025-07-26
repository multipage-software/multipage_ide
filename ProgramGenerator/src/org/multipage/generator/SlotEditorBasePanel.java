/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2025-05-22
 *
 */

package org.multipage.generator;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import org.maclan.Area;
import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.maclan.Revision;
import org.maclan.Slot;
import org.maclan.SlotHolder;
import org.maclan.SlotType;
import org.multipage.basic.ProgramBasic;
import org.multipage.gui.ApplicationEvents;
import org.multipage.gui.FoundAttr;
import org.multipage.gui.GuiSignal;
import org.multipage.gui.Message;
import org.multipage.gui.PreventEventEchos;
import org.multipage.gui.ReceiverAutoRemove;
import org.multipage.gui.Signal;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.TextFieldEx;
import org.multipage.gui.UpdatableComponent;
import org.multipage.gui.Utility;
import org.multipage.util.Closable;
import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Base class for slot editor panels.
 * @author vakol
 * 
 */
public abstract class SlotEditorBasePanel extends JPanel implements UpdatableComponent, ReceiverAutoRemove, PreventEventEchos, Closable {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * List of previous messages.
	 */
	private LinkedList<Message> previousMessages = new LinkedList<Message>();
	
	/**
	 * Class and object for callbacks.
	 */
	public static interface Callbacks {
		
		void afterNewSlot(Slot newSlot);
		
		void afterSlotRevisionChange(Slot slot, Slot newSlot);
		
		void loadDialog();
		
		void saveDialog();
		
		void onOk();
		
		void onCancel();
	}
	
	protected Callbacks callbacks = null;
	
	/**
	 * Expose dialog components.
	 */
	protected abstract JTextField getTextAlias();
	protected abstract JCheckBox getCheckDefaultValue();
	protected abstract TextFieldEx getTextSpecialValue();
	protected abstract JCheckBox getCheckLocalizedFlag();
	protected abstract JTextField getTextHolder();
	protected abstract Container getPanelEditor();
	protected abstract JCheckBox getCheckLocalizedText();
	protected abstract JLabel getLabelSpecialValue();
	protected abstract JButton getButtonSpecialValue();
	protected abstract Component getComponent();
	protected abstract JToggleButton getToggleDebug();
	protected abstract JCheckBox getCheckInterpretPhp();
	protected abstract JLabel getLabelInheritable();

	/**
	 * Font.
	 */
	protected static Font fontState;
	
	/**
	 * Set default data.
	 */
	public static void setDefaultData() {
		
		fontState = new Font("DialogInput", Font.PLAIN, 12);
	}

	/**
	 * Load data.
	 * @param inputStream
	 */
	public static void seriliazeData(StateInputStream inputStream)
		throws IOException, ClassNotFoundException {
		
		fontState = Utility.readInputStreamObject(inputStream, Font.class);
		TextSlotEditorPanel.openHtmlEditor = inputStream.readBoolean();
	}

	/**
	 * Save data.
	 * @param outputStream
	 */
	public static void serializeData(StateOutputStream outputStream)
		throws IOException {

		outputStream.writeObject(fontState);
		outputStream.writeBoolean(TextSlotEditorPanel.openHtmlEditor);
	}
	
	/**
	 * Slot copy.
	 */
	public Slot editedSlot;
	
	/**
	 * Original slot reference
	 */
	public Slot originalSlot;

	/**
	 * Reduced editor reference.
	 */
	protected SlotValueEditorPanelInterface reducedEditor;
	
	/**
	 * Is ready flag.
	 */
	public boolean initialized = false;
	
	/**
	 * Editors.
	 */
	protected TextSlotEditorPanel textEditor;
	protected IntegerEditorPanel integerEditor;
	protected RealEditorPanel realEditor;
	protected BooleanEditorPanelBase booleanEditor;
	protected EnumerationEditorPanelBase enumerationEditor;
	protected ColorEditorPanel colorEditor;
	protected AreaReferenceEditorPanel areaReferenceEditor;
	protected PathPanel pathEditor;
	protected ExternalSlotEditorPanel externalSlotEditor;
	
	/**
	 * Current editor reference.
	 */
	private SlotValueEditorPanelInterface currentEditor;

	/**
	 * Is new slotCopy flag.
	 */
	public boolean isNew;
	
	/**
	 * Found attributes reference.
	 */
	public FoundAttr foundAttr;
		
	/**
	 * Constructor.
	 * @param editor
	 */
	public SlotEditorBasePanel() {
		
		GeneratorMainFrame.registerForUpdate(this);
	}
	
	/**
	 * Load dialog.
	 */
	public void loadDialog() {
		try {
			
			textEditor.setTextFont(fontState);
			
			if (callbacks != null) {
				callbacks.loadDialog();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Save dialog.
	 */
	protected void saveDialog() {
		try {
			
			fontState = textEditor.getTextFont();
			
			if (callbacks != null) {
				callbacks.saveDialog();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On OK.
	 * @param editor 
	 */
	public void onOk() {
		try {
			
			// Save slot.
			if (!saveSlot(false, false)) {
				return;
			}
			
			close();
	
			// Update all modules.
			GeneratorMainFrame.updateAll(this);
			saveDialog();
			
			if (callbacks != null) {
				callbacks.onOk();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On cancel.
	 * @param thisEditor 
	 */
	public void onCancel() {
		try {
			
			// Ask user.
			if (isChanged()) {
				if (JOptionPane.showConfirmDialog(getComponent(),
						Resources.getString("org.multipage.generator.messageLoseSlotChanges"))
						!= JOptionPane.YES_OPTION) {
					return;
				}
			}
			
			close();
			saveDialog();
			
			if (callbacks != null) {
				callbacks.onCancel();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get true value if an editor was reduced.
	 * @return
	 */
	protected boolean isReducedEditor() {
		
		try {
			return reducedEditor != null && !ProgramGenerator.isExtensionToBuilder();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Highlight found texts.
	 */
	public void updateFoundHighlight() {
		try {
			
			textEditor.highlightFound(foundAttr);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Create editors.
	 * @param useHtmlEditor 
	 */
	public void createEditors(boolean useHtmlEditor) {
		try {
			
			textEditor = new TextSlotEditorPanel(Utility.findWindow(getComponent()), useHtmlEditor, editedSlot);
			integerEditor = new IntegerEditorPanel();
			realEditor = new RealEditorPanel();
			booleanEditor = ProgramGenerator.newBooleanEditorPanel();
			enumerationEditor = ProgramGenerator.newEnumerationEditorPanel();
			colorEditor = new ColorEditorPanel();
			areaReferenceEditor = new AreaReferenceEditorPanel();
			pathEditor = new PathPanel();
			externalSlotEditor = new ExternalSlotEditorPanel();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Update editor components.
	 */
	public void updateAreaName() {
		try {
			
			SlotHolder holder = editedSlot.getHolder();
			if (holder != null) {
				Safe.setText(getTextHolder(), holder.toString());
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Select combo type.
	 * @param type
	 */
	protected void selectComboType(SlotType type) {
		
		// Override this method or leave it unused.
	}
	
	/**
	 * Select editor.
	 * @param type
	 */
	public void selectEditor(SlotType type, boolean setDefaultLanguage) {
		
		try {
			if (initialized) {
				editedSlot.setValue(getValue());
			}
			
			if (type == SlotType.UNKNOWN) {
				type = SlotType.TEXT;
			}
			
			// If it is critical conversion, ask user.
			if (editedSlot.isCriticalCoversion(type)) {
				
				String message = String.format(
						Resources.getString("org.multipage.generator.messageConfirmCriticalSlotValueConversion"),
						editedSlot.getType().toString(), type.toString());
				if (JOptionPane.showConfirmDialog(getComponent(), message)
						!= JOptionPane.YES_OPTION) {
					
					// Select previous type.
					selectComboType(editedSlot.getType());
					return;
				}
			}
	
			// Reset editor panel.
			getPanelEditor().removeAll();
			// Clear the editors.
			textEditor.clear();
			integerEditor.clear();
			realEditor.clear();
			
			reducedEditor = null;
			
			editedSlot.setLocalized(false);
	
			switch (type) {
			case LOCALIZED_TEXT:
				editedSlot.setLocalized(true);
			case TEXT:
				currentEditor = textEditor;
				textEditor.setValueMeaning(editedSlot.getValueMeaning());
				textEditor.setValue(editedSlot.getTextValue());
				getPanelEditor().add(textEditor);
				break;
			case ENUMERATION:
				currentEditor = enumerationEditor;
				enumerationEditor.setValue(editedSlot.getEnumerationValue());
				getPanelEditor().add(enumerationEditor);
				reducedEditor = enumerationEditor;
				break;
			case INTEGER:
				currentEditor = integerEditor;
				integerEditor.setValue(editedSlot.getIntegerValue());
				getPanelEditor().add(integerEditor);
				reducedEditor = integerEditor;
				break;
			case REAL:
				currentEditor = realEditor;
				realEditor.setValue(editedSlot.getRealValue());
				getPanelEditor().add(realEditor);
				reducedEditor = realEditor;
				break;
			case BOOLEAN:
				currentEditor = booleanEditor;
				booleanEditor.setValue(editedSlot.getBooleanValue());
				getPanelEditor().add(booleanEditor);
				reducedEditor = booleanEditor;
				break;
			case COLOR:
				currentEditor = colorEditor;
				colorEditor.setValue(editedSlot.getColorValue());
				getPanelEditor().add(colorEditor);
				reducedEditor = colorEditor;
				break;
			case AREA_REFERENCE:
				currentEditor = areaReferenceEditor;
				areaReferenceEditor.setValue(editedSlot.getAreaValue());
				getPanelEditor().add(areaReferenceEditor);
				reducedEditor = areaReferenceEditor;
				break;
			case PATH:
				currentEditor = pathEditor;
				pathEditor.setValue(editedSlot.getTextValue());
				getPanelEditor().add(pathEditor);
				reducedEditor = pathEditor;
				break;
			case EXTERNAL_PROVIDER:
				currentEditor = externalSlotEditor;
				externalSlotEditor.setValue(editedSlot.getTextValue());
				getPanelEditor().add(externalSlotEditor);
				reducedEditor = externalSlotEditor;
				break;
			default:
				currentEditor = null;
				break;
			}
			
			Safe.invokeLater(() -> {
				
				getComponent().validate();
				getComponent().repaint();
			});
		}
		catch (Throwable e) {
			Safe.exception(e);
		}	
	}
	
	/**
	 * Load current slot.
	 * @return
	 */
	public Slot loadCurrentSlot() {
		
		return null;
	}
	
	/**
	 * Returns true value if a value is changed.
	 * @return
	 */
	protected boolean isChanged() {
		
		try {
			Slot newSlot = loadCurrentSlot();
			newSlot.resetEmptyText();
			editedSlot.resetEmptyText();
			
			Obj<Boolean> slotChanges = new Obj<Boolean>(true);
			
			try {
				// Check if the edited slot is changed.
				Middle middle = ProgramBasic.loginMiddle();
				
				slotChanges = new Obj<Boolean>();
				MiddleResult result = middle.loadSlotChanges(newSlot, slotChanges, null);
				
				middle.logout(result);
			}
			catch (Exception e) {
			}
			
			return slotChanges.ref;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Get "slot is preferred" flag.
	 * @return
	 */
	protected boolean isPreferred() {
		
		try {
			return editedSlot.isPreferred() || editedSlot.isUserDefined();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Set editor type.
	 */
	public void updateEditorType() {
		try {
			
			// Select slot type.
			SlotType type = editedSlot.getTypeUseValueMeaning();
			if (type == SlotType.UNKNOWN) {
				type = SlotType.TEXT;
			}
			
			// Possibly disable localized text check box.
			if (!SlotType.isText(type)) {
				getCheckLocalizedFlag().setVisible(false);
			}
			
			selectEditor(type, isNew);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On user localized flag.
	 */
	protected void onUserLocalizedCheck() {
		try {
			
			// Set localized text flag.
			if (editedSlot.getType().equals(SlotType.TEXT)
					|| editedSlot.getType().equals(SlotType.LOCALIZED_TEXT)) {
				
				editedSlot.setLocalized(getCheckLocalizedFlag().isSelected());
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On switch on or off debugging
	 * @param buttonSelected 
	 */
	public void onToggleDebugging(boolean buttonSelected) {
		
		Safe.tryOnChange(getToggleDebug(), () -> {
			// Set flag
			Settings.setEnableDebugging(buttonSelected);
			// Transmit the "enable / disable debugging" signal.
			ApplicationEvents.transmit(this, Signal.debugging, buttonSelected);
		});
	}

	/**
	 * On interpret PHP
	 */
	public void onInterpretPhp() {
		try {
			
			JCheckBox checkBox = getCheckInterpretPhp();
			if (checkBox == null) {
				return;
			}
			
			final boolean selected = checkBox.isSelected();
			// TODO: <---FINISH IT
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On render HTML pages.
	 */
	protected void onRender() {
		try {
			
			GeneratorMainFrame.getFrame().onRender(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Gets current slot revision
	 * @return
	 */
	private long getCurrentRevision() {
		
		Obj<Long> revision = new Obj<Long>(0L);
		
		MiddleResult result = MiddleResult.UNKNOWN_ERROR;
		try {
			Middle middle = ProgramBasic.loginMiddle();
			result = middle.loadSlotHeadRevision(editedSlot, revision);
		}
		catch (Exception e) {
			result = MiddleResult.sqlToResult(e);
		}
		finally {
			ProgramBasic.logoutMiddle();
		}
		if (result.isNotOK()) {
			result.show(getComponent());
		}
		return revision.ref;
	}

	/**
	 * Save slot values.
	 * @param newRevision - if this parameter is set, make new revision.
	 * @param forcedSave - if this parameter is set the slot is saved even if it is not changed.
	 */
	protected boolean saveSlot(boolean newRevision, boolean forcedSave) {
		
		try {
			// Get current slot.
			Slot newSlot = loadCurrentSlot();
			
			Component parent = getComponent();
			boolean localizedTextSelected = editedSlot.isLocalized();
			
			Middle middle = ProgramBasic.getMiddle();
			
			// Database login.
			MiddleResult result = middle.login(
					ProgramBasic.getLoginProperties());
			if (result.isOK()) {
				
				// If this is a new slot, insert it.
				if (isNew) {
					
					// Set description of initial revision.
					String description = Resources.getString("org.multipage.generator.textInitialSlotRevision");
					newSlot.setRevisionDescription(description);
					
					// Insert new slot.
					result = middle.insertSlot(newSlot);
					
					// Callback method.
					afterNewSlot(newSlot);
				}
				// Make new revision of slot or update slot.
				else {
					
					long slotId = editedSlot.getId();
					Slot oldSlot = new Slot();
					
					// Load old slot properties.
					result = middle.loadSlot(slotId, oldSlot, null);
					if (result.isOK()) {
						
						boolean success = true;
						
						// Check if slot changes.
						boolean slotChanges = !newSlot.equalsDeep(oldSlot);	
						if (slotChanges == true || forcedSave) {
							
							if (!forcedSave) {
								String slotName = editedSlot.getNameForGenerator();
								success = Utility.ask(parent, "org.multipage.generator.messageSlotChangesSaveIt", slotName);
							}
							
							// Save slot changes.
							if (success) {
								if (newRevision) {
									
									// Get description of new revision.
									String description = Utility.input(parent, "org.multipage.generator.messageInputSlotRevisionDescription");
									if (description != null) {
										
										// Insert the new revision.
										result = middle.insertSlotRevision(editedSlot, newSlot, description);
										
										// Callback method.
										afterSlotRevisionChange(editedSlot, newSlot);
									}
								}
								else {
									// Update current revision. 
									result = middle.updateSlot(editedSlot, newSlot, localizedTextSelected);
								}
								
								// If alias has changed, update all slot remaining revisions with new alias.
								String newAlias = newSlot.getAlias();
								String oldAlias = oldSlot.getAlias();
								
								boolean aliasChange = !newAlias.equals(oldAlias);
								if (aliasChange) {
									
									SlotHolder holder = oldSlot.getHolder();
									if (holder instanceof Area) {
										
										long areaId = holder.getId();
		                                
		                                // Update all slot revisions with new alias.
		                                result = middle.updateSlotAlias(oldAlias, areaId, newAlias);
									}
									else {
										result = MiddleResult.NULL_SLOT_AREA;
									}
	                            }
							}
						}
					}
				}
	
				// Database logout.
				MiddleResult logoutResult = middle.logout(result);
				if (logoutResult.isNotOK()) {
					result = logoutResult;
				}
			}
			
			// Inform about error.
			if (result.isNotOK()) {
				result.show(parent);
				return false;
			}
			
			// If new slot has been saved, change flag.
			if (isNew) {
				isNew = false;
			}
			
			editedSlot = newSlot;
			originalSlot = newSlot;
			
			// Get selected area IDs.
			HashSet<Long> selectedAreaIds = GeneratorMainFrame.getSectedAreaIds();
			
			// Get selected slot IDs.
			HashSet<Long> selectedSlotIds = getSelectedSlotIds();
			
			// Update all modules.
			GeneratorMainFrame.updateAll(this);
			return true;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Call back method invoked after a new slot is added to the database.
	 * @param newSlot
	 */
	public void afterNewSlot(Slot newSlot) {
		try {
			
			if (callbacks != null) {
				callbacks.afterNewSlot(newSlot);
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Call back method invoked after a slot revision has been changed.
	 * @param slot
	 * @param newSlot
	 */
	public void afterSlotRevisionChange(Slot slot, Slot newSlot) {
		try {
			
			if (callbacks != null) {
				callbacks.afterSlotRevisionChange(slot, newSlot);
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Get selected slot IDs.
	 * @return
	 */
	private HashSet<Long> getSelectedSlotIds() {
		
		try {
			Long slotId = originalSlot.getId();
	
			HashSet<Long> slotIds = new HashSet<Long>();
			slotIds.add(slotId);
			
			return slotIds;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Get value.
	 * @return
	 */
	public Object getValue() {
		
		try {
			Component [] components = getPanelEditor().getComponents();
			if (components.length == 0) {
				return false;
			}
			SlotValueEditorPanelInterface valueInterface = (SlotValueEditorPanelInterface) components[0];
			Object value = valueInterface.getValue();
			
			return value;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Get value meaning.
	 * @return
	 */
	public String getValueMeaning() {
		
		try {
			Component [] components = getPanelEditor().getComponents();
			if (components.length == 0) {
				return null;
			}
			SlotValueEditorPanelInterface valueInterface = (SlotValueEditorPanelInterface) components[0];
			String valueMeaning = valueInterface.getValueMeaning();
			
			return valueMeaning;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Check slot editor type.
	 */
	protected boolean checkSlotEditorType() {
		
		return true;
	}
	
	/**
	 * Update slot value.
	 */
	public void updateSlotValue() {
		try {
			
			Object value = editedSlot.getValue();
			if (value == null) {
				return;
			}
			
			if (!checkSlotEditorType()) {
				return;
			}
		
			// Get slot value editor.
			Component [] components = getPanelEditor().getComponents();
			if (components.length != 1) {
				return;
			}
			SlotValueEditorPanelInterface editor = (SlotValueEditorPanelInterface) components[0];
			
			// Set value.
			editor.setValue(value);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On edit area.
	 */
	public void onEditArea(int tabIdentifier) {
		try {
			
			Area area = (Area) editedSlot.getHolder();
			if (area == null) {
				Utility.show(getComponent(), "org.multipage.generator.messageCannotGetSlotArea");
				return;
			}
			
			// Execute area editor.
			AreaEditorFrame.showDialog(null, area, tabIdentifier);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Show slot help.
	 */
	public void onHelp() {
		try {
			
			if (editedSlot == null) {
				Utility.show(getComponent(), "org.multipage.basic.messageNoDescriptionForSlot");
				return;
			}
			
			ProgramBasic.showSlotHelp(getComponent(), editedSlot.getId());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On save slot.
	 */
	public void onSave() {
		try {
			
			saveSlot(false, false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On commit slot changes.
	 */
	public void onCommit() {
		try {
			
			// Get current slot
			Slot newSlot = loadCurrentSlot();
			
			// If the new slot is equal to original slot, save new revision
			if (newSlot.differs(editedSlot)) {
				
				long current = getCurrentRevision();
				if (Utility.askParam(getComponent(), "org.multipage.generator.messageConfirmRevisionNumber", current + 1)) {
					saveSlot(true, true);
				}
				return;
			}
			
			// Inform user that the slot was not changed and ask them whether save new revision of the slot anyway
			if (Utility.ask(getComponent(), "org.multipage.generator.messageNoSlotChangesCommitAnyway")) {
				saveSlot(true, true);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Updates head revision number of the slot.
	 */
	public boolean updateHeadRevision() {
		
		try {
			Obj<Long> headRevision = new Obj<Long>();
			
			// Update current revision number
			MiddleResult result = MiddleResult.UNKNOWN_ERROR;
			try {
				Middle middle = ProgramBasic.loginMiddle();
				result = middle.loadSlotHeadRevision(this.editedSlot, headRevision);
				this.editedSlot.setRevision(headRevision.ref);
			}
			catch (Exception e) {
				result = MiddleResult.sqlToResult(e);
			}
			finally {
				ProgramBasic.logoutMiddle();
			}
			if (result.isNotOK()) {
				result.show(getComponent());
				return false;
			}
			return true;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Load input revision to slot editor.
	 * @param revision
	 */
	private void loadRevision(Revision revision) {
		
		// Save current slot ID and revision number.
		long editedSlotId = editedSlot.getId();
		long editedRevision = editedSlot.getRevision();
		
		// If a revision is selected in the dialog, display revised slot.
		MiddleResult result2 = MiddleResult.UNKNOWN_ERROR;
		try {
			Middle middle = ProgramBasic.loginMiddle();
			result2 = middle.loadRevisedSlot(revision, editedSlot);
			
			// Preserve edited slot ID and revision number.
			editedSlot.setId(editedSlotId);
			editedSlot.setRevision(editedRevision);
			// Restore head revision number
			updateComponents();
		}
		catch (Exception e) {
			result2 = MiddleResult.sqlToResult(e);
		}
		finally {
			ProgramBasic.logoutMiddle();
		}
		if (result2.isNotOK()) {
			result2.show(getComponent());
		}
	}
	
	/**
	 * On revision
	 */
	public void onRevision() {
		try {
			
			// Get slot ID.
			long slotId = editedSlot.getId();
			// Save current slot values
			Slot saved = (Slot) editedSlot.clone();
			
			// Get revision identifier and load appropriate slot values.
			Revision revision = RevisionsDialog.showDialog(getComponent(), editedSlot,
					selectedRevision -> {
						loadRevision(selectedRevision);
					});
			
			// If cancelled, return old values
			if (revision == null) {
				this.editedSlot = saved;
				updateComponents();
				return;
			}
			
			// Load revision returned by the above dialog.
			loadRevision(revision);
			
			// Get new slot ID.
			long newSlotId = editedSlot.getId();
			// invoke callback method when the slot ID has been changed.
			if (newSlotId != slotId) {
				afterSlotRevisionChange(saved, editedSlot);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On expose component
	 */
	public void onExpose() {
		
		// TODO: expose component
		
	}
	
	/**
	 * Disable / enable current editor.
	 * @param flag
	 */
	public void disableEditor(boolean flag) {
		try {
			
			// Set current value editor to default state.
			if (currentEditor != null) {
	
				// Disable / enable component.
				if (currentEditor instanceof JComponent) {
					
					boolean isSet = false;
					if (currentEditor instanceof TextSlotEditorPanel) {
						isSet = ((TextSlotEditorPanel)currentEditor).setControlsGrayed(flag);
					}
					if (!isSet) {
						Utility.enableComponentTree((JComponent) currentEditor, !flag);
					}
				}
				
				currentEditor.setDefault(flag);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On default value
	 */
	public void onDefaultValue() {
		try {
			
			boolean isDefault = getCheckDefaultValue().isSelected();
			setSpecialValueEnabled(!isDefault);
			
			String specialValue = getSpecialValue();
			disableEditor(isDefault || !isDefault && !specialValue.isEmpty());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set special value text control.
	 * @param specialValue
	 */
	public void setSpecialValueControl(String specialValue) {
		try {
			
			Safe.setText(getTextSpecialValue(), specialValue);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set listeners.
	 */
	public void setListeners() {
		try {
			
			Utility.setTextChangeListener(getTextSpecialValue(), () -> {
				
				Safe.tryOnChange(getTextSpecialValue(), () -> {
					onSpecialValueChanged();
				});
			});
						
			// Receive the "debugging" signal.
			ApplicationEvents.receiver(this, Signal.debugging, message -> {
				try {
					
					// Avoid receiving the signal from current dialog window.
					if (this.equals(message.source)) {
						return;
					}
					
					// Get flag value.
					Boolean debuggingEnabled = message.getRelatedInfo();
					if (debuggingEnabled == null) {
						return;
					}
					
					// Select or unselect the debug button.
					JToggleButton toggleDebug = getToggleDebug();
					Safe.setSelected(toggleDebug, debuggingEnabled);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set special value enabled.
	 * @param enabled
	 */
	public void setSpecialValueEnabled(boolean enabled) {
		try {
			
			getLabelSpecialValue().setEnabled(enabled);
			getTextSpecialValue().setEnabled(enabled);
			getButtonSpecialValue().setEnabled(enabled);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On special value changed.
	 */
	protected void onSpecialValueChanged() {
		try {
			
			String specialValue = getTextSpecialValue().getText();
			disableEditor(!specialValue.isEmpty());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On select special value.
	 */
	public void onSelectSpecialValue() {
		try {
			
			String oldValue = getTextSpecialValue().getText();
			
			String newValue = SpecialValueDialog.showDialog(getComponent(), oldValue);
			if (newValue == null) {
				return;
			}
			
			setSpecialValueControl(newValue);
			onSpecialValueChanged();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get special value.
	 * @return
	 */
	protected String getSpecialValueNull() {
		
		try {
			String specialValue = getTextSpecialValue().getText();
			if (specialValue.isEmpty()) {
				return null;
			}
			
			return specialValue;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Get special value.
	 * @return
	 */
	protected String getSpecialValue() {
		
		try {
			return getTextSpecialValue().getText();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * On display home page.
	 */
	public void onDisplayHomePage() {
		try {
			
			ApplicationEvents.transmit(this, GuiSignal.displayHomePage);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set title.
	 */
	public void setTitle(JFrame frame) {
		try {
			
			frame.setTitle(String.format(
					Resources.getString("org.multipage.generator.textSlotEditor"),
					editedSlot.getAliasWithId(),
					editedSlot.getArea().getDescriptionForced(true)
					));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get enumeration editor.
	 * @return
	 */
	public EnumerationEditorPanelBase getEnumerationEditor() {
		
		return enumerationEditor;
	}
	
	/**
	 * Update enumeration editor.
	 */
	public void updateEnumeration() {
		try {
			
			enumerationEditor.setSlot(editedSlot);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get edited slot.
	 * @return
	 */
	public Slot getEditedSlot() {
		
		return editedSlot;
	}
	
	/**
	 * On provider properties.
	 */
	public void onSlotProperties() {
		try {
			
			// Open slot properties dialog.
			SlotPropertiesDialog.showDialog(getComponent(), editedSlot);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get default slot value flag.
	 * @return
	 */
	public boolean isDefault() {
		
		try {
			return getCheckDefaultValue().isSelected();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Set toggle button used for switching the debugger.
	 * @param enableDebugging
	 */
	public void setToggleDebug(boolean enableDebugging) {
		try {
			
			getToggleDebug().setSelected(enableDebugging);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Update components.
	 */
	public abstract void updateComponents();
	
	/**
	 * Return list of prevoius received messages.
	 */
	@Override
	public LinkedList<Message> getPreviousMessages() {
		
		return previousMessages;
	}
	
	/**
	 * On closing the helper. Override this method and call this code with super.close().
	 */
	public void close() {
		try {
			
			GeneratorMainFrame.unregisterFromUpdate(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
