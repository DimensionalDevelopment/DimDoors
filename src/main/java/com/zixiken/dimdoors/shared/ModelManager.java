package com.zixiken.dimdoors.shared;

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

import static net.minecraft.item.Item.getItemFromBlock;

@SuppressWarnings({"MethodCallSideOnly", "NewExpressionSideOnly"})
public class ModelManager {

    public static void registerModels() {
        //ItemBlock registration
        register(getItemFromBlock(ModBlocks.blockDimWall));
        register(getItemFromBlock(ModBlocks.blockDimWall), 1, "Ancient");
        register(getItemFromBlock(ModBlocks.blockDimWall), 2, "Altered");

        register(getItemFromBlock(ModBlocks.blockRift));
        register(getItemFromBlock(ModBlocks.blockDimHatch));

        //Item registration
        register(ModItems.itemDimDoor);
        register(ModItems.itemDimDoorGold);
        register(ModItems.itemDoorGold);
        register(ModItems.itemDimDoorPersonal);
        register(ModItems.itemDoorQuartz);
        register(ModItems.itemStableFabric);
        register(ModItems.itemDimDoorChaos);
        register(ModItems.itemDimDoorWarp);
        register(ModItems.itemWorldThread);
        register(ModItems.itemRiftConnectionTool);
        register(ModItems.itemRiftBlade);
    }

    public static void registerModelVariants() {
        ModelBakery.registerItemVariants(getItemFromBlock(ModBlocks.blockDimWall),
                ModBlocks.blockDimWall.getRegistryName(),
                new ResourceLocation(ModBlocks.blockDimWall.getRegistryName() + "Ancient"),
                new ResourceLocation(ModBlocks.blockDimWall.getRegistryName() + "Altered"));
    }

    private static void register(Item item) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0,
                new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    private static void register(Item item, int meta, String name) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta,
                new ModelResourceLocation(item.getRegistryName() + name, "inventory"));
    }

    @SuppressWarnings("LocalVariableDeclarationSideOnly")
    public static void addCustomStateMappers() {
        StateMap map = new StateMap.Builder().ignore(BlockDoor.POWERED).build();

        ModelLoader.setCustomStateMapper(ModBlocks.blockDoorGold, map);
        ModelLoader.setCustomStateMapper(ModBlocks.blockDoorQuartz, map);
        ModelLoader.setCustomStateMapper(ModBlocks.blockDimDoorGold, map);
        ModelLoader.setCustomStateMapper(ModBlocks.blockDimDoor, map);
        ModelLoader.setCustomStateMapper(ModBlocks.blockDimDoorPersonal, map);
        ModelLoader.setCustomStateMapper(ModBlocks.blockDimDoorChaos, map);
        ModelLoader.setCustomStateMapper(ModBlocks.blockDimDoorWarp, map);

        ModelLoader.setCustomStateMapper(ModBlocks.blockDimDoorTransient, new StateMap.Builder().ignore(
                BlockDoor.FACING, BlockDoor.HALF, BlockDoor.HINGE, BlockDoor.OPEN, BlockDoor.POWERED).build());
    }
}
