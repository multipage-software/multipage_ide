/*
 * Copyright 2010-2017 (C) vakol
 * 
 * Created on : 26-04-2017
 *
 */

package org.multipage.gui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;

/**
 * 
 * @author
 *
 */
public class CssTransformScaleDialog extends JDialog {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Dialog serialized states.
	 */
	private static Rectangle bounds = new Rectangle();

	/**
	 * Set default states.
	 */
	public static void setDefaultData() {
		
		bounds = new Rectangle();
	}

	/**
	 * Save serialized states.
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream)
			throws IOException {
		
		outputStream.writeObject(bounds);
	}
	
	/**
	 * Load serialized states.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void serializeData(StateInputStream inputStream)
			throws IOException, ClassNotFoundException {
		
		bounds = Utility.readInputStreamObject(inputStream, Rectangle.class);
	}
	
	/**
	 * Confirm flag.
	 */
	private boolean confirm = false;
	
	// $hide<<$
	/**
	 * Components.
	 */
	private JButton buttonCancel;
	private JButton buttonOk;
	private JLabel labelSx;
	private TextFieldEx textSx;
	private JLabel labelSy;
	private TextFieldEx textSy;


	/**
	 * Show dialog.
	 * @param parent
	 * @return
	 */
	public static CssTransformScale showDialog(Component parent) {
		
		CssTransformScaleDialog dialog = new CssTransformScaleDialog(parent);
		dialog.setVisible(true);
		
		if (dialog.confirm) {
			
			return dialog.getScale();
		}
		return null;
	}

	/**
	 * Show edit dialog.
	 * @param parent
	 * @param scale
	 * @return
	 */
	public static boolean editDialog(Component parent,
			CssTransformScale scale) {
		
		CssTransformScaleDialog dialog = new CssTransformScaleDialog(parent);
		dialog.setScale(scale);
		dialog.setVisible(true);
		
		if (dialog.confirm) {
			
			scale.setFrom(dialog.getScale());
		}
		return false;
	}

	/**
	 * Set scale edit fields.
	 * @param scale
	 */
	private void setScale(CssTransformScale scale) {
		
		textSx.setText(String.valueOf(scale.sx));
		textSy.setText(String.valueOf(scale.sy));
	}

	/**
	 * Get scale.
	 * @return
	 */
	private CssTransformScale getScale() {
		
		CssTransformScale scale = new CssTransformScale();
		
		scale.sx = Utility.getFloat(textSx, 0.0f);
		scale.sy = Utility.getFloat(textSy, 0.0f);
		
		return scale;
	}

	/**
	 * Create the dialog.
	 * @param parent 
	 */
	public CssTransformScaleDialog(Component parent) {
		super(Utility.findWindow(parent), ModalityType.APPLICATION_MODAL);

		initComponents();
		
		// $hide>>$
		postCreate();
		// $hide<<$
	}

	/**
	 * Initialize components.
	 */
	private void initComponents() {
		setTitle("org.multipage.gui.textCssTransformScaleDialog");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});
		setBounds(100, 100, 320, 284);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
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
		buttonOk.setMargin(new Insets(0, 0, 0, 0));
		buttonOk.setPreferredSize(new Dimension(80, 25));
		springLayout.putConstraint(SpringLayout.SOUTH, buttonOk, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, buttonOk, -6, SpringLayout.WEST, buttonCancel);
		getContentPane().add(buttonOk);
		
		labelSx = new JLabel("sx = ");
		springLayout.putConstraint(SpringLayout.NORTH, labelSx, 62, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, labelSx, 61, SpringLayout.WEST, getContentPane());
		getContentPane().add(labelSx);
		
		textSx = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textSx, -3, SpringLayout.NORTH, labelSx);
		springLayout.putConstraint(SpringLayout.WEST, textSx, 6, SpringLayout.EAST, labelSx);
		textSx.setColumns(10);
		getContentPane().add(textSx);
		
		labelSy = new JLabel("sy = ");
		springLayout.putConstraint(SpringLayout.EAST, labelSy, 0, SpringLayout.EAST, labelSx);
		getContentPane().add(labelSy);
		
		textSy = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, labelSy, 3, SpringLayout.NORTH, textSy);
		springLayout.putConstraint(SpringLayout.NORTH, textSy, 14, SpringLayout.SOUTH, textSx);
		springLayout.putConstraint(SpringLayout.WEST, textSy, 0, SpringLayout.WEST, textSx);
		textSy.setColumns(10);
		getContentPane().add(textSy);
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		localize();
		setIcons();
		
		loadDialog();
	}

	/**
	 * Set icons.
	 */
	private void setIcons() {
		
		buttonOk.setIcon(Images.getIcon("org/multipage/gui/images/ok_icon.png"));
		buttonCancel.setIcon(Images.getIcon("org/multipage/gui/images/cancel_icon.png"));
	}

	/**
	 * Localize components.
	 */
	private void localize() {
		
		Utility.localize(buttonOk);
		Utility.localize(buttonCancel);
		Utility.localize(this);
	}
	
	/**
	 * On cancel.
	 */
	protected void onCancel() {
		
		saveDialog();
		
		confirm = false;
		dispose();
	}

	/**
	 * On OK.
	 */
	protected void onOk() {
		
		saveDialog();
		
		confirm = true;
		dispose();
	}

	/**
	 * Load dialog.
	 */
	private void loadDialog() {
		
		if (bounds.isEmpty()) {
			Utility.centerOnScreen(this);
		}
		else {
			setBounds(bounds);
		}
		
		textSx.setText("0.0");
		textSy.setText("0.0");
	}
	
	/**
	 * Save dialog.
	 */
	private void saveDialog() {
		
		bounds = getBounds();
	}
}
