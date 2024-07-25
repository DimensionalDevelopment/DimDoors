package org.dimdev.dimdoors.shared.rifts.targets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import org.dimdev.ddutils.EntityUtils;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.RGBA;
import org.dimdev.ddutils.TeleportUtils;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.rifts.registry.RiftRegistry;
import org.dimdev.dimdoors.shared.tileentities.TileEntityRift;
import org.dimdev.dimdoors.shared.world.limbo.WorldProviderLimbo;
import org.dimdev.dimdoors.shared.world.pocketdimension.WorldProviderPersonalPocket;
import org.dimdev.pocketlib.Pocket;
import org.dimdev.pocketlib.PocketRegistry;
import org.dimdev.pocketlib.PrivatePocketData;

import java.util.UUID;

@Getter @AllArgsConstructor @Builder(toBuilder = true) @ToString
public class PrivatePocketExitTarget extends VirtualTarget implements IEntityTarget {
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
    public boolean receiveEntity(Entity entity, float relativeYaw, float relativePitch) {
        Location destLoc;
        UUID uuid = EntityUtils.getEntityOwnerUUID(entity);
        if (uuid != null) {
            destLoc = RiftRegistry.instance().getPrivatePocketExit(uuid);
            Pocket pocket = PrivatePocketData.instance().getPrivatePocket(uuid);
            if (location.getWorld().provider instanceof WorldProviderPersonalPocket && pocket != null && PocketRegistry.instance(pocket.getDim()).getPocketAt(location.pos).equals(pocket)) {
                RiftRegistry.instance().setLastPrivatePocketEntrance(uuid, location); // Remember which exit was used for next time the pocket is entered
            }
            if (destLoc == null || !(destLoc.getTileEntity() instanceof TileEntityRift)) {
                if (destLoc == null) {
                    DimDoors.sendTranslatedMessage(entity, "rifts.destinations.private_pocket_exit.did_not_use_rift");
                } else {
                    DimDoors.sendTranslatedMessage(entity, "rifts.destinations.private_pocket_exit.rift_has_closed");
                }
                TeleportUtils.teleport(entity, WorldProviderLimbo.getLimboSkySpawn(entity));
                return false;
            } else return ((IEntityTarget)destLoc.getTileEntity()).receiveEntity(entity,relativeYaw,relativePitch);
        } else return false; // Non-player/owned entity tried to escape/leave private pocket
    }

    @Override
    public void register() {
        super.register();
        PocketRegistry privatePocketRegistry = PocketRegistry.instance(location.getDim());
        Pocket pocket = privatePocketRegistry.getPocketAt(location.getPos());
        RiftRegistry.instance().addPocketEntrance(pocket, location);
    }

    @Override
    public RGBA getColor() {
        return new RGBA(0, 1, 0, 1);
    }
}
