package org.dimdev.dimdoors.client;

import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.render.entity.model.EntityModelLayer;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;

import org.dimdev.dimdoors.DimensionalDoors;

public class ModEntityModelLayers {
	public static ModelLayerLocation MONOLITH = new ModelLayerLocation(DimensionalDoors.id("monolith"), "body");

	public static void initClient() {
		EntityModelLayerRegistry.register(MONOLITH, MonolithModel::getTexturedModelData);
	}
}
