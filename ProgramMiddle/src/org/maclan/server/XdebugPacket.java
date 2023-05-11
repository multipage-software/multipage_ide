/*
 * Copyright 2010-2023 (C) vakol
 * 
 * Created on : 09-05-2023
 *
 */
package org.maclan.server;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

/**
 * Xdebug packet object.
 * @author vakol
 *
 */
public class XdebugPacket {
	
	/**
	 * XML serializer.
	 */
    private static LSSerializer lsSerializer = null;
	
    /**
     * Static constructor.
     */
    static {
		try {
			DOMImplementationLS domImplementationLS = (DOMImplementationLS) DOMImplementationRegistry.newInstance().getDOMImplementation("LS");
			lsSerializer = domImplementationLS.createLSSerializer();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    }
    
	/**
	 * XML Document representing the packet.
	 */
	private Document xml = null;
	
	/**
	 * Constructor.
	 * @param xml
	 */
	public XdebugPacket(Document xml) {
		
		this.xml = xml;
	}

	/**
	 * Create INIT packet.
	 * @return
	 * @throws Exception 
	 */
	public static XdebugPacket createInitPacket(String areaServerStateLocator)
			throws Exception {
		
		// Set packet content.
		Document xml = newXmlDocument();
		Element rootElement = xml.createElement("init");
		rootElement.setAttribute("appid", "AREA_SERVER");
		rootElement.setAttribute("idekey", "MULTIPAGE_IDE");
		rootElement.setAttribute("session", "");
		rootElement.setAttribute("thread", "");
		rootElement.setAttribute("parent", "");
		rootElement.setAttribute("language", "Maclan");
		rootElement.setAttribute("protocol_version", "1.0");
		rootElement.setAttribute("fileuri", areaServerStateLocator);
		xml.appendChild(rootElement);
        
				// Create new p cket.
		XdebugPacket initPacket = new XdebugPacket(xml);
		return initPacket;
	}
	
	/**
	 * Creates new XML DOM document object.
	 * @return
	 * @throws ParserConfigurationException 
	 */
	private static Document newXmlDocument() 
			throws Exception {
		
        // Create a new DocumentBuilderFactory.
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // Use the factory to create a new DocumentBuilder.
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Create a new Document object.
        Document document = builder.newDocument();
		return document;
	}
	
	/**
	 * Get packet text.
	 * @return
	 * @throws Exception 
	 */
	public String getText() 
			throws Exception {
		
        String text = lsSerializer.writeToString(xml);
        return text;
	}

	/**
	 * Get packet bytes.
	 * @return
	 * @throws Exception 
	 */
	public byte [] getBytes() 
			throws Exception {
		
        // Delegate the call to get string buffer with XML text representation.
		String text = getText();
		byte [] bytes = text.getBytes("UTF-8");
		return bytes;
	}
}
