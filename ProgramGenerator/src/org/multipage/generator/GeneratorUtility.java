/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.apache.commons.imaging.Imaging;
import org.maclan.Area;
import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.maclan.MimeType;
import org.maclan.Slot;
import org.maclan.SlotType;
import org.maclan.server.AreaServer;
import org.multipage.basic.ProgramBasic;
import org.multipage.generator.ProgramPaths.PathSupplier;
import org.multipage.gui.RendererPathItem;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.Safe;

/**
 * Utility functions for Generator.
 * @author vakol
 *
 */
public class GeneratorUtility {

	/**
	 * Current path names.
	 */
	public static String currentResourcePathName;
	public static String currentImagePathName;

	/**
	 * Read serialized data.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void serializeData(StateInputStream inputStream)
		throws IOException, ClassNotFoundException {
		
		GeneratorUtility.currentResourcePathName = inputStream.readUTF();
		GeneratorUtility.currentImagePathName = inputStream.readUTF();
	}

	/**
	 * Write serialized data.
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream)
		throws IOException {

		// Write current paths.
		outputStream.writeUTF(GeneratorUtility.currentResourcePathName);
		outputStream.writeUTF(GeneratorUtility.currentImagePathName);
	}

	/**
	 * Set default data.
	 */
	public static void setDefaultData() {

		currentResourcePathName = "";
		currentImagePathName = "";
	}

