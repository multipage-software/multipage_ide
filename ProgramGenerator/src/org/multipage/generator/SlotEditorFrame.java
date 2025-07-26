/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.maclan.Area;
import org.maclan.Slot;
import org.multipage.generator.SlotEditorBasePanel.Callbacks;
import org.multipage.gui.FoundAttr;
import org.multipage.gui.Images;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.TopMostButton;
import org.multipage.gui.UpdatableComponent;
import org.multipage.gui.Utility;
import org.multipage.util.Closable;
import org.multipage.util.Safe;

/**
 * Base frame class for slot editor.
 * @author vakol
 *
 */
public class SlotEditorFrame extends JFrame implements UpdatableComponent, Closable {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Bounds.
	 */
	public static Rectangle bounds;
	
	/**
	 * Set default data.
	 */
	public static void setDefaultData() {
		
		bounds = new Rectangle();
	}
	
	/**
	 * Load data.
	 * @param inputStream
	 */
	public static void seriliazeData(StateInputStream inputStream)
			throws IOException, ClassNotFoundException {
		
		Object data = inputStream.readObject();
		if (!(data instanceof Rectangle)) {
			throw new ClassNotFoundException();
		}
		bounds = (Rectangle) data;
		
		TextSlotEditorPanel.openHtmlEditor = inputStream.readBoolean();
	}
	
	/**
	 * Save data.
	 * @param outputStream
	 */
	public static void seriliazeData(StateOutputStream outputStream)
		throws IOException {

		outputStream.writeObject(bounds);
		outputStream.writeBoolean(TextSlotEditorPanel.openHtmlEditor);
	}
	
	/**
	 * Slot editor panel object.
	 */
	private SlotEditorBasePanel editor;
	
