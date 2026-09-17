package org.dimdev.dimdoors.api.rift.target;

import net.minecraft.core.Direction;
import org.dimdev.dimdoors.api.util.Location;

public interface RedstoneTarget extends Target {
    boolean recieveSignal(int strength, Location location);

    default int getSignal(Location location) {
        return 0;
    }
}
