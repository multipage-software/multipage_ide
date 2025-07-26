/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;

import javax.swing.ImageIcon;

import org.apache.commons.imaging.Imaging;
import org.multipage.util.Safe;

/**
 * Class that enables to load icons and images.
 * @author vakol
 *
 */
public class Images {
	
	/**
	 * List of loaded icons.
	 */
	private static Hashtable<String, ImageIcon> icons = new Hashtable<String, ImageIcon>();
	
	/**
	 * List if loaded images.
	 */
	private static Hashtable<String, BufferedImage> images = new Hashtable<String, BufferedImage>();
	
	/**
	 * Get icon.
	 */
	public static ImageIcon getIcon(String urlString) {
		
		try {
			ImageIcon icon = icons.get(urlString);
			
			// If icon does't exist load it.
			if (icon == null) {
				URL url = ClassLoader.getSystemResource(urlString);
				if (url != null) {
					ImageIcon newIcon = new ImageIcon(url);
					if (newIcon != null) {
						icon = newIcon;
						icons.put(urlString, icon);
					}
				}
			}
			
			return icon;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**try {
			
			
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	 * Get image.
	 */
	public static BufferedImage getImage(String urlString) {
		
		try {
			BufferedImage image = images.get(urlString);
			
			// If image doesn't exist load it.
			if (image == null) {
				URL url = ClassLoader.getSystemResource(urlString);
				if (url != null) {
					InputStream inputStream = null;
					try {
						inputStream = url.openStream();
						image = Imaging.getBufferedImage(inputStream);
						images.put(urlString, image);
						
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					finally {
						if (inputStream != null	) {
							try {
								inputStream.close();
							}
							catch (Exception e) {
							}
						}
					}
				}
			}
			
			return image;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Get cursor.
	 */
	public static Cursor loadCursor(String file, Point hotspot) {
		
		try {
			// Try to get an image.
			BufferedImage image = getImage(file);
			// Create cursor object.
			return Toolkit.getDefaultToolkit().createCustomCursor(image, hotspot, "img");
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
}
