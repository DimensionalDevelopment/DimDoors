package org.dimdev.dimdoors.shared.rifts;

import net.minecraft.util.math.Vec3i;
import org.dimdev.ddutils.Location;
import org.dimdev.dimdoors.shared.rifts.destinations.GlobalDestination;
import org.dimdev.dimdoors.shared.rifts.destinations.LocalDestination;
import org.dimdev.dimdoors.shared.rifts.destinations.RelativeDestination;

public final class DestinationMaker {
    public static RiftDestination localIfPossible(Location from, Location to) {
        if (from.getDim() != to.getDim()) {
            return new GlobalDestination(to);
        } else {
            return new LocalDestination(to.getPos());
        }
    }

    public static RiftDestination relativeIfPossible(Location from, Location to) {
        if (from.getDim() != to.getDim()) {
            return new GlobalDestination(to);
        } else {
            return new RelativeDestination(new Vec3i(to.getX() - from.getX(), to.getY() - from.getY(), to.getZ() - from.getZ()));
        }
    }
}
