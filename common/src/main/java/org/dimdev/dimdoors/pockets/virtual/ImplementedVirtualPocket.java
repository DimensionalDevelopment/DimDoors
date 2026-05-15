package org.dimdev.dimdoors.pockets.virtual;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.ModRegistries;
import org.dimdev.dimdoors.ModRegistryKeys;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.virtual.reference.IdReference;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.pockets.virtual.reference.TagReference;
import org.dimdev.dimdoors.pockets.virtual.selection.ConditionalSelector;
import org.dimdev.dimdoors.pockets.virtual.selection.PathSelector;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public interface ImplementedVirtualPocket<T extends ImplementedVirtualPocket<T>> extends VirtualPocket {

    Codec<ImplementedVirtualPocket<?>> CODEC = VirtualPocketType.CODEC.dispatch(ImplementedVirtualPocket::getType, VirtualPocketType::codec);

    VirtualPocketType<T> getType();

    record VirtualPocketType<T extends ImplementedVirtualPocket<T>>(MapCodec<T> codec) {
        public static final Codec<VirtualPocketType<?>> CODEC = ModRegistries.VIRTUAL_POCKET_TYPE.byNameCodec();

        public static final VirtualPocketType<NoneVirtualPocket> NONE = register(NoneVirtualPocket.KEY, MapCodec.unit(NoneVirtualPocket.NONE));
        public static final VirtualPocketType<IdReference> ID_REFERENCE = register(IdReference.KEY, IdReference.CODEC);
        public static final VirtualPocketType<TagReference> TAG_REFERENCE = register(TagReference.KEY, TagReference.CODEC);
        public static final VirtualPocketType<ConditionalSelector> CONDITIONAL_SELECTOR = register(ConditionalSelector.KEY, ConditionalSelector.CODEC);
        public static final VirtualPocketType<PathSelector> PATH_SELECTOR = register(PathSelector.KEY, PathSelector.CODEC);

        public static void register() {}

        public static <U extends ImplementedVirtualPocket<U>> VirtualPocketType<U> register(String id, MapCodec<U> codec) {
            return DimensionalDoors.getSided().register(ModRegistryKeys.VIRTUAL_POCKET_TYPE, id, new VirtualPocketType<>(codec));
        }
    }

    // TODO: NoneReference instead?
    class NoneVirtualPocket implements ImplementedVirtualPocket<NoneVirtualPocket> {
        public static final String KEY = "none";
        public static final NoneVirtualPocket NONE = new NoneVirtualPocket();

        private NoneVirtualPocket() {
        }

        @Override
        public Pocket<?, ?> prepareAndPlacePocket(PocketGenerationContext parameters) {
            throw new UnsupportedOperationException("Cannot place a NoneVirtualPocket");
        }

        @Override
        public Pocket<?, ?> prepareAndPlacePocket(PocketGenerationContext parameters, Boolean setupLoot) {
            throw new UnsupportedOperationException("Cannot place a NoneVirtualPocket");
        }

        @Override
        public PocketGeneratorReference<?> getNextPocketGeneratorReference(PocketGenerationContext parameters) {
            throw new UnsupportedOperationException("Cannot get next pocket generator reference on a NoneVirtualPocket");
        }

        @Override
        public PocketGeneratorReference<?> peekNextPocketGeneratorReference(PocketGenerationContext parameters) {
            throw new UnsupportedOperationException("Cannot peek next pocket generator reference on a NoneVirtualPocket");
        }

        @Override
        public double getWeight(PocketGenerationContext parameters) {
            return 0;
        }

        @Override
        public VirtualPocketType<NoneVirtualPocket> getType() {
            return VirtualPocketType.NONE;
        }
    }
}