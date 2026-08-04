package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import org.dimdev.dimdoors.tag.ModWorldTags;
import org.dimdev.dimdoors.world.ModDimensions;

import java.util.concurrent.CompletableFuture;

public class LevelTagProvider extends TagsProvider<DimensionType> {
    public LevelTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.DIMENSION_TYPE, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModWorldTags.MONOLITHS_CAN_EXIST).add(ModDimensions.POCKET_TYPE_KEY, ModDimensions.LIMBO_TYPE_KEY);

        tag(ModWorldTags.UNRAVELLED_FABRIC_CAN_UNRAVEL).add(ModDimensions.LIMBO_TYPE_KEY).add(BuiltinDimensionTypes.END);
    }
}
