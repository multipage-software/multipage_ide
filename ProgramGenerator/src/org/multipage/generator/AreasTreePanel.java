/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.function.Consumer;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.maclan.Area;
import org.maclan.AreaRelation;
import org.multipage.gui.IdentifierTreePath;
import org.multipage.gui.Images;
import org.multipage.gui.RendererJLabel;
import org.multipage.gui.ToolBarKit;
import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Panel that displays areas in tree view.
 * @author vakol
 *
 */
public class AreasTreePanel extends JPanel {
	
	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Expanded tree rows.
	 */
	private static IdentifierTreePath[] expandedPaths;

	/**
	 * Root area reference.
	 */
	private Area rootArea;

	/**
	 * Tree model.
	 */
	private DefaultTreeModel treeModel;
	
	/**
	 * Area listener.
	 */
	private Consumer<LinkedList<Area>> areasListener;
	
	/**
	 * Area with sub areas listener.
	 */
	private Consumer<Area> areaWithSubAreasListener;
	
	/**
	 * Purpose of diagram.
	 */
	private int localMenuHint;
	
	// $hide<<$
	/**
	 * Components.
	 */
	private JScrollPane scrollPane;
	private JTree tree;
	private JLabel labelAreasTree;
	private JToolBar toolBar;
	private JPopupMenu popupMenu;

