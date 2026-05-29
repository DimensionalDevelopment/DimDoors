package org.dimdev.dimdoors.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.enchantment.Rending;
import org.dimdev.dimdoors.item.ItemExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin implements ItemExtensions {
    @Unique
    private Item.Properties settings;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void cacheSettings(Item.Properties settings, CallbackInfo ci) {
    this.settings = settings;
    }

    @Override
    public Item.Properties dimdoors_getSettings() {
    return settings;
    }

    @Inject(method = "isCorrectToolForDrops", at = @At("RETURN"), cancellable = true)
    private void dimdoors$rendingRaisesMiningTier(ItemStack stack, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && Rending.raisesMiningTier(stack, state)) {
            cir.setReturnValue(true);
        }
    }

    @Mixin(Item.Properties.class)
    public static class SettingsMixin implements SettingsExtensions {
    @Override
    public Item.Properties clone() {
        try {
        return (Item.Properties) super.clone();
        } catch (CloneNotSupportedException e) {
        throw new AssertionError(e); // Cant happen, we are Cloneable
        }
    }
    }
}
