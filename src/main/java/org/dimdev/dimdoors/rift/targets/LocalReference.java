package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.Codec;
import org.dimdev.annotatednbt.AnnotatedNbt;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.dimdoors.util.Location;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;

public class LocalReference extends RiftReference {
    public static final Codec<LocalReference> CODEC = BlockPos.field_25064.xmap(LocalReference::new, LocalReference::getTarget).fieldOf("target").codec();

    @Saved
    protected BlockPos target;

    public LocalReference(BlockPos target) {
        this.target = target;
    }

    @Override
    public Location getReferencedLocation() {
        return new Location(location.world, target);
    }

    public BlockPos getTarget() {
        return target;
    }

    @Override
    public VirtualTargetType<? extends VirtualTarget> getType() {
        return VirtualTargetType.LOCAL;
    }
}
