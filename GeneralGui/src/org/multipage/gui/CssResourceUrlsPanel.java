/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.regex.Matcher;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;

import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Panel that displays resource URLs editor.
 * @author vakol
 *
 */
public class CssResourceUrlsPanel extends InsertPanel implements StringValueEditor {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Serialized dialog states.
	 */
	protected static Rectangle bounds;
	private static boolean boundsSet;
	
	/**
	 * Set default states.
	 */
	public static void setDefaultData() {
		
		bounds = new Rectangle(0, 0, 500, 330);
		boundsSet = false;
	}

	/**
	 * Load serialized states.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void serializeData(StateInputStream inputStream)
			throws IOException, ClassNotFoundException {
		
		bounds = Utility.readInputStreamObject(inputStream, Rectangle.class);
		boundsSet = true;
	}

	/**
	 * Save serialized states.
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream)
			throws IOException {
		
		outputStream.writeObject(bounds);
	}

	/**
	 * Initial string.
	 */
	private String initialString;

	/**
	 * Get resource name callback.
	 */
	private Callback getResourceName;

	/**
	 * Default list model.
	 */
	private DefaultListModel listModel;

	// $hide<<$
	
	/**
	 * Components.
	 */
	private JLabel labelResourcesNames;
	private JButton buttonGetResource;
	private JButton buttonRemove;
	private JScrollPane scrollPane;
	private JList<String> list;
	private JButton buttonAddName;

