/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package program.builder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.maclan.Area;
import org.maclan.ConstructorGroup;
import org.maclan.ConstructorGroupRef;
import org.maclan.ConstructorHolder;
import org.maclan.ConstructorSubObject;
import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.multipage.basic.ProgramBasic;
import org.multipage.generator.EditorTabActions;
import org.multipage.generator.ProgramGenerator;
import org.multipage.gui.GraphUtility;
import org.multipage.gui.IdentifierTreePath;
import org.multipage.gui.Images;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.ToolBarKit;
import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Editor panel that displays information about constructors of areas.
 * @author vakol
 *
 */
public class ConstructorsPanel extends JPanel implements EditorTabActions {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Panel state.
	 */
	private static int mainDividerLocation;

	/**
	 * Expanded constructor tree paths.
	 */
	private static IdentifierTreePath [] expandedConstructorTreePaths;
	
	/**
	 * Link cursor.
	 */
	private static Cursor linkCursor;
	
	/**
	 * Static constructor.
	 */
	static {
		try {
			
			// Load cursor.
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Image image = Images.getImage("org/multipage/generator/images/group_reference_cursor.png");
			linkCursor = toolkit.createCustomCursor(image , new Point(4, 4), "cursorConstrGroupRef");
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set default data.
	 */
	public static void setDefaultData() {
		
		mainDividerLocation = -1;
		expandedConstructorTreePaths = new IdentifierTreePath [0];
	}

	/**
	 * Load states.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void serializeData(StateInputStream inputStream)
			throws IOException, ClassNotFoundException {
		
		mainDividerLocation = inputStream.readInt();
		expandedConstructorTreePaths = Utility.readInputStreamObject(inputStream, IdentifierTreePath [].class);
	}

	/**
	 * Save states.
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream)
			throws IOException {
		
		outputStream.writeInt(mainDividerLocation);
		outputStream.writeObject(expandedConstructorTreePaths);
	}

	/**
	 * Area reference.
	 */
	private Area area;
	
	/**
	 * Constructor group object.
	 */
	private ConstructorGroup constructorGroup;

	/**
	 * Constructor tree model.
	 */
	private ConstructorTreeModel constructorTreeModel;

	/**
	 * Constructor properties panel.
	 */
	private ConstructorPropertiesPanel constructorPropertiesPanel;

	/**
	 * Group reference button.
	 */
	private JToggleButton buttonGroupReference;

	/**
	 * Paste constructor button.
	 */
	private JButton buttonPasteConstructorHolder;
	
	/**
	 * Constructor group panel.
	 */
	private ConstructorGroupPanel constructorGroupPanel;
	
	/**
	 * Empty panel.
	 */
	private JPanel emptyPanel = new JPanel();

	/**
	 * Current constructor holder.
	 */
	private ConstructorHolder currentConstructorHolder;

	/**
	 * Copied constructor holder.
	 */
	private ConstructorHolder copiedConstructorHolder;
	
	/**
	 * Copied tree item.
	 */
	private Object copiedTreeItem;
	
	/**
	 * Expanded tree paths set flag.
	 */
	private boolean isExpandedConstructorTreePathsSet = false;
	
	/**
	 * Previous panel.
	 */
	private JPanel previousPanel = null;

	/**
	 * A parent group for linked constructor.
	 */
	private ConstructorGroup constructorLinkParentGroup;

	// $hide<<$
	/**
	 * Components.
	 */
	private JSplitPane splitPaneMain;
	private JPanel panel;
	private JLabel labelConstructorTree;
	private JToolBar toolBarTree;
	private JScrollPane scrollPaneTree;
	private JTree tree;
	private JPopupMenu popupMenu;
	private JMenuItem menuAddNode;
	private JMenuItem menuEditConstructor;
	private JMenuItem menuRemove;
	private JMenuItem menuCopyConstructorItem;
	private JMenuItem menuPasteConstructorHolder;
	private JMenuItem menuCopyTree;
	private JMenuItem menuMoveTree;
	private JMenuItem menuPasteTree;

	/**
	 * Create the panel.
	 */
	public ConstructorsPanel() {
		
		try {
			initComponents();
			postCreate(); // $hide$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Initialize components.
	 */
	private void initComponents() {
		setLayout(new BorderLayout(0, 0));
		
		splitPaneMain = new JSplitPane();
		splitPaneMain.setOneTouchExpandable(true);
		add(splitPaneMain, BorderLayout.CENTER);
		
		panel = new JPanel();
		splitPaneMain.setLeftComponent(panel);
		SpringLayout sl_panel = new SpringLayout();
		panel.setLayout(sl_panel);
		
		labelConstructorTree = new JLabel("builder.textConstructorTree");
		sl_panel.putConstraint(SpringLayout.NORTH, labelConstructorTree, 0, SpringLayout.NORTH, panel);
		sl_panel.putConstraint(SpringLayout.WEST, labelConstructorTree, 0, SpringLayout.WEST, panel);
		panel.add(labelConstructorTree);
		
		toolBarTree = new JToolBar();
		sl_panel.putConstraint(SpringLayout.WEST, toolBarTree, 0, SpringLayout.WEST, labelConstructorTree);
		sl_panel.putConstraint(SpringLayout.SOUTH, toolBarTree, 0, SpringLayout.SOUTH, panel);
		toolBarTree.setFloatable(false);
		panel.add(toolBarTree);
		
		scrollPaneTree = new JScrollPane();
		sl_panel.putConstraint(SpringLayout.NORTH, scrollPaneTree, 0, SpringLayout.SOUTH, labelConstructorTree);
		sl_panel.putConstraint(SpringLayout.WEST, scrollPaneTree, 0, SpringLayout.WEST, labelConstructorTree);
		sl_panel.putConstraint(SpringLayout.SOUTH, scrollPaneTree, 0, SpringLayout.NORTH, toolBarTree);
		sl_panel.putConstraint(SpringLayout.EAST, scrollPaneTree, 0, SpringLayout.EAST, panel);
		panel.add(scrollPaneTree);
		
		tree = new JTree();
		scrollPaneTree.setViewportView(tree);
		
		popupMenu = new JPopupMenu();
		addPopup(tree, popupMenu);
		
		menuAddNode = new JMenuItem("builder.menuAddConstructorTreeItem");
		menuAddNode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onAddNode();
			}
		});
		popupMenu.add(menuAddNode);
		
		menuEditConstructor = new JMenuItem("builder.menuEditConstructor");
		menuEditConstructor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onEditConstructorName();
			}
		});
		popupMenu.add(menuEditConstructor);
		
		menuRemove = new JMenuItem("builder.menuRemoveConstructorTreeItem");
		menuRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRemove();
			}
		});
		popupMenu.add(menuRemove);
		
		menuCopyConstructorItem = new JMenuItem("builder.menuCopyConstrcutorItem");
		menuCopyConstructorItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onCopy();
			}
		});
		popupMenu.add(menuCopyConstructorItem);
		
		menuPasteConstructorHolder = new JMenuItem("builder.menuPasteConstructor");
		menuPasteConstructorHolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onPaste();
			}
		});
		popupMenu.add(menuPasteConstructorHolder);
		splitPaneMain.setDividerLocation(250);
		
		popupMenu.addSeparator();
		
		menuCopyTree = new JMenuItem("builder.menuCopyConstructorTree");
		menuCopyTree.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onCopyTree();
			}
		});
		popupMenu.add(menuCopyTree);
		
		menuMoveTree = new JMenuItem("builder.menuMoveConstructorTree");
		menuMoveTree.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onMoveTree();
			}
		});
		popupMenu.add(menuMoveTree);
		
		menuPasteTree = new JMenuItem("builder.menuPasteConstructorTree");
		menuPasteTree.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onPasteTree();
			}
		});
		popupMenu.add(menuPasteTree);
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			localize();
			setIcons();
			
			initializeTree();
			createTreeToolbar();
			
			updateCopyConstructorState(null);
			updateCopyTreeState();
			
			createPanels();
			splitPaneMain.setRightComponent(emptyPanel);
			
			mapKeys();
	
			loadDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Map keys to actions.
	 */
	@SuppressWarnings("serial")
	private void mapKeys() {
		try {
			
			getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");
			getActionMap().put("escape", new AbstractAction() {
	
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						
						resetReferenceCreation();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create tree tool bar.
	 */
	private void createTreeToolbar() {
		try {
			
			ToolBarKit.addToolBarButton(toolBarTree, "org/multipage/generator/images/add_item_icon.png",
					"builder.tooltipAddNewConstructorOrConstructorGroup", () -> onAddNode());
			
			buttonGroupReference = ToolBarKit.addToggleButton(toolBarTree, "org/multipage/generator/images/group_reference_icon.png",
					"builder.tooltipCreateGroupReference", () -> onGroupReference());
			
			ToolBarKit.addToolBarButton(toolBarTree, "org/multipage/generator/images/edit.png",
					"builder.tooltipEditConstructorName", () -> onEditConstructorName());
			
			ToolBarKit.addToolBarButton(toolBarTree, "org/multipage/generator/images/remove_icon.png",
					"builder.tooltipRemoveConstructorTreeItem", () -> onRemove());
			
			ToolBarKit.addToolBarButton(toolBarTree, "org/multipage/generator/images/update_icon.png",
					"builder.tooltipReloadConstructors", () -> onReload());
	
			ToolBarKit.addToolBarButton(toolBarTree, "org/multipage/gui/images/copy_icon.png",
					"builder.tooltipCopyConstructorItem", () -> onCopy());
			
			buttonPasteConstructorHolder = ToolBarKit.addToolBarButton(toolBarTree, "org/multipage/gui/images/paste_icon.png",
					"builder.tooltipPasteConstructor", () -> onPaste());
			
			ToolBarKit.addToolBarButton(toolBarTree, "org/multipage/generator/images/expand_icon.png",
					"org.multipage.generator.tooltipExpandTree", () -> onExpandTree());
			
			ToolBarKit.addToolBarButton(toolBarTree, "org/multipage/generator/images/collapse_icon.png",
					"org.multipage.generator.tooltipCollapseTree", () -> onCollapseTree());
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
			
			Utility.localize(labelConstructorTree);
			Utility.localize(menuAddNode);
			Utility.localize(menuEditConstructor);
			Utility.localize(menuRemove);
			Utility.localize(menuCopyConstructorItem);
			Utility.localize(menuPasteConstructorHolder);
			Utility.localize(menuCopyTree);
			Utility.localize(menuMoveTree);
			Utility.localize(menuPasteTree);
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
			
			menuAddNode.setIcon(Images.getIcon("org/multipage/generator/images/add_item_icon.png"));
			menuEditConstructor.setIcon(Images.getIcon("org/multipage/generator/images/edit.png"));
			menuRemove.setIcon(Images.getIcon("org/multipage/generator/images/remove_icon.png"));
			menuCopyConstructorItem.setIcon(Images.getIcon("org/multipage/gui/images/copy_icon.png"));
			menuPasteConstructorHolder.setIcon(Images.getIcon("org/multipage/gui/images/paste_icon.png"));
			menuCopyTree.setIcon(Images.getIcon("org/multipage/gui/images/copy_icon.png"));
			menuMoveTree.setIcon(Images.getIcon("org/multipage/generator/images/move_icon.png"));
			menuPasteTree.setIcon(Images.getIcon("org/multipage/gui/images/paste_icon.png"));
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
			
			if (mainDividerLocation != -1) {
				splitPaneMain.setDividerLocation(mainDividerLocation);
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
			
			mainDividerLocation = splitPaneMain.getDividerLocation();
			
			if (isExpandedConstructorTreePathsSet ) {
				expandedConstructorTreePaths = Utility.getExpandedPaths2(tree);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Initialize tree.
	 */
	private void initializeTree() {
		try {
			
			// Create new constructor group.
			constructorGroup = new ConstructorGroup(-1);
			// Create constructor tree model.
			constructorTreeModel = new ConstructorTreeModel(constructorGroup);
			// Set tree model.
			tree.setModel(constructorTreeModel);
			
			tree.setRowHeight(0);
			
			// Set tree cell renderer.
			tree.setCellRenderer(new TreeCellRenderer() {
				
				// Tree cell renderer.
				@SuppressWarnings("serial")
				class Renderer extends JLabel {
	
					// States.
					private boolean selected;
					private boolean hasFocus;
	
					// Paint label.
					@Override
					public void paint(Graphics g) {
						
						try {
							super.paint(g);
							GraphUtility.drawSelection(g, this, selected, hasFocus);
						}
						catch (Throwable e) {
							Safe.exception(e);
						}
					}
					
					// Clear label.
					public void clear() {
						try {
							
							setIcon(null);
							selected = false;
							hasFocus = false;
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
	
					// Set label.
					public void set(Object value, boolean selected, boolean hasFocus) {
						try {
							
							Object selectedTreeObject = tree.getLastSelectedPathComponent();
							
							ConstructorGroup selectedConstructorGroup = null;
							if (selectedTreeObject instanceof ConstructorSubObject) {
								
								ConstructorSubObject selectedSubObject = (ConstructorSubObject) selectedTreeObject;
								selectedConstructorGroup = selectedSubObject.getConstructorGroup();
							}
		
							// Set icon.
							if (value instanceof ConstructorGroup) {
								setIcon(Images.getIcon("org/multipage/generator/images/group_icon.png"));
								setText(value.toString());
								
								// Set text color.
								setForeground(value == selectedConstructorGroup ?
										Color.RED : (constructorTreeModel.isEnabled() ? Color.BLACK : Color.GRAY));
							}
							else if (value instanceof ConstructorHolder) {
								
								ConstructorHolder constructorHolder = (ConstructorHolder) value;
								
								boolean isLink = constructorHolder.isLinkId();
								
								setIcon(Images.getIcon(!isLink ? "org/multipage/generator/images/constructor.png"
										: "program/builder/images/constructor_link.png"));
								
								String name = "";
								if (!isLink) {
									name = constructorHolder.getPathLastName();
								}
								else {
									ConstructorHolder linkedConstructorHolder = constructorHolder.getLinkedConstructorHolder();
									if (linkedConstructorHolder != null) {
										name = linkedConstructorHolder.getPathLastName();
									}
								}
								
								setText("<html><b>" + name + "</b></html>");
								setForeground(!constructorTreeModel.isEnabled() || constructorHolder.isInvisible() ? Color.GRAY : Color.BLACK);
							}
							else if (value instanceof ConstructorGroupRef) {
								setIcon(Images.getIcon("org/multipage/generator/images/group_reference.png"));
								setText(value.toString());
								
								// Set text color.
								setForeground(((ConstructorGroupRef) value).ref == selectedConstructorGroup ?
										Color.RED : (constructorTreeModel.isEnabled() ? Color.BLACK : Color.GRAY));
							}
							
							this.selected = selected;
							this.hasFocus = hasFocus;
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				}
				
				// Renderer object.
				Renderer renderer = new Renderer();
				
				@Override
				public Component getTreeCellRendererComponent(JTree tree, Object value,
	                    boolean selected, boolean expanded,
	                    boolean leaf, int row, boolean hasFocus) {
					
					if (value == null) {
						renderer.clear();
					}
					else {
						renderer.set(value, selected, hasFocus);
					}
					
					return renderer;
				}
			});
			
			// Set tree selection listener.
			tree.addTreeSelectionListener(new TreeSelectionListener() {
				@Override
				public void valueChanged(TreeSelectionEvent e) {
					try {
						
						onTreeSelection();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Save reference to group.
	 * @param constructorHolder
	 * @param referencedGroup
	 */
	private void saveReferenceToGroup(ConstructorHolder constructorHolder, ConstructorGroup referencedGroup) {
		try {
			
			// Prepare prerequisites.
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			
			MiddleResult result = middle.login(login);
			if (result.isOK()) {
				
				// Get constructor holder sub object.
				ConstructorSubObject subObject = constructorHolder.getSubObject();
			
				// Set constructor holder sub group.
				result = middle.updateConstructorHolderSubReference(
						constructorHolder.getId(), referencedGroup.getId());
				if (result.isOK()) {
					
					// If there is a subtree with current constructor holder as a root,
					// remove it.
					if (subObject != null) {
						result = middle.removeConstructorObjectWithSubTree(subObject);
					}
				}
				
				MiddleResult logoutResult = middle.logout(result);
				if (result.isOK()) {
					result = logoutResult;
				}
			}
			
			if (result.isNotOK()) {
				result.show(this);
			}
			
			// Get tree path.
			TreePath path = constructorTreeModel.getPath(constructorHolder);
			
			resetReferenceCreation();
			
			// Update tree.
			if (path != null) {
				tree.expandPath(path);
			}
			// Load new constructor tree.
			loadConstructors();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create a link to constructor.
	 * @param parentGroup
	 * @param linkedConstructorHolder
	 */
	private void saveLinkToContructor(ConstructorGroup parentGroup,
			ConstructorHolder linkedConstructorHolder) {
		try {
			
			// Prepare prerequisites.
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			
			// Save new link.
			MiddleResult result = middle.login(login);
			if (result.isOK()) {
				
				// Create constructor holder link.
				ConstructorHolder constructorHolderLink = linkedConstructorHolder.createLink(parentGroup);
				result = middle.insertConstructorHolder(constructorHolderLink);
				
				MiddleResult logoutResult = middle.logout(result);
				if (result.isOK()) {
					result = logoutResult;
				}
			}
	
			// On error inform user.
			if (result.isNotOK()) {
				
				resetReferenceCreation();
				result.show(this);
				return;
			}
			
			// Reset reference creation procedure and load new constructor tree.
			resetReferenceCreation();
			loadConstructors();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On tree selection.
	 * @param e
	 */
	protected void onTreeSelection() {
		try {
			
			// Stop editing.
			constructorPropertiesPanel.stopEditing();
			
			Object selectedTreeObject = tree.getLastSelectedPathComponent();
			
			// If a reference to selected group should be set, do it.
			if (buttonGroupReference.isSelected() && (selectedTreeObject instanceof ConstructorGroup)
					&& currentConstructorHolder != null) {
				
				ConstructorGroup selectedGroup = (ConstructorGroup) selectedTreeObject;
				saveReferenceToGroup(currentConstructorHolder, selectedGroup);
			}
			
			// If a link to constructor should be set, do it.
			if (constructorLinkParentGroup != null && (selectedTreeObject instanceof ConstructorHolder)) {
				
				ConstructorHolder linkedConstructorHolder = (ConstructorHolder) selectedTreeObject;
				
				saveLinkToContructor(constructorLinkParentGroup, linkedConstructorHolder);
			}
			
			tree.repaint();
			
			// Save previous panel data.
			savePreviousPanelData();
	
			// If the selected tree item is a constructor holder, display constructor properties editor.
			int dividerlocation = splitPaneMain.getDividerLocation();
			if (selectedTreeObject instanceof ConstructorHolder) {
				
				constructorPropertiesPanel.setConstructorHolder((ConstructorHolder) selectedTreeObject);
				splitPaneMain.setRightComponent(constructorPropertiesPanel);
				
				previousPanel = constructorPropertiesPanel;
			}
			// If it i a constructor group or a group reference...
			else if (selectedTreeObject instanceof ConstructorSubObject){
				
				constructorGroupPanel.setConstructorGroup(
						((ConstructorSubObject) selectedTreeObject).getConstructorGroup());
				splitPaneMain.setRightComponent(constructorGroupPanel);
				
				previousPanel = constructorGroupPanel;
			}
			// Otherwise set empty panel.
			else {
				splitPaneMain.setRightComponent(emptyPanel);
				
				previousPanel = emptyPanel;
			}
			splitPaneMain.setDividerLocation(dividerlocation);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Save previous panel data.
	 */
	private void savePreviousPanelData() {
		try {
			
			// Save constructor holder.
			if (previousPanel == constructorPropertiesPanel) {
				constructorPropertiesPanel.saveConstructorHolder();
			}
			else if (previousPanel == constructorGroupPanel) {
				constructorGroupPanel.saveConstructorGroup();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Update copy state.
	 * @param constructorHolder
	 */
	private void updateCopyConstructorState(ConstructorHolder constructorHolder) {
		try {
			
			copiedConstructorHolder = constructorHolder;
	
			boolean enable = copiedConstructorHolder != null;
			menuPasteConstructorHolder.setEnabled(enable);
			buttonPasteConstructorHolder.setEnabled(enable);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Update copy tree state.
	 */
	private void updateCopyTreeState() {
		try {
			
			menuMoveTree.setEnabled(copiedTreeItem != null);
			menuPasteTree.setEnabled(copiedTreeItem != null);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On copy tree.
	 */
	protected void onCopyTree() {
		try {
			
			resetReferenceCreation();
			
			// Get selected object.
			copiedTreeItem = tree.getLastSelectedPathComponent();
					
			// Remember selected constructor.
			updateCopyTreeState();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On move tree.
	 */
	protected void onMoveTree() {
		try {
			
			// Get selected item.
			Object selectedItem = tree.getLastSelectedPathComponent();
			if (selectedItem == null) {
				
				Utility.show(this, "builder.messageSelectConstructorTreeItem");
				return;
			}
			
			// If the selected tree has to be moved, check if it can be moved.
			if (!canMoveTree(copiedTreeItem, selectedItem)) {
				
				Utility.show(this, "builder.messageCannotMoveTreeToSelectedLocation");
				return;
			}
			
			// Prepare prerequisites.
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			MiddleResult result = MiddleResult.OK;
			
			// Selected item must be a group or reference.
			if (selectedItem instanceof ConstructorHolder) {
				Utility.show(this, "builder.textCannotMoveConstructorToConstructor");
				return;
			}
			
			// Get group ID.
			long selectedGroupId = ((ConstructorSubObject) selectedItem).getConstructorGroup().getId();
			
			// Remove tree item.
			if (copiedTreeItem instanceof ConstructorHolder) {
	
				// Moved constructor ID.
				long movedConstructorHolderId = ((ConstructorHolder) copiedTreeItem).getId();
				
				// Move constructor to group.
				result = middle.updateConstructorHolderGroupId(login, movedConstructorHolderId, selectedGroupId);
			}
			else if (copiedTreeItem instanceof ConstructorGroup) {
				
				 ConstructorGroup movedConstructorGroup = (ConstructorGroup) copiedTreeItem;
				 LinkedList<ConstructorHolder> movedConstructorHolders = movedConstructorGroup.getConstructorHolders();
				 
				 // Move constructor holders.
				 result = middle.login(login);
				 if (result.isOK()) {
					 
					 // Do loop for all moved constructor holders.
					 for (ConstructorHolder movedConstructorHolder : movedConstructorHolders) {
						 
						 result = middle.updateConstructorHolderGroupId(login, movedConstructorHolder.getId(), selectedGroupId);
						 if (result.isNotOK()) {
							 break;
						 }
					 }
					 
					 MiddleResult logoutResult = middle.logout(result);
					 if (result.isOK()) {
						 result = logoutResult;
					 }
				 }
			}
			
			// Inform user about an error.
			if (result.isNotOK()) {
				result.show(this);
			}
			
			loadConstructors();
			
			// Reset copied constructor.
			copiedTreeItem = null;
			updateCopyTreeState();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Returns true value if the tree can be moved.
	 * @param movedTree
	 * @param targetItem 
	 * @return
	 */
	private boolean canMoveTree(Object movedTree, Object targetItem) {
		
		try {
			// If the target item is inside the moved tree, return false.
			return !ConstructorGroup.treeContainsItem(movedTree, targetItem);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * On paste tree.
	 */
	protected void onPasteTree() {
		try {
			
			// Get selected item.
			Object selectedItem = tree.getLastSelectedPathComponent();
			if (selectedItem == null) {
				
				Utility.show(this, "builder.messageSelectConstructorTreeItem");
				return;
			}
	
			// Prepare prerequisites.
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			MiddleResult result = MiddleResult.OK;
			
			// Copy group to group.
			if (copiedTreeItem instanceof ConstructorGroup && selectedItem instanceof ConstructorGroup) {
				
				ConstructorGroup copiedConstructorGroup = (ConstructorGroup) copiedTreeItem;
				ConstructorGroup selectedConstructorGroup = (ConstructorGroup) selectedItem;
				
				// Connect to database.
				result = middle.login(login);
				if (result.isOK()) {
					
					// Do loop for all copied constructors.
					for (ConstructorHolder copiedConstructorHolder : copiedConstructorGroup.getConstructorHolders()) {
						
						// Clone tree.
						copiedConstructorHolder = copiedConstructorHolder.cloneTree();
						copiedConstructorHolder.setParentConstructorGroup(selectedConstructorGroup);
						
						// Insert cloned tree.
						result = middle.insertConstructorHolderSubTree(copiedConstructorHolder);
					}
					
					// Logout from database.
					MiddleResult logoutResult = middle.logout(result);
					if (result.isOK()) {
						result = logoutResult;
					}
				}
			}
			// Copy constructor to group.
			else if (copiedTreeItem instanceof ConstructorHolder && selectedItem instanceof ConstructorGroup) {
				
				ConstructorHolder copiedConstructorHolder = ((ConstructorHolder) copiedTreeItem).cloneTree();
				ConstructorGroup selectedConstructorGroup = (ConstructorGroup) selectedItem;
	
				// Connect root constructor holder to selected group.
				copiedConstructorHolder.setParentConstructorGroup(selectedConstructorGroup);
				// Insert sub tree.
				result = middle.insertConstructorHolderSubTree(login, copiedConstructorHolder);
	
			}
			else {
				// Inform user.
				Utility.show(this, "builder.messageCannotPasteConstructorTreeIntoConstructor");
				// Reset copied constructor.
				updateCopyConstructorState(null);
				return;
			}
			
			// Inform user about an error.
			if (result.isNotOK()) {
				result.show(this);
			}
			
			loadConstructors();
			
			// Reset copied constructor.
			copiedTreeItem = null;
			updateCopyTreeState();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Reset reference creation.
	 */
	private void resetReferenceCreation() {
		try {
			
			final Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
			tree.setCursor(cursor);
			
			if (buttonGroupReference.isSelected()) {
				Safe.invokeLater(() -> {
					
					buttonGroupReference.setSelected(false);
				});
			}
			
			currentConstructorHolder = null;
			constructorLinkParentGroup = null;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Update tree.
	 */
	private void updateTree() {
		try {
			
			// Update model.
			constructorTreeModel.update();
			
			// Call tree selection.
			onTreeSelection();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Create panels.
	 */
	private void createPanels() {
		try {
			
			constructorPropertiesPanel = new ConstructorPropertiesPanel(() -> {
				try {
					
					// Update tree UI.
					tree.updateUI();
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};		
			}, constructorGroup);
			
			constructorGroupPanel = new ConstructorGroupPanel();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On load panel information.
	 */
	@Override
	public void onLoadPanelInformation() {
		try {
			
			// Load constructors.
			loadConstructors();
			
			// Set expanded paths.
			Utility.setExpandedPaths(tree, expandedConstructorTreePaths);
			
			isExpandedConstructorTreePathsSet = true;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On save panel information.
	 */
	@Override
	public void onSavePanelInformation() {
		try {
			
			// Save information.
			save();
			
			// Save states.
			constructorPropertiesPanel.saveDialog();
			saveDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Save data.
	 */
	public void save() {
		try {
			
			// Stop editing.
			constructorPropertiesPanel.stopEditing();
			
			// Save data.
			savePreviousPanelData();
			
			// Save constructors.
			saveConstructors();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Load constructors.
	 */
	private void loadConstructors() {
		try {
			
			if (area == null) {
				return;
			}
			
			// Get expanded paths and selected paths.
			IdentifierTreePath[] expandedPaths = Utility.getExpandedPaths2(tree);
			IdentifierTreePath[] selectedPaths = Utility.getSelectedPaths(tree);
			
			// Reload area object.
			area = ProgramGenerator.getArea(area.getId());
	
			// Prepare prerequisites.
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			
			long areaId = area.getId();
			
			// Login to the database.
			MiddleResult result = middle.login(login);
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			// Get area constructor ID.
			Obj<Long> constructorGroupId = new Obj<Long>();
			result = middle.loadAreaConstructorGroupId(login, areaId, constructorGroupId);
			if (result.isOK()) {
				if (constructorGroupId.ref != null) {
				
					// Load constructors.
					result = middle.loadConstructorTree(login, area.getId(), constructorGroup);
					if (result.isOK()) {
						
						constructorGroup.makeConstructorLinks();
					}
				}
				else {
					// Clear constructors tree.
					constructorGroup.clearConstructorHolders();
					constructorGroup.setId(-1);
				}
			}
			
			// Logout from the database.
			MiddleResult logoutResult = middle.logout(result);
			if (result.isOK()) {
				result = logoutResult;
			}
			
			// On error inform user.
			if (result.isNotOK()) {
				result.show(this);
			}
			
			// Clear constructor tree change flags.
			constructorGroup.clearTreeChanged();
			
			// Update tree.
			updateTree();
			
			// Set old expanded paths and selection path.
			Utility.setExpandedPaths(tree, expandedPaths);
			Utility.setSelectedPaths(tree, selectedPaths);
			
			// Update information.
			updateInformation();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	private void updateInformation() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Save constructors.
	 */
	private void saveConstructors() {

	}
	
	/**
	 * Set area.
	 * @param area
	 */
	public void setArea(Area area) {
		try {
			
			this.area = area;
			
			constructorPropertiesPanel.setRootArea(area);
			constructorGroupPanel.setRootArea(area);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Add popup trayMenu.
	 * @param component
	 * @param popup
	 */
	private static void addPopup(Component component, final JPopupMenu popup) {
		try {
			
			component.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					try {
						
						if (e.isPopupTrigger()) {
							showMenu(e);
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				public void mouseReleased(MouseEvent e) {
					try {
						
						if (e.isPopupTrigger()) {
							showMenu(e);
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				private void showMenu(MouseEvent e) {
					try {
						
						popup.show(e.getComponent(), e.getX(), e.getY());
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Add node.
	 */
	public void onAddNode() {
		try {
			
			save();
	
			resetReferenceCreation();
			
			// Get selected item.
			Object selectedObject = tree.getLastSelectedPathComponent();
			if (selectedObject == null) {
				Utility.show(this, "builder.messagePleaseSelectTreeNode");
				return;
			}
			if (selectedObject instanceof ConstructorGroupRef) {
				Utility.show(this, "builder.messageCannotAddGroupReferenceSubItem");
				return;
			}
			
			if (selectedObject instanceof ConstructorGroup) {
				addConstructorHolder((ConstructorGroup) selectedObject);
			}
			else if (selectedObject instanceof ConstructorHolder) {
				addConstructorGroup((ConstructorHolder) selectedObject);
			}
	
			// Update tree and expand selected path.
			TreePath treePath = constructorTreeModel.getPath(selectedObject);
			if (treePath != null) {
				tree.expandPath(treePath);
			}
			
			// Load new constructor tree.
			loadConstructors();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get constructor holder default name.
	 * @param constructorGroup
	 */
	private String getConstructorHolderDefaultName(
			ConstructorGroup constructorGroup) {
		
		try {
			String fullName = "";
			String name = Resources.getString("builder.textDefaultConstructorName");
					
			int index = 1;
			
			while (index < Integer.MAX_VALUE) {
				
				fullName = name + index;
				
				if (!constructorGroup.existsConstructorHolderName(fullName)) {
					break;
				}
				
				index++;
			}
			
			return fullName;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Add constructor holder to constructor group.
	 * @param parentConstructorGroup
	 */
	private void addConstructorHolder(ConstructorGroup parentConstructorGroup) {
		try {
			
			String defaultName = getConstructorHolderDefaultName(parentConstructorGroup);
			
			// Ask constructor holder.
			Obj<Boolean> isLink = new Obj<Boolean>();
			Obj<String> nameRef = new Obj<String>();
			
			if (!AskConstructorHolder.showDialog(this, isLink, nameRef, defaultName)) {
				return;
			}
			
			// If user has selected a link, start link procedure.
			if (isLink.ref) {
				
				startConstructorLinkProcedure(parentConstructorGroup);
				return;
			}
			
			String name = nameRef.ref;
			
			// If the constructor holder name already exists, exit the method.
			if (parentConstructorGroup.existsConstructorHolderName(name)) {
				Utility.show(this, "builder.messageConstructorNameAlreadyExists", name);
				return;
			}
					
			// Prepare prerequisites.
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			
			MiddleResult result = middle.login(login);
			if (result.isOK()) {
			
				// If the group is not in the database, create it and append it to the area.
				if (parentConstructorGroup.getId() == -1) {
					
					// Insert group and attach it to the area.
					result = middle.insertConstructorGroupOrphan(parentConstructorGroup);
					if (result.isOK()) {
					
						result = middle.updateAreaConstructorGroup(area.getId(), parentConstructorGroup.getId());
					}
				}
		
				if (result.isOK()) {
					
					ConstructorHolder constructorHolder = new ConstructorHolder(name);
					parentConstructorGroup.addConstructorHolder(constructorHolder);
			
					
					// Insert constructor holder.
					result = middle.insertConstructorHolder(constructorHolder);
				}
			}
			
			MiddleResult logoutResult = middle.logout(result);
			if (result.isOK()) {
				result = logoutResult;
			}
			
			if (result.isNotOK()) {
				result.show(this);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Start constructor link procedure.
	 * @param parentConstructorGroup
	 */
	private void startConstructorLinkProcedure(ConstructorGroup parentConstructorGroup) {
		try {
			
			// Remember parent group and set cursor.
			constructorLinkParentGroup = parentConstructorGroup;
			tree.setCursor(linkCursor);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Add constructor group to constructor holder.
	 * @param parentConstructor
	 */
	private void addConstructorGroup(ConstructorHolder parentConstructorHolder) {
		try {
			
			// If the sub object already exists, let user confirm the change.
			if (parentConstructorHolder.getSubObject() != null) {
				if (!Utility.askParam(this, "builder.messageConstructorAlreadyHasSubObjectOvewrite",
						parentConstructorHolder.getName())) {
					return;
				}
			}
			
			// Create new constructor group.
			ConstructorGroup newConstructorGroup = new ConstructorGroup();
			// Set it as constructor holder sub group.
			parentConstructorHolder.setSubConstructorGroup(newConstructorGroup);
			
			// Save constructor group.
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			
			MiddleResult result = middle.insertConstructorGroup(login, newConstructorGroup);
			if (result.isNotOK()) {
				result.show(this);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On collapse all.
	 */
	public void onCollapseTree() {
		try {
			
			Utility.expandAll(tree, false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On copy constructor.
	 */
	public void onCopy() {
		try {
			
			resetReferenceCreation();
			
			// Get selected object.
			Object selectedObject = tree.getLastSelectedPathComponent();
			if (!(selectedObject instanceof ConstructorHolder)) {
				Utility.show(this, "builder.messageSelectConstructor");
				return;
			}
			
			ConstructorHolder selectedConstructorHolder = (ConstructorHolder) selectedObject;
			
			// Remember selected constructor.
			updateCopyConstructorState(selectedConstructorHolder);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On edit constructor name.
	 */
	public void onEditConstructorName() {
		try {
			
			resetReferenceCreation();
			
			// Get selected object.
			Object selectedObject = tree.getLastSelectedPathComponent();
			if (!(selectedObject instanceof ConstructorHolder)) {
				
				resetReferenceCreation();
				Utility.show(this, "builder.messageSelectConstructor");
				return;
			}
			
			ConstructorHolder constructorHolder = (ConstructorHolder) selectedObject;
			
			// Get new name.
			String newName = Utility.input(this, "builder.messageInsertConstructorName",
					constructorHolder.getName());
			if (newName == null) {
				return;
			}
			
			// Check constructor holder name.
			boolean exists = false;
			for (ConstructorHolder constructorHolderItem : 
				(LinkedList<ConstructorHolder>) constructorHolder.getParentConstructorGroup().getConstructorHolders().clone()) {
				
				if (constructorHolderItem != constructorHolder && constructorHolderItem.getName().equals(newName)) {
					exists = true;
					break;
				}
			}
			
			if (exists) {
				
				// Inform user and exit.
				Utility.show(this, "builder.messageConstructorNameAlreadyExists", newName);
				return;
			}
			
			// Set new name.
			constructorHolder.setName(newName);
			
			updateTree();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On expand all.
	 */
	public void onExpandTree() {
		try {
			
			Utility.expandAll(tree, true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On group reference.
	 */
	public void onGroupReference() {
		try {
			
			boolean isButtonSelected = buttonGroupReference.isSelected();
			if (!isButtonSelected) {
				
				resetReferenceCreation();
				return;
			}
			
			// Get selected object.
			Object selectedObject = tree.getLastSelectedPathComponent();
			if (selectedObject == null) {
				
				resetReferenceCreation();
				Utility.show(this, "builder.messagePleaseSelectTreeNode");
				return;
			}
			
			// If the selected item is not a constructor holder, inform user and reset button.
			if (!(selectedObject instanceof ConstructorHolder)) {
				
				resetReferenceCreation();
				Utility.show(this, "builder.messageSelectConstructor");
				return;
			}
			
			// Get constructor holder.
			ConstructorHolder constructorHolder = (ConstructorHolder) selectedObject;
			
			// If the sub object already exists, let user confirm the change.
			if (constructorHolder.getSubObject() != null) {
				if (!Utility.askParam(this, "builder.messageConstructorAlreadyHasSubObjectOvewrite",
						constructorHolder.getName())) {
					return;
				}
			}
			
			// Inform user.
			if (!Utility.showConfirm(this, "builder.messageNowSelectReferencedGroup")) {
				resetReferenceCreation();
				return;
			}
			
			// Set cursor.
			tree.setCursor(linkCursor);
			
			// Remember current constructor group.
			currentConstructorHolder = constructorHolder;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On paste constructor holder.
	 */
	protected void onPaste() {
		try {
			
			resetReferenceCreation();
			
			// If the copied constructor holder doesn't exist, exit method.
			if (copiedConstructorHolder == null) {
				Utility.show(this, "builder.messageNoConstrcutorToCopy");
				return;
			}
			
			// Get selected object.
			Object selectedObject = tree.getLastSelectedPathComponent();
			if (!(selectedObject instanceof ConstructorGroup)) {
				Utility.show(this, "builder.messageSelectConstructorGroup");
				return;
			}
			
			ConstructorGroup constructorGroup = (ConstructorGroup) selectedObject;
			
			// Check new constructor holder name.
			String name = copiedConstructorHolder.getName();
			while (constructorGroup.existsConstructorHolderName(name)) {
				
				String enteredName = Utility.input(this, "builder.messageConstructorNameAlreadyExistsEnterNew", name);
				if (enteredName == null) {
					return;
				}
				
				name = enteredName;
			}
			
			// Add constructor clone to the group.
			ConstructorHolder newConstructorHolder = copiedConstructorHolder.clone();
			newConstructorHolder.setParentConstructorGroup(null);
			newConstructorHolder.clearSubObject();
			newConstructorHolder.setName(name);
			
			constructorGroup.addConstructorHolder(newConstructorHolder);
			
			// Prepare prerequisites.
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			
			// Insert new constructor.
			MiddleResult result = middle.insertConstructorHolder(login, newConstructorHolder);
			if (result.isNotOK()) {
				result.show(this);
			}
			
			loadConstructors();
			
			// Expand path.
			TreePath treePath = constructorTreeModel.getPath(constructorGroup);
			if (treePath != null) {
				tree.expandPath(treePath);
			}
			
			// Reset copied constructor.
			updateCopyConstructorState(null);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On reload.
	 */
	public void onReload() {
		try {
			
			save();
			loadConstructors();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On remove node.
	 */
	public void onRemove() {
		try {
			
			save();
			resetReferenceCreation();
			
			// Get selected object.
			Object selectedObject = tree.getLastSelectedPathComponent();
			if (selectedObject == null) {
				Utility.show(this, "builder.messagePleaseSelectTreeNode");
				return;
			}
			
			// Prepare prerequisites.
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			MiddleResult result = MiddleResult.OK;
			
			ConstructorGroup rootGroup = (ConstructorGroup) constructorTreeModel.getRoot();
			
			// Initialize parent object.
			Object parentObject = rootGroup;
			
			// If it is single constructor in root tree, remove the whole tree.
			boolean removeWholeTree = false;
	
			// If it is a constructor holder.
			if (selectedObject instanceof ConstructorHolder) {
				
				ConstructorHolder constructorHolderToRemove = (ConstructorHolder) selectedObject;
				String constructorHolderName = constructorHolderToRemove.getName();
				
				// Ask user.
				String message = String.format(
						Resources.getString("builder.messageConfirmRemoveConstructor"), constructorHolderName);
				if (!Utility.ask2(this, message)) {
					return;
				}
				
				// If the holder group is a root group...
				if (constructorHolderToRemove.getParentConstructorGroup().equals(constructorGroup)) {
					
					// ...and the root group contains only it, remove the whole tree.
					if (constructorGroup.getConstructorHolderCount() == 1) {
						removeWholeTree = true;
					}
				}
	
				// Remove holder with sub tree.
				if (!removeWholeTree) {
					result = middle.removeConstructorObjectWithSubTree(login, constructorHolderToRemove);
				}
			}
			// If it is a group.
			else if (selectedObject instanceof ConstructorGroup) {
				
				ConstructorGroup constructorGroupToRemove = (ConstructorGroup) selectedObject;
				ConstructorHolder parentConstructorHolder = constructorGroupToRemove.getParentConstructorHolder();
	
				if (parentConstructorHolder != null) {
					
					// Ask user.
					if (!Utility.ask(this, "builder.messageConfirmRemoveConstructorGroup")) {
						return;
					}
					
					// Remove group with sub tree.
					result = middle.login(login);
					if (result.isOK()) {
					
						result = middle.updateConstructorHolderSubGroupId(parentConstructorHolder.getId(), null);
						if (result.isOK()) {
						
							result = middle.removeConstructorObjectWithSubTree(constructorGroupToRemove);
						}
					}
					
					MiddleResult logoutResult = middle.logout(result);
					if (result.isOK()) {
						result = logoutResult;
					}
				}
				else {
					// Ask user.
					if (!Utility.ask(this, "builder.messageConfirmRemoveRootConstructorGroupConstructors")) {
						return;
					}
					
					removeWholeTree = true;
				}
			}
			// If it is a group reference.
			else if (selectedObject instanceof ConstructorGroupRef) {
				
				ConstructorGroupRef constructorGroupRef = (ConstructorGroupRef) selectedObject;
				ConstructorHolder parentConstructorHolder = constructorGroupRef.getParentConstructorHolder();
				if (parentConstructorHolder != null) {
					
					// Ask user.
					if (!Utility.ask(this, "builder.messageConfirmRemoveConstructorGroupReference")) {
						return;
					}
					
					// Remove constructor holder sub reference.
					result = middle.updateConstructorHolderSubGroupId(login, parentConstructorHolder.getId(), null);
					if (result.isNotOK()) {
						result.show(this);
					}
				}
			}
			
			// Remove the whole tree.
			if (removeWholeTree && result.isOK()) {
				
				result = middle.login(login);
				if (result.isOK()) {
					
					result = middle.updateAreaConstructorGroup(area.getId(), null);
					if (result.isOK()) {
						
						result = middle.removeConstructorObjectWithSubTree(constructorGroup);
					}
				}
				
				MiddleResult logoutResult = middle.logout(result);
				if (result.isOK()) {
					result = logoutResult;
				}
			}
			
			// Inform user about error.
			if (result.isNotOK()) {
				result.show(this);
			}
			
			// Load new constructor tree.
			loadConstructors();
			
			if (parentObject != null) {
				
				TreePath path = constructorTreeModel.getPathNoLeaf(parentObject);
				tree.expandPath(path);
			}
			
			tree.clearSelection();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On close the window.
	 */
	public void onClose() {
		try {
			
			constructorPropertiesPanel.onClose();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Update dialog components.
	 */
	public void updateComponents() {
		try {
			
			// TODO: <---MAKE Update components.
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
}

/**
 * Constructor tree model.
 * @author
 *
 */
class ConstructorTreeModel implements TreeModel {

	/**
	 * Constructor group.
	 */
	private ConstructorGroup constructorGroup;
	
	/**
	 * Enabled flag.
	 */
	private boolean enabled = true;

	/**
	 * Listeners.
	 */
	private LinkedList<TreeModelListener> listeners = new LinkedList<TreeModelListener>();

	/**
	 * Constructor.
	 * @param constructorGroup
	 */
	public ConstructorTreeModel(ConstructorGroup constructorGroup) {
		
		this.constructorGroup = constructorGroup;
	}

	/**
	 * Get object path.
	 * @param object
	 * @return
	 */
	public TreePath getPath(Object object) {
		
		try {
			LinkedList<Object> path = new LinkedList<Object>();
			
			// Find path.
			Object root = getRoot();
			if (root != null) {
				traverseTree(root, object, path);
			}
			
			if (path.isEmpty()) {
				return null;
			}
			
			// Convert to tree path.
			return new TreePath(path.toArray());
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Get path with removed leaf.
	 * @param object
	 * @return
	 */
	public TreePath getPathNoLeaf(Object object) {
		
		try {
			LinkedList<Object> path = new LinkedList<Object>();
			
			// Find path.
			Object root = getRoot();
			if (root != null) {
				traverseTree(root, object, path);
			}
			
			// If the last node is a leaf, remove it.
			Object last = path.getLast();
			if (last != null && isLeaf(last)) {
				path.removeLast();
			}
			
			if (path.isEmpty()) {
				path.add(constructorGroup);
			}
			
			// Convert to tree path.
			return new TreePath(path.toArray());
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Traverse tree.
	 * @param current
	 * @param searched
	 * @param path
	 * @return
	 */
	private boolean traverseTree(Object current, Object searched, LinkedList<Object> path) {
		
		try {
			// If found.
			if (current == searched) {
				path.addFirst(current);
				return true;
			}
			
			// Do loop for child objects.
			for (int index = 0; index < getChildCount(current); index++) {
				
				Object subObject = getChild(current, index);
				if (subObject == null) {
					continue;
				}
				
				// Call this method recursively.
				if (traverseTree(subObject, searched, path)) {
					path.addFirst(current);
					return true;
				}
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Add listener.
	 */
	@Override
	public void addTreeModelListener(TreeModelListener listener) {
		try {
			
			listeners.add(listener);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Remove listener.
	 */
	@Override
	public void removeTreeModelListener(TreeModelListener listener) {
		try {
			
			listeners.remove(listener);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Fire listeners.
	 */
	public void update() {
		try {
			
			Object [] path = {constructorGroup};
			TreeModelEvent event = new TreeModelEvent(this, path);
			
			for (TreeModelListener listener : listeners) {
				
				listener.treeStructureChanged(event);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		
	}
	
	/**
	 * Get child object.
	 */
	@Override
	public Object getChild(Object parent, int index) {
		
		try {
			if (parent instanceof ConstructorGroup) {
				
				// Get sub constructor.
				ConstructorGroup constructorGroup = (ConstructorGroup) parent;
				return constructorGroup.getConstructorHolder(index);
			}
			else if (parent instanceof ConstructorHolder) {
				
				// Get sub object.
				ConstructorHolder constructorHolder = (ConstructorHolder) parent;
				return constructorHolder.getSubObject();
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Get child count.
	 */
	@Override
	public int getChildCount(Object parent) {
		
		try {
			if (parent instanceof ConstructorGroup) {
				
				// Get constructor count.
				ConstructorGroup constructorGroup = (ConstructorGroup) parent;
				return constructorGroup.getConstructorHolderCount();
			}
			else if (parent instanceof ConstructorHolder) {
				
				// Get group count.
				ConstructorHolder constructorHolder = (ConstructorHolder) parent;
				return constructorHolder.getSubObject() != null ? 1 : 0;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0;
	}

	/**
	 * Get index of child.
	 */
	@Override
	public int getIndexOfChild(Object parent, Object child) {
		
		try {
			if (parent instanceof ConstructorGroup) {
				
				// Get constructor holder index.
				ConstructorGroup constructorGroup = (ConstructorGroup) parent;
				return constructorGroup.getConstructorHolderIndex(child);
			}
			else if (parent instanceof ConstructorHolder) {
				
				// Get sub object.
				ConstructorHolder constructorHolder = (ConstructorHolder) parent;
				return constructorHolder.getSubObject() == child ? 0 : -1;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return -1;
	}

	/**
	 * Get tree root.
	 */
	@Override
	public Object getRoot() {
		
		return constructorGroup;
	}

	/**
	 * Determine leaf object.
	 */
	@Override
	public boolean isLeaf(Object node) {
		
		try {
			if (node instanceof ConstructorGroup) {
				return constructorGroup.getConstructorHolderCount() == 0;
			}
			else if (node instanceof ConstructorHolder) {
				
				ConstructorHolder constructorHolder = (ConstructorHolder) node;
				return constructorHolder.getSubObject() == null;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return true;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}