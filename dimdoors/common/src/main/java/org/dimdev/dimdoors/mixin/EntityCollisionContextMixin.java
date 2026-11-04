package org.dimdev.dimdoors.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityCollisionContext.class)
public class EntityCollisionContextMixin implements CollisionContextMixin {
    @Shadow
    @Final
    private ItemStack heldItem;

    @Override
    public ItemStack dimensionalDoors$getHeldItem() {
        return heldItem;
    }
}
