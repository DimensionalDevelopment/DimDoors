package org.dimdev.dimdoors.shared.rifts.targets;

import org.dimdev.ddutils.RGBA;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.pocketlib.PrivatePocketData;
import org.dimdev.pocketlib.VirtualLocation;
import org.dimdev.pocketlib.Pocket;
import org.dimdev.dimdoors.shared.pockets.PocketGenerator;
import org.dimdev.dimdoors.shared.rifts.registry.RiftRegistry;
import org.dimdev.ddutils.EntityUtils;
import org.dimdev.ddutils.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Objects;
import java.util.UUID;

@Getter @AllArgsConstructor @Builder(toBuilder = true) @ToString
public class PrivatePocketTarget extends VirtualTarget implements IEntityTarget {
    //public PrivateDestination() {}

    @Override public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { nbt = super.writeToNBT(nbt); return nbt; }

    @Override
    public boolean receiveEntity(Entity entity, float relativeYaw, float relativePitch) {
        UUID uuid = EntityUtils.getEntityOwnerUUID(entity);
        VirtualLocation virtualLocation = VirtualLocation.fromLocation(location);
        if (Objects.nonNull(uuid) && Objects.nonNull(virtualLocation)) {
            Pocket pocket = PrivatePocketData.instance().getPrivatePocket(uuid);
            if (Objects.isNull(pocket)) { // generate the private pocket and get its entrances
                // set to where the pocket was first created
                pocket = PocketGenerator.generatePrivatePocket(virtualLocation.toBuilder().depth(-1).build());
                PrivatePocketData.instance().setPrivatePocketID(uuid, pocket);
                if(!((IEntityTarget)RiftRegistry.instance().getPocketEntrance(pocket).getTileEntity()).receiveEntity(entity,relativeYaw,relativePitch))
                    return false; //Return false if the teleport fails
            } else {
                Location destLoc = RiftRegistry.instance().getPrivatePocketEntrance(uuid); // get the last used entrances
                if (Objects.isNull(destLoc))
                    destLoc = RiftRegistry.instance().getPocketEntrance(pocket); // if there's none, then set the target to the main entrances
                if (Objects.isNull(destLoc)) { // if the pocket entrances is gone, then create a new private pocket
                    DimDoors.log.info("All entrances are gone, creating a new private pocket!");
                    pocket = PocketGenerator.generatePrivatePocket(virtualLocation.toBuilder().depth(-1).build());
                    PrivatePocketData.instance().setPrivatePocketID(uuid, pocket);
                    destLoc = RiftRegistry.instance().getPocketEntrance(pocket);
                }
                if(!((IEntityTarget) destLoc.getTileEntity()).receiveEntity(entity, relativeYaw, relativePitch))
                    return false; //Return false if the teleport fails
            }
            RiftRegistry.instance().setLastPrivatePocketExit(uuid, location);
            return true;
        } return false;
    }

    @Override
    public RGBA getColor() {
        return new RGBA(0, 1, 0, 1);
    }
}
