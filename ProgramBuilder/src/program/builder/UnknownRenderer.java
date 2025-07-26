/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package program.builder;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.multipage.gui.Images;
import org.multipage.gui.Utility;
import org.multipage.util.Safe;

/**
 * Panel that displays empty value text.
 * @author vakol
 *
 */
public class UnknownRenderer extends JPanel {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Components.
	 */
	private JLabel label;

	/**
	 * Create the panel.
	 */
	public UnknownRenderer() {
		
		try {
			// Initialize components.
			initComponents();
			// Post creation.
			postCreate();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Initialize components.
	 */
	private void initComponents() {
		try {
			
			setLayout(new BorderLayout(0, 0));
			
			label = new JLabel("builder.textUnknownValue");
			label.setHorizontalAlignment(SwingConstants.CENTER);
			add(label, BorderLayout.CENTER);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			Utility.localize(label);
			label.setIcon(Images.getIcon("org/multipage/generator/images/unknown.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
