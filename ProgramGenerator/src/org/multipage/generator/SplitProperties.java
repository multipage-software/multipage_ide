/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.multipage.gui.Images;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.Utility;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Splitter panel for area properties.
 * @author vakol
 *
 */
public class SplitProperties extends JPanel {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Splitter size.
	 */
	private static final int splitterWidth = 6;
	
	/**
	 * Control area height.
	 */
	private static final int controlHeight = 26;

	/**
	 * Minimized width.
	 */
	private static final int minimizedWidth = 24;

	/**
	 * Dialog states.
	 */
	private static int splitterState;
	
	/**
	 * Serialize module data.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void seriliazeData(StateInputStream inputStream)
		throws IOException, ClassNotFoundException {

		// Read splitter position.
		splitterState = inputStream.readInt();
	}
	
	/**
	 * Serialize module data.
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream)
		throws IOException {
		
		// Write down splitter position.
		outputStream.writeInt(splitterState);
	}

	/**
	 * Set default state.
	 */
	public static void setDefaultData() {

		splitterState = 300;
	}

	/**
	 * Load dialog.
	 */
	private void loadDialog() {
		
		splitter = splitterState;
	}
	
	/**
	 * Save dialog.
	 */
	private void saveDialog() {
		
		splitterState = splitter;
	}

	/**
	 * Main panel.
	 */
	private JComponent main;
	
	/**
	 * Properties panel.
	 */
	private JComponent properties;
	
	/**
	 * Splitter.
	 */
	private int splitter = 225;

	/**
	 * Splitter drag flag.
	 */
	private boolean splitterDrag = false;
	
	/**
	 * Minimize button.
	 */
	private JButton minimizeButton = new JButton();

	/**
	 * Maximize button.
	 */
	private JButton maximizeButton = new JButton();

	/**
	 * Minimized flag.
	 */
	private boolean minimized = true;
	
	/**
	 * Top padding.
	 */
	private JPanel topPadding = new JPanel();

