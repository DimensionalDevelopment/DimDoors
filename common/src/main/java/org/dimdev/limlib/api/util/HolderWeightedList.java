package org.dimdev.limlib.api.util;

import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.Collection;

public class HolderWeightedList<T extends Weighted<C>, C> extends ArrayList<Holder<T>> {
    private final RandomSource random = RandomSource.create();
    private Holder<T> peekedRandom;
    private boolean peeked = false;

    public HolderWeightedList() {
    }

    public HolderWeightedList(Collection<? extends Holder<T>> c) {
        super(c);
    }

    public Holder<T> getNextRandomWeighted(C context) {
        return this.getNextRandomWeighted(context, false);
    }

    public Holder<T> peekNextRandomWeighted(C context) {
        return this.getNextRandomWeighted(context, true);
    }

    private Holder<T> getNextRandomWeighted(C context, boolean peek) {
        if (!this.peeked) {
            double cursor = this.random.nextDouble() * getTotalWeight(context);
            if (cursor == 0) {
                for (Holder<T> weighted : this) {
                    if (weighted.value().getWeight(context) != 0) return weighted;
                }
            }
            for (Holder<T> weighted : this) {
                cursor -= weighted.value().getWeight(context);
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
        return this.stream().map(Holder::value).mapToDouble(weighted -> weighted.getWeight(context)).sum();
    }
}