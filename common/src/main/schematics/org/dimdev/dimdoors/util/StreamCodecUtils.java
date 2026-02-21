package org.dimdev.dimdoors.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.world.pocket.type.addon.cloud.OverworldCloudData;

import java.util.function.Function;

public class StreamCodecUtils {
    public static StreamCodec<ByteBuf, Vec3> VEC3 = StreamCodec.composite(ByteBufCodecs.DOUBLE, Vec3::x, ByteBufCodecs.DOUBLE, Vec3::y, ByteBufCodecs.DOUBLE, Vec3::z, Vec3::new);
}