	/**
	 * Constructor.
	 * @param tabPanel
	 * @param properties
	 */
	public SplitProperties(JComponent tabPanel, JComponent properties) {
		try {
			
			this.main = tabPanel;
			this.properties = properties;
			
			// Load images.
			minimizeButton.setIcon(Images.getIcon("org/multipage/generator/images/minimize.png"));
			maximizeButton.setIcon(Images.getIcon("org/multipage/generator/images/maximize.png"));
	
			// Set tool tips.
			minimizeButton.setToolTipText(Resources.getString("org.multipage.generator.tooltipMinimize"));
			maximizeButton.setToolTipText(Resources.getString("org.multipage.generator.tooltipMaximize"));
			
			setLayout(null);
			
			add(tabPanel);
			add(properties);
			add(topPadding);
			minimizeButton.setSize(22, 22);
			add(minimizeButton);
			maximizeButton.setSize(22, 22);
			add(maximizeButton);
			
			// Set listeners.
			addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					try {
						
						onResized();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseDragged(MouseEvent e) {
					try {
						
						onMouseDragged(e);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					try {
						
						onMousePressed(e);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				@Override
				public void mouseReleased(MouseEvent e) {
					try {
						
						onMouseReleased(e);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				@Override
				public void mouseEntered(MouseEvent e) {
					try {
						
						onMouseEntered(e);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				@Override
				public void mouseExited(MouseEvent e) {
					try {
						
						onMouseExited(e);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			minimizeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						
						minimize();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			maximizeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						
						maximize();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			topPadding.addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseMoved(MouseEvent e) {
					try {
						
						setCursor(Cursor.getDefaultCursor());
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			
			// Load dialog.
			loadDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Initialize.
	 */
	public void init() {
		try {
			
			if (minimized) {
				minimize();
			}
			else {
				maximize();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Maximize.
	 */
	public void maximize() {
		try {
			
			// Set flag.
			minimized = false;
			// Show properties.
			properties.setVisible(true);
			// Split panels.
			setSplitter(splitter);
			// Hide maximize button.
			maximizeButton.setVisible(false);
			// Show minimize button.
			minimizeButton.setVisible(true);
			// Repaint the panel.
			Safe.invokeLater(() -> {
				repaint();
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On minimize.
	 */
	public void minimize() {
		try {
			
			// Set flag.
			minimized = true;
			// Hide minimize button.
			minimizeButton.setVisible(false);
			// Hide properties.
			properties.setVisible(false);
			// Show maximize button.
			maximizeButton.setVisible(true);
			// Set main panel.
			main.setBounds(0, 0, getWidth() - minimizedWidth, getHeight());
			// Repaint the panel.
			Safe.invokeLater(() -> {
				repaint();
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On mouse signalReleased.
	 * @param e
	 */
	protected void onMouseReleased(MouseEvent e) {

		splitterDrag = false;
	}

	/**
	 * On mouse pressed.
	 * @param e
	 */
	protected void onMousePressed(MouseEvent e) {
		try {
			
			splitterDrag  = isOnSplitter(e.getPoint());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On mouse dragged.
	 * @param e
	 */
	protected void onMouseDragged(MouseEvent e) {
		try {
			
			// If splitter dragged.
			if (splitterDrag) {
				setSplitter((int) (getWidth() - e.getPoint().getX()));
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set splitter.
	 * @param newsplitter
	 */
	private void setSplitter(int newsplitter) {
		
		try {
			
			if (newsplitter < splitterWidth / 2) {
				newsplitter = splitterWidth / 2;
			}
			else if (newsplitter > getWidth() - splitterWidth / 2) {
				newsplitter = getWidth() - splitterWidth / 2;
			}
			
			splitter = newsplitter;
			
			int splitterStart = getWidth() - newsplitter - splitterWidth / 2,
		    splitterEnd = splitterStart + splitterWidth + 1;
		
			main.setSize(splitterStart, getHeight());
			properties.setBounds(splitterEnd, controlHeight, getWidth() - splitterEnd, getHeight() - controlHeight);
			topPadding.setBounds(splitterEnd, 0, getWidth() - splitterEnd - 22, controlHeight);
			
			revalidate();
			Utility.repaintLater(this);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * On mouse entered.
	 * @param e
	 */
	protected void onMouseEntered(MouseEvent e) {
		try {
			
			if (!minimized) {
				setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On mouse exited.
	 * @param e
	 */
	protected void onMouseExited(MouseEvent e) {
		try {
			
			setCursor(Cursor.getDefaultCursor());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Returns true if point on splitter.
	 * @param point
	 * @return
	 */
	private boolean isOnSplitter(Point point) {
		
		try {
			int splitterStart = getWidth() - splitter - splitterWidth / 2;
			Rectangle rect = new Rectangle(splitterStart, 0, splitterWidth, getHeight());
			
			return rect.contains(point);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * On resized.
	 */
	protected void onResized() {
		try {
			
			if (minimized) {
				main.setBounds(0, 0, getWidth() - minimizedWidth, getHeight());
			}
			else {
				int splitterStart = getWidth() - splitter - splitterWidth / 2,
				    splitterEnd = splitterStart + splitterWidth + 1;
				
				main.setBounds(0, 0, splitterStart, getHeight());
				properties.setBounds(splitterEnd, controlHeight, getWidth() - splitterEnd, getHeight() - controlHeight);
				topPadding.setBounds(splitterEnd, 0, getWidth() - splitterEnd - 22, controlHeight);
			}
			minimizeButton.setLocation(getWidth() - 22, 0);
			maximizeButton.setLocation(getWidth() - 22, 0);
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
			
			Graphics2D g2 = (Graphics2D) g;
			if (!minimized) {
				int splitterStart = getWidth() - splitter - splitterWidth / 2,
				    splitterEnd = splitterStart + splitterWidth;
				
				// Draw splitter.
				g2.drawLine(splitterStart, 0, splitterStart, getHeight());
				g2.drawLine(splitterEnd, 0, splitterEnd, getHeight());
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Redraw windows.
	 */
	public void redraw() {

		Safe.invokeLater(() -> {

			revalidate();
			repaint();
		});
	}

	/**
	 * Dispose splitter.
	 */
	public void dispose() {
		try {
			
			// Save dialog.
			saveDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
