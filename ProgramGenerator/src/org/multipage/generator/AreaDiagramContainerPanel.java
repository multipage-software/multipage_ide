/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.maclan.Area;
import org.maclan.AreasModel;
import org.maclan.VersionObj;
import org.multipage.generator.AreaDiagramPanel.Callbacks;
import org.multipage.gui.ApplicationEvents;
import org.multipage.gui.GraphUtility;
import org.multipage.gui.GuiSignal;
import org.multipage.gui.Images;
import org.multipage.gui.UpdatableComponent;
import org.multipage.gui.Utility;
import org.multipage.util.Closable;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Container panel for area diagram.
 * @author vakol
 */
public class AreaDiagramContainerPanel extends JPanel implements TabItemInterface, UpdatableComponent, Closable {
	
	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Editor states.
	 */
	public static int splitPositionStateMain = 480;
	public static int splitPositionStateSecondary = 340;
	public static LinkedList<Long> selectedAreasIdsState = new LinkedList<Long>();
	
	/**
	 * Area diagram panel.
	 */
	private AreaDiagramPanel areaDiagramPanel;
	
	/**
	 * Favorites model.
	 */
	private FavoritesModel favoritesModel;
	
	/**
	 * Favorites list renderer.
	 */
	private ListCellRenderer<Long> favoritesRenderer;

	/**
	 * Focused area.
	 */
	Area lastFocusedArea;

	/**
	 * Focused area index.
	 */
	int focusedAreaIndex = 0;
	
	/**
	 * Areas list model.
	 */
	private DefaultListModel<Area> listAreasModel;
	
	/**
	 * Selected area.
	 */
	private Area areaSelection = null;
	
	/**
	 * A reference to the tab label
	 */
	private TabLabel tabLabel;
	
	/**
	 * Top area ID
	 */
	@SuppressWarnings("unused")
	private Long topAreaId = 0L;

	// $hide<<$
	/**
	 * Components.
	 */
	private JSplitPane splitPane;
	private JPanel panelForDiagram;
	private JPanel panelTree;
	private JScrollPane scrollPane;
	private JSplitPane splitPaneTree;
	private JScrollPane scrollPaneFavorites;
	private final JPanel panelFavorites = new JPanel();
	private JList<Area> listAreas;
	private JList<Long> listFavorites;
	private JPopupMenu popupMenuFavorites;
	private JMenuItem menuDeleteFavorites;
	private JPanel panel;
	private JButton buttonUp;
	private JButton buttonDown;
	private JPanel panelAreasTop;
	private JLabel labelFavorites;
	private JPopupMenu popupMenuAreas;
	private JToolBar toolBarAreas;
	private JToggleButton buttonSiblings;
	private JToggleButton buttonSubAreas;
	private JToggleButton buttonSuperAreas;
	private JLabel labelAreaListDescription;
	private JToggleButton buttonHoldListType;
	private JSeparator separator;
	private Component horizontalStrut;

