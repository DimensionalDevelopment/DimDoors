package org.dimdev.dimdoors.util;

public class MathUtils {

    public static boolean betewen(int value, int min, int max) {
        return value >= min && value <= max;
    }

    public static boolean betewen(double value, double min, double max) {
        return value >= min && value <= max;
    }
}
