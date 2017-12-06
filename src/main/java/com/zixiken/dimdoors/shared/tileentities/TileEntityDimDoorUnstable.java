/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared.tileentities;

import com.zixiken.dimdoors.shared.RiftRegistry;
import com.zixiken.dimdoors.shared.TeleporterDimDoors;
import com.zixiken.dimdoors.shared.util.Location;
import net.minecraft.entity.Entity;

/**
 *
 * @author Robijnvogel
 */
public class TileEntityDimDoorUnstable extends TileEntityDimDoor {

    public TileEntityDimDoorUnstable() {
        canRiftBePaired = false;
    }

    @Override
    public boolean tryTeleport(Entity entity) { //this door is never paired
        int otherRiftID = RiftRegistry.INSTANCE.getRandomNonPersonalRiftID();
        Location tpLocation = RiftRegistry.INSTANCE.getTeleportLocation(otherRiftID);
        RiftRegistry.INSTANCE.validatePlayerPocketEntry(entity, otherRiftID);
        return TeleporterDimDoors.instance().teleport(entity, tpLocation);
    }
}
