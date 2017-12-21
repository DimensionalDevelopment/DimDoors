package com.zixiken.dimdoors.shared.util;

import com.zixiken.dimdoors.DimDoors;
import java.util.Random;
import net.minecraft.util.EnumFacing;

/**
 * @author Robijnvogel
 */
public class RandomUtils {

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
        return random.nextInt(trueWeight + falseWeight) < trueWeight;
    }

    /**
     * This method returns the sum of {@code base} and a random element of
     * {@code transformations} the weight of each of the entries in
     *
     * @param base the base value to transform from
     * @param transformations the possible transformations
     * @param weights the chance-weight of those transformations
     * @pre {@code transformations.length = weights.length} && {@code (\forall i; intArray.has(i); i >= 0)}
     * && {@code MathUtils.arraySum(weights, 1) > 0}
     * @throws IllegalArgumentException if precondition is violated
     * @return the sum of {@code base} and the value of an element in
     * {@code transformations}
     */
    public static int transformRandomly(int base, int[] transformations, int[] weights) {
        if (transformations.length != weights.length) {
            throw new IllegalArgumentException("pre was violated, transformations.length != weights.length");
        }
        Random random = new Random();
        int weightSum = MathUtils.arraySum(weights, (short) 0);
        if (weightSum <= 0) {
            throw new IllegalArgumentException("pre was violated, MathUtils.arraySum(weights, 1) <= 0");
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

    /**
     * This method returns a Location that is determined by offsetting
     * origLocation in the x and z direction by a randomly positive or negative
     * random integer value, both based on the formula:
     * {@code randomInt((base * depth) ^ power)}
     *
     * @param base this value is configured in the config files of DimDoors
     * @param power this value is configured in the config files of DimDoors
     * @param depth this should be the depth of the newly generated dungeon
     * pocket
     * @pre {@code base > 0 && depth > 0 && power >= 0 && origLocation != null}
     * @throws IllegalArgumentException if pre is violated
     * @param origLocation the original location to offset from
     * @return a Location for which the x and z coordinates have both been
     * offset by random values between {@code -(base * depth) ^ power)} and
     * {@code ((base * depth) ^ power)} and y and dimensionID are the same as
     * {@code origLocation}'s
     */
    public static Location transformLocationRandomly(int base, double power, int depth, Location origLocation) {
        if (base <= 0 || depth <= 0 || power < 0 || origLocation == null) {
            throw new IllegalArgumentException("pre was violated");
        }
        Random random = new Random();
        DimDoors.log.info("base = " + base + ", power = " + power + ", depth = " + depth + " and power is " + Math.pow(base * depth, power));
        int xOffset = random.nextInt((int) Math.pow(base * depth, power)) * (random.nextBoolean() ? 1 : -1);
        int zOffset = random.nextInt((int) Math.pow(base * depth, power)) * (random.nextBoolean() ? 1 : -1);
        return new Location(origLocation.getWorld(), origLocation.getPos().offset(EnumFacing.EAST, xOffset).offset(EnumFacing.SOUTH, zOffset));
    }
}
