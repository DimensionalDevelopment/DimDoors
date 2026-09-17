package org.dimdev.dimdoors.world.pocket.type.addon;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvent;
import org.dimdev.dimdoors.util.CodecUtils;
import org.dimdev.dimdoors.util.StreamCodecUtils;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public record MusicAddon(Music music) implements PocketAddon {
    public static final MapCodec<MusicAddon> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(CodecUtils.GAME_MUSIC.fieldOf("music").forGetter(MusicAddon::music)).apply(instance, MusicAddon::new)
        );

    public static final StreamCodec<RegistryFriendlyByteBuf, MusicAddon> STREAM_CODEC = StreamCodecUtils.MUSIC.map(MusicAddon::new, MusicAddon::music);

    @Override
    public PocketAddonType<?, ?> getType() {
        return PocketAddonType.MUSIC_ADDON;
    }

    public record MusicAddonBuilder(Music music) implements PocketBuilderAddon<MusicAddon, MusicAddonBuilder> {
        public static final MapCodec<MusicAddonBuilder> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(CodecUtils.GAME_MUSIC.fieldOf("music").forGetter(MusicAddonBuilder::music)).apply(instance, MusicAddonBuilder::new)
        );

        @Override
        public void apply(Pocket pocket) {
            pocket.addAddon(new MusicAddon(music));
        }

        @Override
        public PocketAddonType<MusicAddon, MusicAddonBuilder> getType() {
            return PocketAddonType.MUSIC_ADDON;
        }
    }
}
