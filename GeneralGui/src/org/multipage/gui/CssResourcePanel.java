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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Panel that displays CSS resource editor.
 * @author vakol
 *
 */
public class CssResourcePanel extends InsertPanel implements StringValueEditor {

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
	 * If this flag is true, this editor produces resource URL.
	 */
	private boolean produceUrl = false;

	/**
	 * Get resource name callback.
	 */
	private Callback getResourceName;
	
	// $hide<<$
	
	/**
	 * Components.
	 */
	private JLabel labelResourceName;
	private JTextField textField;
	private JButton buttonGetResource;
	private JButton buttonReset;

	/**
	 * Create the dialog.
	 * @param parentWindow 
	 */
	public CssResourcePanel(String initialString, boolean produceUrl) {
		
		try {
			initComponents();
			
			// $hide>>$
			this.initialString = initialString;
			this.produceUrl = produceUrl;
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
		
		labelResourceName = new JLabel("org.multipage.gui.textResourceName");
		sl_panelMain.putConstraint(SpringLayout.NORTH, labelResourceName, 50, SpringLayout.NORTH, this);
		sl_panelMain.putConstraint(SpringLayout.WEST, labelResourceName, 30, SpringLayout.WEST, this);
		add(labelResourceName);
		
		
		textField = new TextFieldEx();
		sl_panelMain.putConstraint(SpringLayout.NORTH, textField, -4, SpringLayout.NORTH, labelResourceName);
		sl_panelMain.putConstraint(SpringLayout.WEST, textField, 6, SpringLayout.EAST, labelResourceName);
		textField.setPreferredSize(new Dimension(6, 22));
		add(textField);
		textField.setColumns(20);
		
		buttonGetResource = new JButton("");
		buttonGetResource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onFindResource();
			}
		});
		sl_panelMain.putConstraint(SpringLayout.NORTH, buttonGetResource, 0, SpringLayout.NORTH, textField);
		sl_panelMain.putConstraint(SpringLayout.WEST, buttonGetResource, 3, SpringLayout.EAST, textField);
		buttonGetResource.setPreferredSize(new Dimension(22, 22));
		buttonGetResource.setMargin(new Insets(0, 0, 0, 0));
		add(buttonGetResource);
		
		buttonReset = new JButton("");
		buttonReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onReset();
			}
		});
		sl_panelMain.putConstraint(SpringLayout.NORTH, buttonReset, 0, SpringLayout.NORTH, textField);
		sl_panelMain.putConstraint(SpringLayout.WEST, buttonReset, 3, SpringLayout.EAST, buttonGetResource);
		buttonReset.setPreferredSize(new Dimension(22, 22));
		buttonReset.setMargin(new Insets(0, 0, 0, 0));
		add(buttonReset);
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
			String resourceName = textField.getText();
			String specification = produceUrl ? String.format("[@URL thisArea, res=\"#%s\"]", resourceName) : resourceName;
			
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
				
				if (produceUrl) {
					try {
						Obj<Integer> position = new Obj<Integer>(0);
			
						// Get resource name.
						String resourceName = getResourceName(position);
						if (resourceName == null) {
							return;
						}
						
						textField.setText(resourceName);
					}
					catch (Exception e) {
						
					}
				}
				else {
					textField.setText(initialString);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get resource name.
	 * @param position
	 * @return
	 */
	private String getResourceName(Obj<Integer> position) {
		
		try {
			String resourceName = null;
			
			// Get name start.
			String nameStart = Utility.getNextMatch(initialString, position, "res=\"#");
			if (nameStart != null) {
				
				// Get image name.
				resourceName = Utility.getNextMatch(initialString, position, "[^\\\"]*");
			}
			
			return resourceName;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Localize components.
	 */
	private void localize() {
		try {
			
			Utility.localize(labelResourceName);
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
			buttonReset.setIcon(Images.getIcon("org/multipage/gui/images/cancel_icon.png"));
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
			buttonReset.setToolTipText(Resources.getString("org.multipage.gui.tooltipResetResourceName"));
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
			
			// Set image name text control.
			textField.setText(imageName);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On reset.
	 */
	protected void onReset() {
		try {
			
			textField.setText("");
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
			return Resources.getString("org.multipage.gui.textCssResourceUrlBuilder");
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
		
		CssResourcePanel.bounds = bounds;
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
		
		try {
			return produceUrl ? meansCssUrlResource : meansCssResource;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Set controls grayed. If a false value is returned a high level editor grayed this panel.
	 */
	@Override
	public boolean setControlsGrayed(boolean isDefault) {
		
		return false;
	}
}