	/**
	 * Create the panel.
	 */
	public AreaDiagramContainerPanel() {
		
		try {
			panelFavorites.setLayout(new BorderLayout(0, 0));
	
			initComponents();
			// $hide>>$
			postCreation();
			// $hide<<$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Load dialog.
	 */
	private void loadDialog() {
		try {
			
			splitPane.setDividerLocation(splitPositionStateMain);
			splitPaneTree.setDividerLocation(splitPositionStateSecondary);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Save dialog.
	 */
	private void saveDialog() {
		try {
			
			// Save state.
			splitPositionStateMain = splitPane.getDividerLocation();
			splitPositionStateSecondary = splitPaneTree.getDividerLocation();
			saveFavorites();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Initialize components.
	 */
	private void initComponents() {
		setLayout(new BorderLayout(0, 0));
		
		splitPane = new JSplitPane();
		splitPane.setContinuousLayout(true);
		splitPane.setResizeWeight(1.0);
		splitPane.setOneTouchExpandable(true);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		add(splitPane);
		
		panelForDiagram = new JPanel();
		splitPane.setLeftComponent(panelForDiagram);
		panelForDiagram.setLayout(new BorderLayout(0, 0));
		
		splitPaneTree = new JSplitPane();
		splitPaneTree.setContinuousLayout(true);
		splitPaneTree.setOneTouchExpandable(true);
		splitPane.setRightComponent(splitPaneTree);
		
		panelTree = new JPanel();
		splitPaneTree.setLeftComponent(panelTree);
		panelTree.setLayout(new BorderLayout(0, 0));
		
		scrollPane = new JScrollPane();
		panelTree.add(scrollPane, BorderLayout.CENTER);
		
		listAreas = new JList<>();
		listAreas.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				
				if (e.getValueIsAdjusting()) {
					return;
				}
				Safe.tryOnChange(AreaDiagramContainerPanel.this, () -> {
					onSelectArea();
				});
			}
		});
		listAreas.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {	
					Safe.tryOnChange(AreaDiagramContainerPanel.this, () -> {
						onEscapeKey();
					});
				}
			}
		});
		listAreas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				if (e.getButton() == MouseEvent.BUTTON1) {
					Safe.tryOnChange(AreaDiagramContainerPanel.this, () -> {
						int clicks = e.getClickCount();
						if (clicks == 2) {
							onDoubleClickArea();
						}
					});
				}
			}
		});
		scrollPane.setViewportView(listAreas);
		
		popupMenuAreas = new JPopupMenu();
		addPopup(listAreas, popupMenuAreas);
		
		panelAreasTop = new JPanel();
		scrollPane.setColumnHeaderView(panelAreasTop);
		panelAreasTop.setLayout(new BorderLayout(0, 0));
		
		toolBarAreas = new JToolBar();
		toolBarAreas.setFloatable(false);
		panelAreasTop.add(toolBarAreas, BorderLayout.NORTH);
		
		buttonSiblings = new JToggleButton("");
		buttonSiblings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onAreaListButton(e);
			}
		});
		buttonSiblings.setPreferredSize(new Dimension(16, 16));
		toolBarAreas.add(buttonSiblings);
		
		buttonSubAreas = new JToggleButton("");
		buttonSubAreas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onAreaListButton(e);
			}
		});
		buttonSubAreas.setPreferredSize(new Dimension(16, 16));
		toolBarAreas.add(buttonSubAreas);
		
		buttonSuperAreas = new JToggleButton("");
		buttonSuperAreas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onAreaListButton(e);
			}
		});
		buttonSuperAreas.setPreferredSize(new Dimension(16, 16));
		toolBarAreas.add(buttonSuperAreas);
		
		separator = new JSeparator();
		separator.setMaximumSize(new Dimension(6, 32767));
		separator.setOrientation(SwingConstants.VERTICAL);
		toolBarAreas.add(separator);
		
		buttonHoldListType = new JToggleButton("org.multipage.generator.textHoldListType");
		buttonHoldListType.setHorizontalAlignment(SwingConstants.LEFT);
		toolBarAreas.add(buttonHoldListType);
		
		horizontalStrut = Box.createHorizontalStrut(20);
		toolBarAreas.add(horizontalStrut);
		
		labelAreaListDescription = new JLabel("");
		labelAreaListDescription.setFont(new Font("Tahoma", Font.BOLD, 11));
		toolBarAreas.add(labelAreaListDescription);
		
		scrollPaneFavorites = new JScrollPane();
		panelFavorites.add(scrollPaneFavorites);
		splitPaneTree.setRightComponent(panelFavorites);
		
		listFavorites = new JList<>();
		listFavorites.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				
				if (e.getValueIsAdjusting()) {
					return;
				}
				Safe.tryOnChange(AreaDiagramContainerPanel.this, () -> {
					onSelectFavorite();
				});
			}
		});
		listFavorites.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					Safe.tryOnChange(AreaDiagramContainerPanel.this, () -> {
						onEscapeKey();
					});
				}
			}
		});
		listFavorites.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				if (e.getButton() == MouseEvent.BUTTON1) {
					Safe.tryOnChange(AreaDiagramContainerPanel.this, () -> {
						int clicks = e.getClickCount();
						if (clicks == 2) {
							onDoubleClickFavorite();
						}
					});
				}
			}
		});
		scrollPaneFavorites.setViewportView(listFavorites);
		
		popupMenuFavorites = new JPopupMenu();
		addPopup(listFavorites, popupMenuFavorites);
		
		menuDeleteFavorites = new JMenuItem("org.multipage.generator.menuDeleteFavorites");
		menuDeleteFavorites.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteFavorites();
			}
		});
		popupMenuFavorites.add(menuDeleteFavorites);
		popupMenuFavorites.addSeparator();
		
		labelFavorites = new JLabel("org.multipage.generator.textFavoritesLabel");
		scrollPaneFavorites.setColumnHeaderView(labelFavorites);
		
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(35, 10));
		panelFavorites.add(panel, BorderLayout.EAST);
		SpringLayout sl_panel = new SpringLayout();
		panel.setLayout(sl_panel);
		
		buttonUp = new JButton("");
		buttonUp.setToolTipText("org.multipage.generator.tooltipMoveFavoriteUp");
		buttonUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveFavoriteUp();
			}
		});
		buttonUp.setPreferredSize(new Dimension(25, 25));
		sl_panel.putConstraint(SpringLayout.NORTH, buttonUp, 5, SpringLayout.NORTH, panel);
		sl_panel.putConstraint(SpringLayout.WEST, buttonUp, 5, SpringLayout.WEST, panel);
		panel.add(buttonUp);
		
		buttonDown = new JButton("");
		buttonDown.setToolTipText("org.multipage.generator.tooltipMoveFavoriteDown");
		buttonDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveFavoriteDown();
			}
		});
		buttonDown.setPreferredSize(new Dimension(25, 25));
		sl_panel.putConstraint(SpringLayout.NORTH, buttonDown, 6, SpringLayout.SOUTH, buttonUp);
		sl_panel.putConstraint(SpringLayout.WEST, buttonDown, 0, SpringLayout.WEST, buttonUp);
		panel.add(buttonDown);
	}

	/**
	 * On click on favorite.
	 */
	protected void onSelectFavorite() {
		try {
			
			Safe.tryUpdate(this, () -> {
				listAreas.clearSelection();
			});
			
			// Set new area selection.
			Object value = listFavorites.getSelectedValue();
			if (value instanceof Long) {
				Long areaId = (Long) value;
				
				HashSet<Long> selectedAreaIds = new HashSet<>();
				selectedAreaIds.add(areaId);
				
				ApplicationEvents.transmit(this, GuiSignal.displayAreaProperties, selectedAreaIds);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On double click on favorite.
	 */
	protected void onDoubleClickFavorite() {
		try {
			
			// Get selected areas.
			List<Long> selectedAreaIds = listFavorites.getSelectedValuesList();
			if (selectedAreaIds.size() != 1) {
				Utility.show(this, "org.multipage.generator.messageSelectSingleArea");
				return;
			}
			
			Long selectedAreaId = selectedAreaIds.get(0);
			if (selectedAreaId != null) {
				
				// Focus area.
				focusAreaNear(selectedAreaId);
				areaDiagramPanel.selectArea(selectedAreaId);
				
				displayRelatedAreas(selectedAreaId);
	
				HashSet<Long> selectedAreaIdsSet = new HashSet<>();
				selectedAreaIdsSet.add(selectedAreaId);
				
				// Display area properties.
				ApplicationEvents.transmit(this, GuiSignal.displayAreaProperties, selectedAreaIdsSet);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On select area.
	 */
	protected void onSelectArea() {
		try {
			
			// Clear selection in favorites.
			Safe.tryUpdate(AreaDiagramContainerPanel.this, () -> {
				listFavorites.clearSelection();
			});
			
			// Set new area selection.
			HashSet<Long> areaIds = null; 
			Object value = listAreas.getSelectedValue();
			if (value instanceof Area) {
				Area area = (Area) value;
				
				areaIds = new HashSet<>();
				areaIds.add(area.getId());
			}
			ApplicationEvents.transmit(this, GuiSignal.displayAreaProperties, areaIds);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * On double click area.
	 */
	protected void onDoubleClickArea() {
		try {
			
			// Get selected areas.
			List<Area> selectedAreas = listAreas.getSelectedValuesList();
			if (selectedAreas.size() != 1) {
				Utility.show(this, "org.multipage.generator.messageSelectSingleArea");
				return;
			}
			
			areaSelection = selectedAreas.get(0);
			long areaId = areaSelection.getId();
			
			// Repaint GUI.
			repaint();
			
			// Focus area and select it.
			focusAreaNear(areaId);
			areaDiagramPanel.selectArea(areaId);
			
			displayRelatedAreas(areaId);
	
			HashSet<Long> selectedAreaIds = new HashSet<Long>();
			selectedAreaIds.add(areaId);
			
			// Display area properties.
			ApplicationEvents.transmit(this, GuiSignal.displayAreaProperties, selectedAreaIds);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * On escape key pressed.
	 */
	protected void onEscapeKey() {
		try {
			
			Safe.tryUpdate(AreaDiagramContainerPanel.this, () -> {
				listAreas.clearSelection();
				listFavorites.clearSelection();
			});
				
			updateComponents();
			
			HashSet<Long> areaIds = areaDiagramPanel.getSelectedTabAreaIds();
			ApplicationEvents.transmit(this, GuiSignal.displayAreaProperties, areaIds);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Called when the diagram area selection has changed.
	 */
	protected void onDiagramSelectionChanged() {
		try {
			// Display area properties.
			HashSet<Long> areaIds = areaDiagramPanel.getSelectedTabAreaIds();
			displayRelatedAreasForSet(areaIds);
			ApplicationEvents.transmit(this, GuiSignal.displayAreaProperties, areaIds);
			updateComponents();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Post creation.
	 */
	private void postCreation() {
		try {
			
			loadDialog();
			localize();
			setIcons();
			setToolTips();
			setAreasDiagram();
			setTree();
			setFavorites();
			setListeners();
			createPopupMenus();
			createAreasList();
			
			// Register this panel for update.
			GeneratorMainFrame.registerForUpdate(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set tool tips.
	 */
	private void setToolTips() {
		try {
			
			buttonSiblings.setToolTipText(Resources.getString("org.multipage.generator.tooltipAreaSiblings"));
			buttonSubAreas.setToolTipText(Resources.getString("org.multipage.generator.tooltipAreaSubAreas"));
			buttonSuperAreas.setToolTipText(Resources.getString("org.multipage.generator.tooltipAreaSuperAreas"));
			buttonHoldListType.setToolTipText(Resources.getString("org.multipage.generator.tooltipHoldAreasListType"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create popup menus.
	 */
	private void createPopupMenus() {
		try {
			
			// Favorites popup.
			final Component thisComponent = this;
			
			AreaLocalMenu areaLocalMenuFavorites = ProgramGenerator.newAreaLocalMenu(new AreaLocalMenu.Callbacks() {
				
				@Override
				public Component getComponent() {
					// Get this component.
					return thisComponent;
				}
				
				@Override
				public AreaDiagramContainerPanel getAreaDiagramEditor() {
					return AreaDiagramContainerPanel.this;
				}
				
				@Override
				protected Area getCurrentArea() {
					try {
						// Get selected items.
						List<Long> selectedAreaIds = listFavorites.getSelectedValuesList();
						if (selectedAreaIds.size() != 1) {
							return null;
						}
						Long selectedAreaId = selectedAreaIds.get(0);
						return ProgramGenerator.getAreasModel().getArea(selectedAreaId);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return null;
				}
				
				@Override
				protected void selectEditorArea(Long areaId, boolean reset, boolean affectSubareas) {
					try {
						select(areaId, reset, affectSubareas);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
				}

				@Override
				protected void clearEditorAreaSelection() {
					try {
						clearEditorSelection();
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
				}
			});
			
			areaLocalMenuFavorites.isAddFavorites = false;
			areaLocalMenuFavorites.addTo(this, popupMenuFavorites);
			
			AreaLocalMenu areaLocalMenuAreas = ProgramGenerator.newAreaLocalMenu(new AreaLocalMenu.Callbacks() {
				
				@Override
				public Component getComponent() {
					// Get this component.
					return thisComponent;
				}
				
				@Override
				public AreaDiagramContainerPanel getAreaDiagramEditor() {
					return AreaDiagramContainerPanel.this;
				}
				
				@Override
				protected Area getCurrentArea() {
					try {
						// Get selected item.
						return (Area) listAreas.getSelectedValue();
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return null;
				}
				
				@Override
				protected List<Area> getCurrentAreas() {
					try {
						// Return current selected areas.
						List<Area> areas = listAreas.getSelectedValuesList();
						return areas;
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return null;
				}

				@Override
				protected Area getCurrentParentArea() {
					try {
						// Return parent area of the current selected area.
						List<Area> areas = listAreas.getSelectedValuesList();
						HashSet<Area> superAreas = new HashSet<Area>();
						for (Area area : areas) {
                            superAreas.addAll(area.getSuperareas());
                        }
						
						if (superAreas.size() == 1) {
							return superAreas.iterator().next();
						}
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return null;
				}

				@Override
				protected void selectEditorArea(Long areaId, boolean reset, boolean affectSubareas) {
					try {
						select(areaId, reset, affectSubareas);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
				}
				
				@Override
				protected void clearEditorAreaSelection() {
					try {
						clearEditorSelection();
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
				}
			});
			
			areaLocalMenuAreas.addTo(this, popupMenuAreas);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set areas diagram.
	 */
	private void setAreasDiagram() {
		try {
			
			// Create new areas diagram.
			areaDiagramPanel = ProgramGenerator.newAreasDiagram(this);
			panelForDiagram.add(areaDiagramPanel);
			
			areaDiagramPanel.setCallbacks(new Callbacks() {
				@Override
				public void onSelectionChanged() {
					AreaDiagramContainerPanel.this.onDiagramSelectionChanged();
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set icons.
	 */
	private void setIcons() {
		try {
			
			menuDeleteFavorites.setIcon(Images.getIcon("org/multipage/generator/images/remove_icon.png"));
			buttonUp.setIcon(Images.getIcon("org/multipage/generator/images/up.png"));
			buttonDown.setIcon(Images.getIcon("org/multipage/generator/images/down.png"));
			buttonSiblings.setIcon(Images.getIcon("org/multipage/generator/images/siblings_small.png"));
			buttonSubAreas.setIcon(Images.getIcon("org/multipage/generator/images/subareas_small.png"));
			buttonSuperAreas.setIcon(Images.getIcon("org/multipage/generator/images/superareas_small.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Localize components.
	 */
	private void localize() {
		try {
			
			Utility.localize(menuDeleteFavorites);
			Utility.localizeTooltip(buttonUp);
			Utility.localizeTooltip(buttonDown);
			Utility.localize(labelFavorites);
			Utility.localize(buttonHoldListType);
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
			
			// "Select all" event receiver.
			ApplicationEvents.receiver(this, GuiSignal.selectAll, action -> {
				try {
			
					if (AreaDiagramContainerPanel.this.isShowing()) {
						
						HashSet<Long> selectedAreaIds = ProgramGenerator.getAllAreaIds();
						displayRelatedAreasForSet(selectedAreaIds);
						Safe.invokeLater(() -> {
							displayAreaProperties();
						});
					}
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// "Unselect all" event receiver.
			ApplicationEvents.receiver(this, GuiSignal.unselectAll, action -> {
				try {
			
					if (AreaDiagramContainerPanel.this.isShowing()) {
						
						HashSet<Long> selectedAreaIds = new HashSet<Long>();
						displayRelatedAreasForSet(selectedAreaIds);
						Safe.invokeLater(() -> {
							displayAreaProperties();
						});
					}
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// Add receiver for the "show or hide" event.
			ApplicationEvents.receiver(this, GuiSignal.showOrHideIds, message -> {
				try {
			
					// Reload and repaint the GUI.
					reload();
					repaint();
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// "Focus home area" event receiver.
			ApplicationEvents.receiver(this, GuiSignal.focusHomeArea, action -> {
				try {
			
					if (AreaDiagramContainerPanel.this.isShowing()) {
						long homeAreaId = focusHomeArea();
						areaDiagramPanel.selectArea(homeAreaId);
						displayRelatedAreas(homeAreaId);
						Safe.invokeLater(() -> {
							displayAreaProperties();
						});
					}
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// "Focus home area" event receiver.
			ApplicationEvents.receiver(this, GuiSignal.focusTopArea, action -> {
				try {
			
					if (AreaDiagramContainerPanel.this.isShowing()) {
						focusTopArea();
						areaDiagramPanel.selectArea(topAreaId);
						displayRelatedAreas(topAreaId);
						Safe.invokeLater(() -> {
							displayAreaProperties();
						});
					}
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
			
			// Add focus event receiver.
			ApplicationEvents.receiver(this, GuiSignal.focusBasicArea, message -> {
				try {
			
					// Focus currently visible Basic Area.
					if (areaDiagramPanel.isShowing()) {
						areaDiagramPanel.focusBasicArea();
						displayRelatedAreas(0L);
						Safe.invokeLater(() -> {
							displayAreaProperties();
						});
					}
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Remove listeners.
	 * @return
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
	 * Dispose.
	 */
	public void dispose() {
		try {
			
			// Unregister from updatable components.
			GeneratorMainFrame.unregisterFromUpdate(this);
			
			saveDialog();
			removeListeners();
			areaDiagramPanel.dispose();
			areaDiagramPanel.removeDiagram();
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
			
			areaDiagramPanel.init();
			loadFavorites();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Initialize diagram editor.
	 * @param currentAreasEditor 
	 */
	public void initDiagramEditor(AreaDiagramContainerPanel currentAreasEditor) {
		try {
			
			splitPane.setDividerLocation(currentAreasEditor.splitPane.getDividerLocation());
			splitPaneTree.setDividerLocation(currentAreasEditor.splitPaneTree.getDividerLocation());
			
			areaDiagramPanel.init();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load favorites.
	 */
	private void loadFavorites() {
		try {
			
			favoritesModel.setAreasIds(selectedAreasIdsState);
			favoritesModel.update();
			listFavorites.updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Save favorites.
	 */
	private void saveFavorites() {
		try {
			
			selectedAreasIdsState = favoritesModel.getAreasIds();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get diagram.
	 * @return
	 */
	public AreaDiagramPanel getDiagram() {
		
		return areaDiagramPanel;
	}
	
	/**
	 * Get selected areas.
	 * @return
	 */
	public LinkedList<Area> getSelectedAreas() {
		
		try {
			return areaDiagramPanel.getSelectedAreas();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Get selected and enabled areas.
	 * @return
	 */
	public LinkedList<Area> getSelectedAndEnabledAreas() {
		
		try {
			LinkedList<Area> areas = areaDiagramPanel.getSelectedAreas();
			LinkedList<Area> resultAreas = new LinkedList<Area>();
			
			for (Area area : areas) {
				if (area.isEnabled()) {
					resultAreas.add(area);
				}
			}
			return resultAreas;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Set tree.
	 */
	private void setTree() {

	}

	/**
	 * Add popup trayMenu.
	 * @param component
	 * @param popup
	 */
	private static void addPopup(Component component, final JPopupMenu popup) {
		try {
			
			component.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					try {
			
						if (e.isPopupTrigger()) {
							showMenu(e);
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				public void mouseReleased(MouseEvent e) {
					try {
			
						if (e.isPopupTrigger()) {
							showMenu(e);
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				private void showMenu(MouseEvent e) {
					try {
			
						popup.show(e.getComponent(), e.getX(), e.getY());
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Add favorite area.
	 * @param area
	 */
	public void addFavorite(Area area) {
		try {
			
			favoritesModel.addNew(area.getId());
			listFavorites.updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set favorites.
	 */
	private void setFavorites() {
		try {
			
			// Create and set model.
			favoritesModel = new FavoritesModel(this);
			listFavorites.setModel(favoritesModel);
			// Create and set renderer.
			favoritesRenderer = new FavoritesRenderer(this);
			listFavorites.setCellRenderer(favoritesRenderer);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Delete favorites.
	 */
	protected void deleteFavorites() {
		try {
			
			// Get selected areas.
			List<Long> selectedAreaIds = listFavorites.getSelectedValuesList();
			if (selectedAreaIds.size() < 1) {
				Utility.show(this, "org.multipage.generator.messageSelectFavorites");
				return;
			}
			
			// Remove selected areas.
			for (Long selectedAreaId : selectedAreaIds) {
				if (selectedAreaId != null) {
					favoritesModel.removeArea(selectedAreaId);
				}
			}
			
			listFavorites.updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Focus area.
	 * @param areaProperty
	 */
	public void focusArea(long areaId) {
		try {
			
			// Get area object.
			Area area = ProgramGenerator.getAreasModel().getArea(areaId);
			if (area == null) {
				return;
			}
			
			// Get area shapes.
			Object userObject = area.getUser();
			if (!(userObject instanceof AreaShapes)) {
				return;
			}
			
			if (!area.equals(lastFocusedArea)) {
				focusedAreaIndex = 0;
			}
			
			AreaShapes shapes = (AreaShapes) userObject;
			LinkedList<AreaCoordinates> coordinatesList = shapes.getCoordinates();
			if (focusedAreaIndex >= coordinatesList.size()) {
				focusedAreaIndex = 0;
			}
			
			// Focus on area shape.
			AreaCoordinates coordinates = coordinatesList.get(focusedAreaIndex);
			areaDiagramPanel.focus(coordinates, area);
			
			lastFocusedArea = area;
			focusedAreaIndex++;	
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Focus global area.
	 */
	public void focusGlobalArea() {
		try {
			
			focusArea(0L);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Focus near area.
	 * @param areaId
	 */
	public void focusAreaNear(long areaId) {
		try {
			
			// Get area object.
			Area area = ProgramGenerator.getAreasModel().getArea(areaId);
			if (area == null) {
				return;
			}
			
			// Get diagram coordinates.
			Rectangle2D diagramRectangle = areaDiagramPanel.getRectInCoord();
			
			// Get area shapes.
			Object userObject = area.getUser();
			if (!(userObject instanceof AreaShapes)) {
				return;
			}
			AreaShapes shapes = (AreaShapes) userObject;
			LinkedList<AreaCoordinates> coordinatesList = shapes.getCoordinates();
			if (coordinatesList.isEmpty()) {
				return;
			}
	
			// Find nearest area coordinates.
			double maximumIntersectedWidth = -1.0;
			double maximumAngle = -1.0;
			
			double diagramHeight = diagramRectangle.getHeight();
			Point2D diagramCenter = new Point2D.Double(diagramRectangle.getCenterX(), diagramRectangle.getCenterY());
			
			LinkedList<AreaCoordinates> largestIntersected = new LinkedList<AreaCoordinates>();
			LinkedList<AreaCoordinates> closerNotIntersected = new LinkedList<AreaCoordinates>();
			
			// Find largest intersected coordinates and all near not intersected coordinates.
			for (AreaCoordinates coordinates : coordinatesList) {
				
				Rectangle2D areaRectangle = coordinates.getRectangle();
				if (Utility.isIntersection(areaRectangle, diagramRectangle)) {
					
					if (coordinates.getWidth() > maximumIntersectedWidth) {
						maximumIntersectedWidth = coordinates.getWidth();
						
						largestIntersected.clear();
						largestIntersected.add(coordinates);
					}
					else if (coordinates.getWidth() == maximumIntersectedWidth) {
						largestIntersected.add(coordinates);
					}
				}
				else {
					
					// Compute distance.
					Point2D shapeCenter = coordinates.getCenter();
					
					double distance = shapeCenter.distance(diagramCenter);
					double angle = distance == 0.0 ? Math.PI / 2.0 : Math.atan(diagramHeight / distance);
					
					if (angle > maximumAngle) {
						maximumAngle = angle;
						
						closerNotIntersected.clear();
						closerNotIntersected.add(coordinates);
					}
					else if (angle == maximumAngle) {
						closerNotIntersected.add(coordinates);
					}
				}
			}
			
			// If single intersected coordinates exist, focus it.
			if (largestIntersected.size() == 1) {
				
				AreaCoordinates coordinates = largestIntersected.getFirst();
				areaDiagramPanel.focus(coordinates, area);
				return;
			}
			
			// If exist intersected coordinates, get coordinates near the diagram top left corner.
			if (!largestIntersected.isEmpty()) {
				
				Point2D diagramLeftTop = new Point2D.Double(diagramRectangle.getX(), diagramRectangle.getY());
				
				double maximumDistance = -1.0;
				AreaCoordinates nearestCoordinates = null;
				
				for (AreaCoordinates coordinates : largestIntersected) {
					
					// Get coordinates center.
					Point2D shapeCenter = coordinates.getCenter();
					double distance = shapeCenter.distance(diagramLeftTop);
					
					// Update maximum value.
					if (maximumDistance > distance) {
						maximumDistance = distance;
						nearestCoordinates = coordinates;
					}
				}
				
				if (nearestCoordinates != null) {
					areaDiagramPanel.focus(nearestCoordinates, area);
					return;
				}
				
				// ... just to be sure.
				areaDiagramPanel.focus(largestIntersected.getFirst(), area);
				return;
			}
			
			// Get first closer not intersected coordinates.
			double maximumDistance = -1.0;
			AreaCoordinates closerCoordinates = null;
			
			for (AreaCoordinates coordinates : closerNotIntersected) {
				
				// Compute distance.
				Point2D shapeCenter = coordinates.getCenter();
				double distance = shapeCenter.distance(diagramCenter);
				
				if (distance > maximumDistance) {
					
					maximumDistance = distance;
					closerCoordinates = coordinates;
				}
			}
			
			// Focus area.
			if (closerCoordinates != null) {
				areaDiagramPanel.focus(closerCoordinates, area);
				return;
			}
			
			// For sure ...
			if (!closerNotIntersected.isEmpty()) {
				areaDiagramPanel.focus(closerNotIntersected.getFirst(), area);
				return;
			}
			areaDiagramPanel.focus(coordinatesList.getFirst(), area);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Focus tab top area.
	 */
	public long focusTopArea() {
		try {
			
			Long tabAreaId = GeneratorMainFrame.getTabAreaId();
			if (tabAreaId == null) {
				return tabAreaId;
			}
			focusAreaNear(tabAreaId);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return -1L;
	}
	
	/**
	 * Move selected favorites up.
	 */
	protected void moveFavoriteUp() {
		try {
			
			// Get selected favorites.
			List<Long> selectedAreaIds = listFavorites.getSelectedValuesList();
			if (selectedAreaIds.size() != 1) {
				Utility.show(this, "org.multipage.generator.messageSelectSingleFavoriteArea");
				return;
			}
			
			// Get favorite area and move it up.
			Long selectedAreaId = selectedAreaIds.get(0);
			if (selectedAreaId != null) {
				// Move selected object up.
				favoritesModel.moveUp(selectedAreaId, listFavorites);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Move selected favorites down.
	 */
	protected void moveFavoriteDown() {
		try {
			
			// Get selected favorites.
			List<Long> selectedAreaIds = listFavorites.getSelectedValuesList();
			if (selectedAreaIds.size() != 1) {
				Utility.show(this, "org.multipage.generator.messageSelectSingleFavoriteArea");
				return;
			}
			
			// Get favorite area and move it up.
			Long selectedAreaId = selectedAreaIds.get(0);
			if (selectedAreaId != null) {
				// Move selected object down.
				favoritesModel.moveDown(selectedAreaId, listFavorites);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Focus on home area.
	 */
	public long focusHomeArea() {
		
		try {
			AreasModel model = ProgramGenerator.getAreasModel();
			
			long homeAreaId = model.getHomeAreaId();
			focusArea(homeAreaId);
			
			return homeAreaId;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return -1L;
	}

	/**
	 * Select area.
	 * @param areaId
	 * @param reset
	 * @param affectSubareas
	 */
	public void select(long areaId, boolean reset, boolean affectSubareas) {
		try {
			
			if (reset) {
				areaDiagramPanel.clearDiagramSelection();
			}
			areaDiagramPanel.select(areaId, true, affectSubareas);
			areaDiagramPanel.updateComponents();
			updateComponents();
			
			HashSet<Long> areaIds = areaDiagramPanel.getSelectedTabAreaIds();
			ApplicationEvents.transmit(this, GuiSignal.displayAreaProperties, areaIds);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Clear area selection.
	 */
	protected void clearEditorSelection() {
		try {
			
			areaDiagramPanel.clearDiagramSelection();
			areaDiagramPanel.updateComponents();
			updateComponents();
			
			ApplicationEvents.transmit(this, GuiSignal.displayAreaProperties);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Display related areas.
	 * @param areaIds
	 */
	private void displayRelatedAreasForSet(HashSet<Long> areaIds) {
		try {
			
			if (areaIds.size() != 1) {
				displayRelatedAreas(null);
				return;
			}
			
			Long areaId = areaIds.iterator().next();
			displayRelatedAreas(areaId);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Display related areas.
	 */
	private void displayRelatedAreas(Long areaId) {
		try {
			
			if (areaId != null) {
				areaSelection = ProgramGenerator.getArea(areaId);
			}
			else {
				areaSelection = null;
			}
			
			if (!buttonHoldListType.isSelected()) {
				
				// On loose list type.
				resetAreaListButtonsAndDescription();
				loadAreaSiblings();
				return;
			}
	
			// On hold list type.
			if (buttonSiblings.isSelected()) {
				
				resetAreaListButtonsAndDescription();
				loadAreaSiblings();
			}
			else if (buttonSubAreas.isSelected()) {
				
				resetAreaListButtonsAndDescription();
				loadAreaSubAreas();
			}
			else if (buttonSuperAreas.isSelected()) {
				
				resetAreaListButtonsAndDescription();
				loadAreaSuperAreas();
			}
			else {
				resetAreaListButtonsAndDescription();
				loadAreaSiblings();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create areas list.
	 */
	private void createAreasList() {
		try {
			
			listAreasModel = new DefaultListModel<Area>();
			listAreas.setCellRenderer((ListCellRenderer<? super Area>) newListSiblingsRenderer());
			listAreas.setModel(listAreasModel);
			
			resetAreaListButtonsAndDescription();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load siblings.
	 */
	private void loadAreaSiblings() {
		Safe.tryUpdate(this, () -> {
			
			// Remember current selection.
			int [] selectedIndices = listAreas.getSelectedIndices();
			
			buttonSiblings.setSelected(true);
	
			listAreasModel.clear();
			
			if (areaSelection == null) {
				return;
			}
	
			// Get area shapes.
			Object userObject = areaSelection.getUser();
			if (!(userObject instanceof AreaShapes)) {
				return;
			}
			
			AreaShapes shapes = (AreaShapes) userObject;
			// Get parent area.
			Rectangle2D diagramRect = areaDiagramPanel.getRectInCoord();
			Area superArea = shapes.getVisibleParent(diagramRect);
			
			if (superArea != null) {
				
				// Add siblings list.
				int selectedIndex = 0;
				int index = 0;
				
				LinkedList<Area> siblings = superArea.getSubareas();
				
				for (Area subArea : siblings) {
					
					if (superArea.isSubareasHidden(subArea)) {
						continue;
					}				
					listAreasModel.addElement(subArea);
					
					if (subArea.equals(areaSelection)) {
						selectedIndex = index;
					}
					index++;
				}
				
				int siblingsCount = siblings.size();
				
				// Show smartly the selected area.
				if (selectedIndex - 1 > 0) {
					listAreas.ensureIndexIsVisible(selectedIndex - 1);
				}
				
				if (selectedIndex + 1 < siblingsCount) {
					listAreas.ensureIndexIsVisible(selectedIndex + 1);
				}
				
				listAreas.ensureIndexIsVisible(selectedIndex);
			}
			else {
				// Load global area.
				listAreasModel.addElement(areaSelection);
			}
			
			// Set description.
			labelAreaListDescription.setText(Resources.getString("org.multipage.generator.textAreaSiblings"));
			
			// Restore selection.
			Safe.tryUpdate(AreaDiagramContainerPanel.this, () -> {
				listAreas.setSelectedIndices(selectedIndices);
			});
		});
	}

	/**
	 * Load sub areas.
	 */
	private void loadAreaSubAreas() {
		Safe.tryUpdate(this, () -> {
			
			buttonSubAreas.setSelected(true);
			buttonHoldListType.setEnabled(true);
	
			listAreasModel.clear();
			
			if (areaSelection == null) {
				return;
			}
			
			for (Area area : areaSelection.getSubareas()) {
				
				if (areaSelection.isSubareasHidden(area)) {
					continue;
				}			
				listAreasModel.addElement(area);
			}
			
			// Set description.
			labelAreaListDescription.setText(Resources.getString("org.multipage.generator.textAreaSubAreas"));
		});
	}

	/**
	 * Load super areas.
	 */
	private void loadAreaSuperAreas() {
		Safe.tryUpdate(this, () -> {
			
			buttonSuperAreas.setSelected(true);
			buttonHoldListType.setEnabled(true);
			
			listAreasModel.clear();
			
			if (areaSelection == null) {
				return;
			}
			
			for (Area area : areaSelection.getSuperareas()) {
				
				if (area.isSubareasHidden(areaSelection)) {
					continue;
				}			
				listAreasModel.addElement(area);
			}
			
			// Set description.
			labelAreaListDescription.setText(Resources.getString("org.multipage.generator.textAreaSuperAreas"));
		});
	}
	
	/**
	 * Reset area list buttons and description.
	 */
	private void resetAreaListButtonsAndDescription() {
		try {
			
			// Reset buttons.
			buttonSiblings.setSelected(false);
			buttonSubAreas.setSelected(false);
			buttonSuperAreas.setSelected(false);
			
			buttonHoldListType.setEnabled(false);
			
			labelAreaListDescription.setText("");
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On area list button.
	 * @param event
	 */
	protected void onAreaListButton(ActionEvent event) {
		try {
			
			// Reset buttons.
			resetAreaListButtonsAndDescription();
	
			// Get event source.
			Object source = event.getSource();
			if (!(source instanceof JToggleButton)) {
				return;
			}
			
			JToggleButton sourceButton = (JToggleButton) source;
	
			// Do appropriate action.
			if (sourceButton.equals(buttonSiblings)) {
				loadAreaSiblings();
				return;
			}
			if (sourceButton.equals(buttonSubAreas)) {
				loadAreaSubAreas();
				return;
			}
			if (sourceButton.equals(buttonSuperAreas)) {
				loadAreaSuperAreas();
				return;
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create new siblings renderer.
	 * @return
	 */
	@SuppressWarnings("serial")
	private ListCellRenderer<? super Area> newListSiblingsRenderer() {
		
		// Inner class for renderer.
		class Renderer extends JLabel {
			// Fields.
			private boolean isSelected;
			private boolean hasFocus;
			// Constructor.
			public Renderer() {
				try {
			
					setIcon(Images.getIcon("org/multipage/generator/images/area_node.png"));
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			}
			// Set label.
			public void set(Area area, boolean isSelected, boolean hasFocus) {
				try {
			
					this.isSelected = isSelected;
					this.hasFocus = hasFocus;
					setText(area.getDescriptionForDiagram());
					setForeground(area.equals(areaSelection) ? Color.red : Color.black);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			}
			// Reset label.
			public void reset() {
				try {
			
					isSelected = hasFocus = false;
					setText("");
					setForeground(Color.black);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			}
			// Paint label.
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
		}
		
		// Create and return renderer.
		return new DefaultListCellRenderer() {
			Renderer renderer = new Renderer();
			@Override
			public Component getListCellRendererComponent(JList<?> list,
					Object value, int index, boolean isSelected, boolean cellHasFocus) {
				
				try {
					if (value instanceof Area) {
						renderer.set((Area) value, isSelected, cellHasFocus);
					}
					else {
						renderer.reset();
					}
				}
				catch (Throwable e) {
					Safe.exception(e);
				}
				return renderer;
			}
		};
	}

	/**
	 * Focus start area.
	 * @param areaId
	 * @param versionId 
	 */
	public void focusStartArea(long areaId, long versionId) {
		try {
			
			AreasModel model = ProgramGenerator.getAreasModel();
			Area area = model.getArea(areaId);
			VersionObj version = model.getVersion(versionId);
			
			// Check parameters.
			if (area == null || version == null) {
				Utility.show(this, "org.multipage.generator.textFocusStartAreaBadParameter");
				return;
			}
			
			Area startArea = model.getStartArea(area, versionId);
			
			// If not found, inform user.
			if (startArea == null) {
				Utility.show(this, "org.multipage.generator.messageStartAreaNotFound", area.toString(), version.toString());
				return;
			}
			
			// Focus start area.
			focusAreaNear(startArea.getId());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Copy area tree.
	 */
	public void copyAreaTree() {
		
	}
	
	/**
	 * On tab panel change event.
	 */
	@Override
	public void onTabPanelChange(ChangeEvent e, int selectedTabIndex) {
		try {
			
			// Call this method for diagram panel.
			areaDiagramPanel.onTabPanelChange(e, selectedTabIndex);
			
			HashSet<Long> selectedAreaIds = getSelectedTabAreaIds();
			
			// Open selected area properties.
			ApplicationEvents.transmit(this, GuiSignal.displayAreaProperties, selectedAreaIds);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Before tab panel removed.
	 */
	@Override
	public void beforeTabPanelRemoved() {
		try {
			
			// Call the same method for diagram.
			areaDiagramPanel.beforeTabPanelRemoved();
			
			// Remove listeners.
			removeListeners();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get selected area IDs.
	 * @return
	 */
	public HashSet<Long> getSelectedTabAreaIds() {
		
		try {
			List<Area> areas = listAreas.getSelectedValuesList();
			if (areas != null && !areas.isEmpty()) {
				HashSet<Long> areaIds = new HashSet<>();
				areas.forEach(area -> areaIds.add(area.getId()));
				return areaIds;
			}
			List<Long> areaIdList = listFavorites.getSelectedValuesList();
			if (areaIdList != null && !areaIdList.isEmpty()) {
				HashSet<Long> areaIds= new HashSet<>(areaIdList);
				return areaIds;
			}
			return areaDiagramPanel.getSelectedTabAreaIds();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * No tab text.
	 */
	@Override
	public String getTabDescription() {
		
		return "";
	}
	
	/**
	 * Update panel.
	 */
	@Override
	public void reload() {
		
		// Get selected area IDs.
		HashSet<Long> selectedAreaIds = getSelectedTabAreaIds();
        Long areaId = null;
        if (selectedAreaIds != null && selectedAreaIds.size() == 1) {
        	areaId = selectedAreaIds.iterator().next();
        }
        
        // Update diagram.
		displayRelatedAreas(areaId);
	}
	
	/**
	 * Display panel with selected area properties.
	 */
	private void displayAreaProperties() {
		try {
			
			if (isShowing()) {
				// Display area properties.
				HashSet<Long> selectedAreaIds = getSelectedTabAreaIds();
				ApplicationEvents.transmit(this, GuiSignal.displayAreaProperties, selectedAreaIds);
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Get tab state
	 */
	@Override
	public TabState getTabState() {
		
		try {
			// Try to get inner area diagram, set and return the state object.
			AreaDiagramPanel areasDiagram = this.getDiagram();
			if (areasDiagram == null) {
				return null;
			}
			TabState tabState = areasDiagram.getTabState();
			
			// Set title and return the state object
			tabState.title = tabLabel.getDescription();
			return tabState;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Set reference to a tab label
	 */
	@Override
	public void setTabLabel(TabLabel tabLabel) {
		
		this.tabLabel = tabLabel;
	}
	
	/**
	 * Set top area ID
	 */
	@Override
	public void setAreaId(Long topAreaId) {
		try {
			
			this.topAreaId = topAreaId;
			
			// Try to get inner area diagram and set top area ID.
			AreaDiagramPanel areasDiagram = this.getDiagram();
			if (areasDiagram == null) {
				return;
			}
			
			areasDiagram.setAreaId(topAreaId);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Check if input area shape is selected.
	 * @param shapes
	 * @return
	 */
	public boolean isShapeSelected(AreaShapes shapes) {
		try {
			
			if (areaDiagramPanel == null) {
				return false;
			}
			return areaDiagramPanel.isShapeSelected(shapes);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * On closing the panel.
	 */
	@Override
	public void close() {
		
	}

	@Override
	public void recreateContent() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Called when the panel is updated with update manager.
	 */
	@Override
	public void updateComponents() {
		try {
			
			// Reload and repaint the GUI.
			reload();
			repaint();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
