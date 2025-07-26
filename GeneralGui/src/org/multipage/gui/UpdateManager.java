/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2025-02-06
 *
 */
package org.multipage.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Timer;

import org.multipage.util.Safe;

/**
 * Helper class for updating application components.
 * @author vakol
 */
public class UpdateManager {

	/**
	 * Delay in milliseconds for clearing states of the update manager.
	 */
	private static final int CLEARING_DELAY_MS = 2000;
	
	/**
	 * List of priorities.
	 */
	public static final int LOW_PRIORITY = 0;
	public static final int NORMAL_PRIORITY = 100;
	public static final int HIGH_PRIORITY = 200;
	
	/**
	 * Wrapper class for updatable components.
	 */
	private static class UpdatableComponentWrapper {
		
		/**
		 * Updatable component reference.
		 */
		protected UpdatableComponent ref = null;
		
		/**
		 * Flag that signals that this component is busy,
		 */
		protected boolean isBusy = false;
		
		/**
		 * Priority of this component.
		 */
		protected int priority = NORMAL_PRIORITY;
		
		/**
		 * Constructor.
		 * @param component
		 */
		public UpdatableComponentWrapper(UpdatableComponent component) {
			
			this.ref = component;
		}

		/**
		 * Wraps updatable component.
		 * @return
		 */
		protected static UpdatableComponentWrapper wrap(UpdatableComponent component) {
			
			try {
				UpdatableComponentWrapper wrappedComponent = new UpdatableComponentWrapper(component);
				return wrappedComponent;
			}
			catch (Throwable e) {
				Safe.exception(e);
			}
			return null;
		}
	}
	
	/**
	 * Ordered sets of unique updatable components.
	 */
	private List<UpdatableComponentWrapper> updatableComponents = null;
	private List<UpdatableComponentWrapper> updatableComponentsCopy = null;
	
	/**
	 * Flag that signals that update manager is busy.
	 */
	private boolean isManagerBusy = false;
	
