package com.zixiken.dimdoors.shared.util;

import java.util.Map;
import java.util.Random;

/**
 *
 * @author Robijnvogel
 */
public class MathUtils {

    /**
     * Compares the integers in two arrays and returns true if any integer in
     * the first set is within range 0 to {@code difference} - 1 of any integer
     * in the second array.
     *
     * @param setOne the first integer array to compare
     * @param difference see method description
     * @param setTwo the second integer array to compare
     * @pre difference >= 0
     * @throws IllegalArgumentException if precondition is violated
     * @return {@code (\exists i, j; ; abs(setOne[i] - setTwo[j]) < difference }
     */
    public static boolean withinDistanceOf(int[] setOne, int difference, int[] setTwo) throws IllegalArgumentException {
        if (difference < 0) {
            throw new IllegalArgumentException("precondition was violated");
        }
        for (int one : setOne) {
            for (int two : setTwo) {
                if (Math.max(one, two) - Math.min(one, two) < difference) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the sum of the values of all integer elements in an integer array
     *
     * @param intArray the integers to calculate the sum of
     * @param flag this flag is meant to check if all elements in the array must
     * be positive (0), negative(1), or it doesn't matter (anything else)
     * @pre
     * {@code (flag != 0 && flag != 1) || (flag == 0 && (\forall i; intArray.has(i); i >= 0)) || (flag == 1 && (\forall i; intArray.has(i); i <= 0))}
     * @throws IllegalArgumentException if precondition is violated
     * @return {@code sum(i = 0; intArray.has(i); intArray[i]) }
     */
    public static int arraySum(int[] intArray, short flag) {
        int r = 0;
        for (int i : intArray) { //check flag
            if (flag == 0 && i < 0) {
                throw new IllegalArgumentException("all integers in array must be positive");
            } else if (flag == 1 && i > 0) {
                throw new IllegalArgumentException("all integers in array must be negative");
            }
            r += i;
        }
        return r;
    }

    public static <T> T weightedRandom(Map<T, Float> weights) {
        if (weights.size() == 0) return null;
        int totalWeight = 0;
        for (float weight : weights.values()) {
            totalWeight += weight;
        }
        Random random = new Random();
        float f = random.nextFloat() * totalWeight;
        for (Map.Entry<T, Float> e : weights.entrySet()) {
            f -= e.getValue();
            if (f < 0) return e.getKey();
        }
        return null;
    }
}
