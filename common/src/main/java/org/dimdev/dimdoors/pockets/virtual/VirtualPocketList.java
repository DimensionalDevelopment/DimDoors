package org.dimdev.dimdoors.pockets.virtual;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.packs.resources.ResourceManager;
import org.dimdev.dimdoors.api.util.ResourceUtil;
import org.dimdev.dimdoors.api.util.WeightedList;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.jetbrains.annotations.Nullable;

public class VirtualPocketList extends WeightedList<VirtualPocket, PocketGenerationContext> implements VirtualPocket {
	private String resourceKey = null;

	public static VirtualPocketList deserialize(Tag nbt, @Nullable ResourceManager manager) {
		switch (nbt.getId()) {
			case Tag.TAG_LIST:
				return deserialize((ListTag) nbt, manager);
			case Tag.TAG_STRING:
				return ResourceUtil.loadReferencedResource(manager, RESOURCE_STARTING_PATH, nbt.getAsString(), ResourceUtil.NBT_READER.andThenComposable(nbtElement -> deserialize(nbtElement, manager)));
			default:
				throw new RuntimeException(String.format("Unexpected NbtType %d!", nbt.getId()));
		}
	}

	public static VirtualPocketList deserialize(Tag nbt) {
		return deserialize(nbt, null);
	}

	public static VirtualPocketList deserialize(ListTag nbt, @Nullable ResourceManager manager) {
		return new VirtualPocketList().fromNbt(nbt, manager);
	}

	public static VirtualPocketList deserialize(ListTag nbt) {
		return deserialize(nbt, null);
	}



	@Override
	public void setResourceKey(String resourceKey) {
		this.resourceKey = resourceKey;
	}

	@Override
	public String getResourceKey() {
		return this.resourceKey;
	}

	public static Tag serialize(VirtualPocketList virtualPocketList, boolean allowReference) {
		return virtualPocketList.toNbt(new ListTag(), allowReference);
	}

	public static Tag serialize(VirtualPocketList virtualPocket) {
		return serialize(virtualPocket, false);
	}

	public VirtualPocketList() {
		super();
	}

	public VirtualPocketList fromNbt(ListTag nbt, ResourceManager manager) { // Keep in mind, this would add onto the list instead of overwriting it if called multiple times.
		for (Tag value : nbt) {
			this.add(VirtualPocket.deserialize(value, manager));
		}
		return this;
	}

	public VirtualPocketList fromNbt(ListTag nbt) {
		return fromNbt(nbt, null);
	}

	public Tag toNbt(ListTag nbt, boolean allowReference) {
		if (allowReference && resourceKey != null) {
			return StringTag.valueOf(resourceKey);
		}
		for(VirtualPocket virtualPocket : this) {
			nbt.add(VirtualPocket.serialize(virtualPocket, allowReference));
		}
		return nbt;
	}

	public Tag toNbt(ListTag nbt) {
		return toNbt(nbt, false);
	}

	@Override
	public Pocket prepareAndPlacePocket(PocketGenerationContext context) {
		return getNextPocketGeneratorReference(context).prepareAndPlacePocket(context);
	}

	public PocketGeneratorReference getNextPocketGeneratorReference(PocketGenerationContext context) {
		return getNextRandomWeighted(context).getNextPocketGeneratorReference(context);
	}

	public PocketGeneratorReference peekNextPocketGeneratorReference(PocketGenerationContext context) {
		return peekNextRandomWeighted(context).peekNextPocketGeneratorReference(context);
	}

	@Override
	public void init() {
		this.forEach(VirtualPocket::init);
	}

	@Override
	public double getWeight(PocketGenerationContext context) {
		return getTotalWeight(context);
	}
}
