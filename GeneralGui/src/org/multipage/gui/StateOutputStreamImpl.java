/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2021-12-29
 *
 */
package org.multipage.gui;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.util.HashMap;

import org.apache.commons.io.ByteOrderMark;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Output stream that can store application states in output file.
 * The XStream implementation uses the XML format to store application setting objects.
 * @author vakol
 */
public class StateOutputStreamImpl implements StateOutputStream {
	
	/**
	 * Main object of the XStream library.
	 */
	private XStream xStream = null;
	
	/**
	 * Name of the output file.
	 */
	private String fileName = null;
	
	/**
	 * A reference to opened output file stream.
	 */
	private FileOutputStream fileOutputStream = null;
	
	/**
	 * An extension to file stream created with XStream library.
	 * Method of the class residing in the XStream library are compatible with the JRE ObjectOutputStream class.
	 */
	private ObjectOutputStream objectOutputStream = null;
	
	/**
	 * Class record counter.
	 */
	static HashMap<Class<?>, Integer> classRecordCounter = new HashMap<>();
	
	/**
	 * Set alias used used with XStream writer when it writes objects of specified class.
	 */
	@Override
	public void setXmlObjectAlias(String alias, Class<?> classObject) {
		
		xStream.alias(alias, classObject);
	}
	
	/**
	 * Create new class instance.
	 * @param fileName 
	 * @param rootNodeName
	 */
	public static StateOutputStreamImpl newXStreamInstance(String fileName, String rootNodeName) 
			throws Exception {
		
		// Create stream to output file and initialize the XML.
		StateOutputStreamImpl thisObject = new StateOutputStreamImpl();
		thisObject.xStream = new XStream(new DomDriver());
		
		thisObject.xStream.alias("record", StateStreamRecordInfo.class);
		
		thisObject.fileName = fileName;
		thisObject.fileOutputStream = new FileOutputStream(fileName);
		thisObject.objectOutputStream = thisObject.xStream.createObjectOutputStream(thisObject.fileOutputStream, rootNodeName);
		
		return thisObject;
	}
	
	/**
	 * Check initialized reference to the output stream.
	 */
	private StateStreamRecordInfo checkInitialization()
			throws IOException {
		
		// Check initialized reference to the output stream.
		if (objectOutputStream == null) {
			throw new IOException(Resources.getString("org.multipage.gui.messageObjectStreamForStatesNotInitialized"));
		}
		
		// Get calling method information.
		StateStreamRecordInfo callerInfo = null;
		
		// Stack Level 4.
		final int STACK_INFO_LEVEL = 4;
		
	    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
	    if (stackTrace.length > STACK_INFO_LEVEL) {
	    	
	        StackTraceElement caller = stackTrace[STACK_INFO_LEVEL];
	        try {
	        	
	            Class<?> callerClass = Class.forName(caller.getClassName());
                callerInfo = new StateStreamRecordInfo(callerClass);
	        }
	        catch (ClassNotFoundException e) {
	            Safe.exception(e);
	            throw new IOException(e);
	        }
	    }
	    return callerInfo;
	}
	
	/**
	 * Returns name of the output file.
	 * @return
	 */
	public String getFileName() {
		
		return fileName;
	}
	
	/**
	 * Get raw output stream.
	 * @return
	 */
	public FileOutputStream getRawOutputStream() {
		
		return fileOutputStream;
	}
	
	/**
	 * Get XStream library object.
	 * @return
	 */
	public XStream getXStream() {
		
		return xStream;
	}

	/**
	 * Write BOM (byte order mark for UTF-8 encoding into the output file.
	 * @throws IOException 
	 */
	public void writeBom() throws IOException {
		
		// Get UTF8 byte order mark bytes and try to write them into the output file.
		byte [] bom = ByteOrderMark.UTF_8.getBytes();
		
		fileOutputStream.write(bom);
	}
	
	/**
	 * Close the output stream.
	 */
	public void close() throws IOException {
		
		if (objectOutputStream != null) {
			objectOutputStream.close();
		}
		
		if (fileOutputStream != null) {
			fileOutputStream.close();
		}
	}
	
	/**
	 * Delegate the method call to object output stream.
	 */
	@Override
	public void useProtocolVersion(int version) throws IOException {
		
		checkInitialization();
		try {
			objectOutputStream.useProtocolVersion(version);
		}
		catch (Exception e) {
			throw new IOException(e.getLocalizedMessage());
		}
	}

	/**
	 * Delegate the method call to object output stream.
	 */
	@Override
	public void writeUnshared(Object obj) throws IOException {

		checkInitialization();
		try {
			objectOutputStream.writeUnshared(obj);
		}
		catch (Exception e) {
			throw new IOException(e.getLocalizedMessage());
		}
	}

