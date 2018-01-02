package org.dimdev.dimdoors.shared.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.dimdev.dimdoors.DimDoors;

public class ItemRiftConnectionTool extends Item {

    public static final String ID = "rift_connection_tool";

    ItemRiftConnectionTool() {
        setMaxStackSize(1);
        setMaxDamage(16);
        setCreativeTab(DimDoors.DIM_DOORS_CREATIVE_TAB);
        setUnlocalizedName(ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        // TODO: reimplement this using the new registry system (open a GUI that allows configuring the rift)
        ItemStack stack = playerIn.getHeldItem(handIn);
        return new ActionResult<>(EnumActionResult.FAIL, stack);
    }
}
