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
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Closeable;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.maclan.Area;
import org.multipage.gui.ApplicationEvents;
import org.multipage.gui.GuiSignal;
import org.multipage.gui.Images;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.UpdatableComponent;
import org.multipage.gui.Utility;
import org.multipage.util.Safe;

/**
 * Dialog that displays selection of the sub area.
 * @author vakol
 *
 */
public class SelectSubAreaDialog extends JDialog implements Closeable, UpdatableComponent {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Bounds.
	 */
	private static Rectangle bounds;
	
	/**
	 * Set default data.
	 */
	public static void setDefaultData() {
		
		bounds = new Rectangle();
	}

	/**
	 * Load data.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void serializeData(StateInputStream inputStream)
			throws IOException, ClassNotFoundException {
		
		Object object = inputStream.readObject();
		if (!(object instanceof Rectangle)) {
			throw new ClassNotFoundException();
		}
		bounds = (Rectangle) object;
	}

	/**
	 * Save data.
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream)
			throws IOException {
		
		outputStream.writeObject(bounds);
	}

	/**
	 * Confirm flag.
	 */
	private boolean confirm = false;

	/**
	 * Root area reference.
	 */
	private Area rootArea;

	/**
	 * Areas tree panel.
	 */
	private AreasTreePanel treePanel;

	// $hide<<$
	/**
	 * Components.
	 */
	private JPanel panel;
	private JButton buttonOk;
	private JButton buttonCancel;

	/**
	 * Show dialog.
	 * @param parent
	 * @param selectedArea 
	 * @param resource
	 */
	public static Area showDialog(Component parent, Area rootArea, Area selectedArea) {
		
		try {
			// Update area.
			if (rootArea != null) {
				rootArea = ProgramGenerator.getArea(rootArea.getId());
			}
			
			SelectSubAreaDialog dialog = new SelectSubAreaDialog(Utility.findWindow(parent),
					rootArea);
			
			dialog.selectArea(selectedArea);
			dialog.setVisible(true);
			
			if (dialog.confirm) {
				return dialog.getSelectedArea();
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Create the dialog.
	 * @param parentWindow 
	 * @param rootArea 
	 */
	public SelectSubAreaDialog(Window parentWindow, Area rootArea) {
		super(parentWindow, ModalityType.DOCUMENT_MODAL);
		
		try {
			initComponents();
			// $hide>>$
			this.rootArea = rootArea;
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
			public void windowClosing(WindowEvent arg0) {
				onCancel();
			}
		});
		setTitle("org.multipage.generator.textSelectSubAreaDialog");
		
		setBounds(100, 100, 464, 357);
		
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(10, 35));
		getContentPane().add(panel, BorderLayout.SOUTH);
		SpringLayout sl_panel = new SpringLayout();
		panel.setLayout(sl_panel);
		
		buttonOk = new JButton("textOk");
		buttonOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});
		buttonOk.setMargin(new Insets(0, 0, 0, 0));
		buttonOk.setPreferredSize(new Dimension(80, 25));
		panel.add(buttonOk);
		
		buttonCancel = new JButton("textCancel");
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		sl_panel.putConstraint(SpringLayout.SOUTH, buttonCancel, -10, SpringLayout.SOUTH, panel);
		sl_panel.putConstraint(SpringLayout.EAST, buttonCancel, -10, SpringLayout.EAST, panel);
		sl_panel.putConstraint(SpringLayout.NORTH, buttonOk, 0, SpringLayout.NORTH, buttonCancel);
		sl_panel.putConstraint(SpringLayout.EAST, buttonOk, -6, SpringLayout.WEST, buttonCancel);
		buttonCancel.setPreferredSize(new Dimension(80, 25));
		buttonCancel.setMargin(new Insets(0, 0, 0, 0));
		panel.add(buttonCancel);
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			
			createPanels();
			
			localize();
			setIcons();
			setListeners();
			
			loadDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create panels.
	 */
	private void createPanels() {
		try {
			
			treePanel = new AreasTreePanel(rootArea);
			getContentPane().add(treePanel);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load dialog.
	 */
	private void loadDialog() {
		try {
			
			if (bounds.isEmpty()) {
				Utility.centerOnScreen(this);
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
	 * Localize components.
	 */
	private void localize() {
		try {
			
			Utility.localize(this);
			Utility.localize(buttonOk);
			Utility.localize(buttonCancel);
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
			
			buttonOk.setIcon(Images.getIcon("org/multipage/generator/images/ok_icon.png"));
			buttonCancel.setIcon(Images.getIcon("org/multipage/generator/images/cancel_icon.png"));
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
			
			// Receive show IDs event.
			ApplicationEvents.receiver(this, GuiSignal.showOrHideIds, message -> {
				// Update components.
				updateComponents();
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Save dialog.
	 */
	private void saveDialog() {
		try {
			
			bounds = getBounds();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On cancel.
	 */
	protected void onCancel() {
		try {
			confirm = false;
			saveDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
		try {
			close();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * On OK.
	 */
	protected void onOK() {
		try {
			confirm = true;
			saveDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
		try {
			close();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Get selected area.
	 * @return
	 */
	private Area getSelectedArea() {
		
		try {
			return treePanel.getSelectedArea();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Select area.
	 * @param area
	 */
	private void selectArea(Area area) {
		try {
			
			treePanel.selectArea(area);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Update components.
	 */
	@Override
	public void updateComponents() {
		try {
			
			// Update tree panel.
			treePanel.updateComponents();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Close.
	 */
	@Override
	public void close() throws IOException {
		try {
			
			// Remove receivers.
			ApplicationEvents.removeReceivers(this);
			// Dispose dialog.
			dispose();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		}
	}
}