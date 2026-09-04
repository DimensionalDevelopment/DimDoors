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
//
//    public static final int[] OVERWORLD = new int[]{
//            0X05191C, 0X031816, 0X071919, 0X0B1C1D,
//            0X101E18, 0X10161F, 0X151C2A, 0X182717,
//            0X1B2131, 0X181C2F, 0X222325, 0X113E3C,
//            0X322436, 0X0C5052, 0X34634D, 0X1450A8
//    };
//
//    public static final int[] DESATURATED = new int[] {
//        0x1c1c1c, 0x181818, 0x191919, 0x1d1d1d,
//                0x1e1e1e, 0x1f1f1f, 0x2a2a2a, 0x272727,
//                0x313131, 0x2f2f2f, 0x252525, 0x3e3e3e,
//                0x363636, 0x525252, 0x636363, 0xa8a8a8
//    };
//
//    public static final EnumMap<DyeColor, int[]> DYES = Util.make(new EnumMap<>(DyeColor.class), new Consumer<EnumMap<DyeColor, int[]>>() {
//        @Override
//        public void accept(EnumMap<DyeColor, int[]> map) {
//            map.put(DyeColor.WHITE, new int[]{
//                    0X1B1C1C, 0X171818, 0X181919, 0X1C1D1D,
//                    0X3F3238, 0X6F6F6F, 0X6F6767, 0X262727,
//                    0X787A54, 0X2E2F2F, 0X242525, 0X777777,
//                    0X476C71, 0X636363, 0X57636C, 0X95CBC8
//            });
//            map.put(DyeColor.ORANGE, new int[]{
//                    0X1C0E03, 0X180C03, 0X190D03, 0X1D0F03,
//                    0X290502, 0X1F0904, 0X190C0B, 0X271505,
//                    0X1B000C, 0X2F0900, 0X251304, 0X3E2007,
//                    0X362B06, 0X5E2500, 0X811D06, 0X963B13
//            });
//            map.put(DyeColor.MAGENTA, new int[]{
//                    0X1C0B1B, 0X180917, 0X190A18, 0X1D0B1C,
//                    0X1E0C1C, 0X1F0C1D, 0X2A1E10, 0X270F25,
//                    0X31132F, 0X2F122D, 0X250F23, 0X3E183B,
//                    0X672828, 0X521F33, 0X3A2763, 0XAA3CA8
//            });
//            map.put(DyeColor.LIGHT_BLUE, new int[]{
//                    0X07171C, 0X061418, 0X071519, 0X08181D,
//                    0X08191E, 0X08191F, 0X13002A, 0X0A2027,
//                    0X312607, 0X0D272F, 0X0A1E25, 0X232364,
//                    0X0F377C, 0X105F94, 0X1E5BC4, 0X3C2CBE
//            });
//            map.put(DyeColor.YELLOW, new int[]{
//                    0X1C1807, 0X181406, 0X180606, 0X1D1907,
//                    0X1E1A07, 0X1F0C06, 0X2A240A, 0X272109,
//                    0X312A0C, 0X2F2200, 0X4B4D05, 0X3E350F,
//                    0X684B15, 0X696D16, 0XAE930D, 0XB4600D
//            });
//            map.put(DyeColor.LIME, new int[]{
//                    0X121C04, 0X0F1804, 0X101904, 0X131D05,
//                    0X331F0C, 0X141F05, 0X07271D, 0X192706,
//                    0X2E450A, 0X1E2F07, 0X182506, 0X283E0A,
//                    0X233608, 0X35520D, 0X428704, 0X2AA81A
//            });
//            map.put(DyeColor.PINK, new int[]{
//                    0X1C1014, 0X180E11, 0X190E11, 0X1D1114,
//                    0X1E1115, 0X242410, 0X2A181D, 0X1D1E45,
//                    0X311C22, 0X712756, 0X360F1A, 0X773446,
//                    0X742741, 0X702619, 0XA83E8C, 0XDC5177
//            });
//            map.put(DyeColor.GRAY, new int[]{
//                    0X181B1C, 0X111B11, 0X161819, 0X1D3035,
//                    0X1A1D1E, 0X1B1E1F, 0X24282A, 0X222627,
//                    0X3F3934, 0X292D2F, 0X202425, 0X313838,
//                    0X131516, 0X1A1616, 0X101212, 0X131616
//            });
//            map.put(DyeColor.LIGHT_GRAY, new int[]{
//                    0X181817, 0X191918, 0X1F1132, 0X1E1E1D,
//                    0X1F1F1E, 0X2A2A28, 0X272726, 0X31312F,
//                    0X2F2F2D, 0X252524, 0X3E3E3C, 0X1C1C1B,
//                    0X494941, 0X554F59, 0X6C7D62, 0X5E6A61
//            });
//            map.put(DyeColor.CYAN, new int[]{
//                    0X041C1C, 0X031818, 0X041919, 0X041D1D,
//                    0X041E1E, 0X041F1F, 0X062A2A, 0X052727,
//                    0X073131, 0X172F00, 0X40220B, 0X093E3E,
//                    0X054D3F, 0X0C5252, 0X0C4763, 0X17A86F
//            });
//            map.put(DyeColor.PURPLE, new int[]{
//                    0X15081C, 0X120718, 0X130719, 0X16081D,
//                    0X241006, 0X17081F, 0X1F0B2A, 0X1D0B27,
//                    0X240D31, 0X042C1D, 0X1C0A25, 0X2E113E,
//                    0X280F36, 0X321552, 0X481B63, 0X412DA8
//            });
//            map.put(DyeColor.BLUE, new int[]{
//                    0X0A0B1C, 0X080A18, 0X211306, 0X0A0C1D,
//                    0X0B0C1E, 0X0B0C1F, 0X181E43, 0X0E1027,
//                    0X111431, 0X11132F, 0X0D0F25, 0X16193E,
//                    0X131636, 0X2C1D52, 0X19367C, 0X2C35D4
//            });
//            map.put(DyeColor.BROWN, new int[]{
//                    0X1C120B, 0X180F09, 0X19100A, 0X1D130B,
//                    0X0A180A, 0X1F140C, 0X2A1B10, 0X27190F,
//                    0X311F13, 0X2F1E12, 0X2E0F00, 0X2F1B0B,
//                    0X362315, 0X3C230C, 0X3F3B0C, 0X53190F
//            });
//            map.put(DyeColor.GREEN, new int[]{
//                    0X151C05, 0X042728, 0X131904, 0X161D05,
//                    0X1D1106, 0X181F06, 0X202A07, 0X1E2707,
//                    0X193107, 0X0E2F00, 0X0B2507, 0X143E08,
//                    0X29360A, 0X444D0B, 0X066609, 0X186C11
//            });
//            map.put(DyeColor.RED, new int[]{
//                    0X1C0706, 0X180605, 0X080827, 0X1D0806,
//                    0X1E1B06, 0X1F0807, 0X2A0B09, 0X270A08,
//                    0X310D0B, 0X2F0C0A, 0X250A08, 0X57101B,
//                    0X6E2616, 0X49101F, 0X631A15, 0X341208
//            });
//            map.put(DyeColor.BLACK, new int[]{
//                    0x000000, 0x151518, 0xc00000, 0x19191d,
//                    0x1b0d0d, 0x0f0f0f, 0x130000, 0x1f0d0d,
//                    0x1b0000, 0x080808, 0x0d0d0d, 0x0c0c0e,
//                    0x0d0d0f, 0x070708, 0x130000, 0x1b0000
//            });
//        }
//    });
}