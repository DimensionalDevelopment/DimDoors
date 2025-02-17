package org.dimdev.dimdoors.pockets.virtual.reference;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

import java.util.List;

public class IdReference extends PocketGeneratorReference {
	public static final String KEY = "id";

	public static final MapCodec<IdReference> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance)
			.and(Codec.STRING.xmap(DimensionalDoors::id, ResourceLocation::getPath).fieldOf("id").forGetter(a -> a.id)
	).apply(instance, IdReference::new));

	private ResourceLocation id;

	public IdReference(String weight, Boolean setupLoot, List<Modifier> modifierList, List<PocketAddon.PocketBuilderAddon<?, ?>> addons, ResourceLocation id) {
		super(weight, setupLoot, modifierList, addons);
		this.id = id;
	}

	@Override
	public PocketGenerator peekReferencedPocketGenerator(PocketGenerationContext parameters) {
		return getReferencedPocketGenerator(parameters);
	}

	@Override
	public PocketGenerator getReferencedPocketGenerator(PocketGenerationContext parameters) {
		return PocketLoader.getInstance().getGenerator(id);
	}

	@Override
	public VirtualPocketType<? extends ImplementedVirtualPocket> getType() {
		return VirtualPocketType.ID_REFERENCE.get();
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("id", id)
				.add("weight", weight)
				.add("weightEquation", weightEquation)
				.add("setupLoot", setupLoot)
				.add("modifierList", modifierList)
				.toString();
	}
}
