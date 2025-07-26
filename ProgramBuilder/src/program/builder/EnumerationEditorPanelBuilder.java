/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package program.builder;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.SpringLayout;

import org.maclan.AreasModel;
import org.maclan.EnumerationObj;
import org.maclan.EnumerationValue;
import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.maclan.Slot;
import org.multipage.basic.ProgramBasic;
import org.multipage.generator.EnumerationEditorPanelBase;
import org.multipage.generator.ProgramGenerator;
import org.multipage.gui.Images;
import org.multipage.gui.RendererJLabel;
import org.multipage.gui.SearchTextDialog;
import org.multipage.gui.ToolBarKit;
import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.Safe;

/**
 * Panel that displays information about enumeration value.
 * @author vakol
 *
 */
public class EnumerationEditorPanelBuilder extends EnumerationEditorPanelBase {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	// $hide<<$
	/**
	 * Components.
	 */
	private JComboBox<EnumerationValue> comboEnumerationValue;
	private JButton buttonReset;
	private JLabel labelEnumerationType;
	private JComboBox<EnumerationObj> comboEnumerationType;
	private JLabel labelValue;
	private JToolBar toolBarEnumerationType;
	private JToolBar toolBarEnumerationValue;
	

	/**
	 * Create the dialog.
	 */
	public EnumerationEditorPanelBuilder() {
		
		try {
			initComponents();
			
			// $hide>>$
			postCreate();
			setComponentsReferences(comboEnumerationValue);
			// $hide<<$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Initialize components.
	 */
	private void initComponents() {
		setBounds(100, 100, 471, 300);
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		labelEnumerationType = new JLabel("builder.textEnumerationType");
		springLayout.putConstraint(SpringLayout.NORTH, labelEnumerationType, 30, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, labelEnumerationType, 10, SpringLayout.WEST, this);
		add(labelEnumerationType);
		
		comboEnumerationType = new JComboBox<EnumerationObj>();
		comboEnumerationType.setFont(new Font("Tahoma", Font.ITALIC, 11));
		comboEnumerationType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onEnumerationTypeChanged();
			}
		});
		comboEnumerationType.setPreferredSize(new Dimension(200, 27));
		springLayout.putConstraint(SpringLayout.NORTH, comboEnumerationType, 6, SpringLayout.SOUTH, labelEnumerationType);
		springLayout.putConstraint(SpringLayout.WEST, comboEnumerationType, 10, SpringLayout.WEST, this);
		add(comboEnumerationType);
		
		labelValue = new JLabel("builder.textEnumerationValue");
		springLayout.putConstraint(SpringLayout.NORTH, labelValue, 30, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, labelValue, 16, SpringLayout.EAST, comboEnumerationType);
		add(labelValue);
		
		comboEnumerationValue = new JComboBox();
		comboEnumerationValue.setFont(new Font("Tahoma", Font.BOLD, 11));
		springLayout.putConstraint(SpringLayout.NORTH, comboEnumerationValue, 0, SpringLayout.NORTH, comboEnumerationType);
		springLayout.putConstraint(SpringLayout.WEST, comboEnumerationValue, 0, SpringLayout.WEST, labelValue);
		comboEnumerationValue.setPreferredSize(new Dimension(200, 27));
		add(comboEnumerationValue);
		
		toolBarEnumerationType = new JToolBar();
		toolBarEnumerationType.setFloatable(false);
		springLayout.putConstraint(SpringLayout.NORTH, toolBarEnumerationType, 6, SpringLayout.SOUTH, comboEnumerationType);
		springLayout.putConstraint(SpringLayout.WEST, toolBarEnumerationType, 0, SpringLayout.WEST, labelEnumerationType);
		springLayout.putConstraint(SpringLayout.EAST, toolBarEnumerationType, 0, SpringLayout.EAST, comboEnumerationType);
		add(toolBarEnumerationType);
		
		toolBarEnumerationValue = new JToolBar();
		springLayout.putConstraint(SpringLayout.NORTH, toolBarEnumerationValue, 6, SpringLayout.SOUTH, comboEnumerationValue);
		springLayout.putConstraint(SpringLayout.WEST, toolBarEnumerationValue, 0, SpringLayout.WEST, labelValue);
		springLayout.putConstraint(SpringLayout.EAST, toolBarEnumerationValue, 0, SpringLayout.EAST, comboEnumerationValue);
		toolBarEnumerationValue.setFloatable(false);
		add(toolBarEnumerationValue);
		
