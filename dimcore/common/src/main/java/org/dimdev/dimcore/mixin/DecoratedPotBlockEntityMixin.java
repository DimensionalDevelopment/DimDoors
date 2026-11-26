package org.dimdev.dimcore.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import org.dimdev.dimcore.api.event.ItemHolderEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DecoratedPotBlockEntity.class)
public class DecoratedPotBlockEntityMixin {
    @Inject(method = "setTheItem", at = @At("HEAD"))
    private void dimcore$setTheItem(ItemStack stack, CallbackInfo ci) {
        ItemHolderEvents.CONTAINER.invoker().invoke((DecoratedPotBlockEntity) (Object) this, 0, stack);
    }
}
