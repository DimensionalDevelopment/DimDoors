package org.dimdev.dimdoors.util;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class StreamCodecUtils {
    public static final StreamCodec<RegistryFriendlyByteBuf, Music> MUSIC = StreamCodec.composite(
            SoundEvent.STREAM_CODEC, Music::getEvent,
            ByteBufCodecs.VAR_INT, Music::getMinDelay,
            ByteBufCodecs.VAR_INT, Music::getMinDelay,
            ByteBufCodecs.BOOL, Music::replaceCurrentMusic,
            Music::new
    );

    public static final StreamCodec<ByteBuf, Vec3> VEC3 = StreamCodec.composite(ByteBufCodecs.DOUBLE, Vec3::x, ByteBufCodecs.DOUBLE, Vec3::y, ByteBufCodecs.DOUBLE, Vec3::z, Vec3::new);
    public static final StreamCodec<ByteBuf, BoundingBox> BOUNDING_BOX = StreamCodec.composite(
            ByteBufCodecs.INT, BoundingBox::minX,
            ByteBufCodecs.INT, BoundingBox::minY,
            ByteBufCodecs.INT, BoundingBox::minZ,
            ByteBufCodecs.INT, BoundingBox::maxX,
            ByteBufCodecs.INT, BoundingBox::maxY,
            ByteBufCodecs.INT, BoundingBox::maxZ,
            BoundingBox::new
    );

    public static StreamCodec<FriendlyByteBuf, int[]> intArray(int length) {
        return new StreamCodec<>() {
            public int @NotNull [] decode(@NotNull FriendlyByteBuf buffer) {
                return buffer.readVarIntArray(length);
            }

            public void encode(@NotNull FriendlyByteBuf buffer, int @NotNull [] value) {
                if(value.length > length) {
                    throw new EncoderException("Array with size " + value.length + " is bigger than allowed " + length);
                } else {
                    buffer.writeVarIntArray(value);
                }
            }
        };
    }

    StreamCodec<FriendlyByteBuf, int[]> INT_ARRAY = new StreamCodec<>() {
        public int @NotNull [] decode(FriendlyByteBuf buffer) {
            return buffer.readVarIntArray();
        }

        public void encode(FriendlyByteBuf buffer, int @NotNull [] value) {
            buffer.writeVarIntArray(value);
        }
    };
}
