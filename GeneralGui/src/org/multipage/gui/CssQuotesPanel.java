/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.regex.Matcher;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Panel that displays CSS quotes editor.
 * @author vakol
 *
 */
public class CssQuotesPanel extends InsertPanel implements StringValueEditor {

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
		
		bounds = new Rectangle(0, 0, 469, 450);
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
	 * Setting controls flag.
	 */
	private boolean settingControls = false;

	/**
	 * Start setting controls.
	 */
	public void startSettingControls() {
		
		settingControls = true;
	}

	/**
	 * Stop setting controls.
	 */
	public void stopSettingControls() {
		
		Safe.invokeLater(() -> {
			settingControls = false;
		});
	}
	
	// $hide<<$
	
	/**
	 * Components.
	 */
	private JLabel labelLeftQuote;
	private JTextField textLeftQuote;
	private JLabel labelRightQuote;
	private JTextField textRightQuote;
	private JComboBox comboLeftQuote;
	private JComboBox comboRightQuote;
	
	/**
	 * Create the dialog.
	 * @param parentWindow 
	 */
	public CssQuotesPanel(String initialString) {
		
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

		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		
		labelLeftQuote = new JLabel("org.multipage.gui.textCssLeftQuote");
		springLayout.putConstraint(SpringLayout.NORTH, labelLeftQuote, 41, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, labelLeftQuote, 42, SpringLayout.WEST, this);
		add(labelLeftQuote);
		
		textLeftQuote = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textLeftQuote, -3, SpringLayout.NORTH, labelLeftQuote);
		add(textLeftQuote);
		textLeftQuote.setColumns(10);
		
		labelRightQuote = new JLabel("org.multipage.gui.textCssRightQuote");
		springLayout.putConstraint(SpringLayout.NORTH, labelRightQuote, 0, SpringLayout.NORTH, labelLeftQuote);
		springLayout.putConstraint(SpringLayout.WEST, labelRightQuote, 16, SpringLayout.EAST, textLeftQuote);
		add(labelRightQuote);
		
