package org.dimdev.dimdoors.compat.sable.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.dimdev.dimcore.api.event.ItemHolderEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SubLevelAssemblyHelper.class)
public class SubLevelAssemblyHelperMixin {
    @WrapOperation(method = "moveBlocks", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;loadWithComponents(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/HolderLookup$Provider;)V", remap = true))
    private static void dimdoors$retrackContents(BlockEntity blockEntity, CompoundTag tag, HolderLookup.Provider registries, Operation<Void> original) {
        original.call(blockEntity, tag, registries);
        if (blockEntity instanceof Container container) for (int i = 0; i < container.getContainerSize(); i++) ItemHolderEvents.CONTAINER.invoker().invoke(container, i, container.getItem(i));
    }
}