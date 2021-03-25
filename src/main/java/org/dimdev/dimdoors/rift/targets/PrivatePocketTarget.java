package org.dimdev.dimdoors.rift.targets;

import java.util.UUID;

import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.pockets.PocketGenerator;
import org.dimdev.dimdoors.api.util.EntityUtils;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.RGBA;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import org.dimdev.dimdoors.world.pocket.type.PrivatePocket;

public class PrivatePocketTarget extends VirtualTarget implements EntityTarget {
	private static final Logger LOGGER = LogManager.getLogger();

	public static final RGBA COLOR = new RGBA(0, 1, 0, 1);

	public PrivatePocketTarget() {
	}

	@Override
	public boolean receiveEntity(Entity entity, Vec3d relativePos, EulerAngle relativeAngle, Vec3d relativeVelocity) {
		// TODO: make this recursive
		UUID uuid = EntityUtils.getOwner(entity).getUuid();
		VirtualLocation virtualLocation = VirtualLocation.fromLocation(this.location);
		if (uuid != null) {
			PrivatePocket pocket = DimensionalRegistry.getPrivateRegistry().getPrivatePocket(uuid);
			if (pocket == null) { // generate the private pocket and get its entrances
				// set to where the pocket was first created
				Pocket unknownTypePocket = PocketGenerator.generatePrivatePocketV2(new VirtualLocation(virtualLocation.getWorld(), virtualLocation.getX(), virtualLocation.getZ(), -1));
				if (! (unknownTypePocket instanceof PrivatePocket)) throw new RuntimeException("Pocket generated for private pocket is not of type PrivatePocket");
				pocket = (PrivatePocket) unknownTypePocket;


				DimensionalRegistry.getPrivateRegistry().setPrivatePocketID(uuid, pocket);
				BlockEntity be = DimensionalRegistry.getRiftRegistry().getPocketEntrance(pocket).getBlockEntity();
				this.processEntity(pocket, be, entity, uuid, relativePos, relativeAngle, relativeVelocity);
			} else {
				Location destLoc = DimensionalRegistry.getRiftRegistry().getPrivatePocketEntrance(uuid); // get the last used entrances
				if (destLoc == null)
					destLoc = DimensionalRegistry.getRiftRegistry().getPocketEntrance(pocket); // if there's none, then set the target to the main entrances
				if (destLoc == null) { // if the pocket entrances is gone, then create a new private pocket
					LOGGER.info("All entrances are gone, creating a new private pocket!");
					Pocket unknownTypePocket = PocketGenerator.generatePrivatePocketV2(new VirtualLocation(virtualLocation.getWorld(), virtualLocation.getX(), virtualLocation.getZ(), -1));
					if (! (unknownTypePocket instanceof PrivatePocket)) throw new RuntimeException("Pocket generated for private pocket is not of type PrivatePocket");
					pocket = (PrivatePocket) unknownTypePocket;

					DimensionalRegistry.getPrivateRegistry().setPrivatePocketID(uuid, pocket);
					destLoc = DimensionalRegistry.getRiftRegistry().getPocketEntrance(pocket);
				}

				this.processEntity(pocket, destLoc.getBlockEntity(), entity, uuid, relativePos, relativeAngle, relativeVelocity);
			}
			return true;
		} else {
			return false;
		}
	}

	private void processEntity(PrivatePocket pocket, BlockEntity blockEntity, Entity entity, UUID uuid, Vec3d relativePos, EulerAngle relativeAngle, Vec3d relativeVelocity) {
		if (entity instanceof ItemEntity) {
			Item item = ((ItemEntity) entity).getStack().getItem();

			if (item instanceof DyeItem) {
				if (pocket.addDye(EntityUtils.getOwner(entity), ((DyeItem) item).getColor())) {
					entity.remove(Entity.RemovalReason.DISCARDED);
				} else {
					((EntityTarget) blockEntity).receiveEntity(entity, relativePos, relativeAngle, relativeVelocity);
				}
			} else {
				((EntityTarget) blockEntity).receiveEntity(entity, relativePos, relativeAngle, relativeVelocity);
			}
		} else {
			((EntityTarget) blockEntity).receiveEntity(entity, relativePos, relativeAngle, relativeVelocity);
			DimensionalRegistry.getRiftRegistry().setLastPrivatePocketExit(uuid, this.location);
		}
	}

	@Override
	public VirtualTargetType<? extends VirtualTarget> getType() {
		return VirtualTargetType.PRIVATE;
	}
}
