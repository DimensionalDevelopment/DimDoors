package org.dimdev.dimdoors.shared.items;

import java.util.List;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.BlockDimensionalDoorGold;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.ddutils.I18nUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDimensionalDoorGold extends ItemDoor {

    public ItemDimensionalDoorGold() {
        super(ModBlocks.GOLD_DIMENSIONAL_DOOR);
        setCreativeTab(DimDoors.DIM_DOORS_CREATIVE_TAB);
        setUnlocalizedName(BlockDimensionalDoorGold.ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, BlockDimensionalDoorGold.ID));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.addAll(I18nUtils.translateMultiline("info.gold_dimensional_door"));
    }
}
