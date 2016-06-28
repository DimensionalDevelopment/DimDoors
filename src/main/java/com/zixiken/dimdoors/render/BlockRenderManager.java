package com.zixiken.dimdoors.render;

import com.zixiken.dimdoors.DimDoors;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class BlockRenderManager {
    private static final String ID = DimDoors.MODID;

    public static void registerBlockRenderers() {
        register(DimDoors.blockDimWall);
        register(DimDoors.blockDimWall, 1, "Ancient");
        register(DimDoors.blockDimWall, 2, "Altered");
    }

    public static void addModelVariants() {
        ModelBakery.registerItemVariants(Item.getItemFromBlock(DimDoors.blockDimWall),
                new ResourceLocation(ID + ":blockDimWall"),
                new ResourceLocation(ID + ":blockDimWallAncient"),
                new ResourceLocation(ID + ":blockDimWallAltered"));
    }

    private static void register(Block block) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
                .register(Item.getItemFromBlock(block), 0,
                new ModelResourceLocation(ID + ':' + block.getUnlocalizedName().substring(5)));
    }

    private static void register(Block block, int meta, String name) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
                .register(Item.getItemFromBlock(block), meta, new ModelResourceLocation(ID + ':' +
                block.getUnlocalizedName().substring(5) + name, "inventory"));
    }
}
