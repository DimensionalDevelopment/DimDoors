package org.dimdev.dimdoors.world.pocket.type;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.world.pocket.type.addon.DyeableAddon;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

import java.util.List;

public class PrivatePocket extends Pocket<PrivatePocket, PrivatePocket.PrivatePocketBuilder> implements DyeableAddon.DyeablePocket {
    public static String KEY = "private_pocket";

    public static final MapCodec<PrivatePocket> CODEC = RecordCodecBuilder.mapCodec(instance -> commonPocketFields(instance).apply(instance, PrivatePocket::new));

    public PrivatePocket(int id, ResourceKey<Level> world, int range, BoundingBox box, VirtualLocation virtualLocation, List<PocketAddon> addons) {
        super(id, world, range, box, virtualLocation, addons);
    }

    public PrivatePocket() {}

    public static PrivatePocketBuilder builderPrivatePocket() {
        return new PrivatePocketBuilder();
    }

    public static class PrivatePocketBuilder extends PocketBuilder<PrivatePocket, PrivatePocketBuilder> implements DyeableAddon.DyeablePocketBuilder<PrivatePocket, PrivatePocketBuilder> {
        public static final MapCodec<PrivatePocketBuilder> CODEC = RecordCodecBuilder.mapCodec(instance -> PocketBuilder.commonFields(instance).apply(instance, PrivatePocketBuilder::new));

        protected PrivatePocketBuilder(List<PocketAddon.PocketBuilderAddon<?, ?>> addons) {
            super(addons);
        }

        protected PrivatePocketBuilder() {
            super();
        }

        @Override
        PrivatePocketBuilder instance() {
            return builderPrivatePocket();
        }

        @Override
        public AbstractPocketType<PrivatePocket, PrivatePocketBuilder> type() {
            return AbstractPocketType.PRIVATE_POCKET;
        }

        @Override
        public void initAddons() {
            super.initAddons();
            addAddon(new DyeableAddon.DyeableBuilderAddon());
            this.dyeColor(PocketColor.WHITE);
        }
    }

    @Override
    public AbstractPocketType<PrivatePocket, PrivatePocketBuilder> getType() {
        return AbstractPocketType.PRIVATE_POCKET;
    }
}