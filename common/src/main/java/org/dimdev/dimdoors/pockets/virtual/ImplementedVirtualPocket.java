package org.dimdev.dimdoors.pockets.virtual;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
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
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.jetbrains.annotations.Nullable;

public interface ImplementedVirtualPocket extends VirtualPocket {
	String RESOURCE_STARTING_PATH = "pockets/virtual"; //TODO: might want to restructure data packs

	Registrar<ImplementedVirtualPocket.VirtualPocketType<?>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<ImplementedVirtualPocket.VirtualPocketType<?>>builder(DimensionalDoors.id("virtual_pocket_type")).build();
    Codec<ImplementedVirtualPocket> IMPL_CODEC = CodecUtil.registrarCodec(REGISTRY, ImplementedVirtualPocket::getType, VirtualPocketType::mapCodec, ImplementedVirtualPocket::codec);

	static Codec<ImplementedVirtualPocket> codec() {
		return IMPL_CODEC;
	}

	static ImplementedVirtualPocket deserialize(Tag nbt, @Nullable ResourceManager manager) {
		var json = new JsonObject();

		JsonOps.INSTANCE.withParser(IMPL_CODEC).andThen(DataResult::getOrThrow).apply(json);

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

	static Tag serialize(ImplementedVirtualPocket implementedVirtualPocket) {
		return implementedVirtualPocket.toNbt(new CompoundTag());
	}

	ImplementedVirtualPocket fromNbt(CompoundTag nbt, @Nullable ResourceManager manager);

	default ImplementedVirtualPocket fromNbt(CompoundTag nbt) {
		return fromNbt(nbt, null);
	}

	Tag toNbt(CompoundTag nbt);

	VirtualPocketType<? extends ImplementedVirtualPocket> getType();

	String getKey();

	interface VirtualPocketType<T extends ImplementedVirtualPocket> {
		RegistrySupplier<VirtualPocketType<NoneVirtualPocket>> NONE = register(DimensionalDoors.id(NoneVirtualPocket.KEY), MapCodec.unit(NoneVirtualPocket.NONE));
		RegistrySupplier<VirtualPocketType<IdReference>> ID_REFERENCE = register(DimensionalDoors.id(IdReference.KEY), IdReference.CODEC);
		RegistrySupplier<VirtualPocketType<TagReference>> TAG_REFERENCE = register(DimensionalDoors.id(TagReference.KEY), TagReference.CODEC);
		RegistrySupplier<VirtualPocketType<ConditionalSelector>> CONDITIONAL_SELECTOR = register(DimensionalDoors.id(ConditionalSelector.KEY), ConditionalSelector.CODEC);
		RegistrySupplier<VirtualPocketType<PathSelector>> PATH_SELECTOR = register(DimensionalDoors.id(PathSelector.KEY), PathSelector.CODEC);

		T fromNbt(CompoundTag nbt, @Nullable ResourceManager manager);

		default T fromNbt(CompoundTag nbt) {
			return fromNbt(nbt, null);
		}

		CompoundTag toNbt(CompoundTag nbt);

		MapCodec<T> mapCodec();

		static void register() {}

		static <T extends ImplementedVirtualPocket> RegistrySupplier<VirtualPocketType<T>> register(ResourceLocation id, MapCodec<T> mapCodec) {
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
			super(null);
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
