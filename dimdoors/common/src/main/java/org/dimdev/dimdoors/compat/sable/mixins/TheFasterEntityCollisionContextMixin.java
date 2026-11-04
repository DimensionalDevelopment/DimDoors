package org.dimdev.dimdoors.compat.sable.mixins;

import dev.ryanhcode.sable.mixinhelpers.entity.entity_collision.TheFasterEntityCollisionContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.dimdev.dimdoors.api.util.IDimDoorsCollisionContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TheFasterEntityCollisionContext.class)
public class TheFasterEntityCollisionContextMixin implements IDimDoorsCollisionContext {
    @Shadow
    @Final
    private Entity entity;

    @Override
    public ItemStack dimensionalDoors$getHeldItem() {
        return this.entity instanceof final LivingEntity livingEntity ? livingEntity.getMainHandItem() : ItemStack.EMPTY;
    }
}
