/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2020-12-14
 *
 */
package org.multipage.generator;

import java.io.Serializable;

import org.multipage.util.Safe;

/**
 * State of tab that contains area diagram.
 * @author vakol
 *
 */
public class AreaDiagramTabState extends AreasTabState implements Serializable {

	/**
	 * Version
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Initial X translation of the diagram.
	 */
	public double translationx = 0.0;
	
	/**
	 * Initial Y translation of the diagram.
	 */
	public double translationy = 0.0;
	
	/**
	 * Initial zoom of the diagram.
	 */
	public double zoom = 1.0;
	
	/**
	 * Constructor
	 */
	public AreaDiagramTabState() {
		
		type = TabType.areasDiagram;
	}
		
	/**
	 * Set this tab state from the input tab state
	 * @param tabState
	 */
	public void setTabStateFrom(AreaDiagramTabState tabState) {
		try {
			
			super.setTabStateFrom(tabState);
			
			type = TabType.areasDiagram;
			
			translationx = tabState.translationx;
			translationx = tabState.translationx;
			zoom = tabState.zoom;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
