package org.dimdev.dimdoors.pockets.virtual;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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

	Registrar<VirtualPocketType<? extends ImplementedVirtualPocket>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<VirtualPocketType<? extends ImplementedVirtualPocket>>builder(DimensionalDoors.id("virtual_pocket_type")).build();

	static ImplementedVirtualPocket deserialize(Tag nbt, HolderLookup.Provider provider, @Nullable ResourceManager manager) {
		return switch (nbt.getId()) {
			case Tag.TAG_COMPOUND -> deserializeCompunterTag((CompoundTag) nbt, provider, manager);
			case Tag.TAG_STRING -> ResourceUtil.loadReferencedResource(manager, RESOURCE_STARTING_PATH, nbt.getAsString(), ResourceUtil.NBT_READER.andThenComposable(nbtElement -> deserialize(nbtElement, provider, manager)));
			default -> throw new RuntimeException(String.format("Unexpected NbtType %d!", nbt.getId()));
		};
	}
//
//	static ImplementedVirtualPocket deserialize(Tag nbt) {
//		return deserialize(nbt, , null);
//	}

	static ImplementedVirtualPocket deserializeCompunterTag(CompoundTag nbt, HolderLookup.Provider provider, @Nullable ResourceManager manager) {
		ResourceLocation id = ResourceLocation.tryParse(nbt.getString("type"));
		VirtualPocketType<?> type = REGISTRY.get(id);
		return type != null ? type.fromNbt(nbt, provider, manager) : VirtualPocketType.NONE.get().fromNbt(nbt, provider, manager);
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
		RegistrySupplier<VirtualPocketType<NoneVirtualPocket>> NONE = register(DimensionalDoors.id(NoneVirtualPocket.KEY), () -> NoneVirtualPocket.NONE);
		RegistrySupplier<VirtualPocketType<IdReference>> ID_REFERENCE = register(DimensionalDoors.id(IdReference.KEY), IdReference::new);
		RegistrySupplier<VirtualPocketType<TagReference>> TAG_REFERENCE = register(DimensionalDoors.id(TagReference.KEY), TagReference::new);
		RegistrySupplier<VirtualPocketType<ImplementedVirtualPocket>> CONDITIONAL_SELECTOR = register(DimensionalDoors.id(ConditionalSelector.KEY), ConditionalSelector::new);
		RegistrySupplier<VirtualPocketType<PathSelector>> PATH_SELECTOR = register(DimensionalDoors.id(PathSelector.KEY), PathSelector::new);

		ImplementedVirtualPocket fromNbt(CompoundTag nbt, HolderLookup.Provider provider, @Nullable ResourceManager manager);

		default ImplementedVirtualPocket fromNbt(CompoundTag nbt, HolderLookup.Provider provider) {
			return fromNbt(nbt, provider, null);
		}

		CompoundTag toNbt(CompoundTag nbt, HolderLookup.Provider provider);

		static void register() {}

		static <U extends ImplementedVirtualPocket> RegistrySupplier<VirtualPocketType<U>> register(ResourceLocation id, Supplier<U> factory) {
			return REGISTRY.register(id, () -> new VirtualPocketType<U>() {
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
			return VirtualPocketType.NONE.get();
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