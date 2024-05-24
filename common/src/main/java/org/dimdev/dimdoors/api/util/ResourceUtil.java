package org.dimdev.dimdoors.api.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ResourceUtil {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();

	public static final BiFunction<String, ResourceLocation, Path<String>> PATH_KEY_PROVIDER = (startingPath, id) ->  Path.stringPath(id.getNamespace() + ":" + id.getPath().substring(0, id.getPath().lastIndexOf(".")).substring(startingPath.length() + (startingPath.endsWith("/") ? 0 : 1)));

	public static final ComposableFunction<JsonElement, Tag> JSON_TO_NBT = json -> JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, json);

	public static final ComposableFunction<Tag, JsonElement> NBT_TO_JSON = json -> NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, json);

	public static final ComposableFunction<InputStream, JsonElement> JSON_READER = inputStream -> GSON.fromJson(new InputStreamReader(inputStream), JsonElement.class);
	public static final ComposableFunction<InputStream, Tag> NBT_READER = JSON_READER.andThenComposable(JSON_TO_NBT);

	public static <T, K, U> BiFunction<T, K, U> codec(DynamicOps<T> ops, Codec<U> codec) {
		return (t, ignored) -> ops.withDecoder(codec).andThen(DataResult::getOrThrow).andThen(Pair::getFirst).apply(t);
	}

	public static final ComposableFunction<InputStream, CompoundTag> COMPRESSED_NBT_READER = inputStream -> {
		try {
			return NbtIo.readCompressed(inputStream);
		} catch (IOException e) {
			throw new RuntimeException();
		}
	};

	public static <R extends ReferenceSerializable> R loadReferencedResource(ResourceManager manager, String startingPath, ResourceLocation resourceKey, Function<InputStream, R> reader) {
		return loadResource(manager, resourceKey.withPath(startingPath), reader);
	}

	public static <R> R loadResource(ResourceManager manager, ResourceLocation resourceKey, Function<InputStream, R> reader) {
		try {
			return reader.apply(manager.getResource(resourceKey).get().open());
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static  <K, T, M extends Map<K, T>> CompletableFuture<M> loadResourcePathToMap(ResourceManager manager, String startingPath, String extension, M map, BiFunction<InputStream, K, T> reader, BiFunction<String, ResourceLocation, K> keyProvider) {
		Map<ResourceLocation, Resource> ids = new FileToIdConverter(extension, extension).listMatchingResources(manager);
		return StreamUtils.supplyAsync(() -> {
			map.putAll(ids.entrySet().parallelStream().unordered().collect(new ExceptionHandlingCollector<>(Collectors.toConcurrentMap(
					id -> keyProvider.apply(startingPath, id.getKey()),
					id -> {
						try {
							return reader.apply(id.getValue().open(), keyProvider.apply(startingPath, id.getKey()));
						} catch (IOException | RuntimeException e) {
							throw new RuntimeException(e);
						}
					}),
					(a, id, exception) -> LOGGER.error("Error loading resource: " + id, exception))));
			return map;
		});
	}

	public static  <T, M extends Collection<T>> CompletableFuture<M> loadResourcePathToCollection(ResourceManager manager, String startingPath, String extension, M collection, BiFunction<InputStream, ResourceLocation, T> reader) {
		Map<ResourceLocation, Resource> ids = new FileToIdConverter(startingPath, extension).listMatchingResources(manager);
		return StreamUtils.supplyAsync(() -> {
			collection.addAll(ids.entrySet().parallelStream().unordered().map(id -> {
				try {
					return reader.apply(id.getValue().open(), id.getKey());
				} catch (Exception e) {
					LOGGER.error("Error loading resource: " + id, e);
					return null;
				}
			}).collect(Collectors.filtering(Objects::nonNull, Collectors.toList()))); // TODO: change this to smthn concurrent
			return collection;
		});
	}

	public interface ComposableFunction<T, R> extends Function<T, R> {
		 default <K, V> BiFunction<T, K, V> andThenReader(BiFunction<R, K, V> function) {
			return (t, k) -> function.apply(apply(t), k);
		 }

		default <K> BiFunction<T, K, R> composeIdentity() {
			return (t, k) -> apply(t);
		}

		default <V> ComposableFunction<T, V> andThenComposable(Function<? super R, ? extends V> after) {
			return (T t) -> after.apply(apply(t));
		}
	}
}
