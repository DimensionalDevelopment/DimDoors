package org.dimdev.dimdoors.fabric.forge.world.level.registry.fabric;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.forge.world.level.registry.DimensionalRegistry;

public class DimensionalRegistryImpl implements ComponentV3 {
    private final boolean isOverworld;

    public DimensionalRegistryImpl(boolean isOverworld) {

        this.isOverworld = isOverworld;
    }

    @Override
    public void readFromNbt(CompoundTag compoundTag) {
        if(isOverworld) DimensionalRegistry.readFromNbt(compoundTag);
    }

    @Override
    public void writeToNbt(CompoundTag compoundTag) {
        if (isOverworld) DimensionalRegistry.writeToNbt(compoundTag);
    }

    public static DimensionalRegistryImpl createImpl(Level level) {
        return new DimensionalRegistryImpl(DimensionalRegistry.isValidWorld(level));
    }
}
