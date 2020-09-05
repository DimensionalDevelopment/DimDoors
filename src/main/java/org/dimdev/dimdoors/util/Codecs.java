package org.dimdev.dimdoors.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.*;

public class Codecs {
    public static Codec<Set<Integer>> INT_SET = Codec.INT_STREAM.<Set<Integer>>comapFlatMap(a -> DataResult.success(a.boxed().collect(Collectors.toSet())), a -> a.stream().mapToInt(Integer::intValue));

    public static Codec<BlockBox> BLOCK_BOX = Codec.INT_STREAM.<BlockBox>comapFlatMap(a -> DataResult.success(new BlockBox(a.toArray())), a -> IntStream.of(a.minX, a.minY, a.minZ, a.maxX, a.maxY, a.maxZ));

    public static Codec<DyeColor> DYE_COLOR = Codec.INT.xmap(DyeColor::byId, DyeColor::getId);
}
