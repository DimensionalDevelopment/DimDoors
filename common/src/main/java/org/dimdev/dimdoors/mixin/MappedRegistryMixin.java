package org.dimdev.dimdoors.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Mixin(MappedRegistry.class)
public class MappedRegistryMixin<T> {

    @Shadow
    @Final
    private ResourceKey<? extends Registry<T>> key;

    @ModifyVariable(method = "bindTags", at = @At("HEAD"), argsOnly = true)
    public Map<TagKey<T>, List<Holder<T>>> modifyBindTags(Map<TagKey<T>, List<Holder<T>>> map) {

        if(key.equals(Registries.BLOCK)) {
            return (Map<TagKey<T>, List<Holder<T>>>) (Object) DimensionalDoors.getDimensionalDoorBlockRegistrar().populateTags((Map<TagKey<Block>, List<Holder<Block>>>) (Object) map);
        }

        return map;
    }
}
