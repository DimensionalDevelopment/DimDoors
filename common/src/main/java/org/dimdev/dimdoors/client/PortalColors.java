package org.dimdev.dimdoors.client;

import org.dimdev.dimdoors.api.util.RGBA;
import org.joml.Vector3f;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Regenerates the dimensional portal layer colours from the pre-1.7 DimDoors renderer
 * (RenderDimDoor + TileEntityDimDoor#getRenderColor).
 *
 * The old renderer drew 16 stacked quads, pulling three floats per layer from a Random with a
 * fixed seed, so the palette is deterministic. Layer 0 is the outermost/dimmest, 15 the innermost.
 */
public final class PortalColors {
    public static final int LAYERS = 16;

    private static final long SEED = 31100L;

    private static List<ColorVariables> LIST = List.of(new ColorVariables(
            "nether", 0.5f, 0.5f, 0.5f, 0.4f, 0f, 0f
    ),  new ColorVariables(
            "regular", 0.5f, 0.4f, 0.6f, 0.1f, 0.4f, 0.5f
    ));



    /**
     * Raw per-layer hues, {@code float[LAYERS * 3]} laid out as RGB triples.
     * Blue can exceed 1 on a few layers; the old renderer let glColor4f clamp it.
     */
    public static float[] baseColors(ColorVariables nether) {
        Random rand = new Random(SEED);
        float[] out = new float[LAYERS * 3];

        IntStream.range(0, LAYERS).forEach(layer -> nether.apply(out, layer, rand));

        return out;
    }

    public record ColorVariables(
            String name,
            float multiRed, float multiGreen, float multiBlue,
            float baseRed, float baseGreen, float baseBlue
            ) {
        public void apply(float[] array, int layer, Random random) {
            int i = layer * 3;

            float intensity = intensity(layer);

            array[i] = (random.nextFloat() * multiRed + baseRed) * intensity;
            array[i+1] = (random.nextFloat() * multiGreen + baseGreen) * intensity;
            array[i+2] = (random.nextFloat() * multiBlue + baseBlue) * intensity;
        }

        /**
         * Per-layer brightness the old renderer multiplied the colour by: {@code 1 / (16 - layer + 0.8)},
         * except layer 0 which it pinned to 0.1.
         */
        public static float intensity(int layer) {
            return layer == 0 ? 0.1F : 1.0F / ((16 - layer) + 0.8F);
        }
    }

    /** Formats a triple-per-line Java array initialiser, ready to paste into a field. */
    public static String toJavaLiteral(float[] colors) {
        StringBuilder sb = new StringBuilder();

        for (int layer = 0; layer < LAYERS; layer++) {
            int i = layer * 3;
            sb.append(String.format("            %ff, %ff, %ff%s%n",
                    colors[i], colors[i + 1], colors[i + 2], layer == LAYERS - 1 ? "" : ","));
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        for (var color : LIST) {
            String name = color.name;

            System.out.println("// " + name + " base");
            System.out.print(toJavaLiteral(baseColors(color)));
            System.out.println();
        }
    }
}