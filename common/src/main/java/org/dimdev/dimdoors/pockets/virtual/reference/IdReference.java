package org.dimdev.dimdoors.pockets.virtual.reference;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;

import java.util.List;

public class IdReference extends PocketGeneratorReference {
	public static MapCodec<IdReference> CODEC = RecordCodecBuilder.mapCodec(instance -> commonRefereceFields(instance)
			.and(Codec.STRING.optionalFieldOf("id", null).xmap(DimensionalDoors::id, ResourceLocation::getPath).forGetter(a -> a.id))
			.apply(instance, IdReference::new));

	public static final String KEY = "id";

	private ResourceLocation id;

	public IdReference(String resouceKey, String weight, Boolean setupLoot, List<Modifier> modifierList, List<CompoundTag> addons, ResourceLocation id) {
		super(resouceKey, weight, setupLoot, modifierList, addons);
	}

	@Override
	public ImplementedVirtualPocket fromNbt(CompoundTag nbt, ResourceManager manager) {
		super.fromNbt(nbt, manager);

		// TODO: make the json need the "dimdoors:" as well and load id via Identifier#tryParse instead
		id = DimensionalDoors.id(nbt.getString("id"));

		return this;
	}

	@Override
	protected CompoundTag toNbtInternal(CompoundTag nbt) {
		super.toNbtInternal(nbt);

		nbt.putString("id", id.getPath());

		return nbt;
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
