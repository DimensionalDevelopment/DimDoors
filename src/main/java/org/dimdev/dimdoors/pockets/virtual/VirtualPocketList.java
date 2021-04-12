package org.dimdev.dimdoors.pockets.virtual;

import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import net.minecraft.nbt.NbtList;
import org.dimdev.dimdoors.api.util.WeightedList;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public class VirtualPocketList extends WeightedList<VirtualPocket, PocketGenerationContext> implements VirtualPocket {

	public static VirtualPocketList deserialize(NbtList nbt) {
		return new VirtualPocketList().fromNbt(nbt);
	}

	public static NbtList serialize(VirtualPocketList virtualPocketList) {
		return virtualPocketList.toNbt(new NbtList());
	}

	public VirtualPocketList() {
		super();
	}

	public VirtualPocketList fromNbt(NbtList nbt) { // Keep in mind, this would add onto the list instead of overwriting it if called multiple times.
		for (net.minecraft.nbt.NbtElement value : nbt) {
			this.add(VirtualPocket.deserialize(value));
		}
		return this;
	}

	public NbtList toNbt(NbtList nbt) {
		for(VirtualPocket virtualPocket : this) {
			nbt.add(VirtualPocket.serialize(virtualPocket));
		}
		return nbt;
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
