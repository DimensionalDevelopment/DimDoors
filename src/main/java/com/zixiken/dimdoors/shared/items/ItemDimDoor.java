package com.zixiken.dimdoors.shared.items;

import java.util.List;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.blocks.BlockDimDoor;
import com.zixiken.dimdoors.shared.blocks.BlockDimDoorBase;
import com.zixiken.dimdoors.shared.blocks.ModBlocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import static com.zixiken.dimdoors.DimDoors.translateAndAdd;

public class ItemDimDoor extends ItemDoorBase {

    public ItemDimDoor() {
        super(ModBlocks.DIMENSIONAL_DOOR, (ItemDoor) Items.IRON_DOOR);
        setUnlocalizedName(BlockDimDoor.ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, BlockDimDoor.ID));
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        translateAndAdd("info.dimensional_door", tooltip);
    }

    @Override
    protected BlockDimDoorBase getDoorBlock() {
        return ModBlocks.DIMENSIONAL_DOOR;
    }
}
