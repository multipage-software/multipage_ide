/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2021-04-09
 *
 */

package org.multipage.generator;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;

import javax.swing.JFrame;

import org.maclan.Area;
import org.multipage.gui.UpdatableComponent;
import org.multipage.gui.Utility;
import org.multipage.util.Closable;
import org.multipage.util.Safe;

/**
 * Base class for area editor frame.
 * @author vakol
 *
 */
public abstract class AreaEditorFrameBase extends AreaEditorBase implements UpdatableComponent, Closable {

	/**
	 * Version.
	 */
	protected static final long serialVersionUID = 1L;
	
	/**
	 * Frame object.
	 */
	protected JFrame frame = new JFrame();
		
	/**
	 * Constructor.
	 * @param parentComponent
	 * @param area
	 */
	public AreaEditorFrameBase(Component parentComponent, Area area) {
		super(parentComponent, area);
		try {
			
			// Set lambda functions that are used in the base class methods.
			getWindowLambda = () -> {
				try {
					return Utility.findWindow(frame);
				}
				catch (Throwable e) {
					Safe.exception(e);
				}
				return null;
			};
			
			getTitleLambda = () -> {
				try {
					return frame.getTitle();
				}
				catch (Throwable e) {
					Safe.exception(e);
				}
				return "";
			};
			
			setTitleLambda = title -> {
				try {
					
					frame.setTitle(title);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			};
			
			setIconImageLambda = icon -> {
				try {
					
					frame.setIconImage(icon);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			};
			
			getBoundsLambda = () -> {
				try {
					return frame.getBounds();
				}
				catch (Throwable e) {
					Safe.exception(e);
				}
				return null;
			};
			
			setBoundsLambda = bounds -> {
				try {
					
					frame.setBounds(bounds);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			};
			
			disposeLambda = () -> {
				try {
					
					close();
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			};
			
			// Register this object for updates.
			GeneratorMainFrame.registerForUpdate(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set the frame visible.
	 * @param flag
	 */
	public void setVisible(boolean flag) {
		try {
			
			// Delegate the call.
			frame.setVisible(flag);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set content panel of the frame.
	 * @param contentPane
	 */
	protected void setContentPane(Container contentPane) {
		try {
			
			// Delegate the call.
			frame.setContentPane(contentPane);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set boundaries of the frame.
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	protected void setBounds(int x, int y, int width, int height) {
		try {
			
			// Delegate the call.
			frame.setBounds(x, y, width, height);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set default close operation of the frame.
	 * @param operation
	 */
	protected void setDefaultCloseOperation(int operation) {
		try {
			
			// Delegate the call.
			frame.setDefaultCloseOperation(operation);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Add window listener to the frame.
	 * @param windowAdapter
	 */
	protected void addWindowListener(WindowAdapter windowAdapter) {
		try {
			
			// Delegate the call.
			frame.addWindowListener(windowAdapter);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set minimum frame size.
	 * @param dimension
	 */
	protected void setMinimumSize(Dimension dimension) {
		try {
			
			// Delegate the call.
			frame.setMinimumSize(dimension);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
     * Callback method for updating of application components.
     */
	@Override
	public void updateComponents() {
		try {
			
			// Update parent class components.
			super.updateComponents();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Close object.
	 */
	@Override
	public void close() {
		try {
			
			// Unregister this object from updates.
			GeneratorMainFrame.unregisterFromUpdate(this);
			
			frame.dispose();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
}
