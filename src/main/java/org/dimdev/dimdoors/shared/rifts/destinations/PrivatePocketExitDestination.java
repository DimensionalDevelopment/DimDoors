package org.dimdev.dimdoors.shared.rifts.destinations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import org.dimdev.ddutils.*;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.pockets.Pocket;
import org.dimdev.dimdoors.shared.pockets.PocketRegistry;
import org.dimdev.dimdoors.shared.rifts.RiftDestination;
import org.dimdev.dimdoors.shared.rifts.TileEntityRift;
import org.dimdev.dimdoors.shared.rifts.registry.RiftRegistry;
import org.dimdev.dimdoors.shared.world.ModDimensions;
import org.dimdev.dimdoors.shared.world.limbodimension.WorldProviderLimbo;
import org.dimdev.dimdoors.shared.world.pocketdimension.WorldProviderPersonalPocket;

import java.util.UUID;

@Getter @AllArgsConstructor @Builder(toBuilder = true) @ToString
public class PrivatePocketExitDestination extends RiftDestination {
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
    public boolean teleport(RotatedLocation loc, Entity entity) {
        Location destLoc;
        UUID uuid = EntityUtils.getEntityOwnerUUID(entity);
        if (uuid != null) {
            PocketRegistry privatePocketRegistry = PocketRegistry.instance(ModDimensions.getPrivateDim());
            destLoc = RiftRegistry.instance().getPrivatePocketExit(uuid);
            if (loc.getLocation().getWorld().provider instanceof WorldProviderPersonalPocket && privatePocketRegistry.getPrivatePocketID(uuid) == privatePocketRegistry.posToID(loc.getLocation().getPos())) {
                RiftRegistry.instance().setLastPrivatePocketEntrance(uuid, loc.getLocation()); // Remember which exit was used for next time the pocket is entered
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
                ((TileEntityRift) destLoc.getTileEntity()).teleportTo(entity, loc.getYaw(), loc.getPitch());
                return true;
            }
        } else {
            return false; // Non-player/owned entity tried to escape/leave private pocket
        }
    }

    @Override
    public void register(Location location) {
        super.register(location);
        PocketRegistry privatePocketRegistry = PocketRegistry.instance(location.getDim());
        Pocket pocket = privatePocketRegistry.getPocketAt(location.getPos());
        RiftRegistry.instance().addPocketEntrance(pocket, location);
    }

    @Override
    public RGBA getColor(Location location) {
        return new RGBA(0, 1, 0, 1);
    }
}
