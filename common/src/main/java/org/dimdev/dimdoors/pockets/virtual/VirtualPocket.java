package org.dimdev.dimdoors.pockets.virtual;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.api.util.Weighted;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.util.CodecUtils;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.function.Function;

public interface VirtualPocket extends Weighted<PocketGenerationContext> {
	Codec<VirtualPocket> STRING_CODEC = CodecUtils.reference(reference -> PocketLoader.getInstance().getVirtual(ResourceLocation.tryParse(reference)));
	Codec<VirtualPocket> CODEC = Codec.either(ImplementedVirtualPocket.CODEC, Codec.either(VirtualPocketList.CODEC, STRING_CODEC)).xmap(either ->
			either.map(Function.identity(), pocketEither ->
					pocketEither.map(Function.identity(), Function.identity())), virtualPocket -> virtualPocket instanceof ImplementedVirtualPocket implementedVirtualPocket ? Either.left(implementedVirtualPocket) : virtualPocket instanceof VirtualPocketList virtualPocketList ? Either.right(Either.left(virtualPocketList)) : Either.right(Either.right(ImplementedVirtualPocket.NoneVirtualPocket.NONE)));


	Pocket prepareAndPlacePocket(PocketGenerationContext parameters);

	PocketGeneratorReference getNextPocketGeneratorReference(PocketGenerationContext parameters);

	PocketGeneratorReference peekNextPocketGeneratorReference(PocketGenerationContext parameters);

	// Override where needed
	default void init() {

	}
}
