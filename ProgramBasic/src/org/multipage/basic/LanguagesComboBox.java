/*
 * Copyright 2010-2026 (C) vakol
 * 
 * Created on : 2026-02-09
 *
 */

package org.multipage.basic;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Properties;
import java.util.function.BiConsumer;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.maclan.Language;
import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.multipage.util.Obj;
import org.multipage.util.Safe;

/**
 * Combo box with languages.
 * @author vakol
 */
public class LanguagesComboBox extends JComboBox<Language> {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Login properties.
	 */
	private Properties login;

	/**
	 * Start language ID.
	 */
	private Long startLanguageId = 0L;

	/**
	 * Constructor.
	 */
	public LanguagesComboBox() {
		super();
	}
	
	/**
	 * Initialize combo box.
	 */
	public void initializeComboBox() {
		try {
			
			// Set renderer.
			setRenderer(new ListCellRenderer<Object>() {
				// Create renderer.
				private LanguageRenderer renderer = new LanguageRenderer();
				// Return renderer.
				@Override
				public Component getListCellRendererComponent(JList<?> list, Object value,
						int index, boolean isSelected, boolean cellHasFocus) {
					
					try {
						// Check value.
						if (!(value instanceof Language)) {
							return null;
						}
						Language language = (Language) value;
						
						boolean isStart = language.id == startLanguageId;
		
						// Set renderer properties.
						renderer.setProperties(language.description, language.id,
								language.alias, language.image, isStart, index,
								isSelected, cellHasFocus);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return renderer;
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On select language.
	 */
	public void onSelectLanguage(BiConsumer<Language, ActionEvent> supplier) {
		try {
			
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						// Get selected language.
						Language language = getSelectedLanguage();
						if (language == null) {
							return;
						}
						// Run consumer.
						supplier.accept(language, e);
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
	 * Load languages.
	 */
	public void loadLanguagesToCombo() {
		try {
			
			// Get login and middle layer.
			login = ProgramBasic.getLoginProperties();
			Middle middle = ProgramBasic.getMiddle();
			
			// Check references.
			if (middle == null) {
				return;
			}
	
			// Reset combo box.
			removeAllItems();
			
			// Load languages.
			LinkedList<Language> languages = new LinkedList<Language>();
			
			MiddleResult result;
			
			// Login to the database.
			result = middle.login(login);
			if (result.isOK()) {
				
				result = middle.loadLanguages(languages);
				if (result.isOK()) {
					
					// Load start language ID.
					Obj<Long> startLanguageId = new Obj<Long>();
					result = middle.loadStartLanguageId(startLanguageId);
					
					this.startLanguageId  = startLanguageId.ref;
				}
	
				// Logout from the database.
				MiddleResult logoutResult = middle.logout(result);
				if (result.isOK()) {
					result = logoutResult;
				}
			}
			
			if (result.isNotOK()) {
				result.show(this);
				return;
			}
			
			// Load combo box.
			for (Language language : languages) {
				addItem(language);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get selected language.
	 * @return
	 */
	public Language getSelectedLanguage() {
		try {
			Language language = (Language) getSelectedItem();
			if (language == null) {
				return null;
			}
			return language;
		}
		catch (Throwable e) {
			Safe.exception(e);
			return null;	
		}
		
	}
}
