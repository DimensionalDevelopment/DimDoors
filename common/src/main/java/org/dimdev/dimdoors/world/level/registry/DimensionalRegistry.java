package org.dimdev.dimdoors.world.level.registry;

import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.NbtLoaderUtil;
import org.dimdev.dimdoors.api.util.NbtUtil;
import org.dimdev.dimdoors.api.util.StreamUtils;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;
import org.dimdev.dimdoors.world.pocket.PrivateRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.minecraft.nbt.NbtOps.INSTANCE;
import static org.dimdev.dimdoors.api.util.NbtLoaderUtil.joinOrThrow;

public class DimensionalRegistry extends SavedData {
	public static final int RIFT_DATA_VERSION = 1; // Increment this number every time a new schema is added
	private Map<ResourceKey<Level>, PocketDirectory> pocketRegistry;
	private RiftRegistry riftRegistry;
	private PrivateRegistry privateRegistry;

	public DimensionalRegistry(Map<ResourceKey<Level>, PocketDirectory> pocketRegistry, RiftRegistry riftRegistry, PrivateRegistry privateRegistry) {
        this.pocketRegistry = pocketRegistry;
        this.riftRegistry = riftRegistry;
        this.privateRegistry = privateRegistry;
    }

	public DimensionalRegistry() {
		this(new HashMap<>() , new RiftRegistry(), new PrivateRegistry());
	}

	public static void init(MinecraftServer server) {
		server.overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<DimensionalRegistry>(DimensionalRegistry::new, (compoundTag, provider) -> INSTANCE.withDecoder(  ,CODEC_BASE).andThen(a -> a.map(Pair::getFirst)).andThen(a -> a.result()).apply(compoundTag).orElseThrow(), DataFixTypes.LEVEL /*TODO: FIgure out if correct for a singlemon data*/), "dimensional_registry");
	}

	public static final Codec<DimensionalRegistry> CODEC_BASE = RecordCodecBuilder.create(instance -> instance.group(
			Codec.unboundedMap(ResourceKey.codec(Registries.DIMENSION), PocketDirectory.CODEC).fieldOf("pocket_registry").forGetter(a -> getPocketDirectories()),
			RiftRegistry.RiftRegistryData.CODEC.fieldOf("rift_registry").forGetter(a -> getRiftRegistry().asRawData()),
			PrivateRegistry.CODEC.fieldOf("private_registry").forGetter(a -> getPrivateRegistry())

	).apply(instance, (pocketDirectoryMap, riftRegistryData, privateRegistry) -> new DimensionalRegistry(pocketDirectoryMap, riftRegistryData.create(pocketDirectoryMap), privateRegistry)));

	@Override
	public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
		return writeToNbt(compoundTag);
	}

