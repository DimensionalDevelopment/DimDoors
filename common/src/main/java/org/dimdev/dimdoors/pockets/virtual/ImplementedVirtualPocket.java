package org.dimdev.dimdoors.pockets.virtual;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.CodecUtil;
import org.dimdev.dimdoors.api.util.ResourceUtil;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.virtual.reference.IdReference;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.pockets.virtual.reference.TagReference;
import org.dimdev.dimdoors.pockets.virtual.selection.ConditionalSelector;
import org.dimdev.dimdoors.pockets.virtual.selection.PathSelector;
import org.dimdev.dimdoors.util.Serialized;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface ImplementedVirtualPocket<T extends ImplementedVirtualPocket<T>> extends VirtualPocket, Serialized<T> {
	String RESOURCE_STARTING_PATH = "pockets/virtual"; //TODO: might want to restructure data packs

	Registrar<ImplementedVirtualPocket.VirtualPocketType<?>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<ImplementedVirtualPocket.VirtualPocketType<?>>builder(DimensionalDoors.id("virtual_pocket_type")).build();
    Codec<ImplementedVirtualPocket<?>> IMPL_CODEC = CodecUtil.registrarCodec(REGISTRY);

	static Codec<ImplementedVirtualPocket<?>> codec() {
		return IMPL_CODEC;
	}

	static ImplementedVirtualPocket deserialize(Tag nbt, @Nullable ResourceManager manager) {
		return switch (nbt.getId()) {
			case Tag.TAG_COMPOUND -> deserialize((CompoundTag) nbt, manager);
			case Tag.TAG_STRING -> ResourceUtil.loadReferencedResource(manager, RESOURCE_STARTING_PATH, nbt.getAsString(), ResourceUtil.NBT_READER.andThenComposable(nbtElement -> deserialize(nbtElement, manager)));
			default -> throw new RuntimeException(String.format("Unexpected NbtType %d!", nbt.getId()));
		};
	}

	static ImplementedVirtualPocket deserialize(Tag nbt) {
		return deserialize(nbt, null);
	}

	static ImplementedVirtualPocket deserialize(CompoundTag nbt, @Nullable ResourceManager manager) {
		ResourceLocation id = ResourceLocation.tryParse(nbt.getString("type"));
		VirtualPocketType<?> type = REGISTRY.get(id);
		return type != null ? type.fromNbt(nbt, manager) : VirtualPocketType.NONE.get().fromNbt(nbt, manager);
	}

	static ImplementedVirtualPocket deserialize(CompoundTag nbt) {
		return deserialize(nbt, null);
	}

	static Tag serialize(ImplementedVirtualPocket implementedVirtualPocket, boolean allowReference) {
		return implementedVirtualPocket.toNbt(new CompoundTag(), allowReference);
	}

	static Tag serialize(ImplementedVirtualPocket implementedVirtualPocket) {
		return serialize(implementedVirtualPocket, false);
	}

	ImplementedVirtualPocket fromNbt(CompoundTag nbt, @Nullable ResourceManager manager);

	default ImplementedVirtualPocket fromNbt(CompoundTag nbt) {
		return fromNbt(nbt, null);
	}

	Tag toNbt(CompoundTag nbt, boolean allowReference);

	default Tag toNbt(CompoundTag nbt) {
		return this.toNbt(nbt, false);
	}

	VirtualPocketType<ImplementedVirtualPocket> getType();

	String getKey();

	interface VirtualPocketType<T extends ImplementedVirtualPocket<T>> extends Serialized.SerializedType<T> {
		RegistrySupplier<VirtualPocketType<NoneVirtualPocket>> NONE = register(DimensionalDoors.id(NoneVirtualPocket.KEY), () -> NoneVirtualPocket.NONE);
		RegistrySupplier<VirtualPocketType<IdReference>> ID_REFERENCE = register(DimensionalDoors.id(IdReference.KEY), IdReference::new);
		RegistrySupplier<VirtualPocketType<TagReference>> TAG_REFERENCE = register(DimensionalDoors.id(TagReference.KEY), TagReference::new);
		RegistrySupplier<VirtualPocketType<ConditionalSelector>> CONDITIONAL_SELECTOR = register(DimensionalDoors.id(ConditionalSelector.KEY), ConditionalSelector::new);
		RegistrySupplier<VirtualPocketType<PathSelector>> PATH_SELECTOR = register(DimensionalDoors.id(PathSelector.KEY), PathSelector::new);

		T fromNbt(CompoundTag nbt, @Nullable ResourceManager manager);

		default T fromNbt(CompoundTag nbt) {
			return fromNbt(nbt, null);
		}

		CompoundTag toNbt(CompoundTag nbt);

		@Override MapCodec<T> mapCodec();

		static void register() {}

		static <T extends ImplementedVirtualPocket<T>> RegistrySupplier<VirtualPocketType<T>> register(ResourceLocation id, MapCodec<T> mapCodec, Supplier<T> factory) {
			return REGISTRY.register(id, () -> new VirtualPocketType<T>() {
				@Override
				public MapCodec<T> mapCodec() {
					return mapCodec;
				}

				@Override
				public T fromNbt(CompoundTag nbt, @Nullable ResourceManager manager) {
					return null;
				}

				@Override
				public CompoundTag toNbt(CompoundTag nbt) {
					return null;
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
		public ImplementedVirtualPocket fromNbt(CompoundTag nbt, ResourceManager manager) {
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
