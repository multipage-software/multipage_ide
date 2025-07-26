/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2018-04-05
 *
 */
package org.maclan.server;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Filter object for cached request.
 * @author vakol
 *
 */
public class CacheRequestFilter implements Filter {

	/**
	 * Does request filter.
	 */
	@Override
	public void doFilter(final ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
			
		// Chain filters for Jetty and Apache
		if (request instanceof javax.servlet.http.HttpServletRequest) {
			
			// Cache request when input stream needed.
			CachedHttpServletRequest cachedRequest = new CachedHttpServletRequest((javax.servlet.http.HttpServletRequest) request);
			chain.doFilter(cachedRequest, response);
		}
		else {
			// Do not filter the request
			chain.doFilter(request, response);
		}
	}
	
	/**
	 * Initialize filter.
	 */
	@Override
	public void init(FilterConfig filterConfiguration) throws ServletException {
		
		// Nothing to do.
	}

	/**
	 * Destroy filter.
	 */
	@Override
	public void destroy() {
		
		// Nothing to do.
	}
}
