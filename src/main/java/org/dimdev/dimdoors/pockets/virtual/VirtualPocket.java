package org.dimdev.dimdoors.pockets.virtual;

import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.api.util.Weighted;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

public interface VirtualPocket extends Weighted<PocketGenerationContext> {

	static VirtualPocket deserialize(NbtElement nbt) {
		if (nbt.getType() == NbtType.LIST) {
			return VirtualPocketList.deserialize((NbtList) nbt);
		}
		return ImplementedVirtualPocket.deserialize((NbtCompound) nbt); // should be NbtCompound
	}

	static NbtElement serialize(VirtualPocket virtualPocket) {
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
