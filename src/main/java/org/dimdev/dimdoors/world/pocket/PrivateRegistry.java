package org.dimdev.dimdoors.world.pocket;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.nbt.Tag;
import net.minecraft.util.Pair;
import org.dimdev.dimdoors.world.level.DimensionalRegistry;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.PrivatePocket;

public class PrivateRegistry {
	protected static class PocketInfo {
		public final RegistryKey<World> world;
		public final int id;

		public PocketInfo(RegistryKey<World> world, int id) {
			this.world = world;
			this.id = id;
		}

		public static CompoundTag toTag(PocketInfo info) {
			CompoundTag tag = new CompoundTag();
			tag.putString("world", info.world.getValue().toString());
			tag.putInt("id", info.id);
			return tag;
		}

		public static PocketInfo fromTag(CompoundTag tag) {
			return new PocketInfo(
					RegistryKey.of(Registry.DIMENSION, new Identifier(tag.getString("world"))),
					tag.getInt("id")
			);
		}
	}

	private static final String DATA_NAME = "dimdoors_private_pockets";

	protected BiMap<UUID, PocketInfo> privatePocketMap = HashBiMap.create(); // Player UUID -> Pocket Info TODO: fix AnnotatedNBT and use UUID rather than String

	public PrivateRegistry() {
	}

	public void fromTag(CompoundTag tag) {
		privatePocketMap.clear();
		CompoundTag privatePocketMapTag = tag.getCompound("private_pocket_map");
		CompletableFuture<Map<UUID, PocketInfo>> futurePrivatePocketMap = CompletableFuture.supplyAsync(() ->
				privatePocketMapTag.getKeys().stream().unordered().map(key -> {
					CompoundTag pocketInfoTag = privatePocketMapTag.getCompound(key);
					return CompletableFuture.supplyAsync(() -> new Pair<>(UUID.fromString(key), PocketInfo.fromTag(pocketInfoTag)));
				}).parallel().map(CompletableFuture::join).collect(Collectors.toConcurrentMap(Pair::getLeft, Pair::getRight)));

		futurePrivatePocketMap.join().forEach(this.privatePocketMap::put);
	}

	public CompoundTag toTag(CompoundTag tag) {
		CompletableFuture<CompoundTag> futurePrivatePocketMapTag = CompletableFuture.supplyAsync(() -> {
			Map<String, Tag> privatePocketTagMap = this.privatePocketMap.entrySet().parallelStream().unordered().collect(Collectors.toConcurrentMap(entry -> entry.getKey().toString(), entry -> PocketInfo.toTag(entry.getValue())));
			CompoundTag privatePocketMapTag = new CompoundTag();
			privatePocketTagMap.forEach(privatePocketMapTag::put);
			return privatePocketMapTag;
		});

		tag.put("private_pocket_map", futurePrivatePocketMapTag.join());

		return tag;
	}

	public PrivatePocket getPrivatePocket(UUID playerUUID) {
		PocketInfo pocket = this.privatePocketMap.get(playerUUID);
		if (pocket == null) return null;
		return DimensionalRegistry.getPocketDirectory(pocket.world).getPocket(pocket.id, PrivatePocket.class);
	}

	public void setPrivatePocketID(UUID playerUUID, Pocket pocket) {
		this.privatePocketMap.put(playerUUID, new PocketInfo(pocket.getWorld(), pocket.getId()));
	}

	public UUID getPrivatePocketOwner(Pocket pocket) {
		return this.privatePocketMap.inverse().get(new PocketInfo(pocket.getWorld(), pocket.getId()));
	}
}
