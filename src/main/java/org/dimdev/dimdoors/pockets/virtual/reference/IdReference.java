package org.dimdev.dimdoors.pockets.virtual.reference;

import net.minecraft.nbt.CompoundTag;

import com.google.common.base.MoreObjects;
import net.minecraft.util.Identifier;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.virtual.VirtualSingularPocket;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;

public class IdReference extends PocketGeneratorReference {
	public static final String KEY = "id";

	private Identifier id;

	@Override
	public VirtualSingularPocket fromTag(CompoundTag tag) {
		super.fromTag(tag);

		// TODO: make the json need the "dimdoors:" as well and load id via Identifier#tryParse instead
		id = new Identifier("dimdoors", tag.getString("id"));

		return this;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);

		tag.putString("id", id.getPath());

		return tag;
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
	public VirtualSingularPocketType<? extends VirtualSingularPocket> getType() {
		return VirtualSingularPocketType.ID_REFERENCE;
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
