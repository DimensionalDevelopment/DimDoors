package org.dimdev.dimdoors.compat.jei.decay;

import dev.architectury.fluid.FluidStack;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.dimdev.dimdoors.compat.decay.DecayDisplayData;
import org.dimdev.dimdoors.world.decay.results.DecayResult;
import org.jetbrains.annotations.Nullable;

public final class DecayJeiUtil {
    private DecayJeiUtil() {
    }

    public static boolean supports(DecayDisplayData data) {
        return supports(data.input()) && data.outputs().stream().allMatch(DecayJeiUtil::supports);
    }

    public static boolean supports(Object object) {
        return asItemStack(object) != null || asFluid(object) != null;
    }

    public static boolean supports(DecayResult.Result result) {
        return asItemStack(result) != null || asFluid(result) != null;
    }

    public static void addInput(IRecipeSlotBuilder builder, Object object) {
        ItemStack itemStack = asItemStack(object);
        if (itemStack != null) {
            builder.addItemStack(itemStack);
            return;
        }

        Fluid fluid = asFluid(object);
        if (fluid != null) {
            builder.addFluidStack(fluid, FluidStack.bucketAmount());
        }
    }

    public static void addOutput(IRecipeSlotBuilder builder, DecayResult.Result result) {
        ItemStack itemStack = asItemStack(result);
        if (itemStack != null) {
            builder.addItemStack(itemStack);
            return;
        }

        Fluid fluid = asFluid(result);
        if (fluid != null) {
            builder.addFluidStack(fluid, FluidStack.bucketAmount() * result.amount());
        }
    }

    private static @Nullable ItemStack asItemStack(Object object) {
        if (object instanceof ResourceKey<?> key) {
            if (key.isFor(Registries.BLOCK)) {
                return asItemStack(BuiltInRegistries.BLOCK.get(key.location()));
            }
            return null;
        }
        if (object instanceof DecayResult.Result result) {
            return result.obj() instanceof Block block ? new ItemStack(block, result.amount()) : null;
        }
        if (object instanceof ItemLike itemLike) {
            return new ItemStack(itemLike);
        }
        return null;
    }

    private static @Nullable Fluid asFluid(Object object) {
        if (object instanceof ResourceKey<?> key) {
            if (key.isFor(Registries.FLUID)) {
                return BuiltInRegistries.FLUID.get(key.location());
            }
            return null;
        }
        if (object instanceof DecayResult.Result result) {
            return result.obj() instanceof Fluid fluid ? fluid : null;
        }
        if (object instanceof Fluid fluid) {
            return fluid;
        }
        return null;
    }
}
