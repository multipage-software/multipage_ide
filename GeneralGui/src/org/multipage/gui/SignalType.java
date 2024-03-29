/*
 * Copyright 2010-2020 (C) vakol
 * 
 * Created on : 21-07-2017
 *
 */
package org.multipage.gui;

/**
 * Groups.
 * @author user
 *
 */
public enum SignalType implements EventCondition {
	
	// Sets also the priority of signals. (Most top groups have higher priority.)
	
	// Change of area model.
	areaModelChange,
	// Change of slot model.
	slotModelChange,
	// Change of area view state.
	areaViewStateChange,
	// Change of slot view state.
	slotViewStateChange,
	// Change of GUI state.
	guiStateChange,
	// Change of GUI state by the user.
	guiStateChangeByUser,
	// Change of area view.
	areaViewChange,
	// Change of slot view.
	slotViewChange,
	// Server state change.
	serverStateChange,
	// Change of GUI.
	guiChange,
	// Change of GUI by the user.
	guiChangeByUser;
	
	/**
	 * Priority of the signal.
	 */
	private int priority = ConditionalEvents.MIDDLE_PRIORITY;
	
	/**
	 * Returns true if the incoming message matches this signal type.
	 */
	@Override
	public boolean matches(Message incomingMessage) {
		
		return incomingMessage.signal.isOfType(this);
	}

	/**
	 * Set priority.
	 */
	public void setPriority(int priority) {
		
		this.priority = priority;
	}
	
	/**
	 * Get priority.
	 */
	public int getPriority() {
		
		return this.priority;
	}
}