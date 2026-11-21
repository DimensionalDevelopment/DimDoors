package org.dimdev.dimcore.mixin.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import org.dimdev.dimcore.client.node.RenderNodes;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {
    @Inject(method = "renderEntityInInventory", at = @At("TAIL"))
    private static void dimcore$compositeEntity(GuiGraphics guiGraphics, float x, float y, float scale, Vector3f translate, Quaternionf pose, Quaternionf cameraOrientation, LivingEntity entity, CallbackInfo ci) {
        RenderNodes.compositeScissored();
    }
}
