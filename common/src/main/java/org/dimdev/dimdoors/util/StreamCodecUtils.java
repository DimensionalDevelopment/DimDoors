package org.dimdev.dimdoors.util;

import com.google.common.io.Files;
import com.mojang.datafixers.util.Function6;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.network.packet.s2c.SyncPocketAddonsS2CPacket;
import org.dimdev.dimdoors.world.pocket.type.addon.cloud.OverworldCloudData;

import java.util.function.Function;

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
}
