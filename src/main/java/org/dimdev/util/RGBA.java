package org.dimdev.util;

public class RGBA {
    float red;
    float green;
    float blue;
    float alpha;
    public RGBA() {}

    public RGBA (float red, float green, float blue, float alpha) {
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

    public static RGBA[] fromFloatArray(float[][] f) {
        RGBA[] arr = new RGBA[4];
        arr[0] = fromFloatArray(f[0]);
        arr[1] = fromFloatArray(f[1]);
        arr[2] = fromFloatArray(f[2]);
        arr[3] = fromFloatArray(f[3]);
        return arr;
    }
}