//	public static Codec<DimensionalRegistry> CODEC = Codec.PASSTHROUGH.comapFlatMap((Function<Dynamic<?>, DataResult<DimensionalRegistry>>) dynamic -> {
//        int riftDataVersion = dynamic.get("RiftDataVersion").asInt(-1);
//
//		if(riftDataVersion == -1) riftDataVersion = dynamic.get("version").asInt(-1);
//
//        if (riftDataVersion < 0) {
//            throw new IllegalStateException("RiftDataVersion can not be invalid");
//        } else if (riftDataVersion < RIFT_DATA_VERSION) {
//            dynamic = RiftSchemas.update(riftDataVersion, dynamic);
//        } else if (RIFT_DATA_VERSION < riftDataVersion) {
//            return DataResult.error(() -> "Downgrading is not supported!");
//        }
//
//        return dynamic.read(CODEC_BASE);
//    }, dimensionalRegistry -> {
//        var tag = CODEC_BASE.encodeStart(INSTANCE, dimensionalRegistry).getPartialOrThrow();
//        var dynamic = new Dynamic<>(INSTANCE, tag);
//        return dynamic.set("version", dynamic.createInt(RIFT_DATA_VERSION));
//    });

	private static Factory<DimensionalRegistry> FACTORY = new Factory<>(DimensionalRegistry::new, (tag, provider) -> of(tag), DataFixTypes.LEVEL);

	public static DimensionalRegistry of(CompoundTag nbt) {
		int riftDataVersion = nbt.getInt("RiftDataVersion");
		if (riftDataVersion < RIFT_DATA_VERSION) {
			Tag updatedTag = RiftSchemas.update(riftDataVersion, new Dynamic<>(INSTANCE, nbt)).getValue();
			if (!(updatedTag instanceof CompoundTag updatedCompound)) {
				throw new IllegalStateException("Updated tag is not a CompoundTag: " + updatedTag.getClass().getName());
			}
			nbt = updatedCompound;
		} else if (RIFT_DATA_VERSION < riftDataVersion) {
			throw new UnsupportedOperationException("Downgrading is not supported!");
		}

		CompoundTag pocketRegistryNbt = NbtLoaderUtil.getRequiredCompound(nbt, "pocket_registry");

		List<CompletableFuture<Pair<ResourceKey<Level>, PocketDirectory>>> pocketFutures = pocketRegistryNbt.getAllKeys().stream()
				.map(key -> {
					CompoundTag directoryTag = NbtLoaderUtil.getRequiredCompound(pocketRegistryNbt, key);
					ResourceLocation location = ResourceLocation.tryParse(key);
					if (location == null) {
						throw new IllegalArgumentException("Invalid resource location: " + key);
					}
					ResourceKey<Level> resourceKey = ResourceKey.create(Registries.DIMENSION, location);

					return CompletableFuture.supplyAsync(() -> {
						try {
							return new Pair<>(resourceKey, PocketDirectory.readFromNbt(resourceKey, directoryTag));
						} catch (Exception e) {
							throw new CompletionException("Failed to parse PocketDirectory for key: " + key, e);
						}
					});
				})
				.toList();

		CompletableFuture<Map<ResourceKey<Level>, PocketDirectory>> futurePocketRegistry = NbtLoaderUtil.joinAllFutures(pocketFutures, "PocketRegistry");

		Map<ResourceKey<Level>, PocketDirectory> pocketRegistry = joinOrThrow(futurePocketRegistry, "PocketRegistry");

		CompoundTag privateRegistryNbt = NbtLoaderUtil.getRequiredCompound(nbt, "private_registry");
		CompletableFuture<PrivateRegistry> futurePrivateRegistry = NbtLoaderUtil.asyncDecode(privateRegistryNbt, PrivateRegistry.CODEC, "PrivateRegistry");
		PrivateRegistry privateRegistry = joinOrThrow(futurePrivateRegistry, "PrivateRegistry");

		CompoundTag riftRegistryNbt = NbtLoaderUtil.getRequiredCompound(nbt, "rift_registry");
		CompletableFuture<RiftRegistry> futureRiftRegistry = CompletableFuture.supplyAsync(() -> {
			try {
				return NbtUtil.deserialize(riftRegistryNbt, RiftRegistry.RiftRegistryData.CODEC).create(pocketRegistry);
			} catch (Exception e) {
				throw new CompletionException("Failed to deserialize RiftRegistry", e);
			}
		});
		RiftRegistry riftRegistry = joinOrThrow(futureRiftRegistry, "RiftRegistry");

		return new DimensionalRegistry(pocketRegistry, riftRegistry, privateRegistry);
	}

	public CompoundTag writeToNbt(CompoundTag nbt) {
		// Serialize pocketRegistry in parallel
		CompletableFuture<Tag> futurePocketRegistryNbt = StreamUtils.supplyAsync(() -> {
			List<CompletableFuture<Pair<String, Tag>>> futures = pocketRegistry.entrySet().stream()
					.map(entry -> CompletableFuture.supplyAsync(() -> {
						String id = entry.getKey().location().toString();
						Tag tag = entry.getValue().writeToNbt();
						return new Pair<>(id, tag);
					}))
					.toList();

			Map<String, Tag> result = futures.parallelStream()
					.map(CompletableFuture::join)
					.collect(Collectors.toConcurrentMap(Pair::getFirst, Pair::getSecond));

			CompoundTag compound = new CompoundTag();
			result.forEach(compound::put);
			return compound;
		});

		// Serialize riftRegistry and privateRegistry
		CompletableFuture<Tag> futureRiftRegistryNbt =
				StreamUtils.supplyAsync(() -> NbtUtil.serialize(riftRegistry.asRawData(), RiftRegistry.RiftRegistryData.CODEC));

		CompletableFuture<Tag> futurePrivateRegistryNbt =
				CompletableFuture.supplyAsync(() -> NbtUtil.serialize(privateRegistry, PrivateRegistry.CODEC));

		// Wait for all futures and write to the compound tag
		nbt.put("pocket_registry", joinOrThrow(futurePocketRegistryNbt, "pocket_registry"));
		nbt.put("rift_registry", joinOrThrow(futureRiftRegistryNbt, "rift_registry"));
		nbt.put("private_registry", joinOrThrow(futurePrivateRegistryNbt, "private_registry"));
		nbt.putInt("RiftDataVersion", RIFT_DATA_VERSION);

		return nbt;
	}

	public static DimensionalRegistry getInstance() {
		return DimensionalDoors.getServer().overworld().getDataStorage().computeIfAbsent(FACTORY, "dimensional_registry");
	}

	public static RiftRegistry getRiftRegistry() {
		return getInstance().riftRegistry;
	}

	public static PrivateRegistry getPrivateRegistry() {
		return getInstance().privateRegistry;
	}

	public static PocketDirectory getPocketDirectory(ResourceKey<Level> key) {
		if (!(ModDimensions.isPocketDimension(key))) {
			throw new UnsupportedOperationException("PocketRegistry is only available for pocket dimensions!");
		}

		return getInstance().pocketRegistry.computeIfAbsent(key, PocketDirectory::new);
	}

	private static Map<ResourceKey<Level>, PocketDirectory> getPocketDirectories() {
		return getInstance().pocketRegistry;
	}

	public static boolean isValidWorld(Level level) {
        if (level == null) return false;
        return level.dimension().equals(Level.OVERWORLD);
	}

	public Map<ResourceKey<Level>, PocketDirectory> getPocketRegistry() {
		return pocketRegistry;
	}

	@Override
	public boolean isDirty() {
		return true;
	}
}
