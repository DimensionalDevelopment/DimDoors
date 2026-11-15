package org.dimdev.dimcore.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.dimdev.dimcore.api.event.ItemHolderEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public class InventoryMixin {
    @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"))
    private void dimcore$beforeAdd(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir, @Share("added") LocalRef<ItemStack> added) {
        added.set(stack.copy());
    }

    @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"))
    private void dimcore$afterAdd(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir, @Share("added") LocalRef<ItemStack> added) {
        if (cir.getReturnValueZ()) ItemHolderEvents.CONTAINER.invoker().invoke((Inventory) (Object) this, added.get());
    }
}
