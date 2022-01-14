package org.dimdev.dimdoors.api.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.ExceptionHandlingCollector;
import org.dimdev.dimdoors.api.util.Path;

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

	public static final BiFunction<String, Identifier, Path<String>> PATH_KEY_PROVIDER = (startingPath, id) ->  Path.stringPath(id.getNamespace() + ":" + id.getPath().substring(0, id.getPath().lastIndexOf(".")).substring(startingPath.length() + (startingPath.endsWith("/") ? 0 : 1)));

	public static final ComposableFunction<JsonElement, NbtElement> JSON_TO_NBT = json -> JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, json);

	public static final ComposableFunction<NbtElement, JsonElement> NBT_TO_JSON = json -> NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, json);

	public static final ComposableFunction<InputStream, JsonElement> JSON_READER = inputStream -> GSON.fromJson(new InputStreamReader(inputStream), JsonElement.class);
	public static final ComposableFunction<InputStream, NbtElement> NBT_READER = JSON_READER.andThenComposable(JSON_TO_NBT);
	public static final ComposableFunction<InputStream, NbtCompound> COMPRESSED_NBT_READER = inputStream -> {
		try {
			return NbtIo.readCompressed(inputStream);
		} catch (IOException e) {
			throw new RuntimeException();
		}
	};

	public static  <K, T, M extends Map<K, T>> CompletableFuture<M> loadResourcePathToMap(ResourceManager manager, String startingPath, String extension, M map, BiFunction<InputStream, K, T> reader, BiFunction<String, Identifier, K> keyProvider) {
		Collection<Identifier> ids = manager.findResources(startingPath, str -> str.endsWith(extension));
		return CompletableFuture.supplyAsync(() -> {
			map.putAll(ids.parallelStream().unordered().collect(new ExceptionHandlingCollector<>(Collectors.toConcurrentMap(
					id -> keyProvider.apply(startingPath, id),
					id -> {
						try {
							return reader.apply(manager.getResource(id).getInputStream(), keyProvider.apply(startingPath, id));
						} catch (IOException | RuntimeException e) {
							throw new RuntimeException(e);
						}
					}),
					(a, id, exception) -> LOGGER.error("Error loading resource: " + id, exception))));
			return map;
		});
	}

	public static  <T, M extends Collection<T>> CompletableFuture<M> loadResourcePathToCollection(ResourceManager manager, String startingPath, String extension, M collection, BiFunction<InputStream, Identifier, T> reader) {
		Collection<Identifier> ids = manager.findResources(startingPath, str -> str.endsWith(extension));
		return CompletableFuture.supplyAsync(() -> {
			collection.addAll(ids.parallelStream().unordered().map(id -> {
				try {
					return reader.apply(manager.getResource(id).getInputStream(), id);
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
