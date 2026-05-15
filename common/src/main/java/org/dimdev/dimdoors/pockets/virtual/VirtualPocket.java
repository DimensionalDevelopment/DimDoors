package org.dimdev.dimdoors.pockets.virtual;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import org.dimdev.dimdoors.ModRegistryKeys;
import org.dimdev.dimdoors.api.util.Weighted;
import org.dimdev.dimdoors.pockets.PocketCreator;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;

import java.util.function.Function;

public interface VirtualPocket extends Weighted<PocketGenerationContext>, PocketCreator {
    String RESOURCE_STARTING_PATH = "pockets/virtual"; //TODO: might want to restructure data packs
    Codec<VirtualPocket> CODEC = Codec.lazyInitialized(() ->
            Codec.either(
                    ImplementedVirtualPocket.CODEC,
                    VirtualPocketList.CODEC
            ).xmap(either -> either.map(Function.identity(), Function.identity()),
                    virtualPocket -> switch (virtualPocket) {
                        case VirtualPocketList list -> Either.right(list);
                        case ImplementedVirtualPocket implemented -> Either.left(implemented);
                        case null, default -> throw new IllegalStateException("Unknown virtual pocket type.");
                    }));

    Codec<Holder<VirtualPocket>> HOLDER_CODEC = RegistryFileCodec.create(ModRegistryKeys.VIRTUAL_POCKET, CODEC);

    PocketGeneratorReference getNextPocketGeneratorReference(PocketGenerationContext parameters);

    PocketGeneratorReference peekNextPocketGeneratorReference(PocketGenerationContext parameters);

}