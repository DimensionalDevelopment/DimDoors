package org.dimdev.dimdoors.mixin.client.accessor;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

import net.minecraft.client.render.DimensionEffects;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DimensionEffects.class)
public interface SkyPropertiesAccessor {
    @Accessor("BY_IDENTIFIER")
    static Object2ObjectMap<Identifier, DimensionEffects> getIdMap() {
        throw new AssertionError();
    }
}
