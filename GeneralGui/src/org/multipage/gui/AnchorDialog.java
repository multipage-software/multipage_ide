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
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import org.multipage.util.Safe;

/**
 * Dialog that can edit URL anchors.
 * @author vakol
 *
 */
public class AnchorDialog extends JDialog {

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
	private JLabel labelText;
	private JTextField textAnchorText;
	private JLabel labelUrl;
	private TextFieldEx textUrl;

	/**
	 * Show dialog.
	 * @param parent
	 * @param text 
	 * @return
	 */
	public static String showDialog(Component parent, String text) {
		
		try {
			AnchorDialog dialog = new AnchorDialog(parent);
			dialog.textAnchorText.setText(text);
			
			dialog.setVisible(true);
			
			if (dialog.confirm) {
				
				return dialog.getAnchor();
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Compile anchor text and return it.
	 * @return
	 */
	private String getAnchor() {
		
		try {
			return String.format("<a href=\"%s\">%s</a>", textUrl.getText(), textAnchorText.getText());
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Create the dialog.
	 * @param parent 
	 */
	public AnchorDialog(Component parent) {
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
		setTitle("org.multipage.gui.textCompileWebAnchor");
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
		setBounds(100, 100, 361, 223);
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
		
		labelText = new JLabel("org.multipage.gui.textAnchorText");
		springLayout.putConstraint(SpringLayout.NORTH, labelText, 20, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, labelText, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(labelText);
		
		textAnchorText = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textAnchorText, 6, SpringLayout.SOUTH, labelText);
		springLayout.putConstraint(SpringLayout.WEST, textAnchorText, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, textAnchorText, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(textAnchorText);
		textAnchorText.setColumns(10);
		
		labelUrl = new JLabel("org.multipage.gui.textAnchorUrl");
		springLayout.putConstraint(SpringLayout.NORTH, labelUrl, 6, SpringLayout.SOUTH, textAnchorText);
		springLayout.putConstraint(SpringLayout.WEST, labelUrl, 0, SpringLayout.WEST, labelText);
		getContentPane().add(labelUrl);
		
		textUrl = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textUrl, 6, SpringLayout.SOUTH, labelUrl);
		springLayout.putConstraint(SpringLayout.WEST, textUrl, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, textUrl, -10, SpringLayout.EAST, getContentPane());
		textUrl.setColumns(10);
		getContentPane().add(textUrl);
	}

	/**
	 * On window opened.
	 */
	protected void onWindowOpened() {
		try {
			
			textUrl.requestFocus();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
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
			
			setDefaultButton();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set default button.
	 */
	private void setDefaultButton() {
		try {
			
			JRootPane rootPane = SwingUtilities.getRootPane(buttonOk); 
			rootPane.setDefaultButton(buttonOk);
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
			
			setIconImage(Images.getImage("org/multipage/generator/images/main_icon.png"));
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
			
			Utility.localize(this);
			Utility.localize(buttonOk);
			Utility.localize(buttonCancel);
			Utility.localize(labelText);
			Utility.localize(labelUrl);
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
