package org.dimdev.dimdoors.pockets.virtual;

import java.util.function.Supplier;

import com.mojang.serialization.Lifecycle;
import org.jetbrains.annotations.Nullable;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.core.MappedRegistry;
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

public interface ImplementedVirtualPocket extends VirtualPocket {
	String RESOURCE_STARTING_PATH = "pockets/virtual"; //TODO: might want to restructure data packs

	Registry<VirtualPocketType<? extends ImplementedVirtualPocket>> REGISTRY = FabricRegistryBuilder.from(new MappedRegistry<VirtualPocketType<? extends ImplementedVirtualPocket>>(ResourceKey.createRegistryKey(DimensionalDoors.id("virtual_pocket_type")), Lifecycle.stable(), false)).buildAndRegister();

	static ImplementedVirtualPocket deserialize(Tag nbt, @Nullable ResourceManager manager) {
		return switch (nbt.getId()) {
			case NbtType.COMPOUND -> deserialize((CompoundTag) nbt, manager);
			case NbtType.STRING -> ResourceUtil.loadReferencedResource(manager, RESOURCE_STARTING_PATH, nbt.getAsString(), ResourceUtil.NBT_READER.andThenComposable(nbtElement -> deserialize(nbtElement, manager)));
			default -> throw new RuntimeException(String.format("Unexpected NbtType %d!", nbt.getId()));
		};
	}

	static ImplementedVirtualPocket deserialize(Tag nbt) {
		return deserialize(nbt, null);
	}

	static ImplementedVirtualPocket deserialize(CompoundTag nbt, @Nullable ResourceManager manager) {
		ResourceLocation id = ResourceLocation.tryParse(nbt.getString("type"));
		VirtualPocketType<?> type = REGISTRY.get(id);
		return type != null ? type.fromNbt(nbt, manager) : VirtualPocketType.NONE.fromNbt(nbt, manager);
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

	VirtualPocketType<? extends ImplementedVirtualPocket> getType();

	String getKey();

	interface VirtualPocketType<T extends ImplementedVirtualPocket> {
		VirtualPocketType<NoneVirtualPocket> NONE = register(DimensionalDoors.id(NoneVirtualPocket.KEY), () -> NoneVirtualPocket.NONE);
		VirtualPocketType<IdReference> ID_REFERENCE = register(DimensionalDoors.id(IdReference.KEY), IdReference::new);
		VirtualPocketType<TagReference> TAG_REFERENCE = register(DimensionalDoors.id(TagReference.KEY), TagReference::new);
		VirtualPocketType<ConditionalSelector> CONDITIONAL_SELECTOR = register(DimensionalDoors.id(ConditionalSelector.KEY), ConditionalSelector::new);
		VirtualPocketType<PathSelector> PATH_SELECTOR = register(DimensionalDoors.id(PathSelector.KEY), PathSelector::new);

		ImplementedVirtualPocket fromNbt(CompoundTag nbt, @Nullable ResourceManager manager);

		default ImplementedVirtualPocket fromNbt(CompoundTag nbt) {
			return fromNbt(nbt, null);
		}

		CompoundTag toNbt(CompoundTag nbt);

		static void register() {
			DimensionalDoors.apiSubscribers.forEach(d -> d.registerVirtualSingularPocketTypes(REGISTRY));
		}

		static <U extends ImplementedVirtualPocket> VirtualPocketType<U> register(ResourceLocation id, Supplier<U> factory) {
			return Registry.register(REGISTRY, id, new VirtualPocketType<U>() {
				@Override
				public ImplementedVirtualPocket fromNbt(CompoundTag nbt, ResourceManager manager) {
					return factory.get().fromNbt(nbt, manager);
				}

				@Override
				public CompoundTag toNbt(CompoundTag nbt) {
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
		public ImplementedVirtualPocket fromNbt(CompoundTag nbt, ResourceManager manager) {
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
