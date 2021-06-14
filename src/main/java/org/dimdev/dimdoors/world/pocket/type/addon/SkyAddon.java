package org.dimdev.dimdoors.world.pocket.type.addon;

import java.io.IOException;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import org.dimdev.dimdoors.world.pocket.type.Pocket;

public class SkyAddon implements AutoSyncedAddon {
	public static Identifier ID = new Identifier("dimdoors", "sky");

	private RegistryKey<World> world;

	public boolean setWorld(RegistryKey<World> world) {
		this.world = world;
		return true;
	}

	@Override
	public PocketAddon fromNbt(NbtCompound nbt) {
		this.world = RegistryKey.of(Registry.WORLD_KEY, Identifier.tryParse(nbt.getString("world")));

		return this;
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		AutoSyncedAddon.super.toNbt(nbt);

		nbt.putString("world", this.world.getValue().toString());

		return nbt;
	}

	@Override
	public PocketAddonType<? extends PocketAddon> getType() {
		return PocketAddonType.SKY_ADDON;
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public RegistryKey<World> getWorld() {
		return world;
	}

	@Override
	public AutoSyncedAddon read(PacketByteBuf buf) throws IOException {
		this.world = RegistryKey.of(Registry.WORLD_KEY, buf.readIdentifier());
		return this;
	}

	@Override
	public PacketByteBuf write(PacketByteBuf buf) throws IOException {
		buf.writeIdentifier(world.getValue());
		return buf;
	}

	public interface SkyPocketBuilder<T extends Pocket.PocketBuilder<T, ?>> extends PocketBuilderExtension<T> {
		default T world(RegistryKey<World> world) {

			this.<SkyBuilderAddon>getAddon(ID).world = world;

			return getSelf();
		}
	}

	public static class SkyBuilderAddon implements PocketBuilderAddon<SkyAddon> {

		private RegistryKey<World> world = World.OVERWORLD;

		@Override
		public void apply(Pocket pocket) {
			SkyAddon addon = new SkyAddon();
			addon.world = world;
			pocket.addAddon(addon);
		}

		@Override
		public Identifier getId() {
			return ID;
		}

		@Override
		public PocketBuilderAddon<SkyAddon> fromNbt(NbtCompound nbt) {
			this.world = RegistryKey.of(Registry.WORLD_KEY, Identifier.tryParse(nbt.getString("world")));

			return this;
		}

		@Override
		public NbtCompound toNbt(NbtCompound nbt) {
			PocketBuilderAddon.super.toNbt(nbt);

			nbt.putString("world", world.getValue().toString());

			return nbt;
		}

		@Override
		public PocketAddonType<SkyAddon> getType() {
			return PocketAddonType.SKY_ADDON;
		}
	}

	public interface SkyPocket extends AddonProvider {
		default boolean sky(RegistryKey<World> world) {
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
