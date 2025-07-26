/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

import org.eclipse.wb.swing.FocusTraversalOnArray;
import org.maclan.Slot;
import org.maclan.SlotHolder;
import org.multipage.gui.FoundAttr;
import org.multipage.gui.Images;
import org.multipage.gui.TextFieldEx;
import org.multipage.gui.Utility;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Panel that displays slot editor for the Generator.
 * @author vakol
 *
 */
public class SlotEditorPanel extends SlotEditorBasePanel {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Reference to this editor.
	 */
	public SlotEditorPanel thisEditor = this;

	// $hide<<$
	/**
	 * Dialog components.
	 */
	private JButton buttonCancel;
	private JButton buttonOk;
	private JLabel labelSlotHolder;
	private JTextField textHolder;
	private JLabel labelAlias;
	private JTextField textAlias;
	private JPanel panelEditor;
	private JButton buttonSave;
	private JPanel panelAux;
	private JCheckBox checkDefaultValue;
	private Component horizontalGlue;
	private JButton buttonHelp;
	private JLabel labelSpecialValue;
	private Component horizontalGlue_1;
	private TextFieldEx textSpecialValue;
	private JButton buttonSpecialValue;
	private JMenuBar menuBar;
	private JMenu menuArea;
	private JMenu menuSlot;
	private JMenuItem menuAreaEdit;
	private JMenuItem menuEditResources;
	private JMenuItem menuEditDependencies;
	private JMenuItem menuSlotProperties;
	private JCheckBox checkLocalizedFlag;
	private JButton buttonDisplay;
	private Component horizontalStrut_1;
	private JLabel labelInheritable;
	private JToggleButton toggleDebug;
	private Component horizontalStrut_2;
	private Component horizontalStrut_3;
	private JButton buttonRender;
	private Component horizontalStrut_A;
	private JCheckBox checkInterpretPhp;
	private JButton buttonRevision;
	private JButton buttonCommit;
	private JButton buttonExpose;
	
	/**
	 * Expose dialog components. Use SlotEditor for Generator interface.
	 */
	@Override
	public Component getComponent() {
		return this;
	}
	@Override
	public JTextField getTextAlias() {
		return textAlias;
	}
	@Override
	public JCheckBox getCheckDefaultValue() {
		return checkDefaultValue;
	}
	@Override
	public TextFieldEx getTextSpecialValue() {
		return textSpecialValue;
	}
	@Override
	public JCheckBox getCheckLocalizedFlag() {
		return checkLocalizedFlag;
	}
	@Override
	public JTextField getTextHolder() {
		return textHolder;
	}
	@Override
	public Container getPanelEditor() {
		return panelEditor;
	}
	@Override
	public JCheckBox getCheckLocalizedText() {
		return checkLocalizedFlag;
	}
	@Override
	public JLabel getLabelSpecialValue() {
		return labelSpecialValue;
	}
	@Override
	public JButton getButtonSpecialValue() {
		return buttonSpecialValue;
	}
	@Override
	public JToggleButton getToggleDebug() {
		return toggleDebug;
	}
	@Override
	public JCheckBox getCheckInterpretPhp() {
		return checkInterpretPhp;
	}
	@Override
	public JLabel getLabelInheritable() {
		return labelInheritable;
	}
	
