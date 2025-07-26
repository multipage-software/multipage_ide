/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2022-06-20
 *
 */
package org.multipage.api;

import org.multipage.util.Safe;

/**
 * TODO: <---MAKE Area Server API.
 * @author vakol
 *
 */
public class AreaServerApi {
	
	/**
	 * Create Area Server API instance.
	 * @param areaServerMessage
	 */
	public static AreaServerApi createAreaSeverApi() {
		
		try {
			AreaServerApi areaServerApi = new AreaServerApi();
			return areaServerApi;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
}
