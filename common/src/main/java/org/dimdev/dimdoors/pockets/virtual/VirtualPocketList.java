package org.dimdev.dimdoors.pockets.virtual;

import com.mojang.serialization.Codec;
import net.minecraft.Util;
import org.dimdev.dimdoors.api.util.WeightedList;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.function.Function;

public class VirtualPocketList extends WeightedList<VirtualPocket, PocketGenerationContext> implements VirtualPocket {
	private String resourceKey = null;

	public static final Codec<VirtualPocketList> CODEC = Codec.lazyInitialized(() -> VirtualPocket.CODEC.listOf().xmap(virtualPockets -> Util.make(new VirtualPocketList(), list -> list.addAll(virtualPockets)), Function.identity()));

	public VirtualPocketList() {
		super();
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
