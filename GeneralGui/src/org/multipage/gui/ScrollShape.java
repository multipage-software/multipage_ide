/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import org.multipage.util.Safe;

/**
 * Abstract base class for scroll bars.
 * @author vakol
 *
 */
public abstract class ScrollShape implements CursorArea {

	/**
	 * Repeat in milliseconds.
	 */
	protected static final long repeatMs = 200;
	
	/**
	 * Timer.
	 */
	protected javax.swing.Timer timer;

	/**
	 * Content rectangle.
	 */
	protected Rectangle2D content;

	/**
	 * Window rectangle.
	 */
	protected Rectangle2D win;
	
	/**
	 * Step in percent.
	 */
	protected static final double stepPercent = 10;

	/**
	 * Minimum slider size.
	 */
	protected static final int minimumSliderSize = 20;
	
	/**
	 * Listener.
	 */
	protected ScrollListener listener;
	
	/**
	 * Pressed position.
	 */
	protected Point pressedPosition;
	
	/**
	 * Delta.
	 */
	protected double delta = 0.0; 
	
	/**
	 * Cursor area.
	 */
	CursorAreaImpl cursorArea;

	/**
	 * Get cursor area.
	 */
	@Override
	public CursorAreaImpl getCursorArea() {

		return cursorArea;
	}
	
	/**
	 * Constructor.
	 */
	public ScrollShape(Cursor cursor, Component component) {
		try {
			
			cursorArea = new CursorAreaImpl(cursor, component,
				new CursorAreaListener() {
					@Override
					public boolean visible() {
						
						try {
							return isVisible();
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
	 * Override this method.
	 */
	public abstract boolean isVisible();

	/**
	 * Returns if is dragged.
	 */
	public boolean isDragged() {
		
		return pressedPosition != null;
	}
	
	/**
	 * Set scroll.
	 */
	public void set(Rectangle2D win, Rectangle2D content) {

		this.win = win;
		this.content = content;
	}
	
	/**
	 * Invoke on scroll.
	 */
	protected void invokeOnScroll() {
		try {
			
			if (listener != null) {
				listener.onScroll(win);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * @param listener the listener to set
	 */
	public void setListener(ScrollListener listener) {
		this.listener = listener;
	}

	/**
	 * On mouse released.
	 */
	public void onMouseReleased() {
		try {
			
			delta = 0.0;
			pressedPosition = null;
			if (timer != null) {
				timer.stop();
				timer = null;
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
