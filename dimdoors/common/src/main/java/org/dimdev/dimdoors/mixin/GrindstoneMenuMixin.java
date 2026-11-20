package org.dimdev.dimdoors.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import org.dimdev.dimdoors.item.ModDataComponentTypes;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.rift.item.ItemRiftRegistry;
import org.dimdev.dimdoors.util.GrindstoneAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GrindstoneMenu.class)
public class GrindstoneMenuMixin implements GrindstoneAccessor {
    @Shadow
    @Final
    private Container resultSlots;

    @Inject(method = "computeResult", at = @At("HEAD"), cancellable = true)
    public void dimdoor$computeResult(ItemStack inputItem, ItemStack additionalItem, CallbackInfoReturnable<ItemStack> cir) {
        if(inputItem.is(ModItems.PORTABLE_RIFT) && inputItem.has(ModDataComponentTypes.RIFT_DATA) && additionalItem.isEmpty()) {
            var copy = inputItem.copy();
            copy.remove(ModDataComponentTypes.RIFT_DATA);
            copy.remove(ModDataComponentTypes.INSTANCE.getTRACKED_ITEM());

            cir.setReturnValue(copy);
        }
    }

    @Override
    public Container resultSlots() {
        return resultSlots;
    }
}
