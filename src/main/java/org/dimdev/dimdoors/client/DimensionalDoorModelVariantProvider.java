package org.dimdev.dimdoors.client;

import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class DimensionalDoorModelVariantProvider implements ModelVariantProvider {
	private static final String PREFIX = "autogen_";

	@Override
	public @Nullable UnbakedModel loadModelVariant(ModelIdentifier modelId, ModelProviderContext context) throws ModelProviderException {
		String path = modelId.getPath();
		if (!path.startsWith(PREFIX)) return null;
		String[] separatedId = path.substring(PREFIX.length()).split("_dimdoors_");
		ModelIdentifier newId = new ModelIdentifier(new Identifier(separatedId[0], separatedId[1]), modelId.getVariant());
		return context.loadModel(newId);
	}
}
