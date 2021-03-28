package org.dimdev.dimdoors.pockets.virtual;

import com.mojang.serialization.*;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.nbt.CompoundTag;
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

public interface AbstractVirtualPocket extends VirtualPocket {
	Registry<VirtualPocketType<? extends AbstractVirtualPocket>> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<VirtualPocketType<? extends AbstractVirtualPocket>>(RegistryKey.ofRegistry(new Identifier("dimdoors", "virtual_pocket_type")), Lifecycle.stable())).buildAndRegister();

	static AbstractVirtualPocket deserialize(CompoundTag tag) {
		Identifier id = Identifier.tryParse(tag.getString("type"));
		VirtualPocketType<?> type = REGISTRY.get(id);
		return type != null ? type.fromTag(tag) : VirtualPocketType.NONE.fromTag(tag);
	}

	static CompoundTag serialize(AbstractVirtualPocket abstractVirtualPocket) {
		return abstractVirtualPocket.toTag(new CompoundTag());
	}

	AbstractVirtualPocket fromTag(CompoundTag tag);

	default CompoundTag toTag(CompoundTag tag) {
		return this.getType().toTag(tag);
	}

	VirtualPocketType<? extends AbstractVirtualPocket> getType();

	String getKey();

	interface VirtualPocketType<T extends AbstractVirtualPocket> {
		VirtualPocketType<NoneVirtualPocket> NONE = register(new Identifier("dimdoors", NoneVirtualPocket.KEY), () -> NoneVirtualPocket.NONE);
		VirtualPocketType<IdReference> ID_REFERENCE = register(new Identifier("dimdoors", IdReference.KEY), IdReference::new);
		VirtualPocketType<TagReference> TAG_REFERENCE = register(new Identifier("dimdoors", TagReference.KEY), TagReference::new);
		VirtualPocketType<ConditionalSelector> CONDITIONAL_SELECTOR = register(new Identifier("dimdoors", ConditionalSelector.KEY), ConditionalSelector::new);
		VirtualPocketType<PathSelector> PATH_SELECTOR = register(new Identifier("dimdoors", PathSelector.KEY), PathSelector::new);

		AbstractVirtualPocket fromTag(CompoundTag tag);

		CompoundTag toTag(CompoundTag tag);

		static void register() {
			DimensionalDoorsInitializer.apiSubscribers.forEach(d -> d.registerVirtualSingularPocketTypes(REGISTRY));
		}

		static <U extends AbstractVirtualPocket> VirtualPocketType<U> register(Identifier id, Supplier<U> factory) {
			return Registry.register(REGISTRY, id, new VirtualPocketType<U>() {
				@Override
				public AbstractVirtualPocket fromTag(CompoundTag tag) {
					return factory.get().fromTag(tag);
				}

				@Override
				public CompoundTag toTag(CompoundTag tag) {
					tag.putString("type", id.toString());
					return tag;
				}
			});
		}
	}

	// TODO: NoneReference instead?
	public static class NoneVirtualPocket implements AbstractVirtualPocket {
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
		public AbstractVirtualPocket fromTag(CompoundTag tag) {
			return this;
		}

		@Override
		public VirtualPocketType<? extends AbstractVirtualPocket> getType() {
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
