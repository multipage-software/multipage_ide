/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.multipage.gui.Images;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Tab content.
 * @author vakol
 */
public class TabLabel extends JPanel {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Label width.
	 */
	private static final int labelWidth = 80;

	/**
	 * Label size.
	 */
	private static final int size = 20;
	
	/**
	 * Close button.
	 */
	private JButton close;

	/**
	 * Tab panel reference.
	 */
	private TabPanel tabPanel;

	/**
	 * Component.
	 */
	public Component component;
	
	/**
	 * Label text
	 */
	public String labelText = "";
	
	/**
	 * Label.
	 */
	public JLabel label;

	/**
	 * Constructor.
	 * @param index 
	 * @param tabPanel 
	 */
	/**
	 * @param text
	 * @param topAreaId
	 * @param tabPanel
	 * @param component
	 * @param type
	 */
	public TabLabel(String text, Long topAreaId, final TabPanel tabPanel, Component component, TabType type) {
		
		try {
			this.tabPanel = tabPanel;
			this.component = component;
			this.labelText = text;
			
			// Try to set reference to this object and save area ID in the input component
			if (component instanceof TabItemInterface) {
				TabItemInterface tabItem = (TabItemInterface) component;
				
				tabItem.setTabLabel(this);
				tabItem.setAreaId(topAreaId);
			}
			
			// Add components.
			setLayout(null);
			setOpaque(false);
			setPreferredSize(new Dimension(labelWidth + 6 + size, size));
			close = new JButton();
			label = new JLabel(text);
			label.setOpaque(false);
			add(label);
			add(close);
			label.setBounds(0, 0, labelWidth, size);
			close.setBounds(labelWidth + 10, 2, size - 4, size - 4);
			close.setToolTipText(Resources.getString("org.multipage.generator.tooltipCloseTab"));
			
			// Set close button.
			close.setIcon(Images.getIcon("org/multipage/generator/images/cancel_grey.png"));
			close.setOpaque(false);
			close.setContentAreaFilled(false);
			close.setBorderPainted(false);
			
			// Set listener.
			close.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					try {
						// Remove tab.
						onRemoveTab();
						
						// Invoke tab panel method.
						Safe.invokeLater(() -> {
							tabPanel.onRemoveTab();
						});
					}
					catch (Throwable exc) {
						Safe.exception(exc);
					}
				}
			});
			
			close.addMouseListener(new MouseAdapter() {
				
				@Override
				public void mouseExited(MouseEvent arg0) {
					try {
						
						close.setOpaque(false);
						close.setContentAreaFilled(false);
						close.setBorderPainted(false);
						close.setIcon(Images.getIcon("org/multipage/generator/images/cancel_grey.png"));
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				
				@Override
				public void mouseEntered(MouseEvent arg0) {
					try {
						
						close.setOpaque(true);
						close.setContentAreaFilled(true);
						close.setBorderPainted(true);
						close.setIcon(Images.getIcon("org/multipage/generator/images/cancel_icon.png"));
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Get description.
	 */
	public String getDescription() {
		
		try {
			return label.getText();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "error";
	}
	
	/**
	 * Set description.
	 */
	public void setDescription(String description) {
		try {
			
			label.setText(description);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On remove tab.
	 */
	protected void onRemoveTab() {
		try {
			
			if (component instanceof TabItemInterface) {
				TabItemInterface tabComponent = (TabItemInterface) component;
				tabComponent.beforeTabPanelRemoved();
			}
	
			tabPanel.remove(component);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get panel component.
	 * @return
	 */
	public Component getPanelComponent() {
		
		return component;
	}
}