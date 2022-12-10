package org.dimdev.dimdoors.client;

import net.minecraft.client.render.entity.model.EntityModelLayer;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;

import org.dimdev.dimdoors.DimensionalDoors;

public class ModEntityModelLayers {
	public static EntityModelLayer MONOLITH = new EntityModelLayer(DimensionalDoors.id("monolith"), "body");

	public static void initClient() {
		EntityModelLayerRegistry.registerModelLayer(MONOLITH, MonolithModel::getTexturedModelData);
	}
}
