package org.dimdev.dimdoors.world.pocket.type.addon;

import java.io.IOException;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.DimensionTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;

import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public class SkyAddon implements AutoSyncedAddon {
	public static ResourceLocation ID = DimensionalDoors.resource("sky");

	private ResourceKey<DimensionType> world;

	public boolean setWorld(ResourceKey<DimensionType> world) {
		this.world = world;
		return true;
	}

	@Override
	public PocketAddon fromNbt(CompoundTag nbt) {
		this.world = ResourceKey.create(Registries.DIMENSION_TYPE, ResourceLocation.tryParse(nbt.getString("world")));

		return this;
	}

	@Override
	public CompoundTag toNbt(CompoundTag nbt) {
		AutoSyncedAddon.super.toNbt(nbt);

		nbt.putString("world", this.world.location().toString());

		return nbt;
	}

	@Override
	public PocketAddonType<? extends PocketAddon> getType() {
		return PocketAddonType.SKY_ADDON;
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	public ResourceKey<DimensionType> getWorld() {
		return world;
	}

	@Override
	public AutoSyncedAddon read(FriendlyByteBuf buf) throws IOException {
		this.world = ResourceKey.create(Registries.DIMENSION_TYPE, buf.readResourceLocation());
		return this;
	}

	@Override
	public FriendlyByteBuf write(FriendlyByteBuf buf) throws IOException {
		buf.writeResourceLocation(world.location());
		return buf;
	}

	public interface SkyPocketBuilder<T extends Pocket.PocketBuilder<T, ?>> extends PocketBuilderExtension<T> {
		default T world(ResourceKey<DimensionType> world) {

			this.<SkyBuilderAddon>getAddon(ID).world = world;

			return getSelf();
		}
	}

	public static class SkyBuilderAddon implements PocketBuilderAddon<SkyAddon> {

		private ResourceKey<DimensionType> world = BuiltinDimensionTypes.OVERWORLD;

		@Override
		public void apply(Pocket pocket) {
			SkyAddon addon = new SkyAddon();
			addon.world = world;
			pocket.addAddon(addon);
		}

		@Override
		public ResourceLocation getId() {
			return ID;
		}

		@Override
		public PocketBuilderAddon<SkyAddon> fromNbt(CompoundTag nbt) {
			this.world = ResourceKey.create(Registries.DIMENSION_TYPE, ResourceLocation.tryParse(nbt.getString("world")));

			return this;
		}

		@Override
		public CompoundTag toNbt(CompoundTag nbt) {
			PocketBuilderAddon.super.toNbt(nbt);

			nbt.putString("world", world.location().toString());

			return nbt;
		}

		@Override
		public PocketAddonType<SkyAddon> getType() {
			return PocketAddonType.SKY_ADDON;
		}
	}

	public interface SkyPocket extends AddonProvider {
		default boolean sky(ResourceKey<DimensionType> world) {
			ensureIsPocket();
			if (!this.hasAddon(ID)) {
				SkyAddon addon = new SkyAddon();
				this.addAddon(addon);
				return addon.setWorld(world);
			}
			return this.<SkyAddon>getAddon(ID).setWorld(world);
		}
	}
}
