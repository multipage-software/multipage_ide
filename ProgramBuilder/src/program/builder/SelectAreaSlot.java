/*
 * Copyright 20110-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package program.builder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.LinkedList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.maclan.Area;
import org.maclan.Slot;
import org.maclan.SlotHolder;
import org.maclan.VersionObj;
import org.multipage.generator.ProgramGenerator;
import org.multipage.generator.SelectVersionDialog;
import org.multipage.gui.Images;
import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Dialog that enables to select area slot.
 * @author vakol
 *
 */
public class SelectAreaSlot extends JDialog {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Old text.
	 */
	public static String oldText = "";

	/**
	 * List modelAreas.
	 */
	private DefaultListModel modelAreas;

	/**
	 * Slots list model.
	 */
	private DefaultListModel modelSlots;

	/**
	 * Do not update listAreas flag.
	 */
	private boolean doNotUpdateList = false;

	/**
	 * Output values.
	 */
	private Object areaText;
	private String slotText;
	
	/**
	 * Slot reference.
	 */
	private Slot slot;

	/**
	 * Start area.
	 */
	private Area startArea;

	// $hide<<$
	/**
	 * Confirm flag.
	 */
	private boolean confirm = false;
	private JButton buttonOk;
	private JButton buttonCancel;
	private JLabel labelAreaAlias;
	private JTextField textAreaAlias;
	private JScrollPane scrollPaneAreas;
	private JList listAreas;
	private JScrollPane scrollPaneSlots;
	private JList listSlots;
	private JCheckBox checkLocal;
	private JSplitPane splitPane;

