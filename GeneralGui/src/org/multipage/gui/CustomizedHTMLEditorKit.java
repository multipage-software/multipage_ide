/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.io.*;

import javax.swing.text.*;
import javax.swing.text.html.*;

import org.multipage.util.Safe;

/**
 * Customized HTML editor kit.
 * @author vakol
 *
 */
public class CustomizedHTMLEditorKit extends HTMLEditorKit {
	
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1;

	/**
	 * Write method.
	 */
	@Override
	public void write(Writer out, Document doc, int pos, int len)
			throws IOException, BadLocationException {
		
		try {
			if (!(doc instanceof HTMLDocument)) {
				return;
			}
			HTMLDocument document = (HTMLDocument) doc;
			
			// Write document content.
	        HTMLWriter w = new HTMLWriter(out, document, pos, len);
	        w.write();
	    }
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
}
