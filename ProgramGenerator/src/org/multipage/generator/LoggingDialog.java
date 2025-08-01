/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2021-03-04
 *
 */
package org.multipage.generator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.function.BiFunction;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.maclan.help.ProgramHelp;
import org.maclan.server.AreaServer;
import org.multipage.gui.ApplicationEvents;
import org.multipage.gui.EventHandle;
import org.multipage.gui.EventSource;
import org.multipage.gui.GeneralGui;
import org.multipage.gui.Images;
import org.multipage.gui.Message;
import org.multipage.gui.RendererJLabel;
import org.multipage.gui.Signal;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.TextAreaEx;
import org.multipage.gui.TextPaneEx;
import org.multipage.gui.ToolBarKit;
import org.multipage.gui.Utility;
import org.multipage.util.Lock;
import org.multipage.util.Obj;
import org.multipage.util.Safe;
import org.multipage.util.j;

/**
 * Dialog that displays log.
 * @author vakol
 *
 */
public class LoggingDialog extends JDialog {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * List of state constants.
	 */
	public static final int RUNNING = 0;
	public static final int BREAKED = 1;

	/**
	 * Enable/disable logging.
	 */
	private static Boolean enabled = false;

	// $hide>>$

	/**
	 * If the following flag is set to true, the dialog is opened when it is
	 * initialized.
	 */
	private static boolean openedWhenInitialized = false;

	/**
	 * Switch between list and single item view for the log.
	 */
	private static boolean logList = true;

	/**
	 * Message queue viewer update interval
	 */
	private static int messageQueueUpdateIntervalMs = 3000;

	/**
	 * Events tree update interval in milliseconds.
	 */
	private static int eventTreeUpdateIntervalMs = 1000;

	/**
	 * Omit/choose selected signals.
	 */
	private static boolean omitChooseSignals = true;

	/**
	 * Message limit.
	 */
	public static int logLimit = 20;
	
	/**
	 * Queue limit.
	 */
	public static int queueLimit = 20;
	
	/**
	 * Limit of logged events.
	 */
	public static int eventLimit = 30;

	/**
	 * Bounds.
	 */
	private static Rectangle bounds = null;

	/**
	 * Splitter positions.
	 */
	private static int eventsWindowSplitter = -1;
	private static int queueWindowSplitter = -1;

	/**
	 * Selected tab.
	 */
	private static int selectedTab = 0;

	/**
	 * Index of font size for the simple log view.
	 */
	private static int logFontSizeIndex = 0;

	/**
	 * Dark green color constant.
	 */
	private static final Color DARK_GREEN = new Color(0, 128, 0);

	/**
	 * A set of available break point classes.
	 */
	private static final HashSet<Class<?>> availableBreakPointClasses = Utility.makeSet(Signal.class, Message.class,
			LoggedEvent.class);

	/**
	 * Set default state.
	 */
	public static void setDefaultData() {

		bounds = new Rectangle();
	}

	/**
	 * Read state.
	 * 
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void serializeData(StateInputStream inputStream) throws IOException, ClassNotFoundException {

		bounds = Utility.readInputStreamObject(inputStream, Rectangle.class);
		logFontSizeIndex = inputStream.readInt();
		openedWhenInitialized = inputStream.readBoolean();
		logList = inputStream.readBoolean();
		omitChooseSignals = inputStream.readBoolean();
		omittedOrChosenSignals = Utility.readInputStreamObject(inputStream, HashSet.class);
		eventsWindowSplitter = inputStream.readInt();
		queueWindowSplitter = inputStream.readInt();
		selectedTab = inputStream.readInt();
		messageQueueUpdateIntervalMs = inputStream.readInt();
		eventTreeUpdateIntervalMs = inputStream.readInt();
		queueLimit = inputStream.readInt();
		logLimit = inputStream.readInt();
		eventLimit = inputStream.readInt();
	}

	/**
	 * Write state.
	 * 
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream) throws IOException {

		outputStream.writeObject(bounds);
		outputStream.writeInt(logFontSizeIndex);
		outputStream.writeBoolean(openedWhenInitialized);
		outputStream.writeBoolean(logList);
		outputStream.writeBoolean(omitChooseSignals);
		outputStream.writeObject(omittedOrChosenSignals);
		outputStream.writeInt(eventsWindowSplitter);
		outputStream.writeInt(queueWindowSplitter);
		outputStream.writeInt(selectedTab);
		outputStream.writeInt(messageQueueUpdateIntervalMs);
		outputStream.writeInt(eventTreeUpdateIntervalMs);
		outputStream.writeInt(queueLimit);
		outputStream.writeInt(logLimit);
		outputStream.writeInt(eventLimit);
	}

	/**
	 * Logged message class.
	 */
	public static class LoggedMessage {

		/**
		 * Message text
		 */
		private String messageText = "unknown";

		/**
		 * Time stamp.
		 */
		private long timeStamp = -1;

		/**
		 * Logged message.
		 * 
		 * @param message
		 */
		public LoggedMessage(String message) {
			try {
				
				this.messageText = message;
				this.timeStamp = System.currentTimeMillis();
			}
			catch(Throwable expt) {
				Safe.exception(expt);
			};
		}

		/**
		 * Get message text.
		 * 
		 * @return
		 */
		public String getText() {
			
			try {
				Timestamp timeStamp = new Timestamp(this.timeStamp);
				
				DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
			            .withLocale(Locale.ENGLISH);
				
				ZonedDateTime dateTime = timeStamp.toInstant().atZone(ZoneId.systemDefault());
			    String timeStampText = dateTime.format(formatter);
				
				return String.format("[%s] %s", timeStampText, this.messageText);
			}
			catch (Throwable e) {
				Safe.exception(e);
			}
			return "";
		}
	}

	/**
	 * Logged event class.
	 */
	private static class LoggedEvent {

		/**
		 * Invoked event handle.
		 */
		public EventHandle eventHandle = null;

		/**
		 * Error flags.
		 */
		public Long executionTime = null;

		/**
		 * Matching message.
		 */
		public Message matchingMessage = null;
	}

	/**
	 * Logged messages.
	 */
	private static LinkedList<LoggedMessage> logTexts = new LinkedList<LoggedMessage>();

	/**
	 * Logged message queue snapshots. Maps: Time Moment -> List of Messages
	 */              
	private static LinkedHashMap<String, LinkedList<Message>> messageQueueSnapshots = new LinkedHashMap<String, LinkedList<Message>>();
	
	/**
	 * Message queue divider number.
	 */
	private static int messageQueueSnapshotsDividerNumber = 0;
	
	/**
	 * Message queue divider number.
	 */
	private static int eventsDividerNumber = 0;

	/**
	 * Logged events. Maps: Signal or a textual divider between signals -> Message -> Execution time -> Event
	 */
	private static LinkedHashMap<Signal, LinkedHashMap<Message, LinkedHashMap<Long, LinkedList<LoggedEvent>>>> events = new LinkedHashMap<Signal, LinkedHashMap<Message, LinkedHashMap<Long, LinkedList<LoggedEvent>>>>();
	
	/**
	 * Omitted signals.
	 */
	private static HashSet<Signal> omittedOrChosenSignals = new HashSet<Signal>();

	/**
	 * Singleton dialog object.
	 */
	private static LoggingDialog dialog = null;

	/**
	 * Break point matching object.
	 */
	private static HashSet<Object> breakPointMatchObjects = new HashSet<Object>();

	/**
	 * Divider of logged items.
	 */
	private static final String divider = "-----------------------------";

	/**
	 * Set enable/disable logging.
	 * 
	 * @param flag
	 */
	public static void enableLogging(boolean flag) {

		synchronized (LoggingDialog.enabled) {

			LoggingDialog.enabled = flag;
		}
	}

	/**
	 * Returns true if the logging is enabled.
	 * 
	 * @return
	 */
	public static boolean isLoggingEnabled() {

		synchronized (LoggingDialog.enabled) {

			if (LoggingDialog.enabled) {
				return true;
			}
		}

		if (LoggingDialog.dialog == null) {
			return false;
		}

		synchronized (LoggingDialog.dialog) {
			try {
				return LoggingDialog.dialog.isVisible();
			}
			catch (Throwable e) {
				Safe.exception(e);
			}
			return false;
		}
	}

