package org.dimdev.dimdoors.pockets.virtual;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import org.dimdev.dimdoors.api.util.Weighted;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.util.CodecUtils;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.function.Supplier;

public interface VirtualPocket extends Weighted<PocketGenerationContext> {
	Codec<VirtualPocket> CODEC_BASE = Codec.either(ImplementedVirtualPocket.CODEC, VirtualPocketList.CODEC).xmap(Either::unwrap, pocket -> pocket instanceof ImplementedVirtualPocket implemented ? Either.left(implemented) : pocket instanceof VirtualPocketList list ? Either.right(list) : Either.right(null));
	Codec<VirtualPocket> CODEC = CodecUtils.codecWithReference(CODEC_BASE, "pockets/virtual");

	Pocket prepareAndPlacePocket(PocketGenerationContext parameters);

	PocketGeneratorReference getNextPocketGeneratorReference(PocketGenerationContext parameters);

	PocketGeneratorReference peekNextPocketGeneratorReference(PocketGenerationContext parameters);

	// Override where needed
	default void init() {

	}
}
