/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2018-11-20
 *
 */
package org.multipage.util;

import java.util.HashMap;

import org.multipage.util.Safe;

/**
 * Class for synchronization objects.
 * @author vakol
 *
 */
public class Sync {
	
	/**
	 * Gets synchronization object.
	 * @return
	 * @throws Exception
	 */
	public static Object from(HashMap<String, Object> syncs) {
		
		try {
			StackTraceElement [] stack = Thread.currentThread().getStackTrace();
			StackTraceElement exepoint = stack[3];
			String key = String.format("%s#%d", exepoint.getClassName(), exepoint.getLineNumber());
			Object sync = syncs.get(key);
			if (sync == null) {
				sync = new Object();
				syncs.put(key, sync);
			}
			return sync;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
}
