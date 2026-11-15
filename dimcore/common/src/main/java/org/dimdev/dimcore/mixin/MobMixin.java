package org.dimdev.dimcore.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.dimdev.dimcore.api.event.ItemHolderEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public class MobMixin {
    @WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;pickUpItem(Lnet/minecraft/world/entity/item/ItemEntity;)V"))
    private void dimcore$pickUpItem(Mob mob, ItemEntity itemEntity, Operation<Void> original) {
        ItemStack picked = itemEntity.getItem().copy();
        original.call(mob, itemEntity);
        if (itemEntity.getItem().getCount() < picked.getCount()) ItemHolderEvents.ENTITY.invoker().invoke(mob, picked);
    }
}
