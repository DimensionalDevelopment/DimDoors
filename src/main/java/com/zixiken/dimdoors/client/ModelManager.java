package com.zixiken.dimdoors.client;

import com.zixiken.dimdoors.shared.blocks.ModBlocks;
import com.zixiken.dimdoors.shared.items.ModItems;
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
public class ModelManager {

    public static void registerModels() {
        //ItemBlock registration
        register(getItemFromBlock(ModBlocks.FABRIC), 0, "reality");
        register(getItemFromBlock(ModBlocks.FABRIC), 1, "ancient");
        register(getItemFromBlock(ModBlocks.FABRIC), 2, "altered");
        register(getItemFromBlock(ModBlocks.FABRIC), 3, "ancient_altered");
        register(getItemFromBlock(ModBlocks.FABRIC), 4, "unraveled");
        register(getItemFromBlock(ModBlocks.FABRIC), 5, "eternal");

        register(getItemFromBlock(ModBlocks.RIFT));
        register(getItemFromBlock(ModBlocks.DIMENSIONAL_TRAPDOOR));

        //Item registration
        register(ModItems.DIMENSIONAL_DOOR);
        register(ModItems.GOLD_DIMENSIONAL_DOOR);
        register(ModItems.GOLD_DOOR);
        register(ModItems.PERSONAL_DIMENSIONAL_DOOR);
        register(ModItems.QUARTZ_DOOR);
        register(ModItems.STABLE_FABRIC);
        register(ModItems.UNSTABLE_DIMENSIONAL_DOOR);
        register(ModItems.WARP_DIMENSIONAL_DOOR);
        register(ModItems.WORLD_THREAD);
        register(ModItems.RIFT_CONNECTION_TOOL);
        register(ModItems.RIFT_BLADE);
    }

    public static void registerModelVariants() {
        ModelBakery.registerItemVariants(ModItems.FABRIC, // we can't use getItemForBlock yet since items have not yet been registered (and this can't be run later)
                new ResourceLocation(ModBlocks.FABRIC.getRegistryName() + "_reality"),
                new ResourceLocation(ModBlocks.FABRIC.getRegistryName() + "_ancient"),
                new ResourceLocation(ModBlocks.FABRIC.getRegistryName() + "_altered"),
                new ResourceLocation(ModBlocks.FABRIC.getRegistryName() + "_ancient_altered"),
                new ResourceLocation(ModBlocks.FABRIC.getRegistryName() + "_unraveled"),
                new ResourceLocation(ModBlocks.FABRIC.getRegistryName() + "_eternal"));
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
        ModelLoader.setCustomStateMapper(ModBlocks.DIMENSIONAL_DOOR, map);
        ModelLoader.setCustomStateMapper(ModBlocks.PERSONAL_DIMENSIONAL_DOOR, map);
        ModelLoader.setCustomStateMapper(ModBlocks.UNSTABLE_DIMENSIONAL_DOOR, map);
        ModelLoader.setCustomStateMapper(ModBlocks.WARP_DIMENSIONAL_DOOR, map);

        ModelLoader.setCustomStateMapper(ModBlocks.TRANSIENT_DIMENSIONAL_DOOR, new StateMap.Builder().ignore(
                BlockDoor.FACING, BlockDoor.HALF, BlockDoor.HINGE, BlockDoor.OPEN, BlockDoor.POWERED).build());
    }
}
