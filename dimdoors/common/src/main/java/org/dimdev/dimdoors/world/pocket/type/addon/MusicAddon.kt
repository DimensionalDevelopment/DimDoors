package org.dimdev.dimdoors.world.pocket.type.addon

import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.sounds.Music
import org.dimdev.dimdoors.api.util.nullableForGetter
import org.dimdev.dimdoors.util.CodecUtils
import org.dimdev.dimdoors.util.StreamCodecUtils
import org.dimdev.dimdoors.world.pocket.type.Pocket
import kotlin.jvm.optionals.getOrNull

data class MusicAddon(var music: Music? = null) : PocketAddon {
    override val type get() = PocketAddons.MUSIC_ADDON

    data class MusicAddonBuilder(val music: Music? = null) : PocketAddon.PocketBuilderAddon<MusicAddon, MusicAddonBuilder> {
        override fun apply(pocket: Pocket<*, *>): MusicAddon {
            val addon = MusicAddon(music)
            pocket.addAddon(addon)
            return addon
        }

        override val type get() = PocketAddons.MUSIC_ADDON

        companion object {
            val CODEC = RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    CodecUtils.GAME_MUSIC.optionalFieldOf("music").nullableForGetter(MusicAddonBuilder::music)
                ).apply(instance) { MusicAddonBuilder(it.getOrNull()) }
            }
        }
    }

    companion object {
        val CODEC = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                CodecUtils.GAME_MUSIC.fieldOf("music").forGetter(MusicAddon::music)
            ).apply(instance, ::MusicAddon)
        }

        val STREAM_CODEC = StreamCodecUtils.MUSIC.map(::MusicAddon, MusicAddon::music)
    }
}
