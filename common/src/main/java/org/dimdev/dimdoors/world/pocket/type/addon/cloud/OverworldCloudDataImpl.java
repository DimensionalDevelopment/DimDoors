package org.dimdev.dimdoors.world.pocket.type.addon.cloud;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.util.StreamCodecUtils;

public record OverworldCloudDataImpl(float height, Vec3 color) implements OverworldCloudData {
    public static final MapCodec<OverworldCloudData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("height", 128f).forGetter(OverworldCloudData::getCloudHeight),
                    Vec3.CODEC.optionalFieldOf("color", new Vec3(1,1,1)).forGetter(OverworldCloudData::getCloudColor))
            .apply(instance, OverworldCloudDataImpl::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, OverworldCloudData> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT, OverworldCloudData::getCloudHeight, StreamCodecUtils.VEC3, OverworldCloudData::getCloudColor, OverworldCloudDataImpl::new);

    @Override
    public float getCloudHeight() {
        return height;
    }

    @Override
    public Vec3 getCloudColor() {
        return color;
    }
}
