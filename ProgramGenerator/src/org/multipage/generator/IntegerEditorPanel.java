/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */
package org.multipage.generator;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.multipage.gui.StringValueEditor;
import org.multipage.gui.TextFieldEx;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Panel that displays editor for integer value.
 * @author vakol
 *
 */
public class IntegerEditorPanel extends JPanel implements SlotValueEditorPanelInterface {
	
	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Value
	 */
	private Long number = null;

	// $hide<<$
	/**
	 * Components.
	 */
	private JTextField textInteger;
	private JLabel labelMessage;

	/**
	 * Create the panel.
	 */
	public IntegerEditorPanel() {
		
		try {
			// Initialize components.
			initComponents();
			// $hide>>$
			// Post creation.
			postCreation();
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
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		textInteger = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textInteger, 16, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, textInteger, 0, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, textInteger, 0, SpringLayout.EAST, this);
		add(textInteger);
		textInteger.setColumns(10);
		
		labelMessage = new JLabel("");
		springLayout.putConstraint(SpringLayout.EAST, labelMessage, 0, SpringLayout.EAST, textInteger);
		labelMessage.setFont(new Font("Tahoma", Font.ITALIC, 11));
		labelMessage.setForeground(Color.RED);
		labelMessage.setHorizontalAlignment(SwingConstants.CENTER);
		springLayout.putConstraint(SpringLayout.NORTH, labelMessage, 6, SpringLayout.SOUTH, textInteger);
		springLayout.putConstraint(SpringLayout.WEST, labelMessage, 0, SpringLayout.WEST, textInteger);
		add(labelMessage);
	}

	/**
	 * Get value.
	 */
	@Override
	public Object getValue() {
		
		try {
			if (number == null && !ProgramGenerator.isExtensionToBuilder()) {
				number = 0L;
			}
			return number;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0L;
	}

	/**
	 * Set value.
	 */
	@Override
	public void setValue(Object value) {
		try {
			
			if (value instanceof Long) {
				number = (Long) value;
			}
			else if (value instanceof Integer) {
				number = (Long) value;
			}
			else {
				number = null;
			}
			
			if (number != null) {
				textInteger.setText(String.valueOf(number));
			}
			else {
				textInteger.setText("");
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Post creation.
	 */
	private void postCreation() {
		try {
			
			// Set editor listener.
			setEditorListener();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set editor listener.
	 */
	private void setEditorListener() {
		try {
			
			textInteger.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void removeUpdate(DocumentEvent e) {
					try {
						
						onEditorChange();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				@Override
				public void insertUpdate(DocumentEvent e) {
					try {
						
						onEditorChange();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				@Override
				public void changedUpdate(DocumentEvent e) {
					try {
						
						onEditorChange();
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
	 * On editor change.
	 */
	protected void onEditorChange() {
		try {
			
			// Try to convert the value.
			String text = textInteger.getText();
			String message = "";
			
			try {
				if (!text.isEmpty()) {
					number = Long.parseLong(text);
				}
				else {
					number = null;
				}
			}
			catch (NumberFormatException e) {
				
				number = null;
				message = Resources.getString("org.multipage.generator.messageErrorIntegerNumber");
			}
			
			labelMessage.setText(message);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Clear editor.
	 */
	public void clear() {
		try {
			
			number = null;
			textInteger.setText("");
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * @return the textInteger
	 */
	public JTextField getTextInteger() {
		return textInteger;
	}

	/**
	 * Set default value state.
	 */
	@Override
	public void setDefault(boolean isDefault) {
		
		// Nothing to do.
	}

	/**
	 * Get value meaning.
	 */
	@Override
	public String getValueMeaning() {
		
		try {
			return StringValueEditor.meansInteger;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
}
