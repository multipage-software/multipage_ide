/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.io.IOException;

/**
 * Class that serializes application state information.
 * @author vakol
 *
 */
public class SerializeStateAdapter {

	/**
	 * On read state.
	 */
	protected void onReadState(StateInputStream inputStream)
		throws IOException, ClassNotFoundException {
		
		// Override this method.
	}

	/**
	 * On set default state.
	 */
	protected void onSetDefaultState() {
		
		// Override this method.
	}

	/**
	 * On write state.
	 * @param saveStateOutputStream 
	 */
	protected void onWriteState(StateOutputStream outputStream)
		throws IOException {

		// Override this method.
	}
}
