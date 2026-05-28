package org.dimdev.dimdoors.api.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class RotatedLocation extends Location {
    public static final Codec<RotatedLocation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Level.RESOURCE_KEY_CODEC.fieldOf("world").forGetter(location -> location.world),
            BlockPos.CODEC.fieldOf("pos").forGetter(location -> location.pos),
            Codec.FLOAT.fieldOf("yaw").forGetter(location -> location.yaw),
            Codec.FLOAT.fieldOf("pitch").forGetter(location -> location.pitch)
    ).apply(instance, RotatedLocation::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, RotatedLocation> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION), RotatedLocation::getWorldId,
            BlockPos.STREAM_CODEC, RotatedLocation::getBlockPos,
            ByteBufCodecs.FLOAT, a -> a.yaw,
            ByteBufCodecs.FLOAT, a -> a.pitch,
            RotatedLocation::new);

    public final float yaw;
    public final float pitch;

    public RotatedLocation(ResourceKey<Level> world, BlockPos pos, float yaw, float pitch) {
        super(world, pos);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public static CompoundTag serialize(RotatedLocation location) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("world", location.world.location().toString());
        nbt.putIntArray("pos", new int[]{location.getX(), location.getY(), location.getZ()});
        nbt.putFloat("yaw", location.pitch);
        nbt.putFloat("pitch", location.pitch);
        return nbt;
    }

    public static RotatedLocation deserialize(CompoundTag nbt) {
        int[] pos = nbt.getIntArray("pos");
        return new RotatedLocation(
                ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(nbt.getString("world"))),
                new BlockPos(pos[0], pos[1], pos[2]),
                nbt.getFloat("yaw"),
                nbt.getFloat("pitch")
        );
    }
}
