package org.dimdev.dimdoors.util;

import com.google.common.collect.Lists;

import java.util.*;

public class WeightedList<T extends Weighted<P>, P> extends ArrayList<T> {
	private final Random random = new Random();

	public WeightedList() {
	}

	public T getRandomWeighted(P parameters) {
		int totalWeight = stream().mapToInt(weighted -> weighted.getWeight(parameters)).sum();
		int cursor = random.nextInt(totalWeight);
		for (T weighted : this) {
			cursor -= weighted.getWeight(parameters);
			if (cursor <= 0) {
				return weighted; // should never return an entry with weight 0, unless there are only weight 0 entries
			}
		}
		return null;
	}
}
