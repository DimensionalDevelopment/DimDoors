package org.dimdev.dimcore.api

import com.mojang.serialization.Codec
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import org.dimdev.dimcore.DimCore.platform
import java.nio.file.Path

interface ISided<T : ISided<T>> : ICreativeTabHandler {
    fun self(): T = this.cast()

    fun modId(): String

    fun <S> createDynamicRegistry(key: ResourceKey<Registry<S>>, codec: Codec<S>, networkCodec: Codec<S>?)

    fun <S> createDynamicRegistry(key: ResourceKey<Registry<S>>, codec: Codec<S>, synced: Boolean) = createDynamicRegistry(key, codec, if (synced) codec else null)

    fun <S> createDynamicRegistry(key: ResourceKey<Registry<S>>, codec: Codec<S>) = createDynamicRegistry(key, codec, null)

    fun configPath(): Path = platform.configRoot.resolve(modId())

    fun <T> entryRegister(resourceKey: ResourceKey<Registry<T>>, registry: Registry<T>? = null): PlatformRegistry.EntryRegister<T>
}

fun <T : Any> Any.cast(): T = this as T

infix fun <T : Any> Any.cast(clazz: Class<T>): T? = this.takeIf(clazz::isInstance)?.let(clazz::cast)