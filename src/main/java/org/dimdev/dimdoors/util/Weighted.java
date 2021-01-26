package org.dimdev.dimdoors.util;

public interface Weighted<P> {
	/*
	Should always return the same number if the same parameters are provided.
	returned number should always be >= 0
	 */
	int getWeight(P parameters);
}
