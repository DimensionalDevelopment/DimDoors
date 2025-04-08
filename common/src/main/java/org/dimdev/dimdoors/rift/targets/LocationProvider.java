package org.dimdev.dimdoors.rift.targets;

import org.dimdev.dimdoors.api.util.Location;

@FunctionalInterface
public interface LocationProvider {
    Location getLocation();
}
