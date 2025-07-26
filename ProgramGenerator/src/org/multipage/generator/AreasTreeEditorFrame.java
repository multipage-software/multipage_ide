/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
import org.multipage.gui.ReceiverAutoRemove;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.TextPaneEx;
import org.multipage.gui.ToolBarKit;
import org.multipage.gui.TopMostButton;
import org.multipage.gui.UpdatableComponent;
import org.multipage.gui.Utility;
import org.multipage.util.Closable;
import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Editor that displays area tree with information about slots and selected areas.
 * @author vakol
 *
 */
public class AreasTreeEditorFrame extends JFrame implements PreventEventEchos, ReceiverAutoRemove, UpdatableComponent, Closable {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * States.
	 */
	private static boolean loadSubAreasState = true;
	private static boolean loadDescriptionsState = true;
	private static int selectedTabIndexState = 0;
	private static boolean inheritState = false;
	private static boolean showIdsState = false;
	private static boolean caseSensitiveState = false;
	private static boolean wholeWordsState = false;
	private static boolean exactMatchState = false;
	private static String filterState = "";
	private static String levelsState = "";
	private static Rectangle bounds = new Rectangle();
	private static int splitterPosition = -1;
	private static int splitter2Position = -1;
	
	/**
	 * Load state.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void serializeData(StateInputStream inputStream)
			throws IOException, ClassNotFoundException {
		
		loadSubAreasState = inputStream.readBoolean();
		loadDescriptionsState = inputStream.readBoolean();
		selectedTabIndexState = inputStream.readInt();
		inheritState = inputStream.readBoolean();
		showIdsState = inputStream.readBoolean();
		caseSensitiveState = inputStream.readBoolean();
		wholeWordsState = inputStream.readBoolean();
		exactMatchState = inputStream.readBoolean();
		filterState = inputStream.readUTF();
		levelsState = inputStream.readUTF();
		
		Object object = inputStream.readObject();
		if (!(object instanceof Rectangle)) {
			throw new ClassNotFoundException();
		}
		bounds = (Rectangle) object;
		
		splitterPosition = inputStream.readInt();
		splitter2Position = inputStream.readInt();
	}

	/**
	 * Save state.
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream)
			throws IOException {
		
		outputStream.writeBoolean(loadSubAreasState);
		outputStream.writeBoolean(loadDescriptionsState);
		outputStream.writeInt(selectedTabIndexState);
		outputStream.writeBoolean(inheritState);
		outputStream.writeBoolean(showIdsState);
		outputStream.writeBoolean(caseSensitiveState);
		outputStream.writeBoolean(wholeWordsState);
		outputStream.writeBoolean(exactMatchState);
		outputStream.writeUTF(filterState);
		outputStream.writeUTF(levelsState);
		outputStream.writeObject(bounds);
		outputStream.writeInt(splitterPosition);
		outputStream.writeInt(splitter2Position);
	}
	
	/**
	 * List of previous messages.
	 */
	private LinkedList<Message> previousMessages = new LinkedList<>();
	
	/**
	 * List renderer.
	 * @author
	 *
	 */
	@SuppressWarnings("serial")
	class ItemRendererImpl extends JLabel {

		private boolean isSelected;
		private boolean cellHasFocus;
		private boolean isVisible = false;
		
		ItemRendererImpl() {
			try {
			
				setOpaque(true);
			}
			catch(Throwable expt) {
				Safe.exception(expt);
			};
		}
		
		/**
		 * Set renderer properties.
		 * @param text
		 * @param subName
		 * @param superName
		 * @param hiddenSubareas
		 * @param index
		 * @param isSelected
		 * @param cellHasFocus
		 * @param isHomeArea
		 */
		public void setProperties(String text, String subName, String superName, boolean hiddenSubareas, int index,
				boolean isSelected, boolean cellHasFocus, boolean isHomeArea) {
			try {
				
				String theText = text;
			
				setIcon(Images.getIcon(
						isHomeArea ? (isVisible ? "org/multipage/generator/images/home_icon_small.png"
								: "org/multipage/generator/images/home_icon_small_unvisible.png")
								: (isVisible ? "org/multipage/generator/images/area_node.png"
										: "org/multipage/generator/images/area_node_unvisible.png")));
				
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
				setBackground(Utility.itemColor(index));
				this.isSelected = isSelected;
				this.cellHasFocus = cellHasFocus;
			}
			catch(Throwable expt) {
				Safe.exception(expt);
			};
				
		}
		
		/**
		 * Set renderer properties.
		 * @param text
		 * @param index
		 * @param isSelected
		 * @param cellHasFocus
		 * @param isHomeArea
		 */
		public void setProperties(String text, int index,
				boolean isSelected, boolean cellHasFocus, boolean isHomeArea) {
			try {
			
				String theText = text;
				
				setIcon(Images.getIcon(isHomeArea ? "org/multipage/generator/images/home_icon_small.png" : "org/multipage/generator/images/area_node.png"));
	
				if (theText.isEmpty()) {
					theText = Resources.getString("org.multipage.generator.textUnknownAlias");
					setForeground(Color.LIGHT_GRAY);
				}
				else {
					setForeground(Color.BLACK);
				}
				setText(theText);
				setBackground(Utility.itemColor(index));
				this.isSelected = isSelected;
				this.cellHasFocus = cellHasFocus;
			}
			catch(Throwable expt) {
				Safe.exception(expt);
			};
				
		}
		
