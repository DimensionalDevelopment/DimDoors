package org.dimdev.dimdoors.world.pocket.type.addon;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.addon.blockbreak.BlockBreakContainer;

import java.util.Map;
import java.util.function.Supplier;

public interface PocketAddon {
	Registrar<PocketAddonType<? extends PocketAddon>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<PocketAddonType<? extends PocketAddon>>builder(DimensionalDoors.id("pocket_applicable_addon_type")).build();

	static PocketAddon deserialize(CompoundTag nbt) {
		ResourceLocation id = ResourceLocation.tryParse(nbt.getString("type")); // TODO: NONE PocketAddon type;
		return REGISTRY.get(id).fromNbt(nbt);
	}

	static PocketBuilderAddon<?> deserializeBuilder(CompoundTag nbt) {
		ResourceLocation id = ResourceLocation.tryParse(nbt.getString("type")); // TODO: NONE PocketAddon type;
		return REGISTRY.get(id).builderAddonInstance().fromNbt(nbt);
	}

	static CompoundTag serialize(PocketAddon addon) {
		return addon.toNbt(new CompoundTag());
	}


	default boolean applicable(Pocket pocket) {
		return true;
	}

	PocketAddon fromNbt(CompoundTag nbt);

	default CompoundTag toNbt(CompoundTag nbt) {
		return this.getType().toNbt(nbt);
	}

	PocketAddonType<? extends PocketAddon> getType();

	ResourceLocation getId();

	default void addAddon(Map<ResourceLocation, PocketAddon> addons) {
		addons.put(getId(), this);
	}

	interface PocketBuilderExtension<T extends Pocket.PocketBuilder<T, ?>> {
		public <C extends PocketBuilderAddon<?>> C getAddon(ResourceLocation id);

		T getSelf();
	}

	interface PocketBuilderAddon<T extends PocketAddon> {
		default boolean applicable(Pocket.PocketBuilder<?, ?> builder) {
			return true;
		}

		// makes it possible for addons themselves to control how they are added
		default void addAddon(Map<ResourceLocation, PocketBuilderAddon<?>> addons) {
			addons.put(getId(), this);
		}

		void apply(Pocket pocket);

		ResourceLocation getId();

		PocketBuilderAddon<T> fromNbt(CompoundTag nbt);

		default CompoundTag toNbt(CompoundTag nbt) {
			return this.getType().toNbt(nbt);
		}

		PocketAddonType<T> getType();
	}

	interface PocketAddonType<T extends PocketAddon> {
		RegistrySupplier<PocketAddonType<DyeableAddon>> DYEABLE_ADDON = register(DyeableAddon.ID, DyeableAddon::new, DyeableAddon.DyeableBuilderAddon::new);
		RegistrySupplier<PocketAddonType<PreventBlockModificationAddon>> PREVENT_BLOCK_MODIFICATION_ADDON = register(PreventBlockModificationAddon.ID, PreventBlockModificationAddon::new, PreventBlockModificationAddon.PreventBlockModificationBuilderAddon::new);
		RegistrySupplier<PocketAddonType<BlockBreakContainer>> BLOCK_BREAK_CONTAINER = register(BlockBreakContainer.ID, BlockBreakContainer::new, null);
		RegistrySupplier<PocketAddonType<SkyAddon>> SKY_ADDON = register(SkyAddon.ID, SkyAddon::new, SkyAddon.SkyBuilderAddon::new);

		T fromNbt(CompoundTag nbt);

		CompoundTag toNbt(CompoundTag nbt);

		T instance();

		PocketBuilderAddon<T> builderAddonInstance();

		ResourceLocation identifier();

		static void register() {}

		static <U extends PocketAddon> RegistrySupplier<PocketAddonType<U>> register(ResourceLocation id, Supplier<U> factory, Supplier<PocketBuilderAddon<U>> addonSupplier) {
			return REGISTRY.register(id, () -> new PocketAddonType<U>() {
				@Override
				public U fromNbt(CompoundTag nbt) {
					return (U) factory.get().fromNbt(nbt);
				}

				@Override
				public CompoundTag toNbt(CompoundTag nbt) {
					nbt.putString("type", id.toString());
					return nbt;
				}

				@Override
				public U instance() {
					return factory.get();
				}

				@Override
				public PocketBuilderAddon<U> builderAddonInstance() {
					if (addonSupplier == null) return null;
					return addonSupplier.get();
				}

				@Override
				public ResourceLocation identifier() {
					return id;
				}
			});
		}
	}
}
