/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2020-04-21
 *
 */
package org.multipage.sync;

import java.io.IOException;

import org.multipage.gui.SerializeStateAdapter;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.StateSerializer;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Main module for external source code providers watch service.
 * @author vakol
 *
 */
public class ProgramSync {
	
	/**
	 * Application state serializer.
	 */
	private static StateSerializer serializer;
	
	/**
	 * Get state serializer
	 * @return
	 */
	public static StateSerializer getSerializer() {
		
		return serializer;
	}
	
	/**
	 * Initialize data.
	 * @param language
	 * @param country
	 * @param serializer
	 * @return
	 */
	public static boolean initialize(String language, String country, StateSerializer serializer) {
		
		try {
			// Remember the serializer
			ProgramSync.serializer = serializer;
			
			// Initialize resources.
			Resources.setLanguageAndCountry(language, country);
			Resources.loadResource("org.multipage.sync.properties.messages");
			
			// Add state serializer.
			if (serializer != null) {
				serializer.add(new SerializeStateAdapter() {
					// On read state.
					@Override
					protected void onReadState(StateInputStream inputStream)
							throws IOException, ClassNotFoundException {
						// Serialize program dictionary.
						seriliazeData(inputStream);
					}
					// On write state.
					@Override
					protected void onWriteState(StateOutputStream outputStream)
							throws IOException {
						// Serialize program dictionary.
						serializeData(outputStream);
					}
					// On set default state.
					@Override
					protected void onSetDefaultState() {
						// Set default data.
						setDefaultData();
					}
				});
			}
			
			return true;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Set default data.
	 */
	protected static void setDefaultData() {
		
	}
	
	/**
	 * Serialize module data.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void seriliazeData(StateInputStream inputStream)
		throws IOException, ClassNotFoundException {
		
		MessageDialog.serializeData(inputStream);
	}
	
	/**
	 * Serialize module data.
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream)
		throws IOException {
		
		MessageDialog.seriliazeData(outputStream);
	}
}
