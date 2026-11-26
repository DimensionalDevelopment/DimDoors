package org.dimdev.dimcore.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.dimdev.dimcore.api.event.ItemHolderEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractHorse.class)
public class AbstractHorseMixin {
    @Shadow protected SimpleContainer inventory;

    @Inject(method = "containerChanged", at = @At("HEAD"))
    private void dimcore$containerChanged(Container container, CallbackInfo ci) {
        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemHolderEvents.ENTITY_INVENTORY.invoker().invoke((AbstractHorse) (Object) this, i, this.inventory.getItem(i));
        }
    }
}
