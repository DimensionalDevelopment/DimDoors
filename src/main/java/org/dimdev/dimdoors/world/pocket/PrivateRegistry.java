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

		public static NbtCompound toNbt(PocketInfo info) {
			NbtCompound nbt = new NbtCompound();
			nbt.putString("world", info.world.getValue().toString());
			nbt.putInt("id", info.id);
			return nbt;
		}

		public static PocketInfo fromNbt(NbtCompound nbt) {
			return new PocketInfo(
					RegistryKey.of(Registry.WORLD_KEY, new Identifier(nbt.getString("world"))),
					nbt.getInt("id")
			);
		}
	}

	private static final String DATA_NAME = "dimdoors_private_pockets";

	protected BiMap<UUID, PocketInfo> privatePocketMap = HashBiMap.create(); // Player UUID -> Pocket Info TODO: fix AnnotatedNBT and use UUID rather than String

	public PrivateRegistry() {
	}

	public void fromNbt(NbtCompound nbt) {
		privatePocketMap.clear();
		NbtCompound privatePocketMapNbt = nbt.getCompound("private_pocket_map");
		CompletableFuture<Map<UUID, PocketInfo>> futurePrivatePocketMap = CompletableFuture.supplyAsync(() ->
				privatePocketMapNbt.getKeys().stream().unordered().map(key -> {
					NbtCompound pocketInfoNbt = privatePocketMapNbt.getCompound(key);
					return CompletableFuture.supplyAsync(() -> new Pair<>(UUID.fromString(key), PocketInfo.fromNbt(pocketInfoNbt)));
				}).parallel().map(CompletableFuture::join).collect(Collectors.toConcurrentMap(Pair::getLeft, Pair::getRight)));

		futurePrivatePocketMap.join().forEach(this.privatePocketMap::put);
	}

	public NbtCompound toNbt(NbtCompound nbt) {
		CompletableFuture<NbtCompound> futurePrivatePocketMapNbt = CompletableFuture.supplyAsync(() -> {
			Map<String, NbtElement> privatePocketNbtMap = this.privatePocketMap.entrySet().parallelStream().unordered().collect(Collectors.toConcurrentMap(entry -> entry.getKey().toString(), entry -> PocketInfo.toNbt(entry.getValue())));
			NbtCompound privatePocketMapNbt = new NbtCompound();
			privatePocketNbtMap.forEach(privatePocketMapNbt::put);
			return privatePocketMapNbt;
		});

		nbt.put("private_pocket_map", futurePrivatePocketMapNbt.join());

		return nbt;
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
