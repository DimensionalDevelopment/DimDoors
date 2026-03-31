package org.dimdev.dimdoors.pockets.virtual.selection;

import org.dimdev.dimdoors.api.util.WeightedList;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;

import java.util.UUID;

public abstract class AbstractVirtualPocketList extends WeightedList<VirtualPocket, PocketGenerationContext> implements ImplementedVirtualPocket {
    @Override
	public UUID prepareAndPlacePocket(PocketGenerationContext context) {
		return getNextPocketGeneratorReference(context).prepareAndPlacePocket(context);
	}

    @Override
    public UUID prepareAndPlacePocket(PocketGenerationContext context, Boolean setupLoot) {
        return getNextPocketGeneratorReference(context).prepareAndPlacePocket(context, setupLoot);
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
}