	/**
	 * Create the dialog.
	 * @param parentWindow 
	 */
	public CssResourceUrlsPanel(String initialString) {
		
		try {
			initComponents();
			
			// $hide>>$
			this.initialString = initialString;
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
		
		SpringLayout sl_panelMain = new SpringLayout();
		setLayout(sl_panelMain);
		
		labelResourcesNames = new JLabel("org.multipage.gui.textResourcesNames");
		sl_panelMain.putConstraint(SpringLayout.NORTH, labelResourcesNames, 22, SpringLayout.NORTH, this);
		sl_panelMain.putConstraint(SpringLayout.WEST, labelResourcesNames, 23, SpringLayout.WEST, this);
		add(labelResourcesNames);
		
		buttonGetResource = new JButton("");
		buttonGetResource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onFindResource();
			}
		});
		buttonGetResource.setPreferredSize(new Dimension(22, 22));
		buttonGetResource.setMargin(new Insets(0, 0, 0, 0));
		add(buttonGetResource);
		
		buttonRemove = new JButton("");
		sl_panelMain.putConstraint(SpringLayout.WEST, buttonRemove, 0, SpringLayout.WEST, buttonGetResource);
		buttonRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRemove();
			}
		});
		buttonRemove.setPreferredSize(new Dimension(22, 22));
		buttonRemove.setMargin(new Insets(0, 0, 0, 0));
		add(buttonRemove);
		
		scrollPane = new JScrollPane();
		sl_panelMain.putConstraint(SpringLayout.NORTH, buttonGetResource, 0, SpringLayout.NORTH, scrollPane);
		sl_panelMain.putConstraint(SpringLayout.WEST, buttonGetResource, 3, SpringLayout.EAST, scrollPane);
		sl_panelMain.putConstraint(SpringLayout.NORTH, scrollPane, 5, SpringLayout.SOUTH, labelResourcesNames);
		sl_panelMain.putConstraint(SpringLayout.WEST, scrollPane, 23, SpringLayout.WEST, this);
		sl_panelMain.putConstraint(SpringLayout.SOUTH, scrollPane, 185, SpringLayout.SOUTH, labelResourcesNames);
		sl_panelMain.putConstraint(SpringLayout.EAST, scrollPane, 277, SpringLayout.WEST, this);
		add(scrollPane);
		
		list = new JList();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(list);
		
		buttonAddName = new JButton("");
		buttonAddName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onAddName();
			}
		});
		sl_panelMain.putConstraint(SpringLayout.NORTH, buttonRemove, 3, SpringLayout.SOUTH, buttonAddName);
		sl_panelMain.putConstraint(SpringLayout.NORTH, buttonAddName, 3, SpringLayout.SOUTH, buttonGetResource);
		sl_panelMain.putConstraint(SpringLayout.WEST, buttonAddName, 0, SpringLayout.WEST, buttonGetResource);
		buttonAddName.setPreferredSize(new Dimension(22, 22));
		buttonAddName.setMargin(new Insets(0, 0, 0, 0));
		add(buttonAddName);
	}

	/**
	 * Save dialog.
	 */
	@Override
	public void saveDialog() {
		
		
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			localize();
			setIcons();
			setToolTips();
			initList();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Initialize list.
	 */
	private void initList() {
		try {
			
			// Create and set list model.
			listModel = new DefaultListModel();
			list.setModel(listModel);
			
			// Create and set renderer.
			list.setCellRenderer(new ListCellRenderer<String>() {
	
				// Renderer.
				RendererJLabel renderer = new RendererJLabel();
				
				// Constructor.
				{
					try {
						
						renderer.setOpaque(true);
						renderer.setIcon(Images.getIcon("org/multipage/gui/images/url.png"));
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				
				// Get renderer.
				@Override
				public Component getListCellRendererComponent(
						JList<? extends String> list, String value, int index,
						boolean isSelected, boolean cellHasFocus) {
					
					try {
						renderer.setText(value != null ? value : "");
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
	 * Get font specification.
	 * @return
	 */
	@Override
	public String getSpecification() {
		
		try {
			String specification = "";
			
			boolean isFirst = true;
			
			int size = listModel.getSize();
			for (int index = 0; index < size; index++) {
				
				String resourceName = (String) listModel.getElementAt(index);
				
				if (!isFirst) {
					specification += ", ";
				}
				specification += String.format("[@URL thisArea, res=\"#%s\"]", resourceName);
				
				isFirst = false;
			}
			
			return specification;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Set from initial string.
	 */
	private void setFromInitialString() {
		try {
			
			if (initialString != null) {
				listModel.clear();
				
				try {
					Obj<Integer> position = new Obj<Integer>(0);
					Obj<Matcher> outputMatcher = new Obj<Matcher>();
					
					while (true) {
						
						// Get resource name.
						String result = Utility.getNextMatch(initialString, position, "\\G\\s*,?\\s*\\[@URL thisArea, res=\"#([^\\\"]*)\"\\]", outputMatcher);
						if (result == null) {
							break;
						}
						
						String resourceName = outputMatcher.ref.group(1);
						if (!resourceName.isEmpty()) {
							
							listModel.addElement(resourceName);
						}
					}
				}
				catch (Exception e) {
				}
			}
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
			
			Utility.localize(labelResourcesNames);
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
			
			buttonGetResource.setIcon(Images.getIcon("org/multipage/gui/images/find_icon.png"));
			buttonAddName.setIcon(Images.getIcon("org/multipage/gui/images/insert.png"));
			buttonRemove.setIcon(Images.getIcon("org/multipage/gui/images/cancel_icon.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set tool tips.
	 */
	private void setToolTips() {
		try {
			
			buttonGetResource.setToolTipText(Resources.getString("org.multipage.gui.tooltipFindAreaResource"));
			buttonAddName.setToolTipText(Resources.getString("org.multipage.gui.tooltipAddResourceName"));
			buttonRemove.setToolTipText(Resources.getString("org.multipage.gui.tooltipRemovetResource"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set callback.
	 * @param callback
	 */
	public void setResourceNameCallback(Callback callback) {
		
		getResourceName = callback;
	}

	/**
	 * On find resource.
	 */
	protected void onFindResource() {
		try {
			
			if (getResourceName == null) {
				
				Utility.show(this, "org.multipage.gui.messageNoResourcesAssociated");
				return;
			}
			
			// Use callback to obtain resource name.
			Object outputValue = getResourceName.run(null);
			if (!(outputValue instanceof String)) {
				return;
			}
			
			String imageName = (String) outputValue;
			
			// Add image name into the list.
			listModel.addElement(imageName);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On add resource name.
	 */
	protected void onAddName() {
		try {
			
			String resourceName = Utility.input(this, "org.multipage.gui.messageInsertResourceName");
			if (resourceName == null || resourceName.isEmpty()) {
				return;
			}
			
			listModel.addElement(resourceName);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On remove.
	 */
	protected void onRemove() {
		try {
			
			// Remove selected resource name.
			String resourceName = list.getSelectedValue();
			if (resourceName == null) {
				
				Utility.show(this, "org.multipage.gui.messageSelectSingleResourceName");
				return;
			}
			
			if (!Utility.ask(this, "org.multipage.gui.messageRemoveResourceName", resourceName)) {
				return;
			}
			
			listModel.removeElement(resourceName);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/* (non-Javadoc)
	 * @see org.multipage.gui.InsertPanel#getWindowTitle()
	 */
	@Override
	public String getWindowTitle() {
		
		try {
			return Resources.getString("org.multipage.gui.textCssResourcesUrlsBuilder");
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/* (non-Javadoc)
	 * @see org.multipage.gui.InsertPanel#getResultText()
	 */
	@Override
	public String getResultText() {
		
		try {
			return getSpecification();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/* (non-Javadoc)
	 * @see org.multipage.gui.InsertPanel#getContainerDialogBounds()
	 */
	@Override
	public Rectangle getContainerDialogBounds() {
		
		return bounds;
	}

	/* (non-Javadoc)
	 * @see org.multipage.gui.InsertPanel#setContainerDialogBounds(java.awt.Rectangle)
	 */
	@Override
	public void setContainerDialogBounds(Rectangle bounds) {
		
		CssResourceUrlsPanel.bounds = bounds;
	}
	
	/* (non-Javadoc)
	 * @see org.multipage.gui.InsertPanel#boundsSet()
	 */
	@Override
	public boolean isBoundsSet() {

		return boundsSet;
	}
	
	/* (non-Javadoc)
	 * @see org.multipage.gui.InsertPanel#setBoundsSet(boolean)
	 */
	@Override
	public void setBoundsSet(boolean set) {
		
		boundsSet = set;
	}

	/**
	 * Get component.
	 */
	@Override
	public Component getComponent() {
		
		return this;
	}

	/**
	 * Get string value.
	 */
	@Override
	public String getStringValue() {
		
		try {
			return getSpecification();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Set string value.
	 */
	@Override
	public void setStringValue(String string) {
		try {
			
			initialString = string;
			setFromInitialString();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get value meaning.
	 */
	@Override
	public String getValueMeaning() {
		
		return meansCssUrlsResources;
	}
	
	/**
	 * Set controls grayed. If a false value is returned a high level editor grayed this panel.
	 */
	@Override
	public boolean setControlsGrayed(boolean isDefault) {
		
		return false;
	}
}
