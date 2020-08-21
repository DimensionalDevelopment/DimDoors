package org.dimdev.dimdoors.util;

public class RGBA {
    float red;
    float green;
    float blue;
    float alpha;

    public RGBA() {
    }

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

    public static RGBA[] fromFloatArray(float[][] f) {
        RGBA[] arr = new RGBA[4];
        for (int a = 0; a < 4; a ++) {
            arr[a] = fromFloatArray(f[a]);
        }
        return arr;
    }
}
