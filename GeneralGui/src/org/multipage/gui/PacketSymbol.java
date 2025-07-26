/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2023-09-19
 *
 */
package org.multipage.gui;

/**
 * Symbol repsesented by sequence of bytes.
 * @author vakol
 */
public class PacketSymbol extends PacketElement {
	
	/**
	 * Symbol bytes.
	 */
	public byte [] bytes = {};
	
	/**
	 * Create symbol element.
	 * @param bytes
	 * @return
	 */
	public PacketSymbol(byte [] bytes) {
		
		this.bytes = bytes;
	}
	
	/**
	 * String representation.
	 */
	@Override
	public String toString() {
		

		return String.format("SYMBOL %s | bytes=%s", super.toString(), Utility.prettyPrint(bytes));
	}
}