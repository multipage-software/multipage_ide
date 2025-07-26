/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.lang.reflect.Field;

import org.multipage.util.Resources;
import org.multipage.util.Safe;
		
/**
 * Enumeration of colors that are used when drawing GUI components.
 * @author vakol
 *
 */
public enum ColorId {
	
	OUTLINES("OUTLINES", "org.multipage.generator.textOutlinesColor"),
	OUTLINES_PROTECTED("OUTLINES_PROTECTED", "org.multipage.generator.textOutlinesProtectedColor"),
	FILLLABEL("FILLLABEL", "org.multipage.generator.textFillLabelColor"),
	FILLLABEL_PROTECTED("FILLLABEL_PROTECTED", "org.multipage.generator.textFillLabelProtectedColor"),
	TEXT("TEXT", "org.multipage.generator.textTextColor"),
	INACTIVE_TEXT("TEXT_PROTECTED", "org.multipage.generator.textProtectedTextColor"),
	TEXT_PROTECTED(INACTIVE_TEXT),
	SELECTION("SELECTION", "org.multipage.generator.textSelectionColor"),
	SELECTION_PROTECTED("SELECTION_PROTECTED", "org.multipage.generator.textSelectionProtected"),
	BACKGROUND("BACKGROUND", "org.multipage.generator.textBackgroundColor"),
	BACKGROUNDTEXT("BACKGROUNDTEXT", "org.multipage.generator.textBackgroundTextColor"),
	TOOLBACKGROUND("TOOLBACKGROUND", "org.multipage.generator.textToolBackgroundColor"),
	TOOLLISTBACKGROUND("TOOLLISTBACKGROUND", "org.multipage.generator.textToolListBackgroundColor"),
	FREE("FREE", "org.multipage.generator.textFreeColor"),
	FILLBODY("FILLBODY", "org.multipage.generator.textFillBodyColor"),
	DESCRIPTIONTEXT("DESCRIPTIONTEXT", "org.multipage.generator.textDescriptionTextColor"),
	SCROLLBARS("SCROLLBARS", "org.multipage.generator.textScrollBarsColor"),
	OVERVIEWBACKGROUND("OVERVIEWBACKGROUND", "org.multipage.generator.textOverviewBackground"),
	REVERSEDEDGES("REVERSEDEDGES", "org.multipage.generator.textReversedEdges"),
	INACTIVE_OUTLINES("INACTIVE_OUTLINES", "org.multipage.generator.textInactiveOutlines"),
	INACTIVE_BODIES("INACTIVE_BODIES", "org.multipage.generator.textInactiveBodies"),
	SELECTED_TEXT("SELECTED_TEXTS", "org.multipage.generator.textSelectedTexts"),
	SCRIPT_COMMAND_HIGHLIGHT("SCRIPT_COMMAND_HIGHLIGHT", "org.multipage.generator.textScriptCommandHighlightColor"),
	AREA_PROPERTIES_FRAME("AREA_PROPERTIES_FRAME", "org.multipage.generator.textAreaPropertiesFrameBackground"),
	SEARCH_DIALOG("SEARCH_DIALOG", "org.multipage.generator.textSearchDialogBackground"),
	DIALOG_NAVIGATOR("DIALOG_NAVIGATOR", "org.multipage.generator.textDialogNavigatorBackground"),
	AREA_TREE_FRAME("AREA_TREE_FRAME", "org.multipage.generator.textAreaTreeFrameBackground");
	
	/**
	 * Identifier.
	 */
	private String id;
	
	/**
	 * Name string.
	 */
	private String nameString;
	
	/**
	 * Constructor.
	 */
	ColorId(String id, String nameString) {
		
		this.id = id;
		this.nameString = nameString;
	}
	
	/**
	 * Constructor.
	 */
	ColorId(ColorId colorId) {
		
		this.id = colorId.id;
		this.nameString = colorId.nameString;
	}
	
	/**
	 * Get color text.
	 */
	public String getColorText() {
		
		try {
			return Resources.getString(nameString);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get color identifier.
	 * @return
	 */
	public String getColorId() {

		return id;
	}

	/**
	 * Get enumeration member based on input string.
	 * @param colorIdStr
	 * @return
	 */
	public static ColorId getColorId(String colorIdStr) {

		try {
			Class<?> c = Class.forName("org.multipage.generator.ColorId");
			Field fields [] = c.getDeclaredFields();
			// Do loop for all fields.
			for (Field field : fields) {
				if (field.isEnumConstant()) {
					ColorId enumColor = (ColorId) field.get(null);
					if (enumColor.id.compareTo(colorIdStr) == 0) {
						return enumColor;
					}
				}
			}
			
		}
		catch (Exception e) {

			Safe.exception(e);
		}
		return null;
	}
}

