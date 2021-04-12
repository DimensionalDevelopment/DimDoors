package org.dimdev.dimdoors.world.pocket;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.util.Pair;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
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

		public static NbtCompound toTag(PocketInfo info) {
			NbtCompound tag = new NbtCompound();
			tag.putString("world", info.world.getValue().toString());
			tag.putInt("id", info.id);
			return tag;
		}

		public static PocketInfo fromTag(NbtCompound tag) {
			return new PocketInfo(
					RegistryKey.of(Registry.WORLD_KEY, new Identifier(tag.getString("world"))),
					tag.getInt("id")
			);
		}
	}

	private static final String DATA_NAME = "dimdoors_private_pockets";

	protected BiMap<UUID, PocketInfo> privatePocketMap = HashBiMap.create(); // Player UUID -> Pocket Info TODO: fix AnnotatedNBT and use UUID rather than String

	public PrivateRegistry() {
	}

	public void fromTag(NbtCompound tag) {
		privatePocketMap.clear();
		NbtCompound privatePocketMapTag = tag.getCompound("private_pocket_map");
		CompletableFuture<Map<UUID, PocketInfo>> futurePrivatePocketMap = CompletableFuture.supplyAsync(() ->
				privatePocketMapTag.getKeys().stream().unordered().map(key -> {
					NbtCompound pocketInfoTag = privatePocketMapTag.getCompound(key);
					return CompletableFuture.supplyAsync(() -> new Pair<>(UUID.fromString(key), PocketInfo.fromTag(pocketInfoTag)));
				}).parallel().map(CompletableFuture::join).collect(Collectors.toConcurrentMap(Pair::getLeft, Pair::getRight)));

		futurePrivatePocketMap.join().forEach(this.privatePocketMap::put);
	}

	public NbtCompound toTag(NbtCompound tag) {
		CompletableFuture<NbtCompound> futurePrivatePocketMapTag = CompletableFuture.supplyAsync(() -> {
			Map<String, NbtElement> privatePocketTagMap = this.privatePocketMap.entrySet().parallelStream().unordered().collect(Collectors.toConcurrentMap(entry -> entry.getKey().toString(), entry -> PocketInfo.toTag(entry.getValue())));
			NbtCompound privatePocketMapTag = new NbtCompound();
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
