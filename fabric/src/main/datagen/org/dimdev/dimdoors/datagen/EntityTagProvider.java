package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.tag.ModEntityTypeTags;

import java.util.concurrent.CompletableFuture;

public class EntityTagProvider extends TagsProvider<EntityType<?>> {
    public EntityTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.ENTITY_TYPE, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModEntityTypeTags.IMMUNE_TO_FARSHOT_SWAP).add(key("monolith"));
    }

    public static ResourceKey<EntityType<?>> key(String name) {
        return ResourceKey.create(Registries.ENTITY_TYPE, DimensionalDoors.id(name));
    }
}

