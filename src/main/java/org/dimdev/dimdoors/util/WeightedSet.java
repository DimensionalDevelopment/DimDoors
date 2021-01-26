package org.dimdev.dimdoors.util;

import net.minecraft.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class WeightedSet<T> {
	private final TreeSet<Pair<T, Integer>> set;
	private final int defaultWeight;
	private int totalWeight = 0;
	private boolean dirty = false;
	private final Random random = new Random();

	public WeightedSet() {
		this(1);
	}

	//TODO: ensure default Weight is >= 0?
	public WeightedSet(int defaultWeight) {
		this.set = new TreeSet<>((pair1, pair2) -> pair2.getRight().compareTo(pair1.getRight()));
		this.defaultWeight = defaultWeight;
	}

	private void markDirty() {
		dirty = true;
	}

	private void updateTotalWeight() {
		if (dirty) totalWeight = set.stream().mapToInt(Pair::getRight).sum();
	}

	public T getRandomWeighted() {
		updateTotalWeight();
		int cursor = random.nextInt(totalWeight);
		for (Pair<T, Integer> pair : set) {
			cursor -= pair.getRight();
			if (cursor <= 0) {
				return pair.getLeft(); // should never return an entry with weight 0, unless there are only weight 0 entries
			}
		}
		throw new RuntimeException(); // either the list is empty, or it somehow
	}

	// TODO: ensure weight is >= 0? How about a negativeWeightException?
	public boolean add(T t, Integer weight) {
		if (set.add(new Pair<>(t, weight))) {
			markDirty();
			return true;
		}
		return false;
	}

	public boolean add(T t) {
		return add(t, defaultWeight);
	}

	public boolean remove(T t){
		if (set.remove(set.stream().filter(pair -> pair.getLeft().equals(t)).findFirst().orElse(null))) {
			markDirty();
			return true;
		}
		return false;
	}

	public List<T> getObjectList() {
		return set.stream().map(Pair::getLeft).collect(Collectors.toList());
	}
}
