package org.dimdev.dimdoors.util;

import java.util.*;

public class WeightedList<T extends Weighted<P>, P> extends ArrayList<T> {
	private final Random random = new Random();
	private T peekedRandom;
	private boolean peeked = false;

	public WeightedList() { }

	public WeightedList(Collection<? extends T> c) {
		super(c);
	}

	public T getNextRandomWeighted(P parameters) {
		return getNextRandomWeighted(parameters, false);
	}

	public T peekNextRandomWeighted(P parameters) {
		return getNextRandomWeighted(parameters, true);
	}

	private T getNextRandomWeighted(P parameters, boolean peek) {
		if (!peeked) {
			double totalWeight = stream().mapToDouble(weighted -> weighted.getWeight(parameters)).sum();
			double cursor = random.nextDouble() * totalWeight;
			if (cursor == 0) {
				for (T weighted : this) {
					if (weighted.getWeight(parameters) != 0) return weighted;
				}
			}
			for (T weighted : this) {
				cursor -= weighted.getWeight(parameters);
				if (cursor <= 0) {
					if (peek) {
						peekedRandom = weighted;
						peeked = true;
					}
					return weighted; // should never return an entry with weight 0, unless there are only weight 0 entries
				}
			}
			if (peek) {
				peekedRandom = null;
				peeked = true;
			}
			return null;
		}
		if (!peek) peeked = false;
		return peekedRandom;
	}
}
