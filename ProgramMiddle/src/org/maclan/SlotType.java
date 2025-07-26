/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.maclan.expression.ExpressionSolver;
import org.multipage.gui.Utility;
import org.multipage.util.Resources;

/**
 * Enumeration of slot types.
 * @author vakol
 *
 */
public enum SlotType {
	
	UNKNOWN("middle.textUnknownSlotType", "Unknown"),
	TEXT("middle.textTextSlotType", ExpressionSolver.stringTypeName),
	LOCALIZED_TEXT("middle.textLocalizedTextSlotType", "LocalizedText"),
	INTEGER("middle.textIntegerSlotType", ExpressionSolver.longTypeName),
	REAL("middle.textRealSlotType", ExpressionSolver.doubleTypeName),
	BOOLEAN("middle.textBooleanSlotType", ExpressionSolver.booleanTypeName), 
	ENUMERATION("middle.textEnumerationSlotType", "Enumeration"),
	COLOR("middle.textColorSlotType", "Color"), 
	AREA_REFERENCE("middle.textAreaSlotType", "AreaReference"),
	EXTERNAL_PROVIDER("middle.textExternalSlotType", "ExternalProvider"),
	PATH("middle.textPathSlotType", "Path");
	
	/**
	 * Empty value map.
	 */
	private static final Map<Class<?>, Object> emptyValueMap = new HashMap<>();
	
	/**
	 * Static constructor.
	 */
	static {
		
		// Fill the empty value map.
		emptyValueMap.put(String.class, (String) "");
		emptyValueMap.put(Character.class, (Character) '\0');
		emptyValueMap.put(Long.class, (Long) 0L);
		emptyValueMap.put(Integer.class, (Integer) 0);
		emptyValueMap.put(Double.class, (Double) 0.0);
		emptyValueMap.put(Float.class, (Float) 0.0f);
		emptyValueMap.put(Boolean.class, (Boolean) false);
		emptyValueMap.put(Color.class, (Color) Color.BLACK);
	}
	
	/**
	 * Get empty value that has type specified by the valueClass parameter.
	 * @param valueClass - The value class.
	 * @return
	 */
	public static Object getEmptyValue(Class<?> valueClass) {
		
		if (valueClass == null) {
            return null;
        }
		
		Object emptyValue = emptyValueMap.get(valueClass);
		return emptyValue;
	}
	
	/**
	 * Properties.
	 */
	private String text;
	private String typeText;
	
	/**
	 * Constructor.
	 */
	SlotType(String text, String typeText) {
		
		this.text = Resources.getString(text);
		this.typeText = typeText;
	}

	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {

		return text;
	}

	/**
	 * Get type text.
	 * @return
	 */
	public String getTypeText() {
		
		return typeText;
	}

	/**
	 * Returns true value if the type is text.
	 * @return
	 */
	public static boolean isText(SlotType slotType) {
		
		return slotType == SlotType.TEXT || slotType == SlotType.LOCALIZED_TEXT || slotType == SlotType.PATH;
	}
	
	/**
	 * Check if the input type name corresponds with current type.
	 * @param typeName
	 * @return
	 */
	public boolean hasName(String typeName) {
		
		if (typeText == null || typeName == null) {
			return false;
		}
		
		boolean success = typeText.equals(typeName);
		return success;
	}
	
	/**
	 * Get slot type base on input name.
	 * @param typeName
	 * @return
	 */
	public static SlotType parseType(String typeName) {
		
		// Try to find matching slot type.
		List<SlotType> allTypes = SlotType.getAll();
		for (SlotType type : allTypes) {
			
			if (type.hasName(typeName)) {
				return type;
			}
		}
		
		return  SlotType.UNKNOWN;
	}
	
	/**
	 * Parse input text and return value of current type.
	 * @param valueText
	 * @return
	 */
	public Object parseValue(String valueText, Supplier<EnumerationObj> getEnumerationLambda, Supplier<Area> getAreaLambda) 
			throws Exception{
		
		// Check input value.
		if (valueText == null) {
			throw new NullPointerException("SlotType.parseValue");
		}
		
		Object value = null;
		
		switch (this) {
		case INTEGER:
			value = Long.parseLong(valueText);
			break;
			
		case REAL:
			value = Double.parseDouble(valueText);
			break;
			
		case BOOLEAN:
			value = MiddleUtility.parseBoolean(valueText);
			break;
			
		case ENUMERATION:
			if (getEnumerationLambda != null) {
				value = getEnumerationLambda.get();
			}
			if (value == null) {
				Utility.throwException("org.maclan.messageUnknownEnumerationString", valueText);
			}
			break;
			
		case COLOR:
			value = ColorObj.convertString(valueText);
			if (value == null) {
				Utility.throwException("org.maclan.messageUnknownColorConstantString", valueText);
			}
			break;
			
		case AREA_REFERENCE:
			if (getAreaLambda != null) {
				value = getAreaLambda.get();
			}
			if (value == null) {
				Utility.throwException("org.maclan.messageUnknownAreaReferenceExpression", valueText);
			}
			break;
			
		case TEXT:
		case LOCALIZED_TEXT:
		case UNKNOWN:
		case EXTERNAL_PROVIDER:
		case PATH:
		default:
			value = valueText;
		}
		
		return value;
	}
	
	/**
	 * Get all slot types
	 * @return
	 */
	public static List<SlotType> getAll() {
		
		SlotType [] slotTypes =  SlotType.class.getEnumConstants();
		List<SlotType> list = Arrays.asList(slotTypes);
		return list;
	}
	
	/**
	 * Compares text
	 * @param slotType
	 * @return
	 */
	public int compareTextTo(SlotType slotType) {
		
		return this.toString().compareTo(slotType.toString());
	}
	
	/**
	 * If the slot type is unknown, the method returns false
	 * @return
	 */
	public boolean known() {
		
		return this != UNKNOWN;
	}
}
