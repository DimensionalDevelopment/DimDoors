package org.dimdev.dimdoors.pockets.virtual;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.ResourceUtil;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.virtual.reference.IdReference;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.pockets.virtual.reference.TagReference;
import org.dimdev.dimdoors.pockets.virtual.selection.ConditionalSelector;
import org.dimdev.dimdoors.pockets.virtual.selection.PathSelector;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface ImplementedVirtualPocket extends VirtualPocket {
    String RESOURCE_STARTING_PATH = "pockets/virtual"; //TODO: might want to restructure data packs
    ResourceKey<Registry<VirtualPocketType<? extends ImplementedVirtualPocket>>> KEY = ResourceKey.createRegistryKey(DimensionalDoors.id("virtual_pocket_type"));
    Registry<VirtualPocketType<? extends ImplementedVirtualPocket>> REGISTRY = DimensionalDoors.getSided().createRegistry(KEY);

    static ImplementedVirtualPocket deserialize(Tag nbt, HolderLookup.Provider provider, @Nullable ResourceManager manager) {
        return switch (nbt.getId()) {
            case Tag.TAG_COMPOUND -> deserializeCompunterTag((CompoundTag) nbt, provider, manager);
            case Tag.TAG_STRING ->
                    ResourceUtil.loadReferencedResource(manager, RESOURCE_STARTING_PATH, nbt.getAsString(), ResourceUtil.NBT_READER.andThenComposable(nbtElement -> deserialize(nbtElement, provider, manager)));
            default -> throw new RuntimeException(String.format("Unexpected NbtType %d!", nbt.getId()));
        };
    }
//
//    static ImplementedVirtualPocket deserialize(Tag nbt) {
//    return deserialize(nbt, , null);
//    }

    static ImplementedVirtualPocket deserializeCompunterTag(CompoundTag nbt, HolderLookup.Provider provider, @Nullable ResourceManager manager) {
        ResourceLocation id = ResourceLocation.tryParse(nbt.getString("type"));
        VirtualPocketType<?> type = REGISTRY.get(id);
        return type != null ? type.fromNbt(nbt, provider, manager) : VirtualPocketType.NONE.fromNbt(nbt, provider, manager);
    }

    static ImplementedVirtualPocket deserialize(CompoundTag nbt, HolderLookup.Provider provider) {
        return deserializeCompunterTag(nbt, provider, null);
    }

    static Tag serialize(ImplementedVirtualPocket implementedVirtualPocket, HolderLookup.Provider provider, boolean allowReference) {
        return implementedVirtualPocket.toNbt(new CompoundTag(), provider, allowReference);
    }

    static Tag serialize(ImplementedVirtualPocket implementedVirtualPocket, HolderLookup.Provider provider) {
        return serialize(implementedVirtualPocket, provider, false);
    }

    ImplementedVirtualPocket fromNbt(CompoundTag nbt, HolderLookup.Provider provider, @Nullable ResourceManager manager);

    default ImplementedVirtualPocket fromNbt(CompoundTag nbt, HolderLookup.Provider provider) {
        return fromNbt(nbt, provider, null);
    }

    Tag toNbt(CompoundTag nbt, HolderLookup.Provider provider, boolean allowReference);

    default Tag toNbt(CompoundTag nbt, HolderLookup.Provider provider) {
        return this.toNbt(nbt, provider, false);
    }

    VirtualPocketType<? extends ImplementedVirtualPocket> getType();

    String getKey();

    interface VirtualPocketType<T extends ImplementedVirtualPocket> {
        VirtualPocketType<NoneVirtualPocket> NONE = register(NoneVirtualPocket.KEY, () -> NoneVirtualPocket.NONE);
        VirtualPocketType<IdReference> ID_REFERENCE = register(IdReference.KEY, IdReference::new);
        VirtualPocketType<TagReference> TAG_REFERENCE = register(TagReference.KEY, TagReference::new);
        VirtualPocketType<ImplementedVirtualPocket> CONDITIONAL_SELECTOR = register(ConditionalSelector.KEY, ConditionalSelector::new);
        VirtualPocketType<PathSelector> PATH_SELECTOR = register(PathSelector.KEY, PathSelector::new);

        ImplementedVirtualPocket fromNbt(CompoundTag nbt, HolderLookup.Provider provider, @Nullable ResourceManager manager);

        default ImplementedVirtualPocket fromNbt(CompoundTag nbt, HolderLookup.Provider provider) {
            return fromNbt(nbt, provider, null);
        }

        CompoundTag toNbt(CompoundTag nbt, HolderLookup.Provider provider);

        static void register() {
        }

        static <U extends ImplementedVirtualPocket> VirtualPocketType<U> register(String id, Supplier<U> factory) {
            return DimensionalDoors.getSided().register(KEY, id, new VirtualPocketType<U>() {
                @Override
                public ImplementedVirtualPocket fromNbt(CompoundTag nbt, HolderLookup.Provider provider, ResourceManager manager) {
                    return factory.get().fromNbt(nbt, provider, manager);
                }

                @Override
                public CompoundTag toNbt(CompoundTag nbt, HolderLookup.Provider provider) {
                    nbt.putString("type", id.toString());
                    return nbt;
                }
            });
        }
    }

    // TODO: NoneReference instead?
    class NoneVirtualPocket extends AbstractVirtualPocket {
        public static final String KEY = "none";
        public static final NoneVirtualPocket NONE = new NoneVirtualPocket();

        private NoneVirtualPocket() {
        }

        @Override
        public Pocket prepareAndPlacePocket(PocketGenerationContext parameters) {
            throw new UnsupportedOperationException("Cannot place a NoneVirtualPocket");
        }

        @Override
        public Pocket prepareAndPlacePocket(PocketGenerationContext parameters, Boolean setupLoot) {
            throw new UnsupportedOperationException("Cannot place a NoneVirtualPocket");
        }

        @Override
        public PocketGeneratorReference getNextPocketGeneratorReference(PocketGenerationContext parameters) {
            throw new UnsupportedOperationException("Cannot get next pocket generator reference on a NoneVirtualPocket");
        }

        @Override
        public PocketGeneratorReference peekNextPocketGeneratorReference(PocketGenerationContext parameters) {
            throw new UnsupportedOperationException("Cannot peek next pocket generator reference on a NoneVirtualPocket");
        }

        @Override
        public ImplementedVirtualPocket fromNbt(CompoundTag nbt, HolderLookup.Provider provider, ResourceManager manager) {
            return this;
        }

        @Override
        public VirtualPocketType<? extends ImplementedVirtualPocket> getType() {
            return VirtualPocketType.NONE;
        }

        @Override
        public String getKey() {
            return KEY;
        }

        @Override
        public double getWeight(PocketGenerationContext parameters) {
            return 0;
        }
    }
}