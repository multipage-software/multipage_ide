/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.SplashScreen;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.TextUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.io.IOUtils;
import org.multipage.gui.SearchTextDialog.Parameters;
import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;
import org.w3c.dom.Node;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

/**
 * Miscelaneus utility functions.
 * @author vakol
 *
 */
public class Utility {
	
	/**
	 * Standard headers.
	 */
	public static final String xmlHeaderTemplate = "<?xml version=\"1.0\" encoding=\"%s\"?>";
	
	/**
	 * Colors
	 */
	private static final Color colorLight = new Color(250, 250, 250);
	private static final Color colorDark = new Color(230, 230, 230);
	
	/**
	 * CSS units.
	 */
	public static final String [] cssUnits = {"px", "em", "%", "cm", "mm", "in", "ex", "pt", "pc", ""};
	public static final String[] cssAngleUnits = {"deg", "grad", "rad", "turn"};

	/**
	 * Current path name.
	 */
	public static String currentPathName = "";
	
	/**
	 * Application main window.
	 */
	private static Window applicationMainWindow = null;
	
	/**
	 * Separators.
	 */
	public static final char pathSeparatorCharacter = '/';
	public static final String pathSeparator = "/";
	
	/**
	 * Regular expressions.
	 */
	public static final String floatGroupRegex = "\\s*([0-9\\-\\+\\.eE]+)\\s*";
	public static final String lengthUnitsRegex = "(px|em|%|cm|mm|in|ex|pt|pc|\\s*)";
	public static final String angleUnitsRegex = "(deg|grad|rad|turn|\\s*)";
	public static final Pattern x509CommonNameRegex = Pattern.compile("^\\s*CN\\s*=\\s*(?<commonName>[^,]+)");
	
	/**
	 * Regular expression pattern for format specifier in String.fomat(...)
	 */
	public static final String formatSpecifier = "^%(\\d+\\$)?([-#+ 0,(\\<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])$";
	
	/**
	 * Empty line REGEX pattern
	 */
	public static final Pattern emptyLineRegexPattern = Pattern.compile("(?m)^[ \t]*\r?\n", Pattern.MULTILINE);
	
	/**
	 * Character distances. Characters on keyboard: position on keyboard and character topology.
	 */
	private final static char [][][] keyboard = 
		{{{ 'Q' }, { 'W' }, { 'E' }, { 'R' }, { 'T' }, { 'Y' }, { 'U' }, { 'I' }, { 'O' }, { 'P' }},
		 {{ 'A' }, { 'S' }, { 'D' }, { 'F' }, { 'G' }, { 'H' }, { 'J' }, { 'K' }, { 'L' }, { '\0'}},
		 {{ '\0'}, { 'Z' }, { 'X' }, { 'C' }, { 'V' }, { 'B' }, { 'N' }, { 'M' }, { '\0'}, { '\0'}}};
	
	private static int keyboardHeight = -1;
	private static int keyboardWidth = -1;
	
	private static Hashtable<Character, List<Character>> mapKeyNeighbours = null;
	
