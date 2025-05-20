package org.dimdev.dimdoors.pockets.virtual;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.pockets.virtual.selection.AbstractVirtualPocketList;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.List;

public class VirtualPocketList extends AbstractVirtualPocketList {

	public static final MapCodec<VirtualPocketList> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.lazyInitialized(() -> VirtualPocket.CODEC_BASE).listOf().fieldOf("list").forGetter(a -> a)
	).apply(instance, VirtualPocketList::new));

	public static final String ID = "list";

	public VirtualPocketList(List<VirtualPocket> list) {
		super(list);
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

	@Override
	public VirtualPocketType<VirtualPocketList> getType() {
		return VirtualPocketType.LIST.get();
	}

}
