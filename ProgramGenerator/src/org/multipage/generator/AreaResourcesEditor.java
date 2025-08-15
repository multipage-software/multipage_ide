/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Component;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CancellationException;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SpringLayout;
import javax.swing.TransferHandler;

import org.maclan.Area;
import org.maclan.AreaResource;
import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.maclan.MiddleUtility;
import org.maclan.MimeType;
import org.maclan.Resource;
import org.multipage.basic.ProgramBasic;
import org.multipage.gui.ProgressDialog;
import org.multipage.gui.ToolBarKit;
import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.ProgressResult;
import org.multipage.util.Resources;
import org.multipage.util.Safe;
import org.multipage.util.SwingWorkerHelper;

/**
 * Editor panel that displays information about area resources.
 * @author vakol
 *
 */
public class AreaResourcesEditor extends JPanel implements SearchableResourcesList, EditorTabActions {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Area reference.
	 */
	private Area area;
	
	/**
	 * Resources model.
	 */
	private DefaultListModel<AreaResource> resourcesModel = new DefaultListModel<>();
	
	/**
	 * MIME types.
	 */
	private LinkedList<MimeType> mimeTypes = new LinkedList<MimeType>();
	
	/**
	 * Namespaces paths.
	 */
	private HashMap<Long, String> namespacesPaths = new HashMap<Long, String>();

	/**
	 * Auxiliary flags.
	 */
	private boolean panelIsReady = false;
	private boolean resourcesLoadedOnStart = false;
	
	// $hide<<$
	/**
	 * Components.
	 */
	private JLabel labelResources;
	private JScrollPane scrollPane;
	private JList<AreaResource> list;
	private JToolBar toolBar;

	/**
	 * Create the panel.
	 */
	public AreaResourcesEditor() {
		
		try {
			// Initialize components.
			initComponents();
			// Post create.
			// $hide>>$
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
		
		labelResources = new JLabel("org.multipage.generator.textResources");
		springLayout.putConstraint(SpringLayout.NORTH, labelResources, 5, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, labelResources, 10, SpringLayout.WEST, this);
		add(labelResources);
		
		scrollPane = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, labelResources);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, this);
		add(scrollPane);
		
