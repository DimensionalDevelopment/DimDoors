package org.dimdev.dimdoors.rift.targets;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.pockets.PocketGenerator;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.util.EntityUtils;
import org.dimdev.dimdoors.util.Location;
import org.dimdev.dimdoors.world.pocket.Pocket;
import org.dimdev.dimdoors.world.pocket.PrivatePocketData;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;

public class PrivatePocketTarget extends VirtualTarget implements EntityTarget {
    private static final Logger LOGGER = LogManager.getLogger();

    public PrivatePocketTarget() {
    }

    @Override
    public void fromTag(CompoundTag nbt) {
        super.fromTag(nbt);
    }

    @Override
    public CompoundTag toTag(CompoundTag nbt) {
        nbt = super.toTag(nbt);
        return nbt;
    }

    @Override
    public boolean receiveEntity(Entity entity, float yawOffset) {
        // TODO: make this recursive
        UUID uuid = EntityUtils.getOwner(entity).getUuid();
        VirtualLocation virtualLocation = VirtualLocation.fromLocation(location);
        if (uuid != null) {
            Pocket pocket = PrivatePocketData.instance(entity.world).getPrivatePocket(uuid);
            if (pocket == null) { // generate the private pocket and get its entrances
                // set to where the pocket was first created
                pocket = PocketGenerator.generatePrivatePocket(new VirtualLocation(virtualLocation.world, virtualLocation.x, virtualLocation.z, -1));

                PrivatePocketData.instance(entity.world).setPrivatePocketID(uuid, pocket);
                processEntity(pocket, RiftRegistry.instance(entity.world).getPocketEntrance(pocket).getBlockEntity(), entity, uuid, yawOffset);
                return true;
            } else {
                Location destLoc = RiftRegistry.instance(entity.world).getPrivatePocketEntrance(uuid); // get the last used entrances
                if (destLoc == null)
                    destLoc = RiftRegistry.instance(entity.world).getPocketEntrance(pocket); // if there's none, then set the target to the main entrances
                if (destLoc == null) { // if the pocket entrances is gone, then create a new private pocket
                    LOGGER.info("All entrances are gone, creating a new private pocket!");
                    pocket = PocketGenerator.generatePrivatePocket(new VirtualLocation(virtualLocation.world, virtualLocation.x, virtualLocation.z, -1));

                    PrivatePocketData.instance(entity.world).setPrivatePocketID(uuid, pocket);
                    destLoc = RiftRegistry.instance(entity.world).getPocketEntrance(pocket);
                }

                processEntity(pocket, destLoc.getBlockEntity(), entity, uuid, yawOffset);
                return true;
            }
        } else {
            return false;
        }
    }

    private void processEntity(Pocket pocket, BlockEntity BlockEntity, Entity entity, UUID uuid, float relativeYaw) {
        if (entity instanceof ItemEntity) {
            Item item = ((ItemEntity) entity).getStack().getItem();

            if (item instanceof DyeItem) {
                pocket.addDye(EntityUtils.getOwner(entity), ((DyeItem) item).getColor());
                entity.remove();
            } else {
                ((EntityTarget) BlockEntity).receiveEntity(entity, relativeYaw);
            }
        } else {
            ((EntityTarget) BlockEntity).receiveEntity(entity, relativeYaw);
            RiftRegistry.instance(entity.world).setLastPrivatePocketExit(uuid, location);
        }
    }

    @Override
    public float[] getColor() {
        return new float[]{0, 1, 0, 1};
    }
}
