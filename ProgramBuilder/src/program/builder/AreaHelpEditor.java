/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package program.builder;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.multipage.basic.ProgramBasic;
import org.multipage.generator.EditorTabActions;
import org.multipage.generator.ProgramGenerator;
import org.multipage.gui.TextEditorPane;
import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.Safe;
import org.maclan.Area;
import org.maclan.MiddleResult;

/**
 * Editor panel that shows help information for the area.
 * @author vakol
 *
 */
public class AreaHelpEditor extends JPanel implements EditorTabActions {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Text editor.
	 */
	private TextEditorPane editor;

	/**
	 * Area reference.
	 */
	private Area area;
	
	// $hide<<$
	/**
	 * Components.
	 */
	private JPanel panelAux;


	/**
	 * Create the panel.
	 */
	public AreaHelpEditor() {
		
		try {
			// Initialize components.
			initComponents();
			// $hide>>$
			postCreate();
			// $hide<<$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Initialize components.
	 */
	private void initComponents() {
		setLayout(new BorderLayout(0, 0));
		
		panelAux = new JPanel();
		add(panelAux);
		panelAux.setLayout(new BorderLayout(0, 0));
	}
	
	/**
	 * Post create.
	 */
	private void postCreate() {
		try {
			
			editor = new TextEditorPane(Utility.findWindow(this), true);
			editor.setExtractBody(false);
			panelAux.add(editor);
			editor.selectHtmlEditor(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set area.
	 * @param area
	 */
	public void setArea(Area area) {
		
		this.area = area;
	}

	/**
	 * Save help.
	 */
	public void save() {
		try {
			
			// Save help text.
			String helpText = editor.getText();
	        MiddleResult result = ProgramBasic.getMiddle().updateHelp(
	        		ProgramBasic.getLoginProperties(), area, helpText);
	        if (result.isNotOK()) {
	        	result.show(this);
	        }
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Load help.
	 */
	public void load() {
		try {
			
			// Reload area object.
			area = ProgramGenerator.getArea(area.getId());
			
			// Load help text.
			Obj<String> helpText = new Obj<String>("");
	        MiddleResult result = ProgramBasic.getMiddle().loadHelp(
	        		ProgramBasic.getLoginProperties(), area, helpText);
	        if (result.isNotOK()) {
	        	result.show(this);
	        }
	        
	        editor.setText(helpText.ref);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load panel information.
	 */
	@Override
	public void onLoadPanelInformation() {
		try {
			
			// Load information.
			load();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Save panel information.
	 */
	@Override
	public void onSavePanelInformation() {
		try {
			
			// Save information.
			save();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On close the window.
	 */
	public void onClose() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Update dialog components.
	 */
	public void updateComponents() {
		try {
			
			// TODO: <---MAKE Update components.
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
}
