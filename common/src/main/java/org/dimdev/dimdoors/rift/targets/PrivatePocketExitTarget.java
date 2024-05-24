package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Rotations;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.api.util.EntityUtils;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.RGBA;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.UUID;

public class PrivatePocketExitTarget extends VirtualTarget implements EntityTarget {
	public static final MapCodec<PrivatePocketExitTarget> CODEC = MapCodec.unit(PrivatePocketExitTarget::new);
	public static final RGBA COLOR = new RGBA(0, 1, 0, 1);

	public PrivatePocketExitTarget() {
	}

	@Override
	public boolean receiveEntity(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity, Location location) {
		Location destLoc;
		// TODO: make this recursive
		UUID uuid = EntityUtils.getOwner(entity).getUUID();
		if (uuid != null) {
			destLoc = DimensionalRegistry.getRiftRegistry().getPrivatePocketExit(uuid);
			Pocket pocket = DimensionalRegistry.getPrivateRegistry().getPrivatePocket(uuid);
			if (ModDimensions.isPrivatePocketDimension(this.location.getWorld()) && pocket != null && DimensionalRegistry.getPocketDirectory(pocket.getWorld()).getPocketAt(this.location.pos).equals(pocket)) {
				DimensionalRegistry.getRiftRegistry().setLastPrivatePocketEntrance(uuid, this.location); // Remember which exit was used for next time the pocket is entered
			}
			if (destLoc == null || !(destLoc.getBlockEntity() instanceof RiftBlockEntity)) {
				if (destLoc == null) {
					EntityUtils.chat(entity, Component.translatable("rifts.destinations.private_pocket_exit.did_not_use_rift"));
				} else {
					EntityUtils.chat(entity, Component.translatable("rifts.destinations.private_pocket_exit.rift_has_closed"));
				}

				LimboTarget.INSTANCE.receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, location);

				return false;
			} else {
				((EntityTarget) destLoc.getBlockEntity()).receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, location);
				return true;
			}
		} else {
			return false; // Non-player/owned entity tried to escape/leave private pocket
		}
	}

	@Override
	public void register() {
		super.register();
		PocketDirectory privatePocketRegistry = DimensionalRegistry.getPocketDirectory(this.location.world);
		Pocket pocket = privatePocketRegistry.getPocketAt(this.location.pos);
		DimensionalRegistry.getRiftRegistry().addPocketEntrance(pocket, this.location);
	}

	@Override
	public VirtualTargetType<? extends VirtualTarget> getType() {
		return VirtualTargetType.PRIVATE_POCKET_EXIT.get();
	}

	@Override
	public VirtualTarget copy() {
		return this;
	}
}
