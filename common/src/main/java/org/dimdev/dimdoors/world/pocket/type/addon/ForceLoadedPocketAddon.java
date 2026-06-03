package org.dimdev.dimdoors.world.pocket.type.addon;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public final class ForceLoadedPocketAddon implements PocketAddon {
    public static final Identifier ID = DimensionalDoors.id("force_loaded");
    private static final ForceLoadedPocketAddon INSTANCE = new ForceLoadedPocketAddon();
    public static final MapCodec<ForceLoadedPocketAddon> CODEC = MapCodec.unit(INSTANCE);

    public static ForceLoadedPocketAddon instance() {
        return INSTANCE;
    }

    private ForceLoadedPocketAddon() {
    }

    @Override
    public PocketAddonType<?, ?> getType() {
        return PocketAddonType.FORCE_LOADED_ADDON;
    }

    public static class BuilderAddon implements PocketBuilderAddon<ForceLoadedPocketAddon, BuilderAddon> {
        public static final MapCodec<BuilderAddon> CODEC = MapCodec.unit(BuilderAddon::new);

        @Override
        public void apply(Pocket<?, ?> pocket) {
            pocket.addAddon(ForceLoadedPocketAddon.instance());
        }

        @Override
        public PocketAddonType<ForceLoadedPocketAddon, BuilderAddon> getType() {
            return PocketAddonType.FORCE_LOADED_ADDON;
        }
    }
}
