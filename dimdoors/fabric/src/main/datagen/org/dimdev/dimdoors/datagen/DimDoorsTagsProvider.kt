package org.dimdev.dimdoors.datagen

import net.minecraft.Util
import net.minecraft.core.HolderLookup
import net.minecraft.core.Registry
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagBuilder
import net.minecraft.tags.TagFile
import net.minecraft.tags.TagKey
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.function.Function

abstract class DimDoorsTagsProvider<T> protected constructor(
    output: PackOutput,
    protected val registryKey: ResourceKey<out Registry<T>>,
    private val lookupProvider: CompletableFuture<HolderLookup.Provider>,
    private val parentProvider: CompletableFuture<TagLookup<T>> = CompletableFuture.completedFuture(TagLookup.empty())
) : DataProvider {
    protected val pathProvider: PackOutput.PathProvider = output.createRegistryTagsPathProvider(registryKey)
    private val contentsDone = CompletableFuture<Void?>()
    private val builders: MutableMap<ResourceLocation, TagBuilder> = linkedMapOf()

    override fun getName(): String = "Tags for " + registryKey.location()

    protected abstract fun addTags(provider: HolderLookup.Provider)

    override fun run(output: CachedOutput): CompletableFuture<*> {
        return createContentsProvider().thenApply { provider ->
            contentsDone.complete(null)
            provider
        }.thenCombineAsync(parentProvider, { provider, tagLookup -> CombinedData(provider, tagLookup) }, Util.backgroundExecutor()).thenCompose { arg ->
            CompletableFuture.allOf(*builders.entries.map { (resourceLocation, tagBuilder) ->
                val list = tagBuilder.build()

                val path = pathProvider.json(resourceLocation)
                DataProvider.saveStable(output, arg.contents, TagFile.CODEC, TagFile(list, false), path)
            }.toTypedArray())
        }
    }

    protected fun tag(tag: TagKey<T>): TagAppender<T> = TagAppender(getOrCreateRawBuilder(tag))

    protected fun getOrCreateRawBuilder(tag: TagKey<T>): TagBuilder = builders.computeIfAbsent(tag.location()) { TagBuilder.create() }

    fun contentsGetter(): CompletableFuture<TagLookup<T>> = contentsDone.thenApply { TagLookup { tagKey -> Optional.ofNullable(builders[tagKey.location()]) } }

    protected fun createContentsProvider(): CompletableFuture<HolderLookup.Provider> = lookupProvider.thenApply { provider ->
        builders.clear()
        addTags(provider)
        provider
    }

    private data class CombinedData<T>(val contents: HolderLookup.Provider, val parent: TagLookup<T>)

    fun interface TagLookup<T> : Function<TagKey<T>, Optional<TagBuilder>> {
        fun contains(tagKey: TagKey<T>): Boolean = apply(tagKey).isPresent

        companion object {
            fun <T> empty(): TagLookup<T> = TagLookup { Optional.empty() }
        }
    }

    open class TagAppender<T>(private val builder: TagBuilder) {
        fun add(key: ResourceKey<T>): TagAppender<T> {
            builder.addElement(key.location())
            return this
        }

        fun add(vararg keys: ResourceKey<T>): TagAppender<T> {
            for (resourceKey in keys) {
                builder.addElement(resourceKey.location())
            }

            return this
        }

        fun addAll(keys: List<ResourceKey<T>>): TagAppender<T> {
            for (resourceKey in keys) {
                builder.addElement(resourceKey.location())
            }

            return this
        }

        open fun addOptional(location: ResourceLocation): TagAppender<T> {
            builder.addOptionalElement(location)
            return this
        }

        open fun addTag(tag: TagKey<T>): TagAppender<T> {
            builder.addTag(tag.location())
            return this
        }

        open fun addOptionalTag(location: ResourceLocation): TagAppender<T> {
            builder.addOptionalTag(location)
            return this
        }
    }
}
