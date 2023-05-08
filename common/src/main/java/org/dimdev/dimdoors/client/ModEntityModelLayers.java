package org.dimdev.dimdoors.client;

import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModEntityModelLayers {
	public static ModelLayerLocation MONOLITH = new ModelLayerLocation(DimensionalDoors.id("monolith"), "body");

	public static void initClient() {
		EntityModelLayerRegistry.register(MONOLITH, MonolithModel::getTexturedModelData);
	}
}
