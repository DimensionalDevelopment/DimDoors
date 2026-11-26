package org.dimdev.dimcore.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.dimdev.dimcore.api.event.ItemHolderEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public class InventoryMixin {
    @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"))
    private void dimcore$beforeAdd(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir, @Share("added") LocalRef<ItemStack> added) {
        added.set(stack.copy());
    }

    @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"))
    private void dimcore$afterAdd(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir, @Share("added") LocalRef<ItemStack> added) {
        if (!cir.getReturnValueZ()) return;
        Inventory inventory = (Inventory) (Object) this;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (ItemStack.isSameItemSameComponents(inventory.getItem(i), added.get())) ItemHolderEvents.CONTAINER.invoker().invoke(inventory, i, inventory.getItem(i));
        }
    }

    @Inject(method = "setItem", at = @At("HEAD"))
    private void dimcore$setItem(int slot, ItemStack stack, CallbackInfo ci) {
        ItemHolderEvents.CONTAINER.invoker().invoke((Inventory) (Object) this, slot, stack);
    }

    @WrapOperation(method = {"pickSlot", "setPickedItem"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/core/NonNullList;set(ILjava/lang/Object;)Ljava/lang/Object;"))
    private Object dimcore$setListItem(NonNullList<ItemStack> list, int slot, Object stack, Operation<Object> original) {
        Object previous = original.call(list, slot, stack);
        ItemHolderEvents.CONTAINER.invoker().invoke((Inventory) (Object) this, slot, (ItemStack) stack);
        return previous;
    }
}
