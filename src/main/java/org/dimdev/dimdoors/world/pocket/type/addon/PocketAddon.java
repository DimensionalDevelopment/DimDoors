package org.dimdev.dimdoors.world.pocket.type.addon;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.addon.blockbreak.BlockBreakContainer;

import java.util.Map;
import java.util.function.Supplier;

public interface PocketAddon {
	Registry<PocketAddonType<? extends PocketAddon>> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<PocketAddonType<? extends PocketAddon>>(RegistryKey.ofRegistry(new Identifier("dimdoors", "pocket_applicable_addon_type")), Lifecycle.stable())).buildAndRegister();

	static PocketAddon deserialize(NbtCompound nbt) {
		Identifier id = Identifier.tryParse(nbt.getString("type")); // TODO: NONE PocketAddon type;
		return REGISTRY.get(id).fromNbt(nbt);
	}

	static PocketBuilderAddon<?> deserializeBuilder(NbtCompound nbt) {
		Identifier id = Identifier.tryParse(nbt.getString("type")); // TODO: NONE PocketAddon type;
		return REGISTRY.get(id).builderAddonInstance().fromNbt(nbt);
	}

	static NbtCompound serialize(PocketAddon addon) {
		return addon.toNbt(new NbtCompound());
	}


	default boolean applicable(Pocket pocket) {
		return true;
	}

	PocketAddon fromNbt(NbtCompound nbt);

	default NbtCompound toNbt(NbtCompound nbt) {
		return this.getType().toNbt(nbt);
	}

	PocketAddonType<? extends PocketAddon> getType();

	Identifier getId();

	default void addAddon(Map<Identifier, PocketAddon> addons) {
		addons.put(getId(), this);
	}

	interface PocketBuilderExtension<T extends Pocket.PocketBuilder<T, ?>> {
		public <C extends PocketBuilderAddon<?>> C getAddon(Identifier id);

		T getSelf();
	}

	interface PocketBuilderAddon<T extends PocketAddon> {
		default boolean applicable(Pocket.PocketBuilder<?, ?> builder) {
			return true;
		}

		// makes it possible for addons themselves to control how they are added
		default void addAddon(Map<Identifier, PocketBuilderAddon<?>> addons) {
			addons.put(getId(), this);
		}

		void apply(Pocket pocket);

		Identifier getId();

		PocketBuilderAddon<T> fromNbt(NbtCompound nbt);

		default NbtCompound toNbt(NbtCompound nbt) {
			return this.getType().toNbt(nbt);
		}

		PocketAddonType<T> getType();
	}

	interface PocketAddonType<T extends PocketAddon> {
		PocketAddonType<DyeableAddon> DYEABLE_ADDON = register(DyeableAddon.ID, DyeableAddon::new, DyeableAddon.DyeableBuilderAddon::new);
		PocketAddonType<PreventBlockModificationAddon> PREVENT_BLOCK_MODIFICATION_ADDON = register(PreventBlockModificationAddon.ID, PreventBlockModificationAddon::new, PreventBlockModificationAddon.PreventBlockModificationBuilderAddon::new);
		PocketAddonType<BlockBreakContainer> BLOCK_BREAK_CONTAINER = register(BlockBreakContainer.ID, BlockBreakContainer::new, null);
		PocketAddonType<SkyAddon> SKY_ADDON = register(SkyAddon.ID, SkyAddon::new, SkyAddon.SkyBuilderAddon::new);

		T fromNbt(NbtCompound nbt);

		NbtCompound toNbt(NbtCompound nbt);

		T instance();

		PocketBuilderAddon<T> builderAddonInstance();

		Identifier identifier();

		static void register() {
			DimensionalDoorsInitializer.apiSubscribers.forEach(d -> d.registerPocketAddonTypes(REGISTRY));
		}

		static <U extends PocketAddon> PocketAddonType<U> register(Identifier id, Supplier<U> factory, Supplier<PocketBuilderAddon<U>> addonSupplier) {
			return Registry.register(REGISTRY, id, new PocketAddonType<U>() {
				@Override
				public U fromNbt(NbtCompound nbt) {
					return (U) factory.get().fromNbt(nbt);
				}

				@Override
				public NbtCompound toNbt(NbtCompound nbt) {
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
				public Identifier identifier() {
					return id;
				}
			});
		}
	}
}
