package org.dimdev.dimdoors.util;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public final class RGBA implements Cloneable, Comparable<RGBA>, Iterable<Float> {
    public static final RGBA NONE = new RGBA(-1, -1, -1, -1);
    public static Codec<RGBA> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("red").forGetter(RGBA::getRed),
            Codec.FLOAT.fieldOf("green").forGetter(RGBA::getGreen),
            Codec.FLOAT.fieldOf("blue").forGetter(RGBA::getBlue),
            Codec.FLOAT.fieldOf("alpha").forGetter(RGBA::getAlpha)
    ).apply(instance, RGBA::new));

    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;

    public RGBA(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public float getRed() {
        return this.red;
    }

    public float getGreen() {
        return this.green;
    }

    public float getBlue() {
        return this.blue;
    }

    public float getAlpha() {
        return this.alpha;
    }

    public static RGBA fromFloatArray(float[] f) {
        return new RGBA(f[0], f[1], f[2], f[3]);
    }

    public static float[] toFloatArray(RGBA o) {
        return new float[]{o.red, o.blue, o.green, o.alpha};
    }

    public float[] toFloatArray() {
        return new float[]{this.red, this.blue, this.green, this.alpha};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RGBA rgba = (RGBA) o;
        return Float.compare(rgba.red, this.red) == 0 &&
                Float.compare(rgba.green, this.green) == 0 &&
                Float.compare(rgba.blue, this.blue) == 0 &&
                Float.compare(rgba.alpha, this.alpha) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.red, this.green, this.blue, this.alpha);
    }

    public static RGBA[] fromFloatArrays(float[][] f) {
        RGBA[] arr = new RGBA[f.length];
        for (int a = 0; a < f.length; a++) {
            arr[a] = fromFloatArray(f[a]);
        }
        return arr;
    }

    @Override
    public RGBA clone() {
        try {
            return (RGBA) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @Override
    public Iterator<Float> iterator() {
        return new Iterator<Float>() {
            private final float[] rgba = RGBA.this.toFloatArray();
            private int index = 0;

            @Override
            public boolean hasNext() {
                return this.index < 4;
            }

            @Override
            public Float next() {
                return this.rgba[this.index++];
            }
        };
    }

    @Override
    public void forEach(Consumer<? super Float> action) {
        for (Float e : this.toFloatArray()) {
            action.accept(e);
        }
    }

    @Override
    public int compareTo(@NotNull RGBA o) {
        if (this.equals(o)) {
            return 0;
        }
        return Float.compare(this.alpha, o.alpha) +
                Float.compare(this.red, o.red) +
                Float.compare(this.green, o.green) +
                Float.compare(this.blue, o.blue);
    }
}
