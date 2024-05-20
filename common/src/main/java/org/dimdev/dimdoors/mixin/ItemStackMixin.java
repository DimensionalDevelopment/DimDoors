package org.dimdev.dimdoors.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @ModifyArg(method = "getDestroySpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;getDestroySpeed(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;)F"), index = 1)
    public BlockState speed(BlockState state) {
        return state.getBlock() instanceof DimensionalDoorBlockRegistrar.AutoGenDimensionalDoorBlock autogenDoor ? autogenDoor.getOriginalBlock().defaultBlockState() : state;
    }

    @ModifyArg(method = "isCorrectToolForDrops", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;isCorrectToolForDrops(Lnet/minecraft/world/level/block/state/BlockState;)Z"), index = 0)
    public BlockState isCorrect(BlockState state) {
        return state.getBlock() instanceof DimensionalDoorBlockRegistrar.AutoGenDimensionalDoorBlock autogenDoor ? autogenDoor.getOriginalBlock().defaultBlockState() : state;
    }
}
