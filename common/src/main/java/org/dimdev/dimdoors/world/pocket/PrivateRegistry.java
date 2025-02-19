package org.dimdev.dimdoors.world.pocket;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.PrivatePocket;

import java.util.UUID;
import java.util.function.Function;

public class PrivateRegistry {
	protected record PocketInfo(ResourceKey<Level> world, int id) {
		public static final Codec<PocketInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ResourceKey.codec(Registries.DIMENSION).fieldOf("world").forGetter(a -> a.world),
				Codec.INT.fieldOf("id").forGetter(a -> a.id)
		).apply(instance, PocketInfo::new));
	}

	public static final Codec<PrivateRegistry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.unboundedMap(UUIDUtil.STRING_CODEC, PocketInfo.CODEC).xmap(HashBiMap::create, Function.identity()).fieldOf("private_pocket_map").forGetter(a -> (HashBiMap<UUID, PocketInfo>) a.privatePocketMap)
	).apply(instance, PrivateRegistry::new));

	private static final String DATA_NAME = "dimdoors_private_pockets";

	protected BiMap<UUID, PocketInfo> privatePocketMap; // Player UUID -> Pocket Info TODO: fix AnnotatedNBT and use UUID rather than String

	public PrivateRegistry() {
		this(HashBiMap.create());
	}

	private PrivateRegistry(BiMap<UUID, PocketInfo> privatePocketMap) {
        this.privatePocketMap = privatePocketMap;
    }

//	public void fromNbt(CompoundTag nbt) {
//		privatePocketMap.clear();
//		CompoundTag privatePocketMapNbt = nbt.getCompound("private_pocket_map");
//		CompletableFuture<Map<UUID, PocketInfo>> futurePrivatePocketMap = CompletableFuture.supplyAsync(() ->
//				privatePocketMapNbt.getAllKeys().stream().unordered().map(key -> {
//					CompoundTag pocketInfoNbt = privatePocketMapNbt.getCompound(key);
//					return CompletableFuture.supplyAsync(() -> new Pair<>(UUID.fromString(key), PocketInfo.fromNbt(pocketInfoNbt)));
//				}).parallel().map(CompletableFuture::join).collect(Collectors.toConcurrentMap(Pair::getFirst, Pair::getSecond)));
//
//		this.privatePocketMap.putAll(futurePrivatePocketMap.join());
//	}
//
//	public CompoundTag toNbt(CompoundTag nbt) {
//		CompletableFuture<CompoundTag> futurePrivatePocketMapNbt = StreamUtils.supplyAsync(() -> {
//			Map<String, Tag> privatePocketNbtMap = this.privatePocketMap.entrySet().parallelStream().unordered().collect(Collectors.toConcurrentMap(entry -> entry.getKey().toString(), entry -> PocketInfo.toNbt(entry.getValue())));
//			CompoundTag privatePocketMapNbt = new CompoundTag();
//			privatePocketNbtMap.forEach(privatePocketMapNbt::put);
//			return privatePocketMapNbt;
//		});
//
//		nbt.put("private_pocket_map", futurePrivatePocketMapNbt.join());
//
//		return nbt;
//	}

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
