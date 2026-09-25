package org.dimdev.dimdoors.world.pocket.type.addon

import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.sounds.Music
import org.dimdev.dimdoors.util.CodecUtils
import org.dimdev.dimdoors.util.StreamCodecUtils
import org.dimdev.dimdoors.world.pocket.type.Pocket

@JvmRecord
data class MusicAddon(val music: Music?) : PocketAddon {
    override val type get() = PocketAddons.MUSIC_ADDON

    data class MusicAddonBuilder(val music: Music?) : PocketAddon.PocketBuilderAddon<MusicAddon, MusicAddonBuilder> {
        override fun apply(pocket: Pocket<*, *>) {
            pocket.addAddon(MusicAddon(music))
        }

        override val type get() = PocketAddons.MUSIC_ADDON

        companion object {
            val CODEC = RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    CodecUtils.GAME_MUSIC.fieldOf("music").forGetter(MusicAddonBuilder::music)
                ).apply(instance, ::MusicAddonBuilder)
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
