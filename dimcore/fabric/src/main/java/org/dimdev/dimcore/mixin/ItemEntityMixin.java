package org.dimdev.dimcore.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.dimdev.dimcore.FabricPlatform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @WrapOperation(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;add(Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean dimcore$pickup(Inventory inventory, ItemStack stack, Operation<Boolean> original) {
        ItemStack picked = stack.copy();
        boolean added = original.call(inventory, stack);
        if (added) FabricPlatform.ITEM_PICKUP.invoker().invoke(inventory.player, picked);
        return added;
    }
}
