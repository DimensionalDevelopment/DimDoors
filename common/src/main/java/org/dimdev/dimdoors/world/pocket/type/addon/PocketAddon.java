package org.dimdev.dimdoors.world.pocket.type.addon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.addon.sky.EnvironmentAddon;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface PocketAddon {
    Codec<PocketAddon> CODEC = PocketAddonType.CODEC.dispatch(PocketAddon::getType, PocketAddonType::codec);
    Codec<List<PocketAddon>> LIST_CODEC = CODEC.listOf();
    Codec<PocketBuilderAddon<?,?>> BUILDER_CODEC = PocketAddonType.CODEC.dispatch(PocketBuilderAddon::getType, PocketAddonType::builderCodec);
    Codec<List<PocketBuilderAddon<?, ?>>> LIST_BUILDER_CODEC = BUILDER_CODEC.listOf();
    StreamCodec<RegistryFriendlyByteBuf, PocketAddon> STREAM_CODEC = PocketAddonType.STREAM_CODEC.dispatch(PocketAddon::getType, PocketAddonType::streamCodec);
    StreamCodec<RegistryFriendlyByteBuf, List<PocketAddon>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());


    default boolean applicable(Pocket pocket) {
		return true;
	}

    PocketAddonType<?, ?> getType();

    default void addAddon(Map<PocketAddonType<?, ?>, PocketAddon> addons) {
		addons.put(getType(), this);
	}

	interface PocketBuilderExtension<T extends Pocket.PocketBuilder<T, ?>> {
		public <C extends PocketBuilderAddon<?, ?>> C getAddon(PocketAddonType<?, ?> id);

		T getSelf();
	}

	interface PocketBuilderAddon<T extends PocketAddon, U extends PocketBuilderAddon<T, U>> {
		default boolean applicable(Pocket.PocketBuilder<?, ?> builder) {
			return true;
		}

		// makes it possible for addons themselves to control how they are added
		default void addAddon(Map<PocketAddonType<?, ?>, PocketBuilderAddon<?, ?>> addons) {
			addons.put(getType(), this);
		}

		void apply(Pocket pocket);

        PocketAddonType<T, U> getType();
	}

	public record PocketAddonType<T extends PocketAddon, U extends PocketBuilderAddon<T, U>>(MapCodec<T> codec, MapCodec<U> builderCodec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        public static Registrar<PocketAddonType<?, ?>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<PocketAddonType<?, ?>>builder(DimensionalDoors.id("pocket_applicable_addon_type")).build();
        public static Codec<PocketAddonType<?, ?>> CODEC = ResourceLocation.CODEC.xmap(REGISTRY::get, REGISTRY::getId);
        public static StreamCodec<RegistryFriendlyByteBuf, PocketAddonType<?, ?>> STREAM_CODEC = ResourceLocation.STREAM_CODEC.<RegistryFriendlyByteBuf>cast().map(REGISTRY::get, REGISTRY::getId);

		public static final RegistrySupplier<PocketAddonType<DyeableAddon, DyeableAddon.DyeableBuilderAddon>> DYEABLE_ADDON = register(DyeableAddon.ID, DyeableAddon.CODEC, DyeableAddon.DyeableBuilderAddon.CODEC);
		public static RegistrySupplier<PocketAddonType<PreventBlockModificationAddon, PreventBlockModificationAddon.PreventBlockModificationBuilderAddon>> PREVENT_BLOCK_MODIFICATION_ADDON = register(PreventBlockModificationAddon.ID, PreventBlockModificationAddon.CODEC, PreventBlockModificationAddon.PreventBlockModificationBuilderAddon.CODEC, PreventBlockModificationAddon.STREAM_CODEC);
//		RegistrySupplier<PocketAddonType<BlockBreakContainer>> BLOCK_BREAK_CONTAINER = register(BlockBreakContainer.ID, BlockBreakContainer::new, null);
		public static final RegistrySupplier<PocketAddonType<EnvironmentAddon, EnvironmentAddon.EnvironmentBuilderAddon>> ENVIRONMENT_ADDON = register(EnvironmentAddon.ID, EnvironmentAddon.CODEC, EnvironmentAddon.EnvironmentBuilderAddon.CODEC, EnvironmentAddon.STREAM_CODEC);

        public static void register() {}

        static <V extends PocketAddon, S extends PocketBuilderAddon<V, S>> RegistrySupplier<PocketAddonType<V, S>> register(ResourceLocation id, MapCodec<V> codec, MapCodec<S> builderCodec) {
            return register(id, codec, builderCodec, null);
        }

		static <V extends PocketAddon, S extends PocketBuilderAddon<V, S>> RegistrySupplier<PocketAddonType<V, S>> register(ResourceLocation id, MapCodec<V> codec, MapCodec<S> builderCodec, @Nullable StreamCodec<RegistryFriendlyByteBuf, V> streamCodec) {
			return REGISTRY.register(id, () -> new PocketAddonType<>(codec, builderCodec, streamCodec));
		}

        public boolean isSyncable() {
            return streamCodec != null;
        }
    }
}