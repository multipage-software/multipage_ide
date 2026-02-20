/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2020-04-06
 *
 */

package org.multipage.generator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.maclan.Area;
import org.maclan.AreasModel;
import org.multipage.gui.ApplicationEvents;
import org.multipage.gui.DefaultMutableTreeNodeDnD;
import org.multipage.gui.GraphUtility;
import org.multipage.gui.GuiSignal;
import org.multipage.gui.Images;
import org.multipage.gui.JTreeDnD;
import org.multipage.gui.JTreeDndCallback;
import org.multipage.gui.Message;
import org.multipage.gui.PreventEventEchos;
import org.multipage.gui.ToolBarKit;
import org.multipage.gui.UpdatableComponent;
import org.multipage.gui.Utility;
import org.multipage.util.Closable;
import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Panel that displays areas in a tree view.
 * @author vakol
 *
 */
public class AreasTreeEditorPanel extends JPanel implements TabItemInterface, PreventEventEchos, UpdatableComponent, Closable {
	
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * States.
	 */
	private static boolean loadSuperAreasState = false;
	private static boolean loadAliasesState = false;
	private static int selectedTabIndexState = 0;
	private static boolean inheritState = false;
	private static boolean showIdsState = false;
	private static boolean caseSensitiveState = false;
	private static boolean wholeWordsState = false;
	private static boolean exactMatchState = false;
	private static String filterState = "";
	private static String levelsState = "";
	private static Rectangle bounds = new Rectangle();
		
	/**
	 * List renderer.
	 * @author
	 *
	 */
	@SuppressWarnings("serial")
	class ItemRendererImpl extends JLabel {

		/**
		 * Item states.
		 */
		private boolean isSelected;
		private boolean cellHasFocus;
		private boolean isVisible = false;
		
		/**
		 * Constructor.
		 */
		ItemRendererImpl() {
			try {
			
				setOpaque(true);
			}
			catch(Throwable expt) {
				Safe.exception(expt);
			};
		}
		
		/**
		 * Set item properties.
		 * @param text
		 * @param textColor
		 * @param subName
		 * @param superName
		 * @param hiddenSubareas
		 * @param index
		 * @param isSelected
		 * @param cellHasFocus
		 * @param isHomeArea
		 */
		public void setProperties(String text, Color textColor, String subName, String superName, boolean hiddenSubareas, int index,
				boolean isSelected, boolean cellHasFocus, boolean isHomeArea) {
			try {
				
				setIcon(Images.getIcon(
						isHomeArea ? (isVisible ? "org/multipage/generator/images/home_icon_small.png"
								: "org/multipage/generator/images/home_icon_small_unvisible.png")
								: (isVisible ? "org/multipage/generator/images/area_node.png"
										: "org/multipage/generator/images/area_node_unvisible.png")));
				
				String theText = text;
				
				if (theText.isEmpty()) {
					theText = Resources.getString("org.multipage.generator.textUnknownAlias");
					setForeground(Color.LIGHT_GRAY);
				}
				else {
					setForeground(Color.BLACK);
				}
				
				//String outputText = "<b>" + text + "</b>";
				String outputText = theText;
				String outputTextAddition = "";
				
				final boolean isBuilder = ProgramGenerator.isExtensionToBuilder();
				
				if (!subName.isEmpty()) {
					outputTextAddition += " <sup>↓</sup> <font color=gray>" + subName + "</font>";
				}
				if (!superName.isEmpty() && isBuilder) {
					outputTextAddition += " <sup>↑</sup> <font color=gray>" + superName + "</font>";
				}
				if (hiddenSubareas && isBuilder) {
					String textAux = Resources.getString("org.multipage.generator.textHasMoreInfo");
					outputTextAddition += String.format(" <font color=\"red\", style=\"font-size: 70%%\">%s</font>",
							textAux);
				}
				setText(String.format("<html>%s&nbsp;&nbsp;%s</html>", outputText, outputTextAddition));
				setForeground(textColor);
				setBackground(Utility.itemColor(index));
				this.isSelected = isSelected;
				this.cellHasFocus = cellHasFocus;
			}
			catch(Throwable expt) {
				Safe.exception(expt);
			};
		}
		
		/**
		 * Sets item properties.
		 * @param text
		 * @param colorText
		 * @param index
		 * @param isSelected
		 * @param cellHasFocus
		 * @param isHomeArea
		 */
		public void setProperties(String text, Color colorText, int index,
				boolean isSelected, boolean cellHasFocus, boolean isHomeArea) {
			try {
			
				setIcon(Images.getIcon(isHomeArea ? "org/multipage/generator/images/home_icon_small.png" : "org/multipage/generator/images/area_node.png"));
				
				String theText = text;
				
				if (theText.isEmpty()) {
					theText = Resources.getString("org.multipage.generator.textUnknownAlias");
					setForeground(Color.LIGHT_GRAY);
				}
				else {
					setForeground(Color.BLACK);
				}
				setText(theText);
				setForeground(colorText);
				setBackground(Utility.itemColor(index));
				this.isSelected = isSelected;
				this.cellHasFocus = cellHasFocus;
			}
			catch(Throwable expt) {
				Safe.exception(expt);
			};
		}
		
		/**
		 * Draw the node.
		 */
		@Override
		public void paint(Graphics g) {
			try {
			
				super.paint(g);
				GraphUtility.drawSelection(g, this, isSelected, cellHasFocus);
			}
			catch(Throwable expt) {
				Safe.exception(expt);
			};
		}

		/**
		 * Set area visibility.
		 * @param isVisible
		 */
		public void setAreaVisible(boolean isVisible) {
			
			this.isVisible = isVisible;
		}
	}
	
	/**
	 * List of selected area IDs.
	 */
	private HashSet<Long> selectedTreeAreaIds = new HashSet<Long>();
	private HashSet<Long> selectedListAreaIds = new HashSet<Long>();
	
	/**
	 * Area ID.
	 */
	private long areaId;
	
	/**
	 * List of previous update messages.
	 */
	private LinkedList<Message> previousUpdateMessages = new LinkedList<Message>();

	/**
	 * List renderer.
	 */
	private ItemRendererImpl itemRenderer;

	/**
	 * List model.
	 */
	private DefaultListModel<? super Object> listModel;
	
	/**
	 * Tree model.
	 */
	private DefaultTreeModel treeModel;
	
	/**
	 * Toggle button for sub areas and super areas.
	 */
	private JToggleButton buttonSuperAreas;
	
	/**
	 * Toggle areas' descriptions and aliases.
	 */
	private JToggleButton buttonAliases;
	
	/**
	 * Button show ID.
	 */
	private JToggleButton buttonShowIds;
	
	/**
	 * A reference to the tab label
	 */
	private TabLabel tabLabel;
	
	/**
	 * Inheritance for super areas.
	 */
	private JTreeDnD tree;
	private JList<? super Object> list;
	private JPopupMenu popupMenuTree;
	private JPopupMenu popupMenuList;
	private JCheckBox checkInherits = new JCheckBox("org.multipage.generator.textInherits");
	private JTabbedPane tabbedPane;
	private JPanel panelTree;
	private JPanel panelList;
	private JLabel labelFilter;
	private JTextField textFilter;
	private JCheckBox checkCaseSensitive;
	private JCheckBox checkWholeWords;
	private JCheckBox checkExactMatch;
	private JScrollPane scrollList;
	private JScrollPane scrollTree;
	private JLabel labelLevels;
	private JTextField textLevels;
	private JLabel labelFoundAreasCount;
	private JToolBar toolBarTree;
	private JMenuItem menuSelectSubNodes;
	private JMenuItem menuAddSubArea;
	private JMenuItem menuRemoveArea;
	
