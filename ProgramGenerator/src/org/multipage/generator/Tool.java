/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.imaging.Imaging;
import org.multipage.util.Safe;


/**
 * Class for tool
 * @author vakol
 */
public class Tool {
	
	/**
	 * List element width and height.
	 */
	private static int width = 48;
	private static int height = 48;
	
	/**
	 * @return the width
	 */
	public static int getWidth() {
		return width;
	}

	/**
	 * @return the height
	 */
	public static int getHeight() {
		return height;
	}

	/**
	 * Tool list reference.
	 */
	protected ToolList tools;
	
	/**
	 * Position.
	 */
	protected int position = -1;
	
	/**
	 * Selected flag.
	 */
	private boolean selected = false;
	
	/**
	 * Image.
	 */
	private BufferedImage image;
	
	/**
	 * Tool id.
	 */
	private ToolId toolId;
	
	/**
	 * Tool tip text.
	 */
	private String tooltip;
	
	/**
	 * Tool description.
	 */
	private String description;
	
	/**
	 * Constructor.
	 */
	public Tool(ToolList tools, int position, ToolId toolId, String tooltip, String imageFile, String description) {
		try {
			
			this.tools = tools;
			this.position = position;
			this.toolId = toolId;
			this.tooltip = tooltip;
			this.description = description;
			
			// Try to load image.
			URL url = ClassLoader.getSystemResource(imageFile);
			if (url != null) {
				
				InputStream inputStream = null;
				try {
					inputStream = url.openStream();
					this.image = Imaging.getBufferedImage(inputStream);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					if (inputStream != null	) {
						try {
							inputStream.close();
						}
						catch (Exception e) {
						}
					}
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Draw element.
	 */
	public void draw(Graphics2D g2) {
		
		try {
			// Fill rectangle.
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1.0f));
			g2.setColor(CustomizedColors.get(ColorId.TOOLBACKGROUND));
			int y = tools.getYPos(position);
			g2.fillRect(1, y, width, height - 1);
			
			// Draw selected.
			if (selected) {
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
				g2.setStroke(new BasicStroke(1));
				g2.setColor(CustomizedColors.get(ColorId.SELECTION));
				g2.fillRect(1, y , width - 1, height - 2);
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1.0f));
				g2.drawRect(1, y , width - 1, height - 2);
			}
			
			// Draw icon.
			drawIcon(g2);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Draw icon.
	 */
	private void drawIcon(Graphics2D g2) {
		
		try {
			// Draw cursor image.
			if (image != null) {
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
				g2.drawImage(image, 1,
						tools.getYPos(position),
						Tool.width,
						Tool.height,
						null);
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Set selected flag.
	 */
	public void setSelected(boolean isSelected) {
		try {
			
			selected = isSelected;
			if (selected == true) {
				// Set main tool bar text.
				GeneratorMainFrame.getFrame().setMainToolBarText(description);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(int position) {
		
		this.position = position;
	}

	/**
	 * @return the position
	 */
	public int getPosition() {
		
		return position;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		
		return selected;
	}

	/**
	 * @return the toolId
	 */
	public ToolId getToolId() {
		
		return toolId;
	}

	/**
	 * @return the tooltip
	 */
	public String getTooltip() {
		
		return tooltip;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		
		return description;
	}

	/**
	 * @param width the width to set
	 */
	public static void setWidth(int width) {
		
		Tool.width = width;
	}

	/**
	 * @param height the height to set
	 */
	public static void setHeight(int height) {
		
		Tool.height = height;
	}
}