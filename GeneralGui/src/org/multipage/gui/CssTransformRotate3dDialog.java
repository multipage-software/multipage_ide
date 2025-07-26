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
 * Dialog that displays 3D rotation editor. 
 * @author vakol
 *
 */
public class CssTransformRotate3dDialog extends JDialog {

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
	private JLabel labelA;
	private TextFieldEx textA;
	private JComboBox comboUnits;
	private TextFieldEx textX;
	private JLabel labelX;
	private TextFieldEx textY;
	private JLabel labelY;
	private TextFieldEx textZ;
	private JLabel labelZ;


	/**
	 * Show dialog.
	 * @param parent
	 * @return
	 */
	public static CssTransformRotate3d showDialog(Component parent) {
		
		try {
			CssTransformRotate3dDialog dialog = new CssTransformRotate3dDialog(parent);
			dialog.setVisible(true);
			
			if (dialog.confirm) {
				
				return dialog.getRotate();
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
	 * @param rotate
	 * @return
	 */
	public static boolean editDialog(Component parent,
			CssTransformRotate3d rotate) {
		
		try {
			CssTransformRotate3dDialog dialog = new CssTransformRotate3dDialog(parent);
			dialog.setRotate(rotate);
			dialog.setVisible(true);
			
			if (dialog.confirm) {
				
				rotate.setFrom(dialog.getRotate());
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Set rotate edit fields.
	 * @param rotate
	 */
	private void setRotate(CssTransformRotate3d rotate) {
		try {
			
			textX.setText(String.valueOf(rotate.x));
			textY.setText(String.valueOf(rotate.y));
			textZ.setText(String.valueOf(rotate.z));
			textA.setText(String.valueOf(rotate.a));
			comboUnits.setSelectedItem(rotate.aUnits);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get rotate.
	 * @return
	 */
	private CssTransformRotate3d getRotate() {
		
		try {
			CssTransformRotate3d rotate = new CssTransformRotate3d();
			
			rotate.x = Utility.getFloat(textX, 0.0f);
			rotate.y = Utility.getFloat(textY, 0.0f);
			rotate.z = Utility.getFloat(textZ, 0.0f);
			rotate.a = Utility.getFloat(textA, 0.0f);
			rotate.aUnits = (String) comboUnits.getSelectedItem();
	
			return rotate;
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
	public CssTransformRotate3dDialog(Component parent) {
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
		setTitle("org.multipage.gui.textCssTransformRotate3dDialog");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});
		setBounds(100, 100, 297, 318);
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
		
		labelA = new JLabel("a = ");
		getContentPane().add(labelA);
		
		textA = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, labelA, 3, SpringLayout.NORTH, textA);
		textA.setColumns(10);
		getContentPane().add(textA);
		
		comboUnits = new JComboBox();
		springLayout.putConstraint(SpringLayout.NORTH, comboUnits, -3, SpringLayout.NORTH, labelA);
		springLayout.putConstraint(SpringLayout.WEST, comboUnits, 0, SpringLayout.EAST, textA);
		comboUnits.setPreferredSize(new Dimension(50, 20));
		getContentPane().add(comboUnits);
		
		textX = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.EAST, textA, 0, SpringLayout.EAST, textX);
		springLayout.putConstraint(SpringLayout.NORTH, textX, 54, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, textX, 95, SpringLayout.WEST, getContentPane());
		textX.setColumns(10);
		getContentPane().add(textX);
		
		labelX = new JLabel("x = ");
		springLayout.putConstraint(SpringLayout.WEST, labelA, 0, SpringLayout.WEST, labelX);
		springLayout.putConstraint(SpringLayout.NORTH, labelX, 3, SpringLayout.NORTH, textX);
		springLayout.putConstraint(SpringLayout.EAST, labelX, -3, SpringLayout.WEST, textX);
		getContentPane().add(labelX);
		
		textY = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textY, 15, SpringLayout.SOUTH, textX);
		springLayout.putConstraint(SpringLayout.WEST, textY, 0, SpringLayout.WEST, textX);
		textY.setColumns(10);
		getContentPane().add(textY);
		
		labelY = new JLabel("y = ");
		springLayout.putConstraint(SpringLayout.NORTH, labelY, 3, SpringLayout.NORTH, textY);
		springLayout.putConstraint(SpringLayout.EAST, labelY, 0, SpringLayout.EAST, labelX);
		getContentPane().add(labelY);
		
		textZ = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textA, 15, SpringLayout.SOUTH, textZ);
		springLayout.putConstraint(SpringLayout.NORTH, textZ, 15, SpringLayout.SOUTH, textY);
		springLayout.putConstraint(SpringLayout.WEST, textZ, 0, SpringLayout.WEST, textX);
		textZ.setColumns(10);
		getContentPane().add(textZ);
		
		labelZ = new JLabel("z = ");
		springLayout.putConstraint(SpringLayout.NORTH, labelZ, 3, SpringLayout.NORTH, textZ);
		springLayout.putConstraint(SpringLayout.WEST, labelZ, 0, SpringLayout.WEST, labelX);
		getContentPane().add(labelZ);
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
			
			Utility.loadCssAngleUnits(comboUnits);
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
		try {
			
			if (bounds.isEmpty()) {
				Utility.centerOnScreen(this);
			}
			else {
				setBounds(bounds);
			}
			
			textX.setText("0.0");
			textY.setText("0.0");
			textZ.setText("0.0");
			textA.setText("0.0");
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
}
