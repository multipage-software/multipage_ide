/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2018-07-31
 *
 */
package org.maclan.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.multipage.gui.AlertWithTimeout;
import org.multipage.gui.ApplicationEvents;
import org.multipage.gui.EditorPaneEx;
import org.multipage.gui.FindReplaceDialog;
import org.multipage.gui.GuiSignal;
import org.multipage.gui.Images;
import org.multipage.gui.RendererJLabel;
import org.multipage.gui.Signal;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.TextFieldEx;
import org.multipage.gui.ToolBarKit;
import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Class with GUI for the debugger.
 * @author vakol
 *
 */
public class DebugViewer extends JFrame {

	/**
	 * Version
	 */
	private static final long serialVersionUID = 1L;
	
	// $hide>>$
	
	/**
	 * Transaction timeout in milliseconds.
	 */
	private static final int RESPONSE_TIMEOUT_MS = 3000;
	
	/**
	 * Interlinear annotation characters.
	 */
	private static final char INTERLINEAR_ANNOTATION_ANCHOR = '\uFFF9';
	private static final char INTERLINEAR_ANNOTATION_TERMINATOR = '\uFFFB';

	/**
	 * Column indices for table displaying watched items.
	 */
	private static final int WATCHED_NAME_COLUMN_INDEX = 0;
	private static final int WATCHED_FULLNAME_COLUMN_INDEX = 1;
	private static final int WATCHED_PROPERTY_TYPE_COLUMN_INDEX = 2;
	private static final int WATCHED_VALUE_COLUMN_INDEX = 3;
	private static final int WATCHED_VALUE_TYPE_COLUMN_INDEX = 4;
	
	/**
	 * Window boundary.
	 */
	private static Rectangle bounds = null;
	
	/**
	 * Main splitter position.
	 */
	private static int mainSplitterPosition = 0;
	
	/**
	 * Watch list splitter position.
	 */
	private static int watchSplitterPosition = 0;

    /**
	 * Singleton debug viewer object.
	 */
    private static DebugViewer debugViewerInstance = null;
    
	/**
	 * Lines of code to be displayed or null value if there is nothing to display.
	 */
	private LinkedList<String> codeLines = null;
	
	/**
	 * Current session object.
	 */
	private XdebugListenerSession currentSession = null;
	
	/**
	 * Sessions tree model and a root node.
	 */
	private DefaultTreeModel treeSessionsModel = null;
	private DefaultMutableTreeNode sessionsRootNode = null;
	
	/**
	 * Watch tree and table models.
	 */
	private DefaultTreeModel treeWatchModel = null;
	private DefaultMutableTreeNode watchRootNode = null;
	private DefaultTableModel tableWatchModel = null;
	
	/**
	 * Attached debugger listener.
	 */
	private XdebugListener debugListener = null;
	
	/**
	 * Flag that enables events on sessions tree.
	 */
	private boolean enableTreeSessionsEvents = true;
	
	/**
     * Flag that indicates loading of watch tree.
     */
	private boolean loadWatchListValuesRunning = false;
	
	/**
	 * Find and replace dialog.
	 */
	private FindReplaceDialog findDialog = null;
	
	// $hide<<$
	
	/**
	 * Controls
	 */
	private JEditorPane textCode;
	private JEditorPane textInfo;
	private JTextField textCommand;
	private JButton buttonSend;
	private JPanel panelBottom;
	private JTabbedPane tabbedPane;
	private JPanel panelWatch;
	private JPanel panelCommand;
	private JScrollPane scrollPaneOutput;
	private JPanel panelRight;
	private JPanel panelLeft;
	private JToolBar toolBarMenu;
	private JButton buttonRun;
	private JScrollPane scrollCodePane;
	private JPanel panelStatus;
	private JLabel labelStatus;
	private JButton buttonStop;
	private JButton buttonStepInto;
	private JButton buttonStepOut;
	private JButton buttonStepOver;
	private JPanel panelOutput;
	private JScrollPane scrollPane;
	private JTextArea textOutput;
	private JPanel panelExceptions;
	private JScrollPane scrollPaneExceptions;
	private JPanel panelSearch;
	private JLabel labelFilter;
	private TextFieldEx textFilter;
	private JTextPane textExceptions;
	private JCheckBox checkCaseSensitive;
	private JCheckBox checkWholeWords;
	private JCheckBox checkExactMatch;
	private JPanel panelDebuggers;
	private JButton buttonConnected;
	private JLabel labelThreads;
	private JPanel panelThreads;
	private JTree treeSessions;
	private JSplitPane splitPane;
	private JTable tableWatch;
	private JTree treeWatch;
	private JLabel labelWatchName;
	private JLabel labelWatchValue;
	private JSplitPane splitPaneWatch;
	private JPopupMenu popupMenuWatch;
	private JMenuItem menuAddToWatch;
	private JMenuItem menuRemoveFromWatch;
	private JLabel labelSourceInfo;
	private JToolBar toolBarSourceInfo;
	private JTextPane textPaneSourceInfo;
	private JToolBar toolBarText;
	
	/**
	 * Set default window properties.
	 */
	public static void setDefaultData() {
		
		bounds = new Rectangle();
		mainSplitterPosition = 500;
		watchSplitterPosition = 150;
	}
	
	/**
	 * Load window properties.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void serializeData(StateInputStream inputStream)
			throws IOException, ClassNotFoundException {
		
		bounds = Utility.readInputStreamObject(inputStream, Rectangle.class);
		mainSplitterPosition = inputStream.readInt();
		watchSplitterPosition = inputStream.readInt();
	}
	
	/**
	 * Save window properties.
	 * @param outputStream
	 */
	public static void seriliazeData(StateOutputStream outputStream)
		throws IOException {
		
		outputStream.writeObject(bounds);
		outputStream.writeInt(mainSplitterPosition);
		outputStream.writeInt(watchSplitterPosition);
	}
    
    /**
     * Get debug viewer singleton object.
     * @param parent 
     * @return
     */
    public static DebugViewer getInstance(Component parent) {
    	
    	try {
	        if (debugViewerInstance == null) {
	            debugViewerInstance = new DebugViewer(parent);
	        }
	        return debugViewerInstance;
    	}
    	catch (Exception e) {
    		onExceptionStatic(e);
    		return null;
    	}
    }

	/**
     * Get debug viewer singleton.
     * @return
     */
	public static DebugViewer getInstance() {
		
		// Delegate the call.
		return getInstance(null);
	}
	
    /**
     * Constructor of debug viewer.
     * @param parent 
     */
    private DebugViewer(Component parent) {
    	
    	try {
			initComponents();
			postCreate(); // $hide$
    	}
    	catch (Exception e) {
    		onException(e);
        }
    }

