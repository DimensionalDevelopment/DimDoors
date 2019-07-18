package org.dimdev.ddutils;

@FunctionalInterface
public interface TriFunction<X,Y,Z,T> {
    T process(X x, Y y, Z z);
}
