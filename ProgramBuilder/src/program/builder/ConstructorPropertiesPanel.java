/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package program.builder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.LineBorder;

import org.maclan.Area;
import org.maclan.ConstructorGroup;
import org.maclan.ConstructorHolder;
import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.multipage.basic.ProgramBasic;
import org.multipage.generator.AreaEditorFrame;
import org.multipage.generator.AreaResourcesEditor;
import org.multipage.generator.ProgramGenerator;
import org.multipage.generator.RelatedAreaPanel;
import org.multipage.generator.SelectSubAreaDialog;
import org.multipage.gui.ApplicationEvents;
import org.multipage.gui.Images;
import org.multipage.gui.TextFieldEx;
import org.multipage.gui.UpdatableComponent;
import org.multipage.gui.Utility;
import org.multipage.util.Resources;
import org.multipage.util.Safe;
import org.multipage.util.Saveable;

/**
 * Panel that displays constructor properties.
 * @author vakol
 *
 */
public class ConstructorPropertiesPanel extends JPanel implements Saveable, Closeable, UpdatableComponent {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor area reference.
	 */
	private Area constructorArea;

	/**
	 * Name change listener.
	 */
	private Runnable nameChangeListener;

	/**
	 * Constructor holder reference.
	 */
	private ConstructorHolder constructorHolder;

	/**
	 * Constructor holder link if it exists.
	 */
	private ConstructorHolder constructorHolderLink;
	
	/**
	 * Slot list panel.
	 */
	private SlotListPanelBuilder slotListPanel;
	
	/**
	 * Area resources editor.
	 */
	private AreaResourcesEditor areaResourcesEditor;

	/**
	 * Root constructor group reference.
	 */
	private ConstructorGroup rootConstructorGroup;

	// $hide<<$
	/**
	 * Components.
	 */
	private JLabel labelConstructorHolderName;
	private TextFieldEx textConstructorHolderName;
	private JCheckBox checkInheritance;
	private JLabel labelRelationSuperName;
	private TextFieldEx textSuperRelationName;
	private JLabel labelRelationSubName;
	private TextFieldEx textSubRelationName;
	private JLabel labelConstructorAreaReference;
	private TextFieldEx textArea;
	private JButton buttonSelectArea;
	private JTabbedPane tabbedPane;
	private JPanel panelProperties;
	private JPanel panelSlots;
	private JPanel panelResources;
	private JButton buttonEditArea;
	private JButton buttonUpdate;
	private JPanel panel;
	private RelatedAreaPanel panelRelatedArea;
	private JCheckBox checkAskForRelatedArea;
	private JPanel panel_1;
	private JLabel labelSubgroupAlias;
	private JTextField textSubgroupAlias;
	private JCheckBox checkInvisible;
	private JTextField textAlias;
	private JLabel labelAlias;
	private JButton buttonSaveAlias;
	private JCheckBox checkSetHome;

