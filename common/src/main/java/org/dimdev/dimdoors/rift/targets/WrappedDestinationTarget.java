package org.dimdev.dimdoors.rift.targets;

public abstract class WrappedDestinationTarget extends RestoringTarget {
    protected VirtualTarget wrappedDestination = null;

    public WrappedDestinationTarget(VirtualTarget wrappedDestination) {
        this.wrappedDestination = wrappedDestination;
    }

    public WrappedDestinationTarget() {
    }

    @Override
    protected VirtualTarget getTarget() {
        return this.wrappedDestination;
    }

    @Override
    protected void setTarget(VirtualTarget target) {
        this.wrappedDestination = target;
    }
}
