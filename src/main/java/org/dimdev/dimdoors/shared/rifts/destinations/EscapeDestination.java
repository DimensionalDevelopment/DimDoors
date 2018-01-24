package org.dimdev.dimdoors.shared.rifts.destinations;

import org.dimdev.ddutils.RotatedLocation;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.rifts.RiftDestination;
import org.dimdev.dimdoors.shared.rifts.registry.RiftRegistry;
import org.dimdev.dimdoors.shared.rifts.TileEntityRift;
import org.dimdev.dimdoors.shared.world.ModDimensions;
import org.dimdev.dimdoors.shared.world.limbo.WorldProviderLimbo;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.TeleportUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

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
    public boolean teleport(RotatedLocation loc, Entity entity) {
        if (!ModDimensions.isDimDoorsPocketDimension(entity.world)) {
            if (entity.world.provider instanceof WorldProviderLimbo) {
                DimDoors.sendTranslatedMessage(entity, "rifts.destinations.escape.cannot_escape_limbo");
            } else {
                DimDoors.sendTranslatedMessage(entity, "rifts.destinations.escape.not_in_pocket_dim");
            }
            return false;
        }
        UUID uuid = entity.getUniqueID();
        if (uuid != null) {
            Location destLoc = RiftRegistry.instance().getOverworldRift(uuid);
            if (destLoc != null && destLoc.getTileEntity() instanceof TileEntityRift) {
                //TeleportUtils.teleport(entity, new VirtualLocation(destLoc, rift.virtualLocation.getDepth()).projectToWorld()); // TODO
                return true;
            } else {
                if (destLoc == null) {
                    DimDoors.sendTranslatedMessage(entity, "rifts.destinations.escape.did_not_use_rift");
                } else {
                    DimDoors.sendTranslatedMessage(entity, "rifts.destinations.escape.rift_has_closed");
                }
                TeleportUtils.teleport(entity, WorldProviderLimbo.getLimboSkySpawn(entity));
                return true;
            }
        } else {
            return false; // No escape info for that entity
        }
    }
}
