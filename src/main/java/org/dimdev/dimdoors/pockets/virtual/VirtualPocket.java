package org.dimdev.dimdoors.pockets.virtual;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.api.util.Weighted;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import net.fabricmc.fabric.api.util.NbtType;

public interface VirtualPocket extends Weighted<PocketGenerationContext> {

	static VirtualPocket deserialize(Tag tag) {
		if (tag.getType() == NbtType.LIST) {
			return VirtualPocketList.deserialize((ListTag) tag);
		}
		return ImplementedVirtualPocket.deserialize((CompoundTag) tag); // should be CompoundTag
	}

	static Tag serialize(VirtualPocket virtualPocket) {
		if (virtualPocket instanceof VirtualPocketList) {
			return VirtualPocketList.serialize((VirtualPocketList) virtualPocket);
		}
		return ImplementedVirtualPocket.serialize((ImplementedVirtualPocket) virtualPocket);
	}


	Pocket prepareAndPlacePocket(PocketGenerationContext parameters);

	PocketGeneratorReference getNextPocketGeneratorReference(PocketGenerationContext parameters);

	PocketGeneratorReference peekNextPocketGeneratorReference(PocketGenerationContext parameters);

	// Override where needed
	default void init() {

	}
}
