package org.dimdev.dimdoors.world.pocket.type.addon;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamMemberEncoder;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.PocketBase;
import org.dimdev.dimdoors.world.pocket.type.addon.blockbreak.BlockBreakContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

import static org.dimdev.dimdoors.world.pocket.type.AbstractPocket.BUILDER_CODEC;

public interface PocketAddon {
	Registrar<PocketAddonType<? extends PocketAddon>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<PocketAddonType<?>>builder(DimensionalDoors.id("pocket_applicable_addon_type")).build();
	StreamCodec<RegistryFriendlyByteBuf, PocketAddon> STREAM_CODEC = StreamCodec.ofMember((val, buf) -> buf.writeResourceLocation(val), FriendlyByteBuf::readResourceLocation).map(REGISTRY::get, REGISTRY::getId).dispatch(new Function<PocketAddon, PocketAddonType<?>>() {
		@Override
		public PocketAddonType<?> apply(PocketAddon pocketAddon) {
			return pocketAddon.getType();
		}
	}, a -> a.streamCodec());

	StreamCodec<RegistryFriendlyByteBuf, ResourceLocation> LOCATION_STREAM_CODEC = StreamCodec.ofMember((val, buf) -> buf.writeResourceLocation(val), FriendlyByteBuf::readResourceLocation);


	default void encode(RegistryFriendlyByteBuf buf) {
		var type = getType().streamCodec();
		type.encode(buf, this);
	}

	Codec<PocketAddon> CODEC = ResourceLocation.CODEC.dispatch(pocketAddon -> REGISTRY.getId(pocketAddon.getType()), resourceLocation -> REGISTRY.get(resourceLocation).codec());



	static PocketAddon deserialize(CompoundTag nbt) {
		return CODEC.decode(NbtOps.INSTANCE, nbt).result().map(Pair::getFirst).orElse(null); //TODO: NONE PocketAddon type;
	}

	static PocketBuilderAddon deserializeBuilder(CompoundTag nbt) {
		return BUILDER_CODEC.decode(NbtOps.INSTANCE, nbt).result().map(Pair::getFirst).orElse(null); //TODO: NONE PocketAddon type;
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
		<C extends PocketBuilderAddon<?>> C getAddon(ResourceLocation id);

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

		void apply(Pocket<?, ?> pocket);

		ResourceLocation getId();

		PocketBuilderAddon<T> fromNbt(CompoundTag nbt);

		default CompoundTag toNbt(CompoundTag nbt) {
			return this.getType().toNbt(nbt);
		}

		PocketAddonType<T> getType();
	}

	record PocketAddonType<T extends PocketAddon>(ResourceLocation id, @NotNull MapCodec<T> codec, @Nullable StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, Codec<PocketBuilderAddon<T>> builderSupplier) {
		RegistrySupplier<PocketAddonType<DyeableAddon>> DYEABLE_ADDON = register(DyeableAddon.ID, DyeableAddon::new, DyeableAddon.DyeableBuilderAddon::new);
		RegistrySupplier<PocketAddonType<PreventBlockModificationAddon>> PREVENT_BLOCK_MODIFICATION_ADDON = register(PreventBlockModificationAddon.ID, PreventBlockModificationAddon::new, PreventBlockModificationAddon.PreventBlockModificationBuilderAddon::new);
		RegistrySupplier<PocketAddonType<BlockBreakContainer>> BLOCK_BREAK_CONTAINER = register(BlockBreakContainer.ID, BlockBreakContainer::new, null);
		public static final RegistrySupplier<PocketAddonType<SkyAddon>> SKY_ADDON = register(SkyAddon.ID, SkyAddon.CODEC, SkyAddon.STREAM_CODEC, SkyAddon.SkyPocketBuilder);

		public boolean isSyncable() {
			return streamCodec() != null;
		}

		public static void register() {}

		static <T extends PocketAddon> RegistrySupplier<PocketAddonType<T>> register(ResourceLocation id, MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, Codec<PocketBuilderAddon<T>> builderSupplier) {
			return REGISTRY.register(id, () -> new PocketAddonType<>(id, codec, streamCodec, builderSupplier));
		}
	}
}
