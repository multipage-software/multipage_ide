/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.util;

/**
 * Interface for callback method used in sorting algorithm.
 * @author vakol
 *
 */
public interface SortingListener<T> {

	/**
	 * Returns -1 if object1 < object2.
	 * Returns  0 if object1 == object2.
	 * Returns  1 if object1 > object2.
	 * @param object1
	 * @param object2
	 * @return
	 */
	int compare(T object1, T object2);
}
