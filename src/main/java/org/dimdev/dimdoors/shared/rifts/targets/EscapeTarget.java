package org.dimdev.dimdoors.shared.rifts.targets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.TeleportUtils;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.rifts.registry.RiftRegistry;
import org.dimdev.dimdoors.shared.tileentities.TileEntityRift;
import org.dimdev.dimdoors.shared.world.ModDimensions;
import org.dimdev.dimdoors.shared.world.limbo.WorldProviderLimbo;
import org.dimdev.pocketlib.VirtualLocation;

import java.util.Objects;

@Getter @AllArgsConstructor @Builder(toBuilder = true) @ToString
public class EscapeTarget extends VirtualTarget implements IEntityTarget { // TODO: createRift option
    @Builder.Default protected boolean canEscapeLimbo = false;

    public EscapeTarget() {}

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt = super.writeToNBT(nbt);
        return nbt;
    }

    @Override public boolean receiveEntity(Entity entity, float relativeYaw, float relativePitch) {
        if(Objects.nonNull(entity)) {
            if (!ModDimensions.isDimDoorsPocketDimension(entity.world) && !(entity.world.provider instanceof WorldProviderLimbo)) {
                DimDoors.sendTranslatedMessage(entity, "rifts.destinations.escape.not_in_pocket_dim");
                return false;
            }
            if (entity.world.provider instanceof WorldProviderLimbo && !canEscapeLimbo) {
                DimDoors.sendTranslatedMessage(entity, "rifts.destinations.escape.cannot_escape_limbo");
                return false;
            }
            Location destLoc = RiftRegistry.instance().getOverworldRift(entity.getUniqueID());
            if (Objects.nonNull(destLoc) && destLoc.getTileEntity() instanceof TileEntityRift || canEscapeLimbo)
                TeleportUtils.teleport(entity, VirtualLocation.fromLocation(
                        new Location(entity.world, entity.getPosition())).projectToWorld(false));
            else {
                if (Objects.nonNull(destLoc))
                    DimDoors.sendTranslatedMessage(entity, "rifts.destinations.escape.did_not_use_rift");
                else DimDoors.sendTranslatedMessage(entity, "rifts.destinations.escape.rift_has_closed");
                TeleportUtils.teleport(entity, WorldProviderLimbo.getLimboSkySpawn(entity));
            }
            return true;
        } return false;
    }
}