		/**
		 * Draw the rendeder.
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
	 * Area ID.
	 */
	private long areaId;

	/**
	 * List renderer.
	 */
	private ItemRendererImpl itemRenderer;
	
	/**
	 * Tree model.
	 */
	private DefaultTreeModel treeModel;
	
	/**
	 * List model.
	 */
	private DefaultListModel<? super Object> listModel;
	
	/**
	 * Areas properties reference.
	 */
	private AreaPropertiesBasePanel areaPropertiesPanel;
	
	/**
	 * MessagePanel label.
	 */
	private MessagePanel panelMessage;
	
	/**
	 * List of selected area IDs.
	 */
	private HashSet<Long> selectedTreeAreaIds = new HashSet<Long>();
	private HashSet<Long> selectedListAreaIds = new HashSet<Long>();
	
	/**
	 * Toggle debug.
	 */
	private JToggleButton toggleDebug;
		
	// $hide<<$
	/**
	 * Components.
	 */
	private JTreeDnD tree;
	private JList<? super Object> list;
	private JPopupMenu popupMenuTree;
	private JPopupMenu popupMenuList;
	private JRadioButton radioSubAreas;
	private JRadioButton radioSuperAreas;
	private JRadioButton radioDescriptions;
	private JRadioButton radioAliases;
	private JCheckBox checkInherits;
	private JButton buttonClose;
	private final ButtonGroup buttonGroupAreas = new ButtonGroup();
	private final ButtonGroup buttonGroupText = new ButtonGroup();
	private JTabbedPane tabbedPane;
	private JPanel panelList;
	private JPanel panelTree;
	private JButton buttonReload;
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
	private JCheckBox checkShowIds;
	private JMenuItem menuSelectSubNodes;
	private JSplitPane splitPane;
	private JMenuItem menuAddSubArea;
	private JMenuItem menuRemoveArea;
	private JToolBar toolBarMain;
	private JSplitPane splitPaneProviders;
	private JScrollPane scrollPane;
	private JEditorPane editorSlotPreview;