	/**
	 * Initialize this dialog.
	 */
	public static void initialize(Component parent) {
		try {
			
			Window parentWindow = Utility.findWindow(parent);
			dialog = new LoggingDialog(parentWindow);
	
			// Attach the Area Server.
			AreaServer.setCanLogLambda(() -> {
					try {
						return isLoggingEnabled();
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return false;
				});
			
			AreaServer.setLogLambda(text -> {
					try {
						
						log(text);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				});
			
			AreaServer.setLogInvolveUserLambda(() -> {
				    try {
						
						LoggingDialog.involveUserAction();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				});
	
			// Attach the help module.
			ProgramHelp.setCanLogLambda(() -> {
				    try {
                       return isLoggingEnabled();
				    }
					catch (Throwable e) {
						Safe.exception(e);
					}
					return false;
				});
			
			ProgramHelp.setLogLambda(text -> {
					try {
						
						log(text);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				});
			
			ProgramHelp.setLogInvolveUserLambda(() -> {
					try {
						
						LoggingDialog.involveUserAction();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				});
			
			// Attach general GUI functions module.
			GeneralGui.setCanLogLambda(() -> {
					try {
						return isLoggingEnabled();
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return false;
				});
			
			GeneralGui.setLogLambda(text -> {
					try {
						
						log(text);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				});
			
			GeneralGui.setLogInvolveUserLambda(() -> {
					try {
						
						LoggingDialog.involveUserAction();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				});
	
			// If the following flag was set, open the dialog.
			if (openedWhenInitialized) {
				showDialog(parentWindow);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Root node of the message queue viewer.
	 */
	private DefaultMutableTreeNode messageQueueTreeRootNode;

	/**
	 * Message queue tree model.
	 */
	private DefaultTreeModel messageQueueTreeModel;

	/**
	 * A timer that updates message queue viewer.
	 */
	private Timer updateMessageQueueTimer = null;

	/**
	 * Root node of the events tree.
	 */
	private DefaultMutableTreeNode treeNodeEventRoot = null;

	/**
	 * Tree model for displaying logged events.
	 */
	private DefaultTreeModel treeModelEvents = null;

	/**
	 * Update event tree timer.
	 */
	private Timer updateEventTreeTimer = null;

	/**
	 * List model of break points set.
	 */
	private DefaultListModel listBreakPointsModel = null;

	/**
	 * Recently selected event tree object.
	 */
	private Object lastSelectedTreeObject;

	/**
	 * State of the dialog.
	 */
	private int state = RUNNING;

	/**
	 * Synchronization object for the dialog state. Used while accessing the state.
	 */
	private Object stateSynchronizaton = new Object();

	/**
	 * Lock of logging process.
	 */
	private Lock logLock = new Lock();

	// $hide<<$

	/**
	 * Components.
	 */
	private TextAreaEx textLog;
	private JTabbedPane tabbedPane;
	private DefaultListModel<Signal> listModelOmittedSignals;
	private JPanel panelBreakPoints;
	private JList listBreakPoints;
	private JToolBar toolBarBreakPoints;
	private JPanel panelEvents;
	private JToolBar toolBarEvents;
	private JSplitPane splitPaneEvents;
	private JScrollPane scrollPaneEvents;
	private JTree treeEvents;
	private JPanel panelOmitOrChooseSignals;
	private JCheckBox checkOmitOrChooseSignals;
	private JScrollPane scrollPaneOmitOrChoose;
	private JList<Signal> listOmittedOrChosenSignals;
	private JPanel panelLog;
	private JPopupMenu popupMenu;
	private JMenuItem menuAddBreakPoint;
	private JScrollPane scrollPaneEventsDescription;
	private JTextPane textEventDescription;
	private JMenuItem menuAddOmittedChosen;
	private JButton buttonClearOmitedChosen;
	private JSeparator separator;
	private JMenuItem menuEventsPrintReflection;
	private JPanel panelMessageQueue;
	private JScrollPane scrollPaneMessageQueue;
	private JTree treeMessageQueue;
	private JToolBar toolBarMessageQueue;
	private JSplitPane splitPaneMessageQueue;
	private JScrollPane scrollPaneQueueMessageDescription;
	private JTextPane textQueueMessage;
	private JPopupMenu popupMenuMessageQueues;
	private JMenuItem menuMessageQueuePrintReflection;
	private JMenuItem menuMessageGoToEvent;
	private JMenuItem menuGoToQueueMessage;
	private JToolBar toolBarLog;
	private JToggleButton buttonListOrSingleItem;
	private JComboBox<Integer> comboFontSize;
	private JLabel labelLogFontSize;
	private JButton buttonStepSingleItem;
	private JButton buttonRunItems;
	private JButton buttonBreakItems;

	/**
	 * Show dialog.
	 * 
	 * @param parent
	 */
	public static void showDialog(Component parent) {
		try {
			
			// Show window.
			dialog.setVisible(true);
	
			// Reset the flag.
			openedWhenInitialized = true;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create the dialog.
	 */
	public LoggingDialog(Window parentWindow) {
		super(parentWindow, ModalityType.MODELESS);

		synchronized (this) {// $hide$
			try {
				initComponents();
				postCreate(); // $hide$
			}
			catch (Throwable e) {
				Safe.exception(e);
			}
		} // $hide$
	}

	/**
	 * Initialize components.
	 */
	private void initComponents() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onClose();
			}
		});
		setTitle("org.multipage.generator.textLoggingDialogTitle");
		setBounds(100, 100, 557, 471);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		springLayout.putConstraint(SpringLayout.NORTH, tabbedPane, 3, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, tabbedPane, 3, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, tabbedPane, -3, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, tabbedPane, -3, SpringLayout.EAST, getContentPane());
		getContentPane().add(tabbedPane);

		treeEvents = new JTree();
		DefaultTreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
		selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		treeEvents.setSelectionModel(selectionModel);
		treeEvents.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				onEventSelection();
			}
		});

		panelLog = new JPanel();
		tabbedPane.addTab("org.multipage.generator.textLoggedMessages", null, panelLog, null);
		panelLog.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPaneLog = new JScrollPane();
		panelLog.add(scrollPaneLog, BorderLayout.CENTER);

		textLog = new TextAreaEx();
		textLog.setLineWrap(true);
		scrollPaneLog.setViewportView(textLog);

		toolBarLog = new JToolBar();
		toolBarLog.setFloatable(false);
		panelLog.add(toolBarLog, BorderLayout.NORTH);

		labelLogFontSize = new JLabel("org.multipage.generator.textLogFontSize");
		toolBarLog.add(labelLogFontSize);

		comboFontSize = new JComboBox<Integer>();
		comboFontSize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onFontSize();
			}
		});
		comboFontSize.setMaximumSize(new Dimension(50, 22));
		toolBarLog.add(comboFontSize);

		panelMessageQueue = new JPanel();
		tabbedPane.addTab("org.multipage.generator.textLogMessageQueue", null, panelMessageQueue, null);
		panelMessageQueue.setLayout(new BorderLayout(0, 0));
		DefaultTreeSelectionModel queueSelectionModel = new DefaultTreeSelectionModel();
		queueSelectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		treeEvents.setSelectionModel(queueSelectionModel);

		toolBarMessageQueue = new JToolBar();
		toolBarMessageQueue.setFloatable(false);
		panelMessageQueue.add(toolBarMessageQueue, BorderLayout.NORTH);

		splitPaneMessageQueue = new JSplitPane();
		splitPaneMessageQueue.setResizeWeight(0.8);
		splitPaneMessageQueue.setOrientation(JSplitPane.VERTICAL_SPLIT);
		panelMessageQueue.add(splitPaneMessageQueue, BorderLayout.CENTER);

		scrollPaneMessageQueue = new JScrollPane();
		splitPaneMessageQueue.setLeftComponent(scrollPaneMessageQueue);

		treeMessageQueue = new JTree();
		treeMessageQueue.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				onMessageQueueObjectSelected();
			}
		});
		treeMessageQueue.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		scrollPaneMessageQueue.setViewportView(treeMessageQueue);

		popupMenuMessageQueues = new JPopupMenu();
		addPopup(treeMessageQueue, popupMenuMessageQueues);

		menuMessageQueuePrintReflection = new JMenuItem("org.multipage.generator.menuLogPrintReflection");
		menuMessageQueuePrintReflection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onPrintReflection(treeMessageQueue);
			}
		});

		menuMessageGoToEvent = new JMenuItem("org.multipage.generator.menuLogMessageGoToEvent");
		menuMessageGoToEvent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onGoToMessageEvent();
			}
		});
		popupMenuMessageQueues.add(menuMessageGoToEvent);
		popupMenuMessageQueues.add(menuMessageQueuePrintReflection);

		scrollPaneQueueMessageDescription = new JScrollPane();
		splitPaneMessageQueue.setRightComponent(scrollPaneQueueMessageDescription);

		textQueueMessage = new TextPaneEx();
		textQueueMessage.setContentType("text/html");
		scrollPaneQueueMessageDescription.setViewportView(textQueueMessage);

		panelEvents = new JPanel();
		tabbedPane.addTab("org.multipage.generator.textLoggedConditionalEvents", null, panelEvents, null);
		panelEvents.setLayout(new BorderLayout(0, 0));

		toolBarEvents = new JToolBar();
		toolBarEvents.setFloatable(false);
		panelEvents.add(toolBarEvents, BorderLayout.NORTH);

		splitPaneEvents = new JSplitPane();
		splitPaneEvents.setResizeWeight(0.7);
		splitPaneEvents.setOrientation(JSplitPane.VERTICAL_SPLIT);
		panelEvents.add(splitPaneEvents, BorderLayout.CENTER);

		scrollPaneEvents = new JScrollPane();
		splitPaneEvents.setLeftComponent(scrollPaneEvents);
		scrollPaneEvents.setViewportView(treeEvents);

		popupMenu = new JPopupMenu();
		addPopup(treeEvents, popupMenu);

		menuAddBreakPoint = new JMenuItem("org.multipage.generator.menuAddLogBreakPoint");
		menuAddBreakPoint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onAddBreakPoint();
			}
		});
		popupMenu.add(menuAddBreakPoint);

		menuAddOmittedChosen = new JMenuItem("org.multipage.generator.menuAddLogOmittedChosenSignal");
		menuAddOmittedChosen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onMenuOmitChooseSignal();
			}
		});
		popupMenu.add(menuAddOmittedChosen);

		menuEventsPrintReflection = new JMenuItem("org.multipage.generator.menuLogPrintReflection");
		menuEventsPrintReflection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onPrintReflection(treeEvents);
			}
		});

		menuGoToQueueMessage = new JMenuItem("org.multipage.generator.menuLogMessageGoToQueue");
		menuGoToQueueMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onGoToMessageQueue();
			}
		});
		popupMenu.add(menuGoToQueueMessage);
		popupMenu.add(menuEventsPrintReflection);

		scrollPaneEventsDescription = new JScrollPane();
		splitPaneEvents.setRightComponent(scrollPaneEventsDescription);

		textEventDescription = new TextPaneEx();
		textEventDescription.setEditable(false);
		textEventDescription.setContentType("text/html");
		scrollPaneEventsDescription.setViewportView(textEventDescription);

		panelOmitOrChooseSignals = new JPanel();
		tabbedPane.addTab("org.multipage.generator.textOmitOrChooseSignals", null, panelOmitOrChooseSignals, null);
		panelOmitOrChooseSignals.setLayout(new BorderLayout(0, 0));

		JPanel panelTopOmitOrChoose = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelTopOmitOrChoose.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panelOmitOrChooseSignals.add(panelTopOmitOrChoose, BorderLayout.NORTH);

		checkOmitOrChooseSignals = new JCheckBox("org.multipage.generator.textOmitOrChoose");
		checkOmitOrChooseSignals.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOmitChooseSignals();
			}
		});

		buttonClearOmitedChosen = new JButton("");
		buttonClearOmitedChosen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClearOmittedChosen();
			}
		});
		buttonClearOmitedChosen.setToolTipText("org.multipage.generator.tooltipLogClearOmitedChoseSignal");
		buttonClearOmitedChosen.setPreferredSize(new Dimension(24, 24));
		buttonClearOmitedChosen.setMargin(new Insets(0, 0, 0, 0));
		panelTopOmitOrChoose.add(buttonClearOmitedChosen);

		separator = new JSeparator();
		separator.setPreferredSize(new Dimension(2, 24));
		separator.setOrientation(SwingConstants.VERTICAL);
		panelTopOmitOrChoose.add(separator);
		panelTopOmitOrChoose.add(checkOmitOrChooseSignals);

		scrollPaneOmitOrChoose = new JScrollPane();
		panelOmitOrChooseSignals.add(scrollPaneOmitOrChoose, BorderLayout.CENTER);

		listOmittedOrChosenSignals = new JList();
		listOmittedOrChosenSignals.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				onOmittedOrChosenSignalClick(e);
			}
		});
		scrollPaneOmitOrChoose.setViewportView(listOmittedOrChosenSignals);

		panelBreakPoints = new JPanel();
		tabbedPane.addTab("org.multipage.generator.textBreakPointsInLogWindow", null, panelBreakPoints, null);
		panelBreakPoints.setLayout(new BorderLayout(0, 0));

		toolBarBreakPoints = new JToolBar();
		toolBarBreakPoints.setFloatable(false);
		panelBreakPoints.add(toolBarBreakPoints, BorderLayout.NORTH);

		JScrollPane scrollPaneBreakPoints = new JScrollPane();
		scrollPaneBreakPoints.setBorder(null);
		panelBreakPoints.add(scrollPaneBreakPoints, BorderLayout.CENTER);

		listBreakPoints = new JList();
		scrollPaneBreakPoints.setViewportView(listBreakPoints);
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			createToolBars();
			localize();
			setIcons();
			loadDialog();
			createMessaqeQueueTree();
			createEventTree();
			createOmittedSignalList();
			createBreakPointsList();
			setListeners();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Creates tool bars with buttons that enable user to run actions for logged
	 * items.
	 */
	private void createToolBars() {
		try {
			
			// Add tool bar for log.
			buttonListOrSingleItem = ToolBarKit.addToggleButton(toolBarLog, "org/multipage/generator/images/list.png",
					"org.multipage.generator.tooltipLogListOrSingleItem", () -> onListOrSingleItem());
			buttonStepSingleItem = ToolBarKit.addToolBarButton(toolBarLog, "org/multipage/generator/images/step_item.png",
					"org.multipage.generator.tooltipLogStepSingleItem", () -> onStepSingleMessage());
			buttonRunItems = ToolBarKit.addToolBarButton(toolBarLog, "org/multipage/generator/images/run.png",
					"org.multipage.generator.tooltipLogRunSingleItem", () -> onRunMessages());
			buttonBreakItems = ToolBarKit.addToolBarButton(toolBarLog, "org/multipage/generator/images/break.png",
					"org.multipage.generator.tooltipLogBreakItems", () -> onBreakMessages());
			ToolBarKit.addToolBarButton(toolBarLog, "org/multipage/generator/images/clear.png",
					"org.multipage.generator.tooltipLogClearItems", () -> onClearMessages());
			ToolBarKit.addToolBarButton(toolBarLog, "org/multipage/generator/images/settings.png",
					"org.multipage.generator.tooltipLogSettings", () -> onLogSettings());
			toolBarLog.addSeparator();
			ToolBarKit.addToolBarButton(toolBarLog, "org/multipage/generator/images/divider.png",
					"org.multipage.generator.tooltipLogAddDivider", () -> onPrinTextDivider());
	
			// Load font size.
			loadFontSizes(comboFontSize);
	
			// A tool bar for message queue.
			ToolBarKit.addToolBarButton(toolBarMessageQueue, "org/multipage/generator/images/close_all.png",
					"org.multipage.generator.tooltipClearLoggedQueues", () -> onClearQueues());
			ToolBarKit.addToolBarButton(toolBarMessageQueue, "org/multipage/generator/images/settings.png",
					"org.multipage.generator.tooltipLogSettings", () -> onLogSettings());
			toolBarMessageQueue.addSeparator();
			ToolBarKit.addToolBarButton(toolBarMessageQueue, "org/multipage/generator/images/divider.png",
					"org.multipage.generator.tooltipLogAddDivider", () -> onMessagesDivider());
	
			// A tool bar for logged events.
			ToolBarKit.addToolBarButton(toolBarEvents, "org/multipage/generator/images/close_all.png",
					"org.multipage.generator.tooltipClearLoggedEvents", () -> onClearEvents());
			ToolBarKit.addToolBarButton(toolBarEvents, "org/multipage/generator/images/settings.png",
					"org.multipage.generator.tooltipLoggedEventsSettings", () -> onLogSettings());
			toolBarEvents.addSeparator();
			ToolBarKit.addToolBarButton(toolBarEvents, "org/multipage/generator/images/divider.png",
					"org.multipage.generator.tooltipLogAddDivider", () -> onEventsDivider());
	
			// A tool bar for break points.
			ToolBarKit.addToolBarButton(toolBarBreakPoints, "org/multipage/generator/images/close_all.png",
					"org.multipage.generator.tooltipClearLogBreakPoints", () -> onClearBreakPoints());
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
			Utility.localize(tabbedPane);
			Utility.localize(labelLogFontSize);
			Utility.localize(checkOmitOrChooseSignals);
			Utility.localize(buttonClearOmitedChosen);
			Utility.localize(menuAddBreakPoint);
			Utility.localize(menuAddOmittedChosen);
			Utility.localize(menuEventsPrintReflection);
			Utility.localize(menuMessageQueuePrintReflection);
			Utility.localize(menuMessageGoToEvent);
			Utility.localize(menuGoToQueueMessage);
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
			
			// Set window icon.
			setIconImage(Images.getImage("org/multipage/generator/images/main_icon.png"));
	
			// Set control icons.
			menuAddBreakPoint.setIcon(Images.getIcon("org/multipage/generator/images/breakpoint.png"));
			menuAddOmittedChosen.setIcon(Images.getIcon("org/multipage/generator/images/cancel_icon.png"));
			buttonClearOmitedChosen.setIcon(Images.getIcon("org/multipage/generator/images/close_all.png"));
	
			// Set tree icons.
			DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) treeEvents.getCellRenderer();
			renderer.setOpenIcon(null);
			renderer.setClosedIcon(null);
			renderer.setLeafIcon(null);
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
			
			if (bounds == null || bounds.isEmpty()) {
				Utility.centerOnScreen(this);
				bounds = getBounds();
			} else {
				setBounds(bounds);
			}
			if (queueWindowSplitter != -1) {
				splitPaneMessageQueue.setDividerLocation(queueWindowSplitter);
			} else {
				splitPaneMessageQueue.setDividerLocation(0.8);
			}
			if (eventsWindowSplitter != -1) {
				splitPaneEvents.setDividerLocation(eventsWindowSplitter);
			} else {
				splitPaneEvents.setDividerLocation(0.8);
			}
			comboFontSize.setSelectedIndex(logFontSizeIndex);
			tabbedPane.setSelectedIndex(selectedTab);
			checkOmitOrChooseSignals.setSelected(omitChooseSignals);
			buttonListOrSingleItem.setSelected(logList);
			showUserButtons(true);
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
			
			// Receive the "step log" signal.
			ApplicationEvents.receiver(this, Signal.stepLog, message -> {
				try {
					
					// Notify the log process lock.
					Lock.notify(logLock);
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
	 * Show or hide user buttons.
	 * 
	 * @param show
	 */
	private void showUserButtons(boolean show) {
		try {
			
			// Show or hide buttons with respect to the input flag.
			buttonStepSingleItem.setVisible(show);
			buttonRunItems.setVisible(show);
			buttonBreakItems.setVisible(show);
	
			// Apply current state to the GUI controls of this dialog.
			if (show) {
				applyDialogState();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Clear messages in the log view.
	 */
	private void clearMessages() {
		try {
			
			logTexts.clear();
			compileLog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Print text divider.
	 * 
	 * @return
	 */
	private void onPrinTextDivider() {
		try {
			
			logTextDivider();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Print divider after currently displayed messages.
	 * 
	 * @return
	 */
	private void onMessagesDivider() {
		try {
			
			logMessagesDivider();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Print divider after currently displayed event.
	 * 
	 * @return
	 */
	private void onEventsDivider() {
		try {
			
			logEventsDivider();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	
	/**
	 * Apply dialog state to GUI controls.
	 */
	private void applyDialogState() {
		try {
			
			switch (state) {
	
			case RUNNING:
	
				buttonStepSingleItem.setEnabled(false);
				buttonRunItems.setVisible(false);
				buttonBreakItems.setVisible(true);
	
				// If a single message is logged, clear the view box.
				if (!logList) {
					clearMessages();
				}
				break;
	
			case BREAKED:
	
				buttonStepSingleItem.setEnabled(true);
				buttonRunItems.setVisible(true);
				buttonBreakItems.setVisible(false);
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
			logFontSizeIndex = comboFontSize.getSelectedIndex();
			queueWindowSplitter = splitPaneMessageQueue.getDividerLocation();
			eventsWindowSplitter = splitPaneEvents.getDividerLocation();
			selectedTab = tabbedPane.getSelectedIndex();
			omitChooseSignals = checkOmitOrChooseSignals.isSelected();
			logList = buttonListOrSingleItem.isSelected();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Add popup menu.
	 * 
	 * @param component
	 * @param popup
	 */
	private static void addPopup(Component component, final JPopupMenu popup) {
		try {
			
			component.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					if (e.isPopupTrigger()) {
						try {
							
							showMenu(e);
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
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
	 * Load font sizes.
	 * 
	 * @param comboBox
	 */
	private void loadFontSizes(JComboBox comboBox) {
		try {
			
			comboBox.removeAll();
	
			for (int index = 9; index < 14; index++) {
				int size = (int) Math.pow(Math.E, (double) index / 3.0) / 3 + 3;
				comboBox.addItem(size);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create message queue tree.
	 */
	private void createMessaqeQueueTree() {
		try {
			
			// Create the tree model.
			messageQueueTreeRootNode = new DefaultMutableTreeNode();
			messageQueueTreeModel = new DefaultTreeModel(messageQueueTreeRootNode);
			treeMessageQueue.setModel(messageQueueTreeModel);
	
			// Set tree node renderer.
			treeMessageQueue.setCellRenderer(new TreeCellRenderer() {
	
				// Renderer.
				RendererJLabel renderer = new RendererJLabel();
	
				// Callback method.s
				@Override
				public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
						boolean leaf, int row, boolean hasFocus) {
					
					try {
						// Check value.
						if (!(value instanceof DefaultMutableTreeNode)) {
							renderer.setText("unknown");
						}
						else {
							// Get queue object.
							DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
							Object queueObject = node.getUserObject();
		
							Color nodeColor = Color.BLACK;
		
							// Set node text.
							// On the message.
							if (queueObject instanceof Message) {
								Message message = (Message) queueObject;
								renderer.setText(
										String.format("[0x%08X] message %s", message.hashCode(), message.signal.name()));
								nodeColor = DARK_GREEN;
							}
							// Otherwise...
							else if (queueObject != null) {
								renderer.setText(queueObject.toString());
								nodeColor = Color.GRAY;
							}
							else {
								renderer.setText("root");
								nodeColor = Color.GRAY;
							}
		
							// Set node color.
							renderer.setForeground(nodeColor);
						}
		
						// Set renderer properties
						renderer.set(selected, hasFocus, row);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return renderer;
				}
			});
	
			// Start update timer.
			updateMessageQueueTimer = new Timer(messageQueueUpdateIntervalMs, event -> updateMessageQueueTree());
			updateMessageQueueTimer.start();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
	}

	/**
	 * Create a tree with categorized events.
	 */
	private void createEventTree() {
		try {
			
			// Create and set tree model.
			treeNodeEventRoot = new DefaultMutableTreeNode();
			treeModelEvents = new DefaultTreeModel(treeNodeEventRoot);
			treeEvents.setModel(treeModelEvents);
			treeEvents.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	
			// Set tree node renderer.
			treeEvents.setCellRenderer(new TreeCellRenderer() {
	
				// Renderer.
				RendererJLabel renderer = new RendererJLabel();
	
				// Callback method.s
				@Override
				public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
						boolean leaf, int row, boolean hasFocus) {
					
					try {
						// Check value.
						if (!(value instanceof DefaultMutableTreeNode)) {
							renderer.setText("unknown");
						}
						else {
							// Get event object.
							DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
							Object eventObject = node.getUserObject();
		
							Color nodeColor = Color.BLACK;
		
							// Set node text.
							// On the signal.
							if (eventObject instanceof Signal) {
		
								Signal signal = (Signal) eventObject;
								renderer.setText(signal.name());
								nodeColor = Color.RED;
							}
							// On the message.
							else if (eventObject instanceof Message) {
								Message message = (Message) eventObject;
								renderer.setText(String.format("[0x%08X] message", message.hashCode()));
								nodeColor = DARK_GREEN;
							}
							// On the logged event.
							else if (eventObject instanceof LoggedEvent) {
								LoggedEvent event = (LoggedEvent) eventObject;
								renderer.setText(String.format("[0x%08X] event", event.hashCode()));
								nodeColor = Color.BLUE;
							}
							// Otherwise...
							else if (eventObject != null) {
								renderer.setText(eventObject.toString());
								nodeColor = Color.GRAY;
							}
							else {
								renderer.setText("root");
								nodeColor = Color.GRAY;
							}
		
							// Set node color.
							renderer.setForeground(nodeColor);
						}
		
						// Set renderer properties
						renderer.set(selected, hasFocus, row);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return renderer;
				}
			});
	
			// Create update timer.
			updateEventTreeTimer = new Timer(eventTreeUpdateIntervalMs, event -> {
	
				Safe.invokeLater(() -> {
					synchronized (events) {
						updateEventTree();
					}
				});
			});
			updateEventTreeTimer.start();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
	}

	/**
	 * Create omitted signal list.
	 */
	private void createOmittedSignalList() {
		try {
			
			// Create and set list model.
			listModelOmittedSignals = new DefaultListModel();
	
			// Fill the list model.
			Signal.definedSignals().stream().sorted((s1, s2) -> {
					try {
						return s1.name().compareTo(s2.name());
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return 0;
				})
				.forEach(signal -> {
					try {
						
						listModelOmittedSignals.addElement(signal);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				});
	
			listOmittedOrChosenSignals.setModel(listModelOmittedSignals);
	
			// Create list items renderer.
			ListCellRenderer<Signal> renderer = new ListCellRenderer<Signal>() {
	
				// Rendered label.
				RendererJLabel renderer = new RendererJLabel();
	
				// Renderer callback.
				@Override
				public Component getListCellRendererComponent(JList list, Signal signal, int index, boolean isSelected,
						boolean cellHasFocus) {
					
					try {
						// Set caption.
						renderer.setText(signal.toString());
		
						// Set renderer properties.
						renderer.set(isSelected, cellHasFocus, index);
		
						// If it is omitted, colorize it with red.
						if (omittedOrChosenSignals.contains(signal)) {
							boolean omitted = checkOmitOrChooseSignals.isSelected();
							renderer.setForeground(omitted ? Color.RED : DARK_GREEN);
						}
						else {
							renderer.setForeground(Color.GRAY);
						}
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return renderer;
				}
			};
			listOmittedOrChosenSignals.setCellRenderer(renderer);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
	}

	/**
	 * Create list of break points.
	 */
	@SuppressWarnings("unchecked")
	private void createBreakPointsList() {
		try {
			
			// Create and assign list model.
			listBreakPointsModel = new DefaultListModel();
			listBreakPoints.setModel(listBreakPointsModel);
	
			// Create items renderer.
			listBreakPoints.setCellRenderer(new ListCellRenderer() {
	
				// Rendered label.
				private RendererJLabel renderer = new RendererJLabel();
	
				// Constructor.
				{
					try {
						// Set icon.
						renderer.setIcon(Images.getIcon("org/multipage/generator/images/breakpoint.png"));
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				};
	
				// Callback method.
				@Override
				public Component getListCellRendererComponent(JList list, Object breakPointObject, int index,
						boolean isSelected, boolean cellHasFocus) {
					
					try {
						// Set break point caption.
						renderer.setText(breakPointObject.toString());
						// Set renderer.
						renderer.set(isSelected, cellHasFocus, index);
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
	 * On event selection.
	 * 
	 * @param e
	 */
	protected void onEventSelection() {

		synchronized (treeEvents) {
			try {
				
				// Get selected tree item.
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeEvents.getLastSelectedPathComponent();
				if (node == null) {
					return;
				}
	
				// Check selection change.
				Object selectedObject = node.getUserObject();
				if (lastSelectedTreeObject == selectedObject) {
					return;
				}
				lastSelectedTreeObject = selectedObject;
	
				// Get description.
				String description = getNodeDescription(node);
	
				// Display node description.
				textEventDescription.setText(description);
				textEventDescription.select(0, 0);
			}
			catch(Throwable expt) {
				Safe.exception(expt);
			};
		}
	}

	/**
	 * Get node description.
	 * 
	 * @param node
	 * @return
	 */
	private String getNodeDescription(DefaultMutableTreeNode node) {
		
		try {
			// Get description of the event part.
			Object loggedObject = node.getUserObject();
			String description = getLoggedObjectDescription(loggedObject);
	
			return description;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get the input object description.
	 * 
	 * @param theObject
	 * @return
	 */
	private String getObjectDescription(Object theObject) {
		
		try {
			String description = null;
	
			if (theObject instanceof EventSource) {
				EventSource eventSource = (EventSource) theObject;
				description = eventSource.getDescription();
			}
			else if (theObject instanceof Class) {
				Class theClass = (Class) theObject;
				description = theClass.getSimpleName();
			}
			else if (theObject instanceof Integer || theObject instanceof Long || theObject instanceof Boolean
					|| theObject instanceof Character || theObject instanceof String) {
				description = theObject.toString();
			}
			else if (theObject != null) {
				description = theObject.getClass().getSimpleName();
			}
			else {
				description = "null";
			}
			return description;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get array description.
	 * 
	 * @param additionalInfos
	 * @return
	 */
	private Object getArrayDescription(Object[] additionalInfos) {
		
		try {
			if (additionalInfos == null) {
				return "null";
			}
	
			String description = "";
			for (Object info : additionalInfos) {
	
				if (description.length() > 0) {
					description = ", " + description;
					description += String.format("[%s]", getDataDescription(info));
				}
			}
			return description;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "null";
	}

	/**
	 * Get reflection string.
	 * 
	 * @param reflection
	 * @return
	 */
	private String getReflectionDescription(StackTraceElement reflection) {
		
		try {
			if (reflection == null) {
				return "null";
			}
	
			String description = String.format("%s", reflection.toString());
			return description;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "null";
	}

	/**
	 * Get signal types description.
	 * 
	 * @param signal
	 * @return
	 */
	private String getSignalTypesDescription(Signal signal) {
		
		try {
			Obj<String> description = new Obj<String>("");
			signal.getTypes().stream().forEach(signalType -> {
	
				if (!description.ref.isEmpty()) {
					description.ref = description.ref + ", ";
				}
				description.ref += signalType.name();
			});
			return description.ref;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get data description.
	 * 
	 * @param dataObject
	 * @return
	 */
	private String getDataDescription(Object dataObject) {
		
		try {
			if (dataObject == null) {
				return "null";
			}
			String description = String.format("[%s] %s", dataObject.getClass().getSimpleName(), dataObject.toString());
			return description;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get logged object description.
	 * 
	 * @param loggedObject
	 * @return
	 */
	private String getLoggedObjectDescription(Object loggedObject) {
		
		try {
			String description = "";
	
			// Get signal description.
			if (loggedObject instanceof Signal) {
	
				Signal signal = (Signal) loggedObject;
				description = String.format(
						"<html>" + "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">"
								+ "<tr><td><b>signal:</b></td><td>&nbsp;%s</td></tr>"
								+ "<tr><td><b>types:</b></td><td>&nbsp;&nbsp;%s</td></tr>" + "</table>" + "</html>",
						signal.name(), getSignalTypesDescription(signal));
			} 
			else if (loggedObject instanceof Message) {
	
				Message message = (Message) loggedObject;
				description = String.format("<html>" + "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">"
						+ "<tr><td><b>hashcode:</b></td><td>&nbsp;&nbsp;[0x%08X]</td></tr>"
						+ "<tr><td><b>signal:</b></td><td>&nbsp;&nbsp;%s</td></tr>"
						+ "<tr><td><b>recieve&nbsp;time:</b></td><td>&nbsp;&nbsp;%s</td></tr>"
						+ "<tr><td><b>source:</b></td><td>&nbsp;&nbsp;%s</td></tr>"
						+ "<tr><td><b>target:</b></td><td>&nbsp;&nbsp;%s</td></tr>"
						+ "<tr><td><b>info:</b></td><td>&nbsp;&nbsp;%s</td></tr>"
						+ "<tr><td><b>+infos:</b></td><td>&nbsp;&nbsp;%s</td></tr>"
						+ "<tr><td><b>source&nbsp;code:</b></td><td>&nbsp;&nbsp;%s</td></tr>" + "</table>" + "</html>",
						message.hashCode(), message.signal.name(), Utility.formatTime(message.receiveTime),
						getObjectDescription(message.source), getObjectDescription(message.target),
						getDataDescription(message.relatedInfo), getArrayDescription(message.additionalInfos),
						getReflectionDescription(message.reflection));
			}
			else if (loggedObject instanceof LoggedEvent) {
	
				LoggedEvent loggedEvent = (LoggedEvent) loggedObject;
				description = String.format("<html>" + "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">"
						+ "<tr><td><b>hashcode:</b></td><td>&nbsp;&nbsp;[0x%08X]&nbsp;id = %s</td></tr>"
						+ "<tr><td><b>priority:</b></td><td>&nbsp;&nbsp;%d</td></tr>"
						+ "<tr><td><b>key:</b></td><td> [0x%08X]&nbsp;%s</td></tr>"
						+ "<tr><td><b>coalesce&nbsp;time</b>:</td><td>&nbsp;&nbsp;%d ms</td></tr>"
						+ "<tr><td><b>execution&nbsp;time</b>:</td><td>&nbsp;&nbsp;%s</td></tr>"
						+ "<tr><td><b>matching&nbsp;message</b>:</td><td>&nbsp;&nbsp;[0x%08X]</td></tr>"
						+ "<tr><td><b>source&nbsp;code</b>:</td><td>&nbsp;&nbsp;%s</td></tr>" + "</table>" + "</html>",
						loggedEvent.hashCode(), loggedEvent.eventHandle.identifier, loggedEvent.eventHandle.priority,
						loggedEvent.eventHandle.key.hashCode(), loggedEvent.eventHandle.key.getClass().getName(),
						loggedEvent.eventHandle.coalesceTimeSpanMs, Utility.formatTime(loggedEvent.executionTime),
						loggedEvent.matchingMessage.hashCode(),
						getReflectionDescription(loggedEvent.eventHandle.reflection));
			}
			else if (loggedObject != null) {
				description = loggedObject.toString();
			}
			return description;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Log formatted message.
	 * 
	 * @param loggedTextFormat
	 * @param parameters
	 */
	public static void log(String loggedTextFormat, Object... parameters) {
		try {
			
			String logText = String.format(loggedTextFormat, parameters);
	
			// Delegate the call.
			LoggingDialog.log(logText);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Log message.
	 * 
	 * @param logText
	 */
	public static void log(String logText) {
		try {
			
			// Check the "enable/disable" switch.
			if (!isLoggingEnabled()) {
				return;
			}
	
			// Add new message.
			LoggedMessage log = new LoggedMessage(logText);
			
			if (!LoggingDialog.logList) {
				logTexts.clear();
			}
	
			logTexts.add(log);
	
			// Compute message limit.
			int extraMessagesCount = logTexts.size() - logLimit;
	
			// Remove extra messages from the list beginning.
			while (extraMessagesCount-- > 0) {
				logTexts.removeFirst();
			}
	
			// Compile logged messages.
			compileLog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Write divider of logged messages at current row.
	 */
	public static void logTextDivider() {
		try {
			
			log(divider);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Add new divider after currently displayed messages.
	 */
	private void logMessagesDivider() {

		synchronized (messageQueueSnapshots) {
			try {
			
				// Add new divider. The key name for divider should be unique, so append
				// current number of dividers after the divider keyword. Than increase the number.
				String divederText = "divider" + String.valueOf(messageQueueSnapshotsDividerNumber++);
				messageQueueSnapshots.put(divederText, null);
				
				// Update the tree view.
				updateMessageQueueTree();
			}
			catch(Throwable expt) {
				Safe.exception(expt);
			};
		}
	}
	
	/**
	 * Add new divider after currently displayed events.
	 */
	private void logEventsDivider() {
		
		synchronized (events) {
			try {
				
				// Add new divider message. The divider should be unique, so append
				// current number of dividers after the divider keyword. Than increase the number.
				Message dividerMessage = new Message();
				dividerMessage.relatedInfo = "divider" + String.valueOf(eventsDividerNumber++);
				events.forEach((signal, messages) -> messages.put(dividerMessage, null));
				
				// Update the tree view.
				updateEventTree();
			}
			catch(Throwable expt) {
				Safe.exception(expt);
			};
		}
	}
	
	/**
	 * Involve user action in the log process based on dialog state.
	 */
	public static void involveUserAction() {
		try {
			
			// Initialize auxiliary flag.
			boolean lockThisThread = false;
	
			synchronized (dialog.stateSynchronizaton) {
	
				// If the log should run, do not stop it.
				if (dialog.state == RUNNING) {
					return;
				}
	
				// If the log is braked, wait for user action.
				if (dialog.state == BREAKED) {
					lockThisThread = true;
				}
			}
	
			// Lock the thread and wait for user action.
			if (lockThisThread) {
				Lock.waitFor(dialog.logLock);
	
				// TODO: debug
				j.log("THE LOG LOCK HAS BEEN RELEASED BY USER ACTION");
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Log incoming message.
	 * 
	 * @param incommingMessage
	 */
	public static void log(Message incomingMessage) {
		try {
			
			// Check switch.
			if (!isLoggingEnabled()) {
				return;
			}
	
			synchronized (events) {
				// Delegate the call to addMessage(...).
				addMessage(incomingMessage);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Log event.
	 * 
	 * @param message
	 * @param eventHandle
	 * @param executionTime
	 */
	public static void log(Message message, EventHandle eventHandle, long executionTime) {
		try {
			
			// Check switch.
			if (!isLoggingEnabled()) {
				return;
			}
	
			synchronized (events) {
	
				// Get message signal.
				Signal signal = message.signal;
	
				// Get message map.
				LinkedHashMap<Message, LinkedHashMap<Long, LinkedList<LoggedEvent>>> messageMap = events.get(signal);
	
				// Check if the incoming message is missing.
				boolean missingMessage = !messageMap.containsKey(message);
	
				// Add missing incoming message
				if (missingMessage) {
					messageMap = addMessage(message);
				}
				// Limit the number of messages.
				int messageCount = messageMap.size();
				if (messageCount > logLimit) {
	
					// Remove leading entries.
					int messageRemovalCount = messageCount - logLimit;
					HashSet<Message> messagesToRemove = new HashSet<Message>();
	
					for (Message messageToRemove : messageMap.keySet()) {
						if (messageRemovalCount-- <= 0) {
							break;
						}
						messagesToRemove.add(messageToRemove);
					}
					for (Message messageToRemove : messagesToRemove) {
						messageMap.remove(messageToRemove);
					}
				}
	
				// Try to get execution time map.
				LinkedHashMap<Long, LinkedList<LoggedEvent>> timeMap = messageMap.get(message);
				if (timeMap == null) {
					timeMap = new LinkedHashMap<Long, LinkedList<LoggedEvent>>();
					messageMap.put(message, timeMap);
				}
	
				// Try to get event list.
				LinkedList<LoggedEvent> loggedEvents = timeMap.get(executionTime);
				if (loggedEvents == null) {
					loggedEvents = new LinkedList<LoggedEvent>();
					timeMap.put(executionTime, loggedEvents);
				} else {
					// Limit the number of logged events.
					int eventCount = loggedEvents.size();
					if (eventCount > eventLimit) {
	
						// Remove leading items.
						int eventRemovalCount = eventCount - eventLimit;
	
						while (--eventRemovalCount > 0) {
							loggedEvents.removeFirst();
						}
					}
				}
	
				// Append new event.
				LoggedEvent event = new LoggedEvent();
				event.eventHandle = eventHandle;
				event.executionTime = executionTime;
				event.matchingMessage = message;
	
				loggedEvents.add(event);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Add incoming message and return logged event object.
	 * 
	 * @param incomingMessage
	 */
	public static LinkedHashMap<Message, LinkedHashMap<Long, LinkedList<LoggedEvent>>> addMessage(
			Message incomingMessage) {
		
		try {
			// Check switch.
			if (!isLoggingEnabled()) {
				return null;
			}
	
			// Get message signal.
			Signal signal = incomingMessage.signal;
	
			// Get messages mapped to this signal and append the incoming message.
			LinkedHashMap<Message, LinkedHashMap<Long, LinkedList<LoggedEvent>>> messageMap = events.get(signal);
			if (messageMap == null) {
				messageMap = new LinkedHashMap<Message, LinkedHashMap<Long, LinkedList<LoggedEvent>>>();
				events.put(signal, messageMap);
			}
	
			messageMap.put(incomingMessage, null);
	
			return messageMap;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Compile messages.
	 */
	private static void compileLog() {
		try {
			
			if (dialog != null) {
	
				String resultingText = "";
	
				for (LoggedMessage message : logTexts) {
					resultingText += message.getText() + '\n';
				}
	
				dialog.textLog.setText(resultingText);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Restore event selection.
	 * 
	 * @param selectedNode
	 */
	private void restoreTreeNodeSelection(JTree tree, DefaultMutableTreeNode rootNode, TreePath selectedPath,
			BiFunction<Object, Object, Boolean> userObjectsEqualLambda) {
		try {
			
			// Check the node.
			if (selectedPath == null) {
				return;
			}
	
			// Get last path node.
			Object lastComponent = selectedPath.getLastPathComponent();
			if (!(lastComponent instanceof DefaultMutableTreeNode)) {
				return;
			}
	
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) lastComponent;
	
			// Get event object.
			Object selectedQueuebject = selectedNode.getUserObject();
	
			// Check the event object.
			if (selectedQueuebject == null) {
				return;
			}
	
			// Select found node.
			Enumeration<TreeNode> enumeration = rootNode.depthFirstEnumeration();
			while (enumeration.hasMoreElements()) {
	
				// Get the node event object.
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();
				Object queueObject = node.getUserObject();
	
				// If the event object matches, select that node.
				if (userObjectsEqualLambda.apply(selectedQueuebject, queueObject)) {
					
					TreeNode[] treeNodes = node.getPath();
					if (treeNodes.length > 0) {
	
						// Set selection path.
						TreePath treePath = new TreePath(treeNodes);
						tree.setSelectionPath(treePath);
	
						// Ensure that the selection is visible.
						tree.makeVisible(treePath);
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
	 * Check if event objects area equal.
	 * 
	 * @param eventObject1
	 * @param eventObject2
	 * @return
	 */
	private static boolean eventObjectsEqual(Object eventObject1, Object eventObject2) {
		
		try {
			// Check null objects.
			if (eventObject1 == null) {
				return eventObject2 == null;
			} else if (eventObject2 == null) {
				return false;
			}
	
			// Check objects types.
			if (eventObject1.getClass() != eventObject2.getClass()) {
				return false;
			}
	
			// Check signals.
			if (eventObject1 instanceof Signal) {
				Signal signal1 = (Signal) eventObject1;
				Signal signal2 = (Signal) eventObject2;
				return signal1.name().equals(signal2.name());
			}
			// Check messages.
			else if (eventObject1 instanceof Message) {
				Message message1 = (Message) eventObject1;
				Message message2 = (Message) eventObject2;
				return message1 == message2;
			}
			// Check events.
			else if (eventObject1 instanceof LoggedEvent) {
				LoggedEvent event1 = (LoggedEvent) eventObject1;
				LoggedEvent event2 = (LoggedEvent) eventObject2;
				return event1 == event2;
			}
	
			// Perform standard check.
			return eventObject1.equals(eventObject2);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Check if message queue objects area equal.
	 * 
	 * @param queueObject1
	 * @param queueObject2
	 * @return
	 */
	private boolean queueObjectsEqual(Object queueObject1, Object queueObject2) {
		
		try {
			// Check null objects.
			if (queueObject1 == null) {
				return queueObject2 == null;
			} else if (queueObject2 == null) {
				return false;
			}
	
			// Check objects types.
			if (queueObject1.getClass() != queueObject2.getClass()) {
				return false;
			}
	
			// Check time stamps.
			if (queueObject1 instanceof String) {
				String timeStamp1 = (String) queueObject1;
				String timeStamp2 = (String) queueObject2;
				return timeStamp1.equals(timeStamp2);
			}
			// Check messages.
			else if (queueObject1 instanceof Message) {
				Message message1 = (Message) queueObject1;
				Message message2 = (Message) queueObject2;
				return message1 == message2;
			}
	
			// Perform standard check.
			return queueObject1.equals(queueObject2);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Add break point object.
	 * 
	 * @param breakPointObject
	 */
	private void addBreakPoint(Object breakPointObject) {
		try {
			
			// Add the input object into breakpoints set and update GUI list that displays
			// the break points.
			breakPointMatchObjects.add(breakPointObject);
			updateBreakPointsList(breakPointMatchObjects);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Update the list of break points.
	 * 
	 * @param breakPointObjects
	 */
	private void updateBreakPointsList(HashSet<Object> breakPointObjects) {
		try {
			
			// Clear the model.
			listBreakPointsModel.clear();
	
			// Add break points.
			listBreakPointsModel.addAll(breakPointObjects);
	
			// Repaint GUI.
			listBreakPoints.updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Add a message queue snapshot into the list.
	 * 
	 * @param messageQueueSnapshot
	 * @param timeMoment
	 */
	public static void addMessageQueueSnapshot(LinkedList<Message> messageQueueSnapshot, Long timeMoment) {
		try {
			
			// Check switch.
			if (!isLoggingEnabled()) {
				return;
			}
	
			synchronized (messageQueueSnapshots) {
	
				// Check input.
				if (messageQueueSnapshot == null || timeMoment == null) {
					return;
				}
	
				// Get new formatted time moment.
				String timeMomentText = Utility.formatTime(timeMoment);
	
				// Insert new items.
				LinkedList<Message> oldSnapshot = messageQueueSnapshots.get(timeMomentText);
				if (oldSnapshot != null) {
					oldSnapshot.addAll(messageQueueSnapshot);
				} else {
					messageQueueSnapshots.put(timeMomentText, messageQueueSnapshot);
				}
	
				// Remove extra snapshots.
				int snapshotCount = messageQueueSnapshots.size();
				Obj<Integer> snapshotsToRemove = new Obj<Integer>(snapshotCount - queueLimit);
	
				if (snapshotsToRemove.ref > 0) {
					HashSet<String> timeStampsToRemove = new HashSet<String>();
	
					messageQueueSnapshots.forEach((timeStamp, snapshot) -> {
						try {
							
							if (snapshotsToRemove.ref > 0) {
								timeStampsToRemove.add(timeStamp);
							}
							snapshotsToRemove.ref--;
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					});
	
					timeStampsToRemove.forEach(timeStamp -> messageQueueSnapshots.remove(timeStamp));
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Update message queue tree.
	 */
	protected void updateMessageQueueTree() {
		try {
			
			synchronized (messageQueueSnapshots) {
	
				// Get current selection.
				TreePath selectedPath = treeMessageQueue.getSelectionPath();
	
				// Clear the tree nodes except the root node.
				messageQueueTreeRootNode.removeAllChildren();
	
				// Add snap shots.
				messageQueueSnapshots.entrySet().stream().forEach(entry -> {
	
					// Get key text.
					String keyText = entry.getKey();
	
					// On messages divider.
					if (keyText.startsWith("divider")) {
	
						DefaultMutableTreeNode dividerNode = new DefaultMutableTreeNode(divider);
						messageQueueTreeRootNode.add(dividerNode);
					}
					// On message queue snapshot.
					else {
						// Get time moment.
						String timeMomentText = keyText;
	
						// Add new time node.
						DefaultMutableTreeNode timeNode = new DefaultMutableTreeNode(timeMomentText);
						messageQueueTreeRootNode.add(timeNode);
	
						// Get message queue snapshots.
						LinkedList<Message> messageQueueSnapshot = entry.getValue();
						if (messageQueueSnapshot != null) {
							messageQueueSnapshot.forEach(message -> {
								try {
									
									// Add new message node.
									DefaultMutableTreeNode messageNode = new DefaultMutableTreeNode(message);
									timeNode.add(messageNode);
								}
								catch(Throwable expt) {
									Safe.exception(expt);
								};
							});
						}
					}
				});
	
				// Update tree GUI.
				treeMessageQueue.updateUI();
				// Expand all nodes.
				Utility.expandAll(treeMessageQueue, true);
				// Restore selection.
				restoreTreeNodeSelection(treeMessageQueue, messageQueueTreeRootNode, selectedPath,
						(object1, object2) -> {
							try {
								return queueObjectsEqual(object1, object2);
							}
							catch (Throwable e) {
								Safe.exception(e);
							}
							return false;
						});
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Reload event tree.
	 * 
	 * @param events
	 */
	private void updateEventTree() {
		try {
			
			synchronized (treeEvents) {
	
				// Save current selection.
				TreePath selectedPath = treeEvents.getSelectionPath();
	
				// Clear old tree of logged events.
				treeNodeEventRoot.removeAllChildren();
	
				// Add events.
				events.forEach((signal, messageMap) -> {
					try {
						
						// Get signal omitted flag.
						boolean omitSignal = checkOmitOrChooseSignals.isSelected();
		
						// Check if the signal is omitted/chosen.
						synchronized (omittedOrChosenSignals) {
		
							// Check if the signal is omitted.
							if (omitSignal) {
								if (omittedOrChosenSignals.contains(signal)) {
									return;
								}
							}
							// Check if the signal is chosen.
							else {
								if (!omittedOrChosenSignals.contains(signal)) {
									return;
								}
							}
						}
		
						// Create signal.
						DefaultMutableTreeNode signalNode = new DefaultMutableTreeNode(signal);
						treeNodeEventRoot.add(signalNode);
		
						// Create message nodes.
						if (messageMap != null) {
							messageMap.forEach((message, executionTimeMap) -> {
								try {
									
									// Check if the message is just a divider.
									boolean isDivider = false;
									if (message.signal == null && message.relatedInfo instanceof String) {
										
										// Check if the message is a divider.
										String relatedInfo = (String) message.relatedInfo;
										isDivider = relatedInfo.startsWith("divider");
									}
			
									// Add message node.
									DefaultMutableTreeNode messageNode = new DefaultMutableTreeNode(isDivider ? divider : message);
									signalNode.add(messageNode);
			
									// Create execution time nodes.
									if (executionTimeMap != null) {
										executionTimeMap.forEach((executionTime, eventList) -> {
											try {
												
												// Get execution time string representation.
												String executionTimeText = Utility.formatTime(executionTime);
				
												// Add time node.
												DefaultMutableTreeNode timeNode = new DefaultMutableTreeNode(executionTimeText);
												messageNode.add(timeNode);
				
												// Create event nodes.
												if (eventList != null) {
													eventList.forEach(event -> {
														try {
															
															// Add event node.
															DefaultMutableTreeNode eventNode = new DefaultMutableTreeNode(event);
															timeNode.add(eventNode);
														}
														catch(Throwable expt) {
															Safe.exception(expt);
														};
													});
												}
											}
											catch(Throwable expt) {
												Safe.exception(expt);
											};
										});
									}
									else if (!isDivider) {
										// Informative node.
										DefaultMutableTreeNode auxNode = new DefaultMutableTreeNode("DROPPED OR COALESCED");
										messageNode.add(auxNode);
									}
								}
								catch(Throwable expt) {
									Safe.exception(expt);
								};
							});
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				});
	
				// Update tree GUI.
				treeEvents.updateUI();
				// Expand all nodes.
				Utility.expandAll(treeEvents, true);
				// Restore selection.
				restoreTreeNodeSelection(treeEvents, treeNodeEventRoot, selectedPath,
						(object1, object2) -> {
								try {
									return eventObjectsEqual(object1, object2);
								}
								catch (Throwable e) {
									Safe.exception(e);
								}
								return false;
							});
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Clear logged message queues.
	 * 
	 * @return
	 */
	private void onClearQueues() {
		try {
			
			// Ask user.
			if (!Utility.ask(this, "org.multipage.generator.messageShallClearLoggedQueues")) {
				return;
			}
	
			synchronized (messageQueueSnapshots) {
				
				// Clear message queue snapshots.
				messageQueueSnapshots.clear();
				
				// Reset the divider counter.
				messageQueueSnapshotsDividerNumber = 0;
			}
	
			synchronized (treeMessageQueue) {
				// Update the queues tree.
				updateMessageQueueTree();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Clear message queue snapshots viewer.
	 */
	private void onClearEvents() {
		try {
			
			// Ask user.
			if (!Utility.ask(this, "org.multipage.generator.messageShallClearLoggedEvents")) {
				return;
			}
	
			synchronized (events) {
				// Clear events.
				events.clear();
			}
	
			synchronized (treeEvents) {
				// Update the events tree.
				updateEventTree();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};		
	}

	/**
	 * On omitted/chosen signal click.
	 * 
	 * @param event
	 */
	protected void onOmittedOrChosenSignalClick(MouseEvent event) {
		try {
			
			// Check double click.
			if (event.getClickCount() != 2) {
				return;
			}
	
			synchronized (omittedOrChosenSignals) {
	
				// Add/remove omitted signal.
				Signal signal = listOmittedOrChosenSignals.getSelectedValue();
	
				if (!omittedOrChosenSignals.contains(signal)) {
					omittedOrChosenSignals.add(signal);
				}
				else {
					omittedOrChosenSignals.remove(signal);
				}
	
				// Redraw list of signals.
				listOmittedOrChosenSignals.updateUI();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On omitted/chosen check box click.
	 */
	protected void onOmitChooseSignals() {
		try {
			
			// Redraw the window.
			repaint();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On clear omitted/chosen signals list.
	 */
	protected void onClearOmittedChosen() {
		try {
			
			// Ask user.
			if (!Utility.ask(this, "org.multipage.generator.messageLogResetOmittedChosenList")) {
				return;
			}
	
			// Clear the list and update GUI.
			omittedOrChosenSignals.clear();
			listOmittedOrChosenSignals.updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Add break point.
	 */
	protected void onAddBreakPoint() {
		try {
			
			// Get selected event object.
			TreePath selectedPath = treeEvents.getSelectionPath();
			if (selectedPath == null) {
				return;
			}
	
			// Get tree node break point object.
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
			Object breakPointObject = treeNode.getUserObject();
	
			if (breakPointObject == null) {
				breakPointObject = "";
			}
	
			Class<?> breakPointClass = breakPointObject.getClass();
			String breakPointClassName = breakPointClass.getSimpleName();
	
			// Check available break point type.
			if (!availableBreakPointClasses.contains(breakPointClass)) {
				Utility.show(this, "org.multipage.generator.textCannotAddLogBreakPointClass", breakPointClassName);
				return;
			}
	
			// Ask user.
			if (!Utility.askParam(this, "org.multipage.generator.textShallAddLogBreakPoint", breakPointClassName)) {
				return;
			}
	
			// Add the break point to the list.
			addBreakPoint(breakPointObject);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Clear break points.
	 */
	private void onClearBreakPoints() {
		try {
			
			// Ask user.
			if (!Utility.ask(this, "org.multipage.generator.textShouldClearLogBreakPoints")) {
				return;
			}
	
			// Clear break points and update the GUI list.
			breakPointMatchObjects.clear();
			updateBreakPointsList(breakPointMatchObjects);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set update settings for log views.
	 */
	private void onLogSettings() {
		try {
			
			// Open settings.
			Obj<Boolean> isGuiEnabled = new Obj<Boolean>(true);
	
			LoggingSettingsDialog.showDialog(this, messageQueueUpdateIntervalMs, eventTreeUpdateIntervalMs, queueLimit,
					logLimit, eventLimit,
	
					enableGui -> {
							try {
								
								isGuiEnabled.ref = enableGui;
							}
							catch(Throwable expt) {
								Safe.exception(expt);
							};
						},
	
					queuesUpdateIntervalMs -> {
						try {
							// Check interval value.
							if (queuesUpdateIntervalMs == null) {
								if (isGuiEnabled.ref) {
									Utility.show(this, "org.multipage.generator.messageQueuesUpdateIntervalNotNumber");
								}
								return false;
							}
							if (queuesUpdateIntervalMs < 100 || queuesUpdateIntervalMs > 10000) {
								if (isGuiEnabled.ref) {
									Utility.show(this, "org.multipage.generator.messageQueuesUpdateIntervalOutOfRange");
								}
								return false;
							}
		
							// Set interval.
							setQueuesUpdateInterval(queuesUpdateIntervalMs);
							return true;
						}
						catch (Throwable e) {
							Safe.exception(e);
						}
						return false;
					},
	
					eventsUpdateIntervalMs -> {
						try {
							// Check interval value.
							if (eventsUpdateIntervalMs == null) {
								if (isGuiEnabled.ref) {
									Utility.show(this, "org.multipage.generator.messageEventsUpdateIntervalNotNumber");
								}
								return false;
							}
							if (eventsUpdateIntervalMs < 100 || eventsUpdateIntervalMs > 10000) {
								if (isGuiEnabled.ref) {
									Utility.show(this, "org.multipage.generator.messageEventsUpdateIntervalOutOfRange");
								}
								return false;
							}
		
							// Set interval.
							setEventUpdateInterval(eventsUpdateIntervalMs);
							return true;
						}
						catch (Throwable e) {
							Safe.exception(e);
						}
						return false;
					},
	
					newQueueLimit -> {
						try {
							// Check queue limit.
							if (newQueueLimit == null) {
								if (isGuiEnabled.ref) {
									Utility.show(this, "org.multipage.generator.messageQueueLimitNotNumber");
								}
								return false;
							}
							if (newQueueLimit < 0 || newQueueLimit > 100) {
								if (isGuiEnabled.ref) {
									Utility.show(this, "org.multipage.generator.messageQueueLimitOutOfRange");
								}
								return false;
							}
		
							// Set limit.
							setQueueLimit(newQueueLimit);
							return true;
						}
						catch (Throwable e) {
							Safe.exception(e);
						}
						return false;
					},
	
					newMessageLimit -> {
						try {
							// Check message limit.
							if (newMessageLimit == null) {
								if (isGuiEnabled.ref) {
									Utility.show(this, "org.multipage.generator.messageMessagesLimitNotNumber");
								}
								return false;
							}
							if (newMessageLimit < 0 || newMessageLimit > 100) {
								if (isGuiEnabled.ref) {
									Utility.show(this, "org.multipage.generator.messageMessagesLimitOutOfRange");
								}
								return false;
							}
		
							// Set limit.
							setMessageLimit(newMessageLimit);
							return true;
						}
						catch (Throwable e) {
							Safe.exception(e);
						}
						return false;
					},
	
					newEventLimit -> {
						try {
							// Check event limit.
							if (newEventLimit == null) {
								if (isGuiEnabled.ref) {
									Utility.show(this, "org.multipage.generator.messageEventsLimitNotNumber");
								}
								return false;
							}
							if (newEventLimit < 0 || newEventLimit > 100) {
								if (isGuiEnabled.ref) {
									Utility.show(this, "org.multipage.generator.messageEventsLimitOutOfRange");
								}
								return false;
							}
		
							// Set limit.
							setEventLimit(newEventLimit);
							return true;
						}
						catch (Throwable e) {
							Safe.exception(e);
						}
						return false;
					});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On omit/choose signal menu item clicked.
	 */
	protected void onMenuOmitChooseSignal() {
		try {
			
			// Get selected signal.
			TreePath selectedPath = treeEvents.getSelectionPath();
			if (selectedPath == null) {
				return;
			}
	
			int pathNodesCount = selectedPath.getPathCount();
	
			// Reset flag.
			boolean success = false;
	
			// Check path.
			if (pathNodesCount >= 2) {
	
				// Get signal object.
				DefaultMutableTreeNode signalNode = (DefaultMutableTreeNode) selectedPath.getPathComponent(1);
				Object loggedObject = signalNode.getUserObject();
	
				// Add signal to the list.
				if (loggedObject instanceof Signal) {
					Signal signal = (Signal) loggedObject;
	
					omittedOrChosenSignals.add(signal);
					listOmittedOrChosenSignals.updateUI();
	
					// Set the flag.
					success = true;
				}
			}
	
			// If successful, update the tree. If not successful, inform the user.
			if (success) {
				updateEventTree();
			}
			else {
				Utility.show(this, "org.multipage.generator.messageLogCannotOmitOrChooseNode");
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On print reflection.
	 */
	protected void onPrintReflection(JTree tree) {
		try {
			
			// Get selected input message or logged event.
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
			Object userObject = node.getUserObject();
	
			if (userObject instanceof Message) {
				Message message = (Message) userObject;
				System.out.println(message.reflection.toString());
			}
			else if (userObject instanceof LoggedEvent) {
				LoggedEvent event = (LoggedEvent) userObject;
				System.out.println(event.eventHandle.reflection.toString());
			}
			else {
				// Bad selection.
				Utility.show(this, "org.multipage.generator.messageLogNodeHasNoReflection");
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On selected message queue object.
	 */
	protected void onMessageQueueObjectSelected() {
		try {
			
			// Initialization.
			String nodeDescription = "";
	
			// Get selected message queue object and display its description.
			TreePath path = treeMessageQueue.getSelectionPath();
			if (path != null) {
	
				Object component = path.getLastPathComponent();
				if (component instanceof DefaultMutableTreeNode) {
	
					// Get node description.
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) component;
					nodeDescription = getNodeDescription(node);
				}
			}
	
			// Display the description text.
			textQueueMessage.setText(nodeDescription);
			textQueueMessage.select(0, 0);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Go to message event.
	 */
	protected void onGoToMessageEvent() {
		try {
			
			// Get selected message.
			TreePath path = treeMessageQueue.getSelectionPath();
			if (path == null) {
				Utility.show(this, "org.multipage.generator.messageSelectQueueMessage");
				return;
			}
	
			Object pathComponent = path.getLastPathComponent();
			if (!(pathComponent instanceof DefaultMutableTreeNode)) {
				Utility.show(this, "org.multipage.generator.messageSelectQueueMessage");
				return;
			}
	
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) pathComponent;
			Object nodeObject = node.getUserObject();
			if (!(nodeObject instanceof Message)) {
				Utility.show(this, "org.multipage.generator.messageSelectQueueMessage");
				return;
			}
	
			Message message = (Message) nodeObject;
	
			// Switch tab.
			tabbedPane.setSelectedComponent(panelEvents);
	
			// Select event.
			Utility.traverseElements(treeEvents, userObject -> treeNode -> parentNode -> {
				try {
					if (message.equals(userObject)) {
		
						TreePath selectionPath = new TreePath(treeNode.getPath());
						treeEvents.setSelectionPath(selectionPath);
						treeEvents.makeVisible(selectionPath);
						return true;
					}
				}
				catch (Throwable e) {
					Safe.exception(e);
				}
				return false;
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Go to message queue.
	 */
	protected void onGoToMessageQueue() {
		try {
			
			// Get selected message.
			TreePath path = treeEvents.getSelectionPath();
			if (path == null) {
				Utility.show(this, "org.multipage.generator.messageSelectEventsMessage");
				return;
			}
	
			Object pathComponent = path.getLastPathComponent();
			if (!(pathComponent instanceof DefaultMutableTreeNode)) {
				Utility.show(this, "org.multipage.generator.messageSelectEventsMessage");
				return;
			}
	
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) pathComponent;
			Object nodeObject = node.getUserObject();
			if (!(nodeObject instanceof Message)) {
				Utility.show(this, "org.multipage.generator.messageSelectEventsMessage");
				return;
			}
	
			Message message = (Message) nodeObject;
	
			// Switch tab.
			tabbedPane.setSelectedComponent(panelMessageQueue);
	
			// Select event.
			Utility.traverseElements(treeMessageQueue, userObject -> treeNode -> parentNode -> {
				try {
					if (message.equals(userObject)) {
		
						TreePath selectionPath = new TreePath(treeNode.getPath());
						treeMessageQueue.setSelectionPath(selectionPath);
						treeMessageQueue.makeVisible(selectionPath);
						return true;
					}
				}
				catch (Throwable e) {
					Safe.exception(e);
				}
				return false;
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * List or single item switch.
	 */
	private void onListOrSingleItem() {
		try {
			
			// Set flag.
			boolean logList = buttonListOrSingleItem.isSelected();
			LoggingDialog.logList = logList;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On [Step] button pressed.
	 */
	private void onStepSingleMessage() {
		try {
			
			// Check state.
			if (state == RUNNING) {
				applyDialogState();
				return;
			}
	
			// Transmit the "step log" signal.
			ApplicationEvents.transmit(this, Signal.stepLog);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On [Run] button pressed.
	 */
	private void onRunMessages() {
		try {
			
			// Check state.
			if (state == RUNNING) {
				applyDialogState();
				return;
			}
	
			synchronized (dialog.stateSynchronizaton) {
				state = RUNNING;
			}
	
			// Apply dialog state to GUI.
			applyDialogState();
	
			// Transmit the "run logging" signal.
			ApplicationEvents.transmit(this, Signal.runLogging);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On [Break] button pressed.
	 */
	private void onBreakMessages() {
		try {
			
			// Check state.
			if (state == BREAKED) {
				applyDialogState();
				return;
			}
	
			// Transmit the "break logging" signal.
			ApplicationEvents.transmit(this, Signal.breakLogging);
	
			synchronized (dialog.stateSynchronizaton) {
				state = BREAKED;
			}
	
			// Apply dialog state to GUI.
			applyDialogState();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On [Clear] button pressed.
	 */
	private void onClearMessages() {
		try {
			
			// Clear the log view.
			clearMessages();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On font size changed.
	 */
	protected void onFontSize() {
		try {
			
			// Get font size.
			int selectedIndex = comboFontSize.getSelectedIndex();
			if (selectedIndex == -1) {
				return;
			}
	
			Integer fontSize = comboFontSize.getItemAt(selectedIndex);
			if (fontSize == null) {
				return;
			}
	
			// Change text area font size.
			Font newFont = textLog.getFont().deriveFont((float) fontSize);
			textLog.setFont(newFont);
			textLog.updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On close dialog.
	 */
	protected void onClose() {
		try {
			
			// Switch dialog into the running state and release the log lock.
			state = RUNNING;
			Lock.notify(logLock);
	
			// Save dialog state.
			saveDialog();
	
			// Reset the flag.
			openedWhenInitialized = false;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set queues display interval.
	 * 
	 * @param intervalMs
	 */
	private void setQueuesUpdateInterval(Integer intervalMs) {
		try {
			
			// Set event update interval.
			messageQueueUpdateIntervalMs = intervalMs;
			updateMessageQueueTimer.setDelay(messageQueueUpdateIntervalMs);
	
			// Update queues tree.
			updateMessageQueueTree();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set event display interval.
	 * 
	 * @param intervalMs
	 */
	protected void setEventUpdateInterval(int intervalMs) {
		try {
			
			// Set event update interval.
			eventTreeUpdateIntervalMs = intervalMs;
			updateEventTreeTimer.setDelay(eventTreeUpdateIntervalMs);
	
			// Update event tree.
			updateEventTree();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set queue limit.
	 * 
	 * @param newQueueLimit
	 */
	private void setQueueLimit(Integer newQueueLimit) {
		try {
			
			// Set message limit.
			queueLimit = newQueueLimit;
	
			// Update event tree.
			updateMessageQueueTree();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set messages limit.
	 * 
	 * @param newMessageLimit
	 */
	private void setMessageLimit(Integer newMessageLimit) {
		try {
			
			// Set message limit.
			logLimit = newMessageLimit;
	
			// Update event tree.
			updateEventTree();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set events limit.
	 * 
	 * @param newEventLimit
	 */
	private void setEventLimit(Integer newEventLimit) {
		try {
			
			// Set event limit.
			eventLimit = newEventLimit;
	
			// Update event tree.
			updateEventTree();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Breakpoint managed by this log window.
	 * 
	 * @param breakPointObject
	 */
	public static void breakPoint(Object breakPointObject) {
		try {
			
			// Check switch.
			if (!isLoggingEnabled()) {
				return;
			}
	
			boolean isBreakPoint = false;
	
			synchronized (breakPointMatchObjects) {
	
				// Check the break point object.
				for (Object breakPointMatch : breakPointMatchObjects) {
	
					if (!breakPointObject.equals(breakPointMatch)) {
						return;
					}
	
					isBreakPoint = true;
				}
			}
			if (!isBreakPoint) {
				return;
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
