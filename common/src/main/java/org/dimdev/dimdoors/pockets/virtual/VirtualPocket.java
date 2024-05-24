package org.dimdev.dimdoors.pockets.virtual;

import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.packs.resources.ResourceManager;
import org.dimdev.dimdoors.api.util.CodecUtil;
import org.dimdev.dimdoors.api.util.ReferenceSerializable;
import org.dimdev.dimdoors.api.util.ResourceUtil;
import org.dimdev.dimdoors.api.util.Weighted;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface VirtualPocket extends Weighted<PocketGenerationContext>, ReferenceSerializable {

	Codec<VirtualPocket> CODEC = CodecUtil.xor(VirtualPocketList.CODEC, ImplementedVirtualPocket.IMPL_CODEC);
	String RESOURCE_STARTING_PATH = "pockets/virtual"; //TODO: might want to restructure data packs

	static VirtualPocket deserialize(Tag nbt) {
		return deserialize(nbt, null);
	}


	//TODO: split up in ImplementedVirtualPocket and VirtualPocketList

	private static Codec<? extends VirtualPocket> getCodec() {
		return CODEC;
	}

	static VirtualPocket deserialize(Tag nbt, @Nullable ResourceManager manager) {
		return switch (nbt.getId()) {
			case Tag.TAG_LIST -> // It's a list of VirtualPocket
					VirtualPocketList.deserialize((ListTag) nbt, manager);
			case Tag.TAG_COMPOUND -> // It's a serialized VirtualPocket
					ImplementedVirtualPocket.deserialize((CompoundTag) nbt, manager);
			// TODO: throw if manager is null
			case Tag.TAG_STRING -> // It's a reference to a resource location
					ResourceUtil.loadReferencedResource(manager, RESOURCE_STARTING_PATH, nbt.getAsString(), ResourceUtil.NBT_READER.andThenComposable(nbtElement -> deserialize(nbtElement, manager)));
			default -> throw new RuntimeException(String.format("Unexpected NbtType %d!", nbt.getId()));
		};
	}

	static Tag serialize(VirtualPocket virtualPocket, boolean allowReference) {
		if (virtualPocket instanceof VirtualPocketList) {
			return VirtualPocketList.serialize((VirtualPocketList) virtualPocket, allowReference);
		}
		return ImplementedVirtualPocket.serialize((ImplementedVirtualPocket) virtualPocket);
	}

	static Tag serialize(VirtualPocket virtualPocket) {
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
