/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.util;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.multipage.util.Safe;

/**
 * Abstract class for Swing worker thread.
 * @author vakol
 *
 */
public abstract class SwingWorkerHelper<TOutput> extends SwingWorker<TOutput, Void> {

	/**
	 * Property name.
	 */
	public static final String resultPropertyName = "result";
	
	/**
	 * Property change support.
	 */
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	/**
	 * Result reference.
	 */
	private TOutput output;

	/**
	 * Exception reference.
	 */
	private Exception exception;

	/**
	 * Scheduled cancelation flag.
	 */
	private boolean scheduledCancel = false;

	/**
	 * On do in background.
	 */
	@Override
	protected TOutput doInBackground() throws Exception {
		
		try {
			// Call abstract method.
			TOutput output = doBackgroundProcess();
			// Return output.
			return output;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * On done.
	 */
	@Override
	protected void done() {
		
		// Set output and fire property change event.
		try {
			output = get();
			
			propertyChangeSupport.firePropertyChange(
					resultPropertyName, ProgressResult.NONE, ProgressResult.OK);
		}
		catch (CancellationException e) {
			
			exception = e;
			propertyChangeSupport.firePropertyChange(
					resultPropertyName, ProgressResult.NONE, ProgressResult.CANCELLED);
		}
		catch (InterruptedException e) {
			
			exception = e;
			propertyChangeSupport.firePropertyChange(
					resultPropertyName, ProgressResult.NONE, ProgressResult.INTERRUPTED);
		}
		catch (ExecutionException e) {

			exception = e;
			
			if (isScheduledCancel()) {
				propertyChangeSupport.firePropertyChange(
						resultPropertyName, ProgressResult.NONE, ProgressResult.CANCELLED);	
			}
			else {
				propertyChangeSupport.firePropertyChange(
						resultPropertyName, ProgressResult.NONE, ProgressResult.EXECUTION_EXCEPTION);
			}
		}
	}

	/**
	 * Do background process.
	 * @return
	 * @throws Exception
	 */
	protected abstract TOutput doBackgroundProcess() throws Exception;

	/**
	 * @return the output
	 */
	public TOutput getOutput() {
		return output;
	}

	/**
	 * Add result change listener.
	 * @param listener 
	 */
	public void addResultChangeListener(PropertyChangeListener listener) {
		try {
			
			// Add listener.
			propertyChangeSupport.addPropertyChangeListener(listener);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Remove result change listener.
	 * @param listener 
	 */
	public void removeResultChangeListener(PropertyChangeListener listener) {
		try {
			
			// Remove result change listener.
			propertyChangeSupport.removePropertyChangeListener(listener);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Gets exception.
	 * @return
	 */
	public Exception getException() {

		return exception;
	}

	/**
	 * Schedule cancelation.
	 */
	public void scheduleCancel() {

		scheduledCancel = true;
	}

	/**
	 * @return the scheduledCancel
	 */
	public boolean isScheduledCancel() {
		return scheduledCancel;
	}
	
	/**
	 * Set progress.
	 * @param progress
	 */
	public void setProgressBar(int progress) {
		
		try {
			if (progress < 0) {
				progress = 0;
			}
			if (progress > 100) {
				progress = 100;
			}
			setProgress(progress);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Set progress.
	 * @param progress2
	 */
	public void setProgress2Bar(int progress2) {
		
		try {
			if (progress2 < 0) {
				progress2 = 0;
			}
			if (progress2 > 100) {
				progress2 = 100;
			}
			firePropertyChange("progress2", 0, progress2);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Add message.
	 * @param text
	 */
	public void addMessage(String text) {
		try {
			
			firePropertyChange("message", null, text);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
