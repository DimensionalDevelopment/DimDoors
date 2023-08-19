package org.dimdev.dimdoors.client;

import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.EntityType;
import org.dimdev.dimdoors.DimensionalDoors;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ModEntityModelLayers {
	public static ModelLayerLocation MONOLITH = new ModelLayerLocation(DimensionalDoors.id("monolith"), "body");

	public static void initClient(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> consumer) {
		consumer.accept(MONOLITH, MonolithModel::getTexturedModelData);
	}
}
