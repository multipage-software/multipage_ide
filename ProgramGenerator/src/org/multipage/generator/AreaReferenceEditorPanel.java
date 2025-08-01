/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.maclan.Area;
import org.maclan.AreaReference;
import org.multipage.gui.Images;
import org.multipage.gui.StringValueEditor;
import org.multipage.gui.TextFieldEx;
import org.multipage.gui.Utility;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Panel that displays area reference editor.
 * @author vakol
 *
 */
public class AreaReferenceEditorPanel extends JPanel implements SlotValueEditorPanelInterface {
	
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Selected area ID.
	 */
	private AreaReference selectedAreaReference;
	
	/**
	 * Components.
	 */
	private JLabel labelAreaReference;
	private TextFieldEx textAreaName;
	private JButton buttonGetArea;
	private JButton buttonReset;

	/**
	 * Constructor.
	 */
	public AreaReferenceEditorPanel() {
		
		try {
			initComponents();
			// $hide>>$
			postCreate();
			// $hide<<$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Initialize components.
	 */
	private void initComponents() {
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		labelAreaReference = new JLabel("org.multipage.generator.textAreaReference");
		springLayout.putConstraint(SpringLayout.NORTH, labelAreaReference, 40, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, labelAreaReference, 33, SpringLayout.WEST, this);
		add(labelAreaReference);
		
		textAreaName = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textAreaName, 0, SpringLayout.NORTH, labelAreaReference);
		springLayout.putConstraint(SpringLayout.WEST, textAreaName, 6, SpringLayout.EAST, labelAreaReference);
		textAreaName.setPreferredSize(new Dimension(6, 22));
		textAreaName.setEditable(false);
		textAreaName.setColumns(25);
		add(textAreaName);
		
		buttonGetArea = new JButton("");
		springLayout.putConstraint(SpringLayout.EAST, textAreaName, -3, SpringLayout.WEST, buttonGetArea);
		buttonGetArea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onFindArea();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, buttonGetArea, 0, SpringLayout.NORTH, labelAreaReference);
		buttonGetArea.setPreferredSize(new Dimension(22, 22));
		buttonGetArea.setMargin(new Insets(0, 0, 0, 0));
		add(buttonGetArea);
		
		buttonReset = new JButton("");
		springLayout.putConstraint(SpringLayout.EAST, buttonReset, -33, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.EAST, buttonGetArea, -3, SpringLayout.WEST, buttonReset);
		buttonReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onReset();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, buttonReset, 0, SpringLayout.NORTH, labelAreaReference);
		buttonReset.setPreferredSize(new Dimension(22, 22));
		buttonReset.setMargin(new Insets(0, 0, 0, 0));
		add(buttonReset);
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			localize();
			setIcons();
			setToolTips();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Localize.
	 */
	private void localize() {
		try {
			
			Utility.localize(labelAreaReference);
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
			
			buttonGetArea.setIcon(Images.getIcon("org/multipage/gui/images/find_icon.png"));
			buttonReset.setIcon(Images.getIcon("org/multipage/gui/images/cancel_icon.png"));
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
			
			buttonGetArea.setToolTipText(Resources.getString("org.multipage.generator.tooltipFindArea"));
			buttonReset.setToolTipText(Resources.getString("org.multipage.generator.tooltipResetArea"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get value.
	 */
	@Override
	public Object getValue() {

		return selectedAreaReference;
	}

	/**
	 * Set value.
	 */
	@Override
	public void setValue(Object value) {
		try {
			
			// Set controls.
			if (value instanceof AreaReference) {
				
				selectedAreaReference = (AreaReference) value;
				
				Area area = ProgramGenerator.getArea(selectedAreaReference.areaId);
				textAreaName.setText(area.getDescriptionForced(true));
			}
			else {
				onReset();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set default value state.
	 */
	@Override
	public void setDefault(boolean isDefault) {
		
		// Disable / enable components explicitly.
	}

	/**
	 * Get value meaning.
	 */
	@Override
	public String getValueMeaning() {
		
		return StringValueEditor.meansArea;
	}

	/**
	 * On find resource.
	 */
	protected void onFindArea() {
		try {
			
			// Get area.
			Area oldSelectedArea = selectedAreaReference != null ? ProgramGenerator.getArea(selectedAreaReference.areaId) : null;
			
			Area selectedArea = SelectSubAreaDialog.showDialog(this, null, oldSelectedArea);
			if (selectedArea == null) {
				return;
			}
					
			// Set image name text control.
			textAreaName.setText(selectedArea.getDescriptionForced(true));
			selectedAreaReference = new AreaReference(selectedArea);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On reset.
	 */
	protected void onReset() {
		try {
			
			selectedAreaReference = null;
			textAreaName.setText("");
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
