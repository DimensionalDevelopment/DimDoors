package org.dimdev.dimdoors.pockets.virtual;

import net.minecraft.nbt.ListTag;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.api.util.WeightedList;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public class VirtualPocketList extends WeightedList<VirtualPocket, PocketGenerationContext> implements VirtualPocket {

	public static VirtualPocketList deserialize(ListTag tag) {
		return new VirtualPocketList().fromTag(tag);
	}

	public static ListTag serialize(VirtualPocketList virtualPocketList) {
		return virtualPocketList.toTag(new ListTag());
	}

	public VirtualPocketList() {
		super();
	}

	public VirtualPocketList fromTag(ListTag tag) { // Keep in mind, this would add onto the list instead of overwriting it if called multiple times.
		for (net.minecraft.nbt.Tag value : tag) {
			this.add(VirtualPocket.deserialize(value));
		}
		return this;
	}

	public ListTag toTag(ListTag tag) {
		for(VirtualPocket virtualPocket : this) {
			tag.add(VirtualPocket.serialize(virtualPocket));
		}
		return tag;
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
