package org.dimdev.dimdoors.api.util;

import java.util.NoSuchElementException;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;

public final class OptionalBool {
    private static final OptionalBool EMPTY = new OptionalBool();
    public static final OptionalBool TRUE = new OptionalBool(true);
	public static final OptionalBool FALSE = new OptionalBool(false);
    private final boolean present;
    private final boolean value;

    private OptionalBool() {
        this.present = false;
        this.value = false;
    }

    public static OptionalBool empty() {
        return EMPTY;
    }

    private OptionalBool(boolean value) {
        this.present = true;
        this.value = value;
    }

    public static OptionalBool of(boolean value) {
        return value ? TRUE : FALSE;
    }

    public boolean getAsBool() {
        if (!present) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    public boolean isPresent() {
        return present;
    }

    public void ifPresent(BooleanConsumer action) {
        if (present) {
            action.accept(value);
        }
    }

	public void ifPresentAndTrue(Runnable action) {
		if (present && value) action.run();
	}


	@Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof OptionalBool)) {
            return false;
        }

        OptionalBool other = (OptionalBool) obj;
        return (present && other.present) ? value == other.value : present == other.present;
    }

    @Override
    public int hashCode() {
        return present ? Boolean.hashCode(value) : 0;
    }

    @Override
    public String toString() {
        return present ? String.format("OptionalBool[%s]", value) : "OptionalBool.empty";
    }
}
