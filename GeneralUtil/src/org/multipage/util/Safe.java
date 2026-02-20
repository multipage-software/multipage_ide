/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2025-02-11
 *
 */
package org.multipage.util;

import java.util.LinkedList;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

/**
 * Safe methods for GUI components.
 * @author vakol
 *
 */
public class Safe {
	
	/**
	 * List of components excluded from invocation of the change lambda function.
	 */
	private static final LinkedList<Object> excludedComponents = new LinkedList<>();
	
	/**
	 * Process exception.
	 * @param e
	 */
	public static void exception(Throwable e) {
		
        e.printStackTrace();
	}
	
	/**
     * Invoke later safely.
     * @param runnable
     */
	public static void invokeLater(Runnable runnable) {
		
		try {
			SwingUtilities.invokeLater(() -> {
				try {
					runnable.run();
				}
				catch (Throwable e) {
					exception(e);
		        }
			});
		}
		catch (Exception e) {
			exception(e);
		}
	}
	
	/**
	 * Try to update component safely.
	 * @param component
	 * @param setLambda
	 */
	public static void tryToUpdate(Object component, Runnable setLambda) {
		
		try {
			// Exclude component from change actions.
			synchronized (excludedComponents) {
				excludedComponents.add(component);
			}
		}
		catch (Throwable e) {
	        exception(e);
	    }
        // Set the component with lamda function.
		try {
			setLambda.run();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		
        // Remove the component from excluded components.
		Safe.invokeLater(() -> {
			synchronized (excludedComponents) {
                excludedComponents.removeLast();
            }
		});
	}
	
	/**
	 * Try to update components safely.
	 * @param components
	 * @param setLambda
	 */
	public static void tryToUpdate(Runnable setLambda, Object ... components) {
		
		try {
			// Exclude component from change actions.
			synchronized (excludedComponents) {
				for (Object component : components) {
					excludedComponents.add(component);
				}
			}
		}
		catch (Throwable e) {
	        exception(e);
	    }
        // Set the component with lamda function.
		try {
			setLambda.run();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		
        // Remove the component from excluded components.
		Safe.invokeLater(() -> {
			synchronized (excludedComponents) {
				for (int index = 0; index < components.length; index++) {
					excludedComponents.removeLast();
				}
            }
		});
	}
	
	/**
	 * Try to invoke component change method.
	 * @param component
	 * @param onChangeLambda
	 */
	public static void tryOnChange(Object component, Runnable onChangeLambda) {
		
		try {
			// Check if the component is excluded from the change actions.
			synchronized (excludedComponents) {
				
				boolean isExcluded = excludedComponents.contains(component);
				if (isExcluded) {
                    return;
                }
				
				// Exclude the component.
				excludedComponents.add(component);
			}
		}
		catch (Throwable e) {
            exception(e);
        }
		try {	
			// Invoke the lambda method.
			onChangeLambda.run();
		}
		catch (Throwable e) {
	        exception(e);
	    }			
		Safe.invokeLater(() -> {
			// Remove the component from excluded components.
			synchronized (excludedComponents) {
				excludedComponents.removeLast();
			}
		});
	}
	
	/**
	 * Try to invoke component change method.
	 * @param components
	 * @param onChangeLambda
	 */
	public static void tryOnChange(Runnable onChangeLambda, Object ... components) {
		
		try {
			// Check if the component is excluded from the change actions.
			synchronized (excludedComponents) {
				
				boolean isExcluded = false;
				for (Object component : components) {
					if (excludedComponents.contains(component)) {
						isExcluded = true;
						break;
					}
				}
				if (isExcluded) {
                    return;
                }
				
				// Exclude the component.
				for (Object component : components) {
					excludedComponents.add(component);
				}
			}
		}
		catch (Throwable e) {
            exception(e);
        }
		try {	
			// Invoke the lambda method.
			onChangeLambda.run();
		}
		catch (Throwable e) {
	        exception(e);
	    }			
		Safe.invokeLater(() -> {
			// Remove the component from excluded components.
			synchronized (excludedComponents) {
				for (int index = 0; index < components.length; index++) {
					excludedComponents.removeLast();
				}
			}
		});
	}

	/**
	 * Set text content without invoking control events.
	 * @param textField
	 * @param content
	 */
	public static void setText(JTextField textField, String content) {
		
		tryToUpdate(textField, () -> {
			textField.setText(content);
		});
	}
	
	/**
	 * Set checkbox selection without invoking control events.
	 * @param checkBox
	 * @param selected
	 */
	public static void setSelected(JCheckBox checkBox, boolean selected) {
		
		tryToUpdate(checkBox, () -> {
			checkBox.setSelected(selected);
		});
	}
	
	/**
	 * Set combo box selection without invoking control events.
	 * @param comboBox
	 */
	public static void setSelectedIndex(JComboBox<?> comboBox, int index) {

		tryToUpdate(comboBox, () -> {
			comboBox.setSelectedIndex(index);
		});
	}
	
	/**
	 * Set combo box selection without invoking control events.
	 * @param comboBox
	 * @param selectedObject
	 */
	public static void setSelectedItem(JComboBox<?> comboBox, Object selectedObject) {

		tryToUpdate(comboBox, () -> {
			comboBox.setSelectedItem(selectedObject);
		});
	}
	
	/**
	 * Set toggle button selection without invoking control events.
	 * @param togglebotton
	 * @param selected
	 */
	public static void setSelected(JToggleButton togglebotton, boolean selected) {
		
		tryToUpdate(togglebotton, () -> {
			togglebotton.setSelected(selected);
		});
	}
}
