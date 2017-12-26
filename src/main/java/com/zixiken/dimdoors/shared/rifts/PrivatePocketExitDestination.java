package com.zixiken.dimdoors.shared.rifts;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.pockets.PocketRegistry;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;
import com.zixiken.dimdoors.shared.world.pocketdimension.WorldProviderPersonalPocket;
import ddutils.EntityUtils;
import ddutils.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

@Getter @AllArgsConstructor @Builder(toBuilder = true)
public class PrivatePocketExitDestination extends RiftDestination { // TODO: merge into PocketExit or Escape?
    //public PrivatePocketExitDestination() {}

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt = super.writeToNBT(nbt);
        return nbt;
    }

    @Override
    public boolean teleport(TileEntityRift rift, Entity entity) { // TODO: don't use overworld rift, use last used entrance rift
        Location destLoc;
        String uuid = EntityUtils.getEntityOwnerUUID(entity);
        if (uuid != null) {
            PocketRegistry privatePocketRegistry = PocketRegistry.getForDim(DimDoorDimensions.getPrivateDimID());
            RiftRegistry privateRiftRegistry = RiftRegistry.getForDim(DimDoorDimensions.getPrivateDimID());
            destLoc = RiftRegistry.getOverworldRift(uuid);
            RiftRegistry.setOverworldRift(uuid, null); // forget the last used overworld rift TODO: move this on dimension change event
            if (rift.getWorld().provider instanceof WorldProviderPersonalPocket && privatePocketRegistry.getPrivatePocketID(uuid) == privatePocketRegistry.posToID(rift.getPos())) {
                privateRiftRegistry.setPrivatePocketEntrance(uuid, rift.getLocation()); // Remember which exit was used for next time the pocket is entered
            }
            if (destLoc == null || !(destLoc.getTileEntity() instanceof TileEntityRift)) {
                if (entity instanceof EntityPlayer) DimDoors.chat((EntityPlayer) entity, "Either you did not enter using a rift or the rift you entered through no longer exists!");
                return false; // TODO: send to limbo
            }
        } else {
            return false; // Non-player/owned entity tried to escape/leave private pocket
        }
        ((TileEntityRift) destLoc.getTileEntity()).teleportTo(entity);
        return true;
    }
}