	/**
	 * Show new frame.
	 * @param areaId
	 */
	public static void showNewFrame(long areaId) {
		
		try {
			
			AreasTreeEditorFrame frame = new AreasTreeEditorFrame(areaId);
			DialogNavigator.addAreaTreeEditor(frame, true);
	
			frame.reload();
			frame.setVisible(true);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Create the frame.
	 * @param areaId 
	 */
	public AreasTreeEditorFrame(long areaId) {
		
		try {
			setMinimumSize(new Dimension(450, 350));
	
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
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				close();
			}
		});
		setBounds(100, 100, 693, 540);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		radioSubAreas = new JRadioButton("org.multipage.generator.textSubareas");
		radioSubAreas.setOpaque(false);
		springLayout.putConstraint(SpringLayout.NORTH, radioSubAreas, 6, SpringLayout.NORTH, getContentPane());
		radioSubAreas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Safe.tryOnChange(radioSubAreas, () -> {
					reload();
				});
			}
		});
		buttonGroupText.add(radioSubAreas);
		springLayout.putConstraint(SpringLayout.WEST, radioSubAreas, 10, SpringLayout.WEST, getContentPane());
		radioSubAreas.setSelected(true);
		getContentPane().add(radioSubAreas);
		
		radioSuperAreas = new JRadioButton("org.multipage.generator.textSuperareas");
		radioSuperAreas.setOpaque(false);
		springLayout.putConstraint(SpringLayout.NORTH, radioSuperAreas, 0, SpringLayout.NORTH, radioSubAreas);
		springLayout.putConstraint(SpringLayout.WEST, radioSuperAreas, 10, SpringLayout.EAST, radioSubAreas);
		radioSuperAreas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Safe.tryOnChange(radioSuperAreas, () -> {
					reload();
				});
			}
		});
		buttonGroupText.add(radioSuperAreas);
		getContentPane().add(radioSuperAreas);
		
		radioDescriptions = new JRadioButton("org.multipage.generator.textDescriptions");
		springLayout.putConstraint(SpringLayout.NORTH, radioDescriptions, 0, SpringLayout.SOUTH, radioSubAreas);
		radioDescriptions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Safe.tryOnChange(radioDescriptions, () -> {
					reload();
				});
			}
		});
		buttonGroupAreas.add(radioDescriptions);
		springLayout.putConstraint(SpringLayout.WEST, radioDescriptions, 0, SpringLayout.WEST, radioSubAreas);
		radioDescriptions.setSelected(true);
		radioDescriptions.setOpaque(false);
		getContentPane().add(radioDescriptions);
		
		radioAliases = new JRadioButton("org.multipage.generator.textAliases");
		springLayout.putConstraint(SpringLayout.NORTH, radioAliases, 0, SpringLayout.SOUTH, radioSuperAreas);
		springLayout.putConstraint(SpringLayout.WEST, radioAliases, 0, SpringLayout.WEST, radioSuperAreas);
		radioAliases.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Safe.tryOnChange(radioAliases, () -> {
					reload();
				});
			}
		});
		buttonGroupAreas.add(radioAliases);
		radioAliases.setOpaque(false);
		getContentPane().add(radioAliases);
		
		checkInherits = new JCheckBox("org.multipage.generator.textInherits");
		springLayout.putConstraint(SpringLayout.WEST, checkInherits, 10, SpringLayout.EAST, radioSuperAreas);
		checkInherits.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Safe.tryOnChange(checkInherits, () -> {
					reload();
				});
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, checkInherits, 0, SpringLayout.NORTH, radioSubAreas);
		checkInherits.setOpaque(false);
		getContentPane().add(checkInherits);
		
		buttonClose = new JButton("textClose");
		springLayout.putConstraint(SpringLayout.SOUTH, buttonClose, -6, SpringLayout.SOUTH, getContentPane());
		buttonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		springLayout.putConstraint(SpringLayout.EAST, buttonClose, -10, SpringLayout.EAST, getContentPane());
		buttonClose.setPreferredSize(new Dimension(80, 25));
		buttonClose.setMargin(new Insets(0, 0, 0, 0));
		getContentPane().add(buttonClose);
		
		buttonReload = new JButton("org.multipage.generator.textReload");
		buttonReload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reload();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, buttonReload, 0, SpringLayout.NORTH, buttonClose);
		springLayout.putConstraint(SpringLayout.WEST, buttonReload, 0, SpringLayout.WEST, radioSubAreas);
		buttonReload.setPreferredSize(new Dimension(80, 25));
		buttonReload.setMargin(new Insets(0, 0, 0, 0));
		getContentPane().add(buttonReload);
		
		checkShowIds = new JCheckBox("org.multipage.generator.textShowIds");
		checkShowIds.setOpaque(false);
		checkShowIds.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Safe.tryOnChange(checkShowIds, () -> {
					reload();
				});
			}
		});
		springLayout.putConstraint(SpringLayout.WEST, checkShowIds, 0, SpringLayout.WEST, checkInherits);
		springLayout.putConstraint(SpringLayout.SOUTH, checkShowIds, 0, SpringLayout.SOUTH, radioDescriptions);
		getContentPane().add(checkShowIds);
		
		splitPane = new JSplitPane();
		springLayout.putConstraint(SpringLayout.WEST, splitPane, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, splitPane, -10, SpringLayout.EAST, getContentPane());
		splitPane.setBorder(null);
		springLayout.putConstraint(SpringLayout.NORTH, splitPane, 6, SpringLayout.SOUTH, checkShowIds);
		springLayout.putConstraint(SpringLayout.SOUTH, splitPane, -6, SpringLayout.NORTH, buttonClose);
		splitPane.setResizeWeight(0.5);
		splitPane.setOneTouchExpandable(true);
		getContentPane().add(splitPane);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		splitPane.setLeftComponent(tabbedPane);
		springLayout.putConstraint(SpringLayout.NORTH, tabbedPane, 6, SpringLayout.SOUTH, radioDescriptions);
		springLayout.putConstraint(SpringLayout.EAST, tabbedPane, 0, SpringLayout.EAST, radioAliases);
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Safe.invokeLater(() -> {
					Safe.tryOnChange(tabbedPane, () -> {
						reload();
					});
				});
			}
		});
		springLayout.putConstraint(SpringLayout.WEST, tabbedPane, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, tabbedPane, -6, SpringLayout.NORTH, buttonClose);
		
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
		tree.setBorder(null);
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
		tabbedPane.addTab("org.multipage.generator.textSearchAreas", null, panelList, null);
		SpringLayout sl_panelList = new SpringLayout();
		panelList.setLayout(sl_panelList);
		
		labelFilter = new JLabel("org.multipage.generator.textFilter");
		sl_panelList.putConstraint(SpringLayout.NORTH, labelFilter, 10, SpringLayout.NORTH, panelList);
		sl_panelList.putConstraint(SpringLayout.WEST, labelFilter, 10, SpringLayout.WEST, panelList);
		panelList.add(labelFilter);
		
		textFilter = new JTextField();
		sl_panelList.putConstraint(SpringLayout.NORTH, textFilter, 10, SpringLayout.NORTH, panelList);
		sl_panelList.putConstraint(SpringLayout.WEST, textFilter, 6, SpringLayout.EAST, labelFilter);
		sl_panelList.putConstraint(SpringLayout.EAST, textFilter, 197, SpringLayout.EAST, labelFilter);
		panelList.add(textFilter);
		textFilter.setColumns(10);
		
		checkCaseSensitive = new JCheckBox("org.multipage.generator.textCaseSensitive");
		sl_panelList.putConstraint(SpringLayout.NORTH, checkCaseSensitive, 0, SpringLayout.SOUTH, textFilter);
		checkCaseSensitive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Safe.tryOnChange(checkCaseSensitive, () -> {
					reload();
				});
			}
		});
		sl_panelList.putConstraint(SpringLayout.WEST, checkCaseSensitive, 0, SpringLayout.WEST, labelFilter);
		panelList.add(checkCaseSensitive);
		
		checkWholeWords = new JCheckBox("org.multipage.generator.textWholeWords");
		sl_panelList.putConstraint(SpringLayout.NORTH, checkWholeWords, 0, SpringLayout.NORTH, checkCaseSensitive);
		checkWholeWords.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Safe.tryOnChange(checkWholeWords, () -> {
					reload();
				});
			}
		});
		sl_panelList.putConstraint(SpringLayout.WEST, checkWholeWords, 6, SpringLayout.EAST, checkCaseSensitive);
		panelList.add(checkWholeWords);
		
		checkExactMatch = new JCheckBox("org.multipage.generator.textExactMatch");
		sl_panelList.putConstraint(SpringLayout.NORTH, checkExactMatch, 0, SpringLayout.NORTH, checkCaseSensitive);
		checkExactMatch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Safe.tryOnChange(checkExactMatch, () -> {
					reload();
				});
			}
		});
		sl_panelList.putConstraint(SpringLayout.WEST, checkExactMatch, 6, SpringLayout.EAST, checkWholeWords);
		panelList.add(checkExactMatch);
		
		scrollList = new JScrollPane();
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
		sl_panelList.putConstraint(SpringLayout.NORTH, textLevels, 0, SpringLayout.NORTH, labelFilter);
		sl_panelList.putConstraint(SpringLayout.WEST, textLevels, 6, SpringLayout.EAST, labelLevels);
		sl_panelList.putConstraint(SpringLayout.EAST, textLevels, 51, SpringLayout.EAST, labelLevels);
		textLevels.setColumns(10);
		panelList.add(textLevels);
		
		labelFoundAreasCount = new JLabel("org.multipage.generator.textFoundAreasCount");
		sl_panelList.putConstraint(SpringLayout.SOUTH, scrollList, 0, SpringLayout.NORTH, labelFoundAreasCount);
		sl_panelList.putConstraint(SpringLayout.WEST, labelFoundAreasCount, 0, SpringLayout.WEST, panelList);
		sl_panelList.putConstraint(SpringLayout.SOUTH, labelFoundAreasCount, 0, SpringLayout.SOUTH, panelList);
		panelList.add(labelFoundAreasCount);
		{
			splitPaneProviders = new JSplitPane();
			splitPaneProviders.setBorder(null);
			splitPaneProviders.setResizeWeight(0.6);
			splitPaneProviders.setOrientation(JSplitPane.VERTICAL_SPLIT);
			splitPane.setRightComponent(splitPaneProviders);
			{
				scrollPane = new JScrollPane();
				scrollPane.setBorder(null);
				splitPaneProviders.setRightComponent(scrollPane);
				{
					editorSlotPreview = new TextPaneEx();
					editorSlotPreview.setBackground(UIManager.getColor("EditorPane.disabledBackground"));
					editorSlotPreview.setBorder(null);
					editorSlotPreview.setFont(new Font("DialogInput", Font.PLAIN, 12));
					editorSlotPreview.setEditable(false);
					scrollPane.setViewportView(editorSlotPreview);
				}
			}
		}
		
		toolBarMain = new JToolBar();
		toolBarMain.setOpaque(false);
		springLayout.putConstraint(SpringLayout.NORTH, toolBarMain, 3, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, toolBarMain, -40, SpringLayout.EAST, getContentPane());
		toolBarMain.setFloatable(false);
		getContentPane().add(toolBarMain);
	}
	
	/**
	 * On [ESC] key.
	 */
	protected void onTreeEscapeKey() {
		try {
			Safe.tryUpdate(tree, () -> {
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
			Safe.tryUpdate(list, () -> {
				list.clearSelection();
			});
			displayAreaProperties();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Get top area ID.
	 * @return
	 */
	public long getAreaId() {
		
		return areaId;
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
			
			// Update application components.
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
			Area parentArea = (Area) node.getUserObject();
			
			// Add new area.
			Obj<Area> newArea = new Obj<Area>();
			if (GeneratorMainFrame.getVisibleAreasDiagram().addNewArea(parentArea, this, newArea, false)) {
			
				// Select and expand area item.
				if (newArea.ref != null) {
					
					GeneratorMainFrame.updateAll();
					Safe.tryUpdate(tree, () -> {
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
			Safe.tryUpdate(tree, () -> {
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
			return checkShowIds.isSelected();
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
			
			// Add top most window button.
			TopMostButton.add(this, getContentPane()); //$hide$
			
			// Create and set areas properties panel.
			areaPropertiesPanel = ProgramGenerator.newAreasProperties(true);
			panelMessage = new MessagePanel();
			
			setNoAreasSelectedMessage();
			
			// Load dialog.
			loadDialog();
			
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			// Localize components.
			localize();
			// Set icons.
			setIcons();
			// Set colors.
			setColors();
			// Create tool bar.
			createToolBars();
			// Create list.
			createList();
			// Create tree.
			createTree();
			// Set listeners.
			setListeners();
			// Create popup menus.
			createPopupMenus();
			// Switch on or off debugging of PHP code
			boolean selected = Settings.getEnableDebugging();
			toggleDebug.setSelected(selected);
			
			setSlotSelectionListener();
			
			// Register this frame for updates.
			GeneratorMainFrame.registerForUpdate(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set slot selection listener
	 */
	private void setSlotSelectionListener() {
		try {
			
			areaPropertiesPanel.setSlotSelectedEvent(slot -> {
				try {
					
					editorSlotPreview.setText(slot.getTextValue());
					editorSlotPreview.setCaretPosition(0);
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
	 * Set no areas selected message.
	 */
	private void setNoAreasSelectedMessage() {
		try {
			
			panelMessage.setText(Resources.getString("org.multipage.generator.textNoAreaSelected"));
			splitPaneProviders.setLeftComponent(panelMessage);
			editorSlotPreview.setText("");
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
			
			if (loadDescriptionsState) {
				radioDescriptions.setSelected(true);
			}
			else {
				radioAliases.setSelected(true);
			}
			
			if (loadSubAreasState) {
				radioSubAreas.setSelected(true);
			}
			else {
				radioSuperAreas.setSelected(true);
			}
			
			Safe.tryUpdate(tabbedPane, () -> {
				tabbedPane.setSelectedIndex(selectedTabIndexState);
			});
			
			Safe.tryUpdate(checkInherits, () -> {
				checkInherits.setSelected(inheritState);
			});
			Safe.tryUpdate(checkShowIds, () -> {
				checkShowIds.setSelected(showIdsState);
			});
			Safe.tryUpdate(checkCaseSensitive, () -> {
				checkCaseSensitive.setSelected(caseSensitiveState);
			});
			Safe.tryUpdate(checkWholeWords, () -> {
				checkWholeWords.setSelected(wholeWordsState);
			});
			Safe.tryUpdate(checkExactMatch, () -> {
				checkExactMatch.setSelected(exactMatchState);
			});
			Safe.tryUpdate(textFilter, () -> {
				textFilter.setText(filterState);
			});
			Safe.tryUpdate(textLevels, () -> {
				textLevels.setText(levelsState);
			});
			
			if (bounds.isEmpty()) {
				// Center dialog.
				Utility.centerOnScreen(this);
				bounds = getBounds();
			}
			else {
				setBounds(bounds);
			}
			
			if (splitterPosition != -1) {
				splitPane.setDividerLocation(splitterPosition);
			}
			if (splitter2Position != -1) {
				splitPaneProviders.setDividerLocation(splitter2Position);
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
						if (selectedPaths.length != 1) {
							return null;
						}
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectedPaths[0].getLastPathComponent();
						Area area = (Area) node.getUserObject();
						return ProgramGenerator.getAreasModel().getArea(area.getId());
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
						if (selectedPaths.length != 1) {
							return null;
						}
						
						TreePath selectedPath = selectedPaths[0];
						int elementsCount = selectedPath.getPathCount();
						
						if (elementsCount < 2) {
							return null;
						}
		
						DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedPath.getPathComponent(elementsCount - 2);
						Area parentArea = (Area) parentNode.getUserObject();
						return ProgramGenerator.getAreasModel().getArea(parentArea.getId());
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
						
						GeneratorMainFrame.updateAll();
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
						List<Object> selected = list.getSelectedValuesList();
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
			
			// Add new trayMenu items.
			areaMenuTree.addTo(this, popupMenuTree);
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
			
			// Main tool bar.
			toggleDebug = ToolBarKit.addToggleButton(toolBarMain,  "org/multipage/generator/images/debug.png", "org.multipage.generator.tooltipEnableDisplaySourceCode", () -> onToggleDebug());
			toggleDebug.setVisible(false);
			ToolBarKit.addToolBarButton(toolBarMain, "org/multipage/generator/images/render.png", "org.multipage.generator.tooltipRenderHtmlPages", () -> onRender());
			toolBarMain.addSeparator();
			ToolBarKit.addToolBarButton(toolBarMain, "org/multipage/generator/images/display_home_page.png", "org.multipage.generator.tooltipDisplayHomePage", () -> onDisplayHomePage());
			
			// Area tree tool bar.
			ToolBarKit.addToolBarButton(toolBarTree, "org/multipage/generator/images/expand_icon.png", "org.multipage.generator.tooltipExpandTree", () -> onExpandTree());
			ToolBarKit.addToolBarButton(toolBarTree, "org/multipage/generator/images/collapse_icon.png", "org.multipage.generator.tooltipCollapseTree", () -> onCollapseTree());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On render HTML pages.
	 */
	private void onRender() {
		try {
			
			GeneratorMainFrame.getFrame().onRender(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On display home page.
	 */
	private void onDisplayHomePage() {
		try {
			
			// Transmit the "monitor home page" signal.
			ApplicationEvents.transmit(this, GuiSignal.displayHomePage);
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
	 * On switch on or off debugging
	 */
	public void onToggleDebug() {
		try {
			
			if (toggleDebug == null) {
				return;
			}
			final boolean enable = toggleDebug.isSelected();
			
			// Switch on or off debugging of PHP code
			Settings.setEnableDebugging(enable);
			
			// Transmit the "enable / disable" signal.
			ApplicationEvents.transmit(this, GuiSignal.debugging, enable);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set button that enables debugging
	 * @param selected
	 */
	public void setEnableDebugging(boolean selected) {
		try {
			
			if (toggleDebug == null) {
				return;
			}
			toggleDebug.setSelected(selected);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set listeners.
	 */
	private void setListeners() {
		try {
			
			DocumentListener listener = new DocumentListener() {
				@Override
				public void removeUpdate(DocumentEvent e) {
					try {
						reload();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				@Override
				public void insertUpdate(DocumentEvent e) {
					try {
						reload();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				@Override
				public void changedUpdate(DocumentEvent e) {
					try {
						reload();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			};
			
			textFilter.getDocument().addDocumentListener(listener);
			textLevels.getDocument().addDocumentListener(listener);
			
			// On tree item selection.
			tree.addTreeSelectionListener(new TreeSelectionListener() {
			    public void valueChanged(TreeSelectionEvent e) {
			    	try {
			    		displayAreaProperties();
			    	}
			    	catch(Throwable expt) {
			    		Safe.exception(expt);
			    	};
			    }
			});
			
			// Set list selection listener.
			list.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent arg0) {
					try {
						displayAreaProperties();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			
			// Receive the "debugging" signal.
			ApplicationEvents.receiver(this, GuiSignal.debugging, message -> {
				try {
			
					// Avoid receiving the signal from current dialog window.
					if (this.equals(message.source)) {
						return;
					}
					
					// Get flag value.
					Boolean debuggingEnabled = message.getRelatedInfo();
					if (debuggingEnabled == null) {
						return;
					}
					
					// Select or unselect the debug button.
					toggleDebug.setSelected(debuggingEnabled);
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
	 * On display properties for selected areas.
	 */
	private void displayAreaProperties() {
		try {
			
			int location = splitPane.getDividerLocation();
	    	int location2 = splitPaneProviders.getDividerLocation();
	    	
	    	HashSet<Long> areaIds = getSelectedTabAreaIds();
	    	
			// Set areas properties panel.
	    	areaPropertiesPanel.setAreasFromIds(areaIds);
	    	splitPaneProviders.setLeftComponent(areaPropertiesPanel);

			// Clear slot preview
			editorSlotPreview.setText("");
	    	
	    	splitPane.setDividerLocation(location);
	    	splitPaneProviders.setDividerLocation(location2);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
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
		    		
		    		Area area = (Area) node.getUserObject();
		    		Long areaId = area.getId();
		    		
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
		if (selectedAreaIds == null || selectedAreaIds.isEmpty()) {
			return selectedTreeAreaIds;
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
		if (selectedAreaIds == null || selectedAreaIds.isEmpty()) {
			return selectedListAreaIds;
		}
		return selectedAreaIds;
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
							itemRenderer.setProperties("#renderer error#", 0, selected, hasFocus, false);
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
						Area area = (Area) treeNode.getUserObject();
						boolean isHomeArea = ProgramGenerator.getAreasModel().isHomeArea(area);
						boolean isVisible = area.isVisible();
						boolean isDisabled = !area.isEnabled();
						
						itemRenderer.setAreaVisible(isVisible);
						
						// Get area text.
						String text  = radioAliases.isSelected() ? area.getAlias()
								: area.getDescriptionForced(showIds());
						
						// Get sub relation names.
						if (parentTreeNode != null) {
							Area parentArea = (Area) parentTreeNode.getUserObject();
			
							String subName = parentArea.getSubRelationName(area.getId());
							String superName = area.getSuperRelationName(parentArea.getId());
							boolean hiddenSubareas = parentArea.isSubareasHidden(area);
							
							itemRenderer.setProperties(text, subName, superName, hiddenSubareas, 0, selected, hasFocus, isHomeArea);
						}
						else {
							itemRenderer.setProperties(text, 0, selected, hasFocus, isHomeArea);
						}
						
						// If the area is disabled, gray its name
						if (isDisabled) {
							itemRenderer.setForeground(Color.GRAY);
						}
					
						// Display permanent red selections.
						boolean isSelected = selectedTreeAreaIds.contains(area.getId());
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
			
			// Set Drag and Drop callback.
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
						
						if (!(transferredObject instanceof Area && droppedObject instanceof Area)) {
							e.rejectDrop();
							return;
						}
						
						Area transferredParentArea = null;
						if (transferredNodeParent instanceof DefaultMutableTreeNode) {
							
							DefaultMutableTreeNode transferredMutableNodeParent = (DefaultMutableTreeNode) transferredNodeParent;
							Object userObject = transferredMutableNodeParent.getUserObject();
							
							if (userObject instanceof Area) {
								transferredParentArea = (Area) userObject;
							}
						}
						
						Area droppedParentArea = null;
						if (droppedNodeParent instanceof DefaultMutableTreeNode) {
							
							DefaultMutableTreeNode droppedMutableNodeParent = (DefaultMutableTreeNode) droppedNodeParent;
							Object userObject = droppedMutableNodeParent.getUserObject();
							
							if (userObject instanceof Area) {
								droppedParentArea = (Area) userObject;
							}
						}
						
						int action = e.getDropAction();
						
						LinkedList<Area> transferredAreas = new LinkedList<Area>();
						transferredAreas.add((Area)transferredObject);
						
						GeneratorMainFrame.transferArea(
								transferredAreas, transferredParentArea,
								(Area)droppedObject, droppedParentArea,
								action, thisComponent);
						
						// Update GUI components.
						GeneratorMainFrame.updateAll();
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
					
					try {
						itemRenderer.setForeground(Color.BLACK);
						
						if (!(value instanceof Area)) {
							return null;
						}
						Area area = (Area) value;
						String text  = radioAliases.isSelected() ? area.getAlias()
								: area.getDescriptionForced(showIds());
						
						boolean isHomeArea = ProgramGenerator.getAreasModel().isHomeArea(area);
						
						itemRenderer.setProperties(text, index, isSelected, cellHasFocus, isHomeArea);

						// Display permanent red selections.
						boolean isAreaSelected = selectedListAreaIds.contains(area.getId());
						if (isAreaSelected) {
							
							itemRenderer.setForeground(Color.RED);
						}
					}
					catch (Throwable e) {
						Safe.exception(e);
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
	 * Localize components.
	 */
	private void localize() {
		try {
			
			Utility.localize(buttonClose);
			Utility.localize(checkInherits);
			Utility.localize(labelLevels);
			Utility.localize(radioAliases);
			Utility.localize(radioDescriptions);
			Utility.localize(radioSubAreas);
			Utility.localize(radioSuperAreas);
			Utility.localize(tabbedPane);
			Utility.localize(buttonReload);
			Utility.localize(labelFilter);
			Utility.localize(checkCaseSensitive);
			Utility.localize(checkWholeWords);
			Utility.localize(checkExactMatch);
			Utility.localize(checkShowIds);
			Utility.localize(menuSelectSubNodes);
			Utility.localize(menuAddSubArea);
			Utility.localize(menuRemoveArea);
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
			buttonClose.setIcon(Images.getIcon("org/multipage/generator/images/cancel_icon.png"));
			buttonReload.setIcon(Images.getIcon("org/multipage/generator/images/update_icon.png"));
			menuSelectSubNodes.setIcon(Images.getIcon("org/multipage/generator/images/select_subnodes.png"));
			menuAddSubArea.setIcon(Images.getIcon("org/multipage/generator/images/area_node.png"));
			menuRemoveArea.setIcon(Images.getIcon("org/multipage/generator/images/cancel_icon.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set colors.
	 */
	private void setColors() {
		try {
			
			getContentPane().setBackground(CustomizedColors.get(ColorId.AREA_TREE_FRAME));
			areaPropertiesPanel.setBackground(CustomizedColors.get(ColorId.AREA_TREE_FRAME));
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Close the frame.
	 */
	@Override
	public void close() {
		try {
			
			DialogNavigator.removeAreaTreeEditor(this);
			
			// Unregister from updates.
			GeneratorMainFrame.unregisterFromUpdate(this);
			
			saveDialog();
			
			ApplicationEvents.removeReceivers(this);
			areaPropertiesPanel.close();
			
			dispose();
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
			
			loadDescriptionsState = radioDescriptions.isSelected();
			loadSubAreasState = radioSubAreas.isSelected();
			selectedTabIndexState = tabbedPane.getSelectedIndex();
			inheritState = checkInherits.isSelected();
			showIdsState = checkShowIds.isSelected();
			caseSensitiveState = checkCaseSensitive.isSelected();
			wholeWordsState = checkWholeWords.isSelected();
			exactMatchState = checkExactMatch.isSelected();
			filterState = textFilter.getText();
			levelsState = textLevels.getText();
			
			bounds = getBounds();
			splitterPosition = splitPane.getDividerLocation();
			splitter2Position = splitPaneProviders.getDividerLocation();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Reload dialog.
	 */
	private void reload() {
		try {
			
			Safe.invokeLater(() -> {
				
				// Get split pane divider position.
				int splitPaneDividerLocation = splitPane.getDividerLocation();
				int splitPane2DividerLocation = splitPaneProviders.getDividerLocation();
				
				// Reset area editor.
				areaPropertiesPanel.setAreas(null);
				setNoAreasSelectedMessage();
				
				// Clear slot preview.
				editorSlotPreview.setText("");
				
				// Get selected area.
				Area selectedArea = (Area) list.getSelectedValue();
				Long selectedAreaId = null;
				
				if (selectedArea != null) {
					selectedAreaId = selectedArea.getId();
				}
				
				// Set tab icon and text.
				boolean isSubareas = radioSubAreas.isSelected();
				String iconPath = "org/multipage/generator/images/" + (isSubareas ? "subareas" 
						: "superareas") + ".png";
				
				tabbedPane.setIconAt(0, Images.getIcon(iconPath));
				tabbedPane.setTitleAt(0, Resources.getString(
						isSubareas ? "org.multipage.generator.textSubAreasTree" : "org.multipage.generator.textSuperAreasTree"));
				setIconImage(Images.getImage(iconPath));
		
				// Set title.
				Area area = ProgramGenerator.getAreasModel().getArea(areaId);
				String areaName = area != null ? area.getDescriptionForced(showIds())
						: Resources.getString("org.multipage.generator.textUnknownArea");
				
				setTitle(String.format(Resources.getString("org.multipage.generator.textAreaTitle"), areaName));
		
				// Get selected tab.
				int selectedTabIndex = tabbedPane.getSelectedIndex();
				boolean isTreeTab = (selectedTabIndex == 0);
				// Get selected text type.
				boolean isDescription = radioDescriptions.isSelected();
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
				Safe.tryUpdate(checkInherits, () -> {
					checkInherits.setEnabled(!isSubareas);
				});
				tree.enableDragAndDrop(isSubareas);
				
				// Update tree.
				if (isTreeTab) {
					
					// Get tree state.
					AreaTreeState treeState = AreaTreeState.getTreeState(tree);
					
					// Load tree.
					Safe.tryUpdate(tree, () -> {
						updateTreeModel(treeModel, areaId, isSubareas, inheritance);
					});
								
					// Apply tree state.
					Safe.tryUpdate(tree, () -> {
						AreaTreeState.applyTreeState(treeState, tree);
					});
					
					// Expand tree root.
					Safe.invokeLater(() -> {
						Utility.expandTop(tree, true);
					});
				}
				else {
					// Update list.
					// Cleare it.
					Safe.tryUpdate(list, () -> {
						listModel.clear();
					});
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
					Safe.tryUpdate(list, () -> {
						for (Area areaSorted : areasSorted) {
							listModel.addElement(areaSorted);
						}
					});
					
					// Set areas count.
					labelFoundAreasCount.setText(String.format(
							Resources.getString("org.multipage.generator.textFoundAreasCount"), areasSorted.size()));
					
					// Select area.
					if (selectedAreaId != null) {
						final long areaId = selectedAreaId;
						Safe.tryUpdate(() -> {
							selectArea(areaId);
						}, list, tree);
					}
				}
				
				// Set split pane location.
				splitPane.setDividerLocation(splitPaneDividerLocation);
				splitPaneProviders.setDividerLocation(splitPane2DividerLocation);
				
				tree.updateUI();
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
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
			
			// Get root area.
			Area rootArea = ProgramGenerator.getArea(rootAreaId);
			if (rootArea == null) {
				treeModel.setRoot(null);
				return;
			}
			
			// Create root node.
			DefaultMutableTreeNode rootNode = new DefaultMutableTreeNodeDnD(rootArea);
			
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
			if (!(userObject instanceof Area)) {
				return;
			}
			
			Area area = (Area) userObject;
			
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
					areas = area.getInheritsFrom();
				}
			}
			
			for (Area areaItem : areas) {
				
				// Create new node.
				DefaultMutableTreeNode childNode = new DefaultMutableTreeNodeDnD(areaItem);
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
	 * Select area.
	 * @param areaId
	 */
	private void selectArea(long areaId) {
		try {
			
			// Clear list and tree selection.
			list.clearSelection();
			tree.clearSelection();
			
			// Select area in the tree view.
			Utility.traverseElements(tree, userObject -> node -> parentNode -> {
				
				try {
					if (!(userObject instanceof Area)) {
						return false;
					}
					
					Area area = (Area) userObject;
					if (area.getId() == areaId) {
						
						TreeNode [] nodes = node.getPath();
						TreePath treePath = new TreePath(nodes);
						
						tree.setSelectionPath(treePath);
						
						// Expand parent item.
					    tree.expandPath(treePath);
					    return true;
					}
				}
				catch (Throwable e) {
					Safe.exception(e);
				}
				return false;
			});
			
			// Select area in the list view.
			int count = listModel.getSize();
			for (int index = 0; index < count; index++) {
				
				Area area = (Area) listModel.get(index);
				if (area.getId() == areaId) {
					
					list.setSelectedIndex(index);
					list.ensureIndexIsVisible(index);
					break;
				}
			}
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
				
				boolean isSubAreas = radioSubAreas.isSelected();
				
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
	 * Redraw information.
	 */
	public static void redrawInformation() {
		try {
			
			Utility.traverseUI(component -> {
				
				try {
					if (component instanceof AreasTreeEditorFrame) {
						AreasTreeEditorFrame traceFrame = (AreasTreeEditorFrame) component;
						
						traceFrame.tree.repaint();
						traceFrame.list.repaint();
					}
				}
				catch (Throwable e) {
					Safe.exception(e);
				}
				return true;
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get list of previous messages.
	 */
	@Override
	public LinkedList<Message> getPreviousMessages() {
		
		return previousMessages;
	}
	
	/**
	 * Set automatic removal of application events receivers.
	 */
	@Override
	public boolean canAutoRemove() {
		
		return true;
	}
	
	/**
	 * Called when the component should be updated.
	 */
	@Override
	public void updateComponents() {
		try {
			
			// Reload the frame.
			reload();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
