package org.dimdev.dimdoors.world.pocket.type.addon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamMemberEncoder;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.GeneralUtil;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.addon.blockbreak.BlockBreakContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public interface PocketAddon {
	Registrar<PocketAddonType<?>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<PocketAddonType<? extends PocketAddon>>builder(DimensionalDoors.id("pocket_applicable_addon_type")).build();
	public static final StreamCodec<RegistryFriendlyByteBuf, PocketAddon> STREAM_CODEC = StreamCodec.ofMember((value, buf) -> {
        var type = value.getType();
        buf.writeResourceLocation(type.id());
        type.streamCodec().encode(buf, value);
    }, object -> {
        var type = REGISTRY.get(object.readResourceLocation());
        return type.streamCodec().decode(object);
    });
	public static final Codec<PocketAddonType<?>> POCKET_TYPE_CODEC = ResourceLocation.CODEC.xmap(REGISTRY::get, REGISTRY::getId);


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

	public record PocketAddonType<T extends PocketAddon>(ResourceLocation id, @NotNull MapCodec<T> codec, @Nullable StreamCodec<RegistryFriendlyByteBuf, PocketAddon> streamCodec, Codec<PocketBuilderAddon<T>> builderSupplier) {
		RegistrySupplier<PocketAddonType<DyeableAddon>> DYEABLE_ADDON = register(DyeableAddon.ID, DyeableAddon::new, DyeableAddon.DyeableBuilderAddon::new);
		RegistrySupplier<PocketAddonType<PreventBlockModificationAddon>> PREVENT_BLOCK_MODIFICATION_ADDON = register(PreventBlockModificationAddon.ID, PreventBlockModificationAddon::new, PreventBlockModificationAddon.PreventBlockModificationBuilderAddon::new);
		RegistrySupplier<PocketAddonType<BlockBreakContainer>> BLOCK_BREAK_CONTAINER = register(BlockBreakContainer.ID, BlockBreakContainer::new, null);
		public static final RegistrySupplier<PocketAddonType<SkyAddon>> SKY_ADDON = register(SkyAddon.ID, SkyAddon.CODEC, SkyAddon.STREAM_CODEC);

		public boolean isSyncable() {
			return streamCodec() != null;
		}

		static void register() {}

		static <T extends PocketAddon> RegistrySupplier<PocketAddonType<T>> register(ResourceLocation id, MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, PocketAddon> streamCodec, Codec<PocketBuilderAddon<T>> builderSupplier) {
			return REGISTRY.register(id, () -> new PocketAddonType<>(id, codec, streamCodec, builderSupplier));
		}
	}
}
