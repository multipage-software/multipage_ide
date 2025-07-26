/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-36
 *
 */

package org.multipage.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.function.BiConsumer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Renderer that displays CSS keyframe.
 * @author vakol
 *
 */
public class RendererCssKeyframeItem extends JPanel {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	// $hide<<$
	/**
	 * Components.
	 */
	private JLabel labelTimePoints;
	private JTable table;

	/**
	 * Create the panel.
	 */
	public RendererCssKeyframeItem() {
		
		try {
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
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		labelTimePoints = new JLabel("time points");
		labelTimePoints.setPreferredSize(new Dimension(100, 14));
		springLayout.putConstraint(SpringLayout.NORTH, labelTimePoints, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, labelTimePoints, 10, SpringLayout.WEST, this);
		add(labelTimePoints);
		
		table = new JTable();
		springLayout.putConstraint(SpringLayout.WEST, table, 0, SpringLayout.EAST, labelTimePoints);
		table.setBorder(new LineBorder(Color.LIGHT_GRAY));
		springLayout.putConstraint(SpringLayout.NORTH, table, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.SOUTH, table, -10, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, table, -10, SpringLayout.EAST, this);
		table.setOpaque(false);
		add(table);
	}

	/**
	 * Is selected flag.
	 */
	protected boolean isSelected;
	
	/**
	 * Has focus flag.
	 */
	protected boolean hasFocus;

	/**
	 * Table model.
	 */
	private DefaultTableModel tableModel;
	
	/**
	 * Post create.
	 */
	private void postCreate() {
		try {
			
			setOpaque(true);
			initTable();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Initialize table.
	 */
	private void initTable() {
		
		tableModel = new DefaultTableModel();
		table.setModel(tableModel);
		
		// Create columns.
		tableModel.addColumn(Resources.getString("org.multipage.gui.textCssPropertyColumn"));
		tableModel.addColumn(Resources.getString("org.multipage.gui.textCssPropertyValueColumn"));
		
		// Set column width.
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getColumnModel().getColumn(1).setMaxWidth(200);
		table.getColumnModel().getColumn(1).setMinWidth(200);
		table.getColumnModel().getColumn(1).setPreferredWidth(200);
		
		// Set grid line colors.
		table.setGridColor(Color.LIGHT_GRAY);
	}

	/**
	 * Set properties.
	 * @param value 
	 */
	public RendererCssKeyframeItem set(CssKeyFrame value, boolean isSelected, boolean hasFocus, int index) {
		
		try {
			this.isSelected = isSelected;
			this.hasFocus = hasFocus;
			
			Color background = Utility.itemColor(index);
			setBackground(background);
			table.setBackground(background);
			
			// Set time points and properties.
			labelTimePoints.setText(value.getTimePointsText());
			
			tableModel.getDataVector().removeAllElements();
			
			value.forEachProperty(new BiConsumer<String, String>() {
				@Override
				public void accept(String namesText, String value) {
					
					// Add table row
					tableModel.addRow(new String [] { namesText, value });
				}
			});
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return this;
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		try {
			
			super.paint(g);
			GraphUtility.drawSelection(g, this, isSelected, hasFocus);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get preferred size.
	 */
	@Override
	public Dimension preferredSize() {
		
		try {
			return new Dimension(200, 100);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return new Dimension();
	}
}
