package org.dimdev.dimdoors.shared.rifts.destinations;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.rifts.RiftDestination;
import org.dimdev.dimdoors.shared.rifts.RiftRegistry;
import org.dimdev.dimdoors.shared.rifts.TileEntityRift;
import org.dimdev.dimdoors.shared.world.ModDimensions;
import org.dimdev.dimdoors.shared.world.limbodimension.WorldProviderLimbo;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.TeleportUtils;
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
        if (!ModDimensions.isDimDoorsPocketDimension(entity.world)) {
            DimDoors.chat(entity, "Can't escape from a non-pocket dimension!");
            return false;
        }
        String uuid = entity.getCachedUniqueIdString();
        if (uuid != null) {
            Location destLoc = RiftRegistry.getOverworldRift(uuid);
            if (destLoc != null && destLoc.getTileEntity() instanceof TileEntityRift) {
                //TeleportUtils.teleport(entity, new VirtualLocation(destLoc, rift.virtualLocation.getDepth()).projectToWorld()); // TODO
                // TODO
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
