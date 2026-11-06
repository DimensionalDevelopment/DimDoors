package org.dimdev.dimdoors.mixin;

import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.fixes.ItemStackComponentizationFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin({ItemStackComponentizationFix.class})
public class ItemStackComponentizationFixMixin {
    @Unique private static final Set<String> dimensionalDoors$signatures = Set.of("dimdoors:rift_signature", "dimdoors:stabilized_rift_signature");

    @Inject(
            method = {"fixItemStack(Lnet/minecraft/util/datafix/fixes/ItemStackComponentizationFix$ItemStackData;Lcom/mojang/serialization/Dynamic;)V"},
            at = {@At("TAIL")})
    private static void fixItemStackAddition(ItemStackComponentizationFix.ItemStackData itemStackData, Dynamic<?> dynamic, CallbackInfo ci) {
        if(itemStackData.is(dimensionalDoors$signatures)) {
            itemStackData.moveTagToComponent("destination", "dimdoors:destination");
        }


    }
}