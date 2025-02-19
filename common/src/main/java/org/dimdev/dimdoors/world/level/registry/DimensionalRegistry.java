package org.dimdev.dimdoors.world.level.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;
import org.dimdev.dimdoors.world.pocket.PrivateRegistry;

import java.util.Map;
import java.util.function.Function;

public enum DimensionalRegistry {
	INSTANCE;

	public static final int RIFT_DATA_VERSION = 1; // Increment this number every time a new schema is added
	private static Map<ResourceKey<Level>, PocketDirectory> pocketRegistry;
	private static RiftRegistry riftRegistry;
	private static PrivateRegistry privateRegistry;

	private static final ProxyData instance = new ProxyData();

	public static void init(MinecraftServer server) {
		server.overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<ProxyData>(() -> instance, (compoundTag, provider) -> {
            NbtOps.INSTANCE.withDecoder(CODEC_BASE).apply(compoundTag);
            return instance;
        }, DataFixTypes.LEVEL /*TODO: FIgure out if correct for a singlemon data*/), "dimensional_registry");
	}

	public static final Codec<DimensionalRegistry> CODEC_BASE = RecordCodecBuilder.create(instance -> instance.group(
			PrivateRegistry.CODEC.fieldOf("private_registry").forGetter(a -> getPrivateRegistry()),
			Codec.unboundedMap(ResourceKey.codec(Registries.DIMENSION), PocketDirectory.CODEC).fieldOf("pocket_registry").forGetter(a -> getPocketDirectories()),
			RiftRegistry.RiftRegistryData.CODEC.fieldOf("rift_registry").forGetter(a -> getRiftRegistry().asRawData())

	).apply(instance, (privateRegistry, pocketDirectoryMap, riftRegistryData) -> {
			DimensionalRegistry.privateRegistry = privateRegistry;
			pocketRegistry = pocketDirectoryMap;
			DimensionalRegistry.riftRegistry = riftRegistryData.create(pocketDirectoryMap);
			return INSTANCE;
	}));

	public static Codec<DimensionalRegistry> CODEC = Codec.PASSTHROUGH.comapFlatMap((Function<Dynamic<?>, DataResult<DimensionalRegistry>>) dynamic -> {
        int riftDataVersion = dynamic.get("RiftDataVersion").asInt(-1);

		if(riftDataVersion == -1) riftDataVersion = dynamic.get("version").asInt(-1);

        if (riftDataVersion < 0) {
            throw new IllegalStateException("RiftDataVersion can not be invalid");
        } else if (riftDataVersion < RIFT_DATA_VERSION) {
            dynamic = RiftSchemas.update(riftDataVersion, dynamic);
        } else if (RIFT_DATA_VERSION < riftDataVersion) {
            throw new UnsupportedOperationException("Downgrading is not supported!");
        }

        return dynamic.read(CODEC_BASE);
    }, dimensionalRegistry -> {
        var tag = CODEC_BASE.encodeStart(NbtOps.INSTANCE, dimensionalRegistry).getPartialOrThrow();
        var dynamic = new Dynamic<>(NbtOps.INSTANCE, tag);
        return dynamic.set("version", dynamic.createInt(RIFT_DATA_VERSION));
    });

