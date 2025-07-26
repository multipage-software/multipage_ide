/**
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2018-07-31
 */
package org.multipage.util;

import org.multipage.util.Safe;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Document object model utilities.
 * @author vakol
 *
 */
public class DOM {
	
	/**
	 * A reference to DOM node
	 */
	private Node node;
	
	/**
	 * Object factory
	 * @param node
	 * @return
	 */
	public static DOM use(Node node) {
		
		try {
			DOM dom = new DOM();
			dom.node = node;
			return dom;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Get attribute.
	 * @param name
	 * @return
	 */
	public String attribute(String name) {
		
		try {
			if (node != null) {
				NamedNodeMap attributes = node.getAttributes();
				if (attributes != null) {
					
					Node attribute = attributes.getNamedItem(name);
					if (attribute != null) {
						
						String value = attribute.getTextContent();
						if (value != null) {
							return value;
						}
					}
				}
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	};
}