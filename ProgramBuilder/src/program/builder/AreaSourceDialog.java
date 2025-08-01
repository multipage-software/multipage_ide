/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package program.builder;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SpringLayout;

import org.maclan.AreaResource;
import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.maclan.MimeType;
import org.maclan.VersionObj;
import org.multipage.basic.ProgramBasic;
import org.multipage.generator.AreaResourceRendererBase;
import org.multipage.generator.AreaResourcesEditor;
import org.multipage.generator.ProgramGenerator;
import org.multipage.generator.VersionRenderer;
import org.multipage.gui.Images;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.Safe;

/**
 * Dialog that enables to set area code sources.
 * @author vakol
 *
 */
public class AreaSourceDialog extends JDialog {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Dialog serialized states.
	 */
	private static Rectangle bounds = new Rectangle();

	/**
	 * Set default states.
	 */
	public static void setDefaultData() {
		
		bounds = new Rectangle();
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
	 * Load serialized states.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void serializeData(StateInputStream inputStream)
			throws IOException, ClassNotFoundException {
		
		bounds = Utility.readInputStreamObject(inputStream, Rectangle.class);
	}
	
	/**
	 * Resources panel reference.
	 */
	private AreaResourcesEditor panelResources;
	
	/**
	 * Confirm flag.
	 */
	private boolean confirm = false;
	
	// $hide<<$
	/**
	 * Components.
	 */
	private JButton buttonCancel;
	private JButton buttonOk;
	private JLabel labelStartResourceLabel;
	private JComboBox comboBoxResources;
	private JCheckBox checkNotLocalized;
	private JLabel labelVersion;
	private JComboBox comboBoxVersions;

	/**
	 * Show dialog.
	 * @param parent
	 * @param panelResources 
	 * @param notLocalized 
	 * @param version 
	 * @param areaResource 
	 * @return
	 */
	public static boolean insertDialog(Component parent, AreaResourcesEditor panelResources,
			Obj<AreaResource> areaResource, Obj<VersionObj> version, Obj<Boolean> notLocalized) {
		
		try {
			AreaSourceDialog dialog = new AreaSourceDialog(parent, panelResources);
			dialog.setVisible(true);
			
			if (dialog.confirm) {
				
				// Set output values.
				areaResource.ref = dialog.getSelectedAreaResource();
				version.ref = dialog.getSelectedVersion();
				notLocalized.ref = dialog.checkNotLocalized.isSelected();
				
				return true;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Edit dialog.
	 * @param parent
	 * @param panelResources
	 * @param areaResource
	 * @param version
	 * @param notLocalized
	 * @return
	 */
	public static boolean editDialog(Component parent,
			AreaResourcesEditor panelResources,
			Obj<AreaResource> areaResource, Obj<VersionObj> version,
			Obj<Boolean> notLocalized) {
		
		try {
			AreaSourceDialog dialog = new AreaSourceDialog(parent, panelResources);
			dialog.setComponentValues(areaResource.ref, version.ref, notLocalized.ref);
			
			dialog.setVisible(true);
			
			if (dialog.confirm) {
				
				// Set output values.
				areaResource.ref = dialog.getSelectedAreaResource();
				version.ref = dialog.getSelectedVersion();
				notLocalized.ref = dialog.checkNotLocalized.isSelected();
				
				return true;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Set component values.
	 * @param areaResource
	 * @param version
	 * @param notLocalized
	 */
	private void setComponentValues(AreaResource areaResource, VersionObj version,
			boolean notLocalized) {
		try {
			
			selectAreaResource(areaResource.getId());
			selectVersion(version.getId());
			
			checkNotLocalized.setSelected(notLocalized);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get selected version.
	 * @return
	 */
	private VersionObj getSelectedVersion() {
		
		try {
			Object selectedObject = comboBoxVersions.getSelectedItem();
			if (!(selectedObject instanceof VersionObj)) {
				return null;
			}
		
			return (VersionObj) selectedObject;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Get selected area resource.
	 * @return
	 */
	private AreaResource getSelectedAreaResource() {
		
		try {
			Object selectedObject = comboBoxResources.getSelectedItem();
			if (!(selectedObject instanceof AreaResource)) {
				return null;
			}
			
			return (AreaResource) selectedObject;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Create the dialog.
	 * @param parent 
	 * @param panelResources 
	 */
	public AreaSourceDialog(Component parent, AreaResourcesEditor panelResources) {
		super(Utility.findWindow(parent), ModalityType.APPLICATION_MODAL);
		
		try {
			initComponents();
			
			// $hide>>$
			this.panelResources = panelResources;
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
		setTitle("builder.textAreaSourceDialog");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});
		setBounds(100, 100, 565, 358);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		buttonCancel = new JButton("textCancel");
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		buttonCancel.setMargin(new Insets(0, 0, 0, 0));
		buttonCancel.setPreferredSize(new Dimension(80, 25));
		springLayout.putConstraint(SpringLayout.SOUTH, buttonCancel, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, buttonCancel, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(buttonCancel);
		
		buttonOk = new JButton("textOk");
		buttonOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOk();
			}
		});
		buttonOk.setMargin(new Insets(0, 0, 0, 0));
		buttonOk.setPreferredSize(new Dimension(80, 25));
		springLayout.putConstraint(SpringLayout.SOUTH, buttonOk, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, buttonOk, -6, SpringLayout.WEST, buttonCancel);
		getContentPane().add(buttonOk);
		
		labelStartResourceLabel = new JLabel("builder.textStartResource");
		springLayout.putConstraint(SpringLayout.NORTH, labelStartResourceLabel, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, labelStartResourceLabel, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(labelStartResourceLabel);
		
		comboBoxResources = new JComboBox();
		springLayout.putConstraint(SpringLayout.NORTH, comboBoxResources, 6, SpringLayout.SOUTH, labelStartResourceLabel);
		springLayout.putConstraint(SpringLayout.WEST, comboBoxResources, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, comboBoxResources, -10, SpringLayout.EAST, getContentPane());
		comboBoxResources.setPreferredSize(new Dimension(28, 70));
		getContentPane().add(comboBoxResources);
		
		checkNotLocalized = new JCheckBox("builder.textStartResourceNotLocalized");
		springLayout.putConstraint(SpringLayout.NORTH, checkNotLocalized, 6, SpringLayout.SOUTH, comboBoxResources);
		springLayout.putConstraint(SpringLayout.WEST, checkNotLocalized, 160, SpringLayout.WEST, getContentPane());
		checkNotLocalized.setPreferredSize(new Dimension(211, 25));
		getContentPane().add(checkNotLocalized);
		
		labelVersion = new JLabel("org.multipage.generator.textSelectVersionCombo");
		springLayout.putConstraint(SpringLayout.NORTH, labelVersion, 49, SpringLayout.SOUTH, comboBoxResources);
		springLayout.putConstraint(SpringLayout.WEST, labelVersion, 0, SpringLayout.WEST, labelStartResourceLabel);
		getContentPane().add(labelVersion);
		
		comboBoxVersions = new JComboBox();
		springLayout.putConstraint(SpringLayout.NORTH, comboBoxVersions, 6, SpringLayout.SOUTH, labelVersion);
		springLayout.putConstraint(SpringLayout.WEST, comboBoxVersions, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, comboBoxVersions, -10, SpringLayout.EAST, getContentPane());
		comboBoxVersions.setPreferredSize(new Dimension(280, 60));
		getContentPane().add(comboBoxVersions);
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			
			localize();
			setIcons();
			
			// Load components' content.
			initializeComboBoxes();
			loadResources();
			loadVersions();
			
			loadDialog();
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
			
			buttonOk.setIcon(Images.getIcon("org/multipage/gui/images/ok_icon.png"));
			buttonCancel.setIcon(Images.getIcon("org/multipage/gui/images/cancel_icon.png"));
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
			Utility.localize(buttonOk);
			Utility.localize(buttonCancel);
			Utility.localize(labelStartResourceLabel);
			Utility.localize(checkNotLocalized);
			Utility.localize(labelVersion);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On cancel.
	 */
	protected void onCancel() {
		try {
			
			saveDialog();
			confirm = false;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
		dispose();
	}

	/**
	 * On OK.
	 */
	protected void onOk() {
		try {
			
			saveDialog();
			confirm = true;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
		dispose();
	}

	/**
	 * Load dialog.
	 */
	private void loadDialog() {
		try {
			
			if (bounds.isEmpty()) {
				Utility.centerOnScreen(this);
			}
			else {
				setBounds(bounds);
			}
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
			
			bounds = getBounds();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Initialize combobox.
	 */
	private void initializeComboBoxes() {
		try {
			
			// Set renderer.
			comboBoxResources.setRenderer(new ListCellRenderer() {
				// Renderer object.
				private AreaResourceRendererBase renderer = ProgramGenerator.newAreaResourceRenderer();
				private UnknownRenderer unknown = new UnknownRenderer();
				// Return renderer.
				@Override
				public Component getListCellRendererComponent(JList list, Object value,
						int index, boolean isSelected, boolean cellHasFocus) {
					
					try {
						// Check the object.
						if (!(value instanceof AreaResource)) {
							return unknown;
						}
						// Get resource.
						AreaResource resource = (AreaResource) value;
						MimeType mimeType = panelResources.getMime(resource.getMimeTypeId());
						String namespace = panelResources.getNamespacePath(resource.getParentNamespaceId());
						// Set renderer.
						renderer.setProperties(resource, mimeType.type, namespace, index,
								isSelected, cellHasFocus);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return renderer;
				}
			});
			
			// Create and set renderer.
			comboBoxVersions.setRenderer(new ListCellRenderer<VersionObj>() {
	
				// Label object.
				VersionRenderer renderer = new VersionRenderer();
	
				// Renderer method.
				@Override
				public Component getListCellRendererComponent(
						JList<? extends VersionObj> list, VersionObj value,
						int index, boolean isSelected, boolean cellHasFocus) {
					
					try {
						// Propagate enable state.
						renderer.setEnabledComponents(comboBoxVersions.isEnabled());
						
						if (value == null) {
							renderer.reset();
						}
						else {
							renderer.set(value, index, isSelected, cellHasFocus);
						}
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
	 * Enable versions.
	 * @param enable
	 */
	private void enableVersions(boolean enable) {
		try {
			
			labelVersion.setEnabled(enable);
			comboBoxVersions.setEnabled(enable);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load start resource.
	 */
	private void loadResources() {
		try {
			
			int count = panelResources.getResourcesCount();
			
			// Enable / disable versions combo box.
			enableVersions(count > 0);
			
			// Load combo box.
			for (int index = 0; index < count; index++) {
				AreaResource areaResource = panelResources.getResourceFromIndex(index);
				comboBoxResources.addItem(areaResource);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Load versions.
	 */
	private void loadVersions() {
		try {
			
			comboBoxVersions.removeAllItems();
			
			// Prepare prerequisites.
			Properties login = ProgramBasic.getLoginProperties();
			Middle middle = ProgramBasic.getMiddle();
			
			// Load versions.
			LinkedList<VersionObj> versions = new LinkedList<VersionObj>();
			MiddleResult result = middle.loadVersions(login, 0L, versions);
			
			// On error inform user.
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			// Load versions.
			for (VersionObj version : versions) {
				comboBoxVersions.addItem(version);
			}
			
			// Select default version.
			selectVersion(0L);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Select version.
	 * @param versionId
	 */
	private void selectVersion(long versionId) {
		try {
			
			DefaultComboBoxModel model = (DefaultComboBoxModel) comboBoxVersions.getModel();
			
			for (int index = 0; index < model.getSize(); index++) {
				VersionObj version = (VersionObj) model.getElementAt(index);
				
				if (version.getId() == versionId) {
					comboBoxVersions.setSelectedItem(version);
					return;
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Select area resource.
	 * @param areaResourceId
	 */
	private void selectAreaResource(long areaResourceId) {
		try {
			
			DefaultComboBoxModel model = (DefaultComboBoxModel) comboBoxResources.getModel();
			
			for (int index = 0; index < model.getSize(); index++) {
				AreaResource areaResource = (AreaResource) model.getElementAt(index);
				
				if (areaResource.getId() == areaResourceId) {
					comboBoxResources.setSelectedItem(areaResource);
					return;
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
