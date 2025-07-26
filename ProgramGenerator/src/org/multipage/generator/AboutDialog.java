/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.multipage.gui.Images;
import org.multipage.gui.Utility;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

import build_number.BuildNumber;

/**
 * Dialog that displays information about application.
 * @author vakol
 *
 */
public class AboutDialog extends AboutDialogBase {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	private JEditorPane textArea;
	private JLabel labelPicture;
	private JButton buttonClose;
	private JLabel labelBuildNumber;

	/**
	 * Create the dialog.
	 */
	public AboutDialog(Window owner) {
		super(owner, ModalityType.APPLICATION_MODAL);
		
		try {
			// Initialize components.
			initComponents();
			// $hide>>$
			// Post creation.
			postCreation();
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
		setResizable(false);
		setTitle("org.multipage.generator.dialogAboutTitle");
		setSize(new Dimension(559, 350));
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		textArea = new JEditorPane();
		springLayout.putConstraint(SpringLayout.NORTH, textArea, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, textArea, -60, SpringLayout.SOUTH, getContentPane());
		textArea.setContentType("text/html");
		springLayout.putConstraint(SpringLayout.EAST, textArea, -10, SpringLayout.EAST, getContentPane());
		textArea.setFont(new Font("Monospaced", Font.BOLD, 12));
		textArea.setEditable(false);
		textArea.setForeground(new Color(0, 0, 139));
		textArea.setOpaque(false);
		getContentPane().add(textArea);
		
		labelPicture = new JLabel("");
		springLayout.putConstraint(SpringLayout.NORTH, labelPicture, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, labelPicture, -10, SpringLayout.SOUTH, getContentPane());
		labelPicture.setBackground(Color.LIGHT_GRAY);
		springLayout.putConstraint(SpringLayout.WEST, labelPicture, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, textArea, 30, SpringLayout.EAST, labelPicture);
		labelPicture.setPreferredSize(new Dimension(265, 302));
		getContentPane().add(labelPicture);
		
		buttonClose = new JButton("textClose");
		springLayout.putConstraint(SpringLayout.NORTH, buttonClose, -32, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, buttonClose, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, buttonClose, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(buttonClose);
		buttonClose.setMargin(new Insets(0, 0, 0, 0));
		buttonClose.setPreferredSize(new Dimension(80, 25));
		
		labelBuildNumber = new JLabel("build number");
		labelBuildNumber.setHorizontalAlignment(SwingConstants.TRAILING);
		springLayout.putConstraint(SpringLayout.NORTH, labelBuildNumber, 6, SpringLayout.SOUTH, textArea);
		springLayout.putConstraint(SpringLayout.WEST, labelBuildNumber, 0, SpringLayout.WEST, textArea);
		springLayout.putConstraint(SpringLayout.EAST, labelBuildNumber, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(labelBuildNumber);
		buttonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}
	
	/**
	 * Post creation.
	 */
	private void postCreation() {
		try {
			
			// Set build number.
			labelBuildNumber.setText(BuildNumber.getBuildNumber());
			// Localize.
			localize();
			// Set icons.
			setIcons();
			// Center dialog.
			Utility.centerOnScreen(this);
			// Set text.
			textArea.setText(String.format(Resources.getString("org.multipage.generator.messageAbout"), BuildNumber.getVersion()));
			// Set hyperlink listener.
			setHyperlinkListener();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set hyperlink listener.
	 */
	private void setHyperlinkListener() {
		try {
			
			final Desktop desktop = Desktop.getDesktop(); 
			
			textArea.addHyperlinkListener(new HyperlinkListener() {
				@Override
				public void hyperlinkUpdate(HyperlinkEvent e) {
					
					if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
						try {
							desktop.browse(new URI(e.getURL().toString()));
						}
						catch (Exception ex) {
						}
					}  
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Localize components.
	 */
	private void localize() {
		try {
			
			Utility.localize(this);
			Utility.localize(buttonClose);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set icons.
	 */
	private void setIcons() {
		try {
			
			buttonClose.setIcon(Images.getIcon("org/multipage/gui/images/cancel_icon.png"));
			labelPicture.setIcon(Images.getIcon("org/multipage/generator/images/splash.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
