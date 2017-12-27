package org.dimdev.dimdoors.shared;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.dimdoors.shared.items.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;

public final class CraftingManager {

    public static ResourceLocation getNameForRecipe(ItemStack output) {
        ResourceLocation baseLoc = new ResourceLocation(DimDoors.MODID, output.getItem().getRegistryName().getResourcePath());
        ResourceLocation recipeLoc = baseLoc;
        int index = 0;
        while (net.minecraft.item.crafting.CraftingManager.REGISTRY.containsKey(recipeLoc)) {
            index++;
            recipeLoc = new ResourceLocation(DimDoors.MODID, baseLoc.getResourcePath() + "_" + index);
        }
        return recipeLoc;
    }

    public static IRecipe makeShapedRecipe(ItemStack output, Object... params) {
        ResourceLocation location = getNameForRecipe(output);
        CraftingHelper.ShapedPrimer primer = CraftingHelper.parseShaped(params);
        ShapedRecipes recipe = new ShapedRecipes(output.getItem().getRegistryName().toString(), primer.width, primer.height, primer.input, output);
        recipe.setRegistryName(location);
        return recipe;
    }

    public static IRecipe makeShapedOreRecipe(ItemStack output, Object... params) {
        ResourceLocation location = getNameForRecipe(output);
        ShapedOreRecipe recipe = new ShapedOreRecipe(location, output, params);
        recipe.setRegistryName(location);
        return recipe;
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) { // TODO
        event.getRegistry().register(makeShapedRecipe(new ItemStack(ModItems.STABLE_FABRIC, 1),
                "yxy", 'x', Items.ENDER_PEARL, 'y', ModItems.WORLD_THREAD));

        event.getRegistry().register(makeShapedRecipe(new ItemStack(ModItems.DIMENSIONAL_DOOR, 1),
                "yxy", 'x', ModItems.STABLE_FABRIC, 'y', Items.IRON_DOOR));

        event.getRegistry().register(makeShapedRecipe(new ItemStack(ModItems.UNSTABLE_DIMENSIONAL_DOOR, 1),
                "yxy", 'x', Items.ENDER_EYE, 'y', ModItems.DIMENSIONAL_DOOR));

        event.getRegistry().register(makeShapedRecipe(new ItemStack(ModItems.WARP_DIMENSIONAL_DOOR, 1),
                "yxy", 'x', Items.ENDER_PEARL, 'y', Items.OAK_DOOR));

        event.getRegistry().register(makeShapedRecipe(new ItemStack(ModBlocks.WOOD_DIMENSIONAL_TRAPDOOR, 1),
                "y", "x", "y", 'x', Items.ENDER_PEARL, 'y', Blocks.TRAPDOOR));

        event.getRegistry().register(makeShapedRecipe(new ItemStack(ModItems.GOLD_DIMENSIONAL_DOOR, 1),
                "yxy", 'x', ModItems.STABLE_FABRIC, 'y', ModItems.GOLD_DOOR));

        event.getRegistry().register(makeShapedRecipe(new ItemStack(ModItems.GOLD_DOOR, 1),
                "yy", "yy", "yy", 'y', Items.GOLD_INGOT));

        event.getRegistry().register(makeShapedRecipe(new ItemStack(ModItems.PERSONAL_DIMENSIONAL_DOOR, 1),
                "yxy", 'y', ModItems.QUARTZ_DOOR, 'x', ModItems.STABLE_FABRIC));

        event.getRegistry().register(makeShapedOreRecipe(new ItemStack(ModItems.QUARTZ_DOOR, 1),
                "yy", "yy", "yy", 'y', "quartz"));

        event.getRegistry().register(makeShapedRecipe(new ItemStack(ModItems.RIFT_BLADE),
                "y", "y", "x", 'y', ModItems.STABLE_FABRIC, 'x', Items.IRON_SWORD));
        
        event.getRegistry().register(makeShapedRecipe(new ItemStack(ModItems.RIFT_BLADE),
                "y", "y", "x", 'y', ModItems.STABLE_FABRIC, 'x', Items.IRON_SWORD));
        
        event.getRegistry().register(makeShapedRecipe(new ItemStack(ModItems.RIFT_CONNECTION_TOOL),
                " w ", "xyx", "xzx", 'z', Items.DIAMOND_SHOVEL, 'y', ModItems.STABLE_FABRIC, 'x', ModItems.WORLD_THREAD, 'w', Items.DIAMOND));

        event.getRegistry().register(makeShapedRecipe(new ItemStack(ModItems.RIFT_REMOVER),
                " x ", "xyx", " x ", 'x', Items.GOLD_INGOT, 'y', Items.ENDER_PEARL));

        event.getRegistry().register(makeShapedRecipe(new ItemStack(ModItems.RIFT_REMOVER),
                "xxx", "xyx", "xxx", 'x', Items.GOLD_INGOT, 'y', ModItems.STABLE_FABRIC));
    }
}
