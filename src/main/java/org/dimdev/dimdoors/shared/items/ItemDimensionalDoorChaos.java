package org.dimdev.dimdoors.shared.items;

import java.util.List;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.ddutils.I18nUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;

public class ItemDimensionalDoorChaos extends ItemDimensionalDoor { // TODO: remove this when converting to NBT setup, and just add creative menu item?

    public ItemDimensionalDoorChaos() {
        super(ModBlocks.IRON_DIMENSIONAL_DOOR);
        setCreativeTab(DimDoors.DIM_DOORS_CREATIVE_TAB);
        setUnlocalizedName("chaos_dimensional_door");
        setRegistryName(new ResourceLocation(DimDoors.MODID, "chaos_dimensional_door"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.addAll(I18nUtils.translateMultiline("info.chaos_dimensional_door"));
    }

    @Override
    public void setupRift(TileEntityEntranceRift rift) {
        // TODO
    }

    @Override
    public boolean canBePlacedOnRift() {
        return false;
    }
}
