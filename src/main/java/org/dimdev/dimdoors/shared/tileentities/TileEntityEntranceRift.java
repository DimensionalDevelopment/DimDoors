package org.dimdev.dimdoors.shared.tileentities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dimdev.ddutils.nbt.NBTUtils;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.dimdoors.shared.rifts.TileEntityRift;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.RGBA;
import org.dimdev.ddutils.TeleportUtils;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import java.util.Random;

// TODO: merge horizontal and vertical entrances' render code into one, and support custom sizes
@NBTSerializable public class TileEntityEntranceRift extends TileEntityRift {
    @Saved @Getter /*package-private*/ boolean placeRiftOnBreak = false;
    @Saved @Getter /*package-private*/ boolean closeAfterPassThrough = false;
    @Saved @Getter public boolean shouldRender = true;
    @Saved @Getter public byte lockStatus = 0;

    // Set by the block, not saved and not synced to the client
    @Saved public EnumFacing orientation;
    @Saved public int tpOffset = 1; // TODO: float?
    @Saved public double extendUp = 0.5; // Use += to set these. TODO: @SideOnly client?
    @Saved public double extendDown = 0.5;
    @Saved public double extendLeft = 0.5;
    @Saved public double extendRight = 0.5;
    @Saved public double pushIn = 0.01; // TODO: set to 0, and set on door

    @Override
    public void copyFrom(TileEntityRift oldRift) {
        super.copyFrom(oldRift);
        if (oldRift instanceof TileEntityEntranceRift) {
            TileEntityEntranceRift oldEntranceRift = (TileEntityEntranceRift) oldRift;
            closeAfterPassThrough = oldEntranceRift.closeAfterPassThrough;
        }
        placeRiftOnBreak = true;
    }

    @Override public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); NBTUtils.readFromNBT(this, nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt = super.writeToNBT(nbt);
        return NBTUtils.writeToNBT(this, nbt);
    }

    @Override
    public void update() {

    }

    public void setPlaceRiftOnBreak(boolean placeRiftOnBreak) { this.placeRiftOnBreak = placeRiftOnBreak; markDirty(); }
    public void setShouldRender(boolean shouldRender) { this.shouldRender = shouldRender; markDirty(); }
    public void setLockStatus(byte lockStatus) { this.lockStatus = lockStatus; markDirty(); }
    public void setCloseAfterPassThrough(boolean closeAfterPassThrough) { this.closeAfterPassThrough = closeAfterPassThrough; markDirty(); }

    @Override
    public boolean teleport(Entity entity) {
        boolean status = super.teleport(entity);
        if (riftStateChanged && !alwaysDelete) {
            placeRiftOnBreak = true;
            markDirty();
        }
        return status;
    }

    @Override
    public void teleportTo(Entity entity, float fromYaw, float fromPitch) {
        if (relativeRotation) {
            TeleportUtils.teleport(entity, new Location(world, pos.offset(orientation, tpOffset)), orientation.getHorizontalAngle() + entity.rotationYaw - fromYaw, entity.rotationPitch - fromPitch);
        } else {
            TeleportUtils.teleport(entity, new Location(world, pos.offset(orientation, tpOffset)), orientation.getHorizontalAngle(), 0);
        }
    }
    @Override
    public void teleportTo(Entity entity) {
        TeleportUtils.teleport(entity, new Location(world, pos.offset(orientation, tpOffset)), orientation.getHorizontalAngle(), 0);
    }

    // Use vanilla behavior of refreshing only when block changes, not state (otherwise, opening the door would destroy the tile entity)
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        // newState is not accurate if we change the state during onBlockBreak
        newSate = world.getBlockState(pos);
        return oldState.getBlock() != newSate.getBlock();
    }

    public RGBA getEntranceRenderColor(Random rand) { // TODO: custom color
        float red, green, blue;
        switch(world.provider.getDimension()) {
            case -1: // Nether
                red = rand.nextFloat() * 0.5F + 0.4F;
                green = rand.nextFloat() * 0.05F;
                blue = rand.nextFloat() * 0.05F;
                break;
            default:
                red = rand.nextFloat() * 0.5F + 0.1F;
                green = rand.nextFloat() * 0.4F + 0.4F;
                blue = rand.nextFloat() * 0.6F + 0.5F;
                break;
        }
        return new RGBA(red, green, blue, 1);
    }

    @Override
    public boolean isFloating() {
        return false;
    }
}
