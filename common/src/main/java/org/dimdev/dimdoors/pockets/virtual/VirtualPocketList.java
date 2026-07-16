package org.dimdev.dimdoors.pockets.virtual;

import com.mojang.serialization.Codec;
import org.dimdev.limlib.api.util.WeightedList;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public class VirtualPocketList extends WeightedList<VirtualPocket, PocketGenerationContext> implements VirtualPocket {

    public static final Codec<VirtualPocketList> CODEC = Codec.lazyInitialized(() ->
            VirtualPocket.CODEC.listOf().xmap(
                    list -> {
                        VirtualPocketList out = new VirtualPocketList();
                        out.addAll(list);
                        return out;
                    },
                    list -> list
            )
    );

    public VirtualPocketList() {
        super();
    }

    @Override
    public Pocket<?, ?> prepareAndPlacePocket(PocketGenerationContext context) {
        return getNextPocketGeneratorReference(context).prepareAndPlacePocket(context);
    }

    @Override
    public Pocket<?, ?> prepareAndPlacePocket(PocketGenerationContext context, Boolean setupLoot) {
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