/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */
package org.multipage.translator;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JFileChooser;

import org.apache.commons.imaging.Imaging;
import org.multipage.gui.Utility;
import org.multipage.util.Safe;

/**
 * Utility functions for the Translator application.
 * @author vakol
 *
 */
public class TranslatorUtilities {

	/**
	 * Current path names.
	 */
	public static String currentImagePathName = "";

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
			String [][] filters = {{"org.multipage.translator.textPngFile", "png"}};
			
			// Add filters.
			Utility.addFileChooserFilters(dialog, currentImagePathName, filters, true);
							
			// Open dialog.
		    if(dialog.showOpenDialog(parentComponent) != JFileChooser.APPROVE_OPTION) {
		       return null;
		    }
		    
		    // Get selected file.
		    File file = dialog.getSelectedFile();
		    
		    // Set current path name.
		    currentImagePathName = file.getPath();
		    
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
}
