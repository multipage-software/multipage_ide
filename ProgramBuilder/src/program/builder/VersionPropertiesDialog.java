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
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import org.eclipse.wb.swing.FocusTraversalOnArray;
import org.maclan.MiddleResult;
import org.maclan.VersionObj;
import org.multipage.gui.Images;
import org.multipage.gui.TextFieldEx;
import org.multipage.gui.Utility;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Dialog that displays version editor.
 * @author vakol
 *
 */
public class VersionPropertiesDialog extends JDialog {
	
	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Confirm flag.
	 */
	private boolean confirm = false;
	
	/**
	 * Is new version flag.
	 */
	private boolean isNew = true;
	
	/**
	 * Version object reference.
	 */
	private VersionObj version;
	
	// $hide<<$
	/**
	 * Components.
	 */
	private JButton buttonOk;
	private JButton buttonCancel;
	private JLabel labelId;
	private JTextField textId;
	private JLabel labelDescription;
	private JTextField textDescription;
	private JLabel labelAlias;
	private JTextField textAlias;
	private JLabel labelImage;

	/**
	 * Show dialog.
	 * @param parent
	 * @param version 
	 * @param resource
	 */
	public static boolean showNewDialog(Component parent, VersionObj version) {
		
		try {
			VersionPropertiesDialog dialog = new VersionPropertiesDialog(Utility.findWindow(parent),
					version, true);
			
			dialog.setVisible(true);
			
			if (dialog.confirm) {
				version.setAlias(dialog.textAlias.getText());
				version.setDescription(dialog.textDescription.getText());
			}
			
			return dialog.confirm;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * SHow edit dialog.
	 * @param parent
	 * @param version
	 * @return
	 */
	public static boolean showEditDialog(Component parent, VersionObj version) {
		
		try {
			VersionPropertiesDialog dialog = new VersionPropertiesDialog(Utility.findWindow(parent),
					version, false);
			
			dialog.setVisible(true);
			
			if (dialog.confirm) {
				version.setAlias(dialog.textAlias.getText());
				version.setDescription(dialog.textDescription.getText());
			}
			
			return dialog.confirm;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Create the dialog.
	 * @param parentWindow 
	 * @param isNew 
	 * @param version 
	 */
	public VersionPropertiesDialog(Window parentWindow, VersionObj version, boolean isNew) {
		super(parentWindow, ModalityType.DOCUMENT_MODAL);
		
		try {
			initComponents();
			
			// $hide>>$
			this.version = version;
			this.isNew = isNew;
			
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
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("org.multipage.generator.textResourceDataProperties");
		
		setBounds(100, 100, 356, 226);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		labelId = new JLabel("builder.textVersionId2");
		springLayout.putConstraint(SpringLayout.NORTH, labelId, 15, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, labelId, 92, SpringLayout.WEST, getContentPane());
		getContentPane().add(labelId);
		
		textId = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textId, 12, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, textId, 82, SpringLayout.EAST, labelId);
		springLayout.putConstraint(SpringLayout.WEST, textId, 6, SpringLayout.EAST, labelId);
		textId.setHorizontalAlignment(SwingConstants.CENTER);
		textId.setEditable(false);
		getContentPane().add(textId);
		textId.setColumns(10);
		
		labelDescription = new JLabel("builder.textVersionDescription");
		springLayout.putConstraint(SpringLayout.NORTH, labelDescription, 90, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, labelDescription, 0, SpringLayout.WEST, labelId);
		getContentPane().add(labelDescription);
		
		textDescription = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textDescription, 6, SpringLayout.SOUTH, labelDescription);
		springLayout.putConstraint(SpringLayout.WEST, textDescription, 0, SpringLayout.WEST, labelId);
		springLayout.putConstraint(SpringLayout.EAST, textDescription, -24, SpringLayout.EAST, getContentPane());
		getContentPane().add(textDescription);
		textDescription.setColumns(10);
		
		labelAlias = new JLabel("builder.textVersionAlias2");
		springLayout.putConstraint(SpringLayout.WEST, labelAlias, 0, SpringLayout.WEST, labelId);
		getContentPane().add(labelAlias);
		
		textAlias = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.SOUTH, labelAlias, -6, SpringLayout.NORTH, textAlias);
		springLayout.putConstraint(SpringLayout.NORTH, textAlias, 64, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, textAlias, 0, SpringLayout.WEST, labelId);
		springLayout.putConstraint(SpringLayout.EAST, textAlias, -24, SpringLayout.EAST, getContentPane());
		textAlias.setPreferredSize(new Dimension(300, 20));
		getContentPane().add(textAlias);
		textAlias.setColumns(10);
		
		labelImage = new JLabel("");
		springLayout.putConstraint(SpringLayout.NORTH, labelImage, 43, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, labelImage, 20, SpringLayout.WEST, getContentPane());
		labelImage.setPreferredSize(new Dimension(50, 50));
		getContentPane().add(labelImage);
		
		buttonOk = new JButton("textOk");
		getContentPane().add(buttonOk);
		buttonOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onOk();
			}
		});
		buttonOk.setMargin(new Insets(0, 0, 0, 0));
		buttonOk.setPreferredSize(new Dimension(80, 25));
		
		buttonCancel = new JButton("textCancel");
		springLayout.putConstraint(SpringLayout.SOUTH, buttonCancel, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, buttonOk, 0, SpringLayout.NORTH, buttonCancel);
		springLayout.putConstraint(SpringLayout.EAST, buttonOk, -6, SpringLayout.WEST, buttonCancel);
		springLayout.putConstraint(SpringLayout.EAST, buttonCancel, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(buttonCancel);
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onCancel();
			}
		});
		buttonCancel.setPreferredSize(new Dimension(80, 25));
		buttonCancel.setMargin(new Insets(0, 0, 0, 0));
		setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{textAlias, textDescription, buttonCancel, buttonOk, textId, getContentPane(), labelId, labelDescription, labelAlias, labelImage}));
	}
	
	/**
	 * On OK.
	 */
	protected void onOk() {
		try {
			
			// Check version alias.
			MiddleResult result = VersionObj.checkAlias(textAlias.getText());
			
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			confirm = true;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
		
		dispose();
	}

	/**
	 * On cancel.
	 */
	protected void onCancel() {
		
		dispose();
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			Utility.centerOnScreen(this);
			
			// Set dialog title.
			setTitle(isNew ? "builder.textNewVersion" : "builder.textEditVersion");
			
			localize();
			setIcons();
			
			loadDialog();
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
			Utility.localize(labelId);
			Utility.localize(labelDescription);
			Utility.localize(labelAlias);
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
			labelImage.setIcon(Images.getIcon("org/multipage/generator/images/version_big.png"));
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
			
			textId.setText(isNew ? Resources.getString("org.multipage.generator.textUnknown") : String.valueOf(version.getId()));
			
			if (!isNew) {
				textDescription.setText(version.getDescription());
				textAlias.setText(version.getAlias());
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}