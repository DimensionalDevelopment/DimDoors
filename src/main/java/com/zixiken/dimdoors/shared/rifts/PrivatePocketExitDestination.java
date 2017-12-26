package com.zixiken.dimdoors.shared.rifts;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.pockets.Pocket;
import com.zixiken.dimdoors.shared.pockets.PocketRegistry;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;
import com.zixiken.dimdoors.shared.world.limbodimension.WorldProviderLimbo;
import com.zixiken.dimdoors.shared.world.pocketdimension.WorldProviderPersonalPocket;
import ddutils.EntityUtils;
import ddutils.Location;
import ddutils.TeleportUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

@Getter @AllArgsConstructor @Builder(toBuilder = true) @ToString
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
    public boolean teleport(TileEntityRift rift, Entity entity) {
        Location destLoc;
        String uuid = EntityUtils.getEntityOwnerUUID(entity);
        if (uuid != null) {
            PocketRegistry privatePocketRegistry = PocketRegistry.getForDim(DimDoorDimensions.getPrivateDimID());
            RiftRegistry privateRiftRegistry = RiftRegistry.getForDim(DimDoorDimensions.getPrivateDimID());
            destLoc = privateRiftRegistry.getPrivatePocketExit(uuid);
            if (rift.getWorld().provider instanceof WorldProviderPersonalPocket && privatePocketRegistry.getPrivatePocketID(uuid) == privatePocketRegistry.posToID(rift.getPos())) {
                privateRiftRegistry.setPrivatePocketEntrance(uuid, rift.getLocation()); // Remember which exit was used for next time the pocket is entered
            }
            if (destLoc == null || !(destLoc.getTileEntity() instanceof TileEntityRift)) {
                if (destLoc == null) {
                    DimDoors.chat(entity, "You did not use a rift to enter this pocket so you ended up in limbo!");
                } else {
                    DimDoors.chat(entity, "The rift you entered through no longer exists so you ended up in limbo!");
                }
                TeleportUtils.teleport(entity, WorldProviderLimbo.getLimboSkySpawn(entity));
                return false;
            } else {
                ((TileEntityRift) destLoc.getTileEntity()).teleportTo(entity);
                return true;
            }
        } else {
            return false; // Non-player/owned entity tried to escape/leave private pocket
        }
    }

    @Override
    public void register(TileEntityRift rift) {
        PocketRegistry privatePocketRegistry = PocketRegistry.getForDim(rift.getLocation().getDim());
        Pocket pocket = privatePocketRegistry.getPocketAt(rift.getPos());
        String uuid = privatePocketRegistry.getPrivatePocketOwner(pocket.getId());
        if (uuid != null) {
            RiftRegistry.getForDim(DimDoorDimensions.getPrivateDimID()).addPrivatePocketEntrance(uuid, rift.getLocation());
        }
    }

    // TODO: unregister
}
