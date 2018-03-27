package org.dimdev.dimdoors.shared.items;

import java.util.List;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.BlockDimensionalDoorIron;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.ddutils.I18nUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.dimdoors.shared.rifts.destinations.PublicPocketDestination;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;

public class ItemDimensionalDoorIron extends ItemDimensionalDoor {

    public ItemDimensionalDoorIron() {
        super(ModBlocks.IRON_DIMENSIONAL_DOOR);
        setCreativeTab(DimDoors.DIM_DOORS_CREATIVE_TAB);
        setUnlocalizedName(BlockDimensionalDoorIron.ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, BlockDimensionalDoorIron.ID));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.addAll(I18nUtils.translateMultiline("info.iron_dimensional_door"));
    }

    @Override
    public void setupRift(TileEntityEntranceRift rift) {
        PublicPocketDestination destination = PublicPocketDestination.builder().build();
        rift.setDestination(destination);
    }

    @Override
    public boolean canBePlacedOnRift() {
        return true;
    }
}
