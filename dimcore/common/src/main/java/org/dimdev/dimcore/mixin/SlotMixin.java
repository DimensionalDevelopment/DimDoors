package org.dimdev.dimcore.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.dimdev.dimcore.api.event.ItemHolderEvents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Slot.class)
public class SlotMixin {
    @Shadow @Final public Container container;

    @Inject(method = "set", at = @At("HEAD"))
    private void dimcore$set(ItemStack stack, CallbackInfo ci) {
        ItemHolderEvents.CONTAINER.invoker().invoke(this.container, stack);
    }
}
