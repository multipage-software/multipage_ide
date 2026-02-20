/*
 * Copyright 2010-2026 (C) vakol
 * 
 * Created on : 23-01-2026
 */
package org.multipage.generator;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JCheckBox;

import org.maclan.Language;
import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.multipage.basic.ProgramBasic;
import org.multipage.gui.Images;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.Utility;
import org.multipage.util.Resources;
import org.multipage.util.Safe;
import org.multipage.basic.LanguagesComboBox;

/**
 * Class for the readable language export dialog. Use the showDialog(...) method to make this dialog visible.
 * @author user
 *
 */
public class ExportLegiblyDialog extends JDialog {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Dialog window boundaries.
	 */
	private static Rectangle bounds = null;
	
	/**
	 * Root area ID.
	 */
	private long rootAreaId = 0L;
	
	/**
	 * Dialog controls.
	 */
	private JButton buttonOk;
	private JButton buttonCancel;
	private JLabel labelFolderTrust;
	private JTextField textFolderTrust;
	private JTextArea textExport;
	private JLabel labelLanguages;
	private LanguagesComboBox comboLanguages;
	private JCheckBox checkWordWrap;
	
	//$hide>>$
	/**
	 * Dialog object fields.
	 */

	//$hide<<$
	
	/**
	 * Set default states.
	 */
	public static void setDefaultData() {
		
		bounds = null;
	}
	
	/**
	 * Read states.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void serializeData(StateInputStream inputStream)
			throws IOException, ClassNotFoundException {
		
		bounds = Utility.readInputStreamObject(inputStream, Rectangle.class);
	}

	/**
	 * Write states.
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream)
			throws IOException {
		
		outputStream.writeObject(bounds);
	}
	
	/**
	 * Show the dialog window.
	 * @param parent
	 */
	public static void showDialog(Component parent, long areaId) {
		
		// Create a new frame object and make it visible.
		ExportLegiblyDialog dialog = new ExportLegiblyDialog(parent, areaId);
		dialog.setVisible(true);
	}
	
	/**
	 * Create the dialog.
	 * @param areaId 
	 */
	public ExportLegiblyDialog(Component parent, long areaId) {
		super(Utility.findWindow(parent), ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		initComponents();
		postCreate(areaId); //$hide$
	}

	/**
	 * Initialize dialog components.
	 */
	private void initComponents() {

		setBounds(100, 100, 579, 597);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});

		setTitle("org.multipage.generator.titleExportLegiblyDialog");
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
		springLayout.putConstraint(SpringLayout.NORTH, buttonOk, 0, SpringLayout.NORTH, buttonCancel);
		springLayout.putConstraint(SpringLayout.EAST, buttonOk, -6, SpringLayout.WEST, buttonCancel);
		buttonOk.setPreferredSize(new Dimension(80, 25));
		buttonOk.setMargin(new Insets(0, 0, 0, 0));
		getContentPane().add(buttonOk);
		
		labelFolderTrust = new JLabel("org.multipage.generator.textConfirmFolderTrust");
		springLayout.putConstraint(SpringLayout.NORTH, labelFolderTrust, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, labelFolderTrust, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(labelFolderTrust);
		
		textFolderTrust = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, textFolderTrust, 6, SpringLayout.SOUTH, labelFolderTrust);
		springLayout.putConstraint(SpringLayout.WEST, textFolderTrust, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, textFolderTrust, -38, SpringLayout.EAST, getContentPane());
		getContentPane().add(textFolderTrust);
		textFolderTrust.setColumns(10);
		
