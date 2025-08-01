/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import org.multipage.gui.Images;

/**
 * Diaplays the area removal icon.
 * @author vakol
 *
 */
public class RemoveElementShape {

	/**
	 * Visible flag.
	 */
	private boolean visible = false;
	
	/**
	 * Location.
	 */
	private Point location = new Point(0, 0);

	/**
	 * Set visible flag.
	 * @param visible
	 */
	public void setVisible(boolean visible) {

		this.visible = visible;
	}

	/**
	 * Draw the shape.
	 * @param g2
	 */
	public void draw(Graphics2D g2) {
		
		try {
			// If the shape is not visible, exit the method.
			if (!visible) {
				return;
			}
			
			// Get image and draw it.
			BufferedImage image = Images.getImage("org/multipage/generator/images/scissors.png");
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			g2.drawImage(image, location.x, location.y, null);
		}
		catch (Throwable e) {
		}
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(Point location) {
		
		this.location = location;
	}
}
