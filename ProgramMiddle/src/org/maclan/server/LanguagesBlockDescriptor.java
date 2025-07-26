/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan.server;

import java.util.LinkedList;

import org.maclan.Language;

/**
 * Language block descriptor.
 * @author vakol
 *
 */
public class LanguagesBlockDescriptor extends BlockDescriptor {

	/**
	 * Languages list.
	 */
	@SuppressWarnings("unused")
	private LinkedList<Language> languages;
	
	/**
	 * Index.
	 */
	@SuppressWarnings("unused")
	private long index;
	
	/**
	 * Constructor.
	 * @param languages
	 */
	public LanguagesBlockDescriptor(LinkedList<Language> languages) {

		this.languages = languages;
		index = 1;
	}

	/**
	 * Set index.
	 * @param index
	 */
	public void setIndex(int index) {

		this.index = index;
	}
}
