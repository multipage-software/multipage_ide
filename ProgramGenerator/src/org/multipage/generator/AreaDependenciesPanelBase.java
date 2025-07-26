/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JTable;

import org.maclan.Area;
import org.maclan.MiddleResult;
import org.multipage.basic.ProgramBasic;
import org.multipage.gui.Images;
import org.multipage.gui.Utility;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Editor panel that shows information about area dependencies.
 * @author vakol
 *
 */
public class AreaDependenciesPanelBase extends JPanel implements EditorTabActions {
	
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Panel state.
	 */
	public static boolean selectedSubAreas = true;
	
	/**
	 * Area reference.
	 */
	protected Area currentArea;
	
	/**
	 * Components' references.
	 */
	private JLabel labelAreaDependencies;
	private JTable tableAreas;
	private JButton buttonUp;
	private JButton buttonDown;
	private JButton buttonDefault;
	private JRadioButton buttonSubAreas;
	private JRadioButton buttonSuperAreas;
	private JPopupMenu popupMenu;
	protected RelatedAreaPanel panelRelatedArea;

	/**
	 * Set components' references.
	 * @param labelAreaDependencies
	 * @param tableAreas
	 * @param buttonUp
	 * @param buttonDown
	 * @param buttonDefault
	 * @param buttonSubAreas
	 * @param buttonSuperAreas
	 * @param labelRelatedArea 
	 * @param buttonClearRelatedArea 
	 * @param buttonSelectRelatedArea 
	 * @param buttonUpdateRelatedArea 
	 * @param textRelatedArea 
	 * @param buttonSetRelationNames
	 */
	protected void setComponentsReferences(
			JLabel labelAreaDependencies,
			JTable tableAreas,
			JButton buttonUp,
			JButton buttonDown,
			JButton buttonDefault,
			JRadioButton buttonSubAreas,
			JRadioButton buttonSuperAreas,
			JPopupMenu popupMenu,
			RelatedAreaPanel panelRelatedArea
			) {
		
		 this.labelAreaDependencies = labelAreaDependencies;
		 this.tableAreas = tableAreas;
		 this.buttonUp = buttonUp;
		 this.buttonDown = buttonDown;
		 this.buttonDefault = buttonDefault;
		 this.buttonSubAreas = buttonSubAreas;
		 this.buttonSuperAreas = buttonSuperAreas;
		 this.popupMenu = popupMenu;
		 this.panelRelatedArea = panelRelatedArea;
	}
	
	/**
	 * Constructor.
	 */
	public AreaDependenciesPanelBase() {
	}

