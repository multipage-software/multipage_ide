/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */
package org.multipage.generator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.maclan.Area;
import org.maclan.AreasModel;
import org.multipage.gui.GraphUtility;
import org.multipage.gui.Images;
import org.multipage.util.Safe;

/**
 * Favorites list renderer.
 * @author vakol
 *
 */
class FavoritesRenderer implements ListCellRenderer<Long> {
	
	/**
	 * Asterisk.
	 */
	private static Icon asteriskIcon;
	
	/**
	 * Static constructor.
	 */
	static {
		try {
			
			asteriskIcon = Images.getIcon("org/multipage/generator/images/favorite.png");
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
		
	/**
	 * Label.
	 */
	private class FavoriteLabel extends JLabel {

		/**
		 * Version.
		 */
		private static final long serialVersionUID = 1L;
		
		/**
		 * Flags.
		 */
		private boolean isSelected;
		private boolean hasFocus;

		/**
		 * Constructor.
		 */
		public FavoriteLabel() {
			try {
				
				setIcon(asteriskIcon);
			}
			catch(Throwable expt) {
				Safe.exception(expt);
			};
		}

		/**
		 * Reset properties.
		 */
		public void resetProperties() {
			try {
				
				setText("");
				setForeground(Color.BLACK);
				this.isSelected = false;
				this.hasFocus = false;
			}
			catch(Throwable expt) {
				Safe.exception(expt);
			};
		}

		/**
		 * Set properties.
		 * @param areaId
		 * @param hasFocus 
		 * @param isSelected 
		 */
		public void setProperties(long areaId, boolean isSelected, boolean hasFocus) {
			try {
				
				// Get area.
				AreasModel model = ProgramGenerator.getAreasModel();
				Area area = model.getArea(areaId);
				
				if (area == null) {
					resetProperties();
					return;
				}
				setText(area.getDescriptionForGui());
				
				// Area selection.
				Color color = Color.BLACK;
				Object user = area.getUser();
				if (user instanceof AreaShapes) {
					AreaShapes shapes = (AreaShapes) user;
					
					if (areaDiagramContainerPanel.isShapeSelected(shapes)) {
						color = Color.RED;
					}
				}
				setForeground(color);
				
				this.isSelected = isSelected;
				this.hasFocus = hasFocus;
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
				super.paint(g);
				GraphUtility.drawSelection(g, this, isSelected, hasFocus);
			}
			catch (Throwable e) {
				Safe.exception(e);
			}
		}
	}
	
	/**
	 * Reference to diagram panel container.
	 */
	private final AreaDiagramContainerPanel areaDiagramContainerPanel;

	/**
	 * Label.
	 */
	private FavoriteLabel label;

	/**
	 * Constructor.
	 * @param areaDiagramContainerPanel 
	 */
	public FavoritesRenderer(AreaDiagramContainerPanel areaDiagramContainerPanel) {
		this.areaDiagramContainerPanel = areaDiagramContainerPanel;
		try {
			
			label = new FavoriteLabel();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Return renderer component.
	 */
	@Override
	public Component getListCellRendererComponent(JList<? extends Long> list, Long areaId,
			int index, boolean isSelected, boolean cellHasFocus) {
		
		try {
			// Check value type.
			if (areaId == null) {
				label.resetProperties();
			}
			else {
				label.setProperties(areaId, isSelected, cellHasFocus);
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return label;
	}
}