	/**
	 * Delegate the method call to object output stream.
	 */
	@Override
	public void defaultWriteObject() throws IOException {

		checkInitialization();
		try {
			objectOutputStream.defaultWriteObject();
		}
		catch (Exception e) {
			throw new IOException(e.getLocalizedMessage());
		}
	}

	/**
	 * Delegate the method call to object output stream.
	 */
	@Override
	public PutField putFields() throws IOException {

		checkInitialization();
		try {
			return objectOutputStream.putFields();
		}
		catch (Exception e) {
			throw new IOException(e.getLocalizedMessage());
		}
	}

	/**
	 * Delegate the method call to object output stream.
	 */
	@Override
	public void writeFields() throws IOException {

		checkInitialization();
		try {
			objectOutputStream.writeFields();
		}
		catch (Exception e) {
			throw new IOException(e.getLocalizedMessage());
		}
	}

	/**
	 * Delegate the method call to object output stream.
	 */
	@Override
	public void reset() throws IOException {

		checkInitialization();
		try {
			objectOutputStream.reset();
		}
		catch (Exception e) {
			throw new IOException(e.getLocalizedMessage());
		}
	}

	/**
	 * Delegate the method call to object output stream.
	 */
	@Override
	public void write(int val) throws IOException {

		checkInitialization();
		try {
			objectOutputStream.write(val);
		}
		catch (Exception e) {
			throw new IOException(e.getLocalizedMessage());
		}
	}

	/**
	 * Delegate the method call to object output stream.
	 */
	@Override
	public void write(byte[] buf) throws IOException {

		checkInitialization();
		try {
			objectOutputStream.write(buf);
		}
		catch (Exception e) {
			throw new IOException(e.getLocalizedMessage());
		}
	}

	/**
	 * Delegate the method call to object output stream.
	 */
	@Override
	public void write(byte[] buf, int off, int len) throws IOException {

		checkInitialization();
		try {
			objectOutputStream.write(buf, off, len);
		}
		catch (Exception e) {
			throw new IOException(e.getLocalizedMessage());
		}
	}

	/**
	 * Delegate the method call to object output stream.
	 */
	@Override
	public void flush() throws IOException {

		checkInitialization();
		try {
			objectOutputStream.flush();
		}
		catch (Exception e) {
			throw new IOException(e.getLocalizedMessage());
		}
	}
	
	/**
	 * Delegate the method call to object output stream.
	 * @param value
	 * @throws IOException
	 */
	private void writeHelper(Object value) throws IOException {

		StateStreamRecordInfo record = checkInitialization();
		try {
			objectOutputStream.writeObject(record.wrap(value));
		}
		catch (Exception e) {
			throw new IOException(e.getLocalizedMessage());
		}
	}

	/**
	 * Delegate the method call to object output stream.
	 */
	@Override
	public void writeBoolean(boolean val) throws IOException {

		writeHelper(val);
	}

	/**
	 * Delegate the method call to object output stream.
	 */
	@Override
	public void writeByte(int val) throws IOException {

		writeHelper(val);
	}

	/**
	 * Delegate the method call to object output stream.
	 */
	@Override
	public void writeShort(int val) throws IOException {

		writeHelper(val);
	}

	/**
	 * Delegate the method call to object output stream.
	 */
	@Override
	public void writeChar(int val) throws IOException {

		writeHelper(val);
	}

	/**
	 * Delegate the method call to object output stream.
	 */
	@Override
	public void writeInt(int val) throws IOException {

		writeHelper(val);
	}

	/**
	 * Delegate the method call to object output stream.
	 */
	@Override
	public void writeLong(long val) throws IOException {

		writeHelper(val);
	}

	/**
	 * Delegate the method call to object output stream.
	 */
	@Override
	public void writeFloat(float val) throws IOException {

		writeHelper(val);
	}

	/**
	 * Delegate the method call to object output stream.
	 */
	@Override
	public void writeDouble(double val) throws IOException {

		writeHelper(val);
	}

	/**
	 * Delegate the method call to object output stream.
	 */
	@Override
	public void writeBytes(String str) throws IOException {

		writeHelper(str);
	}

	/**
	 * Delegate the method call to object output stream.
	 */
	@Override
	public void writeChars(String str) throws IOException {

		writeHelper(str);
	}

	/**
	 * Delegate the method call to object output stream.
	 */
	@Override
	public void writeUTF(String str) throws IOException {

		writeHelper(str);
	}
	
	/**
	 * Delegate the method call to object output stream.
	 */
	@Override
	public void writeObject(Object object) throws IOException {
		
		writeHelper(object);
	}
}
