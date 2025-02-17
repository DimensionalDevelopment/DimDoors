package org.dimdev.dimdoors.pockets.virtual;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.virtual.reference.IdReference;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.pockets.virtual.reference.TagReference;
import org.dimdev.dimdoors.pockets.virtual.selection.ConditionalSelector;
import org.dimdev.dimdoors.pockets.virtual.selection.PathSelector;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public interface ImplementedVirtualPocket extends VirtualPocket {
	String RESOURCE_STARTING_PATH = "pockets/virtual"; //TODO: might want to restructure data packs

	Registrar<VirtualPocketType<? extends ImplementedVirtualPocket>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<VirtualPocketType<? extends ImplementedVirtualPocket>>builder(DimensionalDoors.id("virtual_pocket_type")).build();
	public static final Codec<VirtualPocketType<? extends ImplementedVirtualPocket>> TYPE_CODEC = ResourceLocation.CODEC.xmap(REGISTRY::get, REGISTRY::getId);
	Codec<ImplementedVirtualPocket> CODEC = TYPE_CODEC.dispatch(ImplementedVirtualPocket::getType, VirtualPocketType::mapCodec);

	VirtualPocketType<? extends ImplementedVirtualPocket> getType();

	String getKey();

	public static record VirtualPocketType<T extends ImplementedVirtualPocket>(MapCodec<T> mapCodec) {
		public static final RegistrySupplier<VirtualPocketType<NoneVirtualPocket>> NONE = register(DimensionalDoors.id(NoneVirtualPocket.KEY), MapCodec.unit(() -> NoneVirtualPocket.NONE));
		public static final RegistrySupplier<VirtualPocketType<IdReference>> ID_REFERENCE = register(DimensionalDoors.id(IdReference.KEY), IdReference.CODEC);
		public static final RegistrySupplier<VirtualPocketType<TagReference>> TAG_REFERENCE = register(DimensionalDoors.id(TagReference.KEY), TagReference.CODEC);
		public static final RegistrySupplier<VirtualPocketType<ConditionalSelector>> CONDITIONAL_SELECTOR = register(DimensionalDoors.id(ConditionalSelector.KEY), ConditionalSelector.CODEC);
		public static final RegistrySupplier<VirtualPocketType<PathSelector>> PATH_SELECTOR = register(DimensionalDoors.id(PathSelector.KEY), PathSelector.CODEC);

		public static void register() {}

		static <U extends ImplementedVirtualPocket> RegistrySupplier<VirtualPocketType<U>> register(ResourceLocation id, MapCodec<U> factory) {
			return REGISTRY.register(id, () -> new VirtualPocketType<U>(factory));
		}
	}

	// TODO: NoneReference instead?
	class NoneVirtualPocket implements ImplementedVirtualPocket {
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
