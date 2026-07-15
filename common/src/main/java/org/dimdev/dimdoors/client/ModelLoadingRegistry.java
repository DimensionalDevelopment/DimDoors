package org.dimdev.dimdoors.client;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public interface ModelLoadingRegistry {
    void replaceBlockStates(Block block);

    void replaceModel(ModelResourceLocation model);

    default void replaceItem(ResourceLocation itemId) {
        replaceModel(ModelResourceLocation.inventory(itemId));
    }
}
