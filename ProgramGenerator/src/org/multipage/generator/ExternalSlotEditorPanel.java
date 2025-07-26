/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2020-04-02
 *
 */
package org.multipage.generator;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.multipage.gui.StringValueEditor;
import org.multipage.gui.TextEditorPane;
import org.multipage.gui.Utility;
import org.multipage.util.Safe;

/**
 * Pnale that displays external slot properties.
 * @author vakol
 *
 */
public class ExternalSlotEditorPanel extends JPanel implements SlotValueEditorPanelInterface {
	
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Text editor panel.
	 */
	private TextEditorPane textEditorPanel;
	
	/**
	 * Constructor.
	 */
	public ExternalSlotEditorPanel() {
		
		try {
			// Initialize components.
			initComponents();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Initialize components.
	 */
	private void initComponents() {
		setLayout(new BorderLayout(0, 0));
		
		textEditorPanel = new TextEditorPane(Utility.findWindow(this), false);
		add(textEditorPanel);
	}
	
	/**
	 * Get value.
	 */
	@Override
	public Object getValue() {
		
		try {
			return textEditorPanel.getText();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Set value.
	 */
	@Override
	public void setValue(Object value) {
		
		try {
			if (value == null) {
				value = "";
			}
			textEditorPanel.setText(value.toString());
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Set default.
	 */
	@Override
	public void setDefault(boolean isDefault) {
		
	}
	
	/**
	 * Return value meaning.
	 */
	@Override
	public String getValueMeaning() {
		
		try {
			return StringValueEditor.meansExternalProvider;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
}
