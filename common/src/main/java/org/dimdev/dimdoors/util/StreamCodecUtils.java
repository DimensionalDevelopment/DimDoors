package org.dimdev.dimdoors.util;

import com.google.common.io.Files;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.world.pocket.type.addon.cloud.OverworldCloudData;

import java.util.function.Function;

public class StreamCodecUtils {
    public static final StreamCodec<RegistryFriendlyByteBuf, Music> MUSIC = StreamCodec.composite(
            SoundEvent.STREAM_CODEC, a -> a.getEvent(),
            ByteBufCodecs.VAR_INT, a -> a.getMinDelay(),
            ByteBufCodecs.VAR_INT, a -> a.getMinDelay(),
            ByteBufCodecs.BOOL, a -> a.replaceCurrentMusic(),
            Music::new
    );

    public static final StreamCodec<ByteBuf, Vec3> VEC3 = StreamCodec.composite(ByteBufCodecs.DOUBLE, Vec3::x, ByteBufCodecs.DOUBLE, Vec3::y, ByteBufCodecs.DOUBLE, Vec3::z, Vec3::new);
}
