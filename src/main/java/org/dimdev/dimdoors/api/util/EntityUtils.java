package org.dimdev.dimdoors.api.util;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;

public final class EntityUtils {
	public static Entity getOwner(Entity entity) {
		Entity topmostEntity = null;

		// Thrower
		if (entity instanceof Projectile) topmostEntity = ((Projectile) entity).getOwner();
		if (entity instanceof FishingHook) topmostEntity = ((FishingHook) entity).getOwner();
		if (entity instanceof ItemEntity)
			topmostEntity = ((ServerLevel) entity.getCommandSenderWorld()).getEntity(((ItemEntity) entity).getThrower());

		// Passengers
		if (entity.getControllingPassenger() != null && !(entity instanceof Player))
			topmostEntity = entity.getControllingPassenger();
		if (entity.getPassengers().size() > 0) topmostEntity = entity.getPassengers().get(0);

		// Owned Animals
		if (entity instanceof Mob && ((Mob) entity).isLeashed())
			topmostEntity = ((Mob) entity).getLeashHolder();
		if (entity instanceof TamableAnimal && ((TamableAnimal) entity).getOwner() != null)
			topmostEntity = ((TamableAnimal) entity).getOwner();

		if (topmostEntity != null) {
			return getOwner(topmostEntity);
		}

		return entity;
	}

	public static void chat(Entity entity, Component text, boolean actionBar) {
		if (entity instanceof Player) ((Player) entity).displayClientMessage(text, actionBar);
	}

	public static void chat(Entity entity, Component text) {
		chat(entity, text, true);
	}
}
