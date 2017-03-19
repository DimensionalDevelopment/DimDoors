/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared.util;

/**
 *
 * @author Robijnvogel
 */
public class DDMathUtils {

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
        for (int One : setOne) {
            for (int Two : setTwo) {
                if ((Math.max(One, Two) - Math.min(One, Two)) < difference) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the sum of the values of all integer elements in an integer array
     *
     * @param intArray
     * @param flag this flag is meant to check if all elements in the array must
     * be positive, negative, or it doesn't matter
     * @pre
     * {@code flag == 0 || (flag == 1 && (\forall i; intArray.has(i); i >= 0)) || (flag == 2 && (\forall i; intArray.has(i); i <= 0))}
     * @throws IllegalArgumentException if precondition is violated
     * @return {@code sum(i = 0; intArray.has(i); intArray[i]) }
     */
    public static int arraySum(int[] intArray, short flag) {
        int r = 0;
        for (int i : intArray) { //check flag
            if (flag == 0 && i < 0) { //
                throw new IllegalArgumentException("all integers in array must be positive");
            }
            r += i;
        }
        return r;
    }
}
