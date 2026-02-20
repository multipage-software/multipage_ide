/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package program.builder;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Closeable;
import java.io.IOException;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.maclan.Area;
import org.maclan.ConstructorGroup;
import org.maclan.ConstructorHolder;
import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.multipage.basic.ProgramBasic;
import org.multipage.generator.ProgramGenerator;
import org.multipage.generator.SelectSubAreaDialog;
import org.multipage.gui.ApplicationEvents;
import org.multipage.gui.GuiSignal;
import org.multipage.gui.Images;
import org.multipage.gui.TextFieldEx;
import org.multipage.gui.UpdatableComponent;
import org.multipage.gui.Utility;
import org.multipage.util.Resources;
import org.multipage.util.Safe;
import org.multipage.util.j;

/**
 * Panel that displays constructor groups.
 * @author vakol
 *
 */
public class ConstructorGroupPanel extends JPanel implements UpdatableComponent, Closeable {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor group.
	 */
	private ConstructorGroup constructorGroup;
	
	/**
	 * List model.
	 */
	private DefaultListModel model;

	// $hide<<$
	/**
	 * Components.
	 */
	private JLabel labelConstructorList;
	private JScrollPane scrollPane;
	private JList list;
	private JLabel labelExtensionArea;
	private JTextField textExtensionArea;
	private JButton buttonClearArea;
	private JButton buttonSelectArea;
	private JLabel labelGroupAlias;
	private JTextField textGroupAlias;

