package org.dimdev.dimcore.api.transfer;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Loader-neutral handle onto something that holds a resource. Already side-resolved; obtained
 * through {@link TransferType#find} or handed out through {@link TransferType#expose}.
 */
public interface Handle<U extends Unit<U>> {
    /** @return amount actually accepted */
    long insert(U unit, boolean simulate);

    /** @return amount of exactly that resource actually removed */
    long extract(U unit, boolean simulate);

    /**
     * What is currently held, one entry per slot / tank, in a stable order. Extractors read this
     * before pulling, so a handle that reports nothing here will never be pulled from.
     */
    List<U> contents();

    /** Remove whatever is available, up to {@code amount}. @return what came out, or null if nothing */
    default @Nullable U extractAny(long amount, boolean simulate) {
        for (U held : contents()) {
            if (held.isEmpty()) {
                continue;
            }
            long taken = extract(held.withAmount(Math.min(amount, held.amount())), simulate);
            if (taken > 0) {
                return held.withAmount(taken);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    static <U extends Unit<U>> Handle<U> empty() {
        return (Handle<U>) Empty.INSTANCE;
    }

    enum Empty implements Handle<FluidUnit> {
        INSTANCE;

        @Override
        public long insert(FluidUnit unit, boolean simulate) {
            return 0;
        }

        @Override
        public long extract(FluidUnit unit, boolean simulate) {
            return 0;
        }

        @Override
        public List<FluidUnit> contents() {
            return List.of();
        }
    }
}
