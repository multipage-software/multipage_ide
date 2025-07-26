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
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import org.maclan.Area;
import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.maclan.VersionObj;
import org.multipage.basic.ProgramBasic;
import org.multipage.generator.GeneratorMainFrame;
import org.multipage.generator.GeneratorTranslatorDialog;
import org.multipage.generator.VersionRenderer;
import org.multipage.gui.Images;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.ToolBarKit;
import org.multipage.gui.Utility;
import org.multipage.util.Safe;

/**
 * Dialog that displays editor of list of versions.
 * @author vakol
 *
 */
public class VersionsEditor extends JDialog {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Dialog boundary.
	 */
	public static Rectangle bounds;

	/**
	 * Load properties.
	 * @param inputStream
	 * @throws ClassNotFoundException 
	 */
	public static void serializeData(StateInputStream inputStream)
		throws IOException, ClassNotFoundException {
		
		// Get dialog bounds.
		bounds = (Rectangle) inputStream.readObject();
	}

	/**
	 * Save properties.
	 * @param outputStream
	 */
	public static void serializeData(StateOutputStream outputStream)
		throws IOException {
		
		// Save dialog bounds.
		outputStream.writeObject(bounds);
	}

	/**
	 * List model.
	 */
	private  DefaultListModel<VersionObj> listModel;

	// $hide<<$
	/**
	 * Components.
	 */
	private JPanel panel;
	private JButton buttonOk;
	private JPanel panelComponents;
	private JLabel labelVersionsList;
	private JScrollPane scrollPane;
	private JToolBar toolBar;
	private JList list;
	private JButton buttonTranslator;

	/**
	 * Show dialog.
	 * @param parent
	 * @param resource
	 */
	public static void showDialog(Component parent) {
		try {
			
			VersionsEditor dialog = new VersionsEditor(Utility.findWindow(parent));
			dialog.setVisible(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Create the dialog.
	 * @param parentWindow 
	 */
	public VersionsEditor(Window parentWindow) {
		super(parentWindow, ModalityType.DOCUMENT_MODAL);
		
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
	 * Load dialog.
	 */
	private void loadDialog() {
		try {
			
			if (bounds != null) {
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
	
	/**
	 * Initialize components.
	 */
	private void initComponents() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				onCancel();
			}
		});
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("builder.textVersionsEditor");
		
		setBounds(100, 100, 450, 344);
		
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(10, 43));
		getContentPane().add(panel, BorderLayout.SOUTH);
		SpringLayout sl_panel = new SpringLayout();
		panel.setLayout(sl_panel);
		
		buttonOk = new JButton("textOk");
		buttonOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onOK();
			}
		});
		buttonOk.setMargin(new Insets(0, 0, 0, 0));
		sl_panel.putConstraint(SpringLayout.SOUTH, buttonOk, -10, SpringLayout.SOUTH, panel);
		sl_panel.putConstraint(SpringLayout.EAST, buttonOk, -10, SpringLayout.EAST, panel);
		buttonOk.setPreferredSize(new Dimension(80, 25));
		panel.add(buttonOk);
		
