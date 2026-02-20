/*
 * Copyright 2010-2026 (C) vakol
 * 
 * Created on : 2026-01-16
 *
 */
package org.multipage.gui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define program state for state serializer.
 * 
 * @author vakol
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ProgramState {

	/**
	 * Get state index.
	 * 
	 * @return State value.
	 */
	int value();
}