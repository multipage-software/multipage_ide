/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.addinloader;

import org.multipage.util.Safe;

/** 
 * This class is a wrapper of T type object.
 * @author vakol
 * 
 */
public class Obj<T> {

	/**
	 * Reference.
	 */
	public T ref;
	
	/**
	 * Constructor.
	 */
	public Obj() {

		ref = null;
	}
	
	/**
	 * Constructor.
	 * @param referencedObject
	 */
	public Obj(T referencedObject) {

		ref = referencedObject;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		if (ref == null) {
            return "null";
        }
		try {
			return ref.toString();
		}
		catch (Throwable e) {
			Safe.exception(e);
			return "error";
		}
	}
}
