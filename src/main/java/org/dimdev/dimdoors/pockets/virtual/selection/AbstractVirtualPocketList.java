package org.dimdev.dimdoors.pockets.virtual.selection;

import net.minecraft.nbt.NbtCompound;
import org.dimdev.dimdoors.api.util.WeightedList;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public abstract class AbstractVirtualPocketList extends WeightedList<VirtualPocket, PocketGenerationContext> implements ImplementedVirtualPocket {

	public NbtCompound toNbt(NbtCompound nbt) {
		ImplementedVirtualPocket.super.toNbt(nbt);
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
	public double getWeight(PocketGenerationContext context) {
		return getTotalWeight(context);
	}

	@Override
	public void init() {
		this.forEach(VirtualPocket::init);
	}
}
