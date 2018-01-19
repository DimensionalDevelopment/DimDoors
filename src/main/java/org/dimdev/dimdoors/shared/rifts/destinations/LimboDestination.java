package org.dimdev.dimdoors.shared.rifts.destinations;

import org.dimdev.ddutils.RotatedLocation;
import org.dimdev.dimdoors.shared.rifts.RiftDestination;
import org.dimdev.dimdoors.shared.world.limbo.WorldProviderLimbo;
import org.dimdev.ddutils.TeleportUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

@Getter @AllArgsConstructor @Builder(toBuilder = true) @ToString
public class LimboDestination extends RiftDestination {
    //public LimboDestination() {}

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
        TeleportUtils.teleport(entity, WorldProviderLimbo.getLimboSkySpawn(entity)); // TODO: do we really want to spam Limbo with items?
        return false;
    }
}
