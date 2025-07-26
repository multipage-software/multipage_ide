/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2021-04-09
 *
 */

package org.multipage.generator;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.maclan.Area;
import org.maclan.AreasModel;
import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.multipage.basic.ProgramBasic;
import org.multipage.gui.Images;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.TextFieldAutoSave;
import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;
import org.multipage.util.j;

/**
 * Base class for area editors.
 * @author vakol
 *
 */
public abstract class AreaEditorBase {
	
	/**
	 * Bounds.
	 */
	protected static Rectangle bounds;

	/**
	 * Tab component selection.
	 */
	protected static int tabSelectionState;
	
	/**
	 * Save timer delay in milliseconds.
	 */
	protected static final int saveTimerDelay = 2000;

	/**
	 * Set default data.
	 */
	public static void setDefaultData() {

		bounds = new Rectangle();
		tabSelectionState = 0;
	}

	/**
	 * Load data.
	 * @param inputStream
	 */
	public static void seriliazeData(StateInputStream inputStream)
		throws IOException, ClassNotFoundException {
		
		Object data = inputStream.readObject();
		if (!(data instanceof Rectangle)) {
			throw new ClassNotFoundException();
		}
		bounds = (Rectangle) data;
		
		tabSelectionState = inputStream.readInt();
		
		AreaDependenciesPanel.selectedSubAreas = inputStream.readBoolean();
	}

	/**
	 * Save data.
	 * @param outputStream
	 */
	public static void seriliazeData(StateOutputStream outputStream)
		throws IOException {

		outputStream.writeObject(bounds);
		outputStream.writeInt(tabSelectionState);
		outputStream.writeBoolean(AreaDependenciesPanel.selectedSubAreas);
	}
	
	/**
	 * Edited area.
	 */
	protected Area area;

	/**
	 * Tab content lookup table.
	 */
	protected Hashtable<Component, EditorTabActions> tabContentsTable = new Hashtable<Component, EditorTabActions>();
	
	/**
	 * Old tab content.
	 */
	protected EditorTabActions oldTabContent;
	
	/**
	 * Resources panel.
	 */
	protected AreaResourcesEditor panelResources;
	
	/**
	 * Dependencies panel.
	 */
	protected AreaDependenciesPanelBase panelDependencies;
	
	/**
	 * Constructors panel.
	 */
	protected AreaConstructorPanel panelConstructor;

	/**
	 * Parent component reference.
	 */
	protected Component parentComponent;
	
	/**
	 * Dispose lambda function.
	 */
	protected Runnable disposeLambda;
	
	/**
	 * Get title lambda function.
	 */
	protected Supplier<String> getTitleLambda;
	
	/**
	 * Set title lambda function.
	 */
	protected Consumer<String> setTitleLambda;
	
	/**
	 * Set icon lambda function.
	 */
	protected Consumer<BufferedImage> setIconImageLambda;
	
	/**
	 * Get the window; lambda function.
	 */
	protected Supplier<Window> getWindowLambda;
	
	/**
	 * Set frame boundaries; lambda function.
	 */
	protected Consumer<Rectangle> setBoundsLambda;
	
	/**
	 * Get frame boundaries; lambda function.
	 */
	protected Supplier<Rectangle> getBoundsLambda;
	
	/**
	 * Constructor.
	 */
	public AreaEditorBase(Component parentComponent, Area area) {
		
		// Remember editor area.
		this.area = area;
		this.parentComponent = parentComponent;
	}
	
	/**
	 * Get edited area.
	 * @return
	 */
	public Area getArea() {
		
		return area;
	}
	
