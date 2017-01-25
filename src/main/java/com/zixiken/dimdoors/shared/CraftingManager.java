package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.shared.blocks.ModBlocks;
import com.zixiken.dimdoors.shared.items.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class CraftingManager {

    public static void registerRecipes() {
        GameRegistry.addRecipe(new ItemStack(ModItems.itemStableFabric, 1),
                "yxy", 'x', Items.ENDER_PEARL, 'y', ModItems.itemWorldThread);

        GameRegistry.addRecipe(new ItemStack(ModItems.itemDimDoor, 1),
                "yxy", 'x', ModItems.itemStableFabric, 'y', Items.IRON_DOOR);

        GameRegistry.addRecipe(new ItemStack(ModItems.itemDimDoorChaos, 1),
                "yxy", 'x', Items.ENDER_EYE, 'y', ModItems.itemDimDoor);

        GameRegistry.addRecipe(new ItemStack(ModItems.itemDimDoorWarp, 1),
                "yxy", 'x', Items.ENDER_PEARL, 'y', Items.OAK_DOOR);

        GameRegistry.addRecipe(new ItemStack(ModBlocks.blockDimHatch, 1),
                "y", "x", "y", 'x', Items.ENDER_PEARL, 'y', Blocks.TRAPDOOR);

        GameRegistry.addRecipe(new ItemStack(ModItems.itemDimDoorGold, 1),
                "yxy", 'x', ModItems.itemStableFabric, 'y', ModItems.itemDoorGold);

        GameRegistry.addRecipe(new ItemStack(ModItems.itemDoorGold, 1),
                "yy", "yy", "yy", 'y', Items.GOLD_INGOT);

        GameRegistry.addRecipe(new ItemStack(ModItems.itemDimDoorPersonal, 1),
                "yxy", 'y', ModItems.itemDoorQuartz, 'x', ModItems.itemStableFabric);

        GameRegistry.addRecipe(new ShapedOreRecipe(ModItems.itemDoorQuartz,
                "yy", "yy", "yy", 'y', "quartz"));
        
        GameRegistry.addRecipe(new ItemStack(ModItems.itemRiftBlade),
                "y", "y", "x", 'y', ModItems.itemStableFabric, 'x', Items.IRON_SWORD);
        
        GameRegistry.addRecipe(new ItemStack(ModItems.itemRiftBlade),
                "y", "y", "x", 'y', ModItems.itemStableFabric, 'x', Items.IRON_SWORD);
        
        GameRegistry.addRecipe(new ItemStack(ModItems.itemRiftConnectionTool),
                " w ", "xyx", "xzx", 'z', Items.DIAMOND_SHOVEL, 'y', ModItems.itemStableFabric, 'x', ModItems.itemWorldThread, 'w', Items.DIAMOND);
    }
}
