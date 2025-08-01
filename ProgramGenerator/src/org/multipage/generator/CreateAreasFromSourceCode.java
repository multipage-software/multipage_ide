/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */
package org.multipage.generator;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.maclan.Area;
import org.maclan.Slot;
import org.multipage.basic.ProgramBasic;
import org.multipage.gui.Images;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.Utility;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

import javax.swing.JPopupMenu;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import java.awt.Insets;

/**
 * Dialog that enables to create areas from source code.
 * @author vakol
 *
 */
public class CreateAreasFromSourceCode extends JDialog {

	/**
	 * Version
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * States.
	 */
	private static Rectangle bounds;

	/**
	 * Set default state.
	 */
	public static void setDefaultData() {
		
		bounds = new Rectangle();
	}

	/**
	 * Read state.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void serializeData(StateInputStream inputStream)
			throws IOException, ClassNotFoundException {
		
		bounds = Utility.readInputStreamObject(inputStream, Rectangle.class);
	}

	/**
	 * Write state.
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream)
			throws IOException {
		
		outputStream.writeObject(bounds);
	}
	
	/**
	 * Components.
	 */
	private JButton buttonAdd;
	private JLabel labelSourceOverview;
	private JScrollPane scrollPane;
	private JTree tree;
	
	/**
	 * Source tree root
	 */
	private DefaultMutableTreeNode root;

	/**
	 * Source tree model
	 */
	private DefaultTreeModel treeModel;
	private JButton buttonCreate;
	
	/**
	 * Superarea for import
	 */
	private Area area;
	
	/**
	 * Slot list panel.
	 */
	private SlotListPanel slotListPanel;
	
	/**
	 * Components.
	 */
	private JSplitPane splitPane;
	private JTextField textSlotName;
	private JCheckBox checkFilesToAreas;
	private JLabel labelAreaSlot;
	private JPopupMenu popupMenuTree;
	private JMenuItem menuRemove;
	