		list = new JList<>();
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
					onEditResource();
				}
			}
		});
		scrollPane.setViewportView(list);
		
		toolBar = new JToolBar();
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, 0, SpringLayout.NORTH, toolBar);
		springLayout.putConstraint(SpringLayout.NORTH, toolBar, -30, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.SOUTH, toolBar, 0, SpringLayout.SOUTH, this);
		toolBar.setFloatable(false);
		springLayout.putConstraint(SpringLayout.WEST, toolBar, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, toolBar, -10, SpringLayout.EAST, this);
		add(toolBar);
	}

	/**
	 * Post create.
	 */
	private void postCreate() {
		try {
			
			// Localize components.
			localize();
			// Create tool bar.
			createToolBar();
			// Initialize list.
			initializeList();
			// Set drag and drop.
			setDragAndDrop();
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
			
			Utility.localize(labelResources);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create tool bars.
	 */
	private void createToolBar() {
		try {
			
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/open.png",
					"org.multipage.generator.tooltipLoadResourceFromFile", () -> onLoadResourceFromFile());
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/load_icon.png",
					"org.multipage.generator.tooltipLoadResourceFromDb", () -> onLoadResourceFromDb());
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/load_area_icon.png",
					"org.multipage.generator.tooltipLoadResourceFromArea", () -> onLoadResourceFromArea());
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/add_item_icon.png",
					"org.multipage.generator.tooltipCreateNewTextResource", () -> onCreateNewTextResource());
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/edit.png",
					"org.multipage.generator.tooltipEditResource", () -> onEditResource());
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/remove_icon.png",
					"org.multipage.generator.tooltipDeleteResource", () -> onDeleteResource());
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/edit_text.png",
					"org.multipage.generator.tooltipEditResourceText", () -> onEditText());
			toolBar.addSeparator();
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/properties.png",
					"org.multipage.generator.tooltipResourceProperties", () -> onProperties());
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/show_content.png",
					"org.multipage.generator.tooltipShowResourceContent", () -> onShowContent());
			toolBar.addSeparator();
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/update_icon.png",
					"org.multipage.generator.tooltipReloadResources", () -> updateComponents());
			toolBar.addSeparator();
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/select_all.png",
					"org.multipage.generator.tooltipSelectAllResources", () -> onSelectAll());
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/deselect_all.png",
					"org.multipage.generator.tooltipDeselectAllResources", () -> onDeselectAll());
			toolBar.addSeparator();
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/search_icon.png",
					"org.multipage.generator.tooltipSearchResources", () -> onSearch());
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/areas.png",
					"org.multipage.generator.tooltipShowResourceAreas", () -> onShowResourceAreas());
			toolBar.addSeparator();
			ToolBarKit.addToolBarButton(toolBar, "org/multipage/generator/images/export_icon.png",
					"org.multipage.generator.tooltipExportResourceFiles", () -> exportFiles());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Initialize list.
	 */
	@SuppressWarnings({"serial"})
	private void initializeList() {
		try {
			
			// Set model.
			list.setModel(resourcesModel);
			
			// Set cell renderer.
			list.setCellRenderer(new DefaultListCellRenderer() {
				
				// Cell renderer.
				private AreaResourceRendererBase renderer = ProgramGenerator.newAreaResourceRenderer();
				
				// Call back.
				@Override
				public Component getListCellRendererComponent(JList<?> list,
						Object value, int index, boolean isSelected,
						boolean cellHasFocus) {
					
					try {
						// Get item.
						AreaResource resource = (AreaResource) value;
						// Get MIME type.
						MimeType mimeType = getMime(resource.getMimeTypeId());
						// Get name space path.
						String namespacePath = getNamespacePath(resource.getParentNamespaceId());
						// Set renderer properties.
						renderer.setProperties(resource, mimeType.type, namespacePath,
								index, isSelected, cellHasFocus);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					// Return renderer.
					return renderer;
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load file.
	 * @param fileToLoad
	 * @param localDescription
	 * @return
	 */
	private boolean loadFile(File fileToLoad, String localDescription) {
		
		try {
			final Obj<Boolean> saveAsText = new Obj<Boolean>();
			final Obj<String> encoding = new Obj<String>();
			final Obj<File> file = new Obj<File>(fileToLoad);
			final Obj<AreaResource> resource = new Obj<AreaResource>();
			
			// Get new resource properties.
		    if (!ResourcePropertiesEditor.showDialogForContainer(this, 
		    		resource, file, saveAsText, encoding, null, localDescription)) {
		    	return false;
		    }
		    
			// If the file is null, inform user.
			if (file.ref == null) {
				Utility.show(this, "org.multipage.generator.messageFileNotSpecified");
				return false;
			}
			
			// Check if the file exists.
			if (!file.ref.exists()) {
				 Utility.show(this,
						String.format(Resources.getString("org.multipage.generator.messageFileDoesntExist"),
						file.ref.getName()));
				 return false;
			}
	
			// Create progress dialog.
			ProgressDialog<Resource> progressDialog = new ProgressDialog<Resource>(
					this,
					Resources.getString("org.multipage.generator.textLoadResourceProgressDialog"),
					String.format(Resources.getString("org.multipage.generator.messageLoadingFile"), file.ref.getName()));
			
			// Execute the progress dialog thread.
			ProgressResult progressResult = progressDialog.execute(
					new SwingWorkerHelper<Resource> () {
				
				// Do background process.
				@Override
				protected Resource doBackgroundProcess() throws Exception {
					
					try {
						// Insert resource.
						MiddleResult result = ProgramBasic.getMiddle().insertAreaResource(
								ProgramBasic.getLoginProperties(), area,
								file.ref, saveAsText.ref, encoding.ref, resource.ref, this);
						
						// On error throw exception.
						if (result.isNotOK()) {
							// If is cancelled, throw exception.
							if (isScheduledCancel()) {
								throw new CancellationException();
							}
							else {
								throw new SQLException(result.getMessage());
							}
						}
						
					    // Return value.
						return resource.ref;
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return null;
				}
			});
					
			// If result is OK.
			if (progressResult == ProgressResult.OK) {
	
			    loadList();
			    return true;
			}
			// If it is an execution exception, show it.
			else if (progressResult == ProgressResult.EXECUTION_EXCEPTION) {
				// Show result message.
				Utility.show2(progressDialog.getException().getLocalizedMessage());
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Load resource.
	 */
	public void onLoadResourceFromFile() {
		try {
			
			loadFile(null, null);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Add MIME and namespace.
	 */
	private MiddleResult loadMimeAndNamespace(Resource resource) {
		
		try {
			// Get prerequisites.
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
		    
		    // Get MIME type object.
		    MimeType mimeType = new MimeType();
		    
		    MiddleResult result = middle.loadMimeType(login, resource.getMimeTypeId(),
		    		mimeType);
		    if (result.isNotOK()) {
		    	return result;
		    }
		    
		    // Add MIME.
		    addMime(mimeType);
		    
		    // Get names pace path.
		    long namespaceId = resource.getParentNamespaceId();
		    Obj<String> namespacePath = new Obj<String>();
		    
		    result = middle.loadNameSpacePath(login, namespaceId,
		    		namespacePath, " > ");
		    if (result.isNotOK()) {
		    	return result;
		    }
		    
		    // Add name space path.
		    namespacesPaths.put(namespaceId, namespacePath.ref);
		    
		    return MiddleResult.OK;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return MiddleResult.UNKNOWN_ERROR;
	}
	
	/**
	 * Add resource to the list.
	 * @param resource
	 */
	private MiddleResult addResourceToList(AreaResource resource) {
	    
		try {
			MiddleResult result = loadMimeAndNamespace(resource);
		    resourcesModel.addElement(resource);
		    
		    return result;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return MiddleResult.UNKNOWN_ERROR;
	}

	/**
	 * Get MIME type.
	 * @param id
	 */
	public MimeType getMime(long id) {
		
		try {
			return MiddleUtility.getListItem(mimeTypes, id);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Get names pace path.
	 * @param id
	 */
	public String getNamespacePath(long id) {
		
		try {
			return namespacesPaths.get(id);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Add MIME type.
	 * @param mimeType
	 */
	private void addMime(MimeType mimeType) {
		try {
			
			MimeType oldMimeType = getMime(mimeType.id);
			
			if (oldMimeType == null) {
				mimeTypes.add(mimeType);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Load resource from list.
	 * @param selectedResources
	 */
	private void loadResourcesFromList(LinkedList<Resource> selectedResources) {
		try {
			
			final AreaResource areaResource = new AreaResource();
			
			for (Resource resource : selectedResources) {
				
				// Get old local description.
				String oldLocalDescription = null;
				if (resource instanceof AreaResource) {
					oldLocalDescription = ((AreaResource) resource).getLocalDescription();
				}
				if (oldLocalDescription == null) {
					oldLocalDescription = "";
				}
				
				areaResource.setFrom(resource);
				
				// Get local description.
				String localDescription = JOptionPane.showInputDialog(this,
						String.format(Resources.getString("org.multipage.generator.messageInsertResourceLocalDescription"),
								resource.getDescription()),
							 	oldLocalDescription);
				if (localDescription == null) {
					areaResource.setLocalDescription("");
				}
				else {
					areaResource.setLocalDescription(localDescription);
				}
				
				// Add resource to the container.
				Middle middle = ProgramBasic.getMiddle();
				MiddleResult result;
				Properties login = ProgramBasic.getLoginProperties();
				
				result = middle.insertResourceRecordToContainer(login, areaResource,
						area);
				if (result.isNotOK()) {
					result.show(this);
					continue;
				}
			}
			
			loadList();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Load resource from database.
	 */
	public void onLoadResourceFromDb() {
		try {
			
			// Get resource from the database.
			LinkedList<Resource> selectedResources = new LinkedList<Resource>();
			if (!ResourcesEditorDialog.showDialog(this, selectedResources)) {
				return;
			}
			
			loadResourcesFromList(selectedResources);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Load resource from area.
	 */
	public void onLoadResourceFromArea() {
		try {
			
			// Get resource from area.
			LinkedList<Resource> selectedResources = new LinkedList<Resource>();
			if (!AreaResourcesDialog.showDialog(this, selectedResources)) {
				return;
			}
			
			loadResourcesFromList(selectedResources);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get resource from the list.
	 * @param resourceId
	 * @return
	 */
	AreaResource getResource(long resourceId) {
		
		try {
			for (int index = 0; index < resourcesModel.size(); index++) {
				
				AreaResource resource = (AreaResource) resourcesModel.getElementAt(index);
				if (resource.getId() == resourceId) {
					return resource;
				}
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Load resources list.
	 */
	private void loadList() {
		try {
			
			// Clear the model.
			resourcesModel.clear();
			
			// If there is no area, exit the method.
			if (area == null) {
				return;
			}
			
			// Reload area object.
			area = ProgramGenerator.getArea(area.getId());
	
			// Check container reference.
			if (area == null) {
				Utility.show(this,
						Resources.getString("org.multipage.generator.messageContainerReferenceIsNull"));
			}
			
			// Get selected area resources.
			List<AreaResource> selectedResources = list.getSelectedValuesList();
			
			// Create resources list.
			LinkedList<AreaResource> resources = new LinkedList<AreaResource>();
					
			// Load resources from the database.
			ProgressDialog<MiddleResult> progressDialog = new ProgressDialog<MiddleResult>(this,
					Resources.getString("org.multipage.generator.textLoadingResources"),
					Resources.getString("org.multipage.generator.textLoadingResources"));
			
			ProgressResult progressResult = progressDialog.execute(new SwingWorkerHelper<MiddleResult>() {
	
				@Override
				protected MiddleResult doBackgroundProcess() throws Exception {

					// Read area resources from the database.
					MiddleResult result = ProgramBasic.getMiddle().loadAreaResources(
							ProgramBasic.getLoginProperties(), area,
							resources, this);
					
					if (result.isNotOK()) {
						return result;
					}
					
					boolean isExtensionToBuilder = ProgramGenerator.isExtensionToBuilder();
					
					// Load resources to the list.
					for (AreaResource resource : resources) {
						
						// Do not load protected resources in the generator application.
						if (!isExtensionToBuilder && resource.isProtected()) {
							continue;
						}
						
						result = addResourceToList(resource);
						if (result.isNotOK()) {
							return result;
						}
					}
					
					// Restore selection.
					boolean isFirst = true;
					
					for (AreaResource selectedResource : selectedResources) {
						for (AreaResource resource : resources) {
							
							if (resource.getExtension().getId() == selectedResource.getExtension().getId()) {
								list.setSelectedValue(selectedResource, isFirst);
								isFirst = false;
							}
						}
					}
					return MiddleResult.OK;
				}
			});
			
			// Process result.
			if (progressResult == ProgressResult.OK) {
				MiddleResult result = progressDialog.getOutput();
				
				if (result.isNotOK()) {
					result.show(this);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * @param container the container to set
	 */
	public void setArea(Area area) {
		
		this.area = area;
	}
	
	/**
	 * On edit resource.
	 */
	public void onEditResource() {
		try {
			
			// Get selected resources.
			List<AreaResource> selectedResources = list.getSelectedValuesList();
			// If nothing selected, inform user and exit the method.
			if (selectedResources.size() != 1) {
				Utility.show(this, "org.multipage.generator.messageSelectSingleResourceForEditing");
				return;
			}
	
			// Get selected resource.
			AreaResource selectedResource = (AreaResource) selectedResources.get(0);
			// Get resource copy.
			AreaResource resourceCopy = selectedResource.clone();
			
			Obj<AreaResource> resource = new Obj<AreaResource>(resourceCopy);
			final Obj<File> file = new Obj<File>();
			final Obj<Boolean> saveAsText = new Obj<Boolean>();
			final Obj<String> encoding = new Obj<String>();
			Obj<Resource> resourceToAssign = new Obj<Resource>();
			
			// Edit resource.
			if (!ResourcePropertiesEditor.showDialogForContainer(this,
					resource, file, saveAsText, encoding, resourceToAssign, null)) {
				return;
			}
			
			resourceCopy.setParentNamespaceId(resource.ref.getParentNamespaceId());
			
			final Middle middle = ProgramBasic.getMiddle();
			final Properties login = ProgramBasic.getLoginProperties();
			MiddleResult result;
			
			// If exists resource to assign.
			if (resourceToAssign.ref != null) {
				
				// Login to the database.
				result = middle.login(login);
				if (result.isOK()) {
					
					// Update area resource.
					result = middle.updateAreaResourceSimple(resource.ref.getExtension().getId(),
							resourceToAssign.ref.getId(),
							resource.ref.getLocalDescription());
					
					// If the resource is not visible and it can be deleted, ask user if to delete the resource.
					if (!selectedResource.isVisible()) {
						
						long resourceId = selectedResource.getId();
						String resourceDescription = selectedResource.getDescription();
						
						Obj<Boolean> isOrphan = new Obj<Boolean>();
						result = middle.selectAreaResourceIsOrphan(resourceId, isOrphan);
						
						if (result.isOK()) {
							
							if (isOrphan.ref) {
								if (Utility.ask(this, "org.multipage.generator.messageRemoveAreaResource", resourceDescription)) {
									
									// Remove resource.
									result = middle.removeResource(resourceId);
								}
								else {
									
									// Set resource visible.
									result = middle.updateResourceVisibiliy(resourceId, true);
									if (result.isOK()) {
										
										Utility.show(this, "org.multipage.generator.messageResourceSetAsVisible", resourceDescription);
									}
								}
							}
						}
					}
					
					// Logout from database.
					MiddleResult logoutResult = middle.logout(result);
					if (result.isOK()) {
						
						result = logoutResult;
					}
				}
			}
			else {
				
				// If there is no new file.
				if (file.ref == null) {
					// Update resource.
					result = middle.updateAreaResourceNoFile(login, resource.ref);
				}
				else {
					final AreaResource resourceCopyRef = resourceCopy;
					
					// Create progress dialog.
					ProgressDialog<Resource> dialog = new ProgressDialog<Resource>(
							this, Resources.getString("org.multipage.generator.textLoadResourceProgressDialog"),
							String.format(Resources.getString("org.multipage.generator.messageLoadingFile"), file.ref.getName()));
					
					// Execute new thread.
					ProgressResult progressResult = dialog.execute(new SwingWorkerHelper<Resource>() {
						// On background process.
						@Override
						protected Resource doBackgroundProcess() throws Exception {
							
							// Update resource.
							MiddleResult result = middle.updateAreaResource(login,
									resourceCopyRef,
									file.ref, saveAsText.ref, encoding.ref, this);
							
							// On error throw an exception.
							if (result.isNotOK()) {
								throw new Exception(result.getMessage());
							}
							// Return resource reference.
							return resourceCopyRef;
						}
					});
					
					if (progressResult == ProgressResult.OK) {
						result = MiddleResult.OK;
					}
					else if (progressResult == ProgressResult.EXECUTION_EXCEPTION) {
						Utility.show2(dialog.getException().getLocalizedMessage());
						return;
					}
					else {
						return;
					}
				}
			}
			
			// On error inform user.
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			// Set selected resource
			selectedResource.setFrom(resourceCopy);
			result = loadMimeAndNamespace(selectedResource);
			if (result.isNotOK()) {
				result.show(this);
			}
			
			// Set flag.
			if (saveAsText.ref != null) {
				selectedResource.setSavedAsText(saveAsText.ref);
			}
			
			// Load resource image.
			result = middle.loadResourceImage(login, selectedResource);
			
			if (result.isNotOK()) {
				result.show(this);
			}
			
			// Reload the list.
			loadList();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Delete resources.
	 */
	public void onDeleteResource() {
		try {
			
			// Get selected resource.
			List<AreaResource> selectedResources = list.getSelectedValuesList();
			LinkedList<Resource> resources = new LinkedList<Resource>();
			
			for (Resource selectedResource : selectedResources) {
				resources.add(selectedResource);
			}
			
			// Inform user.
			if (resources.size() == 0) {
				Utility.show(this, "org.multipage.generator.messageSelectResources");
				return;
			}
			
			// Ask user.
			if (JOptionPane.showConfirmDialog(this, 
					Resources.getString("org.multipage.generator.messageRemoveSelectedResources"))
					!= JOptionPane.YES_OPTION) {
				return;
			}
			
			Obj<Boolean> removed = new Obj<Boolean>();
			
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			MiddleResult result = middle.removeResourcesFromContainer(login, resources,
					area, removed);
			if (result.isNotOK()) {
				result.show(this);
			}
			
			// Reload the list.
			loadList();
			
			// If not removed from the resource table, inform user.
			if (!removed.ref) {
				Utility.show(this, "org.multipage.generator.messageResourceNotRemovedFromTable");
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On select all.
	 */
	public void onSelectAll() {
		try {
			
			list.setSelectionInterval(0, list.getModel().getSize() - 1);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On deselect all.
	 */
	public void onDeselectAll() {
		try {
			
			list.clearSelection();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get resources count.
	 * @return
	 */
	public int getResourcesCount() {
		
		try {
			return resourcesModel.getSize();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0;
	}

	/**
	 * Get resource from index
	 * @param index
	 * @return
	 */
	public AreaResource getResourceFromIndex(int index) {
		
		try {
			Object item = resourcesModel.get(index);
			if (!(item instanceof AreaResource)) {
				return null;
			}
			
			return (AreaResource) item;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * On edit text.
	 */
	public void onEditText() {
		try {
			
			// Get selected text.
			AreaResource resource = (AreaResource) list.getSelectedValue();
			
			// Check the resource and inform user.
			if (resource == null || !resource.isSavedAsText()) {
				Utility.show(this, "org.multipage.generator.messageSelectResourceSavedAsText");
				return;
			}
			
			// Edit the resource.
			TextResourceEditorFrame.showDialog(GeneratorMainFrame.getFrame(),
					resource.getId(), resource.isSavedAsText(), true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On create new text resource.
	 */
	public void onCreateNewTextResource() {
		try {
			
			// Get new resource content.
			Obj<String> text = new Obj<String>();
			Obj<MimeType> mimeType = new Obj<MimeType>();
			
			if (!SelectNewTextResourceDialog.showDialog(this, text, mimeType)) {
				return;
			}
			
			AreaResource resource = new AreaResource();
			resource.setProtected(true);
			
			// Get new text resource properties.
		    if (!ResourcePropertiesEditor.showDialogForNewResource(this, 
		    		resource, mimeType)) {
		    	return;
		    }
	
			// Insert new text resource.
			MiddleResult result = ProgramBasic.getMiddle().insertResourceToContainerText(
					ProgramBasic.getLoginProperties(), area,
					resource, text.ref);
			if (result.isNotOK()) {
				result.show(this);
			}
			
		    // Add resource to the list.
		    result = addResourceToList(resource);
		    if (result.isNotOK()) {
				result.show(this);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set drag and drop.
	 */
	@SuppressWarnings("serial")
	private void setDragAndDrop() {
		try {
			
			final Component thisComponent = this;
			
			list.setTransferHandler(new TransferHandler() {
				// Can import.
				@Override
				public boolean canImport(TransferSupport support) {
					return true;
				}
				// Import data.
				@SuppressWarnings("unchecked")
				@Override
				public boolean importData(TransferSupport support) {
					
					try {
						// If it is not a drop operation, exit the method with
						// false value.
						if (!support.isDrop()) {
							return false;
						}
						
						// Get the string that is dropped.
				        Transferable transferable = support.getTransferable();
						java.util.List<File> fileList;
				        try {
				            fileList = (java.util.List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
				        } 
				        catch (Exception e) {
				        	return false;
				        }
				        
				        // Load files depending on selected list items.
						List<AreaResource> selectedItems = list.getSelectedValuesList();
						int action = support.getDropAction();
						
				        if (action != TransferHandler.COPY && selectedItems.size() == 1 && fileList.size() == 1) {
				        	
				        	// Remove selected area resource and load new resource with the same
				        	// local name.
				        	AreaResource areaResource = (AreaResource) selectedItems.get(0);
				        	String localDescription = areaResource.getLocalDescription();
				        	File file = fileList.get(0);
				        	
				        	// Ask user.
				        	if (!Utility.ask(thisComponent, "org.multipage.generator.messageReplaceAreaResource",
				        			localDescription, file.getName())) {
				        		return false;
				        	}
		
				    		// Add new area resource with given local name from the file.
				    		if (!loadFile(file, localDescription)) {
				    			return false;
				    		}
				    		
				        	// Delete area resource.
				    		Obj<Boolean> removed = new Obj<Boolean>();
				    		
				    		Middle middle = ProgramBasic.getMiddle();
				    		Properties login = ProgramBasic.getLoginProperties();
				    		
				    		// Create one element list.
				    		LinkedList<Resource> resources = new LinkedList<Resource>();
				    		resources.add(areaResource);
				    		
				    		MiddleResult result = middle.removeResourcesFromContainer(login, resources,
				    				area, removed);
				    		if (result.isNotOK()) {
				    			result.show(thisComponent);
				    		}
				    		
				    		// If not removed from the resource table, inform user.
				    		if (!removed.ref) {
				    			Utility.show(thisComponent, "org.multipage.generator.messageResourceNotRemovedFromTable");
				    		}
				        }
				        else {
							// Do loop for all files.
							for (File file : fileList) {
								
								if (!loadFile(file, null)) {
									break;
								}
							}
				        }
				        
				        return true;
				    }
					catch (Throwable e) {
						Safe.exception(e);
					}
					return false;
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Export resources to files.
	 */
	public void exportFiles() {
		try {
			
			// Get selected resources.
			List<AreaResource> selectedResources = list.getSelectedValuesList();
			LinkedList<AreaResource> resources = new LinkedList<AreaResource>();
			
			for (AreaResource selectedResource : selectedResources) {
				resources.add(selectedResource);
			}
			
			// Inform user.
			if (resources.size() == 0) {
				Utility.show(this, "org.multipage.generator.messageSelectResources");
				return;
			}
	
			// Get export path.
			String path = Utility.chooseDirectory(this, 
					Resources.getString("org.multipage.generator.textChooseResourcesExportDirectory"));
			if (path == null) {
				return;
			}
			
			Properties login = ProgramBasic.getLoginProperties();
			Middle middle = ProgramBasic.getMiddle();
			
			// Do loop for all selected resources.
			for (AreaResource resource : resources) {
				// Get resource name.
				String resourceName = String.format("%s[%d]", resource.getDescription(),
						resource.getId());
				// Add local description.
				String localDescription = resource.getLocalDescription();
				if (!localDescription.isEmpty()) {
					resourceName = String.format("%s.%s", localDescription, resourceName);
				}
				// Get resource MIME type.
				MimeType mimeType = new MimeType();
				MiddleResult result = middle.loadMimeType(login,
						resource.getMimeTypeId(), mimeType);
				if (result.isOK()) {
					// Append MIME type.
					resourceName = String.format("%s.%s", resourceName, mimeType.extension);
				}
				
				// Create file object.
				File file = new File(path + "/" + resourceName);
				OutputStream outputStream = null;
				try {
					outputStream = new FileOutputStream(file);
				}
				catch (FileNotFoundException e) {
					Utility.show2("org.multipage.generator.messageErrorCreatingFile", file.toString());
					return;
				}
				
				// Output the resource.
				result = middle.loadResourceToStream(login, resource, outputStream);
				
				// Close stream.
				try {
					outputStream.close();
				}
				catch (IOException e) {
					Utility.show2("org.multipage.generator.messageErrorClosingFile", file.toString());
					return;
				}
				
				if (result.isNotOK()) {
					result.show(this);
					return;
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On properties.
	 */
	public void onProperties() {
		try {
			
			// Get first selected resource and show its properties.
			List<AreaResource> resources = getSelectedResources();
			if (resources.isEmpty()) {
				Utility.show(this, "org.multipage.generator.messageSelectSingleResource");
				return;
			}
			
			ShowResourceImageProperties.showDialog(this, resources.get(0));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On show content.
	 */
	public void onShowContent() {
		try {
			
			// Get first selected resource and show its properties.
			List<AreaResource> resources = getSelectedResources();
			if (resources.isEmpty()) {
				Utility.show(this, "org.multipage.generator.messageSelectSingleResource");
				return;
			}
			
			Resource resource = resources.get(0);
			
			if (!resource.isSavedAsText()) {
				ShowResourceContent.showDialog(this, resource);
			}
			else {
				// Edit the resource.
				TextResourceEditorFrame.showDialog(GeneratorMainFrame.getFrame(),
						resource.getId(), true, true);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Gets selected resources.
	 * @return
	 */
	public List<AreaResource> getSelectedResources() {
		
		try {
			return list.getSelectedValuesList();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Search resources.
	 */
	public void onSearch() {
		try {
			
			// Get search parameters.
			ResourceSearch.showDialog(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get this window.
	 */
	@Override
	public Window getWindow() {
		
		try {
			return Utility.findWindow(this);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Get MIME types.
	 */
	@Override
	public LinkedList<MimeType> getMimeTypes() {
		
		return mimeTypes;
	}

	/**
	 * Get list.
	 */
	@Override
	public JList<AreaResource> getList() {
		
		return list;
	}

	/**
	 * Informs that panel is ready.
	 */
	public void panelIsReady() {
		
		panelIsReady = true;
	}

	/**
	 * On load panel information.
	 */
	@Override
	public void onLoadPanelInformation() {
		try {
			
			// Load resources list.
			if (!panelIsReady && !resourcesLoadedOnStart) {
				
				loadList();
				resourcesLoadedOnStart = true;
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On save panel information.
	 */
	@Override
	public void onSavePanelInformation() {

	}
	
	/**
	 * Show resource areas.
	 */
	public void onShowResourceAreas() {
		try {
			
			// Get first selected resource and show its properties.
			List<AreaResource> resources = getSelectedResources();
			if (resources.isEmpty()) {
				Utility.show(this, "org.multipage.generator.messageSelectSingleResource");
				return;
			}
			
			Resource resource = resources.get(0);
			
			ResourceAreasDialog.showDialog(this, resource, null);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load area.
	 * @param area
	 */
	public void loadArea(Area area) {
		try {
			
			setArea(area);
			loadList();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get area.
	 * @return
	 */
	public Area getArea() {
		
		return area;
	}

	/**
	 * On close the window.
	 */
	public void onClose() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Method for updating of dialog components.
	 */
	public void updateComponents() {
		try {
			
			// Reload the list.
			loadList();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
}