		buttonTranslator = new JButton("builder.textTranslator");
		buttonTranslator.setIconTextGap(6);
		buttonTranslator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onTranslator();
			}
		});
		buttonTranslator.setHorizontalAlignment(SwingConstants.LEFT);
		sl_panel.putConstraint(SpringLayout.WEST, buttonTranslator, 10, SpringLayout.WEST, panel);
		sl_panel.putConstraint(SpringLayout.SOUTH, buttonTranslator, 0, SpringLayout.SOUTH, buttonOk);
		buttonTranslator.setPreferredSize(new Dimension(100, 30));
		buttonTranslator.setMargin(new Insets(0, 3, 0, 0));
		panel.add(buttonTranslator);
		
		panelComponents = new JPanel();
		getContentPane().add(panelComponents, BorderLayout.CENTER);
		SpringLayout sl_panelComponents = new SpringLayout();
		panelComponents.setLayout(sl_panelComponents);
		
		labelVersionsList = new JLabel("builder.textVersionsList");
		sl_panelComponents.putConstraint(SpringLayout.NORTH, labelVersionsList, 10, SpringLayout.NORTH, panelComponents);
		sl_panelComponents.putConstraint(SpringLayout.WEST, labelVersionsList, 10, SpringLayout.WEST, panelComponents);
		panelComponents.add(labelVersionsList);
		
		scrollPane = new JScrollPane();
		sl_panelComponents.putConstraint(SpringLayout.NORTH, scrollPane, 9, SpringLayout.SOUTH, labelVersionsList);
		sl_panelComponents.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, panelComponents);
		sl_panelComponents.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, panelComponents);
		panelComponents.add(scrollPane);
		
		toolBar = new JToolBar();
		sl_panelComponents.putConstraint(SpringLayout.SOUTH, scrollPane, 0, SpringLayout.NORTH, toolBar);
		
		list = new JList();
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				onListClick(event);
			}
		});
		scrollPane.setViewportView(list);
		toolBar.setFloatable(false);
		sl_panelComponents.putConstraint(SpringLayout.EAST, toolBar, -10, SpringLayout.EAST, panelComponents);
		toolBar.setPreferredSize(new Dimension(13, 30));
		sl_panelComponents.putConstraint(SpringLayout.WEST, toolBar, 10, SpringLayout.WEST, panelComponents);
		sl_panelComponents.putConstraint(SpringLayout.SOUTH, toolBar, -10, SpringLayout.SOUTH, panelComponents);
		panelComponents.add(toolBar);
	}

	/**
	 * On cancel.
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

	/**
	 * On list click.
	 * @param event 
	 */
	protected void onListClick(MouseEvent event) {
		try {
			
			if (event.getButton() == MouseEvent.BUTTON1 && event.getClickCount() == 2) {
				onEditVersion();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On OK.
	 */
	protected void onOK() {
		try {
			
			saveDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
		
		dispose();
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			
			Utility.centerOnScreen(this);
			
			createToolBar();
			localize();
			setIcons();
			
			initializeList();
			loadVersions();
			
			loadDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Initialize list.
	 */
	private void initializeList() {
		try {
			
			// Create and set list model.
			listModel = new DefaultListModel<VersionObj>();
			list.setModel(listModel);
			
			// Create and set renderer.
			list.setCellRenderer(new ListCellRenderer<VersionObj>() {
	
				// Label object.
				VersionRenderer renderer = new VersionRenderer();
	
				// Renderer method.
				@Override
				public Component getListCellRendererComponent(
						JList<? extends VersionObj> list, VersionObj value,
						int index, boolean isSelected, boolean cellHasFocus) {
					
					try {
						if (value == null) {
							renderer.reset();
						}
						else {
							renderer.set(value, index, isSelected, cellHasFocus);
						}
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
	 * Create tool bar.
	 */
	private void createToolBar() {
		try {
			
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/add_item_icon.png",
					"builder.tooltipAddVersion", () -> onAddVersion());
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/edit.png",
					"builder.tooltipEditVersion", () -> onEditVersion());
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/remove_icon.png",
					"builder.tooltipRemoveVersion", () -> onRemoveVersion());
			toolBar.addSeparator();
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/update_icon.png",
					"builder.tooltipUpdateVersions", () -> onUpdateVersions());
			toolBar.addSeparator();
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/select_all.png",
					"org.multipage.generator.tooltipSelectAllVersions", () -> onSelectAllVersions());
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/deselect_all.png",
					"org.multipage.generator.tooltipUnselectAllVersions", () -> onUnselectAllVersions());
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
			Utility.localize(labelVersionsList);
			Utility.localize(buttonTranslator);
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
			buttonTranslator.setIcon(Images.getIcon("org/multipage/generator/images/translator.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Load versions.
	 */
	private void loadVersions() {
		try {
			
			listModel.clear();
			
			// Prepare prerequisites.
			Properties login = ProgramBasic.getLoginProperties();
			Middle middle = ProgramBasic.getMiddle();
			
			LinkedList<VersionObj> versions = new LinkedList<VersionObj>();
			
			// Load data from the database.
			MiddleResult result = middle.loadVersions(login, 0L, versions);
			
			// On error inform user.
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			// Populate list.
			for (VersionObj version : versions) {
				listModel.addElement(version);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Gets selected version.
	 * @return
	 */
	private VersionObj getSelectedVersion() {
		
		try {
			if (list.getSelectedIndices().length != 1) {
				Utility.show(this, "org.multipage.generator.messageSelectSingleVersion");
				return null;
			}
			
			return (VersionObj) list.getSelectedValue();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Select version.
	 * @param versionId
	 */
	private void select(long versionId) {
		try {
			
			for (int index = 0; index < listModel.getSize(); index++) {
				
				VersionObj version = listModel.get(index);
				if (version.getId() == versionId) {
					
					list.setSelectedIndex(index);
					break;
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On add version.
	 */
	public void onAddVersion() {
		try {
			
			// Get new version object.
			VersionObj version = new VersionObj();
			
			if (!VersionPropertiesDialog.showNewDialog(this, version)) {
				return;
			}
			
			// Prepare prerequisites.
			Properties login = ProgramBasic.getLoginProperties();
			Middle middle = ProgramBasic.getMiddle();
			
			// Insert data.
			MiddleResult result = middle.insertVersion(login, version);
			
			// On error inform user.
			if (result.isNotOK()) {
				result.show(this);
			}
			
			// Load versions.
			loadVersions();
			
			// Select version.
			select(version.getId());
			
			// Ensure selection visible.
			Utility.ensureSelectedItemVisible(list);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On edit version.
	 */
	public void onEditVersion() {
		try {
			
			VersionObj version = getSelectedVersion();
			if (version == null) {
				return;
			}
			
			// Edit version.
			if (!VersionPropertiesDialog.showEditDialog(this, version)) {
				return;
			}
			
			// Prepare prerequisites.
			Properties login = ProgramBasic.getLoginProperties();
			Middle middle = ProgramBasic.getMiddle();
			
			// Update version.
			MiddleResult result = middle.updateVersion(login, version);
			
			// On error inform user.
			if (result.isNotOK()) {
				result.show(this);
			}
			
			// Load versions.
			loadVersions();
			
			// Select version.
			select(version.getId());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On remove version.
	 */
	public void onRemoveVersion() {
		try {
			
			List<VersionObj> versions = list.getSelectedValuesList();
			if (versions.isEmpty()) {
				Utility.show(this, "org.multipage.generator.messageSelectSingleVersion");
				return;
			}
			
			// Ask user.
			if (!Utility.ask(this, "builder.messageDeleteSelectedVersions")) {
				return;
			}
			
			// Prepare prerequisites.
			Properties login = ProgramBasic.getLoginProperties();
			Middle middle = ProgramBasic.getMiddle();
			
			// Try to login to database.
			MiddleResult result = middle.login(login);
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
				
			// Do for all selected versions.
			for (VersionObj version : versions) {
			
				// Cannot delete default version.
				if (version.getId() == 0L) {
					Utility.show(this, "builder.messageCannotDeleteDefaultVersion");
					continue;
				}
	
				// Update version.
				result = middle.removeVersion(version.getId());
				
				// On error inform user.
				if (result.isNotOK()) {
					break;
				}
			}
			
			// Inform user about an error.
			if (result.isNotOK()) {
				result.show(this);
			}
			
			// Logout from database.
			MiddleResult logoutResult = middle.logout(result);
			if (logoutResult.isNotOK()) {
				logoutResult.show(this);
			}
			
			// Load versions.
			loadVersions();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Update versions.
	 */
	public void onUpdateVersions() {
		try {
			
			loadVersions();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Select all versions.
	 */
	public void onSelectAllVersions() {
		try {
			
			list.setSelectionInterval(0, listModel.getSize() - 1);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Unselect all versions.
	 */
	public void onUnselectAllVersions() {
		try {
			
			list.clearSelection();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On translator.
	 */
	protected void onTranslator() {
		try {
			
			// Show dialog.
			GeneratorTranslatorDialog.showDialog(GeneratorMainFrame.getFrame(), new LinkedList<Area>());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}