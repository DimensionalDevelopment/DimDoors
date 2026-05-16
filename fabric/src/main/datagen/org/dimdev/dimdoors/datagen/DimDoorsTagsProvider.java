package org.dimdev.dimdoors.datagen;

import com.google.common.collect.Maps;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagFile;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

public abstract class DimDoorsTagsProvider<T> implements DataProvider {
    protected final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;
    private final CompletableFuture<Void> contentsDone;
    private final CompletableFuture<TagLookup<T>> parentProvider;
    protected final ResourceKey<? extends Registry<T>> registryKey;
    private final Map<ResourceLocation, TagBuilder> builders;

    protected DimDoorsTagsProvider(PackOutput output, ResourceKey<? extends Registry<T>> registryKey, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        this(output, registryKey, lookupProvider, CompletableFuture.completedFuture(TagLookup.empty()));
    }

    protected DimDoorsTagsProvider(PackOutput output, ResourceKey<? extends Registry<T>> registryKey, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<T>> parentProvider) {
        this.contentsDone = new CompletableFuture<>();
        this.builders = Maps.newLinkedHashMap();
        this.pathProvider = output.createRegistryTagsPathProvider(registryKey);
        this.registryKey = registryKey;
        this.parentProvider = parentProvider;
        this.lookupProvider = lookupProvider;
    }

    public @NotNull String getName() {
        return "Tags for " + String.valueOf(this.registryKey.location());
    }

    protected abstract void addTags(HolderLookup.Provider provider);

    public CompletableFuture<?> run(CachedOutput output) {
        return this.createContentsProvider().thenApply((provider) -> {
            this.contentsDone.complete(null);
            return provider;
        }).thenCombineAsync(this.parentProvider, (provider, tagLookup) -> {
            record CombinedData<T>(HolderLookup.Provider contents, TagLookup<T> parent) {
            }

            return new CombinedData<>(provider, tagLookup);
        }, Util.backgroundExecutor()).thenCompose((arg) -> {
            HolderLookup.RegistryLookup<T> registryLookup = arg.contents.lookupOrThrow(this.registryKey);
            Predicate<ResourceLocation> predicate = (resourceLocation) -> registryLookup.get(ResourceKey.create(this.registryKey, resourceLocation)).isPresent();
            Predicate<ResourceLocation> predicate2 = (resourceLocation) -> this.builders.containsKey(resourceLocation) || arg.parent.contains(TagKey.create(this.registryKey, resourceLocation));
            return CompletableFuture.allOf(this.builders.entrySet().stream().map((entry) -> {
                ResourceLocation resourceLocation = (ResourceLocation)entry.getKey();
                TagBuilder tagBuilder = entry.getValue();
                List<TagEntry> list = tagBuilder.build();

                Path path = this.pathProvider.json(resourceLocation);
                return DataProvider.saveStable(output, arg.contents, TagFile.CODEC, new TagFile(list, false), path);
            }).toArray((i) -> new CompletableFuture[i]));
        });
    }

    protected TagAppender<T> tag(TagKey<T> tag) {
        TagBuilder tagBuilder = this.getOrCreateRawBuilder(tag);
        return new TagAppender<T>(tagBuilder);
    }

    protected TagBuilder getOrCreateRawBuilder(TagKey<T> tag) {
        return this.builders.computeIfAbsent(tag.location(), (resourceLocation) -> TagBuilder.create());
    }

    public CompletableFuture<TagLookup<T>> contentsGetter() {
        return this.contentsDone.thenApply((void_) -> (tagKey) -> Optional.ofNullable((TagBuilder)this.builders.get(tagKey.location())));
    }

    protected CompletableFuture<HolderLookup.Provider> createContentsProvider() {
        return this.lookupProvider.thenApply((provider) -> {
            this.builders.clear();
            this.addTags(provider);
            return provider;
        });
    }

    @FunctionalInterface
    public interface TagLookup<T> extends Function<TagKey<T>, Optional<TagBuilder>> {
        static <T> TagLookup<T> empty() {
            return (tagKey) -> Optional.empty();
        }

        default boolean contains(TagKey<T> tagKey) {
            return ((Optional)this.apply(tagKey)).isPresent();
        }
    }

    public static class TagAppender<T> {
        private final TagBuilder builder;

        protected TagAppender(TagBuilder builder) {
            this.builder = builder;
        }

        public final TagAppender<T> add(ResourceKey<T> key) {
            this.builder.addElement(key.location());
            return this;
        }

        @SafeVarargs
        public final TagAppender<T> add(ResourceKey<T>... keys) {
            for(ResourceKey<T> resourceKey : keys) {
                this.builder.addElement(resourceKey.location());
            }

            return this;
        }

        public final TagAppender<T> addAll(List<ResourceKey<T>> keys) {
            for(ResourceKey<T> resourceKey : keys) {
                this.builder.addElement(resourceKey.location());
            }

            return this;
        }

        public TagAppender<T> addOptional(ResourceLocation location) {
            this.builder.addOptionalElement(location);
            return this;
        }

        public TagAppender<T> addTag(TagKey<T> tag) {
            this.builder.addTag(tag.location());
            return this;
        }

        public TagAppender<T> addOptionalTag(ResourceLocation location) {
            this.builder.addOptionalTag(location);
            return this;
        }
    }
}
