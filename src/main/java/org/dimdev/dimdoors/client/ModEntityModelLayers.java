package org.dimdev.dimdoors.client;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModEntityModelLayers {
	public static ModelLayerLocation MONOLITH = new ModelLayerLocation(DimensionalDoors.resource("monolith"), "body");

	public static void initClient() {
		EntityModelLayerRegistry.registerModelLayer(MONOLITH, MonolithModel::getTexturedModelData);
	}
}
