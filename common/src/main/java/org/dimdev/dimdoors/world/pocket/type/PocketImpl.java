package org.dimdev.dimdoors.world.pocket.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

import java.util.List;

public class PocketImpl extends Pocket<PocketImpl, PocketImpl.Builder> {
    public static final MapCodec<PocketImpl> CODEC = RecordCodecBuilder.mapCodec(instance -> commonPocketFields(instance).apply(instance, PocketImpl::new));

    public PocketImpl(int id, ResourceKey<Level> world, int range, BoundingBox box, VirtualLocation virtualLocation, List<PocketAddon> addons) {
        super(id, world, range, box, virtualLocation, addons);
    }

    public PocketImpl() {
        super();
    }

    @Override
    public AbstractPocketType<PocketImpl, Builder> getType() {
        return AbstractPocketType.POCKET;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder extends PocketBuilder<PocketImpl, Builder> {
        public static final MapCodec<Builder> CODEC = RecordCodecBuilder.mapCodec(instance -> PocketBuilder.commonFields(instance).apply(instance, Builder::new));

        public Builder(List<PocketAddon.PocketBuilderAddon<?, ?>> addons) {
            super(addons);
        }

        public Builder() {
            super();
        }

        @Override
        public AbstractPocketType<PocketImpl, Builder> type() {
            return AbstractPocketType.POCKET;
        }

        @Override
        Builder instance() {
            return builder();
        }
    }
}
