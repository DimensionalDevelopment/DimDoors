package org.dimdev.dimdoors.shared.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.ddutils.I18nUtils;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.BlockDimensionalDoorGold;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.dimdoors.shared.rifts.destinations.AvailableLinkDestination;
import org.dimdev.dimdoors.shared.rifts.registry.LinkProperties;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class ItemDimensionalDoorGold extends ItemDimensionalDoor {

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

    @Override
    public void setupRift(TileEntityEntranceRift rift) {
        rift.setProperties(LinkProperties.builder()
                .groups(new HashSet<>(Arrays.asList(0, 1)))
                .linksRemaining(1).build());
        rift.setDestination(AvailableLinkDestination.builder()
                .acceptedGroups(Collections.singleton(0))
                .coordFactor(1)
                .negativeDepthFactor(10000)
                .positiveDepthFactor(80)
                .weightMaximum(100)
                .noLink(false)
                .noLinkBack(false)
                .newRiftWeight(1).build());
    }

    @Override
    public boolean canBePlacedOnRift() {
        return true;
    }
}
