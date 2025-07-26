/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.text.AttributedCharacterIterator;
import java.util.LinkedList;

import org.multipage.util.Safe;

/**
 * Utilities for area graphs.
 * @author vakol
 *
 */
public class GraphUtility {
	
	/**
	 * Colors.
	 */
	private static final Color colorHighlight = new Color(20, 20, 20);
	
	/**
	 * Gradient rectangle intensity multiplier.
	 */
	private static final float gradientRectangleIntensityMultiplier = 1.0f;
	
	/**
	 * Image for true value.
	 */
	private static BufferedImage trueImage;

	/**
	 * Image for false value.
	 */
	private static BufferedImage falseImage;
	
	/**
	 * Default texture paint.
	 */
	private static TexturePaint defaultTexturePaint;

	/**
	 * Static constructor.
	 */
	static {
		try {
			
			// Set images.
			BufferedImage image = Images.getImage("org/multipage/gui/images/default_texture.png");
			if (image != null) {
				defaultTexturePaint = new TexturePaint(image, new Rectangle(0, 0, 5, 5));
			}
			
			trueImage = Images.getImage("org/multipage/gui/images/true.png");
			falseImage = Images.getImage("org/multipage/gui/images/false.png");
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Draw gradient rectangle.
	 * @param affectedOpacity 
	 */
	public static void drawGradientRectangle(Graphics2D g2, int x, int y,
			int width, int height, int lineSize, Color color, float intensity) {

        try {
			int halfSize = lineSize / 2;
			
			// Draw rectangle.
			g2.setColor(color);
			g2.drawRect(x, y, width, height);
			
			// Set background color.
			Color backgroundColor = new Color(color.getRed() / 255,
					color.getGreen() / 255,
					color.getBlue() / 255,
					0.0f);
			
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
					gradientRectangleIntensityMultiplier * (float) intensity));
			
			// Get old paint.
			Paint oldPaint = g2.getPaint();
			
			Polygon polygon;
			
			// Left border.
			// Create new gradient paint.
			g2.setPaint(new GradientPaint(x - halfSize, 0, backgroundColor, x, 0,
					color, true));
			
			polygon = new Polygon();
			
			polygon.addPoint(x - halfSize, y - halfSize);
			polygon.addPoint(x + halfSize, y + halfSize);
			polygon.addPoint(x + halfSize, y + height - halfSize);
			polygon.addPoint(x - halfSize, y + height + halfSize);
			
			// Fill polygon.
			g2.fill(polygon);
			
			// Top border.
			// Create new gradient paint.
			g2.setPaint(new GradientPaint(0, y - halfSize, backgroundColor, 0, y,
					color, true));
			
			polygon = new Polygon();
	
			polygon.addPoint(x - halfSize, y - halfSize);
			polygon.addPoint(x + width + halfSize, y - halfSize);
			polygon.addPoint(x + width - halfSize, y + halfSize);
			polygon.addPoint(x + halfSize, y + halfSize);
			
			// Fill polygon.
			g2.fill(polygon);
			
			// Right border.
			// Create new gradient paint.
			g2.setPaint(new GradientPaint(x + width - halfSize, 0, backgroundColor,
					x + width, 0, color, true));
			
			polygon = new Polygon();
			
			polygon.addPoint(x + width - halfSize, y + halfSize);
			polygon.addPoint(x + width + halfSize, y - halfSize);
			polygon.addPoint(x + width + halfSize, y + height + halfSize);
			polygon.addPoint(x + width - halfSize, y + height - halfSize);
			
			// Fill polygon.
			g2.fill(polygon);
			
			// Bottom border.
			// Create new gradient paint.
			g2.setPaint(new GradientPaint(0, y + height - halfSize, backgroundColor,
					0, y + height, color, true));
			
			polygon = new Polygon();
			
			polygon.addPoint(x + halfSize, y + height - halfSize);
			polygon.addPoint(x + width - halfSize, y + height - halfSize);
			polygon.addPoint(x + width + halfSize, y + height + halfSize);
			polygon.addPoint(x - halfSize, y + height + halfSize);
			
			// Fill polygon.
			g2.fill(polygon);
			
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	
			// Set old paint.
			g2.setPaint(oldPaint);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Compute arrow.
	 * @param begin
	 * @param end
	 * @param arrowAlpha
	 * @param arrowLength
	 * @param pointA
	 * @param pointB
	 */
	public static void computeArrow(Point begin, Point end,
			double arrowAlpha, double arrowLength, Point pointA, Point pointB) {
		
		try {
			double alpha;
			
			// Compute angle.
			alpha = Math.atan((double) (end.y - begin.y)
					/ (double) (end.x - begin.x));
			
			double gamma = alpha - arrowAlpha / 2.0;
			double delta = alpha + arrowAlpha / 2.0;
			
			double dAX = arrowLength * Math.cos(gamma);
			double dAY = arrowLength * Math.sin(gamma);
			double dBX = arrowLength * Math.cos(delta);
			double dBY = arrowLength * Math.sin(delta);
			
			// Set arrow points.
			if (end.x >= begin.x) {
				pointA.x = end.x - (int) dAX;
				pointA.y = end.y - (int) dAY;
				pointB.x = end.x - (int) dBX;
				pointB.y = end.y - (int) dBY;
			}
			else {
				pointA.x = end.x + (int) dAX;
				pointA.y = end.y + (int) dAY;
				pointB.x = end.x + (int) dBX;
				pointB.y = end.y + (int) dBY;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Create arrow shape.
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param alpha
	 * @param length
	 * @return
	 */
	public static LinkedList<Shape> createArrowShape(int x1, int y1, int x2, int y2,
			double alpha, double length) {
		
		try {
			LinkedList<Shape> arrowShape = new LinkedList<Shape>();
			
			// Compute arrow points.
			Point pointA = new Point();
			Point pointB = new Point();
			
			computeArrow(new Point(x1, y1), new Point(x2, y2), alpha, length, pointA, pointB);
			
			// Create lines.
			arrowShape.add(new Line2D.Double(x1, y1, x2, y2));
			arrowShape.add(new Line2D.Double(x2, y2, pointA.x, pointA.y));
			arrowShape.add(new Line2D.Double(x2, y2, pointB.x, pointB.y));
			
			return arrowShape;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Draw arrow.
	 * @param g2
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param alpha 
	 * @param length 
	 */
	public static void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2,
			double alpha, double length) {
		
		try {
			// Get arrow shape.
			LinkedList<Shape> arrowShape = createArrowShape(x1, y1, x2, y2, alpha, length);
			
			// Draw it.
			for (Shape shape : arrowShape) {
				g2.draw(shape);
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Gets list of line surroundings.
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param radius 
	 * @return
	 */
	public static LinkedList<Shape> createLineSurrounding(int x1,
			int y1, int x2, int y2, double radius) {
		
		try {
			// Create list.
			LinkedList<Shape> shapes = new LinkedList<Shape>();
	
			double dx = x2 - x1;
			double dy = y2 - y1;
			double d = Math.sqrt(dx * dx + dy * dy);
			double alpha = Math.asin(dx / d);
			
			if (dy < 0) {
				alpha = Math.PI - alpha;
			}
			
			int dxa = (int) (radius * Math.cos(alpha));
			int dya = (int) (radius * Math.sin(alpha));
			
			// Create polygon.
			Polygon polygon = new Polygon();
			// Add points.
			polygon.addPoint(x1 - dxa, y1 + dya);
			polygon.addPoint(x1 + dxa, y1 - dya);
			polygon.addPoint(x2 + dxa, y2 - dya);
			polygon.addPoint(x2 - dxa, y2 + dya);
			// Add polygon to the shapes list.
			shapes.add(polygon);
			
			double diameter = 2 * radius;
			
			// Create circle 1 and 2. Add them to the output list.
			Ellipse2D circle1 = new Ellipse2D.Double(x1 - radius, y1 - radius,
					diameter, diameter);
			Ellipse2D circle2 = new Ellipse2D.Double(x2 - radius, y2 - radius,
					diameter, diameter);
			shapes.add(circle1);
			shapes.add(circle2);
			
			return shapes;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Draw selection.
	 * @param g
	 * @param component
	 * @param isSelected
	 * @param hasFocus
	 */
	public static void drawDefaultValue(Graphics g, Component component) {
		
		try {
			Graphics2D g2 = (Graphics2D) g;
	
			g2.setPaint(defaultTexturePaint);
			
			Rectangle bounds = component.getBounds();
			int right = (int) bounds.getWidth();
			int bottom = (int) bounds.getHeight();
			
			g2.fillRect(0, 0, right, bottom);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Draw boolean value.
	 * @param g
	 * @param component
	 * @param booleanValue
	 */
	public static void drawBooleanValue(Graphics g,
			Component component, boolean booleanValue) {
		
		try {
			Graphics2D g2 = (Graphics2D) g;
			
			Rectangle bounds = component.getBounds();
			int height = (int) bounds.getHeight();
			
			final int imageSize = 13;
			final int leftPadding = 1;
			
			g2.drawImage(booleanValue ? trueImage : falseImage, leftPadding,
					(height - imageSize) / 2, null);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Draw selection.
	 * @param g
	 * @param component
	 * @param isSelected
	 * @param hasFocus
	 */
	public static void drawSelection(Graphics g, Component component,
			boolean isSelected, boolean hasFocus) {
		
		try {
			Graphics2D g2 = (Graphics2D) g;
			
			// If is selected.
			if (isSelected) {
	
				// Get properties.
				Composite oldComposite = g2.getComposite();
				Color oldColor = g2.getColor();
		
				// Set opacity.
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
				// Draw rectangle.
				g2.setColor(colorHighlight);
				
				// Draw rectangle.
				Dimension dimension = component.getSize();
				Rectangle rectangle = new Rectangle(dimension);
				g2.fill(rectangle);
				
				// If has focus.
				if (hasFocus) {
					
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1.0f));
					rectangle.setSize(dimension.width - 1, dimension.height - 1);
					g2.draw(rectangle);
				}
				
				// Set old properties.
				g2.setComposite(oldComposite);
				g2.setColor(oldColor);
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Fill rectangle.
	 * @param g2
	 * @param transformation
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public static void fillRectTransform(Graphics2D g2,
			AffineTransform transformation, double x, double y, double width,
			double height) {
		
		try {
			// Initialize.
			double xValue = x;
			double yValue = y;
			double widthValue = width;
			double heightValue = height;
			
			// Perform transformation.
			if (transformation != null) {
				Point2D leftTop = transformation.transform(
						new Point2D.Double(xValue, yValue), null);
				Point2D widthHeight = transformation.deltaTransform(
						new Point2D.Double(widthValue, heightValue), null);
				
				xValue = leftTop.getX();
				yValue = leftTop.getY();
				widthValue = widthHeight.getX();
				heightValue = widthHeight.getY();
			}
			
			int xInt = (int) xValue;
			int yInt = (int) yValue;
			int widthInt = (int) widthValue;
			int heightInt = (int) heightValue;
			
			g2.fillRect(xInt, yInt, widthInt, heightInt);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Fill label rectangle.
	 * @param g2
	 * @param transformation
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param arcWidth
	 * @param arcHeight
	 */
	public static void fillLabelRectTransform(Graphics2D g2,
			AffineTransform transformation, double x, double y, double width,
			double height, double arcWidth, double arcHeight) {
		
		try {
			// Initialize.
			double xValue = x;
            double yValue = y;
            double widthValue = width;
            double heightValue = height;
            double arcWidthValue = arcWidth;
            double arcHeightValue = arcHeight;
			
			// Perform transformation.
			if (transformation != null) {
				Point2D leftTop = transformation.transform(
						new Point2D.Double(xValue, yValue), null);
				Point2D widthHeight = transformation.deltaTransform(
						new Point2D.Double(widthValue, heightValue), null);
				Point2D arcWidthHeight = transformation.deltaTransform(
						new Point2D.Double(arcWidthValue, arcHeightValue), null);
				
				xValue = leftTop.getX();
				yValue = leftTop.getY();
				widthValue = widthHeight.getX();
				heightValue = widthHeight.getY();
				arcWidthValue = arcWidthHeight.getX();
				arcHeightValue = arcWidthHeight.getY();
			}
			
			Path2D path = new Path2D.Double();
			path.moveTo(xValue, yValue + heightValue);
			path.append(new Arc2D.Double(xValue, yValue, arcWidthValue, arcHeightValue, 180, -90, Arc2D.OPEN), true);
			path.lineTo(xValue + widthValue - arcWidthValue / 2, yValue);
			path.append(new Arc2D.Double(xValue + widthValue - arcWidthValue, yValue, arcWidthValue, arcHeightValue, 90, -90, Arc2D.OPEN), true);
			path.lineTo(xValue + widthValue, yValue + heightValue);
			
			g2.fill(path);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}	
	}

	/**
	 * Draw free rectangle.
	 * @param g2
	 * @param transformation
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param arcWidth
	 * @param arcHeight
	 */
	public static void fillFreeRectTransform(Graphics2D g2,
			AffineTransform transformation, double x, double y,
			double width, double height, double arcWidth,
			double arcHeight) {
		
		try {
			// Initialize.
			double xValue = x;
            double yValue = y;
            double widthValue = width;
            double heightValue = height;
            double arcWidthValue = arcWidth;
            double arcHeightValue = arcHeight;
			
			// Perform transformation.
			if (transformation != null) {
				Point2D leftTop = transformation.transform(
						new Point2D.Double(xValue, yValue), null);
				Point2D widthHeight = transformation.deltaTransform(
						new Point2D.Double(widthValue, heightValue), null);
				Point2D arcWidthHeight = transformation.deltaTransform(
						new Point2D.Double(arcWidthValue, arcHeightValue), null);
				
				xValue = leftTop.getX();
				yValue = leftTop.getY();
				widthValue = widthHeight.getX();
				heightValue = widthHeight.getY();
				arcWidthValue = arcWidthHeight.getX();
				arcHeightValue = arcWidthHeight.getY();
			}
			
			Path2D path = new Path2D.Double();
			path.append(new Arc2D.Double(xValue + widthValue - arcWidthValue, yValue + heightValue - arcHeightValue, arcWidthValue, arcHeightValue, 0, -90, Arc2D.OPEN), true);
			path.lineTo(xValue, yValue + heightValue);
			path.lineTo(xValue, yValue);
			path.lineTo(xValue + widthValue, yValue);
			
			g2.fill(path);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Fill root rectangle.
	 * @param g2
	 * @param transformation
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param arcWidth
	 * @param arcHeight
	 */
	public static void fillRootRectTransform(Graphics2D g2,
			AffineTransform transformation, double x, double y, double width,
			double height, double arcWidth, double arcHeight) {
		
		try {
			// Initialize.
			double xValue = x;
            double yValue = y;
            double widthValue = width;
            double heightValue = height;
            double arcWidthValue = arcWidth;
            double arcHeightValue = arcHeight;
			
			// Perform transformation.
			if (transformation != null) {
				Point2D leftTop = transformation.transform(
						new Point2D.Double(xValue, yValue), null);
				Point2D widthHeight = transformation.deltaTransform(
						new Point2D.Double(widthValue, heightValue), null);
				Point2D arcWidthHeight = transformation.deltaTransform(
						new Point2D.Double(arcWidthValue, arcHeightValue), null);
				
				xValue = leftTop.getX();
				yValue = leftTop.getY();
				widthValue = widthHeight.getX();
				heightValue = widthHeight.getY();
				arcWidthValue = arcWidthHeight.getX();
				arcHeightValue = arcWidthHeight.getY();
			}
			
			Path2D path = new Path2D.Double();
			path.append(new Arc2D.Double(xValue, yValue + heightValue - arcHeightValue, arcWidthValue, arcHeightValue, -90, -90, Arc2D.OPEN), true);
			path.lineTo(xValue, yValue);
			path.lineTo(xValue + widthValue, yValue);
			path.lineTo(xValue + widthValue, yValue + heightValue);
			
			g2.fill(path);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Draw rectangle.
	 * @param g2
	 * @param transformation
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public static void drawRectTransform(Graphics2D g2,
			AffineTransform transformation, double x, double y, double width,
			double height) {
		
		try {
			// Initialize.
			double xValue = x;
			double yValue = y;
			double widthValue = width;
			double heightValue = height;
			
			// Perform transformation.
			if (transformation != null) {
				Point2D leftTop = transformation.transform(
						new Point2D.Double(xValue, yValue), null);
				Point2D widthHeight = transformation.deltaTransform(
						new Point2D.Double(widthValue, heightValue), null);
				
				xValue = leftTop.getX();
				yValue = leftTop.getY();
				widthValue = widthHeight.getX();
				heightValue = widthHeight.getY();
			}
			
			int xInt = (int) xValue;
			int yInt = (int) yValue;
			int widthInt = (int) widthValue;
			int heightInt = (int) heightValue;
			
			g2.drawRect(xInt, yInt, widthInt, heightInt);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Draw round rectangle.
	 * @param g2
	 * @param transformation
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public static void drawRoundRectTransform(Graphics2D g2,
			AffineTransform transformation, double x, double y, double width,
			double height, double arcWidth, double arcHeight) {
		
		try {
			// Initialize.
			double xValue = x;
            double yValue = y;
            double widthValue = width;
            double heightValue = height;
            double arcWidthValue = arcWidth;
            double arcHeightValue = arcHeight;
			
			// Perform transformation.
			if (transformation != null) {
				Point2D leftTop = transformation.transform(
						new Point2D.Double(xValue, yValue), null);
				Point2D widthHeight = transformation.deltaTransform(
						new Point2D.Double(widthValue, heightValue), null);
				Point2D arcWidthHeight = transformation.deltaTransform(
						new Point2D.Double(arcWidthValue, arcHeightValue), null);
				
				xValue = leftTop.getX();
				yValue = leftTop.getY();
				widthValue = widthHeight.getX();
				heightValue = widthHeight.getY();
				arcWidthValue = arcWidthHeight.getX();
				arcHeightValue = arcWidthHeight.getY();
			}
			
			int xInt = (int) xValue;
			int yInt = (int) yValue;
			int widthInt = (int) widthValue;
			int heightInt = (int) heightValue;
			int arcWidthInt = (int) arcWidthValue;
			int arcHeightInt = (int) arcHeightValue;
			
			g2.drawRoundRect(xInt, yInt, widthInt, heightInt, arcWidthInt, arcHeightInt);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Draw recursion.
	 * @param g2
	 * @param transformation
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public static void drawRecursionTransform(Graphics2D g2,
			AffineTransform transformation, double x1, double y1, double x2,
			double y2) {

		try {
			// Initialization.
			double x1Value = x1;
            double y1Value = y1;
            double x2Value = x2;
            double y2Value = y2;
			
			// Perform transformation.
			if (transformation != null) {
				Point2D point1 = transformation.transform(
						new Point2D.Double(x1Value, y1Value), null);
				Point2D point2 = transformation.transform(
						new Point2D.Double(x2Value, y2Value), null);
				
				x1Value = point1.getX();
				y1Value = point1.getY();
				x2Value = point2.getX();
				y2Value = point2.getY();
			}
			
			int x1Int = (int) x1Value;
			int y1Int = (int) y1Value;
			int x2Int = (int) x2Value;
			int y2Int = (int) y2Value;
			int widthInt = x2Int - x1Int;
			
			float widthFloat = (float) widthInt;
			
			// Compute line strength.
			float lineStrength = widthFloat * 0.18f;
			int lineStrengthInt = (int) lineStrength;
			if (lineStrengthInt == 0) {
				lineStrength = 1;
			}
			
			// Compute corner size.
			float cornerSize = widthFloat * 0.4f;
			int cornerSizeInt = (int) cornerSize;
			
			// Set arrow size.
			float arrowWidth = widthFloat * 0.5f;
			int arrowWidthInt = (int) arrowWidth;
			float arrowHeight = arrowWidth * 1.25f;
			int arrowHeightInt = (int) arrowHeight;
			
			Stroke oldStroke = g2.getStroke();
			g2.setStroke(new BasicStroke(lineStrengthInt, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			
			// Draw symbol.
			g2.drawLine(x1Int, y1Int + arrowHeightInt / 2, x1Int, y2Int - cornerSizeInt);
			g2.drawArc(x1Int, y2Int - cornerSizeInt * 2, cornerSizeInt * 2, cornerSizeInt * 2, 180, 90);
			g2.drawLine(x1Int + cornerSizeInt, y2Int, x2Int - cornerSizeInt, y2Int);
			g2.drawArc(x2Int - cornerSizeInt * 2, y2Int - cornerSizeInt * 2, cornerSizeInt * 2, cornerSizeInt * 2, 270, 90);
			g2.drawLine(x2Int, y2Int - cornerSizeInt, x2Int, y1Int);
			g2.drawLine(x2Int, y1Int, x2Int - arrowWidthInt / 2, y1Int + arrowHeightInt / 2);
			g2.drawLine(x2Int, y1Int, x2Int + arrowWidthInt / 2, y1Int + arrowHeightInt / 2);
			
			g2.setStroke(oldStroke);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Draw line.
	 * @param g2
	 * @param transformation
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public static void drawLineTransform(Graphics2D g2,
			AffineTransform transformation, double x1, double y1,
			double x2, double y2) {
		
		try {
			// Initialization.
			double x1Value = x1;
			double y1Value = y1;
			double x2Value = x2;
			double y2Value = y2;
			
			// Perform transformation.
			if (transformation != null) {
				Point2D point1 = transformation.transform(
						new Point2D.Double(x1Value, y1Value), null);
				Point2D point2 = transformation.transform(
						new Point2D.Double(x2Value, y2Value), null);
				
				x1Value = point1.getX();
				y1Value = point1.getY();
				x2Value = point2.getX();
				y2Value = point2.getY();
			}
			
			int x1Int = (int) x1Value;
			int y1Int = (int) y1Value;
			int x2Int = (int) x2Value;
			int y2Int = (int) y2Value;
			
			g2.drawLine(x1Int, y1Int, x2Int, y2Int);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Clip rectangle.
	 * @param g2
	 * @param transformation
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public static void clipRectTransform(Graphics2D g2,
			AffineTransform transformation, double x, double y, double width,
			double height) {
		
		try {
			// Initialization.
			double xValue = x;
			double yValue = y;
			double widthValue = width;
			double heightValue = height;
			
			// Perform transformation.
			if (transformation != null) {
				Point2D leftTop = transformation.transform(
						new Point2D.Double(xValue, yValue), null);
				Point2D widthHeight = transformation.deltaTransform(
						new Point2D.Double(widthValue, heightValue), null);
				
				xValue = leftTop.getX();
				yValue = leftTop.getY();
				widthValue = widthHeight.getX();
				heightValue = widthHeight.getY();
			}
			
			int xInt = (int) xValue;
			int yInt = (int) yValue;
			int widthInt = (int) widthValue;
			int heightInt = (int) heightValue;
			
			g2.clipRect(xInt, yInt, widthInt, heightInt);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Draw image.
	 * @param g2
	 * @param transformation
	 * @param image
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public static void drawImageTransform(Graphics2D g2,
			AffineTransform transformation, BufferedImage image, double x,
			double y, double width, double height) {
		
		try {
			// Initialization.
			double xValue = x;
			double yValue = y;
			double widthValue = width;
			double heightValue = height;
			
			// Perform transformation.
			if (transformation != null) {
				Point2D leftTop = transformation.transform(
						new Point2D.Double(xValue, yValue), null);
				Point2D widthHeight = transformation.deltaTransform(
						new Point2D.Double(widthValue, heightValue), null);
				
				xValue = leftTop.getX();
				yValue = leftTop.getY();
				widthValue = widthHeight.getX();
				heightValue = widthHeight.getY();
			}
			
			int xInt = (int) xValue;
			int yInt = (int) yValue;
			int widthInt = (int) widthValue;
			int heightInt = (int) heightValue;
			
			g2.drawImage(image, xInt, yInt, widthInt, heightInt, null);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Draw house icon.
	 * @param g2
	 * @param transformation
	 * @param d
	 * @param e
	 * @param houseSize
	 * @param houseSize2
	 */
	public static void drawHouseTransform(Graphics2D g2, AffineTransform transformation,
			double x, double y, double width, double height, double strokePercentWidth) {
		
		try {
			// Initialization.
			double xValue = x;
			double yValue = y;
			double widthValue = width;
			double heightValue = height;
            
			// Perform transformation.
			if (transformation != null) {
				Point2D leftTop = transformation.transform(
						new Point2D.Double(xValue, yValue), null);
				Point2D widthHeight = transformation.deltaTransform(
						new Point2D.Double(widthValue, heightValue), null);
				
				xValue = leftTop.getX();
				yValue = leftTop.getY();
				widthValue = widthHeight.getX();
				heightValue = widthHeight.getY();
			}
			
			g2.setStroke(new BasicStroke((float) (widthValue * strokePercentWidth / 100.0), BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND));
			
			double roofHeight = heightValue * 0.45;
			double brickworkWidth = widthValue * 0.67;
			double brickworkHeight = heightValue - roofHeight;
			double brickworkLRSpace = (widthValue - brickworkWidth) / 2.0;
			double brickworkTop = heightValue - brickworkHeight;
			
			Path2D house = new Path2D.Double();
			house.moveTo(xValue + brickworkLRSpace, yValue + roofHeight);
			house.lineTo(xValue, yValue + roofHeight);
			house.lineTo(xValue + widthValue / 2.0, yValue);
			house.lineTo(xValue + widthValue, yValue + roofHeight);
			house.lineTo(xValue + widthValue - brickworkLRSpace, yValue + roofHeight);
			house.lineTo(xValue + widthValue - brickworkLRSpace, yValue + heightValue);
			house.lineTo(xValue + brickworkLRSpace, yValue + heightValue);
			house.lineTo(xValue + brickworkLRSpace, yValue + brickworkTop);
			
			g2.draw(house);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Draw arrow.
	 * @param g2
	 * @param transformation
	 * @param stroke
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param alpha
	 * @param length
	 */
	public static void drawArrowTransform(Graphics2D g2,
			AffineTransform transformation, double stroke, double x1, double y1,
			double x2, double y2, double alpha, double length) {
		
		try {
			// Initialization.
			double x1Value = x1;
			double y1Value = y1;
			double x2Value = x2;
			double y2Value = y2;
			double strokeValue = stroke;
			double lengthValue = length;
			
			// Perform transformation.
			if (transformation != null) {
				Point2D xY = transformation.transform(
						new Point2D.Double(x1Value, y1Value), null);
	
				x1Value = xY.getX();
				y1Value = xY.getY();
				
				xY =  transformation.transform(
						new Point2D.Double(x2Value, y2Value), null);
	
				x2Value = xY.getX();
				y2Value = xY.getY();
				
				Point2D sizeVector = transformation.deltaTransform(
						new Point2D.Double(strokeValue, 0), null);
	
				strokeValue = sizeVector.getX();
				
				sizeVector = transformation.deltaTransform(
						new Point2D.Double(lengthValue, 0), null);
	
				lengthValue = sizeVector.getX();
			}
			
			int strokeInt = (int) strokeValue;
			int x1Int = (int) x1Value;
			int y1Int = (int) y1Value;
			int x2Int = (int) x2Value;
			int y2Int = (int) y2Value;
			int lengthInt = (int) lengthValue;
			
			g2.setStroke(new BasicStroke(strokeInt, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			drawArrow(g2, x1Int, y1Int, x2Int, y2Int, alpha, lengthInt);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Draw string.
	 * @param g2
	 * @param transformation
	 * @param iterator
	 * @param x
	 * @param y
	 */
	public static void drawStringTransform(Graphics2D g2,
			AffineTransform transformation,
			AttributedCharacterIterator iterator, double x, double y) {
		
		try {
            // Initialization.
			double xValue = x;
			double yValue = y;
			
			// Perform transformation.
			if (transformation != null) {
				Point2D leftTop = transformation.transform(
						new Point2D.Double(xValue, yValue), null);
	
				xValue = leftTop.getX();
				yValue = leftTop.getY();
			}
			
			int xInt = (int) xValue;
			int yInt = (int) yValue;
			
			g2.drawString(iterator, xInt, yInt);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Draw string.
	 * @param g2
	 * @param transformation
	 * @param text
	 * @param x
	 * @param y
	 */
	public static void drawStringTransform(Graphics2D g2,
			AffineTransform transformation, String text, double x, double y) {
		
		try {
			// Initialization.
			double xValue = x;
			double yValue = y;
			
			// Perform transformation.
			if (transformation != null) {
				Point2D leftTop = transformation.transform(
						new Point2D.Double(xValue, yValue), null);
	
				xValue = leftTop.getX();
				yValue = leftTop.getY();
			}
			
			int xInt = (int) xValue;
			int yInt = (int) yValue;
			
			g2.drawString(text, xInt, yInt);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Get size.
	 * @param transformation
	 * @param size
	 * @return
	 */
	public static int getSizeTransform(AffineTransform transformation,
			double size) {
		
		try {
			// Perform transformation.
			if (transformation != null) {
				Point2D sizeVector = transformation.deltaTransform(
						new Point2D.Double(size, 0), null);
	
				size = sizeVector.getX();
			}
			
			int sizeInt = (int) size;
			return sizeInt;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 1;
	}
	
	/**
	 * Draw gradient rectangle.
	 * @param g2
	 * @param transformation
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param lineSize
	 * @param color
	 * @param intensity
	 */
	public static void drawGradientRectangleTransf(Graphics2D g2,
			AffineTransform transformation, double x, double y,
			double width, double height, double lineSize, Color color,
			float intensity) {
		
		try {
			// Initialization.
			double xValue = x;
			double yValue = y;
			double widthValue = width;
			double heightValue = height;
			double lineSizeValue = lineSize;
			
			// Perform transformation.
			if (transformation != null) {
				
				Point2D leftTop = transformation.transform(
						new Point2D.Double(xValue, yValue), null);
				Point2D widthHeight = transformation.deltaTransform(
						new Point2D.Double(widthValue, heightValue), null);
				
				xValue = leftTop.getX();
				yValue = leftTop.getY();
				widthValue = widthHeight.getX();
				heightValue = widthHeight.getY();
	
				Point2D sizeVector = transformation.deltaTransform(
						new Point2D.Double(lineSizeValue, 0), null);
	
				lineSizeValue = sizeVector.getX();
			}
			
			int xInt = (int) xValue;
			int yInt = (int) yValue;
			int widthInt = (int) widthValue;
			int heightInt = (int) heightValue;
			int lineSizeInt = (int) lineSizeValue;
			
			drawGradientRectangle(g2, xInt, yInt,
				widthInt, heightInt, lineSizeInt, color, intensity);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
}
