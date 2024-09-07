package org.dimdev.dimdoors.rift.targets;

import net.minecraft.core.Rotations;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.api.util.EntityUtils;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.RGBA;
import org.dimdev.dimdoors.pockets.PocketGenerator;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.PrivatePocket;

import java.util.UUID;

public class PrivatePocketTarget extends VirtualTarget implements EntityTarget {
    public static final PrivatePocketTarget INSTANCE = new PrivatePocketTarget();
    private static final Logger LOGGER = LogManager.getLogger();

	public static final RGBA COLOR = new RGBA(0, 1, 0, 1);

	private PrivatePocketTarget() {
	}

	@Override
	public boolean receiveEntity(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity, Location location) {
		UUID uuid = EntityUtils.getOwner(entity).getUUID();
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

	private void processEntity(PrivatePocket pocket, BlockEntity blockEntity, Entity entity, UUID uuid, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity) {
		if (entity instanceof ItemEntity) {
			Item item = ((ItemEntity) entity).getItem().getItem();

			if (item instanceof DyeItem) {
				if (pocket.addDye(EntityUtils.getOwner(entity), ((DyeItem) item).getDyeColor())) {
					entity.remove(Entity.RemovalReason.DISCARDED);
				} else {
					((EntityTarget) blockEntity).receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, null);
				}
			} else {
				((EntityTarget) blockEntity).receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, null);
			}
		} else {
			((EntityTarget) blockEntity).receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, null);
			DimensionalRegistry.getRiftRegistry().setLastPrivatePocketExit(uuid, this.location);
		}
	}

	@Override
	public VirtualTargetType<? extends VirtualTarget> getType() {
		return VirtualTargetType.PRIVATE.get();
	}

	@Override
	public VirtualTarget copy() {
		return this;
	}
}
