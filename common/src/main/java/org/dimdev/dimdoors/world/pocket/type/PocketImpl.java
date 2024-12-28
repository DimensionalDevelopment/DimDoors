package org.dimdev.dimdoors.world.pocket.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

import java.util.Map;

public class PocketImpl extends Pocket {
    public static final String KEY = "pocket";

    public static final MapCodec<PocketImpl> CODEC = RecordCodecBuilder.mapCodec(instance -> commonPocketFields(instance).apply(instance, PocketImpl::new));

    public PocketImpl() {
        super();
    }
    
    public PocketImpl(int id, ResourceKey<Level> world, int range, BoundingBox box, VirtualLocation virtualLocation, Map<ResourceLocation, PocketAddon> addons) {
        super(id, world, range, box, virtualLocation, addons);
    }

    @Override
    public AbstractPocketType<?, ?> getType() {
        return AbstractPocketType.POCKET.get();
    }

    @Override
    public void ensureIsPocket() {
        super.ensureIsPocket();
    }

    public static class PocketImplBuilder extends PocketBuilder<PocketImplBuilder, PocketImpl> {
        public static final MapCodec<PocketImplBuilder> CODEC = RecordCodecBuilder.<PocketImplBuilder>mapCodec(instance -> commonPocketBuilderFields(instance).apply(instance, PocketImplBuilder::configure));



        @Override
        public AbstractPocketType<PocketImpl, PocketImplBuilder> getType() {
            return AbstractPocketType.POCKET.get();
        }

        private static PocketImplBuilder configure(int id, ResourceKey<Level> world, Vec3i origin, Vec3i size, VirtualLocation virtualLocation, int range) {
            return new PocketImplBuilder().id(id).world(world).offsetOrigin(origin).expand(size).range(range).virtualLocation(virtualLocation);
        }
    }
}
