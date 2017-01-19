package com.zixiken.dimdoors.blocks;

import com.zixiken.dimdoors.items.ModItems;
import com.zixiken.dimdoors.shared.Location;
import com.zixiken.dimdoors.shared.RiftRegistry;
import com.zixiken.dimdoors.tileentities.DDTileEntityBase;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockDimDoor extends BlockDimDoorBase {

    public static final String ID = "blockDimDoor";

    public BlockDimDoor() {
        super(Material.IRON);
        setHardness(1.0F);
        setResistance(2000.0F);
        setUnlocalizedName(ID);
        setRegistryName(ID);
    }

    @Override
    public void placeLink(Location location) {
        TileEntity te = location.getTileEntity();

        if(te != null && te instanceof DDTileEntityBase) {
            int id = ((DDTileEntityBase) te).riftID;
            RiftRegistry.Instance.pair(id, id);
        }
    }

    @Override
    public Item getItemDoor() {
        return ModItems.itemDimDoor;
    }
}
