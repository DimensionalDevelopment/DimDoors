package org.dimdev.dimcore.mixin;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import org.dimdev.dimcore.api.event.ItemHolderEvents;
import org.dimdev.dimcore.api.ext.ItemExtensionsKt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorStand.class)
public class ArmorStandMixin {
    @Inject(method = "setItemSlot", at = @At("HEAD"))
    private void dimcore$setItemSlot(EquipmentSlot slot, ItemStack stack, CallbackInfo ci) {
        ItemHolderEvents.ENTITY.invoker().invoke((ArmorStand) (Object) this, slot.ordinal(), stack);
    }
}
