package org.dimdev.dimdoors.world.pocket;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.api.util.StreamUtils;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.PrivatePocket;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PrivateRegistry {
	protected static class PocketInfo {
		public final ResourceKey<Level> world;
		public final int id;

		public PocketInfo(ResourceKey<Level> world, int id) {
			this.world = world;
			this.id = id;
		}

		public static CompoundTag toNbt(PocketInfo info) {
			CompoundTag nbt = new CompoundTag();
			nbt.putString("world", info.world.location().toString());
			nbt.putInt("id", info.id);
			return nbt;
		}

		public static PocketInfo fromNbt(CompoundTag nbt) {
			return new PocketInfo(
					ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryParse(nbt.getString("world"))),
					nbt.getInt("id")
			);
		}
	}

	private static final String DATA_NAME = "dimdoors_private_pockets";

	protected BiMap<UUID, PocketInfo> privatePocketMap = HashBiMap.create(); // Player UUID -> Pocket Info TODO: fix AnnotatedNBT and use UUID rather than String

	public PrivateRegistry() {
	}

	public void fromNbt(CompoundTag nbt) {
		privatePocketMap.clear();
		CompoundTag privatePocketMapNbt = nbt.getCompound("private_pocket_map");
		CompletableFuture<Map<UUID, PocketInfo>> futurePrivatePocketMap = CompletableFuture.supplyAsync(() ->
				privatePocketMapNbt.getAllKeys().stream().unordered().map(key -> {
					CompoundTag pocketInfoNbt = privatePocketMapNbt.getCompound(key);
					return CompletableFuture.supplyAsync(() -> new Pair<>(UUID.fromString(key), PocketInfo.fromNbt(pocketInfoNbt)));
				}).parallel().map(CompletableFuture::join).collect(Collectors.toConcurrentMap(Pair::getFirst, Pair::getSecond)));

		this.privatePocketMap.putAll(futurePrivatePocketMap.join());
	}

	public CompoundTag toNbt(CompoundTag nbt) {
		CompletableFuture<CompoundTag> futurePrivatePocketMapNbt = StreamUtils.supplyAsync(() -> {
			Map<String, Tag> privatePocketNbtMap = this.privatePocketMap.entrySet().parallelStream().unordered().collect(Collectors.toConcurrentMap(entry -> entry.getKey().toString(), entry -> PocketInfo.toNbt(entry.getValue())));
			CompoundTag privatePocketMapNbt = new CompoundTag();
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
