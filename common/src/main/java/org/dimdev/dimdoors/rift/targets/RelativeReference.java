package org.dimdev.dimdoors.rift.targets;

import org.dimdev.dimdoors.rift.registry.RiftRegistry;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Vec3i;
import org.dimdev.dimdoors.api.rift.target.Target;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.RGBA;

import java.util.Set;

public class RelativeReference extends RiftReference {
    public static MapCodec<RelativeReference> CODEC = Vec3i.CODEC.fieldOf("offset").xmap(RelativeReference::new, a -> a.pos);

    private final Vec3i pos;

    public RelativeReference(Vec3i pos) {
        super(null);
        this.pos = pos;
    }


    @Override
    public Location getLocation() {
        return location == null ? null : new Location(location.world, location.pos.offset(pos));
    }

    @Override
    public Target receiveOther() {
        return this.resolveTarget(this.getLocation());
    }

    @Override
    public void register() {
        RiftRegistry.getInstance().addLink(this.location, this.getLocation());
    }

    @Override
    public void unregister() {
        if (this.location != null)
            RiftRegistry.getInstance().removeLink(this.location, getLocation());
    }

    @Override
    public boolean shouldInvalidate(Location deletedRift) {
        // A rift we may have asked the registry to notify us about was deleted
        return deletedRift.equals(this.getLocation());
    }

    @Override
    public RGBA getColor() {

        if (getLocation() != null && RiftRegistry.getInstance().isRiftAt(getLocation())) {
            Set<Location> otherRiftTargets = RiftRegistry.getInstance().getTargets(getLocation());
            if (otherRiftTargets.size() == 1 && otherRiftTargets.contains(this.location)) {
                return new RGBA(0, 1, 0, 1);
            }
        }
        return new RGBA(1, 0, 0, 1);
    }
}
