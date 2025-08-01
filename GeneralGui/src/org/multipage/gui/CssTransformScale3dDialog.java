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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SpringLayout;

import org.multipage.util.Safe;

/**
 * Dialog that displays 3D scale editor.
 * @author vakol
 *
 */
public class CssTransformScale3dDialog extends JDialog {

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
	private TextFieldEx textSz;
	private JLabel labelSz;


	/**
	 * Show dialog.
	 * @param parent
	 * @return
	 */
	public static CssTransformScale3d showDialog(Component parent) {
		
		try {
			CssTransformScale3dDialog dialog = new CssTransformScale3dDialog(parent);
			dialog.setVisible(true);
			
			if (dialog.confirm) {
				
				return dialog.getScale();
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
	 * @param scale
	 * @return
	 */
	public static boolean editDialog(Component parent,
			CssTransformScale3d scale) {
		
		try {
			CssTransformScale3dDialog dialog = new CssTransformScale3dDialog(parent);
			dialog.setScale(scale);
			dialog.setVisible(true);
			
			if (dialog.confirm) {
				
				scale.setFrom(dialog.getScale());
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Set scale edit fields.
	 * @param scale
	 */
	private void setScale(CssTransformScale3d scale) {
		try {
			
			textSx.setText(String.valueOf(scale.sx));
			textSy.setText(String.valueOf(scale.sy));
			textSz.setText(String.valueOf(scale.sz));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get scale.
	 * @return
	 */
	private CssTransformScale3d getScale() {
		try {
			CssTransformScale3d scale = new CssTransformScale3d();
			
			scale.sx = Utility.getFloat(textSx, 0.0f);
			scale.sy = Utility.getFloat(textSy, 0.0f);
			scale.sz = Utility.getFloat(textSz, 0.0f);
	
			return scale;
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
	public CssTransformScale3dDialog(Component parent) {
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
		setTitle("org.multipage.gui.textCssTransformScale3dDialog");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});
		setBounds(100, 100, 281, 284);
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
		
		textSz = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textSz, 14, SpringLayout.SOUTH, textSy);
		springLayout.putConstraint(SpringLayout.EAST, textSz, 0, SpringLayout.EAST, textSx);
		textSz.setColumns(10);
		getContentPane().add(textSz);
		
		labelSz = new JLabel("sy = ");
		springLayout.putConstraint(SpringLayout.NORTH, labelSz, 3, SpringLayout.NORTH, textSz);
		springLayout.putConstraint(SpringLayout.EAST, labelSz, 0, SpringLayout.EAST, labelSx);
		getContentPane().add(labelSz);
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			
			localize();
			setIcons();
			
			loadDialog();
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
			
			textSx.setText("0.0");
			textSy.setText("0.0");
			textSz.setText("0.0");
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
