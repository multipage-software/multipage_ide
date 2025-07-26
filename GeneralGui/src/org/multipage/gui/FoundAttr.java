/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

/**
 * Class for find text attributes.
 * @author vakol
 *
 */
public class FoundAttr {

	/**
	 * Attributes.
	 */
	public String searchText;
	public boolean isCaseSensitive;
	public boolean isWholeWords;

	/**
	 * Constructor.
	 * @param searchText
	 * @param isCaseSensitive
	 * @param isWholeWords
	 * @param isExactMatch
	 */
	public FoundAttr(String searchText, boolean isCaseSensitive,
			boolean isWholeWords) {
		
		this.searchText = searchText;
		this.isCaseSensitive = isCaseSensitive;
		this.isWholeWords = isWholeWords;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FoundAttr [searchText=" + searchText + ", isCaseSensitive="
				+ isCaseSensitive + ", isWholeWords=" + isWholeWords + "]";
	}
}
