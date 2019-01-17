package org.dimdev.dimdoors.shared;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.dimdoors.shared.items.ModItems;

public final class ModRecipes {

    public static ResourceLocation getNameForRecipe(ItemStack output) {
        ResourceLocation baseLoc = new ResourceLocation(DimDoors.MODID, output.getItem().getRegistryName().getPath());
        ResourceLocation recipeLoc = baseLoc;
        int index = 0;
        while (CraftingManager.REGISTRY.containsKey(recipeLoc)) {
            index++;
            recipeLoc = new ResourceLocation(DimDoors.MODID, baseLoc.getPath() + "_" + index);
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

    public static IRecipe makeShapedlessRecipe(ItemStack output, Object... params) {
        ResourceLocation location = getNameForRecipe(output);
        NonNullList<Ingredient> ingredients = NonNullList.create();
        for (Object obj : params) {
            ingredients.add(CraftingHelper.getIngredient(obj));
        }
        ShapelessRecipes recipe = new ShapelessRecipes(output.getItem().getRegistryName().toString(), output, ingredients);
        recipe.setRegistryName(location);
        return recipe;
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        Item usedItem = ModConfig.general.useEnderPearlsInCrafting ? Items.ENDER_PEARL : ModItems.STABLE_FABRIC;

        IForgeRegistry<IRecipe> registry = event.getRegistry();

        if (!ModConfig.general.useEnderPearlsInCrafting) {
            registry.register(makeShapedRecipe(new ItemStack(ModItems.STABLE_FABRIC),
                    "yxy", 'x', Items.ENDER_PEARL, 'y', ModItems.WORLD_THREAD));
        }

        registry.register(makeShapedRecipe(new ItemStack(ModItems.IRON_DIMENSIONAL_DOOR),
                "yxy", 'x',usedItem, 'y', Items.IRON_DOOR));

        registry.register(makeShapedRecipe(new ItemStack(ModItems.UNSTABLE_DIMENSIONAL_DOOR),
                "xyx", 'x', Items.ENDER_EYE, 'y', Items.IRON_DOOR));

        registry.register(makeShapedRecipe(new ItemStack(ModItems.WOOD_DIMENSIONAL_DOOR),
                "yxy", 'x', usedItem, 'y', Items.OAK_DOOR));

        registry.register(makeShapedRecipe(new ItemStack(ModBlocks.WOOD_DIMENSIONAL_TRAPDOOR),
                "yx", 'x', usedItem, 'y', Blocks.TRAPDOOR));

        registry.register(makeShapedRecipe(new ItemStack(ModItems.GOLD_DIMENSIONAL_DOOR),
                "yxy", 'x', usedItem, 'y', ModItems.GOLD_DOOR));

        registry.register(makeShapedRecipe(new ItemStack(ModItems.GOLD_DOOR),
                "yy", "yy", "yy", 'y', Items.GOLD_INGOT));

        registry.register(makeShapedRecipe(new ItemStack(ModItems.QUARTZ_DIMENSIONAL_DOOR),
                "yx", 'x', usedItem, 'y', ModItems.QUARTZ_DOOR));

        registry.register(makeShapedRecipe(new ItemStack(ModItems.QUARTZ_DOOR), "yy", "yy", "yy", 'y', Items.QUARTZ));

        registry.register(makeShapedRecipe(new ItemStack(ModItems.RIFT_BLADE),
                "y", "y", "x", 'y', usedItem, 'x', Items.IRON_SWORD));

        //should not be craftable
        /*registry.register(makeShapedRecipe(new ItemStack(ModItems.RIFT_CONFIGURATION_TOOL),
        " w ", "xyx", "xzx", 'z', Items.DIAMOND_SHOVEL, 'y', ModItems.STABLE_FABRIC, 'x', ModItems.WORLD_THREAD, 'w', Items.DIAMOND));*/

        registry.register(makeShapedRecipe(new ItemStack(ModItems.RIFT_REMOVER),
                "xxx", "xyx", "xxx", 'x', Items.GOLD_INGOT, 'y', usedItem));

        registry.register(makeShapedRecipe(new ItemStack(ModItems.RIFT_REMOVER),
                "x x", " y ", "x x", 'x', Items.GOLD_INGOT, 'y', usedItem));

        registry.register(makeShapedRecipe(new ItemStack(ModItems.RIFT_SIGNATURE),
                "xxx", "xyx", "xxx", 'x', Items.IRON_INGOT, 'y', usedItem));

        registry.register(makeShapedRecipe(new ItemStack(ModItems.RIFT_SIGNATURE),
                "x x", " y ", "x x", 'x', Items.IRON_INGOT, 'y', usedItem));

        registry.register(makeShapedRecipe(new ItemStack(ModItems.STABILIZED_RIFT_SIGNATURE),
                "x x", " y ", "x x", 'x', usedItem, 'y', ModItems.RIFT_SIGNATURE));

        registry.register(makeShapedRecipe(new ItemStack(ModItems.RIFT_STABILIZER),
                "x x", " y ", "x x", 'x', Items.DIAMOND, 'y', usedItem));

        registry.register(makeShapedRecipe(new ItemStack(ModItems.WOVEN_WORLD_THREAD_HELMET),
                "xxx", "x x", 'x', ModItems.WORLD_THREAD));

        registry.register(makeShapedRecipe(new ItemStack(ModItems.WOVEN_WORLD_THREAD_CHESTPLATE),
                "x x", "xxx", "xxx", 'x', ModItems.WORLD_THREAD));

        registry.register(makeShapedRecipe(new ItemStack(ModItems.WOVEN_WORLD_THREAD_LEGGINGS),
                "xxx", "x x", "x x", 'x', ModItems.WORLD_THREAD));

        registry.register(makeShapedRecipe(new ItemStack(ModItems.WOVEN_WORLD_THREAD_BOOTS),
                "x x", "x x", 'x', ModItems.WORLD_THREAD));

        registry.register(makeShapedRecipe(new ItemStack(ModItems.WOVEN_WORLD_THREAD_HELMET),
                "xyx", "x x", 'x', ModItems.WORLD_THREAD, 'y', Items.LEATHER_HELMET));

        registry.register(makeShapedRecipe(new ItemStack(ModItems.WOVEN_WORLD_THREAD_CHESTPLATE),
                "x x", "xyx", "x x", 'x', ModItems.WORLD_THREAD, 'y', Items.LEATHER_CHESTPLATE));

        registry.register(makeShapedRecipe(new ItemStack(ModItems.WOVEN_WORLD_THREAD_LEGGINGS),
                "xyx", "x x", "x x", 'x', ModItems.WORLD_THREAD, 'y', Items.LEATHER_LEGGINGS));

        registry.register(makeShapedRecipe(new ItemStack(ModItems.WOVEN_WORLD_THREAD_BOOTS),
                " y ", "x x", 'x', ModItems.WORLD_THREAD, 'y', Items.LEATHER_BOOTS));

        for (int meta = 0; meta <= 15; meta++) {
            registry.register(makeShapedlessRecipe(new ItemStack(ModItems.FABRIC, 1, meta),
                    ModItems.FABRIC, new ItemStack(Items.DYE, 1, EnumDyeColor.byDyeDamage(meta).getMetadata())));
        }
    }
}
