package org.dimdev.dimdoors.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityCollisionContext.class)
public class EntityCollisionContextMixin implements CollisionContextMixin {
    @Shadow
    @Final
    @Nullable
    private Entity entity;

    @Override
    public ItemStack dimensionalDoors$getHeldItem() {
        return entity instanceof LivingEntity livingEntity ? livingEntity.getMainHandItem() : ItemStack.EMPTY;
    }

    @Override
    public ItemStack dimensionalDoors$getOffhandItem() {
        return entity instanceof LivingEntity livingEntity ? livingEntity.getOffhandItem() : ItemStack.EMPTY;
    }
}
