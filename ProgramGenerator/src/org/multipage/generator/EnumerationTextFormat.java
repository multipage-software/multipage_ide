/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.io.Serializable;

import org.multipage.util.Safe;

/**
 * Format of enumeration text.
 * @author vakol
 *
 */
public class EnumerationTextFormat implements Serializable {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Input format.
	 */
	public String output;
	
	/**
	 * Output format.
	 */
	public String input;
	
	/**
	 * "Is default" flag.
	 */
	public boolean isDefault = false;

	/**
	 * Constructor.
	 * @param outputFormat
	 * @param inputFormat
	 */
	public EnumerationTextFormat(String outputFormat, String inputFormat, boolean isDefault) {
		
		this.output = outputFormat;
		this.input = inputFormat;
		this.isDefault = isDefault;
	}

	/**
	 * A conversion to string value.
	 */
	@Override
	public String toString() {
		
		return output;
	}

	/**
	 * Returns true value if this object matches the input object.
	 */
	@Override
	public boolean equals(Object obj) {
		
		try {
			if (!(obj instanceof EnumerationTextFormat)) {
				return false;
			}
			EnumerationTextFormat format = (EnumerationTextFormat) obj;
			
			if (format == this) {
				return true;
			}
			if (format.output.equals(this.output) && format.input.equals(this.input)) {
				return true;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Returns true value if the format is empty.
	 */
	public boolean isEmpty() {
		
		try {
			return output.isEmpty();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
}
