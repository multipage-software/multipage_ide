/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import org.multipage.util.ProgressResult;
import org.multipage.util.Resources;
import org.multipage.util.Safe;
import org.multipage.util.SwingWorkerHelper;

/**
 * Dialog that displays progress bar.
 * @author vakol
 *
 */
public class ProgressDialog<TOutput> extends JDialog {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Reduce blink interval.
	 */
	static final int reduceBlinkIntervalMs = 700;

	/**
	 * Worker thread.
	 */
	private SwingWorkerHelper<TOutput> swingWorker;

	/**
	 * Worker thread result state.
	 */
	protected ProgressResult resultState = ProgressResult.NONE;

	/**
	 * List model.
	 */
	private DefaultListModel listModel;

	/**
	 * Components.
	 */
	private JLabel labelMessage;
	private JProgressBar progressBar;
	private JLabel labelNote;
	private JButton buttonCancel;
	private JPanel panel;
	private Component horizontalGlue1;
	private Component horizontalGlue2;
	private JCheckBox checkKill;
	private JScrollPane scrollPane;
	private JList list;

	/**
	 * Create the dialog.
	 * @param title 
	 * @param message 
	 */
	public ProgressDialog(Component parent, String title, String message) {
		super(Utility.findWindow(parent), ModalityType.APPLICATION_MODAL);
		
		try {
			setTitle(title);
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					onCancel();
				}
				@Override
				public void windowOpened(WindowEvent e) {
					onWindowOpened();
				}
			});
			setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			setModalityType(ModalityType.TOOLKIT_MODAL);
	
			// Initialize components.
			initComponents();
			// Post creation.
			// $hide>>$
			postCreation(message);
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
		setMinimumSize(new Dimension(280, 230));
		setBounds(100, 100, 342, 242);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		labelMessage = new JLabel("message");
		labelMessage.setFont(new Font("Tahoma", Font.BOLD, 13));
		springLayout.putConstraint(SpringLayout.NORTH, labelMessage, 20, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, labelMessage, 10, SpringLayout.WEST, getContentPane());
		labelMessage.setHorizontalAlignment(SwingConstants.CENTER);
		labelMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
		getContentPane().add(labelMessage);
		
		progressBar = new JProgressBar();
		springLayout.putConstraint(SpringLayout.NORTH, progressBar, 20, SpringLayout.SOUTH, labelMessage);
		springLayout.putConstraint(SpringLayout.WEST, progressBar, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, progressBar, 60, SpringLayout.NORTH, labelMessage);
		springLayout.putConstraint(SpringLayout.EAST, progressBar, -10, SpringLayout.EAST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, labelMessage, 0, SpringLayout.EAST, progressBar);
		getContentPane().add(progressBar);
		
		labelNote = new JLabel("note");
		springLayout.putConstraint(SpringLayout.NORTH, labelNote, 15, SpringLayout.SOUTH, progressBar);
		springLayout.putConstraint(SpringLayout.WEST, labelNote, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, labelNote, -10, SpringLayout.EAST, getContentPane());
		labelNote.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(labelNote);
		
		panel = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, panel, -40, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, panel, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, panel, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, panel, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		horizontalGlue1 = Box.createHorizontalGlue();
		panel.add(horizontalGlue1);
		
		buttonCancel = new JButton("textCancel");
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		buttonCancel.setMinimumSize(new Dimension(80, 25));
		buttonCancel.setMaximumSize(new Dimension(80, 25));
		buttonCancel.setMargin(new Insets(0, 0, 0, 0));
		buttonCancel.setPreferredSize(new Dimension(80, 25));
		panel.add(buttonCancel);
		springLayout.putConstraint(SpringLayout.WEST, buttonCancel, 78, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, buttonCancel, -10, SpringLayout.SOUTH, getContentPane());
		
		horizontalGlue2 = Box.createHorizontalGlue();
		panel.add(horizontalGlue2);
		
		checkKill = new JCheckBox("org.multipage.gui.textKillOperation");
		checkKill.setHorizontalAlignment(SwingConstants.CENTER);
		springLayout.putConstraint(SpringLayout.WEST, checkKill, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, checkKill, -10, SpringLayout.NORTH, panel);
		springLayout.putConstraint(SpringLayout.EAST, checkKill, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(checkKill);
		
		scrollPane = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 10, SpringLayout.SOUTH, labelNote);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -10, SpringLayout.NORTH, checkKill);
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(scrollPane);
		
		list = new JList();
		list.setForeground(Color.RED);
		scrollPane.setViewportView(list);
	}
	
	/**
	 * On cancel.
	 */
	protected void onCancel() {
		try {
			
			// Cancel worker thread.
			if (swingWorker != null) {
				
				// If to kill the thread...
				if (checkKill.isSelected()) {
					// Ask user.
					if (JOptionPane.showConfirmDialog(this,
							Resources.getString("org.multipage.gui.messageShouldKillThread"))
							== JOptionPane.YES_OPTION) {
						
						// Cancel the thread.
						swingWorker.cancel(true);
					}
				}
				else {
					swingWorker.scheduleCancel();
				}
			}
			else {
				dispose();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Post creation.
	 * @param message 
	 */
	private void postCreation(String message) {
		try {
			
			scrollPane.setVisible(false);
			
			// Set message.
			labelMessage.setText(message);
			// Center dialog.
			Utility.centerOnScreen(this);
			// Localize.
			localize();
			// Set icons.
			setIcons();
			// Initialize progress.
			onProgressValue(0);
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
			
			Utility.localize(buttonCancel);
			Utility.localize(checkKill);
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
			
			setIconImage(Images.getImage("org/multipage/gui/images/progress.png"));
			buttonCancel.setIcon(Images.getIcon("org/multipage/gui/images/cancel_icon.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On progress value.
	 * @param progressValue
	 */
	protected void onProgressValue(int progressValue) {
		try {
			
			// Set progress bar.
			progressBar.setValue(progressValue);
			// Set note.
			labelNote.setText(String.format(
					Resources.getString("org.multipage.gui.textProgressNote"), progressValue));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Display the dialog with given input thread.
	 */
	public ProgressResult execute(SwingWorkerHelper<TOutput> swingWorker) {
		try {
			
			// Set worker thread.
			this.swingWorker = swingWorker;
			setVisible(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
		
		// Return result.
		return resultState;
	}

	/**
	 * On window opened.
	 */
	protected void onWindowOpened() {
		try {
			
			// Execute the worker thread.
			if (swingWorker != null) {
				
				// Set property change listener.
				this.swingWorker.addPropertyChangeListener(new PropertyChangeListener() {
					// On property change.
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						try {
							
							// If it is a "progress" property.
							if (evt.getPropertyName().equals("progress")) {
								
								// Get progress value.
								int progressValue = (Integer) evt.getNewValue();
								// Set progress bar.
								onProgressValue(progressValue);
							}
							// If it is "message" property.
							else if (evt.getPropertyName().equals("message")) {
								
								addMessage((String) evt.getNewValue());
							}
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
				
				final ProgressDialog thisDialog = this;
				
				// Listen for result to close the dialog.
				swingWorker.addResultChangeListener(new PropertyChangeListener() {
					// Result changes.
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						try {
							
							// Check property name.
							if (evt.getPropertyName().equals(SwingWorkerHelper.resultPropertyName)) {
								// Set result state.
								resultState = (ProgressResult) evt.getNewValue();
								// Close dialog.
								if (listModel == null) {
									dispose();
								}
								else {
									buttonCancel.setText(Resources.getString("textClose"));
									thisDialog.swingWorker = null;
								}
							}
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
				swingWorker.execute();
			}
			else {
				resultState = ProgressResult.NONE;
				return;
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get output.
	 * @return
	 */
	public TOutput getOutput() {
		
		try {
			if (swingWorker != null) {
				return swingWorker.getOutput();
			}
			else {
				return null;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Gets exception.
	 * @return
	 */
	public Exception getException() {
		
		try {
			if (swingWorker != null) {
				return swingWorker.getException();
			}
			else {
				return null;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Show messages.
	 */
	private void showMessages() {
		try {
			
			Rectangle bounds = getBounds();
			
			bounds.height = 400;
			setBounds(bounds);
			
			scrollPane.setVisible(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Add message.
	 */
	private void addMessage(String text) {
		try {
			
			if (listModel == null) {
				
				showMessages();
				
				listModel = new DefaultListModel<String>();
				list.setModel(listModel);
			}
			
			listModel.addElement(text);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
