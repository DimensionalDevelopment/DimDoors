/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared.util;

import java.util.Random;
import net.minecraft.util.EnumFacing;

/**
 *
 * @author Robijnvogel
 */
public class DDRandomUtils {

    /**
     * Returns either true or false, based on a weighted random, taking in
     * account the parameters as the weights for true and false as outcomes. For
     * instance: (2, 3) should return true in 40% of the cases and false in 60%
     * of the cases.
     *
     * @param trueWeight the chance weight that this method will return true
     * @param falseWeight the chance weight that this method will return false
     * @pre {@code trueWeight > 0 && falseWeight > 0}
     * @throws IllegalArgumentException if precondition is violated
     * @return true or false
     */
    public static boolean weightedBoolean(int trueWeight, int falseWeight) { //@todo make this a utility function
        if (trueWeight <= 0 || falseWeight <= 0) {
            throw new IllegalArgumentException("Either of both weights were 0 or lower. Both should be at least 1.");
        }
        Random random = new Random();
        return (random.nextInt(trueWeight + falseWeight) < trueWeight);
    }

    /**
     * Hello world
     *
     * @param base
     * @param transformations
     * @param weights
     * @pre transformations.length = weights.length
     * @throws IllegalArgumentException if precondition is violated
     * @return the sum of {@code base} and the value of an element in
     * {@code transformations}
     */
    public static int transformRandomly(int base, int[] transformations, int[] weights) {
        if (transformations.length != weights.length) {
            throw new IllegalArgumentException("pre was violated");
        }
        Random random = new Random();
        int weightSum = DDMathUtils.arraySum(weights, (short) 1);
        int choice = random.nextInt(weightSum);
        for (int i = 0; i < weights.length; i++) {
            choice -= weights[i];
            if (choice < 0) {
                return base + transformations[i];
            }
        }
        throw new IllegalStateException("");
    }

    /**
     *
     * @param base
     * @param power
     * @param depth
     * @param origLocation
     * @return
     */
    public static Location transformLocationRandomly(int base, double power, int depth, Location origLocation) {
        Random random = new Random();
        int xOffset = (int) Math.pow(base * depth, power) * (random.nextBoolean() ? 1 : -1);
        int zOffset = (int) Math.pow(base * depth, power) * (random.nextBoolean() ? 1 : -1);
        return new Location(origLocation.getWorld(), origLocation.getPos().offset(EnumFacing.EAST, xOffset).offset(EnumFacing.SOUTH, zOffset));
    }

}
