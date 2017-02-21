/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared.tileentities;

import com.zixiken.dimdoors.shared.EnumPocketType;
import com.zixiken.dimdoors.shared.PocketRegistry;
import com.zixiken.dimdoors.shared.RiftRegistry;
import com.zixiken.dimdoors.shared.TeleportHelper;
import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

/**
 *
 * @author Robijnvogel
 */
public class TileEntityDimDoorPersonal extends TileEntityDimDoor {

    public TileEntityDimDoorPersonal() {
        canRiftBePaired = false;
    }

    @Override
    public boolean tryTeleport(Entity entity) { //this door is never paired
        Location locationOfThisRift = RiftRegistry.Instance.getRiftLocation(this.riftID);
        if (entity instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer) entity;
            if (locationOfThisRift.getDimensionID() == DimDoorDimensions.getPocketDimensionType(EnumPocketType.PRIVATE).getId()) {
                return TeleportHelper.teleport(entity, PocketRegistry.Instance.getPocket(this.pocketID, EnumPocketType.PRIVATE).getDepthZeroLocation());
            } else {
                Location tpLocation = RiftRegistry.Instance.getRiftLocation(PocketRegistry.Instance.getPrivateDimDoorID(entityPlayer.getCachedUniqueIdString()));
                //@todo, this teleports to the location of the rift, not in front of the rift, like it should
                return TeleportHelper.teleport(entity, tpLocation);
            }
        } else {
            return false;
        }
    }

}