		JButton buttonFindFolder = new JButton("");
		springLayout.putConstraint(SpringLayout.NORTH, buttonFindFolder, -2, SpringLayout.NORTH, textFolderTrust);
		springLayout.putConstraint(SpringLayout.WEST, buttonFindFolder, 6, SpringLayout.EAST, textFolderTrust);
		buttonFindFolder.setPreferredSize(new Dimension(24, 24));
		buttonFindFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onFindFolder();
			}
		});
		getContentPane().add(buttonFindFolder);
		
		JScrollPane scrollExport = new JScrollPane();
		springLayout.putConstraint(SpringLayout.WEST, scrollExport, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, scrollExport, -10, SpringLayout.NORTH, buttonOk);
		springLayout.putConstraint(SpringLayout.EAST, scrollExport, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(scrollExport);
		
		textExport = new JTextArea();
		textExport.setFont(new Font("Monospaced", Font.PLAIN, 12));
		// Enable word wrap and wrap by words so long lines are wrapped inside the text area.
		textExport.setLineWrap(true);
		textExport.setWrapStyleWord(true);
		// Disable horizontal scrollbar since lines are wrapped.
		scrollExport.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollExport.setViewportView(textExport);
		
		labelLanguages = new JLabel("org.multipage.translator.textLanguages");
		springLayout.putConstraint(SpringLayout.NORTH, labelLanguages, 11, SpringLayout.SOUTH, buttonFindFolder);
		springLayout.putConstraint(SpringLayout.WEST, labelLanguages, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(labelLanguages);
		
		comboLanguages = new LanguagesComboBox();
		springLayout.putConstraint(SpringLayout.NORTH, scrollExport, 10, SpringLayout.SOUTH, comboLanguages);
		comboLanguages.setPreferredSize(new Dimension(30, 80));
		springLayout.putConstraint(SpringLayout.NORTH, comboLanguages, 31, SpringLayout.SOUTH, buttonFindFolder);
		springLayout.putConstraint(SpringLayout.WEST, comboLanguages, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, comboLanguages, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(comboLanguages);
		
		// Word wrap checkbox
		checkWordWrap = new JCheckBox("org.multipage.generator.textWordWrap");
		checkWordWrap.setSelected(true);
		checkWordWrap.addItemListener((ItemEvent e) -> {
			boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
			textExport.setLineWrap(selected);
			// When enabling wrap, prefer word boundaries.
			textExport.setWrapStyleWord(selected);
			// Update horizontal scrollbar policy depending on wrap state.
			if (selected) {
				scrollExport.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			}
			else {
				scrollExport.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, checkWordWrap, 6, SpringLayout.SOUTH, comboLanguages);
		springLayout.putConstraint(SpringLayout.WEST, checkWordWrap, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(checkWordWrap);
		
		// Adjust scroll pane position to be below the word wrap checkbox.
		springLayout.putConstraint(SpringLayout.NORTH, scrollExport, 10, SpringLayout.SOUTH, checkWordWrap);
	}

	/**
	 * Post creation of the dialog controls.
	 * @param areaId 
	 */
	private void postCreate(long areaId) {
		
		// Remeber the root area.
		this.rootAreaId = areaId;
		
		// Initializa GUI.
		localize();
		setIcons();
		
		// Load languages.
		comboLanguages.initializeComboBox();
		comboLanguages.loadLanguagesToCombo();
		
		// Set listeners.
		setListeners();
		
		// Load dialog.
		loadDialog();
		
		// Process the areas.
		processAreas(null);
	}

	/**
	 * Localize texts of the dialog controls.
	 */
	private void localize() {
		
		Utility.localize(this);
		Utility.localize(buttonOk);
		Utility.localize(buttonCancel);
		Utility.localize(labelFolderTrust);
		Utility.localize(checkWordWrap);
	}
	
	/**
	 * Set frame icons.
	 */
	private void setIcons() {
		
		setIconImage(Images.getImage("org/multipage/gui/images/main.png"));
		buttonOk.setIcon(Images.getIcon("org/multipage/gui/images/ok_icon.png"));
		buttonCancel.setIcon(Images.getIcon("org/multipage/gui/images/cancel_icon.png"));
	}
	
	/**
	 * Set listeners to the langauge combo.
	 */
	private void setListeners() {
		
		try {
			comboLanguages.onSelectLanguage((language, evt) -> {
				// Process selected folder.
				processAreas(textFolderTrust.getText());
			});
		}
		catch (Exception e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * The dialog confirmed by the user click on the [OK] button.
	 */
	protected void onOk() {
		
		saveDialog();
		dispose();
	}
	
	/**
	 * The user clicked on the [Find Folder] button.
	 */
	protected void onFindFolder() {
		try {
			// Implement folder chooser.
	        SwingUtilities.invokeLater(() -> {
	        	
	        	try {
	        		// Create folder chooser.
		            JFileChooser chooser = new JFileChooser();
		            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		            
		            // Set dialog title.
		            String title = Resources.getString("org.multipage.generator.textSelectFolder");
		            chooser.setDialogTitle(title);
		            
		            // Show the folder chooser dialog.
		            int result = chooser.showOpenDialog(this);
	
		            // Process the selected folder.
		            if (result == JFileChooser.APPROVE_OPTION) {
		                File selectedFolder = chooser.getSelectedFile();
		                textFolderTrust.setText(selectedFolder.getAbsolutePath());
		                
		                processAreas(selectedFolder.getCanonicalPath());
		            }
	        	}
	        	catch (Exception e) {
	        		Safe.exception(e);
	        		Utility.show2(this, e.getLocalizedMessage());
	        		return;
	        	}	
	        });
		}
		catch (Exception e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Process areas into selected folder.
	 * @param selectedFolderName
	 */
	private void processAreas(String selectedFolderName) {
		try {
			
			// Login middle object.
			Middle middle = ProgramBasic.loginMiddle();
			StringBuilder exportText = new StringBuilder();
			
			// Get language ID.
			long languageId = getSelectedLanguageId();
			
			// Get export text.
			MiddleResult result = middle.selectLegibly(rootAreaId, languageId, exportText);
			result.throwPossibleException();
			
			// Set export text.
			textExport.setText(exportText.toString());
			
			if (selectedFolderName == null || selectedFolderName.isEmpty()) {
				return;
			}
			// TODO: Save result to the selected folder.
			File selectedFolder = new File(selectedFolderName);
		}
		catch (Exception e) {
			// Display error message.
			Utility.show2(this, e.getLocalizedMessage());
		}
		finally {
			ProgramBasic.logoutMiddle();
		}
	}
	
	/**
	 * Get selected language ID.
	 * @return
	 */
	private long getSelectedLanguageId() {
		try {
			// Get selected language ID.
			Language language = comboLanguages.getSelectedLanguage();
			if (language == null) {
				throw new NullPointerException();
			}
			
			// Return language ID.
			long languageId = language.id;
			return languageId;
		}
		catch (Exception e) {
			Safe.exception(e);
		}
		return 0L;
	}

	/**
	 * The frame has been canceled with the [Cancel] or the [X] button.
	 */
	protected void onCancel() {
		
		saveDialog();
		dispose();
	}
	
	/**
	 * Load and set initial state of the frame window.
	 */
	private void loadDialog() {
		
		// Set dialog window boundaries.
		if (bounds != null && !bounds.isEmpty()) {
			setBounds(bounds);
		}
		else {
			Utility.centerOnScreen(this);
		}
	}
	
	/**
	 * Save current state of the frame window.
	 */
	private void saveDialog() {
		
		// Save current dialog window boundaries.
		bounds = getBounds();
	}
}
