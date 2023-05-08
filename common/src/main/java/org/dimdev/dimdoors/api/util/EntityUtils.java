package org.dimdev.dimdoors.api.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;

public final class EntityUtils {
	public static Entity getOwner(Entity entity) {
		if (entity instanceof Player) {
			return entity;
		}

		Entity topmostEntity = null;

		// Thrower
		if (entity instanceof Projectile projectile) topmostEntity = projectile.getOwner();
		if (entity instanceof FishingHook hook) topmostEntity = hook.getOwner();
		if (entity instanceof ItemEntity item) {
			if (item.getOwner() != null) {
				topmostEntity = item.getOwner();
			}
		}

		// Passengers
		if (entity.getControllingPassenger() != null)
			topmostEntity = entity.getControllingPassenger();
		if (entity.getPassengers().size() > 0)
			topmostEntity = entity.getPassengers().get(0);

		// Owned Animals
		if (entity instanceof Mob mob && mob.isLeashed())
			topmostEntity = mob.getLeashHolder();
		if (entity instanceof TamableAnimal tamable && tamable.getOwner() != null)
			topmostEntity = tamable.getOwner();

		if (topmostEntity != null) {
			return getOwner(topmostEntity);
		}

		return entity;
	}

	public static void chat(Entity entity, Component text, boolean actionBar) {
		if (entity instanceof Player player) player.displayClientMessage(text, actionBar);
	}

	public static void chat(Entity entity, Component text) {
		chat(entity, text, false);
	}
}
