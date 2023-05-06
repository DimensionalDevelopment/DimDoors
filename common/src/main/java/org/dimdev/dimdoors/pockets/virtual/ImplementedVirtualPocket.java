package org.dimdev.dimdoors.pockets.virtual;

import java.util.function.Supplier;

import com.mojang.serialization.Lifecycle;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.util.NbtType;

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

	Registry<VirtualPocketType<? extends ImplementedVirtualPocket>> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<VirtualPocketType<? extends ImplementedVirtualPocket>>(RegistryKey.ofRegistry(DimensionalDoors.id("virtual_pocket_type")), Lifecycle.stable(), false)).buildAndRegister();

	static ImplementedVirtualPocket deserialize(NbtElement nbt, @Nullable ResourceManager manager) {
		return switch (nbt.getId()) {
			case NbtType.COMPOUND -> deserialize((NbtCompound) nbt, manager);
			case NbtType.STRING -> ResourceUtil.loadReferencedResource(manager, RESOURCE_STARTING_PATH, nbt.asString(), ResourceUtil.NBT_READER.andThenComposable(nbtElement -> deserialize(nbtElement, manager)));
			default -> throw new RuntimeException(String.format("Unexpected NbtType %d!", nbt.getId()));
		};
	}

	static ImplementedVirtualPocket deserialize(NbtElement nbt) {
		return deserialize(nbt, null);
	}

	static ImplementedVirtualPocket deserialize(NbtCompound nbt, @Nullable ResourceManager manager) {
		Identifier id = Identifier.tryParse(nbt.getString("type"));
		VirtualPocketType<?> type = REGISTRY.get(id);
		return type != null ? type.fromNbt(nbt, manager) : VirtualPocketType.NONE.fromNbt(nbt, manager);
	}

	static ImplementedVirtualPocket deserialize(NbtCompound nbt) {
		return deserialize(nbt, null);
	}

	static NbtElement serialize(ImplementedVirtualPocket implementedVirtualPocket, boolean allowReference) {
		return implementedVirtualPocket.toNbt(new NbtCompound(), allowReference);
	}

	static NbtElement serialize(ImplementedVirtualPocket implementedVirtualPocket) {
		return serialize(implementedVirtualPocket, false);
	}

	ImplementedVirtualPocket fromNbt(NbtCompound nbt, @Nullable ResourceManager manager);

	default ImplementedVirtualPocket fromNbt(NbtCompound nbt) {
		return fromNbt(nbt, null);
	}

	NbtElement toNbt(NbtCompound nbt, boolean allowReference);

	default NbtElement toNbt(NbtCompound nbt) {
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

		default ImplementedVirtualPocket fromNbt(NbtCompound nbt) {
			return fromNbt(nbt, null);
		}

		NbtCompound toNbt(NbtCompound nbt);

		static void register() {
			DimensionalDoors.apiSubscribers.forEach(d -> d.registerVirtualSingularPocketTypes(REGISTRY));
		}

		static <U extends ImplementedVirtualPocket> VirtualPocketType<U> register(Identifier id, Supplier<U> factory) {
			return Registry.register(REGISTRY, id, new VirtualPocketType<U>() {
				@Override
				public ImplementedVirtualPocket fromNbt(NbtCompound nbt, ResourceManager manager) {
					return factory.get().fromNbt(nbt, manager);
				}

				@Override
				public NbtCompound toNbt(NbtCompound nbt) {
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
		public ImplementedVirtualPocket fromNbt(NbtCompound nbt, ResourceManager manager) {
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
