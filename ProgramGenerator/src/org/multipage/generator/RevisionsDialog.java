/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */
package org.multipage.generator;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.maclan.Area;
import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.maclan.Revision;
import org.maclan.Slot;
import org.multipage.basic.ProgramBasic;
import org.multipage.gui.RendererJLabel;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.Utility;
import org.multipage.util.Safe;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Dialog that displays slot revisions information.
 * @author vakol
 *
 */
public class RevisionsDialog extends JDialog {

	/**
	 * Version
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Window position
	 */
	private static Rectangle bounds;
	
	/**
	 * Set default state
	 */
	public static void setDefaultData() {
		
		bounds = new Rectangle();
	}
	
	/**
	 * Load state.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void serializeData(StateInputStream inputStream)
			throws IOException, ClassNotFoundException {
		
		bounds = Utility.readInputStreamObject(inputStream, Rectangle.class);
	}
	
	/**
	 * Save state.
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream) 
			throws IOException {
		
		outputStream.writeObject(bounds);
	}
	
	/**
	 * Reference to last revision
	 */
	private Revision last;
	
	/**
	 * List model
	 */
	private DefaultListModel<Revision> model;
	
	/**
	 * Slot reference
	 */
	private Slot slot;
	
	/**
	 * Confirmation flag
	 */
	private boolean confirmed = false;
	private JLabel labelRevisions;
	private JList<Revision> list;
	private Consumer<Revision> fireSelection;
	private JButton buttonCancel;
	private JButton buttonOk;
	private JButton buttonDelete;
	
