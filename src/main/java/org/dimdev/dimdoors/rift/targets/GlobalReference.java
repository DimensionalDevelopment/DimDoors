package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.Codec;
import org.dimdev.dimdoors.util.Location;

public class GlobalReference extends RiftReference {
    public static Codec<GlobalReference> CODEC = Location.CODEC.fieldOf("location").xmap(GlobalReference::new, GlobalReference::getReferencedLocation).codec();

    protected Location target;

    public GlobalReference(Location target) {
        this.target = target;
    }

    @Override
    public Location getReferencedLocation() {
        return target;
    }

    @Override
    public VirtualTargetType<? extends VirtualTarget> getType() {
        return VirtualTargetType.GLOBAL;
    }
}