		buttonReset = new JButton("");
		buttonReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onReset();
			}
		});
		buttonReset.setMargin(new Insets(0, 0, 0, 0));
		springLayout.putConstraint(SpringLayout.NORTH, buttonReset, 0, SpringLayout.NORTH, comboEnumerationType);
		springLayout.putConstraint(SpringLayout.WEST, buttonReset, 6, SpringLayout.EAST, comboEnumerationValue);
		buttonReset.setPreferredSize(new Dimension(27, 27));
		add(buttonReset);
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			localize();
			setIcons();
			createToolBars();
			
			initializeComboBox();
			
			// Load enumerations combo box.
			loadEnumerationComboBox();
			
			// Reset enumeration type.
			comboEnumerationType.setSelectedIndex(-1);
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
			
			buttonReset.setIcon(Images.getIcon("org/multipage/generator/images/cancel_icon.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create tool bars.
	 */
	private void createToolBars() {
		try {
			
			ToolBarKit.addToolBarButton(toolBarEnumerationType, "org/multipage/generator/images/add_item_icon.png",
					"builder.tooltipAddNewEnumerationType", () -> onNewEnumerationType());
			ToolBarKit.addToolBarButton(toolBarEnumerationType, "org/multipage/generator/images/edit.png",
					"builder.tooltipEditEnumerationType", () -> onEditEnumerationType());
			ToolBarKit.addToolBarButton(toolBarEnumerationType, "org/multipage/generator/images/remove_icon.png",
					"builder.tooltipRemoveEnumerationType", () -> onRemoveEnumerationType());
			toolBarEnumerationType.addSeparator();
			ToolBarKit.addToolBarButton(toolBarEnumerationType, "org/multipage/generator/images/update_icon.png",
					"builder.tooltipUpdateEnumerations", () -> onUpdade());
			toolBarEnumerationType.addSeparator();
			ToolBarKit.addToolBarButton(toolBarEnumerationType, "org/multipage/generator/images/search_icon.png",
					"builder.tooltipSearchEnumerationType", () -> onSearchEnumerationType());
			
			
			ToolBarKit.addToolBarButton(toolBarEnumerationValue, "org/multipage/generator/images/add_item_icon.png",
					"builder.tooltipAddNewEnumerationValue", () -> onNewEnumerationValue());
			ToolBarKit.addToolBarButton(toolBarEnumerationValue, "org/multipage/generator/images/edit.png",
					"builder.tooltipEditEnumerationValue", () -> onEditEnumerationValue());
			ToolBarKit.addToolBarButton(toolBarEnumerationValue, "org/multipage/generator/images/remove_icon.png",
					"builder.tooltipRemoveEnumerationValue", () -> onRemoveEnumerationValue());
			toolBarEnumerationValue.addSeparator();
			ToolBarKit.addToolBarButton(toolBarEnumerationValue, "org/multipage/generator/images/update_icon.png",
					"builder.tooltipUpdateEnumerations", () -> onUpdade());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Localize.
	 */
	private void localize() {
		try {
			
			Utility.localize(labelEnumerationType);
			Utility.localize(labelValue);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set slot reference.
	 * @param slot
	 */
	@Override
	public void setSlot(Slot slot) {
		try {
			
			this.slot = slot;
			
			// Load enumeration values.
			loadValueComboBox();
			
			// Select enumeration value.
			EnumerationValue enumerationValue = slot.getEnumerationValue();
			if (enumerationValue != null) {
				
				String value = enumerationValue.getValue();
				if (value instanceof String) {
					selectEnumerationValue(value);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Initialize combo box.
	 */
	private void initializeComboBox() {
		try {
			
			comboEnumerationValue.setRenderer(new ListCellRenderer() {
				
				// Renderer object.
				RendererJLabel renderer = new RendererJLabel();
				
				// Overridden method.
				@Override
				public Component getListCellRendererComponent(JList list,
						Object value, int index, boolean isSelected,
						boolean cellHasFocus) {
					
					try {
						if (value instanceof EnumerationValue) {
							renderer.setText(((EnumerationValue) value).getValueDescriptionBuilder());
						}
						else if (value != null) {
							renderer.setText(value.toString());
						}
						else {
							renderer.setText("");
						}
						
						renderer.set(isSelected, cellHasFocus, index);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return renderer;
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load enumeration combo box.
	 */
	private void loadEnumerationComboBox() {
		try {
			
			comboEnumerationType.removeAllItems();
			
			// Load enumerations.
			AreasModel areasModel = ProgramGenerator.getAreasModel();
			
			for (EnumerationObj enumeration : areasModel.getEnumerations()) {
				comboEnumerationType.addItem(enumeration);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Load enumeration value combo box.
	 */
	private void loadValueComboBox() {
		try {
			
			comboEnumerationValue.removeAllItems();
			
			// Get selected enumeration.
			EnumerationObj enumeration = (EnumerationObj) comboEnumerationType.getSelectedItem();
			if (enumeration == null) {
				return;
			}
			
			// Load enumeration values.
			for (EnumerationValue enumerationValue : enumeration.getValues()) {
				
				comboEnumerationValue.addItem(enumerationValue);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Select enumeration type.
	 * @param description
	 */
	private void selectEnumerationType(String description) {
		try {
			
			// Find combo box item and select it.
			for (int index = 0; index < comboEnumerationType.getItemCount(); index++) {
				EnumerationObj enumeration = comboEnumerationType.getItemAt(index);
				
				if (enumeration.getDescription().equals(description)) {
					comboEnumerationType.setSelectedIndex(index);
					break;
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Select enumeration type.
	 * @param enumerationId
	 */
	private void selectEnumerationType(long enumerationId) {
		try {
			
			// Find combo box item and select it.
			for (int index = 0; index < comboEnumerationType.getItemCount(); index++) {
				EnumerationObj enumeration = comboEnumerationType.getItemAt(index);
				
				if (enumeration.getId() == enumerationId) {
					comboEnumerationType.setSelectedIndex(index);
					break;
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On new enumeration type.
	 */
	public void onNewEnumerationType() {
		try {
			
			// Get new enumeration description.
			String description = Utility.input(this, "builder.messageInsertNewEnumerationDescription");
			if (description == null) {
				return;
			}
			
			// Prepare prerequisites.
			Properties login = ProgramBasic.getLoginProperties();
			Middle middle = ProgramBasic.getMiddle();
			
			// Insert new enumeration.
			MiddleResult result = middle.insertEnumeration(login, description);
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			updateInformation();
			
			// Load enumerations combo box and select item.
			loadEnumerationComboBox();
			selectEnumerationType(description);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Remove enumeration type.
	 */
	public void onRemoveEnumerationType() {
		try {
			
			// Get selected item.
			EnumerationObj enumeration = (EnumerationObj) comboEnumerationType.getSelectedItem();
			if (enumeration == null) {
				
				Utility.show(this, "builder.messageSelectSingleEnumeration");
				return;
			}
			
			// Let user confirm deletion.
			if (!Utility.ask(this, "builder.messageDeleteEnumeration", enumeration.getDescription())) {
				return;
			}
			
			// Prepare prerequisites.
			Properties login = ProgramBasic.getLoginProperties();
			Middle middle = ProgramBasic.getMiddle();
			MiddleResult result;
			
			boolean errorReported = false;
			
			// Try to remove enumeration values.
			for (EnumerationValue enumerationValue : enumeration.getValues()) {
				
				
				// Remove enumeration value.
				result = middle.removeEnumerationValue(login, enumerationValue.getId());
				if (!errorReported && result.isNotOK()) {
					result.show(this);
					errorReported = true;
				}
			}
				
			// On error exit the method.
			if (errorReported) {
				return;
			}
			
			// Remove enumeration.
			result = middle.removeEnumeration(login, enumeration.getId());
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
	
			updateInformation();
			
			int selectedIndex = comboEnumerationType.getSelectedIndex();
			
			// Load enumerations combo box and select item.
			loadEnumerationComboBox();
			
			if (selectedIndex >= 0) {
				int rowCount = comboEnumerationType.getItemCount();
				if (selectedIndex >= rowCount) {
					selectedIndex = rowCount - 1;
				}
				comboEnumerationType.setSelectedIndex(selectedIndex);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On edit enumeration type.
	 */
	public void onEditEnumerationType() {
		try {
			
			// Get selected item.
			EnumerationObj enumeration = (EnumerationObj) comboEnumerationType.getSelectedItem();
			if (enumeration == null) {
				
				Utility.show(this, "builder.messageSelectSingleEnumeration");
				return;
			}
			
			// Get new enumeration description.
			String newDescription = Utility.input(this,
					"builder.messageInsertNewEnumerationDescription", enumeration.getDescription());
			
			if (newDescription == null) {
				return;
			}
			
			// Prepare prerequisites.
			Properties login = ProgramBasic.getLoginProperties();
			Middle middle = ProgramBasic.getMiddle();
			
			// Update description.
			MiddleResult result = middle.updateEnumeration(login, enumeration.getId(), newDescription);
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			updateInformation();
			
			updateEnumerationsInformation();
			
			// Restore selection.
			selectEnumerationType(enumeration.getId());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On update.
	 */
	public void onUpdade() {
		try {
			
			updateInformation();
			updateEnumerationsInformation();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On search enumeration type.
	 */
	public void onSearchEnumerationType() {
		try {
			
			// Get search parameters.
			SearchTextDialog.Parameters parameters = SearchTextDialog.showDialog(this,
					"builder.textSearchEnumeration");
			if (parameters == null) {
				return;
			}
			
			// Get selected item index.
			int selectedIndex = comboEnumerationType.getSelectedIndex();
			if (selectedIndex == -1) {
				selectedIndex = 0;
			}
			int count = comboEnumerationType.getItemCount();
			
			// Search combo box item.
			for (int index = selectedIndex; index >= 0 && index < count;
					index = parameters.isForward() ? index + 1 : index - 1) {
				
				// Get item.
				EnumerationObj enumeration = comboEnumerationType.getItemAt(index);
				String description = enumeration.getDescription();
				
				// If the enumeration is found, select it and exit the method.
				if (Utility.find(description, parameters)) {
					
					comboEnumerationType.setSelectedIndex(index);
					return;
				}
			}
			
			// If nothing found inform user.
			Utility.show(this, "builder.messageEnumerationDescriptionNotFound");
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On new enumeration value.
	 */
	public void onNewEnumerationValue() {
		try {
			
			// If the enumeration type is not selected, inform user.
			EnumerationObj enumeration = (EnumerationObj) comboEnumerationType.getSelectedItem();
			if (enumeration == null) {
				
				Utility.show(this, "builder.messageSelectEnumerationType");
				return;
			}
			
			// Get enumeration text value and description.
			Obj<String> value = new Obj<String>();
			Obj<String> description = new Obj<String>();
			
			if (!NewEnumerationValueDialog.showDialog(this, enumeration.getDescription(), value, description)) {
				return;
			}
			
			// Prepare prerequisites.
			Properties login = ProgramBasic.getLoginProperties();
			Middle middle = ProgramBasic.getMiddle();
			
			// Insert enumeration value.
			MiddleResult result = middle.insertEnumerationValue(login, enumeration.getId(),
					value.ref, description.ref);
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			updateInformation();
			
			updateEnumerationsInformation();
			
			// Select enumeration value.
			selectEnumerationValue(value.ref);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On enumeration type changed.
	 */
	protected void onEnumerationTypeChanged() {
		try {
			
			// Get old selection.
			EnumerationValue enumerationValue = (EnumerationValue) comboEnumerationValue.getSelectedItem();
			
			// Load enumeration values.
			loadValueComboBox();
			
			// Restore selection.
			if (enumerationValue != null) {
				selectEnumerationValue(enumerationValue.getValue());
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Select enumeration value.
	 * @param value
	 */
	private void selectEnumerationValue(String value) {
		try {
			
			for (int index = 0; index < comboEnumerationValue.getItemCount();
					index++) {
				
				EnumerationValue enumerationValue = comboEnumerationValue.getItemAt(index);
				if (enumerationValue.getValue().equals(value)) {
					
					// Select combo box item.
					comboEnumerationValue.setSelectedIndex(index);
					break;
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Update dialog information.
	 */
	private void updateEnumerationsInformation() {
		try {
			
			// Get old selections.
			EnumerationObj oldSelectedEnumeration = (EnumerationObj) comboEnumerationType.getSelectedItem();
			EnumerationValue oldSelectedEnumerationValue = (EnumerationValue) comboEnumerationValue.getSelectedItem();
			
			loadEnumerationComboBox();
			
			// Restore selections.
			if (oldSelectedEnumeration != null) {
				selectEnumerationType(oldSelectedEnumeration.getId());
			}
			if (oldSelectedEnumerationValue != null) { 
				selectEnumerationValue(oldSelectedEnumerationValue.getId());
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On edit enumeration value.
	 */
	protected void onEditEnumerationValue() {
		try {
			
			// Get selected enumeration value.
			EnumerationValue enumerationValue = (EnumerationValue) comboEnumerationValue.getSelectedItem();
			
			if (enumerationValue == null) {
				// Inform user.
				Utility.show(this, "builder.messageSelectSingleEnumerationValue");
				return;
			}
			
			// Get new value.
			Obj<String> value = new Obj<String>(enumerationValue.getValue());
			Obj<String> description = new Obj<String>(enumerationValue.getDescription());
			
			if (!EditEnumerationValueDialog.showDialog(this, enumerationValue.getEnumeration().getDescription(), value, description)) {
				return;
			}
			
			// Prepare prerequisites.
			Properties login = ProgramBasic.getLoginProperties();
			Middle middle = ProgramBasic.getMiddle();
			
			// Update enumeration value.
			MiddleResult result = middle.updateEnumerationValueAndDescription(login, enumerationValue.getId(),
					value.ref, description.ref);
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			updateInformation();
			updateEnumerationsInformation();
			
			// Select enumeration value.
			selectEnumerationValue(value.ref);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Remove enumeration value.
	 */
	public void onRemoveEnumerationValue() {
		try {
			
			// Get selected enumeration value.
			EnumerationValue enumerationValue = (EnumerationValue) comboEnumerationValue.getSelectedItem();
			
			if (enumerationValue == null) {
				// Inform user.
				Utility.show(this, "builder.messageSelectSingleEnumerationValue");
				return;
			}
			
			// Check if the enumeration value is associated with slot.
			boolean isEnumerationValueAssociated = false;
			
			if (slot != null) {
				EnumerationValue slotEnumerationValue = slot.getEnumerationValue();
				isEnumerationValueAssociated = slotEnumerationValue != null 
						&& enumerationValue.getId() == slotEnumerationValue.getId();
			}
			
			// Let user confirm the enumeration value deletion.
			String deleteMessage = isEnumerationValueAssociated ? 
					"builder.messageDeleteEnumerationValueAndSlotValue" : "builder.messageDeleteEnumerationValue";
			
			if (!Utility.ask(this, deleteMessage, enumerationValue.getValue())) {
				return;
			}
			
			// Prepare prerequisites.
			Properties login = ProgramBasic.getLoginProperties();
			Middle middle = ProgramBasic.getMiddle();
			
			// Login to the database.
			MiddleResult result = middle.login(login);
			
			// Remove possible associated enumeration value.
			if (isEnumerationValueAssociated) {
				result = middle.updateSlotResetEnumerationValue(slot.getId(), true);
			}
			
			// Remove enumeration value.
			if (result.isOK()) {
				result = middle.removeEnumerationValue(enumerationValue.getId());
			}
			
			// Logout from the database.
			MiddleResult logoutResult = middle.logout(result);
			if (result.isOK()) {
				result = logoutResult;
			}
			
			// On error inform user.
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			int selectedIndex = comboEnumerationValue.getSelectedIndex();
			
			updateInformation();
			updateEnumerationsInformation();
			
			// Select combo box item.
			if (selectedIndex >= 0) {
				int rowCount = comboEnumerationValue.getItemCount();
				if (selectedIndex >= rowCount) {
					selectedIndex = rowCount - 1;
				}
				comboEnumerationValue.setSelectedIndex(selectedIndex);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	private void updateInformation() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Set value.
	 */
	@Override
	public void setValue(Object value) {
		try {
			
			super.setValue(value);
			
			if (value instanceof EnumerationValue) {
				EnumerationValue enumerationValue = (EnumerationValue) value;
				
				// Select enumeration type and value.
				selectEnumerationType(enumerationValue.getEnumerationId());
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set default value state.
	 */
	@Override
	public void setDefault(boolean isDefault) {
		
		// Nothing to do.
	}
}
