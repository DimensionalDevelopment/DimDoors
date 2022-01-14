package org.dimdev.dimdoors.client;

import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;

public class ModEntityModelLayers {
	public static EntityModelLayer MONOLITH = new EntityModelLayer(new Identifier("dimdoors:monolith"), "body");

	public static void initClient() {
		EntityModelLayerRegistry.registerModelLayer(MONOLITH, MonolithModel::getTexturedModelData);
	}
}
