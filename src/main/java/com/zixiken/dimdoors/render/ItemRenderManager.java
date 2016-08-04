package com.zixiken.dimdoors.render;

import com.zixiken.dimdoors.DimDoors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import static net.minecraft.item.Item.getItemFromBlock;

public class ItemRenderManager {
    private static final String ID = DimDoors.MODID;

    public static void registerItemRenderers() {
        //ItemBlock registration
        register(getItemFromBlock(DimDoors.blockDimWall));
        register(getItemFromBlock(DimDoors.blockDimWall), 1, "Ancient");
        register(getItemFromBlock(DimDoors.blockDimWall), 2, "Altered");

        register(getItemFromBlock(DimDoors.blockDimWallPerm));
        register(getItemFromBlock(DimDoors.blockLimbo));
        register(getItemFromBlock(DimDoors.blockRift));

        //Item registration
        register(DimDoors.itemDDKey);
        register(DimDoors.itemDimensionalDoor);
        register(DimDoors.itemGoldenDimensionalDoor);
        register(DimDoors.itemGoldenDoor);
        register(DimDoors.itemPersonalDoor);
        register(DimDoors.itemQuartzDoor);
        register(DimDoors.itemRiftBlade);
        register(DimDoors.itemRiftRemover);
        register(DimDoors.itemRiftSignature);
        register(DimDoors.itemStabilizedRiftSignature);
    }

    public static void addModelVariants() {
        ModelBakery.registerItemVariants(getItemFromBlock(DimDoors.blockDimWall),
                new ResourceLocation(ID + ":blockDimWall"),
                new ResourceLocation(ID + ":blockDimWallAncient"),
                new ResourceLocation(ID + ":blockDimWallAltered"));
    }

    private static void register(Item item) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0,
                new ModelResourceLocation(ID + ':' + item.getUnlocalizedName().substring(5), "inventory"));
    }

    private static void register(Item item, int meta, String name) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta,
                new ModelResourceLocation(ID + ':' + item.getUnlocalizedName().substring(5) + name, "inventory"));
    }
}
