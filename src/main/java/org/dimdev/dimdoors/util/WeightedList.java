package org.dimdev.dimdoors.util;

import com.google.common.collect.Lists;

import java.util.*;

public class WeightedList<T extends Weighted<P>, P> {
	private final List<T> list;
	private final Random random = new Random();

	public WeightedList() {
		this.list = Lists.newArrayList();
	}

	public T getRandomWeighted(P parameters) {
		int totalWeight = list.stream().mapToInt(weighted -> weighted.getWeight(parameters)).sum();
		int cursor = random.nextInt(totalWeight);
		for (T weighted : list) {
			cursor -= weighted.getWeight(parameters);
			if (cursor <= 0) {
				return weighted; // should never return an entry with weight 0, unless there are only weight 0 entries
			}
		}
		return null;
	}

	public boolean add(T t) {
		return list.add(t);
	}

	public boolean remove(T t){
		return list.remove(t);
	}

	// TODO: make weightedList implement List instead
	public List<T> getList() {
		return list;
	}
}
