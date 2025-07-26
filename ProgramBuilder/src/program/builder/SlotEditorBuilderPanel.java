/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2025-05-20
 *
 */
package program.builder;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
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

import org.eclipse.wb.swing.FocusTraversalOnArray;
import org.maclan.Slot;
import org.maclan.SlotHolder;
import org.maclan.SlotType;
import org.multipage.generator.GeneratorMainFrame;
import org.multipage.generator.Settings;
import org.multipage.generator.SlotEditorBasePanel;
import org.multipage.gui.ApplicationEvents;
import org.multipage.gui.FoundAttr;
import org.multipage.gui.GuiSignal;
import org.multipage.gui.Images;
import org.multipage.gui.TextFieldEx;
import org.multipage.gui.Utility;
import org.multipage.util.Safe;

/**
 * Panel that displays slot editor for the Builder.
 * @author vakol
 */
public class SlotEditorBuilderPanel extends SlotEditorBasePanel {
	
	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * These controls do nothing.
	 */
	private JCheckBox checkNoOperation = new JCheckBox();
	private JLabel labelNoOperation = new JLabel();
	
	/**
	 * Types combo.
	 */
	private SlotTypeCombo typesCombo;

	/**
	 * Access combo.
	 */
	private AccessComboBox accessCombo;
	
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
	private JPanel panelTypes;
	private JLabel labelValueType;
	private JLabel labelAccess;
	private JPanel panelAccess;
	private JCheckBox checkCanHidden;
	private JCheckBox checkDefaultValue;
	private JTextField textName;
	private JLabel labelName;
	private JCheckBox checkPreferred;
	private JCheckBox checkUserDefined;
	private JButton buttonHelp;
	private JLabel labelSpecialValue;
	private TextFieldEx textSpecialValue;
	private JButton buttonSpecialValue;
	private JMenuBar menuBar;
	private JMenu menuArea;
	private JMenuItem menuAreaEdit;
	private JMenuItem menuEditResources;
	private JMenuItem menuEditDependencies;
	private JMenuItem menuEditInheritance;
	private JMenuItem menuEditStartResource;
	private JMenuItem menuEditConstructors;
	private JMenuItem menuEditHelp;
	private JMenu menuSlot;
	private JMenuItem menuSlotProperties;
	private JCheckBox checkInterpretPhp;
	private JButton buttonDisplay;
	private JButton buttonRender;
	private JToggleButton toggleDebug;
	private JButton buttonExpose;
	private JButton buttonRevision;
	private JButton buttonCommit;
	
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
		return checkNoOperation;
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
		return checkNoOperation;
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
		return labelNoOperation;
	}
	
	/**
	 * Get panel
	 * @return
	 */
	private JPanel getContentPane() {
		
		return this;
	}
	
	/**
	 * Create the dialog.
	 * @param isNew 
	 * @param isNew 
	 * @param modal 
	 * @param useHtmlEditor 
	 * @param foundAttr 
	 * @param callbacks 
	 * @wbp.parser.constructor
	 */
	public SlotEditorBuilderPanel(Window parentWindow, Slot slot, boolean isNew,
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
	 * Post create.
	 */
	protected void postCreate(Slot slot, boolean useHtmlEditor) {
		try {
			
			// Make copy of slot object
			editedSlot = (Slot) slot.clone();
			originalSlot = slot;
			
			// Localize components, set icons and tool tips.
			localize();
			setIcons();
			// Create editors.
			createEditors(useHtmlEditor);
			
			// Update components with current edited slot.
			updateComponents();
			
			// Set listeners.
			setListeners();
			
			// Load dialog.
			loadDialog();
			initialized = true;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set listeners.
	 */
	@SuppressWarnings("serial")
	public void setListeners() {
		try {
			super.setListeners();
			
			typesCombo.addActionListener(e -> {
				try {
					
					// Get selected type.
					SlotType type = typesCombo.getSelected();
					selectEditor(type, true);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
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
	 * Initialize components.
	 */
	private void initComponents() {
		setMinimumSize(new Dimension(400, 300));
		
		setBounds(100, 100, 753, 575);
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
		springLayout.putConstraint(SpringLayout.EAST, textHolder, 0, SpringLayout.EAST, buttonCancel);
		textHolder.setEditable(false);
		getContentPane().add(textHolder);
		textHolder.setColumns(15);
		
		labelAlias = new JLabel("org.multipage.generator.textSlotAlias");
		springLayout.putConstraint(SpringLayout.NORTH, labelAlias, 10, SpringLayout.NORTH, getContentPane());
		getContentPane().add(labelAlias);
		
		textAlias = new TextFieldEx();
		textAlias.setMinimumSize(new Dimension(160, 20));
		springLayout.putConstraint(SpringLayout.WEST, labelAlias, 0, SpringLayout.WEST, textAlias);
		springLayout.putConstraint(SpringLayout.NORTH, textAlias, 30, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, textAlias, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(textAlias);
		textAlias.setColumns(20);
		
		panelTypes = new JPanel();
		springLayout.putConstraint(SpringLayout.WEST, panelTypes, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, panelTypes, 160, SpringLayout.WEST, getContentPane());
		getContentPane().add(panelTypes);
		panelTypes.setLayout(new BorderLayout(0, 0));
		
		labelValueType = new JLabel("builder.textValueType");
		springLayout.putConstraint(SpringLayout.WEST, labelValueType, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, panelTypes, 2, SpringLayout.SOUTH, labelValueType);
		springLayout.putConstraint(SpringLayout.SOUTH, panelTypes, 29, SpringLayout.SOUTH, labelValueType);
		springLayout.putConstraint(SpringLayout.NORTH, labelValueType, 6, SpringLayout.SOUTH, textAlias);
		getContentPane().add(labelValueType);
		
		panelEditor = new JPanel();
		springLayout.putConstraint(SpringLayout.SOUTH, panelEditor, -6, SpringLayout.NORTH, buttonCancel);
		springLayout.putConstraint(SpringLayout.WEST, panelEditor, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, panelEditor, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(panelEditor);
		panelEditor.setLayout(new BorderLayout(0, 0));
		
		buttonSave = new JButton("textSave");
		springLayout.putConstraint(SpringLayout.NORTH, buttonSave, 0, SpringLayout.NORTH, buttonCancel);
		springLayout.putConstraint(SpringLayout.EAST, buttonSave, -54, SpringLayout.WEST, buttonOk);
		buttonSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSave();
			}
		});
		buttonSave.setPreferredSize(new Dimension(80, 25));
		buttonSave.setMargin(new Insets(0, 0, 0, 0));
		getContentPane().add(buttonSave);
		
		labelAccess = new JLabel("builder.textAccessType");
		springLayout.putConstraint(SpringLayout.NORTH, labelAccess, 6, SpringLayout.SOUTH, textAlias);
		getContentPane().add(labelAccess);
		
		panelAccess = new JPanel();
		springLayout.putConstraint(SpringLayout.WEST, labelAccess, 0, SpringLayout.WEST, panelAccess);
		springLayout.putConstraint(SpringLayout.NORTH, panelAccess, 2, SpringLayout.SOUTH, labelAccess);
		springLayout.putConstraint(SpringLayout.WEST, panelAccess, 6, SpringLayout.EAST, panelTypes);
		springLayout.putConstraint(SpringLayout.SOUTH, panelAccess, 29, SpringLayout.SOUTH, labelAccess);
		springLayout.putConstraint(SpringLayout.EAST, panelAccess, 160, SpringLayout.EAST, panelTypes);
		getContentPane().add(panelAccess);
		panelAccess.setLayout(new BorderLayout(0, 0));
		
		checkCanHidden = new JCheckBox("builder.textIsSlotProtected");
		checkCanHidden.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onProtectedChange();
			}
		});
		getContentPane().add(checkCanHidden);
		
		checkDefaultValue = new JCheckBox("org.multipage.generator.textSlotDefaultValue");
		checkDefaultValue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onDefaultValue();
			}
		});
		getContentPane().add(checkDefaultValue);
		
		textName = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textName, 0, SpringLayout.NORTH, textHolder);
		springLayout.putConstraint(SpringLayout.WEST, textName, 6, SpringLayout.EAST, textAlias);
		springLayout.putConstraint(SpringLayout.EAST, textName, -6, SpringLayout.WEST, textHolder);
		getContentPane().add(textName);
		textName.setColumns(10);
		
		labelName = new JLabel("builder.textSlotName");
		springLayout.putConstraint(SpringLayout.NORTH, labelName, 0, SpringLayout.NORTH, labelAlias);
		springLayout.putConstraint(SpringLayout.WEST, labelName, 0, SpringLayout.WEST, textName);
		getContentPane().add(labelName);
		
		checkPreferred = new JCheckBox("builder.textSlotPreferred");
		springLayout.putConstraint(SpringLayout.NORTH, checkPreferred, 6, SpringLayout.SOUTH, textName);
		springLayout.putConstraint(SpringLayout.NORTH, checkCanHidden, 0, SpringLayout.SOUTH, checkPreferred);
		springLayout.putConstraint(SpringLayout.WEST, checkCanHidden, 0, SpringLayout.WEST, checkPreferred);
		springLayout.putConstraint(SpringLayout.WEST, checkPreferred, 30, SpringLayout.EAST, panelAccess);
		getContentPane().add(checkPreferred);
		
		checkUserDefined = new JCheckBox("builder.textSlotUserDefined");
		checkUserDefined.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onUserDefined();
			}
		});
		springLayout.putConstraint(SpringLayout.WEST, checkUserDefined, 120, SpringLayout.EAST, panelAccess);
		springLayout.putConstraint(SpringLayout.NORTH, checkDefaultValue, 0, SpringLayout.SOUTH, checkUserDefined);
		springLayout.putConstraint(SpringLayout.WEST, checkDefaultValue, 0, SpringLayout.WEST, checkUserDefined);
		springLayout.putConstraint(SpringLayout.NORTH, checkUserDefined, 6, SpringLayout.SOUTH, textName);
		getContentPane().add(checkUserDefined);
		
		buttonHelp = new JButton("");
		buttonHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onHelp();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, buttonHelp, 15, SpringLayout.SOUTH, textHolder);
		buttonHelp.setMargin(new Insets(0, 0, 0, 0));
		springLayout.putConstraint(SpringLayout.EAST, buttonHelp, -10, SpringLayout.EAST, getContentPane());
		buttonHelp.setPreferredSize(new Dimension(25, 25));
		getContentPane().add(buttonHelp);
		
		labelSpecialValue = new JLabel("org.multipage.generator.textSlotSpecialValue");
		springLayout.putConstraint(SpringLayout.NORTH, labelSpecialValue, 12, SpringLayout.SOUTH, panelAccess);
		springLayout.putConstraint(SpringLayout.WEST, labelSpecialValue, 0, SpringLayout.WEST, labelValueType);
		getContentPane().add(labelSpecialValue);
		
		textSpecialValue = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, panelEditor, 10, SpringLayout.SOUTH, textSpecialValue);
		springLayout.putConstraint(SpringLayout.NORTH, textSpecialValue, 6, SpringLayout.SOUTH, checkCanHidden);
		springLayout.putConstraint(SpringLayout.WEST, textSpecialValue, 3, SpringLayout.EAST, labelSpecialValue);
		textSpecialValue.setMinimumSize(new Dimension(160, 20));
		textSpecialValue.setColumns(10);
		getContentPane().add(textSpecialValue);
		
		buttonSpecialValue = new JButton("");
		buttonSpecialValue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSelectSpecialValue();
			}
		});
		springLayout.putConstraint(SpringLayout.EAST, textSpecialValue, 0, SpringLayout.WEST, buttonSpecialValue);
		springLayout.putConstraint(SpringLayout.NORTH, buttonSpecialValue, 0, SpringLayout.NORTH, textSpecialValue);
		springLayout.putConstraint(SpringLayout.WEST, buttonSpecialValue, 0, SpringLayout.EAST, panelAccess);
		buttonSpecialValue.setPreferredSize(new Dimension(20, 20));
		buttonSpecialValue.setMargin(new Insets(0, 0, 0, 0));
		getContentPane().add(buttonSpecialValue);
		
		menuBar = new JMenuBar();
		springLayout.putConstraint(SpringLayout.NORTH, menuBar, 12, SpringLayout.SOUTH, panelAccess);
		springLayout.putConstraint(SpringLayout.EAST, menuBar, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(menuBar);
		
		menuArea = new JMenu("org.multipage.generator.menuArea");
		menuBar.add(menuArea);
		
		menuAreaEdit = new JMenuItem("org.multipage.generator.menuAreaEdit");
		menuAreaEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onEditArea(AreaEditorFrameBuilder.NOT_SPECIFIED);
			}
		});
		menuArea.add(menuAreaEdit);
		
		menuArea.addSeparator();
		
		menuEditInheritance = new JMenuItem("builder.menuAreaEditInheritance");
		menuEditInheritance.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onEditArea(AreaEditorFrameBuilder.INHERITANCE);
			}
		});
		menuArea.add(menuEditInheritance);
		
		menuEditResources = new JMenuItem("org.multipage.generator.menuAreaEditResources");
		menuEditResources.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onEditArea(AreaEditorFrameBuilder.RESOURCES);
			}
		});
		menuArea.add(menuEditResources);
		
		menuEditStartResource = new JMenuItem("builder.menuAreaEditStartResource");
		menuEditStartResource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onEditArea(AreaEditorFrameBuilder.START_RESOURCE);
			}
		});
		menuArea.add(menuEditStartResource);
		
		menuEditDependencies = new JMenuItem("org.multipage.generator.menuAreaEditDependencies");
		menuEditDependencies.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onEditArea(AreaEditorFrameBuilder.DEPENDENCIES);
			}
		});
		menuArea.add(menuEditDependencies);
		
		menuEditConstructors = new JMenuItem("builder.menuAreaEditConstructors");
		menuEditConstructors.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onEditArea(AreaEditorFrameBuilder.CONSTRUCTORS);
			}
		});
		menuArea.add(menuEditConstructors);
		
		menuEditHelp = new JMenuItem("builder.menuAreaEditHelp");
		menuEditHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onEditArea(AreaEditorFrameBuilder.HELP);
			}
		});
		menuArea.add(menuEditHelp);
		
		menuSlot = new JMenu("org.multipage.generator.menuSlot");
		menuBar.add(menuSlot);
		
		menuSlotProperties = new JMenuItem("org.multipage.generator.menuSlotProperties");
		menuSlotProperties.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSlotProperties();
			}
		});
		menuSlot.add(menuSlotProperties);
		
		checkInterpretPhp = new JCheckBox("org.multipage.generator.textInterpretPhp");
		springLayout.putConstraint(SpringLayout.NORTH, checkInterpretPhp, 3, SpringLayout.SOUTH, checkCanHidden);
		springLayout.putConstraint(SpringLayout.WEST, checkInterpretPhp, 30, SpringLayout.EAST, textSpecialValue);
		getContentPane().add(checkInterpretPhp);
		
		buttonDisplay = new JButton("");
		buttonDisplay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onDisplayResult();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, buttonDisplay, 12, SpringLayout.SOUTH, textHolder);
		springLayout.putConstraint(SpringLayout.EAST, buttonDisplay, -30, SpringLayout.WEST, buttonHelp);
		buttonDisplay.setMargin(new Insets(2, 2, 2, 2));
		buttonDisplay.setPreferredSize(new Dimension(32, 32));
		getContentPane().add(buttonDisplay);
		
		buttonRender = new JButton("");
		buttonRender.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRenderResult();
			}
		});
		springLayout.putConstraint(SpringLayout.EAST, buttonRender, -3, SpringLayout.WEST, buttonDisplay);
		buttonRender.setPreferredSize(new Dimension(32, 32));
		springLayout.putConstraint(SpringLayout.NORTH, buttonRender, 0, SpringLayout.NORTH, buttonDisplay);
		getContentPane().add(buttonRender);
		
		toggleDebug = new JToggleButton("");
		toggleDebug.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onToggleDebug();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, toggleDebug, 0, SpringLayout.NORTH, buttonRender);
		springLayout.putConstraint(SpringLayout.EAST, toggleDebug, -3, SpringLayout.WEST, buttonRender);
		toggleDebug.setPreferredSize(new Dimension(32, 32));
		toggleDebug.setMargin(new Insets(2, 2, 2, 2));
		getContentPane().add(toggleDebug);
		
		buttonExpose = new JButton("org.multipage.generator.textExpose");
		buttonExpose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onExpose();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, buttonExpose, 0, SpringLayout.NORTH, buttonCancel);
		buttonExpose.setPreferredSize(new Dimension(80, 25));
		buttonExpose.setMargin(new Insets(0, 0, 0, 0));
		getContentPane().add(buttonExpose);
		
		buttonRevision = new JButton("org.multipage.generator.textRevisions");
		springLayout.putConstraint(SpringLayout.EAST, buttonExpose, -30, SpringLayout.WEST, buttonRevision);
		buttonRevision.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRevision();
			}
		});
		springLayout.putConstraint(SpringLayout.SOUTH, buttonRevision, 0, SpringLayout.SOUTH, buttonCancel);
		buttonRevision.setPreferredSize(new Dimension(80, 25));
		buttonRevision.setMargin(new Insets(0, 0, 0, 0));
		getContentPane().add(buttonRevision);
		
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
		getContentPane().add(buttonCommit);
		setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{textAlias, panelEditor, buttonOk, buttonCancel, getContentPane(), labelSlotHolder, textHolder, labelAlias, panelTypes, labelValueType, checkInterpretPhp, buttonDisplay, buttonRender, toggleDebug, buttonExpose, buttonRevision, buttonCommit}));
		getContentPane().setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{panelEditor, buttonOk, buttonCancel, labelSlotHolder, textHolder, labelAlias, panelTypes, labelValueType, textAlias, checkInterpretPhp, buttonDisplay, buttonRender, toggleDebug, buttonExpose, buttonRevision, buttonCommit}));
		
		typesCombo = new SlotTypeCombo();
		panelTypes.add(typesCombo);
		
		accessCombo = new AccessComboBox();
		panelAccess.add(accessCombo);
	}
	
	/**
	 * Localize components.
	 */
	protected void localize() {
		try {
			
			Utility.localize(this);
			Utility.localize(buttonOk);
			Utility.localize(buttonCancel);
			Utility.localize(buttonSave);
			Utility.localize(labelSlotHolder);
			Utility.localize(labelAlias);
			Utility.localize(checkDefaultValue);
			Utility.localize(labelSpecialValue);
			Utility.localize(menuArea);
			Utility.localize(menuAreaEdit);
			Utility.localize(menuEditResources);
			Utility.localize(menuEditDependencies);
			Utility.localize(menuSlot);
			Utility.localize(menuSlotProperties);
			Utility.localize(checkInterpretPhp);
			Utility.localize(buttonExpose);
			Utility.localize(buttonRevision);
			Utility.localize(buttonCommit);
			
			// Only Builder
			Utility.localize(labelValueType);
			Utility.localize(labelAccess);
			Utility.localize(checkCanHidden);
			Utility.localize(labelName);
			Utility.localize(checkPreferred);
			Utility.localize(checkUserDefined);
			Utility.localize(menuEditInheritance);
			Utility.localize(menuEditStartResource);
			Utility.localize(menuEditConstructors);
			Utility.localize(menuEditHelp);
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
			buttonDisplay.setIcon(Images.getIcon("org/multipage/generator/images/display_home_page.png"));
			buttonRender.setIcon(Images.getIcon("org/multipage/generator/images/render.png"));
			toggleDebug.setIcon(Images.getIcon("org/multipage/generator/images/debug.png"));
			menuArea.setIcon(Images.getIcon("org/multipage/gui/images/edit.png"));
			buttonRevision.setIcon(Images.getIcon("org/multipage/generator/images/revision.png"));
			buttonCommit.setIcon(Images.getIcon("org/multipage/generator/images/commit.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	/**
	 * Check slot editor type.
	 */
	protected boolean checkSlotEditorType() {
		
		try {
			// Check slot and editor type.
			Object slotValue = getEditedSlot().getValue();
			SlotType slotType = getEditedSlot().getType();
			SlotType selectedType = typesCombo.getSelected();
			
			if (slotValue != null && slotType != selectedType) {
				Utility.show(this, "builder.messageSlotAndEditorTypeConflict");
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
	 * Loads current slot.
	 * @return
	 */
	@Override
	public Slot loadCurrentSlot() {
		
		try {
			boolean slotExists = updateHeadRevision();
			if (!slotExists) {
				close();
				return null;
			}
			
			Slot editedSlot = getEditedSlot();
			Long slotId = editedSlot.getId();
			SlotHolder holder = editedSlot.getHolder();
	
			// Trim alias.
			String alias = getAlias();
			// Create new slot.
			Slot newSlot = new Slot(holder, alias);
			
			char access = getAccess();
			boolean hidden = isHidden();
			boolean isDefault = isDefault();
			String name = getSlotName();
			boolean preferred = isPreferred();
			boolean userDefined = isUserDefined();
			String specialValue = getSpecialValue();
			String externalProvider = editedSlot.getExternalProvider();
			boolean readsInput = editedSlot.getReadsInput();
			boolean writesOutput = editedSlot.getWritesOutput();
			Long descriptionId = editedSlot.getDescriptionId();
			
			// Get value.
			Object value = getValue();
			String valueMeaning = getValueMeaning();
			
			newSlot.setId(slotId);
			newSlot.setValue(value);
			newSlot.setValueMeaning(valueMeaning);
			newSlot.setLocalized(value instanceof String && isLocalizedText());
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
	 * On protected flag change.
	 */
	protected void onProtectedChange() {
		try {
			
			boolean isProtected = checkCanHidden.isSelected();
			if (isProtected) {
				
				checkUserDefined.setSelected(false);
				checkPreferred.setSelected(false);
			}
			
			checkUserDefined.setEnabled(!isProtected);
			checkPreferred.setEnabled(!isProtected);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On check user defined flag.
	 */
	protected void onUserDefined() {
		try {
			
			if (checkUserDefined.isSelected()) {
				checkPreferred.setSelected(true);
				checkPreferred.setEnabled(false);
			}
			else {
				checkPreferred.setEnabled(true);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On display result.
	 */
	protected void onDisplayResult() {
		try {
			
			// Transmitt the display event.
			ApplicationEvents.transmit(this, GuiSignal.displayHomePage);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On render the result.
	 */
	protected void onRenderResult() {
		try {
			
			GeneratorMainFrame.getFrame().onRender(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On toggle debug button.
	 */
	protected void onToggleDebug() {
		try {
			
			final boolean selected = toggleDebug.isSelected();
			
			// Switch on or off debugging of PHP code
			Settings.setEnableDebugging(selected);
			
			// Transmit the "enable / disable" signal.
			ApplicationEvents.transmit(this, GuiSignal.debugging, selected);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Gets true value if localized text is selected.
	 * @return
	 */
	private boolean isLocalizedText() {
		
		try {
			return typesCombo.getSelected() == SlotType.LOCALIZED_TEXT;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Get hidden.
	 * @return
	 */
	private boolean isHidden() {
		
		try {
			return checkCanHidden.isSelected();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Get access.
	 * @return
	 */
	private char getAccess() {
		
		try {
			return accessCombo.getSelectedAccess();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return Slot.privateAccess;
	}

	/**
	 * Get slot name.
	 * @return
	 */
	private String getSlotName() {
		
		try {
			return textName.getText();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get alias.
	 * @return
	 */
	private String getAlias() {
		
		try {
			return textAlias.getText().trim();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get "user defined slot" flag.
	 * @return
	 */
	private boolean isUserDefined() {
		
		try {
			return checkUserDefined.isSelected();
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
			return checkPreferred.isSelected() || isUserDefined();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Get special value.
	 * @return
	 */
	protected String getSpecialValue() {
		
		try {
			return textSpecialValue.getText();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Called by system when an update is needed.
	 */
	public void updateComponents() {
		try {
			
			// Check if refactoring is needed.
			boolean slotExists = updateHeadRevision();
			if (!slotExists) {
				close();
				return;
			}
			
			updateAreaName();
			textName.setText(editedSlot.getName());
			textAlias.setText(editedSlot.getAlias());
			
			boolean userDefined = editedSlot.isUserDefined();
			boolean isPreferred = editedSlot.isPreferred();
			boolean isProtected = editedSlot.isHidden();
			boolean isDefault = editedSlot.isDefault();
			
			Safe.setSelected(checkDefaultValue, isDefault);
			disableEditor(isDefault);
			setSpecialValueEnabled(!isDefault);
			
			String specialValue = editedSlot.getSpecialValue();
			setSpecialValueControl(specialValue);
			if (!isDefault && !specialValue.isEmpty()) {
				disableEditor(true);
			}
			
			Safe.setSelected(checkUserDefined, userDefined);
			if (!userDefined) {
				Safe.setSelected(checkPreferred, isPreferred);
			}
			else {
				editedSlot.setPreferred(true);
				Safe.setSelected(checkPreferred, true);
				checkPreferred.setEnabled(false);
			}
			
			if (isProtected) {
				Safe.setSelected(checkUserDefined, false);
				Safe.setSelected(checkPreferred, false);
			}
			
			// Set access combo box and the hidden flag.
			accessCombo.selectItem(editedSlot.getAccess());
			Safe.setSelected(checkCanHidden, editedSlot.isHidden());
			
			// Select slot type and editor.
			updateEditorType();
			SlotType type = editedSlot.getTypeUseValueMeaning();
			Safe.setSelectedItem(typesCombo, type);
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
