/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package program.builder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import org.maclan.AreaResource;
import org.multipage.generator.AreaResourceRendererBase;
import org.multipage.gui.Utility;
import org.multipage.util.Safe;

/**
 * Renderer that displays information about area resources.
 * @author vakol
 *
 */
public class AreaResourceRendererBuilder extends AreaResourceRendererBase {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Components.
	 */
	private JLabel labelDescription;
	private JLabel labelNamespacePath;
	private JLabel labelTextNamespace;
	private JLabel labelTextMime;
	private JLabel labelMimeType;
	private JLabel labelTextId;
	private JLabel labelId;
	private JCheckBox checkBoxVisible;
	private JCheckBox checkBoxSavedAsText;
	private JLabel labelVisible;
	private JLabel labelSaveAsText;
	private JLabel labelImage;
	private JCheckBox checkProtected;

	/**
	 * Create the panel.
	 */
	public AreaResourceRendererBuilder() {
		
		try {
			// Initialize components.
			initComponents();
			// Localize.
			// $hide>>$
			setComponentsReferences(
					labelDescription,
					labelNamespacePath,
					labelTextNamespace,
					labelTextMime,
					labelMimeType,
					labelTextId,
					labelId,
					checkBoxVisible,
					checkBoxSavedAsText,
					labelVisible,
					labelSaveAsText,
					labelImage
					);
			localize();
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
		setEnabled(false);
		setPreferredSize(new Dimension(680, 75));
		setMinimumSize(new Dimension(10, 100));
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		labelDescription = new JLabel("description");
		springLayout.putConstraint(SpringLayout.WEST, labelDescription, 10, SpringLayout.WEST, this);
		labelDescription.setFont(new Font("Tahoma", Font.PLAIN, 11));
		springLayout.putConstraint(SpringLayout.NORTH, labelDescription, 5, SpringLayout.NORTH, this);
		add(labelDescription);
		
		labelNamespacePath = new JLabel("namespace path");
		springLayout.putConstraint(SpringLayout.NORTH, labelNamespacePath, 6, SpringLayout.SOUTH, labelDescription);
		springLayout.putConstraint(SpringLayout.EAST, labelNamespacePath, -114, SpringLayout.EAST, this);
		labelNamespacePath.setFont(new Font("Tahoma", Font.ITALIC, 11));
		add(labelNamespacePath);
		
		labelTextNamespace = new JLabel("org.multipage.generator.textNameSpace");
		springLayout.putConstraint(SpringLayout.WEST, labelTextNamespace, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.WEST, labelNamespacePath, 6, SpringLayout.EAST, labelTextNamespace);
		labelTextNamespace.setFont(new Font("Tahoma", Font.ITALIC, 11));
		springLayout.putConstraint(SpringLayout.NORTH, labelTextNamespace, 6, SpringLayout.SOUTH, labelDescription);
		add(labelTextNamespace);
		
		labelTextMime = new JLabel("org.multipage.generator.textMimeType");
		springLayout.putConstraint(SpringLayout.NORTH, labelTextMime, 6, SpringLayout.SOUTH, labelTextNamespace);
		springLayout.putConstraint(SpringLayout.WEST, labelTextMime, 10, SpringLayout.WEST, this);
		labelTextMime.setFont(new Font("Tahoma", Font.ITALIC, 11));
		add(labelTextMime);
		
		labelMimeType = new JLabel("mime type");
		springLayout.putConstraint(SpringLayout.WEST, labelMimeType, 6, SpringLayout.EAST, labelTextMime);
		labelMimeType.setFont(new Font("Tahoma", Font.ITALIC, 11));
		springLayout.putConstraint(SpringLayout.NORTH, labelMimeType, 6, SpringLayout.SOUTH, labelNamespacePath);
		add(labelMimeType);
		
		labelTextId = new JLabel("org.multipage.generator.textResourceId2");
		springLayout.putConstraint(SpringLayout.EAST, labelMimeType, -6, SpringLayout.WEST, labelTextId);
		springLayout.putConstraint(SpringLayout.NORTH, labelTextId, 0, SpringLayout.NORTH, labelTextMime);
		springLayout.putConstraint(SpringLayout.WEST, labelTextId, 168, SpringLayout.WEST, this);
		labelTextId.setFont(new Font("Tahoma", Font.ITALIC, 11));
		add(labelTextId);
		
		labelId = new JLabel("ID");
		labelId.setPreferredSize(new Dimension(50, 14));
		springLayout.putConstraint(SpringLayout.NORTH, labelId, 0, SpringLayout.NORTH, labelTextId);
		springLayout.putConstraint(SpringLayout.WEST, labelId, 3, SpringLayout.EAST, labelTextId);
		labelId.setFont(new Font("Tahoma", Font.ITALIC, 11));
		add(labelId);
		
		checkBoxVisible = new JCheckBox("");
		checkBoxVisible.setEnabled(false);
		checkBoxVisible.setFont(new Font("Tahoma", Font.ITALIC, 11));
		springLayout.putConstraint(SpringLayout.NORTH, checkBoxVisible, 6, SpringLayout.SOUTH, labelNamespacePath);
		springLayout.putConstraint(SpringLayout.SOUTH, checkBoxVisible, 20, SpringLayout.SOUTH, labelNamespacePath);
		checkBoxVisible.setHorizontalTextPosition(SwingConstants.LEADING);
		checkBoxVisible.setHorizontalAlignment(SwingConstants.LEFT);
		checkBoxVisible.setOpaque(false);
		add(checkBoxVisible);
		
		checkBoxSavedAsText = new JCheckBox("");
		checkBoxSavedAsText.setEnabled(false);
		springLayout.putConstraint(SpringLayout.NORTH, checkBoxSavedAsText, 6, SpringLayout.SOUTH, labelNamespacePath);
		springLayout.putConstraint(SpringLayout.SOUTH, checkBoxSavedAsText, 20, SpringLayout.SOUTH, labelNamespacePath);
		checkBoxSavedAsText.setMargin(new Insets(2, 0, 2, 2));
		checkBoxSavedAsText.setHorizontalAlignment(SwingConstants.LEFT);
		checkBoxSavedAsText.setFont(new Font("Tahoma", Font.ITALIC, 11));
		checkBoxSavedAsText.setOpaque(false);
		checkBoxSavedAsText.setHorizontalTextPosition(SwingConstants.LEFT);
		add(checkBoxSavedAsText);
		
		labelVisible = new JLabel("org.multipage.generator.textVisible");
		springLayout.putConstraint(SpringLayout.WEST, labelVisible, 10, SpringLayout.EAST, labelId);
		springLayout.putConstraint(SpringLayout.WEST, checkBoxVisible, 3, SpringLayout.EAST, labelVisible);
		labelVisible.setFont(new Font("Tahoma", Font.ITALIC, 11));
		springLayout.putConstraint(SpringLayout.SOUTH, labelVisible, 0, SpringLayout.SOUTH, labelTextMime);
		add(labelVisible);
		
		labelSaveAsText = new JLabel("org.multipage.generator.textSavedAsText");
		springLayout.putConstraint(SpringLayout.WEST, checkBoxSavedAsText, 3, SpringLayout.EAST, labelSaveAsText);
		springLayout.putConstraint(SpringLayout.WEST, labelSaveAsText, 10, SpringLayout.EAST, checkBoxVisible);
		labelSaveAsText.setFont(new Font("Tahoma", Font.ITALIC, 11));
		springLayout.putConstraint(SpringLayout.NORTH, labelSaveAsText, 6, SpringLayout.SOUTH, labelNamespacePath);
		add(labelSaveAsText);
		
		labelImage = new JLabel("");
		labelImage.setPreferredSize(new Dimension(62, 62));
		springLayout.putConstraint(SpringLayout.NORTH, labelImage, 0, SpringLayout.NORTH, labelDescription);
		springLayout.putConstraint(SpringLayout.SOUTH, labelImage, 6, SpringLayout.SOUTH, labelTextMime);
		add(labelImage);
		
		checkProtected = new JCheckBox("builder.textResourceProtectedCheck");
		checkProtected.setOpaque(false);
		checkProtected.setFont(new Font("Tahoma", Font.ITALIC, 11));
		checkProtected.setForeground(Color.BLACK);
		checkProtected.setEnabled(false);
		checkProtected.setHorizontalTextPosition(SwingConstants.LEFT);
		checkProtected.setHorizontalAlignment(SwingConstants.LEFT);
		springLayout.putConstraint(SpringLayout.NORTH, checkProtected, 2, SpringLayout.SOUTH, labelNamespacePath);
		springLayout.putConstraint(SpringLayout.WEST, labelImage, 10, SpringLayout.EAST, checkProtected);
		springLayout.putConstraint(SpringLayout.WEST, checkProtected, 10, SpringLayout.EAST, checkBoxSavedAsText);
		add(checkProtected);
	}

	/* (non-Javadoc)
	 * @see org.multipage.generator.AreaResourceRendererBase#localize()
	 */
	@Override
	protected void localize() {
		try {
			
			super.localize();
			Utility.localize(checkProtected);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/* (non-Javadoc)
	 * @see org.multipage.generator.AreaResourceRendererBase#setProperties(com.maclan.AreaResource, java.lang.String, java.lang.String, int, boolean, boolean)
	 */
	@Override
	public void setProperties(AreaResource resource, String mimeType,
			String namespacePath, int index, boolean isSelected,
			boolean hasFocus) {
		try {
			
			super.setProperties(resource, mimeType, namespacePath, index, isSelected,
					hasFocus);
			checkProtected.setSelected(resource.isProtected());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
