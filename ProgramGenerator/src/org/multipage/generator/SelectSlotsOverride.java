/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SpringLayout;

import org.maclan.Slot;
import org.multipage.gui.CheckBoxList;
import org.multipage.gui.CheckBoxListManager;
import org.multipage.gui.Images;
import org.multipage.gui.ToolBarKit;
import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.Safe;

/**
 * Dialog that displays slot override information.
 * @author vakol
 *
 */
public class SelectSlotsOverride extends JDialog {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Confirm flag.
	 */
	private boolean confirm = false;

	/**
	 * Slot list.
	 */
	private CheckBoxList<Slot> slotList;

	/**
	 * Confirmed slots.
	 */
	private Set<Slot> confirmedSlots;

	// $hide<<$
	/**
	 * Components.
	 */
	private JLabel labelSelectSlots;
	private JScrollPane scrollPane;
	private JButton buttonCancel;
	private JButton buttonOk;
	private JToolBar toolBar;

	/**
	 * Show dialog.
	 * @param parentWindow
	 * @param slotsToPaste 
	 * @param slotsToDelete 
	 * @param clipboardSlots 
	 * @param allSlots 
	 * @return
	 */
	public static boolean showDialog(Window parentWindow, List<Slot> allSlots,
			List<Slot> clipboardSlots, List<Slot> slotsToDelete,
			List<Slot> slotsToPaste) {
		
		try {
			slotsToDelete.clear();
			slotsToPaste.clear();
			
			// Get overridden slots.
			List<Slot> overriddenSlots = new LinkedList<Slot>();
			for (Slot slot : clipboardSlots) {
				if (Slot.containsAlias(allSlots, slot.getAlias())) {
					overriddenSlots.add(slot);
				}
				else {
					slotsToPaste.add(slot);
				}
			}
			
			if (overriddenSlots.isEmpty()) {
				return true;
			}
			
			SelectSlotsOverride dialog = new SelectSlotsOverride(parentWindow);
			dialog.setSlotList(overriddenSlots);
			dialog.setVisible(true);
			if (dialog.confirm) {
				
				// Delete confirmed slots.
				for (Slot slot : dialog.confirmedSlots) {
					Slot originalSlot = Slot.getSlot(allSlots, slot.getAlias());
					slotsToDelete.add(originalSlot);
				}
				// Paste deleted slots.
				slotsToPaste.addAll(dialog.confirmedSlots);
			}
			
			return dialog.confirm;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Create the dialog.
	 * @param parentWindow 
	 */
	public SelectSlotsOverride(Window parentWindow) {
		super(parentWindow, ModalityType.APPLICATION_MODAL);
		
		try {
			// Initialize components.
			initComponents();
			// $hide>>$
			postCreate();
			// $hide<<$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	private void initComponents() {
		setTitle("org.multipage.generator.textSelectSlotsOverride");
		setBounds(100, 100, 450, 300);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		labelSelectSlots = new JLabel("org.multipage.generator.textSelectSlotsOverrideLabel");
		springLayout.putConstraint(SpringLayout.NORTH, labelSelectSlots, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, labelSelectSlots, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(labelSelectSlots);
		
		scrollPane = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, labelSelectSlots);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(scrollPane);
		
		buttonCancel = new JButton("textCancel");
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		buttonCancel.setMargin(new Insets(0, 0, 0, 0));
		buttonCancel.setPreferredSize(new Dimension(80, 25));
		springLayout.putConstraint(SpringLayout.SOUTH, buttonCancel, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, buttonCancel, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(buttonCancel);
		
		buttonOk = new JButton("textOk");
		buttonOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOk();
			}
		});
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -10, SpringLayout.NORTH, buttonOk);
		buttonOk.setMargin(new Insets(0, 0, 0, 0));
		buttonOk.setPreferredSize(new Dimension(80, 25));
		springLayout.putConstraint(SpringLayout.SOUTH, buttonOk, 0, SpringLayout.SOUTH, buttonCancel);
		springLayout.putConstraint(SpringLayout.EAST, buttonOk, -7, SpringLayout.WEST, buttonCancel);
		getContentPane().add(buttonOk);
		
		toolBar = new JToolBar();
		springLayout.putConstraint(SpringLayout.EAST, toolBar, -10, SpringLayout.WEST, buttonOk);
		toolBar.setFloatable(false);
		springLayout.putConstraint(SpringLayout.NORTH, toolBar, 0, SpringLayout.SOUTH, scrollPane);
		springLayout.putConstraint(SpringLayout.WEST, toolBar, 0, SpringLayout.WEST, labelSelectSlots);
		springLayout.putConstraint(SpringLayout.SOUTH, toolBar, 30, SpringLayout.SOUTH, scrollPane);
		getContentPane().add(toolBar);
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			Utility.centerOnScreen(this);
			localize();
			setIcons();
			createToolBar();
			setComboList();
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
			Utility.localize(labelSelectSlots);
			Utility.localize(buttonOk);
			Utility.localize(buttonCancel);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create toolbar.
	 */
	private void createToolBar() {
		try {
			
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/select_all.png",
					"org.multipage.generator.tooltipSelectAllSlots", () -> selectAll());
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/deselect_all.png",
					"org.multipage.generator.tooltipUnselectAllSlots", () -> unselectAll());
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
			
			buttonOk.setIcon(Images.getIcon("org/multipage/generator/images/ok_iconp.png"));
			buttonCancel.setIcon(Images.getIcon("org/multipage/generator/images/cancel_iconp.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On OK.
	 */
	protected void onOk() {
		
		confirm = true;
		dispose();
	}
	
	/**
	 * On cancel.
	 */
	protected void onCancel() {
		
		confirm = false;
		dispose();
	}

	/**
	 * Set combo list.
	 */
	private void setComboList() {
		try {
			
			slotList = new CheckBoxList<Slot>();
			scrollPane.setViewportView(slotList);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set slot list.
	 * @param overrideSlots
	 */
	private void setSlotList(List<Slot> overrideSlots) {
		try {
			
			confirmedSlots = new HashSet<Slot>();
			slotList.setContentManager(new CheckBoxListManager<Slot>() {
				// Load item.
				@Override
				protected boolean loadItem(int index, Obj<Slot> object,
						Obj<String> text, Obj<Boolean> selected) {
					
					try {
						if (index >= overrideSlots.size()) {
							return false;
						}
						Slot slot = overrideSlots.get(index);
						object.ref = slot;
						text.ref = slot.getAlias();
						selected.ref = false;
						return true;
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return false;
				}
				// Process change.
				@Override
				protected boolean processChange(Slot slot, boolean selected) {
					
					try {
						if (selected) {
							confirmedSlots.add(slot);
						}
						else {
							confirmedSlots.remove(slot);
						}
						return true;
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
	 * Select all items.
	 */
	public void selectAll() {
		try {
			
			slotList.selectAll(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Unselect all items.
	 */
	public void unselectAll() {
		try {
			
			slotList.selectAll(false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
