/**
 * Copyright 2010-2024 (C) vakol
 * 
 * Created on : 25-06-2024
 *
 */
package org.maclan.server;

import java.util.Properties;

/**
 * Tag information for debugger.
 * @author vakol
 */
public class DebugTagInfo {
	
	/**
	 * Debugged tag name.
	 */
	private String tagName = null;

	/**
	 * Debugged tag properties.
	 */
	private Properties properties = null;

	/**
	 * Start position for replacement.
	 */
	private int cmdBegin = -1;

	/**
	 * Stop position for replacement.
	 */	
	private int cmdEnd = -1;

	/**
	 * Inner text.
	 */
	private String innerText = null;

	/**
	 * Current replacement text.
	 */
	private String replacement = null;

	/**
	 * Get tag name.
	 * @return
	 */
	public String getTagName() {
		return tagName;
	}

	/**
	 * Set tag name.
	 * @param tagName
	 */
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	
	/**
	 * Get tag properties.
	 * @return
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * Set tag properties.
	 * @param properties
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	/**
	 * Get tag beginning.
	 * @return
	 */
	public int getCmdBegin() {
		return cmdBegin;
	}
	
	/**
	 * Set tag beginning.
	 * @param cmdBegin
	 */
	public void setCmdBegin(int cmdBegin) {
		this.cmdBegin = cmdBegin;
	}
	
	/**
	 * Get tag end.
	 * @return
	 */
	public int getCmdEnd() {
		return cmdEnd;
	}

	/**
	 * Set tag end.
	 * @param cmdEnd
	 */
	public void setCmdEnd(int cmdEnd) {
		this.cmdEnd = cmdEnd;
	}
	
	/**
	 * Get tag inner text.
	 * @return
	 */
	public String getInnerText() {
		return innerText;
	}
	
	/**
	 * Set tag inner text.
	 * @param innerText
	 */
	public void setInnerText(String innerText) {
		this.innerText = innerText;
	}
	
	/**
	 * Get tag replacement text.
	 * @return
	 */
	public String getReplacement() {
		return replacement;
	}
	
	/**
	 * Set tag replacement text.
	 * @param replacement
	 */
	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}
}