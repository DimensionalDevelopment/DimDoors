package org.dimdev.dimdoors.shared.rifts.destinations;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.pockets.Pocket;
import org.dimdev.dimdoors.shared.pockets.PocketGenerator;
import org.dimdev.dimdoors.shared.pockets.PocketRegistry;
import org.dimdev.dimdoors.shared.rifts.RiftDestination;
import org.dimdev.dimdoors.shared.rifts.RiftRegistry;
import org.dimdev.dimdoors.shared.rifts.TileEntityRift;
import org.dimdev.dimdoors.shared.world.ModDimensions;
import org.dimdev.ddutils.EntityUtils;
import org.dimdev.ddutils.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

@Getter @AllArgsConstructor @Builder(toBuilder = true) @ToString
public class PrivateDestination extends RiftDestination {
    //public PrivateDestination() {}

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
            PocketRegistry privatePocketRegistry = PocketRegistry.getForDim(ModDimensions.getPrivateDim());
            RiftRegistry privateRiftRegistry = RiftRegistry.getForDim(ModDimensions.getPrivateDim());
            Pocket pocket = privatePocketRegistry.getPocket(privatePocketRegistry.getPrivatePocketID(uuid));
            if (pocket == null) { // generate the private pocket and get its entrances
                pocket = PocketGenerator.generatePrivatePocket(rift.getVirtualLocation() != null ? rift.getVirtualLocation().toBuilder().depth(-1).build() : null); // set to where the pocket was first created
                pocket.setup();
                privatePocketRegistry.setPrivatePocketID(uuid, pocket.getId());
                ((TileEntityRift) pocket.getEntrance().getTileEntity()).teleportTo(entity);
                privateRiftRegistry.setPrivatePocketExit(uuid, rift.getLocation());
                return true;
            } else {
                Location destLoc = privateRiftRegistry.getPrivatePocketEntrance(uuid); // get the last used entrances
                if (destLoc == null) destLoc = pocket.getEntrance(); // if there's none, then set the target to the main entrances
                if (destLoc == null) { // if the pocket entrances is gone, then create a new private pocket
                    DimDoors.log.info("All entrances are gone, creating a new private pocket!");
                    pocket = PocketGenerator.generatePrivatePocket(rift.getVirtualLocation() != null ? rift.getVirtualLocation().toBuilder().depth(-1).build() : null);
                    pocket.setup();
                    privatePocketRegistry.setPrivatePocketID(uuid, pocket.getId());
                    destLoc = pocket.getEntrance();
                }
                ((TileEntityRift) destLoc.getTileEntity()).teleportTo(entity);
                privateRiftRegistry.setPrivatePocketExit(uuid, rift.getLocation());
                return true;
            }
        } else {
            return false; // TODO: There should be a way to get other entities into your private pocket, though. Add API for other mods.
        }
    }
}
