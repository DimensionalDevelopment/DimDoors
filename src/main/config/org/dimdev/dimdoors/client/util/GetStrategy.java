package org.dimdev.dimdoors.client.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GetStrategy {
	String prefix() default "get";

	String suffix() default "";

	FieldNameModifier modifier() default FieldNameModifier.CAPITALIZE_FIRST;

	enum FieldNameModifier {
		CAPITALIZE_FIRST
	}
}
