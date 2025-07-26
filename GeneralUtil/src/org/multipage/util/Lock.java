/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-10-23
 *
 */
package org.multipage.util;

import org.multipage.util.Safe;

/**
 * Lock for thread synchronization with timeout detection.
 * @author vakol
 */
public class Lock {
	
	/**
	 * Lock identifier.
	 */
	private String id;
	
	/**
	 * A signal from notify method.
	 */
	private boolean notified = false;
	
	/**
	 * Constructor.
	 */
	public Lock() {
		this("");
	}
	
	/**
	 * Constructor.
	 * @param id - lock identifier
	 */
	public Lock(String id) {
		
		this.id = id;
	}
	
	/**
	 * Constructor.
	 * @param id - lock identifier
	 * @param notified - initial state of the "notified" flag 
	 */
	public Lock(String id, boolean notified) {
		
		this.id = id;
		this.notified = notified;
	}
	
	/**
	 * Reset the lock state.
	 * @param lock
	 */
	public static void reset(Lock lock) {
		
		lock.notified = false;
	}
	
	/**
	 * Wait for lock.
	 * @param lock
	 * @return true if the waiting state has been interrupted
	 */
	public static boolean waitFor(Lock lock) {
		
		synchronized (lock) {
			try {
				if (lock.notified) {
					lock.notified = false; // reset the signal
					return false;
				}
				lock.wait();
				return false;
			}
			catch (InterruptedException e) {
				return true;
			}
		}
	}
	
	/**
	 * Wait for lock with timeout.
	 * @param lock
	 * @param milliseconds
	 * @return true if the timeout has elapsed
	 */
	public static boolean waitFor(Lock lock, long milliseconds) {
		
		long start = 0L;
		try {
			start = System.currentTimeMillis();
		}
		catch (Throwable e) {
			Safe.exception(e);
			return false;
		}
		
		synchronized (lock) {
			try {
				if (lock.notified) {
					return false;
				}
				lock.wait(milliseconds);
			}
			catch (InterruptedException e) {
			}
		}
		
		try {
			if (lock.notified) {
				return false;
			}
			
			long delta = System.currentTimeMillis() - start;
			if (delta >= milliseconds) {
				return true;
			}
			
			final long accuracy = 90;  // Timeout accuracy in percent
			long deltaPercent = 100 - (milliseconds - delta) * 100 / milliseconds;
			
			return deltaPercent >= accuracy;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Notify lock and write a message to log.
	 * @param lock
	 * @param logMessage
	 */
	public static void notify(Lock lock, String logMessage) {
		try {
			
			synchronized (lock) {
				lock.notify();
				lock.notified = true;
				if (logMessage != null)
					j.log(logMessage);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
		// Switch to another thread
		try {
			Thread.sleep(0);
		}
		catch (InterruptedException e) {
		}
	}
	
	/**
	 * Notify lock.
	 * @param lock
	 */
	public static void notify(Lock lock) {
		try {
			
			notify(lock, null);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Notify lock and write a message to log.
	 * @param lock
	 * @param logMessage
	 */
	public static void notifyAll(Lock lock, String logMessage) {
		try {
			
			synchronized (lock) {
				lock.notifyAll();
				lock.notified = true;
				//j.log("err", "NTF " + lock.id);
				if (logMessage != null)
					j.log(logMessage);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
		// Switch to another thread
		try {
			Thread.sleep(0);
		}
		catch (InterruptedException e) {
		}
	}
	
	/**
	 * Notify lock.
	 * @param lock
	 */
	public static void notifyAll(Lock lock) {
		try {
			
			notifyAll(lock, null);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Returns the lock state.
	 * @return
	 */
	public boolean notified() {
		
		return notified;
	}
	
	/**
	 * Get text representation of the lock state.
	 */
	@Override
	public String toString() {
		
		try {
			return "Lock [id=" + id + ", notified=" + notified + "]";
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
}
