package org.dimdev.dimdoors.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.dimdev.dimdoors.api.event.UseItemOnBlockCallback;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerInteractionManagerMixin {

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerInteractionManager;isCreative()Z", ordinal = 0), method = "interactBlock", cancellable = true)
	public void useItemOnBlock(ServerPlayer player, Level world, ItemStack stack, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> info) {
		InteractionResult result = UseItemOnBlockCallback.EVENT.invoker().useItemOnBlock(player, world, hand, hitResult);
		if (result != InteractionResult.PASS) {
			info.setReturnValue(result);
			info.cancel();
		}
	}
}
