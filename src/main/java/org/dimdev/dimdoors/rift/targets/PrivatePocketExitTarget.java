package org.dimdev.dimdoors.rift.targets;

import java.util.UUID;

import com.mojang.serialization.Codec;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.util.EntityUtils;
import org.dimdev.dimdoors.util.Location;
import org.dimdev.dimdoors.util.RGBA;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.Pocket;
import org.dimdev.dimdoors.world.pocket.PocketRegistry;
import org.dimdev.dimdoors.world.pocket.PrivatePocketData;

import net.minecraft.entity.Entity;
import net.minecraft.text.TranslatableText;

//import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;

public class PrivatePocketExitTarget extends VirtualTarget implements EntityTarget {
    public static final Codec<PrivatePocketExitTarget> CODEC = Codec.unit(PrivatePocketExitTarget::new);
    public static final RGBA COLOR = new RGBA(0, 1, 0, 1);

    public PrivatePocketExitTarget() {
    }

    @Override
    public boolean receiveEntity(Entity entity, float yawOffset) {
        Location destLoc;
        // TODO: make this recursive
        UUID uuid = EntityUtils.getOwner(entity).getUuid();
        if (uuid != null) {
            destLoc = RiftRegistry.instance().getPrivatePocketExit(uuid);
            Pocket pocket = PrivatePocketData.instance().getPrivatePocket(uuid);
            if (ModDimensions.isDimDoorsPocketDimension(location.getWorld()) && pocket != null && PocketRegistry.instance(pocket.world).getPocketAt(location.pos).equals(pocket)) {
                RiftRegistry.instance().setLastPrivatePocketEntrance(uuid, location); // Remember which exit was used for next time the pocket is entered
            }
            if (destLoc == null || !(destLoc.getBlockEntity() instanceof RiftBlockEntity)) {
                if (destLoc == null) {
                    EntityUtils.chat(entity, new TranslatableText("rifts.destinations.private_pocket_exit.did_not_use_rift"));
                } else {
                    EntityUtils.chat(entity, new TranslatableText("rifts.destinations.private_pocket_exit.rift_has_closed"));
                }
                //FabricDimensions.teleport(entity, entity.getServer().getWorld(ModDimensions.LIMBO));
                return false;
            } else {
                ((EntityTarget) destLoc.getBlockEntity()).receiveEntity(entity, yawOffset);
                return true;
            }
        } else {
            return false; // Non-player/owned entity tried to escape/leave private pocket
        }
    }

    @Override
    public void register() {
        super.register();
        PocketRegistry privatePocketRegistry = PocketRegistry.instance(location.world);
        Pocket pocket = privatePocketRegistry.getPocketAt(location.pos);
        RiftRegistry.instance().addPocketEntrance(pocket, location);
    }

    @Override
    public VirtualTargetType<? extends VirtualTarget> getType() {
        return VirtualTargetType.PRIVATE_POCKET_EXIT;
    }
}
