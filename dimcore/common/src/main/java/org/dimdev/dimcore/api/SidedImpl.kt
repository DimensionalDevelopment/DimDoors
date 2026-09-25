package org.dimdev.dimcore.api

import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation

abstract class SidedImpl<V : SidedImpl<V, T>, T : ModCommon<in V>>(@JvmField protected val common: T) : ISided<V> {
    override fun modId(): String {
        return common.modId
    }

    override fun <T : Any, V : T> register(key: ResourceKey<Registry<T>>, id: String, obj: V): V = register(key, ResourceLocation.fromNamespaceAndPath(common.modId, id), obj)

    override fun <R: Any, E : R> registerHolder(key: ResourceKey<Registry<R>>, id: String, obj: E): Holder<R> = registerHolder(key, ResourceLocation.fromNamespaceAndPath(common.modId, id), obj)
}
