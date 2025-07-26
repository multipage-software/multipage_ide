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
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

import org.maclan.Area;
import org.maclan.AreaResource;
import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.maclan.Resource;
import org.multipage.basic.ProgramBasic;
import org.multipage.gui.GraphUtility;
import org.multipage.gui.Images;
import org.multipage.gui.Utility;
import org.multipage.util.Safe;

/**
 * Dialog that enables to select area text resource.
 * @author vakol
 *
 */
public class SelectAreaTextResources extends JDialog {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Dark color.
	 */
	private static final Color darkColor = new Color(250, 250, 210);

	/**
	 * Output resource reference.
	 */
	private Resource resource;

	/**
	 * Area reference.
	 */
	private Area area;
	
	// $hide<<$
	/**
	 * Components.
	 */
	private JLabel label;
	private JScrollPane scrollPane;
	private JList list;
	/**
	 * @wbp.nonvisual location=391,159
	 */
	private final JLabel labelNoResource = new JLabel("builder.textNoTextResource");
	
	/**
	 * Initialize components.
	 */
	private void initComponents() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("area");
		setBounds(100, 100, 252, 366);
		
		label = new JLabel("builder.textSelectTextResource");
		getContentPane().add(label, BorderLayout.NORTH);
		
		scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		list = new JList();
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				onSelected(e);
			}
		});
		scrollPane.setViewportView(list);
	}

	/**
	 * On resource selected.
	 * @param e
	 */
	protected void onSelected(MouseEvent e) {
		
		try {
			if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
				
				// Open text resource editor.
				resource = (Resource) list.getSelectedValue();
				dispose();
			}
		}
		catch (Throwable exc) {
			Safe.exception(exc);
			dispose();
		}
	}

	/**
	 * Lunch dialog.
	 * @param parent
	 * @param area 
	 * @return
	 */
	public static Resource showDialog(Component parent, Area area) {
		
		try {
			if (area == null) {
				return null;
			}
			SelectAreaTextResources dialog = new SelectAreaTextResources(parent, area);
			dialog.setVisible(true);
	
			return dialog.resource;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	/**
	 * Create the dialog.
	 * @param parent 
	 * @param area 
	 */
	public SelectAreaTextResources(Component parent, Area area) {
		super(Utility.findWindow(parent), ModalityType.DOCUMENT_MODAL);
		
		try {
			labelNoResource.setHorizontalAlignment(SwingConstants.CENTER);
			// Initialize components.
			initComponents();
			// $hide>>$
			this.area = area;
			postCreate();
			// $hide<<$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			localize();
			Utility.centerOnScreen(this);
			setTitle();
			setIcons();
			loadTextResources();
			setRenderer();
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
			
			labelNoResource.setIcon(Images.getIcon("org/multipage/generator/images/error.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set list renderer.
	 */
	private void setRenderer() {
		try {
			
			// Set renderer.
			list.setCellRenderer(new ListCellRenderer() {
				// Renderer.
				@SuppressWarnings("serial")
				class Renderer extends JLabel {
					
					// Parameters.
					private boolean isSelected;
					private boolean hasFocus;
					
					// Constructor.
					Renderer() {
						try {
							
							setIcon(Images.getIcon("org/multipage/generator/images/edit_resource.png"));
							setOpaque(true);
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
					
					// Set properties.
					void setProperties(String text, int index, boolean isSelected,
							boolean hasFocus) {
						try {
							
							setText(text);
							setBackground((index % 2 == 0) ? Color.WHITE : darkColor);
							this.isSelected = isSelected;
							this.hasFocus = hasFocus;
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
					
					// Paint label.
					@Override
					public void paint(Graphics g) {
						
						try {
							super.paint(g);
							GraphUtility.drawSelection(g, this, isSelected, hasFocus);
						}
						catch (Throwable e) {
							Safe.exception(e);
						}
					}
				}
				
				Renderer renderer = new Renderer();
				
				@Override
				public Component getListCellRendererComponent(JList list, Object value,
						int index, boolean isSelected, boolean cellHasFocus) {
					
					try {
						renderer.setProperties(value.toString(), index, isSelected, cellHasFocus);
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
	 * Set title.
	 */
	private void setTitle() {
		try {
			
			setTitle(area.toString());
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
			
			Utility.localize(label);
			Utility.localize(labelNoResource);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load text resources.
	 */
	private void loadTextResources() {
		try {
			
			// Load resources.
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			LinkedList<AreaResource> resources = new LinkedList<AreaResource>();
			MiddleResult result = middle.loadAreaResources(login, area, resources, null);
			
			// Report error.
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			// Load resources.
			DefaultListModel model = new DefaultListModel();
			
			for (AreaResource resource : resources) {
				if (resource.isSavedAsText()) {
					model.addElement(resource);
				}
			}
			
			if (!model.isEmpty()) {
				list.setModel(model);
			}
			else {
				getContentPane().remove(scrollPane);
				getContentPane().add(labelNoResource, BorderLayout.CENTER);
				getContentPane().remove(label);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}

