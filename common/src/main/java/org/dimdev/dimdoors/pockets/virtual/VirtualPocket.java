package org.dimdev.dimdoors.pockets.virtual;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import org.dimdev.dimdoors.api.util.Weighted;
import org.dimdev.dimdoors.pockets.PocketCreator;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;

import java.util.function.Function;

public interface VirtualPocket extends Weighted<PocketGenerationContext>, PocketCreator {
    Codec<VirtualPocket> CODEC = Codec.lazyInitialized(() ->
            Codec.either(
                    ImplementedVirtualPocket.CODEC,
                    VirtualPocketList.CODEC
            ).xmap(
                    either -> either.map(Function.identity(), Function.identity()),
                    pocket -> pocket instanceof VirtualPocketList list
                            ? Either.right(list)
                            : Either.left((ImplementedVirtualPocket) pocket)
            )
    );

    PocketGeneratorReference getNextPocketGeneratorReference(PocketGenerationContext parameters);

	PocketGeneratorReference peekNextPocketGeneratorReference(PocketGenerationContext parameters);

}