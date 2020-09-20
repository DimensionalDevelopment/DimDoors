package org.dimdev.dimdoors.util;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockBox;

public class Codecs {
    public static Codec<Set<Integer>> INT_SET = Codec.INT_STREAM.comapFlatMap(a -> DataResult.success(a.boxed().collect(Collectors.toSet())), a -> a.stream().mapToInt(Integer::intValue));

    public static Codec<BlockBox> BLOCK_BOX = Codec.INT_STREAM.comapFlatMap(a -> DataResult.success(new BlockBox(a.toArray())), a -> IntStream.of(a.minX, a.minY, a.minZ, a.maxX, a.maxY, a.maxZ));

    public static Codec<DyeColor> DYE_COLOR = Codec.INT.xmap(DyeColor::byId, DyeColor::getId);
}
