package org.dimdev.dimdoors.pockets.virtual

import com.mojang.datafixers.Products.P1
import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.resources.RegistryFileCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.api.util.Weighted
import org.dimdev.dimdoors.pockets.PocketCreator
import org.dimdev.dimdoors.pockets.PocketGenerationContext
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference
import org.dimdev.dimdoors.world.ModDimensions

interface VirtualPocket : Weighted<PocketGenerationContext>, PocketCreator {
    fun getNextPocketGeneratorReference(parameters: PocketGenerationContext): PocketGeneratorReference<*>

    fun peekNextPocketGeneratorReference(parameters: PocketGenerationContext): PocketGeneratorReference<*>

    companion object {
        @JvmField
        val CODEC: Codec<VirtualPocket> = Codec.lazyInitialized {
            Codec.either(ImplementedVirtualPocket.CODEC, VirtualPocketList.CODEC).xmap<VirtualPocket>(
                { either -> either.map({ it }, { it }) },
                { virtualPocket ->
                    when (virtualPocket) {
                        is VirtualPocketList -> Either.right(virtualPocket)
                        is ImplementedVirtualPocket<*> -> Either.left(virtualPocket)
                        else -> throw IllegalStateException("Unknown virtual pocket type.")
                    }
                }
            )
        }

        @JvmField
        val HOLDER_CODEC: Codec<Holder<VirtualPocket>> = RegistryFileCodec.create(ModRegistryKeys.VIRTUAL_POCKET, CODEC)

        fun <T : VirtualPocket> commonFields(instance: RecordCodecBuilder.Instance<T>): P1<RecordCodecBuilder.Mu<T>, ResourceKey<Level>> = instance.group(ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("level", ModDimensions.DUNGEON).forGetter { it.level })
    }
}