/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.LinkedList;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.maclan.Area;
import org.maclan.MiddleResult;
import org.multipage.basic.ProgramBasic;
import org.multipage.gui.Images;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.TextPopupMenu;
import org.multipage.gui.TopMostButton;
import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.UIManager;

/**
 * Frame that displays area help.
 * @author vakol
 *
 */
public class AreaHelpViewer extends JFrame {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Window states.
	 */
	public static Rectangle bounds = new Rectangle(0, 0, 0, 0);
	public static int dividerLocation = 56;

	/**
	 * Load data.
	 * @param inputStream
	 */
	public static void serializeData(StateInputStream inputStream)
			throws IOException, ClassNotFoundException {
		
		Object object = inputStream.readObject();
		if (object instanceof Rectangle) {
			bounds = (Rectangle) object;
		}
		else {
			throw new ClassNotFoundException();
		}
		dividerLocation = inputStream.readInt();
	}

	/**
	 * Save data.
	 * @param outputStream
	 */
	public static void serializeData(StateOutputStream outputStream)
			throws IOException {
		
		outputStream.writeObject(bounds);
		outputStream.writeInt(dividerLocation);
	}

	// $hide<<$
	/**
	 * Components.
	 */
	private JScrollPane scrollPane;
	private JTextPane textPane;
	private JList listAreas;
	private JScrollPane scrollPaneAreas;
	private JSplitPane splitPane;
	private JPanel panelTop;
	
	/**
	 * Lunch dialog.
	 * @param component
	 * @param foundAreas 
	 * @return
	 */
	public static void showDialog(Component component, LinkedList<Area> foundAreas) {
		try {
			
			Window parentWindow = Utility.findWindow(component);
			AreaHelpViewer dialog = new AreaHelpViewer(parentWindow, foundAreas);
			dialog.setVisible(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Create the dialog.
	 * @param parentWindow 
	 * @param foundAreas 
	 */
	public AreaHelpViewer(Window parentWindow, LinkedList<Area> foundAreas) {
		
		try {
			// Initialize components.
			initComponents();
			// $hide>>$
			postCreate(foundAreas);
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
				onClose();
			}
		});
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("org.multipage.generator.textInfo");
		setBounds(100, 100, 276, 402);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		splitPane = new JSplitPane();
		splitPane.setOneTouchExpandable(true);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		getContentPane().add(splitPane, BorderLayout.CENTER);
		
		scrollPaneAreas = new JScrollPane();
		splitPane.setLeftComponent(scrollPaneAreas);
		scrollPaneAreas.setPreferredSize(new Dimension(2, 54));
		
		listAreas = new JList();
		listAreas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPaneAreas.setViewportView(listAreas);
		listAreas.setForeground(Color.BLACK);
		listAreas.setOpaque(true);
		listAreas.setBackground(UIManager.getColor("ToolTip.background"));
		listAreas.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		scrollPane = new JScrollPane();
		splitPane.setRightComponent(scrollPane);
		
		textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setBackground(UIManager.getColor("ToolTip.background"));
		textPane.setContentType("text/html;charset=UTF-8");
		scrollPane.setViewportView(textPane);
		
		panelTop = new JPanel();
		panelTop.setPreferredSize(new Dimension(10, 24));
		getContentPane().add(panelTop, BorderLayout.NORTH);
		panelTop.setLayout(new SpringLayout());
	}
	
	/**
	 * On close.
	 */
	protected void onClose() {
		try {
			saveDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
		
		dispose();
	}

	/**
	 * Load dialog.
	 */
	private void loadDialog() {
		try {
			
			if (bounds.width == 0 && bounds.height == 0) {
				// Center dialog.
				Utility.centerOnScreen(this);
			}
			else {
				setBounds(bounds);
			}
			
			splitPane.setDividerLocation(dividerLocation);
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
			dividerLocation = splitPane.getDividerLocation();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Post creation.
	 * @param foundAreas 
	 */
	private void postCreate(LinkedList<Area> foundAreas) {
		try {
			
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			// Add top most window toggle button.
			setAlwaysOnTop(true);
			TopMostButton.add(this, panelTop);
			// Load areas list.
			loadAreasList(foundAreas);
			// Load text.
			loadText(foundAreas.getFirst());
			// Set icon.
			setIconImage(Images.getImage("org/multipage/basic/images/main_icon.png"));
			// Load dialog.
			loadDialog();
			// Set popup trayMenu.
			new TextPopupMenu(textPane);
			
			// Select last area.
			int size = listAreas.getModel().getSize();
			if (size > 0) {
				listAreas.setSelectedIndex(size -1);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load areas list.
	 * @param foundAreas
	 */
	@SuppressWarnings({ "serial", "unchecked" })
	private void loadAreasList(LinkedList<Area> foundAreas) {
		try {
			
			final DefaultListModel<Area> model = new DefaultListModel<Area>();
			listAreas.setModel(model);
			
			listAreas.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					try {
						
						// On list selection.
						// Get selected area.
						int selectedIndex = listAreas.getSelectedIndex();
						Area selectedArea = model.get(selectedIndex);
						
						// Load text.
						loadText(selectedArea);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			
			for (Area foundArea : foundAreas) {
				model.addElement(foundArea);
			}
			
			// Set list renderer.
			listAreas.setCellRenderer(new DefaultListCellRenderer() {
				@Override
				public Component getListCellRendererComponent(JList<?> list,
						Object value, int index, boolean isSelected,
						boolean cellHasFocus) {
					
					try {
						if (value instanceof Area) {
							value = ((Area) value).getDescriptionForDiagram();
						}
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return super.getListCellRendererComponent(list, value, index, isSelected,
							cellHasFocus);
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
	}

	/**
	 * Load text.
	 * @param area
	 */
	private void loadText(Area area) {
		try {
			
			// Set title and label.
			String title = String.format(
					Resources.getString("org.multipage.generator.textInfo"),
					area.getDescriptionForced());
			setTitle(title);
			
			// Set content.
			Obj<String> helpText = new Obj<String>();
			MiddleResult result = ProgramBasic.getMiddle().loadHelp(
					ProgramBasic.getLoginProperties(), area, helpText);
			if (result.isNotOK()) {
				helpText.ref = result.getMessage();
			}
			
			textPane.setText(helpText.ref);
			
			Safe.invokeLater(() -> {
				scrollPane.getVerticalScrollBar().setValue(0);
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
