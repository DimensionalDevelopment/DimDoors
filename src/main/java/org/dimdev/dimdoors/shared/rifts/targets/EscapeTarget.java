package org.dimdev.dimdoors.shared.rifts.targets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.TeleportUtils;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.rifts.registry.RiftRegistry;
import org.dimdev.dimdoors.shared.tileentities.TileEntityRift;
import org.dimdev.dimdoors.shared.world.ModDimensions;
import org.dimdev.dimdoors.shared.world.limbo.WorldProviderLimbo;
import org.dimdev.pocketlib.VirtualLocation;

import java.util.Objects;
import java.util.UUID;

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
            UUID uuid = entity.getUniqueID();
            if (Objects.isNull(uuid)) return false;
            else {
                Location destLoc = RiftRegistry.instance().getOverworldRift(uuid);
                if((Objects.isNull(destLoc) || !(destLoc.getTileEntity() instanceof TileEntityRift)) && !this.canEscapeLimbo) {
                    if(Objects.isNull(destLoc))
                        DimDoors.sendTranslatedMessage(entity, "rifts.destinations.escape.did_not_use_rift");
                    else DimDoors.sendTranslatedMessage(entity, "rifts.destinations.escape.rift_has_closed");
                    TeleportUtils.teleport(entity, WorldProviderLimbo.getLimboSkySpawn(entity));
                } else {
                    int dim = 0;
                    BlockPos pos = null;
                    if(entity instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer)entity;
                        dim = player.getSpawnDimension();
                        pos = player.getBedLocation(dim);
                    }
                    if(Objects.isNull(pos)) TeleportUtils.teleport(entity, VirtualLocation.fromLocation(new Location(
                            entity.world, entity.getPosition())).projectToWorld(false));
                    else TeleportUtils.teleport(entity,dim,pos.getX(),pos.getY(),pos.getZ(),entity.rotationYaw,entity.rotationPitch);
                }
                return true;
            }
        } return false;
    }
}
