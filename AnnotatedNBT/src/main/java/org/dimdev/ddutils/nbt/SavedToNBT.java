package org.dimdev.ddutils.nbt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD}) // TODO: split annotation, error when set on field but not containing class
@Retention(RetentionPolicy.RUNTIME)
public @interface SavedToNBT {

}
