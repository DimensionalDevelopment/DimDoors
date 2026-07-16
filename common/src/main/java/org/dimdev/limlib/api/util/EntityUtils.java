package org.dimdev.limlib.api.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;

import java.util.UUID;

public final class EntityUtils {
    public static Entity getOwner(Entity entity) {
        if (entity instanceof Player) {
            return entity;
        }

        Entity topmostEntity = null;

        // Thrower
        if (entity instanceof TraceableEntity traceable) {

            topmostEntity = traceable.getOwner();
        }

        // Passengers
        if (entity.getControllingPassenger() != null)
            topmostEntity = entity.getControllingPassenger();
        if (!entity.getPassengers().isEmpty())
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

    public static Player getOwnerPlayer(Entity entity) {
        Entity owner = getOwner(entity);
        return owner instanceof Player player ? player : null;
    }

    public static Projectile getProjectile(Entity entity) {
        return entity instanceof Projectile projectile ? projectile : null;
    }

    public static Projectile getProjectile(DamageSource source) {
        Projectile directProjectile = getProjectile(source.getDirectEntity());
        return directProjectile != null ? directProjectile : getProjectile(source.getEntity());
    }

    public static UUID getOwnerPlayerUuid(Entity entity) {
        Player player = getOwnerPlayer(entity);
        return player != null ? player.getUUID() : null;
    }

    public static void chat(Entity entity, Component text, boolean actionBar) {
        if (entity instanceof Player player) player.displayClientMessage(text, actionBar);
    }

    public static void chat(Entity entity, Component text) {
        chat(entity, text, false);
    }
}
