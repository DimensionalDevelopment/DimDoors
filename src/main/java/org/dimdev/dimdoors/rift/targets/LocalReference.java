package org.dimdev.dimdoors.rift.targets;

import org.dimdev.dimdoors.util.Location;
import com.mojang.serialization.Codec;

import net.minecraft.util.math.BlockPos;

public class LocalReference extends RiftReference {
    public static final Codec<LocalReference> CODEC = BlockPos.CODEC.xmap(LocalReference::new, LocalReference::getTarget).fieldOf("target").codec();

    protected BlockPos target;

    public LocalReference(BlockPos target) {
        this.target = target;
    }

    @Override
    public Location getReferencedLocation() {
        return new Location(this.location.world, this.target);
    }

    public BlockPos getTarget() {
        return this.target;
    }

    @Override
    public VirtualTargetType<? extends VirtualTarget> getType() {
        return VirtualTargetType.LOCAL;
    }
}
