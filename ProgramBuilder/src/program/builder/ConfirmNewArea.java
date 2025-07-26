/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package program.builder;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

import org.eclipse.wb.swing.FocusTraversalOnArray;
import org.maclan.Area;
import org.multipage.gui.Images;
import org.multipage.gui.TextFieldEx;
import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Dialog that enables to input new area properties.
 * @author vakol
 *
 */
public class ConfirmNewArea extends JDialog {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Confirmation flag.
	 */
	private boolean confirmed = false;

	/**
	 * Area reference.
	 */
	private Area area;

	/**
	 * Inheritance.
	 */
	private Obj<Boolean> inheritance;
	
	// $hide<<$
	/**
	 * Components.
	 */
	private final JPanel contentPanel = new JPanel();
	private JButton okButton;
	private JButton cancelButton;
	private JLabel labelDescription;
	private JTextField textDescription;
	private JCheckBox checkBoxInherit;
	private JPanel buttonPane;
	private JLabel labelAlias;
	private JTextField textAlias;
	private JCheckBox checkBoxVisible;
	private JLabel labelRelationNameSub;
	private JTextField textRelationNameSub;
	private JLabel labelRelationNameSuper;
	private JTextField textRelationNameSuper;
	private JCheckBox checkReadOnly;
	private JCheckBox checkLocalized;
	private JLabel labelFileName;
	private JTextField textFileName;
	private JLabel labelFolder;
	private JTextField textFolder;
	private JCheckBox checkCanImport;
	private JCheckBox checkProjectRoot;
	private SpringLayout sl_buttonPane;

	/**
	 * Launch the dialog.
	 * @param relationNameSub 
	 */
	public static boolean showConfirmDialog(Component parent, Area area,
			Obj<Boolean> inheritance, Obj<String> relationNameSub,
			Obj<String> relationNameSuper) {
		
		try {
			ConfirmNewArea dialog = new ConfirmNewArea(parent);
			dialog.area = area;
			dialog.inheritance = inheritance;
			dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			dialog.setVisible(true);
			if (dialog.confirmed) {
				relationNameSub.ref = dialog.textRelationNameSub.getText();
				relationNameSuper.ref = dialog.textRelationNameSuper.getText();
			}
			
			return dialog.confirmed;
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
	public ConfirmNewArea(Component parent) {
		super(Utility.findWindow(parent), ModalityType.APPLICATION_MODAL);
		
		try {
			setResizable(false);
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					onCancel();
				}
			});
			setTitle("builder.textInsertAreaDescription");
	
			initComponents();
			
			// $hide>>$
			// Set icon
			setIconImage(Images.getImage("org/multipage/generator/images/main_icon.png"));
			
			// Localize dialog.
			localize();
			
			// Center the dialog.
			Utility.centerOnScreen(this);
			
			postCreate();
			// $hide<<$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Last creation step.
	 */
	private void postCreate() {
		try {
			
			// Initialize text field.
			textDescription.setText(Resources.getString("org.multipage.generator.textNewArea"));
			textDescription.selectAll();
			
			// Set icons.
			okButton.setIcon(Images.getIcon("org/multipage/generator/images/ok_icon.png"));
			cancelButton.setIcon(Images.getIcon("org/multipage/generator/images/cancel_icon.png"));
			
			// Set file name components.
			setFileNameComponents();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set file name components.
	 */
	private void setFileNameComponents() {
		try {
			
			boolean enabled = checkBoxVisible.isSelected();
			
			labelFileName.setEnabled(enabled);
			textFileName.setEnabled(enabled);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Initialize components.
	 */
	private void initComponents() {
		setBounds(100, 100, 482, 464);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		SpringLayout sl_contentPanel = new SpringLayout();
		contentPanel.setLayout(sl_contentPanel);
		
		labelDescription = new JLabel("builder.textInsertNewAreaDescriptionLabel");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, labelDescription, 11, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, labelDescription, 10, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, labelDescription, 360, SpringLayout.WEST, contentPanel);
		contentPanel.add(labelDescription);
		
		textDescription = new TextFieldEx();
		sl_contentPanel.putConstraint(SpringLayout.NORTH, textDescription, 36, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, textDescription, 10, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, textDescription, -10, SpringLayout.EAST, contentPanel);
		contentPanel.add(textDescription);
		textDescription.setColumns(10);
		{
			checkBoxInherit = new JCheckBox("org.multipage.generator.textInheritFromSuperArea");
			sl_contentPanel.putConstraint(SpringLayout.NORTH, checkBoxInherit, 134, SpringLayout.NORTH, contentPanel);
			sl_contentPanel.putConstraint(SpringLayout.WEST, checkBoxInherit, 10, SpringLayout.WEST, contentPanel);
			sl_contentPanel.putConstraint(SpringLayout.EAST, checkBoxInherit, 182, SpringLayout.WEST, contentPanel);
			checkBoxInherit.setSelected(true);
			contentPanel.add(checkBoxInherit);
		}
		
		labelAlias = new JLabel("builder.textNewAreaAlias");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, labelAlias, 67, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, labelAlias, 10, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, labelAlias, 381, SpringLayout.WEST, contentPanel);
		contentPanel.add(labelAlias);
		
		textAlias = new JTextField();
		sl_contentPanel.putConstraint(SpringLayout.NORTH, textAlias, 92, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, textAlias, 10, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, textAlias, -10, SpringLayout.EAST, contentPanel);
		contentPanel.add(textAlias);
		textAlias.setColumns(10);
		
		checkBoxVisible = new JCheckBox("builder.textNewAreaVisible");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, checkBoxVisible, 134, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, checkBoxVisible, 184, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, checkBoxVisible, 280, SpringLayout.WEST, contentPanel);
		checkBoxVisible.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onChangeAreaVisible();
			}
		});
		checkBoxVisible.setSelected(true);
		contentPanel.add(checkBoxVisible);
		
