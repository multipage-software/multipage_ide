/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2020-03-03
 *
 */
package org.maclan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Save tag parser.
 * @author vakol
 *
 */
public abstract class SaveParser {
	
	/**
	 * Patterns.
	 */
	private final Pattern resourcePattern = Pattern.compile("\\[@@RESOURCE resId\\=([0-9]+)\\]");
	
	/**
	 * Callback methods.
	 */
	
	/**
	 * On text.
	 * @param text
	 * @return
	 */
	public abstract MiddleResult onText(int start, int end);
	
	/**
	 * On resource.
	 * @param resourceId
	 * @return
	 */
	public abstract MiddleResult onResource(long resourceId);
	
	/**
	 * Parse output text.
	 * @param outputText
	 * @return
	 */
	public MiddleResult parse(StringBuilder outputText) {
		
		Matcher matcher = resourcePattern.matcher(outputText);
		
		int textStart = 0;
		Integer textEnd = null;
		
		// Find resource tags.
		while (matcher.find()) {
			
			// End of text and start of resource.
			textEnd = matcher.start();
			
			// Callback on text.
			if (textEnd > textStart) {
				onText(textStart, textEnd);
			}
			
			String resourceIdText = matcher.group(1);
			try {
				int resourceId = Integer.parseInt(resourceIdText);
				
				// Callback on resource.
				onResource(resourceId);
			}
			catch (Exception e) {
				
				// Return exception.
				return MiddleResult.exceptionToResult(e);
			}
			
			// Set new text start.
			textStart = matcher.end();
			textEnd = null;
		}
		
		// Rest of is a string.
		if (textEnd == null) {
			onText(textStart, outputText.length());
		}
		
		return MiddleResult.OK;
	}
}
