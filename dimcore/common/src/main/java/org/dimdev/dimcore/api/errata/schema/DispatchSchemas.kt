package org.dimdev.dimcore.api.errata.schema

import com.mojang.serialization.MapCodec
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation

typealias DispatchTables = Map<ResourceLocation, Map<ResourceLocation, StructNode>>

object DispatchSchemas {
    private val providers = mutableMapOf<ResourceLocation, () -> Map<ResourceLocation, StructNode>>()
    private val cache = mutableMapOf<ResourceLocation, Map<ResourceLocation, StructNode>>()

    fun register(id: ResourceLocation, variants: () -> Map<ResourceLocation, StructNode>) {
        providers[id] = variants
        cache.remove(id)
    }

    fun <T, B> register(registry: Registry<T>, variant: (T) -> MapCodec<out B>) = register(registry.key().location()) {
        registry.entrySet().associate { (key, value) -> key.location() to variant(value).schema(key.location()) }
    }

    fun variants(id: ResourceLocation): Map<ResourceLocation, StructNode>? = cache[id] ?: providers[id]?.invoke()?.also { cache[id] = it }
}