		labelRelationNameSub = new JLabel("org.multipage.generator.textRelationNameSub");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, labelRelationNameSub, 196, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, labelRelationNameSub, 10, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, labelRelationNameSub, 381, SpringLayout.WEST, contentPanel);
		contentPanel.add(labelRelationNameSub);
		
		textRelationNameSub = new JTextField();
		sl_contentPanel.putConstraint(SpringLayout.NORTH, textRelationNameSub, 210, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, textRelationNameSub, 10, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, textRelationNameSub, -10, SpringLayout.EAST, contentPanel);
		contentPanel.add(textRelationNameSub);
		textRelationNameSub.setColumns(10);
		
		labelRelationNameSuper = new JLabel("org.multipage.generator.textRelationNameSuper");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, labelRelationNameSuper, 241, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, labelRelationNameSuper, 10, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, labelRelationNameSuper, 381, SpringLayout.WEST, contentPanel);
		contentPanel.add(labelRelationNameSuper);
		
		textRelationNameSuper = new JTextField();
		sl_contentPanel.putConstraint(SpringLayout.NORTH, textRelationNameSuper, 256, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, textRelationNameSuper, 10, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, textRelationNameSuper, -10, SpringLayout.EAST, contentPanel);
		textRelationNameSuper.setColumns(10);
		contentPanel.add(textRelationNameSuper);
		
		checkReadOnly = new JCheckBox("org.multipage.generator.textReadOnly");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, checkReadOnly, 134, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, checkReadOnly, 280, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, checkReadOnly, 360, SpringLayout.WEST, contentPanel);
		contentPanel.add(checkReadOnly);
		
		checkLocalized = new JCheckBox("org.multipage.generator.textLocalized");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, checkLocalized, 134, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, checkLocalized, 370, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, checkLocalized, 454, SpringLayout.WEST, contentPanel);
		checkLocalized.setSelected(true);
		contentPanel.add(checkLocalized);
		
		labelFileName = new JLabel("org.multipage.generator.textAreaFileName");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, labelFileName, 287, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, labelFileName, 10, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, labelFileName, 454, SpringLayout.WEST, contentPanel);
		contentPanel.add(labelFileName);
		
		textFileName = new JTextField();
		sl_contentPanel.putConstraint(SpringLayout.NORTH, textFileName, 302, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, textFileName, 10, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, textFileName, -10, SpringLayout.EAST, contentPanel);
		textFileName.setColumns(10);
		contentPanel.add(textFileName);
		
		labelFolder = new JLabel("org.multipage.generator.textAreasFolder");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, labelFolder, 333, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, labelFolder, 10, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, labelFolder, 454, SpringLayout.WEST, contentPanel);
		contentPanel.add(labelFolder);
		
		textFolder = new JTextField();
		sl_contentPanel.putConstraint(SpringLayout.NORTH, textFolder, 348, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, textFolder, 10, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, textFolder, -10, SpringLayout.EAST, contentPanel);
		textFolder.setColumns(10);
		contentPanel.add(textFolder);
		
		checkCanImport = new JCheckBox("builder.textCanImport");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, checkCanImport, 160, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, checkCanImport, 184, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, checkCanImport, 280, SpringLayout.WEST, contentPanel);
		contentPanel.add(checkCanImport);
		
		checkProjectRoot = new JCheckBox("builder.textProjectRoot");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, checkProjectRoot, 160, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, checkProjectRoot, 280, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, checkProjectRoot, 400, SpringLayout.WEST, contentPanel);
		contentPanel.add(checkProjectRoot);
		contentPanel.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{textDescription, checkBoxInherit, labelDescription}));
		{
			buttonPane = new JPanel();
			buttonPane.setPreferredSize(new Dimension(10, 40));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("textOk");
				okButton.setMargin(new Insets(2, 4, 2, 4));
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onOk();
					}
				});
				sl_buttonPane = new SpringLayout();
				sl_buttonPane.putConstraint(SpringLayout.SOUTH, okButton, -10, SpringLayout.SOUTH, buttonPane);
				buttonPane.setLayout(sl_buttonPane);
				okButton.setPreferredSize(new Dimension(80, 25));
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton = new JButton("textCancel");
				sl_buttonPane.putConstraint(SpringLayout.SOUTH, cancelButton, -10, SpringLayout.SOUTH, buttonPane);
				sl_buttonPane.putConstraint(SpringLayout.EAST, okButton, -10, SpringLayout.WEST, cancelButton);
				sl_buttonPane.putConstraint(SpringLayout.EAST, cancelButton, -10, SpringLayout.EAST, buttonPane);
				cancelButton.setMargin(new Insets(2, 4, 2, 4));
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onCancel();
					}
				});
				cancelButton.setPreferredSize(new Dimension(80, 25));
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{textDescription, textAlias, checkBoxInherit, checkBoxVisible, okButton, cancelButton}));
	}
	
	/**
	 * On change visible area check box.
	 */
	protected void onChangeAreaVisible() {
		try {
			
			setFileNameComponents();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On OK.
	 */
	protected void onOk() {
		try {
			
			confirmed = true;
			
			// Set output.
			String text = textDescription.getText();
			area.setDescription(text);
			area.setAlias(textAlias.getText());
			area.setVisible(checkBoxVisible.isSelected());
			inheritance.ref = checkBoxInherit.isSelected();
			area.setReadOnly(checkReadOnly.isSelected());
			area.setLocalized(checkLocalized.isSelected());
			area.setFileName(textFileName.getText());
			area.setFolder(textFolder.getText());
			area.setCanImport(checkCanImport.isSelected());
			area.setProjectRoot(checkProjectRoot.isSelected());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
		
		dispose();
	}

	protected void onCancel() {

		confirmed = false;
		dispose();
	}

	/**
	 * Localize dialog.
	 */
	private void localize() {
		try {
			
			Utility.localize(this);
			Utility.localize(okButton);
			Utility.localize(cancelButton);
			Utility.localize(labelDescription);
			Utility.localize(checkBoxInherit);
			Utility.localize(labelAlias);
			Utility.localize(checkBoxVisible);
			Utility.localize(labelRelationNameSub);
			Utility.localize(labelRelationNameSuper);
			Utility.localize(checkReadOnly);
			Utility.localize(checkLocalized);
			Utility.localize(labelFileName);
			Utility.localize(labelFolder);
			Utility.localize(checkCanImport);
			Utility.localize(checkProjectRoot);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
