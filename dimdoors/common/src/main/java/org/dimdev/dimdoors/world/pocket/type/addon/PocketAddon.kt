package org.dimdev.dimdoors.world.pocket.type.addon

import com.mojang.serialization.Codec
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import org.dimdev.dimcore.api.BuilderType
import org.dimdev.dimcore.api.BuilderTypeHasHolder
import org.dimdev.dimdoors.api.util.mutableList
import org.dimdev.dimdoors.world.pocket.type.Pocket

typealias PocketAddonType<T, V> = BuilderType<T, PocketAddon.PocketBuilderAddon<T, V>>

interface PocketAddon : BuilderTypeHasHolder<PocketAddon, PocketAddon.PocketBuilderAddon<*, *>> {
    fun applicable(pocket: Pocket<*, *>): Boolean = true

    fun addAddon(addons: MutableMap<PocketAddonType<*, *>, PocketAddon>) {
        addons[this.type] = this
    }

    interface PocketBuilderExtension<T : Pocket<T, P>, P : Pocket.PocketBuilder<T, P>> {
        fun <C : PocketBuilderAddon<*, *>> getAddon(id: PocketAddonType<*, *>): C?

        val self: P
    }

    interface PocketBuilderAddon<T : PocketAddon, U : PocketBuilderAddon<T, U>> : BuilderTypeHasHolder<PocketAddon, PocketBuilderAddon<*, *>> {
        fun applicable(builder: Pocket.PocketBuilder<*, *>): Boolean {
            return true
        }

        fun addAddon(addons: MutableMap<PocketAddonType<*, *>, PocketBuilderAddon<*, *>>) {
            addons[this.type] = this
        }

        fun apply(pocket: Pocket<*, *>) : T
    }

    companion object {
        val CODEC: Codec<PocketAddon> = PocketAddons.codec
        val LIST_CODEC: Codec<MutableList<PocketAddon>> = CODEC.mutableList()
        val BUILDER_CODEC: Codec<PocketBuilderAddon<*, *>> = PocketAddons.builderCodec
        val LIST_BUILDER_CODEC: Codec<MutableList<PocketBuilderAddon<*, *>>> = BUILDER_CODEC.mutableList()
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, PocketAddon> = PocketAddons.streamCodec.dispatch({ it.type }, { it.streamCodec!! })
        val LIST_STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf?, List<PocketAddon>> = STREAM_CODEC.apply(ByteBufCodecs.list())
    }
}
