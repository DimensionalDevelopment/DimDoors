package org.dimdev.dimdoors.api.util;

public interface Weighted<P> {
	// Should always return the same number if the same parameters are provided.
	// returned number should always be >= 0
	double getWeight(P parameters);
}
