/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package program.builder;

import java.awt.Component;
import java.util.Properties;

import org.maclan.Area;
import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.multipage.basic.ProgramBasic;
import org.multipage.generator.AreaDiagramPanel;
import org.multipage.generator.AreaDiagramContainerPanel;
import org.multipage.generator.GeneratorMainFrame;
import org.multipage.util.Obj;
import org.multipage.util.Safe;

/**
 * Displays diagram with areas.  
 * @author vakol
 *
 */
public class AreaDiagramBuilderPanel extends AreaDiagramPanel {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * @param parentEditor
	 */
	public AreaDiagramBuilderPanel(AreaDiagramContainerPanel parentEditor) {
		super(parentEditor);
		
	}
	
	/**
	 * Add new area to existing parent area.
	 */
	@Override
	protected boolean addNewAreaConservatively(Area parentArea, Area newArea, Component parentComponent) {
		
		try {
			Obj<Boolean> inheritance = new Obj<Boolean>();
			Obj<String> relationNameSub = new Obj<String>();
			Obj<String> relationNameSuper = new Obj<String>();
			
			// Get new area description.
			if (!ConfirmNewArea.showConfirmDialog(parentComponent, newArea, inheritance,
					relationNameSub, relationNameSuper)) {
				return false;
			}
			
			// Prepare prerequisites.
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
	
			// Try to add new area.
			MiddleResult result = middle.insertArea(
						login,
						parentArea, newArea, inheritance.ref, relationNameSub.ref,
						relationNameSuper.ref);
			
			// On error inform user.
			if (result != MiddleResult.OK) {
				result.show(this);
				return false;
			}
			
			// Update all modules.
			GeneratorMainFrame.updateAll();
			
			// Select the new area.
			selectArea(newArea.getId(), true);
			
			return true;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
}
