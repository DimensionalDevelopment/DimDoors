/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared.tileentities;

import com.zixiken.dimdoors.shared.RiftRegistry;
import com.zixiken.dimdoors.shared.TeleportHelper;
import com.zixiken.dimdoors.shared.util.Location;
import net.minecraft.entity.Entity;

/**
 *
 * @author Robijnvogel
 */
public class TileEntityDimDoorChaos extends TileEntityDimDoor {

    public TileEntityDimDoorChaos() {
        canRiftBePaired = false;
    }

    @Override
    public boolean tryTeleport(Entity entity) { //this door is never paired
        Location tpLocation = RiftRegistry.Instance.getRiftLocation(RiftRegistry.Instance.getRandomNonPersonalRiftID());
        //@todo, this teleports to the location of the rift, not in front of the rift, like it should
        return TeleportHelper.teleport(entity, tpLocation);
    }
}
