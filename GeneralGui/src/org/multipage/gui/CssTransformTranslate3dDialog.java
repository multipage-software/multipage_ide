/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SpringLayout;

import org.multipage.util.Safe;

/**
 * Dialog that displays 3D translation editor.
 * @author vakol
 *
 */
public class CssTransformTranslate3dDialog extends JDialog {

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
	private JLabel labelTx;
	private TextFieldEx textTx;
	private JLabel labelTy;
	private TextFieldEx textTy;
	private JComboBox<String> comboTxUnits;
	private JComboBox<String> comboTyUnits;
	private TextFieldEx textTz;
	private JComboBox comboTzUnits;
	private JLabel labelTz;

	/**
	 * Show dialog.
	 * @param parent
	 * @return
	 */
	public static CssTransformTranslate3d showDialog(Component parent) {
		
		try {
			CssTransformTranslate3dDialog dialog = new CssTransformTranslate3dDialog(parent);
			dialog.setVisible(true);
			
			if (dialog.confirm) {
				
				return dialog.getTranslate();
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Show edit dialog.
	 * @param parent
	 * @param translate
	 * @return
	 */
	public static boolean editDialog(Component parent,
			CssTransformTranslate3d translate) {
		
		try {
			CssTransformTranslate3dDialog dialog = new CssTransformTranslate3dDialog(parent);
			dialog.setTranslate(translate);
			dialog.setVisible(true);
			
			if (dialog.confirm) {
				
				translate.setFrom(dialog.getTranslate());
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Set translate edit fields.
	 * @param translate
	 */
	private void setTranslate(CssTransformTranslate3d translate) {
		try {
			
			textTx.setText(String.valueOf(translate.tx));
			textTy.setText(String.valueOf(translate.ty));
			textTz.setText(String.valueOf(translate.tz));
	
			comboTxUnits.setSelectedItem(translate.txUnits);
			comboTyUnits.setSelectedItem(translate.tyUnits);
			comboTzUnits.setSelectedItem(translate.tzUnits);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get translate.
	 * @return
	 */
	private CssTransformTranslate3d getTranslate() {
		
		try {
			CssTransformTranslate3d translate = new CssTransformTranslate3d();
			
			translate.tx = Utility.getFloat(textTx, 0.0f);
			translate.ty = Utility.getFloat(textTy, 0.0f);
			translate.tz = Utility.getFloat(textTz, 0.0f);
	
			translate.txUnits = (String) comboTxUnits.getSelectedItem();
			translate.tyUnits = (String) comboTyUnits.getSelectedItem();
			translate.tzUnits = (String) comboTzUnits.getSelectedItem();
	
			return translate;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Create the dialog.
	 * @param parent 
	 */
	public CssTransformTranslate3dDialog(Component parent) {
		super(Utility.findWindow(parent), ModalityType.APPLICATION_MODAL);
		
		try {
			initComponents();
			
			// $hide>>$
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
		setTitle("org.multipage.gui.textCssTransformTranslate3dDialog");
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
		
		labelTx = new JLabel("tx = ");
		springLayout.putConstraint(SpringLayout.NORTH, labelTx, 62, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, labelTx, 61, SpringLayout.WEST, getContentPane());
		getContentPane().add(labelTx);
		
		textTx = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textTx, -3, SpringLayout.NORTH, labelTx);
		springLayout.putConstraint(SpringLayout.WEST, textTx, 6, SpringLayout.EAST, labelTx);
		textTx.setColumns(10);
		getContentPane().add(textTx);
		
		labelTy = new JLabel("ty = ");
		springLayout.putConstraint(SpringLayout.EAST, labelTy, 0, SpringLayout.EAST, labelTx);
		getContentPane().add(labelTy);
		
		textTy = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, labelTy, 3, SpringLayout.NORTH, textTy);
		springLayout.putConstraint(SpringLayout.NORTH, textTy, 14, SpringLayout.SOUTH, textTx);
		springLayout.putConstraint(SpringLayout.WEST, textTy, 0, SpringLayout.WEST, textTx);
		textTy.setColumns(10);
		getContentPane().add(textTy);
		
		comboTxUnits = new JComboBox();
		springLayout.putConstraint(SpringLayout.WEST, comboTxUnits, 0, SpringLayout.EAST, textTx);
		comboTxUnits.setPreferredSize(new Dimension(50, 20));
		springLayout.putConstraint(SpringLayout.NORTH, comboTxUnits, 0, SpringLayout.NORTH, textTx);
		getContentPane().add(comboTxUnits);
		
		comboTyUnits = new JComboBox();
		springLayout.putConstraint(SpringLayout.NORTH, comboTyUnits, 0, SpringLayout.NORTH, textTy);
		springLayout.putConstraint(SpringLayout.WEST, comboTyUnits, 0, SpringLayout.EAST, textTy);
		comboTyUnits.setPreferredSize(new Dimension(50, 20));
		getContentPane().add(comboTyUnits);
		
		textTz = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textTz, 16, SpringLayout.SOUTH, textTy);
		springLayout.putConstraint(SpringLayout.WEST, textTz, 0, SpringLayout.WEST, textTx);
		textTz.setColumns(10);
		getContentPane().add(textTz);
		
		comboTzUnits = new JComboBox();
		springLayout.putConstraint(SpringLayout.NORTH, comboTzUnits, 0, SpringLayout.NORTH, textTz);
		springLayout.putConstraint(SpringLayout.WEST, comboTzUnits, 0, SpringLayout.WEST, comboTxUnits);
		comboTzUnits.setPreferredSize(new Dimension(50, 20));
		getContentPane().add(comboTzUnits);
		
		labelTz = new JLabel("tz = ");
		springLayout.putConstraint(SpringLayout.NORTH, labelTz, 3, SpringLayout.NORTH, textTz);
		springLayout.putConstraint(SpringLayout.EAST, labelTz, 0, SpringLayout.EAST, labelTx);
		getContentPane().add(labelTz);
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			
			localize();
			setIcons();
			
			loadUnits();
			loadDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load units.
	 */
	private void loadUnits() {
		try {
			
			Utility.loadCssUnits(comboTxUnits);
			Utility.loadCssUnits(comboTyUnits);
			Utility.loadCssUnits(comboTzUnits);
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
			
			buttonOk.setIcon(Images.getIcon("org/multipage/gui/images/ok_icon.png"));
			buttonCancel.setIcon(Images.getIcon("org/multipage/gui/images/cancel_icon.png"));
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
			
			Utility.localize(buttonOk);
			Utility.localize(buttonCancel);
			Utility.localize(this);
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
			
			saveDialog();
			confirm = false;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
		dispose();
	}

	/**
	 * On OK.
	 */
	protected void onOk() {
		try {
			
			saveDialog();
			confirm = true;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
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
		
		textTx.setText("0.0");
		textTy.setText("0.0");
		textTz.setText("0.0");
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
}
