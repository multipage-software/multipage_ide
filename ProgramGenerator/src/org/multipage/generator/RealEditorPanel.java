/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
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
 * Panel that displays editor for real value.
 * @author vakol
 *
 */
public class RealEditorPanel extends JPanel implements SlotValueEditorPanelInterface {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Double value.
	 */
	private Double number = null;
	
	// $hide<<$
	/**
	 * Components.
	 */
	private JTextField textReal;
	private JLabel labelMessage;
	private JButton buttonE;
	private JButton buttonPi;

	/**
	 * Create the panel.
	 */
	public RealEditorPanel() {
		
		try {
			// Initialize components.
			initComponents();
			// $hide>>$
			// Post creation.
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
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		textReal = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textReal, 16, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, textReal, 0, SpringLayout.WEST, this);
		add(textReal);
		textReal.setColumns(10);
		
		labelMessage = new JLabel("");
		springLayout.putConstraint(SpringLayout.NORTH, labelMessage, 6, SpringLayout.SOUTH, textReal);
		springLayout.putConstraint(SpringLayout.WEST, labelMessage, 0, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, labelMessage, 0, SpringLayout.EAST, this);
		labelMessage.setFont(new Font("Tahoma", Font.ITALIC, 11));
		labelMessage.setForeground(Color.RED);
		labelMessage.setHorizontalAlignment(SwingConstants.CENTER);
		add(labelMessage);
		
		buttonE = new JButton("e");
		springLayout.putConstraint(SpringLayout.NORTH, buttonE, 0, SpringLayout.NORTH, textReal);
		springLayout.putConstraint(SpringLayout.EAST, buttonE, 0, SpringLayout.EAST, labelMessage);
		buttonE.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setEuler();
			}
		});
		buttonE.setPreferredSize(new Dimension(20, 20));
		buttonE.setMargin(new Insets(0, 0, 0, 0));
		add(buttonE);
		
		buttonPi = new JButton("\u03C0");
		springLayout.putConstraint(SpringLayout.EAST, textReal, 0, SpringLayout.WEST, buttonPi);
		springLayout.putConstraint(SpringLayout.EAST, buttonPi, 0, SpringLayout.WEST, buttonE);
		buttonPi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setPi();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, buttonPi, 0, SpringLayout.NORTH, textReal);
		buttonPi.setPreferredSize(new Dimension(20, 20));
		buttonPi.setMargin(new Insets(0, 0, 0, 0));
		add(buttonPi);
	}

	/**
	 * Set PI.
	 */
	protected void setPi() {
		try {
			
			setValue(Math.PI);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Add value of Euler number.
	 */
	protected void setEuler() {
		try {
			
			setValue(Math.E);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get value.
	 */
	@Override
	public Object getValue() {

		try {
			if (number == null && !ProgramGenerator.isExtensionToBuilder()) {
				number = 0.0;
			}
			return number;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0.0;
	}

	/**
	 * Set value.
	 */
	@Override
	public void setValue(Object value) {
		try {
			
			if (value instanceof Double) {
				number = (Double) value;
				textReal.setText(String.valueOf(number));
			}
			else {
				number = null;
				textReal.setText("");
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			// Set document listener.
			setDocumentListener();
			// Set tool tips.
			setToolTips();
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
			
			buttonE.setToolTipText(Resources.getString("org.multipage.generator.tooltipInsertsEulerNumber"));
			buttonPi.setToolTipText(Resources.getString("org.multipage.generator.tooltipInsertsLudolphsNumber"));
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
			textReal.setText("");
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set document listener.
	 */
	private void setDocumentListener() {
		try {
			
			textReal.getDocument().addDocumentListener(new DocumentListener() {
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
			String text = textReal.getText();
			String message = "";
			
			try {
				if (!text.isEmpty()) {
					number = Double.parseDouble(text);
				}
				else {
					number = null;
				}
			}
			catch (NumberFormatException e) {
				
				number = null;
				message = Resources.getString("org.multipage.generator.messageErrorRealNumber");
			}
			
			labelMessage.setText(message);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * @return the textReal
	 */
	public JTextField getTextReal() {
		
		return textReal;
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
		
		return StringValueEditor.meansReal;
	}
}
