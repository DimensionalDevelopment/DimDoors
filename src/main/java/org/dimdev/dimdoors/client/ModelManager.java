package org.dimdev.dimdoors.client;

import net.minecraft.item.EnumDyeColor;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.dimdoors.shared.items.ModItems;
import net.minecraft.block.BlockDoor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.minecraft.item.Item.getItemFromBlock;

@SideOnly(Side.CLIENT)
public final class ModelManager {

    public static void registerModels() {
        for (EnumDyeColor color : EnumDyeColor.values()) {
            register(getItemFromBlock(ModBlocks.FABRIC), color.getMetadata(), color.getName());
            register(getItemFromBlock(ModBlocks.ANCIENT_FABRIC), color.getMetadata(), color.getName());
        }
        register(getItemFromBlock(ModBlocks.UNRAVELLED_FABRIC));
        register(getItemFromBlock(ModBlocks.ETERNAL_FABRIC));

        register(getItemFromBlock(ModBlocks.RIFT));
        register(getItemFromBlock(ModBlocks.WOOD_DIMENSIONAL_TRAPDOOR));

        //Item registration
        register(ModItems.DIMENSIONAL_DOOR);
        register(ModItems.GOLD_DIMENSIONAL_DOOR);
        register(ModItems.GOLD_DOOR);
        register(ModItems.PERSONAL_DIMENSIONAL_DOOR);
        register(ModItems.QUARTZ_DOOR);
        register(ModItems.STABLE_FABRIC);
        register(ModItems.CHAOS_DOOR);
        register(ModItems.WARP_DIMENSIONAL_DOOR);
        register(ModItems.WORLD_THREAD);
        register(ModItems.RIFT_CONNECTION_TOOL);
        register(ModItems.RIFT_BLADE);
        register(ModItems.RIFT_REMOVER);
        register(ModItems.RIFT_SIGNATURE);
        register(ModItems.STABILIZED_RIFT_SIGNATURE);
        register(ModItems.BOOTS_WOVEN_WORLD_THREAD);
        register(ModItems.CHESTPLATE_WOVEN_WORLD_THREAD);
        register(ModItems.HELMET_WOVEN_WORLD_THREAD);
        register(ModItems.LEGGINGS_WOVEN_WORLD_THREAD);
    }

    public static void registerModelVariants() {

        ModelBakery.registerItemVariants(ModItems.FABRIC,
                new ResourceLocation(ModBlocks.FABRIC.getRegistryName() + "_white"),
                new ResourceLocation(ModBlocks.FABRIC.getRegistryName() + "_orange"),
                new ResourceLocation(ModBlocks.FABRIC.getRegistryName() + "_magenta"),
                new ResourceLocation(ModBlocks.FABRIC.getRegistryName() + "_light_blue"),
                new ResourceLocation(ModBlocks.FABRIC.getRegistryName() + "_yellow"),
                new ResourceLocation(ModBlocks.FABRIC.getRegistryName() + "_lime"),
                new ResourceLocation(ModBlocks.FABRIC.getRegistryName() + "_pink"),
                new ResourceLocation(ModBlocks.FABRIC.getRegistryName() + "_gray"),
                new ResourceLocation(ModBlocks.FABRIC.getRegistryName() + "_silver"),
                new ResourceLocation(ModBlocks.FABRIC.getRegistryName() + "_cyan"),
                new ResourceLocation(ModBlocks.FABRIC.getRegistryName() + "_purple"),
                new ResourceLocation(ModBlocks.FABRIC.getRegistryName() + "_blue"),
                new ResourceLocation(ModBlocks.FABRIC.getRegistryName() + "_brown"),
                new ResourceLocation(ModBlocks.FABRIC.getRegistryName() + "_green"),
                new ResourceLocation(ModBlocks.FABRIC.getRegistryName() + "_red"),
                new ResourceLocation(ModBlocks.FABRIC.getRegistryName() + "_black"));

        ModelBakery.registerItemVariants(ModItems.ANCIENT_FABRIC,
                new ResourceLocation(ModBlocks.ANCIENT_FABRIC.getRegistryName() + "_white"),
                new ResourceLocation(ModBlocks.ANCIENT_FABRIC.getRegistryName() + "_orange"),
                new ResourceLocation(ModBlocks.ANCIENT_FABRIC.getRegistryName() + "_magenta"),
                new ResourceLocation(ModBlocks.ANCIENT_FABRIC.getRegistryName() + "_light_blue"),
                new ResourceLocation(ModBlocks.ANCIENT_FABRIC.getRegistryName() + "_yellow"),
                new ResourceLocation(ModBlocks.ANCIENT_FABRIC.getRegistryName() + "_lime"),
                new ResourceLocation(ModBlocks.ANCIENT_FABRIC.getRegistryName() + "_pink"),
                new ResourceLocation(ModBlocks.ANCIENT_FABRIC.getRegistryName() + "_gray"),
                new ResourceLocation(ModBlocks.ANCIENT_FABRIC.getRegistryName() + "_silver"),
                new ResourceLocation(ModBlocks.ANCIENT_FABRIC.getRegistryName() + "_cyan"),
                new ResourceLocation(ModBlocks.ANCIENT_FABRIC.getRegistryName() + "_purple"),
                new ResourceLocation(ModBlocks.ANCIENT_FABRIC.getRegistryName() + "_blue"),
                new ResourceLocation(ModBlocks.ANCIENT_FABRIC.getRegistryName() + "_brown"),
                new ResourceLocation(ModBlocks.ANCIENT_FABRIC.getRegistryName() + "_green"),
                new ResourceLocation(ModBlocks.ANCIENT_FABRIC.getRegistryName() + "_red"),
                new ResourceLocation(ModBlocks.ANCIENT_FABRIC.getRegistryName() + "_black"));
    }

    private static void register(Item item) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0,
                new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    private static void register(Item item, int meta, String name) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta,
                new ModelResourceLocation(item.getRegistryName() + "_" + name, "inventory"));
    }

    public static void addCustomStateMappers() {
        StateMap map = new StateMap.Builder().ignore(BlockDoor.POWERED).build();

        ModelLoader.setCustomStateMapper(ModBlocks.GOLD_DOOR, map);
        ModelLoader.setCustomStateMapper(ModBlocks.QUARTZ_DOOR, map);
        ModelLoader.setCustomStateMapper(ModBlocks.GOLD_DIMENSIONAL_DOOR, map);
        ModelLoader.setCustomStateMapper(ModBlocks.IRON_DIMENSIONAL_DOOR, map);
        ModelLoader.setCustomStateMapper(ModBlocks.PERSONAL_DIMENSIONAL_DOOR, map);
        ModelLoader.setCustomStateMapper(ModBlocks.WARP_DIMENSIONAL_DOOR, map);

        ModelLoader.setCustomStateMapper(ModBlocks.DIMENSIONAL_PORTAL, new StateMap.Builder().ignore(BlockDoor.FACING, BlockDoor.HALF, BlockDoor.HINGE, BlockDoor.OPEN, BlockDoor.POWERED).build());
    }
}
