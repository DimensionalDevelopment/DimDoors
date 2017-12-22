package ddutils.math;

import java.util.Random;

/**
 * @author Robijnvogel
 */
public class RandomUtils { // These utils aren't being used by DimDoors!

    /**
     * Compares the integers in two arrays and returns true if any integer in
     * the first set is within range 0 to {@code difference} - 1 of any integer
     * in the second array.
     *
     * @param set1     the first integer array to compare
     * @param difference see method description
     * @param set2     the second integer array to compare
     * @return {@code (\exists i, j; ; abs(set1[i] - set2[j]) < difference }
     * @throws IllegalArgumentException if precondition is violated
     * @pre difference >= 0
     */
    public static boolean withinDistanceOf(int[] set1, int difference, int[] set2) throws IllegalArgumentException {
        if (difference < 0) throw new IllegalArgumentException("Difference must be larger than 0");
        for (int e1 : set1) {
            for (int e2 : set2) {
                if (Math.max(e1, e2) - Math.min(e1, e2) < difference) return true;
            }
        }
        return false;
    }

    /**
     * Returns the sum of the values of all integer elements in an integer array
     *
     * @param intArray the integers to calculate the sum of
     * @param flag     this flag is meant to check if all elements in the array must
     *                 be positive (0), negative(1), or it doesn't matter (anything else)
     * @return {@code sum(i = 0; intArray.has(i); intArray[i]) }
     * @throws IllegalArgumentException if precondition is violated
     * @pre {@code (flag != 0 && flag != 1) || (flag == 0 && (\forall i; intArray.has(i); i >= 0)) || (flag == 1 && (\forall i; intArray.has(i); i <= 0))}
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

    /**
     * Returns either true or false, based on a weighted random, taking in
     * account the parameters as the weights for true and false as outcomes. For
     * instance: (2, 3) should return true in 40% of the cases and false in 60%
     * of the cases.
     *
     * @param trueWeight  the chance weight that this method will return true
     * @param falseWeight the chance weight that this method will return false
     * @return true or false
     * @throws IllegalArgumentException if precondition is violated
     * @pre {@code trueWeight > 0 && falseWeight > 0}
     */
    public static boolean weightedBoolean(int trueWeight, int falseWeight) {
        if (trueWeight <= 0 || falseWeight <= 0) {
            throw new IllegalArgumentException("Either of both weights were 0 or lower. Both should be at least 1.");
        }
        Random random = new Random();
        return random.nextInt(trueWeight + falseWeight) < trueWeight;
    }

    /**
     * This method returns the sum of {@code base} and a random element of
     * {@code transformations} the weight of each of the entries in
     *
     * @param base            the base value to transform from
     * @param transformations the possible transformations
     * @param weights         the chance-weight of those transformations
     * @return the sum of {@code base} and the value of an element in
     * {@code transformations}
     * @throws IllegalArgumentException if precondition is violated
     * @pre {@code transformations.length = weights.length} && {@code (\forall i; intArray.has(i); i >= 0)}
     * && {@code MathUtils.arraySum(weights, 1) > 0}
     */
    public static int transformRandomly(int base, int[] transformations, int[] weights) {
        if (transformations.length != weights.length) {
            throw new IllegalArgumentException("pre was violated, transformations.length != weights.length");
        }
        Random random = new Random();
        int weightSum = arraySum(weights, (short) 0);
        if (weightSum <= 0) {
            throw new IllegalArgumentException("pre was violated, RandomUtils.arraySum(weights, 1) <= 0");
        }
        int choice = random.nextInt(weightSum);
        for (int i = 0; i < weights.length; i++) {
            choice -= weights[i];
            if (choice < 0) {
                return base + transformations[i];
            }
        }
        throw new IllegalStateException("");
    }
}
