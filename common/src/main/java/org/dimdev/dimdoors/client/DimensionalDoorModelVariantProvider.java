package org.dimdev.dimdoors.client;

import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar;
import org.dimdev.dimdoors.item.door.DimensionalDoorItemRegistrar;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

public class DimensionalDoorModelVariantProvider implements ModelVariantProvider {
    private static final ResourceLocation childItem = DimensionalDoors.id("item/child_item");

    @Override
    public @Nullable UnbakedModel loadModelVariant(ModelResourceLocation modelId, ModelProviderContext context) throws ModelProviderException {
        ResourceLocation identifier = new ResourceLocation(modelId.getNamespace(), modelId.getPath());

        DimensionalDoorBlockRegistrar blockRegistrar = DimensionalDoors.getDimensionalDoorBlockRegistrar();
        if (blockRegistrar.isMapped(identifier)) {
            ResourceLocation mapped = blockRegistrar.get(identifier);
            //ModelIdentifier newId = new ModelIdentifier(mapped, modelId.getVariant());
            //UnbakedModel model = context.loadModel(newId);
            //if (model != null) return model;

            Block original = Registry.BLOCK.get(mapped);
            Set<String> originalProperties = original.getStateDefinition().getProperties().stream().map(Property::getName).collect(Collectors.toSet());

            ArrayList<String> variantArray = new ArrayList<>();
            for (String part : modelId.getVariant().split(",")) {
                if (originalProperties.contains(part.split("=")[0])) variantArray.add(part);
            }
            String variant = String.join(",", variantArray);
            ModelResourceLocation newId = new ModelResourceLocation(mapped, variant);
            return context.loadModel(newId);
        } else if (identifier.getPath().startsWith(DimensionalDoorItemRegistrar.PREFIX)) {
            return context.loadModel(childItem);
        }
        return null;
    }
}