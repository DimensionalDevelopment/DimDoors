package org.dimdev.dimdoors.world.pocket.type.addon;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.pocket.type.AbstractPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface PocketAddon {
	Registrar<PocketAddonType<? extends PocketAddon, ? extends PocketBuilderAddon<?, ?>>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<PocketAddonType<?, ?>>builder(DimensionalDoors.id("pocket_applicable_addon_type")).build();
	StreamCodec<RegistryFriendlyByteBuf, PocketAddon> STREAM_CODEC = ByteBufCodecs.registry(REGISTRY.key()).dispatch(PocketAddon::getType, PocketAddonType::streamCodec);
	public static final Codec<PocketAddonType<?, ?>> POCKET_TYPE_CODEC = ResourceLocation.CODEC.xmap(REGISTRY::get, REGISTRY::getId);
	public static final Codec<PocketAddon> CODEC = POCKET_TYPE_CODEC.dispatch(PocketAddon::getType, PocketAddonType::codec);
	public static final Codec<PocketAddon.PocketBuilderAddon<?, ?>> BUILDER_CODEC = POCKET_TYPE_CODEC.dispatch(PocketBuilderAddon::getType, PocketAddonType::builderMapCodec);

	static PocketAddon deserialize(CompoundTag nbt) {
		return CODEC.decode(NbtOps.INSTANCE, nbt).result().map(Pair::getFirst).orElse(null); //TODO: NONE PocketAddon type;
	}

	static CompoundTag serialize(PocketAddon addon) {
		return addon.toNbt(new CompoundTag());
	}


	default boolean applicable(Pocket pocket) {
		return true;
	}

	default CompoundTag toNbt(CompoundTag nbt) {
		return this.getType().toNbt(nbt);
	}

	PocketAddonType<?, ?> getType();

	ResourceLocation getId();

	default void addAddon(Map<ResourceLocation, PocketAddon> addons) {
		addons.put(getId(), this);
	}

	interface PocketBuilderExtension<T extends AbstractPocket.AbstractPocketBuilder<T, ?>> {
		<C extends PocketBuilderAddon<?, ?>> C getAddon(ResourceLocation id);

		T getSelf();
	}

	interface PocketBuilderAddon<T extends PocketAddon, V extends PocketBuilderAddon<T, V>> {
		default boolean applicable(Pocket.PocketBuilder<?, ?> builder) {
			return true;
		}

		// makes it possible for addons themselves to control how they are added
		default void addAddon(Map<ResourceLocation, PocketBuilderAddon<?, ?>> addons) {
			addons.put(getId(), this);
		}

		void apply(Pocket pocket);

		ResourceLocation getId();

		PocketAddonType<T, V> getType();
	}

	record PocketAddonType<T extends PocketAddon, V extends PocketBuilderAddon<T, V>>(ResourceLocation id, @NotNull MapCodec<T> codec, @Nullable StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, MapCodec<V> builderMapCodec) {
		public static final RegistrySupplier<PocketAddonType<DyeableAddon, DyeableAddon.DyeableBuilderAddon>> DYEABLE_ADDON = register(DyeableAddon.ID, DyeableAddon.CODEC, null, DyeableAddon.DyeableBuilderAddon.CODEC);
		public static final RegistrySupplier<PocketAddonType<PreventBlockModificationAddon, PreventBlockModificationAddon.PreventBlockModificationBuilderAddon>> PREVENT_BLOCK_MODIFICATION_ADDON = register(PreventBlockModificationAddon.ID, PreventBlockModificationAddon.CODEC, STREAM_CODEC, PreventBlockModificationAddon.PreventBlockModificationBuilderAddon.CODEC);
//		RegistrySupplier<PocketAddonType<BlockBreakContainer>> BLOCK_BREAK_CONTAINER = register(BlockBreakContainer.ID, BlockBreakContainer::new, null);
		public static final RegistrySupplier<PocketAddonType<SkyAddon, SkyAddon.SkyBuilderAddon>> SKY_ADDON = register(SkyAddon.ID, SkyAddon.CODEC, SkyAddon.STREAM_CODEC, SkyAddon.SkyBuilderAddon.CODEC);

		public boolean isSyncable() {
			return streamCodec() != null;
		}

		public static void register() {}

		static <T extends PocketAddon, V extends PocketBuilderAddon<T, V>> RegistrySupplier<PocketAddonType<T, V>> register(ResourceLocation id, MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, MapCodec<V> builderSupplier) {
			return REGISTRY.register(id, () -> new PocketAddonType<>(id, codec, streamCodec, builderSupplier));
		}
	}
}
