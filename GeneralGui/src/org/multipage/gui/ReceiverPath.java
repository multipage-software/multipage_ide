/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2024-11-30
 *
 */
package org.multipage.gui;

/**
 * Application event receiver path.
 * @param receiverObject
 */
class ReceiverPath {
	
	/**
	 * Application event.
	 */
	public ApplicationEvent event = null;
	
	/**
	 * Receiver priority.
	 */
	public Integer priority = null;
	
	/**
     * Receiver object.
     */
	public Object receiverObject = null;
	
	/**
     * Constructor.
     * @param event
     * @param priority
     * @param receiverObject
     */
	public ReceiverPath(ApplicationEvent event, Integer priority, Object receiverObject) {
		
		this.event = event;
		this.priority = priority;
		this.receiverObject = receiverObject;
	}

	/**
	 * Constructor.
	 * @param event
	 * @param priority
	 */
	public ReceiverPath(ApplicationEvent event, Integer priority) {
		 
		this.event = event;
		this.priority = priority;
	}

	/**
	 * Constructor.
	 * @param event
	 */
	public ReceiverPath(ApplicationEvent event) {
		
		this.event = event;
	}
}