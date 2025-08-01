/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 26-04-2017
 *
 */

package org.multipage.generator;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SpringLayout;

import org.maclan.Area;
import org.maclan.ExternalLinkParser;
import org.maclan.MiddleResult;
import org.multipage.gui.Images;
import org.multipage.gui.InsertPanel;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.StringValueEditor;
import org.multipage.gui.TextFieldEx;
import org.multipage.gui.TextPopupMenu;
import org.multipage.gui.Utility;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

import com.ibm.icu.text.CharsetMatch;

/**
 * Panel that displays file properties.
 * @author vakol
 *
 */
public class FilePanel extends InsertPanel implements StringValueEditor, ExternalProviderInterface {

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
	 * Area reference.
	 */
	private Area area;
	
	/**
	 * Components.
	 */
	private JLabel labelFilePath;
	private TextFieldEx textFilePath;
	private JButton buttonSelectFilePath;
	private JLabel labelEncoding;
	private JComboBox comboEncoding;

	/**
	 * Create the dialog.
	 * @param parentWindow 
	 */
	public FilePanel(String initialString) {
		
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
		
		labelFilePath = new JLabel("org.multipage.generator.textInsertFilePath");
		sl_panelMain.putConstraint(SpringLayout.NORTH, labelFilePath, 28, SpringLayout.NORTH, this);
		sl_panelMain.putConstraint(SpringLayout.WEST, labelFilePath, 28, SpringLayout.WEST, this);
		add(labelFilePath);
		
		textFilePath = new TextFieldEx();
		sl_panelMain.putConstraint(SpringLayout.NORTH, textFilePath, -3, SpringLayout.NORTH, labelFilePath);
		textFilePath.setPreferredSize(new Dimension(6, 25));
		textFilePath.setMinimumSize(new Dimension(6, 25));
		sl_panelMain.putConstraint(SpringLayout.WEST, textFilePath, 6, SpringLayout.EAST, labelFilePath);
		add(textFilePath);
		textFilePath.setColumns(25);
		
		buttonSelectFilePath = new JButton("");
		sl_panelMain.putConstraint(SpringLayout.EAST, textFilePath, -3, SpringLayout.WEST, buttonSelectFilePath);
		sl_panelMain.putConstraint(SpringLayout.NORTH, buttonSelectFilePath, 0, SpringLayout.NORTH, textFilePath);
		sl_panelMain.putConstraint(SpringLayout.EAST, buttonSelectFilePath, -20, SpringLayout.EAST, this);
		buttonSelectFilePath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSelectFilePath();
			}
		});
		buttonSelectFilePath.setMargin(new Insets(0, 0, 0, 0));
		buttonSelectFilePath.setPreferredSize(new Dimension(25, 25));
		add(buttonSelectFilePath);
		
		labelEncoding = new JLabel("org.multipage.generator.textExternalProviderEncoding");
		sl_panelMain.putConstraint(SpringLayout.NORTH, labelEncoding, 140, SpringLayout.NORTH, this);
		sl_panelMain.putConstraint(SpringLayout.WEST, labelEncoding, 0, SpringLayout.WEST, labelFilePath);
		add(labelEncoding);
		
		comboEncoding = new JComboBox();
		comboEncoding.setPreferredSize(new Dimension(160, 20));
		sl_panelMain.putConstraint(SpringLayout.NORTH, comboEncoding, 0, SpringLayout.NORTH, labelEncoding);
		sl_panelMain.putConstraint(SpringLayout.WEST, comboEncoding, 6, SpringLayout.EAST, labelEncoding);
		add(comboEncoding);
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
			initializePanel();
			setEditBoxMenu();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Initialize panel
	 */
	private void initializePanel() {
		try {
			
			disableEncodings();
			
			// If text changes, reset combo with encodings
			Utility.setTextChangeListener(textFilePath, () -> {
				try {
					
					disableEncodings();
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
	 * Disable encoding
	 */
	private void disableEncodings() {
		try {
			
			comboEncoding.removeAllItems();
			comboEncoding.setEnabled(false);
			labelEncoding.setEnabled(false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On select file path.
	 */
	protected void onSelectFilePath() {
		try {
			
			// Display dialog for file selection
			File textFile = Utility.chooseFileToOpen(this, null);
			if (textFile == null) {
				return;
			}
			
			// Set file.
			setFile(textFile.toString(), "UTF-8");
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set file.
	 * @param preferredEncoding 
	 */
	private void setFile(String filePath, String preferredEncoding) {
		try {
			
			// Set text field and combo box with text encoding.
			textFilePath.setText(filePath);
			
			// Create file.
			File file = new File(filePath);
			
			// Invoke later.
			Safe.invokeLater(() -> {
				
				// Reset controls.
				comboEncoding.removeAllItems();
				comboEncoding.setEnabled(true);
				labelEncoding.setEnabled(true);
				
				// Variables.
				String encoding = null;
				boolean foundPreferredEncoding = false;
				
				// Gets available text encodings
				CharsetMatch [] charsets = Utility.getAvailableEncodingsFor(file);
				if (charsets == null || charsets.length == 0) {
					
					// Add preferred encoding to combo box.
					comboEncoding.addItem(preferredEncoding);
					comboEncoding.setSelectedItem(preferredEncoding);
					return;
				}
				
				for (CharsetMatch charset : charsets) {
					
					// Get encoding and its confidence
					encoding = charset.getName();
					int confidence = charset.getConfidence();
					
					// Prefer UTF-8 encoding if it is with maximum confidence
					if (!foundPreferredEncoding
							//&& (confidence >= maximumConfidence)
							&& encoding.contentEquals(preferredEncoding)) {
						
						foundPreferredEncoding = true;
					}
					
					// Add encoding to combo box.
					comboEncoding.addItem(encoding);
				}
				
				// Selects determined encoding
				if (foundPreferredEncoding) {
					encoding = preferredEncoding;
				}
				else {
					encoding = Utility.getTextEncoding(file);
				}
				comboEncoding.setSelectedItem(encoding);
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get specification.
	 * @return
	 */
	@Override
	public String getSpecification() {
		
		try {
			Object selected = null;
			
			// Get file path.
			String filePath = textFilePath.getText();
			
			// Get file encoding.
			selected = comboEncoding.getSelectedItem();
			String encoding = null;
			if (selected != null) {
				encoding = selected.toString();
			}
			else {
				encoding = "UTF-8";
			}
			
			String specification = encoding + ';' + filePath;
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
				textFilePath.setText(initialString);
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
			
			Utility.localize(labelFilePath);
			Utility.localize(labelEncoding);
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
			
			buttonSelectFilePath.setIcon(Images.getIcon("org/multipage/gui/images/folder.png"));
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
			
			buttonSelectFilePath.setToolTipText(Resources.getString("org.multipage.generator.tooltipSelectDiskFile"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set edit box trayMenu.
	 */
	private void setEditBoxMenu() {
		try {
			
			// Insert trayMenu item
			TextPopupMenu menu = textFilePath.getMenu();
			menu.insertItem(0, "org.multipage.generator.menuInsertPath", null, () -> {
				onInsertPath();
			});
			menu.insertSeparator(1);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set area.
	 * @param area
	 */
	public void setArea(Area area) {
		
		this.area = area;
	}
	
	/**
	 * On insert path.
	 */
	private void onInsertPath() {
		try {
			
			// Insert selected path.
			ProgramPaths.PathSupplier path = PathSelectionDialog.showDialog(this, this.area);
			if (path != null) {
				textFilePath.replaceSelection(path.tag);
			}
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
			return Resources.getString("org.multipage.generator.textFilePanel");
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
			return textFilePath.getText();
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
		
		FilePanel.bounds = bounds;
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
			String filePath = textFilePath.getText();
			return filePath;
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
		
		return meansFile;
	}
	
	/**
	 * Set controls grayed. If a false value is returned a high level editor grayed this panel.
	 */
	@Override
	public boolean setControlsGrayed(boolean isDefault) {
		
		return false;
	}
	
	/**
	 * Set editor controls from link string.
	 */
	@Override
	public void setEditor(String link, Area area) {
		try {
			
			// Parse link string.
			new ExternalLinkParser() {
	
				@Override
				public MiddleResult onFile(String filePath, String encoding) {
					try {
						// Set file path.
						setFile(filePath, encoding);
						return MiddleResult.OK;
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return MiddleResult.UNKNOWN_ERROR;
				}
				
			}.parse(link);
			
			// Set area.
			this.area = area;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
