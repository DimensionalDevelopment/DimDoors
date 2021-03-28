package org.dimdev.dimdoors.api.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class WeightedList<T extends Weighted<C>, C> extends ArrayList<T> {
	private final Random random = new Random();
	private T peekedRandom;
	private boolean peeked = false;

	public WeightedList() {
	}

	public WeightedList(Collection<? extends T> c) {
		super(c);
	}

	public T getNextRandomWeighted(C context) {
		return this.getNextRandomWeighted(context, false);
	}

	public T peekNextRandomWeighted(C context) {
		return this.getNextRandomWeighted(context, true);
	}

	private T getNextRandomWeighted(C context, boolean peek) {
		if (!this.peeked) {
			double cursor = this.random.nextDouble() * getTotalWeight(context);
			if (cursor == 0) {
				for (T weighted : this) {
					if (weighted.getWeight(context) != 0) return weighted;
				}
			}
			for (T weighted : this) {
				cursor -= weighted.getWeight(context);
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

	public double getTotalWeight(C context) {
		return this.stream().mapToDouble(weighted -> weighted.getWeight(context)).sum();
	}
}