	/**
	 * Post creation.
	 */
	protected void postCreate() {
		try {
			
			// Set table property.
			tableAreas.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
			// Localize components.
			localize();
			// Set icons.
			setIcons();
			// Set tool tips.
			setToolTips();
			// Select button.
			buttonSubAreas.setSelected(selectedSubAreas);
			buttonSuperAreas.setSelected(!selectedSubAreas);
			// Create popup trayMenu.
			createPopupMenu();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create popup trayMenu.
	 */
	private void createPopupMenu() {
		try {
			
			final Component thisComponent = this;
			
			AreaLocalMenu menu = ProgramGenerator.newAreaLocalMenu(new AreaLocalMenu.Callbacks() {
				@Override
				protected Area getCurrentArea() {
					try {
						// Get selected area.
						return getSelectedArea();
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return null;
				}
	
				@Override
				public Component getComponent() {
					// Get this component.
					return thisComponent;
				}
			});
			
			menu.addTo(this, popupMenu);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get selected area.
	 * @return
	 */
	protected Area getSelectedArea() {
		
		try {
			int row = tableAreas.getSelectedRow();
			if (row == -1) {
				Utility.show(this, "org.multipage.generator.messageSelectArea");
				return null;
			}
			
			return (Area) tableAreas.getValueAt(row, 0);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Localize components.
	 */
	private void localize() {
		try {
			
			Utility.localize(labelAreaDependencies);
			Utility.localize(buttonSubAreas);
			Utility.localize(buttonSuperAreas);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set current area.
	 * @param currentArea
	 */
	public void setArea(Area area) {
		try {
			
			this.currentArea = area;
			panelRelatedArea.setArea(area);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On currentArea change.
	 */
	protected void onAreaChange() {
		try {
			
			selectedSubAreas = buttonSubAreas.isSelected();
			GeneratorMainFrame.updateAll();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Swap areas priority
	 * @param row1
	 * @param row2
	 */
	private void swapAreaPriorities(int row1, int row2) {
		try {
			
			boolean useSubAreas = buttonSubAreas.isSelected();
	
			// Get area1 and area2.
			Area area1 = (Area) tableAreas.getModel().getValueAt(row1, 0);
			Area area2 = (Area) tableAreas.getModel().getValueAt(row2, 0);
			
			// Swap sub areas priorities.
			MiddleResult result;
			if (useSubAreas) {
				result = ProgramBasic.getMiddle().swapAreaSubAreasPriorities(
						ProgramBasic.getLoginProperties(), currentArea,
						area1, area2);
			}
			else {
				result = ProgramBasic.getMiddle().swapAreaSuperAreasPriorities(
						ProgramBasic.getLoginProperties(), currentArea,
						area1, area2);
			}
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			// Update information.
			onAreaChange();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On currentArea up.
	 */
	protected void onUp() {
		try {
			
			// Get selected currentArea.
			int selectedRow = tableAreas.getSelectedRow();
			if (selectedRow == -1) {
				Utility.show(this, "org.multipage.generator.textSelectAreaPriority");
				return;
			}
			
			// Get previous row.
			int previousRow = selectedRow - 1;
			if (previousRow < 0) {
				return;
			}
			
			// Swap priorities.
			swapAreaPriorities(selectedRow, previousRow);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On currentArea down.
	 */
	protected void onDown() {
		try {
			
			// Get selected currentArea.
			int selectedRow = tableAreas.getSelectedRow();
			if (selectedRow == -1) {
				Utility.show(this, "org.multipage.generator.textSelectAreaPriority");
				return;
			}
	
			// Get next row.
			int nextRow = selectedRow + 1;
			if (nextRow >= tableAreas.getModel().getRowCount()) {
				return;
			}
			
			// Swap priorities.
			swapAreaPriorities(selectedRow, nextRow);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On reset siblings order.
	 */
	protected void onReset() {
		try {
			
			long currentAreaId = currentArea.getId();
	
			// Reset sub areas priorities.
			MiddleResult result = ProgramBasic.getMiddle().resetSubAreasPriorities(
					ProgramBasic.getLoginProperties(), currentAreaId);
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			// Update information.
			onAreaChange();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set icons.
	 */
	protected void setIcons() {
		try {
			
			buttonDefault.setIcon(Images.getIcon("org/multipage/generator/images/reset_order.png"));
			buttonUp.setIcon(Images.getIcon("org/multipage/generator/images/up.png"));
			buttonDown.setIcon(Images.getIcon("org/multipage/generator/images/down.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set tool tips.
	 */
	protected void setToolTips() {
		try {
			
			buttonDefault.setToolTipText(Resources.getString("org.multipage.generator.tooltipSetDefaultOrder"));
			buttonUp.setToolTipText(Resources.getString("org.multipage.generator.tooltipShiftAreaUp"));
			buttonDown.setToolTipText(Resources.getString("org.multipage.generator.tooltipShiftAreaDown"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On close dialog.
	 */
	public boolean close() {
		
		try {
			return !tableAreas.isEditing();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * On load panel information.
	 */
	@Override
	public void onLoadPanelInformation() {
		try {
			
			// Invoke currentArea change method.
			onAreaChange();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On save panel information.
	 */
	@Override
	public void onSavePanelInformation() {
		
	}
	
	/**
	 * Add popup trayMenu.
	 * @param component
	 * @param popup
	 */
	protected static void addPopup(Component component, final JPopupMenu popup) {
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
	 * On table mouse click.
	 * @param e
	 */
	protected void onTableClick(MouseEvent e) {
		try {
			
			if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
				
				// Focus selected area.
				Area area = getSelectedArea();
				if (area != null) {
					
					GeneratorMainFrame.getFrame().getVisibleAreasEditor().focusArea(area.getId());
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On close the window.
	 */
	public void onClose() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Method for updating the dialog components.
	 */
	public void updateComponents() {
		try {
			
			// Load related area.
			panelRelatedArea.updateComponents();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
}
