/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

import javax.swing.JPanel;

import org.multipage.gui.ApplicationEvents;
import org.multipage.gui.CursorArea;
import org.multipage.gui.CursorAreaImpl;
import org.multipage.gui.HorizontalScroll;
import org.multipage.gui.Images;
import org.multipage.gui.ScrollListener;
import org.multipage.gui.ToolTipWindow;
import org.multipage.gui.Utility;
import org.multipage.gui.VerticalScroll;
import org.multipage.gui.ZoomShape;
import org.multipage.util.Safe;

/**
 * Base class for all diagrams.
 * @author vakol
 *
 */
public abstract class GeneralDiagramPanel extends JPanel implements CursorArea {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Tool tip window.
	 */
	protected static ToolTipWindow tooltipWindow;

	/**
	 * Zoom step.
	 */
	protected static final double zoomStep = 2.0;

	/**
	 * Space between zoom shape and vertical scroll.
	 */
	protected static final int zoomScrollSpace = 10;
	
	/**
	 * Tool list.
	 */
	protected ToolList toolList;
	
	/**
	 * Horizontal scroll.
	 */
	protected HorizontalScroll horizontalScroll;
	
	/**
	 * Vertical scroll.
	 */
	protected VerticalScroll verticalScroll;
	
	/**
	 * Zoom shape.
	 */
	protected ZoomShape zoomShape;

	/**
	 * Overview.
	 */
	protected OverviewControl overview = new OverviewControl(this);
	
	/**
	 * Remove element shape object.
	 */
	protected RemoveElementShape removeElementShape;

	/**
	 * Diagram loaded flag.
	 */
	private boolean isLoaded;
	