	/**
	 * SHow dialog.
	 * @param parent
	 * @param slot 
	 * @return
	 */
	public static Object [] showDialog(Window parent, Slot slot) {
		
		try {
			SelectAreaSlot dialog = new SelectAreaSlot(parent, slot);
			dialog.setVisible(true);
			
			if (!dialog.confirm ) {
				return null;
			}
			Object [] result = {dialog.areaText, dialog.slotText,
					dialog.checkLocal.isSelected()};
			return result;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Create the dialog.
	 * @param parent 
	 * @param slot 
	 */
	public SelectAreaSlot(Window parent, Slot slot) {
		super(parent, ModalityType.DOCUMENT_MODAL);
		
		try {
			// Initialize components.
			initComponents();
			// $hide>>$
			setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			this.slot = slot;
			postCreate();
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
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
		setTitle("builder.textSelectAreaSlot");
		setBounds(100, 100, 585, 409);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		buttonOk = new JButton("textOk");
		buttonOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOk();
			}
		});
		springLayout.putConstraint(SpringLayout.SOUTH, buttonOk, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, buttonOk, -96, SpringLayout.EAST, getContentPane());
		buttonOk.setPreferredSize(new Dimension(80, 25));
		buttonOk.setMargin(new Insets(0, 0, 0, 0));
		getContentPane().add(buttonOk);
		
		buttonCancel = new JButton("textCancel");
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		springLayout.putConstraint(SpringLayout.SOUTH, buttonCancel, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, buttonCancel, -10, SpringLayout.EAST, getContentPane());
		buttonCancel.setPreferredSize(new Dimension(80, 25));
		buttonCancel.setMargin(new Insets(0, 0, 0, 0));
		getContentPane().add(buttonCancel);
		
		labelAreaAlias = new JLabel("org.multipage.generator.textAreaAliasFilter");
		springLayout.putConstraint(SpringLayout.NORTH, labelAreaAlias, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, labelAreaAlias, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(labelAreaAlias);
		
		textAreaAlias = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, textAreaAlias, 6, SpringLayout.SOUTH, labelAreaAlias);
		springLayout.putConstraint(SpringLayout.WEST, textAreaAlias, 10, SpringLayout.WEST, getContentPane());
		textAreaAlias.setColumns(10);
		getContentPane().add(textAreaAlias);
		
		checkLocal = new JCheckBox("builder.textLocalAreaSlot");
		checkLocal.setSelected(true);
		springLayout.putConstraint(SpringLayout.NORTH, checkLocal, 0, SpringLayout.NORTH, textAreaAlias);
		springLayout.putConstraint(SpringLayout.SOUTH, checkLocal, 0, SpringLayout.SOUTH, textAreaAlias);
		checkLocal.setForeground(new Color(255, 0, 0));
		springLayout.putConstraint(SpringLayout.EAST, textAreaAlias, -20, SpringLayout.WEST, checkLocal);
		springLayout.putConstraint(SpringLayout.WEST, checkLocal, -200, SpringLayout.EAST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, checkLocal, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(checkLocal);
		
		splitPane = new JSplitPane();
		splitPane.setOneTouchExpandable(true);
		springLayout.putConstraint(SpringLayout.SOUTH, splitPane, -6, SpringLayout.NORTH, buttonOk);
		splitPane.setResizeWeight(0.5);
		springLayout.putConstraint(SpringLayout.NORTH, splitPane, 6, SpringLayout.SOUTH, textAreaAlias);
		springLayout.putConstraint(SpringLayout.WEST, splitPane, 0, SpringLayout.WEST, labelAreaAlias);
		springLayout.putConstraint(SpringLayout.EAST, splitPane, 0, SpringLayout.EAST, buttonCancel);
		getContentPane().add(splitPane);
		
		scrollPaneAreas = new JScrollPane();
		splitPane.setLeftComponent(scrollPaneAreas);
		springLayout.putConstraint(SpringLayout.NORTH, scrollPaneAreas, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, scrollPaneAreas, 0, SpringLayout.WEST, labelAreaAlias);
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPaneAreas, -258, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, scrollPaneAreas, -153, SpringLayout.EAST, textAreaAlias);
		
		listAreas = new JList();
		listAreas.setForeground(new Color(0, 128, 0));
		listAreas.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				onSelectList();
			}
		});
		scrollPaneAreas.setViewportView(listAreas);
		
		scrollPaneSlots = new JScrollPane();
		splitPane.setRightComponent(scrollPaneSlots);
		
		listSlots = new JList();
		listSlots.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					onOk();
				}
			}
		});
		listSlots.setForeground(new Color(0, 0, 128));
		scrollPaneSlots.setViewportView(listSlots);
	}

	/**
	 * On OK.
	 */
	protected void onOk() {
		try {
			
			// Get selected area and slot.
			int areaAliasIndex = listAreas.getSelectedIndex();
			
			if (areaAliasIndex == 0) {
				areaText = (Integer) 1;
			}
			else if (areaAliasIndex == 1) {
				areaText = (Integer) 2;
			}
			else {
				areaText = (String) listAreas.getSelectedValue();
			}
			
			slotText = (String) listSlots.getSelectedValue();
	
			if (areaText == null || slotText == null) {
				Utility.show(this, "org.multipage.generator.messageSelectAreaAndSlot");
				return;
			}
			
			confirm = true;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
		dispose();
	}
	
	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			// Center dialog.
			Utility.centerOnScreen(this);
			// Localize components.
			localize();
			// Set icons.
			setIcons();
			// Create and update listAreas.
			createAreasList();
			updateAreasList();
			// Create slots list.
			createSlotsList();
			// Set text listeners.
			setTextListener();
			// Set old text.
			textAreaAlias.setText(oldText);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load start area.
	 */
	private void loadStartArea() {
		try {
			
			// Select version.
			Obj<VersionObj> version = new Obj<VersionObj>();
			if (!SelectVersionDialog.showDialog(null, version)) {
				
				startArea = null;
				return;
			}
			
			Area currentArea = (Area) slot.getHolder();
			startArea = ProgramGenerator.getAreasModel().getStartArea(currentArea, version.ref.getId());
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
			
			setIconImage(Images.getImage("program/basic/images/main_icon.png"));
			buttonCancel.setIcon(Images.getIcon("org/multipage/generator/images/cancel_icon.png"));
			buttonOk.setIcon(Images.getIcon("org/multipage/generator/images/ok_icon.png"));
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
			
			Utility.localize(this);
			Utility.localize(buttonCancel);
			Utility.localize(buttonOk);
			Utility.localize(labelAreaAlias);
			Utility.localize(checkLocal);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create listAreas.
	 */
	private void createAreasList() {
		try {
			
			// Set model.
			modelAreas = new DefaultListModel();
			listAreas.setModel(modelAreas);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Create slots list.
	 */
	private void createSlotsList() {
		try {
			
			modelSlots = new DefaultListModel();
			listSlots.setModel(modelSlots);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set text listener.
	 */
	private void setTextListener() {
		try {
			
			textAreaAlias.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void removeUpdate(DocumentEvent e) {
					try {
						
						onTextChanged();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				@Override
				public void insertUpdate(DocumentEvent e) {
					try {
						
						onTextChanged();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				@Override
				public void changedUpdate(DocumentEvent e) {
					try {
						
						onTextChanged();
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
	 * On text changed.
	 */
	protected void onTextChanged() {
		try {
			
			if (!doNotUpdateList) {
				oldText = textAreaAlias.getText();
			}
			updateAreasList();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Update listAreas.
	 */
	private void updateAreasList() {
		try {
			
			if (doNotUpdateList) {
				return;
			}
			
			// Reset modelAreas.
			modelAreas.clear();
			// Get areas.
			LinkedList<Area> areas = ProgramGenerator.getAreasModel().getAreas();
			// Get inserted text.
			String text = textAreaAlias.getText();
			
			// Load area aliases.
			LinkedList<String> aliases = new LinkedList<String>();
			for (Area area : areas) {
				String alias = area.getAlias();
				if (!alias.isEmpty()) {
					
					if (!text.isEmpty() && !alias.startsWith(text)) {
						continue;
					}
					if (area.getSlotAliasesCount() == 0) {
						continue;
					}
					aliases.add(alias);
				}
			}
			// Sort aliases.
			Collections.sort(aliases);
			
			// Add to modelAreas.
			for (String alias : aliases) {
				modelAreas.addElement(alias);
			}
			
			// Insert this area.
			modelAreas.add(0, Resources.getString("org.multipage.generator.textStartArea"));
			
			// Insert this area at the beginning.
			modelAreas.add(0, Resources.getString("org.multipage.generator.textThisArea"));
			
			Safe.invokeLater(() -> {
				listAreas.setSelectedIndex(0);
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On select list.
	 */
	protected void onSelectList() {
		try {
			
			// Show local check box.
			checkLocal.setVisible(true);
			
			// If the first item is selected.
			if (listAreas.getSelectedIndex() == 0) {
				
				checkLocal.setVisible(false);
				updateThisAreaSlorList();
			}
			else if (listAreas.getSelectedIndex() == 1) {
				updateStartAreaSlotsList();
			}
			else {
				// Get area alias.
				String areaAlias = (String) listAreas.getSelectedValue();
				// Update slots list.
				updateSlotsList(areaAlias);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Update this area list.
	 */
	private void updateThisAreaSlorList() {
		try {
			
			// Reset list.
			modelSlots.clear();
			
			Area currentArea = null;
			
			if (slot != null) {
				
				SlotHolder holder = slot.getHolder();
				if (!(holder instanceof Area)) {
					return;
				}
				
				currentArea = (Area) holder;
			}
			
			if (currentArea != null) {
				
				// Load slot aliases.
				LinkedList<String> slotAliases = new LinkedList<String>();
				slotAliases.addAll(currentArea.getSlotAliases());
				// Sort the list.
				Collections.sort(slotAliases);
				
				// Insert to model.
				for (String slotAlias : slotAliases) {
					modelSlots.addElement(slotAlias);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Processing flag.
	 */
	private boolean isProcessing = false;
	
	/**
	 * Update start areas slots list.
	 */
	private void updateStartAreaSlotsList() {
		try {
			
			if (isProcessing) {
				return;
			}
	
			isProcessing = true;
			
			// Reset list.
			modelSlots.clear();
			
			// Get area.
			loadStartArea();
			isProcessing = false;
			if (startArea == null) {
				return;
			}
			
			// Load slot aliases.
			LinkedList<String> slotAliases = new LinkedList<String>();
			slotAliases.addAll(startArea.getSlotAliases());
			// Sort the list.
			Collections.sort(slotAliases);
			
			// Insert to model.
			for (String slotAlias : slotAliases) {
				modelSlots.addElement(slotAlias);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Update slots list.
	 * @param areaAlias 
	 */
	private void updateSlotsList(String areaAlias) {
		/*
		try {
			
			// Reset list.
			modelSlots.clear();
			if (areaAlias == null) {
				return;
			}
			// Get area.
			Area area = ProgramGenerator.getAreasModel().getArea(areaAlias);
			if (area == null) {
				return;
			}
			
			// Load slot aliases.
			LinkedList<String> slotAliases = new LinkedList<String>();
			slotAliases.addAll(area.getSlotAliases());
			// Sort the list.
			Collections.sort(slotAliases);
			
			// Insert to model.
			for (String slotAlias : slotAliases) {
				modelSlots.addElement(slotAlias);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
		*/
	}
}
