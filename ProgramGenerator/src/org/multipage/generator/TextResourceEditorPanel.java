/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.maclan.VersionObj;
import org.maclan.help.HelpUtility;
import org.maclan.help.Intellisense;
import org.multipage.basic.ProgramBasic;
import org.multipage.gui.FoundAttr;
import org.multipage.gui.Images;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.TextEditorPane;
import org.multipage.gui.UpdatableComponent;
import org.multipage.gui.Utility;
import org.multipage.util.Closable;
import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Text resource editor.
 * @author vakol
 *
 */
public class TextResourceEditorPanel extends JPanel implements Closable, UpdatableComponent {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Font.
	 */
	private static Font fontState;
	
	/**
	 * Load dialog.
	 */
	private void loadDialog() {
		try {
			
			editor.setTextFont(fontState);
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
			
			fontState = editor.getTextFont();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set default data.
	 */
	public static void setDefaultData() {
		
		fontState = new Font("Consolas", Font.PLAIN, 13);
	}

	/**
	 * Load data.
	 * @param inputStream
	 */
	public static void seriliazeData(StateInputStream inputStream)
		throws IOException, ClassNotFoundException {
		
		fontState = Utility.readInputStreamObject(inputStream, Font.class);
	}

	/**
	 * Save data.
	 * @param outputStream
	 */
	public static void seriliazeData(StateOutputStream outputStream)
		throws IOException {
		
		outputStream.writeObject(fontState);
	}

	/**
	 * Resource ID.
	 */
	public long resourceId;
	
	/**
	 * Flag that is true if it is a start resource.
	 */
	public boolean isStartResource = false;
	
	/**
	 * Version ID.
	 */
	private long versionId = 0L;
	
	/**
	 * Resource area description.
	 */
	private String areaDescription = "";
	
	/**
	 * Safe text.
	 */
	private String safeText = null;
	
	/**
	 * TextResourceEditor.
	 */
	private TextEditorPane editor;

	/**
	 * Menu add in.
	 */
	private GeneratorTextPopupMenuAddIn popupMenuAddIn;
	
	/**
	 * Close lambda.
	 */
	private Runnable closeLambda = null;

	// $hide<<$
	/**
	 * Dialog components.
	 */
	private JButton buttonClose;
	private JButton buttonSave;
	private JPanel editorPanel;
	private JButton buttonSaveAndClose;
	
	/**
	 * Create the dialog.
	 * @param parentWindow
	 * @param resourceId
	 * @param isStartResource
	 * @param versionId
	 * @param areaDescription 
	 * @param modal 
	 */
	public TextResourceEditorPanel(Window parentWindow, long resourceId, boolean isStartResource,
			long versionId, String areaDescription, boolean modal) {
		
		try {
			// Initialize components.
			initComponents();
			// $hide>>$
			// Post creation.
			postCreate(resourceId, isStartResource, versionId, areaDescription);
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
		
		buttonClose = new JButton("textClose");
		buttonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClose();
			}
		});
		buttonClose.setMargin(new Insets(0, 0, 0, 0));
		buttonClose.setMaximumSize(new Dimension(80, 25));
		buttonClose.setMinimumSize(new Dimension(80, 25));
		buttonClose.setPreferredSize(new Dimension(80, 25));
		springLayout.putConstraint(SpringLayout.SOUTH, buttonClose, -10, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, buttonClose, -10, SpringLayout.EAST, this);
		add(buttonClose);
		
		buttonSave = new JButton("textSave");
		springLayout.putConstraint(SpringLayout.NORTH, buttonSave, 0, SpringLayout.NORTH, buttonClose);
		buttonSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSave();
			}
		});
		buttonSave.setPreferredSize(new Dimension(80, 25));
		buttonSave.setMargin(new Insets(0, 0, 0, 0));
		buttonSave.setMaximumSize(new Dimension(80, 25));
		buttonSave.setMinimumSize(new Dimension(80, 25));
		add(buttonSave);
		
		editorPanel = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, editorPanel, 26, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, editorPanel, 0, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, editorPanel, -6, SpringLayout.NORTH, buttonClose);
		springLayout.putConstraint(SpringLayout.EAST, editorPanel, 0, SpringLayout.EAST, this);
		add(editorPanel);
		editorPanel.setLayout(new BorderLayout(0, 0));
		
		buttonSaveAndClose = new JButton("org.multipage.generator.textSaveAndClose");
		springLayout.putConstraint(SpringLayout.EAST, buttonSave, -6, SpringLayout.WEST, buttonSaveAndClose);
		buttonSaveAndClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSaveAndClose();
			}
		});
		buttonSaveAndClose.setMargin(new Insets(0, 0, 0, 0));
		buttonSaveAndClose.setPreferredSize(new Dimension(120, 25));
		springLayout.putConstraint(SpringLayout.NORTH, buttonSaveAndClose, 0, SpringLayout.NORTH, buttonClose);
		springLayout.putConstraint(SpringLayout.EAST, buttonSaveAndClose, -6, SpringLayout.WEST, buttonClose);
		add(buttonSaveAndClose);
	}
	
	/**
	 * Get resource ID.
	 * @return
	 */
	public long getResourceId() {
		
		return resourceId;
	}
	
	/**
	 * Get version ID.
	 * @return
	 */
	public long getVersionId() {
		
		return versionId;
	}
	
	/**
	 * Get area description.
	 * @return
	 */
	public String getAreaDescription() {
		
		if (areaDescription == null) {
			return "";
		}
		return areaDescription;
	}
	
	/**
	 * On close.
	 */
	protected void onClose() {
		try {
			saveDialog();
			// Call close lambda function.
			if (closeLambda != null) {
				closeLambda.run();
			}
			else {
				close();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Post creation.
	 * @param resourceId
	 * @param isStartResource
	 * @param versionId
	 * @param areaDescription 
	 */
	private void postCreate(long resourceId, boolean isStartResource,
							long versionId, String areaDescription) {
		try {
			
			this.resourceId = resourceId;
			this.isStartResource = isStartResource;
			this.versionId = versionId;
			this.areaDescription = areaDescription;
			
			// Create editor.
			createEditor();
			// Localize.
			localize();
			// Set icons.
			setIcons();
			// Initialize intellisens.
			intellisense();
			// Load resource data.
			load();
			// Set editor listeners.
			setEditorListeners();
			// Load dialog data.
			loadDialog();
			// Add builder popup trayMenu add-in.
			popupMenuAddIn = new GeneratorTextPopupMenuAddIn();
			editor.addPopupMenusPlain(popupMenuAddIn);
			// Register for update.
			GeneratorMainFrame.registerForUpdate(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create title text.
	 * @param areaDescription 
	 */
	public String createTitle() {
		
		try {
			// Load resource name.
			Obj<String> name = new Obj<String>();
			Obj<String> type = new Obj<String>();
			MiddleResult result = ProgramGenerator.getResourceNameType(resourceId, name, type);
			if (result.isNotOK()) {
				result.show(this);
				return "";
			}
			
			// Trim area description.
			if (areaDescription == null) {
				areaDescription = "";
			}
			
			// Set dialog title.
			String title = "";
			if (isStartResource) {
				
				// Get version.
				VersionObj version = ProgramGenerator.getVersion(versionId);
				String versionName = "";
				if (version != null) {
					versionName = version.getDescription();
				}
				
				String titleFormat = Resources.getString("org.multipage.generator.titleStartResourceEditorTitle");
				title = String.format(titleFormat, name.ref, versionName, type.ref, areaDescription);
			}
			else {
				String titleFormat = Resources.getString("org.multipage.generator.titleTextResourceEditorTitle");
				title = String.format(titleFormat, name.ref, type.ref, areaDescription);
			}
			return title;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Create editor.
	 */
	private void createEditor() {
		try {
			
			Window thisWindow = Utility.findWindow(this);
			
			editor = new TextEditorPane(thisWindow, false);
			editor.setExtractBody(false);
			editorPanel.add(editor);
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
			
			Utility.localize(this);
			Utility.localize(buttonClose);
			Utility.localize(buttonSave);
			Utility.localize(buttonSaveAndClose);
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
			
			buttonClose.setIcon(Images.getIcon("org/multipage/generator/images/cancel_icon.png"));
			buttonSave.setIcon(Images.getIcon("org/multipage/generator/images/save_icon.png"));
			buttonSaveAndClose.setIcon(Images.getIcon("org/multipage/generator/images/save_icon.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load resource data.
	 */
	private void load() {
		try {
			
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			MiddleResult result;
			
			Obj<String> text = new Obj<String>();
			
			// Load resource text.
			result = middle.loadResourceTextToString(login, resourceId, text);
			if (result.isNotOK()) {
				result.show(this);
			}
			
			// Get old scroll position.
			Point oldScrollPosition = editor.getScrollPosition();
			// Set safe text.
			safeText = text.ref;
			// Set editor text.
			editor.setText(text.ref);
			
			// Scroll to the old position.
			editor.scrollToPosition(oldScrollPosition);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On save.
	 */
	private void onSave() {
		try {
			
			save();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On save and close.
	 */
	protected void onSaveAndClose() {
		try {
			
			save();
			onClose();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Save.
	 */
	protected boolean save() {

		try {
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			MiddleResult result;
			
			// Get text.
			String text = editor.getText();
			
			// Save resource text.
			result = middle.updateResourceText(login, resourceId, text);
			if (result.isNotOK()) {
				result.show(this);
			}
			
			// Set safe text.
			safeText = text;
			
			return result.isOK();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Enable intellisense.
	 */
	private void intellisense() {
		try {
			
			// Use intellisense for the text editor.
			Intellisense.applyTo(editor, (helpPageAlias, fragmentAlias) -> {
				try {
					
					GeneratorMainFrame.displayOnlineArea(HelpUtility.maclanReference, helpPageAlias, fragmentAlias);
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
	 * On before change listener.
	 * @param currentLanguageId
	 * @param oldLanguageId
	 */
	protected void onLanguageBeforeChange(long currentLanguageId,
			long oldLanguageId) {
		try {
			
			// Process new content.
			processNewContent();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On language change.
	 * @param currentLanguageId
	 * @param oldLanguageId
	 */
	protected void onLanguageAfterChanged(long currentLanguageId, long oldLanguageId) {
		try {
			
			load();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Process possible new content. 
	 */
	public void processNewContent() {
		try {
			
			if (safeText == null) {
				return;
			}
			
			// Get current content.
			String currentContent = editor.getText();
			// If the content is not changed, do nothing.
			if (currentContent.equals(safeText)) {
				return;
			}
			
			// Ask user if to save the content.
			if (JOptionPane.showConfirmDialog(this,
					Resources.getString("org.multipage.generator.messageEditorContentChangedSaveIt"))
					!= JOptionPane.YES_OPTION) {
				return;
			}
			
			// Save the content.
			save();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set editor listeners.
	 */
	private void setEditorListeners() {
		try {
			
			editor.getTextPane().addKeyListener(new KeyAdapter() {
				// On key pressed.
				@Override
				public void keyPressed(KeyEvent e) {
					try {
						
						if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S) {
							// Save data.
							save();
						}
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
	 * Set found attributes
	 * @param foundAttributes
	 */
	public void setFoundAttributes(FoundAttr foundAttributes) {
		try {
			
			if (foundAttributes != null) {
				editor.highlightFound(foundAttributes);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set close lambda.
	 * @param closeLambda
	 */
	public void setCloseLambda(Runnable closeLambda) {
		
		this.closeLambda  = closeLambda;
	}
	
	/**
	 * Update editor components.
	 * @return
	 */
	@Override
	public void updateComponents() {
		try {
			
			// TODO: Update components if needed.
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Close editor.
	 */
	public void close() {
		try {
			
			// Remove from update.
			GeneratorMainFrame.unregisterFromUpdate(this);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
}
