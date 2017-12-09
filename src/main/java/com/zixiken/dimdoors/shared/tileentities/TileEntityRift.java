package com.zixiken.dimdoors.shared.tileentities;

import com.zixiken.dimdoors.DimDoors;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class TileEntityRift extends TileEntity implements ITickable {

    public boolean isTeleporting = false;
    public Entity teleportingEntity;

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        // TODO
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        // TODO

        return nbt;
    }

    public void copyFrom(TileEntityRift otherRift) {
        // TODO

        markDirty();
    }

    public boolean teleport(Entity entity) {
        // TODO: this function will handle all the teleportation logic for all rifts, including
        return false;
    }

    @Override
    public void update() {
        if (isTeleporting && teleportingEntity != null) {
            if (!teleport(teleportingEntity)) {
                if (teleportingEntity instanceof EntityPlayer) {
                    EntityPlayer entityPlayer = (EntityPlayer) teleportingEntity;
                    DimDoors.chat(entityPlayer, "Teleporting failed, but since mod is still in alpha, stuff like that might simply happen.");
                    // TODO: It's normal for teleportation to sometimes fail, for example for an unlinked warp door. Change this to an exception-based system to print error only when it really fails?
                }
            }
            isTeleporting = false;
            teleportingEntity = null;
        }
    }
}
