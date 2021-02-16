package org.dimdev.dimdoors.pockets.virtual;

import net.minecraft.nbt.ListTag;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.util.WeightedList;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

// TODO: add weight tha
public class VirtualPocketList extends WeightedList<VirtualPocket, PocketGenerationParameters> implements VirtualPocket {

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
	public Pocket prepareAndPlacePocket(PocketGenerationParameters parameters) {
		return getNextPocketGeneratorReference(parameters).prepareAndPlacePocket(parameters);
	}

	public PocketGeneratorReference getNextPocketGeneratorReference(PocketGenerationParameters parameters) {
		return getNextRandomWeighted(parameters).getNextPocketGeneratorReference(parameters);
	}

	public PocketGeneratorReference peekNextPocketGeneratorReference(PocketGenerationParameters parameters) {
		return peekNextRandomWeighted(parameters).peekNextPocketGeneratorReference(parameters);
	}

	@Override
	public double getWeight(PocketGenerationParameters parameters) {
		return peekNextRandomWeighted(parameters).getWeight(parameters);
	}
}
