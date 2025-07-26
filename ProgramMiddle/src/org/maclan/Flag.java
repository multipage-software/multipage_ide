/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan;

/**
 * Class for flag constants.
 * @author vakol
 *
 */
public class Flag {
	
	public static final int NONE = 0;				// Initial state.
	public static final int SET = 1;				// Active state.
	public static final int PROCESSING = 2;			// Processing state.
	public static final int FINISHED = 4;			// Finished state.
	public static final int PROCESSED = 8;			// Processed state.
	public static final int REVERSED = 16;
}
