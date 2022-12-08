package org.dimdev.dimdoors.pockets.virtual.reference;

import com.google.common.base.MoreObjects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;

public class IdReference extends PocketGeneratorReference {
	public static final String KEY = "id";

	private Identifier id;

	@Override
	public ImplementedVirtualPocket fromNbt(NbtCompound nbt, ResourceManager manager) {
		super.fromNbt(nbt, manager);

		// TODO: make the json need the "dimdoors:" as well and load id via Identifier#tryParse instead
		id = DimensionalDoors.id(nbt.getString("id"));

		return this;
	}

	@Override
	protected NbtCompound toNbtInternal(NbtCompound nbt, boolean allowReference) {
		super.toNbtInternal(nbt, allowReference);

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
		return VirtualPocketType.ID_REFERENCE;
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