	/**
	 * Static constructor.
	 */
	static {
		try {
			
			// Create tool tip window.
			tooltipWindow = new ToolTipWindow(GeneratorMainFrame.getFrame());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Update diagrams controls.
	 */
	public static void updateDiagramsControls() {
		
	}

	/**
	 * Translation.
	 */
	protected double translationx = 0;
	protected double translationy = 0;
	protected double deltax = 0;
	protected double deltay = 0;
	
	/**
	 * Zoom.
	 */
	protected double zoom = 1.0;
	
	/**
	 * Current zoom boundaries.
	 */
	protected double minimumZoom = 1.0;
	protected double maximumZoom = 1.0;
	
	/**
	 * Old zoom boundaries.
	 */
	protected double oldMinimumZoom = 1.0;
	protected double oldMaximumZoom = 1.0;
	
	/**
	 * Animation timer.
	 */
	protected javax.swing.Timer animationTimer;
	
	/**
	 * Position saving delay in milliseconds.
	 */
	private static final int positionSavingDelay = 3000;
	
	/**
	 * Maximum number of redos.
	 */
	private static final int maximumNumberOfRedos = 100;

	/**
	 * Actual animation animationTime in seconds.
	 */
	protected double animationTime;

	/**
	 * Animation vector.
	 */
	protected double animateVectorX;
	protected double animateVectorY;

	/**
	 * Animation zoom.
	 */
	private double animateRelativeZoom = 1.0;

	/**
	 * Zoom multiplier;
	 */
	protected double zoomMultiplier = 1.0;
	
	/**
	 * Cursor area.
	 */
	private CursorAreaImpl cursorArea;
	
	/**
	 * Mouse position when pressed.
	 */
	protected Point mousePosPressed;
	
	/**
	 * Popup trayMenu activation flag.
	 */
    private boolean popupMenuActivation = false;

	/**
	 * Focus undo list.
	 */
	private LinkedList<FocusUndo> focusUndoList =
		new LinkedList<FocusUndo>();
	
	/**
	 * Current focus.
	 */
	private FocusUndo currentFocus;
	
	/**
	 * Undo and redo components.
	 */
	private Component undoComponent;
	private Component redoComponent;

	/**
	 * Is diagram position changed flag.
	 */
	protected boolean isDiagramPositionChanged = false;

	/**
	 * Position save timer.
	 */
	private javax.swing.Timer positionSaveTimer;

	/**
	 * Get cursor area.
	 */
	@Override
	public CursorAreaImpl getCursorArea() {

		return cursorArea;
	}

	/**
	 * Constructor.
	 */
	public GeneralDiagramPanel() {
		try {
			
			cursorArea = new CursorAreaImpl(Cursor.getDefaultCursor(),
					this, null);
			
			// Create tool list.
			toolList = new ToolList(this, Cursor.getDefaultCursor());
			
			horizontalScroll = new HorizontalScroll(Cursor.getDefaultCursor(), this);
			verticalScroll = new VerticalScroll(Cursor.getDefaultCursor(), this);
			
			// Create and set zoom shape.
			zoomShape = new ZoomShape(minimumZoom, maximumZoom, Cursor.getPredefinedCursor(Cursor.HAND_CURSOR), this);
			
			// Set overview button size.
			overview.setSize(VerticalScroll.getWidth(), HorizontalScroll.getHeight());
			
			// Attach diagram listeners.
			setListeners();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set listeners.
	 */
	private void setListeners() {
		try {
			
			// Set tool list listener.
			toolList.setListener(new ToolListListener() {
				@Override
				public void onSelectToolEvent(ToolId toolId, ToolId oldSelection) {
					try {
			
						// Set diagram cursor.
						setDiagramCursor();
						// Call abstract method.
						onSelectTool(toolId, oldSelection);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			// Add listener.
			addMouseListener(new MouseAdapter() {
				
				// On mouse pressed.
				@Override
				public void mousePressed(MouseEvent e) {
					try {
			
						if ((e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3)
								&& toolList.getSelected() != ToolId.ZOOM) {
							popupMenuActivation = true;
							onPopupMenu(e);
							return;
						}
						popupMenuActivation = false;
						
						if (animationTimer.isRunning()) {
							return;
						}
						
						onMousePressedEvent(e);
						if (!isPointOnDiagramControl(e.getPoint())) {
							onMousePressed(e);
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				
				// On mouse released.
				@Override
				public void mouseReleased(MouseEvent e) {
					try {
			
						if (e.isPopupTrigger()
								&& toolList.getSelected() != ToolId.ZOOM) {
							popupMenuActivation = true;
							onPopupMenu(e);
							return;
						}
						popupMenuActivation = false;
						onMouseReleasedEvent(e);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					try {
			
						// Trigger between selection and moving.
						if (e.getKeyCode() == 18) {
							triggerSelectionMoving();
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			// Set listener.
			addMouseMotionListener(new MouseAdapter() {
				// On mouse moved.
				@Override
				public void mouseMoved(MouseEvent e) {
					try {
			
						Point mouse = e.getPoint();
						onMouseMovedEvent(mouse);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			// Listener.
			addMouseMotionListener(new MouseAdapter() {
				// On mouse moved.
				@Override
				public void mouseDragged(MouseEvent e) {
					try {
			
						if (popupMenuActivation) {
							return;
						}
						onMouseDraggedEvent(e);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			
			// Add listeners.
			addMouseWheelListener(new MouseAdapter() {
				// On mouse wheel.
				@Override
				public void mouseWheelMoved(MouseWheelEvent e) {
					try {
			
						onMouseWheel(e);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			
			overview.setListener(new OverviewAdapter() {
				@Override
				public void onDrawContent(Graphics2D g2, AffineTransform transform) {
					try {
			
						onDrawOverview(g2, transform);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
	
			addComponentListener(new ComponentAdapter() {
				// On size changed.
				@Override
				public void componentResized(ComponentEvent e) {
					try {
			
						setLocationAndViewport();
						onResizedEvent();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				// On show panel.
				@Override
				public void componentShown(ComponentEvent e) {
					try {
			
						setLocationAndViewport();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			
			// Add focus listener.
			addFocusListener(new FocusListener() {
				// On focus lost.
				@Override
				public void focusLost(FocusEvent e) {
					try {
			
						onFocusLost();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				// On focus gained.
				@Override
				public void focusGained(FocusEvent e) {
					try {
			
						onFocusGained();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
	
			addMouseListener(new MouseAdapter() {
				// On mouse entered.
				@Override
				public void mouseEntered(MouseEvent e) {
					try {
			
						onMouseEnteredEvent();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				// On mouse exited.
				@Override
				public void mouseExited(MouseEvent e) {
					try {
			
						onMouseExitedEvent();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			horizontalScroll.setListener(new ScrollListener(){
				@Override
				protected void onScroll(Rectangle2D win) {
					try {
			
						onHorizontalScrollEvent(win);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			verticalScroll.setListener(new ScrollListener() {
				@Override
				protected void onScroll(Rectangle2D win) {
					try {
						
						onVerticalScrollEvent(win);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			
			// Get animation tick in milliseconds.
			final double animationTickMs = 20.0;
			
			// Create animate listener.
			ActionListener animateListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						
						// Get animation duration.
						double animationDurationMs = Settings.getAnimationDuration() * 1000.0;
						
						// Invoke on animate method.
						onAnimate();
						
						// If the animation time equals the duration time, stop timer.
						if (animationTime >= animationDurationMs) {
							finishAnimation();
							return;
						}
						
						// Set overview.
						setOverviewControl();
						
						// Increment time.
						animationTime += animationTickMs;
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			};
			
			// Create animation timer.
			animationTimer = new javax.swing.Timer((int) animationTickMs, animateListener);
			animationTimer.setCoalesce(false);
			
			// Set key listener.
			setFocusable(true);
			addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					try {
						
						char keyChar = e.getKeyChar();
						if (keyChar == KeyEvent.VK_ESCAPE) {
							onEscapeKey();
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			
			// Create position save timer.
			positionSaveTimer = new javax.swing.Timer(positionSavingDelay, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						
						// Save focus.
						saveFocus(getTranslatingX(), getTranslatingY(), getZoom());
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			positionSaveTimer.setRepeats(false);			
			
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};

	}
	
	/**
	 * Remove listeners.
	 */
	private void removeListeners() {
		try {
			
			ApplicationEvents.removeReceivers(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * TODO: <---MAKE Reset tool tip.
	 */
	private void resetToolTip() {
		try {
			
			boolean active = false;
			Window diagramWindow = Utility.findWindow(GeneralDiagramPanel.this);
			if (diagramWindow != null) {
				active = diagramWindow.isActive();
			}
			
			// If the diagram is loaded, visible and active.
			if (GeneralDiagramPanel.this.isLoaded() && GeneralDiagramPanel.this.isShowing() && active) {
				
				if (!GeneralDiagramPanel.this.toolList.onToolTip()) {
					if (!GeneralDiagramPanel.this.onToolTip()) {
						
						closeToolTip();
						GeneralDiagramPanel.this.toolList.resetToolTipHistory();
						GeneralDiagramPanel.this.resetToolTipHistory();
					}
					else {
						GeneralDiagramPanel.this.toolList.resetToolTipHistory();
					}
				}
				else {
					GeneralDiagramPanel.this.resetToolTipHistory();
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Trigger between selection and moving.
	 */
	protected void triggerSelectionMoving() {
		try {
			
			// Trigger tool.
			ToolId toolId = toolList.getSelected();
			if (toolId == ToolId.CURSOR) {
				toolList.selectTool(ToolId.MOVE);
			}
			else if (toolId == ToolId.MOVE) {
				toolList.selectTool(ToolId.CURSOR);
			}
			
			Safe.invokeLater(() -> {
				// Set cursor.
				getCursorArea().useCursor();
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On focus gained.
	 */
	protected void onFocusGained() {
		
	}

	/**
	 * On focus lost.
	 */
	protected void onFocusLost() {

	}

	/**
	 * On mouse exited.
	 */
	protected void onMouseExitedEvent() {
		try {
			
			// If the remove tool is selected...
			if (toolList.getSelected() == ToolId.REMOVE) {
				setRemoveShapeVisibility(false);
			}
			// Call abstract method.
			onMouseExited();
			
			repaint();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On mouse exited.
	 */
	protected abstract void onMouseExited();

	/**
	 * On mouse entered.
	 */
	protected void onMouseEnteredEvent() {
		try {
			
			// If the remove tool is selected...
			if (toolList.getSelected() == ToolId.REMOVE) {
				setRemoveShapeVisibility(true);
			}
			// Call abstract method.
			onMouseEntered();
		
			repaint();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On mouse entered.
	 */
	protected abstract void onMouseEntered();

	/**
	 * On resized event.
	 */
	protected void onResizedEvent() {
		try {
			
			toolList.onResized();
			// Set zoom.
			setZoom();
			// Set zoom shape.
			if (zoomShape != null) {
				zoomShape.setPosition(getWidth() - ZoomShape.getWidth(), 0);
				zoomShape.setMinimal(minimumZoom);
			}
	
			// Set scroll bars.
			setScrollBarsLocation();
			setScrollBars();
			
			// Call abstract method.
			onResized();
			
			repaint();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On resized.
	 */
	protected abstract void onResized();

	/**
	 * On mouse signalReleased.
	 * @param e
	 */
	protected void onMouseReleasedEvent(MouseEvent e) {
		try {
			
			if (animationTimer.isRunning()) {
				return;
			}
			
			toolList.onMouseReleased();
			zoomShape.onMouseReleased();
			horizontalScroll.onMouseReleased();
			verticalScroll.onMouseReleased();
			
			// If move tool selected...
			if (toolList.getSelected() == ToolId.MOVE || toolList.getSelected() == ToolId.CONNECTOR) {
				mousePosPressed = null;
				translationx += deltax;
				translationy += deltay;
				deltax = deltay = 0;
			}
			// Otherwise call the abstract method.
			else {
				onMouseReleased(e, isPointOnDiagramControl(e.getPoint()));
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On mouse signalReleased.
	 * @param onDiagramElement 
	 */
	protected abstract void onMouseReleased(MouseEvent e, boolean onDiagramElement);
	
	/**
	 * On vertical scroll.
	 * @param win
	 */
	protected void onVerticalScrollEvent(Rectangle2D win) {
		try {
			
			// Call abstract method.
			onVerticalScroll(win);
			
			setScrollBars();
			repaint();
			
			// Call overridden method.
			onDiagramPositionChanged();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On vertical scroll.
	 * @param win
	 */
	protected abstract void onVerticalScroll(Rectangle2D win);
	
	/**
	 * On horizontal scroll.
	 * @param win
	 */
	protected void onHorizontalScrollEvent(Rectangle2D win) {
		try {
			
			// Call abstract method.
			onHorizontalScroll(win);
			
			setScrollBars();
			repaint();
			
			// Call overridden method.
			onDiagramPositionChanged();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On horizontal scroll.
	 * @param win
	 */
	protected abstract void onHorizontalScroll(Rectangle2D win);

	/**
	 * On mouse dragged.
	 * @param e
	 */
	protected abstract void onMouseDragged(MouseEvent e);
	
	/**
	 * On mouse dragged.
	 * @param e
	 */
	protected void onMouseDraggedEvent(MouseEvent e) {
		try {
			
			if (animationTimer.isRunning()) {
				return;
			}
	
			// Check button. BUTTON2 uses popup trayMenu.
			if (e.getButton() == MouseEvent.BUTTON2) {
				return;
			}
			
			boolean flagSetOverview = true;
			
			// If zoom dragged.
			if (zoomShape.isDragged()) {
				
				zoomShape.onMouseDragged(e.getPoint());
				overview.setScale(zoom);
				
				onDiagramPositionChanged();
			}
			// If horizontal scroll dragged.
			else if (horizontalScroll.isDragged()) {
				
				horizontalScroll.onMouseDragged(e);
			}
			// If vertical scroll dragged.
			else if (verticalScroll.isDragged()) {
				
				verticalScroll.onMouseDragged(e);
			}
			else {
				// Reset flag.
				flagSetOverview = false;
				
				// If the diagram content is moved...
				if ((toolList.getSelected() == ToolId.MOVE || toolList.getSelected() == ToolId.CONNECTOR)
					&& mousePosPressed != null) {
					
					deltax = e.getX() - mousePosPressed.getX();
					deltay = e.getY() - mousePosPressed.getY();
					
					setScrollBars();
					
					// Reset tool tip history.
					resetToolTipHistory();
					
					// Set flag.
					flagSetOverview = true;
					
					// Call overridden method.
					onDiagramPositionChanged();
					
					if (toolList.getSelected() == ToolId.CONNECTOR) {
						// Call overloaded method.
						onMouseDragged(e);
					}
				}
				else {
					// Call overloaded method.
					onMouseDragged(e);
				}
			}
			
			// Set overview window.
			if (flagSetOverview) {
				overview.setTranslation(getTranslatingX(), getTranslatingY());
				repaint();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * O select tool.
	 * @param toolId
	 * @param oldSelection 
	 */
	protected abstract void onSelectTool(ToolId toolId, ToolId oldSelection);

	/**
	 * On mouse pressed.
	 * @param e
	 */
	protected abstract void onMousePressed(MouseEvent e);
	
	/**
	 * On popup trayMenu.
	 * @param e 
	 */
	protected abstract void onPopupMenu(MouseEvent e);

	/**
	 * On mouse moved.
	 * @param mouse
	 */
	protected abstract void onMouseMoved(Point mouse, boolean onDiagramElement);

	/**
	 * On mouse pressed.
	 * @param mouseEvent
	 */
	private void onMousePressedEvent(MouseEvent mouseEvent) {
		try {
			
			int button = mouseEvent.getButton();
			Point mouse = mouseEvent.getPoint();
			
			// Set focus.
			requestFocusInWindow();
				
			// Invoke tool list method.
			toolList.onMouseDown(mouse);
			
			// If the mouse is outside the tool list.
			if (!toolList.contains(mouse)) {
	
				// Use horizontal scroll.
				if (horizontalScroll.contains(mouse)) {
					horizontalScroll.onMousePressed(mouse);
				}
				// Use vertical scroll.
				else if (verticalScroll.contains(mouse)) {
					verticalScroll.onMousePressed(mouse);
				}
				// Use zoom shape.
				else if (zoomShape.contains(mouse)) {
					zoomShape.onMousePressed(mouse);
				}
				// Use overview.
				else if (overview.contains(mouse)) {
					overview.onMouseAction(mouse);
				}
				// Process diagram.
				else {
					// If cursor tool selected.
					if (toolList.getSelected() == ToolId.CURSOR) {
						
						if (button == MouseEvent.BUTTON1) {
							mousePosPressed = mouse;
						}
					}
					// If move tool selected.
					else if (toolList.getSelected() == ToolId.MOVE
							|| toolList.getSelected() == ToolId.CONNECTOR) {
						
						mousePosPressed = mouse;
					}
					// If zoom tool selected.
					else if (toolList.getSelected() == ToolId.ZOOM) {
	
						// On left button zoom.
						if (button == MouseEvent.BUTTON1) {
							zoomDiagram(zoom * zoomStep, mouse.x, mouse.y);
							zoomShape.setZoom(zoom);
							overview.setScale(zoom);
						}
						// On right button.
						else if (button == MouseEvent.BUTTON3) {
							zoomDiagram(zoom / zoomStep, mouse.x, mouse.y);
							zoomShape.setZoom(zoom);
							overview.setScale(zoom);
						}
						
						// Set scroll bars.
						setScrollBars();
						// Set overview.
						overview.setTranslation(getTranslatingX(), getTranslatingY());
						repaint();
					}
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Zoom diagram.
	 */
	protected void zoomDiagram(double newzoom, double x, double y) {
		try {
			
			double oldzoom = zoom;
			zoom = newzoom;
			
			reduceZoom();
	
			// Set translation.
			translationx = x - zoom / oldzoom * (x - translationx);
			translationy = y - zoom / oldzoom * (y - translationy);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On mouse wheel.
	 */
	protected void onMouseWheel(MouseWheelEvent e) {
		try {
			
			if (animationTimer.isRunning()) {
				return;
			}
	
			// Set zoom.
			int wheel = e.getWheelRotation();
			// Zoom.
			zoomDiagram(zoom * Math.exp(-wheel / 10.0), e.getPoint().x, e.getPoint().y);
			
			zoomShape.setZoom(zoom);
			
			setScrollBars();
			// Set overview.
			overview.setScale(zoom);
			overview.setTranslation(translationx, translationy);
			
			repaint();
			
			onDiagramPositionChanged();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set scroll bars.
	 */
	protected void setScrollBars() {
		try {
			
			// Get content and window. Compute union.
			Rectangle2D content = getContentRectangle();
			
			if (content != null) {
				Rectangle2D window = getWindowRectangle();
				Rectangle2D union = Utility.union(window, content);
				
				// Set scroll bars.
				horizontalScroll.set(window, union);
				verticalScroll.set(window, union);
				
				// Set overview.
				overview.setRightBottomSpace(verticalScroll.isVisible(), horizontalScroll.isVisible());
				overview.setVisible(horizontalScroll.isVisible() || verticalScroll.isVisible());
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Gets rectangle.
	 * @return
	 */
	protected abstract Rectangle2D getWindowRectangle();

	/**
	 * Gets content rectangle.
	 * @return
	 */
	protected abstract Rectangle2D getContentRectangle();

	/**
	 * On mouse moved.
	 */
	private void onMouseMovedEvent(Point mouse) {
		try {
			
			// From lower to higher priority.

			// Set diagram cursor.
			getCursorArea().onCursor(mouse);
			// Set tool list cursor.
			toolList.getCursorArea().onCursor(mouse);
			// Set overview cursor.
			overview.getCursorArea().onCursor(mouse);
			// Set overview button cursor.
			overview.getButtonCursorArea().onCursor(mouse);
			// Set zoom shape cursor.
			zoomShape.getCursorArea().onCursor(mouse);
			// Set horizontal scroll cursor.
			horizontalScroll.getCursorArea().onCursor(mouse);
			// Set vertical scroll cursor.
			verticalScroll.getCursorArea().onCursor(mouse);
			
			// Set flag.
			boolean onDiagramControl = isPointOnDiagramControl(mouse);
			
			// If the remove area tool is selected.
			if (!onDiagramControl) {
				if (toolList.getSelected() == ToolId.REMOVE) {
					// Set remove shape location.
					removeElementShape.setLocation(mouse);
					removeElementShape.setVisible(true);
					repaint();
				}
			}
			else {
				boolean repaint = removeElementShape != null;
				// Hide remove shape.
				setRemoveShapeVisibility(false);
				
				if (repaint) {
					repaint();
				}
			}
				
			onMouseMoved(mouse, onDiagramControl);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Returns true value if the point is on diagram element.
	 * @param point
	 * @return
	 */
	public boolean isPointOnDiagramControl(Point point) {
		
		try {
			return toolList.contains(point)
				|| overview.contains(point)
				|| zoomShape.contains(point)
				|| horizontalScroll.contains(point)
				|| verticalScroll.contains(point);
		}
		catch (Throwable e) {
            e.printStackTrace();
        }
		return false;
	}

	/**
	 * Sets diagram cursor.
	 */
	protected void setDiagramCursor() {
		try {
			
			ToolId toolId = toolList.getSelected();
			
			CursorAreaImpl cursorArea = getCursorArea();
			
			// If the move tool is selected...
			if (toolId == ToolId.MOVE) {
				// Set cursor.
				cursorArea.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}
			// If the zoom tool is selected...
			else if (toolId == ToolId.ZOOM) {
				// Set cursor.
				cursorArea.setCursor(
						Images.loadCursor("org/multipage/generator/images/zoom_cursor.png",
								new Point(10, 10)));
			}
			// If the add area tool is selected...
			else if (toolId == ToolId.AREA) {
				// Set cursor.
				cursorArea.setCursor(
						Images.loadCursor("org/multipage/generator/images/add_cursor.png",
								new Point(1, 1)));
			}
			// If the remove tool is selected...
			else if (toolId == ToolId.REMOVE) {
				// Set cursor.
				cursorArea.setCursor(
						Images.loadCursor("org/multipage/generator/images/remove_cursor.png",
								new Point(1, 1)));
		
			}
			// If the connector tool is selected...
			else if (toolId == ToolId.CONNECTOR) {
				
				// Set cursor.
				cursorArea.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
			// Otherwise...
			else {
				// Set cursor.
				cursorArea.setCursor(Cursor.getDefaultCursor());
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set overview.
	 */
	protected void setOverviewControl() {
		try {
			
			overview.setTranslation(getTranslatingX(), getTranslatingY());
			overview.setScale(getZoom());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Gets zoom.
	 * @return
	 */
	protected double getZoom() {
		
		return zoom * zoomMultiplier;
	}

	/**
	 * On escape key.
	 */
	private void onEscapeKey() {
		try {
			
			toolList.selectTool(0);
			// Call on escape method.
			onEscape();
			
			repaint();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On escape.
	 */
	protected abstract void onEscape();
	
	/**
	 * Set viewport.
	 */
	private void setLocationAndViewport() {
		try {
			
			// Set overview location and window size.
			overview.setRightBottomLocation(getWidth(), getHeight());
			
			// Set overview.
			overview.setViewportRectangle(new Rectangle(
					ToolList.getWidth(), 0,
					getWidth() - ToolList.getWidth(), getHeight()));
			
			// Set cursor area.
			cursorArea.setShape(getBounds());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Returns true if the dialog is loaded.
	 * @return
	 */
	protected boolean isLoaded() {

		return isLoaded;
	}

	/**
	 * Remove diagram.
	 */
	protected void removeDiagram() {
		
	}

	/**
	 * Resets tool tip.
	 */
	protected abstract void resetToolTipHistory();

	/**
	 * On tool tip.
	 */
	protected abstract boolean onToolTip();

	/**
	 * Close tool tip.
	 */
	public static void closeToolTip() {
		try {
			
			if (tooltipWindow != null) {
				tooltipWindow.hidew();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set actual status bar text.
	 */
	public void setActualStatusText() {
		try {
			
			String text = toolList.getSelectedDescription();
			GeneratorMainFrame.getFrame().setMainToolBarText(text);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * @return the tooltipWindow
	 */
	public static ToolTipWindow getTooltipWindow() {
		return tooltipWindow;
	}

	/**
	 * Sets "loaded" flag.
	 */
	public void setLoaded() {
		this.isLoaded = true;
	}

	/**
	 * On draw overview content.
	 * @param g2
	 * @param transform 
	 */
	protected abstract void onDrawOverview(Graphics2D g2, AffineTransform transform);

	/**
	 * Animate translation and scale.
	 * @param vectorX
	 * @param vectorY
	 * @param relativeZoom
	 * @param animate
	 * @param addVector
	 */
	public void animateTranslationAndScaleRelative(double vectorX,
			double vectorY, double relativeZoom,
			boolean animate, boolean addVector) {
		try {
			
			// If the diagram is animating exit the method.
			if (animationTimer.isRunning()) {
				return;
			}
			
			// Set animation vector.
			if (addVector) {
				animateVectorX += vectorX;
				animateVectorY += vectorY;
			}
			else {
				animateVectorX = vectorX;
				animateVectorY = vectorY;
			}
			
			// Set animation zoom.
			animateRelativeZoom = relativeZoom;
			
			// If it is null vector, exit method.
			if (animateVectorX == 0.0 && animateVectorY == 0.0) {
				return;
			}
			
			if (animate) {
	
				// Initialize timer.
				animationTime = 0.0;
				// Start animation.
				animationTimer.start();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Animate translation and scale.
	 * @param newTranslationX
	 * @param newTranslationY
	 * @param newZoom
	 */
	public void animateTranslationAndScaleAbsolute(double newTranslationX,
			double newTranslationY, double newZoom) {
		try {
			
			double vectorX = newTranslationX - translationx;
			double vectorY = newTranslationY - translationy;
			double relativeZoom = newZoom / zoom;
	
			animateTranslationAndScaleRelative(vectorX, vectorY, relativeZoom,
					true, false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Finish animation.
	 */
	protected void finishAnimation() {
		try {
			
			// If the animation is stopped, exit the method.
			if (!animationTimer.isRunning()) {
				return;
			}
	
			// Stop the animation and save translation and zoom.
			animationTimer.stop();
			
			animationTime = 0.0;
			translationx += deltax;
			translationy += deltay;
			zoom *= zoomMultiplier;
			deltax = deltay = 0;
			animateVectorX = animateVectorY = 0;
			zoomMultiplier = 1.0;
			
			// Call set zoom virtual method.
			setZoom();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Returns true value if an animation is running.
	 * @return
	 */
	protected boolean isAnimationRunning() {
		
		try {
			return animationTimer.isRunning();
		}
		catch (Throwable e) {
			e.printStackTrace();
            return false;
        }
	}

	/**
	 * Animate.
	 * @param translationX2
	 * @param translationY2
	 * @param newZoom
	 */
	public void animate(double translationX2, double translationY2,
			double newZoom) {
		try {
			
			// Finish possible animation an do new animation.
			finishAnimation();
			animateTranslationAndScaleAbsolute(translationX2, translationY2, newZoom);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Gets x translation.
	 */
	public double getTranslatingX() {
		
		return translationx + deltax;
	}
	
	/**
	 * Gets y translation.
	 */
	public double getTranslatingY() {
		
		return translationy + deltay;
	}

	/**
	 * Undo transformation.
	 */
	public Point2D doTransformation(Point point) {
		
		try {
			return new Point2D.Double((point.x - getTranslatingX()) / zoom,
					                  (point.y - getTranslatingY()) / zoom);
		}
		catch (Throwable e) {
			e.printStackTrace();
			return new Point2D.Double();
		}	
	}

	/**
	 * Do transformation.
	 */
	public Point2D undoTransformation(Point2D point) {
		
		try {
			return new Point2D.Double(zoom * point.getX() + getTranslatingX(),
					         zoom * point.getY() + getTranslatingY());
		}
		catch (Throwable e) {
			e.printStackTrace();
			return new Point2D.Double();
		}
	}

	/**
	 * Do transformation.
	 */
	public double undoTransformationX(double x) {
		
		try {
			return zoom * x + getTranslatingX();
		}
		catch (Throwable e) {
			e.printStackTrace();
            return 0.0;
        }
	}

	/**
	 * Do transformation.
	 */
	public double undoTransformationY(double y) {
		
		try {
			return zoom * y + getTranslatingY();
		}
		catch (Throwable e) {
			e.printStackTrace();
            return 0.0;
        }	
	}

	/**
	 * Do transformation.
	 */
	public double undoTransformationZoom(double d) {
		
		try {
			return zoom * d;
		}
		catch (Throwable e) {
			e.printStackTrace();
            return 0.0;
        }	
	}

	/**
	 * Do transformation.
	 * @param rectangle
	 */
	public Rectangle2D undoTransformation(Rectangle2D rectangle) {
		
		try {
			return new Rectangle2D.Double(
					zoom * rectangle.getX() + getTranslatingX(),
					zoom * rectangle.getY() + getTranslatingY(),
					zoom * rectangle.getWidth(),
					zoom * rectangle.getHeight());
		}
		catch (Throwable e) {
			e.printStackTrace();
            return new Rectangle2D.Double();
        }
	}
	
	/**
	 * On animate.
	 */
	protected void onAnimate() {
		try {
			
			// Get animation duration.
			double T = Settings.getAnimationDuration() * 1000.0;
			double t = animationTime;
			double multiplier;
			
			// Compute multiplier.
			multiplier = t/T - 1/(2*Math.PI) * Math.sin(2*Math.PI*t/T);
	
			// Set translation deltas.
			deltax = animateVectorX * multiplier;
			deltay = animateVectorY * multiplier;
			
			// Set zoom.
			if (animateRelativeZoom > 0.0 && animateRelativeZoom != 1.0) {
	
				zoomMultiplier = (animateRelativeZoom - 1) * multiplier + 1;
			}
			else {
				zoomMultiplier = 1.0;
			}
	
			// Call set zoom virtual method.
			setZoom();
			// Set scroll bars.
			onSetScrollBars();
			// Redraw diagram.
			repaint();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On set scroll bars.
	 */
	protected void onSetScrollBars() {
		
		// Set scroll bars.
		setScrollBars();
	}
	
	/**
	 * Set overview location.
	 */
	public void setScrollBarsLocation() {
		try {
			
			int verticalTop;
			
			// Set vertical tool bar top position.
			if (zoomShape != null && zoomShape.isVisible()) {
				verticalTop = ZoomShape.height + zoomScrollSpace;
			}
			else {
				verticalTop = 0;
			}
			
			// Set scroll bars.
			horizontalScroll.setLocation(ToolList.getWidth(), getHeight() - HorizontalScroll.getHeight(),
					getWidth() - VerticalScroll.getWidth());
			verticalScroll.setLocation(getWidth() - VerticalScroll.getWidth(), verticalTop,
					getHeight() - HorizontalScroll.getHeight());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set zoom.
	 */
	protected abstract void setZoom();
	
	/**
	 * Center diagram.
	 */
	protected abstract void center();
	
	/**
	 * Reduces zoom.
	 */
	protected void reduceZoom() {
		
		try {
			zoom = reduceZoom(zoom);
		}
		catch (Throwable e) {
			e.printStackTrace();
        }
	}
	
	/**
	 * Reduces zoom.
	 * @param zoom
	 */
	protected double reduceZoom(double zoomValue) {
		
		try {
			if (zoomValue < minimumZoom) {
				zoomValue = minimumZoom;
			}
			else if (zoomValue > maximumZoom) {
				zoomValue = maximumZoom;
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
        }
		
		return zoomValue;
	}
	
	/**
	 * Sets remove shape visibility.
	 * @param visible
	 */
	public void setRemoveShapeVisibility(boolean visible) {
		try {
			
			if (removeElementShape != null) {
				removeElementShape.setVisible(visible);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Save focus.
	 * @param translationx2
	 * @param translationy2
	 * @param zoom
	 */
	protected void saveFocus(double translationx2, double translationy2,
			double zoom) {
		try {
			
			if (animationTimer.isRunning()) {
				return;
			}
			
			/*//// test ////
			testDlg.setTranslationX(translationx2);
			testDlg.setTranslationY(translationy2);
			testDlg.setZoom(zoom);
			testDlg.checkValues(translationx2, translationy2, zoom);*/
			
			// Reset flag and stop timer.
			isDiagramPositionChanged = false;
			positionSaveTimer.stop();
			
			// If new position is near the undo position, exit the method.
			if (isFocusChangeSmall(translationx2, translationy2, zoom)) {
				return;
			}
			
			// Trim the number of list items.
			if (focusUndoList.size() > maximumNumberOfRedos) {
				int countToDelete = focusUndoList.size() - maximumNumberOfRedos;
				
				for (int n = 0; n < countToDelete; n++) {
					focusUndoList.removeFirst();
				}
			}
	
			// Remove current focus and all after the current focus.
			LinkedList<FocusUndo> focusesToRemove = new LinkedList<FocusUndo>();
			boolean toRemove = false;
			
			for (FocusUndo focus : focusUndoList) {
				
				if (toRemove) {
					focusesToRemove.add(focus);
				}
				if (focus == currentFocus) {
					toRemove = true;
				}
	
			}
			focusUndoList.removeAll(focusesToRemove);
			
			// Add new focus.
			currentFocus = new FocusUndo(translationx2, translationy2, zoom);
			focusUndoList.add(currentFocus);
			
			// Update undo and redo components.
			updateUndoRedo();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Returns true value if the focus change is small.
	 * @param translationx
	 * @param translationy
	 * @param zoom2
	 * @return
	 */
	private boolean isFocusChangeSmall(double translationx,
			double translationy, double zoom2) {
		
		try {
			if (currentFocus != null) {
				
				if (zoom2 == currentFocus.zoom) {
					
					double delta = Math.sqrt(
							Math.pow(translationx - currentFocus.translationX, 2.0)
							+ Math.pow(translationy - currentFocus.translationY, 2.0));
					
					return delta <= 10.0;
				}
			}
		}
		catch (Throwable e) {
            e.printStackTrace();
        }
		return false;
	}

	/**
	 * Undo focus.
	 */
	public void undoFocus() {
		try {
			
			boolean isPositionChanged = isDiagramPositionChanged;
			
			// Reset flag and stop timer.
			isDiagramPositionChanged = false;
			positionSaveTimer.stop();
	
			if (isPositionChanged && currentFocus != null) {
				// Animate to the previous location.
				animate(currentFocus.translationX, currentFocus.translationY,
						currentFocus.zoom);
			}
			else {
				FocusUndo previousFocus = null;
				// Get previous focus.
				int currentIndex = focusUndoList.indexOf(currentFocus);
		
				if (currentIndex > 0) {
					previousFocus = focusUndoList.get(currentIndex - 1);
				}
				
				// Set current focus.
				if (previousFocus != null) {
					currentFocus = previousFocus;
					// Animate to the previous location.
					animate(previousFocus.translationX, previousFocus.translationY,
							previousFocus.zoom);
				}
			}
	
			// Update undo and redo components.
			updateUndoRedo();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Redo focus.
	 */
	public void redoFocus() {
		try {
			
			// Reset flag and stop timer.
			isDiagramPositionChanged = false;
			positionSaveTimer.stop();
	
			FocusUndo nextFocus = null;
			// Get next focus.
			int currentIndex = focusUndoList.indexOf(currentFocus);
			
			if (currentIndex < focusUndoList.size() - 1) {
				nextFocus = focusUndoList.get(currentIndex + 1);
			}
			
			// Set current focus.
			if (nextFocus != null) {
				currentFocus = nextFocus;
				// Animate to the next location.
				animate(nextFocus.translationX, nextFocus.translationY,
						nextFocus.zoom);
			}
	
			// Update undo and redo components.
			updateUndoRedo();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Update undo and redo buttons.
	 */
	public void updateUndoRedo() {
		try {
			
			boolean enableUndo = false;
			boolean enableRedo = false;
	
			if (!focusUndoList.isEmpty()) {
				
				if (isDiagramPositionChanged) {
					enableUndo = true;
				}
				else {
					enableUndo = focusUndoList.getFirst()
							!= currentFocus;
				}
				
				enableRedo = focusUndoList.getLast() != currentFocus;
			}
			
			if (undoComponent != null) {
				undoComponent.setEnabled(enableUndo);
			}
			if (redoComponent != null) {
				redoComponent.setEnabled(enableRedo);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set undo and redo components references.
	 * @param undoComponent
	 * @param redoComponent
	 */
	public void setUndoRedoComponents(Component undoComponent,
			Component redoComponent) {
		try {
			
			this.undoComponent = undoComponent;
			this.redoComponent = redoComponent;
			updateUndoRedo();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On diagram position changed.
	 */
	protected void onDiagramPositionChanged() {
		try {
			
			isDiagramPositionChanged = true;
			updateUndoRedo();
			
			// Start saving delay.
			positionSaveTimer.start();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Close diagram
	 */
	public void close() {
		
	}

	/**
	 * 
	 * @param translationx
	 * @param translationy
	 * @param zoom
	 */
	public void setDiagramPosition(double translationx, double translationy,
			double zoom) {
		try {
			
			this.translationx = translationx;
			this.translationy = translationy;
			this.zoom = zoom;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * The method is called before this diagram is removed.
	 */
	protected void beforeRemoved() {
		
		// Remove listeners
		removeListeners();
	}
}

/**
 * 
 * @author
 *
 */
class FocusUndo {

	/**
	 * Coordinates.
	 */
	double translationX;
	double translationY;
	double zoom;

	/**
	 * Constructor.
	 * @param translationx2
	 * @param translationy2
	 * @param zoom
	 */
	public FocusUndo(double translationx2, double translationy2,
			double zoom) {
		try {
			
			this.translationX = translationx2;
			this.translationY = translationy2;
			this.zoom = zoom;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
