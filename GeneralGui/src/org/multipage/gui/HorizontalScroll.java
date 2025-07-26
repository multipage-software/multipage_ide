/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.multipage.util.Safe;

/**
 * Horizontal scroll bar.
 * @author vakol
 *
 */
public class HorizontalScroll extends ScrollShape {

	/**
	 * Height.
	 */
	private static final int height = 20;
	
	/**
	 * Get height.
	 * @return
	 */
	public static int getHeight() {

		return height;
	}

	/**
	 * Location.
	 */
	private int x1;
	private int y1;
	private int x2;

	/**
	 * Constructor.
	 * @param cursor
	 */
	public HorizontalScroll(Cursor cursor, Component component) {
		super(cursor, component);
	}

	/**
	 * Set location.
	 */
	public void setLocation(int x1, int y1, int x2) {
		try {
			
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			
			// Set cursor area.
			getCursorArea().setShape(
					new Rectangle(x1, y1, x2 - x1, getHeight()));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Draw horizontal scroll.
	 */
	public void draw(Graphics2D g2, Color color) {
		try {
			
			if (isVisible()) {
				int width = x2 - x1;
				
				// Get image.
				BufferedImage leftImage = Images.getImage("org/multipage/gui/images/left.png");
				BufferedImage rightImage = Images.getImage("org/multipage/gui/images/right.png");
				
				g2.setColor(color);
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
				// Draw outlines.
				g2.drawRect(x1, y1, width - 1, height - 1);
				// Draw right and left buttons.
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
				g2.drawImage(leftImage, null, x1 + 2, y1 + 2);
				g2.drawImage(rightImage, null, x2 - 18, y1 + 2);
				// Draw lines.
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
				g2.drawLine(x1 + 20, y1, x1 + 20, y1 + height);
				g2.drawLine(x2 - 20, y1, x2 - 20, y1 + height);
				// Fill rectangle.
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
				g2.fillRect(x1, y1, width, height);
				
				
				int size = sliderSize(),
				    start = sliderStart();
				// Draw and fill slider.
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
				g2.drawRect(start, y1, size, y1 + height);
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
				g2.fillRect(start, y1, size, y1 + height);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Return slider start.
	 * @return
	 */
	private int sliderStart() {
		
		try {
			int width = x2 - x1;
			int inner = width - 2 * 20;
			int start = x1 + 20 + (int)(inner * (win.getX() - content.getX()) / content.getWidth());
			int size = sliderSize();
			int end = start + size;
			
			// If the end of the slide exceeds boundaries set new start.
			if (end > x2 - 20) {
				start = x2 - 20 - size;
			}
			
			return start;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0;
	}
	
	/**
	 * Get slider end.
	 */
	private int sliderEnd() {
		
		try {
			return sliderStart() + sliderSize();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0;
	}

	/**
	 * Return slider size.
	 * @return
	 */
	private int sliderSize() {
		
		try {
			int width = x2 - x1;
			int inner = width - 2 * 20;
			
			int size = (int)(inner * win.getWidth() / content.getWidth());
			if (size < minimumSliderSize) {
				size = minimumSliderSize;;
			}
			return size;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0;
	}

	/**
	 * Returns true if scroll bar contains point.
	 * @param point
	 * @return
	 */
	public boolean contains(Point point) {
		
		try {
			if (isVisible()) {
				Rectangle rect = new Rectangle(x1, y1, x2 - x1, height);
				return rect.contains(point);
			}
		}
		catch (Exception e) {
            Safe.exception(e);
        }
		return false;
	}
	
	/**
	 * Is visible.
	 */
	@Override
	public boolean isVisible() {
		
		try {
			boolean visible = false;
			
			if (win != null && content != null) {
				visible = content.getMinX() < win.getMinX() || content.getMaxX() > win.getMaxX();
			}
			return visible;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * On mouse pressed.
	 * @param mouse
	 */
	public void onMousePressed(Point mouse) {
		try {
			
			// If the mouse is outside the rectangle, exit the method.
			if (!contains(mouse)) {
				return;
			}
	
			final Rectangle2D winRect = win.getBounds2D();
			final double step = win.getWidth() * stepPercent / 100;
			
			// If right part affected.
			if (mouse.getX() >= sliderEnd()) {
				
				// Create and schedule timer.
				timer = new javax.swing.Timer(0, null);
				timer.setDelay((int) repeatMs);
				
				// Set action command.
				timer.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							
							double newX;
							newX = win.getX() + step;
							if (newX + win.getWidth() > content.getX() + content.getWidth()) {
								newX = content.getX() + content.getWidth() - win.getWidth();
							}
							win.setRect(newX, 0, winRect.getWidth(), 1);
							invokeOnScroll();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
	
				// Start timer.
				timer.start();
			}
			// If left part affected.
			else if (mouse.getX() <= sliderStart()) {
			
				// Create and schedule timer.
				timer = new javax.swing.Timer(0, null);
				timer.setDelay((int) repeatMs);
				
				// Set action command.
				timer.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							
							double newX;
							newX = win.getX() - step;
							if (newX < content.getX()) {
								newX = content.getX();
							}
							win.setRect(newX, 0, winRect.getWidth(), 1);
							invokeOnScroll();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
				
				// Start timer.
				timer.start();
			}
			// If slider affected.
			else {
				// Set pressed position.
				pressedPosition = new Point(mouse);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On mouse dragged.
	 * @param e
	 */
	public void onMouseDragged(MouseEvent e) {
		try {
			
			int width = x2 - x1;
			int inner = width - 2 * 20;
			
			delta = (e.getPoint().x - pressedPosition.getX()) * content.getWidth() / inner;
			
			double newX = win.getX() + delta;
			if (newX < content.getX()) {
				newX = content.getX();
			}
			if (newX + win.getWidth() > content.getX() + content.getWidth()) {
				newX = content.getX() + content.getWidth() - win.getWidth();
			}
			win.setRect(newX, 0, win.getWidth(), 1);
			// Invoke listener.
			invokeOnScroll();
			
			pressedPosition = e.getPoint();
			delta = 0.0;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get rectangle.
	 * @return
	 */
	public Rectangle getRect() {
		
		try {
			return new Rectangle(x1, y1, x2 - x1, height);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return new Rectangle();
	}
}
