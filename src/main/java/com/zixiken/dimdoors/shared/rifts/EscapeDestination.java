package com.zixiken.dimdoors.shared.rifts;

import ddutils.EntityUtils;
import ddutils.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

@Getter @AllArgsConstructor @Builder(toBuilder = true)
public class EscapeDestination extends RiftDestination {
    //public EscapeDestination() {}

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
        String uuid = EntityUtils.getEntityOwnerUUID(entity);
        if (uuid != null) {
            Location destLoc = RiftRegistry.getOverworldRift(uuid);
            RiftRegistry.setOverworldRift(uuid, null); // forget the last used escape rift
            // TODO: teleport the player to random coordinates based on depth around destLoc
            return true;
        } else {
            return false; // Non-player/owned entity tried to escape/leave private pocket
        }
    }
}
