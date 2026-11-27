package org.dimdev.dimcore.util

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.mojang.datafixers.util.Either
import com.mojang.logging.LogUtils
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.mojang.serialization.Lifecycle
import net.minecraft.core.*
import net.minecraft.core.registries.Registries
import net.minecraft.resources.FileToIdConverter
import net.minecraft.resources.RegistryOps
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.tags.TagKey
import net.minecraft.tags.TagLoader
import org.dimdev.dimcore.api.ext.cast
import org.dimdev.dimcore.api.ext.castOrNull
import java.util.*
import java.util.stream.Stream

object DatapackLoader {
    private val LOGGER = LogUtils.getLogger()
    private val stores = linkedMapOf<ResourceLocation, Store<out Any>>()

    fun <T : Any> register(key: ResourceKey<Registry<T>>, codec: Codec<T>) {
        stores[key.location()] = Store(key, codec)
    }

    fun <T : Any> get(key: ResourceKey<T>): T? = store<T>(key.registry()).holders[key]?.takeIf { it.isBound }?.value()

    fun <T : Any> tag(tag: TagKey<T>): HolderSet<T>? = store<T>(tag.registry().location()).tags[tag]

    fun <T : Any> holderCodec(key: ResourceKey<Registry<T>>): Codec<Holder<T>> = Codec.either(ResourceLocation.CODEC, Codec.lazyInitialized { store<T>(key.location()).codec }).xmap(
        { either -> either.map<Holder<T>>({ store<T>(key.location()).reference(ResourceKey.create(key, it)) }, { Holder.direct(it) }) },
        { holder -> holder.unwrap().mapLeft(ResourceKey<T>::location) }
    )

    fun <T : Any> holderSetCodec(key: ResourceKey<Registry<T>>): Codec<HolderSet<T>> = Codec.either(TagKey.hashedCodec(key), Codec.either(holderCodec(key).listOf(), holderCodec(key))).xmap(
        { either -> either.map<HolderSet<T>>({ store<T>(key.location()).named(it) }, { holders -> holders.map({ HolderSet.direct(it) }, { HolderSet.direct(listOf(it)) }) }) },
        { set -> set.unwrap().mapRight { Either.left<List<Holder<T>>, Holder<T>>(it) } }
    )

    fun reload(provider: HolderLookup.Provider, manager: ResourceManager) {
        RegistryOps.create(JsonOps.INSTANCE, provider).also { ops ->
            stores.values.forEach(Store<*>::clear)
            stores.values.forEach { it.loadEntries(manager, ops) }
            stores.values.forEach { it.loadTags(manager) }
        }
    }

    private fun <T : Any> store(registry: ResourceLocation): Store<T> = stores.getValue(registry).cast()

    fun <T: Any> keySet(resourceKey: ResourceKey<Registry<T>>): Set<ResourceKey<T>> = lookup(resourceKey)?.listElementIds()?.toList()?.toSet() ?: emptySet()
    fun <T: Any> lookupOrThrow(key: ResourceKey<Registry<T>>): HolderLookup.RegistryLookup<T> = stores[key.location()]?.castOrNull<Store<T>>() ?: throw IllegalStateException("Registry ${key.location()} not found")

    fun <T : Any> lookup(key: ResourceKey<out Registry<T>>): HolderLookup.RegistryLookup<T>? = if (key.location() in stores) store(key.location()) else null

    private class Store<T : Any>(val key: ResourceKey<Registry<T>>, val codec: Codec<T>) : HolderOwner<T>, HolderLookup.RegistryLookup<T> {
        var holders = mutableMapOf<ResourceKey<T>, Holder.Reference<T>>()
        var tags = mutableMapOf<TagKey<T>, HolderSet.Named<T>>()

        fun reference(id: ResourceKey<T>): Holder.Reference<T> = holders.getOrPut(id) { Holder.Reference.createStandAlone(this, id) }

        fun named(tag: TagKey<T>): HolderSet.Named<T> = tags.getOrPut(tag) { HolderSet.Named(this, tag) }

        fun clear() {
            holders = mutableMapOf()
            tags = mutableMapOf()
        }

        fun loadEntries(manager: ResourceManager, ops: RegistryOps<JsonElement>) {
            val converter = FileToIdConverter.json(Registries.elementsDirPath(key))
            converter.listMatchingResources(manager).forEach { (file, resource) ->
                val id = converter.fileToId(file)
                resource.openAsReader().use { reader ->
                    codec.parse(ops, JsonParser.parseReader(reader))
                        .ifSuccess { reference(ResourceKey.create(key, id)).bindValue(it) }
                        .ifError { LOGGER.error("Couldn't load {} entry {} from {}: {}", key.location(), id, resource.sourcePackId(), it.message()) }
                }
            }
        }

        fun loadTags(manager: ResourceManager) {
            holders.values.filterNot { it.isBound }.forEach { LOGGER.error("Missing {} entry {}", key.location(), it.key().location()) }

            val loaded = TagLoader<Holder<T>>({ id -> Optional.ofNullable(holders[ResourceKey.create(key, id)]?.takeIf { it.isBound }) }, Registries.tagsDirPath(key)).loadAndBuild(manager)
            loaded.forEach { (id, contents) -> named(TagKey.create(key, id)).bind(contents.toList()) }

            val byHolder = loaded.flatMap { (id, contents) -> contents.map { it to TagKey.create(key, id) } }.groupBy({ it.first }, { it.second })
            holders.values.forEach { it.bindTags(byHolder[it].orEmpty()) }
        }

        override fun key(): ResourceKey<out Registry<out T>> = key
        override fun registryLifecycle(): Lifecycle = Lifecycle.stable()
        override fun listElements(): Stream<Holder.Reference<T>> = holders.values.stream().filter { it.isBound }
        override fun listTags(): Stream<HolderSet.Named<T>> = tags.values.stream()
        override fun get(resourceKey: ResourceKey<T>): Optional<Holder.Reference<T>> = holders[resourceKey]?.takeIf { it.isBound }.let(Optional<Holder.Reference<T>>::ofNullable)
        override fun get(tagKey: TagKey<T>): Optional<HolderSet.Named<T>> = tags[tagKey].let(Optional<Holder.Reference<T>>::ofNullable)
    }
}
