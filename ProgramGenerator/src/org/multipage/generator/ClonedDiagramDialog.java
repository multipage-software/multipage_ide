/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */
package org.multipage.generator;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Window;
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
import javax.swing.SpringLayout;

import org.multipage.gui.Images;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.TextFieldEx;
import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.Safe;

/**
 * Cloned diagram dialog window.
 * @author vakol
 *
 */
public class ClonedDiagramDialog extends JDialog {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Serialized window type.
	 */
	private static TabType windowTypeState;

	/**
	 * Set default states.
	 */
	public static void setDefaultData() {
		
		windowTypeState = TabType.areasTree;
	}

	/**
	 * Save serialized states.
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream)
			throws IOException {
		
		outputStream.writeObject(windowTypeState);
	}
	
	/**
	 * Load serialized states.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void serializeData(StateInputStream inputStream)
			throws IOException, ClassNotFoundException {
		
		windowTypeState = Utility.readInputStreamObject(inputStream, TabType.class);
	}
	
	/**
	 * Dialog confirmed flag.
	 */
	private boolean confirmed = false;
	
	/**
	 * Controls.
	 */
	private JLabel labelName;
	private TextFieldEx textCaption;
	private JRadioButton radioDiagram;
	private JRadioButton radioAreaTree;
	private JButton buttonCancel;
	private JButton buttonOk;
	private final ButtonGroup buttonGroup = new ButtonGroup();

	/**
	 * Show dialog.
	 * @param parent
	 * @param caption
	 * @return
	 */
	public static String showDialog(Component parent, String caption, Obj<TabType> type) {
		
		try {
			ClonedDiagramDialog dialog = new ClonedDiagramDialog(Utility.findWindow(parent));
			dialog.setProperties(caption);
			dialog.setVisible(true);
			
			if (dialog.confirmed) {
				type.ref = dialog.getCloneType();
				String captionResult = dialog.getCaption();
				return captionResult;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Create the dialog.
	 * @param window 
	 */
	public ClonedDiagramDialog(Window window) {
		super(window, ModalityType.DOCUMENT_MODAL);
		
		try {
			// Initialize components.
			initComponents();
			// Post creation.
			postCreate(); //$hide$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Initialize components.
	 */
	private void initComponents() {
		setResizable(false);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setTitle("org.multipage.generator.messageGetAreasEditorCloneName");
		setBounds(100, 100, 309, 224);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		labelName = new JLabel("org.multipage.generator.textNewTabCaption");
		springLayout.putConstraint(SpringLayout.NORTH, labelName, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, labelName, 16, SpringLayout.WEST, getContentPane());
		getContentPane().add(labelName);
		
		textCaption = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textCaption, 6, SpringLayout.SOUTH, labelName);
		springLayout.putConstraint(SpringLayout.WEST, textCaption, 0, SpringLayout.WEST, labelName);
		springLayout.putConstraint(SpringLayout.EAST, textCaption, -16, SpringLayout.EAST, getContentPane());
		getContentPane().add(textCaption);
		textCaption.setColumns(10);
		
		radioDiagram = new JRadioButton("org.multipage.generator.textCloneDiagram");
		buttonGroup.add(radioDiagram);
		springLayout.putConstraint(SpringLayout.NORTH, radioDiagram, 16, SpringLayout.SOUTH, textCaption);
		springLayout.putConstraint(SpringLayout.WEST, radioDiagram, 70, SpringLayout.WEST, getContentPane());
		getContentPane().add(radioDiagram);
		
		radioAreaTree = new JRadioButton("org.multipage.generator.textCloneTree");
		buttonGroup.add(radioAreaTree);
		springLayout.putConstraint(SpringLayout.NORTH, radioAreaTree, 6, SpringLayout.SOUTH, radioDiagram);
		springLayout.putConstraint(SpringLayout.WEST, radioAreaTree, 0, SpringLayout.WEST, radioDiagram);
		getContentPane().add(radioAreaTree);
		
		buttonCancel = new JButton("textCancel");
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		springLayout.putConstraint(SpringLayout.SOUTH, buttonCancel, -16, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, buttonCancel, -16, SpringLayout.EAST, getContentPane());
		buttonCancel.setMargin(new Insets(0, 0, 0, 0));
		buttonCancel.setPreferredSize(new Dimension(80, 25));
		getContentPane().add(buttonCancel);
		
		buttonOk = new JButton("textOk");
		buttonOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, buttonOk, 0, SpringLayout.NORTH, buttonCancel);
		springLayout.putConstraint(SpringLayout.EAST, buttonOk, -10, SpringLayout.WEST, buttonCancel);
		buttonOk.setPreferredSize(new Dimension(80, 25));
		buttonOk.setMargin(new Insets(0, 0, 0, 0));
		getContentPane().add(buttonOk);
	}
	
	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			localize();
			setIcons();
			initSelection();
			
			Utility.centerOnScreen(this);
			
			loadDialog();
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
			
			switch (windowTypeState) {
			case areasDiagram:
				radioDiagram.setSelected(true);
				break;
				
			case areasTree:
			default:
				radioAreaTree.setSelected(true);
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
			
			windowTypeState = getCloneType();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Initialize selection.
	 */
	private void initSelection() {
		try {
			
			radioDiagram.setSelected(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set dialog properties.
	 * @param caption
	 */
	private void setProperties(String caption) {
		try {
			
			textCaption.setText(caption);
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
			Utility.localize(labelName);
			Utility.localize(radioDiagram);
			Utility.localize(radioAreaTree);
			Utility.localize(buttonCancel);
			Utility.localize(buttonOk);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
		
	/**
	 * Set components icons.
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
	 * Get clone type.
	 * @return
	 */
	private TabType getCloneType() {
		
		try {
			if (radioDiagram.isSelected()) {
				return TabType.areasDiagram;
			}
			else if (radioAreaTree.isSelected()) {
				return TabType.areasTree;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return TabType.unknown;
	}
	
	/**
	 * Get caption.
	 * @return
	 */
	private String getCaption() {
		
		try {
			String caption = textCaption.getText();
			return caption;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * On OK button.
	 */
	protected void onOK() {
		try {
			
			confirmed = true;
			saveDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
		
		dispose();
	}
	
	/**
	 * On cancel button.
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
}
