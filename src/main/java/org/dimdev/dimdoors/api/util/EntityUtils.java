package org.dimdev.dimdoors.api.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public final class EntityUtils {
	public static Entity getOwner(Entity entity) {
		Entity topmostEntity = null;

		// Thrower
		if (entity instanceof ProjectileEntity) topmostEntity = ((ProjectileEntity) entity).getOwner();
		if (entity instanceof FishingBobberEntity) topmostEntity = ((FishingBobberEntity) entity).getOwner();
		if (entity instanceof ItemEntity)
			topmostEntity = ((ServerWorld) entity.getEntityWorld()).getEntity(((ItemEntity) entity).getThrower());

		// Passengers
		if (entity.getPrimaryPassenger() != null && !(entity instanceof PlayerEntity))
			topmostEntity = entity.getPrimaryPassenger();
		if (entity.getPassengerList().size() > 0) topmostEntity = entity.getPassengerList().get(0);

		// Owned Animals
		if (entity instanceof MobEntity && ((MobEntity) entity).isLeashed())
			topmostEntity = ((MobEntity) entity).getHoldingEntity();
		if (entity instanceof TameableEntity && ((TameableEntity) entity).getOwner() != null)
			topmostEntity = ((TameableEntity) entity).getOwner();

		if (topmostEntity != null) {
			return getOwner(topmostEntity);
		}

		return entity;
	}

	public static void chat(Entity entity, Text text, boolean actionBar) {
		if (entity instanceof PlayerEntity) ((PlayerEntity) entity).sendMessage(text, actionBar);
	}

	public static void chat(Entity entity, Text text) {
		chat(entity, text, true);
	}
}
