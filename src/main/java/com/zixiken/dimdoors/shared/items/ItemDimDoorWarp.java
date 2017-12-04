package com.zixiken.dimdoors.shared.items;

import java.util.List;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.blocks.BlockDimDoorBase;
import com.zixiken.dimdoors.shared.blocks.BlockDimDoorWarp;
import com.zixiken.dimdoors.shared.blocks.ModBlocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import static com.zixiken.dimdoors.DimDoors.translateAndAdd;

public class ItemDimDoorWarp extends ItemDoorBase {

    public ItemDimDoorWarp() {
        super(ModBlocks.WARP_DIMENSIONAL_DOOR, (ItemDoor) Items.OAK_DOOR);
        setUnlocalizedName(BlockDimDoorWarp.ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, BlockDimDoorWarp.ID));
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
        translateAndAdd("info.warp_dimensional_door", tooltip);
    }

    @Override
    protected BlockDimDoorBase getDoorBlock() {
        return ModBlocks.WARP_DIMENSIONAL_DOOR;
    }
}
