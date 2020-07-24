package org.dimdev.dimdoors.rift.targets;

public interface Target {

    // Allows a target to have a default case and forward everything
    // it doesn't handle to a different target. You should never call
    // this, it's just public because Java doesn't allow protected.
    default Target receiveOther() {
        return null;
    }

    default <T extends Target> T as(Class<T> type) {
        if (type.isAssignableFrom(getClass())) {
            return type.cast(this);
        } else {
            Target forwardTo = receiveOther();
            if (forwardTo != null) {
                return forwardTo.as(type);
            } else {
                return DefaultTargets.getDefaultTarget(type);
            }
        }
        // TODO: .as(ModTargets.CUSTOM_WRAPPER)
    }
}
