/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.maclan.Area;
import org.multipage.util.Safe;


/**
 * Area coordinates class.
 * @author vakol
 *
 */
public class AreaCoordinates {

	/**
	 * This area percent.
	 */
	public static final double areaFreeZonePercent = 28.5;
	
	/**
	 * Get height.
	 */
	public static double getHeight(double width) {
		
		return width * (100 - areaFreeZonePercent) / 100;
	}
	/**
	 * Gets caption rectangle.
	 * @return
	 */
	public Rectangle2D getCaptionRect() {
		
		try {
			return new Rectangle2D.Double(x, y, width, getLabelHeight());
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Coordinates.
	 */
	private double x;
	private double y;
	private double width;
	
	/**
	 * Area.
	 */
	private Area area;
	
	/**
	 * Parent area.
	 */
	private Area parentArea;
	
	/**
	 * Show more info flag.
	 */
	private boolean showMoreInfo;
	
	/**
	 * Is reference flag.
	 */
	private boolean isReference;
	
	/**
	 * Constructor.
	 * @param x
	 * @param y
	 * @param width
	 * @param area
	 * @param parentArea
	 * @param showMoreInfo
	 * @param isReference
	 */
	public AreaCoordinates(double x, double y, double width,
			Area area, Area parentArea, boolean showMoreInfo,
			boolean isReference) {

		this.x = x;
		this.y = y;
		this.width = width;
		this.area = area;
		this.parentArea = parentArea;
		this.showMoreInfo = showMoreInfo;
		this.isReference = isReference;
	}

	/**
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	/**
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(double width) {
		this.width = width;
	}

	/**
	 * Multiply coordinates.
	 */
	public void multiply(double multiply) {

		x *= multiply;
		y *= multiply;
		width *= multiply;
	}

	/**
	 * Get font size.
	 */
	public double getLabelFontSize() {
		
		try {
			// Compute font size.
			return getHeight(width) * areaFreeZonePercent / 100 * 0.4;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0.0;
	}

	/**
	 * Get font size.
	 */
	public double getInfoFontSize() {
		
		try {
			// Compute font size.
			return getHeight(width) * 0.08;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0.0;
	}
	
	/**
	 * Get description font size.
	 * @return
	 */
	public double getDescriptionFontSize() {
		
		try {
			// Compute description font size.
			return getLabelFontSize() * 0.2;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0.0;
	}

	/**
	 * Get label height.
	 * @return
	 */
	public double getLabelHeight() {
		
		try {
			return getHeight() * areaFreeZonePercent / 100;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0.0;
	}
	
	/**
	 * Get label rectangle.
	 * @return
	 */
	public Rectangle2D getLabel() {
		
		try {
			return new Rectangle2D.Double(x, y, width, getLabelHeight());
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Get height.
	 * @return
	 */
	public double getHeight() {
		
		try {
			return getHeight(width);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0.0;
	}

	/**
	 * Area free space X position.
	 * @return
	 */
	public double getFreeX() {

		return x + (100 - areaFreeZonePercent) * width / 100;
	}

	/**
	 * Area free space Y position.
	 * @return
	 */
	public double getFreeY() {

		return y + areaFreeZonePercent * getHeight() / 100;
	}

	/**
	 * Get area free space width.
	 * @return
	 */
	public double getFreeWidth() {

		return areaFreeZonePercent * width / 100;
	}

	/**
	 * Get area free space height.
	 * @return
	 */
	public double getFreeHeight() {

		return (100 - areaFreeZonePercent) * getHeight() / 100;
	}
	
	/**
	 * Get free rectangle.
	 * @return
	 */
	public Rectangle2D getFree() {
		
		try {
			return new Rectangle2D.Double(getFreeX(), getFreeY(),
					getFreeWidth(), getFreeHeight());
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	/**
	 * Gets area rectangle.
	 * @return
	 */
	public Rectangle2D getRectangle() {
		
		try {
			return new Rectangle2D.Double(x, y, width, getHeight());
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Gets child area rectangle.
	 * @return
	 */
	public Rectangle2D getChildAreaRectangle() {
		
		try {
			double labelHeight = getLabelHeight();
			double freeWidth = getFreeWidth();
			return new Rectangle2D.Double(x, y + labelHeight, width - freeWidth, getHeight() - labelHeight);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * @return the parentArea
	 */
	public Area getParentArea() {
		return parentArea;
	}
	
	/**
	 * Gets center of shape.
	 * @return
	 */
	public Point2D getLabelCenter() {
		
		try {
			return new Point2D.Double(x + width / 2.0, y + getLabelHeight() / 2.0);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		try {
			AreaDiagramPanel diagram = GeneratorMainFrame.getFrame().getAreaDiagram();
			
			if (diagram != null) {
				return diagram.undoTransformationX(x) + ", "
						+ diagram.undoTransformationY(y) + ", "
						+ diagram.undoTransformationZoom(width) + ", "
						+ diagram.undoTransformationZoom(getHeight());
			}
			else {
				return x + ", " + y + ", " + width + ", " + getHeight();
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "error";
	}
	/**
	 * @return the inherits
	 */
	public boolean getInherits() {
		
		try {
			return area.inheritsFrom(parentArea);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Get help icon size.
	 * @return
	 */
	public double getHelpIconSize() {
		
		try {
			return getLabelHeight() * 0.5;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0.0;
	}
	
	/**
	 * Get help icon X position.
	 * @return
	 */
	public double getHelpIconX() {
		
		try {
			double helpSize = getHelpIconSize();
			double margins = helpSize * 0.2;
			return getX() + getWidth() - helpSize - margins;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0.0;
	}
	
	/**
	 * Get help icon Y position.
	 * @return
	 */
	public double getHelpIconY() {
		
		try {
			double helpSize = getHelpIconSize();
			double margins = helpSize * 0.2;
			return getY() + margins;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0.0;
	}
	
	/**
	 * Returns true value if the position is over the help icon.
	 * @param point
	 * @return
	 */
	public boolean isOverHelpIcon(Point2D point) {
		
		try {
			double size = getHelpIconSize();
			Rectangle2D rectangle = new Rectangle2D.Double(getHelpIconX(), getHelpIconY(),
					size, size);
			return rectangle.contains(point);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * @return the hasSubShapes
	 */
	public boolean isShowMoreInfo() {
		return showMoreInfo;
	}
	/**
	 * @return the isReference
	 */
	public boolean isRecursion() {
		return isReference;
	}
	
	/**
	 * Get shape center.
	 * @return
	 */
	public Point2D getCenter() {
		
		try {
			return new Point2D.Double(x + width / 2.0, y + getHeight() / 2.0);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
}