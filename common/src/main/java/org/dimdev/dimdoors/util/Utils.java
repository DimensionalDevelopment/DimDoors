package org.dimdev.dimdoors.util;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.crafting.RecipeType;
import org.dimdev.dimdoors.recipe.TesselatingRecipe;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Utils {
    public static <T> T cast(Object register) {
        return (T) register;
    }

    public static Iterable<BlockPos> randomInSphere(RandomSource random, int count, BlockPos pos, int radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("Radius must be non-negative");
        }

        return () -> new Iterator<>() {
            private final int diameter = radius * 2 + 1;
            private final long radiusSquared = (long) radius * radius;
            private int remaining = count;

            @Override
            public boolean hasNext() {
                return this.remaining > 0;
            }

            @Override
            public BlockPos next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }

                this.remaining--;

                int xOffset;
                int yOffset;
                int zOffset;
                do {
                    xOffset = random.nextInt(this.diameter) - radius;
                    yOffset = random.nextInt(this.diameter) - radius;
                    zOffset = random.nextInt(this.diameter) - radius;
                } while ((long) xOffset * xOffset + (long) yOffset * yOffset + (long) zOffset * zOffset > this.radiusSquared);

                return pos.offset(xOffset, yOffset, zOffset);
            }
        };
    }
}
