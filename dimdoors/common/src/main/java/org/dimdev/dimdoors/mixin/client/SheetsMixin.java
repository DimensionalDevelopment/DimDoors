package org.dimdev.dimdoors.mixin.client;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.dimdev.dimdoors.block.entity.DriftwoodChestBlockEntity;
import org.dimdev.dimdoors.client.ModSheets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Sheets.class)
public class SheetsMixin {
    @Inject(method = "chooseMaterial(Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/level/block/state/properties/ChestType;Z)Lnet/minecraft/client/resources/model/Material;", at = @At("HEAD"), cancellable = true)
    private static void checkForModdedChest(BlockEntity blockEntity, ChestType chestType, boolean holiday, CallbackInfoReturnable<Material> cir) {
        var material = ModSheets.chestMaterial(blockEntity, chestType, holiday);

        if(material != null) cir.setReturnValue(material);
    }
}
