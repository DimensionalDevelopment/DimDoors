package org.dimdev.dimdoors.compat.rei.decay;

import dev.architectury.fluid.FluidStack;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.dimdev.dimdoors.compat.rei.TesselatingReiCompatClient;
import org.dimdev.dimdoors.world.decay.*;
import org.dimdev.dimdoors.world.decay.conditions.Applicator;
import org.dimdev.dimdoors.world.decay.results.DecayResult;

import java.util.*;
import java.util.stream.Stream;

public class DecayPatternDisplay extends BasicDisplay {
    public DecayPatternDisplay(List<EntryIngredient> input, List<EntryIngredient> output, Optional<ResourceLocation> id) {
        super(input, output, id);
    }

    DecayPatternDisplay(List<EntryIngredient> input, List<EntryIngredient> output) {
        this(input, output, Optional.empty());
    }

    public static List<DecayPatternDisplay> list(DecayPatternHolder patternHolder, RegistryAccess registryAccess) {
        //TODO: Redo
        return Collections.emptyList();

//        var pattern = patternHolder.value();
//        var output = List.of(pattern.result().produces().stream().map(DecayPatternDisplay::toEntryStack).collect(EntryIngredient.collector()));
//        return pattern.conditions().stream()
//                .flatMap(a -> {
//                    return a instanceof Applicator<?> applicator ? applicator.constructApplicable(registryAccess) : Stream.<ResourceKey<?>>empty();
//                })
//                .map(a -> DecayPatternDisplay.toEntryStack(a))
//                .filter(a -> !a.isEmpty())
//                .map(stack -> EntryIngredient.of(stack))
//                .map(o -> Collections.singletonList(o))
//                .map(input ->  new DecayPatternDisplay(input, output))
//                .toList();
    }

//    public static DecayPatternDisplay of(DecayPatternHolder patternHolder) {
//        var pattern = patternHolder.value();
//        var input = pattern.conditions().stream().flatMap(a -> Stream.of(a.constructApplicableBlocks(), a.constructApplicableFluids())).flatMap(Collection::stream).map(DecayPatternDisplay::toEntryStack).filter(a -> !a.isEmpty()).collect(EntryIngredient.collector());
//        var output = pattern.result().produces().stream().map(DecayPatternDisplay::toEntryStack).collect(EntryIngredient.collector());
//        return new DecayPatternDisplay(List.of(input), List.of(output), Optional.of(patternHolder.id()));
//    }

    public static EntryStack<?> toEntryStack(DecayResult.Result result) {
        if(result.obj() instanceof Block block) {
            return EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(block, result.amount()));
        } else if(result.obj() instanceof Fluid fluid){
            return EntryStack.of(VanillaEntryTypes.FLUID, FluidStack.create(fluid, result.amount()));
        } else {
            return EntryStack.empty();
        }
    }

    public static EntryStack<?> toEntryStack(Object object) {
        if(object instanceof ResourceKey<?> key) {
            if(key.isFor(Registries.BLOCK)) return toEntryStack(BuiltInRegistries.BLOCK.get(key.location()));
            else if(key.isFor(Registries.FLUID)) return toEntryStack(BuiltInRegistries.FLUID.get(key.location()));
            else return EntryStack.empty();
        } else if (object instanceof ItemLike item) return EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(item));
        else if (object instanceof Fluid fluid) return EntryStack.of(VanillaEntryTypes.FLUID, FluidStack.create(fluid, FluidStack.bucketAmount()));
        else if(object instanceof DecayResult.Result result) return toEntryStack(result);
        else return EntryStack.empty();
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return TesselatingReiCompatClient.DECAYS_INTO;
    }
}
