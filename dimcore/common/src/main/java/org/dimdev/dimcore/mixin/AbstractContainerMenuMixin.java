package org.dimdev.dimcore.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import org.dimdev.dimcore.api.event.ItemHolderEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {
    @Inject(method = "clicked", at = @At("RETURN"))
    private void dimcore$clicked(int slotId, int button, ClickType clickType, Player player, CallbackInfo ci) {
        ItemStack carried = ((AbstractContainerMenu) (Object) this).getCarried();
        if (!carried.isEmpty()) ItemHolderEvents.ENTITY.invoker().invoke(player, 499, carried);
    }
}
