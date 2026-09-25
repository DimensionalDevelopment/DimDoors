package org.dimdev.dimdoors.world.pocket.type.addon

import com.mojang.serialization.Codec
import net.minecraft.core.Holder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import org.dimdev.dimcore.api.BuilderType
import org.dimdev.dimcore.api.BuilderTypeHasHolder
import org.dimdev.dimdoors.util.CodecUtils.imutableList
import org.dimdev.dimdoors.util.CodecUtils.mutableList
import org.dimdev.dimdoors.world.pocket.type.Pocket

typealias PocketAddonType = BuilderType<PocketAddon, PocketAddon.PocketBuilderAddon<*, *>>

interface PocketAddon : BuilderTypeHasHolder<PocketAddon, PocketAddon.PocketBuilderAddon<*, *>> {
    fun applicable(pocket: Pocket<*, *>): Boolean = true

    fun addAddon(addons: MutableMap<Holder<out PocketAddonType>, PocketAddon>) {
        addons[this.type] = this
    }

    interface PocketBuilderExtension<T : Pocket<T, P>, P : Pocket.PocketBuilder<T, P>> {
        fun <C : PocketBuilderAddon<*, *>> getAddon(id: Holder<out PocketAddonType>): C?

        val self: P
    }

    interface PocketBuilderAddon<T : PocketAddon, U : PocketBuilderAddon<T, U>> : BuilderTypeHasHolder<PocketAddon, PocketBuilderAddon<*, *>> {
        fun applicable(builder: Pocket.PocketBuilder<*, *>): Boolean {
            return true
        }

        // makes it possible for addons themselves to control how they are added
        fun addAddon(addons: MutableMap<Holder<out PocketAddonType>, PocketBuilderAddon<*, *>>) {
            addons[this.type] = this
        }

        fun apply(pocket: Pocket<*, *>)
    }

    companion object {
        val CODEC: Codec<PocketAddon> = PocketAddons.codec
        val LIST_CODEC = CODEC.mutableList()
        val BUILDER_CODEC = PocketAddons.builderCodec
        val LIST_BUILDER_CODEC = BUILDER_CODEC.mutableList()
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, PocketAddon> = PocketAddons.streamCodec.dispatch({ it.type.value() }, { it.streamCodec!! })
        val LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list())
    }
}