	/**
	 * Constructor.
	 */
	public ConstructorGroupPanel() {
		
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
		
		labelConstructorList = new JLabel("builder.textListOfGroupConstructors");
		springLayout.putConstraint(SpringLayout.WEST, labelConstructorList, 10, SpringLayout.WEST, this);
		add(labelConstructorList);
		
		scrollPane = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, labelConstructorList);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -10, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, this);
		add(scrollPane);
		
		list = new JList();
		scrollPane.setViewportView(list);
		
		labelExtensionArea = new JLabel("builder.textExtensionAreaLink");
		springLayout.putConstraint(SpringLayout.NORTH, labelExtensionArea, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, labelExtensionArea, 10, SpringLayout.WEST, this);
		add(labelExtensionArea);
		
		textExtensionArea = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textExtensionArea, 6, SpringLayout.SOUTH, labelExtensionArea);
		springLayout.putConstraint(SpringLayout.WEST, textExtensionArea, 10, SpringLayout.WEST, this);
		textExtensionArea.setPreferredSize(new Dimension(6, 25));
		textExtensionArea.setEditable(false);
		add(textExtensionArea);
		textExtensionArea.setColumns(10);
		
		buttonClearArea = new JButton("");
		buttonClearArea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onClearExtensionArea();
			}
		});
		springLayout.putConstraint(SpringLayout.EAST, buttonClearArea, -10, SpringLayout.EAST, this);
		buttonClearArea.setPreferredSize(new Dimension(25, 25));
		springLayout.putConstraint(SpringLayout.NORTH, buttonClearArea, 30, SpringLayout.NORTH, this);
		add(buttonClearArea);
		
		buttonSelectArea = new JButton("");
		buttonSelectArea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onSelectExtensionArea();
			}
		});
		springLayout.putConstraint(SpringLayout.EAST, textExtensionArea, 0, SpringLayout.WEST, buttonSelectArea);
		springLayout.putConstraint(SpringLayout.NORTH, buttonSelectArea, 30, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.EAST, buttonSelectArea, 0, SpringLayout.WEST, buttonClearArea);
		buttonSelectArea.setPreferredSize(new Dimension(25, 25));
		add(buttonSelectArea);
		
		labelGroupAlias = new JLabel("builder.textConstructorGroupAlias");
		springLayout.putConstraint(SpringLayout.NORTH, labelGroupAlias, 20, SpringLayout.SOUTH, textExtensionArea);
		springLayout.putConstraint(SpringLayout.NORTH, labelConstructorList, 30, SpringLayout.SOUTH, labelGroupAlias);
		springLayout.putConstraint(SpringLayout.WEST, labelGroupAlias, 0, SpringLayout.WEST, labelConstructorList);
		add(labelGroupAlias);
		
		textGroupAlias = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textGroupAlias, -3, SpringLayout.NORTH, labelGroupAlias);
		textGroupAlias.setPreferredSize(new Dimension(6, 25));
		springLayout.putConstraint(SpringLayout.WEST, textGroupAlias, 6, SpringLayout.EAST, labelGroupAlias);
		springLayout.putConstraint(SpringLayout.EAST, textGroupAlias, 0, SpringLayout.EAST, scrollPane);
		add(textGroupAlias);
		textGroupAlias.setColumns(10);
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			localize();
			setIcons();
			setToolTips();
			initializeTable();
			setListeners();
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
			
			Utility.localize(labelConstructorList);
			Utility.localize(labelExtensionArea);
			Utility.localize(labelGroupAlias);
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
			
			buttonSelectArea.setIcon(Images.getIcon("org/multipage/generator/images/area_node.png"));
			buttonClearArea.setIcon(Images.getIcon("org/multipage/generator/images/cancel_icon.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set tool tips.
	 */
	private void setToolTips() {
		try {
			
			buttonSelectArea.setToolTipText(Resources.getString("builder.tooltipSelectExtensionArea"));
			buttonClearArea.setToolTipText(Resources.getString("builder.tooltipClearExtensionArea"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set constructor group.
	 * @param constructorGroup
	 */
	public void setConstructorGroup(ConstructorGroup constructorGroup) {
		try {
			
			this.constructorGroup = constructorGroup;
			
			updateConstructors();
			updateExtensionArea();
			
			textGroupAlias.setText(constructorGroup.getAlias());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Initialize table.
	 */
	private void initializeTable() {
		try {
			
			model = new DefaultListModel<ConstructorHolder>();
			list.setModel(model);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * 	Set listeners.
	 */
	private void setListeners() {
		try {
			
			ApplicationEvents.receiver(this, GuiSignal.showOrHideIds, message -> {
				try {
					// Update linked area.
					updateExtensionArea();
				}
				catch (Throwable e) {
					Safe.exception(e);
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Update constructor list.
	 */
	private void updateConstructors() {
		try {
			// Clear model.
			model.clear(); 
			// Check constructor group.
			if (constructorGroup != null) {
				
				// Load all constructors in group.
				for (ConstructorHolder constructorHolder : constructorGroup.getConstructorHolders()) {
					
					// Skip invisible area holding the constructor.
					if (constructorHolder.isInvisible()) {
						continue;
					}
					
					// Get linked constructor area if any.
					ConstructorHolder linkedConstructorHolder = constructorHolder.getLinkedConstructorHolder();
					// Add direct or linked area (holdrer) to model.
					model.addElement(linkedConstructorHolder != null ? 
							linkedConstructorHolder : constructorHolder);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
		// Update list UI.
		list.updateUI();
	}
	
	/**
	 * Set root area.
	 */
	public void setRootArea(Area rootArea) {
		
		// TODO: Set area reference.
	}
	
	/**
	 * Update extension area.
	 */
	private void updateExtensionArea() {
		try {
			
			Area extensionArea = getExtensionArea();
			if (extensionArea != null) {
				String areaName = extensionArea.getDescriptionForGui();
				textExtensionArea.setText(areaName);
				// TODO: debug
				j.log("Extension area: " + areaName);
			}
		}
		catch(Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Get extension area.
	 */
	private Area getExtensionArea() {
		try {
			
			// Get root area as default value.
			Area rootArea = ProgramGenerator.getArea(0L);
			
			// Update current extension area.
			Area extensionArea = rootArea;
			Long extensionAreaId = constructorGroup.getExtensionAreaId();
			if (extensionAreaId != null) {
				
				Area foundExtensionArea = ProgramGenerator.getArea(extensionAreaId);
				if (foundExtensionArea != null) {
					extensionArea = foundExtensionArea;
				}
			}
			return extensionArea;
		}
		catch(Throwable e) {
			Safe.exception(e);
			return null;
		}
	}
	
	/**
	 * Update group alias.
	 */
	private void updateGroupAlias() {
		try {
			
			// Check constructor group.
			if (constructorGroup != null) {
				// Update group alias text box.
				String groupAlias = constructorGroup.getAliasNull();
				if (groupAlias == null) {
					groupAlias = "";
				}
				textGroupAlias.setText(groupAlias);
			}
		}
		catch(Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Select extension area.
	 */
	protected void onSelectExtensionArea() {
		try {
			
			// Update extension area.
			Area extensionArea = getExtensionArea();
			
			// Select constructor area.
			Area rootArea = ProgramGenerator.getArea(0L);
			Area selectedArea = SelectSubAreaDialog.showDialog(this, rootArea, extensionArea);
			if (selectedArea == null) {
				return;
			}
			
			// Set constructor group extension area ID and save it.
			long areaId = selectedArea.getId();
			constructorGroup.setExtensionAreaId(areaId);
			save();
			updateExtensionArea();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On clear extension area.
	 */
	protected void onClearExtensionArea() {
		try {
			
			// Reset constructor group extension area ID and save it.
			constructorGroup.setExtensionAreaId(null);
			save();
			updateExtensionArea();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Save constructor group.
	 */
	private void save() {
		try {
			
			// Check constructor group object
			if (constructorGroup == null) {
				return;
			}
			
			// Get constructor group area ID and alias and save it.
			Long extensionAreaId = constructorGroup.getExtensionAreaId();
			
			String alias = textGroupAlias.getText();
			constructorGroup.setAlias(alias);
			alias = constructorGroup.getAliasNull();
			
			// Prepare prerequisites.
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			
			MiddleResult result = middle.login(login);
			if (result.isOK()) {
				
				long constructorGroupId = constructorGroup.getId();
			
				result = middle.updateConstructorGroupExtension(
						constructorGroupId, extensionAreaId);
				
				if (result.isOK()) {
					
					result = middle.updateConstructorGroupAlias(
						constructorGroupId, alias);
				}
			}
			
			MiddleResult logoutResult = middle.logout(result);
			if (result.isOK()) {
				result = logoutResult;
			}
				
			if (result.isNotOK()) {
				result.show(this);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Stop editing construvtor group.
	 */
	public void stopEditing() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Save constructor group.
	 */
	public void saveConstructorGroup() {
		try {
			
			save();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Update dialog components.
	 */
	@Override
	public void updateComponents() {
		try {
			
			// Update linked area.
			updateExtensionArea();
			// Update group alias.
			updateGroupAlias();
			// Update constructor list.
			updateConstructors();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Close resources.
	 */
	@Override
	public void close() throws IOException {
		try {
			
			ApplicationEvents.removeReceivers(this);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
}
