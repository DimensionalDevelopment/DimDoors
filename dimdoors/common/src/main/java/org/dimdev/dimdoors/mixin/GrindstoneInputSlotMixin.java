package org.dimdev.dimdoors.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.dimdev.dimdoors.item.ModDataComponentTypes;
import org.dimdev.dimdoors.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.GrindstoneMenu$2")
public abstract class GrindstoneInputSlotMixin extends Slot {
    public GrindstoneInputSlotMixin(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    private void dimdoors$mayPlace(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if(stack.is(ModItems.PORTABLE_RIFT) && stack.has(ModDataComponentTypes.RIFT_DATA)) cir.setReturnValue(true);
    }
}