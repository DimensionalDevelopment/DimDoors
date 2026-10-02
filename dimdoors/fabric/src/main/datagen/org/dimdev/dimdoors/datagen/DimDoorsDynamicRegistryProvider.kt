package org.dimdev.dimdoors.datagen

import com.mojang.serialization.Codec
import com.mojang.serialization.Lifecycle
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.event.registry.DynamicRegistries
import net.minecraft.Util
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.HolderSet
import net.minecraft.core.Registry
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.resources.RegistryDataLoader
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.stream.Stream

abstract class DimDoorsDynamicRegistryProvider(private val output: FabricDataOutput, private val registriesFuture: CompletableFuture<HolderLookup.Provider>) : DataProvider {
    override fun run(cachedOutput: CachedOutput): CompletableFuture<*> {
        return registriesFuture.thenCompose { registries ->
            CompletableFuture.supplyAsync({
                val entries = Entries(registries)
                val provider = entries.createProvider(registries)

                configure(RegistrationHelper(provider, entries))

                ConfiguredEntries(entries, provider)
            }, Util.backgroundExecutor()).thenCompose { configured ->
                CompletableFuture.allOf(*configured.entries.registries.values.flatMap { it.write(cachedOutput, configured.provider) }.toTypedArray())
            }
        }
    }

    protected abstract fun configure(helper: RegistrationHelper)

    data class RegistrationHelper(val registries: HolderLookup.Provider, val entries: Entries) {
        fun <T> register(key: ResourceKey<T>, value: T): Holder<T> {
            entries.add(key, value)
            return lookup(key)
        }

        fun <T> lookup(key: ResourceKey<T>): Holder<T> = registries.lookupOrThrow(key.registryKey()).getOrThrow(key)

        fun <T> registrylookup(key: ResourceKey<Registry<T>>): HolderLookup.RegistryLookup<T> = registries.lookupOrThrow(key)
    }

    private data class ConfiguredEntries(val entries: Entries, val provider: HolderLookup.Provider)

    inner class Entries internal constructor(registries: HolderLookup.Provider) {
        internal val registries: MutableMap<ResourceKey<out Registry<*>>, RegistryEntries<*>> = linkedMapOf()

        init {
            DynamicRegistries.getDynamicRegistries()
                .filter { data -> registries.lookup(data.key()).isPresent }
                .forEach { addRegistry(it) }
        }

        private fun <T> addRegistry(data: RegistryDataLoader.RegistryData<T>) {
            registries[data.key()] = RegistryEntries(data.key(), data.elementCodec())
        }

        fun <T> add(key: ResourceKey<T>, value: T) {
            registry(key).add(key, value)
        }

        @Suppress("UNCHECKED_CAST")
        private fun <T> registry(key: ResourceKey<T>): RegistryEntries<T> =
            registries[key.registryKey()] as RegistryEntries<T>? ?: throw IllegalArgumentException("Registry " + key.registry() + " is not loaded from datapacks")

        internal fun createProvider(parent: HolderLookup.Provider): HolderLookup.Provider {
            val overlays: MutableMap<ResourceKey<out Registry<*>>, HolderLookup.RegistryLookup<*>> = linkedMapOf()
            registries.forEach { (key, entries) -> overlays[key] = entries.createLookup(parent) }
            val parentLookups: MutableMap<ResourceKey<out Registry<*>>, Optional<HolderLookup.RegistryLookup<*>>> = linkedMapOf()

            return object : HolderLookup.Provider {
                override fun listRegistries(): Stream<ResourceKey<out Registry<*>>> = Stream.concat(parent.listRegistries(), overlays.keys.stream()).distinct()

                @Suppress("UNCHECKED_CAST")
                override fun <T> lookup(key: ResourceKey<out Registry<out T>>): Optional<HolderLookup.RegistryLookup<T>> {
                    val overlay = overlays[key] as HolderLookup.RegistryLookup<T>?

                    if (overlay != null) {
                        return Optional.of(overlay)
                    }

                    return parentLookups.computeIfAbsent(key as ResourceKey<out Registry<*>>) { wrapParentLookup<T>(parent, key) as Optional<HolderLookup.RegistryLookup<*>> } as Optional<HolderLookup.RegistryLookup<T>>
                }
            }
        }

        private fun <T> wrapParentLookup(parent: HolderLookup.Provider, key: ResourceKey<out Registry<out T>>): Optional<HolderLookup.RegistryLookup<T>> {
            return parent.lookup(key).map { parentLookup ->
                object : HolderLookup.RegistryLookup<T> {
                    override fun key(): ResourceKey<out Registry<out T>> = parentLookup.key()

                    override fun registryLifecycle(): Lifecycle = parentLookup.registryLifecycle()

                    override fun get(elementKey: ResourceKey<T>): Optional<Holder.Reference<T>> = parentLookup.get(elementKey).map { Holder.Reference.createStandAlone(this, elementKey) }

                    override fun get(tagKey: TagKey<T>): Optional<HolderSet.Named<T>> = parentLookup.get(tagKey).map { HolderSet.emptyNamed(this, tagKey) }

                    override fun listElements(): Stream<Holder.Reference<T>> = parentLookup.listElementIds().map { elementKey -> Holder.Reference.createStandAlone(this, elementKey) }

                    override fun listTags(): Stream<HolderSet.Named<T>> = parentLookup.listTagIds().map { tagKey -> HolderSet.emptyNamed(this, tagKey) }
                }
            }
        }
    }

