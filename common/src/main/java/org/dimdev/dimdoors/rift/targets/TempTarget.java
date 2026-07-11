package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.dimdev.dimdoors.api.rift.target.Target;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.block.RiftVariantProvider;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftData;

public class TempTarget extends VirtualTarget<TempTarget> {
    public static final MapCodec<TempTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            VirtualTarget.CODEC.fieldOf("temp").forGetter(a -> a.temp),
            VirtualTarget.CODEC.fieldOf("original").forGetter(a -> a.original)
    ).apply(instance, TempTarget::new));

    private final VirtualTarget<?> temp;
    private final VirtualTarget<?> original;

    public TempTarget(VirtualTarget<?> temp, VirtualTarget<?> original) {
        this.temp = temp;
        this.original = original;
    }

    @Override
    public VirtualTargetType<TempTarget> getType() {
        return VirtualTargetType.TEMP;
    }

    @Override
    public void setLocation(Location location) {
        super.setLocation(location);
        temp.setLocation(location);
    }

    @Override
    public Target receiveOther() {
        if(this.getLocation() != null && this.getLocation().getBlockEntity() instanceof RiftBlockEntity rift) {
            if(original == NoneTarget.INSTANCE && rift.getBlockState().getBlock() instanceof RiftVariantProvider provider) {
                provider.revertToBaseVariant(this.getLocation().getWorld(), rift.getBlockPos(), rift.getBlockState());
            } else {
                rift.setDestination(original);
            }
        }

        return temp;
    }

    @Override
    public void register() {
        super.register();
    }

    @Override
    public TempTarget copy() {
        return new TempTarget(temp, original);
    }
}