//	public void readFromNbt(Dynamic<?> nbt) {
//
//		int riftDataVersion = nbt.get("RiftDataVersion").asInt(0);
//		if (riftDataVersion < RIFT_DATA_VERSION) {
//			nbt = RiftSchemas.update(riftDataVersion, nbt);
//		} else if (RIFT_DATA_VERSION < riftDataVersion) {
//			throw new UnsupportedOperationException("Downgrading is not supported!");
//		}
//
//		Map<? extends Dynamic<?>, ? extends Dynamic<?>> pocketRegistryNbt = nbt.get("pocket_registry").orElseEmptyMap().getMapValues().getOrThrow();
//
//
//		CompletableFuture<Map<ResourceKey<Level>, PocketDirectory>> futurePocketRegistry = CompletableFuture.supplyAsync(() -> pocketRegistryNbt.keySet().stream().map(key -> {
//					Dynamic<?> pocketDirectoryNbt = pocketRegistryNbt.get(key);
//					return CompletableFuture.supplyAsync(() -> new Pair<>(ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryParse(key.asString(""))), PocketDirectory.readFromNbt(key, pocketDirectoryNbt)));
//				}).parallel().map(CompletableFuture::join).collect(Collectors.toConcurrentMap(Pair::getFirst, Pair::getSecond)));
//
//		CompoundTag privateRegistryNbt = nbt.getCompound("private_registry");
//		CompletableFuture<PrivateRegistry> futurePrivateRegistry = CompletableFuture.supplyAsync(() -> {
//			PrivateRegistry privateRegistry = new PrivateRegistry();
//			privateRegistry.fromNbt(privateRegistryNbt);
//			return privateRegistry;
//		});
//
//		pocketRegistry = futurePocketRegistry.join();
//
//		CompoundTag riftRegistryNbt = nbt.getCompound("rift_registry");
//		CompletableFuture<RiftRegistry> futureRiftRegistry = CompletableFuture.supplyAsync(() -> RiftRegistry.fromNbt(pocketRegistry, riftRegistryNbt));
//		riftRegistry = futureRiftRegistry.join();
//
//		privateRegistry = futurePrivateRegistry.join();
//	}
//
//	public static void writeToNbt(CompoundTag nbt) {
//		CompletableFuture<Tag> futurePocketRegistryNbt = StreamUtils.supplyAsync(() -> {
//			List<CompletableFuture<Pair<String, Tag>>> futurePocketRegistryNbts = new ArrayList<>();
//			pocketRegistry.forEach((key, value) -> futurePocketRegistryNbts.add(CompletableFuture.supplyAsync(() -> new Pair<>(key.location().toString(), value.writeToNbt()))));
//			CompoundTag pocketRegistryNbt = new CompoundTag();
//			futurePocketRegistryNbts.parallelStream().unordered().map(CompletableFuture::join).collect(Collectors.toConcurrentMap(Pair::getFirst, Pair::getSecond)).forEach(pocketRegistryNbt::put);
//			return pocketRegistryNbt;
//		});
//
//		CompletableFuture<Tag> futureRiftRegistryNbt = StreamUtils.supplyAsync(riftRegistry::toNbt);
//		CompletableFuture<Tag> futurePrivateRegistryNbt = CompletableFuture.supplyAsync(() -> privateRegistry.toNbt(new CompoundTag()));
//
//		nbt.put("pocket_registry", futurePocketRegistryNbt.join());
//		nbt.put("rift_registry", futureRiftRegistryNbt.join());
//		nbt.put("private_registry", futurePrivateRegistryNbt.join());
//
//		nbt.putInt("RiftDataVersion", RIFT_DATA_VERSION);
//	}

	public static RiftRegistry getRiftRegistry() {
		return riftRegistry;
	}

	public static PrivateRegistry getPrivateRegistry() {
		return privateRegistry;
	}

	public static PocketDirectory getPocketDirectory(ResourceKey<Level> key) {
		if (!(ModDimensions.isPocketDimension(key))) {
			throw new UnsupportedOperationException("PocketRegistry is only available for pocket dimensions!");
		}

		return pocketRegistry.computeIfAbsent(key, PocketDirectory::new);
	}

	private static Map<ResourceKey<Level>, PocketDirectory> getPocketDirectories() {
		return pocketRegistry;
	}

	public static boolean isValidWorld(Level level) {
        if (level == null) return false;
        return level.dimension().equals(Level.OVERWORLD);
	}

	public Map<ResourceKey<Level>, PocketDirectory> getPocketRegistry() {
		return pocketRegistry;
	}

	private static class ProxyData extends SavedData {

		@Override
		public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
			DimensionalRegistry.CODEC.encode(DimensionalRegistry.INSTANCE, NbtOps.INSTANCE, compoundTag);
			return compoundTag;
		}

		@Override
		public boolean isDirty() {
			return true;
		}
	}
}