	/**
	 * Initialize window components.
	 */
	private void initComponents() {
		
		setBounds(100, 100, 909, 654);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		splitPane = new JSplitPane();
		splitPane.setMinimumSize(new Dimension(0, 0));
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(1.0);
		getContentPane().add(splitPane);
		
		panelRight = new JPanel();
		splitPane.setRightComponent(panelRight);
		panelRight.setLayout(new BorderLayout(0, 0));
		
		tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		panelRight.add(tabbedPane, BorderLayout.CENTER);
		
		panelDebuggers = new JPanel();
		tabbedPane.addTab("org.multipage.generator.textDebuggerProcesses", null, panelDebuggers, null);
		SpringLayout sl_panelDebuggers = new SpringLayout();
		panelDebuggers.setLayout(sl_panelDebuggers);
		
		labelThreads = new JLabel("org.multipage.generator.textDebuggedThreads");
		sl_panelDebuggers.putConstraint(SpringLayout.WEST, labelThreads, 3, SpringLayout.WEST, panelDebuggers);
		sl_panelDebuggers.putConstraint(SpringLayout.NORTH, labelThreads, 3, SpringLayout.NORTH, panelDebuggers);
		panelDebuggers.add(labelThreads);
		
		panelThreads = new JPanel();
		sl_panelDebuggers.putConstraint(SpringLayout.NORTH, panelThreads, 3, SpringLayout.SOUTH, labelThreads);
		sl_panelDebuggers.putConstraint(SpringLayout.WEST, panelThreads, 3, SpringLayout.WEST, panelDebuggers);
		sl_panelDebuggers.putConstraint(SpringLayout.SOUTH, panelThreads, -3, SpringLayout.SOUTH, panelDebuggers);
		sl_panelDebuggers.putConstraint(SpringLayout.EAST, panelThreads, -3, SpringLayout.EAST, panelDebuggers);
		panelDebuggers.add(panelThreads);
		panelThreads.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPaneThreads = new JScrollPane();
		panelThreads.add(scrollPaneThreads);
		
		treeSessions = new JTree();
		treeSessions.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				onSelectTreeNode();
			}
		});
		treeSessions.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int clickCount = e.getClickCount();
				if (clickCount == 2) {
					onDoubleClickProcessNode();
				}
			}
		});
		treeSessions.setRootVisible(false);
		scrollPaneThreads.setViewportView(treeSessions);
		
		JPanel panelSourceInfo = new JPanel();
		panelSourceInfo.setPreferredSize(new Dimension(10, 100));
		panelThreads.add(panelSourceInfo, BorderLayout.SOUTH);
		SpringLayout sl_panelSourceInfo = new SpringLayout();
		panelSourceInfo.setLayout(sl_panelSourceInfo);
		
		labelSourceInfo = new JLabel("org.maclan.server.textCodeSourceInfo");
		sl_panelSourceInfo.putConstraint(SpringLayout.NORTH, labelSourceInfo, 3, SpringLayout.NORTH, panelSourceInfo);
		sl_panelSourceInfo.putConstraint(SpringLayout.WEST, labelSourceInfo, 3, SpringLayout.WEST, panelSourceInfo);
		panelSourceInfo.add(labelSourceInfo);
		
		JScrollPane scrollPaneSourceInfo = new JScrollPane();
		scrollPaneSourceInfo.setBorder(null);
		sl_panelSourceInfo.putConstraint(SpringLayout.NORTH, scrollPaneSourceInfo, 3, SpringLayout.SOUTH, labelSourceInfo);
		sl_panelSourceInfo.putConstraint(SpringLayout.WEST, scrollPaneSourceInfo, 3, SpringLayout.WEST, panelSourceInfo);
		sl_panelSourceInfo.putConstraint(SpringLayout.EAST, scrollPaneSourceInfo, -3, SpringLayout.EAST, panelSourceInfo);
		panelSourceInfo.add(scrollPaneSourceInfo);
		
		toolBarSourceInfo = new JToolBar();
		toolBarSourceInfo.setFloatable(false);
		sl_panelSourceInfo.putConstraint(SpringLayout.SOUTH, scrollPaneSourceInfo, -3, SpringLayout.NORTH, toolBarSourceInfo);
		
		textPaneSourceInfo = new JTextPane();
		textPaneSourceInfo.setEditable(false);
		textPaneSourceInfo.setContentType("text/html");
		textPaneSourceInfo.setBorder(new LineBorder(SystemColor.controlShadow));
		scrollPaneSourceInfo.setViewportView(textPaneSourceInfo);
		sl_panelSourceInfo.putConstraint(SpringLayout.WEST, toolBarSourceInfo, 3, SpringLayout.WEST, panelSourceInfo);
		sl_panelSourceInfo.putConstraint(SpringLayout.SOUTH, toolBarSourceInfo, 0, SpringLayout.SOUTH, panelSourceInfo);
		sl_panelSourceInfo.putConstraint(SpringLayout.EAST, toolBarSourceInfo, 0, SpringLayout.EAST, panelSourceInfo);
		panelSourceInfo.add(toolBarSourceInfo);
		
		panelWatch = new JPanel();
		tabbedPane.addTab("org.multipage.generator.textDebuggerWatch", null, panelWatch, null);
		SpringLayout springLayout = new SpringLayout();
		panelWatch.setLayout(springLayout);
		
		JScrollPane scrollPaneWatch = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollPaneWatch, 0, SpringLayout.NORTH, panelWatch);
		springLayout.putConstraint(SpringLayout.WEST, scrollPaneWatch, 0, SpringLayout.WEST, panelWatch);
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPaneWatch, 0, SpringLayout.SOUTH, panelWatch);
		springLayout.putConstraint(SpringLayout.EAST, scrollPaneWatch, 0, SpringLayout.EAST, panelWatch);
		panelWatch.add(scrollPaneWatch);
		
		splitPaneWatch = new JSplitPane();
		splitPaneWatch.setResizeWeight(0.5);
		scrollPaneWatch.setViewportView(splitPaneWatch);
		
		JPanel paneWatchSplitLeft = new JPanel();
		paneWatchSplitLeft.setPreferredSize(new Dimension(600, 1000));
		splitPaneWatch.setLeftComponent(paneWatchSplitLeft);
		SpringLayout sl_paneWatchSplitLeft = new SpringLayout();
		paneWatchSplitLeft.setLayout(sl_paneWatchSplitLeft);
		
		labelWatchName = new JLabel("org.multipage.generator.textWatchItemName");
		labelWatchName.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		sl_paneWatchSplitLeft.putConstraint(SpringLayout.NORTH, labelWatchName, 0, SpringLayout.NORTH, paneWatchSplitLeft);
		sl_paneWatchSplitLeft.putConstraint(SpringLayout.WEST, labelWatchName, 0, SpringLayout.WEST, paneWatchSplitLeft);
		sl_paneWatchSplitLeft.putConstraint(SpringLayout.EAST, labelWatchName, 0, SpringLayout.EAST, paneWatchSplitLeft);
		paneWatchSplitLeft.add(labelWatchName);
		
		treeWatch = new JTree();
		sl_paneWatchSplitLeft.putConstraint(SpringLayout.NORTH, treeWatch, 0, SpringLayout.SOUTH, labelWatchName);
		sl_paneWatchSplitLeft.putConstraint(SpringLayout.WEST, treeWatch, 0, SpringLayout.WEST, paneWatchSplitLeft);
		sl_paneWatchSplitLeft.putConstraint(SpringLayout.SOUTH, treeWatch, 0, SpringLayout.SOUTH, paneWatchSplitLeft);
		sl_paneWatchSplitLeft.putConstraint(SpringLayout.EAST, treeWatch, 0, SpringLayout.EAST, paneWatchSplitLeft);
		paneWatchSplitLeft.add(treeWatch);
		treeWatch.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				onWatchTreeChange();
			}
		});
		
		JPanel panelWatchSplitRight = new JPanel();
		splitPaneWatch.setRightComponent(panelWatchSplitRight);
		SpringLayout sl_panelWatchSplitRight = new SpringLayout();
		panelWatchSplitRight.setLayout(sl_panelWatchSplitRight);
		
		labelWatchValue = new JLabel("org.multipage.generator.textWatchItemValue");
		labelWatchValue.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		sl_panelWatchSplitRight.putConstraint(SpringLayout.NORTH, labelWatchValue, 0, SpringLayout.NORTH, panelWatchSplitRight);
		sl_panelWatchSplitRight.putConstraint(SpringLayout.WEST, labelWatchValue, 0, SpringLayout.WEST, panelWatchSplitRight);
		sl_panelWatchSplitRight.putConstraint(SpringLayout.EAST, labelWatchValue, 0, SpringLayout.EAST, panelWatchSplitRight);
		panelWatchSplitRight.add(labelWatchValue);
		
		tableWatch = new JTable();
		sl_panelWatchSplitRight.putConstraint(SpringLayout.NORTH, tableWatch, 0, SpringLayout.SOUTH, labelWatchValue);
		sl_panelWatchSplitRight.putConstraint(SpringLayout.WEST, tableWatch, 0, SpringLayout.WEST, panelWatchSplitRight);
		sl_panelWatchSplitRight.putConstraint(SpringLayout.SOUTH, tableWatch, 0, SpringLayout.SOUTH, panelWatchSplitRight);
		sl_panelWatchSplitRight.putConstraint(SpringLayout.EAST, tableWatch, 0, SpringLayout.EAST, panelWatchSplitRight);
		panelWatchSplitRight.add(tableWatch);
		tableWatch.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		
		popupMenuWatch = new JPopupMenu();
		addPopup(treeWatch, popupMenuWatch);
		addPopup(scrollPaneWatch, popupMenuWatch);
		addPopup(tableWatch, popupMenuWatch);
		
		menuAddToWatch = new JMenuItem("org.multipage.generator.menuDebuggerAddToWatch");
		menuAddToWatch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onAddToWatch();
			}
		});
		popupMenuWatch.add(menuAddToWatch);
		
		menuRemoveFromWatch = new JMenuItem("org.multipage.generator.menuDebuggerRemoveFromWatch");
		menuRemoveFromWatch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRemoveWatch();
			}
		});
		popupMenuWatch.add(menuRemoveFromWatch);
		
		panelOutput = new JPanel();
		tabbedPane.addTab("org.multipage.generator.textDebuggerOutput", null, panelOutput, null);
		panelOutput.setLayout(new BorderLayout(0, 0));
		
		scrollPane = new JScrollPane();
		panelOutput.add(scrollPane);
		
		textOutput = new JTextArea();
		textOutput.setFont(new Font("Monospaced", Font.PLAIN, 13));
		textOutput.setEditable(false);
		scrollPane.setViewportView(textOutput);
		
		panelCommand = new JPanel();
		tabbedPane.addTab("org.multipage.generator.textDebuggerCommands", null, panelCommand, null);
		panelCommand.setLayout(new BorderLayout(0, 0));
		
		scrollPaneOutput = new JScrollPane();
		scrollPaneOutput.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		panelCommand.add(scrollPaneOutput);
		
		textInfo = new JEditorPane();
		textInfo.setFont(new Font("Consolas", Font.PLAIN, 15));
		textInfo.setPreferredSize(new Dimension(30, 30));
		scrollPaneOutput.setViewportView(textInfo);
		textInfo.setEditable(false);
		
		panelBottom = new JPanel();
		panelCommand.add(panelBottom, BorderLayout.SOUTH);
		panelBottom.setPreferredSize(new Dimension(10, 19));
		panelBottom.setLayout(new BorderLayout(0, 0));
		
		textCommand = new JTextField();
		textCommand.setPreferredSize(new Dimension(6, 24));
		panelBottom.add(textCommand, BorderLayout.CENTER);
		textCommand.setColumns(10);
		
		buttonSend = new JButton("Send");
		panelBottom.add(buttonSend, BorderLayout.EAST);
		
		panelExceptions = new JPanel();
		panelExceptions.setBackground(Color.WHITE);
		panelExceptions.setOpaque(false);
		tabbedPane.addTab("org.multipage.generator.textDebuggerExceptions", null, panelExceptions, null);
		panelExceptions.setLayout(new BorderLayout(0, 0));
		
		scrollPaneExceptions = new JScrollPane();
		scrollPaneExceptions.setBorder(null);
		scrollPaneExceptions.setOpaque(false);
		scrollPaneExceptions.setBackground(Color.WHITE);
		panelExceptions.add(scrollPaneExceptions, BorderLayout.CENTER);
		
		textExceptions = new JTextPane();
		textExceptions.setContentType("text/html");
		textExceptions.setBorder(null);
		scrollPaneExceptions.setViewportView(textExceptions);
		
		panelSearch = new JPanel();
		panelSearch.setBorder(null);
		panelSearch.setOpaque(false);
		panelSearch.setBackground(Color.WHITE);
		panelSearch.setPreferredSize(new Dimension(10, 52));
		panelExceptions.add(panelSearch, BorderLayout.SOUTH);
		SpringLayout sl_panelSearch = new SpringLayout();
		panelSearch.setLayout(sl_panelSearch);
		
		labelFilter = new JLabel("org.multipage.generator.messageFilterDebugVievewLog");
		sl_panelSearch.putConstraint(SpringLayout.NORTH, labelFilter, 6, SpringLayout.NORTH, panelSearch);
		sl_panelSearch.putConstraint(SpringLayout.WEST, labelFilter, 10, SpringLayout.WEST, panelSearch);
		panelSearch.add(labelFilter);
		
		textFilter = new TextFieldEx();
		sl_panelSearch.putConstraint(SpringLayout.NORTH, textFilter, 6, SpringLayout.NORTH, panelSearch);
		sl_panelSearch.putConstraint(SpringLayout.WEST, textFilter, 3, SpringLayout.EAST, labelFilter);
		sl_panelSearch.putConstraint(SpringLayout.EAST, textFilter, -10, SpringLayout.EAST, panelSearch);
		panelSearch.add(textFilter);
		textFilter.setColumns(10);
		
		checkCaseSensitive = new JCheckBox("org.multipage.generator.textCaseSensitive");
		checkCaseSensitive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCaseSensitiveChange();
			}
		});
		checkCaseSensitive.setOpaque(false);
		sl_panelSearch.putConstraint(SpringLayout.NORTH, checkCaseSensitive, 6, SpringLayout.SOUTH, labelFilter);
		sl_panelSearch.putConstraint(SpringLayout.WEST, checkCaseSensitive, 0, SpringLayout.WEST, labelFilter);
		panelSearch.add(checkCaseSensitive);
		
		checkWholeWords = new JCheckBox("org.multipage.generator.textWholeWords");
		checkWholeWords.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onWholeWordsChange();
			}
		});
		sl_panelSearch.putConstraint(SpringLayout.WEST, checkWholeWords, 6, SpringLayout.EAST, checkCaseSensitive);
		sl_panelSearch.putConstraint(SpringLayout.SOUTH, checkWholeWords, 0, SpringLayout.SOUTH, checkCaseSensitive);
		checkWholeWords.setOpaque(false);
		panelSearch.add(checkWholeWords);
		
		checkExactMatch = new JCheckBox("org.multipage.generator.textExactMatch");
		checkExactMatch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onExactMatchChange();
			}
		});
		sl_panelSearch.putConstraint(SpringLayout.WEST, checkExactMatch, 6, SpringLayout.EAST, checkWholeWords);
		sl_panelSearch.putConstraint(SpringLayout.SOUTH, checkExactMatch, 0, SpringLayout.SOUTH, checkCaseSensitive);
		checkExactMatch.setOpaque(false);
		panelSearch.add(checkExactMatch);
		buttonSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSendCommand();
			}
		});
		
		panelLeft = new JPanel();
		splitPane.setLeftComponent(panelLeft);
		panelLeft.setLayout(new BorderLayout(0, 0));
		
		scrollCodePane = new JScrollPane();
		scrollCodePane.setPreferredSize(new Dimension(30, 30));
		panelLeft.add(scrollCodePane, BorderLayout.CENTER);
		
		textCode = new EditorPaneEx();
		scrollCodePane.setViewportView(textCode);
		textCode.setPreferredSize(new Dimension(20, 20));
		textCode.setContentType("text/html");
		textCode.setEditable(false);
		
		toolBarText = new JToolBar();
		toolBarText.setFloatable(false);
		panelLeft.add(toolBarText, BorderLayout.SOUTH);
		splitPane.setDividerLocation(1.0);
		
		toolBarMenu = new JToolBar();
		toolBarMenu.setRollover(true);
		toolBarMenu.setFloatable(false);
		getContentPane().add(toolBarMenu, BorderLayout.NORTH);
		
		buttonRun = new JButton("org.multipage.generator.textDebuggerRun");
		buttonRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onRun();
			}
		});
		buttonRun.setPreferredSize(new Dimension(20, 20));
		toolBarMenu.add(buttonRun);
		
		buttonStepOver = new JButton("org.multipage.generator.textDebuggerStepOver");
		buttonStepOver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onStepOver();
			}
		});
		
		buttonStepInto = new JButton("org.multipage.generator.textDebuggerStepInto");
		buttonStepInto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onStepInto();
			}
		});
		buttonStepInto.setPreferredSize(new Dimension(20, 20));
		toolBarMenu.add(buttonStepInto);
		buttonStepOver.setPreferredSize(new Dimension(20, 20));
		toolBarMenu.add(buttonStepOver);
		
		buttonStepOut = new JButton("org.multipage.generator.textDebuggerStepOut");
		buttonStepOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onStepOut();
			}
		});
		buttonStepOut.setPreferredSize(new Dimension(20, 20));
		toolBarMenu.add(buttonStepOut);
		
		buttonStop = new JButton("org.multipage.generator.textDebuggerStop");
		buttonStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onStop();
			}
		});
		buttonStop.setPreferredSize(new Dimension(20, 20));
		toolBarMenu.add(buttonStop);
				
		panelStatus = new JPanel();
		panelStatus.setPreferredSize(new Dimension(10, 25));
		getContentPane().add(panelStatus, BorderLayout.SOUTH);
		FlowLayout fl_panelStatus = new FlowLayout(FlowLayout.RIGHT, 5, 5);
		panelStatus.setLayout(fl_panelStatus);
		
		buttonConnected = new JButton("");

		buttonConnected.setPreferredSize(new Dimension(80, 16));
		buttonConnected.setAlignmentY(0.0f);
		buttonConnected.setMargin(new Insets(0, 0, 0, 0));
		panelStatus.add(buttonConnected);
		
		labelStatus = new JLabel("status");
		panelStatus.add(labelStatus);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onWindowClosing();
			}
		});
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		
		try {
			localize();
			setIcons();
			
			createViews();
			setListeners();
			
			loadDialog();
		}
		catch (Exception e) {
			onException(e);
		}
	}
	
	/**
	 * Create views that display debug information.
	 */
	private void createViews() {
		
		try {
			// Create text view.
			createTextView();
			// Create session view.
			createSessionsView();
			// Create watch view.
			createWatchView();
		}
		catch (Exception e) {
			onException(e);
		}
	}
	
	/**
     * Create text content view.
     */
	private void createTextView() {
		
		// Create find/replace dialog.
		findDialog = new FindReplaceDialog(this, textCode);
		findDialog.setOnlyFind();
		// Create tool bar.
		createToolBar();
	}
	
	/**
     * Create tool bar.
     */
	private void createToolBar() {
		
		ToolBarKit.addToolBarButton(toolBarText, "org/multipage/generator/images/search_icon.png",
				"org.maclan.server.tooltipDebugViewerSearchInMainText", () -> onSearchText());
	}
	
	/**
	 * Create sessions view.
	 */
	private void createSessionsView() {
		
		createSessionTree();
		createSourceInfoToolbar();
	}

	/**
	 * Create session tree.
	 */
	private void createSessionTree() {
		
		try {
			// Create tree model.
			sessionsRootNode = new DefaultMutableTreeNode();
			treeSessionsModel = new DefaultTreeModel(sessionsRootNode);
			treeSessions.setModel(treeSessionsModel);
			
			// Create session view renderer.
			treeSessions.setCellRenderer(new TreeCellRenderer() {
				
				// Renderer.
				RendererJLabel renderer = new RendererJLabel();
				
				// Icons for tree nodes.
				ImageIcon sessionIcon = Images.getIcon("org/multipage/generator/images/session_icon.png");
				ImageIcon processIcon = Images.getIcon("org/multipage/generator/images/process.png");
				ImageIcon threadIcon = Images.getIcon("org/multipage/generator/images/thread.png");
				ImageIcon stackLevelIcon = Images.getIcon("org/multipage/generator/images/area_node.png");
				
				// Constructor.
				{
					renderer.setPreferredSize(new Dimension(200, 24));
				}
				
				// Callback function for the node renderer.
				@Override
				public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
						boolean leaf, int row, boolean hasFocus) {
					
					// Check node type.
					if (value instanceof DefaultMutableTreeNode) {
						
						// Get node user object.
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
						Object userObject = node.getUserObject();
						
						// Set node renderer properties and return the renderer.
						if (userObject instanceof XdebugListenerSession) {
							renderer.setIcon(sessionIcon);
						}
						else if (userObject instanceof XdebugProcess) {
							renderer.setIcon(processIcon);
						}
						else if (userObject instanceof XdebugThread) {
							renderer.setIcon(threadIcon);
						}
						else if (userObject instanceof XdebugStackLevel) {
							renderer.setIcon(stackLevelIcon);
						}
					
						renderer.setText(value.toString());
						renderer.set(sel, hasFocus, row);
						return renderer;
					}
					else {
						return null;
					}
				}
			});
		}
		catch (Exception e) {
			onException(e);
        }
	}
	
	/**
	 * Create source information toolbar.
	 */
	private void createSourceInfoToolbar() {
		
		ToolBarKit.addToolBarButton(toolBarSourceInfo, "org/multipage/generator/images/open.png", "org.maclan.server.tooltipOpenCodeSource", () -> openSource());
		ToolBarKit.addToolBarButton(toolBarSourceInfo, "org/multipage/generator/images/area_node.png", "org.maclan.server.tooltipOpenSourceArea", () -> openSourceArea());
		ToolBarKit.addToolBarButton(toolBarSourceInfo, "org/multipage/generator/images/slot_icon.png", "org.maclan.server.tooltipOpenSourceAreaSlots", () -> openSourceAreaSlots());
	}

	/**
	 * Open source of current code.
	 */
	private void openSource() {
		
		// Check process ID. Can open editors only for locally debuged code.
		long sessionProcessId = currentSession.getProcessId();
		long currentProcessId = ProcessHandle.current().pid();
		
		if (sessionProcessId != currentProcessId) {
			Utility.show(this, "org.maclan.server. messageCanOpenForLocalDebugger");
			return;
		}
		
		// Get current session source information.
		DebugSourceInfo sourceInfo = currentSession.getSourceInfo();
		if (sourceInfo == null) {
			onException("org.maclan.server.messageNullCodeSourceInfo");
		}
			
		// Get source type.
		Integer sourceType = sourceInfo.getType();
		if (sourceType == null) {
			onException("org.maclan.server.messageNullCodeSourceType");
		}
		
		Signal signal = null;
		switch (sourceType) {
		
		    case DebugSourceInfo.RESOURCE:
		    	signal = GuiSignal.editResource;
		    	break;
		    	
		    case DebugSourceInfo.SLOT:
		    	signal = GuiSignal.editSlot;
		    	break;
		    	
		    default:
		    	onException("org.maclan.server.messageUnknownCodeSourceType", sourceType);
		}
		
		// Get ID of source.
		Long sourceId = sourceInfo.getId();
		if (sourceId == null) {
			onException("org.maclan.server.messageNullCodeSourceId");
		}	
		
		// Send event to open source editor.
        ApplicationEvents.transmit(this, signal, sourceId);
	}
	
	/**
     * Open source area of current code.
     */
	private void openSourceArea() {

		// Check process ID.
		long sessionProcessId = currentSession.getProcessId();
		long currentProcessId = ProcessHandle.current().pid();
		
		if (sessionProcessId != currentProcessId) {
			Utility.show(this, "org.maclan.server. messageCanOpenForLocalDebugger");
			return;
		}
		
		// Get current session source information.
		DebugSourceInfo sourceInfo = currentSession.getSourceInfo();
		if (sourceInfo == null) {
			onException("org.maclan.server.messageNullCodeSourceInfo");
		}
		
		// Get ID of source area.
		Long areaId = sourceInfo.getAreaId();
		if (areaId == null) {
			onException("org.maclan.server.messageNullCodeSourceAreaId");
		}	

		// Open source area.
        ApplicationEvents.transmit(this, GuiSignal.editArea, areaId);
	}
	
	/**
	 * Open list of slots for source area of current code.
	 */
	private void openSourceAreaSlots() {
		
		// Check process ID.
		long sessionProcessId = currentSession.getProcessId();
		long currentProcessId = ProcessHandle.current().pid();
		
		if (sessionProcessId != currentProcessId) {
			Utility.show(this, "org.maclan.server. messageCanOpenForLocalDebugger");
			return;
		}
		
		// Get current session source information.
		DebugSourceInfo sourceInfo = currentSession.getSourceInfo();
		if (sourceInfo == null) {
			onException("org.maclan.server.messageNullCodeSourceInfo");
		}
		
		// Get ID of source area.
		Long areaId = sourceInfo.getAreaId();
		if (areaId == null) {
			onException("org.maclan.server.messageNullCodeSourceAreaId");
		}	

		// Open source area slot list.
        ApplicationEvents.transmit(this, GuiSignal.editAreaSlots, areaId);
	}

	/**
	 * Initialize watch window
	 */
	private void createWatchView() {
		
		try {
			// Create tree view.
			createWatchTree();
			// Create table view.
			createWatchTable();
		}
		catch (Exception e) {
			onException(e);
		}
	}	
	
	/**
	 * Create watch tree.
	 */
	private void createWatchTree() {
		
		try {
			// Create tree model.
			watchRootNode = new DefaultMutableTreeNode();
			treeWatchModel = new DefaultTreeModel(watchRootNode);
			treeWatch.setModel(treeWatchModel);
			
			treeWatch.setRootVisible(false);
			
			// Create watch tree view renderer.
			treeWatch.setCellRenderer(new TreeCellRenderer() {
				
				// Renderer.
				RendererJLabel renderer = new RendererJLabel();
				
				// Icons for tree nodes.
				ImageIcon areaIcon = Images.getIcon("org/multipage/generator/images/area_node.png");
				ImageIcon tagIcon = Images.getIcon("org/multipage/generator/images/tag.png");
				ImageIcon blockVariableIcon = Images.getIcon("org/multipage/generator/images/block_varable.png");
				ImageIcon blockProcedureIcon = Images.getIcon("org/multipage/generator/images/block_procedure.png");
				ImageIcon serverIcon = Images.getIcon("org/multipage/generator/images/session_icon.png");
				
				// Constructor.
				{
					renderer.setPreferredSize(new Dimension(200, 24));
				}
				
				// Callback function for the node renderer.
				@Override
				public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
						boolean leaf, int row, boolean hasFocus) {
					
					// Check node type.
					if (value instanceof DefaultMutableTreeNode) {
						
						ImageIcon icon = null;
						
						// Get node user object.
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
						Object userObject = node.getUserObject();
						
						if (userObject instanceof DebugWatchItem) {
	
							DebugWatchItem watchItem = (DebugWatchItem) userObject;
							DebugWatchGroup watchItemType = watchItem.getGroup();
							
							// Set node renderer properties.
							switch (watchItemType) {
							case TAG_PROPERTY:
								icon = tagIcon;
								break;
							case BLOCK_VARIABLE:
								icon = blockVariableIcon;
								break;
							case BLOCK_PROCEDURE:
								icon = blockProcedureIcon;
								break;
							case AREA:
								icon = areaIcon;
								break;
							case SERVER:
								icon = serverIcon;
								break;
							}
						}
						
						renderer.setText(value.toString());
						renderer.setIcon(icon);
						renderer.set(sel, hasFocus, row);
						return renderer;
					}
					else {
						return null;
					}
				}
			});
		}
		catch (Exception e) {
			onException(e);
        }
	}

	/**
	 * Create watch table.
	 */
	@SuppressWarnings("serial")
	private void createWatchTable() {
		
		try {
			// Set column widths.
			final int [] columnWidths = { 0, 0, 0, 100, 100 };
			final int columnCount = columnWidths.length;
			
			tableWatchModel = new DefaultTableModel(0, columnCount) {
				@Override
				public boolean isCellEditable(int row, int column) {
					// Disable cell editing.
					return false;
				}
			};
			tableWatch.setModel(tableWatchModel);
			
			DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
			
			// Name column.
			final int nameCellWidth = columnWidths[WATCHED_NAME_COLUMN_INDEX];
			TextFieldEx nameTextField = new TextFieldEx();
			DefaultCellEditor nameEditor = new DefaultCellEditor(nameTextField);
			DefaultTableCellRenderer nameRenderer = new DefaultTableCellRenderer();
			TableColumn nameColumn = new TableColumn(WATCHED_NAME_COLUMN_INDEX, nameCellWidth, nameRenderer, nameEditor);
			if (nameCellWidth == 0) {
				nameColumn.setMinWidth(0);
	            nameColumn.setMaxWidth(0);
			}
			columnModel.addColumn(nameColumn);
			
			// Full name column.
			final int fullNameCellWidth = columnWidths[WATCHED_FULLNAME_COLUMN_INDEX];
			TextFieldEx fullNameTextField = new TextFieldEx();
			DefaultCellEditor fullNameEditor = new DefaultCellEditor(fullNameTextField);
			DefaultTableCellRenderer fullNameRenderer = new DefaultTableCellRenderer();
			TableColumn fullNameColumn = new TableColumn(WATCHED_FULLNAME_COLUMN_INDEX, fullNameCellWidth, fullNameRenderer, fullNameEditor);
			if (fullNameCellWidth == 0) {
				fullNameColumn.setMinWidth(0);
	            fullNameColumn.setMaxWidth(0);
			}
			columnModel.addColumn(fullNameColumn);
			
			// Property type column.
			final int typeCellWidth = columnWidths[WATCHED_PROPERTY_TYPE_COLUMN_INDEX];
			TextFieldEx typeTextField = new TextFieldEx();
			typeTextField.setEditable(false);
			DefaultCellEditor typeEditor = new DefaultCellEditor(typeTextField);
			DefaultTableCellRenderer typeRenderer = new DefaultTableCellRenderer();
			TableColumn typeColumn = new TableColumn(WATCHED_PROPERTY_TYPE_COLUMN_INDEX, typeCellWidth, typeRenderer, typeEditor);
			if (typeCellWidth == 0) {
				typeColumn.setMinWidth(0);
	            typeColumn.setMaxWidth(0);
			}
			columnModel.addColumn(typeColumn);
			
			// Value column.
			final int valueCellWidth = columnWidths[WATCHED_VALUE_COLUMN_INDEX];
			TextFieldEx valueTextField = new TextFieldEx();
			DefaultCellEditor valueEditor = new DefaultCellEditor(valueTextField);
			DefaultTableCellRenderer valueRenderer = new DefaultTableCellRenderer();
			TableColumn valueColumn = new TableColumn(WATCHED_VALUE_COLUMN_INDEX, valueCellWidth, valueRenderer, valueEditor);
			if (valueCellWidth == 0) {
				valueColumn.setMinWidth(0);
	            valueColumn.setMaxWidth(0);
			}
			columnModel.addColumn(valueColumn);
			
			// Value type column.
			final int valueTypeCellWidth = columnWidths[WATCHED_VALUE_TYPE_COLUMN_INDEX];
			TextFieldEx valueTypeTextField = new TextFieldEx();
			DefaultCellEditor valueTypeEditor = new DefaultCellEditor(valueTypeTextField);
			DefaultTableCellRenderer valueTypeRenderer = new DefaultTableCellRenderer();
			TableColumn valueTypeColumn = new TableColumn(WATCHED_VALUE_TYPE_COLUMN_INDEX, valueTypeCellWidth, valueTypeRenderer, valueTypeEditor);
			if (valueTypeCellWidth == 0) {
				valueTypeColumn.setMinWidth(0);
	            valueTypeColumn.setMaxWidth(0);
			}
			columnModel.addColumn(valueTypeColumn);		
			
			tableWatch.setColumnModel(columnModel);
		}
		catch (Exception e) {
			onException(e);
        }
	}

	/**
	 * Attach debugger listener.
	 * @param listener
	 */
	public void attachDebuggerListener(XdebugListener listener) {
		
		try {
			// Open Xdebug viewer event.
			listener.setOpenDebugViewerLambda(session -> {
				
				try {
					// Remeber the session object.
					currentSession = session;
					
					// Show dialog window.
					Safe.invokeLater(() -> {
						DebugViewer.this.setVisible(true);
					});
					
					// When ready for commands, load client contexts and set "server_ready" property to true.
					session.setReadyForCommands(() -> {
						
						try {
							session.loadClientContexts(() -> {
								
								// Set "server_ready" property to true.
								try {
									int transationId = session.createTransaction("property_set", new String [][] {{"-n", "server_ready"},  {"-l", "1"}}, "1", response -> {
										     
										boolean success = response.isPropertySetSuccess();
										if (!success) {
											onException("org.multipage.generator.messageDebugServerReadyError");
										}
									});
									session.beginTransactionWait(transationId, RESPONSE_TIMEOUT_MS);
								}
								catch (Exception e) {
									onException(e);
								}
							});
						}
						catch (Exception e) {
							onException(e);
						}
					});
					
					// Process notifications.
					session.setReceivingNotifications(notification -> {
						try {
							
							// On breakpoint resolved notification.
							boolean breakpointResolved = notification.isBreakpointResolved();
							if (breakpointResolved) {
								
								// Update debugger views.
								Safe.invokeLater(() -> {
									updateViews();
								});
								return;
							}
							
							// On final debugger information.
							boolean finalDebugInfo = notification.isFinalDebugInfo();
							if (finalDebugInfo) {
								
								// Update debugger views.
								Safe.invokeLater(() -> {
										try {
											
											// Set "server finished" client property. Do not expect client response.
											int transationId = session.createTransaction("property_set", new String [][] {{"-n", "server_finished"},  {"-l", "1"}}, "1", null);
											session.beginTransaction(transationId);
											
											// Set finished flag.
											session.setFinished();
											
											// Wait for session to be closed.
											session.waitForClosed(RESPONSE_TIMEOUT_MS);
											
											// Refresh sessions.
											refreshSessions();
											
											// Display final source code.
											String finalSourceCode = notification.getFinalSourceCode();
											displaySourceCode("", finalSourceCode);
										}
										catch (Exception e) {
											onException(e);
										}
								});
								return;
							}
						}
						catch (Exception e) {
							onException(e);
						}
					});
				}
				catch (Exception e) {
                    onException(e);
                }
			});
			
			// Close debug viewer event.
			listener.setCloseDebugViewerLambda(() -> {
				try {
					closeViewer();
				}
				catch (Exception e) {
					onException(e);
				}
			});
			
			debugListener = listener;
		}
		catch (Exception e) {
			onException(e);
        }
	}
	
	/**
	 * Load selected Xdebug session objects.
	 * @param selectedStackLevel 
	 * @param selectedThread 
	 * @param selectedProcess 
	 * @param selectedSession 
	 * @return
	 */
	private void loadSelection(Obj<XdebugListenerSession> selectedSession, Obj<XdebugThread> selectedThread,
			Obj<XdebugStackLevel> selectedStackLevel) {
		
		try {
			// Initialization.
			if (selectedSession != null) {
				selectedSession.ref = null;
			}
			if (selectedThread != null) {
				selectedThread.ref = null;
			}
			if (selectedStackLevel != null) {
				selectedStackLevel.ref = null;
			}
			
			// Check root node.
			if (sessionsRootNode == null) {
				return;
			}
			
			// Get selected path.
			TreePath selectedPath = treeSessions.getSelectionPath();
			if (selectedPath == null) {
				
				selectedPath = new TreePath(new Object [] {sessionsRootNode});
			}
	
			// Get number of path components.
			int componentCount = selectedPath.getPathCount();
			
			// Get session component of selected path.
			DefaultMutableTreeNode sessionNode = null;
			
			if (componentCount > 1) {
				Object sessionNodeObject = selectedPath.getPathComponent(1);
				
				if (sessionNodeObject instanceof DefaultMutableTreeNode) {
					sessionNode = (DefaultMutableTreeNode) sessionNodeObject;
				}
			}
			else {
				// Get first session node.
				Enumeration<TreeNode> sessionNodes = sessionsRootNode.children();
				
				boolean hasFirst = sessionNodes.hasMoreElements();
				if (hasFirst) {
					Object firstNode = sessionNodes.nextElement();
					
					if (firstNode instanceof DefaultMutableTreeNode) {
						sessionNode = (DefaultMutableTreeNode) firstNode;
					}
				}
			}
	
			if (sessionNode != null && selectedSession != null) {
				Object userObject = sessionNode.getUserObject();
				
				if (userObject instanceof XdebugListenerSession) {
					selectedSession.ref = (XdebugListenerSession) userObject;
				}
			}
			
			// Get thread component of selected path.
			DefaultMutableTreeNode threadNode = null;
			if (componentCount > 2) {
				Object threadNodeObject = selectedPath.getPathComponent(2);
				
				if (threadNodeObject instanceof DefaultMutableTreeNode) {
					threadNode = (DefaultMutableTreeNode) threadNodeObject;
				}
			}
			else if (sessionNode != null) {
				
				// Get first thread node.
				Enumeration<TreeNode> threadNodes = sessionNode.children();
				
				boolean hasFirst = threadNodes.hasMoreElements();
				if (hasFirst) {
					Object firstNode = threadNodes.nextElement();
					
					if (firstNode instanceof DefaultMutableTreeNode) {
						threadNode = (DefaultMutableTreeNode) firstNode;
					}
				}
			}
			
			if (threadNode != null && selectedThread != null) {
				Object userObject = threadNode.getUserObject();
				
				if (userObject instanceof XdebugThread) {
					selectedThread.ref = (XdebugThread) userObject;
				}
			}
			
			// Get stack level component.
			DefaultMutableTreeNode levelNode = null;
			if (componentCount > 3) {
				Object levelNodeObject = selectedPath.getPathComponent(3);
				
				if (levelNodeObject instanceof DefaultMutableTreeNode) {
					levelNode = (DefaultMutableTreeNode) levelNodeObject;
				}
			}
			else if (threadNode != null) {
				
				// Get first level node.
				Enumeration<TreeNode> levelNodes = threadNode.children();
				
				boolean hasFirst = levelNodes.hasMoreElements();
				if (hasFirst) {
					Object firstNode = levelNodes.nextElement();
					
					if (firstNode instanceof DefaultMutableTreeNode) {
						levelNode = (DefaultMutableTreeNode) firstNode;
					}
				}
			}
			
			if (levelNode != null && selectedStackLevel != null) {
				Object userObject = levelNode.getUserObject();
				
				if (userObject instanceof XdebugStackLevel) {
					selectedStackLevel.ref = (XdebugStackLevel) userObject;
				}
			}
		}
		catch (Exception e) {
            onException(e);
        }
		return;
	}
	
	/**
	 * On add to watch list.
	 */
	protected void onAddToWatch() {
		
		try {
			// Check session.
			if (currentSession == null) {
				return;
			}
			
			DebugWatchItem watchItem = AddDebugWatchDialog.showDialog(this);
			if (watchItem == null) {
				return;
			}
			
			// Add new tree item.
			DefaultMutableTreeNode newTreeNode = new DefaultMutableTreeNode(watchItem);
			int childCount = watchRootNode.getChildCount();
			treeWatchModel.insertNodeInto(newTreeNode, watchRootNode, childCount);
			Utility.expandTop(treeWatch, true);
			
			// Select new tree item.
			TreeNode [] selectNodes = newTreeNode.getPath();
			TreePath selectTreePath = new TreePath(selectNodes);
			treeWatch.setSelectionPath(selectTreePath);
		}
		catch (Exception e) {
            onException(e);
        }
	}
	
	/**
	 * On remove watch item.
	 */
	protected void onRemoveWatch() {
		
		try {
			// Get selected watch item.
			TreePath selectedPath = treeWatch.getSelectionPath();
			if (selectedPath == null) {
				return;
			}
			
			Object lastComponent = selectedPath.getLastPathComponent();
			if (!(lastComponent instanceof DefaultMutableTreeNode)) {
	            return;
	        }
			
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) lastComponent;
			Object userObject = selectedNode.getUserObject();
			if (!(userObject instanceof DebugWatchItem)) {
				return;
			}
			
			DebugWatchItem watchItem = (DebugWatchItem) userObject;
			
			// Ask user if selected watch item can be removed.
			String watchedItemName = watchItem.getName();
	
			boolean confirmed = Utility.ask(this, "org.multipage.generator.messageDebuggerRemoveWatchedItem", watchedItemName);
			if (!confirmed) {
				return;
			}
			
			// Remove selected watch item.
			treeWatchModel.removeNodeFromParent(selectedNode);
		}
		catch (Exception e) {
            onException(e);
        }
	}	
	
	/**
	 * Event invoked after watch tree change.
	 */
	protected void onWatchTreeChange() {
		
		try {
			// Check controls.
			if (treeWatch == null || tableWatchModel == null) {
				return;
			}
			
			// Get expanded row count.
			int rowCount = treeWatch.getRowCount();
	
			// Update watch table with tree data.
			tableWatchModel.setRowCount(0);
			for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				
				TreePath nodePath = treeWatch.getPathForRow(rowIndex);
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodePath.getLastPathComponent();
				
				Object usertObject = node.getUserObject();
				if (usertObject instanceof DebugWatchItem) {
	
					DebugWatchItem watchItem = (DebugWatchItem) usertObject;
					String watchedItemName = watchItem.getName();
					DebugWatchGroup watchedItemType = watchItem.getGroup();
					
					tableWatchModel.addRow(new Object [] { watchedItemName, null, watchedItemType, null, null });
				}
			}
			
			// Load watched item values.
			if (currentSession == null) {
				return;
			}
			
			// Get current stack.
			XdebugStackLevel stackLevel = currentSession.getCurrentStackLevel();
			if (stackLevel == null) {
				return;
			}
			
			// Load watched values.
			Safe.invokeLater(() -> {
				loadWatchListValues(stackLevel);
			});
		}
		catch (Exception e) {
            onException(e);
        }
	}
	
	/**
	 * Called when a user closes the window.
	 */
	protected void onWindowClosing() {
		
		// Close opened session.
		try {
			closeSessions();
		}
		catch (Exception e) {
            onException(e);
        }
		
		// Hide dialog window.
		setVisible(false);
	}
	
	/**
	 * Close the debug viewer.
	 */
	public void closeViewer() {
		
		try {
			closeSessions();
		}
		catch (Exception e) {
            onException(e);
        }
		try {
			ApplicationEvents.removeReceivers(this);
			saveDialog();
		}
		catch (Exception e) {
            onException(e);
        }
		try {
			dispose();
		}
		catch (Exception e) {
            onException(e);
        }	
	}

	/**
	 * Close debugger sessions.
	 */
	private void closeSessions() {
		
		try {
			List<XdebugListenerSession> sessions = debugListener.getSessions();
			
			synchronized (sessions) {
				
				sessions.forEach(session -> {
					try {	
						
						boolean isOpen = session.isOpen();
						if (isOpen) {
							stopSession(session);
						}
					}
					catch (Exception e) {
	                    onException(e);
	                }
				});
			}
		}
		catch (Exception e) {
            onException(e);
        }
	}
	
	/**
	 * On select tree node.
	 */
	protected void onSelectTreeNode() {
		
		// Check flag that enables GUI events.
		if (!enableTreeSessionsEvents) {
			return;
		}
		
		try {
			// Load current selection.
			Obj<XdebugListenerSession> selectedSession = new Obj<>();
			Obj<XdebugThread> selectedThread = new Obj<>();
			Obj<XdebugStackLevel> selectedStackLevel =  new Obj<>();
			
			loadSelection(selectedSession, selectedThread, selectedStackLevel);
			
			if (selectedSession.ref != null) {
				currentSession = selectedSession.ref;
			}
			
			if (currentSession == null) {
				return;
			}
			
			currentSession.setCurrent(selectedThread.ref, selectedStackLevel.ref);
			
			loadStackLevelProperties(currentSession);
		}
		catch (Exception e) {
            onException(e);
        }
	}
	
	/**
	 * Lod and display stack level properties.
	 * @param session 
	 */
	private void loadStackLevelProperties(XdebugListenerSession session) {
		
		try {
			// Check session.
			if (session == null) {
				return;
			}
			
			// Get current stack.
			XdebugStackLevel stackLevel = session.getCurrentStackLevel();
			if (stackLevel == null) {
				return;
			}
			
			// Load source information.
			session.getSourceInformation(stackLevel, sourceInfo -> {
				
				Safe.invokeLater(() -> {
					displayCodeSourceInfo(sourceInfo);
				});
			});
			
			// Load dialog that can add new watch items.
			LinkedList<DebugWatchItem> allWatchItems = new LinkedList<>();
			try {
				
				// Load context items.
				int contextCount = XdebugClient.getContextsCount();
				for (int contextId = 0; contextId < contextCount; contextId++) {

					session.contextGet(contextId, stackLevel, watchItems -> {
						allWatchItems.addAll(watchItems);
					});
				}
			}
			catch (Exception e) {
				onException(e);
			}
			
			// Set watched items.
			Safe.invokeLater(() -> {
				AddDebugWatchDialog.setWatchItems(allWatchItems);
			});
			
			// Load watched values.
			Safe.invokeLater(() -> {
				loadWatchListValues(stackLevel);
			});
			
			// Get source code from the stack level.
			String sourceCode = stackLevel.getSourceCode();
			
			// Display debugged source code.
			Safe.invokeLater(() -> {
				displayDebuggedSourceCode(sourceCode, stackLevel);
			});
		}
		catch (Exception e) {
            onException(e);
        }
	}

	/**
	 * On process double click.
	 */
	protected void onDoubleClickProcessNode() {
		
		// Display session dialog.
		try {
			Obj<XdebugListenerSession> selectedSession = new Obj<>();
			loadSelection(selectedSession, null, null);
			XdebugSessionDialog.showDialog(this, selectedSession.ref);
		}
		catch (Exception e) {
			onException(e);
		}
	}
	
	/**
	 * Load dialog
	 */
	private void loadDialog() {
		
		try {
			// Set window boundary and main splitter position.
			if (bounds.isEmpty()) {
				Utility.centerOnScreen(this);
			}
			else {
				setBounds(bounds);
			}
			splitPane.setDividerLocation(mainSplitterPosition);
			splitPaneWatch.setDividerLocation(watchSplitterPosition);
		}
		catch (Exception e) {
            onException(e);
        }
	}
	
	/**
	 * Save dialog
	 */
	private void saveDialog() {
		
		try {
			// Save window boundary and main splitter position.
			bounds = getBounds();
			mainSplitterPosition = splitPane.getDividerLocation();
			watchSplitterPosition = splitPaneWatch.getDividerLocation();
		}
		catch (Exception e) {
            onException(e);
        }
	}
	
	/**
	 * Localize components
	 */
	private void localize() {
		
		try {
			// Set window title
			setTitle(Resources.getString("org.multipage.generator.textApplicationDebug"));
			
			Utility.localize(tabbedPane);
			Utility.localize(labelFilter);
			Utility.localize(checkCaseSensitive);
			Utility.localize(checkWholeWords);
			Utility.localize(checkExactMatch);
			Utility.localize(labelThreads);
			Utility.localize(buttonRun);
			Utility.localize(buttonStepOver);
			Utility.localize(buttonStepInto);
			Utility.localize(buttonStepOut);
			Utility.localize(buttonStop);
			Utility.localize(menuAddToWatch);
			Utility.localize(menuRemoveFromWatch);
			Utility.localize(labelWatchName);
			Utility.localize(labelWatchValue);
			Utility.localize(labelSourceInfo);
		}
		catch (Exception e) {
            onException(e);
        }
	}

	/**
	 * Set icons
	 */
	private void setIcons() {
		
		try {
			// Set main icon
			setIconImage(Images.getImage("org/multipage/basic/images/main_icon.png"));
			buttonRun.setIcon(Images.getIcon("org/multipage/generator/images/run.png"));
			buttonStepOver.setIcon(Images.getIcon("org/multipage/generator/images/step_over.png"));
			buttonStepInto.setIcon(Images.getIcon("org/multipage/generator/images/step_into.png"));
			buttonStepOut.setIcon(Images.getIcon("org/multipage/generator/images/step_out.png"));
			buttonStop.setIcon(Images.getIcon("org/multipage/generator/images/stop.png"));
			menuAddToWatch.setIcon(Images.getIcon("org/multipage/generator/images/watch_debug.png"));
			menuRemoveFromWatch.setIcon(Images.getIcon("org/multipage/generator/images/remove_icon.png"));
		}
		catch (Exception e) {
            onException(e);
		}
	}
		
	/**
	 * Checks live sessions and removes closed sessions.
	 */
	private void refreshSessions() {
		
		enableTreeSessionsEvents = false;
		
		try {
			// Checks live sessions and removes closed sessions.
			debugListener.ensureLiveSessions();
			List<XdebugListenerSession> sessions = debugListener.getSessions();
			
			synchronized (sessions) {
				ensureLiveTreeSessions(sessions);
			}
		}
		catch (Exception e) {
            onException(e);
        }
		
		Safe.invokeLater(() -> {
			enableTreeSessionsEvents = true;
		});
	}
	
	/**
	 * Update the source code for current session and other debugger views.
	 */
	private void updateViews() {
		
		try {
			// Get current Xdebug session.
			if (currentSession == null) {
				return;
			}	
			
			List<XdebugListenerSession> sessions = debugListener.getSessions();
			
			synchronized (sessions) {
				for (XdebugListenerSession session : sessions) {
					
					session.stackGet(stack -> (processId, processName) -> (threadId, threadName) -> {
						try {
	
							// Save session stack.
							long sessionProcessId = session.getProcessId();
							if (sessionProcessId == -1L) {
								
								session.setProcessId(processId);
								sessionProcessId = processId;
							}
							
							// Put stack into session.
							if (processId == sessionProcessId) {
								session.putStack(processId, processName, threadId, threadName, stack);
								
								// Display stack information.
								Safe.invokeLater(() -> {
									renewSessionTreeNode(session);
								});
							}
							
							// Set curent session, thread and stack level.
							if (session.equals(currentSession)) {
								
								int levelCount = stack.size();
								if (levelCount > 0) {
									
									XdebugStackLevel stackLevel = stack.getFirst();
									currentSession.setCurrent(threadId, stackLevel);
								}
							}
						}
						catch (Exception e) {
		                    onException(e);
		                }
					});
				}
			}
			// Select the first stack item.
			Safe.invokeLater(() -> {
				selectStackTop(currentSession);
			});
		}
		catch (Exception e) {
            onException(e);
        }
	}

	/**
	 * Remove input sessions from the tree.
	 * @param sessions
	 */
	private void ensureLiveTreeSessions(List<XdebugListenerSession> sessions) {
		
		try {
			// Traverse session tree and remove sessions that where not found.
			LinkedList<DefaultMutableTreeNode []> nodesToRemove = new LinkedList<>();
			Utility.traverseElements(treeSessions, userObject -> treeNode -> parentNode -> {
				
				if (userObject instanceof XdebugListenerSession) {
					
					XdebugListenerSession treeSession = (XdebugListenerSession) userObject;
					if (!sessions.contains(treeSession)) {
						
						nodesToRemove.add(new DefaultMutableTreeNode [] { parentNode, treeNode });
	                }
				}
				return false;
			});
			
			for (DefaultMutableTreeNode [] nodeToRemove : nodesToRemove) {
				
				int count = nodeToRemove.length;
				if (count != 2) {
					continue;
				}
				
				DefaultMutableTreeNode parentNode = nodeToRemove[0];
				DefaultMutableTreeNode childNode = nodeToRemove[1];
				
				if (parentNode == null || childNode == null) {
					continue;
				}
				
				Safe.invokeLater(() -> {
					
					boolean success = parentNode.isNodeChild(childNode);
					if (success) {
						parentNode.remove(childNode);
					}
				});
			}
			
			Safe.invokeLater(() -> {
				treeSessions.updateUI();
			});
		}
		catch (Exception e) {
            onException(e);
        }
	}

	/**
	 * Display source code.
	 * @param caption
	 * @param sourceCode
	 */
	public void displaySourceCode(String caption, String sourceCode) {
		
		try {
			// Split the source code into lines
			String [] inputLines = sourceCode.split("\n");
			
			final Obj<Integer> lineNumber = new Obj<Integer>(0);
			final int lines = inputLines.length;
	
			codeLines = new LinkedList<String>();
			
			// Display source
			displaySourceCode(caption, new DisplayCodeInterface() {
				
				@Override
				public String line() {
					
					try {
						if (lineNumber.ref >= lines) {
							return null;
						}
						String inputLine = inputLines[lineNumber.ref++];
						codeLines.addLast(inputLine);
						return inputLine;
					}
					catch (Exception e) {
                        onException(e);
                    }
					return null;
				}
			});
		}
		catch (Exception e) {
            onException(e);
        }
	}
	
	/**
	 * Display the source code form Xdebug client with highlighted parts.
	 * @param rawSourceCode
	 * @param stackLevel 
	 * @param debugInfo
	 */
	private void displayDebuggedSourceCode(String rawSourceCode, XdebugStackLevel stackLevel) {
		try {
			// Initializaction.
			String sourceCode = null;
			
			// Mark tag beginning and end positions in source code.
			int cmdBegin = stackLevel.getCmdBegin();
			int cmdEnd = stackLevel.getCmdEnd();
			int sourceLength = rawSourceCode.length();
			
			if (cmdBegin >= 0 && cmdEnd >= 0 && cmdBegin <= cmdEnd
				&& cmdBegin <= sourceLength && cmdEnd <= sourceLength) {
				
				// Insert "anchor" and "terminator" special characters into the source code.
				sourceCode = Utility.insertCharacter(rawSourceCode, cmdBegin, INTERLINEAR_ANNOTATION_ANCHOR);
				sourceCode = Utility.insertCharacter(sourceCode, cmdEnd + 1, INTERLINEAR_ANNOTATION_TERMINATOR);
			}
			
			// Do not use text highlights.
			if (sourceCode == null) {
				sourceCode = rawSourceCode;
			}
			
			// Display the source code.
			displaySourceCode("", sourceCode);
		}
		catch (Exception e) {
			onException(e);
		}
	}
	
	/**
	 * Displays source code. The callback is utilized for miscellaneous code sources
	 * @param caption 
	 * @param callbacks
	 */
	private void displaySourceCode(String caption, DisplayCodeInterface callbacks) {
		
		Safe.invokeLater(() -> {
			
			final String tabulator = "&nbsp;&nbsp;&nbsp;&nbsp;";
			
			String code = "<html>"
					+ "<head>"
					+ "<style>"
					+ "body {"
					+ "		white-space:nowrap;"
					+ "}"
					+ "#header {"
					+ "		font-family: Monospaced;"
					+ "		background-color: #DDDDDD;"
					+ "		color: #FFFFFF;"
					+ "}"
					+ ".lino {"
					+ "		font-family: Monospaced;"
					+ "		background-color: #DDDDDD;"
					+ "		color: #FFFFFF;"
					+ "}"
					+ ".code {"
					+ "		font-family: Monospaced;"
					+ "}"
					+ ".currentReplacement {"
					+ "     background-color: #FF0000;"
					+ "		color: #FFFFFF;"
					+ "}"
					+ "</style>"
					+ "</head>"
					+ "<body>";
			
			// Display header
			if (caption != null) {
				code += String.format("<div id='header'><center>%s</center></div><div class='code'>", caption);
			}
		
			// Display lines
			String inputLine;
			int lineNumber = 1;
		
	        for (;;) {
	        	
	        	Object returned = callbacks.line();
	        	if (returned == null) {
	        		break;
	        	}
	        	
	        	inputLine = returned.toString();
	        	inputLine = Utility.htmlSpecialChars(inputLine);
	        	
				inputLine = inputLine.replaceAll("\\t", tabulator);
	        	inputLine = inputLine.replaceAll("\\s", "&nbsp;");
	        	String linoText = String.format("% 3d ", lineNumber);
	        	linoText = linoText.replaceAll("\\s", "&nbsp;");
	        	
	        	inputLine = inputLine.replaceAll(INTERLINEAR_ANNOTATION_ANCHOR + "", "<span class='currentReplacement'>");
	        	inputLine = inputLine.replaceAll(INTERLINEAR_ANNOTATION_TERMINATOR + "", "</span>");
	        	
	    		code += String.format("<span class='lino'>%s</span>&nbsp;%s<br>", linoText, inputLine);
	    		lineNumber++;
	        }

			code += "</div></body>"
					+ "</html>";
			
			// Display code (preserve scroll position)
			Point viewPosition = scrollCodePane.getViewport().getViewPosition();
			textCode.setText(code);
			
			// Set scroll position.
			Safe.invokeLater(() -> {
				scrollCodePane.getViewport().setViewPosition(viewPosition);
			});
		});
	}
	
	/**
	 * Display code source information.
	 * @param sourceInfo
	 */
	private void displayCodeSourceInfo(DebugSourceInfo sourceInfo) {
		
		String codeSourceText = sourceInfo.getSourceInfoHtml();
		textPaneSourceInfo.setText(codeSourceText);
	}
	
	/**
	 * Display Area Server stack information.
	 * @param session 
	 */
	private void renewSessionTreeNode(XdebugListenerSession session) {
		
		Safe.invokeLater(() -> {
			
			// Check root node of the tree view.
			if (sessionsRootNode == null) {
				sessionsRootNode = new DefaultMutableTreeNode("root");
				treeSessionsModel.setRoot(sessionsRootNode);
			}
			
			// Find session node.
			int sessionId = session.getSessionId();
			DefaultMutableTreeNode sessionNode = getSessionNode(sessionId);
			
			// If the session node doesn't exist, create new one.
			if (sessionNode == null) {
				sessionNode = new DefaultMutableTreeNode(session);
				sessionsRootNode.add(sessionNode);
			}
			
			// Update thread nodes.
			sessionNode.removeAllChildren();
			
			HashMap<Long, XdebugThread> threads = session.getThreads();
			
			// Sort thread IDs.
			Set<Long> threadIds = threads.keySet();
			ArrayList<Long> sortedThreadIds = new ArrayList<>(threadIds);
			Collections.sort(sortedThreadIds);
			
			for (Long threadId : sortedThreadIds) {
				
				XdebugThread thread = threads.get(threadId);
				DefaultMutableTreeNode threadNode = new DefaultMutableTreeNode(thread);
				sessionNode.add(threadNode);
				
				// Add stack levels to the thread node.
				LinkedList<XdebugStackLevel> stack = thread.getStack();
				for (XdebugStackLevel stackLevel : stack) {
					
					DefaultMutableTreeNode stackLevelNode = new DefaultMutableTreeNode(stackLevel);
					threadNode.add(stackLevelNode);
				}
			}
			
			// Update the tree view to reflect changes in stack information.
			enableTreeSessionsEvents = false;
			
			try {
				treeSessions.updateUI();
				Utility.expandAll(treeSessions, true);
			}
			catch (Exception e) {
				onException(e);
			}
			
			Safe.invokeLater(() -> {
				enableTreeSessionsEvents = true;
			});
		});
	}
	
	/**
	 * Get session node.
	 * @param sessionId
	 * @return
	 */
	private DefaultMutableTreeNode getSessionNode(int sessionId) {
		
		try {
			// Check root node.
			if (sessionsRootNode == null) {
				return null;
			}
			
			// Get session nodes.
			Enumeration<TreeNode> sessionNodes = sessionsRootNode.children();
			while (sessionNodes.hasMoreElements()) {
				
				TreeNode node = sessionNodes.nextElement();
				if (!(node instanceof DefaultMutableTreeNode)) {
					continue;
				}
				
				DefaultMutableTreeNode sessionNode = (DefaultMutableTreeNode) node;
				Object userObject = sessionNode.getUserObject();
				
				if (!(userObject instanceof XdebugListenerSession)) {
					continue;
				}
				
				XdebugListenerSession session = (XdebugListenerSession) userObject;
				int foundSessionId = session.getSessionId();
				
				// Ceck session ID and return the session object.
				if (foundSessionId == sessionId) {
					return sessionNode;
				}
			}
		}
		catch (Exception e) {
            onException(e);
        }
		return null;
	}
	
	/**
	 * Load watch list values from Xdebug client.
	 */
	private void loadWatchListValues(XdebugStackLevel stackLevel) {
		
		if (loadWatchListValuesRunning ) {
			return;
		}
		loadWatchListValuesRunning = true;
		
		try {
			// Get watched items and load theirs current values.
			LinkedList<DebugWatchItem> watchedItems = getWatchTableItems();
			if (watchedItems != null) {
				
				for (DebugWatchItem watchedItem : watchedItems) {
					loadWatchedValue(stackLevel, watchedItem);
				}
			}
		}
		catch (Exception e) {
            onException(e);
        }
		
		Safe.invokeLater(() -> {
			loadWatchListValuesRunning = false;
		});
	}
	
	/**
	 * Load watched value.
	 * @param stackLevel
	 * @param watchedItem
	 * @return
	 * @throws Exception 
	 */
	private void loadWatchedValue(XdebugStackLevel stackLevel, DebugWatchItem watchedItem) {
		
		try {
			// Get session object from input stack level.
			XdebugListenerSession session = stackLevel.getSession();
			
			// Get properties needed for Xdebug property_get statement.
			int stateHashCode = stackLevel.getStateHashCode();
			String stateHashText = String.valueOf(stateHashCode);
			String watchedName = watchedItem.getName();
			
			DebugWatchGroup watchedGroup = watchedItem.getGroup();
			String watchedGroupText = watchedGroup.getName();
			
			// Send property_get statement for each property context.
			int contextCount = XdebugClient.getContextsCount();
			
			for (int debuggerContextId = 0; debuggerContextId < contextCount; debuggerContextId++) {
				String contextIdText = String.valueOf(debuggerContextId);
				
				// Send Xdebug property_get statement.
				int transactionId = session.createTransaction("property_get", 
					new String [][] { { "-h", stateHashText }, { "-n", watchedName }, { "-g", watchedGroupText }, { "-c", contextIdText } }, "", 
					response -> {
					
					try {
						// Get watched item from Xdebug result and display it.
						DebugWatchItem watchedItemResult = response.getXdebugWathItemResult(watchedGroup);
						Safe.invokeLater(() -> {
							displayWatchedValue(watchedItemResult);
						});
					}
					catch (Exception e) {
						onException(e);
					}
				});
			
				session.beginTransactionWait(transactionId, RESPONSE_TIMEOUT_MS);
			}
		}
		catch (Exception e) {
			onException(e);
		}
	}

	/**
	 * Display watched item value.
	 * @param watchedItem
	 */
	private void displayWatchedValue(DebugWatchItem watchedItem) {
		
		Safe.invokeLater(() -> {
			
			// Try to find watched item by its name and property type.
			int rowCount = tableWatchModel.getRowCount();
			for (int row = 0; row < rowCount; row++) {
				
				// Get table cell values.
				Object nameObject = tableWatchModel.getValueAt(row, WATCHED_NAME_COLUMN_INDEX);
				Object propertyTypeObject = tableWatchModel.getValueAt(row, WATCHED_PROPERTY_TYPE_COLUMN_INDEX);
				
				// Check the cell values.
				if (!(nameObject instanceof String) || !(propertyTypeObject instanceof DebugWatchGroup)) {
					continue;
				}
				
				String name = (String) nameObject;
				DebugWatchGroup propertyType = (DebugWatchGroup) propertyTypeObject;
				
				// If the watched item matches input, update its full name, value and value type.
				if (watchedItem.matches(name, propertyType)) {
					
					String fullNameText = watchedItem.getFullName();
					tableWatchModel.setValueAt(fullNameText, row, WATCHED_FULLNAME_COLUMN_INDEX);
					
					String valueText = watchedItem.getWatchedValue();
					tableWatchModel.setValueAt(valueText, row, WATCHED_VALUE_COLUMN_INDEX);
					
					String valueTypeText = watchedItem.getValueType();
					tableWatchModel.setValueAt(valueTypeText, row, WATCHED_VALUE_TYPE_COLUMN_INDEX);
				}
			}
			
			// Update wathed items table.
			tableWatch.updateUI();
		});
	}

	/**
	 * Get list of watched items in GUI table.
	 * @return
	 */
	private LinkedList<DebugWatchItem> getWatchTableItems() {
		
		try {
			LinkedList<DebugWatchItem> watchedList = new LinkedList<DebugWatchItem>();
			
			int rowCount = tableWatchModel.getRowCount();
			for (int row = 0; row < rowCount; row++) {
				
				// Get item name and type.
				Object cellValue = tableWatchModel.getValueAt(row, WATCHED_NAME_COLUMN_INDEX);
				if (!(cellValue instanceof String)) {
					continue;
				}
				String name = (String) cellValue;
				
				cellValue = tableWatchModel.getValueAt(row, WATCHED_PROPERTY_TYPE_COLUMN_INDEX);
				if (!(cellValue instanceof DebugWatchGroup)) {
					continue;
				}
				DebugWatchGroup type = (DebugWatchGroup) cellValue;
				
				DebugWatchItem watchedItem = new DebugWatchItem(name, type);
				watchedList.add(watchedItem);
			}
			
			return watchedList;
		}
		catch (Exception e) {
            onException(e);
        }
		return null;
	}
	
	/**
	 * Print message on console
	 * @param message
	 */
	private void consolePrint(String message) {
		
		try {
			String content = textInfo.getText() + message + "\r\n";
			textInfo.setText(content);
		}
		catch (Exception e) {
            onException(e);
        }
	}

	/**
	 * On send command to Xdebug server
	 * @throws Exception 
	 */
	protected void onSendCommand() {
		
		try {
			// Get command text.
			String command = textCommand.getText();
			if (command.isEmpty()) {
				return;
			}
			
			XdebugListenerSession session = currentSession;
			
			// Send command to Xdebug client.
			int transactionId = session.createTransaction("expr", null, command, response -> {
				
				try {
					// On error, display error message.
					boolean isError = response.isErrorPacket();
					if (isError) {
						String errorMessage = response.getErrorMessage();
						consolePrint(errorMessage);
						return;
					}
					
					// Print result in text view.
					String resultText = response.getExprResult();
					consolePrint(resultText);
				} 
				catch (Exception e) {
					onException(e);
				}
			});
			session.beginTransaction(transactionId);
		}
		catch (Exception e) {
			onException(e);
		}
	}

	/**
	 * Shows reload alert depending on input parameter
	 * @param show
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private void pageReloadException(boolean show) throws Exception {
		
		if (show) {
			Utility.show(this, "org.multipage.generator.messageReloadPageToStartDebugger");
			return;
		}
	}
	
	/**
	 * On run command.
	 */
	protected void onRun() {
		
		// Process run command
		try {
			if (currentSession == null) {
				return;
			}
			
			// Send "run" command.
			int transactionId = currentSession.createTransaction("run", null, response -> {
				
				try {
					XdebugListenerSession.throwPossibleException(response);
				}
				catch (Exception e) {
					onException(e);
				}
			});
			currentSession.beginTransactionWait(transactionId, RESPONSE_TIMEOUT_MS);
		}
		catch (Exception e) {
			onException(e);
		}
	}
	
	/**
	 * On step into.
	 */
	protected void onStepInto() {
		
		// Process step command.
		try {
			if (currentSession == null) {
				return;
			}
			
			int transactionId = currentSession.createTransaction("step_into", null, response -> {
				try {
					XdebugListenerSession.throwPossibleException(response);
				}
				catch (Exception e) {
					onException(e);
				}
			});
			currentSession.beginTransactionWait(transactionId, RESPONSE_TIMEOUT_MS);
		}
		catch (Exception e) {
			onException(e);
		}
	}

	/**
	 * On step over
	 */
	protected void onStepOver() {
		
		// Process step command
		try {
			if (currentSession == null) {
				return;
			}
			
			// Run "step over" command.
			int transactionId = currentSession.createTransaction("step_over", null, response -> {
				
				try {
					XdebugListenerSession.throwPossibleException(response);
				}
				catch (Exception e) {
					onException(e);
				}
			});
			currentSession.beginTransactionWait(transactionId, RESPONSE_TIMEOUT_MS);
		}
		catch (Exception e) {
			onException(e);
		}
	}	

	/**
	 * On step out
	 */
	protected void onStepOut() {
		
		// Process step command
		try {
			if (currentSession == null) {
				return;
			}
			
			// Run "step out" command.
			int transactionId = currentSession.createTransaction("step_out", null, response -> {
				try {
					XdebugListenerSession.throwPossibleException(response);
				}
				catch (Exception e) {
					onException(e);
				}
			});
			currentSession.beginTransactionWait(transactionId, RESPONSE_TIMEOUT_MS);
		}
		catch (Exception e) {
			onException(e);
		}
	}

	/**
	 * On stop.
	 */
	protected void onStop() {
		
		// Process stop command
		try {
			if (currentSession == null) {
				return;
			}
			
			stopSession(currentSession);
		}
		catch (Exception e) {
			onException(e);
		}
	}
	
	/**
	 * Stop session.
	 * @param xdebugSession
	 * @param completedLambda
	 */
	private void stopSession(XdebugListenerSession xdebugSession) {
		
		// Process stop command.
		try {
		
			int transactionId = xdebugSession.createTransaction("stop", null, response -> {
				
				try {
					XdebugListenerSession.throwPossibleException(response);
				}
				catch (Exception e) {
					onException(e);
				}
			});
			xdebugSession.beginTransactionWait(transactionId, RESPONSE_TIMEOUT_MS);
		}
		catch (Exception e) {
			onException(e);
		}					
	}
	
	/**
	 * Select top stack level for input session.
	 * @param session
	 */
	private void selectStackTop(XdebugListenerSession session) {
		
		// In the tree views, find top stack level within input session and select it.
		Safe.invokeLater(() -> {
			
			Utility.traverseElements(treeSessions, userObject -> treeNode -> parentNode -> {
				try {
					// If found, create node path and select it.
					if (session.equals(userObject)) {
		
						Enumeration<TreeNode> threadNodes = treeNode.children();
						boolean success = threadNodes.hasMoreElements();
						if (success) {
							
							TreeNode threadNode = threadNodes.nextElement();
							if (threadNode instanceof DefaultMutableTreeNode) {
								
								DefaultMutableTreeNode threadMutableNode = (DefaultMutableTreeNode) threadNode;
								Enumeration<TreeNode> stackNodes = threadMutableNode.children();
								
								success = stackNodes.hasMoreElements();
								if (success) {
									
									TreeNode stackTopNode = stackNodes.nextElement();
									if (stackTopNode instanceof DefaultMutableTreeNode) {
										
										DefaultMutableTreeNode stackTopMutableNode = (DefaultMutableTreeNode) stackTopNode;
										TreeNode [] nodePath = stackTopMutableNode.getPath();
										
										TreePath treePathToSelect = new TreePath(nodePath);
										
										// Set selection.
										treeSessions.clearSelection();
										treeSessions.addSelectionPath(treePathToSelect);
										
										// Invoke selection method.
										Safe.invokeLater(() -> {
											onSelectTreeNode();
										});
									}
								}
							}
						}
						return true;
					}
				}
				catch (Exception e) {
                    onException(e);
                }
				return false;
			});
		});
	}
	
	/**
	 * Update log. Use control values.
	 */
	private void updateLog() {
		
	}
	
	/**
	 * Sets listeners.
	 */
	private void setListeners() {
		
		try {
			// Full text filter.
			Utility.onChangeText(textFilter, filterString -> {
				try {
					updateLog();
				}
				catch (Exception e) {
                    onException(e);
                }
			});
		}
		catch (Exception e) {
            onException(e);
        }
	}
	
	/**
	 * On searching in main text editor.
	 */
	private void onSearchText() {
		
        // Open search dialog.
		String selectedText = textCode.getSelectedText();
		findDialog.setVisible(true);
		findDialog.setFindText(selectedText);
	}
	
	/**
	 * On change filter case sensitive.
	 */
	protected void onCaseSensitiveChange() {
		try {
			updateLog();
		}
		catch (Exception e) {
			onException(e);
		}
	}

	/**
	 * On change filter whole words.
	 */
	protected void onWholeWordsChange() {
		try {
			updateLog();
		}
		catch (Exception e) {
			onException(e);
		}
	}

	/**
	 * On change filter exact match.
	 */
	protected void onExactMatchChange() {
		try {
			updateLog();
		}
		catch (Exception e) {
			onException(e);
		}
	}
	
	/**
	 * Show user alert.
	 * @param message
	 * @param timeout 
	 */
	public void showUserAlert(String message, int timeout) {
		
		Safe.invokeLater(() -> {
			AlertWithTimeout.showDialog(this, message, timeout);
		});
	}
	
	/**
	 * Called on exception.
	 * @param e
	 */
	protected void onThrownException(Throwable e) throws Exception {
		
		// Override this method.
		onException(e);
		throw new Exception(e);
	}
	
	/**
	 * Called on exception.
	 * @param messageFormatId
	 * @param exception
	 */
	private void onException(String messageFormatId, Object ... params) {
		
		String messageFormat = Resources.getString(messageFormatId);
		String errorMessage = String.format(messageFormat, params);
		
		Exception e = new Exception(errorMessage);
		onException(e);
	}
	
	/**
	 * Called on exception.
	 * @param e
	 */
	private static void onExceptionStatic(Exception e) {
		
		e.printStackTrace();
	}
	
	/**
	 * Called on exception.
	 * @param e
	 */
	private void onException(Throwable e) {
		
		e.printStackTrace();
	}
	
	/**
	 * Adds popup menu.
	 * @param component
	 * @param popup
	 */
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
