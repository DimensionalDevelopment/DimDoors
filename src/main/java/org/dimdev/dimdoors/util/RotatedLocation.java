package org.dimdev.dimdoors.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.Tag;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.dimdev.annotatednbt.Saved;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;

public class RotatedLocation extends Location {
    static Codec<RotatedLocation> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                World.CODEC.fieldOf("world").forGetter(location -> location.world),
                BlockPos.field_25064.fieldOf("pos").forGetter(location -> location.pos),
                Codec.FLOAT.fieldOf("yaw").forGetter(a -> a.yaw),
                Codec.FLOAT.fieldOf("pitch").forGetter(a -> a.pitch)
        ).apply(instance, RotatedLocation::new);
    });

    @Saved
    public final float yaw;
    @Saved
    public final float pitch;

    public RotatedLocation(RegistryKey<World> world, BlockPos pos, float yaw, float pitch) {
        super(world, pos);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public static RotatedLocation deserialize(CompoundTag nbt) {
        return NbtUtil.deserialize(nbt, CODEC);
    }

    public static Tag serialize(RotatedLocation location) {
        return NbtUtil.serialize(location, CODEC);
    }
}
