package org.dimdev.dimdoors.shared.items;

import java.util.List;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.BlockDimensionalDoorQuartz;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.ddutils.I18nUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.dimdoors.shared.rifts.destinations.PrivateDestination;
import org.dimdev.dimdoors.shared.rifts.destinations.PrivatePocketExitDestination;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;
import org.dimdev.dimdoors.shared.world.pocketdimension.WorldProviderPersonalPocket;

public class ItemDimensionalDoorQuartz extends ItemDimensionalDoor {

    public ItemDimensionalDoorQuartz() {
        super(ModBlocks.PERSONAL_DIMENSIONAL_DOOR);
        setCreativeTab(DimDoors.DIM_DOORS_CREATIVE_TAB);
        setUnlocalizedName(BlockDimensionalDoorQuartz.ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, BlockDimensionalDoorQuartz.ID));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.addAll(I18nUtils.translateMultiline("info.quartz_dimensional_door"));
    }

    @Override
    public void setupRift(TileEntityEntranceRift rift) {
        if (rift.getWorld().provider instanceof WorldProviderPersonalPocket) {
            rift.setDestination(new PrivatePocketExitDestination()); // exit
        } else {
            rift.setDestination(new PrivateDestination()); // entrances
        }
    }

    @Override
    public boolean canBePlacedOnRift() {
        return true;
    }
}
