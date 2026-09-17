package org.dimdev.dimcore.api.transfer;

/** A quantity of a transferable resource, in the loader's native unit. */
public interface Unit<U extends Unit<U>> {
    long amount();

    U withAmount(long amount);

    /** Identity ignoring amount. */
    boolean sameResource(U other);

    default boolean isEmpty() {
        return amount() <= 0;
    }
}
