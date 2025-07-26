/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.util.LinkedList;

import javax.swing.JFrame;

import org.maclan.Area;
import org.multipage.translator.TranslatorDialog;
import org.multipage.util.Safe;

/**
 * Dialog that displays Translator.
 * @author vakol
 *
 */
public class GeneratorTranslatorDialog extends TranslatorDialog {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Launch the dialog.
	 * @param areas 
	 */
	public static void showDialog(JFrame parentFrame, LinkedList<Area> areas) {
		try {
			
			/*if (areas.isEmpty()) {
				Utility.show(null, "org.multipage.generator.messageSelectAreasToTranslate");
				return;
			}*/
			// Create dialog, set selected areas and show the dialog.
			GeneratorTranslatorDialog dialog = new GeneratorTranslatorDialog(
					parentFrame, areas);
			dialog.setVisible(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Constructor.
	 * @param parentFrame
	 * @param areas 
	 * @param middle
	 * @param login
	 */
	public GeneratorTranslatorDialog(JFrame parentFrame, LinkedList<Area> areas) {
		super(null, ModalityType.MODELESS, areas);
	}

	/* (non-Javadoc)
	 * @see org.multipage.translator.TranslatorDialog#onLoadLangauges()
	 */
	@Override
	protected void onLoadLangauges() {

	}

	/* (non-Javadoc)
	 * @see org.multipage.translator.TranslatorDialog#onUpdateInformation()
	 */
	@Override
	protected void onUpdateInformation() {
		
		
	}
}