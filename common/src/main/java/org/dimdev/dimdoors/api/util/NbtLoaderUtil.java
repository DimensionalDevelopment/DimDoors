package org.dimdev.dimdoors.api.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

/**
 * Utility class for safely loading and decoding NBT-based data using codecs.
 * Designed for parallel deserialization with robust error handling and validation.
 */
public final class NbtLoaderUtil {

    private NbtLoaderUtil() {
    // Prevent instantiation
    }

    /**
     * Retrieves a required {@link CompoundTag} from a parent NBT compound.
     *
     * @param root the root compound containing the target tag
     * @param key  the key of the compound tag to retrieve
     * @return the compound tag associated with the given key
     * @throws IllegalArgumentException if the key is missing or not a compound
     */
    public static CompoundTag getRequiredCompound(CompoundTag root, String key) {
    if (!root.contains(key, Tag.TAG_COMPOUND)) {
        throw new IllegalArgumentException("Missing required compound tag: " + key);
    }
    return root.getCompound(key);
    }

    /**
     * Asynchronously decodes a {@link CompoundTag} using the given {@link Codec}.
     * Exceptions are wrapped in a {@link CompletionException} with context information.
     *
     * @param tag         the compound tag to decode
     * @param codec       the codec to use for decoding
     * @param contextName a descriptive name used in error messages for debugging
     * @param <T>         the target type
     * @return a {@link CompletableFuture} that resolves to the decoded object
     */
    public static <T> CompletableFuture<T> asyncDecode(CompoundTag tag, Codec<T> codec, String contextName) {
    return CompletableFuture.supplyAsync(() -> {
        try {
        return NbtUtil.deserialize(tag, codec);
        } catch (Exception e) {
        throw new CompletionException("Failed to decode " + contextName, e);
        }
    });
    }

    /**
     * Joins a {@link CompletableFuture} and throws an {@link IllegalStateException}
     * if it fails, preserving the original cause and adding a contextual message.
     *
     * @param future      the future to join
     * @param contextName a descriptive name used in error messages
     * @param <T>         the type of result expected from the future
     * @return the result of the future
     * @throws IllegalStateException if the future completes exceptionally
     */
    public static <T> T joinOrThrow(CompletableFuture<T> future, String contextName) {
    try {
        return future.join();
    } catch (CompletionException e) {
        throw new IllegalStateException(contextName + " failed: " + e.getCause().getMessage(), e.getCause());
    }
    }

    /**
     * Joins a list of {@link CompletableFuture}s producing {@link Pair}s into a {@link Map}.
     * If any future fails, the method throws with a detailed error message.
     *
     * @param futures     the list of futures to join
     * @param contextName a descriptive name used in error messages
     * @param <K>         the type of keys in the result map
     * @param <V>         the type of values in the result map
     * @return a {@link Map} assembled from the result pairs
     * @throws IllegalStateException if any future fails during joining
     */
    public static <K, V> CompletableFuture<Map<K, V>> joinAllFutures(
    List<CompletableFuture<Pair<K, V>>> futures,
    String contextName
    ) {
    return StreamUtils.supplyAsync(() -> {
        try {
        return futures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toConcurrentMap(Pair::getFirst, Pair::getSecond));
        } catch (CompletionException e) {
        throw new IllegalStateException(contextName + " batch load failed: " + e.getCause().getMessage(), e.getCause());
        }
    });
    }
}
