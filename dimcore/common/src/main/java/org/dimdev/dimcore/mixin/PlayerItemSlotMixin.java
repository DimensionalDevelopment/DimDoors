package org.dimdev.dimcore.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.dimdev.dimcore.api.event.ItemHolderEvents;
import org.dimdev.dimcore.api.ext.ItemExtensionsKt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerItemSlotMixin {
    @Inject(method = "setItemSlot", at = @At("HEAD"))
    private void dimcore$setItemSlot(EquipmentSlot slot, ItemStack stack, CallbackInfo ci) {
        Inventory inventory = ((Player) (Object) this).getInventory();
        int index = switch (slot.getType()) {
            case HAND -> slot == EquipmentSlot.MAINHAND ? inventory.selected : Inventory.SLOT_OFFHAND;
            case HUMANOID_ARMOR -> 36 + slot.getIndex();
            default -> -1;
        };
        if (index >= 0) ItemHolderEvents.CONTAINER.invoker().invoke(inventory, index, stack);
    }

    @WrapOperation(method = "destroyVanishingCursedItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;removeItemNoUpdate(I)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack dimcore$destroyVanishing(Inventory inventory, int index, Operation<ItemStack> original) {
        ItemHolderEvents.CONTAINER_DESTROYED.invoker().invoke(inventory, index);
        return original.call(inventory, index);
    }
}
