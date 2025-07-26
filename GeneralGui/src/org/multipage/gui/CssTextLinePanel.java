/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2025-04-26
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

import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Panel that displays editor of text line.
 * @author vakol
 *
 */
public class CssTextLinePanel extends InsertPanel implements StringValueEditor {

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
	
	// $hide<<$
	
	/**
	 * Components.
	 */
	private JLabel labelText;
	private JTextField textField;
	private JButton buttonEscapeQuotes;
	private JButton buttonUnescapeQuotes;

	/**
	 * Create the dialog.
	 * @param parentWindow 
	 */
	public CssTextLinePanel(String initialString) {
		
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
		
		labelText = new JLabel("org.multipage.gui.textInsertText");
		sl_panelMain.putConstraint(SpringLayout.NORTH, labelText, 10, SpringLayout.NORTH, this);
		sl_panelMain.putConstraint(SpringLayout.WEST, labelText, 10, SpringLayout.WEST, this);
		add(labelText);
		
		
		textField = new TextFieldEx();
		sl_panelMain.putConstraint(SpringLayout.NORTH, textField, 6, SpringLayout.SOUTH, labelText);
		sl_panelMain.putConstraint(SpringLayout.WEST, textField, 10, SpringLayout.WEST, this);
		sl_panelMain.putConstraint(SpringLayout.EAST, textField, -10, SpringLayout.EAST, this);
		add(textField);
		textField.setColumns(10);
		
		buttonEscapeQuotes = new JButton("org.multipage.gui.textEscapeQuotes");
		sl_panelMain.putConstraint(SpringLayout.NORTH, buttonEscapeQuotes, 6, SpringLayout.SOUTH, textField);
		buttonEscapeQuotes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onEscapeQuotes();
			}
		});
		buttonEscapeQuotes.setMargin(new Insets(0, 0, 0, 0));
		buttonEscapeQuotes.setPreferredSize(new Dimension(100, 25));
		buttonEscapeQuotes.setSelected(true);
		add(buttonEscapeQuotes);
		
		buttonUnescapeQuotes = new JButton("org.multipage.gui.textUnescapeQuotes");
		buttonUnescapeQuotes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onUnescapeQuotes();
			}
		});
		sl_panelMain.putConstraint(SpringLayout.EAST, buttonEscapeQuotes, -6, SpringLayout.WEST, buttonUnescapeQuotes);
		sl_panelMain.putConstraint(SpringLayout.NORTH, buttonUnescapeQuotes, 6, SpringLayout.SOUTH, textField);
		sl_panelMain.putConstraint(SpringLayout.EAST, buttonUnescapeQuotes, 0, SpringLayout.EAST, textField);
		buttonUnescapeQuotes.setSelected(true);
		buttonUnescapeQuotes.setPreferredSize(new Dimension(100, 25));
		buttonUnescapeQuotes.setMargin(new Insets(0, 0, 0, 0));
		add(buttonUnescapeQuotes);
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
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On escape quotes.
	 */
	protected void onEscapeQuotes() {
		try {
			
			String text = textField.getText();
			text = text.replace("\"", "\\\"");
			
			textField.setText(text);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On unescape quotes.
	 */
	protected void onUnescapeQuotes() {
		try {
			
			String text = textField.getText();
			text = text.replace("\\\"", "\"");
			
			textField.setText(text);
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
			String specification = textField.getText();
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
				textField.setText(initialString);
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
			
			Utility.localize(labelText);
			Utility.localize(buttonEscapeQuotes);
			Utility.localize(buttonUnescapeQuotes);
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
			return Resources.getString("org.multipage.gui.textCssUrlBuilder");
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
		
		CssTextLinePanel.bounds = bounds;
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
		
		return meansCssTextLine;
	}
	
	/**
	 * Set controls grayed. If a false value is returned a high level editor grayed this panel.
	 */
	@Override
	public boolean setControlsGrayed(boolean isDefault) {
		
		return false;
	}
}
