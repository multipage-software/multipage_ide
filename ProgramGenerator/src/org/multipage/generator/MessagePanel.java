/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2020-04-06
 *
 */
package org.multipage.generator;

import javax.swing.JPanel;

import org.multipage.gui.Images;
import org.multipage.util.Safe;

/**
 * Panel that displays message.
 * @author vakol
 *
 */
class MessagePanel extends JPanel {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Initialize components.
	 */
    private void initComponents() {

        text = new javax.swing.JLabel();

        text.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(text, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(143, 143, 143)
                .addComponent(text)
                .addContainerGap(143, Short.MAX_VALUE))
        );
    }

    // Variables declaration - do not modify
    private javax.swing.JLabel text;
    // End of variables declaration


	/**
	 * Constructor.
	 */
	public MessagePanel() {
		
		try {
			initComponents();
			// Set icon.
			text.setIcon(Images.getIcon("org/multipage/generator/images/error.png"));
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Set message text.
	 * @param string
	 */
	public void setText(String string) {
		try {
			
			text.setText(string);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}