	/**
	 * Initialize MIME types.
	 * @param resourceName 
	 */
	public static void loadMimeAndSelect(String resourceName, JComboBox comboBoxMime) {
		try {
			
			// Load combobox.
			ArrayList<MimeType> mimeTypes = ProgramGenerator.getAreasModel().getMimeTypes();
			
			// Load MIMEs.
			if (!loadMime(comboBoxMime, mimeTypes)) {
				return;
			}
			
			// Get resource extension.
			String extension = Utility.getExtension(resourceName);
			// Set selection.
			MimeType selectedMimeType = MimeType.getMimeWithExtension(mimeTypes, extension);
			comboBoxMime.getModel().setSelectedItem(selectedMimeType);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Select MIME type.
	 * @param comboBoxMime
	 * @param mimeType
	 */
	public static void selectMime(JComboBox comboBoxMime, MimeType mimeType) {
		try {
			
			ComboBoxModel model = comboBoxMime.getModel();
			int size = model.getSize();
			
			// Do loop for all model items.
			for (int index = 0; index < size; index++) {
				
				MimeType mimeTypeItem = (MimeType) model.getElementAt(index);
				if (mimeTypeItem.equals(mimeType)) {
					
					// Select the item and exit the method.
					comboBoxMime.setSelectedIndex(index);
					break;
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Load MIME types.
	 * @param mimeTypeId
	 * @param comboBoxMime
	 */
	public static void loadMimeAndSelect(long mimeTypeId, JComboBox comboBoxMime) {
		try {
			
			// Load combo box.
			ArrayList<MimeType> mimeTypes = ProgramGenerator.getAreasModel().getMimeTypes();
			
			// Load MIMEs.
			if (!loadMime(comboBoxMime, mimeTypes)) {
				return;
			}
			
			// Find and select given MIME.
			MimeType defaultMimeType = null;
			
			for (MimeType mimeType : mimeTypes) {
				
				if (mimeType.id == mimeTypeId) {
					comboBoxMime.getModel().setSelectedItem(mimeType);
					return;
				}
				
				// Get default MIME with ID = 0L.
				if (mimeType.id == 0L) {
					defaultMimeType = mimeType;
				}
			}
			
			// If MIME not found, select default.
			if (defaultMimeType != null) {
				comboBoxMime.getModel().setSelectedItem(defaultMimeType);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load MIME types.
	 * @param mimeTypes 
	 * @param mimeTypes
	 */
	@SuppressWarnings("unchecked")
	private static boolean loadMime(JComboBox comboBoxMime, ArrayList<MimeType> mimeTypes) {

		try {
			DefaultComboBoxModel modelMimeTypes;
			modelMimeTypes = new DefaultComboBoxModel(mimeTypes.toArray());
			comboBoxMime.setModel(modelMimeTypes);
	
			
			// Set renderer.
			comboBoxMime.setRenderer(new ListCellRenderer() {
				/**
				 * Rendering component.
				 */
				private JLabel renderingComponent = new JLabel();
				
				@Override
				public Component getListCellRendererComponent(JList list, Object value,
						int index, boolean isSelected, boolean cellHasFocus) {
					
					// Set opaque.
					renderingComponent.setOpaque(true);
					// Check parameters.
					if (value == null || !(value instanceof MimeType)) {
						renderingComponent.setText("");
					}
					else {
						// Get MIME type.
						MimeType mimeType = (MimeType) value;
						// Set renderer.
						renderingComponent.setText(mimeType.type + " [" + mimeType.extension + "]");
						// Set color.
						Color color = mimeType.preference ? Color.RED : Color.BLACK;
						renderingComponent.setForeground(color);
						renderingComponent.setBackground(isSelected ? list.getSelectionBackground()
			            		: list.getBackground());
					}
					
					return renderingComponent;
				}
			});
			
			return true;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Choose resource file.
	 * @param path 
	 * @return
	 */
	public static File chooseFile(Component parentComponent, String pathName,
			boolean useDefaultFirstSelectedFilter) {
		
		try {
			// If the path name is null, set current path name.
			if (pathName == null) {
				pathName = currentResourcePathName;
			}
			
			// Select resource file.
			JFileChooser dialog = new JFileChooser(pathName);
			
			// List filters.
			String [][] filters = {{"org.multipage.generator.textHtmlFile", "htm", "html", "htmls", "shtml"},
					               {"org.multipage.generator.textJavaScriptFile", "js"},
					               {"org.multipage.generator.textCssFile", "css"},
					               {"org.multipage.generator.textTextFile", "txt", "text"},
					               {"org.multipage.generator.textXmlFiles", "xml"},
					               {"org.multipage.generator.textIconFile", "ico"},
					               {"org.multipage.generator.textBmpFile", "bmp"},
					               {"org.multipage.generator.textGifFile", "gif"},
					               {"org.multipage.generator.textJpegFile", "jpg", "jpeg"},
					               {"org.multipage.generator.textPngFile", "png"},
					               {"org.multipage.generator.textTiffFile", "tif", "tiff"},
					               {"org.multipage.generator.textJavaClassFile", "class"}};
			
			// Add filters.
			Utility.addFileChooserFilters(dialog, pathName, filters, useDefaultFirstSelectedFilter);
							
			// Open dialog.
		    if(dialog.showOpenDialog(parentComponent) != JFileChooser.APPROVE_OPTION) {
		       return null;
		    }
		    
		    // Get selected file.
		    File file = dialog.getSelectedFile();
		    
		    // Set current path name.
		    if (file != null) {
		    	currentResourcePathName = file.getParent();
		    }
	
		    return file;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Choose resource file and save method.
	 * @return
	 */
	public static File chooseFileAndSaveMethod(Component parentComponent,
			Obj<Boolean> saveAsText, Obj<String> encoding) {
	    
		try {
		    // Get selected file.
		    File file = chooseFile(parentComponent, null, false);
		    if (file == null) {
		    	return null;
		    }
	
		    // Select saving method.
		    if (!SelectResourceSavingMethod.showDialog(parentComponent,
		    		file, saveAsText, encoding)) {
		    	return null;
		    }
	
		    return file;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Load image from disk.
	 * @param parentComponent
	 * @return
	 */
	public static BufferedImage loadImageFromDisk(Component parentComponent) {
		
		try {
			// Select resource file.
			JFileChooser dialog = new JFileChooser(currentImagePathName);
			
			// List filters.
			String [][] filters = {{"org.multipage.generator.textIconFile", "ico"},
					               {"org.multipage.generator.textBmpFile", "bmp"},
					               {"org.multipage.generator.textGifFile", "gif"},
					               {"org.multipage.generator.textJpegFile", "jpg", "jpeg"},
					               {"org.multipage.generator.textPngFile", "png"},
					               {"org.multipage.generator.textTiffFile", "tif", "tiff"}};
			
			// Add filters.
			Utility.addFileChooserFilters(dialog, currentImagePathName, filters, false);
							
			// Open dialog.
		    if(dialog.showOpenDialog(parentComponent) != JFileChooser.APPROVE_OPTION) {
		       return null;
		    }
		    
		    // Get selected file.
		    File file = dialog.getSelectedFile();
		    
		    // Set current path name.
		    currentImagePathName = file.getParent();
		    
		    BufferedImage image;
			try {
				image = Imaging.getBufferedImage(file);
			}
			catch (Exception e) {
				return null;
			}
		    
		    return image;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Get MIME type.
	 * @param type
	 * @param extension
	 * @return
	 */
	public static MimeType getMimeType(String type, String extension) {
		
		try {
			// Get MIME types.
			ArrayList<MimeType> mimeTypes = ProgramGenerator.getAreasModel().getMimeTypes();
			MimeType defaultMimeType = null;
			
			// Try to find corresponding MIME type.
			for (MimeType mimeType : mimeTypes) {
				
				// Return found MIME type.
				if (mimeType.equals(type, extension)) {
					return mimeType;
				}
				
				if (defaultMimeType == null && mimeType.type.equals("def")) {
					defaultMimeType = mimeType;
				}
			}
			
			// Use default MIME type.
			if (defaultMimeType == null) {
				defaultMimeType = mimeTypes.isEmpty() ? new MimeType() : mimeTypes.get(0);
			}
			
			return defaultMimeType;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Load all path slots.
	 * @param area
	 * @return
	 */
	public static LinkedList<Slot> getAllPathSlots(Middle middle, Area area)
		throws Exception {
		
		LinkedList<Slot> pathSlots = new LinkedList<Slot>();
		LinkedList<Long> currentPathSlotIds = new LinkedList<Long>();
		LinkedList<Slot> currentPathSlots = new LinkedList<Slot>();
		HashSet<Area> visitedAreas = new HashSet<Area>();
		
		// Load all path slots in super areas.
		LinkedList<Area> queue = new LinkedList<Area>();
		queue.add(area);
		
		while (!queue.isEmpty()) {
			
			// Pop queue item.
			Area currentArea = queue.removeFirst();
			
			// It the area has been visited, do nothing.
			if (visitedAreas.contains(currentArea)) {
				continue;
			}
			visitedAreas.add(currentArea);
			
			// Get area ID.
			long areaId = currentArea.getId();
			
			// Get path slots.
			MiddleResult result = middle.loadPathSlotsIds(areaId, currentPathSlotIds);
			result.throwPossibleException();
			
			if (!currentPathSlotIds.isEmpty()) {
				
				// Load area slot references.
				result = middle.loadAreaSlotsRefData(currentArea);
				
				currentPathSlots.clear();
				
				for (Long slotId : currentPathSlotIds) {
					
					Slot pathSlot = currentArea.getSlot(slotId);
					if (pathSlot != null) {
						currentPathSlots.add(pathSlot);
					}
				}
				pathSlots.addAll(currentPathSlots);
			}
			
			// Check super area.
			if (currentArea.isProjectRoot() || currentArea.isBasic()) {
				continue;
			}
			
			// Get super areas.
			LinkedList<Area> superAreas = currentArea.getSuperareas();
			queue.addAll(superAreas);
		}
		
		return pathSlots;
	}

	/**
	 * Load area paths.
	 * @param comboBoxPaths
	 * @param area 
	 */
	public static void loadAreaPaths(JComboBox<PathSupplier> comboBoxPaths, Area area) {
		
		try {
			// Get current middle layer instance.
			Middle middle = ProgramBasic.loginMiddle();
			
			// Get area ID.
			long areaId = area.getId();
			
			// Get area server.
			AreaServer areaServer = new AreaServer();
			areaServer.setMiddle(middle);
			
			// Load all path slots.
			LinkedList<Slot> pathSlots = getAllPathSlots(middle, area);
			
			// Solve each path.
			for (Slot pathSlot : pathSlots) {
				
				String slotAlias = pathSlot.getAlias();
				String description = pathSlot.getNameForGenerator();
				
				// Area server tag for the slot.
				String areaServerTag = String.format("[@TAG %s]", slotAlias);
				
				try {
					// Try to solve path.
					String solvedPath = areaServer.loadAreaText(areaId, 0L, areaServerTag);
					
					// Add new item to the combobox.
					PathSupplier pathSupplier = PathSupplier.newAreaPath(description, areaServerTag, solvedPath);
					comboBoxPaths.addItem(pathSupplier);
				}
				catch (Exception e) {
				}
			}
		}
		catch (Exception e) {
			Utility.show2(comboBoxPaths, e.getLocalizedMessage());
		}
		finally {
			ProgramBasic.logoutMiddle();
		}
	}

	/**
	 * Load slot types into combo box.
	 * @param comboSlotType
	 */
	public static void loadSlotTypesCombo(JComboBox<SlotType> comboSlotType) {
		try {
			
			SlotType.getAll().stream().filter(slotType -> slotType.known())
				.sorted((slotType1, slotType2) -> {
					try {
						return slotType1.compareTextTo(slotType2);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return 0;
				})
				.forEach(sloType -> {
					try {
						
						comboSlotType.addItem(sloType);
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
	 * Load program paths combo box.
	 * @param comboProgramPaths
	 */
	@SuppressWarnings("unchecked")
	public static void loadProgramPaths(JComboBox comboProgramPaths) {
		try {
			
			// Load combo box list.
			comboProgramPaths.addItem(ProgramPaths.webInterfaceDirectorySupplier);
			comboProgramPaths.addItem(ProgramPaths.userDirectorySupplier);
			comboProgramPaths.addItem(ProgramPaths.phpDirectorySupplier);
			comboProgramPaths.addItem(ProgramPaths.databaseDirectorySupplier);
			comboProgramPaths.addItem(ProgramPaths.temporaryDirectory);
			
			// Initialize renderer.
			comboProgramPaths.setRenderer(new ListCellRenderer<ProgramPaths.PathSupplier>() {
				
				/**
				 * Renderer.
				 */
				RendererPathItem renderer = new RendererPathItem();
				
				/**
				 * Get renderer.
				 */
				@Override
				public Component getListCellRendererComponent(JList<? extends PathSupplier> list, PathSupplier pathSupplier,
						int index, boolean isSelected, boolean cellHasFocus) {
					
					try {
						if (pathSupplier == null) {
							return null;
						}
						
						// Is combo enabled?
						boolean enabled = comboProgramPaths.isEnabled();
						
						// Get path.
						String path = pathSupplier.supplier.get();
						
						renderer.set(enabled, isSelected, cellHasFocus, index, pathSupplier.caption, path);
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
}
