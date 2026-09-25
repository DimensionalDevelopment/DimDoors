package org.dimdev.dimdoors.pockets.modifier

import com.mojang.serialization.Codec
import net.minecraft.core.RegistryCodecs
import net.minecraft.resources.RegistryFileCodec
import org.dimdev.dimcore.api.MapCodecHasHolder
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.pockets.PocketGenerationContext
import org.dimdev.dimdoors.world.pocket.type.Pocket

interface Modifier : MapCodecHasHolder<Modifier> {

    fun apply(parameters: PocketGenerationContext, manager: RiftManager)

    fun apply(parameters: PocketGenerationContext, builder: Pocket.PocketBuilder<*, *>)

    companion object {
        val BASE_CODEC: Codec<Modifier> = Modifiers.codec
        val LIST_CODEC = RegistryCodecs.homogeneousList(ModRegistryKeys.MODIFIER, BASE_CODEC)
        val HOLDER_CODEC = RegistryFileCodec.create(ModRegistryKeys.MODIFIER, BASE_CODEC)
    }
}
