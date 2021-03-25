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
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.function.Supplier;

public abstract class VirtualSingularPocket implements VirtualPocket {
	public static final Registry<VirtualSingularPocketType<? extends VirtualSingularPocket>> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<VirtualSingularPocketType<? extends VirtualSingularPocket>>(RegistryKey.ofRegistry(new Identifier("dimdoors", "virtual_pocket_type")), Lifecycle.stable())).buildAndRegister();

	public static VirtualSingularPocket deserialize(CompoundTag tag) {
		Identifier id = Identifier.tryParse(tag.getString("type"));
		VirtualSingularPocketType<?> type = REGISTRY.get(id);
		return type != null ? type.fromTag(tag) : VirtualSingularPocketType.NONE.fromTag(tag);
	}

	public static CompoundTag serialize(VirtualSingularPocket virtualSingularPocket) {
		return virtualSingularPocket.toTag(new CompoundTag());
	}

	public abstract VirtualSingularPocket fromTag(CompoundTag tag);

	public CompoundTag toTag(CompoundTag tag) {
		return this.getType().toTag(tag);
	}

	public abstract VirtualSingularPocketType<? extends VirtualSingularPocket> getType();

	public abstract String getKey();

	public interface VirtualSingularPocketType<T extends VirtualSingularPocket> {
		VirtualSingularPocketType<NoneVirtualPocket> NONE = register(new Identifier("dimdoors", NoneVirtualPocket.KEY), () -> NoneVirtualPocket.NONE);
		VirtualSingularPocketType<IdReference> ID_REFERENCE = register(new Identifier("dimdoors", IdReference.KEY), IdReference::new);
		VirtualSingularPocketType<TagReference> TAG_REFERENCE = register(new Identifier("dimdoors", TagReference.KEY), TagReference::new);
		VirtualSingularPocketType<ConditionalSelector> CONDITIONAL_SELECTOR = register(new Identifier("dimdoors", ConditionalSelector.KEY), ConditionalSelector::new);

		VirtualSingularPocket fromTag(CompoundTag tag);

		CompoundTag toTag(CompoundTag tag);

		static void register() {
			DimensionalDoorsInitializer.apiSubscribers.forEach(d -> d.registerVirtualSingularPocketTypes(REGISTRY));
		}

		static <U extends VirtualSingularPocket> VirtualSingularPocketType<U> register(Identifier id, Supplier<U> factory) {
			return Registry.register(REGISTRY, id, new VirtualSingularPocketType<U>() {
				@Override
				public VirtualSingularPocket fromTag(CompoundTag tag) {
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
	public static class NoneVirtualPocket extends VirtualSingularPocket {
		public static final String KEY = "none";
		public static final NoneVirtualPocket NONE = new NoneVirtualPocket();

		@Override
		public Pocket prepareAndPlacePocket(PocketGenerationContext parameters) {
			throw new UnsupportedOperationException("Cannot call this method on a NoneVirtualPocket");
		}

		@Override
		public PocketGeneratorReference getNextPocketGeneratorReference(PocketGenerationContext parameters) {
			throw new UnsupportedOperationException("Cannot call this method on a NoneVirtualPocket");
		}

		@Override
		public PocketGeneratorReference peekNextPocketGeneratorReference(PocketGenerationContext parameters) {
			throw new UnsupportedOperationException("Cannot call this method on a NoneVirtualPocket");
		}

		@Override
		public VirtualSingularPocket fromTag(CompoundTag tag) {
			return this;
		}

		@Override
		public VirtualSingularPocketType<? extends VirtualSingularPocket> getType() {
			return VirtualSingularPocketType.NONE;
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
