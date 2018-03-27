package org.dimdev.dimdoors.shared.items;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.BlockDimensionalTrapdoorWood;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.ddutils.I18nUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.dimdoors.shared.rifts.destinations.EscapeDestination;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;

import java.util.List;


public class ItemDimensionalTrapdoorWood extends ItemDimensionalTrapdoor {

    public ItemDimensionalTrapdoorWood() {
        super(ModBlocks.WOOD_DIMENSIONAL_TRAPDOOR);
        setCreativeTab(DimDoors.DIM_DOORS_CREATIVE_TAB);
        setUnlocalizedName(BlockDimensionalTrapdoorWood.ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, BlockDimensionalTrapdoorWood.ID));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.addAll(I18nUtils.translateMultiline("info.dimensional_trapdoor"));
    }

    @Override
    public void setupRift(TileEntityEntranceRift rift) {
        rift.setDestination(new EscapeDestination());
    }
}
