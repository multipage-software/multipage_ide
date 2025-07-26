/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2025-04-26
 *
 */

package org.multipage.gui;

import java.util.*;
import java.util.function.BiConsumer;

import org.multipage.util.Obj;
import org.multipage.util.Safe;

/**
 * Key frame objects.
 * @author vakol
 *
 */
public class CssKeyFrame {

	/**
	 * Time points.
	 */
	public LinkedList<String> timePoints = new LinkedList<String>();
	
	/**
	 * Animated properties.
	 */
	public Hashtable<LinkedList<String>, String> animatedProperties = new Hashtable<LinkedList<String>, String>();

	/**
	 * Add time point.
	 * @param timePoint
	 */
	public void addTimePoint(String timePoint) {
		try {
			
			timePoints.add(timePoint);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set property definition and value.
	 * @param propertyDefinition
	 * @param value
	 */
	public void addProperty(String propertyDefinition, String value) {
		try {
			
			String theValue = value.trim();
			
			// Split properties.
			String [] splittedProperties = propertyDefinition.split(",");
			
			if (splittedProperties.length == 0 || theValue.isEmpty()) {
				return;
			}
			
			// Save properties and value.
			LinkedList<String> propertiesList = new LinkedList<String>();
			for (String property : splittedProperties) {
				
				property = property.trim();
				propertiesList.add(property);
			}
			
			animatedProperties.put(propertiesList, theValue);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get time points text.
	 * @return
	 */
	public String getTimePointsText() {
		
		try {
			String text = "";
			boolean isFirst = true;
			
			for (String timePoint : timePoints) {
				
				if (!isFirst) {
					text += ", ";
				}
				text += timePoint;
				
				isFirst = false;
			}
			
			return text;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get properties text.
	 * @return
	 */
	private String getPropertiesText() {
		
		try {
			Obj<String> text = new Obj<String>("");
			Obj<Boolean> isFirst = new Obj<Boolean>(true);
			
			animatedProperties.forEach(new BiConsumer<LinkedList<String>, String>() {
				@Override
				public void accept(LinkedList<String> names, String value) {
					try {
			
						if (!isFirst.ref) {
							text.ref += " ";
						}
						
						// Do loop for all names.
						boolean isFirstName = true;
						
						for (String name : names) {
							
							if (!isFirstName) {
								text.ref += " ";
							}
							text.ref += name + ": " + value + ";";
							isFirstName = false;
						}
						
						isFirst.ref = false;
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
	
			return text.ref;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get animated properties.
	 * @return
	 */
	public Hashtable<LinkedList<String>, String> getAnimatedProperties() {
		
		return animatedProperties;
	}

	/**
	 * For each animated property.
	 * @param biConsumer
	 */
	public void forEachProperty(BiConsumer<String, String> biConsumer) {
		try {
			
			animatedProperties.forEach(new BiConsumer<LinkedList<String>, String>() {
	
				@Override
				public void accept(LinkedList<String> names, String value) {
					try {
			
						if (names.isEmpty()) {
							return;
						}
						String namesText = "";
						boolean isFirst = true;
						for (String name : names) {
							
							if (!isFirst) {
								namesText += ", ";
							}
							namesText += name;
							isFirst = false;
						}
						
						biConsumer.accept(namesText, value);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Clear key frame.
	 */
	public void clear() {
		try {
			
			timePoints.clear();
			animatedProperties.clear();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get specification text.
	 * @return
	 */
	public String getSpecificationText() {
		
		try {
			String text = getTimePointsText();
			text += " { ";
			text += getPropertiesText();
			text += " } "; 
			return text;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Parse text and create keyframe object.
	 * @param text
	 * @return
	 */
	public static CssKeyFrame parse(String text) {
		
		try {
			CssKeyFrame keyframe = new CssKeyFrame();
			text = text.trim();
			
			Obj<Integer> position = new Obj<Integer>(0);
			
			// Parse time points.
			String timePointsText = Utility.getNextMatch(text, position, "[\\w\\s%,]+(?=\\{)");
			if (timePointsText == null) {
				return null;
			}
			
			for (String timePoint : timePointsText.split(",")) {
				keyframe.timePoints.add(timePoint.trim());
			}
			
			// Parse properties.
			String bracket = Utility.getNextMatch(text, position, "\\{");
			if (bracket == null) {
				return null;
			}
			
			String propertiesValues = Utility.getNextMatch(text, position, ".*(?=})");
			if (propertiesValues == null) {
				return null;
			}
			
			for (String propertyValue : propertiesValues.split(";")) {
				
				String [] textAux = propertyValue.split(":");
				if (textAux.length == 2) {
					
					String property = textAux[0].trim();
					String value = textAux[1].trim();
					
					keyframe.addProperty(property, value);
				}
			}
			
			return keyframe;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
}
