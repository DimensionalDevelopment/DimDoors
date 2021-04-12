package org.dimdev.dimdoors.client;

import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar;
import org.jetbrains.annotations.Nullable;

public class DimensionalDoorModelVariantProvider implements ModelVariantProvider {
	@Override
	public @Nullable UnbakedModel loadModelVariant(ModelIdentifier modelId, ModelProviderContext context) throws ModelProviderException {
		Identifier identifier = new Identifier(modelId.getNamespace(), modelId.getPath());
		DimensionalDoorBlockRegistrar registrar = DimensionalDoorsInitializer.getDimensionalDoorBlockRegistrar();
		if (!registrar.isMapped(identifier)) return null;
		Identifier mapped = registrar.get(identifier);
		ModelIdentifier newId = new ModelIdentifier(mapped, modelId.getVariant());
		return context.loadModel(newId);
	}
}
