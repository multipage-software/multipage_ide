/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.multipage.util.Safe;

/**
 * Tabbed panel with Drag and Drop capability.
 * 
 * @author vakol
 *
 */
public class DnDTabbedPane extends JTabbedPane {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * First tab index. Previous indices are not dragged.
	 */
	public int firstDraggedIndex = 0;

	/**
	 * Set first dragged index.
	 * 
	 * @param index
	 */
	public void setFirstDraggedIndex(int index) {

		firstDraggedIndex = index;
	}

	private static final int LINEWIDTH = 3;
	private static final String NAME = "test";
	private final GhostGlassPane glassPane = new GhostGlassPane();
	private final Rectangle lineRect = new Rectangle();
	private final Color lineColor = new Color(0, 100, 255);
	private int dragTabIndex = -1;

	private void clickArrowButton(String actionKey) {
		try {
			
			ActionMap map = getActionMap();
			if (map != null) {
				Action action = map.get(actionKey);
				if (action != null && action.isEnabled()) {
					action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null, 0, 0));
				}
			}
		}
		catch (Throwable expt) {
			Safe.exception(expt);
		}
	}

	private static Rectangle rBackward = new Rectangle();
	private static Rectangle rForward = new Rectangle();
	private static int rwh = 20;
	private static int buttonsize = 30;// XXX: magic number of scroll button size

	private void autoScrollTest(Point glassPt) {
		try {
			
			Rectangle r = getTabAreaBounds();
			int tabPlacement = getTabPlacement();
			if (tabPlacement == TOP || tabPlacement == BOTTOM) {
				rBackward.setBounds(r.x, r.y, rwh, r.height);
				rForward.setBounds(r.x + r.width - rwh - buttonsize, r.y, rwh + buttonsize, r.height);
			} else if (tabPlacement == LEFT || tabPlacement == RIGHT) {
				rBackward.setBounds(r.x, r.y, r.width, rwh);
				rForward.setBounds(r.x, r.y + r.height - rwh - buttonsize, r.width, rwh + buttonsize);
			}
			rBackward = SwingUtilities.convertRectangle(getParent(), rBackward, glassPane);
			rForward = SwingUtilities.convertRectangle(getParent(), rForward, glassPane);
			if (rBackward.contains(glassPt)) {
				clickArrowButton("scrollTabsBackwardAction");
			} else if (rForward.contains(glassPt)) {
				clickArrowButton("scrollTabsForwardAction");
			}
		}
		catch (Throwable expt) {
			Safe.exception(expt);
		}
	}

	public DnDTabbedPane() {
		super();
		try {
			
			final DragSourceListener dsl = new DragSourceListener() {
				@Override
				public void dragEnter(DragSourceDragEvent e) {
					try {
						
						e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
					}
					catch (Throwable expt) {
						Safe.exception(expt);
					}
				}
	
				@Override
				public void dragExit(DragSourceEvent e) {
					try {
						
						e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
						lineRect.setRect(0, 0, 0, 0);
						glassPane.setPoint(new Point(-1000, -1000));
						glassPane.repaint();
					}
					catch (Throwable expt) {
						Safe.exception(expt);
					}
				}
	
				@Override
				public void dragOver(DragSourceDragEvent e) {
					try {
						
						Point glassPt = e.getLocation();
						SwingUtilities.convertPointFromScreen(glassPt, glassPane);
						int targetIdx = getTargetTabIndex(glassPt);
						// if(getTabAreaBounds().contains(tabPt) && targetIdx>=0 &&
						if (getTabAreaBounds().contains(glassPt) && targetIdx >= 0 && targetIdx != dragTabIndex
								&& targetIdx != dragTabIndex + 1) {
							e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
							glassPane.setCursor(DragSource.DefaultMoveDrop);
						} else {
							e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
							glassPane.setCursor(DragSource.DefaultMoveNoDrop);
						}
					}
					catch (Throwable expt) {
						Safe.exception(expt);
					}
				}
	
				@Override
				public void dragDropEnd(DragSourceDropEvent e) {
					try {
						
						lineRect.setRect(0, 0, 0, 0);
						dragTabIndex = -1;
						glassPane.setVisible(false);
						if (hasGhost()) {
							glassPane.setVisible(false);
							glassPane.setImage(null);
						}
					}
					catch (Throwable expt) {
						Safe.exception(expt);
					}
				}
	
				@Override
				public void dropActionChanged(DragSourceDragEvent e) {
				}
			};
			final Transferable t = new Transferable() {
				private final DataFlavor FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, NAME);
	
				@Override
				public Object getTransferData(DataFlavor flavor) {
					return DnDTabbedPane.this;
				}
	
				@Override
				public DataFlavor[] getTransferDataFlavors() {
					
					try {
						DataFlavor[] f = new DataFlavor[1];
						f[0] = this.FLAVOR;
						return f;
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return null;
				}
	
				@Override
				public boolean isDataFlavorSupported(DataFlavor flavor) {
					
					try {
						return flavor.getHumanPresentableName().equals(NAME);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return false;
				}
			};
			final DragGestureListener dgl = new DragGestureListener() {
				@Override
				public void dragGestureRecognized(DragGestureEvent e) {
					try {
						
						if (getTabCount() <= 1)
							return;
						Point tabPt = e.getDragOrigin();
						dragTabIndex = indexAtLocation(tabPt.x, tabPt.y);
						// "disabled tab problem".
						if (dragTabIndex < 1 || !isEnabledAt(dragTabIndex))
							return;
						initGlassPane(e.getComponent(), e.getDragOrigin());
						try {
							e.startDrag(DragSource.DefaultMoveDrop, t, dsl);
						} catch (InvalidDnDOperationException idoe) {
							idoe.printStackTrace();
						}
					}
					catch (Throwable expt) {
						Safe.exception(expt);
					}
				}
			};
			new DropTarget(glassPane, DnDConstants.ACTION_COPY_OR_MOVE, new CDropTargetListener(), true);
			new DragSource().createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, dgl);
		}
		catch (Throwable expt) {
			Safe.exception(expt);
		}
	}

	class CDropTargetListener implements DropTargetListener {
		@Override
		public void dragEnter(DropTargetDragEvent e) {
			try {
				
				if (isDragAcceptable(e))
					e.acceptDrag(e.getDropAction());
				else
					e.rejectDrag();
			}
			catch (Throwable expt) {
				Safe.exception(expt);
			}
		}

		@Override
		public void dragExit(DropTargetEvent e) {
		}

		@Override
		public void dropActionChanged(DropTargetDragEvent e) {
		}

		private Point _glassPt = new Point();

		@Override
		public void dragOver(final DropTargetDragEvent e) {
			try {
				
				Point glassPt = e.getLocation();
				if (getTabPlacement() == JTabbedPane.TOP || getTabPlacement() == JTabbedPane.BOTTOM) {
					initTargetLeftRightLine(getTargetTabIndex(glassPt));
				} else {
					initTargetTopBottomLine(getTargetTabIndex(glassPt));
				}
				if (hasGhost()) {
					glassPane.setPoint(glassPt);
				}
				if (!_glassPt.equals(glassPt))
					glassPane.repaint();
				_glassPt = glassPt;
				autoScrollTest(glassPt);
			}
			catch (Throwable expt) {
				Safe.exception(expt);
			}
		}

		@Override
		public void drop(DropTargetDropEvent e) {
			try {
				
				if (isDropAcceptable(e)) {
					convertTab(dragTabIndex, getTargetTabIndex(e.getLocation()));
					e.dropComplete(true);
				} else {
					e.dropComplete(false);
				}
				repaint();
			}
			catch (Throwable expt) {
				Safe.exception(expt);
			}
		}

		private boolean isDragAcceptable(DropTargetDragEvent e) {
			
			try {
				Transferable t = e.getTransferable();
				if (t == null)
					return false;
				DataFlavor[] f = e.getCurrentDataFlavors();
				if (t.isDataFlavorSupported(f[0]) && dragTabIndex >= 0) {
					return true;
				}
			}
			catch (Throwable exception) {
				Safe.exception(exception);
			}
			return false;
		}

		private boolean isDropAcceptable(DropTargetDropEvent e) {
			
			try {
				Transferable t = e.getTransferable();
				if (t == null)
					return false;
				DataFlavor[] f = t.getTransferDataFlavors();
				if (t.isDataFlavorSupported(f[0]) && dragTabIndex >= 0) {
					return true;
				}
			}
			catch (Throwable exception) {
				Safe.exception(exception);
			}
			return false;
		}
	}

	private boolean hasGhost = true;

	public void setPaintGhost(boolean flag) {
		hasGhost = flag;
	}

	public boolean hasGhost() {
		return hasGhost;
	}

	private boolean isPaintScrollArea = true;

	public void setPaintScrollArea(boolean flag) {
		isPaintScrollArea = flag;
	}

	public boolean isPaintScrollArea() {
		return isPaintScrollArea;
	}

	private int getTargetTabIndex(Point glassPt) {
		
		try {
			Point tabPt = SwingUtilities.convertPoint(glassPane, glassPt, DnDTabbedPane.this);
			boolean isTB = getTabPlacement() == JTabbedPane.TOP || getTabPlacement() == JTabbedPane.BOTTOM;
			for (int i = 0; i < getTabCount(); i++) {
				Rectangle r = getBoundsAt(i);
				if (isTB)
					r.setRect(r.x - r.width / 2, r.y, r.width, r.height);
				else
					r.setRect(r.x, r.y - r.height / 2, r.width, r.height);
				if (r.contains(tabPt))
					return i;
			}
			Rectangle r = getBoundsAt(getTabCount() - 1);
			if (isTB)
				r.setRect(r.x + r.width / 2, r.y, r.width, r.height);
			else
				r.setRect(r.x, r.y + r.height / 2, r.width, r.height);
			return r.contains(tabPt) ? getTabCount() : -1;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return -1;
	}

	private void convertTab(int prev, int next) {
		try {
			
			if (next < firstDraggedIndex || prev == next) {
				return;
			}
			Component cmp = getComponentAt(prev);
			Component tab = getTabComponentAt(prev);
			String str = getTitleAt(prev);
			Icon icon = getIconAt(prev);
			String tip = getToolTipTextAt(prev);
			boolean flg = isEnabledAt(prev);
			int tgtindex = prev > next ? next : next - 1;
			remove(prev);
			insertTab(str, icon, cmp, tip, tgtindex);
			setEnabledAt(tgtindex, flg);
			// When you drag'n'drop a disabled tab, it finishes enabled and selected.
			// pointed out by dlorde
			if (flg)
				setSelectedIndex(tgtindex);
	
			// I have a component in all tabs (jlabel with an X to close the tab)
			// and when i move a tab the component disappear.
			// pointed out by Daniel Dario Morales Salas
			setTabComponentAt(tgtindex, tab);
		}
		catch (Throwable expt) {
			Safe.exception(expt);
		}
	}

	private void initTargetLeftRightLine(int next) {
		try {
			
			if (next < firstDraggedIndex || dragTabIndex == next || next - dragTabIndex == 1) {
				lineRect.setRect(0, 0, 0, 0);
			} else if (next == 0) {
				Rectangle r = SwingUtilities.convertRectangle(this, getBoundsAt(0), glassPane);
				lineRect.setRect(r.x - LINEWIDTH / 2, r.y, LINEWIDTH, r.height);
			} else {
				Rectangle r = SwingUtilities.convertRectangle(this, getBoundsAt(next - 1), glassPane);
				lineRect.setRect(r.x + r.width - LINEWIDTH / 2, r.y, LINEWIDTH, r.height);
			}
		}
		catch (Throwable expt) {
			Safe.exception(expt);
		}
	}

	private void initTargetTopBottomLine(int next) {
		try {
			
			if (next < 0 || dragTabIndex == next || next - dragTabIndex == 1) {
				lineRect.setRect(0, 0, 0, 0);
			} else if (next == 0) {
				Rectangle r = SwingUtilities.convertRectangle(this, getBoundsAt(0), glassPane);
				lineRect.setRect(r.x, r.y - LINEWIDTH / 2, r.width, LINEWIDTH);
			} else {
				Rectangle r = SwingUtilities.convertRectangle(this, getBoundsAt(next - 1), glassPane);
				lineRect.setRect(r.x, r.y + r.height - LINEWIDTH / 2, r.width, LINEWIDTH);
			}
		}
		catch (Throwable expt) {
			Safe.exception(expt);
		}
	}

	private void initGlassPane(Component c, Point tabPt) {
		try {
			
			getRootPane().setGlassPane(glassPane);
			if (hasGhost()) {
				Rectangle rect = getBoundsAt(dragTabIndex);
				BufferedImage image = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics g = image.getGraphics();
				c.paint(g);
				rect.x = rect.x < 0 ? 0 : rect.x;
				rect.y = rect.y < 0 ? 0 : rect.y;
				image = image.getSubimage(rect.x, rect.y, rect.width, rect.height);
				glassPane.setImage(image);
			}
			Point glassPt = SwingUtilities.convertPoint(c, tabPt, glassPane);
			glassPane.setPoint(glassPt);
			glassPane.setVisible(true);
		}
		catch (Throwable expt) {
			Safe.exception(expt);
		}
	}

	private Rectangle getTabAreaBounds() {
		
		try {
			Rectangle tabbedRect = getBounds();
			// pointed out by daryl. NullPointerException: i.e. addTab("Tab",null)
			// Rectangle compRect = getSelectedComponent().getBounds();
			Component comp = getSelectedComponent();
			int idx = 0;
			while (comp == null && idx < getTabCount())
				comp = getComponentAt(idx++);
			Rectangle compRect = (comp == null) ? new Rectangle() : comp.getBounds();
			int tabPlacement = getTabPlacement();
			if (tabPlacement == TOP) {
				tabbedRect.height = tabbedRect.height - compRect.height;
			} else if (tabPlacement == BOTTOM) {
				tabbedRect.y = tabbedRect.y + compRect.y + compRect.height;
				tabbedRect.height = tabbedRect.height - compRect.height;
			} else if (tabPlacement == LEFT) {
				tabbedRect.width = tabbedRect.width - compRect.width;
			} else if (tabPlacement == RIGHT) {
				tabbedRect.x = tabbedRect.x + compRect.x + compRect.width;
				tabbedRect.width = tabbedRect.width - compRect.width;
			}
			tabbedRect.grow(2, 2);
			return tabbedRect;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return getBounds();
	}

	class GhostGlassPane extends JPanel {

		/**
		 * Version.
		 */
		private static final long serialVersionUID = 1L;

		private AlphaComposite composite;
		private Point location = new Point(0, 0);
		private BufferedImage draggingGhost = null;

		public GhostGlassPane() {
			try {
				
				setOpaque(false);
				composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
				// http://bugs.sun.com/view_bug.do?bug_id=6700748
				// setCursor(null);
			}
			catch (Throwable expt) {
				Safe.exception(expt);
			}
		}

		public void setImage(BufferedImage draggingGhost) {
			this.draggingGhost = draggingGhost;
		}

		public void setPoint(Point location) {
			this.location = location;
		}

		@Override
		public void paintComponent(Graphics g) {
			try {
				
				Graphics2D g2 = (Graphics2D) g;
				g2.setComposite(composite);
				if (isPaintScrollArea() && getTabLayoutPolicy() == SCROLL_TAB_LAYOUT) {
					g2.setPaint(Color.RED);
					g2.fill(rBackward);
					g2.fill(rForward);
				}
				if (draggingGhost != null) {
					double xx = location.getX() - (draggingGhost.getWidth(this) / 2d);
					double yy = location.getY() - (draggingGhost.getHeight(this) / 2d);
					g2.drawImage(draggingGhost, (int) xx, (int) yy, null);
				}
				if (dragTabIndex >= 0) {
					g2.setPaint(lineColor);
					g2.fill(lineRect);
				}
			}
			catch (Throwable expt) {
				Safe.exception(expt);
			}
		}
	}
}