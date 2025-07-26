/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.SpringLayout;

import org.maclan.Language;
import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.maclan.VersionObj;
import org.maclan.server.BrowserParameters;
import org.multipage.basic.ProgramBasic;
import org.multipage.gui.Images;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.TextFieldEx;
import org.multipage.gui.ToolBarKit;
import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Dialog that displays information about rendering.
 * @author vakol
 *
 */
public class RenderDialog extends JDialog {
	
	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Serialized render browser flag.
	 */
	private static boolean serializedRenderBrowser = false;

	/**
	 * Serialized create list fag.
	 */
	private static boolean serializedCreateList = false;

	/**
	 * Serialized run browser flag.
	 */
	private static boolean serializedRunBrowser = false;
	
	/**
	 * Serialized remove old files flag.
	 */
	private static boolean serializedRemoveOldFiles = false;
	
	/**
	 * Serialized create index flag.
	 */
	private static boolean serializedCreateIndexFlag = false;

	/**
	 * Serialized related areas flag.
	 */
	private static boolean serializedRelatedAreasFlag = true;

	/**
	 * Read serialized data.
	 * @param inputStream
	 */
	public static void serializeData(StateInputStream inputStream)
		throws IOException, ClassNotFoundException {
		
		serializedCreateList = inputStream.readBoolean();
		serializedRenderBrowser = inputStream.readBoolean();
		serializedRunBrowser = inputStream.readBoolean();
		serializedRemoveOldFiles = inputStream.readBoolean();
		serializedCreateIndexFlag = inputStream.readBoolean();
		serializedRelatedAreasFlag = inputStream.readBoolean();
	}

	/**
	 * Write serialized data.
	 * @param outputStream
	 */
	public static void serializeData(StateOutputStream outputStream)
		throws IOException {
		
		outputStream.writeBoolean(serializedCreateList);
		outputStream.writeBoolean(serializedRenderBrowser);
		outputStream.writeBoolean(serializedRunBrowser);
		outputStream.writeBoolean(serializedRemoveOldFiles);
		outputStream.writeBoolean(serializedCreateIndexFlag);
		outputStream.writeBoolean(serializedRelatedAreasFlag);
	}

	/**
	 * Confirm flag.
	 */
	private boolean confirm = false;

	/**
	 * Languages' IDs.
	 */
	private LinkedList<Language> languages;
	
	/**
	 * Versions' IDs.
	 */
	private LinkedList<VersionObj> versions;
	
	/**
	 * Default language.
	 */
	private Language defaultLanguage;

	/**
	 * Target.
	 */
	private Obj<String> target;
	
	/**
	 * Coding.
	 */
	private Obj<String> coding;

	/**
	 * Show IDs flag.
	 */
	private Obj<Boolean> showTextIds;

	/**
	 * List model.
	 */
	private DefaultListModel modelLanguages;

	/**
	 * Start language ID.
	 */
	private Long startLanguageId;
	
	/**
	 * Browser parameters.
	 */
	private Obj<BrowserParameters> browserParameters;
	
	/**
	 * List file name.
	 */
	private Obj<Boolean> generateList;
	
	/**
	 * Run browser flag.
	 */
	private Obj<Boolean> runBrowser;
	
	/**
	 * Remove old files flag.
	 */
	private Obj<Boolean> removeOldFiles;

	/**
	 * Generated index flag.
	 */
	private Obj<Boolean> generatedIndex;
	
	/**
	 * Versions list model.
	 */
	private DefaultListModel modelVersions;

	/**
	 * Related areas setting.
	 */
	private Obj<Boolean> relatedAreas;

	// $hide<<$
	/**
	 * Components.
	 */
	private JLabel labelSelectLanguages;
	private JScrollPane scrollPaneLanguages;
	private JList listLanguages;
	private JButton buttonOk;
	private JButton buttonCancel;
	private JToolBar toolBarLanguages;
	private JLabel labelTarget;
	private JTextField textTarget;
	private JButton buttonTarget;
	private JLabel labelCoding;
	private JComboBox comboCoding;
	private JCheckBox checkShowIds;
	private JCheckBox checkRenderBrowser;
	private JCheckBox checkCreateList;
	private JButton buttonBrowserProperties;
	private JCheckBox checkRunBrowser;
	private JCheckBox checkRemoveOldFiles;
	private JButton buttonRemoveFiles;
	private JCheckBox checkCreateIndex;
	private JTabbedPane tabbedPane;
	private JPanel panelLanguages;
	private JPanel panelVersions;
	private JLabel labelSelectVersions;
	private JScrollPane scrollVersions;
	private JToolBar toolBarVersions;
	private JList listVersions;
	private JCheckBox checkRelatedAreas;

