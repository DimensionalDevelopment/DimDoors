package org.dimdev.dimdoors.shared.rifts.destinations;

import org.dimdev.ddutils.RotatedLocation;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.rifts.RiftDestination;
import org.dimdev.dimdoors.shared.rifts.registry.RiftRegistry;
import org.dimdev.dimdoors.shared.tileentities.TileEntityRift;
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
import org.dimdev.pocketlib.VirtualLocation;

import java.util.UUID;

@Getter @AllArgsConstructor @Builder(toBuilder = true) @ToString
public class EscapeDestination extends RiftDestination {
    @Builder.Default boolean canEscapeLimbo = false;

    public EscapeDestination() {}

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
        if (!ModDimensions.isDimDoorsPocketDimension(entity.world) && !(entity.world.provider instanceof WorldProviderLimbo)) {
            DimDoors.sendTranslatedMessage(entity, "rifts.destinations.escape.not_in_pocket_dim");
            return false;
        }
        if (entity.world.provider instanceof WorldProviderLimbo && !canEscapeLimbo) {
            DimDoors.sendTranslatedMessage(entity, "rifts.destinations.escape.cannot_escape_limbo");
            return false;
        }

        UUID uuid = entity.getUniqueID();
        if (uuid != null) {
            Location destLoc = RiftRegistry.instance().getOverworldRift(uuid);
            if (destLoc != null && destLoc.getTileEntity() instanceof TileEntityRift || canEscapeLimbo) {
                TeleportUtils.teleport(entity, VirtualLocation.fromLocation(loc.getLocation()).projectToWorld(false));
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