	/**
	 * Create the panel.
	 * @param nameChangeListener 
	 * @param rootConstructorGroup 
	 * @param rootArea 
	 */
	public ConstructorPropertiesPanel(Runnable nameChangeListener, ConstructorGroup rootConstructorGroup) {
		
		try {
			initComponents();
			
			 // $hide>>$
			this.rootConstructorGroup = rootConstructorGroup;
			
			postCreate();
			this.nameChangeListener = nameChangeListener;
			// $hide<<$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Set root area.
	 * @param rootArea
	 */
	public void setRootArea(Area rootArea) {
		
		// TODO: Set root area reference.
	}

	/**
	 * Fire name change.
	 */
	private void fireNameChangeListener() {
		try {
			
			if (nameChangeListener != null) {
				nameChangeListener.run();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Initialize components.s
	 */
	private void initComponents() {
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		labelConstructorHolderName = new JLabel("builder.textConstructorName");
		springLayout.putConstraint(SpringLayout.NORTH, labelConstructorHolderName, 10, SpringLayout.NORTH, this);
		add(labelConstructorHolderName);
		
		textConstructorHolderName = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.EAST, labelConstructorHolderName, -3, SpringLayout.WEST, textConstructorHolderName);
		springLayout.putConstraint(SpringLayout.NORTH, textConstructorHolderName, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, textConstructorHolderName, 104, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, textConstructorHolderName, -10, SpringLayout.EAST, this);
		textConstructorHolderName.setColumns(20);
		add(textConstructorHolderName);
		
		labelConstructorAreaReference = new JLabel("builder.textConstructorAreaReference");
		add(labelConstructorAreaReference);
		
		textArea = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textArea, 0, SpringLayout.NORTH, labelConstructorAreaReference);
		springLayout.putConstraint(SpringLayout.EAST, labelConstructorAreaReference, -3, SpringLayout.WEST, textArea);
		springLayout.putConstraint(SpringLayout.WEST, textArea, 0, SpringLayout.WEST, textConstructorHolderName);
		textArea.setEditable(false);
		textArea.setPreferredSize(new Dimension(6, 25));
		textArea.setMinimumSize(new Dimension(6, 25));
		textArea.setColumns(20);
		add(textArea);
		
		buttonSelectArea = new JButton("builder.textSelectConstructorArea");
		springLayout.putConstraint(SpringLayout.NORTH, buttonSelectArea, 0, SpringLayout.NORTH, textArea);
		springLayout.putConstraint(SpringLayout.EAST, buttonSelectArea, -10, SpringLayout.EAST, this);
		buttonSelectArea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSelectArea();
			}
		});
		buttonSelectArea.setMargin(new Insets(0, 0, 0, 0));
		buttonSelectArea.setPreferredSize(new Dimension(70, 25));
		add(buttonSelectArea);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		springLayout.putConstraint(SpringLayout.NORTH, tabbedPane, 30, SpringLayout.SOUTH, textArea);
		springLayout.putConstraint(SpringLayout.WEST, tabbedPane, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, tabbedPane, -10, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, tabbedPane, -10, SpringLayout.EAST, this);
		add(tabbedPane);
		
		panelProperties = new JPanel();
		tabbedPane.addTab("builder.textConstructorProperties", null, panelProperties, null);
		SpringLayout sl_panelProperties = new SpringLayout();
		panelProperties.setLayout(sl_panelProperties);
		
		checkInheritance = new JCheckBox("builder.textConstructorAreaInheritance");
		sl_panelProperties.putConstraint(SpringLayout.NORTH, checkInheritance, 20, SpringLayout.NORTH, panelProperties);
		sl_panelProperties.putConstraint(SpringLayout.WEST, checkInheritance, 180, SpringLayout.WEST, panelProperties);
		springLayout.putConstraint(SpringLayout.NORTH, checkInheritance, 10, SpringLayout.NORTH, panelProperties);
		springLayout.putConstraint(SpringLayout.WEST, checkInheritance, 10, SpringLayout.WEST, panelProperties);
		panelProperties.add(checkInheritance);
		
		labelRelationSubName = new JLabel("builder.textConstructorSubName");
		sl_panelProperties.putConstraint(SpringLayout.NORTH, labelRelationSubName, 20, SpringLayout.SOUTH, checkInheritance);
		sl_panelProperties.putConstraint(SpringLayout.EAST, labelRelationSubName, 70, SpringLayout.WEST, panelProperties);
		springLayout.putConstraint(SpringLayout.NORTH, labelRelationSubName, 65, SpringLayout.SOUTH, checkInheritance);
		springLayout.putConstraint(SpringLayout.WEST, labelRelationSubName, 0, SpringLayout.WEST, checkInheritance);
		panelProperties.add(labelRelationSubName);
		
		textSubRelationName = new TextFieldEx();
		sl_panelProperties.putConstraint(SpringLayout.NORTH, textSubRelationName, -3, SpringLayout.NORTH, labelRelationSubName);
		sl_panelProperties.putConstraint(SpringLayout.WEST, textSubRelationName, 6, SpringLayout.EAST, labelRelationSubName);
		sl_panelProperties.putConstraint(SpringLayout.EAST, textSubRelationName, -10, SpringLayout.EAST, panelProperties);
		springLayout.putConstraint(SpringLayout.NORTH, textSubRelationName, 6, SpringLayout.SOUTH, checkInheritance);
		springLayout.putConstraint(SpringLayout.WEST, textSubRelationName, 0, SpringLayout.WEST, textConstructorHolderName);
		springLayout.putConstraint(SpringLayout.EAST, textSubRelationName, -10, SpringLayout.EAST, this);
		textSubRelationName.setColumns(10);
		panelProperties.add(textSubRelationName);
		
		labelRelationSuperName = new JLabel("builder.textConstructorSuperName");
		sl_panelProperties.putConstraint(SpringLayout.NORTH, labelRelationSuperName, 24, SpringLayout.SOUTH, labelRelationSubName);
		sl_panelProperties.putConstraint(SpringLayout.EAST, labelRelationSuperName, 70, SpringLayout.WEST, panelProperties);
		springLayout.putConstraint(SpringLayout.NORTH, labelRelationSuperName, 44, SpringLayout.SOUTH, textSubRelationName);
		springLayout.putConstraint(SpringLayout.WEST, labelRelationSuperName, 107, SpringLayout.WEST, panelProperties);
		panelProperties.add(labelRelationSuperName);
		
		textSuperRelationName = new TextFieldEx();
		sl_panelProperties.putConstraint(SpringLayout.NORTH, textSuperRelationName, -3, SpringLayout.NORTH, labelRelationSuperName);
		sl_panelProperties.putConstraint(SpringLayout.WEST, textSuperRelationName, 6, SpringLayout.EAST, labelRelationSubName);
		sl_panelProperties.putConstraint(SpringLayout.EAST, textSuperRelationName, -10, SpringLayout.EAST, panelProperties);
		springLayout.putConstraint(SpringLayout.NORTH, textSuperRelationName, 61, SpringLayout.SOUTH, labelRelationSuperName);
		springLayout.putConstraint(SpringLayout.WEST, textSuperRelationName, -115, SpringLayout.WEST, textConstructorHolderName);
		springLayout.putConstraint(SpringLayout.EAST, textSuperRelationName, 0, SpringLayout.EAST, panelProperties);
		panelProperties.add(textSuperRelationName);
		textSuperRelationName.setColumns(10);
		
		labelSubgroupAlias = new JLabel("builder.textConstructorSubgroupAlias");
		sl_panelProperties.putConstraint(SpringLayout.NORTH, labelSubgroupAlias, 24, SpringLayout.SOUTH, textSuperRelationName);
		sl_panelProperties.putConstraint(SpringLayout.EAST, labelSubgroupAlias, 50, SpringLayout.EAST, labelRelationSubName);
		panelProperties.add(labelSubgroupAlias);
		
		textSubgroupAlias = new TextFieldEx();
		sl_panelProperties.putConstraint(SpringLayout.NORTH, textSubgroupAlias, -3, SpringLayout.NORTH, labelSubgroupAlias);
		sl_panelProperties.putConstraint(SpringLayout.WEST, textSubgroupAlias, 6, SpringLayout.EAST, labelSubgroupAlias);
		sl_panelProperties.putConstraint(SpringLayout.EAST, textSubgroupAlias, -50, SpringLayout.EAST, textSubRelationName);
		panelProperties.add(textSubgroupAlias);
		textSubgroupAlias.setColumns(10);
		
		panelSlots = new JPanel();
		tabbedPane.addTab("builder.textEditSlots", null, panelSlots, null);
		panelSlots.setLayout(new BorderLayout(0, 0));
		
		panelResources = new JPanel();
		tabbedPane.addTab("builder.textEditResources", null, panelResources, null);
		panelResources.setLayout(new BorderLayout(0, 0));
		
		panel = new JPanel();
		tabbedPane.addTab("builder.textEditRelatedArea", null, panel, null);
		SpringLayout sl_panel = new SpringLayout();
		panel.setLayout(sl_panel);
		
		panelRelatedArea = new RelatedAreaPanel();
		sl_panel.putConstraint(SpringLayout.NORTH, panelRelatedArea, 47, SpringLayout.NORTH, panel);
		sl_panel.putConstraint(SpringLayout.WEST, panelRelatedArea, 10, SpringLayout.WEST, panel);
		sl_panel.putConstraint(SpringLayout.SOUTH, panelRelatedArea, 72, SpringLayout.NORTH, panel);
		sl_panel.putConstraint(SpringLayout.EAST, panelRelatedArea, -10, SpringLayout.EAST, panel);
		panel.add(panelRelatedArea);
		
		panel_1 = new JPanel();
		sl_panel.putConstraint(SpringLayout.NORTH, panel_1, 23, SpringLayout.SOUTH, panelRelatedArea);
		sl_panel.putConstraint(SpringLayout.WEST, panel_1, 10, SpringLayout.WEST, panel);
		sl_panel.putConstraint(SpringLayout.EAST, panel_1, -10, SpringLayout.EAST, panel);
		panel.add(panel_1);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		checkAskForRelatedArea = new JCheckBox("builder.textAskForRelatedArea");
		panel_1.add(checkAskForRelatedArea);
		sl_panel.putConstraint(SpringLayout.WEST, checkAskForRelatedArea, 102, SpringLayout.WEST, panelRelatedArea);
		sl_panel.putConstraint(SpringLayout.SOUTH, checkAskForRelatedArea, -48, SpringLayout.SOUTH, panel);

		
		buttonEditArea = new JButton("");
		springLayout.putConstraint(SpringLayout.NORTH, buttonEditArea, 0, SpringLayout.NORTH, textArea);
		buttonEditArea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onEditConstructorArea();
			}
		});
		springLayout.putConstraint(SpringLayout.EAST, buttonEditArea, 0, SpringLayout.WEST, buttonSelectArea);
		buttonEditArea.setPreferredSize(new Dimension(25, 25));
		buttonEditArea.setMargin(new Insets(0, 0, 0, 0));
		add(buttonEditArea);
		
		buttonUpdate = new JButton("");
		springLayout.putConstraint(SpringLayout.NORTH, buttonUpdate, 0, SpringLayout.NORTH, labelConstructorAreaReference);
		buttonUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateAreaDisplay();
			}
		});
		springLayout.putConstraint(SpringLayout.EAST, textArea, 0, SpringLayout.WEST, buttonUpdate);
		springLayout.putConstraint(SpringLayout.EAST, buttonUpdate, 0, SpringLayout.WEST, buttonEditArea);
		buttonUpdate.setPreferredSize(new Dimension(25, 25));
		buttonUpdate.setMargin(new Insets(0, 0, 0, 0));
		add(buttonUpdate);
		
		checkInvisible = new JCheckBox("builder.textConstructorInvisible");
		springLayout.putConstraint(SpringLayout.NORTH, checkInvisible, 6, SpringLayout.SOUTH, textArea);
		springLayout.putConstraint(SpringLayout.WEST, checkInvisible, 0, SpringLayout.WEST, textConstructorHolderName);
		add(checkInvisible);
		
		textAlias = new TextFieldEx();
		textAlias.setPreferredSize(new Dimension(6, 22));
		textAlias.setBorder(new LineBorder(new Color(171, 173, 179)));
		springLayout.putConstraint(SpringLayout.NORTH, labelConstructorAreaReference, 3, SpringLayout.SOUTH, textAlias);
		springLayout.putConstraint(SpringLayout.NORTH, textAlias, 3, SpringLayout.SOUTH, textConstructorHolderName);
		springLayout.putConstraint(SpringLayout.WEST, textAlias, 0, SpringLayout.WEST, textConstructorHolderName);
		add(textAlias);
		textAlias.setColumns(10);
		
		labelAlias = new JLabel("builder.textConstructorAlias");
		springLayout.putConstraint(SpringLayout.NORTH, labelAlias, 0, SpringLayout.NORTH, textAlias);
		springLayout.putConstraint(SpringLayout.EAST, labelAlias, 0, SpringLayout.EAST, labelConstructorHolderName);
		add(labelAlias);
		
		buttonSaveAlias = new JButton("");
		springLayout.putConstraint(SpringLayout.NORTH, buttonSaveAlias, 0, SpringLayout.NORTH, textAlias);
		buttonSaveAlias.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSaveAlias();
			}
		});
		springLayout.putConstraint(SpringLayout.EAST, textAlias, 0, SpringLayout.WEST, buttonSaveAlias);
		springLayout.putConstraint(SpringLayout.EAST, buttonSaveAlias, -10, SpringLayout.EAST, this);
		buttonSaveAlias.setPreferredSize(new Dimension(22, 22));
		buttonSaveAlias.setMargin(new Insets(0, 0, 0, 0));
		add(buttonSaveAlias);
		
		checkSetHome = new JCheckBox("builder.textSetHome");
		springLayout.putConstraint(SpringLayout.NORTH, checkSetHome, 6, SpringLayout.SOUTH, textArea);
		springLayout.putConstraint(SpringLayout.WEST, checkSetHome, 6, SpringLayout.EAST, checkInvisible);
		add(checkSetHome);
	}

	/**
	 * Post create.
	 */
	private void postCreate() {
		try {
			
			createPanels();
			
			localize();
			setIcons();
			setToolTips();
			
			setListeners();
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
			
			buttonSaveAlias.setToolTipText(Resources.getString("builder.tooltipSaveConstructorAlias"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create panels.
	 */
	@SuppressWarnings("serial")
	private void createPanels() {
		try {
			
			slotListPanel = new SlotListPanelBuilder() {
				@Override
				protected void onChange() {
					try {
						
						// On change, update information.
						updateComponents();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			};
			
			slotListPanel.setDividerPositionToMaximum();
			slotListPanel.setDoNotSaveStateOnExit();
			
			panelSlots.add(slotListPanel);
			
			areaResourcesEditor = new AreaResourcesEditor();
			panelResources.add(areaResourcesEditor);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On save alias.
	 */
	protected void onSaveAlias() {
		try {
			
			if (constructorHolder != null) {
				
				String alias = textAlias.getText();
				/*if (!alias.isEmpty()) {
					
					Utility.show(this, "builder.messageEmptyConstructorAlias");
					return;
				}*/
				
				// Update constructor holder alias.
				Properties login = ProgramBasic.getLoginProperties();
				Middle middle = ProgramBasic.getMiddle();
				
				MiddleResult result = middle.updateConstructorHolderAlias(login, constructorHolder.getId(), alias);
				if (result.isNotOK()) {
					
					result.show(this);
					return;
				}
				
				constructorHolder.setAlias(alias);
				textAlias.setForeground(Color.BLACK);
				textAlias.setBorder(new LineBorder(new Color(171, 173, 179)));
			}
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
			
			// Set name listener.
			Utility.setTextChangeListener(textConstructorHolderName, () -> {
				try {

					// Set constructor holder name and fire change listener.
					if (constructorHolder != null) {
						constructorHolder.setName(textConstructorHolderName.getText());
					}
					
					fireNameChangeListener();
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// Set alias listener.
			Utility.setTextChangeListener(textAlias, () -> {
				try {
					
					// Set constructor holder name and fire change listener.
					if (constructorHolder != null) {
						
						textAlias.setForeground(Color.RED);
						textAlias.setBorder(new LineBorder(Color.RED));
					}
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// Set inheritance listener.
			checkInheritance.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						
						if (constructorHolder != null) {
							constructorHolder.setInheritance(checkInheritance.isSelected());
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			
			// Set sub relation name listener.
			Utility.setTextChangeListener(textSubRelationName, () -> {
				try {
					
					if (constructorHolder != null) {
						if (isLink()) {
							constructorHolderLink.setSubRelationName(textSubRelationName.getText());
						}
						else {
							constructorHolder.setSubRelationName(textSubRelationName.getText());
						}
					}
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// Set super relation name listener.
			Utility.setTextChangeListener(textSuperRelationName, () -> {
				try {
					
					if (constructorHolder != null) {
						if (isLink()) {
							constructorHolderLink.setSuperRelationName(textSuperRelationName.getText());
						}
						else {
							constructorHolder.setSuperRelationName(textSuperRelationName.getText());
						}
					}
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};	
			});
			
			// Set ask for related area listener.
			checkAskForRelatedArea.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						
						if (constructorHolder != null) {
							constructorHolder.setAskForRelatedArea(checkAskForRelatedArea.isSelected());
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			
			// Set sub group alias.
			Utility.setTextChangeListener(textSubgroupAlias, () -> {
				try {
					
					if (constructorHolder != null) {
						constructorHolder.setSubGroupAliases(textSubgroupAlias.getText());
					}
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// Set invisibility flag.
			checkInvisible.addActionListener((ActionEvent e) -> {
				try {
					
					if (constructorHolder != null) {
						constructorHolder.setInvisible(checkInvisible.isSelected());
						
						Safe.tryOnChange(checkInvisible, () -> {
							fireNameChangeListener();
						});
					}
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
	
			// Set home flag.
			checkSetHome.addActionListener((ActionEvent e) -> {
				try {
					
					if (constructorHolder != null) {
						constructorHolder.setHome(checkSetHome.isSelected());
						
						Safe.tryOnChange(checkSetHome, () -> {
							fireNameChangeListener();
						});
					}
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
	 * Returns true value if it is a link.
	 */
	private boolean isLink() {
		
		return constructorHolderLink != null;
	}
	
	/**
	 * Use possible link to a constructor.
	 * @return
	 */
	private ConstructorHolder getPossibleLink() {
		
		try {
			return isLink() ? constructorHolderLink : constructorHolder;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Set constructor holder.
	 * @param constructorHolder
	 */
	public void setConstructorHolder(ConstructorHolder constructorHolder) {
		try {
			
			this.constructorHolder = constructorHolder;
			this.constructorHolderLink = null;
			
			// If it is a link, use it.
			if (constructorHolder.isLinkId() && constructorHolder.isLinkObject()) {
				 
				this.constructorHolder = constructorHolder.getLinkedConstructorHolder();
	
				// Remember the link.
				this.constructorHolderLink = constructorHolder;
			}
			
			// Update editor components.
			updateConstructorName();
			updateConstructorAlias();
			updateConstructorProperties();
			updateConstructorAreaLink();

			// Update constructor area editors.
			updateConstructorDependencies();
			slotListPanel.updateComponents();
			areaResourcesEditor.updateComponents();
			updateRelatedArea();
			
			// Highlight alias editor.
			textAlias.setForeground(Color.BLACK);
			textAlias.setBorder(new LineBorder(new Color(171, 173, 179)));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Update constructor name.
	 */
	private void updateConstructorName() {
		try {
			
			if (constructorHolder == null) {
				return;
			}
			
			Safe.tryToUpdate(textConstructorHolderName, () -> {
				String name = constructorHolder.getName();
				textConstructorHolderName.setText(name);
			});
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Update constructor alias.
	 */
	private void updateConstructorAlias() {
		try {
			
			if (constructorHolder == null) {
				return;
			}
			
			Safe.tryToUpdate(textAlias, () -> {
				String alias = constructorHolder.getAlias();
				textAlias.setText(alias);
			});
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Update constructor properties.
	 */
	private void updateConstructorProperties() {
		try {
			
			if (constructorHolder == null) {
				return;
			}
			
			Safe.tryToUpdate(checkInvisible, () -> {
				boolean invisible = constructorHolder.isInvisible();
				checkInvisible.setSelected(invisible);
			});
			
			Safe.tryToUpdate(checkSetHome, () -> {
				boolean isSetHome = constructorHolder.isSetHome();
				checkSetHome.setSelected(isSetHome);
			});
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Update constructor area link.
	 */
	private void updateConstructorAreaLink() {
		try {
			
			if (constructorHolder == null) {
				return;
			}
			setConstructorArea(constructorHolder.getAreaId());
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Update constructor dependencies.
	 */
	private void updateConstructorDependencies() {
		try {
			
			ConstructorHolder possibleLink = getPossibleLink();
			if (constructorHolder == null || possibleLink == null) {
				return;
			}
			
			Safe.tryToUpdate(checkInheritance, () -> {
				boolean inheritance = constructorHolder.isInheritance();
				checkInheritance.setSelected(inheritance);
			});
			Safe.tryToUpdate(textSubRelationName, () -> {
				String subRelationName = possibleLink.getSubRelationName();
				textSubRelationName.setText(subRelationName);
			});
			Safe.tryToUpdate(textSuperRelationName, () -> {
				String superRelationName = possibleLink.getSuperRelationName();
				textSuperRelationName.setText(superRelationName);
			});
			Safe.tryToUpdate(textSubgroupAlias, () -> {
				String subGroupAliases = constructorHolder.getSubGroupAliases();
				textSubgroupAlias.setText(subGroupAliases);
			});
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Update related area.
	 */
	private void updateRelatedArea() {
		try {
			
			if (constructorHolder == null) {
				return;
			}
			panelRelatedArea.updateComponents();
			checkAskForRelatedArea.setSelected(constructorHolder.isAskForRelatedArea());
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Localize components.
	 */
	private void localize() {
		try {
			
			Utility.localize(labelConstructorHolderName);
			Utility.localize(checkInheritance);
			Utility.localize(labelRelationSubName);
			Utility.localize(labelRelationSuperName);
			Utility.localize(labelConstructorAreaReference);
			Utility.localize(buttonSelectArea);
			Utility.localize(tabbedPane);
			Utility.localize(checkAskForRelatedArea);
			Utility.localize(labelSubgroupAlias);
			Utility.localize(checkInvisible);
			Utility.localize(labelAlias);
			Utility.localize(checkSetHome);
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
			tabbedPane.setIconAt(0, Images.getIcon("org/multipage/generator/images/properties.png"));
			tabbedPane.setIconAt(1, Images.getIcon("org/multipage/generator/images/slot_icon.png"));
			tabbedPane.setIconAt(2, Images.getIcon("org/multipage/generator/images/resources_icon_gray.png"));
			tabbedPane.setIconAt(3, Images.getIcon("org/multipage/generator/images/area_related.png"));
			buttonEditArea.setIcon(Images.getIcon("org/multipage/generator/images/edit.png"));
			buttonUpdate.setIcon(Images.getIcon("org/multipage/generator/images/update_icon.png"));
			buttonSaveAlias.setIcon(Images.getIcon("org/multipage/generator/images/save_icon.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On select area.
	 */
	protected void onSelectArea() {
		try {
			
			Area rootArea = ProgramGenerator.getArea(0L);
			
			// Select constructor area.
			Area selectedArea = SelectSubAreaDialog.showDialog(this, rootArea, constructorArea);
			if (selectedArea == null) {
				return;
			}
			
			constructorArea = selectedArea;
			
			// Set constructor holder area ID.
			long constructorAreaId = constructorArea.getId();
			constructorHolder.setAreaId(constructorAreaId);
			
			updateAreaDisplay();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set constructor area.
	 * @param areaId
	 */
	private void setConstructorArea(long areaId) {
		try {
			
			constructorArea = ProgramBuilder.getArea(areaId);
		updateAreaDisplay();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Update area display.
	 */
	private void updateAreaDisplay() {
		try {
			
			// Set area text component, slot list and resource list.
			String areaText;
			LinkedList<Area> areas = new LinkedList<Area>();
			
			if (constructorArea != null) {
				
				areaText = constructorArea.getDescriptionForGui();
				areas.add(constructorArea);
			}
			else {
				areaText = "";
			}
			
			textArea.setText(areaText);
			
			// Set slot list.
			slotListPanel.setAreas(areas);
		
			// Set area resources' list.
			areaResourcesEditor.loadArea(constructorArea);
			
			// Set related area panel.
			panelRelatedArea.setArea(constructorArea);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Disable changes.
	 */
	public void disableChanges() {
		try {
			
			textConstructorHolderName.setEnabled(false);
			textAlias.setEnabled(false);
			checkInheritance.setEnabled(false);
			textSubRelationName.setEnabled(false);
			textSuperRelationName.setEnabled(false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On edit constructor area.
	 */
	protected void onEditConstructorArea() {
		try {
			
			// Update constructor area.
			if (constructorArea != null) {
				constructorArea = ProgramGenerator.getArea(constructorArea.getId());
			}
			
			// Execute area editor.
			AreaEditorFrame.showDialog(null, constructorArea);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	public void stopEditing() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Save dialog.
	 */
	public void saveDialog() {
		// TODO Auto-generated method stub
	}

	/**
	 * Save constructor holder.
	 */
	public void saveConstructorHolder() {
		try {
			
			if (constructorHolder == null) {
				return;
			}
			
			// Prepare prerequisites.
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			
			// Update constructor holder.
			MiddleResult result = middle.updateConstructorHolderProperties(login, constructorHolder);
			if (result.isNotOK()) {
				result.show(this);
			}
			
			// Update linked constructor holder if it exists.
			if (isLink()) {
				
				result = middle.updateConstructorHolderProperties(login, constructorHolderLink);
				if (result.isNotOK()) {
					result.show(this);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Save object.
	 */
	@Override
	public boolean save() {
		try {
			
			saveConstructorHolder();
			return true;
		}
		catch (Throwable e) {
			Safe.exception(e);
			return false;
		}
	}
	
	/**
	 * Update components.
	 */
	@Override
	public void updateComponents() {
		try {
			// Update constructor name.
			updateConstructorName();
			// Update constructor alias.
			updateConstructorAlias();
			// Update constructor properties.
			updateConstructorProperties();
			// Update constructor area link.
			updateConstructorAreaLink();
			// Update constructor dependencies.
			updateConstructorDependencies();
			// Update slot list.
			slotListPanel.updateComponents();
			// Update area resources editor.
			areaResourcesEditor.updateComponents();
			// Update related area.
			updateRelatedArea();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Close panel.
	 */
	@Override
	public void close() throws IOException {
		try {
			
			// Remove application event receivers from this panel.
			ApplicationEvents.removeReceivers(this);
			
			// Close slot list panel.
			slotListPanel.onClose();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
