package org.dimdev.dimdoors.pockets.virtual;

import com.mojang.serialization.*;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.pockets.virtual.reference.IdReference;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.pockets.virtual.reference.TagReference;
import org.dimdev.dimdoors.pockets.virtual.selection.ConditionalSelector;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.virtual.selection.PathSelector;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.function.Supplier;

public interface ImplementedVirtualPocket extends VirtualPocket {
	Registry<VirtualPocketType<? extends ImplementedVirtualPocket>> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<VirtualPocketType<? extends ImplementedVirtualPocket>>(RegistryKey.ofRegistry(new Identifier("dimdoors", "virtual_pocket_type")), Lifecycle.stable())).buildAndRegister();

	static ImplementedVirtualPocket deserialize(NbtCompound nbt) {
		Identifier id = Identifier.tryParse(nbt.getString("type"));
		VirtualPocketType<?> type = REGISTRY.get(id);
		return type != null ? type.fromNbt(nbt) : VirtualPocketType.NONE.fromNbt(nbt);
	}

	static NbtCompound serialize(ImplementedVirtualPocket implementedVirtualPocket) {
		return implementedVirtualPocket.toNbt(new NbtCompound());
	}

	ImplementedVirtualPocket fromNbt(NbtCompound nbt);

	default NbtCompound toNbt(NbtCompound nbt) {
		return this.getType().toNbt(nbt);
	}

	VirtualPocketType<? extends ImplementedVirtualPocket> getType();

	String getKey();

	interface VirtualPocketType<T extends ImplementedVirtualPocket> {
		VirtualPocketType<NoneVirtualPocket> NONE = register(new Identifier("dimdoors", NoneVirtualPocket.KEY), () -> NoneVirtualPocket.NONE);
		VirtualPocketType<IdReference> ID_REFERENCE = register(new Identifier("dimdoors", IdReference.KEY), IdReference::new);
		VirtualPocketType<TagReference> TAG_REFERENCE = register(new Identifier("dimdoors", TagReference.KEY), TagReference::new);
		VirtualPocketType<ConditionalSelector> CONDITIONAL_SELECTOR = register(new Identifier("dimdoors", ConditionalSelector.KEY), ConditionalSelector::new);
		VirtualPocketType<PathSelector> PATH_SELECTOR = register(new Identifier("dimdoors", PathSelector.KEY), PathSelector::new);

		ImplementedVirtualPocket fromNbt(NbtCompound nbt);

		NbtCompound toNbt(NbtCompound nbt);

		static void register() {
			DimensionalDoorsInitializer.apiSubscribers.forEach(d -> d.registerVirtualSingularPocketTypes(REGISTRY));
		}

		static <U extends ImplementedVirtualPocket> VirtualPocketType<U> register(Identifier id, Supplier<U> factory) {
			return Registry.register(REGISTRY, id, new VirtualPocketType<U>() {
				@Override
				public ImplementedVirtualPocket fromNbt(NbtCompound nbt) {
					return factory.get().fromNbt(nbt);
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
	public static class NoneVirtualPocket implements ImplementedVirtualPocket {
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
		public ImplementedVirtualPocket fromNbt(NbtCompound nbt) {
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
