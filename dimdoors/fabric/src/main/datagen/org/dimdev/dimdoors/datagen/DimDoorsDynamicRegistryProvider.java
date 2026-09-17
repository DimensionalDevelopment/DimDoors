package org.dimdev.dimdoors.datagen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public abstract class DimDoorsDynamicRegistryProvider implements DataProvider {
    private final FabricDataOutput output;
    private final CompletableFuture<HolderLookup.Provider> registriesFuture;

    public DimDoorsDynamicRegistryProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        this.output = output;
        this.registriesFuture = registriesFuture;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        return registriesFuture.thenCompose(registries -> CompletableFuture.supplyAsync(() -> {
            var entries = new Entries(registries);
            var provider = entries.createProvider(registries);

            configure(new RegistrationHelper(provider, entries));

            return new ConfiguredEntries(entries, provider);
        }, Util.backgroundExecutor()).thenCompose(configured -> CompletableFuture.allOf(configured.entries.registries.values().stream()
                .flatMap(registryEntries -> registryEntries.write(cachedOutput, configured.provider))
                .toArray(CompletableFuture[]::new))));
    }

    protected abstract void configure(RegistrationHelper helper);

    public record RegistrationHelper(HolderLookup.Provider registries, Entries entries) {
        public <T> Holder<T> register(ResourceKey<T> key, T value) {
            entries.add(key, value);
            return lookup(key);
        }

        public <T> Holder<T> lookup(ResourceKey<T> key) {
            return registries.lookupOrThrow(key.registryKey()).getOrThrow(key);
        }

        public <T> HolderLookup.RegistryLookup<T> registrylookup(ResourceKey<Registry<T>> key) {
            return registries.lookupOrThrow(key);
        }
    }

    private record ConfiguredEntries(Entries entries, HolderLookup.Provider provider) {
    }

    public class Entries {
        private final Map<ResourceKey<? extends Registry<?>>, RegistryEntries<?>> registries = new LinkedHashMap<>();

        private Entries(HolderLookup.Provider registries) {
            DynamicRegistries.getDynamicRegistries().stream()
                    .filter(data -> registries.lookup(data.key()).isPresent())
                    .forEach(this::addRegistry);
        }

        private <T> void addRegistry(RegistryDataLoader.RegistryData<T> data) {
            registries.put(data.key(), new RegistryEntries<>(data.key(), data.elementCodec()));
        }

        public <T> void add(ResourceKey<T> key, T value) {
            registry(key).add(key, value);
        }

        @SuppressWarnings("unchecked")
        private <T> RegistryEntries<T> registry(ResourceKey<T> key) {
            var registry = (RegistryEntries<T>) registries.get(key.registryKey());

            if (registry == null) {
                throw new IllegalArgumentException("Registry " + key.registry() + " is not loaded from datapacks");
            }

            return registry;
        }

        private HolderLookup.Provider createProvider(HolderLookup.Provider parent) {
            Map<ResourceKey<? extends Registry<?>>, HolderLookup.RegistryLookup<?>> overlays = new LinkedHashMap<>();
            registries.forEach((key, entries) -> overlays.put(key, entries.createLookup(parent)));
            Map<ResourceKey<? extends Registry<?>>, Optional<HolderLookup.RegistryLookup<?>>> parentLookups = new LinkedHashMap<>();

            return new HolderLookup.Provider() {
                @Override
                public Stream<ResourceKey<? extends Registry<?>>> listRegistries() {
                    return Stream.concat(parent.listRegistries(), overlays.keySet().stream()).distinct();
                }

                @Override
                @SuppressWarnings("unchecked")
                public <T> Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> key) {
                    HolderLookup.RegistryLookup<T> overlay = (HolderLookup.RegistryLookup<T>) overlays.get(key);

                    if (overlay != null) {
                        return Optional.of(overlay);
                    }

                    return (Optional<HolderLookup.RegistryLookup<T>>) (Optional<?>) parentLookups.computeIfAbsent((ResourceKey<? extends Registry<?>>) key, ignored -> (Optional<HolderLookup.RegistryLookup<?>>) (Optional<?>) wrapParentLookup(parent, key));
                }
            };
        }

        private <T> Optional<HolderLookup.RegistryLookup<T>> wrapParentLookup(HolderLookup.Provider parent, ResourceKey<? extends Registry<? extends T>> key) {
            return parent.lookup(key).map(parentLookup -> new HolderLookup.RegistryLookup<>() {
                @Override
                public ResourceKey<? extends Registry<? extends T>> key() {
                    return parentLookup.key();
                }

                @Override
                public Lifecycle registryLifecycle() {
                    return parentLookup.registryLifecycle();
                }

                @Override
                public Optional<Holder.Reference<T>> get(ResourceKey<T> elementKey) {
                    return parentLookup.get(elementKey).map(ignored -> Holder.Reference.createStandAlone(this, elementKey));
                }

                @Override
                public Optional<HolderSet.Named<T>> get(TagKey<T> tagKey) {
                    return parentLookup.get(tagKey).map(ignored -> HolderSet.emptyNamed(this, tagKey));
                }

                @Override
                public Stream<Holder.Reference<T>> listElements() {
                    return parentLookup.listElementIds().map(elementKey -> Holder.Reference.createStandAlone(this, elementKey));
                }

                @Override
                public Stream<HolderSet.Named<T>> listTags() {
                    return parentLookup.listTagIds().map(tagKey -> HolderSet.emptyNamed(this, tagKey));
                }
            });
        }
    }

    private class RegistryEntries<T> {
        private final ResourceKey<? extends Registry<T>> registryKey;
        private final Codec<T> codec;
        private final Map<ResourceKey<T>, T> entries = new LinkedHashMap<>();

        private RegistryEntries(ResourceKey<? extends Registry<T>> registryKey, Codec<T> codec) {
            this.registryKey = registryKey;
            this.codec = codec;
        }

        private void add(ResourceKey<T> key, T value) {
            if (entries.put(key, value) != null) {
                throw new IllegalArgumentException("Trying to add registry key " + key + " more than once.");
            }
        }

        private HolderLookup.RegistryLookup<T> createLookup(HolderLookup.Provider parent) {
            var parentLookup = parent.lookup(registryKey);

            return new HolderLookup.RegistryLookup<>() {
                @Override
                public ResourceKey<? extends Registry<? extends T>> key() {
                    return registryKey;
                }

                @Override
                public Lifecycle registryLifecycle() {
                    return parentLookup.map(HolderLookup.RegistryLookup::registryLifecycle).orElse(Lifecycle.stable());
                }

                @Override
                public Optional<Holder.Reference<T>> get(ResourceKey<T> key) {
                    return Optional.of(Holder.Reference.createStandAlone(this, key));
                }

                @Override
                public Optional<HolderSet.Named<T>> get(TagKey<T> tagKey) {
                    return parentLookup.flatMap(lookup -> lookup.get(tagKey)).map(ignored -> HolderSet.emptyNamed(this, tagKey));
                }

                @Override
                public Stream<Holder.Reference<T>> listElements() {
                    Stream<Holder.Reference<T>> generated = entries.keySet().stream().map(key -> Holder.Reference.createStandAlone(this, key));
                    Stream<Holder.Reference<T>> parent = parentLookup
                            .map(lookup -> lookup.listElementIds().map(key -> Holder.Reference.createStandAlone(this, key)))
                            .orElseGet(Stream::empty);
                    return Stream.concat(parent, generated);
                }

                @Override
                public Stream<HolderSet.Named<T>> listTags() {
                    return parentLookup
                            .map(lookup -> lookup.listTagIds().map(tagKey -> HolderSet.emptyNamed(this, tagKey)))
                            .orElseGet(Stream::empty);
                }
            };
        }

        private Stream<CompletableFuture<?>> write(CachedOutput cachedOutput, HolderLookup.Provider provider) {
            var pathProvider = output.createRegistryElementsPathProvider(registryKey);

            return entries.entrySet().stream().map(entry -> {
                Path path = pathProvider.json(entry.getKey().location());
                return DataProvider.saveStable(cachedOutput, provider, codec, entry.getValue(), path);
            });
        }
    }
}
