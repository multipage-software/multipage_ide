/**
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2024-06-25
 *
 */
package org.maclan.server;

import org.multipage.util.Resources;

/**
 * Code source information for debugger.
 * @author vakol
 */
public class DebugSourceInfo {
	
	/**
	 * List of code sources.
	 */
	public static final int RESOURCE = 1;
	public static final int SLOT = 2;
	
	/**
	 * Type of code source information.
	 */
	private Integer type = null;
	
	/**
	 * ID of source.
	 */
	private Long id = null;
	
	/**
	 * Name of the source.
	 */
	private String name = null;
	
	/**
	 * ID of area that is a source of current code.
	 */
	private Long areaId = null;
	
	/**
	 * Name of the area that supplies source code.
	 */
	private String areaName = null;
	
	/**
	 * Create new resource info.
	 * @param resourceId
	 * @param resourceName
	 * @param areaName 
	 * @param areaId 
	 * @return
	 */
	public static DebugSourceInfo newResource(long resourceId, String resourceName, Long areaId, String areaName) {
		
		DebugSourceInfo sourceInfo = new DebugSourceInfo();
		
		sourceInfo.type = RESOURCE;
		
		sourceInfo.id = resourceId;
		sourceInfo.name = resourceName;
		sourceInfo.setAreaId(areaId);
		sourceInfo.setAreaName(areaName);
		
		return sourceInfo;
	}
	
	/**
	 * Create new slot info.
	 * @param slotId
	 * @param slotName 
	 * @param areaName 
	 * @param areaId 
	 * @return
	 */
	public static DebugSourceInfo newSlot(long slotId, String slotName, Long areaId, String areaName) {
		
		DebugSourceInfo sourceInfo = new DebugSourceInfo();
		
		sourceInfo.type = SLOT;
		
		sourceInfo.id = slotId;
		sourceInfo.name = slotName;
		sourceInfo.setAreaId(areaId);
		sourceInfo.setAreaName(areaName);
		
		return sourceInfo;
	}
	
	/**
	 * Make clone of current source information.
	 * @return
	 */
	public DebugSourceInfo cloneSourceInfo() {
		
		DebugSourceInfo clonedSourceInfo = new DebugSourceInfo();
		
		clonedSourceInfo.type = type;
		clonedSourceInfo.id = id;
		clonedSourceInfo.name = name;
		clonedSourceInfo.areaId = areaId;
		clonedSourceInfo.areaName = areaName;
		
		return clonedSourceInfo;
	}
	
	/**
	 * Get source type.
	 * @return
	 */
	public Integer getType() {
		
		return type;
	}
	
	/**
	 * Get source identifier
	 * @return
	 */
	public Long getId() {
		
		return id;
	}
	
	/**
	 * Get resource ID.
	 * @return
	 */
	public Long getResourceId() {
		
		if (type == RESOURCE) {
			return id;
		}
		return null;
	}

	/**
	 * Set resource ID.
	 * @param resourceId
	 */
	public void setResourceId(Long resourceId) {

		this.type = RESOURCE;
		this.id = resourceId;
	}
	
	/**
	 * Get resource name.
	 * @return
	 */
	public String getResourceName() {
		
		if (type == RESOURCE) {
            return name;
        }
		return null;
	}

	/**
     * Set resource name.
     * @param resourceName
     */
	public void setResourceName(String resourceName) {
		
		this.type = RESOURCE;
		this.name = resourceName;
	}

	/**
	 * Get slot ID.
	 * @return
	 */
	public Long getSlotId() {
		
		if (type == SLOT) {
            return id;
        }
		return null;
	}

	/**
	 * Set slot ID.
	 * @param slotId
	 */
	public void setSlotId(Long slotId) {

        this.type = SLOT;
        this.id = slotId;
	}
	
	/**
     * Get slot name.
     * @return
     */
	public String getSlotName() {
		
		if (type == SLOT) {
            return name;
        }
		return null;
	}

	/**
     * Set slot name.
     * @param slotName
     */
	public void setSlotName(String slotName) {
		
		this.type = SLOT;
		this.name = slotName;
	}
	
	/**
	 * Get area ID.
	 * @return
	 */
	public Long getAreaId() {
		return areaId;
	}
	
	/**
     * Set area ID.
     * @param areaId
     */
	public void setAreaId(Long areaId) {
		this.areaId = areaId;
	}

	/**
     * Get area name.
     * @return
     */
	public String getAreaName() {
		return areaName;
	}

	/**
     * Set area name.
     * @param areaName
     */
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	
	/**
	 * Get source information.
	 * @return
	 */
	public String getSourceInfoHtml() {
				
		String sourceInfoText = "<html>";
		
		if (id != null) {
			
			String formatText = null;
			if (type == RESOURCE) {
				formatText = Resources.getString("org.maclan.server.textSourceCodeResource");
			}
			else if (type == SLOT) {
				formatText = Resources.getString("org.maclan.server.textSourceCodeSlot");
			}
			
			if (formatText != null) {
				
				String nameText = (name != null ? name : "");
				Long areaIdParam = (areaId != null ? areaId : -1);
				String areaNameText = (areaName != null ? areaName : "");
				
	            sourceInfoText += String.format(formatText, nameText, id, areaNameText, areaIdParam);
			}
        }
		
		sourceInfoText += "</html>";
		return sourceInfoText;
	}
}
