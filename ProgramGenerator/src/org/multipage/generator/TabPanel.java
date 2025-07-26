/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.multipage.gui.DnDTabbedPane;
import org.multipage.gui.Utility;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Tab panel.
 * @author vakol
 *
 */
public class TabPanel extends DnDTabbedPane {
	
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Component reference.
	 */
	private Component component;
	
	/**
	 * Listener.
	 */
	private Runnable removeListener;

	/**
	 * Constructor.
	 */
	public TabPanel(JPanel areasPanel) {
		try {
			
			setFirstDraggedIndex(1);
			
			String text = Resources.getString("org.multipage.generator.textMainAreasTab");
			add(areasPanel, text);
			setToolTipTextAt(0, text);
			final TabPanel thisObject = this;
			
			// Set listeners.
			addMouseMotionListener(new MouseAdapter() {
				@Override
				public void mouseMoved(MouseEvent e) {
					try {
						
						super.mouseMoved(e);
						// Set default cursor.
						setCursor(Cursor.getDefaultCursor());
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					
					Safe.tryOnChange(TabPanel.this, () -> {
						// Get event source.
						TabPanel tab = (TabPanel) e.getSource();
						int selectedIndex = tab.getSelectedIndex();
						
						// Invoke events.
						Component component = tab.getComponentAt(selectedIndex);
						if (component instanceof TabItemInterface) {
							
							TabItemInterface tabPanelActions = (TabItemInterface) component;
							tabPanelActions.onTabPanelChange(e, selectedIndex);
						}
						
						Safe.invokeLater(() -> {
							// Delegate state changed.
							stateChanged2();
						});
					});
				}
			});
			addContainerListener(new ContainerAdapter() {
				// When a diagram is removed.
				@Override
				public void componentRemoved(ContainerEvent e) {
					try {
						
						Component child = e.getChild();
						
						// If it is areas diagram editor, close it.
						if (child instanceof AreaDiagramContainerPanel) {
							AreaDiagramContainerPanel editor = (AreaDiagramContainerPanel) child;
							editor.dispose();
							return;	
						}
						
						// If it is monitor panel, close it.
						if (child instanceof MonitorPanel) {
							MonitorPanel monitor = (MonitorPanel) child;
							monitor.dispose();
							return;	
						}
						
						// If it is other diagram, close it.
						if (child instanceof GeneralDiagramPanel) {
							GeneralDiagramPanel diagram = (GeneralDiagramPanel) child;
							diagram.close();
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					try {
						
						if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
							
							// Get selected tab.
							int selectedIndex = getSelectedIndex();
							TabLabel tabComponent = (TabLabel) getTabComponentAt(selectedIndex);
							
							// Get text.
							String text = null;
							if (tabComponent != null) {
								text = tabComponent.getDescription();
							}
							else {
								text = getTitleAt(selectedIndex);
							}
							
							// Get new text.
							String newText = Utility.input(thisObject, "org.multipage.generator.messageInsertNewTabText", text);
							if (newText != null) {
								
								// Set new text.
								if (tabComponent != null) {
									tabComponent.setDescription(newText);
								}
								else {
									setTitleAt(selectedIndex, newText);
								}
							}
						}
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
	 * Adds areas editor.
	 * @param areasEditor
	 * @param title 
	 * @param topAreaId 
	 * @param selectIt
	 */
	public void addAreasEditor(Component areasEditor, TabType type, String title, Long topAreaId, boolean selectIt) {
		try {
			
			this.component = areasEditor;
			
			add(areasEditor);
			
			int index = getTabCount() - 1;
			
			String text = Resources.getString("org.multipage.generator.textAreasClone");
			if (title != null && !title.isEmpty()) {
				//text += '-' + title;
				text = title;
			}
			
			setTabComponentAt(index, new TabLabel(text, topAreaId, this, areasEditor, type));
			
			if (selectIt) {
				setSelectedIndex(index);
			}
			
			// Set tool tip.
			setToolTipTextAt(index, text);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Adds monitor panel.
	 * @param monitor
	 * @param title
	 * @param selectIt
	 */
	public void addMonitor(String url, boolean selectIt) {
		try {
			
			MonitorPanel monitor = new MonitorPanel(url);
			this.component = monitor;
			
			add(monitor);
			
			int index = getTabCount() - 1;
			
			setTabComponentAt(index, new TabLabel(url, -1L, this, monitor, TabType.monitor));
			
			if (selectIt) {
				setSelectedIndex(index);
			}
			
			// Set tool tip.
			setToolTipTextAt(index, url);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};	
	}
	
	/**
	 * Set tab title.
	 * @param index
	 * @param title
	 */
	public void setTabTitle(int index, String title) {
		try {
			
			Component component = getTabComponentAt(index);
			if (component instanceof TabLabel) {
				
				((TabLabel) component).setDescription(title);
			}
			else {
				setTitleAt(index, title);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get tab title.
	 * @param index
	 * @return
	 */
	public String getTabTitle(int index) {
		
		try {
			Component component = getTabComponentAt(index);
			if (component instanceof TabLabel) {
				
				return ((TabLabel) component).getDescription();
			}
			String title = getTitleAt(index);
			if (title != null) {
				return title;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Program state changed.
	 */
	public void stateChanged2() {
		try {
			
			int count = getTabCount();
	
			// Do for all tabs. Close tool tips.
			for (int index = 0; index  < count; index++) {
				Component comp = getComponentAt(index);
				if (comp instanceof GeneralDiagramPanel) {
					GeneralDiagramPanel.closeToolTip();
				}
			}
			// Get selected diagram.
			Component comp = getSelectedComponent();
			if (comp instanceof GeneralDiagramPanel) {
				GeneralDiagramPanel diagram = (GeneralDiagramPanel) comp;
				diagram.setActualStatusText();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Close all windows.
	 */
	public void closeAll() {
		try {
			
			int count = getTabCount();
			
			for (int index = 1; index < count; index++) {
				remove(1);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Reload tabs.
	 */
	public void reload() {
		try {
			
			int tabCount = getTabCount();
			
			// Do loop for all tabs.
			for (int tabIndex = 1; tabIndex < tabCount; tabIndex++) {
				// Get tab component.
				Component tabComponent = getTabComponentAt(tabIndex);
				// Check the component.
				if (!(tabComponent instanceof TabLabel)) {
					continue;
				}
				// Get content.
				TabLabel content = (TabLabel) tabComponent;
				// Get contained component.
				Component component = content.component;
				// Check type.
				if (!(component instanceof TabItemInterface)) {
					continue;
				}
				TabItemInterface tabContainerComponent = (TabItemInterface) component;
				tabContainerComponent.reload();
				// Get tab description.
				String description = tabContainerComponent.getTabDescription();
				if (description != null) {
					// Set label text.
					content.label.setText(description);
					// Set tool tip.
					setToolTipTextAt(tabIndex, description);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get tabs states.
	 * @return
	 */
	public LinkedList<TabState> getTabsStates() {
		
		try {
			LinkedList<TabState> list = new LinkedList<TabState>();
			
			// Do loop for all tab components.
			for (int index = 1; index < getTabCount(); index++) {
				
				// Get tab label at index position
				Component component = getTabComponentAt(index);
				if (!(component instanceof TabLabel)) {
					continue;
				}
				TabLabel tabLabel = (TabLabel) component;
				
				// Get tab component
				component = tabLabel.getPanelComponent();
				if (component instanceof TabItemInterface) {
					
					// Get tab state and add it to the list
					TabItemInterface tabInterface = (TabItemInterface) component;
					TabState tabState = tabInterface.getTabState();
					
					list.add(tabState);
				}
			}
			
			return list;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Get tab top area.
	 * @return
	 */
	public Long getTopAreaIdOfSelectedTab() {
		
		try {
			// Get selected tab.
			int index = getSelectedIndex();
			if (index == -1) {
				return 0L;
			}
			
			// Get tab component.
			Component component = getTabComponentAt(index);
			if (!(component instanceof TabLabel)) {
				return 0L;
			}
			TabLabel tabLabel = (TabLabel) component;
			Component tabPanelComponent = tabLabel.getPanelComponent();
			if (!(tabPanelComponent instanceof TabItemInterface)) {
				return 0L;
			}
			
			// Get tab state
			TabItemInterface tabInterface = (TabItemInterface) tabPanelComponent;
			TabState tabState = tabInterface.getTabState();
			
			if (!(tabState instanceof AreasTabState)) {
				return 0L;
			}
			
			// Get the area ID and return it
			AreasTabState areasTabState = (AreasTabState) tabState;
			long topAreaId = areasTabState.areaId;
			
			// Return top area ID.
			return topAreaId;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0L;
	}

	/**
	 * Set remove listener.
	 */
	public void setRemoveListener(Runnable removeListener) {
		
		this.removeListener = removeListener;
	}
	
	/**
	 * On remove tab.
	 */
	public void onRemoveTab() {
		try {
			
			// Delegate call.
			if (component instanceof TabItemInterface) {
				TabItemInterface tabInterface = (TabItemInterface) component;
				tabInterface.beforeTabPanelRemoved();
			}
			
			if (removeListener != null) {
				removeListener.run();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
