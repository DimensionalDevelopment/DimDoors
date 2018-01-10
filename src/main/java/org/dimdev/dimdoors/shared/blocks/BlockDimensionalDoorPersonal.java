package org.dimdev.dimdoors.shared.blocks;

import net.minecraft.block.state.IBlockState;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.items.ModItems;
import org.dimdev.dimdoors.shared.rifts.destinations.PrivateDestination;
import org.dimdev.dimdoors.shared.rifts.destinations.PrivatePocketExitDestination;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;
import org.dimdev.dimdoors.shared.world.pocketdimension.WorldProviderPersonalPocket;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import java.util.Random;

public class BlockDimensionalDoorPersonal extends BlockDimensionalDoor {

    public static final String ID = "quartz_dimensional_door";

    public BlockDimensionalDoorPersonal() {
        super(Material.ROCK);
        setHardness(0.1F);
        setUnlocalizedName(ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
    }

    @Override
    public Item getItem() {
        return ModItems.PERSONAL_DIMENSIONAL_DOOR;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return ModBlocks.QUARTZ_DOOR.getItemDropped(state, rand, fortune);
    }

    @Override
    public void setupRift(TileEntityEntranceRift rift) {
        if (rift.getWorld().provider instanceof WorldProviderPersonalPocket) {
            rift.setSingleDestination(new PrivatePocketExitDestination()); // exit
        } else {
            rift.setSingleDestination(new PrivateDestination()); // entrances
        }
        rift.setChaosWeight(0); // TODO: generated schematic exits too
    }

    @Override
    public boolean canBePlacedOnRift() {
        return false;
    }
}
