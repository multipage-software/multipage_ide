/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.maclan.Resource;
import org.multipage.gui.GraphUtility;
import org.multipage.gui.Utility;
import org.multipage.util.Safe;

/**
 * Base panel class that displays resource within a namespace.
 * @author vakol
 *
 */
public class NamespaceResourceRendererBase extends JPanel {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Is selected flag.
	 */
	protected boolean isSelected = false;
	
	/**
	 * Has focus flag.
	 */
	protected boolean hasFocus = false;
	
	/**
	 * Components' references.
	 */
	private JLabel labelTitle;
	private JLabel labelMimeTypeLabel;
	private JLabel labelMimeType;
	private JLabel labelResourceIdLabel;
	private JLabel labelResourceId;
	private JCheckBox checkBoxSaveAsText;
	private JLabel labelImage;
	private JCheckBox checkVisible;
	
	protected void setComponentsReferences(
			JLabel labelTitle,
			JLabel labelMimeTypeLabel,
			JLabel labelMimeType,
			JLabel labelResourceIdLabel,
			JLabel labelResourceId,
			JCheckBox checkBoxSaveAsText,
			JLabel labelImage,
			JCheckBox checkVisible
			) {
		
		this.labelTitle = labelTitle;
		this.labelMimeTypeLabel = labelMimeTypeLabel;
		this.labelMimeType = labelMimeType;
		this.labelResourceIdLabel = labelResourceIdLabel;
		this.labelResourceId = labelResourceId;
		this.checkBoxSaveAsText = checkBoxSaveAsText;
		this.labelImage = labelImage;
		this.checkVisible = checkVisible;
	}
	

	/**
	 * Set proprties.
	 * @param resource
	 * @param mimeType
	 * @param index
	 * @param isSelected
	 * @param hasFocus
	 */
	public void setProperties(Resource resource, String mimeType, int index,
			boolean isSelected, boolean hasFocus) {
		try {
			
			this.isSelected = isSelected;
			this.hasFocus = hasFocus;
			
			// Get background color.
			Color backGroundColor = Utility.itemColor(index);
			// Set color.
			setBackground(backGroundColor);
			
			// Set values.
			labelTitle.setText(resource.getDescription());
			labelMimeType.setText(mimeType);
			labelResourceId.setText(String.valueOf(resource.getId()));
			checkBoxSaveAsText.setSelected(resource.isSavedAsText());
			checkVisible.setSelected(resource.isVisible());
			
			if (resource.getImage() != null) {
				labelImage.setIcon(new ImageIcon(resource.getImage()));
			}
			else {
				labelImage.setIcon(null);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		
		try {
			// Paint component.
			super.paint(g);
			// Draw selection.
			GraphUtility.drawSelection(g, this, isSelected, hasFocus);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Localize components.
	 */
	protected void localize() {
		try {
			
			Utility.localize(labelMimeTypeLabel);
			Utility.localize(labelResourceIdLabel);
			Utility.localize(checkBoxSaveAsText);
			Utility.localize(checkVisible);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
