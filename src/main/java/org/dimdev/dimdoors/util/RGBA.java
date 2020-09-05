package org.dimdev.dimdoors.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.dimdev.dimdoors.rift.targets.EscapeTarget;

public class RGBA implements Cloneable {
    public static Codec<RGBA> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codec.FLOAT.fieldOf("red").forGetter(RGBA::getRed),
                Codec.FLOAT.fieldOf("green").forGetter(RGBA::getGreen),
                Codec.FLOAT.fieldOf("blue").forGetter(RGBA::getBlue),
                Codec.FLOAT.fieldOf("alpha").forGetter(RGBA::getAlpha)
        ).apply(instance, RGBA::new);
    });

    float red;
    float green;
    float blue;
    float alpha;

    public RGBA(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public float getRed() {
        return red;
    }

    public float getGreen() {
        return green;
    }

    public float getBlue() {
        return blue;
    }

    public float getAlpha() {
        return alpha;
    }

    public static RGBA fromFloatArray(float[] f) {
        return new RGBA(f[0], f[1], f[2], f[3]);
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
}
