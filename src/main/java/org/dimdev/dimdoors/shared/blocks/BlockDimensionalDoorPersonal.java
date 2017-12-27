package org.dimdev.dimdoors.shared.blocks;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.items.ModItems;
import org.dimdev.dimdoors.shared.rifts.PrivateDestination;
import org.dimdev.dimdoors.shared.rifts.PrivatePocketExitDestination;
import org.dimdev.dimdoors.shared.rifts.RiftDestination;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;
import org.dimdev.dimdoors.shared.world.pocketdimension.WorldProviderPersonalPocket;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

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
    public void setupRift(TileEntityEntranceRift rift) {
        if (rift.getWorld().provider instanceof WorldProviderPersonalPocket) {
            rift.setSingleDestination(new PrivatePocketExitDestination()); // exit
        } else {
            rift.setSingleDestination(new PrivateDestination()); // entrances
        }
        rift.setChaosWeight(0); // TODO: generated schematic exits too
    }
}
