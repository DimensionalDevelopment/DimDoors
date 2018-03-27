package org.dimdev.dimdoors.shared.items;

import java.util.Collections;
import java.util.List;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.BlockDimensionalDoorWood;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.ddutils.I18nUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.dimdoors.shared.rifts.destinations.AvailableLinkDestination;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;

public class ItemDimensionalDoorWood extends ItemDimensionalDoor {

    public ItemDimensionalDoorWood() {
        super(ModBlocks.WARP_DIMENSIONAL_DOOR);
        setCreativeTab(DimDoors.DIM_DOORS_CREATIVE_TAB);
        setUnlocalizedName(BlockDimensionalDoorWood.ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, BlockDimensionalDoorWood.ID));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.addAll(I18nUtils.translateMultiline("info.oak_dimensional_door"));
    }

    @Override
    public void setupRift(TileEntityEntranceRift rift) {
        rift.setDestination(AvailableLinkDestination.builder()
                .acceptedGroups(Collections.singleton(0))
                .coordFactor(1)
                .negativeDepthFactor(80)
                .positiveDepthFactor(Double.MAX_VALUE)
                .weightMaximum(100)
                .noLink(false).newRiftWeight(0).build());
    }

    @Override
    public boolean canBePlacedOnRift() {
        return true;
    }
}
