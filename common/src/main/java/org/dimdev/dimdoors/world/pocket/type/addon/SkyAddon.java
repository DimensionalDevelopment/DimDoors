package org.dimdev.dimdoors.world.pocket.type.addon;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public class SkyAddon implements AutoSyncedAddon {
	public static ResourceLocation ID = DimensionalDoors.id("sky");

	private ResourceKey<Level> world;

	public boolean setWorld(ResourceKey<Level> world) {
		this.world = world;
		return true;
	}

	@Override
	public PocketAddon fromNbt(CompoundTag nbt) {
		this.world = ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryParse(nbt.getString("world")));

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
		return PocketAddonType.SKY_ADDON.get();
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	public ResourceKey<Level> getWorld() {
		return world;
	}

	@Override
	public AutoSyncedAddon read(FriendlyByteBuf buf) {
		this.world = ResourceKey.create(Registries.DIMENSION, buf.readResourceLocation());
		return this;
	}

	@Override
	public FriendlyByteBuf write(FriendlyByteBuf buf) {
		buf.writeResourceLocation(world.location());
		return buf;
	}

	public interface SkyPocketBuilder<T extends Pocket.PocketBuilder<T, ?>> extends PocketBuilderExtension<T> {
		default T world(ResourceKey<Level> world) {

			this.<SkyBuilderAddon>getAddon(ID).world = world;

			return getSelf();
		}
	}

	public static class SkyBuilderAddon implements PocketBuilderAddon<SkyAddon> {

		private ResourceKey<Level> world = Level.OVERWORLD;

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
			this.world = ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryParse(nbt.getString("world")));

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
			return PocketAddonType.SKY_ADDON.get();
		}
	}

	public interface SkyPocket extends AddonProvider {
		default boolean sky(ResourceKey<Level> world) {
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
