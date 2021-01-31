package org.dimdev.dimdoors.pockets.virtual;

import net.minecraft.nbt.ListTag;
import org.dimdev.dimdoors.pockets.PocketGroup;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.util.WeightedList;
import org.dimdev.dimdoors.world.pocket.Pocket;

import java.util.List;

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
		List<VirtualPocket> list = this.getList();
		for (net.minecraft.nbt.Tag value : tag) {
			list.add(VirtualPocket.deserialize(value));
		}
		return this;
	}

	public ListTag toTag(ListTag tag) {
		int pointer = tag.size() - 1;
		for(VirtualPocket virtualPocket : this.getList()) {
			tag.set(pointer, VirtualPocket.serialize(virtualPocket));
		}
		return tag;
	}

	public Pocket prepareAndPlacePocket(PocketGenerationParameters parameters) {
		return getNextRandomWeighted(parameters).prepareAndPlacePocket(parameters);
	}

	@Override
	public void init(PocketGroup group) {
		getList().forEach(pocket -> pocket.init(group));
	}

	@Override
	public int getWeight(PocketGenerationParameters parameters) {
		return peekNextRandomWeighted(parameters).getWeight(parameters);
	}
}
