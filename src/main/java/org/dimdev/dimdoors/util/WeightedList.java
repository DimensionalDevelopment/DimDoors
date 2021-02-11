package org.dimdev.dimdoors.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class WeightedList<T extends Weighted<P>, P> extends ArrayList<T> {
	private final Random random = new Random();
	private T peekedRandom;
	private boolean peeked = false;

	public WeightedList() {
	}

	public WeightedList(Collection<? extends T> c) {
		super(c);
	}

	public T getNextRandomWeighted(P parameters) {
		return this.getNextRandomWeighted(parameters, false);
	}

	public T peekNextRandomWeighted(P parameters) {
		return this.getNextRandomWeighted(parameters, true);
	}

	private T getNextRandomWeighted(P parameters, boolean peek) {
		if (!this.peeked) {
			double totalWeight = this.stream().mapToDouble(weighted -> weighted.getWeight(parameters)).sum();
			double cursor = this.random.nextDouble() * totalWeight;
			if (cursor == 0) {
				for (T weighted : this) {
					if (weighted.getWeight(parameters) != 0) return weighted;
				}
			}
			for (T weighted : this) {
				cursor -= weighted.getWeight(parameters);
				if (cursor <= 0) {
					if (peek) {
						this.peekedRandom = weighted;
						this.peeked = true;
					}
					return weighted; // should never return an entry with weight 0, unless there are only weight 0 entries
				}
			}
			if (peek) {
				this.peekedRandom = null;
				this.peeked = true;
			}
			return null;
		}
		if (!peek) this.peeked = false;
		return this.peekedRandom;
	}
}
