package org.dimdev.dimdoors.world.feature;

import java.util.BitSet;
import java.util.Random;

import com.mojang.serialization.Codec;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public class CustomOreFeature extends OreFeature {

    public CustomOreFeature(Codec<OreFeatureConfig> codec) {
        super(codec);
    }

    @Override
    protected boolean generateVeinPart(WorldAccess world, Random random, OreFeatureConfig config, double double_1, double double_2, double double_3, double double_4, double double_5, double double_6, int int_1, int int_2, int int_3, int int_4, int int_5) {
        int int_6 = 0;
        BitSet bitSet = new BitSet(int_4 * int_5 * int_4);
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        double[] doubles_1 = new double[config.size * 4];

        int int_8;
        double double_12;
        double double_13;
        double double_14;
        double double_15;
        for (int_8 = 0; int_8 < config.size; ++int_8) {
            float float_1 = (float) int_8 / (float) config.size;
            double_12 = MathHelper.lerp(float_1, double_1, double_2);
            double_13 = MathHelper.lerp(float_1, double_5, double_6);
            double_14 = MathHelper.lerp(float_1, double_3, double_4);
            double_15 = random.nextDouble() * (double) config.size / 16.0D;
            double double_11 = ((double) (MathHelper.sin(3.1415927F * float_1) + 1.0F) * double_15 + 1.0D) / 2.0D;
            doubles_1[int_8 * 4 + 0] = double_12;
            doubles_1[int_8 * 4 + 1] = double_13;
            doubles_1[int_8 * 4 + 2] = double_14;
            doubles_1[int_8 * 4 + 3] = double_11;
        }

        for (int_8 = 0; int_8 < config.size - 1; ++int_8) {
            if (doubles_1[int_8 * 4 + 3] > 0.0D) {
                for (int int_9 = int_8 + 1; int_9 < config.size; ++int_9) {
                    if (doubles_1[int_9 * 4 + 3] > 0.0D) {
                        double_12 = doubles_1[int_8 * 4 + 0] - doubles_1[int_9 * 4 + 0];
                        double_13 = doubles_1[int_8 * 4 + 1] - doubles_1[int_9 * 4 + 1];
                        double_14 = doubles_1[int_8 * 4 + 2] - doubles_1[int_9 * 4 + 2];
                        double_15 = doubles_1[int_8 * 4 + 3] - doubles_1[int_9 * 4 + 3];
                        if (double_15 * double_15 > double_12 * double_12 + double_13 * double_13 + double_14 * double_14) {
                            if (double_15 > 0.0D) {
                                doubles_1[int_9 * 4 + 3] = -1.0D;
                            } else {
                                doubles_1[int_8 * 4 + 3] = -1.0D;
                            }
                        }
                    }
                }
            }
        }

        for (int_8 = 0; int_8 < config.size; ++int_8) {
            double double_16 = doubles_1[int_8 * 4 + 3];
            if (double_16 >= 0.0D) {
                double double_17 = doubles_1[int_8 * 4 + 0];
                double double_18 = doubles_1[int_8 * 4 + 1];
                double double_19 = doubles_1[int_8 * 4 + 2];
                int int_11 = Math.max(MathHelper.floor(double_17 - double_16), int_1);
                int int_12 = Math.max(MathHelper.floor(double_18 - double_16), int_2);
                int int_13 = Math.max(MathHelper.floor(double_19 - double_16), int_3);
                int int_14 = Math.max(MathHelper.floor(double_17 + double_16), int_11);
                int int_15 = Math.max(MathHelper.floor(double_18 + double_16), int_12);
                int int_16 = Math.max(MathHelper.floor(double_19 + double_16), int_13);

                for (int x = int_11; x <= int_14; ++x) {
                    double double_20 = ((double) x + 0.5D - double_17) / double_16;
                    if (double_20 * double_20 < 1.0D) {
                        for (int y = int_12; y <= int_15; ++y) {
                            double double_21 = ((double) y + 0.5D - double_18) / double_16;
                            if (double_20 * double_20 + double_21 * double_21 < 1.0D) {
                                for (int z = int_13; z <= int_16; ++z) {
                                    double double_22 = ((double) z + 0.5D - double_19) / double_16;
                                    if (double_20 * double_20 + double_21 * double_21 + double_22 * double_22 < 1.0D) {
                                        int int_20 = x - int_1 + (y - int_2) * int_4 + (z - int_3) * int_4 * int_5;
                                        if (!bitSet.get(int_20)) {
                                            bitSet.set(int_20);
                                            mutablePos.set(x, y, z);

                                            //Just added this line in
                                            CustomOreFeatureConfig customConfig = (CustomOreFeatureConfig) config;
                                            if (customConfig.blockPredicate.test(world.getBlockState(mutablePos))) {
                                                world.setBlockState(mutablePos, config.state, 2);
                                                ++int_6;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return int_6 > 0;
    }

}