		textRightQuote = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textRightQuote, -3, SpringLayout.NORTH, labelLeftQuote);
		add(textRightQuote);
		textRightQuote.setColumns(10);
		
		comboLeftQuote = new JComboBox();
		comboLeftQuote.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onLeftComboChanged();
			}
		});
		comboLeftQuote.setFont(new Font("Tahoma", Font.BOLD, 16));
		springLayout.putConstraint(SpringLayout.WEST, textLeftQuote, 6, SpringLayout.EAST, comboLeftQuote);
		springLayout.putConstraint(SpringLayout.NORTH, comboLeftQuote, -3, SpringLayout.NORTH, labelLeftQuote);
		springLayout.putConstraint(SpringLayout.WEST, comboLeftQuote, 6, SpringLayout.EAST, labelLeftQuote);
		comboLeftQuote.setPreferredSize(new Dimension(40, 20));
		add(comboLeftQuote);
		
		comboRightQuote = new JComboBox();
		comboRightQuote.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRightComboChanged();
			}
		});
		comboRightQuote.setFont(new Font("Tahoma", Font.BOLD, 16));
		springLayout.putConstraint(SpringLayout.WEST, textRightQuote, 6, SpringLayout.EAST, comboRightQuote);
		springLayout.putConstraint(SpringLayout.NORTH, comboRightQuote, -3, SpringLayout.NORTH, labelLeftQuote);
		springLayout.putConstraint(SpringLayout.WEST, comboRightQuote, 5, SpringLayout.EAST, labelRightQuote);
		comboRightQuote.setPreferredSize(new Dimension(40, 20));
		add(comboRightQuote);
	}

	/**
	 * On right combo changed.
	 */
	protected void onRightComboChanged() {
		try {
			
			if (settingControls) {
				return;
			}
			
			startSettingControls();
			textRightQuote.setText("");
			stopSettingControls();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On left combo box changed.
	 */
	protected void onLeftComboChanged() {
		try {
			
			if (settingControls) {
				return;
			}
			
			startSettingControls();
			textLeftQuote.setText("");
			stopSettingControls();
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
			
			setFromInitialString();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
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
			loadComboBoxes();
			loadDialog();
			
			setListeners();
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
			
			Utility.setTextChangeListener(textLeftQuote, () -> {
				try {
					
					if (settingControls) {
						return;
					}
					
					startSettingControls();
					comboLeftQuote.setSelectedIndex(0);
					stopSettingControls();
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			Utility.setTextChangeListener(textRightQuote, () -> {
				try {
					
					if (settingControls) {
						return;
					}
					
					startSettingControls();
					comboRightQuote.setSelectedIndex(0);
					stopSettingControls();
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
	 * Load combo boxes.
	 */
	private void loadComboBoxes() {
		try {
			
			final String [] quotes = new String [] {
					"", "\"", "'", "�", "�", "�", "�", "�", "�","�", "�", "�"
			};
			
			Utility.loadItems(comboLeftQuote, quotes);
			comboLeftQuote.setSelectedIndex(1);
			
			Utility.loadItems(comboRightQuote, quotes);
			comboRightQuote.setSelectedIndex(1);
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
			return getQuoteString(textLeftQuote, comboLeftQuote) + " " + getQuoteString(textRightQuote, comboRightQuote);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get quote string.
	 * @param textField
	 * @param comboBox
	 * @return
	 */
	private String getQuoteString(JTextField textField, JComboBox comboBox) {
		
		try {
			String quoteText = textField.getText();
			if (!quoteText.isEmpty()) {
				
				if (quoteText.contains("'")) {
					return "\"" + quoteText + "\"";
				}
				
				return "'" + quoteText + "'";
			}
			
			// Get combo value.
			quoteText = (String) comboBox.getSelectedItem();
			if (quoteText.isEmpty()) {
				return "none";
			}
			
			if (quoteText.equals("'")) {
				return "\"" + quoteText + "\"";
			}
			return "'" + quoteText + "'";
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
				
				try {
					Obj<Integer> position = new Obj<Integer>(0);
					
					// Get 'none' value.
					int positionAux = position.ref;
					
					String text = Utility.getNextMatch(initialString, position, "\\G\\s*none");
					if (text != null) {
						
						comboLeftQuote.setSelectedIndex(0);
						comboRightQuote.setSelectedIndex(0);
						textLeftQuote.setText("");
						textRightQuote.setText("");
						
						return;
					}
	
					position.ref = positionAux;
					
					// Get controls' values.
					if (!setQuoteControls(initialString, position, comboLeftQuote, textLeftQuote)) {
						return;
					}
					
					// Get space between.
					text = Utility.getNextMatch(initialString, position, "\\G\\s+");
					if (text == null) {
						return;
					}
					
					if (!setQuoteControls(initialString, position, comboRightQuote, textRightQuote)) {
						return;
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
	 * Set quote controls.
	 * @param initialString
	 * @param position
	 * @param comboBox
	 * @param textField
	 * @return
	 */
	private boolean setQuoteControls(String initialString,
			Obj<Integer> position, JComboBox comboBox,
			JTextField textField) {
		
		try {
			// Initialize controls.
			textField.setText("");
			comboBox.setSelectedIndex(0);
			
			Obj<Matcher> matcher = new Obj<Matcher>();
	
			// Get combo or text value.
			String text = Utility.getNextMatch(initialString, position, "\\G\\s*\\'([^\\']*)\\'", matcher);
			
			if (text == null) {
				text = Utility.getNextMatch(initialString, position, "\\G\\s*\\\"([^\\\"]*)\\\"", matcher);
			}
			
			if (text != null && matcher.ref.groupCount() == 1) {
				text = matcher.ref.group(1);
					
				// Select quote if it exists.
				DefaultComboBoxModel model = (DefaultComboBoxModel) comboBox.getModel();
				int index = model.getIndexOf(text);
				
				if (index >= 0) {
					comboBox.setSelectedIndex(index);
					return true;
				}
				
				textField.setText(text);
				return true;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Localize components.
	 */
	private void localize() {
		try {
			
			Utility.localize(labelLeftQuote);
			Utility.localize(labelRightQuote);
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
			return Resources.getString("org.multipage.gui.textCssQuotesBuilder");
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
		
		CssQuotesPanel.bounds = bounds;
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
	 * @return
	 */
	@Override
	public Component getComponent() {
		
		return this;
	}

	/**
	 * Get string value.
	 * @return
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
	 * @param string
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
	 * @return
	 */
	@Override
	public String getValueMeaning() {
		
		return meansCssQuotes;
	}

	/**
	 * Set controls grayed. If a false value is returned a high level editor grayed this panel.
	 */
	@Override
	public boolean setControlsGrayed(boolean isDefault) {
		
		return false;
	}
}
