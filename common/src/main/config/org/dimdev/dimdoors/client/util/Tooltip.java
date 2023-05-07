package org.dimdev.dimdoors.client.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(Tooltips.class)
public @interface Tooltip {
	String value();

	/**
	 * Whether the string should be used as
	 * is, or first translated.
	 */
	boolean absolute() default false;
}