	/**
	 * Create the frame.
	 * @param areaId 
	 */
	public AreasTreeEditorPanel(long areaId) {
		try {
			setMinimumSize(new Dimension(400, 350));
	
			// Initialize components.
			initComponents();
			// $hide>>$
			this.areaId = areaId;
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

		setBounds(100, 100, 693, 540);
		setLayout(new BorderLayout(0, 0));
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				onTreeListTabChange();
			}
		});
		add(tabbedPane);
		
		panelTree = new JPanel();
		tabbedPane.addTab("tree", null, panelTree, null);
		SpringLayout sl_panelTree = new SpringLayout();
		panelTree.setLayout(sl_panelTree);
		
		scrollTree = new JScrollPane();
		scrollTree.setBorder(null);
		sl_panelTree.putConstraint(SpringLayout.NORTH, scrollTree, 0, SpringLayout.NORTH, panelTree);
		sl_panelTree.putConstraint(SpringLayout.WEST, scrollTree, 0, SpringLayout.WEST, panelTree);
		sl_panelTree.putConstraint(SpringLayout.EAST, scrollTree, 0, SpringLayout.EAST, panelTree);
		panelTree.add(scrollTree);
		
		tree = new JTreeDnD();
		tree.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {	
					Safe.tryOnChange(tree, () -> {
						onTreeEscapeKey();
					});
				}
			}
		});
		scrollTree.setViewportView(tree);
		
		popupMenuTree = new JPopupMenu();
		addPopup(tree, popupMenuTree);
		
		menuSelectSubNodes = new JMenuItem("org.multipage.generator.menuSelectSubNodes");
		menuSelectSubNodes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectNodeWithSubNodes();
			}
		});
		popupMenuTree.add(menuSelectSubNodes);
		
		menuAddSubArea = new JMenuItem("org.multipage.generator.menuAddSubArea");
		menuAddSubArea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onAddSubArea();
			}
		});
		popupMenuTree.add(menuAddSubArea);
		
		menuRemoveArea = new JMenuItem("org.multipage.generator.menuRemoveArea");
		menuRemoveArea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRemoveArea();
			}
		});
		popupMenuTree.add(menuRemoveArea);
		
		popupMenuTree.addSeparator();
		
		toolBarTree = new JToolBar();
		sl_panelTree.putConstraint(SpringLayout.SOUTH, scrollTree, 0, SpringLayout.NORTH, toolBarTree);
		sl_panelTree.putConstraint(SpringLayout.WEST, toolBarTree, 0, SpringLayout.WEST, panelTree);
		sl_panelTree.putConstraint(SpringLayout.SOUTH, toolBarTree, 0, SpringLayout.SOUTH, panelTree);
		toolBarTree.setFloatable(false);
		panelTree.add(toolBarTree);
		
		panelList = new JPanel();
		panelList.setBackground(Color.WHITE);
		tabbedPane.addTab("org.multipage.generator.textSearchAreas", null, panelList, null);
		SpringLayout sl_panelList = new SpringLayout();
		panelList.setLayout(sl_panelList);
		
		labelFilter = new JLabel("org.multipage.generator.textFilter");
		sl_panelList.putConstraint(SpringLayout.NORTH, labelFilter, 10, SpringLayout.NORTH, panelList);
		sl_panelList.putConstraint(SpringLayout.WEST, labelFilter, 10, SpringLayout.WEST, panelList);
		panelList.add(labelFilter);
		
		textFilter = new JTextField();
		sl_panelList.putConstraint(SpringLayout.NORTH, textFilter, 8, SpringLayout.NORTH, panelList);
		sl_panelList.putConstraint(SpringLayout.WEST, textFilter, 6, SpringLayout.EAST, labelFilter);
		sl_panelList.putConstraint(SpringLayout.EAST, textFilter, 197, SpringLayout.EAST, labelFilter);
		panelList.add(textFilter);
		textFilter.setColumns(10);
		
		checkCaseSensitive = new JCheckBox("org.multipage.generator.textCaseSensitive");
		sl_panelList.putConstraint(SpringLayout.NORTH, checkCaseSensitive, 7, SpringLayout.NORTH, panelList);
		checkCaseSensitive.setBackground(Color.WHITE);
		checkCaseSensitive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCaseSensitive();
			}
		});
		panelList.add(checkCaseSensitive);
		
		checkWholeWords = new JCheckBox("org.multipage.generator.textWholeWords");
		checkWholeWords.setBackground(Color.WHITE);
		sl_panelList.putConstraint(SpringLayout.NORTH, checkWholeWords, 0, SpringLayout.NORTH, checkCaseSensitive);
		checkWholeWords.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onWholeWords();
			}
		});
		sl_panelList.putConstraint(SpringLayout.WEST, checkWholeWords, 6, SpringLayout.EAST, checkCaseSensitive);
		panelList.add(checkWholeWords);
		
		checkExactMatch = new JCheckBox("org.multipage.generator.textExactMatch");
		checkExactMatch.setBackground(Color.WHITE);
		sl_panelList.putConstraint(SpringLayout.NORTH, checkExactMatch, 0, SpringLayout.NORTH, checkCaseSensitive);
		checkExactMatch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onExactMatch();
			}
		});
		sl_panelList.putConstraint(SpringLayout.WEST, checkExactMatch, 6, SpringLayout.EAST, checkWholeWords);
		panelList.add(checkExactMatch);
		
		scrollList = new JScrollPane();
		scrollList.setBorder(null);
		sl_panelList.putConstraint(SpringLayout.NORTH, scrollList, 0, SpringLayout.SOUTH, checkCaseSensitive);
		sl_panelList.putConstraint(SpringLayout.WEST, scrollList, 0, SpringLayout.WEST, panelList);
		sl_panelList.putConstraint(SpringLayout.EAST, scrollList, 0, SpringLayout.EAST, panelList);
		panelList.add(scrollList);
		
		list = new JList<>();
		list.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {	
					Safe.tryOnChange(list, () -> {
						onListEscapeKey();
					});
				}
			}
		});
		scrollList.setViewportView(list);
		
		popupMenuList = new JPopupMenu();
		addPopup(list, popupMenuList);
		
		labelLevels = new JLabel("org.multipage.generator.textLevels");
		sl_panelList.putConstraint(SpringLayout.NORTH, labelLevels, 0, SpringLayout.NORTH, labelFilter);
		sl_panelList.putConstraint(SpringLayout.WEST, labelLevels, 6, SpringLayout.EAST, textFilter);
		panelList.add(labelLevels);
		
		textLevels = new JTextField();
		sl_panelList.putConstraint(SpringLayout.NORTH, textLevels, 0, SpringLayout.NORTH, textFilter);
		sl_panelList.putConstraint(SpringLayout.WEST, checkCaseSensitive, 30, SpringLayout.EAST, textLevels);
		sl_panelList.putConstraint(SpringLayout.WEST, textLevels, 6, SpringLayout.EAST, labelLevels);
		sl_panelList.putConstraint(SpringLayout.EAST, textLevels, 51, SpringLayout.EAST, labelLevels);
		textLevels.setColumns(10);
		panelList.add(textLevels);
		
		labelFoundAreasCount = new JLabel("org.multipage.generator.textFoundAreasCount");
		sl_panelList.putConstraint(SpringLayout.SOUTH, scrollList, 0, SpringLayout.NORTH, labelFoundAreasCount);
		sl_panelList.putConstraint(SpringLayout.WEST, labelFoundAreasCount, 0, SpringLayout.WEST, panelList);
		sl_panelList.putConstraint(SpringLayout.SOUTH, labelFoundAreasCount, 0, SpringLayout.SOUTH, panelList);
		panelList.add(labelFoundAreasCount);
	}

	/**
	 * On "case sensitive" checkbox action.
	 */
	protected void onCaseSensitive() {
		
		Safe.tryOnChange(() -> {
			// Reload panel.
			reload();
			Safe.invokeLater(() -> {
				displayAreaProperties();
			});
		}, checkCaseSensitive, list);
	}
	
	/**
	 * On "whole words" checkbox action.
	 */
	protected void onWholeWords() {
		
		Safe.tryOnChange(() -> {
			// Reload panel.
			reload();
			Safe.invokeLater(() -> {
				displayAreaProperties();
			});
		}, checkWholeWords, list);
	}
	
	/**
	 * On "exact match" checkbox action.
	 */
	protected void onExactMatch() {
		
		Safe.tryOnChange(() -> {
			// Reload panel.
			reload();
			Safe.invokeLater(() -> {
				displayAreaProperties();
			});
		}, checkExactMatch, list);
	}

	/**
	 * On [ESC] key.
	 */
	protected void onTreeEscapeKey() {
		try {
			Safe.tryToUpdate(tree, () -> {
				tree.clearSelection();
			});
			displayAreaProperties();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * On [ESC] key.
	 */
	protected void onListEscapeKey() {
		try {
			Safe.tryToUpdate(list, () -> {
				list.clearSelection();
			});
			displayAreaProperties();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * On remove area.
	 */
	protected void onRemoveArea() {
		try {
			
			// Get selected area.
			TreePath [] selectedPaths = tree.getSelectionPaths();
			if (selectedPaths.length <= 0) {
				Utility.show(this, "org.multipage.generator.messageSelectSingleArea");
				return;
			}
	
			// Get selected areas.
			LinkedList<Area> areas = new LinkedList<Area>();
			Area parentArea = null;
			
			for (TreePath selectedPath : selectedPaths) {
				
				int elementsCount = selectedPath.getPathCount();
				
				// Get parent node.
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedPath.getPathComponent(elementsCount - 2);
				Area newParentArea = getNodeArea(parentNode);
				
				if (parentArea != null) {
					if (parentArea.getId() != newParentArea.getId()) {
						continue;
					}
				}
				parentArea = newParentArea;
				
				// Check root area.
				if (elementsCount < 2) {
					Utility.show(this, "org.multipage.generator.messageCannotRemoveRootArea");
					continue;
				}
				
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
				Area area = getNodeArea(node);
				
				areas.add(area);
			}
			
			// Remove area.
			GeneratorMainFrame.removeAreas(areas, parentArea, this);
			
			// Update all modules.
			GeneratorMainFrame.updateAll();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On add sub area.
	 */
	protected void onAddSubArea() {
		try {
			
			// Get selected area.
			TreePath [] selectedPaths = tree.getSelectionPaths();
			if (selectedPaths.length != 1) {
				Utility.show(this, "org.multipage.generator.messageSelectSingleArea");
				return;
			}
			
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			Area parentArea = getNodeArea(node);
			
			// Add new area.
			Obj<Area> newArea = new Obj<Area>();
			if (GeneratorMainFrame.getVisibleAreasDiagram().addNewArea(parentArea, this, newArea, false)) {
			
				// Select and expand the area.
				if (newArea.ref != null) {
					
					reload();
					Safe.tryToUpdate(tree, () -> {
						AreaTreeState.addSelectionAndExpandIt(tree, selectedPaths);
					});
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Select home area
	 */
	private void selectHomeArea() {
		try {
			
			// Clear selection
			Safe.tryToUpdate(tree, () -> {
				tree.clearSelection();
			});
			
			Obj<Boolean> found = new Obj<>(false);
			
			// Traverse through all tree elements, starting from the root element.
			Utility.traverseElements(tree, userObject -> node -> parentNode -> {
				
				try {
					// If the node holds the home area, select it.
					if (userObject instanceof Long) {
						Long areaId = (Long) userObject;
						
						boolean isHomeArea = ProgramGenerator.getAreasModel().isHomeAreaId(areaId);
						if (isHomeArea) {
							TreePath homeNodePath = new TreePath(node.getPath());
							Safe.tryToUpdate(tree, () -> {
								tree.addSelectionPath(homeNodePath);
							});
							found.ref = true;
							return true;
						}
					}
				}
				catch (Throwable e) {
					Safe.exception(e);
				}
				return false;
			});
			
			Safe.invokeLater(() -> {
				displayAreaProperties();
			});
			
			// If not found, inform user.
			if (!found.ref) {
				Utility.show(this, "org.multipage.generator.messageEditorDoesntContainHomeArea");
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Select area.
	 * @param coordinatesItem
	 */
	private boolean selectArea(final Long areaId) {
		
		try {
			Obj<Boolean> success = new Obj<Boolean>(false);
			
			// Check the input value.
			if (areaId == null) {
				return success.ref;
			}
			
			// Clear selection
			Safe.tryToUpdate(tree, () -> {
				tree.clearSelection();
			});
			
			// Traverse all tree elements starting from the root element.
			Utility.traverseElements(tree, userObject -> node -> parentNode -> {
				
				try {
					// If the node holds input area, select it.
					if (userObject instanceof Long) {
						
						long treeAreaId = (Long) userObject;
						if (treeAreaId == areaId) {
							
							TreePath areaNodePath = new TreePath(node.getPath());
							Safe.tryToUpdate(tree, () -> {
								tree.addSelectionPath(areaNodePath);
							});
							success.ref = true;
						}
					}
				}
				catch (Throwable e) {
					Safe.exception(e);
				}
				return false;
			});
			
			// Clear list selection.
			Safe.tryToUpdate(list, () -> {
				list.clearSelection();
			});
			
			// Select the area by its ID.
			int count = listModel.getSize();
			for (int index = 0; index < count; index++) {
				
				Object item = listModel.get(index);
				if (item instanceof Area) {
					
					Area area = (Area) item;
					long listAreaId = area.getId();
					
					if (areaId == listAreaId) {
						final int indexValue = index;
						Safe.tryToUpdate(list, () -> {
							list.setSelectedIndex(indexValue);
						});
						list.ensureIndexIsVisible(index);
						success.ref = true;
					}
				}
			}
			return success.ref;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Select area with sub nodes.
	 */
	protected void selectNodeWithSubNodes() {
		try {
			
			// Get selected path.
			TreePath [] selectedPaths = tree.getSelectionPaths();
			if (selectedPaths.length != 1) {
				Utility.show(this, "org.multipage.generator.messageSelectSingleArea");
				return;
			}
			
			TreePath treePath = selectedPaths[0];
			LinkedList<TreePath> treePaths = new LinkedList<TreePath>();
			
			getSubPaths((DefaultMutableTreeNode) treePath.getLastPathComponent(), treePaths);
					
			// Select sub nodes.
			Safe.tryToUpdate(tree, () -> {
				tree.setSelectionPaths(treePaths.toArray(new TreePath [0]));
			});
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
	 * Returns true value if the IDs have to be visible.
	 * @return
	 */
	private boolean showIds() {
		
		try {
			boolean showIds = buttonShowIds.isSelected();
			return showIds;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Post create.
	 */
	private void postCreate() {
		try {
			
			// Localize components.
			localize();
			// Set icons.
			setIcons();
			// Create tool bar.
			createToolBars();
			// Load dialog.
			loadDialog();
			// Create tree.
			createTree();
			// Create list.
			createList();
			// Create popup menus.
			createPopupMenus();
			// Set listeners.
			setListeners();
			// Update conents.
			reload();
			// Register for updates.
			GeneratorMainFrame.registerForUpdate(this);
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
			
			buttonAliases.setSelected(loadAliasesState);
			buttonSuperAreas.setSelected(loadSuperAreasState);
			tabbedPane.setSelectedIndex(selectedTabIndexState);
			checkInherits.setSelected(inheritState);
			buttonShowIds.setSelected(showIdsState);
			checkCaseSensitive.setSelected(caseSensitiveState);
			checkWholeWords.setSelected(wholeWordsState);
			checkExactMatch.setSelected(exactMatchState);
			textFilter.setText(filterState);
			textLevels.setText(levelsState);
			
			if (bounds.isEmpty()) {
				// Center dialog.
				bounds = getBounds();
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
	 * Create popup menus.
	 */
	private void createPopupMenus() {
		try {
			
			// Create new area trayMenu.
			final Component thisComponent = this;
			
			AreaLocalMenu areaMenuTree = ProgramGenerator.newAreaLocalMenu(new AreaLocalMenu.Callbacks() {
				
				@Override
				public Component getComponent() {
					// Get this component.
					return thisComponent;
				}
				
				@Override
				protected Area getCurrentArea() {
					
					try {
						// Get selected area.
						TreePath [] selectedPaths = tree.getSelectionPaths();
						if (selectedPaths == null || selectedPaths.length != 1) {
							return null;
						}
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectedPaths[0].getLastPathComponent();
						Long areaId = (Long) node.getUserObject();
						return ProgramGenerator.getAreasModel().getArea(areaId);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return null;
				}
	
				@Override
				protected LinkedList<Area> getCurrentAreas() {
					
					try {
						// Get selected area.
						TreePath [] selectedPaths = tree.getSelectionPaths();
						int length = selectedPaths.length;
						if (selectedPaths == null || length <= 0) {
							return null;
						}
						
						LinkedList<Area> areas = new LinkedList<Area>();
						
						for (int index = 0; index < length; index++) {
							DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectedPaths[index].getLastPathComponent();
							Long areaId = (Long) node.getUserObject();
							Area area = ProgramGenerator.getAreasModel().getArea(areaId);
							areas.add(area);
						}
						return areas;
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return null;
				}
	
				@Override
				public Area getCurrentParentArea() {
					
					try {
						// Get selected area and its parent.
						TreePath [] selectedPaths = tree.getSelectionPaths();
						if (selectedPaths.length < 1) {
							return null;
						}
						
						TreePath selectedPath = selectedPaths[0];
						int elementsCount = selectedPath.getPathCount();
						
						if (elementsCount < 2) {
							return null;
						}
		
						DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedPath.getPathComponent(elementsCount - 2);
						Long parentAreaId = (Long) parentNode.getUserObject();
						return ProgramGenerator.getAreasModel().getArea(parentAreaId);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return null;
				}
	
				@Override
				public void onNewArea(Long newAreaId) {
					try {
			
						if (newAreaId == null) {
							return;
						}
		
						// Select new area (imported).
						GeneratorMainFrame.getVisibleAreasDiagram().clearDiagramSelection();
						GeneratorMainFrame.getVisibleAreasDiagram().select(newAreaId, true, false);
						
						reload();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}

				@Override
				protected void selectEditorArea(Long areaId, boolean reset, boolean affectSubareas) {
					try {
						
						if (reset) {
							selectedTreeAreaIds.clear();
						}
						selectedTreeAreaIds.add(areaId);
						
						if (affectSubareas) {
							HashSet<Long> subAreaIds = ProgramGenerator.getSubAreaIds(areaId);
							selectedTreeAreaIds.addAll(subAreaIds);
						}
						tree.updateUI();
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
				}

				@Override
				protected void clearEditorAreaSelection() {
					try {
						
						selectedTreeAreaIds.clear();
						tree.updateUI();
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
				}
			});
			
			// Add new popup menu items.	
			areaMenuTree.addTo(this, popupMenuTree);
			// Set list of disabled menu items.
			areaMenuTree.disableMenuItems(
					areaMenuTree.menuAddToFavoritesArea,
					areaMenuTree.menuFocusSuperArea,
					areaMenuTree.menuFocusNextArea,
					areaMenuTree.menuFocusPreviousArea,
					areaMenuTree.menuFocusTabTopArea
				);
			
			// Create new area menu.
			AreaLocalMenu areaMenuList = ProgramGenerator.newAreaLocalMenu(new AreaLocalMenu.Callbacks() {
				
				@Override
				public Component getComponent() {
					// Get this component.
					return thisComponent;
				}
				
				@Override
				protected Area getCurrentArea() {
					
					try {
						// Get selected area.
						List<?> selected = list.getSelectedValuesList();
						if (selected.size() != 1) {
							return null;
						}
						Area area = (Area) selected.get(0);
						return ProgramGenerator.getAreasModel().getArea(area.getId());
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return null;
				}

				@Override
				protected void selectEditorArea(Long areaId, boolean reset, boolean affectSubareas) {
					try {
						
						if (reset) {
							selectedListAreaIds.clear();
						}
						selectedListAreaIds.add(areaId);
						
						if (affectSubareas) {
							HashSet<Long> subAreaIds = ProgramGenerator.getSubAreaIds(areaId);
							selectedListAreaIds.addAll(subAreaIds);
						}
						list.updateUI();
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
				}

				@Override
				protected void clearEditorAreaSelection() {
					try {
						
						selectedListAreaIds.clear();
						list.updateUI();
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
				}
			});
			areaMenuList.addTo(this, popupMenuList);			
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create tool bar.
	 */
	private void createToolBars() {
		try {
			
			// Area tree tool bar.
			ToolBarKit.addToolBarButton(toolBarTree, "org/multipage/generator/images/expand_icon.png", "org.multipage.generator.tooltipExpandTree", ()->onExpandTree());
			ToolBarKit.addToolBarButton(toolBarTree, "org/multipage/generator/images/collapse_icon.png", "org.multipage.generator.tooltipCollapseTree", ()->onCollapseTree());
			
			// Add tool bar controls.
			buttonSuperAreas = ToolBarKit.addToggleButton(toolBarTree, "org/multipage/generator/images/superareas.png", "org.multipage.generator.tooltipToggleSubSuperAreas", ()->onToggleSubSuper());
			buttonAliases = ToolBarKit.addToggleButton(toolBarTree, "org/multipage/generator/images/description_alias.png", "org.multipage.generator.tooltipToggleDescriptionsAliases", ()->onToggleDescriptionsAliases());
			buttonShowIds = ToolBarKit.addToggleButton(toolBarTree, "org/multipage/generator/images/show_hide_id.png", "org.multipage.generator.tooltipToggleShowIds", ()->onToggleShowIds());
			toolBarTree.add(checkInherits);
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
	 * On toggle sub and super areas.
	 */
	public void onToggleSubSuper() {
		
		Safe.tryOnChange(() -> {
			// Reload panel.
			reload();
			Safe.invokeLater(() -> {
				displayAreaProperties();
			});
		}, buttonSuperAreas, tree, list);
	}
	
	/**
	 * On toggle descriptions and aliases of areas.
	 */
	public void onToggleDescriptionsAliases() {

		Safe.tryOnChange(() -> {
			// Reload panel.
			reload();
			Safe.invokeLater(() -> {
				displayAreaProperties();
			});
		}, buttonAliases, tree, list);
	}
	
	/**
	 * On toggle IDs of areas.
	 */
	public void onToggleShowIds() {

		Safe.tryOnChange(() -> {
			// Reload panel.
			reload();
			Safe.invokeLater(() -> {
				displayAreaProperties();
			});
		}, buttonShowIds, tree, list);
	}
	
	/**
	 * Set listeners.
	 */
	private void setListeners() {
		try {
			
			Utility.setTextChangeListener(textFilter, () -> {
				
				Safe.tryOnChange(() -> {
					// Reload panel.
					reload();
					Safe.invokeLater(() -> {
						displayAreaProperties();
					});
				}, textFilter, list);
			});
			
			Utility.setTextChangeListener(textLevels, () -> {
				
				Safe.tryOnChange(() -> {
					// Reload panel.
					reload();
					Safe.invokeLater(() -> {
						displayAreaProperties();
					});
				}, textLevels, list);
			});
			
			checkInherits.addActionListener(e -> {
				
				Safe.tryOnChange(() -> {
					// Reload panel.
					reload();
					Safe.invokeLater(() -> {
						displayAreaProperties();
					});
				}, checkInherits, tree, list);
			});
			
			// On tree item selection.
			tree.addTreeSelectionListener(new TreeSelectionListener() {
			    public void valueChanged(TreeSelectionEvent e) {
			    	
			    	Safe.tryOnChange(tree, () -> {
				    	onSelectedTreeItem();				    	
			    	});
			    }
			});
			
			// Set list selection listener.
			list.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting()) {
						return;
					}
					Safe.tryOnChange(list, () -> {
						onSelectedListItem();
					});
				}
			});
			
			// "Select all' properties" event receiver.
			ApplicationEvents.receiver(this, GuiSignal.selectAll, message -> {
				try {
			
					boolean isShowing = AreasTreeEditorPanel.this.isShowing();
					if (isShowing) {
						setAllSelection(true);
					}
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// "Unselect all' properties" event receiver.
			ApplicationEvents.receiver(this, GuiSignal.unselectAll, message -> {
				try {
			
					boolean isShowing = AreasTreeEditorPanel.this.isShowing();
					if (isShowing) {
						setAllSelection(false);
					}
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// "Focus home area' properties" event receiver.
			ApplicationEvents.receiver(this, GuiSignal.focusHomeArea, message -> {
				try {
			
					if (isShowing()) {
						selectHomeArea();
					}
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// "Focus top area" event receiver.
			ApplicationEvents.receiver(this, GuiSignal.focusTopArea, message -> {
				try {
			
					if (isShowing()) {
						Long tabAreaId = GeneratorMainFrame.getTabAreaId();
						if (tabAreaId == null) {
							return;
						}
						selectArea(tabAreaId);
						Safe.invokeLater(() -> {
							displayAreaProperties();
						});
					}
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// Add GUI event listener.
			ApplicationEvents.receiver(this, GuiSignal.focusBasicArea, message -> {
				try {
			
					if (isShowing()) {
						// Center the areas diagram.
						boolean success = selectArea(0L);
						if (!success) {
							Utility.show(AreasTreeEditorPanel.this, "org.multipage.generator.messageEditorDoesntContainBasicArea");
							return;
						}
						Safe.invokeLater(() -> {
							displayAreaProperties();
						});
					}
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// "Focus area" event receiver.
			ApplicationEvents.receiver(this, GuiSignal.focusArea, message -> {
				try {
			
					if (isShowing()) {
						Long areaId = message.getRelatedInfo();
						selectArea(areaId);
						Safe.invokeLater(() -> {
							displayAreaProperties();
						});
					}
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// "Show/hide IDs" event receiver.
			ApplicationEvents.receiver(this, GuiSignal.showOrHideIds, message -> {
				try {
			
					// Set button state.
					boolean showIds = message.getRelatedInfo();
					Safe.tryToUpdate(buttonShowIds, () -> {
						buttonShowIds.setSelected(showIds);
					});
					// Reload editor.
					reload();
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Removes attached listeners.
	 */
	private void removeListeners() {
		try {
			
			// Remove event listener.
			ApplicationEvents.removeReceivers(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On tree item selection.
	 */
	private void onSelectedTreeItem() {
		try {
			displayAreaProperties();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On list item selection.
	 */
	private void onSelectedListItem() {
		try {
			displayAreaProperties();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get selected area IDs depending on current (panel tree or list).
	 * @return
	 */
	public HashSet<Long> getSelectedTabAreaIds() {
		
		try {
			int tab = tabbedPane.getSelectedIndex();
			if (tab == 1) {
				HashSet<Long> selectedAreaIds = getSelectedListAreaIds();
				if (selectedAreaIds != null && !selectedAreaIds.isEmpty()) {
					return selectedAreaIds;
				}
				return selectedListAreaIds;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		HashSet<Long> selectedAreaIds = getSelectedTreeAreaIds();
		if (!selectedAreaIds.isEmpty()) { 
			return selectedAreaIds;
		}
		return selectedTreeAreaIds;
	}
	
	/**
	 * Get IDs of areas selected in the tree view.
	 * @return
	 */
	protected HashSet<Long> getSelectedTreeAreaIds() {
		
		HashSet<Long> selectedAreaIds = null;
		try {
			
			selectedAreaIds = new HashSet<>();
			
			// Get selected areas.
	    	LinkedList<Area> areas = new LinkedList<Area>();
	    	TreePath [] paths = tree.getSelectionPaths();
	    	
	    	if (paths != null) {
	    	
		    	// Do loop for all paths and avoid duplicate areas.
		    	for (TreePath path : paths) {
		    		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		    		
		    		Long areaId = (Long) node.getUserObject();
		    		Area area = ProgramGenerator.getArea(areaId);
		    		
		    		// If the area is already in list, continue loop.
		    		boolean isNewArea = true;
		    		
		    		for (Area item : areas) {
		    			if (item.getId() == areaId) {
		    				isNewArea = false;
		    			}
		    		}
		    		
		    		if (isNewArea) {
		    			areas.add(area);
		    			selectedAreaIds.add(areaId);
		    		}
		    	}
	    	}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return selectedAreaIds;
	}
	
	/**
	 * Get IDs of areas selected in the list view.
	 * @return
	 */
	protected HashSet<Long> getSelectedListAreaIds() {
		
		HashSet<Long> selectedAreaIds = null;
		try {
			selectedAreaIds = new HashSet<>();

			// Get selected areas.
			List<Object> selections = list.getSelectedValuesList();
			for (Object item : selections) {
				if (item instanceof Area) {
					Area area = (Area) item;
					selectedAreaIds.add(area.getId());
				}
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return selectedAreaIds;
	}
	
	/**
	 * Display area IDs specified in the input set
	 */
	public void displayAreaIds(Long [] areaIds) {
		try {
			
			// Check the input array
			if (areaIds == null) {
				return;
			}
			
			Safe.invokeLater(() -> {
				
				// Create look up set from the input array
				HashSet<Long> areaIdsLookup = new HashSet<Long>();
				Arrays.stream(areaIds).forEach(areaId -> { areaIdsLookup.add(areaId); });
				
				// Traverse tree elements
				Utility.traverseElements(tree, userObject -> node -> parentNode -> {
					
					try {
						// If the area ID of user object should be displayed, expand the parent node
						if (userObject instanceof Area) {
							Area area = (Area) userObject;
							
							long areaId = area.getId();
							if (areaIdsLookup.contains(areaId)) {
								
								TreePath pathToNode = new TreePath(parentNode.getPath());
								tree.expandPath(pathToNode);
							}
						}
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return false;
				});
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get set of area IDs that are expanded in the areas tree control
	 * @return
	 */
	public Long [] getDisplayedAreaIds() {
		
		try {
			// Traverse displayed elements of the tree view 
			final HashSet<Long> displayedAreaIds = new HashSet<Long>();
			Utility.traverseExpandedElements(tree, userObject -> {
				
				// Try to get area ID and add it to the output set
				if (userObject instanceof Area) {
					Area area = (Area) userObject;
					
					long areaId = area.getId();
					displayedAreaIds.add(areaId);
				}
			});
			
			return displayedAreaIds.toArray(new Long [] {});
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Create tree.
	 */
	private void createTree() {
		try {
			
			tree.setExpandsSelectedPaths(true);
			
			// Set model.
			treeModel = new DefaultTreeModel(null);
			tree.setModel(treeModel);
			// Set renderer.
			tree.setCellRenderer(new TreeCellRenderer() {
				
				@Override
				public Component getTreeCellRendererComponent(JTree tree, Object value,
						boolean selected, boolean expanded, boolean leaf, int row,
						boolean hasFocus) {
					
					try {
						itemRenderer.setForeground(Color.BLACK);
						
						if (!(value instanceof DefaultMutableTreeNode)) {
							itemRenderer.setProperties("#renderer error#", Color.BLACK, 0, selected, hasFocus, false);
							return itemRenderer;
						}
						
						// Get tree nodes.
						DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
						DefaultMutableTreeNode parentTreeNode = (DefaultMutableTreeNode) treeNode.getParent();
						
						// Get DnD mark.
						boolean dndMark = false;
						if (value instanceof DefaultMutableTreeNodeDnD) {
							
							DefaultMutableTreeNodeDnD dndNode = (DefaultMutableTreeNodeDnD) value;
							dndMark = dndNode.isMarked();
						}
						selected = selected || dndMark;
						
						// Get area.
						Long areaId = (Long) treeNode.getUserObject();
						Area area = ProgramGenerator.getArea(areaId);
						if (area == null) {
							return null;
						}
						
						boolean isHomeArea = ProgramGenerator.getAreasModel().isHomeArea(area);
						boolean isVisible = area.isVisible();
						boolean isDisabled = !area.isEnabled();
						
						itemRenderer.setAreaVisible(isVisible);
						
						// Get area text.
						boolean showIds = showIds();
						String alias = area.getAlias(showIds);
						boolean isEmptyAlias = alias == null;
						boolean isDescription = !buttonAliases.isSelected();
						if (isEmptyAlias) {
							alias = (showIds ? String.format("[%d] ", areaId) : "")
									+ Resources.getString("org.multipage.generator.textUnknownAlias");
						}
						
						String text = isDescription ? area.getDescriptionForced(showIds) : alias;
						Color colorText = (!isDescription && isEmptyAlias ? Color.LIGHT_GRAY : Color.BLACK);
								
						// Get sub relation names.
						if (parentTreeNode != null) {
							Long parentAreaId = (Long) parentTreeNode.getUserObject();
							Area parentArea = ProgramGenerator.getArea(parentAreaId);
			
							String subName = parentArea.getSubRelationName(area.getId());
							String superName = area.getSuperRelationName(parentArea.getId());
							boolean hiddenSubareas = parentArea.isSubareasHidden(area);
							
							itemRenderer.setProperties(text, colorText, subName, superName, hiddenSubareas, 0, selected, hasFocus, isHomeArea);
						}
						else {
							itemRenderer.setProperties(text, colorText, 0, selected, hasFocus, isHomeArea);
						}
						
						// If the area is disabled, gray its name
						if (isDisabled) {
							itemRenderer.setForeground(Color.LIGHT_GRAY);
						}
						
						// Display permanent red selections.
						boolean isSelected = selectedTreeAreaIds.contains(areaId);
						if (isSelected) {
							
							itemRenderer.setForeground(Color.RED);
						}
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					
					return itemRenderer;
				}
			});
			
			final Component thisComponent = this;
			
			tree.setDragAndDropCallback(new JTreeDndCallback() {
				
				@Override
				public void onNodeDropped(
						DefaultMutableTreeNodeDnD droppedDndNode,
						TreeNode droppedNodeParent,
						DefaultMutableTreeNodeDnD transferedDndNode,
						TreeNode transferredNodeParent,
						DropTargetDropEvent e) {
					
					try {
			
						// Get transferred area, target area, parent areas and action number and do an action.
						Object transferredObject = transferedDndNode.getUserObject();
						Object droppedObject = droppedDndNode.getUserObject();
						
						LinkedList<Area> transferredAreas = new LinkedList<Area>();
						
						// Get multiple selected area.
						TreePath[] multipleSelectedPaths = tree.getMultipleSelectedPaths();
						if (multipleSelectedPaths != null) {
							
							for (TreePath selectedPath : multipleSelectedPaths) {
								Object pathComponent = selectedPath.getLastPathComponent();
								if (pathComponent instanceof DefaultMutableTreeNode) {
									
									DefaultMutableTreeNode node = (DefaultMutableTreeNode) pathComponent;
									Object userObject = node.getUserObject();
									
									if (userObject instanceof Long) {
		
										long areaId = (long) userObject;
										Area area = ProgramGenerator.getArea(areaId);
										transferredAreas.add(area);
									}
								}
							}
						}
						
						// Get transferred area. 
						if (transferredAreas.isEmpty()) {
							if (!(transferredObject instanceof Long && droppedObject instanceof Long)) {
								e.rejectDrop();
								return;
							}
							
							Area transferredArea = ProgramGenerator.getArea((long) transferredObject);
							transferredAreas.add(transferredArea);
						}
										
						// Get areas from the model.
						Area droppedArea = ProgramGenerator.getArea((long) droppedObject);
						
						// Get parent areas.
						Area transferredParentArea = null;
						if (transferredNodeParent instanceof DefaultMutableTreeNode) {
							
							DefaultMutableTreeNode transferredMutableNodeParent = (DefaultMutableTreeNode) transferredNodeParent;
							Object parentObject = transferredMutableNodeParent.getUserObject();
							
							if (parentObject instanceof Long) {
								transferredParentArea = ProgramGenerator.getArea((long)parentObject);
							}
						}
						
						Area droppedParentArea = null;
						if (droppedNodeParent instanceof DefaultMutableTreeNode) {
							
							DefaultMutableTreeNode droppedMutableNodeParent = (DefaultMutableTreeNode) droppedNodeParent;
							Object parentObject = droppedMutableNodeParent.getUserObject();
							
							if (parentObject instanceof Long) {
								droppedParentArea = ProgramGenerator.getArea((long)parentObject);
							}
						}
						
						// Make drop and reload the diagrams.
						int action = e.getDropAction();
						
						GeneratorMainFrame.transferArea(
								transferredAreas, transferredParentArea,
								droppedArea, droppedParentArea,
								action, thisComponent);
						
						reload();
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
	 * Create list.
	 */
	private void createList() {
		try {
			
			// Set renderer.
			itemRenderer = new ItemRendererImpl();
			list.setCellRenderer(new ListCellRenderer<>() {
				// Get list renderer.
				@Override
				public Component getListCellRendererComponent(JList<?> list, Object value,
						int index, boolean isSelected, boolean cellHasFocus) {
					
					itemRenderer.setForeground(Color.BLACK);
					
					if (!(value instanceof Area)) {
						return null;
					}
					Area area = (Area) value;
					long areaId = area.getId();
					
					// Get area text.
					boolean showIds = showIds();
					String alias = area.getAlias(showIds);
					boolean isEmptyAlias = alias == null;
					boolean isDescription = !buttonAliases.isSelected();
					boolean isHomeArea = ProgramGenerator.getAreasModel().isHomeArea(area);
					
					if (isEmptyAlias) {
						alias = (showIds ? String.format("[%d] ", areaId) : "")
								+ Resources.getString("org.multipage.generator.textUnknownAlias");
					}
					
					String text  = isDescription ? area.getDescriptionForced(showIds()) : alias;
					Color colorText = (!isDescription && isEmptyAlias ? Color.LIGHT_GRAY : Color.BLACK);
					
					itemRenderer.setProperties(text, colorText, index, isSelected, cellHasFocus, isHomeArea);
					
					// Display permanent red selections.
					boolean isAreaSelected = selectedListAreaIds.contains(areaId);
					if (isAreaSelected) {
						itemRenderer.setForeground(Color.RED);
					}
					return itemRenderer;
				}
			});
			// Set model.
			listModel = new DefaultListModel<>();
			list.setModel(listModel);
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
			
			tabbedPane.setIconAt(1, Images.getIcon("org/multipage/generator/images/list.png"));
			menuSelectSubNodes.setIcon(Images.getIcon("org/multipage/generator/images/select_subnodes.png"));
			menuAddSubArea.setIcon(Images.getIcon("org/multipage/generator/images/area_node.png"));
			menuRemoveArea.setIcon(Images.getIcon("org/multipage/generator/images/cancel_icon.png"));
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
			
			Utility.localize(checkInherits);
			Utility.localize(labelLevels);
			Utility.localize(tabbedPane);
			Utility.localize(labelFilter);
			Utility.localize(checkCaseSensitive);
			Utility.localize(checkWholeWords);
			Utility.localize(checkExactMatch);
			Utility.localize(menuSelectSubNodes);
			Utility.localize(menuAddSubArea);
			Utility.localize(menuRemoveArea);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On close window.
	 */
	protected void onCloseWindow() {
		try {
			
			saveDialog();
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
			
			loadAliasesState = buttonAliases.isSelected();
			loadSuperAreasState = buttonSuperAreas.isSelected();
			selectedTabIndexState = tabbedPane.getSelectedIndex();
			inheritState = checkInherits.isSelected();
			showIdsState = buttonShowIds.isSelected();
			caseSensitiveState = checkCaseSensitive.isSelected();
			wholeWordsState = checkWholeWords.isSelected();
			exactMatchState = checkExactMatch.isSelected();
			filterState = textFilter.getText();
			levelsState = textLevels.getText();
			bounds = getBounds();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Update current panel.
	 */
	@Override
	public void reload() {
		try {
			
			reloadTree();
			reloadList();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Update tree panel.
	 */
	public void reloadTree() {
		Safe.invokeLater(() -> {
			
			// Set tab icon and text.
			boolean isSubareas = !buttonSuperAreas.isSelected();
			String iconPath = "org/multipage/generator/images/" + (isSubareas ? "subareas" 
					: "superareas") + ".png";
			
			tabbedPane.setIconAt(0, Images.getIcon(iconPath));
			tabbedPane.setTitleAt(0, Resources.getString(isSubareas ? "org.multipage.generator.textSubAreasTree"
																	: "org.multipage.generator.textSuperAreasTree"));
			// Get inheritance.
			boolean inheritance = checkInherits.isSelected();
	
			// Set inheritance and Drag and Drop.
			checkInherits.setEnabled(!isSubareas);
			tree.enableDragAndDrop(isSubareas);
				
			// Get tree state.
			AreaTreeState treeState = AreaTreeState.getTreeState(tree);
			
			// Load tree.
			Safe.tryToUpdate(tree, () -> {
				updateTreeModel(treeModel, areaId, isSubareas, inheritance);
			});
			
			// Update the tree control.
			tree.setModel(treeModel);
			treeModel.reload();
						
			// Apply tree state.
			Safe.tryToUpdate(tree, () -> {
				AreaTreeState.applyTreeState(treeState, tree);
			});
			
			// Expand tree root.
			Safe.invokeLater(() -> {
				Utility.expandTop(tree, true);
			});
		});
	}
	
	/**
	 * Update list panel.
	 */
	public void reloadList() {
		Safe.invokeLater(() -> {
			
			// Set tab icon and text.
			boolean isSubareas = !buttonSuperAreas.isSelected();
			String iconPath = "org/multipage/generator/images/" + (isSubareas ? "subareas" 
					: "superareas") + ".png";
			
			tabbedPane.setIconAt(0, Images.getIcon(iconPath));
			tabbedPane.setTitleAt(0, Resources.getString(isSubareas ? "org.multipage.generator.textSubAreasTree"
																	: "org.multipage.generator.textSuperAreasTree"));
			// Get selected text type.
			boolean isDescription = !buttonAliases.isSelected();
			// Get inheritance.
			boolean inheritance = checkInherits.isSelected();
			// Get number of levels.
			String levelsText = textLevels.getText();
			int levels = 0;
			try {
				levels = Integer.parseInt(levelsText);
			}
			catch (Exception e) {
			}
			// Get filter.
			String filterText = textFilter.getText();
			boolean caseSensitive = checkCaseSensitive.isSelected();
			boolean wholeWord = checkWholeWords.isSelected();
			boolean exactMatch = checkExactMatch.isSelected();
	
			// Set inheritance and Drag and Drop.
			checkInherits.setEnabled(!isSubareas);
			tree.enableDragAndDrop(isSubareas);
			
			// Get current selected items.
			int [] selectedIndices = list.getSelectedIndices();
			// Update list.
			listModel.clear();
			// Get areas.
			AreasModel areasModel = ProgramGenerator.getAreasModel();
			LinkedList<Area> areas = isSubareas ? areasModel.getAreaAndSubAreas(areaId, levels) :
				areasModel.getAreaAndSuperAreas(areaId, levels, inheritance);
			LinkedList<Area> areasSorted = new LinkedList<Area>();
			
			// Load texts.
			for (Area areaItem : areas) {
				
				String alias = areaItem.getAlias();
				String description = areaItem.getDescriptionForced(showIds());
				
				String text = isDescription ? description : alias;
				if (!text.isEmpty()) {
					if (!filterText.isEmpty() && !Utility.matches(text, filterText,
							caseSensitive, wholeWord, exactMatch)) {
						continue;
					}
					areasSorted.add(areaItem);
				}
			}
			
			// Sort texts.
			class AreasComparator implements Comparator<Area> {
				
				boolean isAliases;
				
				public AreasComparator(boolean isAliases) {
					this.isAliases = isAliases;
				}

				@Override
				public int compare(Area area1, Area area2) {
					
					try {
						String area1Text;
						String area2Text;
						
						if (isAliases) {
							area1Text = area1.getAlias();
							area2Text = area2.getAlias();
						}
						else {
							area1Text = area1.getDescriptionForced(showIds());
							area2Text = area2.getDescriptionForced(showIds());
						}
						return area1Text.compareTo(area2Text);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return 1;
				}
			}
			
			Collections.sort(areasSorted, new AreasComparator(!isDescription));
			
			// Load list.
			for (Area areaSorted : areasSorted) {
				listModel.addElement(areaSorted);
			}
			
			// Set areas count.
			labelFoundAreasCount.setText(String.format(
					Resources.getString("org.multipage.generator.textFoundAreasCount"), areasSorted.size()));
			
			// Restore selection.
			Safe.tryToUpdate(list, () -> {
				list.setSelectedIndices(selectedIndices);
			});
		});
	}
	
	/**
	 * Update tree model.
	 * @param treeModel
	 * @param isSubareas
	 * @param inheritance
	 */
	private void updateTreeModel(DefaultTreeModel treeModel, Long rootAreaId, boolean isSubareas,
			boolean inheritance) {
		try {
			
			if (rootAreaId == null) {
				// Clear tree model.
				treeModel.setRoot(null);
				return;
			}
			
			// Check root area.
			Area rootArea = ProgramGenerator.getArea(rootAreaId);
			if (rootArea == null) {
				treeModel.setRoot(null);
				return;
			}
			
			// Create root node.
			DefaultMutableTreeNode rootNode = new DefaultMutableTreeNodeDnD(rootAreaId);
			
			// Create nodes.
			createNodes(rootNode, isSubareas, inheritance);
			
			// Set root node.
			treeModel.setRoot(rootNode);
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
	private void createNodes(DefaultMutableTreeNode parentNode, boolean isSubareas, boolean inheritance) {
		try {
			
			Object userObject = parentNode.getUserObject();
			if (!(userObject instanceof Long)) {
				return;
			}
			
			long areaId = (Long) userObject;
			Area area = ProgramGenerator.getArea(areaId);
			
			// Do loop for all sub or super areas.
			LinkedList<Area> areas = null;
			
			if (isSubareas) {
				
				// If the area is disabled, hide its sub areas
				if (!area.isEnabled()) {
					return;
				}
				
				areas = area.getSubareas();
			}
			else {
				if (!inheritance) {
					areas = area.getSuperareas();
				}
				else {
					areas = area.getInheritsFromSuper();
				}
			}
			
			for (Area areaItem : areas) {
				
				// Get area ID.
				areaId = areaItem.getId();
				// Create new node.
				DefaultMutableTreeNode childNode = new DefaultMutableTreeNodeDnD(areaId);
				// Add it to the parent node.
				parentNode.add(childNode);
	
				if (isSubareas) {
					// If area item sub areas are hidden, continue the loop.
					if (area.isSubareasHidden(areaItem)) {
						continue;
					}
				}
				
				// Call this method recursively.
				createNodes(childNode, isSubareas, inheritance);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set all selection.
	 * @param select
	 */
	private void setAllSelection(boolean select) {
		try {
			
			int tabIndex = tabbedPane.getSelectedIndex();
			
			if (select) {
				if (tabIndex == 0) {
					Safe.tryToUpdate(tree, () -> {
						tree.clearSelection();
						tree.addSelectionRow(0);
					});
					selectNodeWithSubNodes();
				}
				else if (tabIndex == 1) {
					int itemCount = listModel.getSize();
					Safe.tryToUpdate(list, () -> {
						list.setSelectionInterval(0, itemCount - 1);
					});
				}
			}
			else {
				if (tabIndex == 0) {
					Safe.tryToUpdate(tree, () -> {
						tree.clearSelection();
					});
				}
				else if (tabIndex == 1) {
					Safe.tryToUpdate(list, () -> {
						list.clearSelection();
					});
				}
			}
			Safe.invokeLater(() -> {
				displayAreaProperties();
			});
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
			
			if (popup.equals(popupMenuTree)) {
				
				boolean isSubAreas = !buttonSuperAreas.isSelected();
				
				menuAddSubArea.setEnabled(isSubAreas);
				menuRemoveArea.setEnabled(isSubAreas);
			}
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Add popup window.
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
	 * On tab panel change event.
	 */
	@Override
	public void onTabPanelChange(ChangeEvent e, int selectedIndex) {
		try {
			
			if (!isVisible()) {
				return;
			}
			displayAreaProperties();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On tab switch.
	 */
	protected void onTreeListTabChange() {
		try {
			
			if (!isVisible()) {
				return;
			}
			displayAreaProperties();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * No tab text.
	 */
	@Override
	public String getTabDescription() {
		
		return "";
	}
	
	/**
	 * Before tab panel removed.
	 */
	@Override
	public void beforeTabPanelRemoved() {
		try {
			
			close();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get tab state
	 */
	@Override
	public TabState getTabState() {
		
		try {
			// Create new area tree panel state
			AreasTreeTabState tabState = new AreasTreeTabState();
			
			// Set title
			tabState.title = tabLabel.getDescription();
			
			// Set state components
			tabState.areaId = areaId;
			
			// Set area IDs displayed in the tree view
			tabState.displayedArea = getDisplayedAreaIds();
			
			return tabState;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Set reference to a tab label
	 */
	@Override
	public void setTabLabel(TabLabel tabLabel) {
		
		this.tabLabel = tabLabel;
	}
	
	/**
	 * Set top area ID
	 */
	@Override
	public void setAreaId(Long topAreaId) {
		try {
			
			Long theTopAreaID = topAreaId;
			
			// Trim the area ID
			if (theTopAreaID == null) {
				theTopAreaID = 0L;
			}
			
			// Set area ID
			this.areaId = theTopAreaID;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Return node area.
	 * @param node
	 * @return
	 */
	public Area getNodeArea(DefaultMutableTreeNode node) {
		
		try {
			// Get node object.
			Object nodeObject = node.getUserObject();
			if (!(nodeObject instanceof Long)) {
				return null;
			}
			
			Long areaId = (Long) nodeObject;
			
			Area area = ProgramGenerator.getArea(areaId);
			return area;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Get previous update messages.
	 */
	@Override
	public LinkedList<Message> getPreviousMessages() {
		
		return previousUpdateMessages;
	}

	@Override
	public void recreateContent() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Display panel with selected area properties.
	 */
	private void displayAreaProperties() {
		try {
			
			if (isShowing()) {
				// Display area properties.
				HashSet<Long> selectedAreaIds = getSelectedTabAreaIds();
				ApplicationEvents.transmit(this, GuiSignal.displayAreaProperties, selectedAreaIds);
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * On update panel components.
	 */
	@Override
	public void updateComponents() {
		try {
			
			// Reload editor.
			reload();
			tree.updateUI();
			list.updateUI();
			displayAreaProperties();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On close panel.
	 */
	@Override
	public void close() {
		try {
			
			// Unregister from updates.
			GeneratorMainFrame.unregisterFromUpdate(this);
			// Remove listeners.
			removeListeners();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
