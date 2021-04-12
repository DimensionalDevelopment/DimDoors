package org.dimdev.dimdoors.pockets.virtual.reference;

import com.google.common.base.MoreObjects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;

public class IdReference extends PocketGeneratorReference {
	public static final String KEY = "id";

	private Identifier id;

	@Override
	public ImplementedVirtualPocket fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);

		// TODO: make the json need the "dimdoors:" as well and load id via Identifier#tryParse instead
		id = new Identifier("dimdoors", nbt.getString("id"));

		return this;
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);

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
