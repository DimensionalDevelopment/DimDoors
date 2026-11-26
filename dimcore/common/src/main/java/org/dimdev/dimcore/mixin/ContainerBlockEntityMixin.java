package org.dimdev.dimcore.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.dimdev.dimcore.api.event.ItemHolderEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BaseContainerBlockEntity.class, HopperBlockEntity.class, AbstractFurnaceBlockEntity.class, AbstractMinecartContainer.class, ChestBoat.class})
public class ContainerBlockEntityMixin {
    @Inject(method = "setItem", at = @At("HEAD"))
    private void dimcore$setItem(int slot, ItemStack stack, CallbackInfo ci) {
        ItemHolderEvents.CONTAINER.invoker().invoke((Container) (Object) this, slot, stack);
    }
}
