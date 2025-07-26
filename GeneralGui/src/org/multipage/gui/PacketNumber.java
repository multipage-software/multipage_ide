/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2024-04-04
 *
 */
package org.multipage.gui;

/**
 * Integer value used in packets.
 * @author vakol
 */
public class PacketNumber extends PacketElement {
	
	/**
	 * Value of integer element. (For example length of block.)
	 */
	public int value = 0;
	
	/**
	 * Reset integer value element. 
	 */
	@Override
	public void reset() {
		
		super.reset();
		value = 0;
	}
	
	/**
	 * String representation.
	 */
	@Override
	public String toString() {
		return String.format("NUMBER %s | val=%2d", super.toString(), value);
	}
}