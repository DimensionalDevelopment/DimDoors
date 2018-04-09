package org.dimdev.dimdoors.shared.rifts.targets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import org.dimdev.ddutils.TeleportUtils;
import org.dimdev.dimdoors.shared.world.limbo.WorldProviderLimbo;

@Getter @AllArgsConstructor @Builder(toBuilder = true) @ToString
public class LimboTarget extends VirtualTarget implements IEntityTarget { // TODO: Make it rain when receiving water, thunder when receiving power
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
    public boolean receiveEntity(Entity entity, float relativeYaw, float relativePitch) {
        TeleportUtils.teleport(entity, WorldProviderLimbo.getLimboSkySpawn(entity));
        return true;
    }
}
