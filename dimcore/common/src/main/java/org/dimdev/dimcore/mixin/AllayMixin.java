package org.dimdev.dimcore.mixin;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.item.ItemEntity;
import org.dimdev.dimcore.api.event.ItemHolderEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Allay.class)
public class AllayMixin {
    @Inject(method = "pickUpItem", at = @At("RETURN"))
    private void dimcore$pickUpItem(ItemEntity itemEntity, CallbackInfo ci) {
        Allay allay = (Allay) (Object) this;
        SimpleContainer inventory = allay.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemHolderEvents.ENTITY_INVENTORY.invoker().invoke(allay, i, inventory.getItem(i));
        }
    }
}
