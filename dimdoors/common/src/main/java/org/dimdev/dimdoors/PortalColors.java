package org.dimdev.dimdoors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import org.dimdev.dimcore.api.util.function.StreamUtils;
import org.dimdev.dimdoors.util.Utils;

import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class PortalColors {
    public static final Codec<Integer> STRING_INT_CODEC = Codec.STRING.xmap(Integer::decode, Integer::toHexString);

    public static final  Codec<Integer> INTEGER = Codec.withAlternative(STRING_INT_CODEC, Codec.INT);

    public static Codec<int[]> COLORS_CODEC = INTEGER
            .listOf(16, 16)
            .xmap(Collection::stream, Stream::toList)
            .xmap(StreamUtils::toIntStream, IntStream::boxed)
            .xmap(IntStream::toArray, Arrays::stream);

    public static final MapCodec<Map<DyeColor, int[]>> DYE_COLORS_CODEC = Codec.unboundedMap(DyeColor.CODEC, COLORS_CODEC).fieldOf("dyes");
    public static final MapCodec<Map<ResourceKey<Level>, int[]>> LEVEL_COLORS_CODEC = Codec.unboundedMap(Level.RESOURCE_KEY_CODEC, COLORS_CODEC).fieldOf("levels");
    public static final MapCodec<int[]> BASE_COLOR_CODEC = COLORS_CODEC.fieldOf("base_color");

    private static int[] baseColor = new int[]{
            0X05191C, 0X031816, 0X071919, 0X0B1C1D,
            0X101E18, 0X10161F, 0X151C2A, 0X182717,
            0X1B2131, 0X181C2F, 0X222325, 0X113E3C,
            0X322436, 0X0C5052, 0X34634D, 0X1450A8
    };

    private static Map<DyeColor, int[]> dyes = Map.of();
    private static Map<ResourceKey<Level>, int[]> levels = Map.of();

    private static final ResourceLocation PORTAL_COLORS = DimensionalDoors.id("portal_colors.json");

    public static int[] dye(DyeColor color) {
        return dyes.get(color);
    }

    public static int[] levels(ResourceKey<Level> level) {
        return levels.get(level);
    }

    public static int[] base() {
        return baseColor;
    }

    public static void load(ResourceManager manager) {
        var list = manager.getResourceStack(PORTAL_COLORS);

        int[] baseColor = null;
        boolean baseColorLoaded = false;
        Map<DyeColor, int[]> dyes = new HashMap<>();
        Map<ResourceKey<Level>, int[]> levels = new HashMap<>();

        for (int i = list.size() - 1; i >= 0; i--) {
            var resource = list.get(i);
            try (var reader = resource.openAsReader()) {
                var json = GsonHelper.parse(reader);

                var baseColorOptional = BASE_COLOR_CODEC.compressedDecode(JsonOps.INSTANCE, json).result();

                if (!baseColorLoaded && baseColorOptional.isPresent()) {
                    baseColor = baseColorOptional.get();
                    baseColorLoaded = true;
                }

                DYE_COLORS_CODEC.compressedDecode(JsonOps.INSTANCE, json).result().ifPresent(dyeColorMap -> Utils.mergeMaps(dyes, dyeColorMap));
                LEVEL_COLORS_CODEC.compressedDecode(JsonOps.INSTANCE, json).result().ifPresent(levelMap -> Utils.mergeMaps(levels, levelMap));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if(!baseColorLoaded) baseColor = new int[]{
                0X05191C, 0X031816, 0X071919, 0X0B1C1D,
                0X101E18, 0X10161F, 0X151C2A, 0X182717,
                0X1B2131, 0X181C2F, 0X222325, 0X113E3C,
                0X322436, 0X0C5052, 0X34634D, 0X1450A8
        };

        PortalColors.baseColor = baseColor;
        PortalColors.dyes = dyes;
        PortalColors.levels = levels;
    }
}