	/**
	 * Lunch the dialog.
	 * @param parentWindow
	 * @param slot
	 * @param isNew
	 * @param modal
	 * @param foundAttr
	 */
	public static void showDialog(Window parentWindow, Slot slot, boolean isNew,
			boolean modal, FoundAttr foundAttr) {
		try {
			
			if (showExisting(slot)) {
				return;
			}
			
			// Display new editor.
			SlotEditorFrame dialog = new SlotEditorFrame(parentWindow, slot, isNew, modal, true, foundAttr);
			dialog.setVisible(true);
			remeberEditor(dialog);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Lunch dialog.
	 * @param slot
	 * @param isNew
	 * @param useHtmlEditor
	 * @param foundAttr
	 * @param onChangeEvent
	 */
	public static void showDialog(Slot slot, boolean isNew, boolean useHtmlEditor,
			FoundAttr foundAttr) {
		try {
			
			if (showExisting(slot)) {
				return;
			}
			
			SlotEditorFrame dialog = new SlotEditorFrame(slot, isNew, useHtmlEditor, foundAttr);
			dialog.setVisible(true);
			remeberEditor(dialog);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Lunch the dialog.
	 * @param parentWindow
	 * @param slot
	 * @param isNew
	 * @param modal
	 * @param foundAttr
	 */
	public static void showDialogSimple(Window parentWindow, Slot slot,
			boolean isNew, boolean modal, FoundAttr foundAttr) {
		try {
			
			if (showExisting(slot)) {
				return;
			}
			
			SlotEditorFrame dialog = new SlotEditorFrame(parentWindow, slot, isNew, modal, false, foundAttr);
			remeberEditor(dialog);
			dialog.setVisible(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Display already opened slot editor.
	 * @param slot
	 * @return
	 */
	protected static boolean showExisting(Slot slot) {
		
		try {
			// Display already opened slot editor.
			Long slotId = slot.getId();
			if (slotId != null) {
				
				SlotEditorFrame editor = DialogNavigator.getSlotEditor(slotId);
				if (editor != null) {
					editor.setVisible(true);
					return true;
				}
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Constructor.
	 * @param slot
	 * @param isNew
	 * @param useHtmlEditor
	 * @param foundAttr
	 * @wbp.parser.constructor
	 */
	/**
	 * Create slot editor.
	 */
	private SlotEditorFrame(Slot slot, boolean isNew, boolean useHtmlEditor, FoundAttr foundAttr) {
		
		this(null, slot, isNew, false, useHtmlEditor, foundAttr);
	}
	
	/**
	 * Constructor.
	 * @param parentWindow
	 * @param slot
	 * @param isNew
	 * @param modal
	 * @param useHtmlEditor
	 * @param foundAttr
	 */
	private SlotEditorFrame(Window parentWindow, Slot slot, boolean isNew, boolean modal, boolean useHtmlEditor, FoundAttr foundAttr) {
		
		try {
			// Create new slot editor.
			Callbacks callbacks = getEditorCallbacks();
			editor = ProgramGenerator.newSlotEditorPanel(parentWindow, slot, isNew, modal, useHtmlEditor, foundAttr, callbacks);
			
			initComponents();
			// $hide>>$
			// Post creation.
			postCreate();
			// $hide<<$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Set editor callbacks.
	 */
	private SlotEditorBasePanel.Callbacks getEditorCallbacks() {
		try {
			return new SlotEditorBasePanel.Callbacks() {
				@Override
				public void afterNewSlot(Slot newSlot) {
					SlotEditorFrame.this.afterNewSlot(newSlot);
				}
				@Override
				public void afterSlotRevisionChange(Slot slot, Slot newSlot) {
					SlotEditorFrame.this.afterSlotRevisionChange(slot, newSlot);
				}
				@Override
				public void onOk() {
					SlotEditorFrame.this.onOk();
				}
				@Override
				public void onCancel() {
					SlotEditorFrame.this.onCancel();
				}
				@Override
				public void loadDialog() {
					SlotEditorFrame.this.loadDialog();
				}
				@Override
				public void saveDialog() {
					SlotEditorFrame.this.saveDialog();
				}
			};
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Initialize components.
	 */
	private void initComponents() {
		
		try {
			// Set editor and listeners
			getContentPane().add(editor);
			setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			setTitle("org.multipage.generator.textSlotEditorTitle");
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Post creation.
	 */
	protected void postCreate() {
		
		try {
			// Set title.
			editor.setTitle(this);
			// Add top most window toggle button.
			TopMostButton.add(this, editor);
			setIcons();
			setListeners();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Set icons.
	 */
	protected void setIcons() {
		try {
			
			setIconImage(Images.getImage("org/multipage/basic/images/main_icon.png"));
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
			
			GeneratorMainFrame.registerForUpdate(this);
			
			// On window closing.
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					try {
						
						onCancel();
						super.windowClosing(e);
						// Close frame.
						close();
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
	 * After new slot has been created.
	 */
	public void afterNewSlot(Slot newSlot) {
		try {
			
			if (newSlot == null) {
				return;
			}
			Long slotId = newSlot.getId();
			if (slotId == null || slotId == 0L) {
				return;
			}
			
			// Check if the editor is already saved in navigator.
			SlotEditorFrame dialog = DialogNavigator.getSlotEditor(slotId);
			if (dialog != null) {
				return;
			}
			// Remember the editor.
			remeberEditor(slotId, SlotEditorFrame.this);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * After slot revision is changed.
	 */
	public void afterSlotRevisionChange(Slot slot, Slot newSlot) {
		try {
			
			if (slot == null || newSlot == null) {
				return;
			}
			Long slotId = slot.getId();
			Long newSlotId = newSlot.getId();
			if (slotId == null || slotId == 0L || newSlotId == null || newSlotId == 0L) {
				return;
			}
			if (newSlotId == slotId) {
				return;
			}
			// Change slot editor ID.
			DialogNavigator.changeSlotEditorId(slotId, newSlotId);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * On OK button.
	 */
	public void onOk() {
		try {
			close();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On Cancel button.
	 */
	public void onCancel() {
		try {
			close();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Load dialog.
	 */
	public void loadDialog() {
		try {
			
			if (bounds.isEmpty()) {
				setBounds(0, 0, 800, 600);
				Utility.centerOnScreen(SlotEditorFrame.this);
				bounds = getBounds();
			}
			else {
				setBounds(bounds);
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
			
			bounds = getBounds();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get edited slot ID.
	 * @return
	 */
	public Long getSlotId() {
		
		try {

			Slot slot = editor.getEditedSlot();
			if (slot == null) {
				return null;
			}
			long slotId = slot.getId();
			if (slotId == 0L) {
				return null;
			}
			return slotId;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Remember opened editor.
	 * @param editor
	 */
	protected static void remeberEditor(SlotEditorFrame editor) {
		
		try {
			DialogNavigator.addSlotEditor(editor);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Remember opened editor.
	 * @param slotId
	 * @param editor
	 */
	protected static void remeberEditor(Long slotId, SlotEditorFrame editor) {
		
		try {
			DialogNavigator.addSlotEditor(slotId, editor);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Update GUI components.
	 */
	@Override
	public void updateComponents() {
		try {
			
			long areaId = editor.originalSlot.getHolder().getId();
			Area area = ProgramGenerator.getArea(areaId);
			if (area == null) {
				Safe.invokeLater(() -> {
					close();
				});
				return;
			}
			
			if (!editor.isNew) {
				long slotId = editor.originalSlot.getId();
				Slot slot = ProgramGenerator.getSlot(slotId);
				if (slot == null) {
					Safe.invokeLater(() -> {
						close();
					});
				}
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Called when slot editor is closed.
	 */
	@Override
	public void close() {
		try {
			GeneratorMainFrame.unregisterFromUpdate(this);
			editor.close();
			dispose();
			DialogNavigator.removeSlotEditor(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
