package org.dimdev.dimdoors.shared.rifts.targets;

import java.util.Objects;

public interface ITarget {

    // Allows a target to have a default case and forward everything
    // it doesn't handle to a different target. You should never call
    // this, it's just public because Java doesn't allow protected.
    default ITarget receiveOther() {
        return null;
    }

    default <T extends ITarget> T as(Class<T> type) {
        if (type.isAssignableFrom(getClass())) return type.cast(this);
        else {
            ITarget forwardTo = receiveOther();
            if (Objects.nonNull(forwardTo)) return forwardTo.as(type);
            else return DefaultTargets.getDefaultTarget(type);
        }
        // TODO: .as(ModTargets.CUSTOM_WRAPPER)
    }
}
