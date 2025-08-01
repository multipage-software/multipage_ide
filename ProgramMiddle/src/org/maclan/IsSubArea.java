/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan;

import java.util.Objects;

/**
 * Class for sub area relation objects.
 * @author vakol
 *
 */
public class IsSubArea {

	public Long id;
	public Long subAreaId;
	public Boolean inheritance;
	public Integer prioritySub;
	public Integer prioritySuper;
	public String nameSub;
	public String nameSuper;
	public Long positionId;
	public Boolean hideSub;
	public Boolean recursion;

	// Auxiliry fileds.
	public boolean mark = false;

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "IsSubArea [id=" + id + ", subAreaId=" + subAreaId
				+ ", inheritance=" + inheritance + ", prioritySub="
				+ prioritySub + ", prioritySuper=" + prioritySuper
				+ ", nameSub=" + nameSub + ", nameSuper=" + nameSuper
				+ ", positionId=" + positionId + ", hideSub=" + hideSub
				+ ", recursion=" + recursion + ", mark=" + mark + "]";
	}

	/**
	 * Check if input object equals to this object.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IsSubArea other = (IsSubArea) obj;
		return Objects.equals(id, other.id) && Objects.equals(subAreaId, other.subAreaId);
	}
}