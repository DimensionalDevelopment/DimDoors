package com.zixiken.dimdoors.shared.rifts;

import com.zixiken.dimdoors.shared.pockets.Pocket;
import com.zixiken.dimdoors.shared.pockets.PocketGenerator;
import com.zixiken.dimdoors.shared.pockets.PocketRegistry;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;
import ddutils.EntityUtils;
import ddutils.Location;
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
            PocketRegistry privatePocketRegistry = PocketRegistry.getForDim(DimDoorDimensions.getPrivateDimID());
            RiftRegistry privateRiftRegistry = RiftRegistry.getForDim(DimDoorDimensions.getPrivateDimID());
            Pocket pocket = privatePocketRegistry.getPocket(privatePocketRegistry.getPrivatePocketID(uuid));
            if (pocket == null) { // generate the private pocket and get its entrance // TODO: use VirtualLocation.fromLoc vvv
                pocket = PocketGenerator.generatePrivatePocket(rift.virtualLocation != null ? rift.virtualLocation.toBuilder().depth(-2).build() : null); // set to where the pocket was first created
                pocket.setup();
                privatePocketRegistry.setPrivatePocketID(uuid, pocket.getId());
                ((TileEntityRift) pocket.getEntrance().getTileEntity()).teleportTo(entity);
                return true;
            } else {
                Location destLoc = privateRiftRegistry.getPrivatePocketEntrance(uuid); // get the last used entrance
                if (destLoc == null) destLoc = pocket.getEntrance(); // if there's none, then set the target to the main entrance
                if (destLoc == null) { // if the pocket entrance is gone, then create a new private pocket
                    pocket = PocketGenerator.generatePrivatePocket(rift.virtualLocation != null ? rift.virtualLocation.toBuilder().depth(-2).build() : null);
                    pocket.setup();
                    privatePocketRegistry.setPrivatePocketID(uuid, pocket.getId());
                    destLoc = pocket.getEntrance();
                }
                ((TileEntityRift) destLoc.getTileEntity()).teleportTo(entity);
                return true;
            }
            // privateRiftRegistry.setPrivatePocketEntrance(uuid, null); // --forget the last entered entrance-- Actually, remember it. We'll eventually store it only in the rift registry, not in the pocket.
        } else {
            return false; // TODO: There should be a way to get other entities into your private pocket, though. Add API for other mods.
        }
    }
}
