/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.Imaging;
import org.multipage.util.Safe;

/**
 * Utility functions for images.
 * @author vakol
 *
 */
public class ImgUtility {

	/**
	 * Converts byte array to the image.
	 * @param bytes
	 * @return
	 */
	public static BufferedImage convertByteArrayToImage(byte[] bytes) {
		
		BufferedImage image = null;
		InputStream inputStream = null;
		
		try {
			// Try to use Apache Commons to load the image.
			inputStream = new ByteArrayInputStream(bytes);
			image = Imaging.getBufferedImage(inputStream);
		}
		catch (Exception e) {
			
			// Otherwise use Java method.
			if (inputStream != null) {
				try {
                    image = ImageIO.read(inputStream);
                }
				catch (Exception e2) {
	            	Safe.exception(e2);
	            }
			}
		}
		finally {
			// Close the input stream.
			if (inputStream != null) {
				try {
					inputStream.close();
				}
				catch (Exception e) {
					Safe.exception(e);
                }
			}
		}
		return image;
	}

	/**
	 * Resize image
	 * @param image
	 * @param width
	 * @param height
	 * @return
	 */
	public static BufferedImage resizeImage(BufferedImage image, int width, int height) {
		
		try {
			int type = image.getType();
			if (type == 0) {
				type = BufferedImage.TYPE_INT_ARGB;
			}
			
			BufferedImage resizedImage = new BufferedImage(width, height, type);
			Graphics2D g = resizedImage.createGraphics();
			g.drawImage(image, 0, 0, width, height, null);
			g.dispose();
			
			return resizedImage;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Converts image to the byte array in given format.
	 * @param bufferedImage
	 * @param formatName
	 * @return
	 */
	public static byte[] convertImageToByteArray(BufferedImage bufferedImage, String formatName) {
		
		// Obtain image content.
		ByteArrayOutputStream outputStream = null;
		try {
			outputStream = new ByteArrayOutputStream();
			ImageFormats format = ImageFormats.valueOf(formatName);
			Imaging.writeImage(bufferedImage, outputStream, format);
		}
		catch (Exception e) {
			Safe.exception(e);
		}
		finally {
			if (outputStream != null) {
				try {
                    outputStream.close();
                }
                catch (Exception e) {
                	Safe.exception(e);
                }
			}
		}
		
		if (outputStream == null) {
			return null;
		}
		return outputStream.toByteArray();
	}
}