	/**
	 * Create the dialog
	 * @param parentWindow 
	 */
	public CreateAreasFromSourceCode(Window parentWindow) {
		super(parentWindow, ModalityType.DOCUMENT_MODAL);
		
		try {
			initComponents();
			postCreate(); //$hide$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Show dialog
	 * @param parent
	 * @param area
	 * @return
	 */
	public static void showDialog(Component parent, Area area) {
		try {
			
			CreateAreasFromSourceCode dialog = new CreateAreasFromSourceCode(Utility.findWindow(parent));
			dialog.area = area;
			dialog.setVisible(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Initialize components
	 */
	private void initComponents() {
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});
		setTitle("org.multipage.generatorCreateAreasFromSourceCode");
		setBounds(100, 100, 677, 530);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		buttonAdd = new JButton("org.multipage.generator.textAddSourceAreas");
		buttonAdd.setMargin(new Insets(0, 0, 0, 0));
		buttonAdd.setPreferredSize(new Dimension(100, 25));
		buttonAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onAddSources();
			}
		});
		getContentPane().add(buttonAdd);
		
		labelSourceOverview = new JLabel("org.multipage.generatorSourceOverview");
		springLayout.putConstraint(SpringLayout.WEST, labelSourceOverview, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(labelSourceOverview);
		
		buttonCreate = new JButton("org.multipage.generatorCreateAreas");
		buttonCreate.setMargin(new Insets(0, 0, 0, 0));
		buttonCreate.setPreferredSize(new Dimension(100, 25));
		springLayout.putConstraint(SpringLayout.EAST, buttonAdd, 0, SpringLayout.EAST, buttonCreate);
		springLayout.putConstraint(SpringLayout.SOUTH, buttonCreate, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, buttonCreate, -10, SpringLayout.EAST, getContentPane());
		buttonCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onCreateAreas();
			}
		});
		getContentPane().add(buttonCreate);
		
		splitPane = new JSplitPane();
		splitPane.setLastDividerLocation(-1);
		splitPane.setResizeWeight(0.5);
		springLayout.putConstraint(SpringLayout.NORTH, splitPane, 39, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, splitPane, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, splitPane, -10, SpringLayout.NORTH, buttonCreate);
		springLayout.putConstraint(SpringLayout.EAST, splitPane, -10, SpringLayout.EAST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, buttonAdd, -6, SpringLayout.NORTH, splitPane);
		getContentPane().add(splitPane);
		
		scrollPane = new JScrollPane();
		springLayout.putConstraint(SpringLayout.SOUTH, labelSourceOverview, -3, SpringLayout.NORTH, scrollPane);
		splitPane.setLeftComponent(scrollPane);
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 39, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, -300, SpringLayout.EAST, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, getContentPane());
		
		tree = new JTree();
		tree.setRootVisible(false);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				onTreeNodeSelected(e);
			}
		});
		scrollPane.setViewportView(tree);
		
		popupMenuTree = new JPopupMenu();
		addPopup(tree, popupMenuTree);
		
		menuRemove = new JMenuItem("org.multipage.generator.menuRemoveSourceCodeArea");
		menuRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRemoveSelected();
			}
		});
		popupMenuTree.add(menuRemove);
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -6, SpringLayout.NORTH, buttonCreate);
		
		checkFilesToAreas = new JCheckBox("org.multipage.generator.textFilesToAreas");
		springLayout.putConstraint(SpringLayout.SOUTH, checkFilesToAreas, -4, SpringLayout.SOUTH, buttonCreate);
		checkFilesToAreas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCheckFilesToAreas();
			}
		});
		springLayout.putConstraint(SpringLayout.WEST, checkFilesToAreas, 0, SpringLayout.WEST, labelSourceOverview);
		getContentPane().add(checkFilesToAreas);
		
		labelAreaSlot = new JLabel("org.multipage.generator.textAreaSlotName");
		springLayout.putConstraint(SpringLayout.WEST, labelAreaSlot, 16, SpringLayout.EAST, checkFilesToAreas);
		springLayout.putConstraint(SpringLayout.SOUTH, labelAreaSlot, -19, SpringLayout.SOUTH, getContentPane());
		getContentPane().add(labelAreaSlot);
		
		textSlotName = new JTextField();
		textSlotName.setText("file");
		springLayout.putConstraint(SpringLayout.NORTH, textSlotName, -3, SpringLayout.NORTH, labelAreaSlot);
		springLayout.putConstraint(SpringLayout.WEST, textSlotName, 3, SpringLayout.EAST, labelAreaSlot);
		getContentPane().add(textSlotName);
		textSlotName.setColumns(15);
	}

	/**
	 * On cancel.
	 */
	protected void onCancel() {
		try {
			
			saveDialog();
			onClose();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
		dispose();
	}
	
	/**
	 * Update states of GUI controls.
	 */
	private void updateControlsState() {
		try {
			
			// Get selection. 
			boolean filesToAreas = checkFilesToAreas.isSelected();
			
			// Select edit box for the user to enter a slot name.
			textSlotName.setEnabled(filesToAreas);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On check box that determines if files should create areas or area slots
	 */
	protected void onCheckFilesToAreas() {
		try {
			
			// Update GUI state.
			updateControlsState();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On create areas.
	 */
	protected void onCreateAreas() {
		
		try {
			ProgramBasic.loginMiddle();
			importAreasFrom(root);
		}
		catch (Exception e) {
			Utility.show2(this, e.getLocalizedMessage());
		}
		finally {
			ProgramBasic.logoutMiddle();
		}
		try {
			
			saveDialog();
			onClose();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
		
		dispose();
	}

	/**
	 * On "add sources" event.
	 */
	protected void onAddSources() {
		try {
			
			// Select directory.
			String source = Utility.chooseDirectory2(this, "org.multipage.generator.textSelectSourceFileOrDirectory");
			
			// Get "files to areas" flag from check box control.
			boolean filesToAreas = checkFilesToAreas.isSelected();
			
			// Get created slot name.
			String fileSlotName = null;
			if (filesToAreas) {
				fileSlotName = textSlotName.getText();
			}
			
			// Create tree nodes.
			final String encoding = "UTF-8";
			try {
				File rootFile = new File(source);
				Area rootArea = new Area(rootFile, fileSlotName, encoding);
				
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(rootArea);
				root.add(node);
				
				processSourceFiles(node, fileSlotName, encoding);
			}
			catch (Exception e) {
			}
			
			treeModel.reload(root);
			Utility.expandAll(tree, true);
			tree.addSelectionRow(0);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On remove selected nodes.
	 */
	protected void onRemoveSelected() {
		try {
			
			// Get selected node.
			TreePath selectedPath = tree.getSelectionPath();
			if (selectedPath != null) {
				
				// Remove selected node from its parent.
			    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
			    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedNode.getParent();
			    if (parent != null) {
			    	
			    	Object userObject = selectedNode.getUserObject();
			    	if (!(userObject instanceof Area)) {
			    		Utility.show(this, "org.multipage.generator.messageUnexpectedTreeNodeType");
                        return;
                    }
			    	Area selectedArea = (Area) userObject;
			    	String areaDescription = selectedArea.getDescriptionText();
			    	
			    	boolean confirmed = Utility.ask(this, "org.multipage.generator.messageRemoveSelectedAreaSource", areaDescription);
			    	if (confirmed) {
			    		treeModel.removeNodeFromParent(selectedNode);
			    	}
			    }
			}
			else {
				Utility.show(this, "org.multipage.generator.messageCannotRemoveTreeRootNode");
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Add new source file or directory
	 * @param node
	 * @param fileSlotName 
	 * @param encoding
	 */
	private void processSourceFiles(DefaultMutableTreeNode node, String fileSlotName, String encoding)
			throws Exception {
		
		// Get area and associated file object.
		Area area = (Area) node.getUserObject();
		
		File file = area.getFile();
		if (file == null) {
			return;
		}
		
		// Process possible child files.
		if (file.isDirectory()) {
			for (File childFile : file.listFiles()) {
				
				boolean isChildDirectory = childFile.isDirectory();
				boolean createChildNodes = (isChildDirectory || fileSlotName != null);
				DefaultMutableTreeNode childNode = null;
				
				// Create child node.
				if (createChildNodes) {
					
					Area childArea = new Area(childFile, fileSlotName, encoding);
					childNode = new DefaultMutableTreeNode(childArea);
					node.add(childNode);
				}
				// Add new file provider to the current area.
				else {
					area.addFileProvider(childFile, fileSlotName, encoding);
				}
				
				// Call recursively this method for a child node.
				if (childNode != null) {
					processSourceFiles(childNode, fileSlotName, encoding);
				}
			}
		}
	}
	
	/**
	 * Imports areas from a node
	 * @param node
	 * @throws Exception
	 */
	private void importAreasFrom(DefaultMutableTreeNode node)
			throws Exception {
		
		Area area = (Area) node.getUserObject();
		Enumeration<TreeNode> children = node.children();
		
		if (root.equals(node)) {
			area = this.area;
		}
		
		while (children.hasMoreElements()) {
			
			Object next = children.nextElement();
			if (next == null) {
				break;
			}
			
			DefaultMutableTreeNode childnode = (DefaultMutableTreeNode) next;
			Area subarea = (Area) childnode.getUserObject();
			
			if (area != null && subarea != null) {
				ProgramBasic.insert(area, subarea);
				LinkedList<Slot> slots = subarea.getSlots();
				ProgramBasic.insert(subarea, slots);
			}
			
			importAreasFrom(childnode);
		}
	}

	/**
	 * Post create
	 */
	private void postCreate() {
		try {
			
			initTree();
			initSlotsPanel();
			localize();
			setIcons();
			
			loadDialog();
			updateControlsState();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Initialize tree
	 */
	private void initTree() {
		try {
			
			root = new DefaultMutableTreeNode();
			treeModel = new DefaultTreeModel(root);
			
			tree.setModel(treeModel);
			
			// Set cell renderer.
			DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
			Icon icon = Images.getIcon("org/multipage/generator/images/area_node.png");
			renderer.setClosedIcon(icon);
			renderer.setOpenIcon(icon);
			renderer.setLeafIcon(icon);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Initialize slots panel.
	 */
	private void initSlotsPanel() {
		try {
			
			slotListPanel = new SlotListPanel();
			slotListPanel.setUseDatabase(false);
			splitPane.setRightComponent(slotListPanel);
			slotListPanel.hideHelpPanel();
			slotListPanel.doNotEditSlots();
			slotListPanel.setTableColumnWidths(new Integer [] { 400, 50 });
			slotListPanel.doNotPreserveColumns();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Localize components
	 */
	private void localize() {
		try {
			
			String title = Resources.getString(getTitle());
			setTitle(title);
			Utility.localize(buttonAdd);
			Utility.localize(labelSourceOverview);
			Utility.localize(buttonCreate);
			Utility.localize(checkFilesToAreas);
			Utility.localize(labelAreaSlot);
			Utility.localize(menuRemove);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set component icons.
	 */
	private void setIcons() {
		try {
			
			buttonAdd.setIcon(Images.getIcon("org/multipage/generator/images/area_node.png"));
			menuRemove.setIcon(Images.getIcon("org/multipage/gui/images/cancel_icon.png"));
			buttonCreate.setIcon(Images.getIcon("org/multipage/gui/images/ok_icon.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * When tree node has been selected.
	 * @param e
	 */
	protected void onTreeNodeSelected(TreeSelectionEvent e) {
		try {
			
			TreePath path = e.getPath();
			if (path == null) {
				return;
			}
			
			Object component = path.getLastPathComponent();
			if (!(component instanceof DefaultMutableTreeNode)) {
				return;
			}
			
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) component;
			Object userObject = node.getUserObject();
			if (!(userObject instanceof Area)) {
				return;
			}
			
			Area area = (Area) userObject;
			slotListPanel.setArea(area);
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
			
			if (!bounds.isEmpty()) {
				setBounds(bounds);
			}
			else {
				Utility.centerOnScreen(this);
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
	 * Called when the dialog window is closing.
	 */
	private void onClose() {
		try {
			
			if (slotListPanel != null) {
				slotListPanel.onClose();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
