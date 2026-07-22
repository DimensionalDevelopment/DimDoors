package org.dimdev.dimdoors.compat.create.target;

import org.dimdev.dimdoors.api.rift.target.Target;

public interface KineticTarget extends Target {
    float getStressCapacity();
    float getRotationalSpeed();
}
