package org.dimdev.dimdoors.world.pocket.type.addon;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.addon.blockbreak.BlockBreakContainer;

import java.util.Map;
import java.util.function.Supplier;

public interface PocketAddon {
	Registry<PocketAddonType<? extends PocketAddon>> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<PocketAddonType<? extends PocketAddon>>(RegistryKey.ofRegistry(new Identifier("dimdoors", "pocket_applicable_addon_type")), Lifecycle.stable())).buildAndRegister();

	static PocketAddon deserialize(CompoundTag tag) {
		Identifier id = Identifier.tryParse(tag.getString("type")); // TODO: NONE PocketAddon type;
		return REGISTRY.get(id).fromTag(tag);
	}

	static PocketBuilderAddon<?> deserializeBuilder(CompoundTag tag) {
		Identifier id = Identifier.tryParse(tag.getString("type")); // TODO: NONE PocketAddon type;
		return REGISTRY.get(id).builderAddonInstance().fromTag(tag);
	}

	static CompoundTag serialize(PocketAddon addon) {
		return addon.toTag(new CompoundTag());
	}


	default boolean applicable(Pocket pocket) {
		return true;
	}

	PocketAddon fromTag(CompoundTag tag);

	default CompoundTag toTag(CompoundTag tag) {
		return this.getType().toTag(tag);
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

		PocketBuilderAddon<T> fromTag(CompoundTag tag);

		default CompoundTag toTag(CompoundTag tag) {
			return this.getType().toTag(tag);
		}

		PocketAddonType<T> getType();
	}

	interface PocketAddonType<T extends PocketAddon> {
		PocketAddonType<DyeableAddon> DYEABLE_ADDON = register(DyeableAddon.ID, DyeableAddon::new, DyeableAddon.DyeableBuilderAddon::new);
		PocketAddonType<PreventBlockModificationAddon> PREVENT_BLOCK_MODIFICATION_ADDON = register(PreventBlockModificationAddon.ID, PreventBlockModificationAddon::new, PreventBlockModificationAddon.PreventBlockModificationBuilderAddon::new);
		PocketAddonType<BlockBreakContainer> BLOCK_BREAK_CONTAINER = register(BlockBreakContainer.ID, BlockBreakContainer::new, null);

		T fromTag(CompoundTag tag);

		CompoundTag toTag(CompoundTag tag);

		T instance();

		PocketBuilderAddon<T> builderAddonInstance();

		Identifier identifier();

		static void register() {
		}

		static <U extends PocketAddon> PocketAddonType<U> register(Identifier id, Supplier<U> factory, Supplier<PocketBuilderAddon<U>> addonSupplier) {
			return Registry.register(REGISTRY, id, new PocketAddonType<U>() {
				@Override
				public U fromTag(CompoundTag tag) {
					return (U) factory.get().fromTag(tag);
				}

				@Override
				public CompoundTag toTag(CompoundTag tag) {
					tag.putString("type", id.toString());
					return tag;
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
