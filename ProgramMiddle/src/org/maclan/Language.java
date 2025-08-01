/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan;

import java.awt.image.BufferedImage;

/**
 * Class for languages.
 * @author vakol
 *
 */
public class Language {
	
	/**
	 * Identifier.
	 */
	public long id;
	
	/**
	 * Description.
	 */
	public String description;
	
	/**
	 * Alias.
	 */
	public String alias;
	
	/**
	 * Image.
	 */
	public BufferedImage image;
	
	/**
	 * User object reference.
	 */
	public Object user;
	
	/**
	 * Constructor.
	 * @param icon 
	 */
	public Language(long id, String description, String alias, BufferedImage image) {
		
		this.id = id;
		this.description = description;
		this.alias = alias;
		this.image = image;
	}

	/**
	 * Constructor.
	 */
	public Language() {

		this(0L, "", "", null);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return description + " (" + alias + ") [" + id +"]";
	}

	/**
	 * Trace language.
	 * @param decorated 
	 * @return
	 */
	public String trace(boolean decorated) {
		
		String text = String.format("[%d] %s (%s)", id, description, alias);
		
		return MiddleUtility.trimTextWithTags(text, decorated);
	}
}
