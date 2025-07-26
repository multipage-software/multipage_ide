/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */
package org.maclan.server;

import java.io.IOException;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Cached HTTP request wrapper.
 * @author vakol
 *
 */
public class CachedHttpServletRequest extends HttpServletRequestWrapper {
	
	/**
	 * Cached input stream reference.
	 */
	private CachedInputStream cachedInputStream;
	
	/**
	 * Thrown exception.
	 */
	private IOException exception;
	
	/**
	 * Constructor.
	 * @param request
	 */
	public CachedHttpServletRequest(javax.servlet.http.HttpServletRequest request) {
		
		super(request);
		
		// Cache input stream.
		try {
			ServletInputStream servletInputStream = request.getInputStream();
			cachedInputStream = new CachedInputStream(servletInputStream);
		}
		catch (IOException e) {
			exception = e;
		}
	}
	
	/**
	 * cached input stream.
	 */
	public ServletInputStream getInputStream() throws IOException {
		
		if (cachedInputStream == null) {
			throw exception;
		}
		return cachedInputStream;
	}
}