	/**
	 * Constructor.
	 */
	public UpdateManager() {
		try {
			
			// Create empty, synchronized and ordered set of updateable component references.
			updatableComponents = Collections.synchronizedList(new LinkedList<>());
			updatableComponentsCopy = Collections.synchronizedList(new LinkedList<>());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Create update manager object.
	 * @return
	 */
	public static UpdateManager getInstance() {
		
		try {
			UpdateManager updateManagerObject = new UpdateManager();
			return updateManagerObject;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Find registered updatable component wrapper.
	 * @param component
	 * @return
	 */
	private UpdatableComponentWrapper findRegisteredWrapper(UpdatableComponent component) {
		try {
			synchronized (updatableComponents) {
				for (UpdatableComponentWrapper updatableComponent : updatableComponents) {
					
					if (updatableComponent.ref == component) {
						return updatableComponent;
					}
				}
			}
		}
		catch (Exception e) {
           Safe.exception(e);
        }
		return null;
	}
	
	/**
	 * Register updatable component.
	 * @param component
	 */
	public void register(UpdatableComponent component) {
		
		// Delegate the call.
		register(component, NORMAL_PRIORITY);
	}
	
	/**
	 * Register updatable component.
	 * @param component
	 * @param priority
	 */
	public void register(UpdatableComponent component, int priority) {
		try {
			
			UpdatableComponentWrapper foundWrapper = findRegisteredWrapper(component);
			if (foundWrapper != null) {
				return;
			}
			
			UpdatableComponentWrapper wrapper = UpdatableComponentWrapper.wrap(component);
			
			add(updatableComponents, wrapper, priority);
			add(updatableComponentsCopy, wrapper, priority);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
		
		// TODO: <---DEBUG Dump registered components.
		//dump(updatableComponents);
	}
	
	/**
	 * Dump components.
	 * @param components
	 * @return
	 */
	@SuppressWarnings("unused")
	private void dump(List<UpdatableComponentWrapper> components) {
		try {
			
			synchronized (components) {
				
				System.out.println("Registered components:");
                for (UpdatableComponentWrapper wrapper : components) {
                    System.out.println(" - " + wrapper.ref.getClass().getName() + " prio " + wrapper.priority);
                }
                System.out.println();;
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
     * Add updatable component to the list with input priority.
     * @param components
     * @param component
     * @param priority
     */
	private static void add(List<UpdatableComponentWrapper> components, UpdatableComponentWrapper component,
			int priority) {
		
		synchronized (components) {
			
			// Set component priority.
			component.priority = priority;
            
            // Add component to the list depending on its priority.
            int insertIndex = 0;
            for (UpdatableComponentWrapper listedComponent : components) {
                
                if (priority > listedComponent.priority) {
                    break;
                }
                else {
                    insertIndex++;
                }
            }
            components.add(insertIndex, component);
		}
	}

	/**
	 * Unregister updatable component.
	 * @param component
	 */
	public void unregister(UpdatableComponent component) {
		try {
			
			UpdatableComponentWrapper foundWrapper = findRegisteredWrapper(component);
			if (foundWrapper == null) {
				return;
			}
			
			synchronized (updatableComponents) {
				updatableComponents.remove(foundWrapper);
			}
			synchronized (updatableComponentsCopy) {
				updatableComponentsCopy.remove(foundWrapper);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};	
	}
	
	/**
	 * Check if update manager is busy.
	 * @return
	 */
	private boolean isManagerBusy() {
		
		try {
			// Check manager flag.
			if (isManagerBusy) {
				return true;
			}
			
			// Check all other flags.
			synchronized (updatableComponentsCopy) {
				
				// Iterate through registered components and find busy component.
	        	for (UpdatableComponentWrapper updatableComponent : updatableComponentsCopy) {
	        		
	        		boolean isComponentBusy = updatableComponent.isBusy;
	        		if (isComponentBusy) {
	        			
	        			return true;
	        		}
	        	}
			}
		}
		catch (Exception e) {
           Safe.exception(e);
        }
		// Update manager is not busy.
		return false;
	}
	
	/**
	 * Clear update manager states.
	 */
	public void clearState() {
		try {
			
			synchronized (updatableComponents) {
				
				// Iterate through registered components and reset busy component flags.
	        	for (UpdatableComponentWrapper updatableComponent : updatableComponents) {
	        		updatableComponent.isBusy = false;
	        	}
	        	
	        	// Clear busy state for update manager.
	        	this.isManagerBusy = false;
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};	
	}
	
	/**
	 * Main update entry point that updates all regsitered application components.
	 * @param excludedComponents - Enumeration of components excluded from update.
	 */
	public void updateAll(UpdatableComponent ...excludedComponents) {
		try {
			
			// Check if manager is busy.
			if (isManagerBusy()) {
				return;
			}
			
			isManagerBusy = true;
	        
	        synchronized (updatableComponents) {
	        	
	        	int excludedCount = excludedComponents.length;
	        	
	        	// Iterate through registered components.
	        	labelContinue:
	        	for (UpdatableComponentWrapper updatableComponent : updatableComponents) {
	        		
	        		// Check if the component is excluded. If it is, do nothing.
	        		if (excludedCount > 0) {
	        			for (UpdatableComponent excludedComponent : excludedComponents) {
	        				if (excludedComponent.equals(updatableComponent.ref)) {
	        					continue labelContinue;
	        				}
	        			}
	        		}
	        		
	        		// Check if the component is busy. If it is do nothing.
	        		boolean isBusy = updatableComponent.isBusy;
	        		if (isBusy) {
	        			continue;
	        		}
	        		
	        		// Update component. Set it busy.
	        		updatableComponent.isBusy = true;
	        		updatableComponent.ref.updateComponents();
	        		
	        		Safe.invokeLater(() -> {
	        			// Remove the busy state.
	        			updatableComponent.isBusy = false;
	        		});
	        	}
	        }
	        
	        // Reset manager busy flag.
	        Safe.invokeLater(() -> {
	        	isManagerBusy = false;
	        });
	        
	        // Run delayed method that clears update manager states.
	        Timer clearingTimer = new Timer(CLEARING_DELAY_MS, new ActionListener() {
	
				@Override
				public void actionPerformed(ActionEvent e) {
					
					// Clear udpate manager state.
					UpdateManager.this.clearState();
				}
	        });
	        
	        clearingTimer.setInitialDelay(CLEARING_DELAY_MS);
	        clearingTimer.setRepeats(false);
	        
	        clearingTimer.start();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
    }
}