    internal inner class RegistryEntries<T>(private val registryKey: ResourceKey<out Registry<T>>, private val codec: Codec<T>) {
        private val entries: MutableMap<ResourceKey<T>, T> = linkedMapOf()

        fun add(key: ResourceKey<T>, value: T) {
            if (entries.put(key, value) != null) {
                throw IllegalArgumentException("Trying to add registry key $key more than once.")
            }
        }

        fun createLookup(parent: HolderLookup.Provider): HolderLookup.RegistryLookup<T> {
            val parentLookup = parent.lookup(registryKey)

            return object : HolderLookup.RegistryLookup<T> {
                override fun key(): ResourceKey<out Registry<out T>> = registryKey

                override fun registryLifecycle(): Lifecycle = parentLookup.map { it.registryLifecycle() }.orElse(Lifecycle.stable())

                override fun get(key: ResourceKey<T>): Optional<Holder.Reference<T>> = Optional.of(Holder.Reference.createStandAlone(this, key))

                override fun get(tagKey: TagKey<T>): Optional<HolderSet.Named<T>> = parentLookup.flatMap { it.get(tagKey) }.map { HolderSet.emptyNamed(this, tagKey) }

                override fun listElements(): Stream<Holder.Reference<T>> {
                    val generated = entries.keys.stream().map { key -> Holder.Reference.createStandAlone(this, key) }
                    val parent = parentLookup
                        .map { lookup -> lookup.listElementIds().map { key -> Holder.Reference.createStandAlone(this, key) } }
                        .orElseGet { Stream.empty() }
                    return Stream.concat(parent, generated)
                }

                override fun listTags(): Stream<HolderSet.Named<T>> = parentLookup
                    .map { lookup -> lookup.listTagIds().map { tagKey -> HolderSet.emptyNamed(this, tagKey) } }
                    .orElseGet { Stream.empty() }
            }
        }

        fun write(cachedOutput: CachedOutput, provider: HolderLookup.Provider): List<CompletableFuture<*>> {
            val pathProvider = output.createRegistryElementsPathProvider(registryKey)

            return entries.map { (key, value) ->
                val path = pathProvider.json(key.location())
                DataProvider.saveStable(cachedOutput, provider, codec, value, path)
            }
        }
    }
}
