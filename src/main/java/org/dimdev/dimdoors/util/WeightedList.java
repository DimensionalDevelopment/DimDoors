package org.dimdev.dimdoors.util;

import java.util.*;

public class WeightedList<T extends Weighted<P>, P> extends ArrayList<T> {
	private final Random random = new Random();
	private T peekedRandom;
	private boolean peeked = false;

	public WeightedList() {
	}

	public T getNextRandomWeighted(P parameters) {
		return getNextRandomWeighted(parameters, false);
	}

	public T peekNextRandomWeighted(P parameters) {
		return getNextRandomWeighted(parameters, true);
	}

	private T getNextRandomWeighted(P parameters, boolean peek) {
		if (!peeked) {
			int totalWeight = stream().mapToInt(weighted -> weighted.getWeight(parameters)).sum();
			int cursor = random.nextInt(totalWeight);
			for (T weighted : this) {
				cursor -= weighted.getWeight(parameters);
				if (cursor <= 0) {
					if (peek) {
						peekedRandom = weighted;
						peeked = true;
					}
					return weighted; // should never return an entry with weight 0, unless there are only weight 0 entries
				} // TODO: fix bug, if first entry has weight 0 and random.nextInt(totalWeight) is 0, then it will return first entry
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
