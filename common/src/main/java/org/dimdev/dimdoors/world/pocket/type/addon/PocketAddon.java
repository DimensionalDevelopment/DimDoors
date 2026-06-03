package org.dimdev.dimdoors.world.pocket.type.addon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.addon.sky.EnvironmentAddon;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface PocketAddon {
    Codec<PocketAddon> CODEC = PocketAddonType.CODEC.dispatch(PocketAddon::getType, PocketAddonType::codec);
    Codec<List<PocketAddon>> LIST_CODEC = CODEC.listOf();
    Codec<PocketBuilderAddon<?, ?>> BUILDER_CODEC = PocketAddonType.CODEC.dispatch(PocketBuilderAddon::getType, PocketAddonType::builderCodec);
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

    interface PocketBuilderExtension<T extends Pocket<T, P>, P extends Pocket.PocketBuilder<T, P>> {
        public <C extends PocketBuilderAddon<?, ?>> C getAddon(PocketAddonType<?, ?> id);

        default P getSelf() {
            return (P) this;
        }
    }

    interface PocketBuilderAddon<T extends PocketAddon, U extends PocketBuilderAddon<T, U>> {
        default boolean applicable(Pocket.PocketBuilder<?, ?> builder) {
            return true;
        }

        // makes it possible for addons themselves to control how they are added
        default void addAddon(Map<PocketAddonType<?, ?>, PocketBuilderAddon<?, ?>> addons) {
            addons.put(getType(), this);
        }

        void apply(Pocket<?, ?> pocket);

        PocketAddonType<T, U> getType();
    }

    public record PocketAddonType<T extends PocketAddon, U extends PocketBuilderAddon<T, U>>(MapCodec<T> codec,
                                                                                             MapCodec<U> builderCodec,
                                                                                             StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        public static ResourceKey<Registry<PocketAddonType<?, ?>>> KEY = ResourceKey.createRegistryKey(DimensionalDoors.id("pocket_applicable_addon_type"));
        public static Registry<PocketAddonType<?, ?>> REGISTRY = DimensionalDoors.getSided().createRegistry(KEY);
        public static Codec<PocketAddonType<?, ?>> CODEC = REGISTRY.byNameCodec();
        public static StreamCodec<RegistryFriendlyByteBuf, PocketAddonType<?, ?>> STREAM_CODEC = Identifier.STREAM_CODEC.<RegistryFriendlyByteBuf>cast().map(REGISTRY::get, REGISTRY::getKey);

        public static final PocketAddonType<DyeableAddon, DyeableAddon.DyeableBuilderAddon> DYEABLE_ADDON = register(DyeableAddon.ID, DyeableAddon.CODEC, DyeableAddon.DyeableBuilderAddon.CODEC);
        public static PocketAddonType<PreventBlockModificationAddon, PreventBlockModificationAddon.PreventBlockModificationBuilderAddon> PREVENT_BLOCK_MODIFICATION_ADDON = register(PreventBlockModificationAddon.ID, PreventBlockModificationAddon.CODEC, PreventBlockModificationAddon.PreventBlockModificationBuilderAddon.CODEC, PreventBlockModificationAddon.STREAM_CODEC);
        public static final PocketAddonType<ForceLoadedPocketAddon, ForceLoadedPocketAddon.BuilderAddon> FORCE_LOADED_ADDON = register(ForceLoadedPocketAddon.ID, ForceLoadedPocketAddon.CODEC, ForceLoadedPocketAddon.BuilderAddon.CODEC);
        //    PocketAddonType<BlockBreakContainer> BLOCK_BREAK_CONTAINER = register(BlockBreakContainer.ID, BlockBreakContainer::new, null);
        public static final PocketAddonType<EnvironmentAddon, EnvironmentAddon.EnvironmentBuilderAddon> ENVIRONMENT_ADDON = register(EnvironmentAddon.ID, EnvironmentAddon.CODEC, EnvironmentAddon.EnvironmentBuilderAddon.CODEC, EnvironmentAddon.STREAM_CODEC);

        public static void register() {
        }

        static <V extends PocketAddon, S extends PocketBuilderAddon<V, S>> PocketAddonType<V, S> register(Identifier id, MapCodec<V> codec, MapCodec<S> builderCodec) {
            return register(id, codec, builderCodec, null);
        }

        static <V extends PocketAddon, S extends PocketBuilderAddon<V, S>> PocketAddonType<V, S> register(Identifier id, MapCodec<V> codec, MapCodec<S> builderCodec, @Nullable StreamCodec<RegistryFriendlyByteBuf, V> streamCodec) {
            return DimensionalDoors.getSided().register(KEY, id, new PocketAddonType<>(codec, builderCodec, streamCodec));
        }

        public boolean isSyncable() {
            return streamCodec != null;
        }
    }
}