	/**
	 * Load dialog.
	 */
	protected void loadDialog() {
		try {
			
			if (bounds.isEmpty()) {
				// Center dialog.
				Utility.centerOnScreen(getWindowLambda.get());
			}
			else {
				setBoundsLambda.accept(bounds);
			}
			getTabbedPane().setSelectedIndex(tabSelectionState);
			if (tabSelectionState == 0) {
				onTabChanged();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Save dialog.
	 */
	protected void saveDialog() {
		try {
			
			bounds = getBoundsLambda.get();
			tabSelectionState = getTabbedPane().getSelectedIndex();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Update area description.
	 */
	public void updateAreaEditor() {
		try {
			
			// Update area object.
			Area newAreaObject = ProgramGenerator.getArea(area.getId());
			if (newAreaObject == null) {
				return;
			}
			area = newAreaObject;
			
			// Set description, alias, folder, file name.
			Safe.tryUpdate(AreaEditorBase.this, () -> {
				getTextDescription().setText(area.getDescription());
				getTextAlias().setText(area.getAlias());
				getTextFolder().setText(area.getFolder());
				getTextFileName().setText(area.getFileName());
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On tab changed.
	 */
	protected void onTabChanged() {
		try {
			
			// Save old tab content information.
			if (oldTabContent != null) {
				oldTabContent.onSavePanelInformation();
			}
			
			// Implement information loading and saving.
			Component component = getTabbedPane().getSelectedComponent();
			// Get tab content interface.
			EditorTabActions tabContent = tabContentsTable.get(component);
			if (tabContent != null) {
				
				// Invoke load information.
				tabContent.onLoadPanelInformation();
			}
			
			// Remember tab content.
			oldTabContent = tabContent;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get tabbed pane.
	 */
	protected abstract JTabbedPane getTabbedPane();
	
	/**
	 * Get text description.
	 */
	protected abstract TextFieldAutoSave getTextDescription();
	
	/**
	 * Get text alias.
	 */
	protected abstract TextFieldAutoSave getTextAlias();
	
	/**
	 * Get file name text field.
	 */
	protected abstract TextFieldAutoSave getTextFileName();
	
	/**
	 * Get file extension text field.
	 */
	protected abstract TextFieldAutoSave getTextFileExtension();
	
	/**
	 * Get folder text field.
	 */
	protected abstract TextFieldAutoSave getTextFolder();
	
	/**
	 * Get identifier text field.
	 */
	protected abstract JTextField getTextIdentifier();
	
	/**
	 * Get save file button.
	 * @return
	 */
	protected abstract JButton getButtonSaveFileName();

	/**
	 * Get save description button.
	 * @return
	 */
	protected abstract JButton getButtonSaveDescription();
	
	/**
	 * Get save alias button.
	 * @return
	 */
	protected abstract JButton getButtonSaveAlias();
	
	/**
	 * Get close button.
	 * @return
	 */
	protected abstract JButton getButtonSaveAndClose();
	
	/**
	 * Save folder button.
	 * @return
	 */
	protected abstract JButton getButtonSaveFolder();
	
	/**
	 * Get save button.
	 * @return
	 */
	protected abstract JButton getButtonSave();
	
	/**
	 * Get update button.
	 * @return
	 */
	protected abstract JButton getButtonUpdate();
	
	/**
	 * Get is visible check box.
	 * @return
	 */
	protected abstract JCheckBox getCheckBoxVisible();

	/**
	 * Get is start area check box.
	 * @return
	 */
	protected abstract JCheckBox getCheckBoxHomeArea();
	
	/**
	 * Get identifier label.
	 * @return
	 */
	protected abstract JLabel getLabelIdentifier();
	
	/**
	 * Get area description label.
	 * @return
	 */
	protected abstract JLabel getLabelAreaDescription();
	
	/**
	 * Get area alias label.
	 * @return
	 */
	protected abstract JLabel getLabelAreaAlias();
	
	/**
	 * Get file name label.
	 * @return
	 */
	protected abstract JLabel getLabelFileName();
	
	/**
	 * Get folder label.
	 * @return
	 */
	protected abstract JLabel getLabelFolder();
	
	/**
	 * Get file extension.
	 * @return
	 */
	protected abstract JLabel getLabelFileExtension();
	
	/**
	 * Get disabled button.
	 */
	protected abstract JCheckBox getCheckBoxIsDisabled();
	
	/**
	 * Insert tabs' contents.
	 */
	protected abstract void insertTabsContents();
	
	/**
	 * Insert tab content.
	 * @param component
	 * @param content
	 */
	protected void insertTabContent(JPanel component, Component content) {
		try {
			
			component.add(content);
			
			if (content instanceof EditorTabActions) {
				tabContentsTable.put(component, (EditorTabActions) content);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Save form data.
	 */
	protected void saveData() {
		try {
			
			// Save description.
			saveDescription();
			// Save alias.
			saveAlias();
			// Save folder name.
			saveFolder();
			// Save file name.
			saveFileName();
			// Save file extension.
			saveFileExtension();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On update.
	 */
	public void onUpdate() {
		try {
			
			// Ask user.
			if (!Utility.ask(getWindowLambda.get(), "org.multipage.generator.messageWouldYouLikeToUpdateChangesLost")) {
				return;
			}
			
			// Update area data.
			updateAreaEditor();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On save.
	 */
	protected void onSave() {
		try {
			
			// Save current panel input.
			saveData();
			// Update application components.
			GeneratorMainFrame.updateAll();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On save and close the dialog.
	 */
	protected void onSaveAndClose() {
		try {
			
			// Remove editor from navigator window.
			DialogNavigator.removeAreaEditor(area);
			// Save current panel input.
			saveData();
			// Update application components.
			GeneratorMainFrame.updateAll();
			// Save dialog.
			saveDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
		// Close the window.
		dispose();
	}
	
	/**
	 * Dispose form.
	 */
	private void dispose() {
		try {
			
			if (disposeLambda != null) {
				disposeLambda.run();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Post creation.
	 */
	protected void postCreate() {
		try {
			
			// Insert tabs' contents.
			insertTabsContents();
			// Create listeners.
			createListeners();
			// Set start area check box.
			setStartAreaCheckbox();
			// Localize dialog.
			localize();
			// Set icons.
			setIcons();
			// Set tool tips.
			setToolTips();
			// Set callback functions
			setCallbacks();
			
			Safe.tryUpdate(AreaEditorBase.this, () -> {
				// Load area description.
				getTextDescription().setText(area.getDescription());
				// Set area alias.
				getTextAlias().setText(area.getAlias());
				// Set area file name.
				getTextFileName().setText(area.getFileName());
				// Set area file extension.
				getTextFileExtension().setText(area.getFileExtension());
				// Set area folder name.
				getTextFolder().setText(area.getFolder());
				// Set title.
				setTitle(getTitle() + " - " + area.toString());
				// Load area identifier.
				getTextIdentifier().setText(String.valueOf(area.getId()));
				// Set area disabled flag.
				if (!area.isReadOnly()) {
					getCheckBoxIsDisabled().setSelected(!area.isEnabled());
				}
				else {
					getCheckBoxIsDisabled().setEnabled(false);
					getCheckBoxIsDisabled().setSelected(false);
				}
			});
			// Load dialog.
			loadDialog();
			// Set filename components.
			setFileNameComponents();
			
			panelResources.panelIsReady();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get title.
	 * @return
	 */
	private String getTitle() {
		
		try {
			if (getTitleLambda != null) {
				String title = getTitleLambda.get();
				return title;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Set title.
	 * @param title
	 */
	public void setTitle(String title) {
		try {
			
			if (setTitleLambda != null) {
				setTitleLambda.accept(title);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set file name components.
	 */
	public void setFileNameComponents() {
		Safe.tryUpdate(AreaEditorBase.this, () -> {
			
			boolean enabled;
			
			if (getCheckBoxVisible() != null) {
				enabled = getCheckBoxVisible().isSelected();
			}
			else {
				enabled = area.isVisible();
			}
			
			getLabelFileName().setEnabled(enabled);
			getLabelFileExtension().setEnabled(enabled);
			getTextFileName().setEditable(enabled);
			getTextFileExtension().setEditable(enabled);
			getButtonSaveFileName().setEnabled(enabled);
		});
	}

	/**
	 * Create listeners.
	 */
	public void createListeners() {
		try {
			
			getCheckBoxHomeArea().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					Safe.tryOnChange(AreaEditorBase.this, () -> {
						onIsHomeAreaAction();
					});
				}
			});
			
			getCheckBoxIsDisabled().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					Safe.tryOnChange(AreaEditorBase.this, () -> {
						onIsDisabledAction();
					});
				}
			});
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
			
			getButtonSaveDescription().setToolTipText(
					Resources.getString("org.multipage.generator.tooltipSaveAreaDescription"));
			getButtonSaveAlias().setToolTipText(
					Resources.getString("org.multipage.generator.tooltipSaveAreaAlias"));
			getButtonSaveFileName().setToolTipText(
					Resources.getString("org.multipage.generator.tooltipSaveFileName"));
			getButtonSaveFolder().setToolTipText(
					Resources.getString("org.multipage.generator.tooltipSaveFolder"));
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
			
			setIconImage(Images.getImage("org/multipage/basic/images/main_icon.png"));
			
			getButtonSaveDescription().setIcon(
					Images.getIcon("org/multipage/generator/images/save_icon.png"));
			getButtonSaveAlias().setIcon(
					Images.getIcon("org/multipage/generator/images/save_icon.png"));
			getButtonSaveAndClose().setIcon(
					Images.getIcon("org/multipage/gui/images/cancel_icon.png"));
			getButtonSave().setIcon(
					Images.getIcon("org/multipage/generator/images/save_icon.png"));
			getButtonSaveFileName().setIcon(
					Images.getIcon("org/multipage/generator/images/save_icon.png"));
			getButtonSaveFolder().setIcon(
					Images.getIcon("org/multipage/generator/images/save_icon.png"));
			getButtonUpdate().setIcon(
					Images.getIcon("org/multipage/generator/images/update_icon.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set icon of the window.
	 * @param image
	 */
	protected void setIconImage(BufferedImage image) {
		try {
			
			if (setIconImageLambda != null) {
				setIconImageLambda.accept(image);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Localize dialog.
	 */
	protected void localize() {
		try {
			
			Utility.localize(getTabbedPane());
			Utility.localize(getLabelIdentifier());
			Utility.localize(getLabelAreaDescription());
			Utility.localize(getButtonSaveAndClose());
			Utility.localize(getCheckBoxHomeArea());
			Utility.localize(getLabelAreaAlias());
			Utility.localize(getButtonSave());
			Utility.localize(getLabelFileName());
			Utility.localize(getLabelFolder());
			Utility.localize(getButtonUpdate());
			Utility.localize(getLabelFileExtension());
			Utility.localize(getCheckBoxIsDisabled());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get window.
	 */
	public JFrame getFrame() {
		
		return null;
	}
	
	/**
	 * Save description.
	 */
	protected void saveDescription() {
		try {
			
			// Delegate the call.
			String description = getTextDescription().getText().trim();
			saveDescription(area, description);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Save description.
	 * @param area 
	 * @param description
	 */
	protected void saveDescription(Area area, String description) {
		try {
			
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
	
			// Save area description.
			MiddleResult result = middle.updateAreaDescription(login, area, description);
			if (result.isNotOK()) {
				
				result.show(getWindowLambda.get());
				
				Safe.tryUpdate(AreaEditorBase.this, () -> {
					getTextDescription().setText("");
				});
				return;
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Save alias.
	 */
	protected void saveAlias() {
		try {
			
			// Delegate the call.
			String alias = getTextAlias().getText().trim();
			saveAlias(area, alias);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Save alias.
	 * @param area 
	 * @param alias
	 */
	protected void saveAlias(Area area, String alias) {
		try {
			
			// Check alias uniqueness against project root.
			AreasModel model = ProgramGenerator.getAreasModel();
			if (!model.isAreaAliasUnique(alias, area.getId())) {
				
				Utility.show(getWindowLambda.get(), "org.multipage.generator.messageAreaAliasAlreadyExists", alias);
				return;
			}
			
			MiddleResult result;
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			
			// Get area ID.
			long areaId = area.getId();
	
			// Save area.
			result = middle.updateAreaAlias(login, areaId, alias);
			if (result.isNotOK()) {
				
				result.show(getWindowLambda.get());
				Safe.tryUpdate(AreaEditorBase.this, () -> {
					getTextAlias().setText("");
				});
				return;
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Save folder.
	 */
	protected void saveFolder() {
		try {
			
			// Delegate the call.
			String folder = getTextFolder().getText().trim();
			saveFolder(area, folder);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Save folder.
	 * @param area 
	 * @param folder
	 */
	protected void saveFolder(Area area, String folder) {
		try {
			
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			
			// Get area ID.
			long areaId = area.getId();
			
			// Update folder name.
			MiddleResult result = middle.updateAreaFolderName(login, areaId, folder);
			if (result.isNotOK()) {
				
				Safe.tryUpdate(AreaEditorBase.this, () -> {
					getTextFolder().setText("");
				});
				result.show(getWindowLambda.get());
				return;
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Save file name.
	 */
	protected void saveFileName() {
		try {
			
			// Delegate the call.
			String fileName = getTextFileName().getText().trim();
			saveFileName(area, fileName);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Save file name.
	 * @param area 
	 * @param fileName
	 */
	protected void saveFileName(Area area, String fileName) {
		try {
			
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			
			MiddleResult result = middle.login(login);
			if (result.isOK()) {
				
				// Get area ID.
				long areaId = area.getId();
				
				// Update file name.
				result = middle.updateAreaFileName(areaId, fileName);
				
				MiddleResult logoutResult = middle.logout(result);
				if (result.isOK()) {
					result = logoutResult;
				}
			}
			
			if (result.isNotOK()) {
				
				getTextFileName().setText("");
				result.show(getWindowLambda.get());
				return;
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Save file extension.
	 */
	protected void saveFileExtension() {
		try {
			
			// Delegate the call.
			String fileExtension = getTextFileExtension().getText().trim();
			saveFileExtension(area, fileExtension);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Save file extension.
	 * @param area 
	 * @param fileExtension
	 */
	protected void saveFileExtension(Area area, String fileExtension) {
		try {
			
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			
			MiddleResult result = middle.login(login);
			if (result.isOK()) {
				
				// Get area ID.
				long areaId = area.getId();
				
				// Update file extension.
				result = middle.updateAreaFileExtension(areaId, fileExtension);
				
				MiddleResult logoutResult = middle.logout(result);
				if (result.isOK()) {
					result = logoutResult;
				}
			}
			
			if (result.isNotOK()) {
				
				Safe.tryUpdate(AreaEditorBase.this, () -> {
					getTextFileExtension().setText("");
				});
				result.show(getWindowLambda.get());
				return;
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set callback event functions.
	 */
	public void setCallbacks() {
		try {
			
			// Set lambda functions.
			getTextDescription().setUpdateLambda(text -> {
				
				// Update required record.
				Safe.tryOnChange(AreaEditorBase.this, () -> {
					
					saveDescription(area, text);
					// Update application components.
					GeneratorMainFrame.updateAll();	
				});			
			});
			
			getTextAlias().setUpdateLambda(text -> {
				
				// Update required record.
				Safe.tryOnChange(AreaEditorBase.this, () -> {
					
					saveAlias(area, text);
					// Update application components.
					GeneratorMainFrame.updateAll();	
				});			
			});
			
			getTextFolder().setUpdateLambda(text -> {
				
				// Update required record.
				Safe.tryOnChange(AreaEditorBase.this, () -> {
					
					saveFolder(area, text);
					// Update application components.
					GeneratorMainFrame.updateAll();
				});
			});
			
			getTextFileName().setUpdateLambda(text -> {
				
				// Update required record.
				Safe.tryOnChange(AreaEditorBase.this, () -> {
					
					saveFileName(area, text);
					// Update application components.
					GeneratorMainFrame.updateAll();
				});
			});
			
			getTextFileExtension().setUpdateLambda(text -> {
				
				// Update required record.
				Safe.tryOnChange(AreaEditorBase.this, () -> {
					
					saveFileExtension(area, text);
					// Update application components.
					GeneratorMainFrame.updateAll();
				});
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get original description.
	 * @return
	 */
	public String getOriginalDescription() {

		try {
			// Get area description.	
			String text = area.getDescription();
			if (text == null) {
				return "";
			}
			return text;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Get original alias.
	 * @return
	 */
	public String getOriginalAlias() {
		
		try {
			// Get area alias.
			String text = area.getAlias();
			if (text == null) {
				return "";
			}
			return text;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Get original folder.
	 * @return
	 */
	public String getOriginalFolder() {
		
		try {
			// Get area folder.
			String text = area.getFolder();
			if (text == null) {
				return "";
			}
			return text;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Get original file name.
	 * @return
	 */
	public String getOriginalFileName() {
		
		try {
			// Get area folder.
			String text = area.getFileName();
			if (text == null) {
				return "";
			}
			return text;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Get original file extension.
	 * @return
	 */
	public String getOriginalFileExtension() {
		
		try {
			// Get area folder.
			String text = area.getFileExtension();
			if (text == null) {
				return "";
			}
			return text;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Enable/disable editing.
	 * @param flag
	 */
	public void enableEditing(boolean flag) {
		try {
			
			// Enable/disable whole frame.
			GeneratorMainFrame.getFrame().setEnabled(flag);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On is home area action.
	 */
	protected void onIsHomeAreaAction() {
		try {
			
			Window parent = getWindowLambda.get();
	
			// Get state.
			boolean isHomeArea = getCheckBoxHomeArea().isSelected();
			
			// If the global area is a home area, inform user and exit.
			if (area.getId() == 0L && !isHomeArea) {
				
				Safe.tryUpdate(AreaEditorBase.this, () -> {
					getCheckBoxHomeArea().setSelected(true);
				});
				Utility.show(parent, "org.multipage.generator.messageCannotResetGlobalAreaStartFlag");
				return;
			}
			
			long areaId = isHomeArea ? area.getId() : 0L;
			Safe.tryUpdate(AreaEditorBase.this, () -> {
				getCheckBoxHomeArea().setSelected(!isHomeArea);
			});
			
			// Inform user. Let confirm the change.
			if (areaId == 0L) {
				if (JOptionPane.showConfirmDialog(parent,
						Resources.getString("org.multipage.generator.messageGlobalAreaSetAsHome")) != JOptionPane.YES_OPTION) {
					return;
				}
			}
			else {
				if (JOptionPane.showConfirmDialog(parent,
						Resources.getString("org.multipage.generator.messageThisAreaSetAsHome")) != JOptionPane.YES_OPTION) {
					return;
				}
			}
			
			// Set start area.
			MiddleResult result = ProgramBasic.getMiddle().setStartArea(
					ProgramBasic.getLoginProperties(), areaId);
			if (result.isNotOK()) {
				result.show(parent);
				return;
			}
			
			Safe.tryUpdate(AreaEditorBase.this, () -> {
				getCheckBoxHomeArea().setSelected(isHomeArea);
			});
	
			// Set home area nad update information.
			GeneratorMainFrame.getFrame().setHomeArea(parent, areaId);
			GeneratorMainFrame.updateAll();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On is disabled action
	 */
	protected void onIsDisabledAction() {
		try {

			// Get state.
			boolean isDisabled = getCheckBoxIsDisabled().isSelected();
			
			// Save the state
			long areaId = area.getId();
			MiddleResult result = ProgramBasic.getMiddle().setAreaDisabled(
					ProgramBasic.getLoginProperties(), areaId, isDisabled);
			if (result.isNotOK()) {
				result.show(getWindowLambda.get());
				return;
			}
			GeneratorMainFrame.updateAll();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set start area check box.
	 */
	protected void setStartAreaCheckbox() {
		try {
			
			Obj<Long> startAreaId = new Obj<Long>();
			
			// Load flag.
			MiddleResult result = ProgramBasic.getMiddle().loadStartAreaId(
					ProgramBasic.getLoginProperties(), startAreaId);
			if (result.isNotOK()) {
				result.show(getWindowLambda.get());
				return;
			}
			GeneratorMainFrame.updateAll();
			
			// Set the check box.
			Safe.tryUpdate(AreaEditorBase.this, () -> {
				getCheckBoxHomeArea().setSelected(area.getId() == startAreaId.ref);
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}


	/**
	 * On description enter key.
	 */
	protected void onDescriptionEnter() {
		try {
			
			saveDescription();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Select tab.
	 * @param tabIndex
	 */
	public void selectTab(final int tabIndex) {
		try {
			
			getTabbedPane().setSelectedIndex(tabIndex);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	protected JCheckBox getCheckBoxIsStartArea() {
		
		return null;
	}
	
	/**
	 * Method for updating of dialog components.
	 */
	public void updateComponents() {
		try {
			
			// TODO: <---TEST
			j.log("UPDATE AREA EDITOR BASE");
			
			// Update components.
			updateAreaEditor();
			panelResources.updateComponents();
			panelDependencies.updateComponents();
			if (panelConstructor != null) {
				panelConstructor.updateComponents();
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
}
