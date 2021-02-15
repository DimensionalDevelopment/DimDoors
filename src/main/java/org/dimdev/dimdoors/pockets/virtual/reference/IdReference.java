package org.dimdev.dimdoors.pockets.virtual.reference;

import net.minecraft.nbt.CompoundTag;

import com.google.common.base.MoreObjects;
import org.dimdev.dimdoors.pockets.SchematicV2Handler;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.virtual.VirtualSingularPocket;
import org.dimdev.dimdoors.util.PocketGenerationParameters;

public class IdReference extends PocketGeneratorReference {
	public static final String KEY = "id";

	private String id;

	@Override
	public VirtualSingularPocket fromTag(CompoundTag tag) {
		super.fromTag(tag);

		id = tag.getString("id");

		return this;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);

		tag.putString("id", id);

		return tag;
	}

	@Override
	public PocketGenerator peekReferencedPocketGenerator(PocketGenerationParameters parameters) {
		return getReferencedPocketGenerator(parameters);
	}

	@Override
	public PocketGenerator getReferencedPocketGenerator(PocketGenerationParameters parameters) {
		return SchematicV2Handler.getInstance().getGenerator(id);
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
