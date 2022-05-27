package org.dimdev.dimdoors.pockets.virtual;

import com.google.common.collect.Multimap;
import net.minecraft.resource.ResourceManager;
import org.dimdev.dimdoors.api.util.ReferenceSerializable;
import org.dimdev.dimdoors.api.util.ResourceUtil;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.api.util.Weighted;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface VirtualPocket extends Weighted<PocketGenerationContext>, ReferenceSerializable {
	String RESOURCE_STARTING_PATH = "pockets/virtual"; //TODO: might want to restructure data packs

	static VirtualPocket deserialize(NbtElement nbt) {
		return deserialize(nbt, null);
	}


	//TODO: split up in ImplementedVirtualPocket and VirtualPocketList
	static VirtualPocket deserialize(NbtElement nbt, @Nullable ResourceManager manager) {
		switch (nbt.getType()) {
			case NbtType.LIST: // It's a list of VirtualPocket
				return VirtualPocketList.deserialize((NbtList) nbt, manager);
			case NbtType.COMPOUND: // It's a serialized VirtualPocket
				return ImplementedVirtualPocket.deserialize((NbtCompound) nbt, manager);
			case NbtType.STRING: // It's a reference to a resource location
				return ResourceUtil.loadReferencedResource(manager, RESOURCE_STARTING_PATH, nbt.asString(), ResourceUtil.NBT_READER.andThenComposable(nbtElement -> deserialize(nbtElement, manager)));
			default:
				throw new RuntimeException(String.format("Unexpected NbtType %d!", nbt.getType()));
		}
	}

	static NbtElement serialize(VirtualPocket virtualPocket, boolean allowReference) {
		if (virtualPocket instanceof VirtualPocketList) {
			return VirtualPocketList.serialize((VirtualPocketList) virtualPocket, allowReference);
		}
		return ImplementedVirtualPocket.serialize((ImplementedVirtualPocket) virtualPocket, allowReference);
	}

	static NbtElement serialize(VirtualPocket virtualPocket) {
		return serialize(virtualPocket, false);
	}

	void setResourceKey(String resourceKey);

	String getResourceKey();

	default void processFlags(Multimap<String, String> flags) {
		// TODO: discuss some flag standardization
		Collection<String> reference = flags.get("reference");
		if (reference.stream().findFirst().map(string -> string.equals("local") || string.equals("global")).orElse(false)) {
			setResourceKey(flags.get("resource_key").stream().findFirst().orElse(null));
		}
	}

	Pocket prepareAndPlacePocket(PocketGenerationContext parameters);

	PocketGeneratorReference getNextPocketGeneratorReference(PocketGenerationContext parameters);

	PocketGeneratorReference peekNextPocketGeneratorReference(PocketGenerationContext parameters);

	// Override where needed
	default void init() {

	}
}
