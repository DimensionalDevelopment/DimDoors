package org.dimdev.dimdoors.pockets.virtual;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.resource.ResourceManager;

import net.fabricmc.fabric.api.util.NbtType;

import org.dimdev.dimdoors.api.util.ResourceUtil;
import org.dimdev.dimdoors.api.util.WeightedList;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public class VirtualPocketList extends WeightedList<VirtualPocket, PocketGenerationContext> implements VirtualPocket {
	private String resourceKey = null;

	public static VirtualPocketList deserialize(NbtElement nbt, @Nullable ResourceManager manager) {
		switch (nbt.getType()) {
			case NbtType.LIST:
				return deserialize((NbtList) nbt, manager);
			case NbtType.STRING:
				return ResourceUtil.loadReferencedResource(manager, RESOURCE_STARTING_PATH, nbt.asString(), ResourceUtil.NBT_READER.andThenComposable(nbtElement -> deserialize(nbtElement, manager)));
			default:
				throw new RuntimeException(String.format("Unexpected NbtType %d!", nbt.getType()));
		}
	}

	public static VirtualPocketList deserialize(NbtElement nbt) {
		return deserialize(nbt, null);
	}

	public static VirtualPocketList deserialize(NbtList nbt, @Nullable ResourceManager manager) {
		return new VirtualPocketList().fromNbt(nbt, manager);
	}

	public static VirtualPocketList deserialize(NbtList nbt) {
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

	public static NbtElement serialize(VirtualPocketList virtualPocketList, boolean allowReference) {
		return virtualPocketList.toNbt(new NbtList(), allowReference);
	}

	public static NbtElement serialize(VirtualPocketList virtualPocket) {
		return serialize(virtualPocket, false);
	}

	public VirtualPocketList() {
		super();
	}

	public VirtualPocketList fromNbt(NbtList nbt, ResourceManager manager) { // Keep in mind, this would add onto the list instead of overwriting it if called multiple times.
		for (NbtElement value : nbt) {
			this.add(VirtualPocket.deserialize(value, manager));
		}
		return this;
	}

	public VirtualPocketList fromNbt(NbtList nbt) {
		return fromNbt(nbt, null);
	}

	public NbtElement toNbt(NbtList nbt, boolean allowReference) {
		if (allowReference && resourceKey != null) {
			return NbtString.of(resourceKey);
		}
		for(VirtualPocket virtualPocket : this) {
			nbt.add(VirtualPocket.serialize(virtualPocket, allowReference));
		}
		return nbt;
	}

	public NbtElement toNbt(NbtList nbt) {
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