	/**
	 * Create the dialog.
	 * @param parentWindow
	 * @param slot 
	 * @param isNew 
	 * @param modal 
	 * @param useHtmlEditor 
	 * @param foundAttr
	 * @param callbacks
	 * @wbp.parser.constructor
	 */
	public SlotEditorPanel(Window parentWindow, Slot slot, boolean isNew,
			boolean modal, boolean useHtmlEditor, FoundAttr foundAttr, Callbacks callbacks) {
		
		try {
			// Set the new flag.
			this.isNew = isNew;
			// Set callbacks.
			this.callbacks = callbacks;
			// Initialize components.
			initComponents();
			// $hide>>$
			// Post creation.
			postCreate(slot, useHtmlEditor);
			// $hide<<$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Constructor.
	 * @param parentWindow 
	 * @param slot
	 * @param isNew
	 * @param useHtmlEditor
	 * @param useHtmlEditor2 
	 * @param foundAttr
	 * @param callbacks 
	 */
	public SlotEditorPanel(Window parentWindow, Slot slot, boolean isNew,
			boolean useHtmlEditor, FoundAttr foundAttr, Callbacks callbacks) {
		
		this(null, slot, isNew, false, useHtmlEditor, foundAttr, callbacks);
	}
	
	/**
	 * Post create.
	 */
	protected void postCreate(Slot slot, boolean useHtmlEditor) {
		try {
			
			// Original slot reference
			originalSlot = slot;
			// Make copy of slot object
			editedSlot = (Slot) slot.clone();
			
			// Localize components, set icons and tool tips.
			localize();
			setIcons();
			setToolTips();
			setKeyBindings();
			
			// Create editors.
			createEditors(useHtmlEditor);
			// Set listeners
			setListeners();
			
			// Do additional creation.
			boolean isUserSlot = getEditedSlot().isUserDefined();
			if (isUserSlot) {
				Utility.localize(checkLocalizedFlag);
			}
			
			// Enable slot properties trayMenu.
			slotPropertiesMenu();
			// Update dialog
			updateComponents();
			// Set debug toggle button
			setToggleDebug(Settings.getEnableDebugging());
			// Load dialog.
			loadDialog();
			// Set flag
			initialized = true;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Enable/disable slot properties menu.
	 */
	private void slotPropertiesMenu() {
		try {
			
			menuSlot.setVisible(getEditedSlot().isExternalProvider());
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
			
			Utility.localize(buttonOk);
			Utility.localize(buttonCancel);
			Utility.localize(buttonSave);
			Utility.localize(buttonCommit);
			Utility.localize(buttonRevision);
			Utility.localize(labelSlotHolder);
			Utility.localize(labelAlias);
			Utility.localize(checkDefaultValue);
			Utility.localize(labelSpecialValue);
			Utility.localize(menuArea);
			Utility.localize(menuSlot);
			Utility.localize(menuAreaEdit);
			Utility.localize(menuEditResources);
			Utility.localize(menuEditDependencies);
			Utility.localize(menuSlotProperties);
			Utility.localize(checkInterpretPhp);
			if (labelInheritable != null) {
				Utility.localize(labelInheritable);
			}
			Utility.localize(buttonExpose);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set icons.
	 */
	protected void setIcons() {
		try {
			
			buttonOk.setIcon(Images.getIcon("org/multipage/generator/images/ok_icon.png"));
			buttonCancel.setIcon(Images.getIcon("org/multipage/generator/images/cancel_icon.png"));
			buttonSave.setIcon(Images.getIcon("org/multipage/generator/images/save_icon.png"));
			buttonHelp.setIcon(Images.getIcon("org/multipage/generator/images/help_small.png"));
			buttonSpecialValue.setIcon(Images.getIcon("org/multipage/gui/images/find_icon.png"));
			menuArea.setIcon(Images.getIcon("org/multipage/gui/images/edit.png"));
			menuSlot.setIcon(Images.getIcon("org/multipage/gui/images/edit.png"));
			buttonRender.setIcon(Images.getIcon("org/multipage/generator/images/render.png"));
			buttonDisplay.setIcon(Images.getIcon("org/multipage/generator/images/display_home_page.png"));
			toggleDebug.setIcon(Images.getIcon("org/multipage/generator/images/debug.png"));
			buttonRevision.setIcon(Images.getIcon("org/multipage/generator/images/revision.png"));
			buttonCommit.setIcon(Images.getIcon("org/multipage/generator/images/commit.png"));
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
			
			buttonRender.setToolTipText(Resources.getString("org.multipage.generator.tooltipRenderHtmlPages"));
			buttonDisplay.setToolTipText(Resources.getString("org.multipage.generator.tooltipDisplayHomePage"));
			toggleDebug.setToolTipText(Resources.getString("org.multipage.generator.tooltipEnableDisplaySourceCode"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set key bindings.
	 */
	@SuppressWarnings("serial")
	protected void setKeyBindings() {
		try {
			
			panelEditor.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control S"), "save slot");
			panelEditor.getActionMap().put("save slot", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						
						// Call on save method.
						onSave();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get panel
	 * @return
	 */
	private JPanel getContentPane() {
		
		return this;
	}

	/**
	 * Initialize components.
	 */
	private void initComponents() {
		setMinimumSize(new Dimension(450, 430));
		setBounds(100, 100, 586, 470);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		buttonCancel = new JButton("textCancel");
		springLayout.putConstraint(SpringLayout.SOUTH, buttonCancel, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, buttonCancel, -10, SpringLayout.EAST, getContentPane());
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		buttonCancel.setPreferredSize(new Dimension(80, 25));
		buttonCancel.setMargin(new Insets(0, 0, 0, 0));
		getContentPane().add(buttonCancel);
		
		buttonOk = new JButton("textOk");
		springLayout.putConstraint(SpringLayout.NORTH, buttonOk, 0, SpringLayout.NORTH, buttonCancel);
		springLayout.putConstraint(SpringLayout.EAST, buttonOk, -6, SpringLayout.WEST, buttonCancel);
		buttonOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOk();
			}
		});
		buttonOk.setPreferredSize(new Dimension(80, 25));
		buttonOk.setMargin(new Insets(0, 0, 0, 0));
		getContentPane().add(buttonOk);
		
		labelSlotHolder = new JLabel("org.multipage.generator.textSlotHolder");
		springLayout.putConstraint(SpringLayout.EAST, labelSlotHolder, 0, SpringLayout.EAST, buttonCancel);
		getContentPane().add(labelSlotHolder);
		
		textHolder = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.WEST, labelSlotHolder, 0, SpringLayout.WEST, textHolder);
		springLayout.putConstraint(SpringLayout.SOUTH, labelSlotHolder, -6, SpringLayout.NORTH, textHolder);
		springLayout.putConstraint(SpringLayout.NORTH, textHolder, 30, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, textHolder, -240, SpringLayout.EAST, getContentPane());
		textHolder.setEditable(false);
		getContentPane().add(textHolder);
		textHolder.setColumns(10);
		
		labelAlias = new JLabel("org.multipage.generator.textSlotAlias2");
		springLayout.putConstraint(SpringLayout.NORTH, labelAlias, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, labelAlias, -6, SpringLayout.WEST, labelSlotHolder);
		getContentPane().add(labelAlias);
		
		textAlias = new TextFieldEx();
		textAlias.setFont(new Font("Tahoma", Font.BOLD, 11));
		textAlias.setMinimumSize(new Dimension(160, 20));
		springLayout.putConstraint(SpringLayout.WEST, labelAlias, 0, SpringLayout.WEST, textAlias);
		springLayout.putConstraint(SpringLayout.NORTH, textAlias, 30, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, textAlias, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, textAlias, -6, SpringLayout.WEST, textHolder);
		getContentPane().add(textAlias);
		textAlias.setColumns(10);
		
		panelEditor = new JPanel();
		springLayout.putConstraint(SpringLayout.WEST, panelEditor, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, panelEditor, -6, SpringLayout.NORTH, buttonCancel);
		springLayout.putConstraint(SpringLayout.EAST, panelEditor, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(panelEditor);
		panelEditor.setLayout(new BorderLayout(0, 0));
		
		buttonSave = new JButton("textSave");
		springLayout.putConstraint(SpringLayout.NORTH, buttonSave, 0, SpringLayout.NORTH, buttonCancel);
		springLayout.putConstraint(SpringLayout.EAST, buttonSave, -30, SpringLayout.WEST, buttonOk);
		buttonSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSave();
			}
		});
		buttonSave.setPreferredSize(new Dimension(80, 25));
		buttonSave.setMargin(new Insets(0, 0, 0, 0));
		getContentPane().add(buttonSave);
		setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{textAlias, panelEditor, buttonOk, buttonCancel, getContentPane(), labelSlotHolder, textHolder, labelAlias}));
		getContentPane().setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{panelEditor, buttonOk, buttonCancel, labelSlotHolder, textHolder, labelAlias, textAlias}));
		
		menuBar = new JMenuBar();
		springLayout.putConstraint(SpringLayout.NORTH, menuBar, 15, SpringLayout.SOUTH, textHolder);
		springLayout.putConstraint(SpringLayout.WEST, menuBar, 0, SpringLayout.WEST, labelAlias);
		getContentPane().add(menuBar);
		
		menuArea = new JMenu("org.multipage.generator.menuArea");
		menuBar.add(menuArea);
		
		menuAreaEdit = new JMenuItem("org.multipage.generator.menuAreaEdit");
		menuAreaEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onEditArea(AreaEditorFrame.NOT_SPECIFIED);
			}
		});
		menuArea.add(menuAreaEdit);
		
		menuArea.addSeparator();
		
		menuEditResources = new JMenuItem("org.multipage.generator.menuAreaEditResources");
		menuEditResources.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onEditArea(AreaEditorFrame.RESOURCES);
			}
		});
		menuArea.add(menuEditResources);
		
		menuEditDependencies = new JMenuItem("org.multipage.generator.menuAreaEditDependencies");
		menuEditDependencies.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onEditArea(AreaEditorFrame.DEPENDENCIES);
			}
		});
		menuArea.add(menuEditDependencies);
			
		menuSlot = new JMenu("org.multipage.generator.menuSlot");
		menuBar.add(menuSlot);
		
		menuSlotProperties = new JMenuItem("org.multipage.generator.menuSlotProperties");
		menuSlotProperties.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onSlotProperties();
			}
		});
		menuSlot.add(menuSlotProperties);
		
		panelAux = new JPanel();
		springLayout.putConstraint(SpringLayout.WEST, panelAux, 3, SpringLayout.EAST, menuBar);
		springLayout.putConstraint(SpringLayout.NORTH, panelEditor, 6, SpringLayout.SOUTH, panelAux);
		springLayout.putConstraint(SpringLayout.NORTH, panelAux, 10, SpringLayout.SOUTH, textHolder);
		springLayout.putConstraint(SpringLayout.EAST, panelAux, 0, SpringLayout.EAST, labelAlias);
		panelAux.setPreferredSize(new Dimension(10, 28));
		getContentPane().add(panelAux);
		panelAux.setLayout(new BoxLayout(panelAux, BoxLayout.X_AXIS));
		
		horizontalGlue = Box.createHorizontalGlue();
		panelAux.add(horizontalGlue);
		
		checkDefaultValue = new JCheckBox("org.multipage.generator.textSlotDefaultValue");
		checkDefaultValue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onDefaultValue();
			}
		});
		checkDefaultValue.setFont(new Font("Tahoma", Font.PLAIN, 13));
		panelAux.add(checkDefaultValue);
		
		horizontalStrut_A = Box.createHorizontalStrut(20);
		panelAux.add(horizontalStrut_A);
		
		checkInterpretPhp = new JCheckBox("org.multipage.generator.textInterpretPhp");
		checkInterpretPhp.setMargin(new Insets(4, 4, 4, 4));
		checkInterpretPhp.setBorder(new CompoundBorder(new LineBorder(new Color(0, 0, 0)), new LineBorder(new Color(192, 192, 192))));
		checkInterpretPhp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onInterpretPhp();
			}
		});
		checkInterpretPhp.setSelected(true);
		panelAux.add(checkInterpretPhp);
		
		Component horizontalStrut_B = Box.createHorizontalStrut(10);
		panelAux.add(horizontalStrut_B);
		
		checkLocalizedFlag = new JCheckBox("org.multipage.generator.textUserTextLocalized");
		checkLocalizedFlag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onUserLocalizedCheck();
			}
		});
		panelAux.add(checkLocalizedFlag);
		
		horizontalGlue_1 = Box.createHorizontalGlue();
		panelAux.add(horizontalGlue_1);
		
		buttonHelp = new JButton("");
		springLayout.putConstraint(SpringLayout.EAST, textHolder, -3, SpringLayout.WEST, buttonHelp);
		buttonHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onHelp();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, buttonHelp, 3, SpringLayout.SOUTH, labelSlotHolder);
		springLayout.putConstraint(SpringLayout.EAST, buttonHelp, 0, SpringLayout.EAST, buttonCancel);
		buttonHelp.setPreferredSize(new Dimension(25, 25));
		buttonHelp.setMargin(new Insets(0, 0, 0, 0));
		getContentPane().add(buttonHelp);
		
		labelSpecialValue = new JLabel("org.multipage.generator.textSlotSpecialValue");
		springLayout.putConstraint(SpringLayout.NORTH, labelSpecialValue, 6, SpringLayout.SOUTH, textHolder);
		springLayout.putConstraint(SpringLayout.WEST, labelSpecialValue, 0, SpringLayout.WEST, labelSlotHolder);
		getContentPane().add(labelSpecialValue);
		
		textSpecialValue = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.SOUTH, panelAux, 0, SpringLayout.SOUTH, textSpecialValue);
		
		buttonDisplay = new JButton("");
		buttonDisplay.setBorder(null);
		buttonDisplay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onDisplayHomePage();
			}
		});
		
		toggleDebug = new JToggleButton("");
		toggleDebug.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onToggleDebugging(toggleDebug.isSelected());
			}
		});
		toggleDebug.setMargin(new Insets(6, 6, 6, 6));
		panelAux.add(toggleDebug);
		
		horizontalStrut_2 = Box.createHorizontalStrut(3);
		horizontalStrut_2.setPreferredSize(new Dimension(3, 0));
		panelAux.add(horizontalStrut_2);
		
		buttonRender = new JButton("");
		buttonRender.setBorder(null);
		buttonRender.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRender();
			}
		});
		buttonRender.setMargin(new Insets(0, 0, 0, 0));
		panelAux.add(buttonRender);
		
		horizontalStrut_3 = Box.createHorizontalStrut(3);
		horizontalStrut_3.setPreferredSize(new Dimension(3, 0));
		panelAux.add(horizontalStrut_3);
		buttonDisplay.setMargin(new Insets(0, 0, 0, 0));
		panelAux.add(buttonDisplay);
		
		horizontalStrut_1 = Box.createHorizontalStrut(0);
		panelAux.add(horizontalStrut_1);
		springLayout.putConstraint(SpringLayout.NORTH, textSpecialValue, 3, SpringLayout.SOUTH, labelSpecialValue);
		springLayout.putConstraint(SpringLayout.WEST, textSpecialValue, 0, SpringLayout.WEST, labelSlotHolder);
		textSpecialValue.setColumns(10);
		getContentPane().add(textSpecialValue);
		
		buttonSpecialValue = new JButton("");
		buttonSpecialValue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSelectSpecialValue();
			}
		});
		springLayout.putConstraint(SpringLayout.EAST, textSpecialValue, -3, SpringLayout.WEST, buttonSpecialValue);
		springLayout.putConstraint(SpringLayout.NORTH, buttonSpecialValue, -3, SpringLayout.NORTH, textSpecialValue);
		springLayout.putConstraint(SpringLayout.EAST, buttonSpecialValue, 0, SpringLayout.EAST, buttonCancel);
		buttonSpecialValue.setPreferredSize(new Dimension(25, 25));
		buttonSpecialValue.setMargin(new Insets(0, 0, 0, 0));
		getContentPane().add(buttonSpecialValue);
		{
			labelInheritable = new JLabel("org.multipage.generator.textUserProviderInheritable");
			springLayout.putConstraint(SpringLayout.NORTH, labelInheritable, 6, SpringLayout.NORTH, buttonCancel);
			springLayout.putConstraint(SpringLayout.WEST, labelInheritable, 10, SpringLayout.WEST, this);
			getContentPane().add(labelInheritable);
		}
		
		buttonRevision = new JButton("org.multipage.generator.textRevisions");
		buttonRevision.setMargin(new Insets(0, 0, 0, 0));
		springLayout.putConstraint(SpringLayout.NORTH, buttonRevision, 0, SpringLayout.NORTH, buttonCancel);
		buttonRevision.setPreferredSize(new Dimension(80, 25));
		buttonRevision.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRevision();
			}
		});
		add(buttonRevision);
		
		buttonCommit = new JButton("org.multipage.generator.textCommit");
		springLayout.putConstraint(SpringLayout.EAST, buttonRevision, -6, SpringLayout.WEST, buttonCommit);
		springLayout.putConstraint(SpringLayout.EAST, buttonCommit, -6, SpringLayout.WEST, buttonSave);
		buttonCommit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCommit();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, buttonCommit, 0, SpringLayout.NORTH, buttonCancel);
		buttonCommit.setPreferredSize(new Dimension(80, 25));
		buttonCommit.setMargin(new Insets(0, 0, 0, 0));
		add(buttonCommit);
		
		buttonExpose = new JButton("org.multipage.generator.textExpose");
		springLayout.putConstraint(SpringLayout.EAST, buttonExpose, -30, SpringLayout.WEST, buttonRevision);
		buttonExpose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onExpose();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, buttonExpose, 0, SpringLayout.NORTH, buttonCancel);
		buttonExpose.setPreferredSize(new Dimension(80, 25));
		buttonExpose.setMargin(new Insets(0, 0, 0, 0));
		add(buttonExpose);
	}
	
	/**
	 * Load current slot. You can override this method to load your own slot data.
	 * @return
	 */
	public Slot loadCurrentSlot() {
		
		try {
			boolean slotExists = updateHeadRevision();
			if (!slotExists) {
				close();
				return null;
			}
			
			Slot editedSlot = getEditedSlot();
			long slotId = editedSlot.getId();
			SlotHolder holder = editedSlot.getHolder();
			boolean userDefined = editedSlot.isUserDefined();
			
			// Trim alias.
			String alias = userDefined ? getTextAlias().getText() : editedSlot.getAlias();
			// Create new slot.
			Slot newSlot = new Slot(holder, alias);
			
			long revision = editedSlot.getRevision();
			boolean localizedTextSelected = getCheckLocalizedText().isSelected();
			char access = editedSlot.getAccess();
			boolean hidden = editedSlot.isHidden();
			boolean isDefault = getCheckDefaultValue().isSelected();
			String name = editedSlot.getName();
			boolean preferred = isPreferred();
			String specialValue = getSpecialValueNull();
			String externalProvider = editedSlot.getExternalProvider();
			boolean readsInput = editedSlot.getReadsInput();
			boolean writesOutput = editedSlot.getWritesOutput();
			Long descriptionId = editedSlot.getDescriptionId();
			
			// Get value.
			Object value = getValue();
			String valueMeaning = getValueMeaning();
			
			newSlot.setId(slotId);
			newSlot.setRevision(revision);
			newSlot.setValue(value);
			newSlot.setValueMeaning(valueMeaning);
			newSlot.setLocalized(value instanceof String && localizedTextSelected);
			newSlot.setAccess(access);
			newSlot.setHidden(hidden);
			newSlot.setDefault(isDefault);
			newSlot.setName(name);
			newSlot.setPreferred(preferred);
			newSlot.setUserDefined(userDefined);
			newSlot.setSpecialValue(specialValue);
			newSlot.setExternalProvider(externalProvider);
			newSlot.setReadsInput(readsInput);
			newSlot.setWritesOutput(writesOutput);
			newSlot.setDescriptionId(descriptionId);
			
			return newSlot;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Called by system when an update is needed.
	 */
	public void updateComponents() {
		try {
			
			boolean slotExists = updateHeadRevision();
			if (!slotExists) {
				close();
				return;
			}
			
			updateAreaName();
			Safe.setText(textAlias, editedSlot.getNameForGenerator());
			
			boolean isDefault = editedSlot.isDefault();
			Safe.setSelected(checkDefaultValue, isDefault);
			disableEditor(isDefault);
			
			setSpecialValueEnabled(!isDefault);
			String specialValue = getEditedSlot().getSpecialValue();
			setSpecialValueControl(specialValue);
			if (!isDefault && !specialValue.isEmpty()) {
				disableEditor(true);
			}
			
			// Set user slot.
			boolean isUserSlot = getEditedSlot().isUserDefined();
			if (!isUserSlot) {
				checkLocalizedFlag.setVisible(false);
			}
			
			// Show/hide "inheritable" bottom label.
			if (labelInheritable != null) {
				labelInheritable.setVisible(getEditedSlot().isInheritable());
			}
			
			// Set the localized slot flag.
			Safe.setSelected(checkLocalizedFlag, editedSlot.isLocalized());
			
			// Enable editor.
			textAlias.setEditable(isUserSlot);
			
			updateEditorType();
			updateSlotValue();
			updateFoundHighlight();
			updateEnumeration();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Enable automatic removing of application event listeners 
	 */
	@Override
	public boolean canAutoRemove() {
		
		return true;
	}
}
