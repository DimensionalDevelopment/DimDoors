package org.dimdev.dimdoors.rift.targets;

import java.util.Objects;

public abstract class WrappedDestinationTarget<T extends WrappedDestinationTarget<T>> extends RestoringTarget<T> {
    protected VirtualTarget<?> wrappedDestination = NoneTarget.INSTANCE;

    public WrappedDestinationTarget(VirtualTarget<?> wrappedDestination) {
        this.setTarget(wrappedDestination);
    }

    public WrappedDestinationTarget() {
    }

    @Override
    protected VirtualTarget<?> getTarget() {
        return this.wrappedDestination;
    }

    @Override
    protected void setTarget(VirtualTarget<?> target) {
        this.wrappedDestination = Objects.requireNonNullElse(target, NoneTarget.INSTANCE);
    }
}