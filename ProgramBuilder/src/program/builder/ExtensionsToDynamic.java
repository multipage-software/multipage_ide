/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package program.builder;

import org.maclan.AreasModel;
import org.maclan.Middle;
import org.multipage.generator.GeneratorMainFrame;
import org.multipage.gui.StateSerializer;

/**
 * Interface for extensions to Dynamic application.
 * @author vakol
 *
 */
public interface ExtensionsToDynamic {

	/**
	 * Initialize dynamic level.
	 * @param language
	 * @param country
	 * @param serializer
	 * @return
	 */
	boolean initializeLevel(String language, String country,
			StateSerializer serializer);


	/**
	 * Create new main frame.
	 * @return
	 */
	GeneratorMainFrame newMainFrame();

	/**
	 * Get middle.
	 * @return
	 */
	Middle getMiddle();

	/**
	 * Create new areas model.
	 * @return
	 */
	AreasModel newAreasModel();
}
