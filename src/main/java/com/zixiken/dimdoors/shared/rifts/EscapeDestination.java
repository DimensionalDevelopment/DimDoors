package com.zixiken.dimdoors.shared.rifts;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.VirtualLocation;
import com.zixiken.dimdoors.shared.world.limbodimension.WorldProviderLimbo;
import ddutils.Location;
import ddutils.TeleportUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

@Getter @AllArgsConstructor @Builder(toBuilder = true) @ToString
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
        String uuid = entity.getCachedUniqueIdString();
        if (uuid != null) {
            Location destLoc = RiftRegistry.getOverworldRift(uuid);
            if (destLoc != null && destLoc.getTileEntity() instanceof TileEntityRift) {
                TeleportUtils.teleport(entity, new VirtualLocation(destLoc, rift.virtualLocation.getDepth()).projectToWorld()); // TODO
                return true;
            } else {
                if (destLoc == null) {
                    DimDoors.chat(entity, "You didn't use a rift to enter so you ended up in Limbo!"); // TODO: better messages, localization
                } else {
                    DimDoors.chat(entity, "The rift you used to enter has closed so you ended up in Limbo!");
                }
                TeleportUtils.teleport(entity, WorldProviderLimbo.getLimboSkySpawn(entity)); // TODO: do we really want to spam limbo with items?
                return true;
            }
        } else {
            return false; // No escape info for that entity
        }
    }
}