	/**
	 * Show dialog.
	 * @param parent
	 * @param slot 
	 * @param area 
	 * @return
	 */
	public static Revision showDialog(Component parent, Slot slot, Consumer<Revision> fireSelection) {
		
		try {
			RevisionsDialog dialog = new RevisionsDialog(parent);
			dialog.fireSelection = fireSelection;
			dialog.loadRevisions(slot);
			dialog.setVisible(true);
			
			if (!dialog.confirmed) {
				return null;
			}
			
			return dialog.getRevision();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Constructor
	 * @param parent
	 */
	public RevisionsDialog(Component parent) {
		super(Utility.findWindow(parent), ModalityType.APPLICATION_MODAL);
		
		try {
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					onCancel();
				}
			});
			setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			initComponents();
			initList();
			localize();
			loadDialog();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Initialize components
	 */
	private void initComponents() {
		
		setTitle("org.multipage.generator.titleSelectRevision");
		setBounds(100, 100, 381, 482);
		getContentPane().setLayout(new BorderLayout());
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				buttonOk = new JButton("textOk");
				buttonOk.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onOk();
					}
				});
				{
					buttonDelete = new JButton("org.multipage.generator.textDelete");
					buttonDelete.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							onDelete();
						}
					});
					buttonDelete.setPreferredSize(new Dimension(80, 25));
					buttonDelete.setActionCommand("OK");
					buttonPane.add(buttonDelete);
				}
				{
					Component horizontalStrut = Box.createHorizontalStrut(40);
					buttonPane.add(horizontalStrut);
				}
				buttonOk.setPreferredSize(new Dimension(80, 25));
				buttonOk.setActionCommand("OK");
				buttonPane.add(buttonOk);
				getRootPane().setDefaultButton(buttonOk);
			}
			{
				buttonCancel = new JButton("textCancel");
				buttonCancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onCancel();
					}
				});
				buttonCancel.setPreferredSize(new Dimension(80, 25));
				buttonCancel.setActionCommand("Cancel");
				buttonPane.add(buttonCancel);
			}
		}
		{
			labelRevisions = new JLabel("org.multipage.generator.titleAvailableRevisions");
			labelRevisions.setBounds(new Rectangle(2, 2, 2, 2));
			getContentPane().add(labelRevisions, BorderLayout.NORTH);
		}
		{
			JScrollPane scrollPane = new JScrollPane();
			getContentPane().add(scrollPane, BorderLayout.CENTER);
			{
				list = new JList<>();
				list.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						onRevisionDoubleClick(e);
					}
				});
				list.addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						onListSelectionChanged(e);
					}
				});
				scrollPane.setViewportView(list);
			}
		}
	}
	
	/**
	 * On double click on revision.
	 * @param event
	 */
	protected void onRevisionDoubleClick(MouseEvent event) {
		try {
			
			// Check double click.
			int mouseButton = event.getButton();
			int clickCount = event.getClickCount();
			if (!(mouseButton == MouseEvent.BUTTON1 && clickCount == 2)) {
				return;
			}
			
			// Get selected revision.
			Revision revision = (Revision) list.getSelectedValue();
			if (revision == null) {
				return;
			}
			
			// Let user edit revision description.
			String description = revision.description;
			if (description == null) {
				description = "";
			}
			
			description = Utility.input(this, "org.multipage.generator.messageEditRevisionDescription", description);
			if (description == null) {
	            return;
	        }
			
			if (description.isEmpty()) {
				description = null;
			}
			revision.description = description;
			
			// Save new revision description.
			Area slotArea = slot.getArea();
			if (slotArea == null) {
				list.updateUI();
				return;
			}
			long areaId = slotArea.getId();
			String slotAlias = slot.getAlias();
			
			Long revisionNumber = revision.number;
			if (revisionNumber == null) {
				revisionNumber = 0L;
			}
			
			try {
				Middle middle = ProgramBasic.loginMiddle();
				MiddleResult result = middle.updateSlotRevisionDescription(areaId, slotAlias, revisionNumber, description);
				result.throwPossibleException();
			}
			catch (Exception e) {
				String message = e.getLocalizedMessage();
				Utility.show2(this, message);
			}
			finally {
				ProgramBasic.logoutMiddle();
			}
			
			list.updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On delete revision
	 */
	protected void onDelete() {
		try {
			
			// Get selected revision
			Revision revision = (Revision) list.getSelectedValue();
			if (revision == null) {
				Utility.show(this, "org.multipage.generator.messageSelectRevisionToDelete");
				return;
			}
			
			// At least one revision must exist
			if (model.size() == 1) {
				Utility.show(this, "org.multipage.generator.messageAtLeastOneRevision");
				return;
			}
			
			// Ask user if delete the revision
			if (!Utility.ask(this, "org.multipage.generator.messageDeleteRevisionNumber", revision.toString(last.equals(revision)))) {
				return;
			}
			
			// Delete revision
			MiddleResult result = MiddleResult.UNKNOWN_ERROR;
			try {
				Middle middle = ProgramBasic.loginMiddle();
				result = middle.removeSlotRevision(slot, revision);
			}
			catch (Exception e) {
				result = MiddleResult.sqlToResult(e);
			}
			finally {
				ProgramBasic.logoutMiddle();
			}
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			// Reload list
			loadRevisions(slot);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On OK
	 */
	protected void onOk() {
		try {
			
			Revision revision = getRevision();
			
			// Confirm revision
			confirmed = revision != null;
			saveDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
		dispose();
	}
	
	/**
	 * On cancel
	 */
	protected void onCancel() {
		try {
			
			saveDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
		
		dispose();
	}
	
	/**
	 * Localize components
	 */
	private void localize() {
		try {
			
			Utility.localize(this);
			Utility.localize(labelRevisions);
			Utility.localize(buttonOk);
			Utility.localize(buttonCancel);
			Utility.localize(buttonDelete);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * 
	 */
	private void initList() {
		try {
			
			model = new DefaultListModel<>();
			list.setModel(model);
			
			list.setCellRenderer(new ListCellRenderer<Revision>() {
				
				RendererJLabel renderer = new RendererJLabel();
				private Font monospaced = new Font(Font.MONOSPACED, Font.PLAIN, 12);
				
				@Override
				public Component getListCellRendererComponent(JList<? extends Revision> list, Revision revision, int index,
						boolean isSelected, boolean cellHasFocus) {
					
					try {
						renderer.setFont(monospaced );
						renderer.setText(revision.toString(last.equals(revision)));
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
	 * Helper function that delegates event action
	 * @param e
	 */
	protected void onListSelectionChanged(ListSelectionEvent e) {
		try {
			
			if (!list.getValueIsAdjusting()) {
				onSelected(list.getSelectedIndex());
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On selected list item
	 * @param index
	 */
	private void onSelected(int index) {
		try {
			
			if (fireSelection != null && index >= 0) {
				fireSelection.accept(model.getElementAt(index));
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load revisions
	 * @param slot 
	 */
	private void loadRevisions(Slot slot) {
		try {
			
			this.slot = slot;
			
			MiddleResult result;
			try {
				Middle middle = ProgramBasic.loginMiddle();
				LinkedList<Revision> revisions = new LinkedList<Revision>();
				result = middle.loadRevisions(slot, revisions);
				if (result.isOK()) {
					display(revisions);
				}
				
				// Set last revision slot ID.
				Revision lastRevision = revisions.getLast();
				if (lastRevision != null) {
					slot.setId(lastRevision.slotId);
				}
			}
			catch (Exception e) {
				result = MiddleResult.sqlToResult(e);
			}
			finally {
				ProgramBasic.logoutMiddle();
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
	 * Displays revisions
	 * @param revisions
	 */
	private void display(LinkedList<Revision> revisions) {
		try {
			
			model.clear();
			if (revisions.isEmpty()) {
				return;
			}
			
			last = revisions.getLast();
			for (Revision revision : revisions) {
				model.addElement(revision);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get revisions
	 * @return
	 */
	private Revision getRevision() {
		
		try {
			// Get selected revision.
			Revision selectedRevision = (Revision) list.getSelectedValue();
			if (selectedRevision == null) {
				
				// Get last list item, the current top revision.
				int count = model.getSize();
				if (count < 1) {
					return null;
				}
				Revision currentRevision = model.get(count - 1);
				return currentRevision;
			}
			return selectedRevision;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Load dialog
	 */
	private void loadDialog() {
		try {
			
			if (!bounds.isEmpty()) {
				setBounds(bounds);
			}
			else {
				Utility.centerOnScreen(this);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Save dialog
	 */
	private void saveDialog() {
		try {
			
			bounds = getBounds();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
