/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package program.builder;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.multipage.gui.Images;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.TextFieldEx;
import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.Safe;

/**
 * Dialog that enables to enter constructor information.
 * @author vakol
 *
 */
public class AskConstructorHolder extends JDialog {

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
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JRadioButton radioNewName;
	private JRadioButton radioLink;
	private JTextField textName;
	private JLabel labelName;

	/**
	 * Show dialog.
	 * @param parent
	 * @param name 
	 * @param isLink 
	 * @param defaultName 
	 * @return
	 */
	public static boolean showDialog(Component parent, Obj<Boolean> isLink,
			Obj<String> name, String defaultName) {
		
		try {
			AskConstructorHolder dialog = new AskConstructorHolder(parent);
			
			dialog.textName.setText(defaultName);
			dialog.setVisible(true);
			
			if (dialog.confirm) {
				
				isLink.ref = dialog.radioLink.isSelected();
				if (!isLink.ref) {
					name.ref = dialog.textName.getText();
				}
				
				return true;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Create the dialog.
	 * @param parent 
	 */
	public AskConstructorHolder(Component parent) {
		super(Utility.findWindow(parent), ModalityType.APPLICATION_MODAL);
		
		try {
			setMinimumSize(new Dimension(450, 255));
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
		setTitle("builder.messageInsertConstructorName");
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
		setBounds(100, 100, 450, 255);
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
		
		radioNewName = new JRadioButton("builder.textNewConstructor");
		springLayout.putConstraint(SpringLayout.NORTH, radioNewName, 43, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, radioNewName, 78, SpringLayout.WEST, getContentPane());
		radioNewName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setComponentsState();
			}
		});
		radioNewName.setSelected(true);
		buttonGroup.add(radioNewName);
		getContentPane().add(radioNewName);
		
		textName = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.EAST, textName, -60, SpringLayout.EAST, getContentPane());
		getContentPane().add(textName);
		textName.setColumns(10);
		
		labelName = new JLabel("builder.textInsertNewConstructorName");
		springLayout.putConstraint(SpringLayout.NORTH, labelName, 5, SpringLayout.SOUTH, radioNewName);
		springLayout.putConstraint(SpringLayout.WEST, labelName, 70, SpringLayout.WEST, radioNewName);
		springLayout.putConstraint(SpringLayout.NORTH, textName, 1, SpringLayout.SOUTH, labelName);
		springLayout.putConstraint(SpringLayout.WEST, textName, 0, SpringLayout.WEST, labelName);
		getContentPane().add(labelName);
		
		radioLink = new JRadioButton("builder.textConstructorLink");
		springLayout.putConstraint(SpringLayout.NORTH, radioLink, 20, SpringLayout.SOUTH, textName);
		springLayout.putConstraint(SpringLayout.WEST, radioLink, 0, SpringLayout.WEST, radioNewName);
		getContentPane().add(radioLink);
		radioLink.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setComponentsState();
			}
		});
		buttonGroup.add(radioLink);
	}
	
	/**
	 * On window opened.
	 */
	protected void onWindowOpened() {
		try {
			
			textName.grabFocus();
			textName.selectAll();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set components state.
	 */
	protected void setComponentsState() {
		try {
			
			boolean isNewName = radioNewName.isSelected();
			labelName.setEnabled(isNewName);
			textName.setEnabled(isNewName);
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
			
			setComponentsState();
			
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
			
			Utility.localize(this);
			Utility.localize(buttonOk);
			Utility.localize(buttonCancel);
			Utility.localize(radioNewName);
			Utility.localize(radioLink);
			Utility.localize(labelName);
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