	/**
	 * Initialization.
	 */
	static {
		try {
			
			// Initialize computation of text distances.
			initTextDistanceComputation();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Load serialized data.
	 * @param inputStream
	 * @throws IOException 
	 */
	public static void serializeData(StateInputStream inputStream)
		throws IOException, ClassNotFoundException {

		// Load path name.
		currentPathName = inputStream.readUTF();
	}

	/**
	 * Save serialized data.
	 * @param outputStream
	 * @throws IOException 
	 */
	public static void serializeData(StateOutputStream outputStream)
		throws IOException {
		
		// Save path name.
		outputStream.writeUTF(currentPathName);
	}

	/**
	 * Set default data.
	 */
	public static void setDefaultData() {

		currentPathName = "";
	}
	
	/**
	 * Get current path name
	 * @return
	 */
	public static String getCurrentPathName() {
		
		return currentPathName;
	}
	
	/**
	 * Get current time stamp.
	 * @return
	 */
	public static Timestamp getCurrentTimestamp() {
		
		try {
			long currentTimeMs = System.currentTimeMillis();
			Timestamp initialTimestamp = new Timestamp(currentTimeMs);
			return initialTimestamp;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Set current path name
	 * @param currentPathName
	 */
	public static void setCurrentPathName(String currentPathName) {
		
		Utility.currentPathName = currentPathName;
	}
	
	/**
	 * Set application main window.
	 * @param window
	 */
	public static void setApplicationMainWindow(Window window) {
		
		Utility.applicationMainWindow = window;
	}
	
	/**
	 * Set application main window.
	 * @param component
	 */
	public static void setApplicationMainWindow(Component component) {
		try {
			
			Utility.applicationMainWindow = findWindowRecursively(component);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get application main window.
	 * @param component
	 */
	public static Window getApplicationMainWindow() {
		
		return Utility.applicationMainWindow;
	}
	
	/**
	 * Compute union of given rectangles.
	 */
	public static Rectangle union(Rectangle rectangle1, Rectangle rectangle2) {
		
		try {
			// Compute positions;
			int x1 = Math.min(rectangle1.x, rectangle2.x);
			int y1 = Math.min(rectangle1.y, rectangle2.y);
			int x2 = Math.max(rectangle1.x + rectangle1.width,
					rectangle2.x + rectangle2.width);
			int y2 = Math.max(rectangle1.y + rectangle1.height,
					rectangle2.y + rectangle2.height);
			
			// Return resulting rectangle.
			Rectangle resultRectangle = new Rectangle(x1, y1, x2 - x1, y2 - y1);
			return resultRectangle;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return new Rectangle();
	}

	/**
	 * Compute union of rectangles.
	 * @param rectangle1
	 * @param rectangle2
	 * @return
	 */
	public static Rectangle2D union(Rectangle2D rectangle1, Rectangle2D rectangle2) {
		
		try {
			// Compute positions;
			double x1 = Math.min(rectangle1.getX(), rectangle2.getX());
			double y1 = Math.min(rectangle1.getY(), rectangle2.getY());
			double x2 = Math.max(rectangle1.getX() + rectangle1.getWidth(),
					rectangle2.getX() + rectangle2.getWidth());
			double y2 = Math.max(rectangle1.getY() + rectangle1.getHeight(),
					rectangle2.getY() + rectangle2.getHeight());
			
			// Return resulting rectangle.
			Rectangle2D resultRectangle = new Rectangle2D.Double(x1, y1, x2 - x1, y2 - y1);
			return resultRectangle;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return new Rectangle2D.Double();
	}

	/**
	 * Repaint later.
	 */
	public static void repaintLater(final Component comp) {
		
		Safe.invokeLater(() -> {
			comp.repaint();
		});
	}

	/**
	 * Center window on the screen.
	 * @param window
	 */
	public static void centerOnScreen(Window window) {
		try {
			
			// Get window width and height.
			int width = window.getWidth();
			int height = window.getHeight();
	
			// Get screen dimensions and set window location.
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			window.setLocation((screenSize.width - width) / 2,
					(screenSize.height - height) / 2);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Center window on the screen.
	 * @param component
	 */
	public static void centerOnScreen(Component component) {
		try {
			
			// Delegate the call.
			Window window = findWindow(component);
			centerOnScreen(window);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Returns true if the text matches the search text.
	 * @param meansText
	 * @param searchText 
	 * @return
	 */
	public static boolean matches(String title, String titleWithIdAndAlias,
			String searchText, boolean caseSensitive, boolean wholeWords,
			boolean exactMatch) {
		
		try {
			String text = exactMatch ? title : titleWithIdAndAlias;
			return matches(text, searchText, caseSensitive, wholeWords, exactMatch);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Returns true if the texts matches the search text.
	 * @param meansText
	 * @param searchText 
	 * @return
	 */
	public static boolean matches(String [] texts, String searchText, boolean caseSensitive,
			boolean wholeWords, boolean exactMatch) {
		
		try {
			// Check input texts.
			if (texts.length <= 0) {
				return searchText.isEmpty();
			}
			
			String text = null;
			if (exactMatch) {
				text = texts[0];
			}
			else {
				text = "";
				for (String textItem : texts) {
					
					if (!text.isEmpty()) {
						text += ' ';
					}
					text += textItem;
				}
			}
			return matches(text, searchText, caseSensitive, wholeWords, exactMatch);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Returns true if the text matches the search text.
	 * @param text
	 * @param searchText 
	 * @return
	 */
	public static boolean matches(String text, String searchText,
			boolean caseSensitive, boolean wholeWords, boolean exactMatch) {
		
		try {
			// If the search text is an asterisk, return true value.
			if (searchText.compareTo("*") == 0) {
				return true;
			}
			
			// If the matching is not case sensitive, convert the
			// strings to upper case.
			if (!caseSensitive) {
				text = text.toUpperCase();
				searchText = searchText.toUpperCase();
			}
			
			// If exact match...
			if (exactMatch) {
				return text.compareTo(searchText) == 0;
			}
			
			// Split the text and the search string into words.
			String [] words = searchText.split(" ");
			String [] textWords = text.split(" ");
	
			// Do loop for all not empty words.
			for (String word : words) {
				
				if (!word.trim().isEmpty()) {
					
					if (!wholeWords) {
						if (text.contains(word)) {
							return true;
						}
					}
					else {
						for (String textWord : textWords) {
							
							textWord = textWord.trim();
							if (!textWord.isEmpty()) {
								if (word.compareTo(textWord) == 0) {
									return true;
								}
							}
						}
					}
				}
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Creates new listening combo box.
	 * @param items 
	 * @param comboBox
	 */
	public static JComboBox<ComponentItem> createListeningComboBox(ComponentItem[] items) {
		
		try {
			JComboBox<ComponentItem> comboBox = new JComboBox<ComponentItem>(items);
			
			comboBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						Object item = e.getItem();
						if (item instanceof ComponentItem) {
							((ComponentItem)item).getMethod().run();
						}
					}
				}
			});
			
			return comboBox;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Performs button localization.
	 * @param button
	 */
	public static void localize(JButton button) {
		try {
			
			// Localize caption.
			String caption = button.getText();
			if (caption != null && !caption.isEmpty()) {
				button.setText(Resources.getString(caption));
			}
			
			// Localize tool tip.
			String tooltip = button.getToolTipText();
			if (tooltip != null && !tooltip.isEmpty()) {
				button.setToolTipText(Resources.getString(tooltip));
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Performs label localization.
	 * @param label
	 */
	public static void localize(JLabel label) {
		try {
			
			label.setText(Resources.getString(label.getText()));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Performs check box localization.
	 * @param checkBox
	 */
	public static void localize(JCheckBox checkBox) {
		try {
			
			checkBox.setText(Resources.getString(checkBox.getText()));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Performs radio button localization.
	 * @param radioButton
	 */
	public static void localize(JRadioButton radioButton) {
		try {
			
			radioButton.setText(Resources.getString(radioButton.getText()));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Loacalize toggle button.
	 * @param toggleButton
	 */
	public static void localize(JToggleButton toggleButton) {
		try {
			
			toggleButton.setText(Resources.getString(toggleButton.getText()));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Performs localization of panel title.
	 * @param panel
	 */
	public static void localize(JPanel panel) {
		try {
			
			Border border = panel.getBorder();
			if (border instanceof TitledBorder) {
				TitledBorder titleBorder = (TitledBorder) border;
				titleBorder.setTitle(Resources.getString(titleBorder.getTitle()));
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Performs localization of dialog title.
	 * @param dialog
	 */
	public static void localize(Dialog dialog) {
		try {
			
			dialog.setTitle(Resources.getString(dialog.getTitle()));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Localize frame title.
	 * @param frame
	 * @param key - Resource bundle key.
	 */
	public static void localize(JFrame frame) {
		try {
			
			frame.setTitle(Resources.getString(frame.getTitle()));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Localize menu.
	 * @param menuArea
	 */
	public static void localize(JMenu menu) {
		try {
			
			menu.setText(Resources.getString(menu.getText()));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Localize menu item.
	 * @param menuAreaEdit
	 */
	public static void localize(JMenuItem menuItem) {
		try {
			
			menuItem.setText(Resources.getString(menuItem.getText()));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Localize tab pane.
	 * @param tabbedPane
	 */
	public static void localize(JTabbedPane tabbedPane) {
		try {
			
			// Convert titles.
			for (int index = 0; index < tabbedPane.getTabCount(); index++) {
				
				tabbedPane.setTitleAt(index, Resources.getString(tabbedPane.getTitleAt(index)));
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Localize tool bar.
	 * @param toolBar
	 */
	public static void localize(JToolBar toolBar) {
		try {
			
			// Get number of tool bar components.
			int componentCount = toolBar.getComponentCount();
			
			// Traverse all tool bar components.
			for (int index = 0; index < componentCount; index++) {
				
				Component component = toolBar.getComponentAtIndex(index);
				
				// Localize each button on the tool bar.
				if (component instanceof JButton) {
					
					JButton button = (JButton) component;
					localize(button);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Localize tooltip.
	 * @param textPane
	 */
	public static void localizeTooltip(JTextPane textPane) {
		try {
			
			textPane.setToolTipText(
					Resources.getString(textPane.getToolTipText()));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Localize tooltip.
	 * @param button
	 */
	public static void localizeTooltip(JButton button) {
		try {
			
			button.setToolTipText(
					Resources.getString(button.getToolTipText()));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Localize tooltip.
	 * @param menu
	 */
	public static void localizeTooltip(JMenu menu) {
		try {
			
			menu.setToolTipText(
					Resources.getString(menu.getToolTipText()));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Gets node of type ELEMENT.
	 * @param node
	 * @return
	 */
	public static Node getElementNode(Node node) {
		
		try {
			while (node != null) {
				// If the node is an element, return it.
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					return node;
				}
				
				node = node.getNextSibling();
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		// Nothing found.
		return null;
	}

	/**
	 * Gets file name of given resource.
	 * @param object
	 * @param resource
	 * @return
	 */
	public static String getFileName(Object object, String resource) {
		
		try {
			// Get class loader.
			ClassLoader classLoader = object.getClass().getClassLoader();
			if (classLoader != null) {
				// Get URL.
				URL url = classLoader.getResource(resource);
				if (url != null) {
					// Get file name.
					String name = url.getFile();
					if (name != null) {
						return name;
					}
				}
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Show message.
	 * @param message
	 */
	public static void show2(String message, Object ... parameters) {
		try {
			
			String finalMessage = String.format(message, parameters);
			JOptionPane.showMessageDialog(null, finalMessage);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Show message.
	 * @param parent
	 * @param message
	 * @param parameters
	 */
	public static void show(Component parent, String message,
			Object ... parameters) {
		try {
			
			String finalMessage = String.format(Resources.getString(message), parameters);
			JOptionPane.showMessageDialog(parent, finalMessage);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}


	/**
	 * Show message and ask user.
	 * @param message
	 */
	public static boolean ask2(String message, Object ... parameters) {
		
		try {
			message = String.format(message, parameters);
			return JOptionPane.showConfirmDialog(null, message) == JOptionPane.YES_OPTION;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Show message and ask user.
	 * @param parent
	 * @param message
	 */
	public static boolean ask2(Component parent, String message, Object ... parameters) {
		
		try {
			message = String.format(message, parameters);
			return JOptionPane.showConfirmDialog(parent, message) == JOptionPane.YES_OPTION;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Ask user.
	 * @param parent
	 * @param message
	 * @param parameters
	 * @return
	 */
	public static boolean askParam(Component parent, String message,
			Object ... parameters) {
		
		try {
			message = String.format(Resources.getString(message), parameters);
			return JOptionPane.showConfirmDialog(parent, message) == JOptionPane.YES_OPTION;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Show localized message.
	 * @param parent
	 * @param textName
	 */
	public static void show(Component parent, String textName) {
		try {
			
			JOptionPane.showMessageDialog(parent, Resources.getString(textName));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Show localized message.
	 * @param parent
	 * @param textName
	 */
	public static void show(String textName) {
		try {
			
			JOptionPane.showMessageDialog(null, Resources.getString(textName));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Show localized message.
	 * @param textName
	 * @param parameters
	 */
	public static void show(String textName, Object ... parameters) {
		try {
			
			String message = String.format(Resources.getString(textName), parameters);
			JOptionPane.showMessageDialog(null, Resources.getString(message));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Show and confirm message.
	 * @param parent
	 * @param textName
	 * @return
	 */
	public static boolean showConfirm(Component parent, String textName) {
		
		try {
			return JOptionPane.showConfirmDialog(parent, 
					Resources.getString(textName), Resources.getString("org.multipage.gui.textConfirmDialog"),
					JOptionPane.YES_NO_CANCEL_OPTION) == JOptionPane.YES_OPTION;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Show localized message and ask user.
	 * @param parent
	 * @param textName
	 */
	public static boolean ask(Component parent, String textName) {
		
		try {
			return JOptionPane.showConfirmDialog(parent, Resources.getString(textName))
					== JOptionPane.YES_OPTION;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Show localized message and ask user.
	 * @param parent
	 * @param textName
	 */
	public static boolean ask(Component parent, String textName, Object ... parameters) {
		
		try {
			String message = String.format(Resources.getString(textName), parameters);
			
			return JOptionPane.showConfirmDialog(parent, message)
					== JOptionPane.YES_OPTION;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Show localized message and ask user.
	 * @param parent
	 * @param textName
	 */
	public static boolean ask(String textName, Object ... parameters) {
		
		try {
			String message = String.format(Resources.getString(textName), parameters);
			
			return JOptionPane.showConfirmDialog(applicationMainWindow, message)
					== JOptionPane.YES_OPTION;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Ask user to insert text.
	 * @param parent
	 * @param textName
	 * @return
	 */
	public static String input(Component parent, String textName, String defaulString) {
		
		try {
			return JOptionPane.showInputDialog(parent, Resources.getString(textName),
					defaulString);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Ask user to insert text.
	 * @param textName
	 * @param defaulString
	 * @return
	 */
	public static String input(String textName, String defaulString) {
		
		try {
			return JOptionPane.showInputDialog(applicationMainWindow, Resources.getString(textName),
					defaulString);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Ask user to insert text.
	 * @param textName
	 * @return
	 */
	public static String input(String textName) {
		
		try {
			return JOptionPane.showInputDialog(applicationMainWindow, Resources.getString(textName),
					"");
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Ask user to insert text.
	 * @param parent
	 * @param text
	 * @return
	 */
	public static String input2(Component parent, String text, String defaulString) {
		
		try {
			return JOptionPane.showInputDialog(parent, text, defaulString);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Input user text.
	 * @param parent
	 * @param textName
	 * @return
	 */
	public static String input(Component parent, String textName) {
		
		try {
			return JOptionPane.showInputDialog(parent, Resources.getString(textName));
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Input user text. Center dialog on screen.
	 * @param parent
	 * @param initialValue
	 * @param textName
	 * @param parameters
	 * @return
	 */
	public static String inputCenter(Component parent, String initialValue, String textName, Object ... parameters) {
		
		try {
			// Create panel for input.
			String messageTemplate = Resources.getString(textName);
			String message = String.format(messageTemplate, parameters);
			JOptionPane optionPane = new JOptionPane(message);
			optionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
			optionPane.setWantsInput(true);
			optionPane.setInitialSelectionValue(initialValue);
			
			// Create dialog from the panel centered on screen.
			String title = Resources.getString("org.multipage.gui.titleInputDialog");
			JDialog inputDialog = optionPane.createDialog(parent, title);
			Utility.centerOnScreen(inputDialog);
			inputDialog.setVisible(true);
			inputDialog.dispose();

			// Get output value.
	        Object value = optionPane.getInputValue();
	        if (value == JOptionPane.UNINITIALIZED_VALUE) {
	            return null;
	        }
			return value.toString();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Show option panel centered on the screen.
	 * @param parent
	 * @param textName
	 * @param parameters
	 */
	public static void showCenter(Component parent, String textName, Object ... parameters) {
		try {
			
			// Create panel to display the message.
			String messageTemplate = Resources.getString(textName);
			String message = String.format(messageTemplate, parameters);
			JOptionPane optionPane = new JOptionPane(message);
			optionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
			
			// Create dialog from the panel centered on screen.
			String title = Resources.getString("org.multipage.gui.titleMessageDialog");
			JDialog messageDialog = optionPane.createDialog(parent, title);
			Utility.centerOnScreen(messageDialog);
			messageDialog.setVisible(true);
			messageDialog.dispose();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Display dialog centered on screen that asks user.
	 * @param parent
	 * @param textName
	 */
	public static boolean askCenter(Component parent, String textName, Object ... parameters) {
		
		try {
			// Create panel to display the message.
			String messageTemplate = Resources.getString(textName);
			String message = String.format(messageTemplate, parameters);
			JOptionPane optionPane = new JOptionPane(message);
			optionPane.setOptionType(JOptionPane.YES_NO_CANCEL_OPTION);
			optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
			
			// Create dialog from the panel centered on screen.
			String title = Resources.getString("org.multipage.gui.titleMessageDialog");
			JDialog messageDialog = optionPane.createDialog(parent, title);
			Utility.centerOnScreen(messageDialog);
			messageDialog.setVisible(true);
			messageDialog.dispose();
			
			// Get selected value.
			Object selectedValue = optionPane.getValue();
			if (selectedValue instanceof Integer) {
				Integer selectedButton = (Integer) selectedValue;
				boolean success = (JOptionPane.YES_OPTION == selectedButton);
				return success;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Show message.
	 * @param component
	 * @param format
	 */
	public static void show2(Component component, String text) {
		try {
			
			JOptionPane.showMessageDialog(component, text);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Show message.t
	 * @param text
	 */
	public static void show2(String text) {
		try {
			
			JOptionPane.showMessageDialog(null, text);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Show message and ask user.
	 * @param component
	 * @param format
	 */
	public static boolean ask2(Component component, String text) {
		
		try {
			return JOptionPane.showConfirmDialog(component, text) == JOptionPane.YES_OPTION;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Show message and ask user. The dialog window is always on top.
	 * @param text
	 * @return
	 */
	public static boolean ask2Top(String text) {
		
		try {
			// Create and initialize panel.
			JOptionPane optionPane = new JOptionPane();
			
			optionPane.setMessage(text);
			optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
			optionPane.setOptionType(JOptionPane.YES_NO_CANCEL_OPTION);
			
			// Create and show dialog. The dialog displayed is on top of the desktop windows.
			JDialog dialog = optionPane.createDialog(Resources.getString("org.multipage.gui.titlePleaseConfirm"));
			dialog.setIconImage(Images.getImage("org/multipage/gui/images/main.png"));
			dialog.setAlwaysOnTop(true);
			dialog.setVisible(true);
			
			// Try to get selected value.
			Object value = optionPane.getValue();
			if (value instanceof Integer) {
				
				Integer answer = (Integer) value;
				boolean returnedValue = answer == JOptionPane.YES_OPTION;
				
				return returnedValue;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Gets file extension.
	 * @param file
	 * @return
	 */
	public static String getExtension(File file) {
		
		try {
			String fileName = file.getName();
			return getExtension(fileName);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get extension.
	 * @param fileName
	 * @return
	 */
	public static String getExtension(String fileName) {
		
		try {
			// Get last dot position.
			int dotPosition = fileName.lastIndexOf('.');
			if (dotPosition == -1) {
				return "";
			}
			// Get substring after the dot.
			try {
				return fileName.substring(dotPosition + 1);
			}
			catch (IndexOutOfBoundsException e) {
				return "";
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Make record index in the model visible.
	 * @param table
	 * @param recordIndex
	 * @param isViewIndex - true = view index, false = model index
	 */
	public static void ensureRecordVisible(JTable table, int recordIndex, boolean isViewIndex) {
		try {
			
			// Check index.
			if (recordIndex < 0 || recordIndex >= table.getRowCount()) {
				return;
			}
	
			// Get view index.
			int viewIndex;
			
			if (isViewIndex) {
				viewIndex = recordIndex;
			}
			else {
				try {
					viewIndex = table.convertRowIndexToView(recordIndex);
				}
				catch (Exception e) {
					return;
				}
			}
			
			// Make the row selected.
			Container container = table.getParent();
			if (container instanceof JViewport) {
				JViewport viewport = (JViewport) container;
				
				// Get table row rectangle.
				Rectangle rowRectangle = table.getCellRect(viewIndex, 0, true);
				// Get view position.
				Point viewPosition = viewport.getViewPosition();
				// Translate the cell location so that it is relative
			    // to the view, assuming the northwest corner of the
			    // view is (0,0).
			    rowRectangle.setLocation(rowRectangle.x - viewPosition.x,
			    		rowRectangle.y - viewPosition.y);
			    // Scroll the area into view.
			    viewport.scrollRectToVisible(rowRectangle);
			}
			
			table.repaint();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Ensure list item visible.
	 * @param list
	 */
	public static void ensureSelectedItemVisible(JList list) {
		try {
			
			list.ensureIndexIsVisible(list.getSelectedIndex());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Returns true value if the file is a text file.
	 * @param file
	 * @return
	 */
	public static boolean isTextFileExtension(File file) {
		
		try {
			// Possible text file extensions.
			String [] textExtensions = {"css", "htm", "html", "htmls", "js", "shtml", "text", "txt", "xml"};
	
			// Get file extension.
			String extension = getExtension(file);
			
			// Find extension match.
			for (String textExtension : textExtensions) {
				if (extension.equalsIgnoreCase(textExtension)) {
					
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
	 * Load encodings to the combobox.
	 * @param comboBox
	 * @param defaultEncoding 
	 */
	public static void loadEncodings(JComboBox comboBox,
			String defaultEncoding) {
		try {
			
			// Get character sets map.
			SortedMap<String, Charset> map = Charset.availableCharsets();
			
			int index = 0;
			int utf8Index = 0;
			
			// Load combobox.
			for (String key : map.keySet()) {
				
				comboBox.addItem(key);
				
				// Select character set.
				if (key.compareToIgnoreCase(defaultEncoding) == 0) {
					
					utf8Index = index;
				}
				
				index++;
			}
			
			comboBox.setSelectedIndex(utf8Index);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Select combo box item.
	 * @param comboBox
	 * @param text
	 */
	public static boolean selectComboItem(JComboBox comboBox, String text) {
		
		try {
			// Do loop for all combobox items.
			for (int index = 0; index < comboBox.getItemCount(); index++) {
				
				String itemText = (String) comboBox.getItemAt(index);
				if (itemText.equals(text)) {
					
					comboBox.setSelectedIndex(index);
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
	 * Select combobox item.
	 * @param comboBox
	 * @param n
	 */
	public static boolean selectComboItem(JComboBox comboBox, Integer n) {
		
		try {
			// Do loop for all combobox items.
			for (int index = 0; index < comboBox.getItemCount(); index++) {
				
				Integer itemValue = (Integer) comboBox.getItemAt(index);
				if (itemValue == n) {
					
					comboBox.setSelectedIndex(index);
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
	 * Add file chooser.
	 * @param dialog
	 * @param pathName
	 * @param filters
	 * @param useStringResources
	 */
	public static void addFileChooserFilters(JFileChooser dialog,
			String pathName, String[][] filters, boolean firstSelected) {
		try {
			
			// Delegate the call
			addFileChooserFilters(dialog, pathName, filters, firstSelected, true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Add file chooser.
	 * @param dialog
	 * @param pathName
	 * @param filters
	 * @param useStringResources
	 */
	public static void addFileChooserFilters(JFileChooser dialog,
			String pathName, String[][] filters, boolean firstSelected, boolean useStringResources) {
		try {
			
			// Get default file chooser.
			FileFilter defaultFilter = dialog.getFileFilter();
			
			FileNameExtensionFilter firstFilter = null;
			FileNameExtensionFilter selectedFilter = null;
			
			String fileExtension = pathName != null ?
					Utility.getExtension(pathName)
					: null;
			
			// Do loop for all filters.
			if (filters != null) {
				for (String [] filter : filters) {
					
					// Get filter text.
					String filterText = useStringResources ? Resources.getString(filter[0]) : filter[0];
					// Extension found flag.
					boolean extensionFound = false;
					// Get extensions.
					String [] extensions = new String [filter.length - 1];
					for (int index = 0; index < extensions.length; index++) {
						
						extensions[index] = filter[index + 1];
						// If the extension matches, set the flag.
						if (fileExtension != null
								&& fileExtension.equalsIgnoreCase(extensions[index])) {
							extensionFound = true;
						}
					}
					// Create file filter object.
					FileNameExtensionFilter fileFilter = new FileNameExtensionFilter(
					        filterText, extensions);
					// Set first filter.
					if (firstFilter == null) {
						firstFilter = fileFilter;
					}
					// Set first found filter with given extension.
					if (extensionFound && selectedFilter == null) {
						selectedFilter = fileFilter;
					}
					// Add it to the dialog.
					dialog.addChoosableFileFilter(fileFilter);
				}
			}
			
			// Set actual filter.
			dialog.setFileFilter(selectedFilter != null ? selectedFilter
					: (firstSelected ? firstFilter : defaultFilter));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get scroll position.
	 * @param scrollPane
	 */
	public static Point getScrollPosition(JScrollPane scrollPane) {
		
		try {
			JViewport viewport = scrollPane.getViewport();
			return viewport.getViewPosition();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Scroll to given position.
	 * @param scrollPane 
	 * @param position
	 */
	public static void scrollToPosition(final JScrollPane scrollPane,
			final Point position) {

		Safe.invokeLater(() -> {

			// Scroll to the start.
			JViewport viewport = scrollPane.getViewport();
			if (viewport != null) {
				viewport.setViewPosition(position);
			}
		});
	}
	
	/**
	 * Returns a TreePath containing the specified node.
	 * @param node
	 * @return
	 */
	public static TreePath getTreePath(TreeNode node) {
		
		try {
		    List list = new ArrayList();
	
		    // Add all nodes to list
		    while (node != null) {
		        list.add(node);
		        node = node.getParent();
		    }
		    Collections.reverse(list);
	
		    // Convert array of nodes to TreePath
		    return new TreePath(list.toArray());
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Choose file name to save.
	 * @return
	 */
	public static File chooseFileNameToSave(Component parentComponent,
			String [][] filters) {
		
		try {
			// Select resource file.
			JFileChooser dialog = new JFileChooser(currentPathName);
			
			// Add filters.
			addFileChooserFilters(dialog, null, filters, true);
							
			// Save dialog.
		    if(dialog.showSaveDialog(parentComponent) != JFileChooser.APPROVE_OPTION) {
		       return null;
		    }
		    
		    // Get selected file.
		    File file = dialog.getSelectedFile();
		    
		    // Set current path name.
		    if (file != null) {
		    	currentPathName = file.getParent();
		    }
		
		    return file;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Choose file name to opem.
	 * @param translatorDialog
	 * @param filters: {{"org.multipage.translator.textXmlFilesDictionary", "xml"}, {...}, ...}
	 * @return
	 */
	public static File chooseFileToOpen(Component parentComponent, String[][] filters) {
		
		try {
			// Delegate the call
			return chooseFileToOpen(parentComponent, filters, true);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Choose file name to open.
	 * @param translatorDialog
	 * @param filters: {{"org.multipage.translator.textXmlFilesDictionary", "xml"}, {...}, ...} or {{"XML files", "xml"}, {...}, ...}
	 * @param useStringResources - if true the method uses IDs of strings in filter definition otherwise ordinary texts
	 * @return
	 */
	public static File chooseFileToOpen(Component parentComponent,
			String[][] filters, boolean useStringResources) {
		
		try {
			// Select resource file.
			JFileChooser dialog = new JFileChooser();
			if (currentPathName.isEmpty()) {
				currentPathName = dialog.getFileSystemView().getDefaultDirectory().toString() + File.separatorChar + "Multipage";
			}
			dialog.setCurrentDirectory(new File(currentPathName));
			
			// Add filters.
			addFileChooserFilters(dialog, null, filters, true);
							
			// Save dialog.
		    if(dialog.showOpenDialog(parentComponent) != JFileChooser.APPROVE_OPTION) {
		       return null;
		    }
		    
		    // Get selected file.
		    File file = dialog.getSelectedFile();
		    
		    // Set current path name.
		    if (file != null) {
		    	currentPathName = file.getParent();
		    }
		
		    return file;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Ensure text position visible.
	 * @param textComponent
	 * @param textPosition
	 */
	public static void ensureTextVisible(JTextComponent textComponent,
			int textPosition) {
		try {
			
			TextUI textUI = textComponent.getUI();
			try {
				Rectangle rectangle = textUI.modelToView(textComponent, textPosition);
				textComponent.scrollRectToVisible(rectangle);
			}
			catch (BadLocationException e) {
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get clipboard string.
	 * @return
	 */
	public static String getClipboardString() {
		
		try {
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable clipData = clipboard.getContents(clipboard);
			if (clipData != null) {
				
				try {
					if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
						String text = (String)(clipData.getTransferData(
								DataFlavor.stringFlavor));
						return text;
					}
				}
				catch (UnsupportedFlavorException e) {
					
					Utility.show(null, "org.multipage.gui.messageFlavorUnsupported");
				}
				catch (IOException e) {
					
					Utility.show(null, "org.multipage.gui.messageDataNotAvailable");
				}
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Put clipboard string.
	 * @param text 
	 * @return
	 */
	public static void putClipboardString(String text) {
		try {
			
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			StringSelection stringSelection = new StringSelection(text);
			clipboard.setContents(stringSelection, null);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Choose directory.
	 * @param parent
	 * @param titleText
	 * @param defaultPath
	 * @return
	 */
	public static String chooseDirectory(Component parent, String titleText, String defaultPath) {
		
		try {
			// Set current path name.
			java.io.File currentDirectory = null;
			if (defaultPath != null && !defaultPath.isEmpty()) {
				currentDirectory = new java.io.File(defaultPath);
			}
			else {
				currentDirectory = new java.io.File(currentPathName);
			}
			
			JFileChooser chooser = new JFileChooser(); 
			chooser.setCurrentDirectory(currentDirectory);
			chooser.setDialogTitle(titleText);
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			
			// Disable the "All files" option.
			chooser.setAcceptAllFileFilterUsed(false);
	 
		    if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) { 
		      currentPathName = chooser.getSelectedFile().toString();
		      return currentPathName;
		    }
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Choose directory.
	 * @param parent
	 * @param titleText
	 * @return
	 */
	public static String chooseDirectory(Component parent, String titleText) {
		
		try {
			// Delegate this call.
			String selectedPath = chooseDirectory(parent, titleText, null);
			return selectedPath;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Choose directory.
	 * @param parent
	 * @param title
	 * @return
	 */
	public static String chooseDirectory2(Component parent, String title) {
		
		try {
			String titleText = Resources.getString(title);
			return chooseDirectory(parent, titleText, null);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Choose directory.
	 * @param parent
	 * @param title
	 * @param defaultPath
	 * @return
	 */
	public static String chooseDirectory2(Component parent, String title, String defaultPath) {
		
		try {
			String titleText = Resources.getString(title);
			return chooseDirectory(parent, titleText, defaultPath);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Set character attributes.
	 * @param htmlTextPane
	 * @param attributes
	 * @param replace
	 */
	public static void setCharacterAttributes(JEditorPane htmlTextPane,
			SimpleAttributeSet attributes, boolean replace) {
		try {
			
			int p0 = htmlTextPane.getSelectionStart();
		    int p1 = htmlTextPane.getSelectionEnd();
		    if (p0 != p1) {
				StyledDocument doc = getStyledDocument(htmlTextPane);
				doc.setCharacterAttributes(p0, p1 - p0, attributes, replace);
		    }
		    StyledEditorKit k = getStyledEditorKit(htmlTextPane);
		    MutableAttributeSet inputAttributes = k.getInputAttributes();
		    if (replace) {
		    	inputAttributes.removeAttributes(inputAttributes);
		    }
		    inputAttributes.addAttributes(attributes);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get styled document.
	 * @param htmlTextPane
	 * @return
	 */
	public static StyledDocument getStyledDocument(JEditorPane htmlTextPane) {
		
		try {
			Document d = htmlTextPane.getDocument();
		    if (d instanceof StyledDocument) {
		    	return (StyledDocument) d;
		    }
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Get styled editor kit.
	 * @param htmlTextPane
	 * @return
	 */
    public static StyledEditorKit getStyledEditorKit(JEditorPane htmlTextPane) {
    	
    	try {
		    EditorKit k = htmlTextPane.getEditorKit();
		    if (k instanceof StyledEditorKit) {
		    	return (StyledEditorKit) k;
		    }
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Get input attributes.
	 * @param htmlTextPane
	 * @return
	 */
	public static AttributeSet getInputAttributes(JEditorPane htmlTextPane) {
		
		try {
			StyledEditorKit kit = Utility.getStyledEditorKit(htmlTextPane);
			return kit.getInputAttributes();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Load font families.
	 * @param combo
	 * @param select 
	 */
	public static void loadFontFamilies(JComboBox combo, String select) {
		try {
			
			String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
			            		.getAvailableFontFamilyNames();
			   
			combo.removeAllItems();
			int index = 0;
			int selectIndex = 0;
			for (String name : fonts) {
				combo.addItem(name);
				if (name.equals(select)) {
					selectIndex = index;
				}
				index++;
			}
			combo.setSelectedIndex(selectIndex);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load font sizes.
	 * @param combo
	 */
	public static void loadFontSizes(JComboBox combo, int select) {
		try {
			
			int [] sizes =
	        	{8, 9, 10, 11, 12, 13, 14, 16, 18, 20, 24, 26, 28, 32, 36, 40, 48, 56, 64, 72};
			
			combo.removeAllItems();
			int index = 0;
			int selectIndex = 0;
			
			for (int size : sizes) {
				
				combo.addItem(size);
				if (size == select) {
					selectIndex = index;
				}
				index++;
			}
			combo.setSelectedIndex(selectIndex);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set alignments.
	 * @param combo
	 * @param select
	 */
	@SuppressWarnings("unchecked")
	public static void setParagraphAlignments(JComboBox combo, int select) {
		try {
			
			final Object [][] items = {
					{Images.getIcon("org/multipage/gui/images/align_center.png"), StyleConstants.ALIGN_CENTER},
					{Images.getIcon("org/multipage/gui/images/align_justified.png"), StyleConstants.ALIGN_JUSTIFIED},
					{Images.getIcon("org/multipage/gui/images/align_left.png"), StyleConstants.ALIGN_LEFT},
					{Images.getIcon("org/multipage/gui/images/align_right.png"), StyleConstants.ALIGN_RIGHT}};
			
			combo.removeAllItems();
			// Load combo.
			int index = 0;
			int selectedIndex = 0;
			
			for (Object [] item : items) {
				combo.addItem(item);
				if (item[1].equals(select)) {
					selectedIndex = index;
				}
				index++;
			}
			
			// Set renderer.
			@SuppressWarnings("serial")
			class Renderer extends JLabel {
				// Flags.
				boolean isSelected;
				boolean hasFocus;
				// Paint renderer.
				@Override
				public void paint(Graphics g) {
					try {
						
						super.paint(g);
						GraphUtility.drawSelection(g, this, isSelected, hasFocus);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			}
			
			combo.setRenderer(new ListCellRenderer() {
				Renderer renderer = new Renderer();
				@Override
				public Component getListCellRendererComponent(JList list, Object value,
						int index, boolean isSelected, boolean cellHasFocus) {
					
					try {
						if (value != null && value instanceof Object[]) {
							Object [] item = (Object []) value;
							renderer.setIcon((Icon) item[0]);
							renderer.isSelected = isSelected;
							renderer.hasFocus = cellHasFocus;
							return renderer;
						}
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return null;
				}
			});
			
			combo.setSelectedIndex(selectedIndex);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
	}

	/**
	 * Select combo box align.
	 * @param comboBox
	 * @param align
	 */
	public static void selectComboAlign(JComboBox comboBox, int align) {
		try {
			
			// Do loop for all combobox items.
			for (int index = 0; index < comboBox.getItemCount(); index++) {
				
				Object object = comboBox.getItemAt(index);
				if (!(object instanceof Object[])) {
					continue;
				}
				Object [] itemValue = (Object []) object;
				object = itemValue[1];;
				if (!(object instanceof Integer)) {
					continue;
				}
				int itemAlign = (Integer) object;
				if (itemAlign == align) {
					
					comboBox.setSelectedIndex(index);
					break;
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Choose color.
	 */
	public static Color chooseColor(Component parent, Color initialColor) {
		
		try {
			final JColorChooser colorChooser = new JColorChooser(initialColor);
			final Obj<Color> currentColor = new Obj<Color>(initialColor);
			
			// Create color dialog.
			JDialog colorDialog = JColorChooser.createDialog(
					parent,
					Resources.getString("org.multipage.gui.textColorChooserDialog"),
					true,
					colorChooser,
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							// Set current color.
							currentColor.ref = colorChooser.getColor();
						}
					},
					null);
			colorDialog.setVisible(true);
			return currentColor.ref;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return Color.BLACK;
	}
	
	/**
	 * Return input color with given intensity.
	 * @param rgbColor
	 * @param intensity
	 * @return
	 */
	public static int adjustColorIntesity(int rgbColor, double intensity) {
		
		try {
			// Check input intesity.
			if (intensity >= 1.0) {
				return rgbColor;
			}
			if (intensity <= 0.0) {
				return 0;
			}
			
			// Compute components of the input color.
			int newRed = (int) (((rgbColor >>> 16) & 0x0000FF) * intensity);
			int newGreen = (int) (((rgbColor >>> 8) & 0x0000FF) * intensity);
			int newBlue = (int) ((rgbColor & 0x0000FF) * intensity);
			
			
			// Compute output color value and return it.
			int outputColor = (newRed << 16) | (newGreen << 8) | newBlue;
			return outputColor;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0;
	}
	
	/**
	 * Expand / collapse all.
	 * @param tree
	 * @param model
	 * @param parent
	 * @param expand
	 */
	private static void expandAll(JTree tree, TreeModel model, TreePath parent,
			boolean expand) {
		try {
			
			// Traverse children
			Object node = parent.getLastPathComponent();
			int count = model.getChildCount(node);
			
			for (int index = 0; index < count; index++) {
	
				Object child = model.getChild(node, index);
				TreePath path = parent.pathByAddingChild(child);
				expandAll(tree, model, path, expand);
		    }
	
		    // Expansion or collapse must be done bottom-up
		    if (expand) {
		        tree.expandPath(parent);
		    }
		    else {
		        tree.collapsePath(parent);
		    }
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Expand top of the tree.
	 * @param tree
	 * @param expand
	 */
	public static void expandTop(JTree tree, boolean expand) {
		
		try {
			TreeModel model = tree.getModel();
			if (model != null) {
				TreePath parent = new TreePath(model.getRoot());
				
			    // Expansion or collapse must be done bottom-up
			    if (expand) {
			        tree.expandPath(parent);
			    } 
			    else {
			        tree.collapsePath(parent);
			    }
			}
		}
		catch (Exception e) {
		}
	}

	/**
	 * Expand / collapse all.
	 * @param tree
	 * @param expand
	 */
	public static void expandAll(JTree tree, boolean expand) {
		try {
			
			TreeModel model = tree.getModel();
			if (model != null) {
				TreePath parent = new TreePath(model.getRoot());
				expandAll(tree, model, parent, expand);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Expand / collapse selected tree items.
	 * @param tree
	 * @param expand
	 */
	public static void expandSelected(JTree tree, boolean expand) {
		try {
			
			if (expand) {
		        expandSelected(tree);
		    } 
		    else {
		        collapseSelected(tree);
		    }
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Expand selected tree items.
	 * @param tree
	 * @param expand
	 */
	public static void expandSelected(JTree tree) {
		try {
			
			// Get selected paths.
			TreePath [] selectedPaths = tree.getSelectionPaths();
			if (selectedPaths == null) {
				return;
			}
			
			LinkedList<TreePath> queuedPaths = new LinkedList<TreePath>();
			queuedPaths.addAll(Arrays.asList(selectedPaths));
		    
			while (!queuedPaths.isEmpty()) {
				
				// Expand/collapse current path.
				TreePath path = queuedPaths.removeFirst();
			    tree.expandPath(path);
			    
			    // Get child paths.
			    LinkedList<TreePath> childPaths = getTreeChildPaths(tree, path);
			    queuedPaths.addAll(childPaths);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Collapse selected tree items.
	 * @param tree
	 * @param expand
	 */
	public static void collapseSelected(JTree tree) {
		try {
			
			// Get selected paths.
			TreePath [] selectedPaths = tree.getSelectionPaths();
			if (selectedPaths == null) {
				return;
			}
			
			LinkedList<TreePath> pathsToCollapse = new LinkedList<TreePath>();
			pathsToCollapse.addAll(Arrays.asList(selectedPaths));
			
			LinkedList<TreePath> queue = new LinkedList<TreePath>();
			queue.addAll(Arrays.asList(selectedPaths));
		    
			while (!queue.isEmpty()) {
				
			    // Get child paths.
				TreePath path = queue.removeFirst();
			    LinkedList<TreePath> childPaths = getTreeChildPaths(tree, path);
			    
			    queue.addAll(childPaths);
			    pathsToCollapse.addAll(childPaths);
			}
			
		    while (!pathsToCollapse.isEmpty()) {
			    
				// Collapse current path.
				TreePath path = pathsToCollapse.removeLast();
			    tree.collapsePath(path);
		    }
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Returns true value if the list contains given item.
	 * @param list
	 * @param item
	 * @return
	 */
	public static boolean contains(LinkedList list, Object item) {
		
		try {
			for (Object included : list) {
				if (included.equals(item)) {
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
	 * Gets child paths of given path in the tree.
	 * @param tree
	 * @param path
	 * @return
	 */
	private static LinkedList<TreePath> getTreeChildPaths(JTree tree, TreePath path) {
		
		try {
			LinkedList<TreePath> childPaths = new LinkedList<TreePath>();
			TreeModel model = tree.getModel();
			
			Object node = path.getLastPathComponent();
		    for (int index = 0; index < model.getChildCount(node); index++) {
		    	
		    	Object childNode = model.getChild(node, index);
		    	
		    	TreePath childPath = path.pathByAddingChild(childNode);
		    	childPaths.addLast(childPath);
		    }
			
			return childPaths;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Returns true value if an intersection of given rectangles exists.
	 * @param r1
	 * @param r2
	 * @return
	 */
	public static boolean isIntersection(Rectangle2D r1,
			Rectangle2D r2) {
		
		try {
			double x1 = r1.getX();
			double x2 = r2.getX();
			double y1 = r1.getY();
			double y2 = r2.getY();
			double w1 = r1.getWidth();
			double w2 = r2.getWidth();
			double h1 = r1.getHeight();
			double h2 = r2.getHeight();
			
			// Intersection of rectangle1 and strip1.
			boolean interR1S1 = !(x2 > x1 + w1 || x1 > x2 + w2);
			// Intersection of rectangle1 and strip2.
			boolean interR1S2 = !(y2 > y1 + h1 || y1 > y2 + h2);
			
			// Intersection exists if rectangle1 intersects with strips 1 and 2.
			return interR1S1 && interR1S2;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Returns true value if boundary intersects with rectangle.
	 * @param r
	 * @param b
	 * @return
	 */
	public static boolean isBoundaryIntersection(Rectangle2D r,
			Rectangle2D b) {
		
		try {
			// If there is not an intersection, return false value.
			if (!isIntersection(r, b)) {
				return false;
			}
			
			double x1 = r.getX();
			double x2 = b.getX();
			double y1 = r.getY();
			double y2 = b.getY();
			double w1 = r.getWidth();
			double w2 = b.getWidth();
			double h1 = r.getHeight();
			double h2 = b.getHeight();
			
			boolean isRinB = (x2 < x1) && (x2 + w2 > x1 + w1) &&
							(y2 < y1) && (y2 + h2 > y1 + h1);
			// Rectangle must not be inside the boundary.
			return !isRinB;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Get item color.
	 * @param index
	 * @return
	 */
	public static Color itemColor(int index) {
		
		try {
			return (index % 2 == 0) ? colorLight : colorDark;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return Color.BLACK;
	}
	
	/**
	 * Find component window.
	 * @param component
	 * @return
	 * @throws Exception 
	 */
	public static Window findWindow(Component component) {
		
		try {
			// Check the component.
			if (component == null) {
				return getApplicationMainWindow();
			}
			
			// Find window recursively.
			Window window = findWindowRecursively(component);
			return window;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	  
	/**
	 * Find component window recursively.
	 * @param component
	 * @return
	 */
	private static Window findWindowRecursively(Component component) {
		
		try {
			if (component == null) {
			    return JOptionPane.getRootFrame();
			}
			else if (component instanceof Window) {
			    return (Window) component;
			}
			else {
			    return findWindowRecursively(component.getParent());
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return JOptionPane.getRootFrame();
	}

	/**
	 * Removes only our private highlights
	 * @param textComponent
	 */
	public static void removeFindHighlights(JTextComponent textComponent) {
		try {
			
			Highlighter hilite = textComponent.getHighlighter();
		    Highlighter.Highlight[] hilites = hilite.getHighlights();
	
		    for (int i=0; i < hilites.length; i++) {
		        if (hilites[i].getPainter() instanceof FindHighlightPainter) {
		            hilite.removeHighlight(hilites[i]);
		        }
		    }
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Returns true value if the position points to word start.
	 * @param text
	 * @param start
	 * @return
	 */
	public static boolean isWordStart(String text, int start) {
		
		try {
			int textLength = text.length();
			if (textLength <= 0) {
				return false;
			}
			if (start <= 0) {
				return true;
			}
			
			// Get previous character.
			char previousCharacter = text.charAt(start - 1);
			return isNotWordCharacter(previousCharacter);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Is not word character.
	 * @param character
	 * @return
	 */
	public static boolean isNotWordCharacter(char character) {
		
		try {
			char [] interpunctions = {',', '.', ';', '\'', '\"', ':', '!', '?'};
			
			for (char interpuction : interpunctions) {
				if (character == interpuction) {
					return true;
				}
			}
			return Character.isSpaceChar(character);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Returns true value if the position points to word end.
	 * @param text
	 * @param end
	 * @return
	 */
	public static boolean isWordEnd(String text, int end) {
		
		try {
			int textLength = text.length();
			if (textLength <= 0) {
				return false;
			}
			if (end >= textLength) {
				return true;
			}
			
			// Get next character.
			char nextCharacter = text.charAt(end);
			return isNotWordCharacter(nextCharacter);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Highlight found.
	 * @param textComponent
	 * @param foundAttr
	 */
	public static void highlight(JTextComponent textComponent, FoundAttr foundAttr,
			Highlighter.HighlightPainter myHighlightPainter) {
		try {
			
			// First remove all old highlights
		    removeFindHighlights(textComponent);
		    // Find texts.
		    find(null, foundAttr, textComponent, myHighlightPainter);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Returns true value if the text is found.
	 * @param pattern
	 * @param foundAttr
	 * @param textComponent
	 * @param hilite
	 * @param myHighlightPainter
	 * @return
	 */
	private static boolean find(String text, FoundAttr foundAttr, JTextComponent textComponent,
			Highlighter.HighlightPainter myHighlightPainter) {
	    
		try {
		    // If nothing found exit the method.
		    if (foundAttr == null) {
		    	return false;
		    }
		    
		    String pattern = foundAttr.searchText;
		    boolean caseSensitive = foundAttr.isCaseSensitive;
		    boolean wholeWord = foundAttr.isWholeWords;
		    
			// If the pattern is empty, exit the method.
			if (pattern.isEmpty()) {
				return false;
			}
	
			boolean result = false;
			
		    try {
		        Highlighter hilite = null;
		        
		        if (textComponent != null) {
		        	hilite = textComponent.getHighlighter();
		        }
		        
		        if (text == null) {
		        	if (textComponent != null) {
			        	Document document = textComponent.getDocument();
			        	int length = document.getLength();
			        	text = document.getText(0, length);
		        	}
		        }
		        
		        if (text == null) {
		        	return false;
		        }
		        
		        int position = 0;
				
				// If not case sensitive, covert texts to upper case.
				if (!caseSensitive) {
					text = text.toUpperCase();
					pattern = pattern.toUpperCase();
				}
	
		        // Search for pattern
		        while ((position = text.indexOf(pattern, position)) >= 0) {
		        	
		            // Create highlighter using private painter and apply around pattern
		        	int start = position;
		        	int end = position + pattern.length();
		            boolean isWholeWord = isWordStart(text, start) && isWordEnd(text, end);
		            
		            if (!wholeWord || (wholeWord && isWholeWord)) {
		            	result = true;
		            	if (hilite != null && myHighlightPainter != null) {
		            		hilite.addHighlight(start, end, myHighlightPainter);
		            		
		            	}
		            }
		            
		            position += pattern.length();
		        }
		    }
		    catch (BadLocationException e) {
		    }
		    return result;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Returns true value if the text is found.
	 * @param text
	 * @param foundAttr
	 * @return
	 */
	public static boolean find(String text, FoundAttr foundAttr) {
		
		try {
			return find(text, foundAttr, null, null);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Returns true value if the text is found.
	 * @param text
	 * @param parameters
	 * @return
	 */
	public static boolean find(String text, Parameters parameters) {
		
		try {
			FoundAttr foundAttr = new FoundAttr(parameters.getSearchedText(),
					parameters.isCaseSensitive(),
					parameters.isWholeWords());
			
			return find(text, foundAttr, null, null);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Convert text to file name.
	 * @param text
	 * @return
	 */
	public static String convertToFileName(String text) {
		
		try {
			if (text.isEmpty()) {
				return "default";
			}
			
			text = text.trim();
			
			String newText = "";
			final String notAllowed = "\\/:*?\"<>";
			
			for (int index = 0; index < text.length(); index++) {
				char character = text.charAt(index);
				if (notAllowed.indexOf(character) == -1) {
					newText += character;
				}
				else {
					newText += '_';
				}
			}
			
			return newText;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Read text from input stream
	 * @param is
	 * @param bufferSize
	 * @return
	 */
	public static String readTextFromInputStream(final InputStream is, final int bufferSize)
	{
		try {
			final char[] buffer = new char[bufferSize];
			final StringBuilder out = new StringBuilder();
			
			try {
				final Reader in = new InputStreamReader(is, "UTF-8");
				try {
					for (;;) {
						int rsz = in.read(buffer, 0, buffer.length);
						if (rsz < 0) {
							break;
						}
						out.append(buffer, 0, rsz);
					}
				}
				finally {
					in.close();
				}
			}
			catch (Exception ex) {
			}
			return out.toString();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get SQL chained exception message.
	 * @param exception
	 * @return
	 */
	public static String getSQLExceptionChainedMessage(SQLException exception) {
		
		try {
			String message = "";
			int exceptionNumber = 1;
			
			while (exception != null) {
				
				if (exceptionNumber > 1) {
					message += "\r\n";
				}
				message += exceptionNumber + ".)  " + exception.getMessage();
				
				exceptionNumber++;
				
				exception = exception.getNextException();
			}
			
			return message;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Remove folder content.
	 * @param folderName
	 */
	public static void deleteFolderContent(File folder) {
		try {
			
			File[] files = folder.listFiles();
		    
		    // Some JVMs return null for empty directories.
		    if (files != null) {
		    	
		        for(File file : files) {
		        	
		            if(file.isDirectory()) {
		                deleteFolderContent(file);
		            }
		            file.delete();
		        }
		    }
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Remove folder content.
	 * @param folderName
	 */
	public static boolean deleteFolderContent(String folderName) {
		
		try {
			// Ask user.
			if (!ask2(null, String.format(Resources.getString("org.multipage.gui.messageDeleteFolder"), folderName))) {
				return false;
			}
			
			File folder = new File(folderName);
			deleteFolderContent(folder);
			
			return true;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Find regular expression in text.
	 * @param text
	 * @param regExpText
	 * @return
	 */
	public static boolean findRegExp(String text, String regExpText, boolean isCaseInsensitive) {
		
		try {
			Pattern pattern = Pattern.compile(regExpText, isCaseInsensitive ? Pattern.CASE_INSENSITIVE : 0);
			
			Matcher matcher = pattern.matcher(text);
			return matcher.find();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Compute intersection length.
	 * @param x1
	 * @param width1
	 * @param x2
	 * @param width2
	 * @return
	 */
	public static double getIntersectionLength(double A1, double length1,
			double A2, double length2) {
		
		try {
			if (length1 <= 0.0 || length2 <= 0.0) {
				return 0.0;
			}
			
			double B1 = A1 + length1;
			double B2 = A2 + length2;
			
			if (A2 > B1 || B2 < A1) {
				return 0.0;
			}
			
			double A = Math.max(A1, A2);
			double B = Math.min(B1, B2);
			
			return B - A;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0.0;
	}

	/**
	 * Trim folder.
	 * @param folder
	 * @return
	 */
	public static String trimFolder(String folder) throws Exception {
		
		try {
			if (folder == null) {
				return null;
			}
			
			// Trim folder name.
			String output = "";
			boolean previousIsSeparator = false;
			
			for (int index = 0; index < folder.length(); index++) {
				
				char character = folder.charAt(index);
				boolean isSeparator = character == '\\' || character == '/';
				
				if (isSeparator) {
					character = pathSeparatorCharacter;
				}
				else {
					if (!(Character.isJavaIdentifierPart(character)
							|| character == '-'
							|| character == '_')) {
						throw new Exception(Resources.getString("org.multipage.gui.messageBadFolderName"));
					}
				}
				
				if (!(isSeparator && previousIsSeparator)) {
					output += character;
				}
				
				previousIsSeparator = isSeparator;
			}
			
			if (output.equals(pathSeparator)) {
				return output;
			}
			
			folder = output;
			
			int length = folder.length();
					
			// Remove possible starting separator.
			if (folder.startsWith(pathSeparator)) {
				output = folder.substring(1, length);
			}
			
			folder = output;
			
			length = folder.length();
			
			// Remove possible trailing separator.
			if (length > 0 && folder.lastIndexOf(pathSeparator) == length - 1) {
				output = folder.substring(0, length - 1);
			}
	
			if (output.isEmpty()) {
				return null;
			}
			
			return output;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Get folder name (OS dependent).
	 * @param folder
	 * @return
	 */
	public static String getFolderOSDependent(String folder) {
		
		try {
			if (folder == null) {
				return null;
			}
			
			String output = "";
			
			// Replace separators.
			for (int index = 0; index < folder.length(); index++) {
				
				char character = folder.charAt(index);
				
				if (character == pathSeparatorCharacter) {
					character = File.separatorChar;
				}
				
				output += character;
			}
			
			return output;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Get relative path.
	 * @param basePathText
	 * @param referencedPathText
	 * @return
	 */
	public static String getRelativePath(String basePathText,
			String referencedPathText) {
		
		try {
			Path basePath = Paths.get(basePathText);
			Path referencedPath = Paths.get(referencedPathText);
			
			Path relativePath = basePath.relativize(referencedPath);
			String relativePathText = relativePath.toString();
			
			return relativePathText;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Returns true value if the string is in the list.
	 * @param text
	 * @param texts
	 * @return
	 */
	public static boolean isStringInList(String text,
			LinkedList<String> texts) {
		
		try {
			for (String item : texts) {
				
				if (item.equalsIgnoreCase(text)) {
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
	 * Finds the smallest rectangle enclosing all 
	 * this container's components. This makes JScrollPane
	 * behave correctly when you want put a container in it
	 * that has a null (absolute) layout.
	 * @param component
	 * @param bottomSpace 
	 */
	public static void forceDoLayout(JComponent component, int bottomSpace) {
		try {
			
			component.doLayout();
			
			int maxX = 0;
			int maxY = 0;
			
			Component[] components = component.getComponents();
			
			for (int i = 0; i < components.length; i++) {
				
				Rectangle bounds = components[i].getBounds();
				maxX = Math.max(maxX, (int) bounds.getMaxX());
				maxY = Math.max(maxY, (int) bounds.getMaxY());
			}
			
			component.setPreferredSize(new Dimension(maxX, maxY + bottomSpace));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get expanded paths.
	 * @param tree
	 */
	public static TreePath [] getExpandedPaths(JTree tree) {
		
		try {
			// Get expanded objects.
			LinkedList<TreePath> outputExpandedPaths = new LinkedList<TreePath>();
			TreePath rootPath = tree.getPathForRow(0);
			if (rootPath != null) {
				Enumeration<TreePath> expandedPaths = tree.getExpandedDescendants(rootPath);
				if (expandedPaths != null) {
					
					// Do loop for all expanded paths.
					while (expandedPaths.hasMoreElements()) {
						// Get path.
						TreePath path = expandedPaths.nextElement();
						if (tree.isExpanded(path)) {
							outputExpandedPaths.add(path);
						}
					}
				}
			}
			
			return outputExpandedPaths.toArray(new TreePath[0]);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Traverse expanded elements info
	 * @param tree
	 * @return
	 */
	public static <T> void traverseExpandedElements(JTree tree, Consumer<Object> consumer) {
		try {
			
			// Get list of expanded elements
			int displayedRowCount = tree.getRowCount();
			for (int displayedRow = 0; displayedRow < displayedRowCount; displayedRow++) {
				
				// Retrieve leaf component of the path
				TreePath displayedPath = tree.getPathForRow(displayedRow);
				Object leafComponent = displayedPath.getLastPathComponent();
				
				// Get corresponding element
				if (leafComponent instanceof DefaultMutableTreeNode) {
					DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) leafComponent;
					
					Object nodeUserObject = treeNode.getUserObject();
					if (nodeUserObject != null) {
						
						// Use callback for user object of the node
						consumer.accept(nodeUserObject);
					}
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
	}
	
	/**
	 * Traverse expanded elements info
	 * @param tree
	 * @return
	 */
	public static <T> void traverseExpandedElements(JTree tree, BiConsumer<DefaultMutableTreeNode, Object> consumer) {
		try {
			
			// Get list of expanded elements
			int displayedRowCount = tree.getRowCount();
			for (int displayedRow = 0; displayedRow < displayedRowCount; displayedRow++) {
				
				// Retrieve leaf component of the path
				TreePath displayedPath = tree.getPathForRow(displayedRow);
				Object leafComponent = displayedPath.getLastPathComponent();
				
				// Get corresponding element
				if (leafComponent instanceof DefaultMutableTreeNode) {
					DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) leafComponent;
					
					Object nodeUserObject = treeNode.getUserObject();
					if (nodeUserObject != null) {
						
						// Use callback for user object of the node
						consumer.accept(treeNode, nodeUserObject);
					}
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set expanded paths.
	 * @param tree
	 * @param expandedPaths
	 */
	public static void setExpandedPaths(JTree tree, TreePath[] expandedPaths) {
		try {
			
			// Expand tree paths.
			for (TreePath treePath : expandedPaths) {
				if (treePath != null) {
					tree.expandPath(treePath);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set text change listener.
	 * @param textField
	 * @param onTextChange
	 */
	public static DocumentListener setTextChangeListener(JTextField textField,
			final Runnable onTextChange) {
		
		try {
			DocumentListener listener = new DocumentListener() {
				@Override
				public void removeUpdate(DocumentEvent arg0) {
					try {
						
						// On remove text.
						if (onTextChange != null) {
							onTextChange.run();
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				@Override
				public void insertUpdate(DocumentEvent arg0) {
					try {
						
						// On insert text.
						if (onTextChange != null) {
							onTextChange.run();
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				
				@Override
				public void changedUpdate(DocumentEvent arg0) {
					try {
						
						// On change text.
						if (onTextChange != null) {
							onTextChange.run();
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			};
			
			textField.getDocument().addDocumentListener(listener);
			return listener;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Returns true value if the pattern matches on index position.
	 * @param text
	 * @param index
	 * @param pattern
	 * @return
	 */
	public static boolean patternMatches(String text, int index,
			Pattern pattern) {
		
		try {
			Matcher matcher = pattern.matcher(text);
			
			if (matcher.find(index)) {
				return matcher.start() == index;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Finds end of given pattern.
	 * @param text
	 * @param index
	 * @param pattern
	 * @return
	 */
	public static Integer findPattarnEnd(String text, int index,
			Pattern pattern) {
		
		try {
			Matcher matcher = pattern.matcher(text);
			
			if (matcher.find(index)) {
				return matcher.end();
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Remove diavritics.
	 * @param text
	 * @return
	 */
	public static String removeDiacritics(String text) {
		
		try {
			text = Normalizer.normalize(text, Normalizer.Form.NFD);
		    text = text.replaceAll("[^\\p{ASCII}]", "");
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return text;
	}

	/**
	 * Enable / disable tool bar.
	 * @param container
	 * @param enable
	 */
	public static void enableComponentTree(Container container,
			boolean enable) {
		try {
			
			container.setEnabled(enable);
			
			for (Component component : container.getComponents()) {
				
				// Call the method recursively.
				if (component instanceof Container) {
					enableComponentTree((Container) component, enable);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Input stream object.
	 * @param inputStream
	 * @param type
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T readInputStreamObject(StateInputStream inputStream, Class<?> type)
			throws IOException, ClassNotFoundException {
		
		Object object = inputStream.readObject();
		
		if (object == null) {
			return null;
		}
		
		if (!object.getClass().equals(type)) {
			
			// Try to use type conversion.
			try {
				T typedObject = (T) object;
				return typedObject;
			}
			catch (Exception e) {
			}
			
			throw new ClassNotFoundException();
		}
		
		return (T) object;
	}

	/**
	 * Reset scroll bar position.
	 * @param scrollPane 
	 */
	public static void resetScrollBarPosition(JScrollPane scrollPane) {
		try {
			
			JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
		    JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
		    verticalScrollBar.setValue(verticalScrollBar.getMinimum());
		    horizontalScrollBar.setValue(horizontalScrollBar.getMinimum());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set expanded tree paths.
	 * @param tree
	 * @param treePaths
	 */
	public static void setExpandedPaths(JTree tree,
			IdentifierTreePath[] treePaths) {
		try {
			
			// Do loop for all tree paths.
			for (IdentifierTreePath treePath : treePaths) {
				treePath.expandTreePath(tree);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get expanded tree paths.
	 * @param tree
	 * @return
	 */
	public static IdentifierTreePath[] getExpandedPaths2(JTree tree) {
		
		try {
			// Get expanded objects.
			LinkedList<IdentifierTreePath> outputExpandedPaths = new LinkedList<IdentifierTreePath>();
			TreePath rootPath = tree.getPathForRow(0);
			if (rootPath != null) {
				Enumeration<TreePath> expandedPaths = tree.getExpandedDescendants(rootPath);
				if (expandedPaths != null) {
					
					// Do loop for all expanded paths.
					while (expandedPaths.hasMoreElements()) {
						// Get path.
						TreePath path = expandedPaths.nextElement();
						if (tree.isExpanded(path)) {
							
							outputExpandedPaths.add(new IdentifierTreePath(path));
						}
					}
				}
			}
			
			return outputExpandedPaths.toArray(new IdentifierTreePath[0]);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Load units.
	 * @param comboBox
	 */
	public static void loadCssUnits(JComboBox comboBox) {
		try {
			
			loadCssUnits(comboBox, Utility.cssUnits);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load CSS units.
	 * @param comboBox 
	 * @param units 
	 */
	public static void loadCssUnits(JComboBox comboBox, String[] units) {
		try {
			
			for (String unit : units) {
				comboBox.addItem(unit);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get CSS color.
	 * @param color
	 * @return
	 */
	public static String getCssColor(Color color) {
		
		try {
			if (color == null) {
				return "#000000";
			}
			
			int red = color.getRed();
			int green = color.getGreen();
			int blue = color.getBlue();
			
			String cssColor = String.format("#%02X%02X%02X", red, green, blue);
			return cssColor;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Convert string to number and unit.
	 * @param string
	 * @param number
	 * @param unit
	 */
	public static boolean convertCssStringToNumberUnit(String string,
			Obj<String> number, Obj<String> unit) {
		
		try {
			number.ref = "";
			unit.ref = "";
			
			boolean numberExpected = true;
			
			for (int index = 0; index < string.length(); index++) {
				
				char character = string.charAt(index);
				
				if (numberExpected) {
					boolean isNumber = Character.isDigit(character) || character == '-' || character == '.';
					
					if (isNumber) {
						number.ref += character;
					}
					else {
						numberExpected = false;
					}
				}
				
				// Unit character expected.
				if (!numberExpected) {
					unit.ref += character;
				}
			}
			
			boolean success = true;
			
			// Trim values.
			try {
				number.ref = String.valueOf(Double.parseDouble(number.ref));
			}
			catch (Exception e) {
				
				success = false;
				number.ref = "";
			}
			
			return success;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Select combo named item.
	 * @param comboBox
	 * @param value
	 */
	public static boolean selectComboNamedItem(JComboBox comboBox, String value) {
		
		try {
			for (int index = 0; index < comboBox.getItemCount(); index++) {
				
				Object object = comboBox.getItemAt(index);
				if (object instanceof NamedItem) {
					
					String valueCombo = ((NamedItem) object).value;
					if (valueCombo.equals(value)) {
						
						comboBox.setSelectedIndex(index);
						return true;
					}
				}
				else if (object instanceof String) {
					String textValue = (String) object;
					
					if (textValue.equals(value)) {
						return true;
					}
				}
			}
			
			comboBox.setSelectedIndex(0);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Get CSS value and units.
	 * @param textField
	 * @param comboUnits
	 * @return
	 */
	public static String getCssValueAndUnits(JTextField textField,
			JComboBox comboUnits) {
		
		try {
			return getCssValueAndUnits(textField, comboUnits, null, "0px");
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Get CSS value and units.
	 * @param textField
	 * @param comboUnits
	 * @param comboValues
	 * @return
	 */
	public static String getCssValueAndUnits(JTextField textField,
			JComboBox comboUnits,
			JComboBox comboValues) {
		
		try {
			return getCssValueAndUnits(textField, comboUnits, comboValues, "0px");
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Get CSS value and units.
	 * @param textField
	 * @param comboUnits
	 * @param defaultValue
	 * @return
	 */
	public static String getCssValueAndUnits(JTextField textField,
			JComboBox comboUnits,
			String defaultValue) {
		
		try {
			return getCssValueAndUnits(textField, comboUnits, null, defaultValue);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Get CSS value and units.
	 * @param textField
	 * @param comboUnits
	 * @param comboValues
	 * @param defaultValue
	 * @return
	 */
	public static String getCssValueAndUnits(JTextField textField,
			JComboBox comboUnits,
			JComboBox comboValues,
			String defaultValue) {
		
		try {
			// Try to get combo value.
			if (comboValues != null && textField.getText().isEmpty()) {
				
				String text =  getSelectedNamedItem(comboValues);
				if (text.isEmpty()) {
					return defaultValue;
				}
				return text;
			}
	
			// Try to get number and units.
			try {
				
				String numberText = textField.getText();
				if (!numberText.isEmpty()) {
					
					double number = Double.parseDouble(numberText);
					
					// Get units.
					String units = (String) comboUnits.getSelectedItem();
					
					return String.valueOf(number) + units;
				}
			}
			catch (Exception e) {
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return defaultValue;
	}

	/**
	 * Get CSS number value.
	 * @param textField
	 * @param defaultValue
	 * @return
	 */
	public static String getCssNumberValue(JTextField textField, String defaultValue) {
		
		// Try to get number.
		try {
			
			String numberText = textField.getText();
			if (!numberText.isEmpty()) {
				
				double number = Double.parseDouble(numberText);	
				return String.valueOf(number);
			}
		}
		catch (Exception e) {
		}
		
		return defaultValue;
	}
	
	/**
	 * Set CSS number value.
	 * @param string
	 * @param textField
	 */
	public static void setCssNumberValue(String string, JTextField textField) {
		try {
			
			setCssNumberValue(string, textField, null);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set CSS number value.
	 * @param string
	 * @param textField
	 * @param defaultValue
	 */
	public static void setCssNumberValue(String string, JTextField textField, String defaultValue) {
		
		try {
			if (!string.isEmpty()) {
				
				double number = Double.parseDouble(string);	
				textField.setText(String.valueOf(number));
				return;
			}
		}
		catch (Exception e) {
		}
		
		try {
			
			if (defaultValue != null) {
				textField.setText(defaultValue);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Check if the input field contains a number.
	 * @param textField
	 * @return
	 */
	public static boolean isTextFieldNumber(JTextField textField) {
		
		try {
			String numberText = textField.getText();
			if (!numberText.isEmpty()) {
				
				Double.parseDouble(numberText);
				return true;
			}
		}
		catch (Exception e) {
		}
		
		return false;
	}
	
	/**
	 * Set CSS value and units.
	 * @param string
	 * @param textField
	 * @param comboUnits
	 */
	public static void setCssValueAndUnits(String string,
			JTextField textField, JComboBox comboUnits) {
		try {
			
			setCssValueAndUnits(string, textField, comboUnits, null, null);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set CSS value and units.
	 * @param string
	 * @param textField
	 * @param comboUnits
	 * @param defaultValue
	 */
	public static void setCssValueAndUnits(String string,
			JTextField textField, JComboBox comboUnits, String defaultValue) {
		try {
			
			setCssValueAndUnits(string, textField, comboUnits, defaultValue, null);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set CSS value and units.
	 * @param string
	 * @param textField
	 * @param comboUnits
	 * @param defaultValue
	 * @param defaultUnits
	 */
	public static void setCssValueAndUnits(String string,
			JTextField textField, JComboBox comboUnits, String defaultValue, String defaultUnits) {
		try {
			
			Obj<String> number = new Obj<String>();
			Obj<String> unit = new Obj<String>();
			Utility.convertCssStringToNumberUnit(string, number, unit);
			
			if (number.ref.isEmpty() && defaultValue != null) {
				number.ref = defaultValue;
			}
			
			textField.setText(number.ref);
			
			// Select unit.
			if (!Utility.selectComboItem(comboUnits, unit.ref) && defaultUnits != null) {
				Utility.selectComboItem(comboUnits, defaultUnits);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get color from CSS string.
	 * @param string
	 * @return
	 */
	public static Color getColorFromCss(String string) {
		
		try {
			if (string.length() != 7 && string.charAt(0) != '#') {
				return Color.BLACK;
			}
			
			int red = Integer.parseInt(string.substring(1, 3), 16);
			int green = Integer.parseInt(string.substring(3, 5), 16);
			int blue = Integer.parseInt(string.substring(5, 7), 16);
			
			Color color = new Color(red, green, blue);
			return color;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return Color.BLACK;
	}

	/**
	 * Load named items into the combo-box.
	 * @param comboBox
	 * @param strings
	 */
	public static void loadNamedItems(JComboBox comboBox,
			String[][] strings) {
		try {
			
			for (String [] items : strings) {
				
				if (items.length == 1) {
					comboBox.addItem(items[0]);
				}
				else if (items.length >= 2) {
					comboBox.addItem(new NamedItem(items[1], items[0]));
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Add empty item.
	 * @param comboBox
	 */
	public static void loadEmptyItem(JComboBox comboBox) {
		try {
			
			comboBox.addItem(new NamedItem(""));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get selected named item.
	 * @param comboBox
	 * @return
	 */
	public static String getSelectedNamedItem(JComboBox comboBox) {
		
		try {
			Object object = comboBox.getSelectedItem();
			
			if (object instanceof NamedItem) {
				return ((NamedItem) object).value;
			}
			if (object instanceof String) {
				return (String) object;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Get next match of given regular expression.
	 * @param string
	 * @param position
	 * @param regex
	 * @return
	 */
	public static String getNextMatch(String string,
			Obj<Integer> position, String regex) {
		
		try {
			return getNextMatch(string, position, regex, null);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Get next match of given regular expression.
	 * @param string
	 * @param position
	 * @param regex
	 * @return
	 */
	public static String getNextMatch(String string,
			Obj<Integer> position, String regex, Obj<Matcher> outputMatcher) {
		
		try {
			if (outputMatcher != null) {
				outputMatcher.ref = null;
			}
			
			if (position.ref == null) {
				position.ref = 0;
			}
			
			if (position.ref < 0 || position.ref >= string.length()) {
				return null;
			}
			
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(string);
			
			if (outputMatcher != null) {
				outputMatcher.ref = matcher;
			}
			
			if (matcher.find(position.ref)) {
				
				int start = matcher.start();
				int end = matcher.end();
				
				String matchedString = string.substring(start, end);
				position.ref = end;
				
				return matchedString;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Returns true value if the string is a number with unit.
	 * @param string
	 * @return
	 */
	public static boolean isCssStringNumberUnit(String string) {
		
		try {
			Obj<String> number = new Obj<String>();
			Obj<String> unit = new Obj<String>();
			
			return convertCssStringToNumberUnit(string, number, unit);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Get selected paths.
	 * @param tree
	 * @return
	 */
	public static IdentifierTreePath[] getSelectedPaths(JTree tree) {
		
		try {
			// Get selected objects.
			LinkedList<IdentifierTreePath> outputSelectedPaths = new LinkedList<IdentifierTreePath>();
			TreePath rootPath = tree.getPathForRow(0);
			if (rootPath != null) {
				TreePath [] selectedPaths = tree.getSelectionPaths();
				if (selectedPaths != null) {
					
					// Do loop for all selected paths.
					for (TreePath path : selectedPaths) {
						// Add path.
						outputSelectedPaths.add(new IdentifierTreePath(path));
					}
				}
			}
			
			return outputSelectedPaths.toArray(new IdentifierTreePath[0]);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Set selected paths.
	 * @param tree
	 * @param treePaths
	 */
	public static void setSelectedPaths(JTree tree,
			IdentifierTreePath[] treePaths) {
		try {
			
			// Do loop for all tree paths.
			for (IdentifierTreePath treePath : treePaths) {
				treePath.addSelection(tree);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Close splash screen.
	 */
	public static void closeSplash() {
		try {
			
			SplashScreen splash = SplashScreen.getSplashScreen();
			if (splash != null) {
				try {
					Thread.sleep(1500);
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				splash.close();
			}
			
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Split aliases.
	 * @param aliases
	 * @return
	 */
	public static HashSet<String> splitAliases(String aliases) {
		
		try {
			String [] aliasesArray = aliases.split(",");
			HashSet<String> aliasesSet = new HashSet<String>();
			
			for (String alias : aliasesArray) {
				
				alias = alias.trim();
				
				if (!alias.isEmpty()) {
					aliasesSet.add(alias);
				}
			}
			
			return aliasesSet;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Select first combo item.
	 * @param comboBox
	 */
	public static void selectFirst(JComboBox comboBox) {
		try {
			
			if (comboBox.getItemCount() > 0) {
				comboBox.setSelectedIndex(0);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Remove float trailing nulls.
	 * @param floatInput
	 * @return
	 */
	public static String removeFloatNulls(String floatInput) {
		
		try {
			float floatNumber = Float.parseFloat(floatInput);
	        String outputFloat = String.format("%s", floatNumber);
	        
	        return outputFloat;
	    }
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Get float value.
	 * @param textField
	 * @param defaultValue
	 * @return
	 */
	public static float getFloat(JTextField textField, float defaultValue) {
		
		try {
			return Float.parseFloat(textField.getText());
		}
		catch (Exception e) {
		}
		
		return defaultValue;
	}

	/**
	 * Load angle units.
	 * @param comboBox
	 */
	public static void loadCssAngleUnits(JComboBox comboBox) {
		try {
			
			loadCssUnits(comboBox, Utility.cssAngleUnits);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get next number.
	 * @param text
	 * @param position
	 * @param terminalRegex
	 * @return
	 */
	public static Float getNextNumber(String text, Obj<Integer> position, String terminalRegex) {
		
		try {
			String textFloat = getNextMatch(text, position, "\\G\\s*[0-9\\.\\-\\+]+\\s*" + (terminalRegex != null ? terminalRegex : ""));
			if (textFloat == null) {
				return null;
			}
			try {
				float number = Float.parseFloat(textFloat.trim());
				return number;
			}
			catch (Exception e) {
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Load combo box items.
	 * @param comboBox
	 * @param strings
	 */
	public static void loadItems(JComboBox comboBox, String[] strings) {
		try {
			
			comboBox.removeAllItems();
			
			for (String string : strings) {
				comboBox.addItem(string);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Show HTML message.
	 * @param parentComponent
	 * @param htmlText
	 */
	public static void showHtml(Component parentComponent,
			String htmlText) {
		try {
			
			ShowHtmlMessageDialog.showDialog(parentComponent, htmlText);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Show HTML message.
	 * @param parentComponent
	 * @param htmlMessage
	 */
	public static void showHtml(Component parentComponent,
			String htmlTextName, Object ... parameters) {
		try {
			
			String htmlTextTemplate = Resources.getString(htmlTextName);
			String htmlText = String.format(htmlTextTemplate, parameters);
			
			ShowHtmlMessageDialog.showDialog(parentComponent, htmlText);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Add array items to a list.
	 * @param queue
	 * @param array
	 */
	public static <T> void addArrayItems(LinkedList<T> queue,
			T[] array) {
		try {
			
			for (T item : array) {
				queue.add(item);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Traverse UI.
	 * @param update
	 */
	public static void traverseUI(ComponentUpdate update) {
		try {
			
			// Get windows and traverse UI components.
			Window [] windows = Window.getWindows();
				
			LinkedList<Component> queue = new LinkedList<Component>();
			Utility.addArrayItems(queue, windows);
			
			while (!queue.isEmpty()) {
	
				Component component = queue.removeFirst();
				if (!update.run(component)) {
	
					if (component instanceof Container) {
						
						Container container = (Container) component;
						Utility.addArrayItems(queue, container.getComponents());
					}
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Enable web links.
	 */
	public static void enableWebLinks(Component parent, JEditorPane editor) {
		try {
			
			editor.addHyperlinkListener((HyperlinkEvent event) -> {
				try {
					
					if(event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			        	
			        	// Open bowser with given URL.
			        	if(Desktop.isDesktopSupported()) {
			        	    try {
								Desktop.getDesktop().browse(event.getURL().toURI());
							}
			        	    catch (Exception e) {
			        	    	
								// Report error.
								Utility.show2(parent, e.getLocalizedMessage());
							}
			        	}
			        }
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
	 * Normalize new lines.
	 * @param text
	 * @return
	 */
	public static String normalizeNewLines(String text) {
		
		try {
			text = text.replaceAll("\r(?!\n)|\r\n", "\n");
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return text;
	}

	/**
	 * Replace non ASCII characters.
	 * @param text
	 * @return
	 */
	public static String replaceNonAsciiChars(String text) {
		
		try {
			String nfdNormalizedString = Normalizer.normalize(text, Normalizer.Form.NFD); 
		    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		    return pattern.matcher(nfdNormalizedString).replaceAll("");
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return text;
	}

	/**
	 * Trim text.
	 * @param text
	 * @return
	 */
	public static String trim(String text) {
		
		if (text == null) {
			return "";
		}
		return text;
	}

	/**
	 * Get combo box items.
	 * @param comboBox
	 * @return
	 */
	public static <T> LinkedList<T> getList(JComboBox<T> comboBox) {
		
		try {
			LinkedList<T> list = new LinkedList<T>();
			
			for (int index = 0; index < comboBox.getItemCount(); index++) {
				list.add(comboBox.getItemAt(index));
			}
			
			return list;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Set list items.
	 * @param comboBox
	 * @param items
	 */
	public static<T> void setList(JComboBox<T> comboBox, LinkedList<T> items) {
		try {
			
			comboBox.removeAllItems();
			
			for (T item : items) {
				comboBox.addItem(item);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get index of selected button.
	 * @param buttonGroup
	 * @return
	 */
	public static int getSelected(ButtonGroup buttonGroup) {
		
		int index = -1;
		try {
			Enumeration<AbstractButton> buttons = buttonGroup.getElements();
			while (buttons.hasMoreElements()) {
				
				AbstractButton button = buttons.nextElement();
				
				index++;
				if (button.isSelected()) {
					break;
				}
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return index;
	}

	/**
	 * Set button selection depending on index value.
	 * @param buttonGroup
	 * @param index
	 */
	public static void setSelected(ButtonGroup buttonGroup, int index) {
		try {
			
			if (index < 0 || index >= buttonGroup.getButtonCount()) {
				return;
			}
			
			Enumeration<AbstractButton> buttons = buttonGroup.getElements();
			
			int buttonIndex = 0;
			while (buttons.hasMoreElements()) {
				
				AbstractButton button = buttons.nextElement();
				if (buttonIndex++ == index) {
					
					button.setSelected(true);
					break;
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Unzip input stream to a directory.
	 * @param inputStream
	 * @param path
	 * @param method - can be "zip" or "gzip"
	 * @throws Exception 
	 */
	public static void unzipInputStreamToDirectory(InputStream inputStream, String path, String method)
			throws Exception {
		
        switch (method) {
    	
	    case "gzip":
	    	
	    	File file = new File(path);
	    	
	    	// Extracts file.
	    	Files.copy(new GZIPInputStream(new BufferedInputStream(inputStream)), file.toPath());
	    	break;
    	
        default:
        case "zip":
        	
        	ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));
        	ZipEntry entry = zipInputStream.getNextEntry();
        	
            // Iterates over entries in the zip file.
            while (entry != null) {
            	
				try {
    	        	// Get system dependent entry name.
    	        	String entryName = entry.getName();
    	        	entryName = Paths.get(entryName).toString();
    	            
    	        	// Create file object.
    	        	String filePath = path + File.separator + entryName;
    	            file = new File(filePath);
    	
    	            // Extract file only if it doesn't exist.
    	            if (!file.exists()) {
    	            	
    	            	// Create directories and the file.
	                	if (entry.isDirectory()) {
	                		file.mkdirs();
	                	}
	                	else {
	                		if (file.getParentFile() != null) {
	                			file.getParentFile().mkdirs();
	                		}
	                			                		
	                		// Transfer data from input stream to a file.
	                    	Files.copy(zipInputStream, file.toPath());
	                	}
    	            }
            	}
            	catch (Exception e) {
            		throw e;
            	}
            	finally {
            		zipInputStream.closeEntry();
            	}
            	
                entry = zipInputStream.getNextEntry();
            }
        	break;
        }
	}
	
	/**
	 * Unzip file to a directory.
	 * @param file
	 * @param path
	 * @param method
	 * @throws IOException 
	 * @throws ZipException 
	 */
	public static void unzipFileToDirectory(File file, String path, String method)
			throws Exception {
		
		// Try to open zip file.
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(file);
		}
		catch (Exception e) {
			throw new Exception();
		}
		
		Exception exception = null;
		
		// Iterates over entries in the zip file.
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			
			ZipEntry entry = (ZipEntry) entries.nextElement();
            	
			InputStream zipInputStream = null;
			
			try {
	        	// Get system dependent entry name.
	        	String entryName = entry.getName();
	        	entryName = Paths.get(entryName).toString();
	            
	        	// Create unzipped file object.
	        	String filePath = path + File.separator + entryName;
	            File unzippedfile = new File(filePath);
	
	            // Extract the file only if it doesn't exist.
	            if (!unzippedfile.exists()) {
	            	
	            	// Create directories and/or the file.
                	if (entry.isDirectory()) {
                		unzippedfile.mkdirs();
                	}
                	else {
                		if (unzippedfile.getParentFile() != null) {
                			unzippedfile.getParentFile().mkdirs();
                		}
                			                		
                		// Transfer data from input stream to a file.
                		zipInputStream = zipFile.getInputStream(entry);
                		
                    	Files.copy(zipInputStream, unzippedfile.toPath());
                	}
	            }
        	}
        	catch (Exception e) {
        		exception = e;
        	}
        	finally {
        		
        		// Close the input stream.
        		try { zipInputStream.close(); } catch (Exception e) {}
        	}
		}
		
		zipFile.close();
		
		// If there was an exception, throw it.
		if (exception != null) {
			throw exception;
		}
	}

    /**
     * Load long values from a text file.
     * @param path
     * @return
     */
	public static TreeSet<Long> loadLongsFromFile(Path path) {
		
		try {
			String content = "";
			try {
				content = new String(Files.readAllBytes(path));
			}
			catch (Exception e) {
			}
			TreeSet<Long> resourceIds = new TreeSet<Long>();
			for (String item : content.split(" ")) {
				try {
					resourceIds.add(Long.parseLong(item.trim()));
				}
				catch (Exception e) {
				}
			}
			
			return resourceIds;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Read all text lines from the input stream.
	 * @param inputStream
	 * @return
	 */
	public static StringBuilder readAllLines(InputStream inputStream)
			throws Exception {
		
		try {
			StringBuilder text = new StringBuilder("");
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			
			while (true) {
				
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				
				text.append(line);
				text.append("\n");
			}
			return text;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Run executable file using given command that is placed in directory
	 * designated by "path" argument.
	 * @param workingDirectory
	 * @param command
	 * @return
	 * @throws Exception 
	 */
	public static String runExecutable(String workingDirectory, String command)
			throws Exception {
		
		// Delegate the call.
		String outputString = runExecutable(workingDirectory, command, null, null);
		return outputString;
	}

	/**
	 * Run executable file using given command that is placed in directory
	 * designated by "path" argument.
	 * @param workingDirectoryPath
	 * @param command
	 * @param timeout
	 * @param unit
	 * @return
	 * @throws Exception 
	 */
	public static String runExecutable(String workingDirectoryPath, String command, Integer timeout, TimeUnit unit)
			throws Exception {
		
		StringBuilder text = new StringBuilder("");
		Exception exception = null;
		
		// Check working directory.
		File workingDirectory = new File(workingDirectoryPath);
		if (!workingDirectory.isDirectory()) {
			throw new Exception(String.format(
					Resources.getString("org.multipage.gui.messageUnknownWorkingDirectoryForExecutable"),
					workingDirectoryPath,
					command));
		}
		
		InputStream processOutput = null;
		BufferedReader reader = null;
		try {
			
			// Run the command as a process and wait for it.
			Process process = Runtime.getRuntime().exec(command, null, workingDirectory);
			
			// Wait given time span for process termination.
			if (timeout != null && unit != null) {
				process.waitFor(timeout, unit);
			}
			else {
				process.waitFor();
			}
			
			// Get its stdout and read the output text.
			processOutput = process.getInputStream();
			text = readAllLines(processOutput);
			processOutput = process.getErrorStream();
			text.append(readAllLines(processOutput));
		}
		catch (Exception e) {
			exception = e;
		}
		finally {
			
			// Close stdout.
			if (processOutput != null) {
				try {
					processOutput.close();
				}
				catch (Exception e) {
				}
			}
			
			// Close reader.
			if (reader != null) {
				try {
					reader.close();
				}
				catch (Exception e) {
				}
			}
		}
		
		// If there is an exception, throw it.
		if (exception != null) {
			throw exception;
		}
		
		return text.toString();
	}
	
	/**
	 * Get JRE directory.
	 * @return
	 */
	public static String getJavaRuntimeFolder() {
		
		try {
			String javaRuntimePath = System.getProperty("java.home") + File.separatorChar + "bin";
			return javaRuntimePath;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Run executable JAR using java.exe.
	 * @param workingDirectory
	 * @param executableJarPath
	 * @param parameters
	 */
	public static String runExecutableJar(String workingDirectory, String executableJarPath, String [] parameters)
			throws Exception {
		
		// Get Java home directory.
		String javaRuntimePath = getJavaRuntimeFolder();
		String javaExePath = javaRuntimePath + File.separatorChar + "java.exe";
		
		// Compile java execution command.
		StringBuilder javaCommand = new StringBuilder();
		javaCommand.append('\"').append(javaExePath).append("\" -jar \"").append(executableJarPath).append('\"');
		
		// Add parameters.
		if (parameters != null) {
			for (String parameter : parameters) {
				javaCommand.append(" \"").append(parameter).append('\"');
			}
		}
		
		// Run java command.
		String result = runExecutable(workingDirectory, javaCommand.toString());
		return result;
	}
	
	/**
	 * Run Java class.
	 * @param runnableClass
	 * @param classDirectory
	 * @param workingDirectory
	 * @param parameters
	 * @throws Exception 
	 */
	public static String runJavaClass(Class<?> runnableClass, String classDirectory, String workingDirectory, String[] parameters)
			throws Exception {
		
		// Get Java home directory.
		String javaRuntimePath = getJavaRuntimeFolder();
		String javaExePath = javaRuntimePath + File.separatorChar + "java.exe";
		
		// Compile java execution command.
		String classFullName = runnableClass.getCanonicalName();
		StringBuilder javaCommand = new StringBuilder();
		javaCommand.append('\"').append(javaExePath).append("\" -cp \"").append(classDirectory).append("\" ").append(classFullName);
		
		// Add parameters.
		if (parameters != null) {
			for (String parameter : parameters) {
				javaCommand.append(" \"").append(parameter).append('\"');
			}
		}
		
		String javaCommandString = javaCommand.toString();
		
		// TODO: <---DEBUG Run Java class.
		log(javaCommandString);
		show2(javaCommandString);
		
		// Run java command.
		try {
			show2("RUN EXECUTABLE");
			String result = "JAVA RESULT " + runExecutable(workingDirectory, javaCommandString);
			log(result);
			show2(result);
			return result;
		}
		catch (Exception e) {
			String result = "JAVA EXCEPTION " + e.getLocalizedMessage();
			log(result);
			show2(result);
			return result;
		}
	}
	
	/**
	 * Run selected Java tool.
	 * @param workingDirectory
	 * @param javaToolName
	 * @param parameters
	 */
	public static String runJavaTool(String workingDirectory, String javaToolName, String[] parameters, int timeout, TimeUnit unit)
			throws Exception {
		
		final Pattern whitespaceRegex = Pattern.compile("\\s+");
			
		// Get Java home directory.
		String javaToolExecutable = getJavaRuntimeFolder() + File.separator + javaToolName;
		
		// Build Java tool statement.
		StringBuilder javaStatement = new StringBuilder();
		javaStatement.append('\"').append(javaToolExecutable).append('\"');
		
		// Add parameters.
		if (parameters != null) {
			for (String parameter : parameters) {
				
				// If the parameter contains white space, wrap it with quotes.
				Matcher matcher = whitespaceRegex.matcher(parameter);
				boolean found = matcher.find();
				javaStatement.append(' ');
				if (found) {
					javaStatement.append('\"');
				}
				javaStatement.append(parameter);
				if (found) {
					javaStatement.append('\"');
				}
			}
		}
		
		// Run executable statement.
		String resultText = runExecutable(workingDirectory, javaStatement.toString(), timeout, unit);
		return resultText;
	}
	
	/**
	 * Returns true value if text is a format string
	 * @param text
	 * @return
	 */
	public static boolean isFormatString(String text) {
		
		try {
			return text.matches(formatSpecifier);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Opens editor for given file.
	 * @param temporaryPhpFile
	 */
	public static void openEditor(File file) {
		
		try {
			Desktop.getDesktop().open(file);
		} 
		catch (IOException e) {
			Utility.show2(null, e.getLocalizedMessage());
		}
	}
	
	/**
	 * Gets first line of the input text.
	 * @param text
	 * @return
	 */
	public static String extractFirstLine(String text) {
		
		try {
			Pattern pattern = Pattern.compile("^(.*)$", Pattern.MULTILINE);
			Matcher matcher = pattern.matcher(text);
			boolean matches = matcher.find();
			int groups = matcher.groupCount();
					
			if (matches && groups == 1) {
				String firstLine = matcher.group(1);
				return firstLine;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Escape HTML tags.
	 * @param standardOutput
	 * @return
	 */
	public static String htmlSpecialChars(String standardOutput) {
		
		try {
			standardOutput = standardOutput.replace("&", "&amp;");
			standardOutput = standardOutput.replace("<", "&lt;");
			standardOutput = standardOutput.replace(">", "&gt;");
			standardOutput = standardOutput.replace("\"", "&quot;");
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return standardOutput;
	}

	/**
	 * Log message
	 * @param message
	 */
	public static boolean log(String message) {
		
		try {
			final long maximumFileSize = 65000;
			final String folder = "C:\\logs\\Multipage";
			final File logFilePath = new File(folder);
			final File logFile = new File(folder, "logfile.txt");
			
			// Check path
			if (!logFilePath.exists()) {
				//logFilePath.mkdirs();
				return false;
			}
			
			try {
				RandomAccessFile file = new RandomAccessFile(logFile, "rws");
				long length = file.length();
				byte [] content = new byte [(int) length];
				file.readFully(content);
				
				file.seek(0);
				LocalDateTime timePoint = LocalDateTime.now();
				file.writeBytes(timePoint.format(DateTimeFormatter.ofPattern("'['yyyy'/'MM'/'dd' 'HH':'mm':'ss'] '")));
				file.writeBytes(message);
				file.writeBytes("\r\n");
				file.write(content);
				
				file.getChannel().truncate(maximumFileSize);
				file.close();
				
				return true;
			}
			catch (Exception e) {
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Write message to system ERR output.
	 * @param textName
	 * @param parameters
	 */
	public static void err(String textName, Object ... parameters) {
		try {
			
			String message = Resources.getString(textName);
			if (parameters.length > 0) {
				message = String.format(message, parameters);
			}
			System.err.println(message);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Output string
	 * @param input
	 */
	public static void out(Object input) {
		try {
			
			System.err.println(input.toString());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Format time.
	 * @param time
	 * @return
	 */
	public static String formatTime(long time) {
		
		try {
			Timestamp timeStamp = new Timestamp(time);
			String timeString = timeStamp.toString();
			return timeString;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Converts byte array to primitive type
	 * @param bytes
	 */
	public static byte [] toPrimitives(Byte[] bytes) {
		
		try {
			byte [] returned = new byte [bytes.length];
			
			for (int index = 0; index < bytes.length; index++) {
				returned[index] = bytes[index];
			}
			
			return returned;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Throw no operation exception.
	 * @throws IOException
	 */
	public static void throwIoOperationException() throws IOException {
			
		throw new IOException("I/O operation not supported!");
	}
	
	/**
	 * Creates new exception with given message
	 * @param messageResourceId
	 * @param parameters
	 * @return
	 */
	public static Exception newException(String messageResourceId, Object ... parameters) {
		
		try {
			String message = Resources.getString(messageResourceId);
			message = String.format(message, parameters);
			return new Exception(message);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Throws exception with given message
	 * @param messageResourceId
	 * @param error 
	 */
	public static void throwException(String messageResourceId)
			throws Exception {
		
		String message = Resources.getString(messageResourceId);
		throw new Exception(message);
	}
	
	/**
	 * Throws exception with given message
	 * @param messageResourceId
	 * @param parameters 
	 */
	public static void throwException(String messageResourceId, Object ... parameters)
			throws Exception {
		
		throw newException(messageResourceId, parameters);
	}
	
	/**
	 * Throw HTTP exception with exception body and parameters
	 * @param messageResourceId
	 * @param exceptionBody
	 * @param parameters
	 * @throws Exception
	 */
	public static void throwHttpException(String messageResourceId, String exceptionBody, Object ... parameters)
			throws Exception {
		
		String message = Resources.getString(messageResourceId);
		message = String.format(message, parameters);
		HttpException httpException = new HttpException(message, exceptionBody);
		throw httpException;
	}
	
	/**
	 * Returns true value if the input file has enabled extension
	 * @param file
	 * @param enabledExtensions
	 * @return
	 */
	public static boolean isFileExtension(File file, String[] enabledExtensions) {
		
		try {
			String fileName = file.getName();
			int dotPosition = fileName.lastIndexOf('.');
			if (dotPosition == -1) {
				return false;
			}
			
			try {
				String extension = fileName.substring(dotPosition + 1, fileName.length());
				for (String enabledExtension : enabledExtensions) {
					
					if (extension.equals(enabledExtension)) {
						return true;
					}
				}
			}
			catch (Exception e) {
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Get recognized available encodings
	 * @param textFile
	 */
	public static CharsetMatch [] getAvailableEncodingsFor(File textFile) {
		
		try {
			// Check file.
			if (!textFile.exists()) {
				return null;
			}
			
			CharsetMatch [] possibleEncodings = null;
			
			BufferedInputStream inputStream = null;
			
			try {
				// Create file input stream
				inputStream = new BufferedInputStream(new FileInputStream(textFile));
				
				// Detect encoding
				CharsetDetector detector = new CharsetDetector();
				detector.setText(inputStream);
				possibleEncodings = detector.detectAll();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					}
					catch (IOException e) {
					}
				}
			}
			
			if (possibleEncodings == null) {
				possibleEncodings = new CharsetMatch [] {};
			}
			
			return possibleEncodings;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	
	/**
	 * Recognize encoding of text file
	 * @param textFile
	 */
	public static String getTextEncoding(File textFile) {
		
		try {
			String encoding = "";
			BufferedInputStream inputStream = null;
			
			try {
				// Create file input stream
				inputStream = new BufferedInputStream(new FileInputStream(textFile));
				
				// Detect encoding
				CharsetDetector detector = new CharsetDetector();
				detector.setText(inputStream);
				CharsetMatch charset = detector.detect();
				
				encoding = charset.getName();
			}
			catch (Exception e) {
			}
			finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					}
					catch (IOException e) {
					}
				}
			}
			
			return encoding;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Add new menu Swing item.
	 * @param menu
	 * @param textResource
	 * @param iconResource
	 * @param listener
	 */
	public static void addMenuItem(JMenu menu, String textResource, String iconResource, ActionListener listener) {
		try {
			
			// Load text.
			String text = "";
			if (textResource != null && !textResource.isEmpty()) {
				text = Resources.getString(textResource);
				if (text == null) {
					text = "";
				}
			}
			
			// Create menu item.
			JMenuItem item = new JMenuItem(text);
			
			// Load icon.
			if (iconResource != null && !iconResource.isEmpty()) {
				Icon icon = Images.getIcon(iconResource);
				if (icon != null) {
					item.setIcon(icon);
				}
			}
			
			// Add action.
			item.addActionListener(listener);
			
			// Attach item to menu.
			menu.add(item);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
	}
	

	/**
	 * Add new menu item.
	 * @param menu
	 * @param textResource
	 * @param iconResource
	 * @param listener
	 */
	public static void addPopupMenuItem(PopupMenu menu, String textResource, ActionListener listener) {
		try {
			
			// Load text.
			String text = "";
			if (textResource != null && !textResource.isEmpty()) {
				text = Resources.getString(textResource);
				if (text == null) {
					text = "";
				}
			}
			
			// Create menu item.
			MenuItem item = new MenuItem(text);
			
			// Add action.
			item.addActionListener(listener);
			
			// Attach item to menu.
			menu.add(item);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Add sub menu item.
	 * @param subMenu
	 * @param textResource
	 * @param listener
	 */
	public static void addSubMenu(Menu subMenu, String textResource, ActionListener listener) {
		try {
			
			// Load text.
			String text = "";
			if (textResource != null && !textResource.isEmpty()) {
				text = Resources.getString(textResource);
				if (text == null) {
					text = "";
				}
			}
			
			// Create menu item.
			MenuItem item = new MenuItem(text);
			
			// Add action.
			item.addActionListener(listener);
			
			// Attach item to menu.
			subMenu.add(item);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get user folder.
	 * @return
	 */
	public static String getUserFolder() {
		
		try {
			// Get user directory
			String userProfile = System.getenv("LOCALAPPDATA");
			if (userProfile.isEmpty()) {
				userProfile = System.getProperty("user.home", "");
			}
			return userProfile;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Put new combo box item.
	 * @param comboBox
	 * @param item
	 */
	public static void putComboBoxItem(JComboBox comboBox, Object item) {
		try {
			
			for (int index = 0; index < comboBox.getItemCount(); index++) {
				Object listItem = comboBox.getItemAt(index);
				if (listItem.equals(item)) {
					return;
				}
			}
			comboBox.addItem(item);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Check if content of input list is equal.
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static boolean contentEquals(LinkedList list1, LinkedList list2) {
		
		try {
			if (list1 == null || list2 == null) {
				return false;
			}
			
			int length = list1.size();
			
			if (length != list2.size()) {
				return false;
			}
			
			for (int index = 0; index < length; index++) {
				if (list1.get(index) != list2.get(index)) {
					return false;
				}
			}
			
			return true;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Returns living URL from text or null if text is not an URL.
	 * @param text
	 * @return
	 */
	@SuppressWarnings("unused")
	public static URL tryUrl(String text)
		throws MissingUrlProtocolException, MalformedURLException, IOException {
		
		if (text == null || text.isEmpty()) {
			return null;
		}
		
		// Check protocol.
		final String protocols [] = { "http://", "https://" };
		boolean isProtocol = false;
		
		for (String protocol : protocols) {
			
			if (text.startsWith(protocol)) {
				isProtocol = true;
				break;
			}
		}
		
		if (!isProtocol) {
			throw new MissingUrlProtocolException(
					Resources.getString("org.multipage.gui.messageMissingUrlProtocol"));
		}
		
		MalformedURLException malformedUrlException = null;
		
		try {
			URL url = new URL(text);
			HttpURLConnection connection = null;
			IOException ioException = null;
			try {
				connection = (HttpURLConnection) url.openConnection();
				connection.connect();
				return url;
			}
			catch (IOException e) {
				ioException = e;
			}
			finally {
				if (connection != null) {
					connection.disconnect();
				}
			}
			if (ioException != null) {
				throw ioException;
			}
			return null;
		}
		catch (MalformedURLException e) {
			throw e;
		}
	}
	
	/**
	 * Get combo box text.
	 * @param comboBox
	 * @return
	 */
	public static String getComboBoxText(JComboBox comboBox) {
		
		try {
			Component component = comboBox.getEditor().getEditorComponent();
			if (component instanceof JTextComponent) {
				
				JTextComponent textComponent = (JTextComponent) component;
				String text = textComponent.getText();
				
				return text;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	
	/**
	 * set combo box text.
	 * @param comboBox
	 * @param text
	 * @return
	 */
	public static void setComboBoxText(JComboBox comboBox, String text) {
		try {
			
			Component component = comboBox.getEditor().getEditorComponent();
			if (component instanceof JTextComponent) {
				
				JTextComponent textComponent = (JTextComponent) component;
				textComponent.setText(text);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get combo box items array.
	 * @param comboBox
	 * @return
	 */
	public static Object [] getComboBoxItemsArray(JComboBox comboBox) {
		
		try {
			final int count = comboBox.getItemCount();
			Object items[]  = new String[count];
			Object item;
			
			for (int index = 0; index < count ; index++) {
				
				item = comboBox.getItemAt(index);
				items[index] = item;
			}
			
			return items;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Load combo box items array.
	 * @param comboBox
	 * @param items
	 * @param enableEmptyItem
	 */
	public static void loadComboBoxItemsArray(JComboBox comboBox, Object[] items, boolean enableEmptyItem) {
		try {
			
			for (Object item : items) {
				
				if (item == null || item.toString().isEmpty()) {
					if (enableEmptyItem) {
						putComboBoxItem(comboBox, "");
					}
					continue;
				}
				
				putComboBoxItem(comboBox, item);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Save stream data to file.
	 * @param inputStream
	 * @param dataLength
	 * @param outputFile
	 */
	public static void saveStreamToFile(InputStream inputStream, int dataLength, File outputFile)
		throws Exception {
		
		final int bufferLength = 65536;
		
		if (dataLength <= 0) {
			return;
		}
		
		BufferedOutputStream outputStream = null;
		Exception exception = null;
		
		byte bytes [] = new byte [bufferLength];
		int bytesWritten = 0;
		int bytesToWrite = 0;
		int remaining = 0;
		int length = 0;
		
		try {
			outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
			
			do {
				remaining = dataLength - bytesWritten;
				length = remaining < bufferLength ? remaining : bufferLength;
				bytesToWrite = inputStream.read(bytes, 0, length);
				
				if (bytesToWrite == -1) {
					break;
				}
				
				outputStream.write(bytes, 0, bytesToWrite);
				bytesWritten += bytesToWrite;
			}
			while (bytesWritten < dataLength);
		}
		catch (Exception e) {
			exception = e;
		}
		finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
		
		if (exception != null) {
			throw exception;
		}
	}
	
	/**
	 * Replace empty lines in the text with a replacement text.
	 * @param text
	 * @param replacement
	 * @return
	 */
	public static String replaceEmptyLines(String text, String replacement) {
		
		try {
			Matcher matcher = emptyLineRegexPattern.matcher(text);
			text = matcher.replaceAll(replacement);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return text;
	}

	/**
	 * Connect via HTTP and get resulting text
	 * @param uri
	 * @param method
	 * @param connectionTimeoutMs
	 * @param headers
	 * @return
	 */
	public static InputStream getHttpStream(String uriText, long connectionTimeoutMs, Obj<HttpResponse<InputStream>> response, String ... headers)
				throws Exception {
		
		// Create URI object
		URI uri = new URI(uriText);
		
		// Create new HTTP client
		HttpClient httpClient = HttpClient.newBuilder()
				.build();
		
		// Build the client request
		HttpRequest request = HttpRequest.newBuilder(uri)
				.GET()
				.headers(headers)
				.timeout(Duration.ofMillis(connectionTimeoutMs))
				.build();
		
		// Send request and get HTTP response
		response.ref = httpClient.send(request, BodyHandlers.ofInputStream());
		
		// Get response text
		InputStream responseInputStream = response.ref.body();
		if (responseInputStream == null) {
			Utility.throwException("org.multipage.gui.messageHttpCannotGetStream");
		}
		
		// Return the input stream
		return responseInputStream;
	}
	
	/**
	 * Read string from the input stream
	 * @param inputStream
	 * @return
	 * @throws IOException 
	 */
	public static String readString(InputStream inputStream, Charset charset)
			throws Exception {
		
		// Read all bytes
		byte [] allBytes = inputStream.readAllBytes();
		
		// Convert the bytes to an output string and return it
		String wholeText = new String(allBytes, charset);
		return wholeText;
	}
	
	/**
	 * Close input stream
	 * @param inputStream
	 */
	public static void close(InputStream inputStream) {
		
		// Check the parameter
		if (inputStream == null) {
			return;
		}
		try {
			inputStream.close();
		}
		catch (Exception e) {
		}
	}
	
	/**
	 * Remove last punctuation from the input line
	 * @param line
	 * @return
	 */
	public static String removeLastPunctuation(String line) {
		
		try {
			line = line.replaceAll("[\\.\\!\\?]$", "");
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return line;
	}
	
	/**
	 * Color all HTML texts that matches regular exception
	 * @param text
	 * @param textRegex
	 * @param color
	 * @return
	 */
	public static String colorHtmlTexts(String text, Pattern textRegex, String groupName, Color color) {
		
		try {
			// Get HTML color
			final String cssColor = Utility.getCssColor(color);
			
			// Do loop for all matches and create output string with replacements
			Matcher matcher = textRegex.matcher(text);
			StringBuffer output = new StringBuffer();
			
			while (matcher.find()) {
				
				try {
					// Get text part
					String textPart = matcher.group(groupName);
					if (textPart != null) {
						
						// Replace the text part with colored one
						String coloredTextPart = String.format("<font color=\"%s\">%s</font>", cssColor, textPart);
						matcher.appendReplacement(output, coloredTextPart);
					}
				}
				catch (Exception e) {
				}
			}
			
			// Finalize the output
			matcher.appendTail(output);
			
			return output.toString();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return text;
	}
	
	/**
	 * Traverse tree elements
	 * @param tree - input tree
	 * @param callbackFunction - callback lambda function
	 */
	public static void traverseElements(JTree tree, Function<Object, Function<DefaultMutableTreeNode, Function<DefaultMutableTreeNode, Boolean>>> callbackFunctions) {
		try {
			
			// Recursive function
			class Helper {
				
				boolean consume(TreeNode node, TreeNode parent) {
					
					try {
						// Check the node type
						if (node instanceof DefaultMutableTreeNode) {
							
							// Call input consumer for the userObject and its parent node
							DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
							DefaultMutableTreeNode parentNode = null;
							
							if (parent instanceof DefaultMutableTreeNode) {
								parentNode = (DefaultMutableTreeNode) parent;
							}
							
							Object userObject = treeNode.getUserObject();
							boolean stop = callbackFunctions.apply(userObject).apply(treeNode).apply(parentNode);
							return stop;
						}
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return false;
				}
				
				void traverseRecursively(TreeNode parentNode) {
					try {
						
						// Enumerate children
						Enumeration<? extends TreeNode> childrenEnumerator = parentNode.children();
						while (childrenEnumerator.hasMoreElements()) {
							
							// Consume the child node
							TreeNode child = childrenEnumerator.nextElement();
							boolean stop = consume(child, parentNode);
							
							// Do recursion for the child node
							if (!stop) {
								traverseRecursively(child);
							}
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				};
			};
			
			// Get the root node
			Object rootObject = tree.getModel().getRoot();
			if (rootObject instanceof TreeNode) {
				Helper helper = new Helper();
				
				// Consume the root node and traverse the tree recursively from the root node
				TreeNode rootNode = (TreeNode) rootObject;
				helper.consume(rootNode, null);
				helper.traverseRecursively(rootNode);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Decode Base 64 text.
	 * @param base64String
	 * @return
	 */
	public static byte[] decodeBase64(String base64String) {
		
		try {
			byte[] decodedBytes = Base64.getDecoder().decode(base64String);
			return decodedBytes;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Encode Base 64 text.
	 * @param string
	 * @return
	 */
	public static String encodeBase64(String string) {
		
		try {
			String base64String = Base64.getEncoder().encodeToString(string.getBytes());
			return base64String;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Helper function that creates a hash set from variable arguments.
	 * @param setItems
	 * @return
	 */
	public static<T> HashSet<T> makeSet(T ... setItems) {
		
		try {
			HashSet<T> theSet = new HashSet<T>();
			
			// Fill the set with input items.
			for (T item : setItems) {
				theSet.add(item);
			}
			
			return theSet;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Get formated date/time string for current date and time.
	 * @param format
	 * @return
	 */
	public static String getNowText(String format) {
		
		try {
			DateFormat dateFormat = new SimpleDateFormat(format);
			Calendar calendar = Calendar.getInstance();
			
			String nowText = dateFormat.format(calendar.getTimeInMillis());
			return nowText;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get current time.
	 * @param format
	 * @return
	 */
	public static long getNow() {
		
		try {
			Calendar calendar = Calendar.getInstance();
			
			long now = calendar.getTimeInMillis();
			return now;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0L;
	}
	
	/**
	 * Check if two input values are equal.
	 * @param value1
	 * @param value2
	 * @return
	 */
	public static boolean equalsShallow(Object value1, Object value2) {
		
		try {
			if (value1 == null && value2 == null) {
				return true;
			}
			boolean success = false;
			if (value1 != null) {
				success = value1.equals(value2);
			}
			else if (value2 != null) {
				success = value2.equals(value1);
			}
			return success;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Make deep check of equivalence of two input objects.
	 * @param object1
	 * @param object2
	 * @return
	 */
	public static boolean equalsDeep(Object object1, Object object2) {
		
		// Initialize.
		boolean equals = false;
		
		try {
			
			// Try to invoke method with same name on the first object.
			Method method = object1.getClass().getMethod("equalsDeep", Object.class);
			Object result = method.invoke(object2);
			
			// Check invoke result.
			if (result instanceof Boolean) {
				equals = (Boolean) result;
			}
		}
		catch (Exception e) {
			// Leave the "equals" flag unchanged.
		}

		return equals;
	}
	
	/**
	 * Clear table.
	 * @param table
	 */
	public static void clearTable(JTable table) {
		try {
			
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			model.setRowCount(0);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Sort table by column contents.
	 * @param table
	 * @param columnIndex
	 */
	public static void sortTable(JTable table, int columnIndex) {
		try {
			
			// Get column count.
			javax.swing.table.TableModel tableModel = table.getModel();
			int columnCount = tableModel.getColumnCount();
			
			// Get table sorter.
			RowSorter<? extends TableModel> sorter = table.getRowSorter();
			
			// Check column index.
			if (columnIndex < 0 && columnIndex >= columnCount) {
				sorter.setSortKeys(null);
				return;
			}
			
			// Switch sorter.
			try {
				
				// Get current sort order for the column
				List<? extends SortKey> sortKeys = sorter.getSortKeys();
				SortKey sortKey = sortKeys.get(columnIndex);
				SortOrder sortOrder = sortKey.getSortOrder();
				
				// Switch the sort order.
				SortOrder newSortOrder = sortOrder == SortOrder.ASCENDING ? SortOrder.DESCENDING : SortOrder.ASCENDING;
				
				// Create sort keys and set new sort order for the column.
				LinkedList<SortKey> newSortKeys = new LinkedList<SortKey>();
				newSortKeys.add(new RowSorter.SortKey(columnIndex, newSortOrder));
				sorter.setSortKeys(sortKeys);
			}
			catch (Exception e) {
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set table cell rendeder.
	 * @param table
	 * @param columnIndex
	 * @param parametersLambda
	 */
	public static void setTableCellRenderer(JTable table, int columnIndex, Function<Object, Function<Boolean, Function<Boolean, Function<Integer, Object>>>> parametersLambda) {
		try {
			
			// Get table column.
			TableColumnModel columnModel = table.getColumnModel();
			TableColumn column = columnModel.getColumn(columnIndex);
			
			// Set cell renderer.
			TableCellRenderer cellRenderer = new TableCellRenderer() {
				
				private RendererJTextPane renderer = new RendererJTextPane();
				
				{
					try {
						
						renderer.setContentType("text/html");
						renderer.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
						boolean hasFocus, int row, int column) {
					
					try {
						value = parametersLambda.apply(value)
								.apply(isSelected)
								.apply(hasFocus)
								.apply(row);
						
						renderer.set(isSelected, hasFocus, row);
						renderer.setText(value.toString());
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return renderer;
				}
				
			};
			
			column.setCellRenderer(cellRenderer);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get minimum integer value from input numbers.
	 * @param numbers
	 * @return
	 */
    public static int min(int... numbers) {
    	
    	try {
    		return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
    	}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return -1;
    }
    
    /**
     * Initialize computation of text and pattern distances.
     */
    public static void initTextDistanceComputation() {
    	try {
			
			// Create map of neighbours of a keyboard key.
			mapKeyNeighbours = new Hashtable<Character, List<Character>>();
			
			// Set keyboard width and height.
			keyboardHeight = keyboard.length;
			keyboardWidth = keyboard[0].length;
			
			// Go throug the keyboard.
			for (int i = 0; i < keyboardHeight; i++) {
				for (int j = 0; j < keyboardWidth; j++) {
					
					// Get text key.
					char textKey = keyboard[i][j][0];
					
					if (textKey == 0) {
						continue;
					}
					
					// Create empty neighbour list.
					LinkedList<Character> neighbours = new LinkedList<Character>();
					
					// Compute distances for neigbhour keys.
					for (int deltaI = -1; deltaI <= 1; deltaI++) {
						for (int deltaJ = -1; deltaJ <= 1; deltaJ++) {
							
							// Compute neighbour indices.
							Integer iNeigbourhood = i + deltaI;
							Integer jNeighbourhood = j + deltaJ;
							
							// Apply keyboard constraints.
							if (iNeigbourhood < 0 || iNeigbourhood >= keyboardHeight) {
								continue;
							}
							
							if (jNeighbourhood < 0 || jNeighbourhood >= keyboardWidth) {
								continue;
							}
							
							// Get pattern key.
							char patternKey = (char) keyboard[iNeigbourhood][jNeighbourhood][0];
							
							if (patternKey == 0) {
								continue;
							}
							
							// Add new neighbour key.
							neighbours.add(patternKey);
						}
					}
					
					// Map key to its neighbours.
					mapKeyNeighbours.put(textKey, neighbours);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
    }
	
	/**
	 * Get Levenshtein distance between input text and its pattern.
	 * Reference: https://web.stanford.edu/~jurafsky/slp3/slides/2_EditDistance.pdf.
	 * @param text
	 * @param pattern
	 * @return 
	 */
	public static int getLevenshteinDistance(String text, String pattern) {
		
		try {
			// Cost of substitution of character when typing an error on keyboard.
			BiFunction<Character, Character, Integer> costOfSubstitutionLambda = (textCharacter, patternCharacter) -> {
				
				// Check equality.
				if (textCharacter == patternCharacter) {
					return 0;
				}
				
				// Get neighbour characters.
				List<Character> neighbourCharacters = mapKeyNeighbours.get(textCharacter);
				if (neighbourCharacters == null) {
					return 2;
				}
				
				// Neighbour character distance.
				Integer neigbourDistance = neighbourCharacters.contains(patternCharacter) ? 1 : 2;
				return neigbourDistance;
			};
			
			// Main algorithm.
			int M = text.length();
			int N = pattern.length();
			int D[][] = new int[M + 1][N + 1];
			
			// Initialiation.
	        for(int i = 1;i <= M; i++)
	        {
	            D[i][0] = i;        
	        }
	        for(int j = 1; j <= N; j++)
	        {
	            D[0][j] = j;
	        }
	        
			// Recurrence relations.
			for (int j = 1; j <= N; j++) {
				for (int i = 1; i <= M; i++) {
					
					char Xi = text.charAt(i - 1);
					char Yj =  pattern.charAt(j - 1);
					
					// Base conditions.
					if (Xi == Yj) {
						D[i][j] = D[i - 1][j - 1];
					} 
					
					else {
						// Compute cost of substitution.
						int costOfSubstitution = costOfSubstitutionLambda.apply(Xi, Yj);
						
						// Compute subsequence distance.
						D[i][j] = min(
									D[i - 1][j] + 1,							// Deletion.
									D[i][j - 1] + 1,							// Insertion.
									D[i - 1][j - 1] + costOfSubstitution);		// Substitution.
					}
				}
			}
	
			return D[M][N];
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return -1;
	}
	
	/**
	 * Repeat character.
	 * @param c
	 * @param length
	 * @return
	 */
	public static String repeat(char c, int length) {
		
		try {
			StringBuilder string = new StringBuilder();
			
			while (length-- >= 0) {
				string.append(c);
			}
			
			return string.toString();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Compute output value using sigmoid function (logistic function).
	 * @param L - maximum value
	 * @param x0 - midpoint value
	 * @param k - grow rate
	 * @param x - input value
	 * @return
	 */
	public static double sigmoid(double L, double x0, double k, double x) {
		
		try {
			double y = L / ( 1 + Math.exp( -k * ( x - x0 ) ) );
			return y;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0.0;
	}
	
	/**
	 * Compute output value using invrerse sigmoid function.
	 * @param L - maximum value
	 * @param x0 - midpoint value
	 * @param k - grow rate
	 * @param x - input value
	 * @return
	 */
	public static double invereseSigmoid(double L, double x0, double k, double x) {
		
		try {
			if ( x < 0.0 ) {
				return Double.NaN;
			}
			
			double y = - Math.log( L / x - 1 ) / k + x0;
			return y;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0.0;
	}
	
	/**
	 * Normalize input value.
	 * @param value
	 * @param minimumValue
	 * @param maximumValue
	 * @return
	 */
	public static double normalize(double value, double minimumValue, double maximumValue) {
		
		try {
			double deltaValue = maximumValue - minimumValue;
			double normalValue = value - minimumValue / deltaValue;
			
			return normalValue;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0.0;
	}
	
	/**
	 * Write text with given encoding into the output stream.
	 * @param outputStream
	 * @param text
	 * @param charset
	 */
	public static void writeString(OutputStream outputStream, String text, Charset charset) 
			throws Exception {
		
		byte [] bytes = text.getBytes(charset);
		outputStream.write(bytes);
	}
	
	/**
	 * Write XML header to the output stream.
	 * @param outputStream
	 * @param charset
	 * @param newLine 
	 */
	public static void writeXmlHeader(OutputStream outputStream, Charset charset, boolean newLine)
			throws Exception {
		
		String xmlHeader = String.format(xmlHeaderTemplate, charset.toString());
		writeString(outputStream, xmlHeader, charset);
		
		if (newLine) {
			outputStream.write('\n');
		}
	}
	
	/**
	 * Check occurrence of input string in the input stream.
	 * @param inputStream
	 * @param text
	 * @param charset
	 * @param caseSensitive
	 */
	public static void checkString(InputStream inputStream, String text, Charset charset, boolean caseSensitive)
			throws Exception {
		
		// Convert input text to a byte array.
		byte [] bytesToFind = text.getBytes(charset);
		
		
		// Read bytes from the input stream. If there is any mismatch, throw an
		// exception.
		for (byte checkByte : bytesToFind) {
			byte readByte = (byte) inputStream.read();
			
			boolean mismatch = caseSensitive ? readByte != checkByte : Character.toUpperCase(readByte) != Character.toUpperCase(checkByte);
			if (mismatch) {
				Utility.throwException("org.multipage.gui.messageStringNotFoundInInputStream", text);
			}
		}
	}
	
	/**
	 * Dump byte buffer.
	 * @param buffer
	 * @return
	 */
    public static String dump(ByteBuffer buffer) {
    	
    	try {
	    	if (buffer == null) {
	    		return "null";
	    	}
	    	
	        ByteBuffer protectedBuffer = buffer.asReadOnlyBuffer();
	        byte[] bytes = new byte[protectedBuffer.capacity()];
	        protectedBuffer.rewind();
	        protectedBuffer.get(bytes);
	        
	        String text = Arrays.toString(bytes);
	        return text;
	    }
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
    }
	
	/**
	 * Copy source directory to a target JAR file system.
	 * @param sourcePath
	 * @param destinationFoldersPath
	 * @param destinationFileSystem 
	 * @throws Exception
	 */
	public static void copyDirToJar(Path sourcePath, String [] destinationFoldersPath, FileSystem destinationFileSystem)
			throws Exception {
		
		// Get destination file system path separator.
		String destinationSeparator = destinationFileSystem.getSeparator();
		
		// Compile destination path.
		Path destinationPath = destinationFileSystem.getPath(destinationSeparator, destinationFoldersPath);
		
		// Delegate the call.
		copyFromDirToJar(sourcePath, destinationPath, destinationFileSystem);
	}
	
	/**
	 * Copy source directory to a target JAR file system.
	 * @param sourcePath
	 * @param destinationPath
	 * @param destinationFileSystemo
	 */
	private static void copyFromDirToJar(Path sourcePath, Path destinationPath, FileSystem destinationFileSystem)
			throws Exception {
		
		// Create destination directory if it doesn't exist.
	    if (!Files.exists(destinationPath)) {
	    	Files.createDirectories(destinationPath);
	    }
	    
	    // If the source and destination paths designate files, copy the source
	    // file directly to the destination file.
	    if (Files.isRegularFile(sourcePath) && Files.isRegularFile(destinationPath)) {
	    	Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
	    }
	    
	    // List child source paths.
	    Obj<Exception> exception = new Obj<Exception>();
	    Files.list(sourcePath).forEachOrdered(sourceSubPath -> {
	    	try {
	    		Path fileOrFolder = sourceSubPath.getFileName();
	    		Path destinationSubPath = destinationFileSystem.getPath(destinationPath.toString(), fileOrFolder.toString());
	    		
	    		// Copy the directory or the file.
	    	    if (Files.isDirectory(sourceSubPath)) {
	    	        copyFromDirToJar(sourceSubPath, destinationSubPath, destinationFileSystem);
	    	    }
	    	    else {
	    			Files.copy(sourceSubPath, destinationSubPath, StandardCopyOption.REPLACE_EXISTING);
	    	    }
	    	}
	    	catch (Exception e) {
	    		exception.ref = e;
	    	}
	    });
	    
	    // Throw exception.
	    if (exception.ref != null) {
	    	throw exception.ref;
	    }
	}
	
	/**
	 * Copy JAR source directory to a target JAR file system.
	 * @param sourcePath
	 * @param destinationPath
	 * @param fileSystemLoader
	 * @throws IOException 
	 */
	public static void copyFromJarToJar(Path sourcePath, Path destinationPath, FileSystem destinationFileSystem)
			throws Exception {
		
		// Create destination directory if it doesn't exist.
	    if (!Files.exists(destinationPath)) {
	    	Files.createDirectories(destinationPath);
	    }
	    
	    // If the source and destination paths designate files, copy the source
	    // file directly to the destination file.
	    Obj<Exception> exception = new Obj<Exception>();
	    if (Files.isRegularFile(sourcePath) && Files.isRegularFile(destinationPath)) {
	    	Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
	    	return;
	    }
	    
	    // List child source paths.
	    Files.list(sourcePath).forEachOrdered(sourcePathEntry -> {
	    	
	    	try {
	    		Path fileOrFolder = sourcePathEntry.getFileName();
	    		Path newDestination = destinationFileSystem.getPath(destinationPath.toString(), fileOrFolder.toString());
	    		
	    		// Copy the directory or the file.
	    	    if (Files.isDirectory(sourcePathEntry)) {
	    	        copyFromDirToJar(sourcePathEntry, newDestination, destinationFileSystem);
	    	    }
	    	    else {
	    			Files.copy(sourcePathEntry, newDestination, StandardCopyOption.REPLACE_EXISTING);
	    	    }
	    	}
	    	catch (Exception e) {
	    		exception.ref = e;
	    	}
	    });
	    
	    // Throw exception.
	    if (exception.ref != null) {
	    	throw exception.ref;
	    }
	}

	/**
	 * Import directory classes from an application package to a JAR file.
	 * @param applicationPath
	 * @param thePackage
	 * @param jarFile
	 * @throws Exception 
	 */
	public static void importDirectoryClassesToJarFile(String applicationPath, Package thePackage, File jarFile)
			throws Exception {
		
		// Create uninitialized local variables.
		FileSystem destinationJarFileSystem = null;
		Exception exception = null;
		
		try {
		
			// Get destination JAR file system and a separator.
			final URI uri = URI.create("jar:file:/" + jarFile.toString().replace(File.separatorChar, '/'));
			final Map<String, String> environment = Map.of("create", "true");
			destinationJarFileSystem = FileSystems.newFileSystem(uri, environment);
			final String destinationSeparator = destinationJarFileSystem.getSeparator();
			
			// Get the package folders separated with file system separators.
			final String sourcePackageFolders = File.separator + thePackage.getName().replace(".", File.separator);
			final String [] destinationPackageFolders = thePackage.getName().split("\\.");
			
			// Get paths to the classes.
			final Path sourcePathInDirectory = Paths.get(applicationPath, sourcePackageFolders);
			
			// Copy source classes into the target JAR file.
			Utility.copyDirToJar(sourcePathInDirectory, destinationPackageFolders, destinationJarFileSystem);
			
			// Meta information folder name.
			final String metaInfFolderName = "META-INF";
			
			// Copy source META-INF folder to the target JAR archive.
			final String sourceMetaInfRoot = thePackage.getName().replace('.', '_') + "_" + metaInfFolderName;
			final Path sourceMetaInfPath = Paths.get(applicationPath, sourceMetaInfRoot);
			final String [] destinationMetaInfPath = new String [] {destinationSeparator, metaInfFolderName};
			
			Utility.copyDirToJar(sourceMetaInfPath, destinationMetaInfPath, destinationJarFileSystem);
		}
		catch (Exception e) {
			exception = e;
		}
		finally {
			// Close both file systems.
			try {
				if (destinationJarFileSystem != null) {
					destinationJarFileSystem.close();
				}
			}
			catch (Exception e) {
				if (exception == null) {
					exception = e;
				}
			}
		}
		
		// Throw exception.
		if (exception != null) {
			throw exception;
		}
	}
	
	/**
	 * Import JAR package to JAR file.
	 * @param sourceJarPath
	 * @param sourcePackage
	 * @param destinationJarFile
	 * @param destinationPackage
	 */
	public static void importJarPackageToJarFile(String sourceJarPath, Package sourcePackage, File destinationJarFile, Package destinationPackage)
			throws Exception {
		
		// Create uninitialized local variables.
		FileSystem sourceFileSystem = null;
		FileSystem destinationFileSystem = null;
		Exception exception = null;
		
		try {
			// Obtain file system of the application JAR.
			final String sourceUriString = "jar:file:/" + sourceJarPath.replace('\\', '/');
			final URI sourceUri = URI.create(sourceUriString);
			final Map<String, String> sourceEnvironment = Map.of("create", "true");
			sourceFileSystem = FileSystems.newFileSystem(sourceUri, sourceEnvironment);
			final String sourceSeparator = sourceFileSystem.getSeparator();
			
			// Obtain file system of the loader JAR.
			String destinationUriString = "jar:file:/" + destinationJarFile.getPath().replace('\\', '/');
			final URI destinationUri = URI.create(destinationUriString);
			final Map<String, String> destinationEnvironment = Map.of("create", "true");
			destinationFileSystem = FileSystems.newFileSystem(destinationUri, destinationEnvironment);
			final String destinationSeparator = destinationFileSystem.getSeparator();
			
			// Get source and destination paths.
			final Path sourcePath = sourceFileSystem.getPath(sourceSeparator, sourcePackage.getName().replace(".", sourceSeparator));
			final Path destinationPath = destinationFileSystem.getPath(destinationSeparator, destinationPackage.getName().replace(".", destinationSeparator));
			
			// Copy source classes to the destination path.
			Utility.copyFromJarToJar(sourcePath, destinationPath, destinationFileSystem);
			
			// Path to JAR meta information.
			final String metaInfFolderName = "META-INF";
			
			// Copy META-INF folder to the runnable JAR archive.
			final String sourceMetaInfRoot = sourcePackage.getName().replace('.', '_') + "_" + metaInfFolderName;
			final Path sourceMetaInfPath = sourceFileSystem.getPath(destinationSeparator, sourceMetaInfRoot);
			final Path destinationMetaInfPath = destinationFileSystem.getPath(destinationSeparator, metaInfFolderName);
			Utility.copyFromJarToJar(sourceMetaInfPath, destinationMetaInfPath, destinationFileSystem);
		}
		catch (Exception e) {
			exception = e;
		}
		finally {
			// Close both file systems.
			try {
				if (sourceFileSystem != null) {
					sourceFileSystem.close();
				}
			}
			catch (IOException e) {
				if (exception == null) {
					exception = e;
				}
			}
			try {
				if (destinationFileSystem != null) {
					destinationFileSystem.close();
				}
			}
			catch (IOException e) {
				if (exception == null) {
					exception = e;
				}
			}
		}
		
		// Throw exception.
		if (exception != null) {
			throw exception;
		}
	}
	
	/**
	 * Returns true if this application is zipped in JAR file.
	 * @return
	 */
	public static boolean isApplicationZipped() {
		
		try {
			// Get application path.
			URL applicationUrl = Utility.class.getProtectionDomain().getCodeSource().getLocation();
			String applicationPathName = applicationUrl.getPath();
			File fileOrFolder = new File(applicationPathName);
	
			// If the path is a file return true.
			if (fileOrFolder.exists() && fileOrFolder.isFile()) {
				return true;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		// Otherwise return false.
		return false;
	}
	
	/**
	 * Returns true if this application is zipped in JAR file.
	 * @return
	 */
	public static File getApplicationFile(Class<?> mainClass) {
		
		try {
			// Get application file.
			URL applicationUrl = mainClass.getProtectionDomain().getCodeSource().getLocation();
			String applicationPathName = applicationUrl.getPath();
			
			File applicationFile = new File(applicationPathName);
			
			// Otherwise return false.
			return applicationFile;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Get application path.
	 * @return
	 * @throws Exception 
	 */
	public static String getApplicationPath(Class<?> mainClass)
			throws Exception {
		
		try {
			// Get application file.
			URL applicationUrl = mainClass.getProtectionDomain().getCodeSource().getLocation();
			String applicationPath = new File(applicationUrl.getPath()).getCanonicalPath();
			return applicationPath;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Check occurrence of XML header in the input stream.
	 * @param inputStream
	 * @param charset
	 * @throws Exception
	 */
	public static void checkXmlHeader(InputStream inputStream, Charset charset)
			throws Exception {
		
		String xmlHeader = String.format(xmlHeaderTemplate, charset.toString());
		checkString(inputStream, xmlHeader, charset, false);
	}
	
	/**
	 * Get content of an application file.
	 * @param filePath
	 * @return
	 */
	public static byte [] getApplicationFileContent(String filePath) {
		
		try {
			byte [] content = null;
			
			URL fileUrl = ClassLoader.getSystemResource(filePath);
			if (fileUrl != null) {
				
				InputStream inputStream = null;
				try {
					inputStream = fileUrl.openStream();
					content = inputStream.readAllBytes();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					if (inputStream != null	) {
						try {
							inputStream.close();
						}
						catch (Exception e) {
						}
					}
				}
			}
			
			return content;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Get content of a disk file.
	 * @param filePath
	 * @return
	 */
	public static byte[] getFileContent(String filePath) {
		
		try {
			byte [] content = null;
			
			InputStream inputStream = null;
			try {
				filePath = filePath.replace('\\', '/');
				
				URL fileUrl = new URL("file://" + filePath);
				if (fileUrl != null) {
					inputStream = fileUrl.openStream();
					content = inputStream.readAllBytes();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				if (inputStream != null	) {
					try {
						inputStream.close();
					}
					catch (Exception e) {
					}
				}
			}
			
			return content;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Expose application file.
	 * @param fileFolder
	 * @param fileName
	 * @param outputDirectory	 
	 */
	public static File exposeApplicationFile(String fileFolder, String fileName, String outputDirectory)
			throws Exception {
		
		// Initialize field value.
		File exposedFile = null;
		
		BufferedInputStream inputStream = null;
		BufferedOutputStream outputStream = null;
		
		try {
			// Trim input strings.
			fileFolder = fileFolder.replace(File.separatorChar, '/');
			fileName = fileName.replace(File.separatorChar, '/');
			// Get keystore input stream. 
			URL fileUrl = ClassLoader.getSystemResource(fileFolder + '/' + fileName);
			
			Utility.show2("Exposed file %s", fileUrl);
			inputStream = new BufferedInputStream(fileUrl.openStream());
			
			// Get file.
			exposedFile = new File(outputDirectory + File.separatorChar + fileFolder.replace('/', File.separatorChar) +
								   File.separatorChar + fileName);
			exposedFile.getParentFile().mkdirs();
			
			// Create output stream.
			outputStream = new BufferedOutputStream(new FileOutputStream(exposedFile));
			
			// Write data to the temporary file.
			IOUtils.copy(inputStream, outputStream);
		}
		catch (Exception e) {
			
			show2("Expose %s error. Line no. %d", fileName, e.getStackTrace()[0].getLineNumber());
			throwException("org.multipage.gui.messageCannotExposeApplicationFile", e.getLocalizedMessage());
		}
		finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				}
				catch (Exception e) {
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				}
				catch (Exception e) {
				}
			}
		}
		
		return exposedFile;
	}

	/**
	 * Expose application PKCS12 keystore.
	 * @param parent
	 * @param keystoreFile
	 */
	public static File exposeApplicationKeystore(Component parent, String keystoreFile) {
		
		// Initialize field value.
		File tempKeystoreFile = null;
		
		BufferedInputStream inputStream = null;
		BufferedOutputStream outputStream = null;
		
		try {
			// Trim input string.
			keystoreFile = keystoreFile.replace(File.separatorChar, '/');
			// Get keystore input stream.
			URL certificateUrl = ClassLoader.getSystemResource(keystoreFile);
			inputStream = new BufferedInputStream(certificateUrl.openStream());
			
			// Get temporary file.
			String tempKeystorePath = Files.createTempFile("Multipage", "").toString();
			tempKeystoreFile = new File(tempKeystorePath);
			
			// Create output stream.
			outputStream = new BufferedOutputStream(new FileOutputStream(tempKeystorePath));
			
			// Write data to the temporary file.
			IOUtils.copy(inputStream, outputStream);
		}
		catch (Exception e) {
			show(parent, "org.multipage.gui.messageCannotExposeApplicationKeystore", e.getLocalizedMessage());
			// TODO: <---DEBUG Expose keystore exception.
			show2("Line no. %d", e.getStackTrace()[0].getLineNumber());
		}
		finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				}
				catch (Exception e) {
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				}
				catch (Exception e) {
				}
			}
		}
		
		return tempKeystoreFile;
	}
	
	/**
	 * Get content of an application certificate.
	 * @param certificatePath
	 * @return
	 */
	public static X509Certificate getApplicationCertificate(String certificatePath) {
		
		try {
			X509Certificate certificate = null;
			
			URL certificateUrl = ClassLoader.getSystemResource(certificatePath);
			if (certificateUrl != null) {
				
				InputStream inputStream = null;
				try {
					inputStream = certificateUrl.openStream();
					
					CertificateFactory factory = CertificateFactory.getInstance("X.509");
					certificate = (X509Certificate)factory.generateCertificate(inputStream);
				}
				catch (Exception e) {
					Safe.exception(e);
				}
				finally {
					if (inputStream != null	) {
						try {
							inputStream.close();
						}
						catch (Exception e) {
						}
					}
				}
			}
			
			return certificate;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Get content of certificate in disk file.
	 * @param certificatePath
	 * @return
	 */
	public static X509Certificate getFileCertificate(String certificatePath) {
		
		X509Certificate certificate = null;
		InputStream inputStream = null;
		
		try {
			certificatePath = certificatePath.replace('\\', '/');
			
			URL certificateUrl = new URL("file://" + certificatePath);
			if (certificateUrl != null) {
				inputStream = certificateUrl.openStream();
				
				CertificateFactory factory = CertificateFactory.getInstance("X.509");
				certificate = (X509Certificate)factory.generateCertificate(inputStream);
			}
		}
		catch (Exception e) {
			Safe.exception(e);
		}
		finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				}
				catch (Exception e) {
				}
			}
		}
		
		return certificate;
	}
	
	/**
	 * Get common name (the CN field) of X.509 certificate.
	 * @param certificate
	 * @return
	 */
	public static String getCommonName(X509Certificate certificate) {
		
		try {
			String textLine = certificate.getSubjectX500Principal().getName();
			Matcher matcher = x509CommonNameRegex.matcher(textLine);
			
			boolean found = matcher.find();
			int count = matcher.groupCount();
			
			// Use group count count.
			if (!found || count != 1) {
				return "";
			}
			String commonName = "CN=" + matcher.group("commonName");
			return commonName;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 *  When content of text field is changed, the method calls input lambda function.
	 * @param textField
	 * @param callbackLambda
	 */
	public static void onChangeText(JTextField textField, Consumer<String> callbackLambda) {
		try {
			
			final String alreadySet = "set";
			
			// If the component is already set, exit the method.
			String componentFlag = textField.getName();
			if (alreadySet.equals(componentFlag)) {
				return;
			}
			
			// Get text field document and create new document listener.
			Document document = textField.getDocument();
			DocumentListener listener = new DocumentListener() {
				@Override
				public void removeUpdate(DocumentEvent e) {
					callbackLambda.accept(textField.getText());
				}
				@Override
				public void insertUpdate(DocumentEvent e) {
					callbackLambda.accept(textField.getText());
				}
				@Override
				public void changedUpdate(DocumentEvent e) {
					callbackLambda.accept(textField.getText());
				}
			};
			
			// Add listener and set component flag.
			document.addDocumentListener(listener);
			textField.setName(alreadySet);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set table cell editor font. 
	 * @param table
	 * @param font
	 */
    public static void setCellEditorFont(JTable table, Font font) {
    	try {
			
			DefaultCellEditor editor = (DefaultCellEditor) table.getDefaultEditor(Object.class);
	    	Component component = editor.getComponent();
	    	component.setFont(font);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
    }
	
	/**
	 * Read bytes from input array until the terminal symbol is found. If the terminal is not found set
     * the "terminated" flag to false.
	 * @param inputBytes
	 * @param outputBuffer
	 * @param bufferIncrease
	 * @param terminalSymbol
	 * @param successfullyTerminated
	 */
	public static ByteBuffer readUntil(byte [] inputBytes, Obj<ByteBuffer> outputBuffer, int bufferIncrease, byte[] terminalSymbol,
			Obj<Boolean> successfullyTerminated) {
		
		try {
			// Initialization.
			int terminalLength = terminalSymbol.length;
			int terminalIndex = 0;
			int inputLength = inputBytes.length;
	
			// Do loop.
			boolean terminateIt = false;
			for (int index = 0; index < inputLength; index++) {
				
				// Read current byte from the buffer.
				byte theByte = inputBytes[index];
				
				// TODO: <---TEST Display input byte.
				String className = Thread.currentThread().getStackTrace()[2].getClassName();
				if ("org.maclan.server.XdebugListenerSession".equals(className)) {
					if (theByte != 0) {
						System.out.format("|%c", (char) theByte);
					}
					else {
						System.out.format("|%c\n", (char) 0x2588);
					}
				}
				
				// Try to match bytes with the terminal symbol.
				if (theByte == terminalSymbol[terminalIndex]) {
					terminalIndex++;
					if (terminalIndex >= terminalLength) {
						terminateIt = true;
					}
				}
				else {
					terminalIndex = 0;
				}
				
				// Check buffer capacity.
				int limit = outputBuffer.ref.limit();
				int capacity = outputBuffer.ref.capacity();
				if (!outputBuffer.ref.hasRemaining() && limit >= capacity) {
					
					// Increase buffer capacity.
					int increasedCapacity = outputBuffer.ref.capacity() + bufferIncrease;
					ByteBuffer increasedOutputBuffer = ByteBuffer.allocate(increasedCapacity);
					
					outputBuffer.ref.flip();
					
					increasedOutputBuffer.put(outputBuffer.ref);
					outputBuffer.ref = increasedOutputBuffer;
				}
				
				// Output current byte.
				outputBuffer.ref.put(theByte);
				
				// Terminate the loop.
				if (terminateIt) {
					break;
				}
			}
			
			// If the termnal symbol was not found, set the output flag to false value.
			successfullyTerminated.ref = terminalIndex >= terminalLength;
			
			// Return the output buffer.
			return outputBuffer.ref;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Write MSB integer value to stream.
	 * @param stream
	 */
	public static void writeMsbInteger(OutputStream stream, int intValue) 
			throws Exception {
		
		for (int index = 3; index >= 0; index--) {
			
			// Shift bytes to get the resulting byte.
			byte theByte = (byte) (intValue >>> (index * 8));
			stream.write(theByte);
		}
	}
	
	/**
	 * Split input bytes to the array of strings.
	 * @param inputBytes
	 * @param dividerSymbol
	 * @param terminalSymbol
	 * @return
	 * @throws Exception 
	 */
	public static List<String> splitBytesToStrings(byte[] inputBytes, int bufferIncrease, byte [] dividerSymbol, byte [] terminalSymbol)
			throws Exception {
		
		LinkedList<String> stringList = new LinkedList<String>();
		
		int dividerIndex = 0;
		int dividerLastIndex = dividerSymbol.length - 1;
		
		int terminalIndex = 0;
		int terminalLastIndex = terminalSymbol.length - 1;
		
		// Create byte array.
		ArrayList<Byte> byteArray = new ArrayList<Byte>();
		
		for (byte inputByte : inputBytes) {
			
			// Exit the loop.
			if (inputByte == terminalSymbol[terminalIndex]) {
				if (terminalIndex >= terminalLastIndex) {
					break;
				}
				terminalIndex++;
			}
			else {
				terminalIndex = 0;
			}
			
			// Check divider index.
			if (inputByte == dividerSymbol[dividerIndex]) {
				if (dividerIndex >= dividerLastIndex) {
					
					int length = byteArray.size();
					byte [] bytes = new byte[length];
					
					for (int index = 0; index < length; index++) {
						bytes[index] = byteArray.get(index);
					}
					
					String listItem = new String(bytes, "UTF-8");
					stringList.add(listItem);

					byteArray.clear();

					dividerIndex = 0;
					continue;
				}
				
				dividerIndex++;
				continue;
			}

			// Add byte to output buffer.
			byteArray.add(inputByte);
		}
				
		return stringList;
	}
	
	/**
	 * Pretty print of byte array.
	 * @param bytes
	 * @return
	 */
	public static String prettyPrint(byte [] bytes) {
		
		try {
			String bytesString = "[";
			String divider = "";
			for (int index = 0; index < bytes.length; index++) {
				bytesString += String.format("%s%02X", divider, bytes[index]);
				divider = ",";
			}
			bytesString += ']';
			return bytesString;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Insert character at given position in string.
	 * @param text
	 * @param position
	 * @param character
	 * @return
	 */
	public static String insertCharacter(String text, int position, char character) {
		
		try {
			int length = text.length();
			if (position < 0 || position > length) {
				return text;
			}
			
			text = text.substring(0, position) + character + text.substring(position, length);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return text;
	}
	
	/**
	 * Get current process and thread information.
	 * @param processId
	 * @param processName
	 * @param threadId
	 * @param threadName
	 */
	public static void getProcessAndThread(Obj<Long> processId, Obj<String> processName, Obj<Long> threadId,
			Obj<String> threadName) {
		try {
			
			// Get current process information.
			ProcessHandle currentProcess = ProcessHandle.current();
			
			if (processId != null) {
				processId.ref = currentProcess.pid();
			}
			if (processName != null) {
				processName.ref = currentProcess.info().toString();
			}
			
			// Get current thread information.
			Thread currentThread = Thread.currentThread();
			
			if (threadId != null) {
				threadId.ref = currentThread.getId();
			}
			if (threadName != null) {
				threadName.ref = currentThread.getName();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Disable GUI events on tree view.
	 * @param tree
	 */
	public static Object disableGuiEvents(JTree tree) {
		
		try {
			PropertyChangeListener [] listeners = tree.getPropertyChangeListeners();
			
			for (PropertyChangeListener listener : listeners) {
				tree.removePropertyChangeListener(listener);
			}
			return listeners;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Enable GUI events on tree view.
	 * @param tree
	 * @param listenersObject
	 */
	public static void enableGuiEvents(JTree tree, Object listenersObject) {
		try {
			
			if (!(listenersObject instanceof PropertyChangeListener [])) {
				return;
			}
			
			Safe.invokeLater(() -> {
				PropertyChangeListener [] listeners = (PropertyChangeListener []) listenersObject;
				
				for (PropertyChangeListener listener : listeners) {
					tree.addPropertyChangeListener(listener);
				}			
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Wrap text input event.
	 * @return
	 */
	public static DocumentListener createTextListener(Runnable textInputEvent) {
		
		try {
			DocumentListener listener = new DocumentListener() {
				@Override
				public void insertUpdate(DocumentEvent e) {
					try {
						
						textInputEvent.run();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				@Override
				public void removeUpdate(DocumentEvent e) {
					try {
						
						textInputEvent.run();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				@Override
				public void changedUpdate(DocumentEvent e) {
					try {
						
						textInputEvent.run();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
	        };
	        return listener;
	    }
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Enable text box input events.
	 * @param textField
	 * @param listener
	 */
	public static void setTextInputEvents(JTextField textField, DocumentListener listener) {
		try {
			
			// Listen for changes in the text field.
			Document document = textField.getDocument();
			document.removeDocumentListener(listener);
			document.addDocumentListener(listener);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get current screen height.
	 * @return
	 */
	public static Dimension getScreenSize()
			throws Exception {

		try {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			return screenSize;
		}
		catch (Throwable e) {
			Safe.exception(e);
			throw e;
		}
	}
	
	/**
	 * Display or hide frame.
	 * @param frame
	 * @param visible
	 */
	public static void displayFrame(JFrame frame, boolean visible) {
		try {
			
			if (frame == null) {
				return;
			}
			if (visible) {
				frame.setState(Frame.NORMAL);
				frame.toFront();
			}
			else {
				frame.setState(Frame.ICONIFIED);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Toggle frame visibility.
	 * @param frame
	 */
	public static void toggleFrame(JFrame frame) {
		try {
			
			int frameState = frame.getState();
			boolean isActive = frame.isActive();
			boolean isDiplayed = (frameState != Frame.ICONIFIED && isActive);
			displayFrame(frame, !isDiplayed);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}