	/**
	 * Create the panel.
	 */
	public AreasTreePanel(int localMenuHint) {
		
		try {
			initComponents();
			this.localMenuHint = localMenuHint;
			postCreate(); // $hide$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Constructor.
	 */
	public AreasTreePanel() {
		
		this(AreaLocalMenu.DIAGRAM);
	}

	/**
	 * Constructor.
	 * @param rootArea
	 */
	public AreasTreePanel(Area rootArea) {
		
		try {
			this.rootArea = rootArea;
	
			initComponents();
			postCreate(); // $hide$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Constructor.
	 * @param rootArea
	 * @param localMenuHint
	 */
	public AreasTreePanel(Area rootArea, int localMenuHint) {
		
		try {
			this.rootArea = rootArea;
			this.localMenuHint = localMenuHint;
	
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
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		scrollPane = new JScrollPane();
		scrollPane.setBorder(null);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, 0, SpringLayout.EAST, this);
		add(scrollPane);
		
		tree = new JTree();
		tree.addTreeExpansionListener(new TreeExpansionListener() {
			public void treeCollapsed(TreeExpansionEvent arg0) {
				onTreeExpandedCollapsed();
			}
			public void treeExpanded(TreeExpansionEvent arg0) {
				onTreeExpandedCollapsed();
			}
		});
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				onAreasSelected(e);
			}
		});
		scrollPane.setViewportView(tree);
		
		popupMenu = new JPopupMenu();
		addPopup(tree, popupMenu);
		
		labelAreasTree = new JLabel("org.multipage.generator.textAreasTree");
		springLayout.putConstraint(SpringLayout.NORTH, labelAreasTree, 0, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, labelAreasTree, 0, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 0, SpringLayout.SOUTH, labelAreasTree);
		add(labelAreasTree);
		
		toolBar = new JToolBar();
		springLayout.putConstraint(SpringLayout.WEST, toolBar, 0, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, 0, SpringLayout.NORTH, toolBar);
		springLayout.putConstraint(SpringLayout.SOUTH, toolBar, 0, SpringLayout.SOUTH, this);
		toolBar.setPreferredSize(new Dimension(13, 30));
		springLayout.putConstraint(SpringLayout.EAST, toolBar, -10, SpringLayout.EAST, this);
		toolBar.setFloatable(false);
		add(toolBar);
	}
	
	/**
	 * On tree expanded or collapsed.
	 */
	protected void onTreeExpandedCollapsed() {
		try {
			
			expandedPaths = Utility.getExpandedPaths2(tree);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On area selected.
	 * @param e 
	 */
	protected void onAreasSelected(TreeSelectionEvent e) {
		try {
			
			if (areasListener != null) {
				LinkedList<Area> areas = getSelectedAreas();
				
				if (areas != null) {
					areasListener.accept(areas);
				}
			}
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
			
			localize();
			
			createToolBar();
			
			addAreaPopupMenu();
			
			initializeTree();
			loadAreasTree();
			
			// Expand tree items.
			if (expandedPaths != null) {
				Utility.setExpandedPaths(tree, expandedPaths);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Select area with sub nodes.
	 */
	protected void selectNodeWithSubNodes() {
		try {
			
			// Get selected area.
			TreePath [] selectedPaths = tree.getSelectionPaths();
			if (selectedPaths.length != 1) {
				Utility.show(this, "org.multipage.generator.messageSelectSingleArea");
				return;
			}
			
			TreePath treePath = selectedPaths[0];
			LinkedList<TreePath> treePaths = new LinkedList<TreePath>();
			
			getSubPaths((DefaultMutableTreeNode) treePath.getLastPathComponent(), treePaths);
			
			// Select sub nodes.
			tree.setSelectionPaths(treePaths.toArray(new TreePath [0]));
			
			// Invoke listener.
			if (areaWithSubAreasListener != null) {
				Area area = getSelectedArea();
				
				if (area != null) {
					areaWithSubAreasListener.accept(area);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get sun paths.
	 * @param node
	 * @param treePaths
	 */
	private void getSubPaths(DefaultMutableTreeNode node,
			LinkedList<TreePath> treePaths) {
		try {
			
			// Add this node path.
			TreeNode [] nodePath = node.getPath();
			TreePath treePath = new TreePath(nodePath);
			treePaths.add(treePath);
			
			// Do loop for all sub nodes.		
			Enumeration<? super TreeNode> childNodes = node.children();
			while (childNodes.hasMoreElements()) {
				
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) childNodes.nextElement();
				
				// Call this method recursively.
				getSubPaths(childNode, treePaths);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Add area popup trayMenu.
	 */
	private void addAreaPopupMenu() {
		try {
			
			final Component thisComponent = this;
			
			AreaLocalMenu localMenu = ProgramGenerator.newAreaLocalMenu(new AreaLocalMenu.Callbacks() {
				@Override
				protected Area getCurrentArea() {
					try {
						return getSelectedArea();
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return null;
				}
	
				@Override
				public Component getComponent() {
					// Get this component.
					return thisComponent;
				}
	
			});
			
			localMenu.setHint(localMenuHint);
			
			JMenuItem menuSelectSubNodes = new JMenuItem(
					Resources.getString("org.multipage.generator.menuSelectSubNodes"));
			menuSelectSubNodes.setIcon(Images.getIcon("org/multipage/generator/images/select_subnodes.png"));
			menuSelectSubNodes.addActionListener(e -> {
				try {
			
					selectNodeWithSubNodes();
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			JMenuItem menuAddSubArea = new JMenuItem(
					Resources.getString("org.multipage.generator.menuAddSubArea"));
			menuAddSubArea.setIcon(Images.getIcon("org/multipage/generator/images/area_node.png"));
			menuAddSubArea.addActionListener(e -> {
				try {
			
					onAddSubArea();
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			JMenuItem menuRemoveArea = new JMenuItem(
					Resources.getString("org.multipage.generator.menuRemoveArea"));
			menuRemoveArea.setIcon(Images.getIcon("org/multipage/generator/images/cancel_icon.png"));
			menuRemoveArea.addActionListener(e -> {
				try {
			
					onRemoveArea();
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			int index = 0;
			popupMenu.insert(menuSelectSubNodes, index++);
			popupMenu.insert(menuAddSubArea, index++);
			popupMenu.insert(menuRemoveArea, index++);
			popupMenu.addSeparator(); index++;
			
			localMenu.addTo(this, popupMenu);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};		
	}
	
	/**
	 * On remove area.
	 */
	protected void onRemoveArea() {
		try {
			
			// Get selected area.
			TreePath [] selectedPaths = tree.getSelectionPaths();
			if (selectedPaths.length != 1) {
				Utility.show(this, "org.multipage.generator.messageSelectSingleArea");
				return;
			}
			
			// Get parent area.
			TreePath path = selectedPaths[0];
			int elementsCount = path.getPathCount();
			if (elementsCount < 2) {
				Utility.show(this, "org.multipage.generator.messageCannotRemoveRootArea");
				return;
			}
			
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) path.getPathComponent(elementsCount - 2);
			Area parentArea = (Area) parentNode.getUserObject();
			
			// Get selected area.
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			Area area = (Area) node.getUserObject();
			
			AreaShapes areaShapes = (AreaShapes) area.getUser();
			HashSet<AreaShapes> shapesSet = new HashSet<AreaShapes>();
			shapesSet.add(areaShapes);
			
			// Remove area.
			GeneratorMainFrame.getVisibleAreasDiagram().removeDiagramArea(shapesSet, parentArea, this);
	
			updateData();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On add sub area.
	 */
	public void onAddSubArea() {
		try {
			
			// Get selected area.
			TreePath [] selectedPaths = tree.getSelectionPaths();
			if (selectedPaths.length != 1) {
				Utility.show(this, "org.multipage.generator.messageSelectSingleArea");
				return;
			}
			
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			Area parentArea = (Area) node.getUserObject();
			
			// Add new area.
			Obj<Area> newArea = new Obj<Area>();
			if (GeneratorMainFrame.getVisibleAreasDiagram().addNewArea(parentArea, this, newArea, false)) {
			
				// Select and expand area item.
				if (newArea.ref != null) {
					
					updateData();
					AreaTreeState.addSelectionAndExpandIt(tree, selectedPaths);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Localize.
	 */
	private void localize() {
		try {
			
			Utility.localize(labelAreasTree);
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
			
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/expand_icon.png", this, "onExpandTree", "org.multipage.generator.tooltipExpandTree");
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/collapse_icon.png", this, "onCollapseTree", "org.multipage.generator.tooltipCollapseTree");
			if (ProgramGenerator.isExtensionToBuilder()) {
				ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/add_item_icon.png", this, "onAddSubArea", "org.multipage.generator.tooltipAddArea");
			}
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/edit.png", this, "onEdit", "org.multipage.generator.tooltipEditArea");
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/update_icon.png", this, "onUpdate", "org.multipage.generator.tooltipUpdateAreasTree");
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
			
			treeModel = new DefaultTreeModel(null);
			tree.setModel(treeModel);
			
			tree.setExpandsSelectedPaths(true);
			
			// Set model.
			treeModel = new DefaultTreeModel(null);
			tree.setModel(treeModel);
			
			// Set renderer.
			tree.setCellRenderer(new TreeCellRenderer() {
				
				private long homeAreaId = 0L;
				
				@SuppressWarnings("serial")
				RendererJLabel renderer = new RendererJLabel() {
					{
						try {
							homeAreaId = ProgramGenerator.getHomeArea().getId();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				};
				
				@Override
				public Component getTreeCellRendererComponent(JTree tree, Object value,
						boolean selected, boolean expanded, boolean leaf, int row,
						boolean hasFocus) {
					
					try {
						if (!(value instanceof DefaultMutableTreeNode)) {
							renderer.setText("***error***");
							renderer.set(selected, hasFocus, row);
							return renderer;
						}
						
						renderer.setIcon(Images.getIcon("org/multipage/generator/images/area_node.png"));
						
						Object object = ((DefaultMutableTreeNode) value).getUserObject();
						if (object instanceof Area) {
							 Area area = (Area) object;
							 
							 // Set home area icon.
							 if (area.getId() == homeAreaId) {
								 renderer.setIcon(Images.getIcon("org/multipage/generator/images/home_icon_small.png"));
							 }
						}
		
						renderer.setText(object instanceof Area ? ((Area) object).getDescriptionForDiagram() : object.toString());
						renderer.set(selected, hasFocus, row);
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
	 * Load areas tree.
	 */
	private void loadAreasTree() {
		try {
			
			// Get root area.
			if (rootArea == null) {
				rootArea = ProgramGenerator.getArea(0L);
			}
					
			// Get area tree state.
			AreaTreeState treeState = AreaTreeState.getTreeState(tree);
			
			// Create root node.
			DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootArea);
			
			// Create nodes.
			createNodes(rootNode);
	
			// Set root node.
			treeModel.setRoot(rootNode);
			
			// Apply area tree state.
			AreaTreeState.applyTreeState(treeState, tree);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create nodes.
	 * @param parentNode
	 * @param inheritance 
	 */
	private void createNodes(DefaultMutableTreeNode parentNode) {
		try {
			
			Object userObject = parentNode.getUserObject();
			if (!(userObject instanceof Area)) {
				return;
			}
			
			Area area = (Area) userObject;
			
			boolean isGenerator = !ProgramGenerator.isExtensionToBuilder();
			
			// Do loop for all sub areas.
			for (Area areaItem : area.getSubareas()) {
				
				// Create new node.
				DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(areaItem);
				// Add it to the parent node.
				parentNode.add(childNode);
				
				// If it is Generator skip hidden sub areas.
				if (isGenerator) {
					AreaRelation relation = area.getSubRelation(areaItem.getId());
					if (relation == null) {
						continue;
					}
					if (relation.isHideSub()) {
						continue;
					}
				}
				
				// Call this method recursively.
				createNodes(childNode);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get selected area.
	 * @return
	 */
	public Area getSelectedArea() {
		
		try {
			LinkedList<Area> areas = getSelectedAreas();
			if (areas.isEmpty()) {
				return null;
			}
			Area area = areas.getFirst();
			return area;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Get selected areas.
	 * @return
	 */
	public LinkedList<Area> getSelectedAreas() {
		
		try {
			// Get selected paths.
			TreePath [] paths = tree.getSelectionPaths();
			if (paths == null) {
				return null;
			}
			
			// Get areas.
			LinkedList<Area> areas = new LinkedList<Area>();
			for (TreePath path : paths) {
				
				// Get last path item.
				Object component = path.getLastPathComponent();
				if (!(component instanceof DefaultMutableTreeNode)) {
					continue;
				}
				
				Object object = ((DefaultMutableTreeNode) component).getUserObject();
				if (object instanceof Area) {
					Area area = (Area) object;
					areas.add(area);
				}
			}
		
			return areas;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	
	/**
	 * On expand all.
	 */
	public void onExpandTree() {
		try {
			
			Utility.expandSelected(tree, true);
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
			
			Utility.expandSelected(tree, false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Select tree area.
	 * @param area
	 */
	public void selectArea(Area area) {
		try {
			
			if (treeModel == null) {
				return;
			}
			
			// Get root node.
			Object rootObject = treeModel.getRoot();
			if (!(rootObject instanceof DefaultMutableTreeNode)) {
				return;
			}
			
			DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) rootObject;
			
			// Traverse tree breadth first.
			Enumeration<? extends TreeNode> enumeration = rootNode.breadthFirstEnumeration();
			while (enumeration.hasMoreElements()) {
				
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();
				Object userObject = node.getUserObject();
				
				if (userObject instanceof Area) {
					Area areaItem = (Area) userObject;
					
					if (areaItem.equals(area)) {
						
						// Select tree node.
						TreeNode [] nodePath = node.getPath();
						TreePath selectionPath = new TreePath(nodePath);
						tree.setSelectionPath(selectionPath);
						
						return;
					}
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Update data.
	 */
	private void updateData() {
		try {
			
			if (rootArea != null) {
				rootArea = ProgramGenerator.getArea(rootArea.getId());
			}
			
			Safe.invokeLater(() -> {
				loadAreasTree();
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On update.
	 */
	public void onUpdate() {
		try {
			
			updateData();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On add area.
	 */
	public void onAddArea() {
		try {
			
			// Get selected area.
			Area area = getSelectedArea();
			if (area == null) {
				
				Utility.show(this, "org.multipage.generator.messageSelectSingleArea");
				return;
			}
			
			// Get areas diagram and add new area.
			AreaDiagramPanel diagram = GeneratorMainFrame.getFrame().getVisibleAreasEditor().getDiagram();
			
			Area newArea = new Area();
			diagram.addNewAreaConservatively(area, newArea, this);
			
			updateData();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On edit area.
	 */
	public void onEdit() {
		try {
			
			// Get selected area.
			Area area = getSelectedArea();
			if (area == null) {
				
				Utility.show(this, "org.multipage.generator.messageSelectSingleArea");
				return;
			}
			
			// Execute area editor.
			AreaEditorFrame.showDialog(null, area);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Show trayMenu.
	 * @param e
	 * @param popup 
	 */
	protected void showMenu(MouseEvent e, JPopupMenu popup) {
		try {
			
			popup.show(e.getComponent(), e.getX(), e.getY());
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
	private void addPopup(Component component, final JPopupMenu popup) {
		try {
			
			component.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					try {
			
						if (e.isPopupTrigger()) {
							showMenu(e, popup);
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				public void mouseReleased(MouseEvent e) {
					try {
			
						if (e.isPopupTrigger()) {
							showMenu(e, popup);
						}
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
	 * Area selection listener.
	 */
	public void setSelectionListener(Consumer<LinkedList<Area>> areasListener) {
		
		this.areasListener = areasListener;
	}
	
	/**
	 * Area with sub areas selection listener.
	 */
	public void setSelectionWithSubListener(Consumer<Area> areaListener) {
		
		this.areaWithSubAreasListener = areaListener;
	}
}
