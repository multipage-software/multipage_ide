/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.util;

import java.util.*;

import org.multipage.util.Safe;

/**
 * Helper class for sorting.
 * @author vakol
 *
 */
public class SortingHelper <T> {
	
	/**
	 * Object.
	 */
	private T object;
	
	/**
	 * Left part.
	 */
	private SortingHelper<T> left;
	
	/**
	 * Right part.
	 */
	private SortingHelper<T> right;

	/**
	 * Sets object.
	 * @param object
	 */
	public void addObject(T newObject, SortingListener<T> listener) {
		try {
			
			// If the object is null, set it and exit the method.
			if (object == null) {
				
				this.object = newObject;
				return;
			}
			
			// If the new object is less than current object, add it
			// to the left else add it to the right.
			if (listener.compare(newObject, object) <= 0) {
				
				if (left == null) {
					left = new SortingHelper<T>();
				}
				left.addObject(newObject, listener);
			}
			else {
				if (right == null) {
					right = new SortingHelper<T>();
				}
				right.addObject(newObject, listener);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get sorted objects recursively.
	 * @param sortingHelper
	 * @param sortedObjects
	 */
	private void getSortedObjects(SortingHelper<T> sortingHelper,
			ArrayList<T> sortedObjects) {
		try {
			
			// Get left sorted objects.
			if (left != null) {
				left.getSortedObjects(sortingHelper.left, sortedObjects);
			}
			
			// Check and save this object reference.
			if (object != null) {
				sortedObjects.add(object);
			}
			
			// Get right sorted objects.
			if (right != null) {
				right.getSortedObjects(sortingHelper.right, sortedObjects);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Gets sorted objects.
	 * @return
	 */
	public ArrayList<T> getSortedObjects() {
		
		try {
			ArrayList<T> sortedObjects = new ArrayList<T>();
			
			// Call recursive method.
			getSortedObjects(this, sortedObjects);
			
			return sortedObjects;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
}