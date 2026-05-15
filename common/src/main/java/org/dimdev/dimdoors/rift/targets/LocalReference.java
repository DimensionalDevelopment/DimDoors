package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import org.dimdev.dimdoors.api.rift.target.Target;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.RGBA;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;

import java.util.Set;

public class LocalReference extends RiftReference {
    public static MapCodec<LocalReference> CODEC = BlockPos.CODEC.fieldOf("target").xmap(LocalReference::new, a -> a.pos);

    private final BlockPos pos;

    private Location target;

    public LocalReference(BlockPos pos) {
        super(null);
        this.pos = pos;
    }


    @Override
    public Location getLocation() {
        if(location != null && target == null) {
            target = new Location(location.world, pos);
        }
        return target;
    }

    @Override
    public Target receiveOther() {
        return this.getLocation().getBlockEntity() instanceof Target beTarget ? beTarget : null;
    }

    @Override
    public void register() {
        DimensionalRegistry.getRiftRegistry().addLink(this.location, this.getLocation());
    }

    @Override
    public void unregister() {
        if (this.location != null)
            DimensionalRegistry.getRiftRegistry().removeLink(this.location, getLocation());
    }

    @Override
    public boolean shouldInvalidate(Location deletedRift) {
        // A rift we may have asked the registry to notify us about was deleted
        return deletedRift.equals(this.getLocation());
    }

    @Override
    public RGBA getColor() {

        if (getLocation() != null && DimensionalRegistry.getRiftRegistry().isRiftAt(getLocation())) {
            Set<Location> otherRiftTargets = DimensionalRegistry.getRiftRegistry().getTargets(getLocation());
            if (otherRiftTargets.size() == 1 && otherRiftTargets.contains(this.location)) {
                return new RGBA(0, 1, 0, 1);
            }
        }
        return new RGBA(1, 0, 0, 1);
    }
}