	/**
	 * Show dialog.
	 * @param component
	 * @param languages
	 * @param selectAllLanguages
	 * @param target
	 * @param coding
	 * @param showTextIds
	 * @param browserParameters
	 * @param generateList
	 * @param generateIndex
	 * @param runBrowser
	 * @param removeOldFiles
	 * @param versions
	 * @param relatedAreas
	 * @return
	 */
	public static boolean showDialog(Component component, LinkedList<Language> languages,
			boolean selectAllLanguages, Obj<String> target, Obj<String> coding,
			Obj<Boolean> showTextIds, Obj<BrowserParameters> browserParameters,
			Obj<Boolean> generateList, Obj<Boolean> generateIndex, Obj<Boolean> runBrowser,
			Obj<Boolean> removeOldFiles, LinkedList<VersionObj> versions, Obj<Boolean> relatedAreas) {
		
		try {
			RenderDialog dialog = new RenderDialog(Utility.findWindow(component),
					languages, selectAllLanguages, target, coding, showTextIds,
					browserParameters, generateList, generateIndex, runBrowser, removeOldFiles,
					versions, relatedAreas);
			dialog.setVisible(true);
	
			return dialog.confirm;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Create the dialog.
	 * @param window 
	 * @param languages 
	 * @param selectAllLanguages 
	 * @param target 
	 * @param coding 
	 * @param showTextIds 
	 * @param browserParameters 
	 * @param runBrowser 
	 * @param removeOldFiles 
	 * @param versions 
	 * @param relatedAreas 
	 */
	public RenderDialog(Window window, LinkedList<Language> languages, boolean selectAllLanguages,
			Obj<String> target, Obj<String> coding, Obj<Boolean> showTextIds,
			Obj<BrowserParameters> browserParameters, Obj<Boolean> generateList, Obj<Boolean> generateIndex,
			Obj<Boolean> runBrowser, Obj<Boolean> removeOldFiles, LinkedList<VersionObj> versions, Obj<Boolean> relatedAreas) {
		
		super(window, ModalityType.APPLICATION_MODAL);
		
		try {
			initComponents();
			// $hide>>$
			postCreation(languages, selectAllLanguages, target, coding, showTextIds,
						browserParameters, generateList, generateIndex, runBrowser, removeOldFiles, versions, relatedAreas);
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
		setTitle("org.multipage.generator.textRenderPages");
		setBounds(100, 100, 467, 530);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		buttonOk = new JButton("textOk");
		buttonOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOk();
			}
		});
		buttonOk.setPreferredSize(new Dimension(80, 25));
		buttonOk.setMargin(new Insets(0, 0, 0, 0));
		getContentPane().add(buttonOk);
		
		buttonCancel = new JButton("textCancel");
		springLayout.putConstraint(SpringLayout.EAST, buttonCancel, -10, SpringLayout.EAST, getContentPane());
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		springLayout.putConstraint(SpringLayout.SOUTH, buttonCancel, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, buttonOk, 0, SpringLayout.NORTH, buttonCancel);
		springLayout.putConstraint(SpringLayout.EAST, buttonOk, -6, SpringLayout.WEST, buttonCancel);
		buttonCancel.setPreferredSize(new Dimension(80, 25));
		buttonCancel.setMargin(new Insets(0, 0, 0, 0));
		getContentPane().add(buttonCancel);
		
		labelTarget = new JLabel("org.multipage.generator.textRenderingTarget");
		springLayout.putConstraint(SpringLayout.WEST, labelTarget, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(labelTarget);
		
		textTarget = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textTarget, 6, SpringLayout.SOUTH, labelTarget);
		springLayout.putConstraint(SpringLayout.WEST, textTarget, 10, SpringLayout.WEST, getContentPane());
		textTarget.setEditable(false);
		getContentPane().add(textTarget);
		textTarget.setColumns(10);
		
		buttonTarget = new JButton("");
		springLayout.putConstraint(SpringLayout.EAST, textTarget, -3, SpringLayout.WEST, buttonTarget);
		buttonTarget.setPreferredSize(new Dimension(20, 9));
		springLayout.putConstraint(SpringLayout.NORTH, buttonTarget, 0, SpringLayout.NORTH, textTarget);
		springLayout.putConstraint(SpringLayout.SOUTH, buttonTarget, 0, SpringLayout.SOUTH, textTarget);
		springLayout.putConstraint(SpringLayout.EAST, buttonTarget, -10, SpringLayout.EAST, getContentPane());
		buttonTarget.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onTarget();
			}
		});
		getContentPane().add(buttonTarget);
		
		labelCoding = new JLabel("org.multipage.generator.textCoding");
		springLayout.putConstraint(SpringLayout.NORTH, labelCoding, 6, SpringLayout.SOUTH, textTarget);
		springLayout.putConstraint(SpringLayout.WEST, labelCoding, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(labelCoding);
		
		comboCoding = new JComboBox();
		comboCoding.setPreferredSize(new Dimension(100, 20));
		springLayout.putConstraint(SpringLayout.NORTH, comboCoding, 6, SpringLayout.SOUTH, labelCoding);
		springLayout.putConstraint(SpringLayout.WEST, comboCoding, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(comboCoding);
		
		checkShowIds = new JCheckBox("org.multipage.generator.textShowTextIds");
		springLayout.putConstraint(SpringLayout.NORTH, checkShowIds, 0, SpringLayout.NORTH, comboCoding);
		getContentPane().add(checkShowIds);
		
		checkRenderBrowser = new JCheckBox("org.multipage.generator.textRenderBrowser");
		checkRenderBrowser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRenderBrowserFlag();
			}
		});
		getContentPane().add(checkRenderBrowser);
		
		checkCreateList = new JCheckBox("org.multipage.generator.textCreateRenderedList");
		springLayout.putConstraint(SpringLayout.WEST, checkRenderBrowser, 0, SpringLayout.WEST, checkCreateList);
		checkCreateList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCreateListFlag();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, checkRenderBrowser, 7, SpringLayout.SOUTH, checkCreateList);
		getContentPane().add(checkCreateList);
		
		buttonBrowserProperties = new JButton("org.multipage.generator.textBrowserProperties");
		buttonBrowserProperties.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSetBrowserProperties();
			}
		});
		buttonBrowserProperties.setMargin(new Insets(0, 0, 0, 0));
		buttonBrowserProperties.setPreferredSize(new Dimension(60, 23));
		springLayout.putConstraint(SpringLayout.NORTH, buttonBrowserProperties, 0, SpringLayout.NORTH, checkRenderBrowser);
		springLayout.putConstraint(SpringLayout.WEST, buttonBrowserProperties, 6, SpringLayout.EAST, checkRenderBrowser);
		getContentPane().add(buttonBrowserProperties);
		
		checkRunBrowser = new JCheckBox("org.multipage.generator.textRunBrowser");
		springLayout.putConstraint(SpringLayout.NORTH, checkRunBrowser, 0, SpringLayout.NORTH, checkRenderBrowser);
		springLayout.putConstraint(SpringLayout.WEST, checkRunBrowser, 10, SpringLayout.EAST, buttonBrowserProperties);
		getContentPane().add(checkRunBrowser);
		
		checkRemoveOldFiles = new JCheckBox("org.multipage.generator.textRemoveOldRenderedFiles");
		springLayout.putConstraint(SpringLayout.WEST, checkCreateList, 0, SpringLayout.WEST, checkRemoveOldFiles);
		springLayout.putConstraint(SpringLayout.WEST, checkRemoveOldFiles, 126, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, checkCreateList, 7, SpringLayout.SOUTH, checkRemoveOldFiles);
		springLayout.putConstraint(SpringLayout.NORTH, checkRemoveOldFiles, 7, SpringLayout.SOUTH, checkShowIds);
		getContentPane().add(checkRemoveOldFiles);
		
		buttonRemoveFiles = new JButton("org.multipage.generator.textRemoveFiles");
		springLayout.putConstraint(SpringLayout.NORTH, buttonRemoveFiles, 7, SpringLayout.SOUTH, checkShowIds);
		buttonRemoveFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRemoveOldFiles();
			}
		});
		springLayout.putConstraint(SpringLayout.WEST, buttonRemoveFiles, 6, SpringLayout.EAST, checkRemoveOldFiles);
		buttonRemoveFiles.setPreferredSize(new Dimension(80, 23));
		buttonRemoveFiles.setMargin(new Insets(0, 0, 0, 0));
		getContentPane().add(buttonRemoveFiles);
		
		checkCreateIndex = new JCheckBox("org.multipage.generator.textCreateIndex");
		springLayout.putConstraint(SpringLayout.WEST, checkCreateIndex, 10, SpringLayout.EAST, checkCreateList);
		springLayout.putConstraint(SpringLayout.SOUTH, checkCreateIndex, 0, SpringLayout.SOUTH, checkCreateList);
		getContentPane().add(checkCreateIndex);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		springLayout.putConstraint(SpringLayout.SOUTH, tabbedPane, 250, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, labelTarget, 10, SpringLayout.SOUTH, tabbedPane);
		springLayout.putConstraint(SpringLayout.NORTH, tabbedPane, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, tabbedPane, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, tabbedPane, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(tabbedPane);
		
		panelLanguages = new JPanel();
		tabbedPane.addTab("org.multipage.generator.textRenderedLanguages", null, panelLanguages, null);
		SpringLayout sl_panelLanguages = new SpringLayout();
		panelLanguages.setLayout(sl_panelLanguages);
		
		labelSelectLanguages = new JLabel("org.multipage.generator.textSelectLanguages");
		sl_panelLanguages.putConstraint(SpringLayout.NORTH, labelSelectLanguages, 3, SpringLayout.NORTH, panelLanguages);
		sl_panelLanguages.putConstraint(SpringLayout.WEST, labelSelectLanguages, 10, SpringLayout.WEST, panelLanguages);
		springLayout.putConstraint(SpringLayout.SOUTH, labelSelectLanguages, -47, SpringLayout.SOUTH, panelLanguages);
		panelLanguages.add(labelSelectLanguages);
		
		scrollPaneLanguages = new JScrollPane();
		sl_panelLanguages.putConstraint(SpringLayout.NORTH, scrollPaneLanguages, 3, SpringLayout.SOUTH, labelSelectLanguages);
		sl_panelLanguages.putConstraint(SpringLayout.WEST, scrollPaneLanguages, 10, SpringLayout.WEST, panelLanguages);
		sl_panelLanguages.putConstraint(SpringLayout.EAST, scrollPaneLanguages, -10, SpringLayout.EAST, panelLanguages);
		springLayout.putConstraint(SpringLayout.NORTH, scrollPaneLanguages, 0, SpringLayout.NORTH, panelLanguages);
		springLayout.putConstraint(SpringLayout.WEST, scrollPaneLanguages, 0, SpringLayout.WEST, panelLanguages);
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPaneLanguages, -25, SpringLayout.SOUTH, panelLanguages);
		springLayout.putConstraint(SpringLayout.EAST, scrollPaneLanguages, -178, SpringLayout.EAST, panelLanguages);
		springLayout.putConstraint(SpringLayout.WEST, labelSelectLanguages, 6, SpringLayout.EAST, scrollPaneLanguages);
		panelLanguages.add(scrollPaneLanguages);
		
		listLanguages = new JList();
		listLanguages.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		listLanguages.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				onLanguagesListClicked(e);
			}
		});
		scrollPaneLanguages.setViewportView(listLanguages);
		
		toolBarLanguages = new JToolBar();
		sl_panelLanguages.putConstraint(SpringLayout.WEST, toolBarLanguages, 10, SpringLayout.WEST, panelLanguages);
		sl_panelLanguages.putConstraint(SpringLayout.EAST, toolBarLanguages, -10, SpringLayout.EAST, panelLanguages);
		sl_panelLanguages.putConstraint(SpringLayout.SOUTH, scrollPaneLanguages, -3, SpringLayout.NORTH, toolBarLanguages);
		sl_panelLanguages.putConstraint(SpringLayout.SOUTH, toolBarLanguages, 0, SpringLayout.SOUTH, panelLanguages);
		springLayout.putConstraint(SpringLayout.NORTH, toolBarLanguages, 4, SpringLayout.SOUTH, scrollPaneLanguages);
		springLayout.putConstraint(SpringLayout.SOUTH, toolBarLanguages, -67, SpringLayout.NORTH, labelTarget);
		panelLanguages.add(toolBarLanguages);
		springLayout.putConstraint(SpringLayout.WEST, toolBarLanguages, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, toolBarLanguages, -10, SpringLayout.EAST, getContentPane());
		toolBarLanguages.setFloatable(false);
		
		panelVersions = new JPanel();
		tabbedPane.addTab("org.multipage.generator.textRenderedVersions", null, panelVersions, null);
		SpringLayout sl_panelVersions = new SpringLayout();
		panelVersions.setLayout(sl_panelVersions);
		
		labelSelectVersions = new JLabel("org.multipage.generator.textSelectVersions");
		sl_panelVersions.putConstraint(SpringLayout.NORTH, labelSelectVersions, 3, SpringLayout.NORTH, panelVersions);
		sl_panelVersions.putConstraint(SpringLayout.WEST, labelSelectVersions, 10, SpringLayout.WEST, panelVersions);
		panelVersions.add(labelSelectVersions);
		
		scrollVersions = new JScrollPane();
		sl_panelVersions.putConstraint(SpringLayout.NORTH, scrollVersions, 3, SpringLayout.SOUTH, labelSelectVersions);
		sl_panelVersions.putConstraint(SpringLayout.WEST, scrollVersions, 10, SpringLayout.WEST, panelVersions);
		sl_panelVersions.putConstraint(SpringLayout.EAST, scrollVersions, -10, SpringLayout.EAST, panelVersions);
		panelVersions.add(scrollVersions);
		
		toolBarVersions = new JToolBar();
		sl_panelVersions.putConstraint(SpringLayout.SOUTH, scrollVersions, -3, SpringLayout.NORTH, toolBarVersions);
		
		listVersions = new JList();
		listVersions.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		listVersions.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				onVersionsListClicked(e);
			}
		});
		scrollVersions.setViewportView(listVersions);
		sl_panelVersions.putConstraint(SpringLayout.WEST, toolBarVersions, 10, SpringLayout.WEST, panelVersions);
		sl_panelVersions.putConstraint(SpringLayout.SOUTH, toolBarVersions, 0, SpringLayout.SOUTH, panelVersions);
		sl_panelVersions.putConstraint(SpringLayout.EAST, toolBarVersions, -10, SpringLayout.EAST, panelVersions);
		toolBarVersions.setFloatable(false);
		panelVersions.add(toolBarVersions);
		
		checkRelatedAreas = new JCheckBox("org.multipage.generator.textRenderRelatedAreas");
		springLayout.putConstraint(SpringLayout.WEST, checkShowIds, 10, SpringLayout.EAST, checkRelatedAreas);
		springLayout.putConstraint(SpringLayout.NORTH, checkRelatedAreas, 0, SpringLayout.NORTH, comboCoding);
		springLayout.putConstraint(SpringLayout.WEST, checkRelatedAreas, 126, SpringLayout.WEST, getContentPane());
		getContentPane().add(checkRelatedAreas);
	}

	/**
	 * On remove old files.
	 */
	protected void onRemoveOldFiles() {
		try {
			
			if (textTarget.getForeground() == Color.RED) {
				Utility.show(this, "org.multipage.generator.messageTargetDoesntExist");
				return;
			}
			
			if (Utility.ask(this, "org.multipage.generator.textRemoveOldRenderedFiles2")) {
				Utility.deleteFolderContent(textTarget.getText());
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On OK button.
	 */
	protected void onOk() {
		try {
			
			if (textTarget.getForeground() == Color.RED) {
				Utility.show(this, "org.multipage.generator.messageTargetDoesntExist");
				return;
			}
			
			target.ref = textTarget.getText();
			coding.ref = (String) comboCoding.getSelectedItem();
			showTextIds.ref = checkShowIds.isSelected();
			
			if (browserParameters != null) {
				if (checkRenderBrowser.isSelected()) {
					browserParameters.ref = BrowserParametersDialog.getParameters();
				}
				else {
					browserParameters.ref = null;
				}
			}
			
			if (generateList != null) {
				generateList.ref = checkCreateList.isSelected();
			}
			
			if (runBrowser != null) {
				runBrowser.ref = checkRunBrowser.isSelected();
			}
			
			removeOldFiles.ref = checkRemoveOldFiles.isSelected();
			
			if (generatedIndex != null) {
				generatedIndex.ref = checkCreateIndex.isSelected();
			}
			
			if (relatedAreas != null) {
				relatedAreas.ref = checkRelatedAreas.isSelected();
			}
	
			outputSelectedLanguagesIds();
			outputSelectedVersionsIds();
			
			saveDialog();
			
			confirm = true;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
		dispose();
	}

	/**
	 * Post creation.
	 * @param languages
	 * @param selectAllLanguages
	 * @param target
	 * @param coding
	 * @param showTextIds
	 * @param browserParameters
	 * @param generateList
	 * @param generatedIndex
	 * @param runBrowser
	 * @param removeOldFiles
	 * @param versions 
	 * @param relatedAreas 
	 */
	private void postCreation(LinkedList<Language> languages, boolean selectAllLanguages,
			Obj<String> target, Obj<String> coding, Obj<Boolean> showTextIds,
			Obj<BrowserParameters> browserParameters, Obj<Boolean> generateList,
			Obj<Boolean> generatedIndex, Obj<Boolean> runBrowser, Obj<Boolean> removeOldFiles,
			LinkedList<VersionObj> versions, Obj<Boolean> relatedAreas) {
		try {
			
			this.languages = languages;
			this.target = target;
			this.coding = coding;
			this.showTextIds = showTextIds;
			this.browserParameters = browserParameters;
			this.generateList = generateList;
			this.generatedIndex = generatedIndex;
			this.runBrowser = runBrowser;
			this.removeOldFiles = removeOldFiles;
			this.versions = versions;
			this.relatedAreas = relatedAreas;
			
			setTartgetField(target.ref);
			
			Utility.centerOnScreen(this);
			
			localize();
			setIcons();
			createLanguagesList(selectAllLanguages);
			createVersionsList();
			createToolBars();
			createCodingsList();
			
			loadDialog();
			
			// On display disable controls.
			disableControls();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Disable controls on display.
	 */
	private void disableControls() {
		try {
			
			if (browserParameters == null) {
				checkRenderBrowser.setEnabled(false);
				buttonBrowserProperties.setEnabled(false);
				checkRunBrowser.setEnabled(false);
			}
			
			if (generateList == null) {
				checkCreateList.setEnabled(false);
				checkCreateIndex.setEnabled(false);
			}
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
			buttonTarget.setIcon(Images.getIcon("org/multipage/generator/images/open.png"));
			buttonBrowserProperties.setIcon(Images.getIcon("org/multipage/generator/images/properties.png"));
			buttonRemoveFiles.setIcon(Images.getIcon("org/multipage/generator/images/bin.png"));
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
			Utility.localize(labelSelectLanguages);
			Utility.localize(buttonOk);
			Utility.localize(buttonCancel);
			Utility.localize(labelTarget);
			Utility.localize(labelCoding);
			Utility.localize(checkShowIds);
			Utility.localize(checkRenderBrowser);
			Utility.localize(checkCreateList);
			Utility.localize(buttonBrowserProperties);
			Utility.localize(checkRunBrowser);
			Utility.localize(checkRemoveOldFiles);
			Utility.localize(buttonRemoveFiles);
			Utility.localize(checkCreateIndex);
			Utility.localize(tabbedPane);
			Utility.localize(labelSelectVersions);
			Utility.localize(checkRelatedAreas);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create languages list.
	 * @param selectAllLanguages 
	 */
	private void createLanguagesList(boolean selectAllLanguages) {
		try {
			
			// Set model.
			createLanguagesListModel(selectAllLanguages);
			// Set renderer.
			createLanguagesListRenderer();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create list renderer.
	 */
	@SuppressWarnings("unchecked")
	private void createLanguagesListRenderer() {
		try {
			
			listLanguages.setCellRenderer(new ListCellRenderer() {
				
				private final LanguageListItem renderer = new LanguageListItem();
				private final JLabel defaultRenderer = new JLabel();
				
				@Override
				public Component getListCellRendererComponent(JList list, Object value,
						int index, boolean isSelected, boolean cellHasFocus) {
					
					try {
						if (!(value instanceof Language)) {
							return defaultRenderer;
						}
						
						renderer.setProperties((Language) value, index);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return renderer;
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create list model.
	 * @param selectAllLanguages 
	 */
	private void createLanguagesListModel(boolean selectAllLanguages) {
		try {
			
			modelLanguages = new DefaultListModel();
			listLanguages.setModel(modelLanguages);
	
			// Load languages.
			LinkedList<Language> languages = new LinkedList<Language>();
			
			MiddleResult result;
			
			// Login to the database.
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			
			result = middle.login(login);
			if (result.isOK()) {
				
				result = middle.loadLanguages(languages);
				if (result.isOK()) {
					
					// Load start language ID.
					Obj<Long> startLanguageId = new Obj<Long>();
					result = middle.loadStartLanguageId(startLanguageId);
					
					this.startLanguageId = startLanguageId.ref;
					
					// Load into the list model.
					for (Language language : languages) {
						
						if (selectAllLanguages) {
							language.user = true;
						}
						else {
							language.user = language.id == this.startLanguageId;
						}
						
						if (language.id != 0L) {
	
							modelLanguages.addElement(language);
						}
						else {
							defaultLanguage = language;
							
							modelLanguages.add(0, language);
						}
					}
				}
	
				// Logout from the database.
				MiddleResult logoutResult = middle.logout(result);
				if (result.isOK()) {
					result = logoutResult;
				}
			}
			
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create tool bars.
	 */
	private void createToolBars() {
		try {
			
			ToolBarKit.addToolBarButton(toolBarLanguages, "org/multipage/generator/images/select_all.png",
					"org.multipage.generator.tooltipSelectAllLanguages", () -> onSelectAllLanguages());
			ToolBarKit.addToolBarButton(toolBarLanguages, "org/multipage/generator/images/deselect_all.png",
					"org.multipage.generator.tooltipUnselectAllLanguages", () -> onUnselectAllLanguages());
			
			ToolBarKit.addToolBarButton(toolBarVersions, "org/multipage/generator/images/select_all.png",
					"org.multipage.generator.tooltipSelectAllVersions", () -> onSelectAllVersions());
			ToolBarKit.addToolBarButton(toolBarVersions, "org/multipage/generator/images/deselect_all.png",
					"org.multipage.generator.tooltipUnselectAllVersions", () -> onUnselectAllVersions());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create codings list.
	 */
	private void createCodingsList() {
		try {
			
			comboCoding.addItem("UTF-16");
			comboCoding.addItem("UTF-8");
			
			comboCoding.setSelectedItem(coding.ref);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On list clicked.
	 * @param e 
	 */
	protected void onLanguagesListClicked(MouseEvent e) {
		try {
			
			int index = listLanguages.locationToIndex(e.getPoint());
			Rectangle rectangle = listLanguages.getCellBounds(index, index);
			if (rectangle != null) {
				if (rectangle.contains(e.getPoint())) {
					
					// Select language.
					Object object = modelLanguages.get(index);
					if (object instanceof Language) {
						
						Language language = (Language) object;
						if (language.user instanceof Boolean) {
							language.user = !(Boolean) language.user;
							
							listLanguages.updateUI();
						}
					}
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On versions list clicked.
	 * @param e
	 */
	protected void onVersionsListClicked(MouseEvent e) {
		try {
			
			int index = listVersions.locationToIndex(e.getPoint());
			Rectangle rectangle = listVersions.getCellBounds(index, index);
			if (rectangle != null) {
				if (rectangle.contains(e.getPoint())) {
					
					// Select language.
					Object object = modelVersions.get(index);
					if (object instanceof VersionObj) {
						
						VersionObj version = (VersionObj) object;
						if (version.getUser() instanceof Boolean) {
							version.setUser(!(Boolean) version.getUser());
							
							listVersions.updateUI();
						}
					}
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On select all languages.
	 */
	public void onSelectAllLanguages() {
		try {
			
			selectAllLanguages(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On unselect all languages.
	 */
	public void onUnselectAllLanguages() {
		try {
			
			selectAllLanguages(false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On select all versions.
	 */
	public void onSelectAllVersions() {
		try {
			
			selectAllVersions(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On unselect all versions.
	 */
	public void onUnselectAllVersions() {
		try {
			
			selectAllVersions(false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Select / unselect all languages.
	 * @param select
	 */
	private void selectAllLanguages(boolean select) {
		try {
			
			for (int index = 0; index < modelLanguages.getSize(); index++) {
				Object object = modelLanguages.get(index);
				if (object instanceof Language) {
					Language language = (Language) object;
					language.user = select;
				}
			}
			
			listLanguages.updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Select / unselect all versions.
	 * @param select
	 */
	private void selectAllVersions(boolean select) {
		try {
			
			for (int index = 0; index < modelVersions.getSize(); index++) {
				Object object = modelVersions.get(index);
				if (object instanceof VersionObj) {
					VersionObj version = (VersionObj) object;
					version.setUser(select);
				}
			}
			
			listVersions.updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On select target.
	 */
	protected void onTarget() {
		try {
			
			// Get rendering target.
			String target = Utility.chooseDirectory(this, Resources.getString("org.multipage.generator.textSelectRenderingTarget"));
			if (target == null) {
				return;
			}
			
			// Set target field.
			setTartgetField(target);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set taget field.
	 * @param target
	 */
	private void setTartgetField(String target) {
		
		try {
			
			if (!existsTarget(target)) {
				target = Resources.getString("org.multipage.generator.textTargetDoesntExist");
				textTarget.setForeground(Color.RED);
			}
			else {
				textTarget.setForeground(Color.BLACK);
			}
			
			textTarget.setText(target);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Returns true value if the target exists.
	 * @param target2
	 * @return
	 */
	private boolean existsTarget(String target) {
		
		try {
			return new File(target).exists();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Output selected languages' IDs.
	 */
	private void outputSelectedLanguagesIds() {
		try {
			
			languages.clear();
			
			for (int index = 0; index < modelLanguages.getSize(); index++) {
				Object object = modelLanguages.get(index);
				if (object instanceof Language) {
					Language language = (Language) object;
					
					if (language.user instanceof Boolean) {
						boolean selected = (Boolean) language.user;
						
						if (selected) {
							languages.add(language);
						}
					}
				}
			}
			
			// If nothing selected use the default language.
			if (languages.isEmpty()) {
				// Get default language.
				if (defaultLanguage != null) {
					languages.add(defaultLanguage);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Output selected versions' IDs.
	 */
	private void outputSelectedVersionsIds() {
		try {
			
			versions.clear();
			
			VersionObj defaultVersion = null;
			
			for (int index = 0; index < modelVersions.getSize(); index++) {
				Object object = modelVersions.get(index);
				if (object instanceof VersionObj) {
					VersionObj version = (VersionObj) object;
					
					if (version.getUser() instanceof Boolean) {
						boolean selected = (Boolean) version.getUser();
						
						if (selected) {
							versions.add(version);
						}
					}
					
					if (version.getId() == 0L) {
						defaultVersion = version;
					}
				}
			}
			
			// If nothing selected use the default version.
			if (versions.isEmpty()) {
				// Get default version.
				if (defaultVersion != null) {
					versions.add(defaultVersion);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On render browser flag changed.
	 */
	protected void onRenderBrowserFlag() {
		try {
			
			buttonBrowserProperties.setEnabled(checkRenderBrowser.isSelected());
			checkRunBrowser.setEnabled(checkRenderBrowser.isSelected());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On set browser properties.
	 */
	protected void onSetBrowserProperties() {
		try {
			
			BrowserParametersDialog.showDialog(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On create list flag.
	 */
	protected void onCreateListFlag() {
		try {
			
			checkCreateIndex.setEnabled(checkCreateList.isSelected());
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
			
			checkCreateList.setSelected(serializedCreateList);
			checkRenderBrowser.setSelected(serializedRenderBrowser);
			checkRunBrowser.setSelected(serializedRunBrowser);
			checkRemoveOldFiles.setSelected(serializedRemoveOldFiles);
			checkCreateIndex.setSelected(serializedCreateIndexFlag);
			checkRelatedAreas.setSelected(serializedRelatedAreasFlag);
			
			// Set list and index file controls.
			checkCreateIndex.setEnabled(checkCreateList.isSelected());
			
			// Set browser controls.
			boolean isBrowserRendered = checkRenderBrowser.isSelected();
			buttonBrowserProperties.setEnabled(isBrowserRendered);
			checkRunBrowser.setEnabled(isBrowserRendered);
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
			
			serializedCreateList = checkCreateList.isSelected();
			serializedRenderBrowser = checkRenderBrowser.isSelected();
			serializedRunBrowser = checkRunBrowser.isSelected();
			serializedRemoveOldFiles = checkRemoveOldFiles.isSelected();
			serializedCreateIndexFlag = checkCreateIndex.isSelected();
			serializedRelatedAreasFlag = checkRelatedAreas.isSelected();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create versions list.
	 */
	private void createVersionsList() {
		try {
			
			createVersionsModel();
			createVersionsListRenderer();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create versions model.
	 */
	private void createVersionsModel() {
		try {
			
			modelVersions = new DefaultListModel();
			
			// Load versions.
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			
			LinkedList<VersionObj> versions = new LinkedList<VersionObj>();
			
			MiddleResult result = middle.loadVersions(login, 0L, versions);
			
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			VersionObj defaultVersion = null;
			
			// Put versions into the list model.
			for (VersionObj version : versions) {
				
				version.setUser(false);
				modelVersions.addElement(version);
				
				if (version.getId() == 0L) {
					defaultVersion = version;
				}
			}
			
			if (defaultVersion != null) {
				defaultVersion.setUser(true);
			}
			
			listVersions.setModel(modelVersions);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	

	/**
	 * Create versions list renderer.
	 */
	@SuppressWarnings("unchecked")
	private void createVersionsListRenderer() {
		try {
			
			listVersions.setCellRenderer(new ListCellRenderer() {
				
				private final VersionsListItem renderer = new VersionsListItem();
				private final JLabel defaultRenderer = new JLabel();
				
				@Override
				public Component getListCellRendererComponent(JList list, Object value,
						int index, boolean isSelected, boolean cellHasFocus) {
					
					try {
						if (!(value instanceof VersionObj)) {
							return defaultRenderer;
						}
						
						renderer.setProperties((VersionObj) value, index);
						return renderer;
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return defaultRenderer;
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
